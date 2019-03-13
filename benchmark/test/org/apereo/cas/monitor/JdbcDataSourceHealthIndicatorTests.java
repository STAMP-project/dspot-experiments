package org.apereo.cas.monitor;


import Status.UP;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
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
 * Unit test for {@link JdbcDataSourceHealthIndicator}.
 *
 * @author Marvin S. Addison
 * @since 3.5.1
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
@ContextConfiguration(initializers = EnvironmentConversionServiceInitializer.class)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class JdbcDataSourceHealthIndicatorTests {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    private CasConfigurationProperties casProperties;

    private DataSource dataSource;

    @Test
    public void verifyObserve() {
        val monitor = new JdbcDataSourceHealthIndicator(5000, this.dataSource, this.executor, "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
        val status = monitor.health();
        Assertions.assertEquals(UP, status.getStatus());
    }
}

