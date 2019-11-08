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
package fr.recia.menucantine.adoria.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.recia.menucantine.adoria.beans.Service.SousMenu;
import lombok.Data;

@Data
public class ReponseAdoria implements Serializable {
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6097943202328775674L;
	

	//utile a la lecture du flux adoria
	String cycleMenuName;
	
	Boolean previousWeekExportable;
	Boolean nextWeekExportable;
	
	//complement pour l'ecriture
	@JsonInclude(Include.NON_NULL)
	Integer nbJours;
	
	List<Journee> dates;
	
	@JsonIgnore
	NbPlatParSsMenuParService nbPlatMaxParService ; 
	
	@JsonIgnore
	List<Service> serviceVideAvailable;
	
	/**
	 * Le netoyage consiste a netoyer chaque jour et a supprimer les jours vide.
	 * @return
	 */
	public ReponseAdoria clean(){
		if (nbJours == null) { // clean n'a pas encore été fait
			if (dates == null || dates.isEmpty()) {
				nbJours = 0;
			} else {
				for (Iterator<Journee> iterator = dates.iterator(); iterator.hasNext();) {
					Journee journee = (Journee) iterator.next();
					if (journee != null) {
						
						journee.clean();
						
						if (journee.getDestinations().isEmpty()) {
							iterator.remove();
						} 
					}
				}
				nbJours = dates.size();
			}
		}
		//TODO trier journee par date 
		return this;
	}
	
	private void initNbPlatMaxParService(){
		if (dates != null) {
			nbPlatMaxParService = new NbPlatParSsMenuParService();

			for (Journee journee : dates) {
				nbPlatMaxParService.calculMax(journee.serviceChoixNbPlats);
			}
		}
	}
	
	
	// ajout de plat vide au un sous-menu pour en obtenir le nombre max donnée
	private void ajoutPlatVide(SousMenu ssMenu,  Integer max){
		List<Plat> plats = ssMenu.choix;
		
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
		int currentSVide = 0;
		
		List<Service> destinations = new ArrayList<>();
		
		for (Service service : journee.getDestinations()) {
			
			Service sVide = serviceVideAvailable.get(currentSVide++);
		
			while (service.rank > sVide.rank) {
				//ajout d'un service vide
				destinations.add(sVide);
				completeSsMenu(sVide);
				sVide = serviceVideAvailable.get(currentSVide++);
			}
				
			destinations.add(service);
			completeSsMenu(service);
		}
		
		journee.setDestinations(destinations);
		
	}
	
	private void initServiceVideAvailabled(){
		Map<String, Service> map = new HashMap<>();
		for (Journee journee : dates) {
			for (Service service : journee.destinations){
				Service sVide = map.get(service.name);
				if (sVide == null) {
					sVide = new Service();
					sVide.name = service.name;
					sVide.rank = service.rank;
					sVide.typeVide = true;
					map.put(service.name, sVide);
				}
			}
		}
		serviceVideAvailable= new ArrayList<>(map.values());
		serviceVideAvailable.sort((s1, s2) -> Integer.compare(s1.rank, s2.rank));
	}
	
	/**
	 * complete les sous-menus avec des plats vides pour qu'ils aient tous la même taille.
	 */
	public void complete(){
		
		initNbPlatMaxParService();
		initServiceVideAvailabled();
		if (dates != null) {
			for (Journee journee : dates) {
				if ( ! journee.isVide()){
					completeSsMenu(journee);
				}
			};
		}
	}
	
}
