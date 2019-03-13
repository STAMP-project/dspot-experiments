package io.dropwizard.request.logging;


import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import org.junit.jupiter.api.Test;


public class ExternalRequestLogFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Test
    public void canBeDeserialized() throws Exception {
        RequestLogFactory<?> externalRequestLogFactory = new io.dropwizard.configuration.YamlConfigurationFactory(RequestLogFactory.class, BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw").build(new File(Resources.getResource("yaml/externalRequestLog.yml").toURI()));
        assertThat(externalRequestLogFactory).isNotNull();
        assertThat(externalRequestLogFactory).isInstanceOf(ExternalRequestLogFactory.class);
        assertThat(externalRequestLogFactory.isEnabled()).isTrue();
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(ExternalRequestLogFactory.class);
    }
}

