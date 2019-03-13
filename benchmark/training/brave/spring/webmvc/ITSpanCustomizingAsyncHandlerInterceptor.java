package brave.spring.webmvc;


import brave.test.http.ITServletContainer;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin2.Span;


/**
 * This tests when you use servlet for tracing but MVC for tagging
 */
public class ITSpanCustomizingAsyncHandlerInterceptor extends ITServletContainer {
    @Test
    public void addsControllerTags() throws Exception {
        get("/foo");
        Span span = takeSpan();
        assertThat(span.tags()).containsEntry("mvc.controller.class", "Servlet3TestController").containsEntry("mvc.controller.method", "foo");
    }

    @Configuration
    @EnableWebMvc
    static class TracingConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new SpanCustomizingAsyncHandlerInterceptor());
        }
    }
}

