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
	
	/**
	 * regroupe les plats par sous menu triéés
	 * supprime les plat vide... 
	 * @return
	 */
	NbPlatParSsMenu clean(){
		
	/*	recipes.forEach(Plat::clean); */
	/* recipes.forEach(plat -> plat.clean());
	 */
		if (recipes == null) return rankCompte;
		
		menu = new ArrayList<>();
		int maxRank = 0;
		
		
		for (Plat plat : recipes) {
			plat.clean();
			Integer rank = plat.getFamilyRank();
		
			while (rank > maxRank) { // on creer un sous menu par rank possible 
				menu.add(new SousMenu(new ArrayList<>(), ++maxRank));
			}
			menu.get(rank-1).getChoix().add(plat);
		}
		for (SousMenu sousMenu : menu) {
			int nbPlats = sousMenu.getChoix().size();
			sousMenu.setNbPlats(nbPlats); 
			rankCompte.put(sousMenu.getRank(), nbPlats);
		}
		recipes = null;
		return rankCompte;
	}
}
