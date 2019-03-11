package org.junit.tests.running.methods;


import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class ExpectedTest {
    public static class Expected {
        @Test(expected = Exception.class)
        public void expected() throws Exception {
            throw new Exception();
        }
    }

    @Test
    public void expected() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(ExpectedTest.Expected.class);
        Assert.assertTrue(result.wasSuccessful());
    }

    public static class Unexpected {
        @Test(expected = Exception.class)
        public void expected() throws Exception {
            throw new Error();
        }
    }

    @Test
    public void unexpected() {
        Result result = JUnitCore.runClasses(ExpectedTest.Unexpected.class);
        Failure failure = result.getFailures().get(0);
        String message = failure.getMessage();
        Assert.assertTrue(message.contains("expected<java.lang.Exception> but was<java.lang.Error>"));
        Assert.assertEquals(Error.class, failure.getException().getCause().getClass());
    }

    public static class NoneThrown {
        @Test(expected = Exception.class)
        public void nothing() {
        }
    }

    @Test
    public void noneThrown() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(ExpectedTest.NoneThrown.class);
        Assert.assertFalse(result.wasSuccessful());
        String message = result.getFailures().get(0).getMessage();
        Assert.assertTrue(message.contains("Expected exception: java.lang.Exception"));
    }

    public static class ExpectSuperclass {
        @Test(expected = RuntimeException.class)
        public void throwsSubclass() {
            throw new ClassCastException();
        }
    }

    @Test
    public void expectsSuperclass() {
        Assert.assertTrue(new JUnitCore().run(ExpectedTest.ExpectSuperclass.class).wasSuccessful());
    }

    public static class ExpectAssumptionViolatedException {
        @Test(expected = AssumptionViolatedException.class)
        public void throwsAssumptionViolatedException() {
            throw new AssumptionViolatedException("expected");
        }
    }

    @Test
    public void expectsAssumptionViolatedException() {
        Assert.assertTrue(new JUnitCore().run(ExpectedTest.ExpectAssumptionViolatedException.class).wasSuccessful());
    }
}

