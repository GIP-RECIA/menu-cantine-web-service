package fr.recia.menucantine.adoria.data;

import java.util.Iterator;
import java.util.List;

import lombok.Data;

@Data
public class Reponse {
	String cycleMenuName;
	
	List<Journee> dates;
	
	public Reponse clean(){
		
		for (Iterator<Journee> iterator = dates.iterator(); iterator.hasNext();) {
			Journee journee = (Journee) iterator.next();
			if (journee != null) {
				journee.clean();
				if (journee.getDestinations().isEmpty()) {
					iterator.remove();
				}
			}
			
		}
		return this;
	}
}
