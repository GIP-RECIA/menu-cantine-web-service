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
 * Association: pour chaque service donne le nombre de plats par sous menu
 * @author legay
 */
public class NbPlatParSsMenuParService extends Hashtable<String, NbPlatParSsMenu>{
	private static final long serialVersionUID = -2984649791310236653L;

	public void calculMax(NbPlatParSsMenuParService src){
		src.forEach((service, rankCompte) -> {
			NbPlatParSsMenu rcMax = this.get(service);
			if (rcMax == null) {
				(rcMax = new NbPlatParSsMenu()).putAll(rankCompte);
				this.put(service, rcMax);
			} else {
				rcMax.calculMax(rankCompte);
			}
		});
		
	}

}
