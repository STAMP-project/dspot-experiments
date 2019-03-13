package org.stagemonitor.web.servlet.filter;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.web.servlet.ServletPlugin;


public class HttpRequestMonitorFilterTest {
    private ConfigurationRegistry configuration = Mockito.mock(ConfigurationRegistry.class);

    private ServletPlugin servletPlugin = Mockito.spy(new ServletPlugin());

    private CorePlugin corePlugin = Mockito.mock(CorePlugin.class);

    private TracingPlugin tracingPlugin = Mockito.mock(TracingPlugin.class);

    private HttpRequestMonitorFilter httpRequestMonitorFilter;

    private String testHtml = "<html><head></head><body></body></html>";

    @Test
    public void testWidgetInjector() throws IOException, ServletException {
        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        httpRequestMonitorFilter.doFilter(requestWithAccept("text/html"), servletResponse, writeInResponseWhenCallingDoFilter(testHtml));
        final String response = servletResponse.getContentAsString();
        assertThat(response).startsWith("<html><head><script");
        assertThat(response).endsWith("</body></html>");
        assertThat(response).contains("window.StagemonitorLoaded");
        assertThat(response).contains("'/stagemonitor/public/eum.js'");
    }

    @Test
    public void testBinaryData() throws IOException, ServletException {
        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        httpRequestMonitorFilter.doFilter(requestWithAccept("text/html"), servletResponse, writeBinaryDataInResponseWhenCallingDoFilter(new byte[]{ 1 }));
        Assert.assertEquals(1, servletResponse.getContentAsByteArray().length);
        Assert.assertEquals(1, servletResponse.getContentAsByteArray()[0]);
    }

    @Test
    public void testWidgetShouldNotBeInjectedIfInjectionDisabled() throws IOException, ServletException {
        Mockito.doReturn(false).when(servletPlugin).isClientSpanCollectionEnabled();
        Mockito.doReturn(false).when(servletPlugin).isWidgetAndStagemonitorEndpointsAllowed(ArgumentMatchers.any(HttpServletRequest.class));
        Mockito.doReturn(false).when(servletPlugin).isWidgetEnabled();
        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        httpRequestMonitorFilter.doFilter(requestWithAccept("text/html"), servletResponse, writeInResponseWhenCallingDoFilter(testHtml));
        assertThat(servletResponse.getContentAsString()).isEqualTo(testHtml);
    }

    @Test
    public void testWidgetShouldNotBeInjectedIfHtmlIsNotAcceptable() throws IOException, ServletException {
        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        httpRequestMonitorFilter.doFilter(requestWithAccept("application/json"), servletResponse, writeInResponseWhenCallingDoFilter(testHtml));
        assertThat(servletResponse.getContentAsString()).isEqualTo(testHtml);
    }

    @Test
    public void testWidgetInjectorWithMultipleBodyTags() throws IOException, ServletException {
        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        final String html = "<html><body></body><body></body><body></body><body>asdf</body></html>";
        httpRequestMonitorFilter.doFilter(requestWithAccept("text/html"), servletResponse, writeInResponseWhenCallingDoFilter(html));
        assertThat(servletResponse.getContentAsString()).startsWith("<html><body></body><body></body><body></body><body>asdf");
        assertThat(servletResponse.getContentAsString()).endsWith("</body></html>");
        assertThat(servletResponse.getContentAsString()).contains("window.StagemonitorLoaded");
    }

    @Test
    public void testWidgetInjectorWithMultipleHeadTags() throws IOException, ServletException {
        final MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        final String html = "<html><head></head><body><script>var html = '<head></head>'</script></body></html>";
        httpRequestMonitorFilter.doFilter(requestWithAccept("text/html"), servletResponse, writeInResponseWhenCallingDoFilter(html));
        final String response = servletResponse.getContentAsString();
        assertThat(response).startsWith("<html><head><script");
        assertThat(response).endsWith("</body></html>");
        assertThat(response).contains("window.StagemonitorLoaded");
    }
}

