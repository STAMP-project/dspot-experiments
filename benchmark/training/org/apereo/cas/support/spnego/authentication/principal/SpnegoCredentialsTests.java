package org.apereo.cas.support.spnego.authentication.principal;


import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SpnegoCredentialsTests {
    @Test
    public void verifyToStringWithNoPrincipal() {
        val credentials = new SpnegoCredential(ArrayUtils.EMPTY_BYTE_ARRAY);
        Assertions.assertTrue(credentials.getId().contains("unknown"));
    }

    @Test
    public void verifyToStringWithPrincipal() {
        val credentials = new SpnegoCredential(ArrayUtils.EMPTY_BYTE_ARRAY);
        val principal = new DefaultPrincipalFactory().createPrincipal("test");
        credentials.setPrincipal(principal);
        Assertions.assertEquals("test", credentials.getId());
    }

    /**
     * Make sure that when the Principal becomes populated / changes we return a new hash
     */
    @Test
    public void verifyPrincipalAffectsHash() {
        val credential = new SpnegoCredential(ArrayUtils.EMPTY_BYTE_ARRAY);
        val hash1 = credential.hashCode();
        val principal = new DefaultPrincipalFactory().createPrincipal("test");
        credential.setPrincipal(principal);
        val hash2 = credential.hashCode();
        Assertions.assertNotEquals(hash1, hash2);
    }
}

