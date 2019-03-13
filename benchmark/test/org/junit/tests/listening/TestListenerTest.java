package org.junit.tests.listening;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


public class TestListenerTest {
    int count;

    class ErrorListener extends RunListener {
        @Override
        public void testRunStarted(Description description) throws Exception {
            throw new Error();
        }
    }

    public static class OneTest {
        @Test
        public void nothing() {
        }
    }

    @Test(expected = Error.class)
    public void failingListener() {
        JUnitCore runner = new JUnitCore();
        runner.addListener(new TestListenerTest.ErrorListener());
        runner.run(TestListenerTest.OneTest.class);
    }

    class ExceptionListener extends TestListenerTest.ErrorListener {
        @Override
        public void testRunStarted(Description description) throws Exception {
            (count)++;
            throw new Exception();
        }
    }

    @Test
    public void reportsFailureOfListener() {
        JUnitCore core = new JUnitCore();
        core.addListener(new TestListenerTest.ExceptionListener());
        count = 0;
        Result result = core.run(TestListenerTest.OneTest.class);
        Assert.assertEquals(1, count);
        Assert.assertEquals(1, result.getFailureCount());
        Failure testFailure = result.getFailures().get(0);
        Assert.assertEquals(Description.TEST_MECHANISM, testFailure.getDescription());
    }

    @Test
    public void freshResultEachTime() {
        JUnitCore core = new JUnitCore();
        Result first = core.run(TestListenerTest.OneTest.class);
        Result second = core.run(TestListenerTest.OneTest.class);
        Assert.assertNotSame(first, second);
    }
}

