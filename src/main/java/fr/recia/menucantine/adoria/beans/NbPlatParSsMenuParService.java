package fr.recia.menucantine.adoria.beans;

import java.util.Hashtable;

/**
 * Association: pour chaque service donne le nombre de plat par sous menu
 * @author legay
 *
 */
public class NbPlatParSsMenuParService extends Hashtable<String, NbPlatParSsMenu>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2984649791310236653L;

	// TODO VERIFIER QUE C'est encore utile
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
