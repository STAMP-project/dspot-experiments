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


public class SmallPriorityQueueTest {
    Integer[] universe1 = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15 };

    @Test
    public void testDeleteFirst() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        Assert.assertTrue(q.remove(universe1[0]));
        Assert.assertEquals(q.peek(), universe1[1]);
        Assert.assertEquals(q.poll(), universe1[1]);
    }

    @Test
    public void testNew() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        Assert.assertEquals(universe1.length, q.size());
        Assert.assertFalse(q.isEmpty());
    }

    @Test
    public void testInstance() {
        Assert.assertTrue(((PriorityQueue.of(universe1)) instanceof SmallPriorityQueue));
    }

    @Test
    public void testMixedAddDelete() {
        int second = 6;
        Queue<Integer> q = PriorityQueue.noneOf(universe1);
        Assert.assertTrue(q.add(universe1[0]));
        Assert.assertFalse(q.offer(universe1[0]));
        Assert.assertTrue(q.add(universe1[second]));
        Assert.assertTrue(q.remove(universe1[0]));
        Assert.assertEquals(q.peek(), universe1[second]);
        Assert.assertEquals(q.poll(), universe1[second]);
    }

    @Test
    public void testOfferAlreadyInQueue() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        for (Integer i : universe1) {
            Assert.assertFalse(q.offer(i));
        }
    }

    @Test
    public void testClear() {
        Queue<Integer> q = PriorityQueue.of(universe1);
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
        Queue<Integer> q = PriorityQueue.of(universe1);
        q.offer(999);
    }

    @Test(expected = NullPointerException.class)
    public void testOfferNull() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        q.offer(null);
    }

    @Test
    public void testRemoveNull() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        Assert.assertFalse(q.remove(null));
    }

    @Test
    public void testIteratorAll() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        int j = 0;
        for (Integer i : q) {
            Assert.assertEquals(universe1[(j++)], i);
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorDelete() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        int j = 0;
        for (Integer i : q) {
            Assert.assertEquals(universe1[(j++)], i);
            Assert.assertTrue(q.remove(universe1[((universe1.length) - 1)]));
        }
    }

    @Test
    public void testOffer() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        int i = 0;
        Assert.assertEquals(universe1[(i++)], q.poll());
        Assert.assertEquals(universe1[(i++)], q.poll());
        Assert.assertEquals(universe1[(i++)], q.poll());
        Assert.assertEquals(universe1[(i++)], q.poll());
        q.add(universe1[(i = 0)]);
        Assert.assertEquals(universe1[(i++)], q.poll());
    }

    @Test
    public void testIteratorRemove() {
        Queue<Integer> q = PriorityQueue.of(universe1);
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
        Queue<Integer> q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        while (it.hasNext()) {
            it.next();
        } 
        it.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorDoubleRemove() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
            it.remove();
        } 
    }

    @Test(expected = IllegalStateException.class)
    public void testIteratorBeforeFirst() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        Iterator<Integer> it = q.iterator();
        it.remove();
    }

    @Test
    public void testIteratorHole1() {
        Queue<Integer> q = PriorityQueue.of(universe1);
        int hole = 7;
        Assert.assertTrue(q.remove(universe1[hole]));
        int j = 0;
        for (Integer i : q) {
            if (j == hole)
                j++;

            Assert.assertEquals(universe1[(j++)], i);
        }
    }
}

