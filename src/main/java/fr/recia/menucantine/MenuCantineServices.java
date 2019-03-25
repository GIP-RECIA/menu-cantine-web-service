package fr.recia.menucantine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import fr.recia.menucantine.adoria.RestAdoriaClient;
import fr.recia.menucantine.adoria.RestAdoriaClientException;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;

@Configuration
@ManagedBean
public class MenuCantineServices {
	private static final Logger log = LoggerFactory.getLogger(MenuCantineServices.class);	
	
	@Autowired
	private RestAdoriaClient adoriaClient ;
	
	
	
	public Semaine findSemaine(Requete requete) throws RestAdoriaClientException{
		
		if (requete == null || requete.getUai() == null){
			throw new NullPointerException("requete ou uai null: " + requete);
		}
	
		Integer annee = requete.getAnnee();
		Integer semaine = requete.getSemaine();
		Integer jour = requete.getJour();
		LocalDate date = LocalDate.now();
		
		if (annee == null) {
			annee = date.getYear();
			requete.setAnnee(annee);
		}
		if (semaine == null) {
			requete.setDate(date);
			semaine = requete.getSemaine();
			annee = requete.getAnnee();
		} 
		
		if (jour == null) {
			jour = DayOfWeek.MONDAY.getValue();
			requete.setJour(jour);
		}
		
		return new Semaine(adoriaClient.call(requete.getUai(), semaine, annee), requete);
		 
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
	/*
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		LocalDate date = LocalDate.of(2021, 1, 1);
		System.out.println(date);
		int semaine = date.get(weekFields.weekOfWeekBasedYear());
		System.out.println(semaine);	
		
		date = date.minusDays(7);
		 semaine = date.get(weekFields.weekOfWeekBasedYear());
		System.out.println(semaine);
	*/	
	}
}
