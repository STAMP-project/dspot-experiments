package org.apereo.cas.util.io;


import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.util.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link RestfulSmsSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class, MailSenderAutoConfiguration.class, MailSenderValidatorAutoConfiguration.class })
@Tag("RestfulApi")
@TestPropertySource(properties = { "cas.smsProvider.rest.url=http://localhost:8132" })
public class RestfulSmsSenderTests {
    @Autowired
    @Qualifier("communicationsManager")
    private CommunicationsManager communicationsManager;

    private MockWebServer webServer;

    @Test
    public void verifyRestAttributeRepository() {
        Assertions.assertTrue(communicationsManager.isSmsSenderDefined());
        Assertions.assertTrue(communicationsManager.sms("CAS", "CAS", "Hello CAS"));
    }
}

