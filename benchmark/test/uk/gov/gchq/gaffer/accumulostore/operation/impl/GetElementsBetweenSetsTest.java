package uk.gov.gchq.gaffer.accumulostore.operation.impl;


import DirectedType.UNDIRECTED;
import SeededGraphFilters.IncludeIncomingOutgoingType.INCOMING;
import com.google.common.collect.Lists;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloTestData;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class GetElementsBetweenSetsTest extends OperationTest<GetElementsBetweenSets> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetElementsBetweenSets op = new GetElementsBetweenSets.Builder().input(AccumuloTestData.SEED_SOURCE_1, AccumuloTestData.SEED_DESTINATION_1).inputB(AccumuloTestData.SEED_SOURCE_2, AccumuloTestData.SEED_DESTINATION_2).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetElementsBetweenSets deserialisedOp = JSONSerialiser.deserialise(json, GetElementsBetweenSets.class);
        // Then
        final Iterator itrSeedsA = deserialisedOp.getInput().iterator();
        Assert.assertEquals(AccumuloTestData.SEED_SOURCE_1, itrSeedsA.next());
        Assert.assertEquals(AccumuloTestData.SEED_DESTINATION_1, itrSeedsA.next());
        Assert.assertFalse(itrSeedsA.hasNext());
        final Iterator itrSeedsB = deserialisedOp.getInputB().iterator();
        Assert.assertEquals(AccumuloTestData.SEED_SOURCE_2, itrSeedsB.next());
        Assert.assertEquals(AccumuloTestData.SEED_DESTINATION_2, itrSeedsB.next());
        Assert.assertFalse(itrSeedsB.hasNext());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final GetElementsBetweenSets getElementsBetweenSets = new GetElementsBetweenSets.Builder().input(AccumuloTestData.SEED_B).inputB(AccumuloTestData.SEED_A).directedType(UNDIRECTED).inOutType(INCOMING).option(AccumuloTestData.TEST_OPTION_PROPERTY_KEY, "true").view(new View.Builder().edge("testEdgeGroup").build()).build();
        Assert.assertEquals("true", getElementsBetweenSets.getOption(AccumuloTestData.TEST_OPTION_PROPERTY_KEY));
        Assert.assertEquals(UNDIRECTED, getElementsBetweenSets.getDirectedType());
        Assert.assertEquals(INCOMING, getElementsBetweenSets.getIncludeIncomingOutGoing());
        Assert.assertEquals(AccumuloTestData.SEED_B, getElementsBetweenSets.getInput().iterator().next());
        Assert.assertEquals(AccumuloTestData.SEED_A, getElementsBetweenSets.getInputB().iterator().next());
        Assert.assertNotNull(getElementsBetweenSets.getView());
    }

    @Test
    public void shouldCreateInputFromVertices() {
        // Given
        final GetElementsBetweenSets op = new GetElementsBetweenSets.Builder().input(AccumuloTestData.SEED_B, AccumuloTestData.SEED_B1.getVertex()).inputB(AccumuloTestData.SEED_A, AccumuloTestData.SEED_A1.getVertex()).build();
        // Then
        Assert.assertEquals(Lists.newArrayList(AccumuloTestData.SEED_B, AccumuloTestData.SEED_B1), Lists.newArrayList(op.getInput()));
        Assert.assertEquals(Lists.newArrayList(AccumuloTestData.SEED_A, AccumuloTestData.SEED_A1), Lists.newArrayList(op.getInputB()));
    }
}

