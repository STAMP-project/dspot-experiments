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
package org.apache.mahout.math.random;


import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.MahoutTestCase;
import org.junit.Test;


public class MultinomialTest extends MahoutTestCase {
    @Test(expected = IllegalArgumentException.class)
    public void testNoValues() {
        Multiset<String> emptySet = HashMultiset.create();
        new Multinomial(emptySet);
    }

    @Test
    public void testSingleton() {
        Multiset<String> oneThing = HashMultiset.create();
        oneThing.add("one");
        Multinomial<String> s = new Multinomial(oneThing);
        assertEquals("one", s.sample(0));
        assertEquals("one", s.sample(0.1));
        assertEquals("one", s.sample(1));
    }

    @Test
    public void testEvenSplit() {
        Multiset<String> stuff = HashMultiset.create();
        for (int i = 0; i < 5; i++) {
            stuff.add(String.valueOf(i));
        }
        Multinomial<String> s = new Multinomial(stuff);
        double EPSILON = 1.0E-15;
        Multiset<String> cnt = HashMultiset.create();
        for (int i = 0; i < 5; i++) {
            cnt.add(s.sample((i * 0.2)));
            cnt.add(s.sample(((i * 0.2) + EPSILON)));
            cnt.add(s.sample((((i + 1) * 0.2) - EPSILON)));
        }
        assertEquals(5, cnt.elementSet().size());
        for (String v : cnt.elementSet()) {
            assertEquals(3, cnt.count(v), 1.01);
        }
        assertTrue(cnt.contains(s.sample(1)));
        assertEquals(s.sample((1 - EPSILON)), s.sample(1));
    }

    @Test
    public void testPrime() {
        List<String> data = Lists.newArrayList();
        for (int i = 0; i < 17; i++) {
            String s = "0";
            if ((i & 1) != 0) {
                s = "1";
            }
            if ((i & 2) != 0) {
                s = "2";
            }
            if ((i & 4) != 0) {
                s = "3";
            }
            if ((i & 8) != 0) {
                s = "4";
            }
            data.add(s);
        }
        Multiset<String> stuff = HashMultiset.create();
        for (String x : data) {
            stuff.add(x);
        }
        Multinomial<String> s0 = new Multinomial(stuff);
        Multinomial<String> s1 = new Multinomial(stuff);
        Multinomial<String> s2 = new Multinomial(stuff);
        double EPSILON = 1.0E-15;
        Multiset<String> cnt = HashMultiset.create();
        for (int i = 0; i < 50; i++) {
            double p0 = i * 0.02;
            double p1 = (i + 1) * 0.02;
            cnt.add(s0.sample(p0));
            cnt.add(s0.sample((p0 + EPSILON)));
            cnt.add(s0.sample((p1 - EPSILON)));
            assertEquals(s0.sample(p0), s1.sample(p0));
            assertEquals(s0.sample((p0 + EPSILON)), s1.sample((p0 + EPSILON)));
            assertEquals(s0.sample((p1 - EPSILON)), s1.sample((p1 - EPSILON)));
            assertEquals(s0.sample(p0), s2.sample(p0));
            assertEquals(s0.sample((p0 + EPSILON)), s2.sample((p0 + EPSILON)));
            assertEquals(s0.sample((p1 - EPSILON)), s2.sample((p1 - EPSILON)));
        }
        assertEquals(s0.sample(0), s1.sample(0));
        assertEquals(s0.sample((0 + EPSILON)), s1.sample((0 + EPSILON)));
        assertEquals(s0.sample((1 - EPSILON)), s1.sample((1 - EPSILON)));
        assertEquals(s0.sample(1), s1.sample(1));
        assertEquals(s0.sample(0), s2.sample(0));
        assertEquals(s0.sample((0 + EPSILON)), s2.sample((0 + EPSILON)));
        assertEquals(s0.sample((1 - EPSILON)), s2.sample((1 - EPSILON)));
        assertEquals(s0.sample(1), s2.sample(1));
        assertEquals(5, cnt.elementSet().size());
        // regression test, really.  These values depend on the original seed and exact algorithm.
        // the actual values should be within about 2 of these, however, almost regardless of seed
        Map<String, Integer> ref = ImmutableMap.of("3", 35, "2", 18, "1", 9, "0", 16, "4", 72);
        for (String v : cnt.elementSet()) {
            assertTrue(((Math.abs(((ref.get(v)) - (cnt.count(v))))) <= 2));
        }
        assertTrue(cnt.contains(s0.sample(1)));
        assertEquals(s0.sample((1 - EPSILON)), s0.sample(1));
    }

    @Test
    public void testInsert() {
        Random rand = RandomUtils.getRandom();
        Multinomial<Integer> table = new Multinomial();
        double[] p = new double[10];
        for (int i = 0; i < 10; i++) {
            p[i] = rand.nextDouble();
            table.add(i, p[i]);
        }
        MultinomialTest.checkSelfConsistent(table);
        for (int i = 0; i < 10; i++) {
            assertEquals(p[i], table.getWeight(i), 0);
        }
    }

    @Test
    public void testSetZeroWhileIterating() {
        Multinomial<Integer> table = new Multinomial();
        for (int i = 0; i < 10000; ++i) {
            table.add(i, i);
        }
        // Setting a sample's weight to 0 removes from the items map.
        // If that map is used when iterating (it used to be), it will
        // trigger a ConcurrentModificationException.
        for (Integer sample : table) {
            table.set(sample, 0);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNoNullValuesAllowed() {
        Multinomial<Integer> table = new Multinomial();
        // No null values should be allowed.
        table.add(null, 1);
    }

    @Test
    public void testDeleteAndUpdate() {
        Random rand = RandomUtils.getRandom();
        Multinomial<Integer> table = new Multinomial();
        assertEquals(0, table.getWeight(), 1.0E-9);
        double total = 0;
        double[] p = new double[10];
        for (int i = 0; i < 10; i++) {
            p[i] = rand.nextDouble();
            table.add(i, p[i]);
            total += p[i];
            assertEquals(total, table.getWeight(), 1.0E-9);
        }
        assertEquals(total, table.getWeight(), 1.0E-9);
        MultinomialTest.checkSelfConsistent(table);
        double delta = (p[7]) + (p[8]);
        table.delete(7);
        p[7] = 0;
        table.set(8, 0);
        p[8] = 0;
        total -= delta;
        MultinomialTest.checkSelfConsistent(table);
        assertEquals(total, table.getWeight(), 1.0E-9);
        for (int i = 0; i < 10; i++) {
            assertEquals(p[i], table.getWeight(i), 0);
            assertEquals(((p[i]) / total), table.getProbability(i), 1.0E-10);
        }
        table.set(9, 5.1);
        total -= p[9];
        p[9] = 5.1;
        total += 5.1;
        assertEquals(total, table.getWeight(), 1.0E-9);
        for (int i = 0; i < 10; i++) {
            assertEquals(p[i], table.getWeight(i), 0);
            assertEquals(((p[i]) / total), table.getProbability(i), 1.0E-10);
        }
        MultinomialTest.checkSelfConsistent(table);
        for (int i = 0; i < 10; i++) {
            assertEquals(p[i], table.getWeight(i), 0);
        }
    }
}

