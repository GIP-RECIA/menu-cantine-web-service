package fr.recia.menucantine.adoria.beans;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class Plat {
	String name;
	String family; // family et subFamily peut etre inutile ?
	String subFamily;
	
	@JsonInclude(Include.NON_NULL)
	List<String> allergens;
	
	@JsonInclude(Include.NON_NULL)
	List<Nutrition> nutritions;
	

	Integer familyRank; // donne peut-être le rang  dans le menu la place()
	
	void clean(){
		if (allergens != null && allergens.isEmpty()) {
			allergens = null;
		}
		if (nutritions != null) {
			for (Iterator<Nutrition> iterator = nutritions.iterator(); iterator.hasNext();) {
				Nutrition nut = (Nutrition) iterator.next();
				if (nut != null && nut.value == null){
					iterator.remove();
				}
				
			}
			if (nutritions.isEmpty()) {
				nutritions= null;
			}
		}
	}
	
}
