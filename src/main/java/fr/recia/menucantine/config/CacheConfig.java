package fr.recia.menucantine.config;

import net.sf.ehcache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {

    @Bean
    public CacheManager ehCacheManager() {
        return CacheManager.create(getClass().getResource("/ehcache.xml"));
    }
}
