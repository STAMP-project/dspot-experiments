package org.junit.tests.experimental.theories;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.suppliers.TestedOnSupplier;


public class TestedOnSupplierTest {
    @Test
    public void descriptionStatesParameterName() throws Exception {
        TestedOnSupplier supplier = new TestedOnSupplier();
        List<PotentialAssignment> assignments = supplier.getValueSources(signatureOfFoo());
        Assert.assertThat(assignments.get(0).getDescription(), CoreMatchers.is("\"1\" <from ints>"));
    }
}

