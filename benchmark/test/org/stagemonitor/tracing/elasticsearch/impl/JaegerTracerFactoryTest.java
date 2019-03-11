package org.stagemonitor.tracing.elasticsearch.impl;


import StagemonitorPlugin.InitArguments;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.StagemonitorPlugin;


public class JaegerTracerFactoryTest {
    @Test
    public void testIsRoot() throws Exception {
        final StagemonitorPlugin.InitArguments initArguments = Mockito.mock(InitArguments.class);
        Mockito.when(initArguments.getMeasurementSession()).thenReturn(new MeasurementSession("JaegerTracerFactoryTest", "test", "test"));
        final JaegerTracerFactory jaegerTracerFactory = new JaegerTracerFactory();
        final Tracer tracer = jaegerTracerFactory.getTracer(initArguments);
        try (final Scope rootSpan = tracer.buildSpan("foo").startActive(true)) {
            try (final Scope childSpan = tracer.buildSpan("bar").startActive(true)) {
                assertThat(jaegerTracerFactory.isRoot(rootSpan.span())).isTrue();
                assertThat(jaegerTracerFactory.isRoot(childSpan.span())).isFalse();
            }
        }
    }
}

