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
package uk.gov.gchq.gaffer.accumulostore.key.impl.classic;


import AccumuloStoreConstants.DEDUPLICATE_UNDIRECTED_EDGES;
import AccumuloStoreConstants.DIRECTED_EDGE_ONLY;
import AccumuloStoreConstants.INCLUDE_EDGES;
import AccumuloStoreConstants.INCOMING_EDGE_ONLY;
import AccumuloStoreConstants.OUTGOING_EDGE_ONLY;
import AccumuloStoreConstants.UNDIRECTED_EDGE_ONLY;
import TestGroups.EDGE;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.classic.ClassicAccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.core.impl.classic.ClassicEdgeDirectedUndirectedFilterIterator;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;


public class ClassicEdgeDirectedUndirectedFilterIteratorTest {
    private static final Schema SCHEMA = new Schema.Builder().type("string", String.class).type("true", Boolean.class).edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("string").directed("true").build()).vertexSerialiser(new StringSerialiser()).build();

    private static final List<Edge> EDGES = Arrays.asList(new Edge.Builder().group(EDGE).source("vertexA").dest("vertexB").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexD").dest("vertexC").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexE").dest("vertexE").directed(true).build(), new Edge.Builder().group(EDGE).source("vertexF").dest("vertexG").directed(false).build(), new Edge.Builder().group(EDGE).source("vertexH").dest("vertexH").directed(false).build());

    private final ClassicAccumuloElementConverter converter = new ClassicAccumuloElementConverter(ClassicEdgeDirectedUndirectedFilterIteratorTest.SCHEMA);

    @Test
    public void shouldOnlyAcceptDeduplicatedEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
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
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            // First key is deduplicated
            Assert.assertTrue(("Failed for edge: " + (edge.toString())), filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                Assert.assertFalse(("Failed for edge: " + (edge.toString())), filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptDeduplicatedDirectedEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(OUTGOING_EDGE_ONLY, "true");
                put(DIRECTED_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            // First key is deduplicated
            Assert.assertEquals(("Failed for edge: " + (edge.toString())), edge.isDirected(), filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                Assert.assertFalse(("Failed for edge: " + (edge.toString())), filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptDeduplicatedUndirectedEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DEDUPLICATE_UNDIRECTED_EDGES, "true");
                put(UNDIRECTED_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            // First key is deduplicated
            Assert.assertEquals(("Failed for edge: " + (edge.toString())), (!(edge.isDirected())), filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                Assert.assertFalse(("Failed for edge: " + (edge.toString())), filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptDirectedEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(DIRECTED_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final boolean expectedResult = edge.isDirected();
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            Assert.assertEquals(("Failed for edge: " + (edge.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                Assert.assertEquals(("Failed for edge: " + (edge.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptUndirectedEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<String, String>() {
            {
                put(UNDIRECTED_EDGE_ONLY, "true");
                put(INCLUDE_EDGES, "true");
            }
        };
        filter.init(null, options, null);
        final Value value = null;// value should not be used

        // When / Then
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final boolean expectedResult = !(edge.isDirected());
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            Assert.assertEquals(("Failed for edge: " + (edge.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                Assert.assertEquals(("Failed for edge: " + (edge.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptIncomingEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
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
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            Assert.assertEquals(("Failed for edge: " + (edge.toString())), false, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                final boolean expectedResult = edge.isDirected();
                Assert.assertEquals(("Failed for edge: " + (edge.toString())), expectedResult, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldOnlyAcceptOutgoingEdges() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
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
        for (final Edge edge : ClassicEdgeDirectedUndirectedFilterIteratorTest.EDGES) {
            final Pair<Key, Key> keys = converter.getKeysFromEdge(edge);
            final boolean expectedResult = edge.isDirected();
            Assert.assertEquals(("Failed for edge: " + (edge.toString())), expectedResult, filter.accept(keys.getFirst(), value));
            if (null != (keys.getSecond())) {
                // self edges are not added the other way round
                Assert.assertEquals(("Failed for edge: " + (edge.toString())), false, filter.accept(keys.getSecond(), value));
            }
        }
    }

    @Test
    public void shouldThrowExceptionWhenValidateOptionsWithDirectedAndUndirectedEdgeFlags() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<>();
        options.put(DIRECTED_EDGE_ONLY, "true");
        options.put(UNDIRECTED_EDGE_ONLY, "true");
        // When / Then
        try {
            filter.validateOptions(options);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenValidateOptionsWithInAndOutEdgeFlags() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<>();
        options.put(INCOMING_EDGE_ONLY, "true");
        options.put(OUTGOING_EDGE_ONLY, "true");
        // When / Then
        try {
            filter.validateOptions(options);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldValidateOptionsSuccessfully() throws IOException, OperationException {
        // Given
        final ClassicEdgeDirectedUndirectedFilterIterator filter = new ClassicEdgeDirectedUndirectedFilterIterator();
        final Map<String, String> options = new HashMap<>();
        // When
        final boolean result = filter.validateOptions(options);
        // Then
        Assert.assertTrue(result);
    }
}

