/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine.adoria.beans;

import java.util.Hashtable;

/**
 * Donne le nombre de plats pour chaque sous-menu d'un service d'un jour.
 * int sous-menu => int nbPlats
 * @author legay
 */
public class NbPlatParSsMenu extends Hashtable<Integer, Integer>{

	private static final long serialVersionUID = 2971745506304642901L;
	private int maxKey = 0;

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
	/**
	 * Renvoie le nombre de plat d'un sous-menu donné.
	 * s'il est vide renvoie 0.
	 */
	public synchronized Integer get(Object key) {
		Integer i = super.get(key);
		if (i == null) return 0;
		return i;
	}
	
	

	@Override
	public synchronized Integer put(Integer key, Integer value) {
		if (key > maxKey) maxKey = key;
		return super.put(key, value);
	}

	public synchronized int getMaxKey() {
		return maxKey;
	}
}
