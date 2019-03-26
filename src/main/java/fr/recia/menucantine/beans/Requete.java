package fr.recia.menucantine.beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;

import lombok.Data;


@Data
public class Requete {
	static public  WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());
	static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	Integer semaine;
	Integer annee;
	Integer jour;
	String dateJour;
	
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
		dateJour = date.format(dateFormatter);
		// dateJour = String.format("%td/%tm/%tY", date, date, date);
	}
	public LocalDate getDate() {
		if (dateJour != null && ! dateJour.isEmpty()) {
			try {
				return LocalDate.parse(dateJour, dateFormatter);
			} catch (Exception e) {
				dateJour = null;
			}
		}
		return null;
	}
	
}

