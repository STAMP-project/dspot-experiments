package org.apereo.cas.authentication;


import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;


/**
 * Tests for the {@code FileTrustStoreSslSocketFactory} class, checking for self-signed
 * and missing certificates via a local truststore.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class FileTrustStoreSslSocketFactoryTests {
    private static final ClassPathResource RESOURCE = new ClassPathResource("truststore.jks");

    @Test
    public void verifyTrustStoreLoadingSuccessfullyWithCertAvailable() {
        val client = FileTrustStoreSslSocketFactoryTests.getSimpleHttpClient();
        Assertions.assertTrue(client.isValidEndPoint("https://self-signed.badssl.com"));
    }

    @Test
    public void verifyTrustStoreLoadingSuccessfullyWithCertAvailable2() {
        val client = FileTrustStoreSslSocketFactoryTests.getSimpleHttpClient();
        Assertions.assertTrue(client.isValidEndPoint("https://untrusted-root.badssl.com"));
    }

    @Test
    public void verifyTrustStoreNotFound() {
        Assertions.assertThrows(IOException.class, () -> FileTrustStoreSslSocketFactoryTests.sslFactory(new FileSystemResource("test.jks"), "changeit"));
    }

    @Test
    public void verifyTrustStoreBadPassword() {
        Assertions.assertThrows(IOException.class, () -> FileTrustStoreSslSocketFactoryTests.sslFactory(FileTrustStoreSslSocketFactoryTests.RESOURCE, "invalid"));
    }

    @Test
    public void verifyTrustStoreLoadingSuccessfullyForValidEndpointWithNoCert() {
        val client = FileTrustStoreSslSocketFactoryTests.getSimpleHttpClient();
        Assertions.assertTrue(client.isValidEndPoint("https://www.google.com"));
    }

    @Test
    public void verifyTrustStoreLoadingSuccessfullyWihInsecureEndpoint() {
        val client = FileTrustStoreSslSocketFactoryTests.getSimpleHttpClient();
        Assertions.assertTrue(client.isValidEndPoint("http://wikipedia.org"));
    }
}

