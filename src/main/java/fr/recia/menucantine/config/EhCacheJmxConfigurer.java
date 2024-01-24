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
