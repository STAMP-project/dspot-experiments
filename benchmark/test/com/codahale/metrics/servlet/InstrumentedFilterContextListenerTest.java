package com.codahale.metrics.servlet;


import com.codahale.metrics.MetricRegistry;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.junit.Test;
import org.mockito.Mockito;


public class InstrumentedFilterContextListenerTest {
    private final MetricRegistry registry = Mockito.mock(MetricRegistry.class);

    private final InstrumentedFilterContextListener listener = new InstrumentedFilterContextListener() {
        @Override
        protected MetricRegistry getMetricRegistry() {
            return registry;
        }
    };

    @Test
    public void injectsTheMetricRegistryIntoTheServletContext() {
        final ServletContext context = Mockito.mock(ServletContext.class);
        final ServletContextEvent event = Mockito.mock(ServletContextEvent.class);
        Mockito.when(event.getServletContext()).thenReturn(context);
        listener.contextInitialized(event);
        Mockito.verify(context).setAttribute("com.codahale.metrics.servlet.InstrumentedFilter.registry", registry);
    }
}

