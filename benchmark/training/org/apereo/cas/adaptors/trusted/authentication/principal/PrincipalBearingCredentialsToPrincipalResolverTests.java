package org.apereo.cas.adaptors.trusted.authentication.principal;


import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class PrincipalBearingCredentialsToPrincipalResolverTests {
    private PrincipalBearingPrincipalResolver resolver;

    @Test
    public void verifySupports() {
        val credential = new PrincipalBearingCredential(PrincipalFactoryUtils.newPrincipalFactory().createPrincipal("test"));
        Assertions.assertTrue(this.resolver.supports(credential));
        Assertions.assertFalse(this.resolver.supports(new UsernamePasswordCredential()));
        Assertions.assertFalse(this.resolver.supports(null));
    }

    @Test
    public void verifyReturnedPrincipal() {
        val credential = new PrincipalBearingCredential(PrincipalFactoryUtils.newPrincipalFactory().createPrincipal("test"));
        val p = this.resolver.resolve(credential, Optional.of(CoreAuthenticationTestUtils.getPrincipal()), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertEquals("test", p.getId());
    }
}

