package fr.recia.menucantine.adoria.beans;

import java.util.Hashtable;


public class NbPlatParSsMenuParService extends Hashtable<String, NbPlatParSsMenu>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2984649791310236653L;

	void calculMax(NbPlatParSsMenuParService src){
		
		src.forEach((service, rankCompte) -> {
			NbPlatParSsMenu rcMax = this.get(service);
			if (rcMax == null ) {
				(rcMax = new NbPlatParSsMenu()).putAll(rankCompte);
				this.put(service, rcMax);
			} else {
				rcMax.calculMax(rankCompte);
			}
		});
		
	}

}
