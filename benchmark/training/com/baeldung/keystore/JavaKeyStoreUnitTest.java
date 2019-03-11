package com.baeldung.keystore;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by adi on 4/14/18.
 */
public class JavaKeyStoreUnitTest {
    private JavaKeyStore keyStore;

    private static final String KEYSTORE_PWD = "abc123";

    private static final String KEYSTORE_NAME = "myKeyStore";

    private static final String KEY_STORE_TYPE = "JCEKS";

    private static final String MY_SECRET_ENTRY = "mySecretEntry";

    private static final String DN_NAME = "CN=test, OU=test, O=test, L=test, ST=test, C=CY";

    private static final String SHA1WITHRSA = "SHA1withRSA";

    private static final String MY_PRIVATE_KEY = "myPrivateKey";

    private static final String MY_CERTIFICATE = "myCertificate";

    @Test
    public void givenNoKeyStore_whenCreateEmptyKeyStore_thenGetKeyStoreNotNull() throws Exception {
        keyStore.createEmptyKeyStore();
        KeyStore result = keyStore.getKeyStore();
        Assert.assertNotNull(result);
    }

    @Test
    public void givenEmptyKeystore_whenLoadKeyStore_thenKeyStoreLoadedAndSizeZero() throws Exception {
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();
        KeyStore result = keyStore.getKeyStore();
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.size()) == 0));
    }

    @Test
    public void givenLoadedKeyStore_whenSetEntry_thenSizeIsOneAndGetKeyNotNull() throws Exception {
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();
        KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keygen.generateKey();
        // ideally, password should be different for every key
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(JavaKeyStoreUnitTest.KEYSTORE_PWD.toCharArray());
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        keyStore.setEntry(JavaKeyStoreUnitTest.MY_SECRET_ENTRY, secretKeyEntry, protParam);
        KeyStore result = keyStore.getKeyStore();
        Assert.assertTrue(((result.size()) == 1));
        KeyStore.Entry entry = keyStore.getEntry(JavaKeyStoreUnitTest.MY_SECRET_ENTRY);
        Assert.assertTrue((entry != null));
    }

    @Test
    public void givenLoadedKeyStore_whenSetKeyEntry_thenSizeIsOneAndGetEntryNotNull() throws Exception {
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();
        // Generate the key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // Generate a self signed certificate
        X509Certificate certificate = generateSelfSignedCertificate(keyPair);
        X509Certificate[] certificateChain = new X509Certificate[1];
        certificateChain[0] = certificate;
        keyStore.setKeyEntry(JavaKeyStoreUnitTest.MY_PRIVATE_KEY, keyPair.getPrivate(), JavaKeyStoreUnitTest.KEYSTORE_PWD, certificateChain);
        KeyStore result = keyStore.getKeyStore();
        Assert.assertTrue(((result.size()) == 1));
        KeyStore.Entry entry = keyStore.getEntry(JavaKeyStoreUnitTest.MY_PRIVATE_KEY);
        Assert.assertTrue((entry != null));
    }

    @Test
    public void givenLoadedKeyStore_whenSetCertificateEntry_thenSizeIsOneAndGetCertificateEntryNotNull() throws Exception {
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();
        // Generate the key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // Generate a self signed certificate
        X509Certificate certificate = generateSelfSignedCertificate(keyPair);
        keyStore.setCertificateEntry(JavaKeyStoreUnitTest.MY_CERTIFICATE, certificate);
        KeyStore result = this.keyStore.getKeyStore();
        Assert.assertTrue(((result.size()) == 1));
        Certificate resultCertificate = keyStore.getCertificate(JavaKeyStoreUnitTest.MY_CERTIFICATE);
        Assert.assertNotNull(resultCertificate);
    }

    @Test
    public void givenLoadedKeyStoreWithOneEntry_whenDeleteEntry_thenKeyStoreSizeIsZero() throws Exception {
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();
        KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keygen.generateKey();
        // ideally, password should be different for every key
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(JavaKeyStoreUnitTest.KEYSTORE_PWD.toCharArray());
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        keyStore.setEntry(JavaKeyStoreUnitTest.MY_SECRET_ENTRY, secretKeyEntry, protParam);
        keyStore.deleteEntry(JavaKeyStoreUnitTest.MY_SECRET_ENTRY);
        KeyStore result = this.keyStore.getKeyStore();
        Assert.assertTrue(((result.size()) == 0));
    }

    @Test
    public void givenLoadedKeystore_whenDeleteKeyStore_thenKeyStoreIsNull() throws Exception {
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();
        keyStore.deleteKeyStore();
        KeyStore result = this.keyStore.getKeyStore();
        Assert.assertTrue((result == null));
    }
}

