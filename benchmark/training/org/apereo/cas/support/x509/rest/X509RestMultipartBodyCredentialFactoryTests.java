package org.apereo.cas.support.x509.rest;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import lombok.Cleanup;
import lombok.val;
import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;


/**
 * Unit tests for {@link X509RestMultipartBodyCredentialFactory}.
 *
 * @author Dmytro Fedonin
 * @since 5.1.0
 */
@ExtendWith(MockitoExtension.class)
public class X509RestMultipartBodyCredentialFactoryTests {
    private final X509RestMultipartBodyCredentialFactory factory = new X509RestMultipartBodyCredentialFactory();

    @Test
    public void createX509Credential() throws IOException {
        val requestBody = new org.springframework.util.LinkedMultiValueMap<String, String>();
        @Cleanup
        val scan = new Scanner(new ClassPathResource("ldap-crl.crt").getFile(), StandardCharsets.UTF_8.name());
        val certStr = scan.useDelimiter("\\Z").next();
        scan.close();
        requestBody.add("cert", certStr);
        val cred = factory.fromRequest(null, requestBody).iterator().next();
        Assertions.assertTrue((cred instanceof X509CertificateCredential));
    }

    @Test
    public void createDefaultCredential() {
        val requestBody = new org.springframework.util.LinkedMultiValueMap<String, String>();
        requestBody.add("username", "name");
        requestBody.add("password", "passwd");
        val cred = factory.fromRequest(null, requestBody);
        Assertions.assertTrue(cred.isEmpty());
    }
}

