package org.apereo.cas.support.x509.rest;


import java.io.FileInputStream;
import java.security.cert.X509Certificate;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;
import org.apereo.cas.util.crypto.CertUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Unit tests for {@link X509RestTlsClientCertCredentialFactory}.
 *
 * @author Dmytro Fedonin
 * @author St?phane Adenot
 * @since 6.0.0
 */
public class X509RestTlsClientCertCredentialFactoryTests {
    private static final String REQUEST_ATTRIBUTE_X509_CERTIFICATE = "javax.servlet.request.X509Certificate";

    private final X509RestTlsClientCertCredentialFactory factory = new X509RestTlsClientCertCredentialFactory();

    @Test
    @SneakyThrows
    public void createX509Credential() {
        val request = new MockHttpServletRequest();
        try (val inStream = new FileInputStream(new ClassPathResource("ldap-crl.crt").getFile())) {
            val certs = new X509Certificate[]{ CertUtils.readCertificate(inStream) };
            request.setAttribute(X509RestTlsClientCertCredentialFactoryTests.REQUEST_ATTRIBUTE_X509_CERTIFICATE, certs);
            val cred = factory.fromRequest(request, null).iterator().next();
            Assertions.assertTrue((cred instanceof X509CertificateCredential));
        }
    }

    @Test
    public void createDefaultCredential() {
        val request = new MockHttpServletRequest();
        val requestBody = new org.springframework.util.LinkedMultiValueMap<String, String>();
        requestBody.add("username", "name");
        requestBody.add("password", "passwd");
        val cred = factory.fromRequest(request, requestBody);
        Assertions.assertTrue(cred.isEmpty());
    }
}

