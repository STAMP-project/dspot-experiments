/**
 * Original work Copyright 2015 Real Logic Ltd.
 * Modified work Copyright (c) 2015-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.util.collection;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.collection.Long2LongHashMap.LongLongCursor;
import com.hazelcast.util.function.LongLongConsumer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Long2LongHashMapTest {
    public static final long MISSING_VALUE = -1L;

    private Long2LongHashMap map = new Long2LongHashMap(Long2LongHashMapTest.MISSING_VALUE);

    @Test
    public void shouldInitiallyBeEmpty() {
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void getShouldReturnMissingValueWhenEmpty() {
        Assert.assertEquals(Long2LongHashMapTest.MISSING_VALUE, map.get(1L));
    }

    @Test
    public void getShouldReturnMissingValueWhenThereIsNoElement() {
        map.put(1L, 1L);
        Assert.assertEquals(Long2LongHashMapTest.MISSING_VALUE, map.get(2L));
    }

    @Test
    public void getShouldReturnPutValues() {
        map.put(1L, 1L);
        Assert.assertEquals(1L, map.get(1L));
    }

    @Test
    public void putShouldReturnOldValue() {
        map.put(1L, 1L);
        Assert.assertEquals(1L, map.put(1L, 2L));
    }

    @Test
    public void clearShouldResetSize() {
        map.put(1L, 1L);
        map.put(100L, 100L);
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void clearShouldRemoveValues() {
        map.put(1L, 1L);
        map.put(100L, 100L);
        map.clear();
        Assert.assertEquals(Long2LongHashMapTest.MISSING_VALUE, map.get(1L));
        Assert.assertEquals(Long2LongHashMapTest.MISSING_VALUE, map.get(100L));
    }

    @Test
    public void forEachShouldLoopOverEveryElement() {
        map.put(1L, 1L);
        map.put(100L, 100L);
        final LongLongConsumer mockConsumer = Mockito.mock(LongLongConsumer.class);
        map.longForEach(mockConsumer);
        final InOrder inOrder = Mockito.inOrder(mockConsumer);
        inOrder.verify(mockConsumer).accept(1L, 1L);
        inOrder.verify(mockConsumer).accept(100L, 100L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void cursorShouldLoopOverEveryElement() {
        map.put(1L, 1L);
        map.put(100L, 100L);
        final LongLongCursor cursor = map.cursor();
        Assert.assertTrue(cursor.advance());
        Assert.assertEquals(1L, cursor.key());
        Assert.assertEquals(1L, cursor.value());
        Assert.assertTrue(cursor.advance());
        Assert.assertEquals(100L, cursor.key());
        Assert.assertEquals(100L, cursor.value());
    }

    @Test
    public void shouldNotContainKeyOfAMissingKey() {
        Assert.assertFalse(map.containsKey(1L));
    }

    @Test
    public void shouldContainKeyOfAPresentKey() {
        map.put(1L, 1L);
        Assert.assertTrue(map.containsKey(1L));
    }

    @Test
    public void shouldNotContainValueForAMissingEntry() {
        Assert.assertFalse(map.containsValue(1L));
    }

    @Test
    public void shouldContainValueForAPresentEntry() {
        map.put(1L, 1L);
        Assert.assertTrue(map.containsValue(1L));
    }

    @Test
    public void shouldExposeValidKeySet() {
        map.put(1L, 1L);
        map.put(2L, 2L);
        Long2LongHashMapTest.assertCollectionContainsElements(map.keySet());
    }

    @Test
    public void shouldExposeValidValueSet() {
        map.put(1L, 1L);
        map.put(2L, 2L);
        Long2LongHashMapTest.assertCollectionContainsElements(map.values());
    }

    @Test
    public void shouldPutAllMembersOfAnotherHashMap() {
        map.put(1L, 1L);
        map.put(2L, 3L);
        final Map<Long, Long> other = new HashMap<Long, Long>();
        other.put(1L, 2L);
        other.put(3L, 4L);
        map.putAll(other);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(2, map.get(1L));
        Assert.assertEquals(3, map.get(2L));
        Assert.assertEquals(4, map.get(3L));
    }

    @Test
    public void entrySetShouldContainEntries() {
        map.put(1L, 1L);
        map.put(2L, 3L);
        final Set<Map.Entry<Long, Long>> entrySet = map.entrySet();
        Assert.assertEquals(2, entrySet.size());
        Assert.assertFalse(entrySet.isEmpty());
        final Iterator<Map.Entry<Long, Long>> it = entrySet.iterator();
        Assert.assertTrue(it.hasNext());
        Long2LongHashMapTest.assertEntryIs(it.next(), 2L, 3L);
        Assert.assertTrue(it.hasNext());
        Long2LongHashMapTest.assertEntryIs(it.next(), 1L, 1L);
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void removeShouldReturnMissing() {
        Assert.assertEquals(Long2LongHashMapTest.MISSING_VALUE, map.remove(1L));
    }

    @Test
    public void removeShouldReturnValueRemoved() {
        map.put(1L, 2L);
        Assert.assertEquals(2L, map.remove(1L));
    }

    @Test
    public void removeShouldRemoveEntry() {
        map.put(1L, 2L);
        map.remove(1L);
        Assert.assertTrue(map.isEmpty());
        Assert.assertFalse(map.containsKey(1L));
        Assert.assertFalse(map.containsValue(2L));
    }

    @Test
    public void shouldOnlyRemoveTheSpecifiedEntry() {
        for (int i = 0; i < 8; i++) {
            map.put(i, (i * 2));
        }
        map.remove(5L);
        for (int i = 0; i < 8; i++) {
            if (i != 5) {
                Assert.assertTrue(map.containsKey(i));
                Assert.assertTrue(map.containsValue((2 * i)));
            }
        }
    }

    @Test
    public void shouldResizeWhenMoreElementsAreAdded() {
        for (int key = 0; key < 100; key++) {
            final int value = key * 2;
            Assert.assertEquals(Long2LongHashMapTest.MISSING_VALUE, map.put(key, value));
            Assert.assertEquals(value, map.get(key));
        }
    }

    @Test
    public void toStringShouldReportAllEntries() {
        map.put(1, 2);
        map.put(3, 4);
        Assert.assertEquals("{1->2 3->4}", map.toString());
    }

    @Test
    public void sizeShouldReturnNumberOfEntries() {
        final int count = 100;
        for (int key = 0; key < count; key++) {
            map.put(key, 1);
        }
        Assert.assertEquals(count, map.size());
    }
}

