package uk.gov.gchq.gaffer.sparkaccumulo.operation.scalardd;


import AccumuloTestData.TEST_OPTION_PROPERTY_KEY;
import DirectedType.UNDIRECTED;
import SeededGraphFilters.IncludeIncomingOutgoingType.EITHER;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloTestData;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.id.ElementId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class GetRDDOfElementsInRangesTest extends OperationTest<GetRDDOfElementsInRanges> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final List<Pair<ElementId, ElementId>> pairList = new ArrayList<>();
        final Pair<ElementId, ElementId> pair1 = new Pair(AccumuloTestData.SEED_SOURCE_1, AccumuloTestData.SEED_DESTINATION_1);
        final Pair<ElementId, ElementId> pair2 = new Pair(AccumuloTestData.SEED_SOURCE_2, AccumuloTestData.SEED_DESTINATION_2);
        pairList.add(pair1);
        pairList.add(pair2);
        final GetRDDOfElementsInRanges op = new GetRDDOfElementsInRanges.Builder().input(pairList).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetRDDOfElementsInRanges deserialisedOp = JSONSerialiser.deserialise(json, GetRDDOfElementsInRanges.class);
        // Then
        final Iterator<? extends Pair<? extends ElementId, ? extends ElementId>> itrPairs = deserialisedOp.getInput().iterator();
        Assert.assertEquals(pair1, itrPairs.next());
        Assert.assertEquals(pair2, itrPairs.next());
        Assert.assertFalse(itrPairs.hasNext());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final Pair<ElementId, ElementId> seed = new Pair(AccumuloTestData.SEED_A, AccumuloTestData.SEED_B);
        final GetRDDOfElementsInRanges GetRDDOfElementsInRanges = new GetRDDOfElementsInRanges.Builder().inOutType(EITHER).input(seed).directedType(UNDIRECTED).option(TEST_OPTION_PROPERTY_KEY, "true").view(new View.Builder().edge("testEdgeGroup").build()).build();
        Assert.assertEquals("true", GetRDDOfElementsInRanges.getOption(TEST_OPTION_PROPERTY_KEY));
        Assert.assertEquals(EITHER, GetRDDOfElementsInRanges.getIncludeIncomingOutGoing());
        Assert.assertEquals(UNDIRECTED, GetRDDOfElementsInRanges.getDirectedType());
        Assert.assertEquals(seed, GetRDDOfElementsInRanges.getInput().iterator().next());
        Assert.assertNotNull(GetRDDOfElementsInRanges.getView());
    }
}

