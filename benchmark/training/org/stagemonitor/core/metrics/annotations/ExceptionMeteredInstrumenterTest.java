package org.stagemonitor.core.metrics.annotations;


import com.codahale.metrics.annotation.ExceptionMetered;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class ExceptionMeteredInstrumenterTest {
    private ExceptionMeteredInstrumenterTest.TestObject testObject = new ExceptionMeteredInstrumenterTest.TestObject();

    private static class TestObject {
        @ExceptionMetered
        private void exceptionMeteredDefault() {
            throw null;
        }

        @ExceptionMetered
        private void exceptionMeteredPrivate() {
            throw null;
        }

        @ExceptionMetered(absolute = true)
        public void exceptionMeteredAbsolute() {
            throw null;
        }

        @ExceptionMetered(name = "myExceptionMeteredName")
        public void exceptionMeteredName() {
            throw null;
        }

        @ExceptionMetered(name = "myExceptionMeteredNameAbsolute", absolute = true)
        public void exceptionMeteredNameAbsolute() {
            throw null;
        }

        @ExceptionMetered(cause = NullPointerException.class, absolute = true)
        public void exceptionMeteredCauseExact() {
            throw null;
        }

        @ExceptionMetered(cause = RuntimeException.class, absolute = true)
        public void exceptionMeteredCauseSubtype() {
            throw null;
        }

        @ExceptionMetered(cause = RuntimeException.class, absolute = true)
        public void exceptionMeteredCauseSupertype() throws Exception {
            throw new Exception();
        }

        @ExceptionMetered(cause = Exception.class)
        public void exceptionMeteredCauseNoException() {
        }

        @ExceptionMetered
        public void exceptionMeteredNoException() {
        }
    }

    @Test
    public void testExceptionMeteredAspectDefault() {
        try {
            testObject.exceptionMeteredDefault();
        } catch (Exception e) {
            // ignore
        }
        assertOneMeterExists(MetricName.name("exception_rate").tag("signature", "ExceptionMeteredInstrumenterTest$TestObject#exceptionMeteredDefault").build());
    }

    @Test
    public void testExceptionMeteredAspectexceptionMeteredPrivate() {
        try {
            testObject.exceptionMeteredPrivate();
        } catch (Exception e) {
            // ignore
        }
        Assert.assertEquals(1, Stagemonitor.getMetric2Registry().getMeters().size());
    }

    @Test
    public void testExceptionMeteredAspectAbsolute() {
        try {
            testObject.exceptionMeteredAbsolute();
        } catch (Exception e) {
            // ignore
        }
        assertOneMeterExists(MetricName.name("exception_rate").tag("signature", "exceptionMeteredAbsolute").build());
    }

    @Test
    public void testExceptionMeteredName() {
        try {
            testObject.exceptionMeteredName();
        } catch (Exception e) {
            // ignore
        }
        assertOneMeterExists(MetricName.name("exception_rate").tag("signature", "ExceptionMeteredInstrumenterTest$TestObject#myExceptionMeteredName").build());
    }

    @Test
    public void testExceptionMeteredNameAbsolute() {
        try {
            testObject.exceptionMeteredNameAbsolute();
        } catch (Exception e) {
            // ignore
        }
        assertOneMeterExists(MetricName.name("exception_rate").tag("signature", "myExceptionMeteredNameAbsolute").build());
    }

    @Test
    public void testExceptionMeteredCauseExact() {
        try {
            testObject.exceptionMeteredCauseExact();
        } catch (Exception e) {
            // ignore
        }
        assertOneMeterExists(MetricName.name("exception_rate").tag("signature", "exceptionMeteredCauseExact").build());
    }

    @Test
    public void testExceptionMeteredCauseSubtype() {
        try {
            testObject.exceptionMeteredCauseSubtype();
        } catch (Exception e) {
            // ignore
        }
        assertOneMeterExists(MetricName.name("exception_rate").tag("signature", "exceptionMeteredCauseSubtype").build());
    }

    @Test
    public void testExceptionMeteredCauseSupertype() {
        try {
            testObject.exceptionMeteredCauseSupertype();
        } catch (Exception e) {
            // ignore
        }
        final Metric2Registry metricRegistry = Stagemonitor.getMetric2Registry();
        Assert.assertEquals(metricRegistry.getMeters().toString(), 0, metricRegistry.getMeters().size());
    }

    @Test
    public void testExceptionMeteredCauseNoException() {
        try {
            testObject.exceptionMeteredCauseNoException();
        } catch (Exception e) {
            // ignore
        }
        final Metric2Registry metricRegistry = Stagemonitor.getMetric2Registry();
        Assert.assertEquals(0, metricRegistry.getMeters().size());
    }

    @Test
    public void testExceptionMeteredNoException() {
        try {
            testObject.exceptionMeteredNoException();
        } catch (Exception e) {
            // ignore
        }
        final Metric2Registry metricRegistry = Stagemonitor.getMetric2Registry();
        Assert.assertEquals(0, metricRegistry.getMeters().size());
    }
}

