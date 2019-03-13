package io.dropwizard.request.logging.old;


import Level.INFO;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import io.dropwizard.logging.BootstrapLogging;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class DropwizardSlf4jRequestLogWriterTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @SuppressWarnings("unchecked")
    private final Appender<ILoggingEvent> appender = Mockito.mock(Appender.class);

    private final AppenderAttachableImpl<ILoggingEvent> appenders = new AppenderAttachableImpl();

    private final DropwizardSlf4jRequestLogWriter slf4jRequestLog = new DropwizardSlf4jRequestLogWriter(appenders);

    private final Request request = Mockito.mock(Request.class);

    private final Response response = Mockito.mock(Response.class, Mockito.RETURNS_DEEP_STUBS);

    private final HttpChannelState channelState = Mockito.mock(HttpChannelState.class);

    @Test
    public void logsRequestsToTheAppenders() throws Exception {
        final String requestLine = "1, 2 buckle my shoe";
        slf4jRequestLog.write(requestLine);
        final ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
        Mockito.verify(appender, Mockito.timeout(1000)).doAppend(captor.capture());
        final ILoggingEvent event = captor.getValue();
        assertThat(event.getFormattedMessage()).isEqualTo(requestLine);
        assertThat(event.getLevel()).isEqualTo(INFO);
        assertThat(event).hasToString("[INFO] 1, 2 buckle my shoe");
    }
}

