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
import fr.recia.menucantine.webgerest.CacheKeyRequete;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResponseCacheTest {

    private static final CacheKeyRequete KEY = new CacheKeyRequete("UAI_TEST", "20261021", 2);

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache erreurCache;

    @Mock
    private Cache permanentCache;

    @Mock
    private Cache requetesCache;

    private ResponseCache responseCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("erreur")).thenReturn(erreurCache);
        when(cacheManager.getCache("permanent")).thenReturn(permanentCache);
        when(cacheManager.getCache("requetes")).thenReturn(requetesCache);
        responseCache = new ResponseCache(cacheManager);
    }

    @Test
    void findErrorRenvoieNullQuandRienEnCache() {
        when(erreurCache.get(KEY)).thenReturn(null);
        assertNull(responseCache.findError(KEY));
    }

    @Test
    void findErrorRenvoieLaValeurMiseEnCache() {
        ServiceDTO value = new ServiceDTO("erreur", 1, 0, null);
        when(erreurCache.get(KEY)).thenReturn(new Element(KEY, value));

        assertSame(value, responseCache.findError(KEY));
    }

    @Test
    void findPermanentEtFindPendingInterrogentChacunLeurPropreRegion() {
        ServiceDTO permanentValue = new ServiceDTO("ok", 0, 1, null);
        ServiceDTO pendingValue = new ServiceDTO("ok", 0, 1, null);
        when(permanentCache.get(KEY)).thenReturn(new Element(KEY, permanentValue));
        when(requetesCache.get(KEY)).thenReturn(new Element(KEY, pendingValue));

        assertSame(permanentValue, responseCache.findPermanent(KEY));
        assertSame(pendingValue, responseCache.findPending(KEY));
    }

    @Test
    void storeErrorStorePermanentStorePendingEcriventDansLaBonneRegion() {
        ServiceDTO value = new ServiceDTO("ok", 0, 1, null);

        responseCache.storeError(KEY, value);
        responseCache.storePermanent(KEY, value);
        responseCache.storePending(KEY, value);

        verify(erreurCache).put(org.mockito.ArgumentMatchers.any(Element.class));
        verify(permanentCache).put(org.mockito.ArgumentMatchers.any(Element.class));
        verify(requetesCache).put(org.mockito.ArgumentMatchers.any(Element.class));
    }
}
