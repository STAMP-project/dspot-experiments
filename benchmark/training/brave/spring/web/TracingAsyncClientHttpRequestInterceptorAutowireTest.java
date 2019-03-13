package brave.spring.web;


import brave.Tracing;
import brave.http.HttpTracing;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;


public class TracingAsyncClientHttpRequestInterceptorAutowireTest {
    @Configuration
    static class HttpTracingConfiguration {
        @Bean
        HttpTracing httpTracing() {
            return HttpTracing.create(Tracing.newBuilder().build());
        }
    }

    @Test
    public void autowiredWithBeanConfig() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(TracingAsyncClientHttpRequestInterceptorAutowireTest.HttpTracingConfiguration.class);
        ctx.register(TracingAsyncClientHttpRequestInterceptor.class);
        ctx.refresh();
        ctx.getBean(AsyncClientHttpRequestInterceptor.class);
    }
}

