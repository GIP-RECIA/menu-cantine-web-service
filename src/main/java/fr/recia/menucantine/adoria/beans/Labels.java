package fr.recia.menucantine.adoria.beans;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.Getter;




@Data
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Labels {
	private static final String DEFAULT_FILE = "classpath:labels.csv";
	private static final Pattern PV = Pattern.compile("\\s*;\\s*");
	
	private static final Logger log = LoggerFactory.getLogger(Labels.class);
	//private static List<Labels> allLabels = new ArrayList<>();
	
	
	private static Map<String, Labels> labelByName = new HashMap<String, Labels>();
	
	int ordre;
	String nom;
	String logo;
	
	static {
		loadFile(DEFAULT_FILE);
	}
	
	
	public static void loadFile (String fileName)  {
		try (Scanner scannerFile = new Scanner(ResourceUtils.getFile(fileName))) {
			log.info("load labels nomenclature file {}",  fileName );
			
			scannerFile.nextLine();
			
			
			while (scannerFile.hasNextLine()) {
				try (Scanner scannerLine = new Scanner(scannerFile.nextLine())){
				//	System.out.print(scannerLine);
					scannerLine.useDelimiter(PV);
					if (scannerLine.hasNext()) {
						Labels label = new Labels();
						label.ordre = scannerLine.nextInt();
						label.nom = scannerLine.next();
						//label.logo =  scannerLine.next();
						labelByName.put(label.nom , label);
						
					}
				}
			}
		} catch (FileNotFoundException e) {
			log.error("loadFile Labels filename=" + fileName, e);
		}
		
	}

	static public Labels getLabel(String name) {
		Labels l = labelByName.get(name);
		if (l == null) {
			log.warn("Label inexistant {}", name);
			l = new Labels();
			l.nom = name;
			l.ordre = labelByName.size() + 1;
			labelByName.put(name, l);
		}
		return l;
	}
	
}
