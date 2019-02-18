package fr.recia.menucantine.adoria.beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Journee {
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
	
	String typeVide;
	boolean isVide(){
		if (destinations == null) {
			if (typeVide==null) {
				typeVide = "vide1";
				}
			return true;
		}
		return false;
	}
	
	@JsonIgnore 
	NbPlatParSsMenuParService nbPlatParSsMenuParService = new NbPlatParSsMenuParService();
	
	
	NbPlatParSsMenuParService clean(){
		if (jour == null && date != null) {
			
			jour = date.format(formatJour);
			
			for (Iterator<Service> iterator = destinations.iterator(); iterator.hasNext();) {
				Service service = (Service) iterator.next();
				if (service != null) {
					if (service.getRecipes().isEmpty()) {
						iterator.remove();
					} else {
						nbPlatParSsMenuParService.put(service.name, service.clean());
					}
				}
			}
		}
		return nbPlatParSsMenuParService;
	}
}
