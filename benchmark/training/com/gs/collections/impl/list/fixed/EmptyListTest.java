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
package com.gs.collections.impl.list.fixed;


import Lists.fixedSize;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class EmptyListTest {
    @Test
    public void size() {
        Verify.assertSize(0, new EmptyList());
    }

    @Test
    public void empty() {
        Assert.assertTrue(new EmptyList().isEmpty());
        Assert.assertFalse(new EmptyList().notEmpty());
        Assert.assertTrue(fixedSize.of().isEmpty());
        Assert.assertFalse(fixedSize.of().notEmpty());
    }

    @Test
    public void getFirstLast() {
        Assert.assertNull(new EmptyList().getFirst());
        Assert.assertNull(new EmptyList().getLast());
    }

    @Test
    public void readResolve() {
        Verify.assertInstanceOf(EmptyList.class, fixedSize.of());
        Verify.assertPostSerializedIdentity(fixedSize.of());
    }

    @Test
    public void testClone() {
        Assert.assertSame(fixedSize.of().clone(), fixedSize.of());
    }

    @Test(expected = NoSuchElementException.class)
    public void min() {
        fixedSize.of().min(com.gs.collections.impl.block.factory.Comparators.naturalOrder());
    }

    @Test(expected = NoSuchElementException.class)
    public void max() {
        fixedSize.of().max(com.gs.collections.impl.block.factory.Comparators.naturalOrder());
    }

    @Test(expected = NoSuchElementException.class)
    public void min_without_comparator() {
        fixedSize.of().min();
    }

    @Test(expected = NoSuchElementException.class)
    public void max_without_comparator() {
        fixedSize.of().max();
    }

    @Test(expected = NoSuchElementException.class)
    public void minBy() {
        fixedSize.of().minBy(String::valueOf);
    }

    @Test(expected = NoSuchElementException.class)
    public void maxBy() {
        fixedSize.of().maxBy(String::valueOf);
    }

    @Test
    public void zip() {
        Assert.assertEquals(fixedSize.of(), fixedSize.of().zip(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void zipWithIndex() {
        Assert.assertEquals(fixedSize.of(), fixedSize.of().zipWithIndex());
    }

    @Test
    public void chunk_large_size() {
        Assert.assertEquals(fixedSize.of(), fixedSize.of().chunk(10));
    }

    @Test
    public void sortThis() {
        MutableList<Object> expected = fixedSize.of();
        MutableList<Object> list = fixedSize.of();
        MutableList<Object> sortedList = list.sortThis();
        Assert.assertEquals(expected, sortedList);
        Assert.assertSame(sortedList, list);
    }

    @Test
    public void sortThisBy() {
        MutableList<Object> expected = fixedSize.of();
        MutableList<Object> list = fixedSize.of();
        MutableList<Object> sortedList = list.sortThisBy(String::valueOf);
        Assert.assertEquals(expected, sortedList);
        Assert.assertSame(sortedList, list);
    }

    @Test
    public void with() {
        MutableList<Integer> list = new EmptyList<Integer>().with(1);
        Verify.assertListsEqual(FastList.newListWith(1), list);
        Verify.assertInstanceOf(SingletonList.class, list);
    }

    @Test
    public void withAll() {
        MutableList<Integer> list = new EmptyList<Integer>().withAll(FastList.newListWith(1, 2));
        Verify.assertListsEqual(FastList.newListWith(1, 2), list);
        Verify.assertInstanceOf(DoubletonList.class, list);
    }

    @Test
    public void without() {
        MutableList<Integer> list = new EmptyList();
        Assert.assertSame(list, list.without(2));
    }

    @Test
    public void withoutAll() {
        MutableList<Integer> list = new EmptyList();
        Assert.assertEquals(list, list.withoutAll(FastList.newListWith(1, 2)));
    }

    @Test
    public void collectPrimitives() {
        MutableList<Integer> list = new EmptyList();
        Verify.assertEmpty(list.collectBoolean(PrimitiveFunctions.integerIsPositive()));
        Verify.assertEmpty(list.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
        Verify.assertEmpty(list.collectChar(PrimitiveFunctions.unboxIntegerToChar()));
        Verify.assertEmpty(list.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
        Verify.assertEmpty(list.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
        Verify.assertEmpty(list.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
        Verify.assertEmpty(list.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
        Verify.assertEmpty(list.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }
}

