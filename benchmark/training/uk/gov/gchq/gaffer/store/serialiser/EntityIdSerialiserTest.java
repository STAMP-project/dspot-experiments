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
package uk.gov.gchq.gaffer.store.serialiser;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class EntityIdSerialiserTest {
    private Schema schema;

    private EntityIdSerialiser serialiser;

    @Test
    public void testNullSerialiser() {
        // Given
        schema = new Schema.Builder().build();
        // When / Then
        try {
            serialiser = new EntityIdSerialiser(schema);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Vertex serialiser is required"));
        }
    }

    @Test
    public void testCanSerialiseEntityId() throws SerialisationException {
        // Given
        final EntityId entityId = new EntitySeed("vertex");
        // When
        final byte[] serialisedEntityId = serialiser.serialise(entityId);
        final EntityId deserialisedEntityId = serialiser.deserialise(serialisedEntityId);
        // Then
        Assert.assertEquals(entityId, deserialisedEntityId);
    }

    @Test
    public void testCanSerialiseVertex() throws SerialisationException {
        // Given
        final String testVertex = "TestVertex";
        // When
        final byte[] serialisedVertex = serialiser.serialiseVertex(testVertex);
        final EntityId deserialisedEntityId = serialiser.deserialise(serialisedVertex);
        // Then
        Assert.assertEquals(testVertex, deserialisedEntityId.getVertex());
    }

    @Test
    public void testCantSerialiseIntegerClass() throws SerialisationException {
        Assert.assertFalse(serialiser.canHandle(Integer.class));
    }

    @Test
    public void testCanSerialiseEntityIdClass() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(EntityId.class));
    }

    @Test
    public void testDeserialiseEmpty() throws SerialisationException {
        Assert.assertEquals(null, serialiser.deserialiseEmpty());
    }

    @Test
    public void testPreserveObjectOrdering() throws SerialisationException {
        Assert.assertEquals(true, serialiser.preservesObjectOrdering());
    }

    @Test
    public void testIsConsistent() {
        Assert.assertEquals(true, serialiser.isConsistent());
    }
}

