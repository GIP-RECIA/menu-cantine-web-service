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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import javax.cache.CacheManager;

@Component
@RequiredArgsConstructor
public class ResponseCache {

    private static final String CACHE_ERREUR = "erreur";
    private static final String CACHE_PERMANENT = "permanent";
    private static final String CACHE_REQUETES = "requetes";

    private final CacheManager cacheManager;

    public ServiceDTO findError(CacheKeyRequete key) {
        return get(CACHE_ERREUR, key);
    }

    public ServiceDTO findPermanent(CacheKeyRequete key) {
        return get(CACHE_PERMANENT, key);
    }

    public ServiceDTO findPending(CacheKeyRequete key) {
        return get(CACHE_REQUETES, key);
    }

    public void storeError(CacheKeyRequete key, ServiceDTO value) {
        cacheManager.getCache(CACHE_ERREUR).put(key, value);
    }

    public void storePermanent(CacheKeyRequete key, ServiceDTO value) {
        cacheManager.getCache(CACHE_PERMANENT).put(key, value);
    }

    public void storePending(CacheKeyRequete key, ServiceDTO value) {
        cacheManager.getCache(CACHE_REQUETES).put(key, value);
    }

    private ServiceDTO get(String cacheName, CacheKeyRequete key) {
        return (ServiceDTO) cacheManager.getCache(cacheName).get(key);
    }
}