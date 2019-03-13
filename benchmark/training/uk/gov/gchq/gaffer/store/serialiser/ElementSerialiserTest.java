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
import TestGroups.ENTITY;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class ElementSerialiserTest {
    private Schema schema;

    private ElementSerialiser elementSerialiser;

    private static final String TEST_VERTEX = "testVertex";

    @Test
    public void testCanSerialiseEdgeElement() throws SerialisationException {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).source("source").dest("destination").directed(true).build();
        // When
        final byte[] serialisedEdge = elementSerialiser.serialise(edge);
        final Element deserialisedElement = elementSerialiser.deserialise(serialisedEdge);
        // Then
        Assert.assertEquals(edge, deserialisedElement);
    }

    @Test
    public void testCanSerialiseEntityElement() throws SerialisationException {
        // Given
        final Entity entity = new Entity(TestGroups.ENTITY, ElementSerialiserTest.TEST_VERTEX);
        // When
        final byte[] serialisedEntity = elementSerialiser.serialise(entity);
        final Element deserialisedEntity = elementSerialiser.deserialise(serialisedEntity);
        // Then
        Assert.assertEquals(entity, deserialisedEntity);
    }

    @Test
    public void testGetGroup() throws SerialisationException {
        // Given
        final Edge edge = new Edge.Builder().group(ENTITY).source("source").dest("destination").directed(true).build();
        // When
        final byte[] serialisedEdge = elementSerialiser.serialise(edge);
        // Then
        Assert.assertEquals(ENTITY, elementSerialiser.getGroup(serialisedEdge));
    }

    @Test
    public void testCantSerialiseIntegerClass() throws SerialisationException {
        Assert.assertFalse(elementSerialiser.canHandle(Integer.class));
    }

    @Test
    public void testCanSerialiseElementClass() throws SerialisationException {
        Assert.assertTrue(elementSerialiser.canHandle(Element.class));
    }

    @Test
    public void testDeserialiseEmpty() throws SerialisationException {
        Assert.assertEquals(null, elementSerialiser.deserialiseEmpty());
    }

    @Test
    public void testPreserveObjectOrdering() throws SerialisationException {
        Assert.assertEquals(false, elementSerialiser.preservesObjectOrdering());
    }
}

