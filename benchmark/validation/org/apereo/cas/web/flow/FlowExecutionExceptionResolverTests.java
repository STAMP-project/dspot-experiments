package org.apereo.cas.web.flow;


import lombok.val;
import org.apereo.cas.web.FlowExecutionExceptionResolver;
import org.apereo.spring.webflow.plugin.ClientFlowExecutionRepositoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.test.MockRequestContext;


/**
 * This is {@link FlowExecutionExceptionResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class FlowExecutionExceptionResolverTests {
    @Test
    public void verifyActionNull() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val r = new FlowExecutionExceptionResolver();
        Assertions.assertNull(r.resolveException(request, response, new Object(), new RuntimeException()));
    }

    @Test
    public void verifyActionModelView() {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.setRequestURI("/cas/login");
        request.setQueryString("param=value&something=something");
        val response = new MockHttpServletResponse();
        context.setExternalContext(new org.springframework.webflow.context.servlet.ServletExternalContext(new MockServletContext(), request, response));
        val r = new FlowExecutionExceptionResolver();
        val mv = r.resolveException(request, response, new Object(), new ClientFlowExecutionRepositoryException("error"));
        Assertions.assertNotNull(mv);
        Assertions.assertTrue(mv.getModel().containsKey(r.getModelKey()));
    }
}

