/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona.collections;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import static IntArrayList.INITIAL_CAPACITY;


public class IntArrayListTest {
    private final IntArrayList list = new IntArrayList();

    @Test
    public void shouldReportEmpty() {
        Assert.assertThat(list.size(), CoreMatchers.is(0));
        Assert.assertThat(list.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldAddValue() {
        list.add(7);
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertThat(list.get(0), CoreMatchers.is(7));
    }

    @Test
    public void shouldAddNull() {
        list.add(null);
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertNull(list.get(0));
    }

    @Test
    public void shouldAddIntValue() {
        list.addInt(7);
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertThat(list.getInt(0), CoreMatchers.is(7));
    }

    @Test
    public void shouldAddValueAtIndex() {
        final int count = 20;
        IntStream.range(0, count).forEachOrdered(list::addInt);
        list.addInt(10, 777);
        Assert.assertThat(list.size(), CoreMatchers.is((count + 1)));
        Assert.assertThat(list.getInt(10), CoreMatchers.is(777));
        Assert.assertThat(list.getInt(count), CoreMatchers.is((count - 1)));
    }

    @Test
    public void shouldAddValueAtIndexWithNearlyFullCapacity() {
        final int count = (INITIAL_CAPACITY) - 1;
        final int value = count + 1;
        IntStream.range(0, count).forEachOrdered(list::addInt);
        list.addInt(0, value);
        Assert.assertThat(list.size(), CoreMatchers.is((count + 1)));
        Assert.assertThat(list.getInt(0), CoreMatchers.is(value));
        Assert.assertThat(list.getInt(count), CoreMatchers.is((count - 1)));
    }

    @Test
    public void shouldSetIntValue() {
        list.addInt(7);
        list.setInt(0, 8);
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertThat(list.getInt(0), CoreMatchers.is(8));
    }

    @Test
    public void shouldSetValue() {
        list.add(7);
        list.set(0, 8);
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertThat(list.getInt(0), CoreMatchers.is(8));
    }

    @Test
    public void shouldContainCorrectValues() {
        final int count = 20;
        IntStream.range(0, count).forEachOrdered(list::addInt);
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(list.containsInt(i));
        }
        Assert.assertFalse(list.containsInt((-1)));
        Assert.assertFalse(list.containsInt(20));
    }

    @Test
    public void shouldRemoveAtIndex() {
        final int count = 20;
        IntStream.range(0, count).forEachOrdered(list::addInt);
        Assert.assertThat(list.remove(10), CoreMatchers.is(10));
        Assert.assertThat(list.size(), CoreMatchers.is((count - 1)));
        Assert.assertThat(list.getInt(10), CoreMatchers.is(11));
    }

    @Test
    public void shouldRemoveAtIndexForListLengthOne() {
        list.addInt(1);
        Assert.assertThat(list.fastUnorderedRemove(0), CoreMatchers.is(1));
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void shouldFastRemoveUnorderedAtIndex() {
        final int count = 20;
        IntStream.range(0, count).forEachOrdered(list::addInt);
        Assert.assertThat(list.fastUnorderedRemove(10), CoreMatchers.is(10));
        Assert.assertThat(list.size(), CoreMatchers.is((count - 1)));
        Assert.assertThat(list.getInt(10), CoreMatchers.is(19));
    }

    @Test
    public void shouldFastRemoveUnorderedByValue() {
        final int count = 20;
        IntStream.range(0, count).forEachOrdered(( value) -> list.addInt((value * 10)));
        Assert.assertTrue(list.fastUnorderedRemoveInt(10));
        Assert.assertThat(list.size(), CoreMatchers.is((count - 1)));
        Assert.assertThat(list.getInt(1), CoreMatchers.is(190));
    }

    @Test
    public void shouldForEachOrderedInt() {
        final List<Integer> expected = new ArrayList<>();
        IntStream.range(0, 20).forEachOrdered(expected::add);
        list.addAll(expected);
        final List<Integer> actual = new ArrayList<>();
        list.forEachOrderedInt(actual::add);
        Assert.assertThat(actual, CoreMatchers.is(expected));
    }

    @Test
    public void shouldCreateObjectRefArray() {
        final int count = 20;
        final List<Integer> expected = new ArrayList<>();
        IntStream.range(0, count).forEachOrdered(expected::add);
        list.addAll(expected);
        Assert.assertArrayEquals(expected.toArray(), list.toArray());
    }

    @Test
    public void shouldCreateIntArray() {
        final int count = 20;
        final int[] expected = new int[count];
        for (int i = 0; i < count; i++) {
            list.add(i);
            expected[i] = i;
        }
        Assert.assertArrayEquals(expected, list.toIntArray());
        final int[] copy = new int[count];
        final int[] result = list.toIntArray(copy);
        Assert.assertSame(copy, result);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void shouldCreateIntegerArray() {
        final int count = 20;
        final Integer[] expected = new Integer[count];
        for (int i = 0; i < count; i++) {
            list.add(i);
            expected[i] = i;
        }
        final Integer[] integers = list.toArray(new Integer[0]);
        Assert.assertEquals(expected.getClass(), integers.getClass());
        Assert.assertArrayEquals(expected, integers);
    }

    @Test
    public void shouldPushAndThenPopInOrder() {
        final int count = 7;
        for (int i = 0; i < count; i++) {
            list.pushInt(i);
        }
        for (int i = count - 1; i >= 0; i--) {
            Assert.assertThat(list.popInt(), CoreMatchers.is(i));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowExceptionWhenPoppingEmptyList() {
        list.popInt();
    }

    @Test
    public void shouldEqualGenericList() {
        final int count = 7;
        final List<Integer> genericList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i);
            genericList.add(i);
        }
        list.add(null);
        genericList.add(null);
        Assert.assertEquals(list, genericList);
    }

    @Test
    public void shouldGenerateStringRepresentation() {
        final int[] testEntries = new int[]{ 3, 1, -1, 19, 7, 11, 12, 7 };
        for (final int testEntry : testEntries) {
            list.add(testEntry);
        }
        final String mapAsAString = "[3, 1, -1, 19, 7, 11, 12, 7]";
        Assert.assertThat(list.toString(), IsEqual.equalTo(mapAsAString));
    }
}

