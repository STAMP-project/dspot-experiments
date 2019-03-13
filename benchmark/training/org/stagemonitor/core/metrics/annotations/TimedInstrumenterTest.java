package org.stagemonitor.core.metrics.annotations;


import com.codahale.metrics.annotation.Timed;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class TimedInstrumenterTest {
    private TimedInstrumenterTest.TestObject testObject = new TimedInstrumenterTest.TestObject();

    private static class TestObject {
        @Timed
        public void timedDefault() {
        }

        @Timed
        private void timedPrivate() {
        }

        @Timed(absolute = true)
        public void timedAbsolute() {
        }

        @Timed(name = "myTimedName")
        public void timedName() {
        }

        @Timed(name = "myTimedNameAbsolute", absolute = true)
        public void timedNameAbsolute() {
        }
    }

    @Test
    public void testTimedAspectDefault() {
        testObject.timedDefault();
        assertOneTimerExists(MetricName.name("timer").tag("signature", "TimedInstrumenterTest$TestObject#timedDefault").build());
    }

    @Test
    public void testTimedAspectPrivate() {
        testObject.timedPrivate();
        Assert.assertEquals(1, Stagemonitor.getMetric2Registry().getTimers().size());
    }

    @Test
    public void testTimedAspectAbsolute() {
        testObject.timedAbsolute();
        assertOneTimerExists(MetricName.name("timer").tag("signature", "timedAbsolute").build());
    }

    @Test
    public void testTimedName() {
        testObject.timedName();
        assertOneTimerExists(MetricName.name("timer").tag("signature", "TimedInstrumenterTest$TestObject#myTimedName").build());
    }

    @Test
    public void testTimedNameAbsolute() {
        testObject.timedNameAbsolute();
        assertOneTimerExists(MetricName.name("timer").tag("signature", "myTimedNameAbsolute").build());
    }
}

