package org.stagemonitor.tracing;


import SpanUtils.PARAMETERS_PREFIX;
import com.codahale.metrics.Timer;
import java.util.Map;
import org.junit.Test;
import org.springframework.scheduling.annotation.Async;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.metrics.metrics2.MetricName;


public class TracedTransformerTest {
    private TracedTransformerTest.TestClass testClass;

    private TracedTransformerTest.TestClassLevelAnnotationClass testClassLevelAnnotationClass;

    private Metric2Registry metricRegistry;

    private SpanCapturingReporter spanCapturingReporter;

    private Map<String, Object> tags;

    @Test
    public void testMonitorRequests() throws Exception {
        testClass.monitorMe(1);
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        // either parameters.arg0 or parameters.s
        assertThat("1").isEqualTo(getTagsStartingWith(tags, PARAMETERS_PREFIX).iterator().next());
        assertThat("TracedTransformerTest$TestClass#monitorMe").isEqualTo(spanContext.getOperationName());
        assertThat(1).isEqualTo(spanContext.getCallTree().getChildren().size());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        assertThat(signature).contains("org.stagemonitor.tracing.TracedTransformerTest$TestClass.monitorMe");
        final Map<MetricName, Timer> timers = metricRegistry.getTimers();
        assertThat(timers).containsKey(name("response_time").operationName("TracedTransformerTest$TestClass#monitorMe").operationType("method_invocation").build());
    }

    @Test
    public void testMonitorAsyncMethods() throws Exception {
        testClass.asyncMethod();
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        assertThat("TracedTransformerTest$TestClass#asyncMethod").isEqualTo(spanContext.getOperationName());
        assertThat(1).isEqualTo(spanContext.getCallTree().getChildren().size());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        assertThat(signature).contains("org.stagemonitor.tracing.TracedTransformerTest$TestClass.asyncMethod");
        final Map<MetricName, Timer> timers = metricRegistry.getTimers();
        assertThat(timers).containsKey(name("response_time").operationName("TracedTransformerTest$TestClass#asyncMethod").operationType("method_invocation").build());
    }

    @Test
    public void testDontMonitorAnySuperMethod() throws Exception {
        testClass.dontMonitorMe();
        assertThat(spanCapturingReporter.get()).isNull();
    }

    @Test
    public void testMonitorRequestsThrowingException() throws Exception {
        assertThatThrownBy(() -> testClass.monitorThrowException()).isInstanceOf(NullPointerException.class);
        assertThat(NullPointerException.class.getName()).isEqualTo(tags.get("exception.class"));
        final Map<MetricName, Timer> timers = metricRegistry.getTimers();
        assertThat(timers).containsKey(name("response_time").operationName("TracedTransformerTest$TestClass#monitorThrowException").operationType("method_invocation").build());
    }

    @Test
    public void testMonitorRequestsAnnonymousInnerClass() throws Exception {
        testClass.monitorAnnonymousInnerClass();
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        assertThat("TracedTransformerTest$TestClass$1#run").isEqualTo(spanContext.getOperationName());
        assertThat(1).isEqualTo(spanContext.getCallTree().getChildren().size());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        assertThat(signature).contains("org.stagemonitor.tracing.TracedTransformerTest$TestClass$1.run");
        final Map<MetricName, Timer> timers = metricRegistry.getTimers();
        assertThat(timers).containsKey(name("response_time").operationName("TracedTransformerTest$TestClass$1#run").operationType("method_invocation").build());
    }

    @Test
    public void testMonitorRequestsResolvedAtRuntime() throws Exception {
        testClass.resolveNameAtRuntime();
        final String operationName = spanCapturingReporter.get().getOperationName();
        assertThat("TracedTransformerTest$TestSubClass#resolveNameAtRuntime").isEqualTo(operationName);
    }

    @Test
    public void testMonitorStaticMethod() throws Exception {
        TracedTransformerTest.TestClass.monitorStaticMethod();
        final String operationName = spanCapturingReporter.get().getOperationName();
        assertThat("TracedTransformerTest$TestClass#monitorStaticMethod").isEqualTo(operationName);
    }

    @Test
    public void testMonitorRequestsCustomName() throws Exception {
        testClass.doFancyStuff();
        final String operationName = spanCapturingReporter.get().getOperationName();
        assertThat("My Cool Method").isEqualTo(operationName);
    }

    @Test
    public void testNestedTracing() throws Exception {
        final TracedTransformerTest.TracedNestedTestClass testClass = new TracedTransformerTest.TracedNestedTestClass();
        testClass.foo();
        final SpanContextInformation barInfo = spanCapturingReporter.get();
        final SpanContextInformation fooInfo = spanCapturingReporter.get();
        assertThat(fooInfo.getOperationName()).contains("foo").doesNotContain("bar");
        assertThat(fooInfo.getCallTree().getSignature()).contains("foo").doesNotContain("bar");
        assertThat(fooInfo.getCallTree().getChildren().get(0).getChildren().get(0).getSignature()).contains("bar()");
        assertThat(barInfo.getCallTree()).isNull();
    }

    public static class TracedNestedTestClass {
        @Traced
        public void foo() {
            bar();
        }

        @Traced
        public void bar() {
        }
    }

    private abstract static class SuperAbstractTestClass {
        @Traced
        public abstract int monitorMe(int i) throws Exception;
    }

    private abstract static class AbstractTestClass extends TracedTransformerTest.SuperAbstractTestClass {
        public abstract void dontMonitorMe() throws Exception;
    }

    private static class TestClass extends TracedTransformerTest.AbstractTestClass {
        @Traced
        public int monitorMe(int i) throws Exception {
            return i;
        }

        @Override
        public void dontMonitorMe() throws Exception {
        }

        @Traced(resolveNameAtRuntime = true)
        public void resolveNameAtRuntime() throws Exception {
        }

        @Traced
        public static void monitorStaticMethod() {
        }

        @Traced(requestName = "My Cool Method")
        public void doFancyStuff() throws Exception {
        }

        public void monitorAnnonymousInnerClass() {
            new Runnable() {
                @Override
                @Traced
                public void run() {
                }
            }.run();
        }

        @Traced
        public int monitorThrowException() throws Exception {
            throw null;
        }

        @Async
        public void asyncMethod() {
        }
    }

    private static class TestSubClass extends TracedTransformerTest.TestClass {}

    @Test
    public void testClassLevelAnnotationClass() throws Exception {
        testClassLevelAnnotationClass.monitorMe("1");
        testClassLevelAnnotationClass.dontMonitorMe();
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        // either parameters.arg0 or parameters.s
        assertThat("1").isEqualTo(getTagsStartingWith(tags, PARAMETERS_PREFIX).iterator().next());
        assertThat("TracedTransformerTest$TestClassLevelAnnotationClass#monitorMe").isEqualTo(spanContext.getOperationName());
        assertThat(1).isEqualTo(spanContext.getCallTree().getChildren().size());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        assertThat(signature).contains("org.stagemonitor.tracing.TracedTransformerTest$TestClassLevelAnnotationClass.monitorMe");
        final Map<MetricName, Timer> timers = metricRegistry.getTimers();
        assertThat(timers).containsKey(name("response_time").operationName("TracedTransformerTest$TestClassLevelAnnotationClass#monitorMe").operationType("method_invocation").build());
    }

    @Traced
    private static class SuperTestClassLevelAnnotationClass {}

    private static class TestClassLevelAnnotationClass extends TracedTransformerTest.SuperTestClassLevelAnnotationClass {
        public String monitorMe(String s) throws Exception {
            return s;
        }

        private int dontMonitorMe() throws Exception {
            return 0;
        }
    }
}

