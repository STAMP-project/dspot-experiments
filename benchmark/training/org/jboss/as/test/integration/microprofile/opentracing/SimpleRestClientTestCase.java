package org.jboss.as.test.integration.microprofile.opentracing;


import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import java.net.URL;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class SimpleRestClientTestCase {
    @Inject
    Tracer tracer;

    @ArquillianResource
    URL url;

    @Test
    public void clientRequestSpanJoinsServer() {
        // sanity checks
        Assert.assertNotNull(tracer);
        Assert.assertTrue(((tracer) instanceof MockTracer));
        // test
        // the first span
        try (Scope ignored = tracer.buildSpan("existing-span").startActive(true)) {
            // the second span is the client request, as a child of `existing-span`
            Client restClient = ClientTracingRegistrar.configure(ClientBuilder.newBuilder()).build();
            // the third span is the traced endpoint, child of the client request
            String targetUrl = (url.toString()) + "opentracing/traced";
            WebTarget target = restClient.target(targetUrl);
            try (Response response = target.request().get()) {
                // just a sanity check
                Assert.assertEquals(200, response.getStatus());
            }
        }
        // verify
        MockTracer mockTracer = ((MockTracer) (tracer));
        List<MockSpan> spans = mockTracer.finishedSpans();
        Assert.assertEquals(3, spans.size());
        long traceId = spans.get(0).context().traceId();
        for (MockSpan span : spans) {
            // they should all belong to the same trace
            Assert.assertEquals(traceId, span.context().traceId());
        }
    }
}

