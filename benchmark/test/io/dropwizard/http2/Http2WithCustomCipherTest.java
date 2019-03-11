package io.dropwizard.http2;


import io.dropwizard.Configuration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import java.util.Optional;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class Http2WithCustomCipherTest extends AbstractHttp2Test {
    private static final String PREFIX = "tls_custom_http2";

    @Rule
    public final DropwizardAppExtension<Configuration> appRule = new DropwizardAppExtension(FakeApplication.class, resourceFilePath("test-http2-with-custom-cipher.yml"), Optional.of(Http2WithCustomCipherTest.PREFIX), config(Http2WithCustomCipherTest.PREFIX, "server.connector.keyStorePath", resourceFilePath("stores/http2_server.jks")), config(Http2WithCustomCipherTest.PREFIX, "server.connector.trustStorePath", resourceFilePath("stores/http2_client.jts")));

    @Test
    public void testHttp2WithCustomCipher() throws Exception {
        AbstractHttp2Test.assertResponse(client.GET((("https://localhost:" + (appRule.getLocalPort())) + "/api/test")));
    }
}

