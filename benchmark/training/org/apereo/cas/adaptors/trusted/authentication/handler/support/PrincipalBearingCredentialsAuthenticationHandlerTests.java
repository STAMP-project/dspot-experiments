package org.apereo.cas.adaptors.trusted.authentication.handler.support;


import lombok.val;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Andrew Petro
 * @since 3.0.0
 */
public class PrincipalBearingCredentialsAuthenticationHandlerTests {
    private final PrincipalBearingCredentialsAuthenticationHandler handler = new PrincipalBearingCredentialsAuthenticationHandler("", null, PrincipalFactoryUtils.newPrincipalFactory(), null);

    /**
     * When the credentials bear a Principal, succeed the authentication.
     */
    @Test
    public void verifyNonNullPrincipal() {
        val credentials = new org.apereo.cas.adaptors.trusted.authentication.principal.PrincipalBearingCredential(PrincipalFactoryUtils.newPrincipalFactory().createPrincipal("scott"));
        Assertions.assertNotNull(this.handler.authenticate(credentials));
    }

    @Test
    public void verifySupports() {
        val credentials = new org.apereo.cas.adaptors.trusted.authentication.principal.PrincipalBearingCredential(PrincipalFactoryUtils.newPrincipalFactory().createPrincipal("scott"));
        Assertions.assertTrue(this.handler.supports(credentials));
        Assertions.assertFalse(this.handler.supports(new UsernamePasswordCredential()));
    }
}

