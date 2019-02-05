package fr.recia.menucantine.adoria.data;

import java.util.List;

import lombok.Data;

@Data
public class Reponse {
	String cycleMenuName;
	List<Journee> dates;
}
