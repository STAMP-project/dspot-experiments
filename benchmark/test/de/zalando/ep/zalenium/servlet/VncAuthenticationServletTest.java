package de.zalando.ep.zalenium.servlet;


import de.zalando.ep.zalenium.container.DockerContainerClient;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.grid.internal.GridRegistry;


@SuppressWarnings("Duplicates")
public class VncAuthenticationServletTest {
    private GridRegistry registry;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Supplier<DockerContainerClient> originalContainerClient;

    @Test
    public void testAuthenticationSucceedsForNoVnc() {
        VncAuthenticationServlet vncAuthenticationServlet = new VncAuthenticationServlet(registry);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(request.getHeader("X-Original-URI")).thenReturn("http://localhost/vnc/host/machine1/port/40000/?nginx=machine1:40000&view_only=true");
        vncAuthenticationServlet.doGet(request, response);
        Mockito.verify(response).setStatus(statusCaptor.capture());
        MatcherAssert.assertThat(statusCaptor.getValue(), CoreMatchers.equalTo(200));
    }

    @Test
    public void testAuthenticationSucceedsForWebsockify() {
        VncAuthenticationServlet vncAuthenticationServlet = new VncAuthenticationServlet(registry);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(request.getHeader("X-Original-URI")).thenReturn("http://localhost/proxy/machine1:40000/websockify");
        vncAuthenticationServlet.doGet(request, response);
        Mockito.verify(response).setStatus(statusCaptor.capture());
        MatcherAssert.assertThat(statusCaptor.getValue(), CoreMatchers.equalTo(200));
    }

    @Test
    public void testAuthenticationFailsForWebsockify() {
        VncAuthenticationServlet vncAuthenticationServlet = new VncAuthenticationServlet(registry);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(request.getHeader("X-Original-URI")).thenReturn("http://localhost/proxy/not_a_machine:40000/websockify");
        vncAuthenticationServlet.doGet(request, response);
        Mockito.verify(response).setStatus(statusCaptor.capture());
        MatcherAssert.assertThat(statusCaptor.getValue(), CoreMatchers.equalTo(403));
    }

    @Test
    public void testAuthenticationFailsForVncWithBadPort() {
        VncAuthenticationServlet vncAuthenticationServlet = new VncAuthenticationServlet(registry);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(request.getHeader("X-Original-URI")).thenReturn("http://localhost/vnc/host/machine1/port/50003/?nginx=machine1:50003&view_only=true");
        vncAuthenticationServlet.doGet(request, response);
        Mockito.verify(response).setStatus(statusCaptor.capture());
        MatcherAssert.assertThat(statusCaptor.getValue(), CoreMatchers.equalTo(403));
    }

    @Test
    public void testAuthenticationFailsForVncWithBadHost() {
        VncAuthenticationServlet vncAuthenticationServlet = new VncAuthenticationServlet(registry);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.when(request.getHeader("X-Original-URI")).thenReturn("http://localhost/vnc/host/fakehost/port/40000/?nginx=fakehost:40000&view_only=true");
        vncAuthenticationServlet.doGet(request, response);
        Mockito.verify(response).setStatus(statusCaptor.capture());
        MatcherAssert.assertThat(statusCaptor.getValue(), CoreMatchers.equalTo(403));
    }

    @Test
    public void testAuthenticationFailsWithNoHeader() {
        VncAuthenticationServlet vncAuthenticationServlet = new VncAuthenticationServlet(registry);
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        vncAuthenticationServlet.doGet(request, response);
        Mockito.verify(response).setStatus(statusCaptor.capture());
        MatcherAssert.assertThat(statusCaptor.getValue(), CoreMatchers.equalTo(403));
    }
}

