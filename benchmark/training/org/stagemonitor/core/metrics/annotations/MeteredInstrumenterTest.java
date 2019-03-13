package org.stagemonitor.core.metrics.annotations;


import com.codahale.metrics.annotation.Metered;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class MeteredInstrumenterTest {
    private MeteredInstrumenterTest.TestObject testObject = new MeteredInstrumenterTest.TestObject();

    private static class TestObject {
        @Metered
        public void meteredDefault() {
        }

        @Metered
        private void meteredPrivate() {
        }

        @Metered(absolute = true)
        public void meteredAbsolute() {
        }

        @Metered(name = "myMeteredName")
        public void meteredName() {
        }

        @Metered(name = "myMeteredNameAbsolute", absolute = true)
        public void meteredNameAbsolute() {
        }
    }

    @Test
    public void testMeteredAspectDefault() {
        testObject.meteredDefault();
        assertOneMeterExists(MetricName.name("rate").tag("signature", "MeteredInstrumenterTest$TestObject#meteredDefault").build());
    }

    @Test
    public void testMeteredAspectPrivate() {
        testObject.meteredPrivate();
        Assert.assertEquals(1, Stagemonitor.getMetric2Registry().getMeters().size());
    }

    @Test
    public void testMeteredAspectAbsolute() {
        testObject.meteredAbsolute();
        assertOneMeterExists(MetricName.name("rate").tag("signature", "meteredAbsolute").build());
    }

    @Test
    public void testMeteredName() {
        testObject.meteredName();
        assertOneMeterExists(MetricName.name("rate").tag("signature", "MeteredInstrumenterTest$TestObject#myMeteredName").build());
    }

    @Test
    public void testMeteredNameAbsolute() {
        testObject.meteredNameAbsolute();
        assertOneMeterExists(MetricName.name("rate").tag("signature", "myMeteredNameAbsolute").build());
    }
}

