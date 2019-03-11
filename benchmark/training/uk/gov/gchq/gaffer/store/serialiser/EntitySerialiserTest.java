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
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class EntitySerialiserTest {
    private Schema schema;

    private EntitySerialiser serialiser;

    @Test
    public void testNullSerialiser() {
        // Given
        schema = new Schema.Builder().build();
        // When / Then
        try {
            serialiser = new EntitySerialiser(schema);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Vertex serialiser is required"));
        }
    }

    @Test
    public void testCanSerialiseEntity() throws SerialisationException {
        // Given
        final Entity entity = new Entity(TestGroups.ENTITY, "testVertex");
        // When
        final byte[] serialisedEntity = serialiser.serialise(entity);
        final Entity deserialisedEntity = serialiser.deserialise(serialisedEntity);
        // Then
        Assert.assertEquals(entity, deserialisedEntity);
    }

    @Test
    public void testCantSerialiseIntegerClass() throws SerialisationException {
        Assert.assertFalse(serialiser.canHandle(Integer.class));
    }

    @Test
    public void testCanSerialiseElementClass() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(Entity.class));
    }

    @Test
    public void testDeserialiseEmpty() throws SerialisationException {
        Assert.assertEquals(null, serialiser.deserialiseEmpty());
    }

    @Test
    public void testPreserveObjectOrdering() throws SerialisationException {
        Assert.assertEquals(false, serialiser.preservesObjectOrdering());
    }
}

