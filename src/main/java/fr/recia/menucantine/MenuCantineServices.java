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

import java.time.LocalDate;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

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
import fr.recia.menucantine.adoria.beans.ReponseAdoria;
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
