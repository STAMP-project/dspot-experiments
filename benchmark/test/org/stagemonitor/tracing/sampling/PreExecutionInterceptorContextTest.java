package org.stagemonitor.tracing.sampling;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.tracing.SpanContextInformation;


public class PreExecutionInterceptorContextTest {
    private PreExecutionInterceptorContext interceptorContext = new PreExecutionInterceptorContext(Mockito.mock(SpanContextInformation.class));

    @Test
    public void mustCollectCallTree() throws Exception {
        interceptorContext.shouldNotCollectCallTree("reasons");
        Assert.assertFalse(interceptorContext.isCollectCallTree());
        interceptorContext.mustCollectCallTree("reasons");
        Assert.assertTrue(interceptorContext.isCollectCallTree());
        interceptorContext.shouldNotCollectCallTree("reasons");
        Assert.assertTrue(interceptorContext.isCollectCallTree());
    }

    @Test
    public void shouldNotCollectCallTree() throws Exception {
        Assert.assertTrue(interceptorContext.isCollectCallTree());
        interceptorContext.shouldNotCollectCallTree("reasons");
        Assert.assertFalse(interceptorContext.isCollectCallTree());
    }
}

