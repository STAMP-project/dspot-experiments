package org.robolectric;


import InstrumentationConfiguration.Builder;
import android.app.Application;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;
import org.robolectric.internal.bytecode.Sandbox;


@RunWith(JUnit4.class)
public class TestRunnerSequenceTest {
    public static class StateHolder {
        public static List<String> transcript;
    }

    private String priorResourcesMode;

    @Test
    public void shouldRunThingsInTheRightOrder() throws Exception {
        assertNoFailures(run(new TestRunnerSequenceTest.Runner(TestRunnerSequenceTest.SimpleTest.class)));
        assertThat(TestRunnerSequenceTest.StateHolder.transcript).containsExactly("configureSandbox", "application.onCreate", "beforeTest", "application.beforeTest", "prepareTest", "application.prepareTest", "TEST!", "application.onTerminate", "afterTest", "application.afterTest");
        TestRunnerSequenceTest.StateHolder.transcript.clear();
    }

    @Test
    public void whenNoAppManifest_shouldRunThingsInTheRightOrder() throws Exception {
        assertNoFailures(run(new TestRunnerSequenceTest.Runner(TestRunnerSequenceTest.SimpleTest.class) {}));
        assertThat(TestRunnerSequenceTest.StateHolder.transcript).containsExactly("configureSandbox", "application.onCreate", "beforeTest", "application.beforeTest", "prepareTest", "application.prepareTest", "TEST!", "application.onTerminate", "afterTest", "application.afterTest");
        TestRunnerSequenceTest.StateHolder.transcript.clear();
    }

    @Test
    public void shouldReleaseAllStateAfterClassSoWeDontLeakMemory() throws Exception {
        final List<RobolectricTestRunner.RobolectricFrameworkMethod> methods = new ArrayList<>();
        RobolectricTestRunner robolectricTestRunner = new TestRunnerSequenceTest.Runner(TestRunnerSequenceTest.SimpleTest.class) {
            @Override
            protected void finallyAfterTest(FrameworkMethod method) {
                super.finallyAfterTest(method);
                RobolectricFrameworkMethod roboMethod = ((RobolectricFrameworkMethod) (method));
                assertThat(roboMethod.getEnvironment()).isNull();
                assertThat(roboMethod.testLifecycle).isNull();
                methods.add(roboMethod);
            }
        };
        robolectricTestRunner.run(new RunNotifier());
        assertThat(methods).isNotEmpty();
    }

    @Config(application = TestRunnerSequenceTest.MyApplication.class)
    public static class SimpleTest {
        @Test
        public void shouldDoNothingMuch() throws Exception {
            TestRunnerSequenceTest.StateHolder.transcript.add("TEST!");
        }
    }

    public static class Runner extends SingleSdkRobolectricTestRunner {
        Runner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        @Nonnull
        @Override
        protected InstrumentationConfiguration createClassLoaderConfig(FrameworkMethod method) {
            InstrumentationConfiguration.Builder builder = new InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method));
            builder.doNotAcquireClass(TestRunnerSequenceTest.StateHolder.class);
            return builder.build();
        }

        @Nonnull
        @Override
        protected Class<? extends TestLifecycle> getTestLifecycleClass() {
            return TestRunnerSequenceTest.MyTestLifecycle.class;
        }

        @Override
        protected void configureSandbox(Sandbox sandbox, FrameworkMethod frameworkMethod) {
            TestRunnerSequenceTest.StateHolder.transcript.add("configureSandbox");
            super.configureSandbox(sandbox, frameworkMethod);
        }
    }

    @DoNotInstrument
    public static class MyTestLifecycle extends DefaultTestLifecycle {
        @Override
        public void beforeTest(Method method) {
            TestRunnerSequenceTest.StateHolder.transcript.add("beforeTest");
            super.beforeTest(method);
        }

        @Override
        public void prepareTest(Object test) {
            TestRunnerSequenceTest.StateHolder.transcript.add("prepareTest");
            super.prepareTest(test);
        }

        @Override
        public void afterTest(Method method) {
            TestRunnerSequenceTest.StateHolder.transcript.add("afterTest");
            super.afterTest(method);
        }
    }

    public static class MyApplication extends Application implements TestLifecycleApplication {
        @Override
        public void onCreate() {
            TestRunnerSequenceTest.StateHolder.transcript.add("application.onCreate");
        }

        @Override
        public void beforeTest(Method method) {
            TestRunnerSequenceTest.StateHolder.transcript.add("application.beforeTest");
        }

        @Override
        public void prepareTest(Object test) {
            TestRunnerSequenceTest.StateHolder.transcript.add("application.prepareTest");
        }

        @Override
        public void afterTest(Method method) {
            TestRunnerSequenceTest.StateHolder.transcript.add("application.afterTest");
        }

        @Override
        public void onTerminate() {
            TestRunnerSequenceTest.StateHolder.transcript.add("application.onTerminate");
        }
    }
}

