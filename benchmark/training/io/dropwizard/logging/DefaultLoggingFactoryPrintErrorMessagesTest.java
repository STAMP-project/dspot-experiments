package io.dropwizard.logging;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;


public class DefaultLoggingFactoryPrintErrorMessagesTest {
    private DefaultLoggingFactory factory;

    private ByteArrayOutputStream output;

    @Test
    public void testWhenUsingDefaultConstructor_SystemErrIsSet() throws Exception {
        PrintStream configurationErrorsStream = new DefaultLoggingFactory().getConfigurationErrorsStream();
        assertThat(configurationErrorsStream).isSameAs(System.err);
    }

    @Test
    public void testWhenUsingDefaultConstructor_StaticILoggerFactoryIsSet() throws Exception {
        LoggerContext loggerContext = new DefaultLoggingFactory().getLoggerContext();
        assertThat(loggerContext).isSameAs(LoggerFactory.getILoggerFactory());
    }

    @Test
    public void testLogbackStatusPrinterPrintStreamIsRestoredToSystemOut() throws Exception {
        Field field = StatusPrinter.class.getDeclaredField("ps");
        field.setAccessible(true);
        PrintStream out = ((PrintStream) (field.get(null)));
        assertThat(out).isSameAs(System.out);
    }
}

