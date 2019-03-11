package brave.spring.web;


import brave.test.http.ITHttpClient;
import java.util.Arrays;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;


public class ITTracingClientHttpRequestInterceptor extends ITHttpClient<ClientHttpRequestFactory> {
    ClientHttpRequestInterceptor interceptor;

    @Test
    public void currentSpanVisibleToUserInterceptors() throws Exception {
        server.enqueue(new MockResponse());
        RestTemplate restTemplate = new RestTemplate(client);
        restTemplate.setInterceptors(Arrays.asList(interceptor, ( request, body, execution) -> {
            request.getHeaders().add("my-id", currentTraceContext.get().traceIdString());
            return execution.execute(request, body);
        }));
        restTemplate.getForObject(server.url("/foo").toString(), String.class);
        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("x-b3-traceId")).isEqualTo(request.getHeader("my-id"));
        takeSpan();
    }
}

