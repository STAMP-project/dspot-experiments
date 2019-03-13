package de.zalando.ep.zalenium.servlet;


import de.zalando.ep.zalenium.container.DockerContainerClient;
import java.io.IOException;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.grid.internal.GridRegistry;


@SuppressWarnings("Duplicates")
public class LiveNodeServletTest {
    private GridRegistry registry;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Supplier<DockerContainerClient> originalContainerClient;

    @Test
    public void addedNodesAreRenderedInServlet() throws IOException {
        LivePreviewServlet livePreviewServletServlet = new LivePreviewServlet(registry);
        livePreviewServletServlet.doPost(request, response);
        String responseContent = response.getOutputStream().toString();
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("Zalenium Live Preview"));
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("http://machine1:4444"));
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("http://machine2:4444"));
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("/vnc/host/machine1/port/40000/?nginx=&path=proxy/machine1:40000/websockify&view_only=true'"));
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("/vnc/host/machine1/port/40000/?nginx=&path=proxy/machine1:40000/websockify&view_only=false'"));
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("/vnc/host/machine2/port/40000/?nginx=&path=proxy/machine2:40000/websockify&view_only=true'"));
        MatcherAssert.assertThat(responseContent, CoreMatchers.containsString("/vnc/host/machine2/port/40000/?nginx=&path=proxy/machine2:40000/websockify&view_only=false'"));
    }

    @Test
    public void postAndGetReturnSameContent() throws IOException {
        LivePreviewServlet livePreviewServletServlet = new LivePreviewServlet(registry);
        livePreviewServletServlet.doPost(request, response);
        String postResponseContent = response.getOutputStream().toString();
        livePreviewServletServlet.doGet(request, response);
        String getResponseContent = response.getOutputStream().toString();
        MatcherAssert.assertThat(getResponseContent, CoreMatchers.containsString(postResponseContent));
    }

    @Test
    public void noRefreshInHtmlWhenParameterIsInvalid() throws IOException {
        Mockito.when(request.getParameter("refresh")).thenReturn("XYZ");
        LivePreviewServlet livePreviewServletServlet = new LivePreviewServlet(registry);
        livePreviewServletServlet.doPost(request, response);
        String postResponseContent = response.getOutputStream().toString();
        MatcherAssert.assertThat(postResponseContent, CoreMatchers.containsString("<meta http-equiv='refresh' content='XYZ' />"));
    }
}

