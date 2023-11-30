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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NonNull;


@Data
public class Service implements Serializable, Cloneable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2763451889039709686L;
	
	String name;
	String serviceName;
	
	@JsonInclude(Include.NON_NULL)
	List<Plat> recipes;
	
	@JsonInclude(Include.NON_NULL)
	List<SousMenu> menu;
	
	
	Integer rank;
	
	@JsonInclude(Include.NON_NULL)
	Boolean typeVide;
	
	@JsonIgnore
	NbPlatParSsMenu rankCompte = new NbPlatParSsMenu();
	
	public SousMenu makeSousMenu(Integer rank, Boolean typeVide){
		SousMenu sm =  new SousMenu(new ArrayList<>(), rank);
		sm.typeVide = typeVide;
		return sm;
	}
	
	/**
	 * regroupe les plats par sous menu triés
	 * supprime les plat vide... 
	 * On creer des sous-menus intermediaire vide.
	 * Mais pas en fin de liste (il peut donc en manqué). 
	 * 
	 * renvoie le nombre de plats par sous menu
	 * Efface la liste des plats d'origines (ils sont placé par sous-menu).
	 * @return
	 */
	NbPlatParSsMenu clean(){
		
	/*	recipes.forEach(Plat::clean); */
	/* recipes.forEach(plat -> plat.clean());
	 */
		if (recipes == null) return rankCompte;
		
		menu = new ArrayList<>();
		
		recipes.forEach(plat -> { 
			plat.clean();
			Integer rank = plat.getFamilyRank();
		
			while (rank > menu.size()) { // on creer un sous menu par rank possible 
				menu.add(new SousMenu(new ArrayList<>(), menu.size()));
			}
			menu.get(rank-1).getChoix().add(plat);
		});

		for (SousMenu sousMenu : menu) {
			int nbPlats = sousMenu.getChoix().size();
			sousMenu.setNbPlats(nbPlats); 
			rankCompte.put(sousMenu.getRank(), nbPlats);
		}
		recipes = null;
		return rankCompte;
	}

	NbPlatParSsMenu newclean(){
		NbPlatParSsMenu nbPlatParSsMenu = new NbPlatParSsMenu();
		for (SousMenu sousMenu : menu) {
			int nbPlats = sousMenu.getChoix().size();
			sousMenu.setNbPlats(nbPlats);
			nbPlatParSsMenu.put(sousMenu.getRank(), nbPlats);
		}
		return nbPlatParSsMenu;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Service clone = (Service) super.clone();
		if (menu != null) {
			clone.menu = new ArrayList<SousMenu>(menu.size());
			for (SousMenu sousMenu : menu) {
				clone.menu.add((SousMenu) sousMenu.clone());
			}
		}
		if (rankCompte != null) {
			clone.rankCompte = (NbPlatParSsMenu) rankCompte.clone();
		}
		if (recipes != null) {
			clone.recipes = new ArrayList<Plat>(recipes.size());
			for (Plat p : recipes) {
				clone.recipes.add((Plat) p.clone());
			}
		}
		return clone;
	}
	
	
}
