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
package com.gs.collections.test.bag.mutable.sorted;


import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.test.IterableTestCase;
import com.gs.collections.test.MutableOrderedIterableTestCase;
import com.gs.collections.test.collection.mutable.MutableCollectionTestCase;
import org.junit.Assert;
import org.junit.Test;


public interface MutableSortedBagTestCase extends MutableOrderedIterableTestCase , SortedBagTestCase , MutableCollectionTestCase {
    @Test
    default void MutableBag_addOccurrences() {
        MutableSortedBag<Integer> mutableSortedBag = newWith(3, 3, 3, 2, 2, 1);
        mutableSortedBag.addOccurrences(4, 4);
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1), mutableSortedBag);
        mutableSortedBag.addOccurrences(1, 2);
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1, 1, 1), mutableSortedBag);
        mutableSortedBag.addOccurrences(1, 0);
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 4, 4, 4, 3, 3, 3, 2, 2, 1, 1, 1), mutableSortedBag);
    }

    @Test(expected = IllegalArgumentException.class)
    default void MutableBag_addOccurrences_throws() {
        MutableSortedBag<Integer> mutableSortedBag = newWith(3, 3, 3, 2, 2, 1);
        mutableSortedBag.addOccurrences(4, (-1));
    }

    @Test
    default void MutableBag_removeOccurrences() {
        MutableSortedBag<Integer> mutableBag = newWith(3, 3, 3, 2, 2, 1);
        Assert.assertFalse(mutableBag.removeOccurrences(4, 4));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1), mutableBag);
        Assert.assertFalse(mutableBag.removeOccurrences(3, 0));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2, 1), mutableBag);
        Assert.assertTrue(mutableBag.removeOccurrences(1, 2));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 3, 3, 2, 2), mutableBag);
        Assert.assertTrue(mutableBag.removeOccurrences(3, 2));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 2, 2), mutableBag);
        Assert.assertTrue(mutableBag.removeOccurrences(2, 1));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3, 2), mutableBag);
        Assert.assertTrue(mutableBag.removeOccurrences(2, 2));
        IterableTestCase.assertEquals(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 3), mutableBag);
    }

    @Test(expected = IllegalArgumentException.class)
    default void MutableBag_removeOccurrences_throws() {
        MutableSortedBag<Integer> mutableBag = newWith(3, 3, 3, 2, 2, 1);
        Assert.assertFalse(mutableBag.removeOccurrences(4, (-1)));
    }
}

