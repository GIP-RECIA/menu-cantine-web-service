package fr.recia.menucantine.adoria.beans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequetteAdoria {
	String siteInternalCode;
	Integer weekNumber;
	Integer year;
}
