package io.dropwizard.logging;


import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import org.junit.jupiter.api.Test;


public class ExternalLoggingFactoryTest {
    @Test
    public void canBeDeserialized() throws Exception {
        LoggingFactory externalRequestLogFactory = new io.dropwizard.configuration.YamlConfigurationFactory(LoggingFactory.class, BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw").build(new File(Resources.getResource("yaml/logging_external.yml").toURI()));
        assertThat(externalRequestLogFactory).isNotNull();
        assertThat(externalRequestLogFactory).isInstanceOf(ExternalLoggingFactory.class);
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(ExternalLoggingFactory.class);
    }
}

