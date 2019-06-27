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

import java.time.LocalDate;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.adoria.beans.RequeteAdoria;
import fr.recia.menucantine.beans.RequeteHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import reactor.core.publisher.Mono;


// @Configuration
@ManagedBean
@Component("adoriaWeb")
public class RestAdoriaWebClient implements IRestAdoriaClient {
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaWebClient.class);	

	@Autowired
    private WebClient webClient ;
	
	@Value("${adoria.url}")
	private String webServiceUrl;
	
	@Value("${adoria.client-key}")
	private String clientKey;
	
	@Value("${adoria.guid}")
	private String guid;
	
	@Autowired
	private CacheManager cacheManager;
	private Cache cache;
	private Cache cacheErreur;
	
	public RestAdoriaWebClient() {
		super();
	}
	
	
	/**
	 * 
	 * Appel du web service adoria :
	 * 3 caches sont utilisés.
	 * 	Celui par annotation ('requetes') cache les réponses correctes sur une duré moyenne  (les menus pouvant changer dans la semaine)
	 *	Celui des réponses définitives ('permanent') cache toutes les réponses et met a jour régulièrement celle de la semaine courante.
	 *	Celui des erreurs ('erreur') cache les réponses en erreur sur une courte période,
	 *
	 */
	@Override
	@Cacheable("requetes")
	public  ReponseAdoria call(RequeteAdoria requete) throws RestAdoriaClientException{	
		ReponseAdoria reponseAdoria;
		try {
			log.debug("reponse NOT IN CACHE: {}", cacheManager.getClass());
			
			RestAdoriaClientException e = (RestAdoriaClientException) getFromCache(getCacheErreur(), requete);
			if (e != null) throw e;
			
			LocalDate now = LocalDate.now();
			if (	RequeteHelper.semaine(now) > requete.getWeekNumber()
				||  now.getYear() > requete.getYear()) {
					
				reponseAdoria = (ReponseAdoria) getFromCache(getCachePerm(), requete);
				if (reponseAdoria != null) return reponseAdoria;
			}
			
			
			Mono<ReponseAdoria> reponse =  webClient.post()
				.uri(webServiceUrl)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header("AdoriaClientKey", clientKey)
				.header("Guid", guid)
				.syncBody(requete)
				.retrieve()
				.bodyToMono(ReponseAdoria.class);
		 
				/*	.onStatus(HttpStatus::is4xxClientError, 
					response -> response.bodyToMono(Void.class))
					.onStatus(HttpStatus::is5xxServerError, response -> response.body(null))
				 */	
				
			reponseAdoria = reponse.block().clean();
			getCachePerm().put(new Element(requete, reponseAdoria));
			return reponseAdoria;
			
		} catch (WebClientResponseException  e) {
			reponseAdoria = (ReponseAdoria) getFromCache(getCachePerm(),requete);
			
			if (reponseAdoria != null ) {
					return reponseAdoria;
			}
			
			RestAdoriaClientException ee = new RestAdoriaClientException(e, requete);
			getCacheErreur().put(new Element(requete, ee));
			throw ee;
		}
	}
	 
	private Object getFromCache(Cache cache, RequeteAdoria requete) {
		Object reponse;
		Element cacheElem = cache.get(requete);
		if (cacheElem != null ) {
			reponse = cacheElem.getObjectValue()			;
			if (reponse != null) {
				return reponse;
			}
		}
		return null;
	}
	
	private Cache getCachePerm(){
		Cache aux = cache;
		if (aux == null) {
			aux = cache = cacheManager.getCache("permanent");
		}
		return aux;
	}
	private Cache getCacheErreur(){
		Cache aux = cacheErreur;
		if (aux == null) {
			aux = cacheErreur = cacheManager.getCache("erreur");
		}
		return aux;
	}
}
