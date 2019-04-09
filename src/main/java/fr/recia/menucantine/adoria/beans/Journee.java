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
public class Journee implements Serializable {
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
	
	boolean isVide(){
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
	NbPlatParSsMenuParService clean(){
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
}
