package org.apereo.cas.oidc.jwks;


import org.apereo.cas.oidc.AbstractOidcTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link OidcDefaultJsonWebKeystoreCacheLoaderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcDefaultJsonWebKeystoreCacheLoaderTests extends AbstractOidcTests {
    @Test
    public void verifyOperation() {
        Assertions.assertTrue(oidcDefaultJsonWebKeystoreCache.get("https://sso.example.org/cas/oidc").isPresent());
        Assertions.assertTrue(oidcDefaultJsonWebKeystoreCache.get("https://sso.example.org/cas/oidc").isPresent());
    }
}

