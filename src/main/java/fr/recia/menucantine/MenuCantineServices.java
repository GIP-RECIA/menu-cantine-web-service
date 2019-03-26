package fr.recia.menucantine;

import java.time.DayOfWeek;
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
import fr.recia.menucantine.beans.Requete;
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
	
	@Value("${test.no-web-service}")
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
		
		Integer annee = requete.getAnnee();
		Integer semaine = requete.getSemaine();
		LocalDate date = requete.getDate();
		
		if (date == null) {
			date = LocalDate.now();
		}
		
		if (annee == null) {
			annee = date.getYear();
			requete.setAnnee(annee);
		}
		if (semaine == null) {
			requete.setDate(date);
			semaine = requete.getSemaine();
			annee = requete.getAnnee();
		}
		
		Integer jour = requete.getJour();
		
		if (jour == null) {
			jour = DayOfWeek.MONDAY.getValue();
			requete.setJour(jour);
		}
		
		return new Semaine(adoriaHelper.call(requete.getUai(), semaine, annee), requete);
		 
	}
	
	
	public static void main(String[] args) {
		Requete r = new Requete();
		r.setDate(LocalDate.of(2018, 12, 31));
		System.out.println(r);
		r.setDate(LocalDate.now());
		System.out.println(r);
		r.setDate(LocalDate.of(2019, 1, 1));
		System.out.println(r);
		r.setDate(LocalDate.of(2020, 1, 3));
		System.out.println(r);
		r.setDate(LocalDate.of(2020, 12, 31));
		System.out.println(r);
		
		LocalDate date =  LocalDate.parse("31/12/2020" , Requete.dateFormatter);
		System.out.println(date);
	}
	
}
