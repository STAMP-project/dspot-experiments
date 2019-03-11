package org.junit.tests.experimental.theories.runner;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.tests.experimental.theories.TheoryTestUtils;


public class WithNamedDataPoints {
    @RunWith(Theories.class)
    public static class HasSpecificDatapointsParameters {
        @DataPoints
        public static String[] badStrings = new String[]{ "bad" };

        @DataPoint
        public static String badString = "also bad";

        @DataPoints("named")
        public static String[] goodStrings = new String[]{ "expected", "also expected" };

        @DataPoint("named")
        public static String goodString = "expected single value";

        @DataPoints("named")
        public static String[] methodStrings() {
            return new String[]{ "expected method value" };
        }

        @DataPoint("named")
        public static String methodString() {
            return "expected single method string";
        }

        @DataPoints
        public static String[] otherMethod() {
            return new String[]{ "other method value" };
        }

        @DataPoint
        public static String otherSingleValueMethod() {
            return "other single value string";
        }

        @Theory
        public void theory(@FromDataPoints("named")
        String param) {
        }
    }

    @Test
    public void onlyUseSpecificDataPointsIfSpecified() throws Throwable {
        List<PotentialAssignment> assignments = TheoryTestUtils.potentialAssignments(WithNamedDataPoints.HasSpecificDatapointsParameters.class.getMethod("theory", String.class));
        Assert.assertEquals(5, assignments.size());
        for (PotentialAssignment assignment : assignments) {
            Assert.assertThat(((String) (assignment.getValue())), CoreMatchers.containsString("expected"));
        }
    }
}

