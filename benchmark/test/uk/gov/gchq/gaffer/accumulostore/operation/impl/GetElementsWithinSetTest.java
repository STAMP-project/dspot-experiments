package uk.gov.gchq.gaffer.accumulostore.operation.impl;


import DirectedType.DIRECTED;
import com.google.common.collect.Lists;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloTestData;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class GetElementsWithinSetTest extends OperationTest<GetElementsWithinSet> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetElementsWithinSet op = new GetElementsWithinSet.Builder().input(AccumuloTestData.SEED_SOURCE_1, AccumuloTestData.SEED_DESTINATION_1, AccumuloTestData.SEED_SOURCE_2, AccumuloTestData.SEED_DESTINATION_2).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetElementsWithinSet deserialisedOp = JSONSerialiser.deserialise(json, GetElementsWithinSet.class);
        // Then
        final Iterator itrSeedsA = deserialisedOp.getInput().iterator();
        Assert.assertEquals(AccumuloTestData.SEED_SOURCE_1, itrSeedsA.next());
        Assert.assertEquals(AccumuloTestData.SEED_DESTINATION_1, itrSeedsA.next());
        Assert.assertEquals(AccumuloTestData.SEED_SOURCE_2, itrSeedsA.next());
        Assert.assertEquals(AccumuloTestData.SEED_DESTINATION_2, itrSeedsA.next());
        Assert.assertFalse(itrSeedsA.hasNext());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final GetElementsWithinSet getElementsWithinSet = new GetElementsWithinSet.Builder().input(AccumuloTestData.SEED_A).directedType(DIRECTED).option(AccumuloTestData.TEST_OPTION_PROPERTY_KEY, "true").view(new View.Builder().edge("testEdgegroup").build()).build();
        Assert.assertEquals("true", getElementsWithinSet.getOption(AccumuloTestData.TEST_OPTION_PROPERTY_KEY));
        Assert.assertEquals(DIRECTED, getElementsWithinSet.getDirectedType());
        Assert.assertEquals(AccumuloTestData.SEED_A, getElementsWithinSet.getInput().iterator().next());
        Assert.assertNotNull(getElementsWithinSet.getView());
    }

    @Test
    public void shouldCreateInputFromVertices() {
        // When
        final GetElementsWithinSet op = new GetElementsWithinSet.Builder().input(AccumuloTestData.SEED_B, AccumuloTestData.SEED_B1.getVertex()).build();
        // Then
        Assert.assertEquals(Lists.newArrayList(AccumuloTestData.SEED_B, AccumuloTestData.SEED_B1), Lists.newArrayList(op.getInput()));
    }
}

