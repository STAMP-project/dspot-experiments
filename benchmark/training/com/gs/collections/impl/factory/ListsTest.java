/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.factory;


import Lists.fixedSize;
import Lists.immutable;
import Lists.mutable;
import com.gs.collections.api.factory.list.FixedSizeListFactory;
import com.gs.collections.api.factory.list.ImmutableListFactory;
import com.gs.collections.api.list.FixedSizeList;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static Lists.fixedSize;
import static Lists.immutable;


public class ListsTest {
    @Test
    public void immutables() {
        ImmutableListFactory listFactory = immutable;
        Assert.assertEquals(FastList.newList(), listFactory.of());
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of());
        Assert.assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1));
        Assert.assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2));
        Assert.assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assert.assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void immutableWithListTest() {
        Assert.assertEquals(mutable.of(), mutable.of().toImmutable());
        Assert.assertEquals(mutable.of(1).without(1), mutable.of(1).without(1).toImmutable());
        for (int i = 0; i < 12; i++) {
            MutableList<Integer> integers = Interval.fromTo(0, i).toList();
            Assert.assertEquals(integers, integers.toImmutable());
            Assert.assertEquals(integers.toImmutable(), integers.toImmutable());
        }
    }

    @Test
    public void fixedSize() {
        FixedSizeListFactory listFactory = fixedSize;
        Assert.assertEquals(FastList.newList(), listFactory.of());
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of());
        Assert.assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1));
        Assert.assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2));
        Assert.assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assert.assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void castToList() {
        List<Object> list = immutable.of().castToList();
        Assert.assertNotNull(list);
        Assert.assertSame(immutable.of(), list);
    }

    @Test
    public void newList() {
        for (int i = 1; i <= 11; i++) {
            Interval interval = Interval.oneTo(i);
            Verify.assertEqualsAndHashCode(FastList.newList(interval), immutable.ofAll(interval));
        }
    }

    @Test
    public void copyList() {
        Verify.assertInstanceOf(ImmutableList.class, immutable.ofAll(fixedSize.of()));
        MutableList<Integer> list = fixedSize.of(1);
        ImmutableList<Integer> immutableList = list.toImmutable();
        Verify.assertInstanceOf(ImmutableList.class, immutable.ofAll(list));
        Assert.assertSame(immutable.ofAll(immutableList.castToList()), immutableList);
    }

    @Test
    public void emptyList() {
        Assert.assertTrue(immutable.of().isEmpty());
        Assert.assertSame(immutable.of(), immutable.of());
        Verify.assertPostSerializedIdentity(immutable.of());
    }

    @Test
    public void newListWith() {
        ImmutableList<String> list = immutable.of();
        Assert.assertEquals(list, immutable.of(list.toArray()));
        Assert.assertEquals((list = list.newWith("1")), immutable.of("1"));
        Assert.assertEquals((list = list.newWith("2")), immutable.of("1", "2"));
        Assert.assertEquals((list = list.newWith("3")), immutable.of("1", "2", "3"));
        Assert.assertEquals((list = list.newWith("4")), immutable.of("1", "2", "3", "4"));
        Assert.assertEquals((list = list.newWith("5")), immutable.of("1", "2", "3", "4", "5"));
        Assert.assertEquals((list = list.newWith("6")), immutable.of("1", "2", "3", "4", "5", "6"));
        Assert.assertEquals((list = list.newWith("7")), immutable.of("1", "2", "3", "4", "5", "6", "7"));
        Assert.assertEquals((list = list.newWith("8")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        Assert.assertEquals((list = list.newWith("9")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        Assert.assertEquals((list = list.newWith("10")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Assert.assertEquals((list = list.newWith("11")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        Assert.assertEquals((list = list.newWith("12")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @Test
    public void newListWithArray() {
        ImmutableList<String> list = immutable.of();
        Assert.assertEquals((list = list.newWith("1")), immutable.of(new String[]{ "1" }));
        Assert.assertEquals((list = list.newWith("2")), immutable.of(new String[]{ "1", "2" }));
        Assert.assertEquals((list = list.newWith("3")), immutable.of(new String[]{ "1", "2", "3" }));
        Assert.assertEquals((list = list.newWith("4")), immutable.of(new String[]{ "1", "2", "3", "4" }));
        Assert.assertEquals((list = list.newWith("5")), immutable.of(new String[]{ "1", "2", "3", "4", "5" }));
        Assert.assertEquals((list = list.newWith("6")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6" }));
        Assert.assertEquals((list = list.newWith("7")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7" }));
        Assert.assertEquals((list = list.newWith("8")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8" }));
        Assert.assertEquals((list = list.newWith("9")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        Assert.assertEquals((list = list.newWith("10")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        Assert.assertEquals((list = list.newWith("11")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newListWithList() {
        ImmutableList<String> list = immutable.of();
        FastList<String> fastList = FastList.newListWith("1");
        Assert.assertEquals((list = list.newWith("1")), fastList.toImmutable());
        Assert.assertEquals((list = list.newWith("2")), fastList.with("2").toImmutable());
        Assert.assertEquals((list = list.newWith("3")), fastList.with("3").toImmutable());
        Assert.assertEquals((list = list.newWith("4")), fastList.with("4").toImmutable());
        Assert.assertEquals((list = list.newWith("5")), fastList.with("5").toImmutable());
        Assert.assertEquals((list = list.newWith("6")), fastList.with("6").toImmutable());
        Assert.assertEquals((list = list.newWith("7")), fastList.with("7").toImmutable());
        Assert.assertEquals((list = list.newWith("8")), fastList.with("8").toImmutable());
        Assert.assertEquals((list = list.newWith("9")), fastList.with("9").toImmutable());
        Assert.assertEquals((list = list.newWith("10")), fastList.with("10").toImmutable());
        Assert.assertEquals((list = list.newWith("11")), fastList.with("11").toImmutable());
    }

    @Test
    public void newListWithWithList() {
        Assert.assertEquals(FastList.newList(), immutable.ofAll(FastList.newList()));
        for (int i = 0; i < 12; i++) {
            List<Integer> list = Interval.fromTo(0, i);
            Assert.assertEquals(list, immutable.ofAll(list));
        }
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(Lists.class);
    }
}

