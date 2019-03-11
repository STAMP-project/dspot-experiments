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
package uk.gov.gchq.gaffer.accumulostore.key.impl.byteEntity;


import AccumuloStoreConstants.DEDUPLICATE_UNDIRECTED_EDGES;
import AccumuloStoreConstants.DIRECTED_EDGE_ONLY;
import AccumuloStoreConstants.INCLUDE_EDGES;
import AccumuloStoreConstants.INCLUDE_ENTITIES;
import AccumuloStoreConstants.INCOMING_EDGE_ONLY;
import AccumuloStoreConstants.OUTGOING_EDGE_ONLY;
import AccumuloStoreConstants.UNDIRECTED_EDGE_ONLY;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.byteEntity.ByteEntityAccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.byteEntity.ByteEntityRangeElementPropertyFilterIterator;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;


public class ByteEntityRangeElementPropertyFilterIteratorTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", String.class).type("type", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).entity(ENTITY, new SchemaEntityDefinition.Builder().vertex("string").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final List<Element> ELEMENTS = Arrays.asList(new Edge.Builder().group(EDGE).source("vertexA").dest("vertexB").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexD").dest("vertexC").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexE").dest("vertexE").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexF").dest("vertexG").directed(false).build(), new Edge.Builder().group(EDGE).source("vertexH").dest("vertexH").directed(false).build(), new Entity.Builder().group(ENTITY).vertex("vertexI").build());

    private final ByteEntityAccumuloElementConverter converter = new ByteEntityAccumuloElementConverter(ByteEntityRangeElementPropertyFilterIteratorTest.SCHEMA);

    @Test
    public void shouldOnlyAcceptDeduplicatedEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(OUTGOING_EDGE_ONLY, "true");
                put(DEDUPLICATE_UNDIRECTED_EDGES, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            // First key is deduplicated, but only edges should be excepted
            Assert.assertEquals(("Failed for element: " + (element.toString())), (element instanceof Edge), filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), false, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptDeduplicatedDirectedEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DEDUPLICATE_UNDIRECTED_EDGES, "true");
                put(DIRECTED_EDGE_ONLY, "true");
                put(OUTGOING_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            // First key is deduplicated, but only directed edges should be excepted
            final boolean expectedResult = (element instanceof Edge) && (isDirected());
            Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), false, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptDeduplicatedUndirectedEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DEDUPLICATE_UNDIRECTED_EDGES, "true");
                put(UNDIRECTED_EDGE_ONLY, "true");
                put(OUTGOING_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            // First key is deduplicated, but only undirected edges should be excepted
            final boolean expectedResult = (element instanceof Edge) && (!(isDirected()));
            Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), false, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptDirectedEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DIRECTED_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final boolean expectedResult = (element instanceof Edge) && (isDirected());
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptUndirectedEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(UNDIRECTED_EDGE_ONLY, "true");
                put(OUTGOING_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final boolean expectedResult = (element instanceof Edge) && (!(isDirected()));
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptIncomingEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DIRECTED_EDGE_ONLY, "true");
                put(INCOMING_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            Assert.assertEquals(("Failed for element: " + (element.toString())), false, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                final boolean expectedResult = (element instanceof Edge) && (isDirected());
                Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptOutgoingEdges() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DIRECTED_EDGE_ONLY, "true");
                put(OUTGOING_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            final boolean expectedResult = (element instanceof Edge) && (isDirected());
            Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self elements are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), false, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldAcceptOnlyEntities() throws IOException, OperationException {
        // Given
        final ByteEntityRangeElementPropertyFilterIterator filter = new ByteEntityRangeElementPropertyFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(INCLUDE_ENTITIES, "true");
                put(OUTGOING_EDGE_ONLY, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Element element : ByteEntityRangeElementPropertyFilterIteratorTest.ELEMENTS) {
            final boolean expectedResult = element instanceof Entity;
            final Pair<Key, Key> keys = converter.getKeysFromElement(element);
            Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // entities and self edges are not added the other way round
                Assert.assertEquals(("Failed for element: " + (element.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }
}

