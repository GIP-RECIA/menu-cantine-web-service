package fr.recia.menucantine.beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.recia.menucantine.adoria.beans.GemRcn;
import fr.recia.menucantine.adoria.beans.Journee;
import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import lombok.Data;

@Data
public class Semaine {
	@JsonIgnore 
	static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

	Integer nbJours;
	String debut;
	String fin;
	
	Requete requete;
	
	List<GemRcn> allGemRcn = GemRcn.getList();
	
	
	LocalDate previousWeek;
	LocalDate nextWeek;
	
	List<Journee> jours;
	
	public Semaine(){
		super();
	}
	
	public Semaine(ReponseAdoria menuSemaine, Requete requete) {
		super();
		setMenuSemaine(menuSemaine);
		setRequete(requete);
	}
	
	
	public void setMenuSemaine(ReponseAdoria menuSemaine) {
	
		if (menuSemaine == null) {
			nbJours = null;
			debut = null;
			fin = null;
			jours = null;
		} else {
			menuSemaine.clean();
			menuSemaine.complete();
			nbJours = menuSemaine.getNbJours();
			jours = menuSemaine.getDates();
			
			if (nbJours > 0 ) {
				debut = formatDate(0);
				if (nbJours > 1 ) {
					fin = formatDate(nbJours-1);
				}
			}
			
			if (menuSemaine.getPreviousWeekExportable()) {
				previousWeek = dateJour(0).minusDays(7);
			};
			if (menuSemaine.getNextWeekExportable()) {
				nextWeek = dateJour(0).plusDays(7);
			}
			 
		}
	}
	
	private LocalDate dateJour(Integer jour) {
		return jours.get(jour).getDate();
	}
	
	private String formatDate(Integer jour){
		return dateJour(jour).format(formatter);
	}

	
}
