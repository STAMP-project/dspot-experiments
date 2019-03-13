package org.junit.internal.runners.statements;


import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestTimedOutException;


/**
 *
 *
 * @author Asaf Ary, Stefan Birkner
 */
public class FailOnTimeoutTest {
    private static final long TIMEOUT = 100;

    private static final long DURATION_THAT_EXCEEDS_TIMEOUT = (60 * 60) * 1000;// 1 hour


    private final FailOnTimeoutTest.TestStatement statement = new FailOnTimeoutTest.TestStatement();

    private final FailOnTimeout failOnTimeout = FailOnTimeout.builder().withTimeout(FailOnTimeoutTest.TIMEOUT, TimeUnit.MILLISECONDS).build(statement);

    @Test
    public void throwsTestTimedOutException() {
        Assert.assertThrows(TestTimedOutException.class, evaluateWithWaitDuration(FailOnTimeoutTest.DURATION_THAT_EXCEEDS_TIMEOUT));
    }

    @Test
    public void throwExceptionWithNiceMessageOnTimeout() {
        TestTimedOutException e = Assert.assertThrows(TestTimedOutException.class, evaluateWithWaitDuration(FailOnTimeoutTest.DURATION_THAT_EXCEEDS_TIMEOUT));
        Assert.assertEquals("test timed out after 100 milliseconds", e.getMessage());
    }

    @Test
    public void sendUpExceptionThrownByStatement() {
        RuntimeException exception = new RuntimeException();
        RuntimeException e = Assert.assertThrows(RuntimeException.class, evaluateWithException(exception));
        Assert.assertSame(exception, e);
    }

    @Test
    public void throwExceptionIfTheSecondCallToEvaluateNeedsTooMuchTime() throws Throwable {
        evaluateWithWaitDuration(0).run();
        Assert.assertThrows(TestTimedOutException.class, evaluateWithWaitDuration(FailOnTimeoutTest.DURATION_THAT_EXCEEDS_TIMEOUT));
    }

    @Test
    public void throwTimeoutExceptionOnSecondCallAlthoughFirstCallThrowsException() {
        try {
            evaluateWithException(new RuntimeException()).run();
        } catch (Throwable expected) {
        }
        TestTimedOutException e = Assert.assertThrows(TestTimedOutException.class, evaluateWithWaitDuration(FailOnTimeoutTest.DURATION_THAT_EXCEEDS_TIMEOUT));
        Assert.assertEquals("test timed out after 100 milliseconds", e.getMessage());
    }

    @Test
    public void throwsExceptionWithTimeoutValueAndTimeUnitSet() {
        TestTimedOutException e = Assert.assertThrows(TestTimedOutException.class, evaluateWithWaitDuration(FailOnTimeoutTest.DURATION_THAT_EXCEEDS_TIMEOUT));
        Assert.assertEquals(FailOnTimeoutTest.TIMEOUT, e.getTimeout());
        Assert.assertEquals(TimeUnit.MILLISECONDS, e.getTimeUnit());
    }

    private static final class TestStatement extends Statement {
        long waitDuration;

        Exception nextException;

        @Override
        public void evaluate() throws Throwable {
            Thread.sleep(waitDuration);
            if ((nextException) != null) {
                throw nextException;
            }
        }
    }

    @Test
    public void stopEndlessStatement() throws Throwable {
        FailOnTimeoutTest.InfiniteLoopStatement infiniteLoop = new FailOnTimeoutTest.InfiniteLoopStatement();
        FailOnTimeout infiniteLoopTimeout = FailOnTimeout.builder().withTimeout(FailOnTimeoutTest.TIMEOUT, TimeUnit.MILLISECONDS).build(infiniteLoop);
        try {
            infiniteLoopTimeout.evaluate();
        } catch (Exception timeoutException) {
            Thread.sleep(20);// time to interrupt the thread

            int firstCount = FailOnTimeoutTest.InfiniteLoopStatement.COUNT;
            Thread.sleep(20);// time to increment the count

            Assert.assertTrue("Thread has not been stopped.", (firstCount == (FailOnTimeoutTest.InfiniteLoopStatement.COUNT)));
        }
    }

    private static final class InfiniteLoopStatement extends Statement {
        private static int COUNT = 0;

        @Override
        public void evaluate() throws Throwable {
            while (true) {
                Thread.sleep(10);// sleep in order to enable interrupting thread

                ++(FailOnTimeoutTest.InfiniteLoopStatement.COUNT);
            } 
        }
    }

    @Test
    public void stackTraceContainsRealCauseOfTimeout() throws Throwable {
        FailOnTimeoutTest.StuckStatement stuck = new FailOnTimeoutTest.StuckStatement();
        FailOnTimeout stuckTimeout = FailOnTimeout.builder().withTimeout(FailOnTimeoutTest.TIMEOUT, TimeUnit.MILLISECONDS).build(stuck);
        try {
            stuckTimeout.evaluate();
            // We must not get here, we expect a timeout exception
            Assert.fail("Expected timeout exception");
        } catch (Exception timeoutException) {
            StackTraceElement[] stackTrace = timeoutException.getStackTrace();
            boolean stackTraceContainsTheRealCauseOfTheTimeout = false;
            boolean stackTraceContainsOtherThanTheRealCauseOfTheTimeout = false;
            for (StackTraceElement element : stackTrace) {
                String methodName = element.getMethodName();
                if ("theRealCauseOfTheTimeout".equals(methodName)) {
                    stackTraceContainsTheRealCauseOfTheTimeout = true;
                }
                if ("notTheRealCauseOfTheTimeout".equals(methodName)) {
                    stackTraceContainsOtherThanTheRealCauseOfTheTimeout = true;
                }
            }
            Assert.assertTrue("Stack trace does not contain the real cause of the timeout", stackTraceContainsTheRealCauseOfTheTimeout);
            Assert.assertFalse("Stack trace contains other than the real cause of the timeout, which can be very misleading", stackTraceContainsOtherThanTheRealCauseOfTheTimeout);
        }
    }

    private static final class StuckStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {
            try {
                // Must show up in stack trace
                theRealCauseOfTheTimeout();
            } catch (InterruptedException e) {
            } finally {
                // Must _not_ show up in stack trace
                notTheRealCauseOfTheTimeout();
            }
        }

        private void theRealCauseOfTheTimeout() throws InterruptedException {
            Thread.sleep(Long.MAX_VALUE);
        }

        private void notTheRealCauseOfTheTimeout() {
            for (long now = System.currentTimeMillis(), eta = now + 1000L; now < eta; now = System.currentTimeMillis()) {
                // Doesn't matter, just pretend to be busy
                Math.atan(now);
            }
        }
    }

    @Test
    public void threadGroupNotLeaked() throws Throwable {
        Collection<ThreadGroup> groupsBeforeSet = subGroupsOfCurrentThread();
        evaluateWithWaitDuration(0);
        for (ThreadGroup group : subGroupsOfCurrentThread()) {
            if ((!(groupsBeforeSet.contains(group))) && ("FailOnTimeoutGroup".equals(group.getName()))) {
                Assert.fail("A 'FailOnTimeoutGroup' thread group remains referenced after the test execution.");
            }
        }
    }
}

