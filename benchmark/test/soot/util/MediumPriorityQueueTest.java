/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.util;


import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;


public class MediumPriorityQueueTest {
    Integer[] universe1;

    Integer[] clone;

    Queue<Integer> q;

    @Test
    public void testDeleteFirst() {
        q = PriorityQueue.of(universe1);
        Assert.assertTrue(q.remove(universe1[0]));
        Assert.assertEquals(q.peek(), universe1[1]);
        Assert.assertEquals(q.poll(), universe1[1]);
    }

    @Test
    public void testNew() {
        q = PriorityQueue.of(universe1);
        Assert.assertEquals(universe1.length, q.size());
        Assert.assertFalse(q.isEmpty());
    }

    @Test
    public void testPollAll() {
        q = PriorityQueue.of(universe1);
        int i = 0;
        while (!(q.isEmpty()))
            Assert.assertEquals(universe1[(i++)], q.poll());

    }

    @Test
    public void testPoll2() {
        q = PriorityQueue.noneOf(universe1);
        for (int i = 0; i < (universe1.length); i += 3)
            q.add(universe1[i]);

        int i = -3;
        while (!(q.isEmpty())) {
            Object e = universe1[(i += 3)];
            Assert.assertEquals(e, q.peek());
            Assert.assertEquals(e, q.poll());
        } 
    }

    @Test
    public void testPeekPollAll() {
        q = PriorityQueue.of(universe1);
        while (!(q.isEmpty()))
            Assert.assertEquals(q.peek(), q.poll());

    }

    @Test
    public void testOffer() {
        q = PriorityQueue.of(universe1);
        int i = 0;
        Assert.assertEquals(universe1[(i++)], q.poll());
        Assert.assertEquals(universe1[(i++)], q.poll());
        Assert.assertEquals(universe1[(i++)], q.poll());
        Assert.assertEquals(universe1[(i++)], q.poll());
        q.add(universe1[(i = 0)]);
        Assert.assertEquals(universe1[(i++)], q.poll());
    }

    @Test
    public void testMixedAddDelete() {
        q = PriorityQueue.noneOf(universe1);
        Integer z = universe1[0];
        Integer x = universe1[666];
        Assert.assertTrue(q.add(z));
        Assert.assertFalse(q.offer(z));
        Assert.assertTrue(q.contains(z));
        Assert.assertTrue(q.add(x));
        for (Integer i : universe1)
            Assert.assertEquals(((i == z) || (i == x)), q.contains(i));

        Assert.assertTrue(q.remove(z));
        for (Integer i : universe1)
            Assert.assertEquals((i == x), q.contains(i));

        Assert.assertEquals(x, q.peek());
        Assert.assertEquals(x, q.poll());
    }

    @Test
    public void testOfferAlreadyInQueue() {
        q = PriorityQueue.of(universe1);
        for (Integer i : universe1) {
            Assert.assertFalse(q.offer(i));
        }
    }

    @Test
    public void testClear() {
        q = PriorityQueue.of(universe1);
        q.clear();
        Assert.assertEquals(q.size(), 0);
        Assert.assertTrue(q.isEmpty());
        Assert.assertNull(q.peek());
        Assert.assertNull(q.poll());
        for (Integer i : universe1)
            Assert.assertFalse(q.contains(i));

    }

    @Test(expected = NoSuchElementException.class)
    public void testOfferNotInUniverse() {
        q = PriorityQueue.of(universe1);
        q.offer((-999));
    }

    @Test(expected = NullPointerException.class)
    public void testOfferNull() {
        q = PriorityQueue.of(universe1);
        q.offer(null);
    }

    @Test
    public void testRemoveNull() {
        q = PriorityQueue.of(universe1);
        Assert.assertFalse(q.remove(null));
    }

    @Test
    public void testIteratorAll() {
        q = PriorityQueue.of(universe1);
        int j = 0;
        for (Integer i : q) {
            Assert.assertEquals(universe1[(j++)], i);
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorDelete() {
        q = PriorityQueue.of(universe1);
        int j = 0;
        for (Integer i : q) {
            Assert.assertEquals(universe1[(j++)], i);
            Assert.assertTrue(q.remove(universe1[((universe1.length) - 1)]));
        }
    }

    @Test
    public void testIteratorRemove() {
        q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        while (it.hasNext()) {
            Integer i = it.next();
            Assert.assertTrue(q.contains(i));
            it.remove();
            Assert.assertFalse(q.contains(i));
        } 
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorOutOfBounds() {
        q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        while (it.hasNext()) {
            it.next();
        } 
        it.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorDoubleRemove() {
        q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
            it.remove();
        } 
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorBeforeFirst() {
        q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        it.remove();
    }

    @Test
    public void testIteratorHole1() {
        q = PriorityQueue.of(universe1);
        int hole = 7;
        Assert.assertTrue(q.remove(universe1[hole]));
        Assert.assertFalse(q.contains(universe1[hole]));
        int j = 0;
        for (Integer i : q) {
            if (j == hole)
                j++;

            Assert.assertEquals(universe1[(j++)], i);
        }
    }
}

