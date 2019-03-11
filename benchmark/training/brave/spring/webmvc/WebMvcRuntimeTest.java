package brave.spring.webmvc;


import brave.http.HttpTracing;
import brave.spring.webmvc.WebMvcRuntime.WebMvc25;
import brave.spring.webmvc.WebMvcRuntime.WebMvc31;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;


// Added to declutter console: tells power mock not to mess with implicit classes we aren't testing
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.logging.*", "javax.script.*" })
@PrepareForTest(WebMvcRuntime.class)
public class WebMvcRuntimeTest {
    @Test
    public void findWebMvcRuntime_HandlerMethod_exists() throws Exception {
        assertThat(WebMvcRuntime.findWebMvcRuntime()).isInstanceOf(WebMvc31.class);
    }

    @Test
    public void findWebMvcRuntime_HandlerMethod_notFound() throws Exception {
        mockStatic(Class.class);
        when(Class.forName(HandlerMethod.class.getName())).thenThrow(new ClassNotFoundException());
        assertThat(WebMvcRuntime.findWebMvcRuntime()).isInstanceOf(WebMvc25.class);
    }

    @Test
    public void WebMvc31_isHandlerMethod() {
        HandlerMethod handlerMethod = Mockito.mock(HandlerMethod.class);
        assertThat(new WebMvc31().isHandlerMethod(handlerMethod)).isTrue();
    }

    /**
     * Due to HandlerMethod being only present after 3.1, we can't look up the class in 2.5
     */
    @Test
    public void WebMvc25_isHandlerMethod_isFalse() {
        HandlerMethod handlerMethod = Mockito.mock(HandlerMethod.class);
        assertThat(new WebMvc25().isHandlerMethod(handlerMethod)).isFalse();
    }

    /**
     * Spring 3+ can get beans by type, so use that!
     */
    @Test
    public void WebMvc31_httpTracing_byType() {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        new WebMvc31().httpTracing(context);
        Mockito.verify(context).getBean(HttpTracing.class);
        Mockito.verifyNoMoreInteractions(context);
    }

    /**
     * Spring 2.5 cannot get beans by type, so fallback to name
     */
    @Test
    public void WebMvc25_httpTracing_byName() {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(context.containsBean("httpTracing")).thenReturn(true);
        when(context.getBean("httpTracing")).thenReturn(Mockito.mock(HttpTracing.class));
        new WebMvc25().httpTracing(context);
        Mockito.verify(context).containsBean("httpTracing");
        Mockito.verify(context).getBean("httpTracing");
        Mockito.verifyNoMoreInteractions(context);
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void WebMvc25_httpTracing_whenWrongType() {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(context.containsBean("httpTracing")).thenReturn(true);
        when(context.getBean("httpTracing")).thenReturn("foo");
        new WebMvc25().httpTracing(context);
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void WebMvc25_httpTracing_whenDoesntExist() {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(context.containsBean("httpTracing")).thenReturn(false);
        new WebMvc25().httpTracing(context);
    }
}

