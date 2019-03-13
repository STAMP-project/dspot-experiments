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
package uk.gov.gchq.gaffer.data.element;


import EdgeId.MatchedVertex.DESTINATION;
import EdgeId.MatchedVertex.SOURCE;
import IdentifierType.ADJACENT_MATCHED_VERTEX;
import IdentifierType.MATCHED_VERTEX;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestPropertyNames.STRING;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;


public class EdgeTest extends ElementTest {
    @Test
    public void shouldSetAndGetIdentifiersWithMatchedSource() {
        // Given
        final Edge edge = new Edge.Builder().group("group").source("source vertex").dest("destination vertex").directed(true).matchedVertex(SOURCE).build();
        // When/Then
        Assert.assertEquals("source vertex", edge.getMatchedVertexValue());
        Assert.assertEquals("source vertex", edge.getIdentifier(MATCHED_VERTEX));
        Assert.assertEquals("destination vertex", edge.getAdjacentMatchedVertexValue());
        Assert.assertEquals("destination vertex", edge.getIdentifier(ADJACENT_MATCHED_VERTEX));
        Assert.assertTrue(edge.isDirected());
    }

    @Test
    public void shouldSetAndGetIdentifiersWithMatchedDestination() {
        // Given
        final Edge edge = new Edge.Builder().group("group").source("source vertex").dest("destination vertex").directed(true).matchedVertex(DESTINATION).build();
        // When/Then
        Assert.assertEquals("destination vertex", edge.getMatchedVertexValue());
        Assert.assertEquals("destination vertex", edge.getIdentifier(MATCHED_VERTEX));
        Assert.assertEquals("source vertex", edge.getIdentifier(ADJACENT_MATCHED_VERTEX));
        Assert.assertEquals("source vertex", edge.getAdjacentMatchedVertexValue());
        Assert.assertTrue(edge.isDirected());
    }

    @Test
    public void shouldSetAndGetIdentifiersWithMatchedSourceIsNull() {
        // Given
        final Edge edge = new Edge.Builder().group("group").source("source vertex").dest("destination vertex").directed(true).matchedVertex(null).build();
        // When/Then
        Assert.assertEquals("source vertex", edge.getMatchedVertexValue());
        Assert.assertEquals("source vertex", edge.getIdentifier(MATCHED_VERTEX));
        Assert.assertEquals("destination vertex", edge.getIdentifier(ADJACENT_MATCHED_VERTEX));
        Assert.assertEquals("destination vertex", edge.getIdentifier(ADJACENT_MATCHED_VERTEX));
        Assert.assertTrue(edge.isDirected());
    }

    @Test
    public void shouldBuildEdge() {
        // Given
        final String source = "source vertex";
        final String destination = "dest vertex";
        final boolean directed = true;
        final String propValue = "propValue";
        // When
        final Edge edge = new Edge.Builder().group(EDGE).source(source).dest(destination).directed(directed).property(STRING, propValue).build();
        // Then
        Assert.assertEquals(EDGE, edge.getGroup());
        Assert.assertEquals(source, edge.getSource());
        Assert.assertEquals(destination, edge.getDestination());
        Assert.assertTrue(edge.isDirected());
        Assert.assertEquals(propValue, edge.getProperty(STRING));
    }

    @Test
    public void shouldConstructEdge() {
        // Given
        final String source = "source vertex";
        final String destination = "dest vertex";
        final boolean directed = true;
        final String propValue = "propValue";
        // When
        final Edge edge = new Edge.Builder().group(EDGE).source(source).dest(destination).directed(directed).build();
        edge.putProperty(STRING, propValue);
        // Then
        Assert.assertEquals(EDGE, edge.getGroup());
        Assert.assertEquals(source, edge.getSource());
        Assert.assertEquals(destination, edge.getDestination());
        Assert.assertTrue(edge.isDirected());
        Assert.assertEquals(propValue, edge.getProperty(STRING));
    }

    @Test
    public void shouldCloneEdge() {
        // Given
        final String source = "source vertex";
        final String destination = "dest vertex";
        final boolean directed = true;
        // When
        final Edge edge = new Edge.Builder().group(EDGE).source(source).dest(destination).directed(directed).build();
        final Edge clone = edge.emptyClone();
        // Then
        Assert.assertEquals(edge, clone);
    }

    @Test
    public void shouldReturnTrueForShallowEqualsWhenAllCoreFieldsAreEqual() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(true).property("some property", "some value").build();
        final Edge edge2 = cloneCoreFields(edge1);
        edge2.putProperty("some different property", "some other value");
        // When
        boolean isEqual = edge1.shallowEquals(((Object) (edge2)));
        // Then
        Assert.assertTrue(isEqual);
    }

    @Test
    public void shouldReturnFalseForEqualsWhenPropertyIsDifferent() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(true).property("some property", "some value").build();
        final Edge edge2 = cloneCoreFields(edge1);
        edge2.putProperty("some property", "some other value");
        // When
        boolean isEqual = edge1.equals(((Object) (edge2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertNotEquals(edge1.hashCode(), edge2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenDirectedIsDifferent() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(true).build();
        final Edge edge2 = new Edge.Builder().group(edge1.getGroup()).source(edge1.getSource()).dest(edge1.getDestination()).directed((!(edge1.isDirected()))).build();
        // When
        boolean isEqual = edge1.equals(((Object) (edge2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertFalse(((edge1.hashCode()) == (edge2.hashCode())));
    }

    @Test
    public void shouldReturnFalseForEqualsWhenSourceIsDifferent() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(true).build();
        final Edge edge2 = new Edge.Builder().group(edge1.getGroup()).source("different source").dest(edge1.getDestination()).directed(edge1.isDirected()).build();
        // When
        boolean isEqual = edge1.equals(((Object) (edge2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertFalse(((edge1.hashCode()) == (edge2.hashCode())));
    }

    @Test
    public void shouldReturnFalseForEqualsWhenDestinationIsDifferent() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(true).build();
        final Edge edge2 = new Edge.Builder().group(edge1.getGroup()).source(edge1.getSource()).dest("different destination").directed(edge1.isDirected()).build();
        // When
        boolean isEqual = edge1.equals(((Object) (edge2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertFalse(((edge1.hashCode()) == (edge2.hashCode())));
    }

    @Test
    public void shouldReturnTrueForEqualsWhenUndirectedIdentifiersFlipped() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(false).build();
        // Given
        final Edge edge2 = new Edge.Builder().group("group").source("dest vertex").dest("source vertex").directed(false).build();
        // When
        boolean isEqual = edge1.equals(((Object) (edge2)));
        // Then
        Assert.assertTrue(isEqual);
        Assert.assertTrue(((edge1.hashCode()) == (edge2.hashCode())));
    }

    @Test
    public void shouldReturnFalseForEqualsWhenDirectedIdentifiersFlipped() {
        // Given
        final Edge edge1 = new Edge.Builder().group("group").source("source vertex").dest("dest vertex").directed(true).build();
        // Given
        final Edge edge2 = new Edge.Builder().group("group").source("dest vertex").dest("source vertex").directed(true).build();
        // When
        boolean isEqual = edge1.equals(((Object) (edge2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertFalse(((edge1.hashCode()) == (edge2.hashCode())));
    }

    @Test
    public void shouldSwapVerticesIfSourceIsGreaterThanDestination_toString() {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).directed(false).source(new EdgeTest.Vertex("2")).dest(new EdgeTest.Vertex("1")).build();
        // Then
        Assert.assertThat(edge.getSource(), IsEqual.equalTo(new EdgeTest.Vertex("1")));
        Assert.assertThat(edge.getDestination(), IsEqual.equalTo(new EdgeTest.Vertex("2")));
    }

    @Test
    public void shouldNotSwapVerticesIfSourceIsLessThanDestination_toString() {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).directed(false).source(new EdgeTest.Vertex("1")).dest(new EdgeTest.Vertex("2")).build();
        // Then
        Assert.assertThat(edge.getSource(), IsEqual.equalTo(new EdgeTest.Vertex("1")));
        Assert.assertThat(edge.getDestination(), IsEqual.equalTo(new EdgeTest.Vertex("2")));
    }

    @Test
    public void shouldSwapVerticesIfSourceIsGreaterThanDestination_comparable() {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).directed(false).source(new Integer(2)).dest(new Integer(1)).build();
        // Then
        Assert.assertThat(edge.getSource(), IsEqual.equalTo(new Integer(1)));
        Assert.assertThat(edge.getDestination(), IsEqual.equalTo(new Integer(2)));
    }

    @Test
    public void shouldNotSwapVerticesIfSourceIsLessThanDestination_comparable() {
        // Given
        final Edge edge = new Edge.Builder().group(EDGE).directed(false).source(new Integer(1)).dest(new Integer(2)).build();
        // Then
        Assert.assertThat(edge.getSource(), IsEqual.equalTo(new Integer(1)));
        Assert.assertThat(edge.getDestination(), IsEqual.equalTo(new Integer(2)));
    }

    @Test
    public void shouldFailToConsistentlySwapVerticesWithNoToStringImplementation() {
        // Given
        final List<Edge> edges = new ArrayList<>();
        final List<EdgeTest.Vertex2> sources = new ArrayList<>();
        final List<EdgeTest.Vertex2> destinations = new ArrayList<>();
        // Create a load of edges with Vertex2 objects as source and destination.
        // Vertex2 has no toString method and does not implement Comparable, so
        // this should result in Edges being created with different sources and
        // destinations.
        for (int i = 0; i < 1000; i++) {
            final EdgeTest.Vertex2 source = new EdgeTest.Vertex2("1");
            final EdgeTest.Vertex2 destination = new EdgeTest.Vertex2("2");
            sources.add(source);
            destinations.add(destination);
        }
        for (int i = 0; i < 1000; i++) {
            final Edge edge = new Edge.Builder().group(EDGE).directed(false).source(sources.get(i)).dest(destinations.get(i)).build();
            edges.add(edge);
        }
        // Then
        Assert.assertThat(edges.stream().map(Edge::getSource).distinct().count(), Matchers.greaterThan(1L));
        Assert.assertThat(edges.stream().map(Edge::getDestination).distinct().count(), Matchers.greaterThan(1L));
    }

    @Test
    public void shouldNotFailToConsistentlySwapVerticesWithStringImplementation() {
        // Opposite to shouldFailToConsistentlySwapVerticesWithNoToStringImplementation(),
        // showing that Edges which implement toString, equals and hashCode are
        // consistently created with source and destination the correct way round
        // Given
        final List<Edge> edges = new ArrayList<>();
        final List<EdgeTest.Vertex> sources = new ArrayList<>();
        final List<EdgeTest.Vertex> destinations = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            final EdgeTest.Vertex source = new EdgeTest.Vertex("1");
            final EdgeTest.Vertex destination = new EdgeTest.Vertex("2");
            sources.add(source);
            destinations.add(destination);
        }
        for (int i = 0; i < 1000; i++) {
            final Edge edge = new Edge.Builder().group(EDGE).directed(false).source(sources.get(i)).dest(destinations.get(i)).build();
            edges.add(edge);
        }
        // Then
        Assert.assertThat(edges.stream().map(Edge::getSource).distinct().count(), IsEqual.equalTo(1L));
        Assert.assertThat(edges.stream().map(Edge::getDestination).distinct().count(), IsEqual.equalTo(1L));
    }

    @Test
    public void shouldSetIdentifiers() {
        // Given
        final Edge edge1 = new Edge(TestGroups.EDGE, 1, 2, false);
        final Edge edge2 = new Edge(TestGroups.EDGE_2, 4, 3, false);
        // When
        edge1.setIdentifiers(4, 3, false);
        edge1.setGroup(EDGE_2);
        // Then
        Assert.assertEquals(3, edge1.getSource());
        Assert.assertThat(edge1, IsEqual.equalTo(edge2));
    }

    @Test
    public void shouldFallbackToToStringComparisonIfSourceAndDestinationHaveDifferentTypes() {
        // Given
        final Edge edge1 = new Edge(TestGroups.EDGE, 1, "2", false);
        final Edge edge2 = new Edge(TestGroups.EDGE, "2", 1, false);
        // Then
        Assert.assertThat(edge1, IsEqual.equalTo(edge2));
    }

    @Test
    public void shouldDeserialiseFromJsonUsingDirectedTrueField() throws SerialisationException {
        // Given
        final String json = "{\"class\": \"uk.gov.gchq.gaffer.data.element.Edge\", \"directed\": true}";
        // When
        final Edge deserialisedEdge = JSONSerialiser.deserialise(json.getBytes(), Edge.class);
        // Then
        Assert.assertTrue(deserialisedEdge.isDirected());
    }

    @Test
    public void shouldDeserialiseFromJsonUsingDirectedFalseField() throws SerialisationException {
        // Given
        final String json = "{\"class\": \"uk.gov.gchq.gaffer.data.element.Edge\", \"directed\": false}";
        // When
        final Edge deserialisedEdge = JSONSerialiser.deserialise(json.getBytes(), Edge.class);
        // Then
        Assert.assertFalse(deserialisedEdge.isDirected());
    }

    @Test
    public void shouldDeserialiseFromJsonWhenDirectedTypeIsDirected() throws SerialisationException {
        // Given
        final String json = "{\"class\": \"uk.gov.gchq.gaffer.data.element.Edge\", \"directedType\": \"DIRECTED\"}";
        // When
        final Edge deserialisedEdge = JSONSerialiser.deserialise(json.getBytes(), Edge.class);
        // Then
        Assert.assertTrue(deserialisedEdge.isDirected());
    }

    @Test
    public void shouldDeserialiseFromJsonWhenDirectedTypeIsUndirected() throws SerialisationException {
        // Given
        final String json = "{\"class\": \"uk.gov.gchq.gaffer.data.element.Edge\", \"directedType\": \"UNDIRECTED\"}";
        // When
        final Edge deserialisedEdge = JSONSerialiser.deserialise(json.getBytes(), Edge.class);
        // Then
        Assert.assertFalse(deserialisedEdge.isDirected());
    }

    @Test
    public void shouldDeserialiseFromJsonWhenDirectedTypeIsEither() throws SerialisationException {
        // Given
        final String json = "{\"class\": \"uk.gov.gchq.gaffer.data.element.Edge\", \"directedType\": \"EITHER\"}";
        // When
        final Edge deserialisedEdge = JSONSerialiser.deserialise(json.getBytes(), Edge.class);
        // Then
        Assert.assertTrue(deserialisedEdge.isDirected());
    }

    @Test
    public void shouldThrowExceptionWhenDeserialiseFromJsonUsingDirectedAndDirectedType() {
        // Given
        final String json = "{\"class\": \"uk.gov.gchq.gaffer.data.element.Edge\", \"directed\": true, \"directedType\": \"DIRECTED\"}";
        // When / Then
        try {
            JSONSerialiser.deserialise(json.getBytes(), Edge.class);
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains("not both"));
        }
    }

    private class Vertex {
        private final String property;

        public Vertex(final String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }

        @Override
        public boolean equals(final Object obj) {
            if ((this) == obj) {
                return true;
            }
            if ((obj == null) || ((getClass()) != (obj.getClass()))) {
                return false;
            }
            final EdgeTest.Vertex vertex = ((EdgeTest.Vertex) (obj));
            return new EqualsBuilder().append(property, vertex.property).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(19, 23).append(property).toHashCode();
        }

        @Override
        public String toString() {
            return ("Vertex[property=" + (property)) + "]";
        }
    }

    private class Vertex2 {
        private final String property;

        public Vertex2(final String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }
}

