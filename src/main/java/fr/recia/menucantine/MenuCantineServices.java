package fr.recia.menucantine;

import java.time.LocalDate;

import javax.annotation.ManagedBean;

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
import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.RequeteHelper;
import fr.recia.menucantine.beans.Semaine;

@Configuration
@ManagedBean
public class MenuCantineServices {
	@SuppressWarnings("unused")
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
		rh.dateJour(requete,date);
		
		try {
			return new Semaine(adoriaHelper.call(requete.getUai(), requete.getSemaine(), requete.getAnnee()), requete);
		} catch (RestAdoriaClientException e) {
			LocalDate lundi = rh.dateFromYearWeekDay(requete.getAnnee(), requete.getSemaine(), 1);
			LocalDate vendredi = lundi.plusDays(4);
			e.getMap().put("debut", lundi.format(Semaine.formatter));
			e.getMap().put("fin", vendredi.format(Semaine.formatter));
			
			vendredi = lundi.minusDays(3);
			Requete rPrev = new Requete();
			rh.dateJour(rPrev, vendredi);
			try {
				ReponseAdoria res = adoriaHelper.call(requete.getUai(), rPrev.getSemaine(), rPrev.getAnnee());
				if (res != null) {
					e.getMap().put("previousWeek", vendredi.format(RequeteHelper.dateFormatter));
				}
			} catch (Exception catched) {
				log.debug("requette semaine precedante : " +  catched.getMessage());
			}
			
			throw e;
		}
	}
	
	
}
