package fr.recia.menucantine.adoria.beans;

import java.util.Hashtable;

/**
 * Donne le nombre de plats pour chaque sous-menu d'un service d'jour.
 * int sous-menu => int nbPlats
 * @author legay
 *
 */
public class NbPlatParSsMenu extends Hashtable<Integer, Integer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4312414121393987878L;

	/**
	 * Calcul le max pour chaque sous-menu du nombre de plats entre le {@link platParSsMenu} et this.
	 * this contient les maximums en sortie.
	 * @param platParSsMenu
	 */
	void calculMax(NbPlatParSsMenu platParSsMenu){
		if (this.isEmpty()) {
			this.putAll(platParSsMenu);
		} else {
			
			platParSsMenu.forEach(
					(cle, val) -> { 
						Integer valMax = this.get(cle);
						if (valMax == null || valMax < val) {
							this.put(cle, val);
						}
						
					});
			
		}
	}

	@Override
	public synchronized Integer get(Object key) {
		
		Integer i = super.get(key);
		if (i == null) return 0;
		return i;
	}
	 
}
