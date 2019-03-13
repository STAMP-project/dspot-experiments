package org.junit.tests.experimental.theories.internal;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.internal.SpecificDataPointsSupplier;
import org.junit.runners.model.TestClass;


public class SpecificDataPointsSupplierTest {
    public static class TestClassWithNamedDataPoints {
        @DataPoints({ "field", "named" })
        public static String[] values = new String[]{ "named field" };

        @DataPoints
        public static String[] otherValues = new String[]{ "other" };

        @DataPoints({ "method", "named" })
        public static String[] getValues() {
            return new String[]{ "named method" };
        }

        @DataPoint({ "single", "named" })
        public static String singleValue = "named single value";

        @DataPoint
        public static String otherSingleValue = "other value";

        @DataPoint({ "singlemethod", "named" })
        public static String getSingleValue() {
            return "named single method value";
        }

        @DataPoint
        public static String getSingleOtherValue() {
            return "other single method value";
        }

        @DataPoints
        public static String[] getOtherValues() {
            return new String[]{ "other method" };
        }
    }

    @Test
    public void shouldReturnOnlyTheNamedDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(SpecificDataPointsSupplierTest.TestClassWithNamedDataPoints.class));
        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingAllNamedStrings"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);
        Assert.assertEquals(4, assignedStrings.size());
        Assert.assertThat(assignedStrings, CoreMatchers.hasItems("named field", "named method", "named single value", "named single method value"));
    }

    @Test
    public void shouldReturnOnlyTheNamedFieldDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(SpecificDataPointsSupplierTest.TestClassWithNamedDataPoints.class));
        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedFieldString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);
        Assert.assertEquals(1, assignedStrings.size());
        Assert.assertThat(assignedStrings, CoreMatchers.hasItem("named field"));
    }

    @Test
    public void shouldReturnOnlyTheNamedMethodDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(SpecificDataPointsSupplierTest.TestClassWithNamedDataPoints.class));
        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedMethodString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);
        Assert.assertEquals(1, assignedStrings.size());
        Assert.assertThat(assignedStrings, CoreMatchers.hasItem("named method"));
    }

    @Test
    public void shouldReturnOnlyTheNamedSingleFieldDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(SpecificDataPointsSupplierTest.TestClassWithNamedDataPoints.class));
        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedSingleFieldString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);
        Assert.assertEquals(1, assignedStrings.size());
        Assert.assertThat(assignedStrings, CoreMatchers.hasItem("named single value"));
    }

    @Test
    public void shouldReturnOnlyTheNamedSingleMethodDataPoints() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(SpecificDataPointsSupplierTest.TestClassWithNamedDataPoints.class));
        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingNamedSingleMethodString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);
        Assert.assertEquals(1, assignedStrings.size());
        Assert.assertThat(assignedStrings, CoreMatchers.hasItem("named single method value"));
    }

    @Test
    public void shouldReturnNothingIfTheNamedDataPointsAreMissing() throws Throwable {
        SpecificDataPointsSupplier supplier = new SpecificDataPointsSupplier(new TestClass(SpecificDataPointsSupplierTest.TestClassWithNamedDataPoints.class));
        List<PotentialAssignment> assignments = supplier.getValueSources(signature("methodWantingWrongNamedString"));
        List<String> assignedStrings = getStringValuesFromAssignments(assignments);
        Assert.assertEquals(0, assignedStrings.size());
    }
}

