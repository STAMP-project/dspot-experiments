/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dropwizard.metrics;


import MetricFilter.ALL;
import java.lang.management.ManagementFactory;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JmxReporterTest {
    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private final String name = UUID.randomUUID().toString().replaceAll("[{\\-}]", "");

    private final MetricRegistry registry = new MetricRegistry();

    private final JmxReporter reporter = JmxReporter.forRegistry(registry).registerWith(mBeanServer).inDomain(name).convertDurationsTo(TimeUnit.MILLISECONDS).convertRatesTo(TimeUnit.SECONDS).filter(ALL).build();

    private final Gauge gauge = Mockito.mock(Gauge.class);

    private final Counter counter = Mockito.mock(Counter.class);

    private final Histogram histogram = Mockito.mock(Histogram.class);

    private final Meter meter = Mockito.mock(Meter.class);

    private final Timer timer = Mockito.mock(Timer.class);

    private final ObjectNameFactory mockObjectNameFactory = Mockito.mock(ObjectNameFactory.class);

    private final ObjectNameFactory concreteObjectNameFactory = reporter.getObjectNameFactory();

    @Test
    public void registersMBeansForMetricObjectsUsingProvidedObjectNameFactory() throws Exception {
        ObjectName n = new ObjectName(((name) + ":name=dummy"));
        try {
            String widgetName = "something";
            Mockito.when(mockObjectNameFactory.createName(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(MetricName.class))).thenReturn(n);
            Gauge aGauge = Mockito.mock(Gauge.class);
            Mockito.when(aGauge.getValue()).thenReturn(1);
            JmxReporter reporter = JmxReporter.forRegistry(registry).registerWith(mBeanServer).inDomain(name).createsObjectNamesWith(mockObjectNameFactory).build();
            registry.register(widgetName, aGauge);
            reporter.start();
            Mockito.verify(mockObjectNameFactory).createName(ArgumentMatchers.eq("gauges"), ArgumentMatchers.any(String.class), ArgumentMatchers.eq(MetricName.build("something")));
            // verifyNoMoreInteractions(mockObjectNameFactory);
        } finally {
            reporter.stop();
            if (mBeanServer.isRegistered(n)) {
                mBeanServer.unregisterMBean(n);
            }
        }
    }

    @Test
    public void registersMBeansForGauges() throws Exception {
        final AttributeList attributes = getAttributes("gauge", "Value");
        assertThat(values(attributes)).contains(entry("Value", 1));
    }

    @Test
    public void registersMBeansForCounters() throws Exception {
        final AttributeList attributes = getAttributes("test.counter", "Count");
        assertThat(values(attributes)).contains(entry("Count", 100L));
    }

    @Test
    public void registersMBeansForHistograms() throws Exception {
        final AttributeList attributes = getAttributes("test.histogram", "Count", "Max", "Mean", "Min", "StdDev", "50thPercentile", "75thPercentile", "95thPercentile", "98thPercentile", "99thPercentile", "999thPercentile");
        assertThat(values(attributes)).contains(entry("Count", 1L)).contains(entry("Max", 2L)).contains(entry("Mean", 3.0)).contains(entry("Min", 4L)).contains(entry("StdDev", 5.0)).contains(entry("50thPercentile", 6.0)).contains(entry("75thPercentile", 7.0)).contains(entry("95thPercentile", 8.0)).contains(entry("98thPercentile", 9.0)).contains(entry("99thPercentile", 10.0)).contains(entry("999thPercentile", 11.0));
    }

    @Test
    public void registersMBeansForMeters() throws Exception {
        final AttributeList attributes = getAttributes("test.meter", "Count", "MeanRate", "OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate", "RateUnit");
        assertThat(values(attributes)).contains(entry("Count", 1L)).contains(entry("MeanRate", 2.0)).contains(entry("OneMinuteRate", 3.0)).contains(entry("FiveMinuteRate", 4.0)).contains(entry("FifteenMinuteRate", 5.0)).contains(entry("RateUnit", "events/second"));
    }

    @Test
    public void registersMBeansForTimers() throws Exception {
        final AttributeList attributes = getAttributes("test.another.timer", "Count", "MeanRate", "OneMinuteRate", "FiveMinuteRate", "FifteenMinuteRate", "Max", "Mean", "Min", "StdDev", "50thPercentile", "75thPercentile", "95thPercentile", "98thPercentile", "99thPercentile", "999thPercentile", "RateUnit", "DurationUnit");
        assertThat(values(attributes)).contains(entry("Count", 1L)).contains(entry("MeanRate", 2.0)).contains(entry("OneMinuteRate", 3.0)).contains(entry("FiveMinuteRate", 4.0)).contains(entry("FifteenMinuteRate", 5.0)).contains(entry("Max", 100.0)).contains(entry("Mean", 200.0)).contains(entry("Min", 300.0)).contains(entry("StdDev", 400.0)).contains(entry("50thPercentile", 500.0)).contains(entry("75thPercentile", 600.0)).contains(entry("95thPercentile", 700.0)).contains(entry("98thPercentile", 800.0)).contains(entry("99thPercentile", 900.0)).contains(entry("999thPercentile", 1000.0)).contains(entry("RateUnit", "events/second")).contains(entry("DurationUnit", "milliseconds"));
    }

    @Test
    public void cleansUpAfterItselfWhenStopped() throws Exception {
        reporter.stop();
        try {
            getAttributes("gauge", "Value");
            failBecauseExceptionWasNotThrown(InstanceNotFoundException.class);
        } catch (InstanceNotFoundException ignored) {
        }
    }

    @Test
    public void objectNameModifyingMBeanServer() throws Exception {
        MBeanServer mockedMBeanServer = Mockito.mock(MBeanServer.class);
        // overwrite the objectName
        Mockito.when(mockedMBeanServer.registerMBean(ArgumentMatchers.any(Object.class), ArgumentMatchers.any(ObjectName.class))).thenReturn(new ObjectInstance("DOMAIN:key=value", "className"));
        MetricRegistry testRegistry = new MetricRegistry();
        JmxReporter testJmxReporter = JmxReporter.forRegistry(testRegistry).registerWith(mockedMBeanServer).inDomain(name).build();
        testJmxReporter.start();
        // should trigger a registerMBean
        testRegistry.timer("test");
        // should trigger an unregisterMBean with the overwritten objectName = "DOMAIN:key=value"
        testJmxReporter.stop();
        Mockito.verify(mockedMBeanServer).unregisterMBean(new ObjectName("DOMAIN:key=value"));
    }

    @Test
    public void testJmxMetricNameWithAsterisk() {
        MetricRegistry metricRegistry = new MetricRegistry();
        JmxReporter.forRegistry(metricRegistry).build().start();
        metricRegistry.counter("test*");
    }
}

