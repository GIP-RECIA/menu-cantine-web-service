/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import fr.recia.menucantine.config.MapperConfig;
import fr.recia.menucantine.config.UAIConfig;
import fr.recia.menucantine.exception.NoDataExchangeException;
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
import fr.recia.menucantine.mapper.IMapper;
import fr.recia.menucantine.mapper.MapperWebGerest;
import fr.recia.menucantine.dto.JourneeDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.webgerest.APIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import fr.recia.menucantine.adoria.beans.Labels;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.RequeteHelper;
import fr.recia.menucantine.beans.Semaine;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Configuration
@ManagedBean
public class MenuCantineServices {
	
	private static final Logger log = LoggerFactory.getLogger(MenuCantineServices.class);

	@Autowired
	APIClient apiClient;

	@Autowired
	IMapper mapperWebGerest;

	@Autowired
	private MapperConfig mapperConfig;

	@Autowired
	private UAIConfig uaiConfig;
	
	@Value("${adoria.gemrcn-csv}")
	String gemrcnFilename;
	
	@Value("${adoria.labels-csv}")
	String labelsFilename;
	
	@PostConstruct
	public void postConstructInit(){
		// Pas besoin de charger les GemRcn car on ne les utilise pas dans cette version
		// GemRcn.loadFile(gemrcnFilename);
		Labels.loadFile(labelsFilename);
	}

	/**
	 * Prends en entrée un objet Requête, fait les appels correspondants à l'API et retourne une Semaine
	 * @param requete La requête avec la date et l'UAI demandé
	 * @return Un objet de type Semaine à envoyer directement au front
	 * @throws UnknownUAIException Dans le cas ou l'UAI n'a pas d'URL associé
	 * @throws WebgerestRequestException Dans le cas ou on a une erreur innatendue sur une des requêtes
	 */
	public Semaine newFindSemaine(Requete requete) throws UnknownUAIException, WebgerestRequestException, NoDataExchangeException {

		log.trace("Dans la méthode newFindSemaine");

		// Première étape, on regarde si l'UAI demandé est mappé par un autre UAI
		String uai = requete.getUai().toUpperCase();
		if(uaiConfig.getRegroupements() != null){
			if(uaiConfig.isMapped(requete.getUai())){
				uai = uaiConfig.getNewUAI(uai);
				log.debug("Mapping de l'UAI {} par l'UAI {}", requete.getUai(), uai);
			}
		}

		// Par défaut on cherche par rapport au jour d'aujourd'hui, mais si jamais on spécifie la date dans la requête
		// alors on cherche par rapport à cette date en question
		LocalDate today = LocalDate.now();
        if (!Objects.equals(requete.getDateJour(), "")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            today = LocalDate.parse(requete.getDateJour(), formatter);
        }

		if(requete.getSemaine() != null){
			final int numeroSemaine = requete.getSemaine();
			final int annee = today.getYear();
			final int numeroJourDansSemaine = 1;
			LocalDate premiereDate = LocalDate.of(annee, 1, 1);
			LocalDate premierJourSemaine = premiereDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			today = premierJourSemaine.plusWeeks(numeroSemaine - 1).plusDays(numeroJourDansSemaine - 1);
		}

		log.debug("Date demandée par la requête : " + today);

        // On doit faire une requête par jour de la semaine pour reconstituer la semaine (numJour=1=lundi, ...)
		List<JourneeDTO> journeeDTOList = new ArrayList<>();
		for(int numJour=1; numJour<=5; numJour++){
			JourneeDTO journeeDTO = new JourneeDTO();
			LocalDate menuDay = RequeteHelper.jourMemeSemaine(today, numJour);
			journeeDTO.setDate(menuDay);
			String menuDayString = RequeteHelper.localeDateToStringWithNoSlashes(menuDay);
			// On doit aussi faire une requête par service pour reconstituer une journée (numService=2=déjeuner, ...)
			for(int numService=1; numService<=4; numService++){
				log.debug("Requête avec les paramètres uai={}, date={}, service={}", uai, menuDayString, numService);
				ServiceDTO serviceDTO = apiClient.makeAuthenticatedApiCallGetMenu(uai, menuDayString, numService);
				// Si on a une erreur ca ne sert a rien d'ajouter le service à la journée
				if(serviceDTO.getError() == 0){
					journeeDTO.addService(mapperConfig.serviceKeyFromValue(numService), serviceDTO);
				}else{
					log.error("Erreur sur le retour de la requête avec les paramètres uai={}, date={}, service={}." +
							"\nMessage retourné : {}", uai, menuDayString, numService, serviceDTO.getMessage());
					if(serviceDTO.getMessage() != null){
						if(serviceDTO.getMessage().equals("L'etablissement a bloqué l'echange de donnees.")) {
							throw new NoDataExchangeException("L'établissement a bloqué l'échange de donnees.");
						}
					}else{
						throw new WebgerestRequestException("Erreur innatendue lors de la requête");
					}
				}

			}
			journeeDTOList.add(journeeDTO);
		}

		// A partir de la liste des journées, on peut alors reconstituer une semaine
		// On map la semaine sur l'ancien model pour l'envoyer au front, en donnant aussi la date de la requête et l'uai
		final Semaine semaine = mapperWebGerest.buildSemaine(journeeDTOList, today, uai);

		// Si le nombre de jours vaut 0, cela veut dire qu'on a pas de menu, on renvoie une exception
		if(semaine.getNbJours() == 0) {
			log.warn("Aucun menu trouvé pour l'UAI " + uai + " pour la semaine du "
					+ semaine.getDebut() + " au " + semaine.getFin());
		}

		return semaine;
	}

}
