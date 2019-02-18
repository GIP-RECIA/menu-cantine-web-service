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
	static int NB_TYPE_LIGNE_POSSIBLE = 6;
	
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
	
	private void completeSsMenu(Journee journee, NbPlatParSsMenuParService nbPlatMaxParService, String typeFormat){
		
		for (Service service : journee.getDestinations()) {
			String serviceName = service.getName();
			NbPlatParSsMenu platParSsMenuActuel =  service.getRankCompte();
			NbPlatParSsMenu platParSsMenuMax = nbPlatMaxParService.get(serviceName);
			
			if (platParSsMenuMax == null) return ;
			
			List<SousMenu> menuComplet = new ArrayList<>();
			int rankCourant = 0;
			for (SousMenu ssMenu : service.getMenu()) {
				Integer rank = ssMenu.getRank();
				Integer nbMax;
				
				while (rank > rankCourant) {
					nbMax = platParSsMenuMax.get(rankCourant);
					if (nbMax != null && nbMax != 0 ) {
						SousMenu newSsMenu = service.makeSousMenu(rankCourant, typeFormat);
						
						List<Plat> plats = newSsMenu.choix;
						for (int i = 0 ; i < nbMax; i++) {
							plats.add(Plat.platVide(typeFormat));
						}
					
						menuComplet.add(newSsMenu);
					}
					rankCourant++;
				}
				nbMax = platParSsMenuMax.get(rank);
				int nbActuel = ssMenu.choix.size();
				
				for (int i = nbActuel; i < nbMax ; i++) {
					
					ssMenu.choix.add(Plat.platVide(typeFormat));
				}
				menuComplet.add(ssMenu);
				rankCourant++;
			}
			service.setMenu(menuComplet);
		}
	}
	
	public void complete(){
		if (nbPlatMaxParTypeLigne != null) {
			
			initNbPlatMaxParTypeLine();
			
			while (dates.size() < 6) {
				dates.add(new Journee());
			}
			
			int jour = 0;
			for (Journee journee : dates) {
				
				if (journee.isVide()) continue;
				
				for (int nbLine = 3; nbLine > 0; nbLine--) {
					completeSsMenu(
							journee, 
							nbPlatMaxChoix(jour, nbLine, NB_JOUR_MAX), 
							String.format("vide%d", nbLine));
				}
				jour++;
				
			};
		}
	}
	
}
