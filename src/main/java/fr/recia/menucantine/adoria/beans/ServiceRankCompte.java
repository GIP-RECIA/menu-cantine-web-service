package fr.recia.menucantine.adoria.beans;

import java.util.Hashtable;

public class ServiceRankCompte extends Hashtable<String, RankCompte>{
	
	void calculMax(ServiceRankCompte src){
		
	//	for (Entry<String, RankCompte> entry: src.entrySet()) {
		
	///		RankCompte rankCompte = entry.getValue();
		
		src.forEach((service, rankCompte) -> {
			RankCompte rcMax = this.get(service);
			if (rcMax == null ) {
				(rcMax = new RankCompte()).putAll(rankCompte);
				this.put(service, rcMax);
			} else {
				rcMax.calculMax(rankCompte);
			}
		});
		
	}

}
