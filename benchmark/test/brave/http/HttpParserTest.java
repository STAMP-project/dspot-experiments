package brave.http;


import brave.SpanCustomizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HttpParserTest {
    @Mock
    HttpClientAdapter<Object, Object> adapter;

    @Mock
    SpanCustomizer customizer;

    Object request = new Object();

    Object response = new Object();

    HttpParser parser = new HttpParser();

    @Test
    public void spanName_isMethod() {
        Mockito.when(adapter.method(request)).thenReturn("GET");
        assertThat(parser.spanName(adapter, request)).isEqualTo("GET");// note: in practice this will become lowercase

    }

    @Test
    public void request_addsMethodAndPath() {
        Mockito.when(adapter.method(request)).thenReturn("GET");
        Mockito.when(adapter.path(request)).thenReturn("/foo");
        parser.request(adapter, request, customizer);
        Mockito.verify(customizer).tag("http.method", "GET");
        Mockito.verify(customizer).tag("http.path", "/foo");
    }

    @Test
    public void request_doesntCrashOnNullPath() {
        parser.request(adapter, request, customizer);
        Mockito.verify(customizer, Mockito.never()).tag("http.path", null);
    }

    @Test
    public void response_tagsStatusAndErrorOnResponseCode() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(400);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer).tag("http.status_code", "400");
        Mockito.verify(customizer).tag("error", "400");
    }

    @Test
    public void response_statusZeroIsNotAnError() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(0);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer, Mockito.never()).tag("http.status_code", "0");
        Mockito.verify(customizer, Mockito.never()).tag("error", "0");
    }

    // Ensures "HTTP/1.1 101 Switching Protocols" aren't classified as error spans
    @Test
    public void response_status101IsNotAnError() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(101);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer).tag("http.status_code", "101");
        Mockito.verify(customizer, Mockito.never()).tag("error", "101");
    }

    @Test
    public void response_tagsErrorFromException() {
        parser.response(adapter, response, new RuntimeException("drat"), customizer);
        Mockito.verify(customizer).tag("error", "drat");
    }

    @Test
    public void response_tagsErrorPrefersExceptionVsResponseCode() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(400);
        parser.response(adapter, response, new RuntimeException("drat"), customizer);
        Mockito.verify(customizer).tag("error", "drat");
    }

    @Test
    public void response_tagsErrorOnExceptionEvenIfStatusOk() {
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(200);
        parser.response(adapter, response, new RuntimeException("drat"), customizer);
        Mockito.verify(customizer).tag("error", "drat");
    }

    @Test
    public void routeBasedName() {
        Mockito.when(adapter.methodFromResponse(response)).thenReturn("GET");
        Mockito.when(adapter.route(response)).thenReturn("/users/:userId");
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(200);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer).name("GET /users/:userId");// zipkin will implicitly lowercase this

    }

    @Test
    public void routeBasedName_redirect() {
        Mockito.when(adapter.methodFromResponse(response)).thenReturn("GET");
        Mockito.when(adapter.route(response)).thenReturn("");
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(307);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer).name("GET redirected");// zipkin will implicitly lowercase this

    }

    @Test
    public void routeBasedName_notFound() {
        Mockito.when(adapter.methodFromResponse(response)).thenReturn("DELETE");
        Mockito.when(adapter.route(response)).thenReturn("");
        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(404);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer).name("DELETE not_found");// zipkin will implicitly lowercase this

    }

    @Test
    public void routeBasedName_skipsOnMissingData() {
        Mockito.when(adapter.methodFromResponse(response)).thenReturn("DELETE");
        Mockito.when(adapter.route(response)).thenReturn(null);// missing!

        Mockito.when(adapter.statusCodeAsInt(response)).thenReturn(404);
        parser.response(adapter, response, null, customizer);
        Mockito.verify(customizer, Mockito.never()).name(ArgumentMatchers.any(String.class));
    }
}

