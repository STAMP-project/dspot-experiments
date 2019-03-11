package org.junit.tests.running.classes;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.junit.runners.BlockJUnit4ClassRunner;


public class ThreadsTest {
    private List<Boolean> interruptedFlags = new ArrayList<Boolean>();

    private JUnitCore core = new JUnitCore();

    public static class TestWithInterrupt {
        @Test
        public void interruptCurrentThread() {
            Thread.currentThread().interrupt();
        }

        @Test
        public void otherTestCaseInterruptingCurrentThread() {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void currentThreadInterruptedStatusIsClearedAfterEachTestExecution() {
        core.addListener(new RunListener() {
            @Override
            public void testFinished(Description description) {
                interruptedFlags.add(Thread.currentThread().isInterrupted());
            }
        });
        Result result = core.run(ThreadsTest.TestWithInterrupt.class);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(Arrays.asList(false, false), interruptedFlags);
    }

    @RunWith(BlockJUnit4ClassRunner.class)
    public static class TestWithInterruptFromAfterClass {
        @AfterClass
        public static void interruptCurrentThread() {
            Thread.currentThread().interrupt();
        }

        @Test
        public void test() {
            // no-op
        }
    }

    @Test
    public void currentThreadInterruptStatusIsClearedAfterSuiteExecution() {
        core.addListener(new RunListener() {
            @Override
            public void testSuiteFinished(Description description) {
                interruptedFlags.add(Thread.currentThread().isInterrupted());
            }
        });
        Request request = Request.aClass(ThreadsTest.TestWithInterruptFromAfterClass.class);
        Result result = core.run(request);
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(Collections.singletonList(false), interruptedFlags);
    }
}

