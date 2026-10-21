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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpClient {

    private final WebClient webClient;

    @Value("${api.menu-endpoint}")
    private String menuEndpoint;

    public ServiceDTO call(String url, String uai, String datemenu, int service, String token) {
        log.trace("Dans la méthode call");
        return webClient.get()
                .uri(url + menuEndpoint + "?rne={rne}&date_menu={datemenu}&service={service}",
                        uai, datemenu, service)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(ServiceDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}
