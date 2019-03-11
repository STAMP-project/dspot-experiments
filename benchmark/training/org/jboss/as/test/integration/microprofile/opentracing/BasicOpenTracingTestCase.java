package org.jboss.as.test.integration.microprofile.opentracing;


import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class BasicOpenTracingTestCase {
    @Inject
    Tracer tracer;

    @Test
    public void hasDefaultInjectedTracer() {
        Assert.assertNotNull(tracer);
        Assert.assertTrue(((tracer) instanceof JaegerTracer));
    }
}

