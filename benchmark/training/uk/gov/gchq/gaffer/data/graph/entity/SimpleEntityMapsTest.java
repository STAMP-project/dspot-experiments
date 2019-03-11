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


import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class SimpleEntityMapsTest {
    @Test
    public void shouldIterate() {
        // When
        final EntityMaps entityMaps = getEntityMaps();
        // Then
        final Iterator<EntityMap> it = entityMaps.iterator();
        final EntityMap first = it.next();
        final EntityMap second = it.next();
        MatcherAssert.assertThat(first.getVertices(), hasSize(3));
        MatcherAssert.assertThat(second.getVertices(), hasSize(4));
    }

    @Test
    public void shouldGetSize() {
        // When
        final EntityMaps entityMaps = getEntityMaps();
        // Then
        MatcherAssert.assertThat(entityMaps.size(), Is.is(Matchers.equalTo(2)));
    }

    @Test
    public void shouldGetNth() {
        // When
        final EntityMaps entityMaps = getEntityMaps();
        // Then
        MatcherAssert.assertThat(entityMaps.get(0).get(0), Matchers.hasItem(makeEntity(0)));
        MatcherAssert.assertThat(entityMaps.get(0).get(1), Matchers.hasItem(makeEntity(1)));
        MatcherAssert.assertThat(entityMaps.get(0).get(2), Matchers.hasItem(makeEntity(2)));
        MatcherAssert.assertThat(entityMaps.get(1).get(0), Matchers.hasItem(makeEntity(0)));
        MatcherAssert.assertThat(entityMaps.get(1).get(1), Matchers.hasItem(makeEntity(1)));
        MatcherAssert.assertThat(entityMaps.get(1).get(2), Matchers.hasItem(makeEntity(2)));
        MatcherAssert.assertThat(entityMaps.get(1).get(3), Matchers.hasItem(makeEntity(3)));
    }

    @Test
    public void shouldCheckEmpty() {
        // When
        final EntityMaps first = new SimpleEntityMaps();
        final EntityMaps second = new SimpleEntityMaps();
        second.add(getEntityMap(3));
        // Then
        Assert.assertTrue(first.empty());
        Assert.assertFalse(second.empty());
    }
}

