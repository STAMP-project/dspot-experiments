package org.stagemonitor.tracing;


import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.tracing.profiler.CallStackElement;
import org.stagemonitor.tracing.profiler.Profiler;


public class MultipleAnnotationsAndProfilerTest {
    private MultipleAnnotationsAndProfilerTest.TestObject testObject = new MultipleAnnotationsAndProfilerTest.TestObject();

    private static class TestObject {
        @Metered
        @Timed
        public void testMethod() {
        }
    }

    @Test
    public void testMeterTimer() {
        CallStackElement total = Profiler.activateProfiling("total");
        testObject.testMethod();
        Profiler.stop();
        final String signature = total.getChildren().get(0).getSignature();
        Assert.assertTrue(signature, signature.contains("org.stagemonitor.tracing.MultipleAnnotationsAndProfilerTest$TestObject.testMethod"));
        assertOneMeterExists(name("rate").tag("signature", "MultipleAnnotationsAndProfilerTest$TestObject#testMethod").build());
        assertOneTimerExists(name("timer").tag("signature", "MultipleAnnotationsAndProfilerTest$TestObject#testMethod").build());
    }
}

