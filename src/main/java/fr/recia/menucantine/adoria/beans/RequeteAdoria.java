package fr.recia.menucantine.adoria.beans;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequeteAdoria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6366607238702531121L;
	
	String siteInternalCode;
	Integer weekNumber;
	Integer year;
}
