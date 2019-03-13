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
import java.util.Set;
import java.util.function.Function;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.graph.Walk;


public class ExtractWalkEntitiesFromHopTest {
    private static final Edge EDGE_AB = new Edge.Builder().group(EDGE).source("A").dest("B").directed(true).build();

    private static final Edge EDGE_BC = new Edge.Builder().group(EDGE).source("B").dest("C").directed(true).build();

    private static final Edge EDGE_CA = new Edge.Builder().group(EDGE).source("C").dest("A").directed(true).build();

    private static final Entity ENTITY_B = new Entity.Builder().group(ENTITY).vertex("B").build();

    private static final Entity ENTITY_C = new Entity.Builder().group(ENTITY).vertex("C").build();

    @Test
    public void shouldReturnEdgesFromWalkObject() {
        // Given
        final Function<Walk, Set<Entity>> function = new ExtractWalkEntitiesFromHop(2);
        final Walk walk = new Walk.Builder().edge(ExtractWalkEntitiesFromHopTest.EDGE_AB).entity(ExtractWalkEntitiesFromHopTest.ENTITY_B).edge(ExtractWalkEntitiesFromHopTest.EDGE_BC).entity(ExtractWalkEntitiesFromHopTest.ENTITY_C).edge(ExtractWalkEntitiesFromHopTest.EDGE_CA).build();
        // When
        final Set<Entity> results = function.apply(walk);
        // Then
        Assert.assertThat(results, Matchers.contains(ExtractWalkEntitiesFromHopTest.ENTITY_C));
    }
}

