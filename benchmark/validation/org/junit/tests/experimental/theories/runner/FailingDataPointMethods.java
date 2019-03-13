package org.junit.tests.experimental.theories.runner;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


public class FailingDataPointMethods {
    @RunWith(Theories.class)
    public static class HasFailingSingleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldFailFromExceptionsInSingleDataPointMethods() {
        Assert.assertThat(PrintableResult.testResult(FailingDataPointMethods.HasWronglyIgnoredFailingSingleDataPointMethod.class), CoreMatchers.not(ResultMatchers.isSuccessful()));
    }

    @RunWith(Theories.class)
    public static class HasFailingDataPointArrayMethod {
        @DataPoints
        public static int[] num = new int[]{ 1, 2, 3 };

        @DataPoints
        public static int[] failingDataPoints() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldFailFromExceptionsInDataPointArrayMethods() {
        Assert.assertThat(PrintableResult.testResult(FailingDataPointMethods.HasFailingDataPointArrayMethod.class), CoreMatchers.not(ResultMatchers.isSuccessful()));
    }

    @RunWith(Theories.class)
    public static class HasIgnoredFailingSingleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint(ignoredExceptions = Throwable.class)
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldIgnoreSingleDataPointMethodExceptionsOnRequest() {
        Assert.assertThat(PrintableResult.testResult(FailingDataPointMethods.HasIgnoredFailingSingleDataPointMethod.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class HasIgnoredFailingMultipleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoints(ignoredExceptions = Throwable.class)
        public static int[] failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldIgnoreMultipleDataPointMethodExceptionsOnRequest() {
        Assert.assertThat(PrintableResult.testResult(FailingDataPointMethods.HasIgnoredFailingMultipleDataPointMethod.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class HasWronglyIgnoredFailingSingleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint(ignoredExceptions = NullPointerException.class)
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldNotIgnoreNonMatchingSingleDataPointExceptions() {
        Assert.assertThat(PrintableResult.testResult(FailingDataPointMethods.HasWronglyIgnoredFailingSingleDataPointMethod.class), CoreMatchers.not(ResultMatchers.isSuccessful()));
    }

    @RunWith(Theories.class)
    public static class HasWronglyIgnoredFailingMultipleDataPointMethod {
        @DataPoint
        public static int num = 10;

        @DataPoint(ignoredExceptions = NullPointerException.class)
        public static int failingDataPoint() {
            throw new RuntimeException();
        }

        @Theory
        public void theory(int x) {
        }
    }

    @Test
    public void shouldNotIgnoreNonMatchingMultipleDataPointExceptions() {
        Assert.assertThat(PrintableResult.testResult(FailingDataPointMethods.HasWronglyIgnoredFailingMultipleDataPointMethod.class), CoreMatchers.not(ResultMatchers.isSuccessful()));
    }
}

