package fr.recia.menucantine.adoria;

import java.util.Map;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import fr.recia.menucantine.adoria.beans.RequetteAdoria;

public class RestAdoriaClientException extends Exception {
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
	}
	
	
	WebClientResponseException webClientException;
	RequetteAdoria requette;
	
	public RestAdoriaClientException(){
		super();
	}
	public RestAdoriaClientException(TypeError type){
		super();
		
	}
	public RestAdoriaClientException(WebClientResponseException webClientException, RequetteAdoria requette) {
		super();
		this.webClientException = webClientException;
		this.requette = requette;
	}
	
	public String getJson(){
		if (webClientException != null) {
			return  webClientException.getResponseBodyAsString();
		}
		return null;
	}
	
	public Map<String, Object> getMap(){
		Map<String, Object>  err = null;
		if (webClientException != null) {
			err =  parser.parseMap(webClientException.getResponseBodyAsString());
			err.put("ErrorCode", webClientException.getRawStatusCode());
			err.put("ErrorText", webClientException.getStatusCode());
			if (requette != null) {
				err.put("Requette", requette);
			}
		}
		return err;
	}
	
}
