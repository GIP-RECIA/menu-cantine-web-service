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
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenProviderTest {

    private MockWebServer server;
    private TokenProvider tokenProvider;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache tokenCache;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        server = new MockWebServer();
        server.start();

        when(cacheManager.getCache("token")).thenReturn(tokenCache);

        tokenProvider = new TokenProvider(WebClient.builder().build(), cacheManager);
        ReflectionTestUtils.setField(tokenProvider, "authEndpoint", "/auth");
        ReflectionTestUtils.setField(tokenProvider, "client_id", "test-client");
        ReflectionTestUtils.setField(tokenProvider, "client_secret", "test-secret");
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void getTokenRenvoieNullAvantToutRafraichissement() {
        assertNull(tokenProvider.getToken(server.url("/").toString()));
    }

    @Test
    void refreshTokenMetAJourLeTokenEnCasDeSucces() throws WebgerestRequestException {
        server.enqueue(new MockResponse()
                .setBody("{\"message\":null,\"token\":\"abc123\",\"error\":0}")
                .addHeader("Content-Type", "application/json"));

        String url = server.url("/").toString();
        tokenProvider.refreshToken(url);

        assertEquals("abc123", tokenProvider.getToken(url));
    }

    @Test
    void refreshTokenLeveWebgerestRequestExceptionEtBlackListeQuandLAuthEchoue() {
        server.enqueue(new MockResponse()
                .setBody("{\"message\":\"identifiants invalides\",\"token\":null,\"error\":1}")
                .addHeader("Content-Type", "application/json"));

        String url = server.url("/").toString();

        assertThrows(WebgerestRequestException.class, () -> tokenProvider.refreshToken(url));
        verify(tokenCache).put(any(Element.class));
        assertNull(tokenProvider.getToken(url), "Aucun token ne doit être retenu en cas d'échec");
    }

    @Test
    void refreshTokenLeveWebgerestRequestExceptionEtBlackListeQuandLeCorpsEstVide() {
        // Même piège que pour UaiUrlResolver : une réponse sans corps fait renvoyer null par .block().
        server.enqueue(new MockResponse());

        String url = server.url("/").toString();

        assertThrows(WebgerestRequestException.class, () -> tokenProvider.refreshToken(url));
        verify(tokenCache).put(any(Element.class));
    }

    @Test
    void isBlacklistedRepondSelonLeCacheToken() {
        String url = "http://exemple.invalide";

        when(tokenCache.get(url)).thenReturn(null);
        assertFalse(tokenProvider.isBlacklisted(url));

        when(tokenCache.get(url)).thenReturn(new Element(url, "erreur"));
        assertTrue(tokenProvider.isBlacklisted(url));
    }
}
