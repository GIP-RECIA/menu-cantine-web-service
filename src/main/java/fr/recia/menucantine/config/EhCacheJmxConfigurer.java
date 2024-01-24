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
package fr.recia.menucantine.config;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

@Service
@Profile("!unit")
public class EhCacheJmxConfigurer {

    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();;

    @Autowired
    private CacheManager cacheManager;

    @Bean(initMethod = "init", destroyMethod = "dispose")
    public ManagementService ehCacheManagementService() {
        return new ManagementService(cacheManager, mBeanServer,
                true, true, true, true, true);

    }

}
