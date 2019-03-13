package org.apereo.cas.util.io;


import lombok.val;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link CommunicationsManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class, MailSenderAutoConfiguration.class, MailSenderValidatorAutoConfiguration.class })
@Tag("Mail")
@EnabledIfContinuousIntegration
@EnabledIfPortOpen(port = 25000)
@TestPropertySource(properties = { "spring.mail.host=localhost", "spring.mail.port=25000", "spring.mail.testConnection=true" })
public class CommunicationsManagerTests {
    @Autowired
    @Qualifier("communicationsManager")
    private CommunicationsManager communicationsManager;

    @Test
    public void verifyMailSender() {
        Assertions.assertTrue(communicationsManager.isMailSenderDefined());
        Assertions.assertTrue(communicationsManager.email("Test Body", "cas@example.org", "Subject", "sample@example.org"));
        val p = Mockito.mock(Principal.class);
        Mockito.when(p.getId()).thenReturn("casuser");
        Mockito.when(p.getAttributes()).thenReturn(CollectionUtils.wrap("email", "cas@example.org"));
        Assertions.assertTrue(communicationsManager.email(p, "email", "Body", "cas@example.org", "Test Subject", "cc@example.org", "bcc@example.org"));
        Assertions.assertTrue(communicationsManager.email("Test Body", "cas@example.org", "Subject", "sample@example.org", "cc@example.org", "bcc@example.org"));
    }
}

