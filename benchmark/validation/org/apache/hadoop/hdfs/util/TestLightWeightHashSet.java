/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.util;


import LightWeightHashSet.MINIMUM_CAPACITY;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static LightWeightHashSet.DEFAULT_MAX_LOAD_FACTOR;
import static LightWeightHashSet.DEFAUT_MIN_LOAD_FACTOR;
import static LightWeightHashSet.MINIMUM_CAPACITY;


public class TestLightWeightHashSet {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestLightWeightHashSet");

    private final ArrayList<Integer> list = new ArrayList<Integer>();

    private final int NUM = 100;

    private LightWeightHashSet<Integer> set;

    private Random rand;

    @Test
    public void testEmptyBasic() {
        TestLightWeightHashSet.LOG.info("Test empty basic");
        Iterator<Integer> iter = set.iterator();
        // iterator should not have next
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        TestLightWeightHashSet.LOG.info("Test empty - DONE");
    }

    @Test
    public void testOneElementBasic() {
        TestLightWeightHashSet.LOG.info("Test one element basic");
        set.add(list.get(0));
        // set should be non-empty
        Assert.assertEquals(1, set.size());
        Assert.assertFalse(set.isEmpty());
        // iterator should have next
        Iterator<Integer> iter = set.iterator();
        Assert.assertTrue(iter.hasNext());
        // iterator should not have next
        Assert.assertEquals(list.get(0), iter.next());
        Assert.assertFalse(iter.hasNext());
        TestLightWeightHashSet.LOG.info("Test one element basic - DONE");
    }

    @Test
    public void testMultiBasic() {
        TestLightWeightHashSet.LOG.info("Test multi element basic");
        // add once
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        Assert.assertEquals(list.size(), set.size());
        // check if the elements are in the set
        for (Integer i : list) {
            Assert.assertTrue(set.contains(i));
        }
        // add again - should return false each time
        for (Integer i : list) {
            Assert.assertFalse(set.add(i));
        }
        // check again if the elements are there
        for (Integer i : list) {
            Assert.assertTrue(set.contains(i));
        }
        Iterator<Integer> iter = set.iterator();
        int num = 0;
        while (iter.hasNext()) {
            Integer next = iter.next();
            Assert.assertNotNull(next);
            Assert.assertTrue(list.contains(next));
            num++;
        } 
        // check the number of element from the iterator
        Assert.assertEquals(list.size(), num);
        TestLightWeightHashSet.LOG.info("Test multi element basic - DONE");
    }

    @Test
    public void testRemoveOne() {
        TestLightWeightHashSet.LOG.info("Test remove one");
        Assert.assertTrue(set.add(list.get(0)));
        Assert.assertEquals(1, set.size());
        // remove from the head/tail
        Assert.assertTrue(set.remove(list.get(0)));
        Assert.assertEquals(0, set.size());
        // check the iterator
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        // add the element back to the set
        Assert.assertTrue(set.add(list.get(0)));
        Assert.assertEquals(1, set.size());
        iter = set.iterator();
        Assert.assertTrue(iter.hasNext());
        TestLightWeightHashSet.LOG.info("Test remove one - DONE");
    }

    @Test
    public void testRemoveMulti() {
        TestLightWeightHashSet.LOG.info("Test remove multi");
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        for (int i = 0; i < ((NUM) / 2); i++) {
            Assert.assertTrue(set.remove(list.get(i)));
        }
        // the deleted elements should not be there
        for (int i = 0; i < ((NUM) / 2); i++) {
            Assert.assertFalse(set.contains(list.get(i)));
        }
        // the rest should be there
        for (int i = (NUM) / 2; i < (NUM); i++) {
            Assert.assertTrue(set.contains(list.get(i)));
        }
        TestLightWeightHashSet.LOG.info("Test remove multi - DONE");
    }

    @Test
    public void testRemoveAll() {
        TestLightWeightHashSet.LOG.info("Test remove all");
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        for (int i = 0; i < (NUM); i++) {
            Assert.assertTrue(set.remove(list.get(i)));
        }
        // the deleted elements should not be there
        for (int i = 0; i < (NUM); i++) {
            Assert.assertFalse(set.contains(list.get(i)));
        }
        // iterator should not have next
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        Assert.assertTrue(set.isEmpty());
        TestLightWeightHashSet.LOG.info("Test remove all - DONE");
    }

    @Test
    public void testRemoveAllViaIterator() {
        TestLightWeightHashSet.LOG.info("Test remove all via iterator");
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        for (Iterator<Integer> iter = set.iterator(); iter.hasNext();) {
            int e = iter.next();
            // element should be there before removing
            Assert.assertTrue(set.contains(e));
            iter.remove();
            // element should not be there now
            Assert.assertFalse(set.contains(e));
        }
        // the deleted elements should not be there
        for (int i = 0; i < (NUM); i++) {
            Assert.assertFalse(set.contains(list.get(i)));
        }
        // iterator should not have next
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        Assert.assertTrue(set.isEmpty());
        TestLightWeightHashSet.LOG.info("Test remove all via iterator - DONE");
    }

    @Test
    public void testPollAll() {
        TestLightWeightHashSet.LOG.info("Test poll all");
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        // remove all elements by polling
        List<Integer> poll = set.pollAll();
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        // the deleted elements should not be there
        for (int i = 0; i < (NUM); i++) {
            Assert.assertFalse(set.contains(list.get(i)));
        }
        // we should get all original items
        for (Integer i : poll) {
            Assert.assertTrue(list.contains(i));
        }
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        TestLightWeightHashSet.LOG.info("Test poll all - DONE");
    }

    @Test
    public void testPollNMulti() {
        TestLightWeightHashSet.LOG.info("Test pollN multi");
        // use addAll
        set.addAll(list);
        // poll zero
        List<Integer> poll = set.pollN(0);
        Assert.assertEquals(0, poll.size());
        for (Integer i : list) {
            Assert.assertTrue(set.contains(i));
        }
        // poll existing elements (less than size)
        poll = set.pollN(10);
        Assert.assertEquals(10, poll.size());
        for (Integer i : poll) {
            // should be in original items
            Assert.assertTrue(list.contains(i));
            // should not be in the set anymore
            Assert.assertFalse(set.contains(i));
        }
        // poll more elements than present
        poll = set.pollN(1000);
        Assert.assertEquals(((NUM) - 10), poll.size());
        for (Integer i : poll) {
            // should be in original items
            Assert.assertTrue(list.contains(i));
        }
        // set is empty
        Assert.assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.size());
        TestLightWeightHashSet.LOG.info("Test pollN multi - DONE");
    }

    @Test
    public void testPollNMultiArray() {
        TestLightWeightHashSet.LOG.info("Test pollN multi array");
        // use addAll
        set.addAll(list);
        // poll existing elements (less than size)
        Integer[] poll = new Integer[10];
        poll = set.pollToArray(poll);
        Assert.assertEquals(10, poll.length);
        for (Integer i : poll) {
            // should be in original items
            Assert.assertTrue(list.contains(i));
            // should not be in the set anymore
            Assert.assertFalse(set.contains(i));
        }
        // poll other elements (more than size)
        poll = new Integer[NUM];
        poll = set.pollToArray(poll);
        Assert.assertEquals(((NUM) - 10), poll.length);
        for (int i = 0; i < ((NUM) - 10); i++) {
            Assert.assertTrue(list.contains(poll[i]));
        }
        // set is empty
        Assert.assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.size());
        // //////
        set.addAll(list);
        // poll existing elements (exactly the size)
        poll = new Integer[NUM];
        poll = set.pollToArray(poll);
        Assert.assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.size());
        Assert.assertEquals(NUM, poll.length);
        for (int i = 0; i < (NUM); i++) {
            Assert.assertTrue(list.contains(poll[i]));
        }
        // //////
        // //////
        set.addAll(list);
        // poll existing elements (exactly the size)
        poll = new Integer[0];
        poll = set.pollToArray(poll);
        for (int i = 0; i < (NUM); i++) {
            Assert.assertTrue(set.contains(list.get(i)));
        }
        Assert.assertEquals(0, poll.length);
        // //////
        TestLightWeightHashSet.LOG.info("Test pollN multi array- DONE");
    }

    @Test
    public void testClear() {
        TestLightWeightHashSet.LOG.info("Test clear");
        // use addAll
        set.addAll(list);
        Assert.assertEquals(NUM, set.size());
        Assert.assertFalse(set.isEmpty());
        // clear the set
        set.clear();
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        // iterator should be empty
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        TestLightWeightHashSet.LOG.info("Test clear - DONE");
    }

    @Test
    public void testCapacity() {
        TestLightWeightHashSet.LOG.info("Test capacity");
        float maxF = DEFAULT_MAX_LOAD_FACTOR;
        float minF = DEFAUT_MIN_LOAD_FACTOR;
        // capacity lower than min_capacity
        set = new LightWeightHashSet<Integer>(1, maxF, minF);
        Assert.assertEquals(MINIMUM_CAPACITY, set.getCapacity());
        // capacity not a power of two
        set = new LightWeightHashSet<Integer>(30, maxF, minF);
        Assert.assertEquals(Math.max(MINIMUM_CAPACITY, 32), set.getCapacity());
        // capacity valid
        set = new LightWeightHashSet<Integer>(64, maxF, minF);
        Assert.assertEquals(Math.max(MINIMUM_CAPACITY, 64), set.getCapacity());
        // add NUM elements
        set.addAll(list);
        int expCap = MINIMUM_CAPACITY;
        while ((expCap < (NUM)) && ((maxF * expCap) < (NUM)))
            expCap <<= 1;

        Assert.assertEquals(expCap, set.getCapacity());
        // see if the set shrinks if we remove elements by removing
        set.clear();
        set.addAll(list);
        int toRemove = ((set.size()) - ((int) ((set.getCapacity()) * minF))) + 1;
        for (int i = 0; i < toRemove; i++) {
            set.remove(list.get(i));
        }
        Assert.assertEquals(Math.max(MINIMUM_CAPACITY, (expCap / 2)), set.getCapacity());
        TestLightWeightHashSet.LOG.info("Test capacity - DONE");
    }

    @Test
    public void testOther() {
        TestLightWeightHashSet.LOG.info("Test other");
        // remove all
        Assert.assertTrue(set.addAll(list));
        Assert.assertTrue(set.removeAll(list));
        Assert.assertTrue(set.isEmpty());
        // remove sublist
        List<Integer> sub = new LinkedList<Integer>();
        for (int i = 0; i < 10; i++) {
            sub.add(list.get(i));
        }
        Assert.assertTrue(set.addAll(list));
        Assert.assertTrue(set.removeAll(sub));
        Assert.assertFalse(set.isEmpty());
        Assert.assertEquals(((NUM) - 10), set.size());
        for (Integer i : sub) {
            Assert.assertFalse(set.contains(i));
        }
        Assert.assertFalse(set.containsAll(sub));
        // the rest of the elements should be there
        List<Integer> sub2 = new LinkedList<Integer>();
        for (int i = 10; i < (NUM); i++) {
            sub2.add(list.get(i));
        }
        Assert.assertTrue(set.containsAll(sub2));
        // to array
        Integer[] array = set.toArray(new Integer[0]);
        Assert.assertEquals(((NUM) - 10), array.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertTrue(sub2.contains(array[i]));
        }
        Assert.assertEquals(((NUM) - 10), set.size());
        // to array
        Object[] array2 = set.toArray();
        Assert.assertEquals(((NUM) - 10), array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertTrue(sub2.contains(array2[i]));
        }
        TestLightWeightHashSet.LOG.info("Test other - DONE");
    }

    @Test
    public void testGetElement() {
        LightWeightHashSet<TestLightWeightHashSet.TestObject> objSet = new LightWeightHashSet<TestLightWeightHashSet.TestObject>();
        TestLightWeightHashSet.TestObject objA = new TestLightWeightHashSet.TestObject("object A");
        TestLightWeightHashSet.TestObject equalToObjA = new TestLightWeightHashSet.TestObject("object A");
        TestLightWeightHashSet.TestObject objB = new TestLightWeightHashSet.TestObject("object B");
        objSet.add(objA);
        objSet.add(objB);
        Assert.assertSame(objA, objSet.getElement(objA));
        Assert.assertSame(objA, objSet.getElement(equalToObjA));
        Assert.assertSame(objB, objSet.getElement(objB));
        Assert.assertNull(objSet.getElement(new TestLightWeightHashSet.TestObject("not in set")));
    }

    /**
     * Wrapper class which is used in
     * {@link TestLightWeightHashSet#testGetElement()}
     */
    private static class TestObject {
        private final String value;

        public TestObject(String value) {
            super();
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            TestLightWeightHashSet.TestObject other = ((TestLightWeightHashSet.TestObject) (obj));
            return this.value.equals(other.value);
        }
    }
}

