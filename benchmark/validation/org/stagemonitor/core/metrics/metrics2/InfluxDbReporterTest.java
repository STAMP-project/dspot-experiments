package org.stagemonitor.core.metrics.metrics2;


import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.stagemonitor.core.metrics.MetricsReporterTestHelper;
import org.stagemonitor.core.util.HttpClient;


public class InfluxDbReporterTest {
    private static final TimeUnit DURATION_UNIT = TimeUnit.MICROSECONDS;

    private InfluxDbReporter influxDbReporter;

    private HttpClient httpClient;

    private long timestamp;

    @Test
    public void testReportGauges() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("cpu_usage").type("user").tag("core", "1").build(), MetricsReporterTestHelper.gauge(3)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("cpu_usage,core=1,type=user,app=test value=3 %d", timestamp))));
    }

    @Test
    public void testReportNullGauge() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("cpu_usage").type("user").tag("core", "1").build(), MetricsReporterTestHelper.gauge(null)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.emptyList()));
    }

    @Test
    public void testReportBooleanGauge() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("gauge").build(), MetricsReporterTestHelper.gauge(true)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("gauge,app=test value_boolean=true %d", timestamp))));
    }

    @Test
    public void testReportStringGauge() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("gauge").build(), MetricsReporterTestHelper.gauge("foo")), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("gauge,app=test value_string=\"foo\" %d", timestamp))));
    }

    @Test
    public void testReportGaugesExponent() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("cpu_usage").type("user").tag("core", "1").build(), MetricsReporterTestHelper.gauge(1.0E-8)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("cpu_usage,core=1,type=user,app=test value=1.0e-8 %d", timestamp))));
    }

    @Test
    public void testReportCounters() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(Gauge.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("web_sessions").build(), MetricsReporterTestHelper.counter(123)), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("web_sessions,app=test count=123i %d", timestamp))));
    }

    @Test
    public void testReportHistograms() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(Gauge.class), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("histogram").build(), MetricsReporterTestHelper.histogram(400)), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("histogram,app=test count=1i,min=400,max=200,mean=400.0,p50=600.0,std=500.0,p25=0.0,p75=700.0,p95=800.0,p98=900.0,p99=1000.0,p999=1100.0 %d", timestamp))));
    }

    @Test
    public void testReportMeters() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(Gauge.class), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("meter").build(), MetricsReporterTestHelper.meter(10)), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("meter,app=test count=10i,m1_rate=3.0,m5_rate=4.0,m15_rate=5.0,mean_rate=2.0 %d", timestamp))));
    }

    @Test
    public void testReportTimers() throws Exception {
        influxDbReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(Gauge.class), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("response_time").build(), MetricsReporterTestHelper.timer(400)));
        Mockito.verify(httpClient).send(ArgumentMatchers.eq("POST"), ArgumentMatchers.eq("http://localhost:8086/write?precision=ms&db=stm"), ArgumentMatchers.eq(Collections.singletonList(String.format("response_time,app=test count=1i,m1_rate=3.0,m5_rate=4.0,m15_rate=5.0,mean_rate=2.0,min=0.4,max=0.2,mean=0.4,p50=0.6,std=0.5,p25=0.0,p75=0.7,p95=0.8,p98=0.9,p99=1.0,p999=1.1 %d", timestamp))));
    }

    @Test
    public void testGetInfluxDbStringOrderedTags() throws Exception {
        Assert.assertEquals("cpu_usage,core=1,level=user", InfluxDbReporter.getInfluxDbLineProtocolString(MetricName.name("cpu_usage").tag("level", "user").tag("core", "1").build()));
    }

    @Test
    public void testGetInfluxDbStringWhiteSpace() throws Exception {
        Assert.assertEquals("cpu\\ usage,level=soft\\ irq", InfluxDbReporter.getInfluxDbLineProtocolString(MetricName.name("cpu usage").tag("level", "soft irq").build()));
    }

    @Test
    public void testGetInfluxDbStringNoTags() throws Exception {
        Assert.assertEquals("cpu_usage", InfluxDbReporter.getInfluxDbLineProtocolString(MetricName.name("cpu_usage").build()));
    }

    @Test
    public void testGetInfluxDbStringAllEscapingAndQuotingBehavior() throws Exception {
        Assert.assertEquals("\"measurement\\ with\\ quotes\",tag\\ key\\ with\\ spaces=tag\\,value\\,with\"commas\"", InfluxDbReporter.getInfluxDbLineProtocolString(MetricName.name("\"measurement with quotes\"").tag("tag key with spaces", "tag,value,with\"commas\"").build()));
    }
}

