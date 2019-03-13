/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore.operation.impl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloTestData;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.id.ElementId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class SummariseGroupOverRangesTest extends OperationTest<SummariseGroupOverRanges> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final List<Pair<ElementId, ElementId>> pairList = new ArrayList<>();
        final Pair<ElementId, ElementId> pair1 = new Pair(AccumuloTestData.SEED_SOURCE_1, AccumuloTestData.SEED_DESTINATION_1);
        final Pair<ElementId, ElementId> pair2 = new Pair(AccumuloTestData.SEED_SOURCE_2, AccumuloTestData.SEED_DESTINATION_2);
        pairList.add(pair1);
        pairList.add(pair2);
        final SummariseGroupOverRanges op = new SummariseGroupOverRanges.Builder().input(pairList).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final SummariseGroupOverRanges deserialisedOp = JSONSerialiser.deserialise(json, SummariseGroupOverRanges.class);
        // Then
        final Iterator<? extends Pair<? extends ElementId, ? extends ElementId>> itrPairs = deserialisedOp.getInput().iterator();
        Assert.assertEquals(pair1, itrPairs.next());
        Assert.assertEquals(pair2, itrPairs.next());
        Assert.assertFalse(itrPairs.hasNext());
    }
}

