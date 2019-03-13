package org.apereo.cas.oidc.token;


import lombok.val;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OidcIdTokenSigningAndEncryptionServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcIdTokenSigningAndEncryptionServiceTests extends AbstractOidcTests {
    @Test
    public void verifyOperation() {
        val claims = AbstractOidcTests.getClaims();
        val result = oidcTokenSigningAndEncryptionService.encode(AbstractOidcTests.getOidcRegisteredService(), claims);
        Assertions.assertNotNull(result);
    }

    @Test
    public void verifyValidationOperation() {
        val claims = AbstractOidcTests.getClaims();
        val result = oidcTokenSigningAndEncryptionService.encode(AbstractOidcTests.getOidcRegisteredService(true, false), claims);
        val jwt = oidcTokenSigningAndEncryptionService.validate(result);
        Assertions.assertNotNull(jwt);
    }
}

