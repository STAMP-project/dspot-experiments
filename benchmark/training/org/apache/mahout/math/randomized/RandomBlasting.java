/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math.randomized;


import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.Repeat;
import com.carrotsearch.randomizedtesting.annotations.Seed;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.mahout.math.map.OpenIntIntHashMap;
import org.apache.mahout.math.map.OpenIntObjectHashMap;
import org.apache.mahout.math.map.OpenObjectIntHashMap;
import org.apache.mahout.math.set.AbstractIntSet;
import org.apache.mahout.math.set.OpenIntHashSet;
import org.junit.Test;


/**
 * Some randomized tests against Java Util Collections.
 */
public class RandomBlasting extends RandomizedTest {
    private static enum Operation {

        ADD,
        REMOVE,
        CLEAR,
        INDEXOF,
        ISEMPTY,
        SIZE;}

    @Test
    @Repeat(iterations = 20)
    public void testAgainstReferenceOpenObjectIntHashMap() {
        OpenObjectIntHashMap<Integer> base = new OpenObjectIntHashMap();
        Map<Integer, Integer> reference = new HashMap<>();
        List<RandomBlasting.Operation> ops = Lists.newArrayList();
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ADD, 60);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.REMOVE, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.INDEXOF, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.CLEAR, 5);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ISEMPTY, 2);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.SIZE, 2);
        int max = randomIntBetween(1000, 20000);
        for (int reps = 0; reps < max; reps++) {
            // Ensure some collisions among keys.
            int k = randomIntBetween(0, (max / 4));
            int v = randomInt();
            switch (randomFrom(ops)) {
                case ADD :
                    assertEquals(((reference.put(k, v)) == null), base.put(k, v));
                    break;
                case REMOVE :
                    assertEquals(((reference.remove(k)) != null), base.removeKey(k));
                    break;
                case INDEXOF :
                    assertEquals(reference.containsKey(k), base.containsKey(k));
                    break;
                case CLEAR :
                    reference.clear();
                    base.clear();
                    break;
                case ISEMPTY :
                    assertEquals(reference.isEmpty(), base.isEmpty());
                    break;
                case SIZE :
                    assertEquals(reference.size(), base.size());
                    break;
                default :
                    throw new RuntimeException();
            }
        }
    }

    @Test
    @Repeat(iterations = 20)
    public void testAgainstReferenceOpenIntObjectHashMap() {
        OpenIntObjectHashMap<Integer> base = new OpenIntObjectHashMap();
        Map<Integer, Integer> reference = new HashMap<>();
        List<RandomBlasting.Operation> ops = Lists.newArrayList();
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ADD, 60);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.REMOVE, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.INDEXOF, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.CLEAR, 5);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ISEMPTY, 2);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.SIZE, 2);
        int max = randomIntBetween(1000, 20000);
        for (int reps = 0; reps < max; reps++) {
            // Ensure some collisions among keys.
            int k = randomIntBetween(0, (max / 4));
            int v = randomInt();
            switch (randomFrom(ops)) {
                case ADD :
                    assertEquals(((reference.put(k, v)) == null), base.put(k, v));
                    break;
                case REMOVE :
                    assertEquals(((reference.remove(k)) != null), base.removeKey(k));
                    break;
                case INDEXOF :
                    assertEquals(reference.containsKey(k), base.containsKey(k));
                    break;
                case CLEAR :
                    reference.clear();
                    base.clear();
                    break;
                case ISEMPTY :
                    assertEquals(reference.isEmpty(), base.isEmpty());
                    break;
                case SIZE :
                    assertEquals(reference.size(), base.size());
                    break;
                default :
                    throw new RuntimeException();
            }
        }
    }

    @Test
    @Repeat(iterations = 20)
    public void testAgainstReferenceOpenIntIntHashMap() {
        OpenIntIntHashMap base = new OpenIntIntHashMap();
        HashMap<Integer, Integer> reference = new HashMap<>();
        List<RandomBlasting.Operation> ops = Lists.newArrayList();
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ADD, 60);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.REMOVE, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.INDEXOF, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.CLEAR, 5);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ISEMPTY, 2);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.SIZE, 2);
        int max = randomIntBetween(1000, 20000);
        for (int reps = 0; reps < max; reps++) {
            // Ensure some collisions among keys.
            int k = randomIntBetween(0, (max / 4));
            int v = randomInt();
            switch (randomFrom(ops)) {
                case ADD :
                    Integer prevValue = reference.put(k, v);
                    if (prevValue == null) {
                        assertEquals(true, base.put(k, v));
                    } else {
                        assertEquals(prevValue.intValue(), base.get(k));
                        assertEquals(false, base.put(k, v));
                    }
                    break;
                case REMOVE :
                    assertEquals(reference.containsKey(k), base.containsKey(k));
                    Integer removed = reference.remove(k);
                    if (removed == null) {
                        assertEquals(false, base.removeKey(k));
                    } else {
                        assertEquals(removed.intValue(), base.get(k));
                        assertEquals(true, base.removeKey(k));
                    }
                    break;
                case INDEXOF :
                    assertEquals(reference.containsKey(k), base.containsKey(k));
                    break;
                case CLEAR :
                    reference.clear();
                    base.clear();
                    break;
                case ISEMPTY :
                    assertEquals(reference.isEmpty(), base.isEmpty());
                    break;
                case SIZE :
                    assertEquals(reference.size(), base.size());
                    break;
                default :
                    throw new RuntimeException();
            }
        }
    }

    @Test
    @Repeat(iterations = 20)
    public void testAgainstReferenceOpenIntHashSet() {
        AbstractIntSet base = new OpenIntHashSet();
        HashSet<Integer> reference = Sets.newHashSet();
        List<RandomBlasting.Operation> ops = Lists.newArrayList();
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ADD, 60);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.REMOVE, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.INDEXOF, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.CLEAR, 5);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ISEMPTY, 2);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.SIZE, 2);
        int max = randomIntBetween(1000, 20000);
        for (int reps = 0; reps < max; reps++) {
            // Ensure some collisions among keys.
            int k = randomIntBetween(0, (max / 4));
            switch (randomFrom(ops)) {
                case ADD :
                    assertEquals(reference.add(k), base.add(k));
                    break;
                case REMOVE :
                    assertEquals(reference.remove(k), base.remove(k));
                    break;
                case INDEXOF :
                    assertEquals(reference.contains(k), base.contains(k));
                    break;
                case CLEAR :
                    reference.clear();
                    base.clear();
                    break;
                case ISEMPTY :
                    assertEquals(reference.isEmpty(), base.isEmpty());
                    break;
                case SIZE :
                    assertEquals(reference.size(), base.size());
                    break;
                default :
                    throw new RuntimeException();
            }
        }
    }

    @Seed("deadbeef")
    @Test
    @Repeat(iterations = 20)
    public void testAgainstReferenceOpenHashSet() {
        Set<Integer> base = new org.apache.mahout.math.set.OpenHashSet();
        Set<Integer> reference = Sets.newHashSet();
        List<RandomBlasting.Operation> ops = Lists.newArrayList();
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ADD, 60);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.REMOVE, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.INDEXOF, 30);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.CLEAR, 5);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.ISEMPTY, 2);
        RandomBlasting.addOp(ops, RandomBlasting.Operation.SIZE, 2);
        int max = randomIntBetween(1000, 20000);
        for (int reps = 0; reps < max; reps++) {
            // Ensure some collisions among keys.
            int k = randomIntBetween(0, (max / 4));
            switch (randomFrom(ops)) {
                case ADD :
                    assertEquals(reference.contains(k), base.contains(k));
                    break;
                case REMOVE :
                    assertEquals(reference.remove(k), base.remove(k));
                    break;
                case INDEXOF :
                    assertEquals(reference.contains(k), base.contains(k));
                    break;
                case CLEAR :
                    reference.clear();
                    base.clear();
                    break;
                case ISEMPTY :
                    assertEquals(reference.isEmpty(), base.isEmpty());
                    break;
                case SIZE :
                    assertEquals(reference.size(), base.size());
                    break;
                default :
                    throw new RuntimeException();
            }
        }
    }

    /**
     *
     *
     * @see "https://issues.apache.org/jira/browse/MAHOUT-1225"
     */
    @Test
    public void testMahout1225() {
        AbstractIntSet s = new OpenIntHashSet();
        s.clear();
        s.add(23);
        s.add(46);
        s.clear();
        s.add(70);
        s.add(93);
        s.contains(100);
    }

    /**
     *
     */
    @Test
    public void testClearTable() throws Exception {
        OpenObjectIntHashMap<Integer> m = new OpenObjectIntHashMap();
        m.clear();// rehash from the default capacity to the next prime after 1 (3).

        m.put(1, 2);
        m.clear();// Should clear internal references.

        Field tableField = m.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = ((Object[]) (tableField.get(m)));
        assertEquals(Sets.newHashSet(Arrays.asList(new Object[]{ null })), Sets.newHashSet(Arrays.asList(table)));
    }
}

