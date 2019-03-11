package org.apereo.cas.authentication;


import lombok.val;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasPersonDirectoryTestConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.config.SurrogateAuthenticationAuditConfiguration;
import org.apereo.cas.config.SurrogateAuthenticationConfiguration;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link SurrogateAuthenticationPostProcessorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, SurrogateAuthenticationConfiguration.class, SurrogateAuthenticationAuditConfiguration.class, CasCoreServicesConfiguration.class, CasCoreUtilConfiguration.class, CasRegisteredServicesTestConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasPersonDirectoryTestConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreHttpConfiguration.class })
@TestPropertySource(properties = { "cas.authn.surrogate.simple.surrogates.casuser=cassurrogate" })
public class SurrogateAuthenticationPostProcessorTests {
    @Autowired
    @Qualifier("surrogateAuthenticationPostProcessor")
    private AuthenticationPostProcessor surrogateAuthenticationPostProcessor;

    @Test
    public void verifySupports() {
        Assertions.assertFalse(surrogateAuthenticationPostProcessor.supports(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
        Assertions.assertTrue(surrogateAuthenticationPostProcessor.supports(new SurrogateUsernamePasswordCredential()));
    }

    @Test
    public void verifySurrogateCredentialNotFound() {
        val c = new SurrogateUsernamePasswordCredential();
        c.setUsername("casuser");
        c.setPassword("Mellon");
        val transaction = DefaultAuthenticationTransaction.of(RegisteredServiceTestUtils.getService("service"), c);
        val builder = Mockito.mock(AuthenticationBuilder.class);
        Mockito.when(builder.build()).thenReturn(CoreAuthenticationTestUtils.getAuthentication("casuser"));
        Assertions.assertThrows(AuthenticationException.class, () -> surrogateAuthenticationPostProcessor.process(builder, transaction));
    }

    @Test
    public void verifyProcessorWorks() {
        val c = new SurrogateUsernamePasswordCredential();
        c.setUsername("casuser");
        c.setPassword("Mellon");
        c.setSurrogateUsername("cassurrogate");
        val transaction = DefaultAuthenticationTransaction.of(RegisteredServiceTestUtils.getService("https://localhost"), c);
        val builder = Mockito.mock(AuthenticationBuilder.class);
        Mockito.when(builder.build()).thenReturn(CoreAuthenticationTestUtils.getAuthentication("casuser"));
        surrogateAuthenticationPostProcessor.process(builder, transaction);
    }
}

