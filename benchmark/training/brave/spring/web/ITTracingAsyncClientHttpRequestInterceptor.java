package brave.spring.web;


import brave.test.http.ITHttpAsyncClient;
import java.util.Arrays;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.web.client.AsyncRestTemplate;


public class ITTracingAsyncClientHttpRequestInterceptor extends ITHttpAsyncClient<AsyncClientHttpRequestFactory> {
    AsyncClientHttpRequestInterceptor interceptor;

    @Test
    public void currentSpanVisibleToUserInterceptors() throws Exception {
        server.enqueue(new MockResponse());
        AsyncRestTemplate restTemplate = new AsyncRestTemplate(client);
        restTemplate.setInterceptors(Arrays.asList(interceptor, ( request, body, execution) -> {
            request.getHeaders().add("my-id", currentTraceContext.get().traceIdString());
            return execution.executeAsync(request, body);
        }));
        restTemplate.getForEntity(server.url("/foo").toString(), String.class).get();
        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("x-b3-traceId")).isEqualTo(request.getHeader("my-id"));
        takeSpan();
    }
}

