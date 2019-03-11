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
import SeedMatchingType.EQUAL;
import SeedMatchingType.RELATED;
import com.google.common.collect.Lists;
import java.util.Iterator;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.id.ElementId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.ElementSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.types.TypeSubTypeValue;


public class GetElementsTest extends OperationTest<GetElements> {
    @Test
    public void shouldSetSeedMatchingTypeToEquals() {
        // Given
        final ElementId elementId1 = new EntitySeed("identifier");
        // When
        final GetElements op = new GetElements.Builder().input(elementId1).seedMatching(EQUAL).build();
        // Then
        Assert.assertEquals(EQUAL, op.getSeedMatching());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = new GetElements().getOutputClass();
        // Then
        Assert.assertEquals(CloseableIterable.class, outputClass);
    }

    @Test
    public void shouldSerialiseAndDeserialiseOperationWithElementIds() throws SerialisationException {
        // Given
        final ElementSeed elementSeed1 = new EntitySeed("identifier");
        final ElementSeed elementSeed2 = new EdgeSeed("source2", "destination2", true);
        final GetElements op = new GetElements.Builder().input(elementSeed1, elementSeed2).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetElements deserialisedOp = JSONSerialiser.deserialise(json, GetElements.class);
        // Then
        final Iterator itr = deserialisedOp.getInput().iterator();
        Assert.assertEquals(elementSeed1, itr.next());
        Assert.assertEquals(elementSeed2, itr.next());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void shouldDeserialiseOperationWithVertices() throws SerialisationException {
        // Given
        final String json = "{\"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetElements\"," + (((("\"input\":[" + "1,") + "{\"class\":\"uk.gov.gchq.gaffer.types.TypeSubTypeValue\",\"type\":\"t\",\"subType\":\"s\",\"value\":\"v\"},") + "[\"java.lang.Long\",2]") + "]}");
        // When
        final GetElements deserialisedOp = JSONSerialiser.deserialise(json, GetElements.class);
        // Then
        Assert.assertEquals(Lists.newArrayList(new EntitySeed(1), new EntitySeed(new TypeSubTypeValue("t", "s", "v")), new EntitySeed(2L)), Lists.newArrayList(deserialisedOp.getInput()));
    }

    @Test
    public void shouldDeserialiseOperationWithVerticesAndIds() throws SerialisationException {
        // Given
        final String json = String.format(("{\"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetElements\"," + (((("\"input\":[" + "1,") + "{\"class\":\"uk.gov.gchq.gaffer.types.TypeSubTypeValue\",\"type\":\"t\",\"subType\":\"s\",\"value\":\"v\"},") + "{\"vertex\":{\"java.lang.Long\":2},\"class\":\"uk.gov.gchq.gaffer.operation.data.EntitySeed\"}") + "]}")));
        // When
        final GetElements deserialisedOp = JSONSerialiser.deserialise(json, GetElements.class);
        // Then
        Assert.assertEquals(Lists.newArrayList(new EntitySeed(1), new EntitySeed(new TypeSubTypeValue("t", "s", "v")), new EntitySeed(2L)), Lists.newArrayList(deserialisedOp.getInput()));
    }

    @Test
    public void shouldSetSeedMatchingTypeToRelated() {
        final ElementId elementId1 = new EntitySeed("identifier");
        final ElementId elementId2 = new EdgeSeed("source2", "destination2", true);
        // When
        final GetElements op = new GetElements.Builder().input(elementId1, elementId2).seedMatching(RELATED).build();
        // Then
        Assert.assertEquals(RELATED, op.getSeedMatching());
    }

    @Test
    public void shouldSetDirectedTypeToBoth() {
        // When
        final GetElements op = new GetElements.Builder().directedType(EITHER).input(new EntitySeed()).build();
        // Then
        Assert.assertEquals(EITHER, op.getDirectedType());
    }

    @Test
    public void shouldSetOptionToValue() {
        // When
        final GetElements op = new GetElements.Builder().option("key", "value").input(new EntitySeed()).build();
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
    public void shouldCreateInputFromVertices() {
        // When
        final GetElements op = new GetElements.Builder().input("1", new EntitySeed("2"), new Entity("group1", "3"), new EdgeSeed("4", "5"), new Edge("group", "6", "7", true)).build();
        // Then
        Assert.assertEquals(Lists.newArrayList(new EntitySeed("1"), new EntitySeed("2"), new Entity("group1", "3"), new EdgeSeed("4", "5"), new Edge("group", "6", "7", true)), Lists.newArrayList(op.getInput()));
    }
}

