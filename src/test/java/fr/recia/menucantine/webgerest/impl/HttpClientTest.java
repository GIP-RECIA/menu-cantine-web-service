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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpClientTest {

    private MockWebServer server;
    private HttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        httpClient = new HttpClient(WebClient.builder().build());
        ReflectionTestUtils.setField(httpClient, "menuEndpoint", "/menus");
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void callRenvoieLeServiceDTOParseEtEnvoieLeTokenEnAuthorization() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setBody("{\"message\":null,\"error\":0,\"nbObjet\":1,\"contenu\":[]}")
                .addHeader("Content-Type", "application/json"));

        ServiceDTO result = httpClient.call(server.url("/").toString(), "UAI_TEST", "20261021", 2, "mon-token");

        assertEquals(0, result.getError());

        RecordedRequest request = server.takeRequest();
        assertEquals("mon-token", request.getHeader("Authorization"));
        assertTrue(request.getPath().contains("rne=UAI_TEST"));
        assertTrue(request.getPath().contains("date_menu=20261021"));
        assertTrue(request.getPath().contains("service=2"));
    }

    @Test
    void callLeveWebClientResponseExceptionSurUneErreurHttp() {
        server.enqueue(new MockResponse().setResponseCode(401));
        String url = server.url("/").toString();

        assertThrows(WebClientResponseException.class,
                () -> httpClient.call(url, "UAI_TEST", "20261021", 2, "mon-token"));
    }
}
