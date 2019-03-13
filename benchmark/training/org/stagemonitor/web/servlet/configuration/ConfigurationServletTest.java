package org.stagemonitor.web.servlet.configuration;


import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.configuration.ConfigurationRegistry;


public class ConfigurationServletTest {
    private ConfigurationRegistry configuration;

    private ConfigurationServlet configurationServlet;

    @Test
    public void testUpdateConfigurationWithoutConfigurationSource() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("key", "stagemonitor.internal.monitoring");
        request.addParameter("value", "true");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("Missing parameter 'configurationSource'", response.getContentAsString());
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testUpdateConfigurationWithoutKey() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("value", "true");
        request.addParameter("configurationSource", "test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("Missing parameter 'key'", response.getContentAsString());
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testReload() throws IOException, ServletException {
        for (String method : Arrays.asList("POST", "GET")) {
            MockHttpServletRequest request = new MockHttpServletRequest(method, "/stagemonitor/configuration");
            request.addParameter("reload", "");
            final MockHttpServletResponse res = new MockHttpServletResponse();
            configurationServlet.service(request, res);
            Assert.assertEquals(204, res.getStatus());
            Assert.assertEquals("", res.getContentAsString());
        }
    }

    @Test
    public void testNoError() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("key", "stagemonitor.internal.monitoring");
        request.addParameter("value", "true");
        request.addParameter("configurationSource", "test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("", response.getContentAsString());
        Assert.assertEquals(204, response.getStatus());
    }

    @Test
    public void testIllegalArgumentException() throws IOException, ServletException {
        Mockito.doThrow(new IllegalArgumentException("test")).when(configuration).save(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("key", "stagemonitor.internal.monitoring");
        request.addParameter("value", "true");
        request.addParameter("configurationSource", "test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("test", response.getContentAsString());
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testIllegalStateException() throws IOException, ServletException {
        Mockito.doThrow(new IllegalStateException("test")).when(configuration).save(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("key", "stagemonitor.internal.monitoring");
        request.addParameter("value", "true");
        request.addParameter("configurationSource", "test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("test", response.getContentAsString());
        Assert.assertEquals(401, response.getStatus());
    }

    @Test
    public void testUnsupportedOperationException() throws IOException, ServletException {
        Mockito.doThrow(new UnsupportedOperationException("test")).when(configuration).save(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("key", "stagemonitor.internal.monitoring");
        request.addParameter("value", "true");
        request.addParameter("configurationSource", "test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("test", response.getContentAsString());
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testException() throws IOException, ServletException {
        Mockito.doThrow(new RuntimeException("test")).when(configuration).save(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stagemonitor/configuration");
        request.addParameter("key", "stagemonitor.internal.monitoring");
        request.addParameter("value", "true");
        request.addParameter("configurationSource", "test");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        configurationServlet.service(request, response);
        Assert.assertEquals("Internal Error. Check your server logs.", response.getContentAsString());
        Assert.assertEquals(500, response.getStatus());
    }
}

