/**
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.utilities.collection;


import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class CircularBufferTest {
    @Test
    public void testAddItems() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(4);
        for (int i = 0; i < 100; ++i) {
            buffer.add(i);
            Assert.assertEquals(((Integer) (i)), buffer.getLast());
        }
    }

    @Test
    public void testRemoveItems() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(4);
        buffer.add(1);
        buffer.add(2);
        buffer.add(3);
        buffer.add(4);
        buffer.add(5);
        Assert.assertEquals(4, buffer.size());
        Assert.assertEquals(((Integer) (2)), buffer.getFirst());
        Assert.assertEquals(((Integer) (2)), buffer.popFirst());
        Assert.assertEquals(((Integer) (3)), buffer.getFirst());
        Assert.assertEquals(3, buffer.size());
        Assert.assertEquals(((Integer) (5)), buffer.popLast());
        Assert.assertEquals(((Integer) (4)), buffer.popLast());
        Assert.assertEquals(((Integer) (3)), buffer.getLast());
        Assert.assertEquals(((Integer) (3)), buffer.popLast());
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test
    public void testCollectionMethods() {
        Collection<Integer> buffer = CircularBuffer.create(4);
        buffer.addAll(ImmutableList.of(1, 2, 3, 4, 5, 6));
        buffer.add(4);
        Assert.assertTrue(buffer.contains(5));
        Assert.assertTrue(buffer.containsAll(ImmutableList.of(5, 4)));
    }

    @Test
    public void testGetSet() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(4);
        buffer.addAll(ImmutableList.of(11, 12, 0, 1, 2, 3));
        Assert.assertEquals(((Integer) (0)), buffer.get(0));
        Assert.assertEquals(((Integer) (3)), buffer.get(3));
        Assert.assertEquals(((Integer) (2)), buffer.set(2, 8));
        Assert.assertEquals(((Integer) (8)), buffer.get(2));
        Assert.assertEquals(((Integer) (0)), buffer.set(0, 5));
        Assert.assertEquals(((Integer) (5)), buffer.get(0));
        Assert.assertEquals(((Integer) (3)), buffer.set(3, 6));
        Assert.assertEquals(((Integer) (6)), buffer.get(3));
    }

    @Test
    public void testInsert() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(4);
        buffer.addAll(ImmutableList.of(1, 2, 5, 7));
        // remove from the middle
        Assert.assertEquals(((Integer) (2)), buffer.remove(1));
        Assert.assertEquals(((Integer) (5)), buffer.get(1));
        // remove from the left side
        Assert.assertEquals(((Integer) (1)), buffer.remove(0));
        // remove from the right side
        Assert.assertEquals(((Integer) (7)), buffer.remove(1));
        // remove the only element
        Assert.assertEquals(((Integer) (5)), buffer.remove(0));
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test
    public void testIterator1() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(2);
        buffer.addAll(ImmutableList.of(1, 2));
        Iterator<Integer> iterator = buffer.iterator();
        iterator.next();
        iterator.remove();
    }

    @Test
    public void testIterator2() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(2);
        buffer.addAll(ImmutableList.of(1, 2));
        Iterator<Integer> iterator = buffer.iterator();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.remove();
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorRemoveTwice() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(2);
        buffer.addAll(ImmutableList.of(1, 2));
        Iterator<Integer> iterator = buffer.iterator();
        iterator.next();
        iterator.remove();
        iterator.remove();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorRemoveWithoutNext() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(2);
        buffer.addAll(ImmutableList.of(1, 2));
        buffer.iterator().remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorAfterEnd() {
        CircularBuffer<Integer> buffer = CircularBuffer.create(1);
        buffer.add(1);
        Iterator<Integer> it = buffer.iterator();
        it.next();
        it.remove();
        it.next();
        it.remove();
    }
}

