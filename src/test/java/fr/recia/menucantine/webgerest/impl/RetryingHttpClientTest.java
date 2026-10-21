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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetryingHttpClientTest {

    private static final String URL = "http://exemple.invalide";

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private RetryingHttpClient retryingHttpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static WebClientResponseException httpError(int status) {
        return WebClientResponseException.create(status, "erreur", new HttpHeaders(), new byte[0], null);
    }

    @Test
    void callLeveDirectementSiLUrlEstBlacklistee() throws WebgerestRequestException {
        when(tokenProvider.isBlacklisted(URL)).thenReturn(true);

        assertThrows(WebgerestRequestException.class,
                () -> retryingHttpClient.call(URL, "uai", "20261021", 2));

        verify(httpClient, never()).call(anyString(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void callRenvoieLeResultatDuPremierAppelQuandToutSePasseBien() throws WebgerestRequestException {
        ServiceDTO expected = new ServiceDTO("ok", 0, 1, null);
        when(tokenProvider.isBlacklisted(URL)).thenReturn(false);
        when(tokenProvider.getToken(URL)).thenReturn("token-actuel");
        when(httpClient.call(URL, "uai", "20261021", 2, "token-actuel")).thenReturn(expected);

        ServiceDTO result = retryingHttpClient.call(URL, "uai", "20261021", 2);

        assertSame(expected, result);
        verify(tokenProvider, never()).refreshToken(anyString());
    }

    @Test
    void callRafraichitLeTokenEtReessaieUneFoisSurUn401() throws WebgerestRequestException {
        ServiceDTO expected = new ServiceDTO("ok", 0, 1, null);
        when(tokenProvider.isBlacklisted(URL)).thenReturn(false);
        when(tokenProvider.getToken(URL)).thenReturn("ancien-token", "nouveau-token");
        when(httpClient.call(URL, "uai", "20261021", 2, "ancien-token")).thenThrow(httpError(401));
        when(httpClient.call(URL, "uai", "20261021", 2, "nouveau-token")).thenReturn(expected);

        ServiceDTO result = retryingHttpClient.call(URL, "uai", "20261021", 2);

        assertSame(expected, result);
        verify(tokenProvider, times(1)).refreshToken(URL);
        verify(httpClient, times(2)).call(anyString(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void callLeveWebgerestRequestExceptionSiLaSecondeTentativeEchoueAussi() throws WebgerestRequestException {
        when(tokenProvider.isBlacklisted(URL)).thenReturn(false);
        when(tokenProvider.getToken(URL)).thenReturn("ancien-token", "nouveau-token");
        when(httpClient.call(URL, "uai", "20261021", 2, "ancien-token")).thenThrow(httpError(401));
        when(httpClient.call(URL, "uai", "20261021", 2, "nouveau-token")).thenThrow(httpError(401));

        assertThrows(WebgerestRequestException.class,
                () -> retryingHttpClient.call(URL, "uai", "20261021", 2));

        verify(tokenProvider, times(1)).refreshToken(URL);
        verify(httpClient, times(2)).call(anyString(), anyString(), anyString(), anyInt(), any());
    }

    @Test
    void callLeveWebgerestRequestExceptionDirectementSurUneErreurNon401() throws WebgerestRequestException {
        when(tokenProvider.isBlacklisted(URL)).thenReturn(false);
        when(tokenProvider.getToken(URL)).thenReturn("token-actuel");
        when(httpClient.call(URL, "uai", "20261021", 2, "token-actuel")).thenThrow(httpError(500));

        assertThrows(WebgerestRequestException.class,
                () -> retryingHttpClient.call(URL, "uai", "20261021", 2));

        verify(tokenProvider, never()).refreshToken(anyString());
        verify(httpClient, times(1)).call(anyString(), anyString(), anyString(), anyInt(), any());
    }
}
