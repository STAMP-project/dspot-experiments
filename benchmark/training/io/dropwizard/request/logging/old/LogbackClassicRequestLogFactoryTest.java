package io.dropwizard.request.logging.old;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.logging.ConsoleAppenderFactory;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.logging.SyslogAppenderFactory;
import io.dropwizard.request.logging.RequestLogFactory;
import java.util.Collections;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class LogbackClassicRequestLogFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private RequestLogFactory<?> requestLog;

    @Test
    public void testDeserialized() {
        LogbackClassicRequestLogFactory classicRequestLogFactory = ((LogbackClassicRequestLogFactory) (requestLog));
        assertThat(classicRequestLogFactory.getTimeZone()).isEqualTo(TimeZone.getTimeZone("Europe/Amsterdam"));
        assertThat(classicRequestLogFactory.getAppenders()).hasSize(3).extractingResultOf("getClass").contains(ConsoleAppenderFactory.class, FileAppenderFactory.class, SyslogAppenderFactory.class);
    }

    @Test
    public void testLogFormat() throws Exception {
        final LogbackClassicRequestLogFactory factory = new LogbackClassicRequestLogFactory();
        @SuppressWarnings("unchecked")
        final Appender<ILoggingEvent> appender = Mockito.mock(Appender.class);
        final Request request = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class, Mockito.RETURNS_DEEP_STUBS);
        final HttpChannelState channelState = Mockito.mock(HttpChannelState.class);
        factory.setAppenders(Collections.singletonList(( context, applicationName, layoutFactory, levelFilterFactory, asyncAppenderFactory) -> appender));
        final String tz = TimeZone.getAvailableIDs(((int) (TimeUnit.HOURS.toMillis((-5)))))[0];
        factory.setTimeZone(TimeZone.getTimeZone(tz));
        CustomRequestLog logger = null;
        try {
            Mockito.when(channelState.isInitial()).thenReturn(true);
            Mockito.when(request.getRemoteHost()).thenReturn("10.0.0.1");
            // Jetty log format compares against System.currentTimeMillis, so there
            // isn't a way for us to set our own clock
            Mockito.when(request.getTimeStamp()).thenReturn(System.currentTimeMillis());
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(request.getRequestURI()).thenReturn("/test/things");
            Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
            Mockito.when(request.getHttpChannelState()).thenReturn(channelState);
            Mockito.when(response.getCommittedMetaData().getStatus()).thenReturn(200);
            Mockito.when(response.getHttpChannel().getBytesWritten()).thenReturn(8290L);
            final ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
            logger = ((CustomRequestLog) (factory.build("my-app")));
            logger.log(request, response);
            Mockito.verify(appender, Mockito.timeout(1000)).doAppend(captor.capture());
            final ILoggingEvent event = captor.getValue();
            assertThat(event.getFormattedMessage()).startsWith("10.0.0.1").doesNotContain("%").contains("\"GET /test/things HTTP/1.1\"").contains("-0500");
        } catch (Exception e) {
            if (logger != null) {
                logger.stop();
            }
            throw e;
        }
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(LogbackClassicRequestLogFactory.class);
    }
}

