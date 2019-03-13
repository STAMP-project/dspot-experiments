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


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class Int2IntCounterMapTest {
    public static final int INITIAL_VALUE = 0;

    private final Int2IntCounterMap map = new Int2IntCounterMap(Int2IntCounterMapTest.INITIAL_VALUE);

    @Test
    public void shouldInitiallyBeEmpty() {
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void getShouldReturnInitialValueWhenEmpty() {
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(1));
    }

    @Test
    public void getShouldReturnInitialValueWhenThereIsNoElement() {
        map.put(1, 1);
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(2));
    }

    @Test
    public void getShouldReturnPutValues() {
        map.put(1, 1);
        Assert.assertEquals(1, map.get(1));
    }

    @Test
    public void putShouldReturnOldValue() {
        map.put(1, 1);
        Assert.assertEquals(1, map.put(1, 2));
    }

    @Test
    public void clearShouldResetSize() {
        map.put(1, 1);
        map.put(100, 100);
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void clearShouldRemoveValues() {
        map.put(1, 1);
        map.put(100, 100);
        map.clear();
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(1));
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(100));
    }

    @Test
    public void forEachShouldLoopOverEveryElement() {
        map.put(1, 1);
        map.put(100, 100);
        final IntIntConsumer mockConsumer = Mockito.mock(IntIntConsumer.class);
        map.forEach(mockConsumer);
        final InOrder inOrder = Mockito.inOrder(mockConsumer);
        inOrder.verify(mockConsumer).accept(1, 1);
        inOrder.verify(mockConsumer).accept(100, 100);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldNotContainKeyOfAMissingKey() {
        Assert.assertFalse(map.containsKey(1));
    }

    @Test
    public void shouldContainKeyOfAPresentKey() {
        map.put(1, 1);
        Assert.assertTrue(map.containsKey(1));
    }

    @Test
    public void shouldNotContainValueForAMissingEntry() {
        Assert.assertFalse(map.containsValue(1));
    }

    @Test
    public void shouldContainValueForAPresentEntry() {
        map.put(1, 1);
        Assert.assertTrue(map.containsValue(1));
    }

    @Test
    public void removeShouldReturnMissing() {
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.remove(1));
    }

    @Test
    public void removeShouldReturnValueRemoved() {
        map.put(1, 2);
        Assert.assertEquals(2, map.remove(1));
    }

    @Test
    public void removeShouldRemoveEntry() {
        map.put(1, 2);
        map.remove(1);
        Assert.assertTrue(map.isEmpty());
        Assert.assertFalse(map.containsKey(1));
        Assert.assertFalse(map.containsValue(2));
    }

    @Test
    public void shouldOnlyRemoveTheSpecifiedEntry() {
        IntStream.range(1, 8).forEach(( i) -> map.put(i, (i * 2)));
        map.remove(5);
        IntStream.range(1, 8).filter(( i) -> i != 5L).forEach(( i) -> {
            Assert.assertTrue(map.containsKey(i));
            Assert.assertTrue(map.containsValue((2 * i)));
        });
    }

    @Test
    public void shouldResizeWhenMoreElementsAreAdded() {
        IntStream.range(1, 100).forEach(( key) -> {
            final int value = key * 2;
            Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.put(key, value));
            Assert.assertEquals(value, map.get(key));
        });
    }

    @Test
    public void shouldHaveNoMinValueForEmptyCollection() {
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.minValue());
    }

    @Test
    public void shouldFindMinValue() {
        map.put(1, 2);
        map.put(2, 10);
        map.put(3, (-5));
        Assert.assertEquals((-5), map.minValue());
    }

    @Test
    public void shouldHaveNoMaxValueForEmptyCollection() {
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.maxValue());
    }

    @Test
    public void shouldFindMaxValue() {
        map.put(1, 2);
        map.put(2, 10);
        map.put(3, (-5));
        Assert.assertEquals(10, map.maxValue());
    }

    @Test
    public void sizeShouldReturnNumberOfEntries() {
        final int count = 100;
        for (int key = 0; key < count; key++) {
            map.put(key, 1);
        }
        Assert.assertEquals(count, map.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSupportLoadFactorOfGreaterThanOne() {
        new Int2IntHashMap(4, 2, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSupportLoadFactorOfOne() {
        new Int2IntHashMap(4, 1, 0);
    }

    @Test
    public void correctSizeAfterRehash() {
        final Int2IntHashMap map = new Int2IntHashMap(16, 0.6F, (-1));
        IntStream.range(1, 17).forEach(( i) -> map.put(i, i));
        Assert.assertEquals("Map has correct size", 16, map.size());
        final List<Integer> keys = new java.util.ArrayList(map.keySet());
        keys.forEach(map::remove);
        Assert.assertTrue("Map isn't empty", map.isEmpty());
    }

    @Test
    public void shouldComputeIfAbsent() {
        final int testKey = 7;
        final int testValue = 7;
        final IntUnaryOperator function = ( i) -> testValue;
        Assert.assertEquals(map.initialValue(), map.get(testKey));
        Assert.assertThat(map.computeIfAbsent(testKey, function), Is.is(testValue));
        Assert.assertThat(map.get(testKey), Is.is(testValue));
    }

    @Test
    public void shouldContainValueForIncAndDecEntries() {
        map.incrementAndGet(1);
        map.getAndIncrement(2);
        map.getAndAdd(3, 2);
        map.addAndGet(4, 3);
        map.decrementAndGet(5);
        map.getAndDecrement(6);
        Assert.assertTrue(map.containsKey(1));
        Assert.assertTrue(map.containsKey(2));
        Assert.assertTrue(map.containsKey(3));
        Assert.assertTrue(map.containsKey(4));
        Assert.assertTrue(map.containsKey(5));
        Assert.assertTrue(map.containsKey(6));
    }

    @Test
    public void shouldResultInEmptyAfterIncAndDecWhenEmpty() {
        map.incrementAndGet(1);
        map.decrementAndGet(1);
        Assert.assertTrue(map.isEmpty());
        map.getAndIncrement(1);
        map.getAndDecrement(1);
        Assert.assertTrue(map.isEmpty());
        map.incrementAndGet(1);
        map.getAndDecrement(1);
        Assert.assertTrue(map.isEmpty());
        map.getAndIncrement(1);
        map.decrementAndGet(1);
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void shouldResultInNotEmptyAfterIncAndDecWhenHaveKey() {
        map.put(1, 1);
        map.incrementAndGet(1);
        map.decrementAndGet(1);
        Assert.assertEquals(1, map.get(1));
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void shouldReturnInitialValueForGetAndAdd0IfKeyMissing() {
        final int val = map.getAndAdd(1, 0);
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, val);
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void shouldReturnOldValueForGetAndAdd0IfKeyExists() {
        map.put(1, 1);
        final int val = map.getAndAdd(1, 0);
        Assert.assertEquals(1, val);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void shouldReturnOldValueForGetAndAddNot0IfKeyExists() {
        map.put(1, 1);
        int amount;
        do {
            amount = ThreadLocalRandom.current().nextInt();
        } while (amount == 0 );
        final int val = map.getAndAdd(1, amount);
        Assert.assertEquals(1, val);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void shouldRemoveEntryAfterDecToInitialVal() {
        map.put(1, ((Int2IntCounterMapTest.INITIAL_VALUE) + 1));
        map.decrementAndGet(1);
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(1));
        Assert.assertTrue(map.isEmpty());
        Assert.assertFalse(map.containsKey(1));
    }

    @Test
    public void shouldRemoveEntryAfterIncToInitialVal() {
        map.put(1, ((Int2IntCounterMapTest.INITIAL_VALUE) - 1));
        map.incrementAndGet(1);
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(1));
        Assert.assertTrue(map.isEmpty());
        Assert.assertFalse(map.containsKey(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowInitialValueAsValue() {
        map.put(1, Int2IntCounterMapTest.INITIAL_VALUE);
    }

    @Test
    public void shouldAllowInitialValueAsKey() {
        map.put(Int2IntCounterMapTest.INITIAL_VALUE, 1);
        Assert.assertEquals(1, map.get(Int2IntCounterMapTest.INITIAL_VALUE));
        Assert.assertTrue(map.containsKey(Int2IntCounterMapTest.INITIAL_VALUE));
        Assert.assertEquals(1, map.size());
        final int[] tuple = new int[2];
        map.forEach(( k, v) -> {
            tuple[0] = k;
            tuple[1] = v;
        });
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, tuple[0]);
        Assert.assertEquals(1, tuple[1]);
        Assert.assertEquals(1, map.remove(Int2IntCounterMapTest.INITIAL_VALUE));
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(Int2IntCounterMapTest.INITIAL_VALUE, map.get(Int2IntCounterMapTest.INITIAL_VALUE));
    }

    @Test
    public void shouldNotContainInitialValue() {
        Assert.assertFalse(map.containsValue(Int2IntCounterMapTest.INITIAL_VALUE));
        map.put(Int2IntCounterMapTest.INITIAL_VALUE, 1);
        Assert.assertFalse(map.containsValue(Int2IntCounterMapTest.INITIAL_VALUE));
    }
}

