/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.util;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link EvictingBoundedList}.
 */
public class EvictingBoundedListTest {
    @Test
    public void testAddGet() {
        int insertSize = 17;
        int boundSize = 5;
        Integer defaultElement = 4711;
        EvictingBoundedList<Integer> list = new EvictingBoundedList(boundSize, defaultElement);
        Assert.assertTrue(list.isEmpty());
        for (int i = 0; i < insertSize; ++i) {
            list.add(i);
        }
        Assert.assertEquals(17, list.size());
        for (int i = 0; i < insertSize; ++i) {
            int exp = (i < (insertSize - boundSize)) ? defaultElement : i;
            int act = list.get(i);
            Assert.assertEquals(exp, act);
        }
    }

    @Test
    public void testSet() {
        int insertSize = 17;
        int boundSize = 5;
        Integer defaultElement = 4711;
        List<Integer> reference = new ArrayList<>(insertSize);
        EvictingBoundedList<Integer> list = new EvictingBoundedList(boundSize, defaultElement);
        for (int i = 0; i < insertSize; ++i) {
            reference.add(i);
            list.add(i);
        }
        Assert.assertEquals(reference.size(), list.size());
        list.set(0, 123);
        list.set(((insertSize - boundSize) - 1), 123);
        list.set((insertSize - boundSize), 42);
        reference.set((insertSize - boundSize), 42);
        list.set(13, 43);
        reference.set(13, 43);
        list.set(16, 44);
        reference.set(16, 44);
        try {
            list.set(insertSize, 23);
            Assert.fail("Illegal index in set not detected.");
        } catch (IllegalArgumentException ignored) {
        }
        for (int i = 0; i < insertSize; ++i) {
            int exp = (i < (insertSize - boundSize)) ? defaultElement : reference.get(i);
            int act = list.get(i);
            Assert.assertEquals(exp, act);
        }
        Assert.assertEquals(reference.size(), list.size());
    }

    @Test
    public void testClear() {
        int insertSize = 17;
        int boundSize = 5;
        Integer defaultElement = 4711;
        EvictingBoundedList<Integer> list = new EvictingBoundedList(boundSize, defaultElement);
        for (int i = 0; i < insertSize; ++i) {
            list.add(i);
        }
        list.clear();
        Assert.assertEquals(0, list.size());
        Assert.assertTrue(list.isEmpty());
        try {
            list.get(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void testIterator() {
        int insertSize = 17;
        int boundSize = 5;
        Integer defaultElement = 4711;
        EvictingBoundedList<Integer> list = new EvictingBoundedList(boundSize, defaultElement);
        Assert.assertTrue(list.isEmpty());
        for (int i = 0; i < insertSize; ++i) {
            list.add(i);
        }
        Iterator<Integer> iterator = list.iterator();
        for (int i = 0; i < insertSize; ++i) {
            Assert.assertTrue(iterator.hasNext());
            int exp = (i < (insertSize - boundSize)) ? defaultElement : i;
            int act = iterator.next();
            Assert.assertEquals(exp, act);
        }
        Assert.assertFalse(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Next on exhausted iterator did not trigger exception.");
        } catch (NoSuchElementException ignored) {
        }
        iterator = list.iterator();
        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        list.add(123);
        Assert.assertTrue(iterator.hasNext());
        try {
            iterator.next();
            Assert.fail("Concurrent modification not detected.");
        } catch (ConcurrentModificationException ignored) {
        }
    }
}

