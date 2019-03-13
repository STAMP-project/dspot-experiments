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
package com.gs.collections.test.collection.mutable;


import Lists.immutable;
import com.gs.collections.api.collection.ImmutableCollection;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.test.CollectionTestCase;
import com.gs.collections.test.IterableTestCase;
import com.gs.collections.test.RichIterableTestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public interface MutableCollectionTestCase extends CollectionTestCase , RichIterableTestCase {
    @Test
    default void MutableCollection_iterationOrder() {
        MutableCollection<Integer> injectIntoWithIterationOrder = this.newMutableForFilter();
        this.getInstanceUnderTest().injectIntoWith(0, ( a, b, c) -> {
            injectIntoWithIterationOrder.add(b);
            return 0;
        }, 0);
        IterableTestCase.assertEquals(this.newMutableForFilter(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), injectIntoWithIterationOrder);
    }

    @Test
    default void MutableCollection_sanity_check() {
        String s = "";
        MutableCollection<String> collection = newWith();
        Assert.assertTrue(collection.add(s));
        IterableTestCase.assertEquals(this.allowsDuplicates(), collection.add(s));
        IterableTestCase.assertEquals((this.allowsDuplicates() ? 2 : 1), collection.size());
    }

    @Test
    default void MutableCollection_toImmutable() {
        Assert.assertThat(this.newWith(), Matchers.instanceOf(MutableCollection.class));
        Assert.assertThat(this.newWith().toImmutable(), Matchers.instanceOf(ImmutableCollection.class));
    }

    @Test
    default void MutableCollection_removeIf() {
        MutableCollection<Integer> collection1 = newWith(5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
        Assert.assertTrue(collection1.removeIf(Predicates.cast(( each) -> (each % 2) == 0)));
        IterableTestCase.assertEquals(this.getExpectedFiltered(5, 5, 3, 3, 1, 1), collection1);
        MutableCollection<Integer> collection2 = newWith(1, 2, 3);
        Assert.assertFalse(collection2.removeIf(Predicates.cast(( each) -> each > 4)));
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 2, 3), collection2);
        Assert.assertTrue(collection2.removeIf(Predicates.cast(( each) -> each > 0)));
        MutableCollection<Integer> collection3 = newWith();
        Assert.assertFalse(collection3.removeIf(Predicates.cast(( each) -> (each % 2) == 0)));
        IterableTestCase.assertEquals(this.getExpectedFiltered(), collection3);
        MutableCollection<Integer> collection4 = newWith(2, 2, 4, 6);
        Assert.assertTrue(collection4.removeIf(Predicates.cast(( each) -> (each % 2) == 0)));
        IterableTestCase.assertEquals(this.getExpectedFiltered(), collection4);
        Assert.assertFalse(collection4.removeIf(Predicates.cast(( each) -> (each % 2) == 0)));
    }

    @Test
    default void MutableCollection_removeIfWith() {
        MutableCollection<Integer> collection1 = newWith(5, 5, 4, 4, 3, 3, 2, 2, 1, 1);
        Assert.assertTrue(collection1.removeIfWith(Predicates2.<Integer>in(), immutable.with(5, 3, 1)));
        IterableTestCase.assertEquals(this.getExpectedFiltered(4, 4, 2, 2), collection1);
        MutableCollection<Integer> collection2 = newWith(1, 2, 3);
        Assert.assertFalse(collection2.removeIfWith(Predicates2.<Integer>in(), immutable.with(4)));
        IterableTestCase.assertEquals(this.getExpectedFiltered(1, 2, 3), collection2);
        Assert.assertTrue(collection2.removeIfWith(Predicates2.<Integer>in(), immutable.with(1, 2, 3)));
        MutableCollection<Integer> collection3 = newWith();
        Assert.assertFalse(collection3.removeIfWith(Predicates2.<Integer>in(), immutable.with()));
        IterableTestCase.assertEquals(this.getExpectedFiltered(), collection3);
        MutableCollection<Integer> collection4 = newWith(2, 2, 4, 6);
        Assert.assertTrue(collection4.removeIfWith(Predicates2.greaterThan(), 1));
        IterableTestCase.assertEquals(this.getExpectedFiltered(), collection4);
        Assert.assertFalse(collection4.removeIfWith(Predicates2.greaterThan(), 1));
    }

    @Test
    default void MutableCollection_injectIntoWith() {
        MutableCollection<Integer> collection = newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        IterableTestCase.assertEquals(Integer.valueOf(81), collection.injectIntoWith(1, ( a, b, c) -> (a + b) + c, 5));
    }
}

