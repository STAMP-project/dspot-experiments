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
package com.gs.collections.impl.bag.sorted.immutable;


import SortedBags.immutable;
import SortedBags.mutable;
import com.gs.collections.api.bag.sorted.ImmutableSortedBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableSortedBagFactoryTest {
    @Test
    public void empty() {
        Assert.assertEquals(TreeBag.newBag(), immutable.empty());
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.empty());
        Assert.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), immutable.empty(Comparators.reverseNaturalOrder()));
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.empty(Comparators.reverseNaturalOrder()));
    }

    @Test
    public void ofElements() {
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.of(1, 1, 2)), immutable.of(1, 1, 2));
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.of(Comparators.reverseNaturalOrder(), 1, 1, 2)), immutable.of(Comparators.reverseNaturalOrder(), 1, 1, 2));
        Assert.assertEquals(TreeBag.newBag(), immutable.of());
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.of());
        Comparator<Integer> nullComparator = null;
        Assert.assertEquals(TreeBag.newBag(), immutable.of(nullComparator));
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.of(nullComparator));
        Assert.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), immutable.of(Comparator.reverseOrder()));
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.of(Comparator.reverseOrder()));
        Assert.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), immutable.of(Comparator.reverseOrder(), new Integer[]{  }));
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.of(Comparator.reverseOrder(), new Integer[]{  }));
        Assert.assertEquals(TreeBag.newBag(), immutable.of(new Integer[]{  }));
        Verify.assertInstanceOf(ImmutableSortedBag.class, immutable.of(new Integer[]{  }));
    }

    @Test
    public void withElements() {
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.with(1, 1, 2)), immutable.with(1, 1, 2));
        Verify.assertThrows(IllegalArgumentException.class, () -> {
            new ImmutableSortedBagImpl<>(SortedBags.mutable.with(Comparators.reverseNaturalOrder(), FastList.newList().toArray()));
        });
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.with(Comparators.reverseNaturalOrder(), 1, 1, 2)), immutable.with(Comparators.reverseNaturalOrder(), 1, 1, 2));
    }

    @Test
    public void ofAll() {
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.of(1, 1, 2)), immutable.ofAll(new ImmutableSortedBagImpl(TreeBag.newBagWith(1, 1, 2))));
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.of(1, 1, 2)), immutable.ofAll(FastList.newListWith(1, 1, 2)));
        Assert.assertEquals(new ImmutableSortedBagImpl(mutable.of(Comparators.reverseNaturalOrder(), 1, 1, 2)), immutable.ofAll(Comparators.reverseNaturalOrder(), FastList.newListWith(1, 1, 2)));
    }

    @Test
    public void ofSortedBag() {
        Assert.assertEquals(new ImmutableSortedBagImpl(immutable.of(1)), immutable.ofSortedBag(new ImmutableSortedBagImpl(TreeBag.newBagWith(1))));
        Assert.assertEquals(new ImmutableSortedBagImpl(immutable.of(1)), immutable.ofSortedBag(TreeBag.newBagWith(1)));
        Assert.assertEquals(immutable.of(Comparators.reverseNaturalOrder()), immutable.ofSortedBag(TreeBag.newBag(Comparators.reverseNaturalOrder())));
    }

    @Test
    public void withSortedBag() {
        Assert.assertEquals(new ImmutableSortedBagImpl(immutable.of(1)), immutable.ofSortedBag(new ImmutableSortedBagImpl(TreeBag.newBagWith(1))));
    }
}

