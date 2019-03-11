package org.apereo.cas.support.sms;


import org.apereo.cas.config.ClickatellSmsConfiguration;
import org.apereo.cas.util.MockWebServer;
import org.apereo.cas.util.io.SmsSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link ClickatellSmsSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, ClickatellSmsConfiguration.class })
@TestPropertySource(properties = { "cas.smsProvider.clickatell.serverUrl=http://localhost:8099", "cas.smsProvider.clickatell.token=DEMO_TOKEN" })
public class ClickatellSmsSenderTests {
    @Autowired
    @Qualifier("smsSender")
    private SmsSender smsSender;

    private MockWebServer webServer;

    @Test
    public void verifySmsSender() {
        Assertions.assertTrue(smsSender.send("123-456-7890", "123-456-7890", "TEST"));
    }
}

