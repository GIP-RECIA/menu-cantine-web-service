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
package fr.recia.menucantine.beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Permet de normaliser les requetes et de calculer les dates
 * @author legay
 *
 */
public class RequeteHelper {
	static public  WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());
	static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	

	
	static public LocalDate jourMemeSemaine(LocalDate jour, int jourDemande) {
		int j = jour.get(WEEK_FIELDS.dayOfWeek());
		if (j != jourDemande) {
			return jour.plusDays(jourDemande - j);
		}
		return jour;
	}
	
	static public int semaine(LocalDate jour) {
		return jour.get(WEEK_FIELDS.weekOfWeekBasedYear());
	}
	
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
		requete.semaine = semaine(jeudi);
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
			semaine = semaine(now);
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
