package brave.httpclient;


import brave.test.http.ITHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;


public class ITTracingHttpClientBuilder extends ITHttpClient<CloseableHttpClient> {
    @Test
    public void currentSpanVisibleToUserFilters() throws Exception {
        server.enqueue(new MockResponse());
        closeClient(client);
        client = TracingHttpClientBuilder.create(httpTracing).disableAutomaticRetries().addInterceptorFirst(((HttpRequestInterceptor) (( request, context) -> request.setHeader("my-id", currentTraceContext.get().traceIdString())))).build();
        get(client, "/foo");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("x-b3-traceId")).isEqualTo(request.getHeader("my-id"));
        takeSpan();
    }
}

