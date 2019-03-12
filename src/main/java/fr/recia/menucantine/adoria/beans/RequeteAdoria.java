package fr.recia.menucantine.adoria.beans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequeteAdoria {
	String siteInternalCode;
	Integer weekNumber;
	Integer year;
}
