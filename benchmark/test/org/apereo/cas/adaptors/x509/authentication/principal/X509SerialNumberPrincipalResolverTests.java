package org.apereo.cas.adaptors.x509.authentication.principal;


import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Scott Battaglia
 * @author Jan Van der Velpen
 * @since 3.0.0.6
 */
public class X509SerialNumberPrincipalResolverTests extends AbstractX509CertificateTests {
    private final X509SerialNumberPrincipalResolver resolver = new X509SerialNumberPrincipalResolver();

    @Test
    public void verifyResolvePrincipalInternal() {
        val c = new X509CertificateCredential(new X509Certificate[]{ AbstractX509CertificateTests.VALID_CERTIFICATE });
        c.setCertificate(AbstractX509CertificateTests.VALID_CERTIFICATE);
        Assertions.assertEquals(AbstractX509CertificateTests.VALID_CERTIFICATE.getSerialNumber().toString(), this.resolver.resolve(c, Optional.of(CoreAuthenticationTestUtils.getPrincipal()), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler())).getId());
    }

    @Test
    public void verifySupport() {
        val c = new X509CertificateCredential(new X509Certificate[]{ AbstractX509CertificateTests.VALID_CERTIFICATE });
        Assertions.assertTrue(this.resolver.supports(c));
    }

    @Test
    public void verifySupportFalse() {
        Assertions.assertFalse(this.resolver.supports(new UsernamePasswordCredential()));
    }

    @Test
    public void verifyHexPrincipalOdd() {
        val r = new X509SerialNumberPrincipalResolver(16, true);
        val mockCert = Mockito.mock(X509Certificate.class);
        Mockito.when(mockCert.getSerialNumber()).thenReturn(BigInteger.valueOf(300L));
        val principal = r.resolvePrincipalInternal(mockCert);
        Assertions.assertEquals("012c", principal);
    }

    @Test
    public void verifyHexPrincipalOddFalse() {
        val r = new X509SerialNumberPrincipalResolver(16, false);
        val mockCert = Mockito.mock(X509Certificate.class);
        Mockito.when(mockCert.getSerialNumber()).thenReturn(BigInteger.valueOf(300L));
        val principal = r.resolvePrincipalInternal(mockCert);
        Assertions.assertEquals("12c", principal);
    }

    @Test
    public void verifyHexPrincipalEven() {
        val r = new X509SerialNumberPrincipalResolver(16, true);
        val mockCert = Mockito.mock(X509Certificate.class);
        Mockito.when(mockCert.getSerialNumber()).thenReturn(BigInteger.valueOf(60300L));
        val principal = r.resolvePrincipalInternal(mockCert);
        Assertions.assertEquals("eb8c", principal);
    }
}

