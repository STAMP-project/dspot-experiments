package org.apereo.cas.monitor;


import Status.OUT_OF_SERVICE;
import lombok.val;
import org.apereo.cas.config.CasCoreUtilSerializationConfiguration;
import org.apereo.cas.monitor.config.MemcachedMonitorConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link MemcachedHealthIndicatorTests}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, MemcachedMonitorConfiguration.class, CasCoreUtilSerializationConfiguration.class })
@TestPropertySource(properties = { "cas.monitor.memcached.servers=localhost:11212", "cas.monitor.memcached.failureMode=Redistribute", "cas.monitor.memcached.locatorType=ARRAY_MOD", "cas.monitor.memcached.hashAlgorithm=FNV1A_64_HASH" })
@DirtiesContext
@Tag("Memcached")
public class MemcachedHealthIndicatorTests {
    @Autowired
    @Qualifier("memcachedHealthIndicator")
    private HealthIndicator monitor;

    @Test
    public void verifyMonitorNotRunning() {
        val health = monitor.health();
        Assertions.assertEquals(OUT_OF_SERVICE, health.getStatus());
    }
}

