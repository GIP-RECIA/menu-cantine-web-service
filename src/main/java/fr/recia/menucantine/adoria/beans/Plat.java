package fr.recia.menucantine.adoria.beans;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;



@Data
public class Plat {
	private static final Logger log = LoggerFactory.getLogger(Plat.class);	
	String name = " ";
	String family = " "; // se sont des blancs insecable
	String subFamily;
	Boolean typeVide = false;

	List<String> gemrcn;

	@JsonInclude(Include.NON_NULL)
	List<String> allergens;

	@JsonInclude(Include.NON_NULL)
	List<Nutrition> nutritions;

	Integer familyRank; // donne peut-être le rang dans le menu la place()

	static Plat platVide() {
		Plat p = new Plat();
		p.typeVide = true;
		return p;
	}

	boolean isVide() {
		return typeVide == true;
	}

	void clean() {
		if (allergens != null && allergens.isEmpty()) {
			allergens = null;
		}
		if (gemrcn != null && gemrcn.size() > 0) {
			
			gemrcn.replaceAll(codeS -> String.valueOf(GemRcn.getGemRcn(codeS).getCodeI()) );
			// gemrcn.stream().map(codeS ->
			// GemRcn.getGemRcn(codeS).getColor()).collect(Collectors.toList());
		}
		if (nutritions != null) {
			for (Iterator<Nutrition> iterator = nutritions.iterator(); iterator.hasNext();) {
				Nutrition nut = (Nutrition) iterator.next();
				if (nut != null && nut.value == null) {
					iterator.remove();
				}

			}
			if (nutritions.isEmpty()) {
				nutritions = null;
			}
		}
	}

}
