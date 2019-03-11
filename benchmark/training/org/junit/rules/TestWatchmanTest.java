package org.junit.rules;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runners.model.FrameworkMethod;

import static junit.framework.Assert.fail;


@SuppressWarnings("deprecation")
public class TestWatchmanTest {
    public static class ViolatedAssumptionTest {
        static StringBuilder log;

        @BeforeClass
        public static void initLog() {
            TestWatchmanTest.ViolatedAssumptionTest.log = new StringBuilder();
        }

        @Rule
        public TestWatchmanTest.LoggingTestWatchman watchman = new TestWatchmanTest.LoggingTestWatchman(TestWatchmanTest.ViolatedAssumptionTest.log);

        @Test
        public void succeeds() {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void neitherLogSuccessNorFailedForViolatedAssumption() {
        JUnitCore.runClasses(TestWatchmanTest.ViolatedAssumptionTest.class);
        Assert.assertThat(TestWatchmanTest.ViolatedAssumptionTest.log.toString(), CoreMatchers.is("starting finished "));
    }

    public static class FailingTest {
        static StringBuilder log;

        @BeforeClass
        public static void initLog() {
            TestWatchmanTest.FailingTest.log = new StringBuilder();
        }

        @Rule
        public TestWatchmanTest.LoggingTestWatchman watchman = new TestWatchmanTest.LoggingTestWatchman(TestWatchmanTest.FailingTest.log);

        @Test
        public void succeeds() {
            fail();
        }
    }

    @Test
    public void logFailingTest() {
        JUnitCore.runClasses(TestWatchmanTest.FailingTest.class);
        Assert.assertThat(TestWatchmanTest.FailingTest.log.toString(), CoreMatchers.is("starting failed finished "));
    }

    private static class LoggingTestWatchman extends TestWatchman {
        private final StringBuilder log;

        private LoggingTestWatchman(StringBuilder log) {
            this.log = log;
        }

        @Override
        public void succeeded(FrameworkMethod method) {
            log.append("succeeded ");
        }

        @Override
        public void failed(Throwable e, FrameworkMethod method) {
            log.append("failed ");
        }

        @Override
        public void starting(FrameworkMethod method) {
            log.append("starting ");
        }

        @Override
        public void finished(FrameworkMethod method) {
            log.append("finished ");
        }
    }
}

