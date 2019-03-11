/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.kahadb.disk.util;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class SequenceSetTest {
    @Test
    public void testAddLong() {
        SequenceSet set = new SequenceSet();
        set.add(1);
        Assert.assertEquals(1, set.rangeSize());
        set.add(10);
        set.add(20);
        Assert.assertEquals(3, set.rangeSize());
    }

    @Test
    public void testRangeSize() {
        SequenceSet set = new SequenceSet();
        set.add(1);
        Assert.assertEquals(1, set.rangeSize());
        set.add(10);
        set.add(20);
        Assert.assertEquals(3, set.rangeSize());
        set.clear();
        Assert.assertEquals(0, set.rangeSize());
    }

    @Test
    public void testIsEmpty() {
        SequenceSet set = new SequenceSet();
        Assert.assertTrue(set.isEmpty());
        set.add(1);
        Assert.assertFalse(set.isEmpty());
    }

    @Test
    public void testClear() {
        SequenceSet set = new SequenceSet();
        set.clear();
        Assert.assertTrue(set.isEmpty());
        set.add(1);
        Assert.assertFalse(set.isEmpty());
        set.clear();
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testContains() {
        SequenceSet set = new SequenceSet();
        set.add(new Sequence(0, 10));
        set.add(new Sequence(21, 42));
        set.add(new Sequence(47, 90));
        set.add(new Sequence(142, 512));
        Assert.assertTrue(set.contains(0));
        Assert.assertTrue(set.contains(42));
        Assert.assertTrue(set.contains(49));
        Assert.assertTrue(set.contains(153));
        Assert.assertFalse(set.contains(43));
        Assert.assertFalse(set.contains(99));
        Assert.assertFalse(set.contains((-1)));
        Assert.assertFalse(set.contains(11));
    }

    @Test
    public void testAddValuesToTail() {
        SequenceSet set = new SequenceSet();
        set.add(new Sequence(0, 10));
        set.add(new Sequence(21, 42));
        set.add(new Sequence(142, 512));
        set.add(513);
        for (int i = 600; i < 650; i++) {
            set.add(i);
        }
        for (int i = 0; i < 10; i++) {
            set.add((i * 100));
        }
        Assert.assertTrue(set.contains(0));
        Assert.assertTrue(set.contains(25));
        Assert.assertTrue(set.contains(513));
        Assert.assertTrue((!(set.contains(514))));
        Assert.assertFalse(set.contains(599));
        Assert.assertTrue(set.contains(625));
        Assert.assertFalse(set.contains(651));
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(set.contains((i * 100)));
        }
    }

    @Test
    public void testRemove() {
        SequenceSet set = new SequenceSet();
        set.add(new Sequence(0, 100));
        Assert.assertEquals(101, set.rangeSize());
        Assert.assertEquals(1, set.size());
        Assert.assertFalse(set.remove(101));
        Assert.assertTrue(set.remove(50));
        Assert.assertEquals(2, set.size());
        Assert.assertEquals(100, set.rangeSize());
        Assert.assertFalse(set.remove(101));
        set.remove(0);
        Assert.assertEquals(2, set.size());
        Assert.assertEquals(99, set.rangeSize());
        set.remove(100);
        Assert.assertEquals(2, set.size());
        Assert.assertEquals(98, set.rangeSize());
        set.remove(10);
        Assert.assertEquals(3, set.size());
        Assert.assertEquals(97, set.rangeSize());
        SequenceSet toRemove = new SequenceSet();
        toRemove.add(new Sequence(0, 100));
        set.remove(toRemove);
        Assert.assertEquals(0, set.size());
        Assert.assertEquals(0, set.rangeSize());
    }

    @Test
    public void testMerge() {
        SequenceSet set = new SequenceSet();
        set.add(new Sequence(0, 100));
        SequenceSet set2 = new SequenceSet();
        set.add(new Sequence(50, 150));
        set.merge(set2);
        Assert.assertEquals(151, set.rangeSize());
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void testIterator() {
        SequenceSet set = new SequenceSet();
        set.add(new Sequence(0, 2));
        set.add(new Sequence(4, 5));
        set.add(new Sequence(7));
        set.add(new Sequence(20, 21));
        long[] expected = new long[]{ 0, 1, 2, 4, 5, 7, 20, 21 };
        int index = 0;
        Iterator<Long> iterator = set.iterator();
        while (iterator.hasNext()) {
            Assert.assertEquals(expected[(index++)], iterator.next().longValue());
        } 
    }

    @Test
    public void testIteratorEmptySequenceSet() {
        SequenceSet set = new SequenceSet();
        Iterator<Long> iterator = set.iterator();
        while (iterator.hasNext()) {
            Assert.fail("Should not have any elements");
        } 
    }
}

