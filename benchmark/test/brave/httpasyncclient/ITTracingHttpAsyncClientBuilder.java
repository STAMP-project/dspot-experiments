package brave.httpasyncclient;


import brave.test.http.ITHttpAsyncClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;


public class ITTracingHttpAsyncClientBuilder extends ITHttpAsyncClient<CloseableHttpAsyncClient> {
    @Test
    public void currentSpanVisibleToUserFilters() throws Exception {
        server.enqueue(new MockResponse());
        closeClient(client);
        client = TracingHttpAsyncClientBuilder.create(httpTracing).addInterceptorLast(((HttpRequestInterceptor) (( request, context) -> request.setHeader("my-id", currentTraceContext.get().traceIdString())))).build();
        client.start();
        get(client, "/foo");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("x-b3-traceId")).isEqualTo(request.getHeader("my-id"));
        takeSpan();
    }
}

