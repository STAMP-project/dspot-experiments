/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.networking.nio;


import com.hazelcast.internal.networking.nio.SelectorOptimizer.IteratorImpl;
import com.hazelcast.internal.networking.nio.SelectorOptimizer.SelectionKeys;
import com.hazelcast.internal.networking.nio.SelectorOptimizer.SelectionKeysSet;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SelectionKeysSetTest extends HazelcastTestSupport {
    private final SelectionKey key1 = Mockito.mock(SelectionKey.class);

    private final SelectionKey key2 = Mockito.mock(SelectionKey.class);

    private final SelectionKey key3 = Mockito.mock(SelectionKey.class);

    private SelectionKeysSet selectionKeysSet;

    @Test
    public void remove_doesNothing() {
        Assert.assertFalse(selectionKeysSet.remove(key1));
    }

    @Test
    public void contains_doesNothing() {
        Assert.assertFalse(selectionKeysSet.contains(key1));
    }

    @Test
    public void iteratorRecycled() {
        Iterator it1 = selectionKeysSet.iterator();
        Iterator it2 = selectionKeysSet.iterator();
        Assert.assertSame(it1, it2);
    }

    @Test
    public void add_whenCapacityNotSufficient() {
        List<SelectionKey> expectedKeys = new LinkedList<SelectionKey>();
        for (int k = 0; k < ((SelectionKeys.INITIAL_CAPACITY) * 4); k++) {
            SelectionKey key = Mockito.mock(SelectionKey.class);
            expectedKeys.add(key);
            selectionKeysSet.add(key);
            Assert.assertEquals(expectedKeys.size(), selectionKeysSet.size());
        }
        SelectionKeys active = selectionKeysSet.activeKeys;
        Assert.assertEquals(active.size, expectedKeys.size());
        for (int k = 0; k < (expectedKeys.size()); k++) {
            SelectionKey expected = expectedKeys.get(k);
            SelectionKey found = active.keys[k];
            Assert.assertSame(expected, found);
        }
    }

    @Test
    public void add_whenNull() {
        boolean result = selectionKeysSet.add(null);
        Assert.assertFalse(result);
        SelectionKeys active = selectionKeysSet.activeKeys;
        Assert.assertEquals(0, active.size);
    }

    // tests the regular nio loop; hasNext, next, remove.
    @Test
    public void testLoop() {
        List<SelectionKey> addedKeys = Arrays.asList(key1, key2, key3);
        for (SelectionKey selectionKey : addedKeys) {
            selectionKeysSet.add(selectionKey);
        }
        IteratorImpl it = ((IteratorImpl) (selectionKeysSet.iterator()));
        int k = 0;
        for (SelectionKey expected : addedKeys) {
            // check if the hasNext returns true
            boolean hasNext = it.hasNext();
            Assert.assertTrue(hasNext);
            // check if the key is correct.
            SelectionKey next = it.next();
            Assert.assertSame(expected, next);
            Assert.assertEquals(k, it.index);
            // do the remove and check if the slot is nulled
            it.remove();
            Assert.assertNull(it.keys[it.index]);
            k++;
        }
        Assert.assertFalse(it.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void next_whenNoItem() {
        IteratorImpl it = ((IteratorImpl) (selectionKeysSet.iterator()));
        it.next();
    }

    @Test(expected = IllegalStateException.class)
    public void remove_whenNoItem() {
        IteratorImpl it = ((IteratorImpl) (selectionKeysSet.iterator()));
        it.remove();
    }

    // see https://github.com/hazelcast/hazelcast/issues/10436
    @Test
    public void remove_whenLastItemFromArray() {
        for (int k = 0; k < (SelectionKeys.INITIAL_CAPACITY); k++) {
            selectionKeysSet.add(Mockito.mock(SelectionKey.class));
        }
        IteratorImpl it = ((IteratorImpl) (selectionKeysSet.iterator()));
        // we now next/remove all items apart from the last one.
        for (int k = 0; k < ((SelectionKeys.INITIAL_CAPACITY) - 1); k++) {
            it.next();
            it.remove();
        }
        // last item we next
        it.next();
        // and we remove; with the bug we would get a IllegalStateException
        it.remove();
        Assert.assertFalse(it.hasNext());
    }
}

