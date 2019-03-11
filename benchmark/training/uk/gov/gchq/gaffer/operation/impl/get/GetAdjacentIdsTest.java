/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.operation.impl.get;


import DirectedType.EITHER;
import com.google.common.collect.Lists;
import java.util.Iterator;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;


public class GetAdjacentIdsTest extends OperationTest<GetAdjacentIds> {
    @Test
    public void shouldSetDirectedTypeToBoth() {
        // Given
        final EntityId elementId1 = new EntitySeed("identifier");
        // When
        final GetAdjacentIds op = new GetAdjacentIds.Builder().input(elementId1).directedType(EITHER).build();
        // Then
        Assert.assertEquals(EITHER, op.getDirectedType());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(CloseableIterable.class, outputClass);
    }

    @Test
    public void shouldSerialiseAndDeserialiseOperationWithEntityIds() throws SerialisationException {
        // Given
        final EntityId entitySeed = new EntitySeed("identifier");
        final GetAdjacentIds op = new GetAdjacentIds.Builder().input(entitySeed).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetAdjacentIds deserialisedOp = JSONSerialiser.deserialise(json, GetAdjacentIds.class);
        // Then
        final Iterator itr = deserialisedOp.getInput().iterator();
        Assert.assertEquals(entitySeed, itr.next());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void shouldSetIncludeIncomingOutgoingTypeToBoth() {
        final EntityId elementId = new EntitySeed("identifier");
        // When
        final GetAdjacentIds op = new GetAdjacentIds.Builder().input(elementId).inOutType(IncludeIncomingOutgoingType.EITHER).build();
        // Then
        Assert.assertEquals(IncludeIncomingOutgoingType.EITHER, op.getIncludeIncomingOutGoing());
    }

    @Test
    public void shouldSetOptionToValue() {
        // When
        final GetAdjacentIds op = new GetAdjacentIds.Builder().option("key", "value").build();
        // Then
        Assert.assertThat(op.getOptions(), Is.is(IsNull.notNullValue()));
        Assert.assertThat(op.getOptions().get("key"), Is.is("value"));
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        builderShouldCreatePopulatedOperationAll();
        builderShouldCreatePopulatedOperationIncoming();
    }

    @Test
    public void shouldSetInputFromVerticesAndEntityIds() {
        // When
        final GetAdjacentIds op = new GetAdjacentIds.Builder().input("1", new EntitySeed("2"), new Entity("group1", "3")).build();
        // Then
        Assert.assertEquals(Lists.newArrayList(new EntitySeed("1"), new EntitySeed("2"), new Entity("group1", "3")), Lists.newArrayList(op.getInput()));
    }
}

