package fr.recia.menucantine;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javax.annotation.ManagedBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import fr.recia.menucantine.adoria.RestAdoriaClient;
import fr.recia.menucantine.beans.Requette;
import fr.recia.menucantine.beans.Semaine;

@Configuration
@ManagedBean
public class MenuCantineServices {

	@Autowired
	private RestAdoriaClient adoriaClient ;
	
	public Semaine findSemaine(Requette requette){
		
		if (requette == null || requette.getUai() == null){
			throw new NullPointerException("requette ou uai null");
		}
	
		Integer annee = requette.getAnnee();
		Integer semaine = requette.getSemaine();
		LocalDate now = LocalDate.now();
		
		if (annee == null) {
			annee = now.getYear();
		}
		if (semaine == null) {
			WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
			semaine = now.get(weekFields.weekOfWeekBasedYear());
		}
		
		return new Semaine(adoriaClient.call(requette.getUai(), semaine, annee));
	}
}
