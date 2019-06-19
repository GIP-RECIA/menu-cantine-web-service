package fr.recia.menucantine.beans;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.recia.menucantine.adoria.beans.GemRcn;
import fr.recia.menucantine.adoria.beans.Journee;
import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import lombok.Data;

@Data
public class Semaine {
	@JsonIgnore 
	static public DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
//	static public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM");

	Integer nbJours;
	String debut;
	String fin;
	
	Requete requete;
	
	List<GemRcn> allGemRcn = GemRcn.getList();
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	LocalDate previousWeek;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
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
			menuSemaine.clean(); // devrait déjà être clean
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
				previousWeek = RequeteHelper.jourMemeSemaine(dateJour(0), 5).minusDays(7);
			};
			if (menuSemaine.getNextWeekExportable()) {
				nextWeek = RequeteHelper.jourMemeSemaine(dateJour(0), 1).plusDays(7);
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
