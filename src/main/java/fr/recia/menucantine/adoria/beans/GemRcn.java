/**
 * Copyright (C) 2019 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2019 Pierre Legay <pierre.legay@recia.fr>
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class GemRcn {
	private static final String DEFAULT_FILE = "classpath:GemRcn.csv";
	private static final Pattern P_INT = Pattern.compile("\\s*,\\s*\"");
	private static final Pattern P_STR = Pattern.compile("\"?\\s*,\\s*\"");
	private static final Pattern P_END = Pattern.compile("\"(\\s*,\\s*\")?");
													
	private static final Logger log = LoggerFactory.getLogger(GemRcn.class);	
	
	static private Map<String, GemRcn> s2GemRcn = new HashMap<>();
	
	public static final GemRcn Autre = new  GemRcn("","", 0, "#fff");
	
	private static int maxCodeI = 0;
	
	static List<GemRcn> listAll ;
	
	String codeS;
	
	@Getter
	String comment;
	
	@Getter
	int codeI;
	
	@Getter
	String color;
	
	static {
		GemRcn.loadFile(DEFAULT_FILE);
	}
	
	public static void loadFile (String fileName)  {
		try (Scanner scannerFile = new Scanner(ResourceUtils.getFile(fileName))) {		
			while (scannerFile.hasNextLine()) {
				try (Scanner scannerLine = new Scanner(scannerFile.nextLine())){
					//scannerLine.useDelimiter("(\"?\\s*,\\s*\")|(\"\\s*$)");
					scannerLine.useDelimiter(P_INT);
					if (scannerLine.hasNext()) {
						int code = scannerLine.nextInt();
						scannerLine.useDelimiter(P_STR);
						String color =   scannerLine.next();
						String text =  scannerLine.next();
						scannerLine.useDelimiter(P_END);
						String comment =  scannerLine.next();
						new GemRcn(text, comment, code, color);
					}
				}
			}
		} catch (FileNotFoundException e) {
			log.error("loadFile GemRcn filename=" + fileName, e);
		}
	}
	
	private GemRcn(String codeS, String comment, int codeI, String color) {
		this.codeS = codeS;
		this.comment = comment;
		this.codeI = codeI;
		this.color = color;
		s2GemRcn.put(codeS, this);
		if (codeI > maxCodeI) {
			maxCodeI = codeI;
		}
		
	}
	
	static public GemRcn getGemRcn(String codeS) {
		GemRcn g = s2GemRcn.get(codeS);
		if (g == null) {
			log.warn("not found: {}", codeS);
		}
		return g == null ? Autre : g;
	}
	
	static public List<GemRcn> getList(){
		List<GemRcn> list = listAll;
		if (list == null || list.size() <= maxCodeI) {
			GemRcn [] tab = new GemRcn[maxCodeI + 1];
			for (GemRcn gr : s2GemRcn.values()) {
				tab[gr.codeI] = gr;
			}
			list = listAll = Arrays.asList(tab);
		}
		return list;
	}
	
	public static void main(String[] args) {
		for (GemRcn g : GemRcn.getList()){
			if (g != null) {
			System.out.print(g.codeI);
			System.out.println("|\t|" + g.color + "|\t|" + g.codeS + "|\t|" + g.comment + "|");
			} else {
				System.out.println("NULL");
			}
		}
	}
}
