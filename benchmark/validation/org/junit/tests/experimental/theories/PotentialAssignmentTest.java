package org.junit.tests.experimental.theories;


import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.PotentialAssignment;


public class PotentialAssignmentTest {
    @Test
    public void shouldUseQuotedValueInDescription() throws PotentialAssignment.CouldNotGenerateValueException {
        String name = "stringDatapoint";
        Object value = new Object() {
            @Override
            public String toString() {
                return "string value";
            }
        };
        PotentialAssignment assignment = PotentialAssignment.forValue(name, value);
        Assert.assertEquals("\"string value\" <from stringDatapoint>", assignment.getDescription());
    }

    @Test
    public void shouldNotUseQuotesForNullValueDescriptions() throws PotentialAssignment.CouldNotGenerateValueException {
        String name = "nullDatapoint";
        Object value = null;
        PotentialAssignment assignment = PotentialAssignment.forValue(name, value);
        Assert.assertEquals("null <from nullDatapoint>", assignment.getDescription());
    }

    @Test
    public void shouldIncludeFailureInDescriptionIfToStringFails() throws PotentialAssignment.CouldNotGenerateValueException {
        String name = "explodingValue";
        Object value = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Oh no!");
            }
        };
        PotentialAssignment assignment = PotentialAssignment.forValue(name, value);
        Assert.assertEquals("[toString() threw RuntimeException: Oh no!] <from explodingValue>", assignment.getDescription());
    }

    @Test
    public void shouldReturnGivenValue() throws PotentialAssignment.CouldNotGenerateValueException {
        Object value = new Object();
        PotentialAssignment assignment = PotentialAssignment.forValue("name", value);
        Assert.assertEquals(value, assignment.getValue());
    }
}

