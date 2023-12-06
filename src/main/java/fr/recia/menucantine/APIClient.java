package fr.recia.menucantine;

import fr.recia.menucantine.dto.ServiceDTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class APIClient {

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

    /*
    Requête pour récupérer l'URL à utiliser pour les toutes les autres requêtes
    Pas besoin de token pour celle-ci. Prend en entrée l'UAI de l'établissement.
     */
    public DynamicURLResponse initializeAndGetDynamicEndpoint(String uai){
        return webClient.get()
                .uri(initialQueryURL+"?rne={uai}", uai)
                .retrieve()
                .bodyToMono(DynamicURLResponse.class)
                .block();
    }

    /*
    Requête permettant de récupérer le token que l'on va utiliser pour s'authentifier dans
    les autres requêtes. Chagre directement les infos nécéssaires depuis la config
     */
    public AuthResponse authenticateAndGetToken(String url) {
        System.out.println("Récupération d'un nouveau token");
        return webClient.get()
                .uri(url + authEndpoint + "?client_id={client_id}&client_secret={client_secret}",
                        client_id, client_secret)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .block();
    }

    /*
    Fait un appel à l'API pour récupérer un menu pour un UAI donné, une date donnée et un service donné
    La date est de la forme suivante : YYYYMMJJ
    Le service est un entier compris entre 1 et 4 inclus (petit déjeuner, déjeuner, gouter, diner)
     */
    public ServiceDTO makeAuthenticatedApiCallGetMenuInternal(String url, String uai, String datemenu, int service) {
        System.out.println("Arrivée dans la méthode makeAuthenticatedApiCallGetMenu");
        return webClient.get()
                .uri(url + menuEndpoint+"?rne={rne}&date_menu={datemenu}&service={service}",
                                uai, datemenu, service)
                .header("Authorization", authToken.get(url))
                .retrieve()
                .bodyToMono(ServiceDTO.class)
                .block();
    }

    /*
    Se charge de préparer tout ce qui est nécéssaire pour faire un appel à l'API, puis fait l'appel
    1) Cherche si on a déjà l'URL associée à l'UAI ; si ce n'est pas le cas, fait une requête pour la récupérer
    2) Effectue la requête avec l'url et le token, et si on obtient en retour un 401, autrement dit que le token
    n'est plus valide, récupère un nouveau token et rejoue la requête
     */
    public ServiceDTO makeAuthenticatedApiCallGetMenu(String uai, String datemenu, int service) {

        // Première étape : vérification du cache avant de faire la requête
        final LocalDate today = LocalDate.now();
        final LocalDate dateRequete = LocalDate.parse(datemenu, DateTimeFormatter.ofPattern("yyyyMMdd"));
        final CacheKeyRequete cacheKeyRequete = new CacheKeyRequete(uai, datemenu, service);

        // On va regarder dans un cache ou l'autre en fonction de la date actuelle par rapport à la date de la requête
        // Cache permanent : requête sur une date passée
        if(today.isAfter(dateRequete)){
            ServiceDTO serviceDTO = cacheManager.getCache("permanent").get(cacheKeyRequete, ServiceDTO.class);
            if(serviceDTO != null){
                System.out.println("Récupération de la requête depuis le cache permanent");
                return serviceDTO;
            }
        }
        // Cache requetes : requête sur une date future
        else{
            ServiceDTO serviceDTO = cacheManager.getCache("requetes").get(cacheKeyRequete, ServiceDTO.class);
            if(serviceDTO != null){
                System.out.println("Récupération de la requête depuis le cache requetes");
                return serviceDTO;
            }
        }

        // Si on passe par là, c'est que la requête n'était pas dans le cache
        System.out.println("Requête non stockée dans le cache. On va faire appel à l'API.");

        // Deuxième étape : regarder si on a l'URL associée à l'UAI
        if(!dynamicURL.containsKey(uai)){
            System.out.println("Nouvel UAI, pas d'association connue");
            dynamicURL.put(uai, initializeAndGetDynamicEndpoint(uai).getContenu());
        }
        final String url = dynamicURL.get(uai);

        //Troisième étape : lancer la requête, et se réauthentifier si le token est expiré
        ServiceDTO serviceDTO = null;
        try {
            serviceDTO = makeAuthenticatedApiCallGetMenuInternal(url, uai, datemenu, service);
            System.out.println("Requête réussie");
        }catch(WebClientResponseException webClientResponseException){
            System.out.println("Erreur");
            // Si erreur 401 on recupère un nouveau token et on rééssaye une fois
            if(webClientResponseException.getStatusCode().value() == 401){
                System.out.println("Nouvelle tentative");
                authToken.put(url, authenticateAndGetToken(url).getToken());
                System.out.println("TOKEN : "+this.authToken);
                serviceDTO = makeAuthenticatedApiCallGetMenuInternal(url, uai, datemenu, service);
            }
        }

        //Avant de retourner la valeur on la place dans le bon cache
        if(today.isAfter(dateRequete)){
            System.out.println("Stockage de la requête dans le cache permanent");
            cacheManager.getCache("permanent").put(cacheKeyRequete, serviceDTO);
        }else{
            System.out.println("Stockage de la requête dans le cache requetes");
            cacheManager.getCache("requetes").put(cacheKeyRequete, serviceDTO);
        }
        return serviceDTO;
    }
}

