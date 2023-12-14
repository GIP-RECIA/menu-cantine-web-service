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
	
	private LocalDate dateJour(Integer jour) {
		return jours.get(jour).getDate();
	}
	
	private String formatDate(Integer jour){
		return dateJour(jour).format(formatter);
	}

	/**
	 * Le netoyage consiste à nettoyer chaque jour et à supprimer les jours vide.
	 * @return
	 */
	public void clean() {
		// Enlever les jours vide et nettoyer les jours avec du contenu
		for (Iterator<Journee> iterator = jours.iterator(); iterator.hasNext();) {
			Journee journee = (Journee) iterator.next();
			if (journee != null) {
				if (journee.isVide()) {
					iterator.remove();
				} else {
					journee.clean();
				}
			}
		}

		//Mettre à jour le nombre de jours une fois les jours vides enlevés
		this.nbJours = jours.size();
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

	/**
	 * Complète un sous-menu en ajoutant des plats vides jusqu'au nombre voulu
	 * @param ssMenu Le sous-menu auquel on veut ajouter un plat
	 * @param max Le nombre de plats que doit contenir le sous-menu au final
	 */
	private void ajoutPlatVide(SousMenu ssMenu, Integer max){
		List<Plat> plats = ssMenu.getChoix();

		int nbPlats = plats.size();

		if (nbPlats == 0) {
			ssMenu.setTypeVide(true);
		}

		for (int i= nbPlats;  i < max; i++ ) {
			ssMenu.addChoix(Plat.platVide());
		}
	}


	private void completeSsMenu(Service service) {
		String serviceName = service.getName();

		List<SousMenu> menuComplet = new ArrayList<>();
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

		// TODO : à cause du front les plats vides ne fonctionne pas sur le premier sous-menu donc il faut en ajouter un vide
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
