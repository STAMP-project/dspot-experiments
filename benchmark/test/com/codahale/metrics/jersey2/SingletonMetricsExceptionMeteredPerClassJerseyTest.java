package com.codahale.metrics.jersey2;


import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jersey2.resources.InstrumentedResourceExceptionMeteredPerClass;
import com.codahale.metrics.jersey2.resources.InstrumentedSubResourceExceptionMeteredPerClass;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * Tests registering {@link InstrumentedResourceMethodApplicationListener} as a singleton
 * in a Jersey {@link ResourceConfig}
 */
public class SingletonMetricsExceptionMeteredPerClassJerseyTest extends JerseyTest {
    static {
        Logger.getLogger("org.glassfish.jersey").setLevel(Level.OFF);
    }

    private MetricRegistry registry;

    @Test
    public void exceptionMeteredMethodsAreExceptionMetered() {
        final Meter meter = registry.meter(MetricRegistry.name(InstrumentedResourceExceptionMeteredPerClass.class, "exceptionMetered", "exceptions"));
        assertThat(target("exception-metered").request().get(String.class)).isEqualTo("fuh");
        assertThat(meter.getCount()).isZero();
        try {
            target("exception-metered").queryParam("splode", true).request().get(String.class);
            failBecauseExceptionWasNotThrown(ProcessingException.class);
        } catch (ProcessingException e) {
            assertThat(e.getCause()).isInstanceOf(IOException.class);
        }
        assertThat(meter.getCount()).isEqualTo(1);
    }

    @Test
    public void subresourcesFromLocatorsRegisterMetrics() {
        final Meter meter = registry.meter(MetricRegistry.name(InstrumentedSubResourceExceptionMeteredPerClass.class, "exceptionMetered", "exceptions"));
        assertThat(target("subresource/exception-metered").request().get(String.class)).isEqualTo("fuh");
        assertThat(meter.getCount()).isZero();
        try {
            target("subresource/exception-metered").queryParam("splode", true).request().get(String.class);
            failBecauseExceptionWasNotThrown(ProcessingException.class);
        } catch (ProcessingException e) {
            assertThat(e.getCause()).isInstanceOf(IOException.class);
        }
        assertThat(meter.getCount()).isEqualTo(1);
    }
}

