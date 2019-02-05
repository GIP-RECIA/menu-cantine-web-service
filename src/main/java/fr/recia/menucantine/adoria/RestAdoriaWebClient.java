package fr.recia.menucantine.adoria;

import java.util.Map;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;


@Configuration
@ManagedBean
public class RestAdoriaWebClient {
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaWebClient.class);	

	
	
	
	public RestAdoriaWebClient() {
		super();
	}

	
	
	@Autowired
    private WebClient webClient ;
	
	
	@Cacheable("requettes")
	public  Map<String, Object> call(RestAdoriaRequetteMenu requette) throws RestAdoriaClientException{	
				try {	
					Mono<Map<String, Object>> reponse =  webClient.post()
							.uri("https://api.adoria.com/Api/EProduction/CycleMenu/GetCycleMenusForEnt")
							.accept(MediaType.APPLICATION_JSON)
							.contentType(MediaType.APPLICATION_JSON)
							.header("AdoriaClientKey", "3513501c-bebc-4f1f-b937-d426a6a76ce6")
							.header("Guid", "46390997-f811-45cc-b86d-1291d36e753f")
							.syncBody(requette)
							.retrieve()
							
					/*	.onStatus(HttpStatus::is4xxClientError, 
									response -> response.bodyToMono(Void.class))
							.onStatus(HttpStatus::is5xxServerError, response -> response.body(null))
					*/		
							.bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {});
					log.debug("reponse NOT IN CACHE");
					return reponse.block();
				} catch (WebClientResponseException  e) {
					throw new RestAdoriaClientException(e, requette);
				}
	}
	 
}
