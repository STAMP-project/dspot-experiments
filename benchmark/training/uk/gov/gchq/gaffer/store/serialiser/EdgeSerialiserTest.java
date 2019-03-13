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


import TestGroups.EDGE;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class EdgeSerialiserTest {
    private Schema schema;

    private EdgeSerialiser serialiser;

    @Test
    public void testNullSerialiser() {
        // Given
        schema = new Schema.Builder().build();
        // When / Then
        try {
            serialiser = new EdgeSerialiser(schema);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Vertex serialiser is required"));
        }
    }

    @Test
    public void testCanSerialiseEdge() throws SerialisationException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).source("source").dest("destination").directed(true).build();
        // When
        final byte[] serialisedEdge = serialiser.serialise(edge);
        final Edge deserialisedEdge = serialiser.deserialise(serialisedEdge);
        // Then
        Assert.assertEquals(edge, deserialisedEdge);
    }

    @Test
    public void testCantSerialiseIntegerClass() throws SerialisationException {
        Assert.assertFalse(serialiser.canHandle(Integer.class));
    }

    @Test
    public void testCanSerialiseEdgeClass() throws SerialisationException {
        Assert.assertTrue(serialiser.canHandle(Edge.class));
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

