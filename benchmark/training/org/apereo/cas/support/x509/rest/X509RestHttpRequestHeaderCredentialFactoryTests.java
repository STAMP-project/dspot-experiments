package org.apereo.cas.support.x509.rest;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import lombok.val;
import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;
import org.apereo.cas.web.extractcert.RequestHeaderX509CertificateExtractor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Unit tests for {@link X509RestMultipartBodyCredentialFactory}.
 *
 * @author Dmytro Fedonin
 * @since 5.1.0
 */
@ExtendWith(MockitoExtension.class)
public class X509RestHttpRequestHeaderCredentialFactoryTests {
    private static final String HEADER = "ssl_client_cert";

    private final X509RestHttpRequestHeaderCredentialFactory factory = new X509RestHttpRequestHeaderCredentialFactory(new RequestHeaderX509CertificateExtractor(X509RestHttpRequestHeaderCredentialFactoryTests.HEADER));

    @Test
    public void createX509Credential() throws IOException {
        val request = new MockHttpServletRequest();
        try (val scan = new Scanner(new ClassPathResource("ldap-crl.crt").getFile(), StandardCharsets.UTF_8.name())) {
            val certStr = scan.useDelimiter("\\Z").next();
            request.addHeader(X509RestHttpRequestHeaderCredentialFactoryTests.HEADER, certStr);
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

