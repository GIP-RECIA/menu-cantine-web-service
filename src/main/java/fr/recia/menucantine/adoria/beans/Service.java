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
public class Service implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2763451889039709686L;

	@Data
	class SousMenu implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6998445821377161534L;
		
		@NonNull
		List<Plat> choix;
		@NonNull
		Integer rank;
		
		Integer nbPlats; 
		
		Boolean typeVide = false;
	}
	
	String name;
	String serviceName;
	
	@JsonInclude(Include.NON_NULL)
	List<Plat> recipes;
	
	@JsonInclude(Include.NON_NULL)
	List<SousMenu> menu;
	
	
	Integer rank;
	
	@JsonIgnore
	NbPlatParSsMenu rankCompte = new NbPlatParSsMenu();
	
	SousMenu makeSousMenu(Integer rank, Boolean typeVide){
		SousMenu sm =  new SousMenu(new ArrayList<>(), rank);
		sm.typeVide = typeVide;
		return sm;
	}
	
	/**
	 * regroupe les plats par sous menu triés
	 * supprime les plat vide... 
	 * 
	 * renvoie le nombre de plats par sous menu
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
}
