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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class APIClientTest {

    private static final String UAI = "UAI_TEST";
    private static final String DATE_PASSEE = "20200101";
    private static final String DATE_FUTURE = "20991231";
    private static final int SERVICE = 2;

    @Mock
    private UaiUrlResolver urlResolver;

    @Mock
    private RetryingHttpClient retryingClient;

    @Mock
    private ResponseCache responseCache;

    @InjectMocks
    private APIClient apiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void renvoieDirectementLeCacheErreurSansAppelerLeResteDeLaChaine()
            throws UnknownUAIException, WebgerestRequestException {
        ServiceDTO cached = new ServiceDTO("erreur en cache", 1, 0, null);
        when(responseCache.findError(any())).thenReturn(cached);

        ServiceDTO result = apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_PASSEE, SERVICE);

        assertSame(cached, result);
        verify(urlResolver, never()).resolve(anyString());
        verify(retryingClient, never()).call(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void renvoieDirectementLeCachePermanentPourUneDatePassee()
            throws UnknownUAIException, WebgerestRequestException {
        ServiceDTO cached = new ServiceDTO("ok", 0, 1, null);
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPermanent(any())).thenReturn(cached);

        ServiceDTO result = apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_PASSEE, SERVICE);

        assertSame(cached, result);
        verify(responseCache, never()).findPending(any());
        verify(urlResolver, never()).resolve(anyString());
    }

    @Test
    void renvoieDirectementLeCacheRequetesPourUneDateFuture()
            throws UnknownUAIException, WebgerestRequestException {
        ServiceDTO cached = new ServiceDTO("ok", 0, 1, null);
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPending(any())).thenReturn(cached);

        ServiceDTO result = apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_FUTURE, SERVICE);

        assertSame(cached, result);
        verify(responseCache, never()).findPermanent(any());
        verify(urlResolver, never()).resolve(anyString());
    }

    @Test
    void stockeEnPermanentUnSuccesSurUneDatePassee() throws UnknownUAIException, WebgerestRequestException {
        ServiceDTO reponse = new ServiceDTO("ok", 0, 1, null);
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPermanent(any())).thenReturn(null);
        when(urlResolver.resolve(UAI)).thenReturn("https://etablissement.example");
        when(retryingClient.call(eq("https://etablissement.example"), eq(UAI), eq(DATE_PASSEE), eq(SERVICE)))
                .thenReturn(reponse);

        ServiceDTO result = apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_PASSEE, SERVICE);

        assertSame(reponse, result);
        verify(responseCache).storePermanent(any(CacheKeyRequete.class), eq(reponse));
        verify(responseCache, never()).storePending(any(), any());
        verify(responseCache, never()).storeError(any(), any());
    }

    @Test
    void stockeEnRequetesUnSuccesSurUneDateFuture() throws UnknownUAIException, WebgerestRequestException {
        ServiceDTO reponse = new ServiceDTO("ok", 0, 1, null);
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPending(any())).thenReturn(null);
        when(urlResolver.resolve(UAI)).thenReturn("https://etablissement.example");
        when(retryingClient.call(eq("https://etablissement.example"), eq(UAI), eq(DATE_FUTURE), eq(SERVICE)))
                .thenReturn(reponse);

        ServiceDTO result = apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_FUTURE, SERVICE);

        assertSame(reponse, result);
        verify(responseCache).storePending(any(CacheKeyRequete.class), eq(reponse));
        verify(responseCache, never()).storePermanent(any(), any());
    }

    @Test
    void stockeEnErreurUneReponseEnErreurSansLaRemplacerParUnSentinelle()
            throws UnknownUAIException, WebgerestRequestException {
        ServiceDTO reponseEnErreur = new ServiceDTO("message réel", 1, 0, null);
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPending(any())).thenReturn(null);
        when(urlResolver.resolve(UAI)).thenReturn("https://etablissement.example");
        when(retryingClient.call(anyString(), anyString(), anyString(), anyInt())).thenReturn(reponseEnErreur);

        ServiceDTO result = apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_FUTURE, SERVICE);

        assertSame(reponseEnErreur, result);
        verify(responseCache).storeError(any(CacheKeyRequete.class), eq(reponseEnErreur));
    }

    @Test
    void stockeUneErreurSentinelleEtRepropageLExceptionQuandLAppelEchoue()
            throws UnknownUAIException, WebgerestRequestException {
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPending(any())).thenReturn(null);
        when(urlResolver.resolve(UAI)).thenReturn("https://etablissement.example");
        when(retryingClient.call(anyString(), anyString(), anyString(), anyInt()))
                .thenThrow(new WebgerestRequestException("erreur webgerest"));

        assertThrows(WebgerestRequestException.class,
                () -> apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_FUTURE, SERVICE));

        verify(responseCache).storeError(any(CacheKeyRequete.class), any(ServiceDTO.class));
    }

    @Test
    void nePasAppelerLaChaineHttpQuandLUrlEstInconnue() throws UnknownUAIException, WebgerestRequestException {
        when(responseCache.findError(any())).thenReturn(null);
        when(responseCache.findPending(any())).thenReturn(null);
        when(urlResolver.resolve(UAI)).thenThrow(new UnknownUAIException("UAI inconnu"));

        assertThrows(UnknownUAIException.class,
                () -> apiClient.makeAuthenticatedApiCallGetMenu(UAI, DATE_FUTURE, SERVICE));

        verify(retryingClient, never()).call(anyString(), anyString(), anyString(), anyInt());
        verify(responseCache, never()).storeError(any(), any());
    }
}
