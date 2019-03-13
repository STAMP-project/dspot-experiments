package uk.gov.gchq.gaffer.operation.impl;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.koryphe.ValidationResult;


public class SampleElementsForSplitPointsTest extends OperationTest<SampleElementsForSplitPoints> {
    @Test
    public void shouldFailValidationIfNumSplitsIsLessThan1() {
        // Given
        final SampleElementsForSplitPoints op = new SampleElementsForSplitPoints.Builder<>().numSplits(0).build();
        // When
        final ValidationResult result = op.validate();
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString(), result.getErrorString().contains("numSplits must be null or greater than 0"));
    }

    @Test
    public void shouldFailValidationIfProportionToSampleIsNotIn0_1Range() {
        // Given
        final SampleElementsForSplitPoints op = new SampleElementsForSplitPoints.Builder<>().proportionToSample(1.1F).build();
        // When
        final ValidationResult result = op.validate();
        // Then
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString(), result.getErrorString().contains("proportionToSample must within range: [0, 1]"));
    }

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final SampleElementsForSplitPoints op = new SampleElementsForSplitPoints.Builder<>().numSplits(10).proportionToSample(0.5F).input(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "vertex")).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final SampleElementsForSplitPoints deserialisedOp = JSONSerialiser.deserialise(json, SampleElementsForSplitPoints.class);
        // Then
        Assert.assertEquals(10, ((int) (deserialisedOp.getNumSplits())));
        Assert.assertEquals(0.5F, deserialisedOp.getProportionToSample(), 0.1);
        Assert.assertEquals(Collections.singletonList(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "vertex")), deserialisedOp.getInput());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final SampleElementsForSplitPoints op = new SampleElementsForSplitPoints.Builder<>().numSplits(10).proportionToSample(0.5F).input(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "vertex")).build();
        // Then
        Assert.assertEquals(10, ((int) (op.getNumSplits())));
        Assert.assertEquals(0.5F, op.getProportionToSample(), 0.1);
        Assert.assertEquals(Collections.singletonList(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, "vertex")), op.getInput());
    }
}

