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
package com.gs.collections.impl.bag.immutable;


import Bags.immutable;
import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.tuple.primitive.ObjectIntPair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.factory.Iterables;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import static ImmutableArrayBag.MAXIMUM_USEFUL_ARRAY_BAG_SIZE;


public class ImmutableArrayBagTest extends ImmutableBagTestCase {
    @Override
    @Test
    public void testSize() {
        Verify.assertIterableSize(10, this.newBag());
    }

    @Override
    @Test
    public void newWith() {
        super.newWith();
        int maximumUsefulArrayBagSize = MAXIMUM_USEFUL_ARRAY_BAG_SIZE;
        Verify.assertInstanceOf(ImmutableArrayBag.class, immutable.ofAll(Interval.oneTo((maximumUsefulArrayBagSize - 1))).newWith(maximumUsefulArrayBagSize));
        Verify.assertInstanceOf(ImmutableHashBag.class, immutable.ofAll(Interval.oneTo(maximumUsefulArrayBagSize)).newWith((maximumUsefulArrayBagSize + 1)));
        Interval items = Interval.oneTo(maximumUsefulArrayBagSize);
        Verify.assertInstanceOf(ImmutableHashBag.class, new ImmutableArrayBag(items.toArray(), items.toIntArray()).newWith((maximumUsefulArrayBagSize + 1)));
    }

    @Override
    @Test
    public void newWithout() {
        super.newWithout();
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> newBag2 = bag.newWithout("2").newWithout("2");
        Assert.assertNotEquals(bag, newBag2);
        Assert.assertEquals(newBag2.size(), ((bag.size()) - 2));
        Assert.assertEquals(3, newBag2.sizeDistinct());
        ImmutableBag<String> newBag3 = bag.newWithout("3").newWithout("3").newWithout("3");
        Assert.assertNotEquals(bag, newBag3);
        Assert.assertEquals(newBag3.size(), ((bag.size()) - 3));
        Assert.assertEquals(3, newBag3.sizeDistinct());
        ImmutableBag<String> newBag4 = bag.newWithout("4").newWithout("4").newWithout("4").newWithout("4");
        Assert.assertNotEquals(bag, newBag4);
        Assert.assertEquals(newBag4.size(), ((bag.size()) - 4));
        Assert.assertEquals(3, newBag4.sizeDistinct());
        ImmutableBag<String> newBag5 = bag.newWithout("5");
        Assert.assertEquals(bag, newBag5);
    }

    @Override
    @Test
    public void toMap() {
        super.toMap();
        ImmutableBag<String> integers = this.newBag();
        MutableMap<String, String> map = integers.toMap(String::valueOf, String::valueOf);
        Assert.assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void testNewBag() {
        for (int i = 1; i <= ((MAXIMUM_USEFUL_ARRAY_BAG_SIZE) + 1); i++) {
            Interval interval = Interval.oneTo(i);
            Verify.assertEqualsAndHashCode(HashBag.newBag(interval), immutable.ofAll(interval));
        }
        Verify.assertThrows(IllegalArgumentException.class, () -> new ImmutableArrayBag<>(new Integer[]{ 2, 3 }, new int[]{ 2 }));
    }

    @Override
    @Test
    public void selectInstancesOf() {
        super.selectInstancesOf();
        ImmutableBag<Number> numbers = ImmutableArrayBag.<Number>newBagWith(1, 2.0, 2.0, 3, 3, 3, 4.0, 4.0, 4.0, 4.0);
        Assert.assertEquals(Iterables.iBag(1, 3, 3, 3), numbers.selectInstancesOf(Integer.class));
        Assert.assertEquals(Iterables.iBag(2.0, 2.0, 4.0, 4.0, 4.0, 4.0), numbers.selectInstancesOf(Double.class));
    }

    @Override
    @Test
    public void groupByUniqueKey() {
        // Only works on bags without duplicates
        ImmutableBag<Integer> immutableBag = ImmutableArrayBag.newBagWith(1, 2, 3);
        Assert.assertEquals(Maps.immutable.of(1, 1, 2, 2, 3, 3), immutableBag.groupByUniqueKey(( id) -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target() {
        // Only works on bags without duplicates
        ImmutableBag<Integer> immutableBag = ImmutableArrayBag.newBagWith(1, 2, 3);
        Assert.assertEquals(Maps.immutable.of(0, 0, 1, 1, 2, 2, 3, 3), immutableBag.groupByUniqueKey(( id) -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test
    public void topOccurrences() {
        MutableBag<String> mutable = HashBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        ImmutableBag<String> strings = ImmutableArrayBag.copyFrom(mutable);
        ImmutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertIterableSize(5, top5);
        Assert.assertEquals("ten", top5.getFirst().getOne());
        Assert.assertEquals(10, top5.getFirst().getTwo());
        Assert.assertEquals("six", top5.getLast().getOne());
        Assert.assertEquals(6, top5.getLast().getTwo());
        Verify.assertIterableSize(0, ImmutableArrayBag.newBagWith().topOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertIterableSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").topOccurrences(5));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").topOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three", "three").topOccurrences(1));
        Verify.assertIterableSize(0, this.newWith("one").newWithout("one").topOccurrences(0));
        Verify.assertIterableSize(0, this.newWith("one").topOccurrences(0));
        Verify.assertThrows(IllegalArgumentException.class, () -> this.newWith("one").topOccurrences((-1)));
    }

    @Test
    public void bottomOccurrences() {
        MutableBag<String> mutable = HashBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        ImmutableBag<String> strings = ImmutableArrayBag.copyFrom(mutable);
        ImmutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertIterableSize(5, bottom5);
        Assert.assertEquals("one", bottom5.getFirst().getOne());
        Assert.assertEquals(1, bottom5.getFirst().getTwo());
        Assert.assertEquals("five", bottom5.getLast().getOne());
        Assert.assertEquals(5, bottom5.getLast().getTwo());
        Verify.assertIterableSize(0, ImmutableArrayBag.newBagWith().bottomOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").bottomOccurrences(5));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").bottomOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "two", "three").bottomOccurrences(2));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three").bottomOccurrences(2));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").bottomOccurrences(5));
        Verify.assertIterableSize(3, this.newWith(null, "one", "two").bottomOccurrences(1));
        Verify.assertIterableSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertIterableSize(0, this.newWith("one").newWithout("one").bottomOccurrences(0));
        Verify.assertIterableSize(0, this.newWith("one").bottomOccurrences(0));
        Verify.assertThrows(IllegalArgumentException.class, () -> this.newWith("one").bottomOccurrences((-1)));
    }
}

