package fr.recia.menucantine.adoria;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.adoria.beans.RequeteAdoria;
import reactor.core.publisher.Mono;


// @Configuration
@ManagedBean
@Component("adoriaWeb")
public class RestAdoriaWebClient implements IRestAdoriaClient {
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaWebClient.class);	

	@Autowired
    private WebClient webClient ;
	
	public RestAdoriaWebClient() {
		super();
	}
	
	@Override
	@Cacheable("requetes")
	public  ReponseAdoria call(RequeteAdoria requete) throws RestAdoriaClientException{	
		try {	
			Mono<ReponseAdoria> reponse =  webClient.post()
				.uri("https://api.adoria.com/Api/EProduction/CycleMenu/GetCycleMenusForEnt")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header("AdoriaClientKey", "3513501c-bebc-4f1f-b937-d426a6a76ce6")
				.header("Guid", "46390997-f811-45cc-b86d-1291d36e753f")
				.syncBody(requete)
				.retrieve()
				.bodyToMono(ReponseAdoria.class);
		 
				/*	.onStatus(HttpStatus::is4xxClientError, 
					response -> response.bodyToMono(Void.class))
					.onStatus(HttpStatus::is5xxServerError, response -> response.body(null))
				 */	
					
			log.debug("reponse NOT IN CACHE");
			return reponse.block().clean();
		} catch (WebClientResponseException  e) {
			throw new RestAdoriaClientException(e, requete);
		}
	}
	 
	
	
}
