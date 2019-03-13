package io.dropwizard.metrics;


import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import org.junit.jupiter.api.Test;


public class ConsoleReporterFactoryTest {
    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(ConsoleReporterFactory.class);
    }
}

