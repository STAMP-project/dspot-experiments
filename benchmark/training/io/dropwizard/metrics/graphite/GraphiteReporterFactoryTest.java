package io.dropwizard.metrics.graphite;


import GraphiteReporter.Builder;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteUDP;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.BaseValidator;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class GraphiteReporterFactoryTest {
    private final Builder builderSpy = Mockito.mock(Builder.class);

    private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory() {
        @Override
        protected Builder builder(MetricRegistry registry) {
            return builderSpy;
        }
    };

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(GraphiteReporterFactory.class);
    }

    @Test
    public void createDefaultFactory() throws Exception {
        final GraphiteReporterFactory factory = new io.dropwizard.configuration.YamlConfigurationFactory(GraphiteReporterFactory.class, BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw").build();
        assertThat(factory.getFrequency()).isEqualTo(Optional.empty());
    }

    @Test
    public void testNoAddressResolutionForGraphite() throws Exception {
        graphiteReporterFactory.build(new MetricRegistry());
        final ArgumentCaptor<Graphite> argument = ArgumentCaptor.forClass(Graphite.class);
        Mockito.verify(builderSpy).build(argument.capture());
        final Graphite graphite = argument.getValue();
        assertThat(GraphiteReporterFactoryTest.getField(graphite, "hostname")).isEqualTo("localhost");
        assertThat(GraphiteReporterFactoryTest.getField(graphite, "port")).isEqualTo(2003);
        assertThat(GraphiteReporterFactoryTest.getField(graphite, "address")).isNull();
    }

    @Test
    public void testCorrectTransportForGraphiteUDP() throws Exception {
        graphiteReporterFactory.setTransport("udp");
        graphiteReporterFactory.build(new MetricRegistry());
        final ArgumentCaptor<GraphiteUDP> argument = ArgumentCaptor.forClass(GraphiteUDP.class);
        Mockito.verify(builderSpy).build(argument.capture());
        final GraphiteUDP graphite = argument.getValue();
        assertThat(GraphiteReporterFactoryTest.getField(graphite, "hostname")).isEqualTo("localhost");
        assertThat(GraphiteReporterFactoryTest.getField(graphite, "port")).isEqualTo(2003);
        assertThat(GraphiteReporterFactoryTest.getField(graphite, "address")).isNull();
    }
}

