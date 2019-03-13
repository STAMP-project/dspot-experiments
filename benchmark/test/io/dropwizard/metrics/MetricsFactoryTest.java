package io.dropwizard.metrics;


import MetricAttribute.P50;
import MetricAttribute.P95;
import MetricAttribute.P98;
import MetricAttribute.P99;
import com.codahale.metrics.MetricAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.BootstrapLogging;
import io.dropwizard.util.Duration;
import io.dropwizard.util.Resources;
import io.dropwizard.validation.BaseValidator;
import java.io.File;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;


public class MetricsFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    private final YamlConfigurationFactory<MetricsFactory> factory = new YamlConfigurationFactory(MetricsFactory.class, BaseValidator.newValidator(), objectMapper, "dw");

    private MetricsFactory config;

    @Test
    public void hasADefaultFrequency() throws Exception {
        assertThat(config.getFrequency()).isEqualTo(Duration.seconds(10));
    }

    @Test
    public void hasReporters() throws Exception {
        CsvReporterFactory csvReporter = new CsvReporterFactory();
        csvReporter.setFile(new File("metrics"));
        assertThat(config.getReporters()).hasSize(3);
    }

    @Test
    public void canReadExcludedAndIncludedAttributes() {
        assertThat(config.getReporters()).hasSize(3);
        final ReporterFactory reporterFactory = config.getReporters().get(0);
        assertThat(reporterFactory).isInstanceOf(ConsoleReporterFactory.class);
        final ConsoleReporterFactory consoleReporterFactory = ((ConsoleReporterFactory) (reporterFactory));
        assertThat(consoleReporterFactory.getIncludesAttributes()).isEqualTo(EnumSet.of(P50, P95, P98, P99));
        assertThat(consoleReporterFactory.getExcludesAttributes()).isEqualTo(EnumSet.of(P98));
    }

    @Test
    public void canReadDefaultExcludedAndIncludedAttributes() {
        assertThat(config.getReporters()).hasSize(3);
        final ReporterFactory reporterFactory = config.getReporters().get(1);
        assertThat(reporterFactory).isInstanceOf(CsvReporterFactory.class);
        final CsvReporterFactory csvReporterFactory = ((CsvReporterFactory) (reporterFactory));
        assertThat(csvReporterFactory.getIncludesAttributes()).isEqualTo(EnumSet.allOf(MetricAttribute.class));
        assertThat(csvReporterFactory.getExcludesAttributes()).isEmpty();
    }

    @Test
    public void reportOnStopFalseByDefault() {
        assertThat(config.reportOnStop).isFalse();
    }

    @Test
    public void reportOnStopCanBeTrue() throws Exception {
        config = factory.build(new File(Resources.getResource("yaml/metrics-report-on-stop.yml").toURI()));
        assertThat(config.reportOnStop).isTrue();
    }
}

