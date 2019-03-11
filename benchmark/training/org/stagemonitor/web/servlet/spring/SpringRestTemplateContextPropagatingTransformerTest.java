package org.stagemonitor.web.servlet.spring;


import B3HeaderFormat.SPAN_ID_NAME;
import B3HeaderFormat.TRACE_ID_NAME;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.ThreadLocalScopeManager;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.tracing.B3Propagator;
import org.stagemonitor.web.servlet.spring.SpringRestTemplateContextPropagatingTransformer.SpringRestTemplateContextPropagatingInterceptor;


public class SpringRestTemplateContextPropagatingTransformerTest {
    private final MockTracer mockTracer = new MockTracer(new ThreadLocalScopeManager(), new B3Propagator());

    private TracingPlugin tracingPlugin;

    @Test
    public void testRestTemplateHasInterceptor() throws Exception {
        final List<RestTemplate> restTemplates = Arrays.asList(new RestTemplate(), new RestTemplate(new SimpleClientHttpRequestFactory()), new RestTemplate(Collections.singletonList(new StringHttpMessageConverter())));
        for (RestTemplate restTemplate : restTemplates) {
            assertThat(restTemplate.getInterceptors()).hasSize(1);
            assertThat(restTemplate.getInterceptors()).hasAtLeastOneElementOfType(SpringRestTemplateContextPropagatingInterceptor.class);
        }
    }

    @Test
    public void testB3HeaderContextPropagation() throws Exception {
        HttpRequest httpRequest = new org.springframework.mock.http.client.MockClientHttpRequest(HttpMethod.GET, new URI("http://example.com/foo?bar=baz"));
        new SpringRestTemplateContextPropagatingInterceptor(tracingPlugin).intercept(httpRequest, null, Mockito.mock(ClientHttpRequestExecution.class));
        assertThat(httpRequest.getHeaders()).containsKey(SPAN_ID_NAME);
        assertThat(httpRequest.getHeaders()).containsKey(TRACE_ID_NAME);
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        assertThat(mockTracer.finishedSpans().get(0).operationName()).isEqualTo("GET http://example.com/foo");
    }

    @Test
    public void testRemoveQuery() throws Exception {
        assertThat(SpringRestTemplateContextPropagatingInterceptor.removeQuery(new URI("http://example.com/foo"))).isEqualTo("http://example.com/foo");
        assertThat(SpringRestTemplateContextPropagatingInterceptor.removeQuery(new URI("http://example.com/foo?bar=baz"))).isEqualTo("http://example.com/foo");
    }
}

