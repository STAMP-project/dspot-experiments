package org.apereo.cas.util;


import lombok.val;
import org.apereo.cas.util.http.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class TrustedProxyAuthenticationTrustStoreSslSocketFactoryTests {
    private static final ClassPathResource TRUST_STORE = new ClassPathResource("truststore.jks");

    private static final String TRUST_STORE_PSW = "changeit";

    private HttpClient client;

    @Test
    public void verifySuccessfulConnection() {
        val valid = client.isValidEndPoint("https://www.github.com");
        Assertions.assertTrue(valid);
    }

    @Test
    public void verifySuccessfulConnectionWithCustomSSLCert() {
        val valid = client.isValidEndPoint("https://self-signed.badssl.com");
        Assertions.assertTrue(valid);
    }
}

