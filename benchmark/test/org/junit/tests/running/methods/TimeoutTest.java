package org.junit.tests.running.methods;


import java.util.concurrent.TimeUnit;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


public class TimeoutTest {
    public static class FailureWithTimeoutTest {
        @Test(timeout = 1000)
        public void failure() {
            Assert.fail();
        }
    }

    @Test
    public void failureWithTimeout() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.FailureWithTimeoutTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertEquals(AssertionError.class, result.getFailures().get(0).getException().getClass());
    }

    public static class FailureWithTimeoutRunTimeExceptionTest {
        @Test(timeout = 1000)
        public void failure() {
            throw new NullPointerException();
        }
    }

    @Test
    public void failureWithTimeoutRunTimeException() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.FailureWithTimeoutRunTimeExceptionTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
        Assert.assertEquals(NullPointerException.class, result.getFailures().get(0).getException().getClass());
    }

    public static class SuccessWithTimeoutTest {
        @Test(timeout = 1000)
        public void success() {
        }
    }

    @Test
    public void successWithTimeout() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.SuccessWithTimeoutTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(0, result.getFailureCount());
    }

    public static class TimeoutFailureTest {
        @Test(timeout = 100)
        public void success() throws InterruptedException {
            Thread.sleep(40000);
        }
    }

    public static class InfiniteLoopTest {
        @Test(timeout = 100)
        public void failure() {
            infiniteLoop();
        }

        private void infiniteLoop() {
            for (; ;) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Test
    public void infiniteLoop() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.InfiniteLoopTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        Assert.assertTrue(exception.getMessage().contains("test timed out after 100 milliseconds"));
    }

    public static class ImpatientLoopTest {
        @Test(timeout = 1)
        public void failure() {
            infiniteLoop();
        }

        private void infiniteLoop() {
            for (; ;);
        }
    }

    @Test
    public void stalledThreadAppearsInStackTrace() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.InfiniteLoopTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        Assert.assertThat(stackForException(exception), CoreMatchers.containsString("infiniteLoop"));// Make sure we have the stalled frame on the stack somewhere

    }

    public static class InfiniteLoopMultithreaded {
        private static class ThreadTest implements Runnable {
            private boolean fStall;

            public ThreadTest(boolean stall) {
                fStall = stall;
            }

            public void run() {
                if (fStall)
                    for (; ;);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }

        public void failure(boolean mainThreadStalls) throws Exception {
            Thread t1 = new Thread(new TimeoutTest.InfiniteLoopMultithreaded.ThreadTest(false), "timeout-thr1");
            Thread t2 = new Thread(new TimeoutTest.InfiniteLoopMultithreaded.ThreadTest((!mainThreadStalls)), "timeout-thr2");
            Thread t3 = new Thread(new TimeoutTest.InfiniteLoopMultithreaded.ThreadTest(false), "timeout-thr3");
            t1.start();
            t2.start();
            t3.start();
            if (mainThreadStalls)
                for (; ;);

            t1.join();
            t2.join();
            t3.join();
        }
    }

    public static class InfiniteLoopWithStuckThreadTest {
        @Rule
        public TestRule globalTimeout = Timeout.builder().withTimeout(100, TimeUnit.MILLISECONDS).withLookingForStuckThread(true).build();

        @Test
        public void failure() throws Exception {
            new TimeoutTest.InfiniteLoopMultithreaded().failure(false);
        }
    }

    public static class InfiniteLoopStuckInMainThreadTest {
        @Rule
        public TestRule globalTimeout = Timeout.builder().withTimeout(100, TimeUnit.MILLISECONDS).withLookingForStuckThread(true).build();

        @Test
        public void failure() throws Exception {
            new TimeoutTest.InfiniteLoopMultithreaded().failure(true);
        }
    }

    @Test
    public void timeoutFailureMultithreaded() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.InfiniteLoopWithStuckThreadTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(2, result.getFailureCount());
        Throwable[] exception = new Throwable[2];
        for (int i = 0; i < 2; i++)
            exception[i] = result.getFailures().get(i).getException();

        Assert.assertThat(exception[0].getMessage(), CoreMatchers.containsString("test timed out after 100 milliseconds"));
        Assert.assertThat(stackForException(exception[0]), CoreMatchers.containsString("Thread.join"));
        Assert.assertThat(exception[1].getMessage(), CoreMatchers.containsString("Appears to be stuck in thread timeout-thr2"));
    }

    @Test
    public void timeoutFailureMultithreadedStuckInMain() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.InfiniteLoopStuckInMainThreadTest.class);
        Assert.assertEquals(1, result.getRunCount());
        Assert.assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        Assert.assertThat(exception.getMessage(), CoreMatchers.containsString("test timed out after 100 milliseconds"));
        Assert.assertThat(exception.getMessage(), CoreMatchers.not(CoreMatchers.containsString("Appears to be stuck")));
    }

    @Test
    public void compatibility() {
        TestResult result = new TestResult();
        new JUnit4TestAdapter(TimeoutTest.InfiniteLoopTest.class).run(result);
        Assert.assertEquals(1, result.errorCount());
    }

    public static class WillTimeOut {
        static boolean afterWasCalled = false;

        @Test(timeout = 1)
        public void test() {
            for (; ;) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // ok, tests are over
                }
            }
        }

        @After
        public void after() {
            TimeoutTest.WillTimeOut.afterWasCalled = true;
        }
    }

    @Test
    public void makeSureAfterIsCalledAfterATimeout() {
        JUnitCore.runClasses(TimeoutTest.WillTimeOut.class);
        Assert.assertThat(TimeoutTest.WillTimeOut.afterWasCalled, CoreMatchers.is(true));
    }

    public static class TimeOutZero {
        @Rule
        public Timeout timeout = Timeout.seconds(0);

        @Test
        public void test() {
            try {
                Thread.sleep(200);// long enough to suspend thread execution

            } catch (InterruptedException e) {
                // Don't care
            }
        }
    }

    @Test
    public void testZeroTimeoutIsIgnored() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.TimeOutZero.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("Test should not have failed", 0, result.getFailureCount());
    }

    private static class TimeoutSubclass extends Timeout {
        public TimeoutSubclass(long timeout, TimeUnit timeUnit) {
            super(timeout, timeUnit);
        }

        public long getTimeoutFromSuperclass(TimeUnit unit) {
            return super.getTimeout(unit);
        }
    }

    public static class TimeOutOneSecond {
        @Rule
        public TimeoutTest.TimeoutSubclass timeout = new TimeoutTest.TimeoutSubclass(1, TimeUnit.SECONDS);

        @Test
        public void test() {
            Assert.assertEquals(1000, timeout.getTimeoutFromSuperclass(TimeUnit.MILLISECONDS));
        }
    }

    @Test
    public void testGetTimeout() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutTest.TimeOutOneSecond.class);
        Assert.assertEquals("Should run the test", 1, result.getRunCount());
        Assert.assertEquals("Test should not have failed", 0, result.getFailureCount());
    }
}

