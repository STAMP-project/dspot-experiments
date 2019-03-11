package org.junit.tests.experimental.theories.runner;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;


public class WithOnlyTestAnnotations {
    @RunWith(Theories.class)
    public static class HonorExpectedException {
        @Test(expected = NullPointerException.class)
        public void shouldThrow() {
        }
    }

    @Test
    public void honorExpected() throws Exception {
        Assert.assertThat(PrintableResult.testResult(WithOnlyTestAnnotations.HonorExpectedException.class).failureCount(), CoreMatchers.is(1));
    }

    @RunWith(Theories.class)
    public static class HonorExpectedExceptionPasses {
        @Test(expected = NullPointerException.class)
        public void shouldThrow() {
            throw new NullPointerException();
        }
    }

    @Test
    public void honorExpectedPassing() throws Exception {
        Assert.assertThat(PrintableResult.testResult(WithOnlyTestAnnotations.HonorExpectedExceptionPasses.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class HonorTimeout {
        @Test(timeout = 5)
        public void shouldStop() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            } 
        }
    }

    @Test
    public void honorTimeout() throws Exception {
        Assert.assertThat(PrintableResult.testResult(WithOnlyTestAnnotations.HonorTimeout.class), ResultMatchers.failureCountIs(1));
    }

    @RunWith(Theories.class)
    public static class ErrorWhenTestHasParametersDespiteTheories {
        @DataPoint
        public static int ZERO = 0;

        @Test
        public void testMethod(int i) {
        }
    }

    @Test
    public void testErrorWhenTestHasParametersDespiteTheories() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(WithOnlyTestAnnotations.ErrorWhenTestHasParametersDespiteTheories.class);
        Assert.assertEquals(1, result.getFailureCount());
        String message = result.getFailures().get(0).getMessage();
        Assert.assertThat(message, CoreMatchers.containsString("should have no parameters"));
    }
}

