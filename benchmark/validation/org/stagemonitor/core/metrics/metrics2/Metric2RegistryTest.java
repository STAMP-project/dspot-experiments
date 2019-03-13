package org.stagemonitor.core.metrics.metrics2;


import com.codahale.metrics.Metered;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistryListener;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class Metric2RegistryTest {
    @Test
    public void testWrappingDropwizardMetrics() {
        Metric2Registry registry = new Metric2Registry();
        MetricRegistryListener listener = Mockito.mock(MetricRegistryListener.class);
        registry.getMetricRegistry().addListener(listener);
        registry.register(MetricName.name("test").build(), new com.codahale.metrics.Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 1;
            }
        });
        // Verify the underlying Dropwizard listener was called
        Mockito.verify(listener).onGaugeAdded(Mockito.eq("test"), Mockito.<com.codahale.metrics.Gauge>any());
        // Should be able to read the gauge from either registry
        Map.Entry<MetricName, com.codahale.metrics.Gauge> stagemonitorEntry = registry.getGauges().entrySet().iterator().next();
        Map.Entry<String, com.codahale.metrics.Gauge> dropwizardEntry = registry.getMetricRegistry().getGauges().entrySet().iterator().next();
        Assert.assertEquals("test", stagemonitorEntry.getKey().getName());
        Assert.assertEquals(1, stagemonitorEntry.getValue().getValue());
        Assert.assertEquals("test", dropwizardEntry.getKey());
        Assert.assertEquals(1, dropwizardEntry.getValue().getValue());
        // Unregister should notify Dropwizard listeners
        registry.remove(MetricName.name("test").build());
        Mockito.verify(listener).onGaugeRemoved(Mockito.eq("test"));
        Assert.assertEquals(0, registry.getGauges().size());
        Assert.assertEquals(0, registry.getMetricRegistry().getGauges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateMetricNameEndsWithIllegalArgumentException() {
        Metric2Registry registry = new Metric2Registry();
        MetricName metric1 = MetricName.name("test").build();
        registry.register(metric1, getGauge());
        registry.register(metric1, getMetered());
    }

    @Test
    public void testRegisterAny() {
        Metric2Registry registry = new Metric2Registry();
        final MetricName metric1 = MetricName.name("test").build();
        final com.codahale.metrics.Gauge<Integer> gauge = getGauge();
        final Metered metered = getMetered();
        Metric2Set metricSet1 = new Metric2Set() {
            @Override
            public Map<MetricName, Metric> getMetrics() {
                Map<MetricName, Metric> map = new HashMap<MetricName, Metric>(1);
                map.put(metric1, gauge);
                return map;
            }
        };
        Metric2Set metricSet2 = new Metric2Set() {
            @Override
            public Map<MetricName, Metric> getMetrics() {
                Map<MetricName, Metric> map = new HashMap<MetricName, Metric>(1);
                map.put(metric1, metered);
                return map;
            }
        };
        registry.registerAny(metricSet1);
        registry.registerAny(metricSet2);
        // first one is registered only
        Assert.assertEquals(1, registry.getGauges().entrySet().size());
        Assert.assertEquals(0, registry.getMeters().entrySet().size());
        Map.Entry<MetricName, com.codahale.metrics.Gauge> stagemonitorEntry = registry.getGauges().entrySet().iterator().next();
        Assert.assertEquals("test", stagemonitorEntry.getKey().getName());
        Assert.assertEquals(1, stagemonitorEntry.getValue().getValue());
    }
}

