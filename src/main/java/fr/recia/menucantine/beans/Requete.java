package fr.recia.menucantine.beans;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import lombok.Data;


@Data
public class Requete {
	static public  WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());
	
	Integer semaine;
	Integer annee;
	Integer jour;
	
	String uai;
	
	public void setDate(LocalDate date) {
		LocalDate jeudi;
		jour = date.getDayOfWeek().getValue();
		if (jour == 4) {
			jeudi = date;
		} else {
			jeudi = date.plusDays( 4 - jour );
		}
		annee = jeudi.getYear();
		semaine = jeudi.get(WEEK_FIELDS.weekOfWeekBasedYear());
	}
}

