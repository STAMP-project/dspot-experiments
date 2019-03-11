package com.gateway.gateway.responserewriting;


import com.netflix.zuul.context.RequestContext;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import springfox.documentation.swagger2.web.Swagger2Controller;


/**
 * Tests SwaggerBasePathRewritingFilter class.
 */
public class SwaggerBasePathRewritingFilterIntegrationTest {
    private SwaggerBasePathRewritingFilter filter = new SwaggerBasePathRewritingFilter();

    @Test
    public void shouldFilter_on_default_swagger_url() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", Swagger2Controller.DEFAULT_URL);
        RequestContext.getCurrentContext().setRequest(request);
        Assert.assertTrue(filter.shouldFilter());
    }

    /**
     * Zuul DebugFilter can be triggered by "deug" parameter.
     */
    @Test
    public void shouldFilter_on_default_swagger_url_with_param() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", Swagger2Controller.DEFAULT_URL);
        request.setParameter("debug", "true");
        RequestContext.getCurrentContext().setRequest(request);
        Assert.assertTrue(filter.shouldFilter());
    }

    @Test
    public void shouldNotFilter_on_wrong_url() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/management/info");
        RequestContext.getCurrentContext().setRequest(request);
        Assert.assertFalse(filter.shouldFilter());
    }

    @Test
    public void run_on_valid_response() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", ("/service1" + (Swagger2Controller.DEFAULT_URL)));
        RequestContext context = RequestContext.getCurrentContext();
        context.setRequest(request);
        MockHttpServletResponse response = new MockHttpServletResponse();
        context.setResponseGZipped(false);
        context.setResponse(response);
        InputStream in = IOUtils.toInputStream("{\"basePath\":\"/\"}", StandardCharsets.UTF_8);
        context.setResponseDataStream(in);
        filter.run();
        Assert.assertEquals("UTF-8", response.getCharacterEncoding());
        Assert.assertEquals("{\"basePath\":\"/service1\"}", context.getResponseBody());
    }
}

