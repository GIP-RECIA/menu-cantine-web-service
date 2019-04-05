package fr.recia.menucantine.beans;

import lombok.Data;


@Data
public class Requete {
	Integer semaine;
	Integer annee;
	Integer jour;
	String dateJour;	
	String uai;
}

