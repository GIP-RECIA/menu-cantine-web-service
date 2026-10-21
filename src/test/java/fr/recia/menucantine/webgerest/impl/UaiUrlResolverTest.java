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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UaiUrlResolverTest {

    private MockWebServer server;
    private UaiUrlResolver resolver;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        resolver = new UaiUrlResolver(WebClient.builder().build());
        ReflectionTestUtils.setField(resolver, "initialQueryURL", server.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void resolveRenvoieLUrlQuandLaReponseEstEnSucces() throws UnknownUAIException {
        server.enqueue(new MockResponse()
                .setBody("{\"contenu\":\"https://etablissement.example\",\"error\":0}")
                .addHeader("Content-Type", "application/json"));

        String url = resolver.resolve("UAI_TEST");

        assertEquals("https://etablissement.example", url);
    }

    @Test
    void resolveLeveUnknownUAIExceptionQuandLaReponseIndiqueUneErreur() {
        server.enqueue(new MockResponse()
                .setBody("{\"contenu\":null,\"error\":1}")
                .addHeader("Content-Type", "application/json"));

        assertThrows(UnknownUAIException.class, () -> resolver.resolve("INCONNU"));
    }

    @Test
    void resolveLeveUnknownUAIExceptionQuandLeCorpsEstVide() {
        // Cas réel rencontré en production : le serveur répond sans corps, .block() renvoie null.
        server.enqueue(new MockResponse());

        assertThrows(UnknownUAIException.class, () -> resolver.resolve("VIDE"));
    }

    @Test
    void resolveNeRappelleEnPasLeServeurPourUnUaiDejaResolu() throws UnknownUAIException {
        server.enqueue(new MockResponse()
                .setBody("{\"contenu\":\"https://etablissement.example\",\"error\":0}")
                .addHeader("Content-Type", "application/json"));

        resolver.resolve("UAI_TEST");
        resolver.resolve("UAI_TEST");

        assertEquals(1, server.getRequestCount(), "Le deuxième appel doit être servi depuis le cache, sans requête HTTP");
    }

    @Test
    void resolveNeRappelleEnPasLeServeurPourUnUaiDejaConnuCommeInvalide() {
        server.enqueue(new MockResponse()
                .setBody("{\"contenu\":null,\"error\":1}")
                .addHeader("Content-Type", "application/json"));

        assertThrows(UnknownUAIException.class, () -> resolver.resolve("INCONNU"));
        assertThrows(UnknownUAIException.class, () -> resolver.resolve("INCONNU"));

        assertEquals(1, server.getRequestCount(), "Un UAI déjà connu comme invalide ne doit pas redéclencher d'appel");
    }
}
