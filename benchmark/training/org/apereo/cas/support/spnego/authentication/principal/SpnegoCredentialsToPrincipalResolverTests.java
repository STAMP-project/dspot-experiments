package org.apereo.cas.support.spnego.authentication.principal;


import java.util.Optional;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Marc-Antoine Garrigue
 * @author Arnaud Lesueur
 * @since 3.1
 */
public class SpnegoCredentialsToPrincipalResolverTests {
    private SpnegoPrincipalResolver resolver;

    private SpnegoCredential spnegoCredentials;

    @Test
    public void verifyValidCredentials() {
        this.spnegoCredentials.setPrincipal(new DefaultPrincipalFactory().createPrincipal("test"));
        Assertions.assertEquals("test", this.resolver.resolve(this.spnegoCredentials, Optional.of(CoreAuthenticationTestUtils.getPrincipal()), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler())).getId());
    }

    @Test
    public void verifySupports() {
        Assertions.assertFalse(this.resolver.supports(null));
        Assertions.assertTrue(this.resolver.supports(this.spnegoCredentials));
        Assertions.assertFalse(this.resolver.supports(new UsernamePasswordCredential()));
    }
}

