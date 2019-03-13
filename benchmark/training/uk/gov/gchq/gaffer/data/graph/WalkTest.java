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
package uk.gov.gchq.gaffer.data.graph;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import Walk.Builder;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;


public class WalkTest {
    private static final Edge EDGE_AB = new Edge.Builder().group(EDGE).source("A").dest("B").directed(true).build();

    private static final Edge EDGE_BC = new Edge.Builder().group(EDGE).source("B").dest("C").directed(true).build();

    private static final Edge EDGE_CB = new Edge.Builder().group(EDGE).source("C").dest("B").directed(true).build();

    private static final Edge EDGE_FC = new Edge.Builder().group(EDGE).source("F").dest("C").directed(true).build();

    private static final Edge EDGE_EF = new Edge.Builder().group(EDGE).source("E").dest("F").directed(true).build();

    private static final Edge EDGE_ED = new Edge.Builder().group(EDGE).source("E").dest("D").directed(true).build();

    private static final Edge EDGE_DA = new Edge.Builder().group(EDGE).source("D").dest("A").directed(true).build();

    private static final Edge EDGE_AE = new Edge.Builder().group(EDGE).source("A").dest("E").directed(true).build();

    private static final Entity ENTITY_A = new Entity.Builder().group(ENTITY).vertex("A").build();

    private static final Entity ENTITY_B = new Entity.Builder().group(ENTITY).vertex("B").build();

    private static final Entity ENTITY_C = new Entity.Builder().group(ENTITY).vertex("C").build();

    private static final Entity ENTITY_D = new Entity.Builder().group(ENTITY).vertex("D").build();

    private static final Entity ENTITY_E = new Entity.Builder().group(ENTITY).vertex("E").build();

    private static final Entity ENTITY_F = new Entity.Builder().group(ENTITY).vertex("F").build();

    private static final Entity ENTITY_G = new Entity.Builder().group(ENTITY).vertex("G").build();

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws Exception {
        // Given
        final Walk walk = new Walk.Builder().edge(WalkTest.EDGE_AB).entity(WalkTest.ENTITY_B).edge(WalkTest.EDGE_BC).build();
        // When
        final byte[] json = JSONSerialiser.serialise(walk);
        final Walk deserialisedWalk = JSONSerialiser.deserialise(json, Walk.class);
        // Then
        MatcherAssert.assertThat(walk, Is.is(Matchers.equalTo(deserialisedWalk)));
        JsonAssert.assertEquals(String.format(("{" + (((((((((((((((((((((((((((("  \"edges\": [" + "  [") + "    {\"group\": \"BasicEdge\",") + "     \"source\": \"A\",") + "     \"destination\": \"B\",") + "     \"directed\": true,") + "     \"properties\": {},") + "     \"class\": \"uk.gov.gchq.gaffer.data.element.Edge\"}") + "  ],") + "  [") + "    {\"group\": \"BasicEdge\",") + "     \"source\": \"B\",") + "     \"destination\": \"C\",") + "     \"directed\": true,") + "     \"properties\": {},") + "     \"class\": \"uk.gov.gchq.gaffer.data.element.Edge\"}") + "    ]") + "  ],") + "  \"entities\": [") + "    {\"A\": []},") + "    {\"B\": [") + "      {\"group\": \"BasicEntity\",") + "      \"vertex\": \"B\",") + "      \"properties\": {},") + "      \"class\": \"uk.gov.gchq.gaffer.data.element.Entity\"}]") + "    },") + "    {\"C\": []}") + "  ]") + "}\n"))), new String(json));
    }

    @Test
    public void shouldFailToAddEdgeWithInvalidEntitySource() {
        // Given
        final Walk.Builder builder = new Walk.Builder().entity(WalkTest.ENTITY_A);
        // When
        try {
            builder.edge(WalkTest.EDGE_BC);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid edge.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Edge must continue the current walk."));
        }
    }

    @Test
    public void shouldFailToAddEdgeWithInvalidEdgeSource() {
        // Given
        final Walk.Builder builder = new Walk.Builder().edge(WalkTest.EDGE_AB);
        // When
        try {
            builder.edge(WalkTest.EDGE_AB);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid edge.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Edge must continue the current walk."));
        }
    }

    @Test
    public void shouldFailToAddEntityWithInvalidEdgeSource() {
        // Given
        final Walk.Builder builder = new Walk.Builder().edge(WalkTest.EDGE_AB);
        // When
        try {
            builder.entity(WalkTest.ENTITY_A);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid entity.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Entity must be added to correct vertex."));
        }
    }

    @Test
    public void shouldFailToAddEntityWithInvalidEntitySource() {
        // Given
        final Walk.Builder builder = new Walk.Builder().entity(WalkTest.ENTITY_A);
        // When
        try {
            builder.entity(WalkTest.ENTITY_B);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid entity.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Entity must be added to correct vertex."));
        }
    }

    @Test
    public void shouldFailToAddEntitiesWithDifferentVertices() {
        // Given
        final Walk.Builder builder = new Walk.Builder();
        // When
        try {
            builder.entities(WalkTest.ENTITY_A, WalkTest.ENTITY_B);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid entity.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Entities must all have the same vertex."));
        }
    }

    @Test
    public void shouldFailToAddEntitiesWithInvalidEdgeSource() {
        // Given
        final Walk.Builder builder = new Walk.Builder().edge(WalkTest.EDGE_AB);
        // When
        try {
            builder.entities(WalkTest.ENTITY_A, WalkTest.ENTITY_A);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid entity.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Entity must be added to correct vertex."));
        }
    }

    @Test
    public void shouldFailToAddEntitiesWithInvalidEntitySource() {
        // Given
        final Walk.Builder builder = new Walk.Builder().entity(WalkTest.ENTITY_A);
        // When
        try {
            builder.entities(WalkTest.ENTITY_B, WalkTest.ENTITY_B);
            Assert.fail("Expecting exception to be thrown when attempting to add an invalid entity.");
        } catch (final Exception e) {
            // Then
            MatcherAssert.assertThat(e, Is.is(IsInstanceOf.instanceOf(IllegalArgumentException.class)));
            MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Entity must be added to correct vertex."));
        }
    }

    @Test
    public void shouldBuildWalkStartingWithEdge() {
        // Given
        // [A] -> [B] -> [C]
        // \
        // (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().edge(WalkTest.EDGE_AB).entity(WalkTest.ENTITY_B).edge(WalkTest.EDGE_BC).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesAsEntries(), hasSize(3));// A, B, C

        MatcherAssert.assertThat(walk.getEdges(), hasSize(2));// A -> B, B -> C

        MatcherAssert.assertThat(walk.getEdges().stream().flatMap(Set::stream).collect(Collectors.toList()), contains(WalkTest.EDGE_AB, WalkTest.EDGE_BC));
        MatcherAssert.assertThat(walk.getEntities(), contains(Collections.emptySet(), Sets.newHashSet(WalkTest.ENTITY_B), Collections.emptySet()));
        MatcherAssert.assertThat(walk.getVerticesOrdered(), contains("A", "B", "C"));
    }

    @Test
    public void shouldBuildWalkStartingWithEntity() {
        // Given
        // [A] -> [B] -> [C]
        // \             \
        // (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edges(WalkTest.EDGE_AB, WalkTest.EDGE_BC).entity(WalkTest.ENTITY_C).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesAsEntries(), hasSize(3));// A, B, C

        MatcherAssert.assertThat(walk.getEdges(), hasSize(2));// A -> B, B -> C

        MatcherAssert.assertThat(walk.getEdges().stream().flatMap(Set::stream).collect(Collectors.toList()), contains(WalkTest.EDGE_AB, WalkTest.EDGE_BC));
        MatcherAssert.assertThat(walk.getEntities(), contains(Sets.newHashSet(WalkTest.ENTITY_A), Collections.emptySet(), Sets.newHashSet(WalkTest.ENTITY_C)));
        MatcherAssert.assertThat(walk.getVerticesOrdered(), contains("A", "B", "C"));
    }

    @Test
    public void shouldBuildWalkStartingWithEntities() {
        // Given
        // [A] -> [B] -> [C]
        // \             \
        // (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entities(WalkTest.ENTITY_A, WalkTest.ENTITY_A).edges(WalkTest.EDGE_AB, WalkTest.EDGE_BC).entity(WalkTest.ENTITY_C).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesAsEntries(), hasSize(3));// A, B, C

        MatcherAssert.assertThat(walk.getEdges(), hasSize(2));// A -> B, B -> C

        MatcherAssert.assertThat(walk.getEdges().stream().flatMap(Set::stream).collect(Collectors.toList()), contains(WalkTest.EDGE_AB, WalkTest.EDGE_BC));
        MatcherAssert.assertThat(walk.getEntities(), contains(Sets.newHashSet(WalkTest.ENTITY_A, WalkTest.ENTITY_A), Collections.emptySet(), Sets.newHashSet(WalkTest.ENTITY_C)));
        MatcherAssert.assertThat(walk.getVerticesOrdered(), contains("A", "B", "C"));
    }

    @Test
    public void shouldBuildWalkWithLoop() {
        // Given
        // [A] -> [E] -> [D] -> [A]
        // \             \
        // (BasicEntity) (BasicEntity, BasicEntity)
        // When
        final Walk walk = new Walk.Builder().edge(WalkTest.EDGE_AE).entity(WalkTest.ENTITY_E).edges(WalkTest.EDGE_ED, WalkTest.EDGE_DA).entities(WalkTest.ENTITY_A, WalkTest.ENTITY_A).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesAsEntries(), hasSize(4));// A, D, E, A

        MatcherAssert.assertThat(walk.getEdges(), hasSize(3));// A -> E, E -> D, D -> A

        MatcherAssert.assertThat(walk.getEdges().stream().flatMap(Set::stream).collect(Collectors.toList()), contains(WalkTest.EDGE_AE, WalkTest.EDGE_ED, WalkTest.EDGE_DA));
        MatcherAssert.assertThat(walk.getEntities(), contains(Collections.emptySet(), Sets.newHashSet(WalkTest.ENTITY_E), Collections.emptySet(), Sets.newHashSet(WalkTest.ENTITY_A, WalkTest.ENTITY_A)));
        MatcherAssert.assertThat(walk.getVerticesOrdered(), contains("A", "E", "D", "A"));
    }

    @Test
    public void shouldAddEmptyIterableOfEntities() {
        // Given
        // [A] -> [E] -> [D] -> [A]
        // \             \
        // (BasicEntity) (EmptyIterable)
        // When
        final Walk walk = new Walk.Builder().edge(WalkTest.EDGE_AE).entity(WalkTest.ENTITY_E).edges(WalkTest.EDGE_ED, WalkTest.EDGE_DA).entities(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesAsEntries(), hasSize(4));// A, D, E, A

        MatcherAssert.assertThat(walk.getEdges(), hasSize(3));// A -> E, E -> D, D -> A

        MatcherAssert.assertThat(walk.getEdges().stream().flatMap(Set::stream).collect(Collectors.toList()), contains(WalkTest.EDGE_AE, WalkTest.EDGE_ED, WalkTest.EDGE_DA));
        MatcherAssert.assertThat(walk.getEntities(), contains(Collections.emptySet(), Sets.newHashSet(WalkTest.ENTITY_E), Collections.emptySet(), Collections.emptySet()));
        MatcherAssert.assertThat(walk.getVerticesOrdered(), contains("A", "E", "D", "A"));
    }

    @Test
    public void shouldGetEntitiesForVertices() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesForVertex("E"), hasSize(1));
        MatcherAssert.assertThat(walk.getEntitiesForVertex("E"), contains(WalkTest.ENTITY_E));
    }

    @Test
    public void shouldGetEntitiesAtDistance() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E, WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // Then
        MatcherAssert.assertThat(walk.getEntitiesAtDistance(2), hasSize(1));
        MatcherAssert.assertThat(walk.getEntitiesAtDistance(2), contains(WalkTest.ENTITY_D));
    }

    @Test
    public void shouldGetVertexSet() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E, WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // Then
        MatcherAssert.assertThat(walk.getVertexSet(), IsCollectionContaining.hasItems("A", "E", "D"));
    }

    @Test
    public void shouldGetLength() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E, WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // Then
        MatcherAssert.assertThat(walk.length(), Is.is(2));
    }

    @Test
    public void shouldGetTrail() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E, WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // Then
        MatcherAssert.assertThat(walk.isTrail(), Is.is(true));
    }

    @Test
    public void shouldGetNotTrail() {
        // Given
        // [A] -> [B] -> [C] -> [B] -> [C]
        // When
        final Walk walk = new Walk.Builder().edge(WalkTest.EDGE_AB).edge(WalkTest.EDGE_BC).edge(WalkTest.EDGE_CB).edge(WalkTest.EDGE_BC).build();
        // Then
        MatcherAssert.assertThat(walk.isTrail(), Is.is(false));
    }

    @Test
    public void shouldGetPath() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        // When
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E, WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // Then
        MatcherAssert.assertThat(walk.isPath(), Is.is(true));
    }

    @Test
    public void shouldGetNotPath() {
        // Given
        // [A] -> [B] -> [C] -> [B]
        // When
        final Walk walk = new Walk.Builder().edge(WalkTest.EDGE_AB).edge(WalkTest.EDGE_BC).edge(WalkTest.EDGE_CB).build();
        // Then
        MatcherAssert.assertThat(walk.isPath(), Is.is(false));
    }

    @Test
    public void shouldGetSourceVertexFromWalk() {
        // Given
        // [A] -> [B] -> [C]
        // \             \
        // (BasicEntity) (BasicEntity)
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edges(WalkTest.EDGE_AB, WalkTest.EDGE_BC).entity(WalkTest.ENTITY_C).build();
        // When
        final Object result = walk.getSourceVertex();
        // Then
        Assert.assertEquals("A", result);
    }

    @Test
    public void shouldGetDestinationVertexFromWalk() {
        // Given
        // [A]     ->    [E]     ->    [D]
        // \             \             \
        // (BasicEntity) (BasicEntity) (BasicEntity)
        final Walk walk = new Walk.Builder().entity(WalkTest.ENTITY_A).edge(WalkTest.EDGE_AE).entities(WalkTest.ENTITY_E).edge(WalkTest.EDGE_ED).entity(WalkTest.ENTITY_D).build();
        // When
        final Object result = walk.getDestinationVertex();
        // Then
        Assert.assertEquals("D", result);
    }

    @Test
    public void shouldGetSourceVertexFromWalkWithNoEntities() {
        // Given
        // [A] -> [B] -> [C]
        final Walk walk = new Walk.Builder().edges(WalkTest.EDGE_AB, WalkTest.EDGE_BC).build();
        // When
        final Object result = walk.getSourceVertex();
        // Then
        Assert.assertEquals("A", result);
    }

    @Test
    public void shouldGetDestinationVertexFromWalkWithNoEntities() {
        // Given
        // [A] -> [B] -> [C]
        final Walk walk = new Walk.Builder().edges(WalkTest.EDGE_AB, WalkTest.EDGE_BC).build();
        // When
        final Object result = walk.getDestinationVertex();
        // Then
        Assert.assertEquals("C", result);
    }
}

