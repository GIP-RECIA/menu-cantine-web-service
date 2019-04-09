package fr.recia.menucantine.adoria.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private static final long serialVersionUID = 3442267876530079710L;
	
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
	
	
	private Integer calculMaxPlat(String serviceName, Integer rank) {
		//Integer allMax [] = new Integer[NB_LIGNE_MAX+1];
		NbPlatParSsMenu platParSsMenuMax = nbPlatMaxParService.get(serviceName);
		Integer nbMax = 0;	
		if (platParSsMenuMax != null) {
			nbMax = platParSsMenuMax.get(rank);
		}
		return nbMax;
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
	
	private void completeSsMenu(Journee journee){
		
		for (Service service : journee.getDestinations()) {
			String serviceName = service.getName();
	
			List<SousMenu> menuComplet = new ArrayList<>();
			
			// no du sous-menu courant
			int rankCourant = 0;
			
			for (SousMenu ssMenu : service.getMenu()) {
					// no reel du sous-menu
				Integer rank = ssMenu.getRank();
				Integer nbMax = calculMaxPlat(serviceName, rank);

					
				while (rank > rankCourant) { 
						// il manque encore un sous menu (il faut completer avec des ssmenus vide)
					Integer nbMaxNew = calculMaxPlat(serviceName, rankCourant);
					
					if (nbMaxNew != null && nbMaxNew != 0 ) {
						SousMenu newSsMenu = service.makeSousMenu(rankCourant, false);
						menuComplet.add(newSsMenu);
						// on le remplie avec le nombre de plat vide qu'il faut.
						
							ajoutPlatVide(newSsMenu, nbMaxNew);
						
					}
					rankCourant++;
				}	
					// on ajoute des plat vide suplementaire si besoin
				
				ajoutPlatVide(ssMenu, nbMax);
				
				menuComplet.add(ssMenu);
				rankCourant++;
			}
			service.setMenu(menuComplet);
		}
	}
	
	/**
	 * complete les sous-menus avec des plat vide pour qu'ils ai tous la même taille.
	 */
	public void complete(){
		
		initNbPlatMaxParService();
		if (dates != null) {
			for (Journee journee : dates) {
				
				if ( ! journee.isVide()){
					completeSsMenu(journee);
				}
			};
		}
	}
	
}
