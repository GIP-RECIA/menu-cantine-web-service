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
package fr.recia.menucantine.webgerest.impl;

import fr.recia.menucantine.exception.WebgerestRequestException;
import fr.recia.menucantine.webgerest.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import javax.cache.CacheManager;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String CACHE_ERREUR = "erreur";
    private static final String CACHE_TOKEN = "token";

    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final Map<String, String> authToken = new ConcurrentHashMap<>();
    @Value("${api.auth-endpoint}")
    private String authEndpoint;
    @Value("${api.client_secret}")
    private String client_secret;
    @Value("${api.client_id}")
    private String client_id;

    public String getToken(String url) {
        return authToken.get(url);
    }

    public boolean isBlacklisted(String url) {
        return cacheManager.getCache(CACHE_TOKEN).get(url) != null;
    }

    public void refreshToken(String url) throws WebgerestRequestException {
        log.trace("Dans la méthode refreshToken");
        AuthResponse authResponse = webClient.get()
                .uri(url + authEndpoint + "?client_id={client_id}&client_secret={client_secret}",
                        client_id, client_secret)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
        if (authResponse == null) {
            log.debug("Réponse vide lors de l'authentification.");
            cacheManager.getCache(CACHE_TOKEN).put(url, CACHE_ERREUR);
            throw new WebgerestRequestException("Erreur lors de l'authentification. Réponse vide.");
        }
        if (authResponse.getError() == 1) {
            log.debug("Mise en cache erreur de la requête pour récupérer le token. Authentification échouée.");
            cacheManager.getCache(CACHE_TOKEN).put(url, CACHE_ERREUR);
            throw new WebgerestRequestException("Erreur lors de l'authentification. Message retourné : "
                    + authResponse.getMessage());
        }
        authToken.put(url, authResponse.getToken());
    }
}
