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
package uk.gov.gchq.gaffer.data.graph.function.walk;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.graph.Walk;


public class ExtractWalkVertexTest {
    private static final Edge EDGE_AB = new Edge.Builder().group(EDGE).source("A").dest("B").directed(true).build();

    private static final Edge EDGE_BC = new Edge.Builder().group(EDGE).source("B").dest("C").directed(true).build();

    private static final Edge EDGE_CA = new Edge.Builder().group(EDGE).source("C").dest("A").directed(true).build();

    private static final Entity ENTITY_A = new Entity.Builder().group(ENTITY).vertex("A").build();

    private static final Entity ENTITY_B = new Entity.Builder().group(ENTITY).vertex("B").build();

    private static final Entity ENTITY_C = new Entity.Builder().group(ENTITY).vertex("C").build();

    @Test
    public void shouldReturnVertexFromWalkObject() {
        // Given
        final Function<Walk, Object> function = new ExtractWalkVertex();
        final Walk walk = new Walk.Builder().entity(ExtractWalkVertexTest.ENTITY_A).edge(ExtractWalkVertexTest.EDGE_AB).entity(ExtractWalkVertexTest.ENTITY_B).edge(ExtractWalkVertexTest.EDGE_BC).entity(ExtractWalkVertexTest.ENTITY_C).edge(ExtractWalkVertexTest.EDGE_CA).entity(ExtractWalkVertexTest.ENTITY_A).build();
        // When
        final Object result = function.apply(walk);
        // Then
        Assert.assertEquals("A", result);
    }

    @Test
    public void shouldThrowIAEForNullInput() {
        // Given
        final Function<Walk, Object> function = new ExtractWalkVertex();
        final Walk walk = null;
        // When / Then
        try {
            function.apply(walk);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Walk cannot be null"));
        }
    }
}

