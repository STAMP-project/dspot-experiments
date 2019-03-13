package org.junit.tests.experimental.theories.runner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.junit.tests.experimental.theories.TheoryTestUtils;


public class WithParameterSupplier {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private static class SimplePotentialAssignment extends PotentialAssignment {
        private String description;

        private Object value;

        public SimplePotentialAssignment(Object value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public Object getValue() throws PotentialAssignment.CouldNotGenerateValueException {
            return value;
        }

        @Override
        public String getDescription() throws PotentialAssignment.CouldNotGenerateValueException {
            return description;
        }
    }

    private static final List<String> DATAPOINTS = Arrays.asList("qwe", "asd");

    public static class SimpleSupplier extends ParameterSupplier {
        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            List<PotentialAssignment> assignments = new ArrayList<PotentialAssignment>();
            for (String datapoint : WithParameterSupplier.DATAPOINTS) {
                assignments.add(new WithParameterSupplier.SimplePotentialAssignment(datapoint, datapoint));
            }
            return assignments;
        }
    }

    @RunWith(Theories.class)
    public static class TestClassUsingParameterSupplier {
        @Theory
        public void theoryMethod(@ParametersSuppliedBy(WithParameterSupplier.SimpleSupplier.class)
        String param) {
        }
    }

    @Test
    public void shouldPickUpDataPointsFromParameterSupplier() throws Throwable {
        List<PotentialAssignment> assignments = TheoryTestUtils.potentialAssignments(WithParameterSupplier.TestClassUsingParameterSupplier.class.getMethod("theoryMethod", String.class));
        Assert.assertEquals(2, assignments.size());
        Assert.assertEquals(WithParameterSupplier.DATAPOINTS.get(0), assignments.get(0).getValue());
        Assert.assertEquals(WithParameterSupplier.DATAPOINTS.get(1), assignments.get(1).getValue());
    }

    public static class SupplierWithUnknownConstructor extends ParameterSupplier {
        public SupplierWithUnknownConstructor(String param) {
        }

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            return null;
        }
    }

    @RunWith(Theories.class)
    public static class TestClassUsingSupplierWithUnknownConstructor {
        @Theory
        public void theory(@ParametersSuppliedBy(WithParameterSupplier.SupplierWithUnknownConstructor.class)
        String param) {
        }
    }

    @Test
    public void shouldRejectSuppliersWithUnknownConstructors() throws Exception {
        expected.expect(InitializationError.class);
        new Theories(WithParameterSupplier.TestClassUsingSupplierWithUnknownConstructor.class);
    }

    public static class SupplierWithTwoConstructors extends ParameterSupplier {
        public SupplierWithTwoConstructors(String param) {
        }

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            return null;
        }
    }

    @RunWith(Theories.class)
    public static class TestClassUsingSupplierWithTwoConstructors {
        @Theory
        public void theory(@ParametersSuppliedBy(WithParameterSupplier.SupplierWithTwoConstructors.class)
        String param) {
        }
    }

    @Test
    public void shouldRejectSuppliersWithTwoConstructors() throws Exception {
        expected.expect(InitializationError.class);
        new Theories(WithParameterSupplier.TestClassUsingSupplierWithTwoConstructors.class);
    }

    public static class SupplierWithTestClassConstructor extends ParameterSupplier {
        public SupplierWithTestClassConstructor(TestClass param) {
        }

        @Override
        public List<PotentialAssignment> getValueSources(ParameterSignature sig) {
            return null;
        }
    }

    @RunWith(Theories.class)
    public static class TestClassUsingSupplierWithTestClassConstructor {
        @Theory
        public void theory(@ParametersSuppliedBy(WithParameterSupplier.SupplierWithTestClassConstructor.class)
        String param) {
        }
    }

    @Test
    public void shouldAcceptSuppliersWithTestClassConstructor() throws Exception {
        new Theories(WithParameterSupplier.TestClassUsingSupplierWithTestClassConstructor.class);
    }
}

