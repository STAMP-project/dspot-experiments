package org.apereo.cas.monitor;


import Status.OUT_OF_SERVICE;
import Status.UP;
import lombok.val;
import org.apereo.cas.config.support.EnvironmentConversionServiceInitializer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;


/**
 * Unit test for {@link AbstractCacheHealthIndicator}.
 *
 * @author Marvin S. Addison
 * @since 3.5.1
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
@ContextConfiguration(initializers = EnvironmentConversionServiceInitializer.class)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CacheHealthIndicatorTests {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyObserveOk() {
        val warn = casProperties.getMonitor().getWarn();
        val monitor = new AbstractCacheHealthIndicator(warn.getEvictionThreshold(), warn.getThreshold()) {
            @Override
            protected SimpleCacheStatistics[] getStatistics() {
                return CacheHealthIndicatorTests.statsArray(new SimpleCacheStatistics(100, 200, 0));
            }
        };
        val status = monitor.health().getStatus();
        Assertions.assertEquals(UP, status);
    }

    @Test
    public void verifyObserveWarn() {
        val warn = casProperties.getMonitor().getWarn();
        val monitor = new AbstractCacheHealthIndicator(warn.getEvictionThreshold(), warn.getThreshold()) {
            @Override
            protected SimpleCacheStatistics[] getStatistics() {
                return CacheHealthIndicatorTests.statsArray(new SimpleCacheStatistics(199, 200, 100));
            }
        };
        val status = monitor.health().getStatus();
        Assertions.assertEquals("WARN", status.getCode());
    }

    @Test
    public void verifyObserveError() {
        val warn = casProperties.getMonitor().getWarn();
        val monitor = new AbstractCacheHealthIndicator(warn.getEvictionThreshold(), warn.getThreshold()) {
            @Override
            protected SimpleCacheStatistics[] getStatistics() {
                return CacheHealthIndicatorTests.statsArray(new SimpleCacheStatistics(100, 110, 0));
            }
        };
        val status = monitor.health().getStatus();
        Assertions.assertEquals(OUT_OF_SERVICE, status);
    }

    @Test
    public void verifyObserveError2() {
        val warn = casProperties.getMonitor().getWarn();
        val monitor = new AbstractCacheHealthIndicator(warn.getEvictionThreshold(), warn.getThreshold()) {
            @Override
            protected SimpleCacheStatistics[] getStatistics() {
                return CacheHealthIndicatorTests.statsArray(new SimpleCacheStatistics(199, 200, 1));
            }
        };
        Assertions.assertEquals("WARN", monitor.health().getStatus().getCode());
    }
}

