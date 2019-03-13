package brave.httpclient;


import brave.Span;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HttpAdapterTest {
    @Mock
    HttpRequestWrapper request;

    @Mock
    Span span;

    @Test
    public void parseTargetAddress_skipsOnNoop() {
        Mockito.when(span.isNoop()).thenReturn(true);
        HttpAdapter.parseTargetAddress(request, span);
        Mockito.verify(span).isNoop();
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseTargetAddress_prefersAddress() throws UnknownHostException {
        Mockito.when(span.isNoop()).thenReturn(false);
        Mockito.when(span.remoteIpAndPort("1.2.3.4", (-1))).thenReturn(true);
        Mockito.when(request.getTarget()).thenReturn(new HttpHost(InetAddress.getByName("1.2.3.4"), "3.4.5.6", (-1), "http"));
        HttpAdapter.parseTargetAddress(request, span);
        Mockito.verify(span).isNoop();
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", (-1));
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseTargetAddress_acceptsHostname() {
        Mockito.when(span.isNoop()).thenReturn(false);
        Mockito.when(request.getTarget()).thenReturn(new HttpHost("1.2.3.4"));
        HttpAdapter.parseTargetAddress(request, span);
        Mockito.verify(span).isNoop();
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", (-1));
        Mockito.verifyNoMoreInteractions(span);
    }

    @Test
    public void parseTargetAddress_IpAndPortFromHost() {
        Mockito.when(span.isNoop()).thenReturn(false);
        Mockito.when(span.remoteIpAndPort("1.2.3.4", 9999)).thenReturn(true);
        Mockito.when(request.getTarget()).thenReturn(new HttpHost("1.2.3.4", 9999));
        HttpAdapter.parseTargetAddress(request, span);
        Mockito.verify(span).isNoop();
        Mockito.verify(span).remoteIpAndPort("1.2.3.4", 9999);
        Mockito.verifyNoMoreInteractions(span);
    }
}

