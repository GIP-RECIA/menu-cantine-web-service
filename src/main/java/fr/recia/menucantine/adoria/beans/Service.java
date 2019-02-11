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
	}
	
	String name;
	String serviceName;
	
	@JsonInclude(Include.NON_NULL)
	List<Plat> recipes;
	
	@JsonInclude(Include.NON_NULL)
	List<SousMenu> menu;
	
	String rank;
	
	@JsonIgnore
	RankCompte rankCompte = new RankCompte();
	
	RankCompte clean(){
		
	/*	recipes.forEach(Plat::clean); */
	/* recipes.forEach(plat -> plat.clean());
	 */
		if (recipes == null) return rankCompte;
		menu = new ArrayList<Service.SousMenu>();
		int oldRank = 0;
		List<Plat> plats = new ArrayList<>();

		for (Plat plat : recipes) {
			plat.clean();
			Integer rank = plat.getFamilyRank();
			if (oldRank < rank) {
				rankCompte.put(oldRank, plats.size());
				plats = new ArrayList<>();
				menu.add(new SousMenu(plats, rank));
				oldRank = rank;
			}
			plats.add(plat);
		}
		rankCompte.put(oldRank, plats.size());
		recipes = null;
		return rankCompte;
	}
}
