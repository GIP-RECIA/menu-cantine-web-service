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
	
	//utile a la lecture du flux adoria
	String cycleMenuName;
	
	//complement pour l'ecriture
	@JsonInclude(Include.NON_NULL)
	Integer nbJours;
	
	List<Journee> dates;
	
	@JsonIgnore
	ServiceRankCompte maxRankCompte= new ServiceRankCompte();

	
	public ReponseAdoria clean(){
		if (nbJours == null) { // clean n'a pas encore été fait
			if (dates == null || dates.isEmpty()) {
				nbJours = 0;
			} else {
				for (Iterator<Journee> iterator = dates.iterator(); iterator.hasNext();) {
					Journee journee = (Journee) iterator.next();
					if (journee != null) {
						
						ServiceRankCompte sRankCompte =journee.clean();
						
						if (journee.getDestinations().isEmpty()) {
							iterator.remove();
						} else {
							maxRankCompte.calculMax(sRankCompte);
						}
						
					}
				}
				nbJours = dates.size();
			}
		}
		return this;
	}
	
	public void complete(){
		if (maxRankCompte != null) {
			while (dates.size() < 6) {
				dates.add(new Journee());
			}
			for (Journee journee : dates) {
				
				if (journee.isVide()) continue;
				
				for (Service service : journee.getDestinations()) {
					
					String serviceName = service.getName();
					RankCompte rankCompte =  service.getRankCompte();
					RankCompte rankCompteMax = maxRankCompte.get(serviceName);
					
					List<SousMenu> menuComplet = new ArrayList<>();
					
					int rankCourant = 0;
					
					for (SousMenu ssMenu : service.getMenu()) {
						Integer rank = ssMenu.getRank();
						Integer nbMax;
						
						while (rank > rankCourant) {
							nbMax = rankCompteMax.get(rankCourant);
							if (nbMax != null && nbMax != 0 ) {
								
								SousMenu newSsMenu = service.makeSousMenu(rankCourant);
								
								List<Plat> plats = newSsMenu.choix;
								
								for (int i = 0 ; i < nbMax; i++) {
									plats.add(new Plat());
								}
								
								menuComplet.add(newSsMenu);
							}
							rankCourant++;
						}
						// rank = rankCourant;
						nbMax = rankCompteMax.get(rank);
						for (int i = rankCompte.get(rank); i < nbMax ; i++) {
							ssMenu.choix.add(new Plat());
						}
						menuComplet.add(ssMenu);
						rankCourant++;
						
					};
					
					service.setMenu(menuComplet);
				};
			};
			maxRankCompte = null;
		}
	}
	
}
