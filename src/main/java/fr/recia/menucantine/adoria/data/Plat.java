package fr.recia.menucantine.adoria.data;

import java.util.List;

import lombok.Data;

@Data
public class Plat {
	String name;
	String family; // family et subFamily peut etre inutile
	String subFamily;
	
	List<String> allergens;
	List<Nutrition> nutritions;
	
	String familyRank; // donne peut-être le rang  dans le menu la place()
}
