/**
 * Copyright (C) 2019 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2019 Pierre Legay <pierre.legay@recia.fr>
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
import java.util.HashMap;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import fr.recia.menucantine.enums.EnumTypeService;
import fr.recia.menucantine.exception.NoMenuException;
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
import fr.recia.menucantine.mapper.MapperWebGerest;
import fr.recia.menucantine.dto.JourneeDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.webgerest.APIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import fr.recia.menucantine.adoria.AdoriaHelper;
import fr.recia.menucantine.adoria.IRestAdoriaClient;
import fr.recia.menucantine.adoria.RestAdoriaClientException;
import fr.recia.menucantine.adoria.beans.GemRcn;
import fr.recia.menucantine.adoria.beans.Labels;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.RequeteHelper;
import fr.recia.menucantine.beans.Semaine;

@Configuration
@ManagedBean
public class MenuCantineServices {
	
	private static final Logger log = LoggerFactory.getLogger(MenuCantineServices.class);	

	@Autowired
	@Lazy
	private AdoriaHelper adoriaHelper ;
	
	@Autowired
	IRestAdoriaClient adoriaWeb;
	
	@Autowired
	IRestAdoriaClient adoriaTest;
	
	@Value("${adoria.test.no-web-service}")
	Boolean noWebService;

	@Autowired
	APIClient apiClient;

	@Autowired
	MapperWebGerest mapper;
	
	@Bean
	IRestAdoriaClient adoriaClient () {
		if (noWebService) {
			return adoriaTest;
		} 
		return adoriaWeb;
	}
	
	@Value("${adoria.gemrcn-csv}")
	String gemrcnFilename;
	
	@Value("${adoria.labels-csv}")
	String labelsFilename;
	
	@PostConstruct
	public void postConstructInit(){
		GemRcn.loadFile(gemrcnFilename);
		Labels.loadFile(labelsFilename);
	}
	
	
	public List<String> loadAllSemaine() {
		RequeteHelper rh = new RequeteHelper();
		Requete requete = new Requete();
		LocalDate date = rh.dateSemaine(requete);
		rh.dateJour(requete, date);
		return adoriaHelper.callTest(adoriaWeb, requete.getSemaine() -1 , requete.getAnnee());
	}

	public Semaine newFindSemaine(Requete requete) throws UnknownUAIException, WebgerestRequestException, NoMenuException {

		log.trace("Dans la méthode newFindSemaine");

		// Par défaut on cherche par rapport au jour d'aujourd'hui, mais si jamais on spécifie la date dans la requête
		// alors on cherche par rapport à cette date en question
		final String uai = requete.getUai();
		LocalDate today = LocalDate.now();
        if (requete.getDateJour() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            today = LocalDate.parse(requete.getDateJour(), formatter);
        }
		log.debug("Date demandée par la requête : " + today);

        // On doit faire une requête par jour de la semaine pour reconstituer la semaine (numJour=1=lundi, ...)
		List<JourneeDTO> journeeDTOList = new ArrayList<>();
		for(int numJour=1; numJour<=5; numJour++){
			JourneeDTO journeeDTO = new JourneeDTO();
			LocalDate menuDay = RequeteHelper.jourMemeSemaine(today, numJour);
			journeeDTO.setDate(menuDay);
			String menuDayString = RequeteHelper.localeDateToString(menuDay);
			// On doit aussi faire une requête par service pour reconstituer une journée (numService=2=déjeuner, ...)
			for(int numService=1; numService<=4; numService++){
				log.debug("Requête avec les paramètres uai={}, date={}, service={}", uai, menuDayString, numService);
				ServiceDTO serviceDTO = apiClient.makeAuthenticatedApiCallGetMenu(uai, menuDayString, numService);
				// Si on a une erreur ca ne sert a rien d'ajouter le service à la journée
				if(serviceDTO.getError() == 0){
					journeeDTO.addService(EnumTypeService.serviceNumber(numService), serviceDTO);
				}else{
					log.warn("Erreur sur le retour de la requête avec les paramètres uai={}, date={}, service={}", uai, menuDayString, numService);
				}

			}
			journeeDTOList.add(journeeDTO);
		}

		// A partir de la liste des journées, on peut alors reconstituer une semaine
		// On map la semaine sur l'ancien model pour l'envoyer au front, en donnant aussi la date de la requête et l'uai
		final Semaine semaine = mapper.buildSemaine(journeeDTOList, today, uai);

		// Si le nombre de jours vaut 0, cela veut dire qu'on a pas de menu, on renvoie une exception
		if(semaine.getNbJours() == 0){
			throw new NoMenuException("Aucun menu trouvé pour l'UAI " + uai + " pour la semaine du "
					+ semaine.getDebut()+ " au " + semaine.getFin());
		}

		return semaine;
	}
	
	
	public Semaine findSemaine(Requete requete) throws RestAdoriaClientException{
		
		if (requete == null || requete.getUai() == null){
			throw new NullPointerException("requete ou uai null: " + requete);
		}
		RequeteHelper rh = new RequeteHelper();
		
		
		LocalDate date = rh.dateJour(requete);
		
		if (date == null) { 
				// on a pas de date la requette est basé sur la semaine 
			date = rh.dateSemaine(requete);
		}
			// pour normalisé la requete
		rh.dateJour(requete, date);
		
		try {
			
			return new Semaine(	adoriaHelper.call(
									requete.getUai(), 
									requete.getSemaine(), 
									requete.getAnnee()), 
								requete);
		
		} catch (RestAdoriaClientException e) {
			
			LocalDate lundi = rh.dateFromYearWeekDay(requete.getAnnee(), requete.getSemaine(), 1);
			
			LocalDate vendredi = lundi.plusDays(4);
			
			e.getMap().put("debut", lundi.format(Semaine.formatter));
			
			e.getMap().put("fin", vendredi.format(Semaine.formatter));
			
			vendredi = lundi.minusDays(3);
			
			Requete rPrev = new Requete();
			
			rh.dateJour(rPrev, vendredi);
			
			LocalDate aux = vendredi;
			
		/*	
			try {
				int nbIterBack = 5;
				ReponseAdoria res = null;
				while (res == null && nbIterBack -- > 0){
						rh.dateJour(rPrev, aux);
						
						res = adoriaHelper.call(requete.getUai(), rPrev.getSemaine(), rPrev.getAnnee());
						
						aux = aux.minusDays(7);
				}
				
				if (res != null) {
					e.getMap().put("previousWeek", vendredi.format(RequeteHelper.dateFormatter));
				}
				
			} catch (Exception catched) {
				log.debug("requette semaine precedante : " +  catched.getMessage());
			}
		*/	
			lundi = lundi.plusDays(7);
			e.getMap().put("previousWeek", vendredi.format(RequeteHelper.dateFormatter));
			e.getMap().put("nextWeek", lundi.format(RequeteHelper.dateFormatter));
			throw e;
		}
	}
	
	
}
