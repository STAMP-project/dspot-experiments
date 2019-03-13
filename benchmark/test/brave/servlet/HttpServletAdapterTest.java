package brave.servlet;


import brave.Span;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HttpServletAdapterTest {
    HttpServletAdapter adapter = new HttpServletAdapter();

    @Mock
    HttpServletRequest request;

    @Mock
    Span span;

    @Test
    public void path_doesntCrashOnNullUrl() {
        assertThat(adapter.path(request)).isNull();
    }

    @Test
    public void path_getRequestURI() {
        Mockito.when(request.getRequestURI()).thenReturn("/bar");
        assertThat(adapter.path(request)).isEqualTo("/bar");
    }

    @Test
    public void url_derivedFromUrlAndQueryString() {
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://foo:8080/bar"));
        Mockito.when(request.getQueryString()).thenReturn("hello=world");
        assertThat(adapter.url(request)).isEqualTo("http://foo:8080/bar?hello=world");
    }

    @Test
    public void parseClientIpAndPort_prefersXForwardedFor() {
        Mockito.when(span.remoteIpAndPort("1.2.3.4", 0)).thenReturn(true);
        Mockito.when(adapter.requestHeader(request, "X-Forwarded-For")).thenReturn("1.2.3.4");
        adapter.parseClientIpAndPort(request, span);
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 0);
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseClientIpAndPort_skipsRemotePortOnXForwardedFor() {
        Mockito.when(request.getHeader("X-Forwarded-For")).thenReturn("1.2.3.4");
        Mockito.when(span.remoteIpAndPort("1.2.3.4", 0)).thenReturn(true);
        adapter.parseClientIpAndPort(request, span);
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 0);
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseClientIpAndPort_acceptsRemoteAddr() {
        Mockito.when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        Mockito.when(request.getRemotePort()).thenReturn(61687);
        adapter.parseClientIpAndPort(request, span);
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 61687);
        Mockito.verifyNoMoreInteractions(span);
    }
}

