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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Journee implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 440359812635582065L;

	@JsonIgnore 
	static DateTimeFormatter formatJour = DateTimeFormatter.ofPattern("cccc");//(FormatStyle.LONG);
	
	@JsonInclude(Include.NON_NULL)
	String jour;
	
	
	LocalDate date;
	

	@JsonFormat(pattern = "dd/MM/YYYY")
	public LocalDate getDate() {
		return date;
	}

	@JsonFormat()
	public void setDate(LocalDate date) {
		this.date = date;
	}

	List<Service> destinations;
	
	Boolean typeVide = false;
	
	public boolean isVide(){
		return typeVide;
	}
	
//	@JsonIgnore 
	NbPlatParSsMenuParService serviceChoixNbPlats = new NbPlatParSsMenuParService();
	
	/**
	 * Netoyage de chaque service de la journée, suppression des service vide.
	 * Calcul du nombre de plat proposé dans chaque sous-menu de chaque service.
	 * 
	 * Les services sont ordonnés suivant leurs rank
	 * @return
	 */
	public NbPlatParSsMenuParService clean(){
		if (jour == null && date != null) {
			
			jour = date.format(formatJour);

			for (Iterator<Service> iterator = destinations.iterator(); iterator.hasNext();) {
				
				Service service = (Service) iterator.next();
				
				if (service != null) {
					if (service.getRecipes().isEmpty()) {
						iterator.remove();
					} else {
						serviceChoixNbPlats.put(service.name, service.clean());
					}
				}
			}
			destinations.sort((s1, s2) -> {
					if (s1.rank != null) {
						return s1.rank.compareTo(s2.rank);
					}
					if (s2.rank != null) {
						return s2.rank.compareTo(s1.rank);
					}
				return 0;
			});
		}
		return serviceChoixNbPlats;
	}

	public void newclean(){

		for (Iterator<Service> iterator = destinations.iterator(); iterator.hasNext();) {

			Service service = (Service) iterator.next();

			if (service != null) {
				if (service.getTypeVide()) {
					iterator.remove();
				} else {
					serviceChoixNbPlats.put(service.name, service.newclean());
				}
			}
		}
		destinations.sort((s1, s2) -> {
			if (s1.rank != null) {
				return s1.rank.compareTo(s2.rank);
			}
			if (s2.rank != null) {
				return s2.rank.compareTo(s1.rank);
			}
			return 0;
		});

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Journee j =  (Journee) super.clone();
		j.serviceChoixNbPlats = (NbPlatParSsMenuParService) serviceChoixNbPlats.clone();
		if (destinations != null) {
			j.destinations =  new ArrayList<>(destinations.size());
			for (Service service : destinations) {
				j.destinations.add((Service) service.clone());
			}
		}
		return j;
	}
}
