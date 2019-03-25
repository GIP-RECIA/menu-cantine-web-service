package fr.recia.menucantine.adoria;

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
	
	
	WebClientResponseException webClientException;
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
	
	public String getJson(){
		if (webClientException != null) {
			return  webClientException.getResponseBodyAsString();
		}
		return null;
	}
	
	public Map<String, Object> getMap(){
		Map<String, Object>  err = new LinkedHashMap<String, Object>();
		if (webClientException != null) {
			err.put("ErrorCode", webClientException.getRawStatusCode());
			err.put("ErrorText", webClientException.getStatusCode());
			
			err.putAll(parser.parseMap(webClientException.getResponseBodyAsString()));
			
			if (requete != null) {
				err.put("Requete", requete);
			}
		}
		return err;
	}
	
}
