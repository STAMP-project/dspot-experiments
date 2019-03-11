package com.codahale.metrics.jetty9;


import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class InstrumentedQueuedThreadPoolTest {
    private static final String PREFIX = "prefix";

    private final MetricRegistry metricRegistry = Mockito.mock(MetricRegistry.class);

    private final InstrumentedQueuedThreadPool iqtp = new InstrumentedQueuedThreadPool(metricRegistry);

    private final ArgumentCaptor<String> metricNameCaptor = ArgumentCaptor.forClass(String.class);

    @Test
    public void customMetricsPrefix() throws Exception {
        iqtp.setPrefix(InstrumentedQueuedThreadPoolTest.PREFIX);
        iqtp.doStart();
        Mockito.verify(metricRegistry, Mockito.atLeastOnce()).register(metricNameCaptor.capture(), ArgumentMatchers.any(Metric.class));
        String metricName = metricNameCaptor.getValue();
        assertThat(metricName).overridingErrorMessage("Custom metric's prefix doesn't match").startsWith(InstrumentedQueuedThreadPoolTest.PREFIX);
    }

    @Test
    public void metricsPrefixBackwardCompatible() throws Exception {
        iqtp.doStart();
        Mockito.verify(metricRegistry, Mockito.atLeastOnce()).register(metricNameCaptor.capture(), ArgumentMatchers.any(Metric.class));
        String metricName = metricNameCaptor.getValue();
        assertThat(metricName).overridingErrorMessage("The default metrics prefix was changed").startsWith(QueuedThreadPool.class.getName());
    }
}

