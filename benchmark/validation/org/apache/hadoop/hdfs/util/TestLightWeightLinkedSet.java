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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLightWeightLinkedSet {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestLightWeightLinkedSet");

    private final ArrayList<Integer> list = new ArrayList<Integer>();

    private final int NUM = 100;

    private LightWeightLinkedSet<Integer> set;

    private Random rand;

    @Test
    public void testEmptyBasic() {
        TestLightWeightLinkedSet.LOG.info("Test empty basic");
        Iterator<Integer> iter = set.iterator();
        // iterator should not have next
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        // poll should return nothing
        Assert.assertNull(set.pollFirst());
        Assert.assertEquals(0, set.pollAll().size());
        Assert.assertEquals(0, set.pollN(10).size());
        TestLightWeightLinkedSet.LOG.info("Test empty - DONE");
    }

    @Test
    public void testOneElementBasic() {
        TestLightWeightLinkedSet.LOG.info("Test one element basic");
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
        TestLightWeightLinkedSet.LOG.info("Test one element basic - DONE");
    }

    @Test
    public void testMultiBasic() {
        TestLightWeightLinkedSet.LOG.info("Test multi element basic");
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
            Assert.assertEquals(list.get((num++)), iter.next());
        } 
        // check the number of element from the iterator
        Assert.assertEquals(list.size(), num);
        TestLightWeightLinkedSet.LOG.info("Test multi element basic - DONE");
    }

    @Test
    public void testRemoveOne() {
        TestLightWeightLinkedSet.LOG.info("Test remove one");
        Assert.assertTrue(set.add(list.get(0)));
        Assert.assertEquals(1, set.size());
        // remove from the head/tail
        Assert.assertTrue(set.remove(list.get(0)));
        Assert.assertEquals(0, set.size());
        // check the iterator
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        // poll should return nothing
        Assert.assertNull(set.pollFirst());
        Assert.assertEquals(0, set.pollAll().size());
        Assert.assertEquals(0, set.pollN(10).size());
        // add the element back to the set
        Assert.assertTrue(set.add(list.get(0)));
        Assert.assertEquals(1, set.size());
        iter = set.iterator();
        Assert.assertTrue(iter.hasNext());
        TestLightWeightLinkedSet.LOG.info("Test remove one - DONE");
    }

    @Test
    public void testRemoveMulti() {
        TestLightWeightLinkedSet.LOG.info("Test remove multi");
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
        Iterator<Integer> iter = set.iterator();
        // the remaining elements should be in order
        int num = (NUM) / 2;
        while (iter.hasNext()) {
            Assert.assertEquals(list.get((num++)), iter.next());
        } 
        Assert.assertEquals(num, NUM);
        TestLightWeightLinkedSet.LOG.info("Test remove multi - DONE");
    }

    @Test
    public void testRemoveAll() {
        TestLightWeightLinkedSet.LOG.info("Test remove all");
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
        TestLightWeightLinkedSet.LOG.info("Test remove all - DONE");
    }

    @Test
    public void testPollOneElement() {
        TestLightWeightLinkedSet.LOG.info("Test poll one element");
        set.add(list.get(0));
        Assert.assertEquals(list.get(0), set.pollFirst());
        Assert.assertNull(set.pollFirst());
        TestLightWeightLinkedSet.LOG.info("Test poll one element - DONE");
    }

    @Test
    public void testPollMulti() {
        TestLightWeightLinkedSet.LOG.info("Test poll multi");
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        // remove half of the elements by polling
        for (int i = 0; i < ((NUM) / 2); i++) {
            Assert.assertEquals(list.get(i), set.pollFirst());
        }
        Assert.assertEquals(((NUM) / 2), set.size());
        // the deleted elements should not be there
        for (int i = 0; i < ((NUM) / 2); i++) {
            Assert.assertFalse(set.contains(list.get(i)));
        }
        // the rest should be there
        for (int i = (NUM) / 2; i < (NUM); i++) {
            Assert.assertTrue(set.contains(list.get(i)));
        }
        Iterator<Integer> iter = set.iterator();
        // the remaining elements should be in order
        int num = (NUM) / 2;
        while (iter.hasNext()) {
            Assert.assertEquals(list.get((num++)), iter.next());
        } 
        Assert.assertEquals(num, NUM);
        // add elements back
        for (int i = 0; i < ((NUM) / 2); i++) {
            Assert.assertTrue(set.add(list.get(i)));
        }
        // order should be switched
        Assert.assertEquals(NUM, set.size());
        for (int i = (NUM) / 2; i < (NUM); i++) {
            Assert.assertEquals(list.get(i), set.pollFirst());
        }
        for (int i = 0; i < ((NUM) / 2); i++) {
            Assert.assertEquals(list.get(i), set.pollFirst());
        }
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        TestLightWeightLinkedSet.LOG.info("Test poll multi - DONE");
    }

    @Test
    public void testPollAll() {
        TestLightWeightLinkedSet.LOG.info("Test poll all");
        for (Integer i : list) {
            Assert.assertTrue(set.add(i));
        }
        // remove all elements by polling
        while ((set.pollFirst()) != null);
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        // the deleted elements should not be there
        for (int i = 0; i < (NUM); i++) {
            Assert.assertFalse(set.contains(list.get(i)));
        }
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        TestLightWeightLinkedSet.LOG.info("Test poll all - DONE");
    }

    @Test
    public void testPollNOne() {
        TestLightWeightLinkedSet.LOG.info("Test pollN one");
        set.add(list.get(0));
        List<Integer> l = set.pollN(10);
        Assert.assertEquals(1, l.size());
        Assert.assertEquals(list.get(0), l.get(0));
        TestLightWeightLinkedSet.LOG.info("Test pollN one - DONE");
    }

    @Test
    public void testPollNMulti() {
        TestLightWeightLinkedSet.LOG.info("Test pollN multi");
        // use addAll
        set.addAll(list);
        // poll existing elements
        List<Integer> l = set.pollN(10);
        Assert.assertEquals(10, l.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(list.get(i), l.get(i));
        }
        // poll more elements than present
        l = set.pollN(1000);
        Assert.assertEquals(((NUM) - 10), l.size());
        // check the order
        for (int i = 10; i < (NUM); i++) {
            Assert.assertEquals(list.get(i), l.get((i - 10)));
        }
        // set is empty
        Assert.assertTrue(set.isEmpty());
        Assert.assertEquals(0, set.size());
        TestLightWeightLinkedSet.LOG.info("Test pollN multi - DONE");
    }

    @Test
    public void testClear() {
        TestLightWeightLinkedSet.LOG.info("Test clear");
        // use addAll
        set.addAll(list);
        Assert.assertEquals(NUM, set.size());
        Assert.assertFalse(set.isEmpty());
        // Advance the bookmark.
        Iterator<Integer> bkmrkIt = set.getBookmark();
        for (int i = 0; i < (((set.size()) / 2) + 1); i++) {
            bkmrkIt.next();
        }
        Assert.assertTrue(bkmrkIt.hasNext());
        // clear the set
        set.clear();
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
        bkmrkIt = set.getBookmark();
        Assert.assertFalse(bkmrkIt.hasNext());
        // poll should return an empty list
        Assert.assertEquals(0, set.pollAll().size());
        Assert.assertEquals(0, set.pollN(10).size());
        Assert.assertNull(set.pollFirst());
        // iterator should be empty
        Iterator<Integer> iter = set.iterator();
        Assert.assertFalse(iter.hasNext());
        TestLightWeightLinkedSet.LOG.info("Test clear - DONE");
    }

    @Test
    public void testOther() {
        TestLightWeightLinkedSet.LOG.info("Test other");
        Assert.assertTrue(set.addAll(list));
        // to array
        Integer[] array = set.toArray(new Integer[0]);
        Assert.assertEquals(NUM, array.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertTrue(list.contains(array[i]));
        }
        Assert.assertEquals(NUM, set.size());
        // to array
        Object[] array2 = set.toArray();
        Assert.assertEquals(NUM, array2.length);
        for (int i = 0; i < (array2.length); i++) {
            Assert.assertTrue(list.contains(array2[i]));
        }
        TestLightWeightLinkedSet.LOG.info("Test capacity - DONE");
    }

    @Test(timeout = 60000)
    public void testGetBookmarkReturnsBookmarkIterator() {
        TestLightWeightLinkedSet.LOG.info("Test getBookmark returns proper iterator");
        Assert.assertTrue(set.addAll(list));
        Iterator<Integer> bookmark = set.getBookmark();
        Assert.assertEquals(bookmark.next(), list.get(0));
        final int numAdvance = (list.size()) / 2;
        for (int i = 1; i < numAdvance; i++) {
            bookmark.next();
        }
        Iterator<Integer> bookmark2 = set.getBookmark();
        Assert.assertEquals(bookmark2.next(), list.get(numAdvance));
    }

    @Test(timeout = 60000)
    public void testBookmarkAdvancesOnRemoveOfSameElement() {
        TestLightWeightLinkedSet.LOG.info("Test that the bookmark advances if we remove its element.");
        Assert.assertTrue(set.add(list.get(0)));
        Assert.assertTrue(set.add(list.get(1)));
        Assert.assertTrue(set.add(list.get(2)));
        Iterator<Integer> it = set.getBookmark();
        Assert.assertEquals(it.next(), list.get(0));
        set.remove(list.get(1));
        it = set.getBookmark();
        Assert.assertEquals(it.next(), list.get(2));
    }

    @Test(timeout = 60000)
    public void testBookmarkSetToHeadOnAddToEmpty() {
        TestLightWeightLinkedSet.LOG.info("Test bookmark is set after adding to previously empty set.");
        Iterator<Integer> it = set.getBookmark();
        Assert.assertFalse(it.hasNext());
        set.add(list.get(0));
        set.add(list.get(1));
        it = set.getBookmark();
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(it.next(), list.get(0));
        Assert.assertEquals(it.next(), list.get(1));
        Assert.assertFalse(it.hasNext());
    }

    @Test(timeout = 60000)
    public void testResetBookmarkPlacesBookmarkAtHead() {
        set.addAll(list);
        Iterator<Integer> it = set.getBookmark();
        final int numAdvance = (set.size()) / 2;
        for (int i = 0; i < numAdvance; i++) {
            it.next();
        }
        Assert.assertEquals(it.next(), list.get(numAdvance));
        set.resetBookmark();
        it = set.getBookmark();
        Assert.assertEquals(it.next(), list.get(0));
    }
}

