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


@Data
public class Service implements Serializable, Cloneable {

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

	public Service(){}

	public Service(String name, int rank, Boolean typeVide){
		this.name = name;
		this.rank = rank;
		this.typeVide = typeVide;
	}
	
	public SousMenu makeSousMenu(Integer rank, Boolean typeVide){
		SousMenu sm =  new SousMenu(new ArrayList<>(), rank);
		sm.typeVide = typeVide;
		return sm;
	}

	/**
	 * Regroupe les plats par sous menu triés et supprime les plats vide
	 * @return Le nombre de plats par sous-menu
	 */
	public NbPlatParSsMenu clean(){
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
