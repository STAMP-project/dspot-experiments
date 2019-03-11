package org.mockserver.logging;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.log.model.MessageLogEntry;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.event.Level;


public class MockServerLoggerTest {
    private static boolean disableSystemOut;

    @Test
    public void shouldFormatInfoLogMessagesForRequest() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("INFO");
            Logger mockLogger = Mockito.mock(Logger.class);
            HttpStateHandler mockHttpStateHandler = Mockito.mock(HttpStateHandler.class);
            MockServerLogger logFormatter = new MockServerLogger(mockLogger, mockHttpStateHandler);
            Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
            HttpRequest request = HttpRequest.request("some_path");
            // when
            logFormatter.info(TRACE, request, "some random message with {} and {}", (((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"), (((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"));
            // then
            String message = ((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE);
            Mockito.verify(mockLogger).info(message);
            ArgumentCaptor<MessageLogEntry> captor = ArgumentCaptor.forClass(MessageLogEntry.class);
            Mockito.verify(mockHttpStateHandler, Mockito.times(1)).log(captor.capture());
            MessageLogEntry messageLogEntry = captor.getValue();
            MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(Collections.singletonList(request)));
            MatcherAssert.assertThat(messageLogEntry.getMessage(), StringContains.containsString((((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE))));
            MatcherAssert.assertThat(messageLogEntry.getMessageFormat(), StringContains.containsString("some random message with {} and {}"));
            MatcherAssert.assertThat(messageLogEntry.getArguments(), arrayContaining(new Object[]{ ((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object", ((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object" }));
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }

    @Test
    public void shouldFormatInfoLogMessagesForRequestList() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("INFO");
            Logger mockLogger = Mockito.mock(Logger.class);
            HttpStateHandler mockHttpStateHandler = Mockito.mock(HttpStateHandler.class);
            MockServerLogger logFormatter = new MockServerLogger(mockLogger, mockHttpStateHandler);
            Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
            HttpRequest request = HttpRequest.request("some_path");
            // when
            logFormatter.info(TRACE, Arrays.asList(request, request), "some random message with {} and {}", (((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"), (((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"));
            // then
            String message = ((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE);
            Mockito.verify(mockLogger).info(message);
            ArgumentCaptor<MessageLogEntry> captor = ArgumentCaptor.forClass(MessageLogEntry.class);
            Mockito.verify(mockHttpStateHandler, Mockito.times(1)).log(captor.capture());
            for (MessageLogEntry messageLogEntry : captor.getAllValues()) {
                MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(Arrays.asList(request, request)));
                MatcherAssert.assertThat(messageLogEntry.getMessage(), StringContains.containsString((((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE))));
                MatcherAssert.assertThat(messageLogEntry.getMessageFormat(), StringContains.containsString("some random message with {} and {}"));
                MatcherAssert.assertThat(messageLogEntry.getArguments(), arrayContaining(new Object[]{ ((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object", ((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object" }));
            }
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }

    @Test
    public void shouldFormatErrorLogMessagesForRequest() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("INFO");
            Logger mockLogger = Mockito.mock(Logger.class);
            HttpStateHandler mockHttpStateHandler = Mockito.mock(HttpStateHandler.class);
            MockServerLogger logFormatter = new MockServerLogger(mockLogger, mockHttpStateHandler);
            Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
            HttpRequest request = HttpRequest.request("some_path");
            // when
            logFormatter.error(request, "some random message with {} and {}", (((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"), (((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"));
            // then
            String message = ((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE);
            Mockito.verify(mockLogger).error(message, ((Throwable) (null)));
            ArgumentCaptor<MessageLogEntry> captor = ArgumentCaptor.forClass(MessageLogEntry.class);
            Mockito.verify(mockHttpStateHandler, Mockito.times(1)).log(captor.capture());
            MessageLogEntry messageLogEntry = captor.getValue();
            MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(Collections.singletonList(request)));
            MatcherAssert.assertThat(messageLogEntry.getMessage(), StringContains.containsString((((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE))));
            MatcherAssert.assertThat(messageLogEntry.getMessageFormat(), StringContains.containsString("some random message with {} and {}"));
            MatcherAssert.assertThat(messageLogEntry.getArguments(), arrayContaining(new Object[]{ ((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object", ((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object" }));
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }

    @Test
    public void shouldFormatExceptionErrorLogMessagesForRequest() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("INFO");
            Logger mockLogger = Mockito.mock(Logger.class);
            HttpStateHandler mockHttpStateHandler = Mockito.mock(HttpStateHandler.class);
            MockServerLogger logFormatter = new MockServerLogger(mockLogger, mockHttpStateHandler);
            Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
            HttpRequest request = HttpRequest.request("some_path");
            RuntimeException exception = new RuntimeException("TEST EXCEPTION");
            // when
            logFormatter.error(request, exception, "some random message with {} and {}", (((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"), (((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"));
            // then
            String message = ((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE);
            Mockito.verify(mockLogger).error(message, exception);
            ArgumentCaptor<MessageLogEntry> captor = ArgumentCaptor.forClass(MessageLogEntry.class);
            Mockito.verify(mockHttpStateHandler, Mockito.times(1)).log(captor.capture());
            MessageLogEntry messageLogEntry = captor.getValue();
            MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(Collections.singletonList(request)));
            MatcherAssert.assertThat(messageLogEntry.getMessage(), CoreMatchers.is((((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE))));
            MatcherAssert.assertThat(messageLogEntry.getMessageFormat(), StringContains.containsString("some random message with {} and {}"));
            MatcherAssert.assertThat(messageLogEntry.getArguments(), arrayContaining(new Object[]{ ((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object", ((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object" }));
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }

    @Test
    public void shouldFormatExceptionErrorLogMessagesForRequestList() {
        Level originalLevel = ConfigurationProperties.logLevel();
        try {
            // given
            ConfigurationProperties.logLevel("INFO");
            Logger mockLogger = Mockito.mock(Logger.class);
            HttpStateHandler mockHttpStateHandler = Mockito.mock(HttpStateHandler.class);
            MockServerLogger logFormatter = new MockServerLogger(mockLogger, mockHttpStateHandler);
            Mockito.when(mockLogger.isErrorEnabled()).thenReturn(true);
            HttpRequest request = HttpRequest.request("some_path");
            RuntimeException exception = new RuntimeException("TEST EXCEPTION");
            // when
            logFormatter.error(Arrays.asList(request, request), exception, "some random message with {} and {}", (((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"), (((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object"));
            // then
            String message = ((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE);
            Mockito.verify(mockLogger).error(message, exception);
            ArgumentCaptor<MessageLogEntry> captor = ArgumentCaptor.forClass(MessageLogEntry.class);
            Mockito.verify(mockHttpStateHandler, Mockito.times(1)).log(captor.capture());
            for (MessageLogEntry messageLogEntry : captor.getAllValues()) {
                MatcherAssert.assertThat(messageLogEntry.getHttpRequests(), CoreMatchers.is(Arrays.asList(request, request)));
                MatcherAssert.assertThat(messageLogEntry.getMessage(), StringContains.containsString((((((((((((((((((("some random message with " + (NEW_LINE)) + (NEW_LINE)) + "\tsome") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE)) + (NEW_LINE)) + " and ") + (NEW_LINE)) + (NEW_LINE)) + "\tanother") + (NEW_LINE)) + "\tmulti-line") + (NEW_LINE)) + "\tobject") + (NEW_LINE))));
                MatcherAssert.assertThat(messageLogEntry.getMessageFormat(), StringContains.containsString("some random message with {} and {}"));
                MatcherAssert.assertThat(messageLogEntry.getArguments(), arrayContaining(new Object[]{ ((("some" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object", ((("another" + (NEW_LINE)) + "multi-line") + (NEW_LINE)) + "object" }));
            }
        } finally {
            ConfigurationProperties.logLevel(originalLevel.toString());
        }
    }
}

