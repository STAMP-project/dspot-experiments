package io.dropwizard.metrics;


import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import org.junit.jupiter.api.Test;


public class Slf4jReporterFactoryTest {
    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(Slf4jReporterFactory.class);
    }
}

