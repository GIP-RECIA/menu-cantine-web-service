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
package fr.recia.menucantine.webgerest;

import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
import lombok.Data;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class APIClient {

    private static final Logger log = LoggerFactory.getLogger(APIClient.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WebClient webClient;

    @Value("${api.initial-query-url}")
    private String initialQueryURL;

    @Value("${api.auth-endpoint}")
    private String authEndpoint;

    @Value("${api.menu-endpoint}")
    private String menuEndpoint;

    @Value("${api.client_secret}")
    private String client_secret;

    @Value("${api.client_id}")
    private String client_id;

    private Map<String,String> dynamicURL; // Associe un UAI a une URL
    private Map<String,String> authToken; // Associe une URL à un token utilisé pour les requêtes

    public APIClient(WebClient.Builder webClientBuilder) {
        this.dynamicURL = new HashMap<>();
        this.authToken = new HashMap<>();
    }

    /**
     * Requête pour récupérer l'URL à utiliser pour les toutes les autres requêtes. Pas besoin de token pour celle-ci.
     * @param uai L'UAI de l'établissement à chercher
     * @return Un objet contenant l'url à utiliser
     */
    public DynamicURLResponse initializeAndGetDynamicEndpoint(String uai){
        log.trace("Dans la méthode initializeAndGetDynamicEndpoint");
        return webClient.get()
                .uri(initialQueryURL+"?rne={uai}", uai)
                .retrieve()
                .bodyToMono(DynamicURLResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    /**
     * Requête permettant de récupérer le token que l'on va utiliser pour s'authentifier dans les autres requêtes.
     * Charge directement les infos nécéssaires pour l'authentification depuis la config.
     * @param url L'url sur laquelle il faut faire la requête
     * @return Un objet contenant le token à utiliser (valable environ 24h)
     */
    public AuthResponse authenticateAndGetToken(String url) {
        log.trace("Dans la méthode authenticateAndGetToken");
        return webClient.get()
                .uri(url + authEndpoint + "?client_id={client_id}&client_secret={client_secret}",
                        client_id, client_secret)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    /**
     * Fait un appel à l'API pour récupérer un menu pour un UAI donné, une date donnée et un service donné.
     * Le token n'est pas passé en paramètre mais chargé depuis les attributs de la classe.
     * @param url L'url sur laquelle il faut faire la requête
     * @param uai L'UAI de l'établissement recherché
     * @param datemenu La date recherchée sous forme de String (YYYYMMJJ)
     * @param service Un entier compris entre 1 et 4 inclus (petit déjeuner, déjeuner, gouter, diner)
     * @return Un objet qui représente le résultat de l'appel sans aucune transformation
     */
    public ServiceDTO makeAuthenticatedApiCallGetMenuInternal(String url, String uai, String datemenu, int service) {
        log.trace("Dans la méthode makeAuthenticatedApiCallGetMenuInternal");
        return webClient.get()
                .uri(url + menuEndpoint+"?rne={rne}&date_menu={datemenu}&service={service}",
                                uai, datemenu, service)
                .header("Authorization", authToken.get(url))
                .retrieve()
                .bodyToMono(ServiceDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    /**
     * Se charge de préparer tout ce qui est nécessaire pour faire un appel à l'API, puis fait l'appel.
     * 1) Cherche si on a déjà la requête dans le cache, et la récupère si c'est le cas
     * 2) Cherche si on a déjà l'URL associée à l'UAI ; si ce n'est pas le cas, fait une requête pour la récupérer
     * 3) Effectue la requête avec l'url et le token
     * 4) Si on obtient en retour un 401 (token probablement invalide), récupère un nouveau token et rejoue la requête
     * 5) Stocke dans le bon cache la requête avant la retourner
     * @param uai L'UAI de l'établissement recherché
     * @param datemenu La date recherchée sous forme de String (YYYYMMJJ).
     * @param service Un entier compris entre 1 et 4 inclus (petit déjeuner, déjeuner, gouter, diner)
     * @return Un objet qui représente le résultat de l'appel sans aucune transformation
     */
    public ServiceDTO makeAuthenticatedApiCallGetMenu(String uai, String datemenu, int service) throws UnknownUAIException, WebgerestRequestException {

        log.trace("Dans la méthode makeAuthenticatedApiCallGetMenu");
        log.debug("Recherche de la réponse de la requête dans le cache");

        // Première étape : vérification du cache avant de faire la requête
        final LocalDate today = LocalDate.now();
        final LocalDate dateRequete = LocalDate.parse(datemenu, DateTimeFormatter.ofPattern("yyyyMMdd"));
        final CacheKeyRequete cacheKeyRequete = new CacheKeyRequete(uai, datemenu, service);

        // Au préalable, on regarde si la requête n'est pas dans le cache d'erreur
        ServiceDTO serviceDTO = null;
        Element element = cacheManager.getCache("erreur").get(cacheKeyRequete);
        if(element != null){
            serviceDTO = (ServiceDTO) element.getObjectValue();
            log.debug("Récupération de la réponse de la requête depuis le cache erreur");
            return serviceDTO;
        }

        // On va regarder dans un cache ou l'autre en fonction de la date actuelle par rapport à la date de la requête
        // Cache permanent : requête sur une date passée
        if(today.isAfter(dateRequete)){
            element = cacheManager.getCache("permanent").get(cacheKeyRequete);
            if(element != null){
                serviceDTO = (ServiceDTO) element.getObjectValue();
                log.debug("Récupération de la réponse de la requête depuis le cache permanent");
                return serviceDTO;
            }
        }
        // Cache requetes : requête sur une date future
        else{
            element = cacheManager.getCache("requetes").get(cacheKeyRequete);
            if(element != null){
                serviceDTO = (ServiceDTO) element.getObjectValue();
                log.debug("Récupération de la réponse de la requête depuis le cache requetes");
                return serviceDTO;
            }
        }

        // Deuxième étape : regarder si on a l'URL associée à l'UAI
        if(!dynamicURL.containsKey(uai)){
            log.debug("Nouvel UAI, pas d'association connue");
            final DynamicURLResponse dynamicURLResponse = initializeAndGetDynamicEndpoint(uai);
            if(dynamicURLResponse.getError() == 0){
                dynamicURL.put(uai, dynamicURLResponse.getContenu());
                log.debug("Association trouvée pour l'UAI " + uai);
            }else{
                dynamicURL.put(uai, null);
                throw new UnknownUAIException("Pas d'URL connue pour l'UAI " + uai);
            }
        }
        final String url = dynamicURL.get(uai);
        if(url == null){
            throw new UnknownUAIException("Pas d'URL connue pour l'UAI " + uai);
        }

        // Si pour l'URL on a un token invalide on sort directement
        if(cacheManager.getCache("token").get(url) != null){
            log.debug("Requête pour retrouver le token stockée dans le cache erreur.");
            throw new WebgerestRequestException("Erreur lors de l'authentification. Token invalide encore en cache.");
        }

        // Si on passe par là, c'est que la requête n'était pas dans le cache
        log.debug("Requête non stockée dans le cache. On va faire appel à l'API.");

        //Troisième étape : lancer la requête, et se réauthentifier si le token est expiré
        try {
            serviceDTO = makeAuthenticatedApiCallGetMenuInternal(url, uai, datemenu, service);
        }catch(WebClientResponseException webClientResponseException){
            log.debug("Erreur dans la requête : code erreur " + webClientResponseException.getStatusCode().value());
            // Si erreur 401 on récupère un nouveau token et on réessaye une fois
            if(webClientResponseException.getStatusCode().value() == 401){
                log.debug("Récupération d'un nouveau token");
                AuthResponse authResponse = authenticateAndGetToken(url);
                // Si on a une erreur sur l'authentification on retourne directement et on passe le token en cache erreur
                if(authResponse.getError() == 1){
                    log.debug("Mise en cache erreur de la requête pour récupérer le token. Authentification échouée.");
                    cacheManager.getCache("token").put(new Element(url, "erreur"));
                    throw new WebgerestRequestException("Erreur lors de l'authentification. Message retourné : "
                            + authResponse.getMessage());
                }
                // Sinon on réessaye
                authToken.put(url, authResponse.getToken());
                log.debug("Nouvelle requête avec le nouveau token");
                try{
                    serviceDTO = makeAuthenticatedApiCallGetMenuInternal(url, uai, datemenu, service);
                }catch(WebClientResponseException webClientInternalResponseException){
                    log.debug("Nouvelle erreur, stockage de la requête dans le cache erreur");
                    // On passe volontairement un service non null au cache pour indiquer qu'on a un service en erreur associé à cette requête
                    cacheManager.getCache("erreur").put(new Element(cacheKeyRequete, new ServiceDTO(null, 1, 0, null)));
                    throw new WebgerestRequestException("Erreur innatendue lors de la requête "
                            + webClientInternalResponseException.getRequest().getURI() + "\nCode erreur retourné : "
                            + webClientInternalResponseException.getStatusCode().value());
                }
            }
            else{
                cacheManager.getCache("erreur").put(new Element(cacheKeyRequete, new ServiceDTO(null, 1, 0, null)));
                throw new WebgerestRequestException("Erreur innatendue lors de la requête "
                        + webClientResponseException.getRequest().getURI() + "\nCode erreur retourné : "
                        + webClientResponseException.getStatusCode().value());
            }
        }

        // On vérifie qu'on a pas reçu une réponse en erreur, si c'est le cas on ne la place pas dans le cache
        if(serviceDTO.getError() == 0){
            //Avant de retourner la valeur on la place dans le bon cache
            if(today.isAfter(dateRequete)){
                log.debug("Stockage de la réponse de la requête dans le cache permanent");
                cacheManager.getCache("permanent").put(new Element(cacheKeyRequete, serviceDTO));
            }else{
                log.debug("Stockage de la réponse de la requête dans le cache requetes");
                cacheManager.getCache("requetes").put(new Element(cacheKeyRequete, serviceDTO));
            }
        }
        // Une requête qui n'est pas en erreur mais retourne une erreur doit aussi être placée dans le cache erreur
        else{
            cacheManager.getCache("erreur").put(new Element(cacheKeyRequete, serviceDTO));
        }

        log.debug("Retour de la réponse récupérée depuis l'API");
        return serviceDTO;
    }
}

