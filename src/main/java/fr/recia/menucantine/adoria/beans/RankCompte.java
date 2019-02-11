package fr.recia.menucantine.adoria.beans;

import java.util.Hashtable;

public class RankCompte extends Hashtable<Integer, Integer>{
	
	void calculMax(RankCompte rankCompte){
		if (this.isEmpty()) {
			this.putAll(rankCompte);
		} else {
			
			rankCompte.forEach(
					(cle, val) -> { 
						Integer valMax = this.get(cle);
						if (valMax == null || valMax < val) {
							this.put(cle, val);
						}
						
					});
			
		}
	}
	 
}
