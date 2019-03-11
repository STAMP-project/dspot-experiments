package org.stagemonitor.core.metrics.metrics2;


import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.metrics.MetricsReporterTestHelper;
import org.stagemonitor.util.StringUtils;


public class ElasticsearchReporterTest {
    private static final TimeUnit DURATION_UNIT = TimeUnit.MICROSECONDS;

    private static final double DURATION_FACTOR = 1.0 / (ElasticsearchReporterTest.DURATION_UNIT.toNanos(1));

    private ElasticsearchReporter elasticsearchReporter;

    private long timestamp;

    private ByteArrayOutputStream out;

    private Logger metricsLogger;

    private CorePlugin corePlugin;

    private Metric2Registry registry;

    private Clock clock;

    @Test(expected = IllegalStateException.class)
    public void testScheduleTwice() throws Exception {
        elasticsearchReporter.start(100, TimeUnit.MILLISECONDS);
        elasticsearchReporter.start(100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSchedule() throws Exception {
        Mockito.when(corePlugin.isOnlyLogElasticsearchMetricReports()).thenReturn(true);
        // this clock starts at 0 and then progresses normally
        Mockito.when(clock.getTime()).then(new Answer<Long>() {
            long firstTimestamp;

            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                final long time = System.currentTimeMillis();
                if ((firstTimestamp) == 0) {
                    firstTimestamp = time;
                }
                return time - (firstTimestamp);
            }
        });
        registry.register(MetricName.name("test").build(), new com.codahale.metrics.Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 1;
            }
        });
        elasticsearchReporter.start(100, TimeUnit.MILLISECONDS);
        Thread.sleep(300);
        Mockito.verify(metricsLogger).info(ArgumentMatchers.eq(String.format(("{\"index\":{\"_index\":\"stagemonitor-metrics-%s\",\"_type\":\"metrics\"}}\n" + "{\"@timestamp\":100,\"name\":\"test\",\"app\":\"test\",\"value\":1.0}\n"), StringUtils.getLogstashStyleDate())));
        Mockito.verify(metricsLogger).info(ArgumentMatchers.eq(String.format(("{\"index\":{\"_index\":\"stagemonitor-metrics-%s\",\"_type\":\"metrics\"}}\n" + "{\"@timestamp\":200,\"name\":\"test\",\"app\":\"test\",\"value\":1.0}\n"), StringUtils.getLogstashStyleDate())));
    }

    @Test
    public void testReportGauges() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("cpu_usage").type("user").tag("core", "1").build(), MetricsReporterTestHelper.gauge(3)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        final String jsons = new String(out.toByteArray());
        Assert.assertEquals(jsons, 2, jsons.split("\n").length);
        Assert.assertEquals(MetricsReporterTestHelper.objectMap("index", MetricsReporterTestHelper.map("_type", "metrics").add("_index", ("stagemonitor-metrics-" + (StringUtils.getLogstashStyleDate())))), asMap(jsons.split("\n")[0]));
        Assert.assertEquals(MetricsReporterTestHelper.objectMap("@timestamp", timestamp).add("name", "cpu_usage").add("app", "test").add("type", "user").add("core", "1").add("value", 3.0), asMap(jsons.split("\n")[1]));
    }

    @Test
    public void testReportNullGauge() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("gauge").build(), MetricsReporterTestHelper.gauge(null)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Assert.assertEquals(MetricsReporterTestHelper.objectMap("@timestamp", timestamp).add("name", "gauge").add("app", "test"), asMap(out));
    }

    @Test
    public void testReportToLog() throws Exception {
        Mockito.when(corePlugin.isOnlyLogElasticsearchMetricReports()).thenReturn(true);
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("gauge").build(), MetricsReporterTestHelper.gauge(1)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Mockito.verify(metricsLogger).info(ArgumentMatchers.eq(String.format(("{\"index\":{\"_index\":\"stagemonitor-metrics-%s\",\"_type\":\"metrics\"}}\n" + "{\"@timestamp\":%d,\"name\":\"gauge\",\"app\":\"test\",\"value\":1.0}\n"), StringUtils.getLogstashStyleDate(), timestamp)));
    }

    @Test
    public void testReportBooleanGauge() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("gauge").build(), MetricsReporterTestHelper.gauge(true)), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Assert.assertEquals(MetricsReporterTestHelper.objectMap("@timestamp", timestamp).add("name", "gauge").add("app", "test").add("value_boolean", true), asMap(out));
    }

    @Test
    public void testReportStringGauge() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(MetricName.name("gauge").build(), MetricsReporterTestHelper.gauge("foo")), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Assert.assertEquals(MetricsReporterTestHelper.objectMap("@timestamp", timestamp).add("name", "gauge").add("app", "test").add("value_string", "foo"), asMap(out));
    }

    @Test
    public void testReportCounters() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(com.codahale.metrics.Gauge.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("web_sessions").build(), MetricsReporterTestHelper.counter(123)), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Assert.assertEquals(MetricsReporterTestHelper.map("@timestamp", timestamp, Object.class).add("name", "web_sessions").add("app", "test").add("count", 123), asMap(out));
    }

    @Test
    public void testReportHistograms() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(com.codahale.metrics.Gauge.class), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("histogram").build(), MetricsReporterTestHelper.histogram(400)), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Assert.assertEquals(MetricsReporterTestHelper.objectMap("@timestamp", timestamp).add("name", "histogram").add("app", "test").add("count", 1).add("max", 200.0).add("mean", 400.0).add("p50", 600.0).add("min", 400.0).add("p25", 0.0).add("p75", 700.0).add("p95", 800.0).add("p98", 900.0).add("p99", 1000.0).add("p999", 1100.0).add("std", 500.0), asMap(out));
    }

    @Test
    public void testReportMeters() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(com.codahale.metrics.Gauge.class), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("meter").build(), MetricsReporterTestHelper.meter(10)), MetricsReporterTestHelper.metricNameMap(Timer.class));
        Assert.assertEquals(MetricsReporterTestHelper.map("@timestamp", timestamp, Object.class).add("name", "meter").add("app", "test").add("count", 10).add("m15_rate", 5.0).add("m1_rate", 3.0).add("m5_rate", 4.0).add("mean_rate", 2.0), asMap(out));
    }

    @Test
    public void testReportTimers() throws Exception {
        elasticsearchReporter.reportMetrics(MetricsReporterTestHelper.metricNameMap(com.codahale.metrics.Gauge.class), MetricsReporterTestHelper.metricNameMap(Counter.class), MetricsReporterTestHelper.metricNameMap(Histogram.class), MetricsReporterTestHelper.metricNameMap(Meter.class), MetricsReporterTestHelper.metricNameMap(MetricName.name("response_time").build(), MetricsReporterTestHelper.timer(400)));
        Assert.assertEquals(MetricsReporterTestHelper.map("@timestamp", timestamp, Object.class).add("name", "response_time").add("app", "test").add("count", 1).add("m15_rate", 5.0).add("m1_rate", 3.0).add("m5_rate", 4.0).add("mean_rate", 2.0).add("max", (200.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("mean", (400.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p50", (600.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("min", (400.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p25", (0.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p75", (700.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p95", (800.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p98", (900.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p99", (1000.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("p999", (1100.0 * (ElasticsearchReporterTest.DURATION_FACTOR))).add("std", (500.0 * (ElasticsearchReporterTest.DURATION_FACTOR))), asMap(out));
    }
}

