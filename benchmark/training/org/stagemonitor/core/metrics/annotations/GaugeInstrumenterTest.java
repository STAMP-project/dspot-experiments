package org.stagemonitor.core.metrics.annotations;


import org.junit.Test;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class GaugeInstrumenterTest {
    private GaugeTestObject testObject = new GaugeTestObject();

    @Test
    public void testGaugeAspectDefault() {
        final Metric2Registry metricRegistry = Stagemonitor.getMetric2Registry();
        assertThat(metricRegistry.getGauges().keySet()).contains(MetricName.name("gauge_GaugeTestObject#gaugeDefault").build(), MetricName.name("gauge_GaugeTestObject#staticGaugeDefault").build(), MetricName.name("gauge_gaugeAbsolute").build(), MetricName.name("gauge_GaugeTestObject#myGaugeName").build(), MetricName.name("gauge_myGaugeNameAbsolute").build());
    }
}

