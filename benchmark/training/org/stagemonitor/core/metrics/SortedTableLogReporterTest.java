package org.stagemonitor.core.metrics;


import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class SortedTableLogReporterTest extends MetricsReporterTestHelper {
    private static final TimeUnit DURATION_UNIT = TimeUnit.MICROSECONDS;

    private static final double DURATION_FACTOR = 1.0 / (SortedTableLogReporterTest.DURATION_UNIT.toNanos(1));

    private Logger logger;

    private SortedTableLogReporter reporter;

    @Test
    public void reportsGaugeValues() throws Exception {
        reporter.reportMetrics(testGauges(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map());
        Mockito.verify(logger).info(("Metrics ========================================================================\n" + ((((((("\n" + "-- Gauges ----------------------------------------------------------------------\n") + "name               | value\n") + "            gauge3 | 3\n") + "            gauge2 | 2\n") + "gaugeWithLongName1 | 1\n") + "\n") + "\n")));
    }

    @Test
    public void reportsCounterValues() throws Exception {
        reporter.reportMetrics(MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(MetricName.name("test.counter").build(), MetricsReporterTestHelper.counter(100L)).add(MetricName.name("test.counter2").build(), MetricsReporterTestHelper.counter(200L)), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map());
        Mockito.verify(logger).info(("Metrics ========================================================================\n" + (((((("\n" + "-- Counters --------------------------------------------------------------------\n") + "name          | count\n") + "test.counter2 | 200\n") + " test.counter | 100\n") + "\n") + "\n")));
    }

    @Test
    public void reportsHistogramValues() throws Exception {
        reporter.reportMetrics(MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), testHistograms(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map());
        Mockito.verify(logger).info(("Metrics ========================================================================\n" + (((((("\n" + "-- Histograms ------------------------------------------------------------------\n") + "name            | count     | mean      | min       | max       | stddev    | p50       | p75       | p95       | p98       | p99       | p999      |\n") + "test.histogram2 |         1 |    400.00 |    400.00 |    200.00 |    500.00 |    600.00 |    700.00 |    800.00 |    900.00 |  1,000.00 |  1,100.00 | \n") + " test.histogram |         1 |    300.00 |    400.00 |    200.00 |    500.00 |    600.00 |    700.00 |    800.00 |    900.00 |  1,000.00 |  1,100.00 | \n") + "\n") + "\n")));
    }

    @Test
    public void reportsMeterValues() throws Exception {
        reporter.reportMetrics(MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(MetricName.name("test.meter1").tag("foo", "bar").build(), MetricsReporterTestHelper.meter(1L)).add(MetricName.name("test.meter2").build(), MetricsReporterTestHelper.meter(2)), MetricsReporterTestHelper.map());
        Mockito.verify(logger).info(("Metrics ========================================================================\n" + (((((("\n" + "-- Meters ----------------------------------------------------------------------\n") + "name                | count     | mean_rate | m1_rate   | m5_rate   | m15_rate  | rate_unit     | duration_unit\n") + "        test.meter2 |         2 |      2.00 |      3.00 |      4.00 |      5.00 | second        | microseconds\n") + "test.meter1,foo=bar |         1 |      2.00 |      3.00 |      4.00 |      5.00 | second        | microseconds\n") + "\n") + "\n")));
    }

    @Test
    public void reportsTimerValues() throws Exception {
        reporter.reportMetrics(MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(), MetricsReporterTestHelper.map(MetricName.name("timer1").build(), MetricsReporterTestHelper.timer(400)).add(MetricName.name("timer2").build(), MetricsReporterTestHelper.timer(200)).add(MetricName.name("timer3").build(), MetricsReporterTestHelper.timer(300)));
        Mockito.verify(logger).info(("Metrics ========================================================================\n" + ((((((("\n" + "-- Timers ----------------------------------------------------------------------\n") + "name   | count     | mean      | min       | max       | stddev    | p50       | p75       | p95       | p98       | p99       | p999      | mean_rate | m1_rate   | m5_rate   | m15_rate  | rate_unit     | duration_unit\n") + "timer1 |         1 |      0.40 |      0.40 |      0.20 |      0.50 |      0.60 |      0.70 |      0.80 |      0.90 |      1.00 |      1.10 |      2.00 |      3.00 |      4.00 |      5.00 | second        | microseconds\n") + "timer3 |         1 |      0.30 |      0.40 |      0.20 |      0.50 |      0.60 |      0.70 |      0.80 |      0.90 |      1.00 |      1.10 |      2.00 |      3.00 |      4.00 |      5.00 | second        | microseconds\n") + "timer2 |         1 |      0.20 |      0.40 |      0.20 |      0.50 |      0.60 |      0.70 |      0.80 |      0.90 |      1.00 |      1.10 |      2.00 |      3.00 |      4.00 |      5.00 | second        | microseconds\n") + "\n") + "\n")));
    }
}

