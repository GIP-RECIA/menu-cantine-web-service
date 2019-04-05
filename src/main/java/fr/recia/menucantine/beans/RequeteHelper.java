package fr.recia.menucantine.beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class RequeteHelper {
	static public  WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());
	static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	
	
	private LocalDate now = LocalDate.now();

	
	/**
	 * Fixe tous les attributs de la requete en fonction de la date donnée
	 * @param date
	 */
	public void dateJour(Requete requete, LocalDate date) {
		LocalDate jeudi;
		requete.jour = date.getDayOfWeek().getValue();
		if (requete.jour == 4) {
			jeudi = date;
		} else {
			jeudi = date.plusDays( 4 - requete.jour );
		}
		requete.annee = jeudi.getYear();
		requete.semaine = jeudi.get(WEEK_FIELDS.weekOfWeekBasedYear());
		requete.dateJour = date.format(dateFormatter);
		// dateJour = String.format("%td/%tm/%tY", date, date, date);
	}
	
	/**
	 * Parse la date du champ dateJour de la requete
	 * @return
	 */
	public LocalDate dateJour(Requete requete) {
		if (requete.dateJour != null && ! requete.dateJour.isEmpty()) {
			try {
				return LocalDate.parse(requete.dateJour, dateFormatter);
			} catch (Exception e) {
				requete.dateJour = null;
			}
		}
		return null;
	}
	
	
	
	public LocalDate dateSemaine(Requete requete) {
		return dateFromYearWeekDay(requete.annee, requete.semaine, requete.jour);
	}
	

	/**
	 * Donne la date en fonction de l'annee, la semaine et le jour dans la semaine.
	 * si une valeur est  0 on prend celle  du jour courant  (now)
	 * @param annee
	 * @param semaine
	 * @param jour
	 * @return
	 */
	public LocalDate dateFromYearWeekDay(Integer annee, Integer semaine, Integer jour) {
		LocalDate date;
		
		if (semaine == null || semaine == 0) {
			semaine = now.get(WEEK_FIELDS.weekOfWeekBasedYear());
		}
		if (jour == null || jour == 0) {
			jour = now.get(WEEK_FIELDS.dayOfWeek());
		}
		if (annee == null || annee == 0) {
			date = now;
		} else {
			date  = LocalDate.of(annee, 1, 1);
		}
		
		date = date.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, semaine);

		return date.plusDays(jour - date.get(WEEK_FIELDS.dayOfWeek()));
		// return  date.with(TemporalAdjusters.previousOrSame(DayOfWeek.of(jour)));
	}


	
	
}
