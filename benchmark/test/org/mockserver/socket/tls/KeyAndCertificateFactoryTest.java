package org.mockserver.socket.tls;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jnormington
 */
public class KeyAndCertificateFactoryTest {
    @Test
    public void shouldCreateCACertWithPositiveSerialNumber() throws Exception {
        KeyAndCertificateFactory.keyAndCertificateFactory().buildAndSaveCertificates();
        Assert.assertTrue("The ca cert serial number is non-negative", ((KeyAndCertificateFactory.keyAndCertificateFactory().mockServerCertificateAuthorityX509Certificate().getSerialNumber().compareTo(BigInteger.ZERO)) > 0));
    }

    @Test
    public void shouldCreateClientCertWithPositiveSerialNumber() throws Exception {
        KeyAndCertificateFactory.keyAndCertificateFactory().buildAndSaveCertificates();
        Assert.assertTrue("The client cert serial number is non-negative", ((KeyAndCertificateFactory.keyAndCertificateFactory().mockServerX509Certificate().getSerialNumber().compareTo(BigInteger.ZERO)) > 0));
    }
}

