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
import java.time.format.FormatStyle;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.recia.menucantine.adoria.beans.*;
import lombok.Data;

@Data
public class Semaine {
	@JsonIgnore 
	static public DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
//	static public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM");

	Integer nbJours;
	String debut;
	String fin;
	
	Requete requete;
	
	List<GemRcn> allGemRcn = GemRcn.getList();
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	LocalDate previousWeek;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	LocalDate nextWeek;
	
	List<Journee> jours;

	NbPlatParSsMenuParService nbPlatMaxParService;

	public Semaine(){
		super();
	}
	
	public Semaine(ReponseAdoria menuSemaine, Requete requete) {
		super();
		setMenuSemaine(menuSemaine);
		setRequete(requete);
	}
	
	
	public void setMenuSemaine(ReponseAdoria menuSemaine) {
	
		if (menuSemaine == null) {
			nbJours = null;
			debut = null;
			fin = null;
			jours = null;
		} else {
			menuSemaine.clean(); // devrait déjà être clean
			menuSemaine.complete();
			nbJours = menuSemaine.getNbJours();
			jours = menuSemaine.getDates();
			
			if (nbJours > 0 ) {
				debut = formatDate(0);
				if (nbJours > 1 ) {
					fin = formatDate(nbJours-1);
				}
			}
			
			if (menuSemaine.getPreviousWeekExportable()) {
				previousWeek = RequeteHelper.jourMemeSemaine(dateJour(0), 5).minusDays(7);
			};
			if (menuSemaine.getNextWeekExportable()) {
				nextWeek = RequeteHelper.jourMemeSemaine(dateJour(0), 1).plusDays(7);
			}
			 
		}
	}
	
	private LocalDate dateJour(Integer jour) {
		return jours.get(jour).getDate();
	}
	
	private String formatDate(Integer jour){
		return dateJour(jour).format(formatter);
	}

	/**
	 * Le netoyage consiste a netoyer chaque jour et a supprimer les jours vide.
	 * @return
	 */
	public void clean() {
		for(Journee journee: this.getJours()){
			journee.newclean();
		}
	}

	public void complete(){
		initNbPlatMaxParService();
		if (this.getJours() != null) {
			for(Journee journee : this.getJours()) {
				if (!journee.isVide()){
					completeSsMenu(journee);
				}
			};
		}
	}

	private void initNbPlatMaxParService(){
		if (this.getJours() != null) {
			nbPlatMaxParService = new NbPlatParSsMenuParService();
			for (Journee journee : this.getJours()) {
				nbPlatMaxParService.calculMax(journee.getServiceChoixNbPlats());
			}
		}
	}

	// ajout de plat vide au un sous-menu pour en obtenir le nombre max donnée
	private void ajoutPlatVide(SousMenu ssMenu, Integer max){
		List<Plat> plats = ssMenu.getChoix();

		int nbPlats = plats.size();

		if (nbPlats == 0) {
			ssMenu.setTypeVide(true);
		}

		for (int i= nbPlats;  i < max; i++ ) {
			plats.add(Plat.platVide());
		}
	}


	private void completeSsMenu(Service service) {
		String serviceName = service.getName();

		List<SousMenu> menuComplet = new ArrayList<>();
		System.out.println(nbPlatMaxParService);
		NbPlatParSsMenu nbPlatMax = nbPlatMaxParService.get(serviceName);

		int nbSsMenu = nbPlatMax.getMaxKey();

		Iterator<SousMenu> iterator = null;
		List<SousMenu> menuIncomplet = service.getMenu();
		if (menuIncomplet != null) {
			iterator = service.getMenu().iterator();
		}

		SousMenu ssMenu = null;
		Integer rankSsMenu;

		if (iterator != null && iterator.hasNext()) {
			ssMenu = iterator.next();
			rankSsMenu = ssMenu.getRank();
		} else {
			rankSsMenu = -1;
		}

		for (int rank = 0; rank <= nbSsMenu; rank++){
			int nbMax = nbPlatMax.get(rank);
			SousMenu newSousMenu;

			if (rankSsMenu == rank) {
				newSousMenu = ssMenu;

				if (iterator != null && iterator.hasNext()) {
					ssMenu = iterator.next();
					rankSsMenu = ssMenu.getRank();
				} else {
					rankSsMenu = -1;
				}
			} else {
				// pas de sous-menu pour ce rank on le creer
				newSousMenu = service.makeSousMenu(rank, false);
			}

			ajoutPlatVide(newSousMenu, nbMax);
			menuComplet.add(newSousMenu);
		}
		service.setMenu(menuComplet);
	}

	private void completeSsMenu(Journee journee){
		for (Service service : journee.getDestinations()) {
			completeSsMenu(service);
		}
	}

}
