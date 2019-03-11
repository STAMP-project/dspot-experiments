package org.stagemonitor.web.servlet;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class StagemonitorFileServletTest {
    private StagemonitorFileServlet fileServlet;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void testGetStaticResource() throws Exception {
        request.setRequestURI("/stagemonitor/static/test.html");
        fileServlet.service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("test", response.getContentAsString());
        Assert.assertTrue(((response.getContentType().equals("text/html")) || (response.getContentType().equals("application/octet-stream"))));
    }

    @Test
    public void testGetStaticResourceDirUp() throws Exception {
        request.setRequestURI("/stagemonitor/static/../test2.js");
        fileServlet.service(request, response);
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("", response.getContentAsString());
    }
}

