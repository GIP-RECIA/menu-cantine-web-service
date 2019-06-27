/**
 * Copyright (C) 2019 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2019 Pierre Legay <pierre.legay@recia.fr>
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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;



@Data
public class Plat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6660011102902753149L;
	private static final Logger log = LoggerFactory.getLogger(Plat.class);	
	String name = " ";
	String family = " "; // se sont des blancs insecable
	String subFamily;
	Boolean typeVide = false;

	List<String> gemrcn;

	@JsonInclude(Include.NON_NULL)
	List<String> allergens;

	@JsonInclude(Include.NON_NULL)
	List<Nutrition> nutritions;

	Integer familyRank; // donne peut-être le rang dans le menu la place()

	static Plat platVide() {
		Plat p = new Plat();
		p.typeVide = true;
		return p;
	}

	boolean isVide() {
		return typeVide == true;
	}

	/**
	 * Netoyage d'un plat:
	 * annule les liste des allergens vide.
	 * remplace les gemrcn par leurs code.
	 * supprime les info de nutrition null.
	 */
	void clean() {
		if (allergens != null && allergens.isEmpty()) {
			allergens = null;
		}
		if (gemrcn != null && gemrcn.size() > 0) {
			
			gemrcn.replaceAll(codeS -> String.valueOf(GemRcn.getGemRcn(codeS).getCodeI()) );
			// gemrcn.stream().map(codeS ->
			// GemRcn.getGemRcn(codeS).getColor()).collect(Collectors.toList());
		}
		if (nutritions != null) {
			for (Iterator<Nutrition> iterator = nutritions.iterator(); iterator.hasNext();) {
				Nutrition nut = (Nutrition) iterator.next();
				if (nut != null && nut.value == null) {
					iterator.remove();
				}

			}
			if (nutritions.isEmpty()) {
				nutritions = null;
			}
		}
	}

}
