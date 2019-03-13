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
package uk.gov.gchq.gaffer.operation.impl.output;


import EdgeVertices.BOTH;
import ToVertices.UseMatchedVertex.OPPOSITE;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;


public class ToVerticesTest extends OperationTest<ToVertices> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws JsonProcessingException, SerialisationException {
        // Given
        final ToVertices op = new ToVertices.Builder().input(new EntitySeed("2")).edgeVertices(BOTH).useMatchedVertex(OPPOSITE).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ToVertices deserialisedOp = JSONSerialiser.deserialise(json, ToVertices.class);
        // Then
        Assert.assertNotNull(deserialisedOp);
    }

    @Test
    public void shouldSerialiseAndDeserialiseOperationWithMissingEdgeVertices() throws JsonProcessingException, SerialisationException {
        // Given
        final ToVertices op = new ToVertices.Builder().input(new EntitySeed("2")).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ToVertices deserialisedOp = JSONSerialiser.deserialise(json, ToVertices.class);
        // Then
        Assert.assertNotNull(deserialisedOp);
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given
        final ToVertices toVertices = new ToVertices.Builder().input(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY_2)).edgeVertices(BOTH).build();
        // Then
        Assert.assertThat(toVertices.getInput(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(toVertices.getInput(), Matchers.iterableWithSize(2));
        Assert.assertThat(toVertices.getEdgeVertices(), Matchers.is(BOTH));
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(Iterable.class, outputClass);
    }
}

