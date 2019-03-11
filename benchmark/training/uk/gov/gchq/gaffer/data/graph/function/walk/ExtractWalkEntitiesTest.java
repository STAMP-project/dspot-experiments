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
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Function;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.graph.Walk;


public class ExtractWalkEntitiesTest {
    private static final Edge EDGE_AB = new Edge.Builder().group(EDGE).source("A").dest("B").directed(true).build();

    private static final Edge EDGE_BC = new Edge.Builder().group(EDGE).source("B").dest("C").directed(true).build();

    private static final Edge EDGE_CA = new Edge.Builder().group(EDGE).source("C").dest("A").directed(true).build();

    private static final Entity ENTITY_A = new Entity.Builder().group(ENTITY).vertex("A").build();

    private static final Entity ENTITY_B = new Entity.Builder().group(ENTITY).vertex("B").build();

    private static final Entity ENTITY_C = new Entity.Builder().group(ENTITY).vertex("C").build();

    @Test
    public void shouldReturnEntitiesFromWalkObject() {
        // Given
        final Function<Walk, Iterable<Set<Entity>>> function = new ExtractWalkEntities();
        final Walk walk = new Walk.Builder().entity(ExtractWalkEntitiesTest.ENTITY_A).edge(ExtractWalkEntitiesTest.EDGE_AB).entity(ExtractWalkEntitiesTest.ENTITY_B).edge(ExtractWalkEntitiesTest.EDGE_BC).entity(ExtractWalkEntitiesTest.ENTITY_C).edge(ExtractWalkEntitiesTest.EDGE_CA).entity(ExtractWalkEntitiesTest.ENTITY_A).build();
        // When
        final Iterable<Set<Entity>> results = function.apply(walk);
        // Then
        Assert.assertThat(results, Matchers.containsInAnyOrder(Sets.newHashSet(ExtractWalkEntitiesTest.ENTITY_A), Sets.newHashSet(ExtractWalkEntitiesTest.ENTITY_B), Sets.newHashSet(ExtractWalkEntitiesTest.ENTITY_C), Sets.newHashSet(ExtractWalkEntitiesTest.ENTITY_A)));
    }
}

