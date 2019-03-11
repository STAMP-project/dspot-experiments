package org.jboss.as.test.integration.microprofile.opentracing;


import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import java.net.URL;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ResourceWithCDITestCase {
    @Inject
    Tracer tracer;

    @ArquillianResource
    private URL url;

    @Test
    public void tracedEndpointYieldsSpan() throws Exception {
        Assert.assertTrue(((tracer) instanceof MockTracer));
        MockTracer mockTracer = ((MockTracer) (tracer));
        performCall("opentracing/with-bean");
        Assert.assertEquals(3, mockTracer.finishedSpans().size());
    }
}

