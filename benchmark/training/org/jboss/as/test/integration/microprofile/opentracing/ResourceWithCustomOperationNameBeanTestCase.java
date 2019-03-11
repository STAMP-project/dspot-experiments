package org.jboss.as.test.integration.microprofile.opentracing;


import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import java.net.URL;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.microprofile.opentracing.application.WithCustomOperationNameEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ResourceWithCustomOperationNameBeanTestCase {
    @Inject
    Tracer tracer;

    @ArquillianResource
    private URL url;

    @Test
    public void customOperationName() throws Exception {
        Assert.assertTrue(((tracer) instanceof MockTracer));
        MockTracer mockTracer = ((MockTracer) (tracer));
        performCall("opentracing/with-custom-operation-name");
        List<MockSpan> spans = mockTracer.finishedSpans();
        Assert.assertEquals(3, spans.size());
        Assert.assertEquals("my-custom-method-operation-name", spans.get(0).operationName());
        Assert.assertEquals("my-custom-class-operation-name", spans.get(1).operationName());
        Assert.assertTrue(spans.get(2).operationName().contains(WithCustomOperationNameEndpoint.class.getName()));
    }
}

