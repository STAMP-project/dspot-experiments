package com.baeldung.metrics.servo;


import com.netflix.servo.DefaultMonitorRegistry;
import com.netflix.servo.Metric;
import com.netflix.servo.monitor.DynamicCounter;
import com.netflix.servo.monitor.Gauge;
import com.netflix.servo.monitor.MonitorConfig;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricObserverManualTest extends MetricTestBase {
    @Test
    public void givenMetrics_whenRegister_thenMonitored() throws InterruptedException {
        Gauge<Double> gauge = new com.netflix.servo.monitor.BasicGauge(MonitorConfig.builder("test").build(), () -> 2.32);
        Assert.assertEquals(2.32, gauge.getValue(), 0.01);
        DefaultMonitorRegistry.getInstance().register(gauge);
        for (int i = 0; i < 2; i++) {
            TimeUnit.SECONDS.sleep(1);
        }
        List<List<Metric>> metrics = observer.getObservations();
        Assert.assertThat(metrics, Matchers.hasSize(Matchers.greaterThanOrEqualTo(2)));
        Iterator<List<Metric>> metricIterator = metrics.iterator();
        // skip first empty observation
        metricIterator.next();
        while (metricIterator.hasNext()) {
            Assert.assertThat(metricIterator.next(), Matchers.hasItem(Matchers.allOf(Matchers.hasProperty("config", Matchers.hasProperty("tags", Matchers.hasItem(GAUGE))), Matchers.hasProperty("value", Matchers.is(2.32)))));
        } 
    }

    @Test
    public void givenMetrics_whenRegisterDynamically_thenMonitored() throws Exception {
        for (int i = 0; i < 2; i++) {
            DynamicCounter.increment("monitor-name", "tag-key", "tag-value");
            TimeUnit.SECONDS.sleep(1);
        }
        List<List<Metric>> metrics = observer.getObservations();
        Assert.assertThat(metrics, Matchers.hasSize(Matchers.greaterThanOrEqualTo(2)));
        Iterator<List<Metric>> metricIterator = metrics.iterator();
        // skip first empty observation
        metricIterator.next();
        while (metricIterator.hasNext()) {
            Assert.assertThat(metricIterator.next(), Matchers.hasItem(Matchers.hasProperty("value", Matchers.greaterThanOrEqualTo(1.0))));
        } 
    }
}

