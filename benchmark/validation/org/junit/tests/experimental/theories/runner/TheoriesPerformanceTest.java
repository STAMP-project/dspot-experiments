package org.junit.tests.experimental.theories.runner;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


public class TheoriesPerformanceTest {
    @RunWith(Theories.class)
    public static class UpToTen {
        @DataPoints
        public static int[] ints = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        @Theory
        public void threeInts(int x, int y, int z) {
            // pass always
        }
    }

    private static final boolean TESTING_PERFORMANCE = false;

    // If we do not share the same instance of TestClass, repeatedly parsing the
    // class's annotations looking for @Befores and @Afters gets really costly.
    // 
    // Likewise, the TestClass must be passed into AllMembersSupplier, or the
    // annotation parsing is again costly.
    @Test
    public void tryCombinationsQuickly() {
        Assume.assumeTrue(TheoriesPerformanceTest.TESTING_PERFORMANCE);
        Assert.assertThat(PrintableResult.testResult(TheoriesPerformanceTest.UpToTen.class), ResultMatchers.isSuccessful());
    }
}

