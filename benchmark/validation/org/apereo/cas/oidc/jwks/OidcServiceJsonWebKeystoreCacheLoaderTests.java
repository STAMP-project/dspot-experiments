package org.apereo.cas.oidc.jwks;


import lombok.val;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OidcServiceJsonWebKeystoreCacheLoaderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcServiceJsonWebKeystoreCacheLoaderTests extends AbstractOidcTests {
    @Test
    public void verifyOperation() {
        val service = AbstractOidcTests.getOidcRegisteredService();
        Assertions.assertTrue(oidcServiceJsonWebKeystoreCache.get(service).isPresent());
        Assertions.assertTrue(oidcServiceJsonWebKeystoreCache.get(service).isPresent());
    }
}

