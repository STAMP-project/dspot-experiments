package com.baeldung.exceptions;


import Level.INFO;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionHandlerUnitTest {
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Test
    public void whenArithmeticException_thenUseUncaughtExceptionHandler() throws InterruptedException {
        Thread globalExceptionHandlerThread = new Thread() {
            public void run() {
                GlobalExceptionHandler globalExceptionHandlerObj = new GlobalExceptionHandler();
                globalExceptionHandlerObj.performArithmeticOperation(99, 0);
            }
        };
        globalExceptionHandlerThread.start();
        globalExceptionHandlerThread.join();
        Mockito.verify(mockAppender).doAppend(captorLoggingEvent.capture());
        LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        assertThat(loggingEvent.getLevel()).isEqualTo(INFO);
        assertThat(loggingEvent.getFormattedMessage()).isEqualTo("Unhandled exception caught!");
    }
}

