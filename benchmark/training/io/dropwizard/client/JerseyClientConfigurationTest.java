package io.dropwizard.client;


import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.util.Resources;
import java.io.File;
import org.junit.jupiter.api.Test;


public class JerseyClientConfigurationTest {
    @Test
    public void testBasicJerseyClient() throws Exception {
        final JerseyClientConfiguration configuration = new io.dropwizard.configuration.YamlConfigurationFactory(JerseyClientConfiguration.class, Validators.newValidator(), Jackson.newObjectMapper(), "dw").build(new File(Resources.getResource("yaml/jersey-client.yml").toURI()));
        assertThat(configuration.getMinThreads()).isEqualTo(8);
        assertThat(configuration.getMaxThreads()).isEqualTo(64);
        assertThat(configuration.getWorkQueueSize()).isEqualTo(16);
        assertThat(configuration.isGzipEnabled()).isFalse();
        assertThat(configuration.isGzipEnabledForRequests()).isFalse();
        assertThat(configuration.isChunkedEncodingEnabled()).isFalse();
    }
}

