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
import org.junit.runners.model.TestClass;


public class UnsuccessfulWithDataPointFields {
    @RunWith(Theories.class)
    public static class HasAFailingTheory {
        @DataPoint
        public static int ONE = 1;

        @Theory
        public void everythingIsZero(int x) {
            Assert.assertThat(x, CoreMatchers.is(0));
        }
    }

    @Test
    public void theoryClassMethodsShowUp() throws Exception {
        Assert.assertThat(new Theories(UnsuccessfulWithDataPointFields.HasAFailingTheory.class).getDescription().getChildren().size(), CoreMatchers.is(1));
    }

    @Test
    public void theoryAnnotationsAreRetained() throws Exception {
        Assert.assertThat(new TestClass(UnsuccessfulWithDataPointFields.HasAFailingTheory.class).getAnnotatedMethods(Theory.class).size(), CoreMatchers.is(1));
    }

    @Test
    public void canRunTheories() throws Exception {
        Assert.assertThat(PrintableResult.testResult(UnsuccessfulWithDataPointFields.HasAFailingTheory.class), ResultMatchers.hasSingleFailureContaining("Expected"));
    }

    @RunWith(Theories.class)
    public static class DoesntUseParams {
        @DataPoint
        public static int ONE = 1;

        @Theory
        public void everythingIsZero(int x, int y) {
            Assert.assertThat(2, CoreMatchers.is(3));
        }
    }

    @Test
    public void reportBadParams() throws Exception {
        Assert.assertThat(PrintableResult.testResult(UnsuccessfulWithDataPointFields.DoesntUseParams.class), ResultMatchers.hasSingleFailureContaining("everythingIsZero(\"1\" <from ONE>, \"1\" <from ONE>)"));
    }

    @RunWith(Theories.class)
    public static class NullsOK {
        @DataPoint
        public static String NULL = null;

        @DataPoint
        public static String A = "A";

        @Theory
        public void everythingIsA(String a) {
            Assert.assertThat(a, CoreMatchers.is("A"));
        }
    }

    @Test
    public void nullsUsedUnlessProhibited() throws Exception {
        Assert.assertThat(PrintableResult.testResult(UnsuccessfulWithDataPointFields.NullsOK.class), ResultMatchers.hasSingleFailureContaining("null"));
    }

    @RunWith(Theories.class)
    public static class TheoriesMustBePublic {
        @DataPoint
        public static int THREE = 3;

        @Theory
        void numbers(int x) {
        }
    }

    @Test
    public void theoriesMustBePublic() {
        Assert.assertThat(PrintableResult.testResult(UnsuccessfulWithDataPointFields.TheoriesMustBePublic.class), ResultMatchers.hasSingleFailureContaining("public"));
    }

    @RunWith(Theories.class)
    public static class DataPointFieldsMustBeStatic {
        @DataPoint
        public int THREE = 3;

        @DataPoints
        public int[] FOURS = new int[]{ 4 };

        @Theory
        public void numbers(int x) {
        }
    }

    @Test
    public void dataPointFieldsMustBeStatic() {
        Assert.assertThat(PrintableResult.testResult(UnsuccessfulWithDataPointFields.DataPointFieldsMustBeStatic.class), CoreMatchers.<PrintableResult>both(ResultMatchers.hasFailureContaining("DataPoint field THREE must be static")).and(ResultMatchers.hasFailureContaining("DataPoint field FOURS must be static")));
    }

    @RunWith(Theories.class)
    public static class DataPointMethodsMustBeStatic {
        @DataPoint
        public int singleDataPointMethod() {
            return 1;
        }

        @DataPoints
        public int[] dataPointArrayMethod() {
            return new int[]{ 1, 2, 3 };
        }

        @Theory
        public void numbers(int x) {
        }
    }

    @Test
    public void dataPointMethodsMustBeStatic() {
        Assert.assertThat(PrintableResult.testResult(UnsuccessfulWithDataPointFields.DataPointMethodsMustBeStatic.class), CoreMatchers.<PrintableResult>both(ResultMatchers.hasFailureContaining("DataPoint method singleDataPointMethod must be static")).and(ResultMatchers.hasFailureContaining("DataPoint method dataPointArrayMethod must be static")));
    }

    @RunWith(Theories.class)
    public static class DataPointFieldsMustBePublic {
        @DataPoint
        static int THREE = 3;

        @DataPoints
        static int[] THREES = new int[]{ 3 };

        @DataPoint
        protected static int FOUR = 4;

        @DataPoints
        protected static int[] FOURS = new int[]{ 4 };

        @DataPoint
        private static int FIVE = 5;

        @DataPoints
        private static int[] FIVES = new int[]{ 5 };

        @Theory
        public void numbers(int x) {
        }
    }

    @Test
    public void dataPointFieldsMustBePublic() {
        PrintableResult result = PrintableResult.testResult(UnsuccessfulWithDataPointFields.DataPointFieldsMustBePublic.class);
        Assert.assertThat(result, CoreMatchers.allOf(ResultMatchers.hasFailureContaining("DataPoint field THREE must be public"), ResultMatchers.hasFailureContaining("DataPoint field THREES must be public"), ResultMatchers.hasFailureContaining("DataPoint field FOUR must be public"), ResultMatchers.hasFailureContaining("DataPoint field FOURS must be public"), ResultMatchers.hasFailureContaining("DataPoint field FIVE must be public"), ResultMatchers.hasFailureContaining("DataPoint field FIVES must be public")));
    }

    @RunWith(Theories.class)
    public static class DataPointMethodsMustBePublic {
        @DataPoint
        static int three() {
            return 3;
        }

        @DataPoints
        static int[] threes() {
            return new int[]{ 3 };
        }

        @DataPoint
        protected static int four() {
            return 4;
        }

        @DataPoints
        protected static int[] fours() {
            return new int[]{ 4 };
        }

        @DataPoint
        private static int five() {
            return 5;
        }

        @DataPoints
        private static int[] fives() {
            return new int[]{ 5 };
        }

        @Theory
        public void numbers(int x) {
        }
    }

    @Test
    public void dataPointMethodsMustBePublic() {
        PrintableResult result = PrintableResult.testResult(UnsuccessfulWithDataPointFields.DataPointMethodsMustBePublic.class);
        Assert.assertThat(result, CoreMatchers.allOf(ResultMatchers.hasFailureContaining("DataPoint method three must be public"), ResultMatchers.hasFailureContaining("DataPoint method threes must be public"), ResultMatchers.hasFailureContaining("DataPoint method four must be public"), ResultMatchers.hasFailureContaining("DataPoint method fours must be public"), ResultMatchers.hasFailureContaining("DataPoint method five must be public"), ResultMatchers.hasFailureContaining("DataPoint method fives must be public")));
    }
}

