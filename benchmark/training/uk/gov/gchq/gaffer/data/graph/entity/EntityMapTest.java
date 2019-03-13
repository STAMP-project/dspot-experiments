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
package uk.gov.gchq.gaffer.data.graph.entity;


import com.google.common.collect.Sets;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;


public class EntityMapTest {
    @Test
    public void shouldGetEntities() {
        // Given
        final EntityMap entityMap = getEntityMap();
        // When
        final Set<Object> results = entityMap.getVertices();
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(1));
    }

    @Test
    public void shouldGetEmptyEntitySet() {
        // Given
        final EntityMap entityMap = new EntityMap();
        // When
        final Set<Object> results = entityMap.getVertices();
        // Then
        MatcherAssert.assertThat(results, Is.is(Matchers.empty()));
    }

    @Test
    public void shouldGetVertices() {
        // Given
        final EntityMap entityMap = getEntityMap();
        // When
        final Set<Object> results = entityMap.getVertices();
        // Then
        MatcherAssert.assertThat(results, IsCollectionContaining.hasItems(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void shouldContainVertex() {
        // Given
        final EntityMap entityMap = getEntityMap();
        // When
        final boolean results = entityMap.containsVertex(6);
        // Then
        MatcherAssert.assertThat(results, Is.is(true));
    }

    @Test
    public void shouldNotContainVertex() {
        // Given
        final EntityMap entityMap = getEntityMap();
        // When
        final boolean results = entityMap.containsVertex(7);
        // Then
        MatcherAssert.assertThat(results, Is.is(false));
    }

    @Test
    public void shouldPutSingleEntity() {
        // Given
        final EntityMap entityMap = new EntityMap();
        // When
        entityMap.putEntity(1, new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1));
        // Then
        MatcherAssert.assertThat(entityMap.containsVertex(1), Is.is(true));
        MatcherAssert.assertThat(entityMap.get(1), IsCollectionContaining.hasItems(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1)));
        MatcherAssert.assertThat(entityMap.getVertices(), IsCollectionContaining.hasItems(1));
    }

    @Test
    public void shouldPutMultipleEntities() {
        // Given
        final EntityMap entityMap = new EntityMap();
        // When
        entityMap.putEntities(1, Sets.newHashSet(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        // Then
        MatcherAssert.assertThat(entityMap.containsVertex(1), Is.is(true));
        MatcherAssert.assertThat(entityMap.get(1), IsCollectionContaining.hasItems(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        MatcherAssert.assertThat(entityMap.getVertices(), IsCollectionContaining.hasItems(1));
    }

    @Test
    public void shouldPutMultipleEntities_2() {
        // Given
        final EntityMap entityMap = new EntityMap();
        // When
        entityMap.putEntities(1, Sets.newHashSet(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        entityMap.putEntities(1, Sets.newHashSet(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 4), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 5), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 6)));
        // Then
        MatcherAssert.assertThat(entityMap.containsVertex(1), Is.is(true));
        MatcherAssert.assertThat(entityMap.get(1), IsCollectionContaining.hasItems(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 4), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 5), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 6)));
        MatcherAssert.assertThat(entityMap.getVertices(), IsCollectionContaining.hasItems(1));
    }

    @Test
    public void shouldPutMultipleEntities_3() {
        // Given
        final EntityMap entityMap = new EntityMap();
        // When
        entityMap.putEntities(1, Sets.newHashSet(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        entityMap.putEntities(2, Sets.newHashSet(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        // Then
        MatcherAssert.assertThat(entityMap.containsVertex(1), Is.is(true));
        MatcherAssert.assertThat(entityMap.get(1), IsCollectionContaining.hasItems(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        MatcherAssert.assertThat(entityMap.get(2), IsCollectionContaining.hasItems(new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 1), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 2), new uk.gov.gchq.gaffer.data.element.Entity(TestGroups.ENTITY, 3)));
        MatcherAssert.assertThat(entityMap.getVertices(), IsCollectionContaining.hasItems(1, 2));
    }
}

