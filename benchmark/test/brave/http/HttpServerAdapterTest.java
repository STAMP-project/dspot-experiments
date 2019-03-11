package brave.http;


import brave.Span;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HttpServerAdapterTest {
    @Mock
    HttpServerAdapter<Object, Object> adapter;

    @Mock
    Span span;

    Object request = new Object();

    Object response = new Object();

    @Test
    public void path_doesntCrashOnNullUrl() {
        assertThat(adapter.path(request)).isNull();
    }

    @Test
    public void statusCodeAsInt_callsStatusCodeByDefault() {
        Mockito.when(adapter.statusCode(response)).thenReturn(400);
        assertThat(adapter.statusCodeAsInt(response)).isEqualTo(400);
    }

    @Test
    public void path_derivedFromUrl() {
        Mockito.when(adapter.url(request)).thenReturn("http://foo:8080/bar?hello=world");
        assertThat(adapter.path(request)).isEqualTo("/bar");
    }

    @Test
    public void parseClientIpAndPort_prefersXForwardedFor() {
        Mockito.when(adapter.requestHeader(request, "X-Forwarded-For")).thenReturn("1.2.3.4");
        adapter.parseClientIpAndPort(request, span);
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 0);
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseClientIpAndPort_skipsOnNoIp() {
        adapter.parseClientIpAndPort(request, span);
        Mockito.verifyNoMoreInteractions(span);
    }
}

