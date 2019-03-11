package com.codahale.metrics.jersey2;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jersey2.resources.InstrumentedFilteredResource;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * Tests registering {@link InstrumentedResourceMethodApplicationListener} as a singleton
 * in a Jersey {@link ResourceConfig} with filter tracking
 */
public class SingletonFilterMetricsJerseyTest extends JerseyTest {
    static {
        Logger.getLogger("org.glassfish.jersey").setLevel(Level.OFF);
    }

    private MetricRegistry registry;

    private TestClock testClock;

    @Test
    public void timedMethodsAreTimed() {
        assertThat(target("timed").request().get(String.class)).isEqualTo("yay");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.class, "timed"));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(1);
    }

    @Test
    public void explicitNamesAreTimed() {
        assertThat(target("named").request().get(String.class)).isEqualTo("fancy");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.class, "fancyName"));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(1);
    }

    @Test
    public void absoluteNamesAreTimed() {
        assertThat(target("absolute").request().get(String.class)).isEqualTo("absolute");
        final Timer timer = registry.timer("absolutelyFancy");
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(1);
    }

    @Test
    public void requestFiltersOfTimedMethodsAreTimed() {
        assertThat(target("timed").request().get(String.class)).isEqualTo("yay");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.class, "timed", "request", "filtering"));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(4);
    }

    @Test
    public void responseFiltersOfTimedMethodsAreTimed() {
        assertThat(target("timed").request().get(String.class)).isEqualTo("yay");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.class, "timed", "response", "filtering"));
        assertThat(timer.getCount()).isEqualTo(1);
    }

    @Test
    public void totalTimeOfTimedMethodsIsTimed() {
        assertThat(target("timed").request().get(String.class)).isEqualTo("yay");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.class, "timed", "total"));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(5);
    }

    @Test
    public void requestFiltersOfNamedMethodsAreTimed() {
        assertThat(target("named").request().get(String.class)).isEqualTo("fancy");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.class, "fancyName", "request", "filtering"));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(4);
    }

    @Test
    public void requestFiltersOfAbsoluteMethodsAreTimed() {
        assertThat(target("absolute").request().get(String.class)).isEqualTo("absolute");
        final Timer timer = registry.timer(MetricRegistry.name("absolutelyFancy", "request", "filtering"));
        assertThat(timer.getCount()).isEqualTo(1);
        assertThat(timer.getSnapshot().getValues()[0]).isEqualTo(4);
    }

    @Test
    public void subResourcesFromLocatorsRegisterMetrics() {
        assertThat(target("subresource/timed").request().get(String.class)).isEqualTo("yay");
        final Timer timer = registry.timer(MetricRegistry.name(InstrumentedFilteredResource.InstrumentedFilteredSubResource.class, "timed"));
        assertThat(timer.getCount()).isEqualTo(1);
    }
}

