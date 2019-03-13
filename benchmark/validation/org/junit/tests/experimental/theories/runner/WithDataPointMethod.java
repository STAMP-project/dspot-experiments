package org.junit.tests.experimental.theories.runner;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.tests.experimental.theories.TheoryTestUtils;


public class WithDataPointMethod {
    @RunWith(Theories.class)
    public static class HasDataPointMethod {
        @DataPoint
        public static int oneHundred() {
            return 100;
        }

        @Theory
        public void allIntsOk(int x) {
        }
    }

    @Test
    public void pickUpDataPointMethods() {
        Assert.assertThat(PrintableResult.testResult(WithDataPointMethod.HasDataPointMethod.class), ResultMatchers.isSuccessful());
    }

    @RunWith(Theories.class)
    public static class DataPointMethodReturnsMutableObject {
        @DataPoint
        public static List<Object> empty() {
            return new ArrayList<Object>();
        }

        @DataPoint
        public static int ONE = 1;

        @DataPoint
        public static int TWO = 2;

        @Theory
        public void everythingsEmpty(List<Object> first, int number) {
            Assert.assertThat(first.size(), CoreMatchers.is(0));
            first.add("a");
        }
    }

    @Test
    public void mutableObjectsAreCreatedAfresh() {
        Assert.assertThat(failures(WithDataPointMethod.DataPointMethodReturnsMutableObject.class), empty());
    }

    @RunWith(Theories.class)
    public static class HasDateMethod {
        @DataPoint
        public static int oneHundred() {
            return 100;
        }

        public static Date notADataPoint() {
            return new Date();
        }

        @Theory
        public void allIntsOk(int x) {
        }

        @Theory
        public void onlyStringsOk(String s) {
        }

        @Theory
        public void onlyDatesOk(Date d) {
        }
    }

    @Test
    public void ignoreDataPointMethodsWithWrongTypes() throws Throwable {
        Assert.assertThat(TheoryTestUtils.potentialAssignments(WithDataPointMethod.HasDateMethod.class.getMethod("onlyStringsOk", String.class)).toString(), CoreMatchers.not(CoreMatchers.containsString("100")));
    }

    @Test
    public void ignoreDataPointMethodsWithoutAnnotation() throws Throwable {
        Assert.assertThat(TheoryTestUtils.potentialAssignments(WithDataPointMethod.HasDateMethod.class.getMethod("onlyDatesOk", Date.class)).size(), CoreMatchers.is(0));
    }
}

