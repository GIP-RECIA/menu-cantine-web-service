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

import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.webgerest.DynamicURLResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UaiUrlResolver {

    private static final String UAI_INCONNU = "";
    private final WebClient webClient;

    @Value("${api.initial-query-url}")
    private String initialQueryURL;

    private final Map<String, String> dynamicURL = new ConcurrentHashMap<>();

    public String resolve(String uai) throws UnknownUAIException {
        if (!dynamicURL.containsKey(uai)) {
            log.debug("Nouvel UAI, pas d'association connue");
            DynamicURLResponse dynamicURLResponse = getDynamiqueUrlResponse(uai);
            if (dynamicURLResponse != null && dynamicURLResponse.getError() == 0
                    && dynamicURLResponse.getContenu() != null) {
                dynamicURL.put(uai, dynamicURLResponse.getContenu());
                log.debug("Association trouvée pour l'UAI {}", uai);
            } else {
                dynamicURL.put(uai, UAI_INCONNU);
            }
        }
        String url = dynamicURL.get(uai);
        if (UAI_INCONNU.equals(url)) {
            throw new UnknownUAIException("Pas d'URL connue pour l'UAI " + uai);
        }
        return url;
    }

    private DynamicURLResponse getDynamiqueUrlResponse(String uai) {
        return webClient.get()
                .uri(initialQueryURL + "?rne={uai}", uai)
                .retrieve()
                .bodyToMono(DynamicURLResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
