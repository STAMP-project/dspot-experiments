package io.fabric8.maven.docker.access;


import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Stas Sukhanov
 * @since 08.03.2017
 */
public class KeyStoreUtilTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createKeyStore() throws Exception {
        KeyStore keyStore = KeyStoreUtil.createDockerKeyStore(getFile("certpath"));
        KeyStore.PrivateKeyEntry pkEntry = ((KeyStore.PrivateKeyEntry) (keyStore.getEntry("docker", new KeyStore.PasswordProtection("docker".toCharArray()))));
        Assert.assertNotNull(pkEntry);
        Assert.assertNotNull(pkEntry.getCertificate());
        Assert.assertNotNull(keyStore.getCertificate("cn=ca-test,o=internet widgits pty ltd,st=some-state,c=cr"));
        Assert.assertNotNull(keyStore.getCertificate("cn=ca-test-2,o=internet widgits pty ltd,st=some-state,c=cr"));
    }

    @Test
    public void loadPrivateKeyDefault() throws Exception {
        PrivateKey privateKey = KeyStoreUtil.loadPrivateKey(getFile("keys/pkcs1.pem"));
        Assert.assertNotNull(privateKey);
    }

    @Test
    public void loadPrivateKeyPKCS8() throws Exception {
        PrivateKey privateKey = KeyStoreUtil.loadPrivateKey(getFile("keys/pkcs8.pem"));
        Assert.assertNotNull(privateKey);
    }

    @Test
    public void loadPrivateKeyECDSA() throws Exception {
        // ecdsa.pem has been created via `openssl ecparam -name secp521r1 -genkey -param_enc explicit -out ecdsa.pem`
        PrivateKey privateKey = KeyStoreUtil.loadPrivateKey(getFile("keys/ecdsa.pem"));
        Assert.assertNotNull(privateKey);
    }

    @Test
    public void loadInvalidPrivateKey() throws Exception {
        exception.expect(GeneralSecurityException.class);
        exception.expectMessage("Cannot generate private key");
        KeyStoreUtil.loadPrivateKey(getFile("keys/invalid.pem"));
    }
}

