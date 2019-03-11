package com.codahale.metrics;


import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.Marker;


public class Slf4jReporterTest {
    private final Logger logger = Mockito.mock(Logger.class);

    private final Marker marker = Mockito.mock(Marker.class);

    private final MetricRegistry registry = Mockito.mock(MetricRegistry.class);

    /**
     * The set of disabled metric attributes to pass to the Slf4jReporter builder
     * in the default factory methods of {@link #infoReporter}
     * and {@link #errorReporter}.
     *
     * This value can be overridden by tests before calling the {@link #infoReporter}
     * and {@link #errorReporter} factory methods.
     */
    private Set<MetricAttribute> disabledMetricAttributes = null;

    @Test
    public void reportsGaugeValuesAtErrorDefault() {
        reportsGaugeValuesAtError();
    }

    @Test
    public void reportsGaugeValuesAtErrorAllDisabled() {
        disabledMetricAttributes = EnumSet.allOf(MetricAttribute.class);// has no effect

        reportsGaugeValuesAtError();
    }

    @Test
    public void reportsCounterValuesAtErrorDefault() {
        reportsCounterValuesAtError();
    }

    @Test
    public void reportsCounterValuesAtErrorAllDisabled() {
        disabledMetricAttributes = EnumSet.allOf(MetricAttribute.class);// has no effect

        reportsCounterValuesAtError();
    }

    @Test
    public void reportsHistogramValuesAtErrorDefault() {
        reportsHistogramValuesAtError(("type=HISTOGRAM, name=test.histogram, count=1, min=4, " + "max=2, mean=3.0, stddev=5.0, p50=6.0, p75=7.0, p95=8.0, p98=9.0, p99=10.0, p999=11.0"));
    }

    @Test
    public void reportsHistogramValuesAtErrorWithDisabledMetricAttributes() {
        disabledMetricAttributes = EnumSet.of(MetricAttribute.COUNT, MetricAttribute.MIN, MetricAttribute.P50);
        reportsHistogramValuesAtError(("type=HISTOGRAM, name=test.histogram, max=2, mean=3.0, " + "stddev=5.0, p75=7.0, p95=8.0, p98=9.0, p99=10.0, p999=11.0"));
    }

    @Test
    public void reportsMeterValuesAtErrorDefault() {
        reportsMeterValuesAtError(("type=METER, name=test.meter, count=1, m1_rate=3.0, m5_rate=4.0, " + "m15_rate=5.0, mean_rate=2.0, rate_unit=events/second"));
    }

    @Test
    public void reportsMeterValuesAtErrorWithDisabledMetricAttributes() {
        disabledMetricAttributes = EnumSet.of(MetricAttribute.MIN, MetricAttribute.P50, MetricAttribute.M1_RATE);
        reportsMeterValuesAtError(("type=METER, name=test.meter, count=1, m5_rate=4.0, m15_rate=5.0, " + "mean_rate=2.0, rate_unit=events/second"));
    }

    @Test
    public void reportsTimerValuesAtErrorDefault() {
        reportsTimerValuesAtError(("type=TIMER, name=test.another.timer, count=1, min=300.0, max=100.0, " + (("mean=200.0, stddev=400.0, p50=500.0, p75=600.0, p95=700.0, p98=800.0, p99=900.0, p999=1000.0, " + "m1_rate=3.0, m5_rate=4.0, m15_rate=5.0, mean_rate=2.0, rate_unit=events/second, ") + "duration_unit=milliseconds")));
    }

    @Test
    public void reportsTimerValuesAtErrorWithDisabledMetricAttributes() {
        disabledMetricAttributes = EnumSet.of(MetricAttribute.MIN, MetricAttribute.STDDEV, MetricAttribute.P999, MetricAttribute.MEAN_RATE);
        reportsTimerValuesAtError(("type=TIMER, name=test.another.timer, count=1, max=100.0, mean=200.0, " + ("p50=500.0, p75=600.0, p95=700.0, p98=800.0, p99=900.0, m1_rate=3.0, m5_rate=4.0, m15_rate=5.0, " + "rate_unit=events/second, duration_unit=milliseconds")));
    }

    @Test
    public void reportsGaugeValuesDefault() {
        Mockito.when(logger.isInfoEnabled(marker)).thenReturn(true);
        infoReporter().report(map("gauge", () -> "value"), map(), map(), map(), map());
        Mockito.verify(logger).info(marker, "type=GAUGE, name=prefix.gauge, value=value");
    }

    @Test
    public void reportsCounterValuesDefault() {
        final Counter counter = counter();
        Mockito.when(logger.isInfoEnabled(marker)).thenReturn(true);
        infoReporter().report(map(), map("test.counter", counter), map(), map(), map());
        Mockito.verify(logger).info(marker, "type=COUNTER, name=prefix.test.counter, count=100");
    }

    @Test
    public void reportsHistogramValuesDefault() {
        final Histogram histogram = histogram();
        Mockito.when(logger.isInfoEnabled(marker)).thenReturn(true);
        infoReporter().report(map(), map(), map("test.histogram", histogram), map(), map());
        Mockito.verify(logger).info(marker, ("type=HISTOGRAM, name=prefix.test.histogram, count=1, min=4, max=2, mean=3.0, " + "stddev=5.0, p50=6.0, p75=7.0, p95=8.0, p98=9.0, p99=10.0, p999=11.0"));
    }

    @Test
    public void reportsMeterValuesDefault() {
        final Meter meter = meter();
        Mockito.when(logger.isInfoEnabled(marker)).thenReturn(true);
        infoReporter().report(map(), map(), map(), map("test.meter", meter), map());
        Mockito.verify(logger).info(marker, ("type=METER, name=prefix.test.meter, count=1, m1_rate=3.0, m5_rate=4.0, " + "m15_rate=5.0, mean_rate=2.0, rate_unit=events/second"));
    }

    @Test
    public void reportsTimerValuesDefault() {
        final Timer timer = timer();
        Mockito.when(logger.isInfoEnabled(marker)).thenReturn(true);
        infoReporter().report(map(), map(), map(), map(), map("test.another.timer", timer));
        Mockito.verify(logger).info(marker, ("type=TIMER, name=prefix.test.another.timer, count=1, min=300.0, max=100.0, " + ("mean=200.0, stddev=400.0, p50=500.0, p75=600.0, p95=700.0, p98=800.0, p99=900.0, p999=1000.0," + " m1_rate=3.0, m5_rate=4.0, m15_rate=5.0, mean_rate=2.0, rate_unit=events/second, duration_unit=milliseconds")));
    }

    @Test
    public void reportsAllMetricsDefault() {
        Mockito.when(logger.isInfoEnabled(marker)).thenReturn(true);
        infoReporter().report(map("test.gauge", () -> "value"), map("test.counter", counter()), map("test.histogram", histogram()), map("test.meter", meter()), map("test.timer", timer()));
        Mockito.verify(logger).info(marker, "type=GAUGE, name=prefix.test.gauge, value=value");
        Mockito.verify(logger).info(marker, "type=COUNTER, name=prefix.test.counter, count=100");
        Mockito.verify(logger).info(marker, ("type=HISTOGRAM, name=prefix.test.histogram, count=1, min=4, max=2, mean=3.0, " + "stddev=5.0, p50=6.0, p75=7.0, p95=8.0, p98=9.0, p99=10.0, p999=11.0"));
        Mockito.verify(logger).info(marker, ("type=METER, name=prefix.test.meter, count=1, m1_rate=3.0, m5_rate=4.0, " + "m15_rate=5.0, mean_rate=2.0, rate_unit=events/second"));
        Mockito.verify(logger).info(marker, ("type=TIMER, name=prefix.test.timer, count=1, min=300.0, max=100.0, " + ("mean=200.0, stddev=400.0, p50=500.0, p75=600.0, p95=700.0, p98=800.0, p99=900.0, p999=1000.0," + " m1_rate=3.0, m5_rate=4.0, m15_rate=5.0, mean_rate=2.0, rate_unit=events/second, duration_unit=milliseconds")));
    }
}

