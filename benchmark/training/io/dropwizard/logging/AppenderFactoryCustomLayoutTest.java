package io.dropwizard.logging;


import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.async.AsyncLoggingEventAppenderFactory;
import io.dropwizard.logging.layout.DropwizardLayoutFactory;
import io.dropwizard.validation.BaseValidator;
import org.junit.jupiter.api.Test;


@SuppressWarnings("unchecked")
public class AppenderFactoryCustomLayoutTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    @SuppressWarnings("rawtypes")
    private final YamlConfigurationFactory<ConsoleAppenderFactory> factory = new YamlConfigurationFactory(ConsoleAppenderFactory.class, BaseValidator.newValidator(), objectMapper, "dw-layout");

    @Test
    public void testLoadAppenderWithCustomLayout() throws Exception {
        final ConsoleAppenderFactory<ILoggingEvent> appender = factory.build(AppenderFactoryCustomLayoutTest.loadResource());
        assertThat(appender.getLayout()).isNotNull().isInstanceOf(TestLayoutFactory.class);
        TestLayoutFactory layoutFactory = ((TestLayoutFactory) (appender.getLayout()));
        assertThat(layoutFactory).isNotNull().extracting(TestLayoutFactory::isIncludeSeparator).isEqualTo(true);
    }

    @Test
    public void testBuildAppenderWithCustomLayout() throws Exception {
        AsyncAppender appender = ((AsyncAppender) (factory.build(AppenderFactoryCustomLayoutTest.loadResource()).build(new LoggerContext(), "test-custom-layout", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        ConsoleAppender<?> consoleAppender = ((ConsoleAppender<?>) (appender.getAppender("console-appender")));
        LayoutWrappingEncoder<?> encoder = ((LayoutWrappingEncoder<?>) (consoleAppender.getEncoder()));
        assertThat(encoder.getLayout()).isInstanceOf(TestLayoutFactory.TestLayout.class);
    }
}

