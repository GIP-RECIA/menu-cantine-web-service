package fr.recia.menucantine.adoria.beans;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.recia.menucantine.adoria.beans.Service.SousMenu;
import lombok.Data;

@Data
public class ReponseAdoria {
	
	
	@JsonIgnore
	static int NB_JOUR_MAX = 6;
	static int NB_LIGNE_MAX = 3;
	static int NB_TYPE_LIGNE_POSSIBLE = 6;
	static String TYPE_FORMAT = " vide%d ";
	
	//utile a la lecture du flux adoria
	String cycleMenuName;
	
	//complement pour l'ecriture
	@JsonInclude(Include.NON_NULL)
	Integer nbJours;
	
	List<Journee> dates;
	
	@JsonIgnore
	NbPlatParSsMenuParService nbPlatMaxParTypeLigne[]= new NbPlatParSsMenuParService[NB_TYPE_LIGNE_POSSIBLE];
	
	NbPlatParSsMenuParService nbPlatMaxChoix(int jour, int nbLine, int nbJour) {
		NbPlatParSsMenuParService res;
		int indice = 0;
		int minCol = nbJour / nbLine ; // nombre minimal de colonne par ligne. 
		int supCol = nbJour % nbLine; // nombre de ligne avec une colonne suplémentaire
		int line = 0;
		int lastJour = 0; // dernier jour de la ligne en cour
		
		if (nbLine == 1) {
			indice = 1;
		} else {
			while (jour >= lastJour) {
				line++;
				lastJour +=  minCol + (supCol > 0 ? 1 : 0);
			}
			indice = line + (nbLine * (nbLine -1)/ 2);
		}
		res = nbPlatMaxParTypeLigne[--indice];
		if (res == null) {
			res = new NbPlatParSsMenuParService();
			nbPlatMaxParTypeLigne[indice] = res;
		}
		return res;
	}

	
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
	
	private void initNbPlatMaxParTypeLine(){
		if (dates != null) {
			int jour = 0;
			
			for (Journee journee : dates) {
				for (int nbLine = 1; nbLine <= 3 ; nbLine++) {
					nbPlatMaxChoix(jour, nbLine, NB_JOUR_MAX).calculMax(journee.nbPlatParSsMenuParService);
				}
				jour++;
			}
		}
	}
	
	
	private Integer[] calculAllMax(int jour, String serviceName, Integer rank) {
		Integer allMax [] = new Integer[NB_LIGNE_MAX+1];
		for (int nbLigne = 1; nbLigne <= NB_LIGNE_MAX; nbLigne++) {
			
			NbPlatParSsMenuParService nbPlatMaxParService = nbPlatMaxChoix(jour, nbLigne, NB_JOUR_MAX);
			NbPlatParSsMenu platParSsMenuMax = nbPlatMaxParService.get(serviceName);
			
			if (platParSsMenuMax == null) continue ;
			
			Integer nbMax = platParSsMenuMax.get(rank);
/*			if (ssMenu != null && nbMax != null && nbMax > 0) {
				ssMenu.typeVide += String.format(TYPE_FORMAT, nbLigne);
				
			}
	*/
			allMax[nbLigne] = nbMax;
		}
		return allMax;
	}
	
	private void ajoutPlatVide(SousMenu ssMenu,  Integer allMax[]){
		List<Plat> plats = ssMenu.choix;
		
		int nbPlats = plats.size();
		
		if (nbPlats == 0) {
			StringBuilder sb = new StringBuilder();
			for (int line = 1; line < allMax.length; line++) {
				if (allMax[line] != null && allMax[line] != 0 ) {
					sb.append(String.format(TYPE_FORMAT, line));
				}
			}
			ssMenu.setTypeVide(sb.toString());
		}
		
		for (int i= nbPlats;  i < allMax[1]; i++ ) {
			Plat plat = Plat.platVide(String.format(TYPE_FORMAT, 1));
			plats.add(plat);
			for (int line = 2; line < allMax.length; line++) {
				if (allMax[line] != null && allMax[line] > i ) {
					plat.addTypeVide(String.format(TYPE_FORMAT, line));
				}
			}
		}
	}
	
	private void completeSsMenu(int jour, Journee journee){
		
		for (Service service : journee.getDestinations()) {
			String serviceName = service.getName();
	
			List<SousMenu> menuComplet = new ArrayList<>();
			
			int rankCourant = 0;
			
			for (SousMenu ssMenu : service.getMenu()) {
				Integer rank = ssMenu.getRank();
				Integer nbMax[] = calculAllMax(jour, serviceName, rank);

				while (rank > rankCourant) { 
						// il manque tout un sous menu
					
					
					Integer nbMaxNew[] = calculAllMax(jour, serviceName, rankCourant);
					
					if (nbMaxNew[1] != null && nbMaxNew[1] != 0 ) {
						SousMenu newSsMenu = service.makeSousMenu(rankCourant, "");
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
	
	public void complete(){
		if (nbPlatMaxParTypeLigne != null) {
			
			initNbPlatMaxParTypeLine();
			
	/*		while (dates.size() < 6) {
				Journee jVide = new Journee();
				jVide.setTypeVide(" vide2 vide3  ");
				dates.add(jVide);
			}
	*/		
			int jour = 0;
			for (Journee journee : dates) {
				
				if ( ! journee.isVide()){
					completeSsMenu(jour++, journee);
				}
				
			};
		}
	}
	
}
