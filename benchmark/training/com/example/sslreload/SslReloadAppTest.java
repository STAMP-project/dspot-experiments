package com.example.sslreload;


import io.dropwizard.Configuration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.util.Resources;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class SslReloadAppTest {
    private static final X509TrustManager TRUST_ALL = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    private static Path keystore;

    public final DropwizardAppExtension<Configuration> rule = new DropwizardAppExtension(SslReloadApp.class, ResourceHelpers.resourceFilePath("sslreload/config.yml"), ConfigOverride.config("server.applicationConnectors[0].keyStorePath", SslReloadAppTest.keystore.toString()), ConfigOverride.config("server.adminConnectors[0].keyStorePath", SslReloadAppTest.keystore.toString()));

    @Test
    public void reloadCertificateChangesTheServerCertificate() throws Exception {
        // Copy over our new keystore that has our new certificate to the current
        // location of our keystore
        final byte[] keystore2Bytes = Resources.toByteArray(Resources.getResource("sslreload/keystore2.jks"));
        Files.write(SslReloadAppTest.keystore, keystore2Bytes);
        // Get the bytes for the first certificate, and trigger a reload
        byte[] firstCertBytes = certBytes(200, "Reloaded certificate configuration\n");
        // Get the bytes from our newly reloaded certificate
        byte[] secondCertBytes = certBytes(200, "Reloaded certificate configuration\n");
        // Get the bytes from the reloaded certificate, but it should be the same
        // as the second cert because we didn't change anything!
        byte[] thirdCertBytes = certBytes(200, "Reloaded certificate configuration\n");
        assertThat(firstCertBytes).isNotEqualTo(secondCertBytes);
        assertThat(secondCertBytes).isEqualTo(thirdCertBytes);
    }

    @Test
    public void badReloadDoesNotChangeTheServerCertificate() throws Exception {
        // This keystore has a different password than what jetty has been configured with
        // the password is "password2"
        final byte[] badKeystore = Resources.toByteArray(Resources.getResource("sslreload/keystore-diff-pwd.jks"));
        Files.write(SslReloadAppTest.keystore, badKeystore);
        // Get the bytes for the first certificate. The reload should fail
        byte[] firstCertBytes = certBytes(500, "Keystore was tampered with, or password was incorrect");
        // Issue another request. The returned certificate should be the same as setUp
        byte[] secondCertBytes = certBytes(500, "Keystore was tampered with, or password was incorrect");
        // And just to triple check, a third request will continue with
        // the same original certificate
        byte[] thirdCertBytes = certBytes(500, "Keystore was tampered with, or password was incorrect");
        assertThat(firstCertBytes).isEqualTo(secondCertBytes).isEqualTo(thirdCertBytes);
    }
}

