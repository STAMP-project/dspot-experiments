package io.dropwizard.request.logging;


import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.logging.FileAppenderFactory;
import org.junit.jupiter.api.Test;


public class RequestLogFactoryTest {
    private LogbackAccessRequestLogFactory logbackAccessRequestLogFactory;

    @Test
    public void fileAppenderFactoryIsSet() {
        assertThat(logbackAccessRequestLogFactory).isNotNull();
        assertThat(logbackAccessRequestLogFactory.getAppenders()).isNotNull();
        assertThat(logbackAccessRequestLogFactory.getAppenders().size()).isEqualTo(1);
        assertThat(logbackAccessRequestLogFactory.getAppenders().get(0)).isInstanceOf(FileAppenderFactory.class);
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(LogbackAccessRequestLogFactory.class);
    }
}

