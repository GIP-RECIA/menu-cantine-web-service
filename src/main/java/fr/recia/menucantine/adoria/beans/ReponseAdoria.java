package fr.recia.menucantine.adoria.beans;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class ReponseAdoria {
	
	
	String cycleMenuName;
	
	@JsonInclude(Include.NON_NULL)
	Integer nbJours;
	
	List<Journee> dates;
	
	@JsonIgnore
	ServiceRankCompte maxRankCompte= new ServiceRankCompte();
	
	public ReponseAdoria clean(){
		if (nbJours == null) { // clean n'a pas encore été fait
			if (dates == null || dates.isEmpty()) {
				nbJours = 0;
			} else {
				for (Iterator<Journee> iterator = dates.iterator(); iterator.hasNext();) {
					Journee journee = (Journee) iterator.next();
					if (journee != null) {
						
						ServiceRankCompte sRankCompte =journee.clean();
						
						if (journee.getDestinations().isEmpty()) {
							iterator.remove();
						} else {
							maxRankCompte.calculMax(sRankCompte);
						}
						
					}
				}
				nbJours = dates.size();
			}
		}
		return this;
	}
	
	
}
