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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of TreeSet
 */
public class FoldedTreeSetTest {
    private static Random srand;

    public FoldedTreeSetTest() {
    }

    /**
     * Test of comparator method, of class TreeSet.
     */
    @Test
    public void testComparator() {
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        Assert.assertEquals(null, new FoldedTreeSet().comparator());
        Assert.assertEquals(comparator, new FoldedTreeSet(comparator).comparator());
        FoldedTreeSet<String> set = new FoldedTreeSet(comparator);
        set.add("apa3");
        set.add("apa2");
        set.add("apa");
        set.add("apa5");
        set.add("apa4");
        Assert.assertEquals(5, set.size());
        Assert.assertEquals("apa", set.get("apa"));
    }

    /**
     * Test of first method, of class TreeSet.
     */
    @Test
    public void testFirst() {
        FoldedTreeSet<Integer> tree = new FoldedTreeSet();
        for (int i = 0; i < 256; i++) {
            tree.add((1024 + i));
            Assert.assertEquals(1024, tree.first().intValue());
        }
        for (int i = 1; i < 256; i++) {
            tree.remove((1024 + i));
            Assert.assertEquals(1024, tree.first().intValue());
        }
    }

    /**
     * Test of last method, of class TreeSet.
     */
    @Test
    public void testLast() {
        FoldedTreeSet<Integer> tree = new FoldedTreeSet();
        for (int i = 0; i < 256; i++) {
            tree.add((1024 + i));
            Assert.assertEquals((1024 + i), tree.last().intValue());
        }
        for (int i = 0; i < 255; i++) {
            tree.remove((1024 + i));
            Assert.assertEquals(1279, tree.last().intValue());
        }
    }

    /**
     * Test of size method, of class TreeSet.
     */
    @Test
    public void testSize() {
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        String entry = "apa";
        Assert.assertEquals(0, instance.size());
        instance.add(entry);
        Assert.assertEquals(1, instance.size());
        instance.remove(entry);
        Assert.assertEquals(0, instance.size());
    }

    /**
     * Test of isEmpty method, of class TreeSet.
     */
    @Test
    public void testIsEmpty() {
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        Assert.assertEquals(expResult, result);
        instance.add("apa");
        instance.remove("apa");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of contains method, of class TreeSet.
     */
    @Test
    public void testContains() {
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        String entry = "apa";
        Assert.assertEquals(false, instance.contains(entry));
        instance.add(entry);
        Assert.assertEquals(true, instance.contains(entry));
        Assert.assertEquals(false, instance.contains((entry + entry)));
    }

    /**
     * Test of iterator method, of class TreeSet.
     */
    @Test
    public void testIterator() {
        for (int iter = 0; iter < 10; iter++) {
            FoldedTreeSet<FoldedTreeSetTest.Holder> set = new FoldedTreeSet();
            long[] longs = new long[64723];
            for (int i = 0; i < (longs.length); i++) {
                FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
                while (set.contains(val)) {
                    val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
                } 
                longs[i] = val.getId();
                set.add(val);
            }
            Assert.assertEquals(longs.length, set.size());
            Arrays.sort(longs);
            Iterator<FoldedTreeSetTest.Holder> it = set.iterator();
            for (int i = 0; i < (longs.length); i++) {
                Assert.assertTrue(it.hasNext());
                FoldedTreeSetTest.Holder val = it.next();
                Assert.assertEquals(longs[i], val.getId());
                // remove randomly to force non linear removes
                if (FoldedTreeSetTest.srand.nextBoolean()) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Test of toArray method, of class TreeSet.
     */
    @Test
    public void testToArray() {
        FoldedTreeSet<Integer> tree = new FoldedTreeSet();
        ArrayList<Integer> list = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            list.add((1024 + i));
        }
        tree.addAll(list);
        Assert.assertArrayEquals(list.toArray(), tree.toArray());
    }

    /**
     * Test of toArray method, of class TreeSet.
     */
    @Test
    public void testToArray_GenericType() {
        FoldedTreeSet<Integer> tree = new FoldedTreeSet();
        ArrayList<Integer> list = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            list.add((1024 + i));
        }
        tree.addAll(list);
        Assert.assertArrayEquals(list.toArray(new Integer[tree.size()]), tree.toArray(new Integer[tree.size()]));
        Assert.assertArrayEquals(list.toArray(new Integer[(tree.size()) + 100]), tree.toArray(new Integer[(tree.size()) + 100]));
    }

    /**
     * Test of add method, of class TreeSet.
     */
    @Test
    public void testAdd() {
        FoldedTreeSet<String> simpleSet = new FoldedTreeSet();
        String entry = "apa";
        Assert.assertTrue(simpleSet.add(entry));
        Assert.assertFalse(simpleSet.add(entry));
        FoldedTreeSet<Integer> intSet = new FoldedTreeSet();
        for (int i = 512; i < 1024; i++) {
            Assert.assertTrue(intSet.add(i));
        }
        for (int i = -1024; i < (-512); i++) {
            Assert.assertTrue(intSet.add(i));
        }
        for (int i = 0; i < 512; i++) {
            Assert.assertTrue(intSet.add(i));
        }
        for (int i = -512; i < 0; i++) {
            Assert.assertTrue(intSet.add(i));
        }
        Assert.assertEquals(2048, intSet.size());
        FoldedTreeSet<FoldedTreeSetTest.Holder> set = new FoldedTreeSet();
        long[] longs = new long[23432];
        for (int i = 0; i < (longs.length); i++) {
            FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            while (set.contains(val)) {
                val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            } 
            longs[i] = val.getId();
            Assert.assertTrue(set.add(val));
        }
        Assert.assertEquals(longs.length, set.size());
        Arrays.sort(longs);
        Iterator<FoldedTreeSetTest.Holder> it = set.iterator();
        for (int i = 0; i < (longs.length); i++) {
            Assert.assertTrue(it.hasNext());
            FoldedTreeSetTest.Holder val = it.next();
            Assert.assertEquals(longs[i], val.getId());
        }
        // Specially constructed adds to exercise all code paths
        FoldedTreeSet<Integer> specialAdds = new FoldedTreeSet();
        // Fill node with even numbers
        for (int i = 0; i < 128; i += 2) {
            Assert.assertTrue(specialAdds.add(i));
        }
        // Remove left and add left
        Assert.assertTrue(specialAdds.remove(0));
        Assert.assertTrue(specialAdds.add((-1)));
        Assert.assertTrue(specialAdds.remove((-1)));
        // Add right and shift everything left
        Assert.assertTrue(specialAdds.add(127));
        Assert.assertTrue(specialAdds.remove(127));
        // Empty at both ends
        Assert.assertTrue(specialAdds.add(0));
        Assert.assertTrue(specialAdds.remove(0));
        Assert.assertTrue(specialAdds.remove(126));
        // Add in the middle left to slide entries left
        Assert.assertTrue(specialAdds.add(11));
        Assert.assertTrue(specialAdds.remove(11));
        // Add in the middle right to slide entries right
        Assert.assertTrue(specialAdds.add(99));
        Assert.assertTrue(specialAdds.remove(99));
        // Add existing entry in the middle of a node
        Assert.assertFalse(specialAdds.add(64));
    }

    @Test
    public void testAddOrReplace() {
        FoldedTreeSet<String> simpleSet = new FoldedTreeSet();
        String entry = "apa";
        Assert.assertNull(simpleSet.addOrReplace(entry));
        Assert.assertEquals(entry, simpleSet.addOrReplace(entry));
        FoldedTreeSet<Integer> intSet = new FoldedTreeSet();
        for (int i = 0; i < 1024; i++) {
            Assert.assertNull(intSet.addOrReplace(i));
        }
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals(i, intSet.addOrReplace(i).intValue());
        }
    }

    private static class Holder implements Comparable<FoldedTreeSetTest.Holder> {
        private final long id;

        public Holder(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        @Override
        public int compareTo(FoldedTreeSetTest.Holder o) {
            return (id) < (o.getId()) ? -1 : (id) > (o.getId()) ? 1 : 0;
        }
    }

    @Test
    public void testRemoveWithComparator() {
        FoldedTreeSet<FoldedTreeSetTest.Holder> set = new FoldedTreeSet();
        long[] longs = new long[98327];
        for (int i = 0; i < (longs.length); i++) {
            FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            while (set.contains(val)) {
                val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            } 
            longs[i] = val.getId();
            set.add(val);
        }
        Assert.assertEquals(longs.length, set.size());
        Comparator<Object> cmp = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                long lookup = ((long) (o1));
                long stored = ((FoldedTreeSetTest.Holder) (o2)).getId();
                return lookup < stored ? -1 : lookup > stored ? 1 : 0;
            }
        };
        for (long val : longs) {
            set.remove(val, cmp);
        }
        Assert.assertEquals(0, set.size());
        Assert.assertTrue(set.isEmpty());
    }

    @Test
    public void testGetWithComparator() {
        FoldedTreeSet<FoldedTreeSetTest.Holder> set = new FoldedTreeSet();
        long[] longs = new long[32147];
        for (int i = 0; i < (longs.length); i++) {
            FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            while (set.contains(val)) {
                val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            } 
            longs[i] = val.getId();
            set.add(val);
        }
        Assert.assertEquals(longs.length, set.size());
        Comparator<Object> cmp = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                long lookup = ((long) (o1));
                long stored = ((FoldedTreeSetTest.Holder) (o2)).getId();
                return lookup < stored ? -1 : lookup > stored ? 1 : 0;
            }
        };
        for (long val : longs) {
            Assert.assertEquals(val, set.get(val, cmp).getId());
        }
    }

    @Test
    public void testGet() {
        FoldedTreeSet<FoldedTreeSetTest.Holder> set = new FoldedTreeSet();
        long[] longs = new long[43277];
        for (int i = 0; i < (longs.length); i++) {
            FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            while (set.contains(val)) {
                val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            } 
            longs[i] = val.getId();
            set.add(val);
        }
        Assert.assertEquals(longs.length, set.size());
        for (long val : longs) {
            Assert.assertEquals(val, set.get(new FoldedTreeSetTest.Holder(val)).getId());
        }
    }

    /**
     * Test of remove method, of class TreeSet.
     */
    @Test
    public void testRemove() {
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        Assert.assertEquals(false, instance.remove("apa"));
        instance.add("apa");
        Assert.assertEquals(true, instance.remove("apa"));
        removeLeft();
        removeRight();
        removeAt();
        removeRandom();
    }

    /**
     * Test of containsAll method, of class TreeSet.
     */
    @Test
    public void testContainsAll() {
        Collection<String> list = Arrays.asList(new String[]{ "apa", "apa2", "apa" });
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        Assert.assertEquals(false, instance.containsAll(list));
        instance.addAll(list);
        Assert.assertEquals(true, instance.containsAll(list));
    }

    /**
     * Test of addAll method, of class TreeSet.
     */
    @Test
    public void testAddAll() {
        Collection<String> list = Arrays.asList(new String[]{ "apa", "apa2", "apa" });
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        Assert.assertEquals(true, instance.addAll(list));
        Assert.assertEquals(false, instance.addAll(list));// add same entries again

    }

    /**
     * Test of retainAll method, of class TreeSet.
     */
    @Test
    public void testRetainAll() {
        Collection<String> list = Arrays.asList(new String[]{ "apa", "apa2", "apa" });
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        instance.addAll(list);
        Assert.assertEquals(false, instance.retainAll(list));
        Assert.assertEquals(2, instance.size());
        Collection<String> list2 = Arrays.asList(new String[]{ "apa" });
        Assert.assertEquals(true, instance.retainAll(list2));
        Assert.assertEquals(1, instance.size());
    }

    /**
     * Test of removeAll method, of class TreeSet.
     */
    @Test
    public void testRemoveAll() {
        Collection<String> list = Arrays.asList(new String[]{ "apa", "apa2", "apa" });
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        Assert.assertEquals(false, instance.removeAll(list));
        instance.addAll(list);
        Assert.assertEquals(true, instance.removeAll(list));
        Assert.assertEquals(true, instance.isEmpty());
    }

    /**
     * Test of clear method, of class TreeSet.
     */
    @Test
    public void testClear() {
        FoldedTreeSet<String> instance = new FoldedTreeSet();
        instance.clear();
        Assert.assertEquals(true, instance.isEmpty());
        instance.add("apa");
        Assert.assertEquals(false, instance.isEmpty());
        instance.clear();
        Assert.assertEquals(true, instance.isEmpty());
    }

    @Test
    public void testFillRatio() {
        FoldedTreeSet<Integer> set = new FoldedTreeSet();
        final int size = 1024;
        for (int i = 1; i <= size; i++) {
            set.add(i);
            Assert.assertEquals(("Iteration: " + i), 1.0, set.fillRatio(), 0.0);
        }
        for (int i = 1; i <= (size / 2); i++) {
            set.remove((i * 2));
            // Need the max since all the removes from the last node doesn't
            // affect the fill ratio
            Assert.assertEquals(("Iteration: " + i), Math.max(((size - i) / ((double) (size))), 0.53125), set.fillRatio(), 0.0);
        }
    }

    @Test
    public void testCompact() {
        FoldedTreeSet<FoldedTreeSetTest.Holder> set = new FoldedTreeSet();
        long[] longs = new long[24553];
        for (int i = 0; i < (longs.length); i++) {
            FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            while (set.contains(val)) {
                val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            } 
            longs[i] = val.getId();
            set.add(val);
        }
        Assert.assertEquals(longs.length, set.size());
        long[] longs2 = new long[longs.length];
        for (int i = 0; i < (longs2.length); i++) {
            FoldedTreeSetTest.Holder val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            while (set.contains(val)) {
                val = new FoldedTreeSetTest.Holder(FoldedTreeSetTest.srand.nextLong());
            } 
            longs2[i] = val.getId();
            set.add(val);
        }
        Assert.assertEquals(((longs.length) + (longs2.length)), set.size());
        // Create fragementation
        for (long val : longs) {
            Assert.assertTrue(set.remove(new FoldedTreeSetTest.Holder(val)));
        }
        Assert.assertEquals(longs2.length, set.size());
        Assert.assertFalse(set.compact(0));
        Assert.assertTrue(set.compact(Long.MAX_VALUE));
        Assert.assertEquals(longs2.length, set.size());
        for (long val : longs) {
            Assert.assertFalse(set.remove(new FoldedTreeSetTest.Holder(val)));
        }
        for (long val : longs2) {
            Assert.assertEquals(val, set.get(new FoldedTreeSetTest.Holder(val)).getId());
        }
    }
}

