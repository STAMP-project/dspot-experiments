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
package uk.gov.gchq.gaffer.data.graph.adjacency;


import TestGroups.EDGE_2;
import TestGroups.EDGE_3;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;


public class AdjacencyMapTest {
    @Test
    public void shouldGetEdges() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Edge> results = adjacencyMap.getEdges(1, 2);
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(makeEdge(1, 2)));
    }

    @Test
    public void shouldGetEmptyEdgeSet() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Edge> results = adjacencyMap.getEdges(1, 6);
        // Then
        MatcherAssert.assertThat(results, Is.is(Matchers.empty()));
    }

    @Test
    public void shouldGetDestinations() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Object> results = adjacencyMap.getDestinations(1);
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(1, 2, 5));
    }

    @Test
    public void shouldGetAllDestinations() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Object> results = adjacencyMap.getAllDestinations();
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void shouldGetSources() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Object> results = adjacencyMap.getSources(1);
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(1, 4));
    }

    @Test
    public void shouldGetAllSources() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Object> results = adjacencyMap.getAllSources();
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(1, 2, 4, 5, 6));
    }

    @Test
    public void shouldGetEntry() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final Set<Edge> results = adjacencyMap.getEdges(1, 2);
        // Then
        MatcherAssert.assertThat(results, Matchers.equalTo(Collections.singleton(makeEdge(1, 2))));
    }

    @Test
    public void shouldPutMultipleEdges() {
        // Given
        final AdjacencyMap adjacencyMap = new AdjacencyMap();
        adjacencyMap.putEdge(1, 2, makeEdge(1, 2));
        adjacencyMap.putEdges(1, 2, Sets.newHashSet(makeEdge(EDGE_2, 1, 2), makeEdge(EDGE_3, 1, 2)));
        // When
        final Set<Edge> results = adjacencyMap.getEdges(1, 2);
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(makeEdge(1, 2), makeEdge(EDGE_2, 1, 2), makeEdge(EDGE_3, 1, 2)));
    }

    @Test
    public void shouldPutEdgeWhenExisting() {
        // Given
        final AdjacencyMap adjacencyMap = new AdjacencyMap();
        adjacencyMap.putEdge(1, 2, makeEdge(1, 2));
        adjacencyMap.putEdge(1, 2, makeEdge(EDGE_2, 1, 2));
        adjacencyMap.putEdge(1, 2, makeEdge(EDGE_3, 1, 2));
        // When
        final Set<Edge> results = adjacencyMap.getEdges(1, 2);
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(makeEdge(1, 2), makeEdge(EDGE_2, 1, 2), makeEdge(EDGE_3, 1, 2)));
    }

    @Test
    public void shouldContainDestination() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final boolean result = adjacencyMap.containsDestination(2);
        // Then
        MatcherAssert.assertThat(result, Is.is(true));
    }

    @Test
    public void shouldNotContainDestination() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final boolean result = adjacencyMap.containsDestination(7);
        // Then
        MatcherAssert.assertThat(result, Is.is(false));
    }

    @Test
    public void shouldContainSource() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final boolean result = adjacencyMap.containsSource(2);
        // Then
        MatcherAssert.assertThat(result, Is.is(true));
    }

    @Test
    public void shouldNotContainSource() {
        // Given
        final AdjacencyMap adjacencyMap = getAdjacencyMap();
        // When
        final boolean result = adjacencyMap.containsSource(7);
        // Then
        MatcherAssert.assertThat(result, Is.is(false));
    }
}

