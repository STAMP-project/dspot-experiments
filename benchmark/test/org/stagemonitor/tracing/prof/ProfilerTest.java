package org.stagemonitor.tracing.prof;


import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.tracing.profiler.CallStackElement;
import org.stagemonitor.tracing.profiler.Profiler;


public class ProfilerTest {
    @Test
    public void testProfiler() {
        ProfilerTest profilerTest = new ProfilerTest();
        CallStackElement total = Profiler.activateProfiling("total");
        Assert.assertEquals(21, profilerTest.method1());
        Profiler.stop();
        Assert.assertEquals(total.toString(), 1, total.getChildren().size());
        Assert.assertEquals(total.toString(), 3, total.getChildren().get(0).getChildren().size());
        final String method5 = total.getChildren().get(0).getChildren().get(2).getSignature();
        Assert.assertTrue(method5, method5.contains("org.stagemonitor.tracing.prof.ProfilerTest.method5"));
    }

    @Test
    public void testInnerPrivateMethod() {
        class Test {
            private void test() {
            }
        }
        Test test = new Test();
        CallStackElement total = Profiler.activateProfiling("total");
        test.test();
        Profiler.stop();
        Assert.assertFalse(total.toString(), total.getChildren().iterator().next().getSignature().contains("access$"));
    }
}

