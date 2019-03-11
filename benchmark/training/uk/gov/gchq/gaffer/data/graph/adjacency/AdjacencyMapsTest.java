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


import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AdjacencyMapsTest {
    private final AdjacencyMaps adjacencyMaps;

    public AdjacencyMapsTest(final AdjacencyMaps adjacencyMaps) {
        this.adjacencyMaps = adjacencyMaps;
    }

    @Test
    public void shouldIterate() {
        // Then
        final Iterator<AdjacencyMap> it = adjacencyMaps.iterator();
        final AdjacencyMap first = it.next();
        final AdjacencyMap second = it.next();
        MatcherAssert.assertThat(first.getAllDestinations(), hasSize(3));
        MatcherAssert.assertThat(second.getAllDestinations(), hasSize(4));
    }

    @Test
    public void shouldGetSize() {
        // Then
        MatcherAssert.assertThat(adjacencyMaps.size(), Is.is(2));
    }

    @Test
    public void shouldGetNotEmpty() {
        // Then
        MatcherAssert.assertThat(adjacencyMaps.empty(), Is.is(false));
    }

    @Test
    public void shouldGetEmpty() {
        // Then
        MatcherAssert.assertThat(adjacencyMaps.empty(), Is.is(false));
    }
}

