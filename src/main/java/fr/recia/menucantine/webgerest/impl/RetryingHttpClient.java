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

import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.exception.WebgerestRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;


@Slf4j
@RequiredArgsConstructor
@Component
public class RetryingHttpClient {

    private final TokenProvider tokenProvider;
    private final HttpClient httpClient;


    private static final int MAX_ATTEMPTS = 2;

    public ServiceDTO call(String url, String uai, String datemenu, int service) throws WebgerestRequestException {
        if (tokenProvider.isBlacklisted(url)) {
            throw new WebgerestRequestException("Erreur lors de l'authentification. Token invalide encore en cache.");
        }
        return callWithRetry(url, uai, datemenu, service, MAX_ATTEMPTS);
    }

    private ServiceDTO callWithRetry(String url, String uai, String datemenu, int service, int attemptsLeft)
            throws WebgerestRequestException {
        String token = tokenProvider.getToken(url);
        try {
            return httpClient.call(url, uai, datemenu, service, token);
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode().value() == 401 && attemptsLeft > 1) {
                tokenProvider.refreshToken(url);
                return callWithRetry(url, uai, datemenu, service, attemptsLeft - 1);
            }
            HttpRequest request = exception.getRequest();
            URI uri = request != null ? request.getURI() : null;
            throw new WebgerestRequestException("Erreur innatendue lors de la requête "
                    + uri + "\nCode erreur retourné : " + exception.getStatusCode().value());
        }
    }
}
