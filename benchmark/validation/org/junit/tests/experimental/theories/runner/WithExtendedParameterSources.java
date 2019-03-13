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
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;


public class WithExtendedParameterSources {
    @RunWith(Theories.class)
    public static class ParameterAnnotations {
        @Theory
        public void everythingIsOne(@TestedOn(ints = { 1 })
        int number) {
            Assert.assertThat(number, CoreMatchers.is(1));
        }
    }

    @Test
    public void testedOnLimitsParameters() throws Exception {
        Assert.assertThat(PrintableResult.testResult(WithExtendedParameterSources.ParameterAnnotations.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class ShouldFilterOutNullSingleDataPoints {
        @DataPoint
        public static String A = "a";

        @DataPoint
        public static String NULL = null;

        @Theory(nullsAccepted = false)
        public void allStringsAreNonNull(String s) {
            Assert.assertThat(s, CoreMatchers.notNullValue());
        }
    }

    @Test
    public void shouldFilterOutNullSingleDataPoints() {
        Assert.assertThat(PrintableResult.testResult(WithExtendedParameterSources.ShouldFilterOutNullSingleDataPoints.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class ShouldFilterOutNullElementsFromDataPointArrays {
        @DataPoints
        public static String[] SOME_NULLS = new String[]{ "non-null", null };

        @Theory(nullsAccepted = false)
        public void allStringsAreNonNull(String s) {
            Assert.assertThat(s, CoreMatchers.notNullValue());
        }
    }

    @Test
    public void shouldFilterOutNullElementsFromDataPointArrays() {
        Assert.assertThat(PrintableResult.testResult(WithExtendedParameterSources.ShouldFilterOutNullElementsFromDataPointArrays.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class ShouldRejectTheoriesWithOnlyDisallowedNullData {
        @DataPoints
        public static String value = null;

        @Theory(nullsAccepted = false)
        public void allStringsAreNonNull(String s) {
        }
    }

    @Test
    public void ShouldRejectTheoriesWithOnlyDisallowedNullData() {
        Assert.assertThat(PrintableResult.testResult(WithExtendedParameterSources.ShouldRejectTheoriesWithOnlyDisallowedNullData.class), CoreMatchers.not(ResultMatchers.isSuccessful()));
    }

    @RunWith(Theories.class)
    public static class DataPointArrays {
        public static String log = "";

        @DataPoints
        public static String[] STRINGS = new String[]{ "A", "B" };

        @Theory
        public void addToLog(String string) {
            WithExtendedParameterSources.DataPointArrays.log += string;
        }
    }

    @Test
    public void getDataPointsFromArray() {
        WithExtendedParameterSources.DataPointArrays.log = "";
        JUnitCore.runClasses(WithExtendedParameterSources.DataPointArrays.class);
        Assert.assertThat(WithExtendedParameterSources.DataPointArrays.log, CoreMatchers.is("AB"));
    }

    @RunWith(Theories.class)
    public static class DataPointArrayMethod {
        public static String log = "";

        @DataPoints
        public static String[] STRINGS() {
            return new String[]{ "A", "B" };
        }

        @Theory
        public void addToLog(String string) {
            WithExtendedParameterSources.DataPointArrayMethod.log += string;
        }
    }

    @Test
    public void getDataPointsFromArrayMethod() {
        WithExtendedParameterSources.DataPointArrayMethod.log = "";
        JUnitCore.runClasses(WithExtendedParameterSources.DataPointArrayMethod.class);
        Assert.assertThat(WithExtendedParameterSources.DataPointArrayMethod.log, CoreMatchers.is("AB"));
    }

    @RunWith(Theories.class)
    public static class DataPointMalformedArrayMethods {
        public static String log = "";

        @DataPoints
        public static String[] STRINGS() {
            return new String[]{ "A", "B" };
        }

        @DataPoints
        public static String STRING() {
            return "C";
        }

        @DataPoints
        public static int[] INTS() {
            return new int[]{ 1, 2, 3 };
        }

        @Theory
        public void addToLog(String string) {
            WithExtendedParameterSources.DataPointMalformedArrayMethods.log += string;
        }
    }

    @Test
    public void getDataPointsFromArrayMethodInSpiteOfMalformedness() {
        WithExtendedParameterSources.DataPointArrayMethod.log = "";
        JUnitCore.runClasses(WithExtendedParameterSources.DataPointArrayMethod.class);
        Assert.assertThat(WithExtendedParameterSources.DataPointArrayMethod.log, CoreMatchers.is("AB"));
    }

    @RunWith(Theories.class)
    public static class DataPointArrayToBeUsedForWholeParameter {
        public static String log = "";

        @DataPoint
        public static String[] STRINGS = new String[]{ "A", "B" };

        @Theory
        public void addToLog(String[] strings) {
            WithExtendedParameterSources.DataPointArrayToBeUsedForWholeParameter.log += strings[0];
        }
    }

    @Test
    public void dataPointCanBeArray() {
        WithExtendedParameterSources.DataPointArrayToBeUsedForWholeParameter.log = "";
        JUnitCore.runClasses(WithExtendedParameterSources.DataPointArrayToBeUsedForWholeParameter.class);
        Assert.assertThat(WithExtendedParameterSources.DataPointArrayToBeUsedForWholeParameter.log, CoreMatchers.is("A"));
    }
}

