package fr.recia.menucantine.adoria.data;

import java.util.Iterator;
import java.util.List;

import lombok.Data;

@Data
public class Journee {
	String date;
	List<Service> destinations;
	
	void clean(){
		for (Iterator<Service> iterator = destinations.iterator(); iterator.hasNext();) {
			Service service = (Service) iterator.next();
			if (service != null) {
				if (service.getRecipes().isEmpty()) {
					iterator.remove();
				} else {
					service.clean();
				}
			}
		}
	}
}
