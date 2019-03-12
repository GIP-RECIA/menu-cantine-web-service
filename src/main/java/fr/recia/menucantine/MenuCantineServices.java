package fr.recia.menucantine;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javax.annotation.ManagedBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import fr.recia.menucantine.adoria.RestAdoriaClient;
import fr.recia.menucantine.adoria.RestAdoriaClientException;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;

@Configuration
@ManagedBean
public class MenuCantineServices {

	@Autowired
	private RestAdoriaClient adoriaClient ;
	
	public Semaine findSemaine(Requete requete) throws RestAdoriaClientException{
		
		if (requete == null || requete.getUai() == null){
			throw new NullPointerException("requete ou uai null: " + requete);
		}
	
		Integer annee = requete.getAnnee();
		Integer semaine = requete.getSemaine();
		LocalDate now = LocalDate.now();
		
		if (annee == null) {
			annee = now.getYear();
			requete.setAnnee(annee);
		}
		if (semaine == null) {
			WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
			semaine = now.get(weekFields.weekOfWeekBasedYear());
			requete.setSemaine(semaine);
		}
		
		return new Semaine(adoriaClient.call(requete.getUai(), semaine, annee));
	}
}
