package org.apereo.cas.digest.util;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link DigestAuthenticationUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class DigestAuthenticationUtilsTests {
    @Test
    public void verifyNonce() {
        Assertions.assertNotNull(DigestAuthenticationUtils.createNonce());
    }

    @Test
    public void verifyCNonce() {
        Assertions.assertNotNull(DigestAuthenticationUtils.createCnonce());
    }

    @Test
    public void verifyOpaque() {
        Assertions.assertNotNull(DigestAuthenticationUtils.createOpaque("domain", DigestAuthenticationUtils.createNonce()));
    }

    @Test
    public void verifyHeader() {
        val header = DigestAuthenticationUtils.createAuthenticateHeader("domain", "authMethod", DigestAuthenticationUtils.createNonce());
        Assertions.assertNotNull(header);
        Assertions.assertTrue(header.contains("nonce="));
        Assertions.assertTrue(header.contains("opaque="));
        Assertions.assertTrue(header.contains("qop="));
    }
}

