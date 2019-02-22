package fr.recia.menucantine.adoria.beans;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NonNull;

@Data
public class Service {
	
	
	
	@Data
	class SousMenu {
		
		@NonNull
		List<Plat> choix;
		@NonNull
		Integer rank;
		
		Integer nbPlats; 
		
		String typeVide;
	}
	
	String name;
	String serviceName;
	
	@JsonInclude(Include.NON_NULL)
	List<Plat> recipes;
	
	@JsonInclude(Include.NON_NULL)
	List<SousMenu> menu;
	
	
	String rank;
	
	@JsonIgnore
	NbPlatParSsMenu rankCompte = new NbPlatParSsMenu();
	
	SousMenu makeSousMenu(Integer rank, String typeVide){
		SousMenu sm =  new SousMenu(new ArrayList<>(), rank);
		sm.typeVide = typeVide;
		return sm;
	}
	
	NbPlatParSsMenu clean(){
		
	/*	recipes.forEach(Plat::clean); */
	/* recipes.forEach(plat -> plat.clean());
	 */
		if (recipes == null) return rankCompte;
		menu = new ArrayList<Service.SousMenu>();
		int oldRank = 0;
		List<Plat> plats = null;
		SousMenu ssMenu = null;
		int nbPlats = 0;
		
		for (Plat plat : recipes) {
			plat.clean();
			Integer rank = plat.getFamilyRank();
			if (oldRank < rank) {
				
				if (ssMenu != null) {
					nbPlats = plats.size();
					ssMenu.setNbPlats(nbPlats);
				} 
				
				rankCompte.put(oldRank, nbPlats);
				plats = new ArrayList<>();
				ssMenu = new SousMenu(plats, rank);
				menu.add(ssMenu);
				oldRank = rank;
			}
			plats.add(plat);
		}
		if (ssMenu != null) {
			nbPlats = plats.size();
			ssMenu.setNbPlats(nbPlats);
		}
		rankCompte.put(oldRank, nbPlats);
		recipes = null;
		return rankCompte;
	}
}
