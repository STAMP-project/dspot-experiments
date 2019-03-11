package io.dropwizard.logging;


import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.validation.BaseValidator;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;


public class AppenderFactoryCustomTimeZone {
    static {
        BootstrapLogging.bootstrap();
    }

    @SuppressWarnings("rawtypes")
    private final YamlConfigurationFactory<ConsoleAppenderFactory> factory = new YamlConfigurationFactory(ConsoleAppenderFactory.class, BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw");

    @Test
    public void testLoadAppenderWithTimeZoneInFullFormat() throws Exception {
        final ConsoleAppenderFactory<?> appender = factory.build(AppenderFactoryCustomTimeZone.loadResource("yaml/appender_with_time_zone_in_full_format.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("America/Los_Angeles");
    }

    @Test
    public void testLoadAppenderWithTimeZoneInCustomFormat() throws Exception {
        final ConsoleAppenderFactory<?> appender = factory.build(AppenderFactoryCustomTimeZone.loadResource("yaml/appender_with_custom_time_zone_format.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("GMT-02:00");
    }

    @Test
    public void testLoadAppenderWithNoTimeZone() throws Exception {
        final ConsoleAppenderFactory<?> appender = factory.build(AppenderFactoryCustomTimeZone.loadResource("yaml/appender_with_no_time_zone.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("UTC");
    }

    @Test
    public void testLoadAppenderWithUtcTimeZone() throws Exception {
        final ConsoleAppenderFactory<?> appender = factory.build(AppenderFactoryCustomTimeZone.loadResource("yaml/appender_with_utc_time_zone.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("UTC");
    }

    @Test
    public void testLoadAppenderWithWrongTimeZone() throws Exception {
        final ConsoleAppenderFactory<?> appender = factory.build(AppenderFactoryCustomTimeZone.loadResource("yaml/appender_with_wrong_time_zone.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("GMT");
    }

    @Test
    public void testLoadAppenderWithSystemTimeZone() throws Exception {
        final ConsoleAppenderFactory<?> appender = factory.build(AppenderFactoryCustomTimeZone.loadResource("yaml/appender_with_system_time_zone.yml"));
        assertThat(appender.getTimeZone()).isEqualTo(TimeZone.getDefault());
    }
}

