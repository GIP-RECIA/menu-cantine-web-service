/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine.adoria.beans;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class Labels implements Serializable, Cloneable {

	private static final long serialVersionUID = 4037048636409313674L;
	private static final Pattern PV = Pattern.compile("\\s*;\\s*");
	private static final Logger log = LoggerFactory.getLogger(Labels.class);
	private static final Map<String, Labels> labelByName = new HashMap<String, Labels>();
	
	int ordre;
	String nom;
	String logo;
	
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
						label.logo =  scannerLine.next();
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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
