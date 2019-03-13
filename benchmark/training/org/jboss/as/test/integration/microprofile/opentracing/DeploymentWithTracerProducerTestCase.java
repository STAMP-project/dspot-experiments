package org.jboss.as.test.integration.microprofile.opentracing;


import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class DeploymentWithTracerProducerTestCase {
    @Inject
    Tracer tracer;

    @Test
    public void mockTracerIsUsed() {
        Assert.assertNotNull(tracer);
        Assert.assertTrue(((tracer) instanceof MockTracer));
    }
}

