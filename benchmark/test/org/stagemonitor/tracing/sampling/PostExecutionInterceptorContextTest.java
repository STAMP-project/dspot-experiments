package org.stagemonitor.tracing.sampling;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.tracing.SpanContextInformation;


public class PostExecutionInterceptorContextTest {
    private PostExecutionInterceptorContext interceptorContext = new PostExecutionInterceptorContext(Mockito.mock(SpanContextInformation.class));

    @Test
    public void excludeCallTree() throws Exception {
        Assert.assertFalse(interceptorContext.isExcludeCallTree());
        interceptorContext.excludeCallTree("reasons");
        Assert.assertTrue(interceptorContext.isExcludeCallTree());
    }

    @Test
    public void mustPreserveCallTree() throws Exception {
        interceptorContext.excludeCallTree("reasons");
        interceptorContext.mustPreserveCallTree("reasons");
        Assert.assertFalse(interceptorContext.isExcludeCallTree());
        interceptorContext.excludeCallTree("reasons");
        Assert.assertFalse(interceptorContext.isExcludeCallTree());
    }
}

