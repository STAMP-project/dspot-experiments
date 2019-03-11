package io.dropwizard.http2;


import HttpHeaders.CONTENT_TYPE;
import MediaType.APPLICATION_JSON;
import io.dropwizard.Configuration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DropwizardExtensionsSupport.class)
public class Http2CIntegrationTest extends AbstractHttp2Test {
    public DropwizardAppExtension<Configuration> appRule = new DropwizardAppExtension(FakeApplication.class, ResourceHelpers.resourceFilePath("test-http2c.yml"));

    @Test
    public void testHttp11() {
        final String hostname = "127.0.0.1";
        final int port = appRule.getLocalPort();
        final JerseyClient http11Client = new JerseyClientBuilder().build();
        final Response response = http11Client.target((((("http://" + hostname) + ":") + port) + "/api/test")).request().get();
        assertThat(response.getHeaderString(CONTENT_TYPE)).isEqualTo(APPLICATION_JSON);
        assertThat(response.readEntity(String.class)).isEqualTo(FakeApplication.HELLO_WORLD);
        http11Client.close();
    }

    @Test
    public void testHttp2c() throws Exception {
        AbstractHttp2Test.assertResponse(client.GET((("http://localhost:" + (appRule.getLocalPort())) + "/api/test")));
    }

    @Test
    public void testHttp2cManyRequests() throws Exception {
        performManyAsyncRequests(client, (("http://localhost:" + (appRule.getLocalPort())) + "/api/test"));
    }
}

