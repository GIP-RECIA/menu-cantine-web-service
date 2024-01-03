/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
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
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Permet de normaliser les requetes et de calculer les dates
 * @author legay
 */
public class RequeteHelper {
	final static public WeekFields WEEK_FIELDS = WeekFields.ISO;
	final static public DateTimeFormatter dateFormatterSlashes = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	final static public DateTimeFormatter dateFormatterNoSlashes = DateTimeFormatter.ofPattern("yyyyMMdd");
	final static public DateTimeFormatter dateFormatterSpaces = DateTimeFormatter.ofPattern("d MMMM yy").withLocale(Locale.FRENCH);

	/**
	 * Donne la journée de la même semaine que la journée passée en paramètre en fonction du numéro demandé
	 * @param jour La journée utilisée pour récupérer la semaine
	 * @param jourDemande Le numéro de la journée dans la semaine
	 * @return Un LocalDate de la même semaine que jour et qui est le numéro jourDemande de la semaine
	 */
	static public LocalDate jourMemeSemaine(LocalDate jour, int jourDemande) {
		final int j = jour.get(WEEK_FIELDS.dayOfWeek());
		if (j != jourDemande) {
			return jour.plusDays(jourDemande - j);
		}
		return jour;
	}

	/**
	 * Donne le numéro de semaine associé à un jour
	 * @param jour Le jour de la semaine recherchée
	 * @return Le numéro de la semaine
	 */
	static public int semaine(LocalDate jour) {
		return jour.get(WEEK_FIELDS.weekOfWeekBasedYear());
	}

	/**
	 * Fixe tous les attributs de la requete en fonction de la date donnée
	 * @param requete La requete dont on veut fixer les attributs
	 * @param date La date à laquelle on veut fixer la requete
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
		requete.dateJour = date.format(dateFormatterSlashes);
	}

	/**
	 * Méthodes pour transformer un objet LocaleDate en un String utilisable dans la requête
	 * de différentes manières en fonction du dateFormatter utilisé
	 */
	public static String localeDateToStringWithSlashes(LocalDate date){
		return date.format(dateFormatterSlashes);
	}

	public static String localeDateToStringWithNoSlashes(LocalDate date){
		return date.format(dateFormatterNoSlashes);
	}

	public static String localeDateToStringWithSpaces(LocalDate date){
		return date.format(dateFormatterSpaces);
	}
	
	/**
	 * Parse la date du champ dateJour de la requete
	 * @param requete L'objet requête duquel on veut récupérer la date
	 * @return La date de la requête sous forme d'une LocalDate
	 */
	public LocalDate dateJour(Requete requete) {
		if (requete.dateJour != null && ! requete.dateJour.isEmpty()) {
			try {
				return LocalDate.parse(requete.dateJour, dateFormatterSlashes);
			} catch (Exception e) {
				requete.dateJour = null;
			}
		}
		return null;
	}
}
