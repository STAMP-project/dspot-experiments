package io.dropwizard.request.logging;


import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.core.Appender;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LogbackAccessRequestLogTest {
    @SuppressWarnings("unchecked")
    private final Appender<IAccessEvent> appender = Mockito.mock(Appender.class);

    private final LogbackAccessRequestLog requestLog = new LogbackAccessRequestLog();

    private final Request request = Mockito.mock(Request.class);

    private final Response response = Mockito.mock(Response.class);

    private final HttpChannelState channelState = Mockito.mock(HttpChannelState.class);

    @Test
    public void logsRequestsToTheAppender() {
        final IAccessEvent event = logAndCapture();
        assertThat(event.getRemoteAddr()).isEqualTo("10.0.0.1");
        assertThat(event.getMethod()).isEqualTo("GET");
        assertThat(event.getRequestURI()).isEqualTo("/test/things?yay");
        assertThat(event.getProtocol()).isEqualTo("HTTP/1.1");
        assertThat(event.getStatusCode()).isEqualTo(200);
        assertThat(event.getContentLength()).isEqualTo(8290L);
    }
}

