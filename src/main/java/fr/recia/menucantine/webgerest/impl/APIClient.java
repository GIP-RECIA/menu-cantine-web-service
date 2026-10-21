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
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
import fr.recia.menucantine.webgerest.CacheKeyRequete;
import fr.recia.menucantine.webgerest.IAPIClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Profile("!mock")
@Slf4j
@RequiredArgsConstructor
public class APIClient implements IAPIClient {

    private final UaiUrlResolver urlResolver;
    private final RetryingHttpClient retryingClient;
    private final ResponseCache responseCache;

    @Override
    public ServiceDTO makeAuthenticatedApiCallGetMenu(String uai, String datemenu, int service)
            throws UnknownUAIException, WebgerestRequestException {

        log.trace("Dans la méthode makeAuthenticatedApiCallGetMenu");

        final LocalDate today = LocalDate.now();
        final LocalDate dateRequete = LocalDate.parse(datemenu, DateTimeFormatter.ofPattern("yyyyMMdd"));
        final CacheKeyRequete cacheKeyRequete = new CacheKeyRequete(uai, datemenu, service);
        final boolean isPastDate = today.isAfter(dateRequete);

        ServiceDTO cached = responseCache.findError(cacheKeyRequete);
        if (cached != null) {
            log.debug("Récupération de la réponse de la requête depuis le cache erreur");
            return cached;
        }

        cached = isPastDate ? responseCache.findPermanent(cacheKeyRequete) : responseCache.findPending(cacheKeyRequete);
        if (cached != null) {
            log.debug("Récupération de la réponse de la requête depuis le cache {}", isPastDate ? "permanent" : "requetes");
            return cached;
        }

        log.debug("Requête non stockée dans le cache. On va faire appel à l'API.");
        String url = urlResolver.resolve(uai);

        ServiceDTO serviceDTO;
        try {
            serviceDTO = retryingClient.call(url, uai, datemenu, service);
        } catch (WebgerestRequestException exception) {
            responseCache.storeError(cacheKeyRequete, new ServiceDTO(null, 1, 0, null));
            throw exception;
        }

        if (serviceDTO.getError() == 0) {
            if (isPastDate) {
                responseCache.storePermanent(cacheKeyRequete, serviceDTO);
            } else {
                responseCache.storePending(cacheKeyRequete, serviceDTO);
            }
        } else {
            responseCache.storeError(cacheKeyRequete, serviceDTO);
        }

        log.debug("Retour de la réponse récupérée depuis l'API");
        return serviceDTO;
    }
}
