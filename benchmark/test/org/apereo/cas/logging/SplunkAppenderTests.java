package org.apereo.cas.logging;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * This is {@link SplunkAppenderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = RefreshAutoConfiguration.class)
@Slf4j
public class SplunkAppenderTests {
    @Test
    public void verifyAction() {
        LOGGER.info("Testing splunk appender");
        Assertions.assertTrue(LOGGER.isInfoEnabled());
    }
}

