package org.apereo.cas.adaptors.x509.authentication.principal;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.adaptors.x509.authentication.CasX509Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.
 */
public class X509CertificateCredentialTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "x509CertificateCredential.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifySerializeAX509CertificateCredentialToJson() throws IOException {
        X509CertificateCredentialTests.MAPPER.findAndRegisterModules();
        val certificate = new CasX509Certificate(true);
        val credentialWritten = new X509CertificateCredential(new X509Certificate[]{ certificate });
        X509CertificateCredentialTests.MAPPER.writeValue(X509CertificateCredentialTests.JSON_FILE, credentialWritten);
        val credentialRead = X509CertificateCredentialTests.MAPPER.readValue(X509CertificateCredentialTests.JSON_FILE, X509CertificateCredential.class);
        Assertions.assertEquals(credentialWritten, credentialRead);
    }
}

