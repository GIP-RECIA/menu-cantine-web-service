package fr.recia.menucantine;

import fr.recia.menucantine.dto.ServiceDTO;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    private final WebClient webClient;

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
        this.webClient = webClientBuilder.build();
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
                .block();
    }

    /**
     * Se charge de préparer tout ce qui est nécéssaire pour faire un appel à l'API, puis fait l'appel.
     * 1) Cherche si on a déjà la requête dans le cache, et la récupère si c'est le cas
     * 2) Cherche si on a déjà l'URL associée à l'UAI ; si ce n'est pas le cas, fait une requête pour la récupérer
     * 3) Effectue la requête avec l'url et le token
     * 4) Si on obtient en retour un 401 (token problablement invalide), récupère un nouveau token et rejoue la requête
     * 5) Stocke dans le bon cache la requête avant la retourner
     * @param uai L'UAI de l'établissement recherché
     * @param datemenu La date recherchée sous forme de String (YYYYMMJJ).
     * @param service Un entier compris entre 1 et 4 inclus (petit déjeuner, déjeuner, gouter, diner)
     * @return Un objet qui représente le résultat de l'appel sans aucune transformation
     */
    public ServiceDTO makeAuthenticatedApiCallGetMenu(String uai, String datemenu, int service) {

        log.trace("Dans la méthode makeAuthenticatedApiCallGetMenu");
        log.debug("Recherche de la réponse de la requête dans le cache");

        // Première étape : vérification du cache avant de faire la requête
        final LocalDate today = LocalDate.now();
        final LocalDate dateRequete = LocalDate.parse(datemenu, DateTimeFormatter.ofPattern("yyyyMMdd"));
        final CacheKeyRequete cacheKeyRequete = new CacheKeyRequete(uai, datemenu, service);

        // On va regarder dans un cache ou l'autre en fonction de la date actuelle par rapport à la date de la requête
        // Cache permanent : requête sur une date passée
        if(today.isAfter(dateRequete)){
            ServiceDTO serviceDTO = cacheManager.getCache("permanent").get(cacheKeyRequete, ServiceDTO.class);
            if(serviceDTO != null){
                log.debug("Récupération de la réponse de la requête depuis le cache permanent");
                return serviceDTO;
            }
        }
        // Cache requetes : requête sur une date future
        else{
            ServiceDTO serviceDTO = cacheManager.getCache("requetes").get(cacheKeyRequete, ServiceDTO.class);
            if(serviceDTO != null){
                log.debug("Récupération de la réponse de la requête depuis le cache requetes");
                return serviceDTO;
            }
        }

        // Si on passe par là, c'est que la requête n'était pas dans le cache
        log.debug("Requête non stockée dans le cache. On va faire appel à l'API.");

        // Deuxième étape : regarder si on a l'URL associée à l'UAI
        if(!dynamicURL.containsKey(uai)){
            log.debug("Nouvel UAI, pas d'association connue");
            dynamicURL.put(uai, initializeAndGetDynamicEndpoint(uai).getContenu());
        }
        final String url = dynamicURL.get(uai);

        //Troisième étape : lancer la requête, et se réauthentifier si le token est expiré
        ServiceDTO serviceDTO = null;
        try {
            serviceDTO = makeAuthenticatedApiCallGetMenuInternal(url, uai, datemenu, service);
        }catch(WebClientResponseException webClientResponseException){
            log.debug("Erreur dans la requête : code erreur "+webClientResponseException.getStatusCode().value());
            // Si erreur 401 on recupère un nouveau token et on rééssaye une fois
            if(webClientResponseException.getStatusCode().value() == 401){
                log.debug("Récupération d'un nouveau token");
                authToken.put(url, authenticateAndGetToken(url).getToken());
                System.out.println("TOKEN : "+this.authToken);
                log.debug("Nouvelle requête avec le nouveau token");
                serviceDTO = makeAuthenticatedApiCallGetMenuInternal(url, uai, datemenu, service);
            }
            // TODO : que faire dans le cas ou on a une autre erreur ?
            else{
                log.error("Erreur innatendue lors de la requête");
            }
        }

        //Avant de retourner la valeur on la place dans le bon cache
        if(today.isAfter(dateRequete)){
            log.debug("Stockage de la réponse de la requête dans le cache permanent");
            cacheManager.getCache("permanent").put(cacheKeyRequete, serviceDTO);
        }else{
            log.debug("Stockage de la réponse de la requête dans le cache requetes");
            cacheManager.getCache("requetes").put(cacheKeyRequete, serviceDTO);
        }

        log.debug("Retour de la réponse récupérée depuis l'API");
        return serviceDTO;
    }
}

