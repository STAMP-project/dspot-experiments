package org.apereo.cas.authentication;


import java.util.Collection;
import javax.security.auth.login.FailedLoginException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.LdapAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.util.junit.EnabledIfContinuousIntegration;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.UncheckedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;


/**
 * Unit test for {@link LdapAuthenticationHandler}.
 *
 * @author Marvin S. Addison
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreHttpConfiguration.class, CasCoreUtilConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreServicesConfiguration.class, LdapAuthenticationConfiguration.class })
@Tag("Ldap")
@EnabledIfContinuousIntegration
public abstract class BaseLdapAuthenticationHandlerTests {
    @Autowired
    @Qualifier("ldapAuthenticationHandlers")
    protected Collection<AuthenticationHandler> handler;

    @Test
    public void verifyAuthenticateFailure() throws Throwable {
        Assertions.assertNotEquals(handler.size(), 0);
        assertThrowsWithRootCause(UncheckedException.class, FailedLoginException.class, () -> this.handler.forEach(Unchecked.consumer(( h) -> h.authenticate(new UsernamePasswordCredential("admin", "bad")))));
    }

    @Test
    public void verifyAuthenticateSuccess() {
        Assertions.assertNotEquals(handler.size(), 0);
        this.handler.forEach(Unchecked.consumer(( h) -> {
            val credential = new UsernamePasswordCredential("admin", "password");
            val result = h.authenticate(credential);
            assertNotNull(result.getPrincipal());
            assertEquals(credential.getUsername(), result.getPrincipal().getId());
            val attributes = result.getPrincipal().getAttributes();
            assertTrue(attributes.containsKey("cn"));
            assertTrue(attributes.containsKey("description"));
        }));
    }
}

