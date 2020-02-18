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
package fr.recia.menucantine.adoria;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import fr.recia.menucantine.adoria.beans.RequeteAdoria;

public class RestAdoriaClientException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4941524452580166415L;
	
	static JacksonJsonParser parser = new JacksonJsonParser();
	
	static enum TypeError{
		MissingParameter ("Missing parameters"),
		NotAllowed("Not Allowed in ENT"),
		UnknownGUID ("Unknown GUID"),
		NoDatas ("No datas");
		
		private String texte;
		
		private TypeError(String texte){
			this.texte = texte;
		}

		public String getTexte() {
			return texte;
		}
	}
	
	private Map<String, Object> mapErreur;
	
	
	WebClientResponseException webClientException;
	IOException ioException;
	
	RequeteAdoria requete;
	
	public RestAdoriaClientException(){
		super();
	}
	public RestAdoriaClientException(TypeError type){
		super();
		
	}
	public RestAdoriaClientException(WebClientResponseException webClientException, RequeteAdoria requete) {
		super();
		
		this.webClientException = webClientException;
		
		this.requete = requete;
	}
	public RestAdoriaClientException(IOException ioException, RequeteAdoria requete) {
		super();
		
		this.ioException = ioException;
		
		this.requete = requete;
	}
	public String getJson(){
		
		if (webClientException != null) {
			return  webClientException.getResponseBodyAsString();
		}
		
		return null;
	}
	
	public String getMessage(){
		String mess = getJson();
		if (mess == null) {
			return super.getMessage();
		}
		return mess;
	}
	
	public Map<String, Object> getMap(){
		
		Map<String, Object>  err = mapErreur;
		if (err == null) {
			mapErreur = err = new LinkedHashMap<String, Object>();
			if (webClientException != null) {
				
				err.put("ErrorCode", webClientException.getRawStatusCode());
				
				err.put("ErrorText", webClientException.getStatusCode());
				
				err.putAll(parser.parseMap(webClientException.getResponseBodyAsString()));
				
			} else if (ioException != null) {
				
				err.put("ErrorCode", "404");
				
				err.put("ErrorText", ioException.getLocalizedMessage());
			}
			if (requete != null) {
				
				err.put("Requete", requete);
			
			}
		}
		return err;
	}	
}
