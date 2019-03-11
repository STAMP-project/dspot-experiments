package org.junit.tests.experimental.theories.runner;


import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


public class TypeMatchingBetweenMultiDataPointsMethod {
    @RunWith(Theories.class)
    public static class WithWrongfullyTypedDataPointsMethod {
        @DataPoint
        public static String[] correctlyTyped = new String[]{ "Good", "Morning" };

        @DataPoints
        public static String[] wrongfullyTyped() {
            return new String[]{ "Hello", "World" };
        }

        @Theory
        public void testTheory(String[] array) {
        }
    }

    @Test
    public void ignoreWrongTypedDataPointsMethod() {
        Assert.assertThat(PrintableResult.testResult(TypeMatchingBetweenMultiDataPointsMethod.WithWrongfullyTypedDataPointsMethod.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class WithCorrectlyTypedDataPointsMethod {
        @DataPoint
        public static String[] correctlyTyped = new String[]{ "Good", "Morning" };

        @DataPoints
        public static String[][] anotherCorrectlyTyped() {
            return new String[][]{ new String[]{ "Hello", "World" } };
        }

        @Theory
        public void testTheory(String[] array) {
        }
    }

    @Test
    public void pickUpMultiPointDataPointMethods() throws Exception {
        Assert.assertThat(PrintableResult.testResult(TypeMatchingBetweenMultiDataPointsMethod.WithCorrectlyTypedDataPointsMethod.class), ResultMatchers.isSuccessful());
    }
}

