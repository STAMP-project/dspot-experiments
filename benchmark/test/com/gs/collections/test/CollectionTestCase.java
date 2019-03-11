/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.test;


import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface CollectionTestCase extends IterableTestCase {
    @Test
    default void Collection_size() {
        if (this.allowsDuplicates()) {
            Collection<Integer> collection = this.newWith(3, 3, 3, 2, 2, 1);
            Assert.assertThat(collection, Matchers.hasSize(6));
        } else {
            Collection<Integer> collection = this.newWith(3, 2, 1);
            Assert.assertThat(collection, Matchers.hasSize(3));
        }
        Assert.assertThat(this.newWith(), Matchers.hasSize(0));
    }

    @Test
    default void Collection_contains() {
        Collection<Integer> collection = this.newWith(3, 2, 1);
        Assert.assertTrue(collection.contains(1));
        Assert.assertTrue(collection.contains(2));
        Assert.assertTrue(collection.contains(3));
        Assert.assertFalse(collection.contains(4));
    }

    @Test
    default void Collection_add() {
        Collection<Integer> collection = this.newWith(3, 2, 1);
        Assert.assertTrue(collection.add(4));
        IterableTestCase.assertEquals(this.allowsDuplicates(), collection.add(4));
    }

    @Test
    default void Collection_clear() {
        Collection<Integer> collection = this.newWith(1, 2, 3);
        Assert.assertThat(collection, Matchers.is(Matchers.not(Matchers.empty())));
        collection.clear();
        Assert.assertThat(collection, Matchers.is(Matchers.empty()));
        Assert.assertThat(collection, Matchers.hasSize(0));
        collection.clear();
        Assert.assertThat(collection, Matchers.is(Matchers.empty()));
    }
}

