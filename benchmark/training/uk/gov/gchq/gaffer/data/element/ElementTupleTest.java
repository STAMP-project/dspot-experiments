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


import EdgeId.MatchedVertex.SOURCE;
import IdentifierType.ADJACENT_MATCHED_VERTEX;
import IdentifierType.DESTINATION;
import IdentifierType.DIRECTED;
import IdentifierType.MATCHED_VERTEX;
import TestPropertyNames.COUNT;
import org.junit.Assert;
import org.junit.Test;


public class ElementTupleTest {
    @Test
    public void shouldGetValues() {
        // Given
        final Edge edge = new Edge.Builder().group("group").source("source vertex").dest("destination vertex").directed(true).matchedVertex(SOURCE).property(COUNT, 1).build();
        final ElementTuple tuple = new ElementTuple(edge);
        // When / Then
        Assert.assertEquals("source vertex", tuple.get(IdentifierType.SOURCE.name()));
        Assert.assertEquals("destination vertex", tuple.get(DESTINATION.name()));
        Assert.assertEquals(true, tuple.get(DIRECTED.name()));
        Assert.assertEquals("source vertex", tuple.get(MATCHED_VERTEX.name()));
        Assert.assertEquals("destination vertex", tuple.get(ADJACENT_MATCHED_VERTEX.name()));
        Assert.assertEquals(1, tuple.get(COUNT));
    }

    @Test
    public void shouldSetValues() {
        // Given
        final Edge edge = new Edge.Builder().group("group").build();
        final ElementTuple tuple = new ElementTuple(edge);
        // When
        tuple.put(IdentifierType.SOURCE.name(), "source vertex");
        tuple.put(DESTINATION.name(), "destination vertex");
        tuple.put(DIRECTED.name(), true);
        tuple.put(COUNT, 1);
        // Then
        Assert.assertEquals("source vertex", edge.getSource());
        Assert.assertEquals("destination vertex", edge.getDestination());
        Assert.assertEquals(true, tuple.get(DIRECTED.name()));
        Assert.assertEquals(1, tuple.get(COUNT));
        // When
        tuple.put(MATCHED_VERTEX.name(), "source vertex 2");
        tuple.put(ADJACENT_MATCHED_VERTEX.name(), "destination vertex 2");
        // Then
        Assert.assertEquals("source vertex 2", tuple.get(MATCHED_VERTEX.name()));
        Assert.assertEquals("destination vertex 2", tuple.get(ADJACENT_MATCHED_VERTEX.name()));
    }
}

