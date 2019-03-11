/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util;


import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import org.apache.calcite.linq4j.function.Function0;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit and performance test for {@link ChunkList}.
 */
public class ChunkListTest {
    /**
     * Unit test for {@link ChunkList}.
     */
    @Test
    public void testChunkList() {
        final ChunkList<Integer> list = new ChunkList();
        final ChunkList<Integer> list0 = new ChunkList(list);
        final ChunkList<Integer> list1 = new ChunkList(list);
        list1.add(123);
        Assert.assertEquals(0, list.size());
        Assert.assertEquals(0, list0.size());
        Assert.assertEquals(1, list1.size());
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals("[]", list.toString());
        try {
            list.remove(0);
            Assert.fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            list.get((-1));
            Assert.fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            list.get(0);
            Assert.fail("expected exception");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        list.add(7);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(7, ((int) (list.get(0))));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals("[7]", list.toString());
        list.add(9);
        list.add(null);
        list.add(11);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(7, ((int) (list.get(0))));
        Assert.assertEquals(9, ((int) (list.get(1))));
        Assert.assertNull(list.get(2));
        Assert.assertEquals(11, ((int) (list.get(3))));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals("[7, 9, null, 11]", list.toString());
        Assert.assertTrue(list.contains(9));
        Assert.assertFalse(list.contains(8));
        list.addAll(Collections.nCopies(70, 1));
        Assert.assertEquals(74, list.size());
        Assert.assertEquals(1, ((int) (list.get(40))));
        Assert.assertEquals(1, ((int) (list.get(70))));
        int n = 0;
        for (Integer integer : list) {
            Util.discard(integer);
            ++n;
        }
        Assert.assertEquals(n, list.size());
        int i = list.indexOf(null);
        Assert.assertEquals(2, i);
        // can't sort if null is present
        list.set(2, 123);
        i = list.indexOf(null);
        Assert.assertEquals((-1), i);
        // sort an empty list
        Collections.sort(list0);
        Assert.assertThat(list0.isEmpty(), CoreMatchers.is(true));
        // sort a list with 1 element
        Collections.sort(list1);
        Assert.assertThat(list1.size(), CoreMatchers.is(1));
        Collections.sort(list);
        Assert.assertEquals(74, list.size());
        list.remove(((Integer) (7)));
        Collections.sort(list);
        Assert.assertEquals(1, ((int) (list.get(3))));
        // remove all instances of a value that exists
        boolean b = list.removeAll(Collections.singletonList(9));
        Assert.assertTrue(b);
        // remove all instances of a non-existent value
        b = list.removeAll(Collections.singletonList(99));
        Assert.assertFalse(b);
        // remove all instances of a value that occurs in the last chunk
        list.add(12345);
        b = list.removeAll(Collections.singletonList(12345));
        Assert.assertTrue(b);
        // remove all instances of a value that occurs in the last chunk but
        // not as the last value
        list.add(12345);
        list.add(123);
        b = list.removeAll(Collections.singletonList(12345));
        Assert.assertTrue(b);
        Assert.assertThat(new ChunkList(Collections.nCopies(1000, 77)).size(), CoreMatchers.is(1000));
        // add to an empty list via iterator
        // noinspection MismatchedQueryAndUpdateOfCollection
        final ChunkList<String> list2 = new ChunkList();
        list2.listIterator(0).add("x");
        Assert.assertEquals("[x]", list2.toString());
        // add at start
        list2.add(0, "y");
        Assert.assertEquals("[y, x]", list2.toString());
        list2.remove(0);
        Assert.assertEquals("[x]", list2.toString());
        // clear a list of length 5, one element at a time, using an iterator
        list2.clear();
        list2.addAll(ImmutableList.of("a", "b", "c", "d", "e"));
        Assert.assertThat(list2.size(), CoreMatchers.is(5));
        final ListIterator<String> listIterator = list2.listIterator(0);
        Assert.assertThat(listIterator.next(), CoreMatchers.is("a"));
        listIterator.remove();
        Assert.assertThat(listIterator.next(), CoreMatchers.is("b"));
        listIterator.remove();
        Assert.assertThat(listIterator.next(), CoreMatchers.is("c"));
        listIterator.remove();
        Assert.assertThat(listIterator.next(), CoreMatchers.is("d"));
        listIterator.remove();
        Assert.assertThat(list2.size(), CoreMatchers.is(1));
        Assert.assertThat(listIterator.next(), CoreMatchers.is("e"));
        listIterator.remove();
        Assert.assertThat(list2.size(), CoreMatchers.is(0));
    }

    /**
     * Clears lists of various sizes.
     */
    @Test
    public void testClear() {
        checkListClear(0);
        checkListClear(1);
        checkListClear(2);
        checkListClear(32);
        checkListClear(64);
        checkListClear(65);
        checkListClear(66);
        checkListClear(100);
        checkListClear(127);
        checkListClear(128);
        checkListClear(129);
    }

    /**
     * Removing via an iterator.
     */
    @Test
    public void testIterator() {
        final ChunkList<String> list = new ChunkList();
        list.add("a");
        list.add("b");
        final ListIterator<String> listIterator = list.listIterator(0);
        try {
            listIterator.remove();
            Assert.fail("excepted exception");
        } catch (IllegalStateException e) {
            // ok
        }
        listIterator.next();
        listIterator.remove();
        Assert.assertThat(list.size(), CoreMatchers.is(1));
        Assert.assertThat(listIterator.hasNext(), CoreMatchers.is(true));
        listIterator.next();
        listIterator.remove();
        Assert.assertThat(list.size(), CoreMatchers.is(0));
        Assert.assertThat(listIterator.hasNext(), CoreMatchers.is(false));
    }

    /**
     * Unit test for {@link ChunkList} that applies random
     * operations.
     */
    @Test
    public void testRandom() {
        final int iterationCount = 10000;
        checkRandom(new Random(1), new ChunkList<Integer>(), new ArrayList<Integer>(), iterationCount);
        final Random random = new Random(2);
        for (int j = 0; j < 10; j++) {
            checkRandom(random, new ChunkList<Integer>(), new ArrayList<Integer>(), iterationCount);
        }
        final ChunkList<Integer> chunkList = new ChunkList(Collections.nCopies(1000, 5));
        final List<Integer> referenceList = new ArrayList(chunkList);
        checkRandom(new Random(3), chunkList, referenceList, iterationCount);
    }

    @Test
    public void testPerformance() {
        if (!(Benchmark.enabled())) {
            return;
        }
        // noinspection unchecked
        final Iterable<Pair<Function0<List<Integer>>, String>> factories0 = Pair.zip(Arrays.asList(ArrayList::new, LinkedList::new, ChunkList::new), Arrays.asList("ArrayList", "LinkedList", "ChunkList-64"));
        final List<Pair<Function0<List<Integer>>, String>> factories1 = new ArrayList<>();
        for (Pair<Function0<List<Integer>>, String> pair : factories0) {
            factories1.add(pair);
        }
        List<Pair<Function0<List<Integer>>, String>> factories = factories1.subList(2, 3);
        Iterable<Pair<Integer, String>> sizes = Pair.zip(Arrays.asList(100000, 1000000, 10000000), Arrays.asList("100k", "1m", "10m"));
        for (final Pair<Function0<List<Integer>>, String> pair : factories) {
            run();
        }
        for (final Pair<Function0<List<Integer>>, String> pair : factories) {
            run();
        }
        for (final Pair<Function0<List<Integer>>, String> pair : factories) {
            for (final Pair<Integer, String> size : sizes) {
                if ((size.left) > 1000000) {
                    continue;
                }
                run();
            }
        }
        for (final Pair<Function0<List<Integer>>, String> pair : factories) {
            for (final Pair<Integer, String> size : sizes) {
                if ((size.left) > 1000000) {
                    continue;
                }
                run();
            }
        }
        for (final Pair<Function0<List<Integer>>, String> pair : factories) {
            for (final Pair<Integer, String> size : sizes) {
                if ((size.left) > 1000000) {
                    continue;
                }
                run();
            }
        }
    }
}

/**
 * End ChunkListTest.java
 */
