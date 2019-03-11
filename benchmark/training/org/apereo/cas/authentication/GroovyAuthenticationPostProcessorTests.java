package org.apereo.cas.authentication;


import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link GroovyAuthenticationPostProcessorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("Groovy")
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class GroovyAuthenticationPostProcessorTests {
    @Test
    public void verifyAction() {
        val g = new GroovyAuthenticationPostProcessor(new ClassPathResource("GroovyPostProcessor.groovy"));
        val transaction = Mockito.mock(AuthenticationTransaction.class);
        val creds = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        Mockito.when(transaction.getPrimaryCredential()).thenReturn(Optional.of(creds));
        Assertions.assertTrue(g.supports(creds));
        val authenticationBuilder = CoreAuthenticationTestUtils.getAuthenticationBuilder();
        g.process(authenticationBuilder, transaction);
        Assertions.assertFalse(authenticationBuilder.getSuccesses().isEmpty());
        Assertions.assertFalse(authenticationBuilder.getSuccesses().get("test").getWarnings().isEmpty());
    }
}

