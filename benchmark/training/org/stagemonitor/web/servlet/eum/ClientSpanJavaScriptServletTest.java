package org.stagemonitor.web.servlet.eum;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.web.servlet.ServletPlugin;


public class ClientSpanJavaScriptServletTest {
    @Test
    public void testDoGet_simple_Cache1Minute() throws Exception {
        // Given
        ClientSpanJavaScriptServlet clientSpanJavaScriptServlet = new ClientSpanJavaScriptServlet(mockServletPluginWithCacheTime(1));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        // When
        clientSpanJavaScriptServlet.doGet(request, response);
        // Then
        assertThat(response.getContentAsString()).contains("EumObject");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader(ClientSpanJavaScriptServlet.CACHE_CONTROL)).isEqualTo("max-age=60");
    }

    @Test
    public void testDoGet_simple_DoNotCache() throws Exception {
        // Given
        ClientSpanJavaScriptServlet clientSpanJavaScriptServlet = new ClientSpanJavaScriptServlet(mockServletPluginWithCacheTime(0));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        // When
        clientSpanJavaScriptServlet.doGet(request, response);
        // Then
        assertThat(response.getContentAsString()).contains("EumObject");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader(ClientSpanJavaScriptServlet.CACHE_CONTROL)).isEqualTo("no-cache");
    }

    @Test
    public void testDoGet_conditionalGet() throws Exception {
        // Given
        ClientSpanJavaScriptServlet clientSpanJavaScriptServlet = new ClientSpanJavaScriptServlet(mockServletPluginWithCacheTime(0));
        final MockHttpServletResponse response = new MockHttpServletResponse();
        // When
        clientSpanJavaScriptServlet.doGet(new MockHttpServletRequest(), response);
        final String etag = response.getHeader("etag");
        assertThat(etag).isNotNull();
        final MockHttpServletRequest conditionalGetRequest = new MockHttpServletRequest();
        conditionalGetRequest.addHeader("if-none-match", etag);
        final MockHttpServletResponse conditionalGetResponse = new MockHttpServletResponse();
        clientSpanJavaScriptServlet.doGet(conditionalGetRequest, conditionalGetResponse);
        // Then
        assertThat(conditionalGetResponse.getStatus()).isEqualTo(304);
        assertThat(conditionalGetResponse.getContentLength()).isZero();
    }

    @Test
    public void testDoGet_returns404IfEumDisabled() throws Exception {
        // Given
        ServletPlugin servletPlugin = mockServletPluginWithCacheTime(0);
        Mockito.when(servletPlugin.isClientSpanCollectionEnabled()).thenReturn(false);
        ClientSpanJavaScriptServlet clientSpanJavaScriptServlet = new ClientSpanJavaScriptServlet(servletPlugin);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        // When
        clientSpanJavaScriptServlet.doGet(new MockHttpServletRequest(), response);
        // Then
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getContentAsString()).isEmpty();
    }
}

