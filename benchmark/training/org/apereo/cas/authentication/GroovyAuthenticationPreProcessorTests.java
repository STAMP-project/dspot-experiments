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
 * This is {@link GroovyAuthenticationPreProcessorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class GroovyAuthenticationPreProcessorTests {
    @Test
    public void verifyAction() {
        val g = new GroovyAuthenticationPreProcessor(new ClassPathResource("GroovyPreProcessor.groovy"));
        val transaction = Mockito.mock(AuthenticationTransaction.class);
        val creds = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        Mockito.when(transaction.getPrimaryCredential()).thenReturn(Optional.of(creds));
        Assertions.assertTrue(g.process(transaction));
        Assertions.assertTrue(g.supports(creds));
    }
}

