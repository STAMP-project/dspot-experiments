package org.jivesoftware.openfire.keystore;


import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests that verify the functionality of {@link OpenfireX509TrustManager}.
 *
 * @author Guus der Kinderen, guus.der.kinderen@gmail.com
 */
public class OpenfireX509TrustManagerTest {
    /**
     * An instance that is freshly recreated before each test.
     */
    private OpenfireX509TrustManager systemUnderTest;

    /**
     * The keystore that contains the certificates used by the system under test (refreshed before every test invocation).
     */
    private KeyStore trustStore;

    private String location;// location on disc for the keystore. Used to clean-up after each test.


    private X509Certificate[] validChain;

    private X509Certificate[] expiredIntChain;

    private X509Certificate[] expiredRootChain;

    private X509Certificate[] untrustedCAChain;

    /**
     * This test verifies that {@link OpenfireX509TrustManager#getAcceptedIssuers()} does not return expired
     * certificates.
     */
    @Test
    public void testAcceptedIssuersAreAllValid() throws Exception {
        // Setup fixture.
        // Execute system under test.
        final X509Certificate[] result = systemUnderTest.getAcceptedIssuers();
        // Verify results.
        Assert.assertEquals(2, result.length);
        Assert.assertTrue(Arrays.asList(result).contains(OpenfireX509TrustManagerTest.getLast(validChain)));
        Assert.assertTrue(Arrays.asList(result).contains(OpenfireX509TrustManagerTest.getLast(expiredIntChain)));
        Assert.assertFalse(Arrays.asList(result).contains(OpenfireX509TrustManagerTest.getLast(expiredRootChain)));
    }

    /**
     * Verifies that a valid chain is not rejected.
     */
    @Test
    public void testValidChain() throws Exception {
        // Setup fixture.
        // Execute system under test.
        systemUnderTest.checkClientTrusted(validChain, "RSA");
        // Verify result
        // (getting here without an exception being thrown is enough).
    }

    /**
     * Verifies that a chain that has an intermediate certificate that is expired is rejected.
     */
    @Test(expected = CertificateException.class)
    public void testInvalidChainExpiredIntermediate() throws Exception {
        // Setup fixture.
        // Execute system under test.
        systemUnderTest.checkClientTrusted(expiredIntChain, "RSA");
    }

    /**
     * Verifies that a chain that has an root certificate (trust anchor) that is expired is rejected.
     */
    @Test(expected = CertificateException.class)
    public void testInvalidChainExpiredTrustAnchor() throws Exception {
        // Setup fixture.
        // Execute system under test.
        systemUnderTest.checkClientTrusted(expiredRootChain, "RSA");
    }

    /**
     * Verifies that a chain that is missing an intermediate certificate is rejected.
     */
    @Test(expected = CertificateException.class)
    public void testInvalidChainMissingIntermediate() throws Exception {
        // Setup fixture.
        assert (validChain.length) == 4;
        final X509Certificate[] input = new X509Certificate[3];
        input[0] = validChain[0];
        input[1] = validChain[2];
        input[2] = validChain[3];
        // Execute system under test.
        systemUnderTest.checkClientTrusted(input, "RSA");
    }

    /**
     * Verifies that a chain that is valid, but does not have its root CA certificate in the trust store, is rejected.
     */
    @Test(expected = CertificateException.class)
    public void testInvalidChainCAnotTrusted() throws Exception {
        // Setup fixture.
        // Execute system under test.
        systemUnderTest.checkClientTrusted(untrustedCAChain, "RSA");
    }
}

