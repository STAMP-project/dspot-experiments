package org.pac4j.saml.crypto;


import java.security.KeyStore;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link KeyStoreCredentialProvider}.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public final class KeyStoreCredentialProviderTests implements TestsConstants {
    @Test
    public void testReturnFirstAliasWhenNoKeystoreAlias() throws Exception {
        final KeyStore keyStore = prepareKeyStore();
        Assert.assertEquals(VALUE, KeyStoreCredentialProvider.getPrivateKeyAlias(keyStore, null));
    }

    @Test
    public void testReturnMatchingAlias() throws Exception {
        final KeyStore keyStore = prepareKeyStore();
        Assert.assertEquals(VALUE, KeyStoreCredentialProvider.getPrivateKeyAlias(keyStore, VALUE.toLowerCase()));
    }
}

