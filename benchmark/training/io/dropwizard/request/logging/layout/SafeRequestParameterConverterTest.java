package io.dropwizard.request.logging.layout;


import ch.qos.logback.access.spi.AccessEvent;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class SafeRequestParameterConverterTest {
    private final SafeRequestParameterConverter safeRequestParameterConverter = new SafeRequestParameterConverter();

    private final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

    private AccessEvent accessEvent;

    @Test
    public void testConvertOneParameter() throws Exception {
        Mockito.when(httpServletRequest.getParameterValues("name")).thenReturn(new String[]{ "Alice" });
        Mockito.when(httpServletRequest.getParameterNames()).thenReturn(Collections.enumeration(Collections.singleton("name")));
        // Invoked by AccessEvent#prepareForDeferredProcessing
        accessEvent.buildRequestParameterMap();
        // Jetty recycled the request
        Mockito.reset(httpServletRequest);
        String value = safeRequestParameterConverter.convert(accessEvent);
        assertThat(value).isEqualTo("Alice");
    }

    @Test
    public void testConvertSeveralParameters() throws Exception {
        Mockito.when(httpServletRequest.getParameterValues("name")).thenReturn(new String[]{ "Alice", "Bob" });
        Mockito.when(httpServletRequest.getParameterNames()).thenReturn(Collections.enumeration(Collections.singleton("name")));
        // Invoked by AccessEvent#prepareForDeferredProcessing
        accessEvent.buildRequestParameterMap();
        // Jetty recycled the request
        Mockito.reset(httpServletRequest);
        final String value = safeRequestParameterConverter.convert(accessEvent);
        assertThat(value).isEqualTo("[Alice, Bob]");
    }
}

