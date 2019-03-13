/**
 * Copyright (c), Data Geekery GmbH, contact@datageekery.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jooq.lambda;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple5;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class TupleTest {
    @Test
    public void testEqualsHashCode() {
        Set<Tuple2<Integer, String>> set = new HashSet<>();
        set.add(tuple(1, "abc"));
        Assert.assertEquals(1, set.size());
        set.add(tuple(1, "abc"));
        Assert.assertEquals(1, set.size());
        set.add(tuple(null, null));
        Assert.assertEquals(2, set.size());
        set.add(tuple(null, null));
        Assert.assertEquals(2, set.size());
        set.add(tuple(1, null));
        Assert.assertEquals(3, set.size());
        set.add(tuple(1, null));
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void testEqualsNull() {
        Assert.assertFalse(tuple(1).equals(null));
        Assert.assertFalse(tuple(1, 2).equals(null));
        Assert.assertFalse(tuple(1, 2, 3).equals(null));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("(1, abc)", tuple(1, "abc").toString());
    }

    @Test
    public void testToArrayAndToList() {
        Assert.assertEquals(Arrays.asList(1, "a", null), Arrays.asList(tuple(1, "a", null).toArray()));
        Assert.assertEquals(Arrays.asList(1, "a", null), tuple(1, "a", null).toList());
    }

    @Test
    public void testToMap() {
        Map<String, Object> m1 = new LinkedHashMap<>();
        m1.put("v1", 1);
        m1.put("v2", "a");
        m1.put("v3", null);
        Assert.assertEquals(m1, tuple(1, "a", null).toMap());
        Map<Integer, Object> m2 = new LinkedHashMap<>();
        m2.put(0, 1);
        m2.put(1, "a");
        m2.put(2, null);
        Assert.assertEquals(m2, tuple(1, "a", null).toMap(( i) -> i));
        Map<String, Object> m3 = new LinkedHashMap<>();
        m3.put("A", 1);
        m3.put("B", "a");
        m3.put("C", null);
        Assert.assertEquals(m3, tuple(1, "a", null).toMap("A", "B", "C"));
        Assert.assertEquals(m3, tuple(1, "a", null).toMap(() -> "A", () -> "B", () -> "C"));
    }

    @Test
    public void testToSeq() {
        Assert.assertEquals(Arrays.asList(1, "a", null), tuple(1, "a", null).toSeq().toList());
    }

    @Test
    public void testSwap() {
        Assert.assertEquals(tuple(1, "a"), tuple("a", 1).swap());
        Assert.assertEquals(tuple(1, "a"), tuple(1, "a").swap().swap());
    }

    @Test
    public void testConcat() {
        Assert.assertEquals(tuple(1, "a"), tuple(1).concat("a"));
        Assert.assertEquals(tuple(1, "a", 2), tuple(1).concat("a").concat(2));
        Assert.assertEquals(tuple(1, "a"), tuple(1).concat(tuple("a")));
        Assert.assertEquals(tuple(1, "a", 2, "b", 3, "c", 4, "d"), tuple(1).concat(tuple("a", 2, "b").concat(tuple(3).concat(tuple("c", 4, "d")))));
    }

    @Test
    public void testCompareTo() {
        Set<Tuple2<Integer, String>> set = new TreeSet<>();
        set.add(tuple(2, "a"));
        set.add(tuple(1, "b"));
        set.add(tuple(1, "a"));
        set.add(tuple(2, "a"));
        Assert.assertEquals(3, set.size());
        Assert.assertEquals(Arrays.asList(tuple(1, "a"), tuple(1, "b"), tuple(2, "a")), new java.util.ArrayList(set));
    }

    @Test
    public void testCompareToWithNulls() {
        Set<Tuple2<Integer, String>> set = new TreeSet<>();
        set.add(tuple(2, "a"));
        set.add(tuple(1, "b"));
        set.add(tuple(1, null));
        set.add(tuple(null, "a"));
        set.add(tuple(null, "b"));
        set.add(tuple(null, null));
        Assert.assertEquals(6, set.size());
        Assert.assertEquals(Arrays.asList(tuple(1, "b"), tuple(1, null), tuple(2, "a"), tuple(null, "a"), tuple(null, "b"), tuple(null, null)), new java.util.ArrayList(set));
    }

    @Test
    public void testCompareToWithNonComparables() {
        Set<Tuple2<Integer, Object>> set = new TreeSet<>();
        Utils.assertThrows(ClassCastException.class, () -> set.add(tuple(1, new Object())));
        Assert.assertEquals(0, set.size());
    }

    @Test
    public void testIterable() {
        LinkedList<Object> list = new LinkedList(tuple(1, "b", null).toList());
        for (Object o : tuple(1, "b", null)) {
            Assert.assertEquals(list.poll(), o);
        }
    }

    @Test
    public void testFunctions() {
        Assert.assertEquals("(1, b, null)", tuple(1, "b", null).map(( v1, v2, v3) -> tuple(v1, v2, v3).toString()));
        Assert.assertEquals("1-b", tuple(1, "b", null).map(( v1, v2, v3) -> (v1 + "-") + v2));
    }

    @Test
    public void testMapN() {
        Assert.assertEquals(tuple(1, "a", 2, "b"), tuple(1, null, 2, null).map2(( v) -> "a").map4(( v) -> "b"));
    }

    @Test
    public void testOverlaps() {
        Assert.assertTrue(Tuple2.overlaps(tuple(1, 3), tuple(1, 3)));
        Assert.assertTrue(Tuple2.overlaps(tuple(1, 3), tuple(2, 3)));
        Assert.assertTrue(Tuple2.overlaps(tuple(1, 3), tuple(2, 4)));
        Assert.assertTrue(Tuple2.overlaps(tuple(1, 3), tuple(3, 4)));
        Assert.assertFalse(Tuple2.overlaps(tuple(1, 3), tuple(4, 5)));
        Assert.assertFalse(Tuple2.overlaps(tuple(1, 1), tuple(2, 2)));
        Assert.assertTrue(range(1, 3).overlaps(tuple(1, 3)));
        Assert.assertTrue(range(1, 3).overlaps(tuple(2, 3)));
        Assert.assertTrue(range(1, 3).overlaps(tuple(2, 4)));
        Assert.assertTrue(range(1, 3).overlaps(tuple(3, 4)));
        Assert.assertFalse(range(1, 3).overlaps(tuple(4, 5)));
        Assert.assertFalse(range(1, 1).overlaps(2, 2));
    }

    @Test
    public void testIntersect() {
        Assert.assertEquals(Optional.of(tuple(2, 3)), range(1, 3).intersect(range(2, 4)));
        Assert.assertEquals(Optional.of(tuple(2, 3)), range(3, 1).intersect(range(4, 2)));
        Assert.assertEquals(Optional.of(tuple(3, 3)), range(1, 3).intersect(3, 5));
        Assert.assertEquals(Optional.empty(), range(1, 3).intersect(range(4, 5)));
    }

    @Test
    public void testRange() {
        Assert.assertEquals(range(1, 3), range(3, 1));
    }

    @Test
    public void testCollectorsWithAgg() {
        Tuple5<Long, Optional<BigDecimal>, Optional<BigDecimal>, Optional<BigDecimal>, Optional<BigDecimal>> result = Stream.of(new BigDecimal(0), new BigDecimal(1), new BigDecimal(2)).collect(collectors(Agg.count(), Agg.sum(), Agg.avg(), Agg.<BigDecimal>min(), Agg.<BigDecimal>max()));
        Assert.assertEquals(tuple(3L, Optional.of(new BigDecimal(3)), Optional.of(new BigDecimal(1)), Optional.of(new BigDecimal(0)), Optional.of(new BigDecimal(2))), result);
    }

    @Test
    public void testCollectors() {
        Assert.assertEquals(tuple(3L), Stream.of(1, 2, 3).collect(collectors(Collectors.counting())));
        Assert.assertEquals(tuple(3L, "1, 2, 3"), Stream.of(1, 2, 3).collect(collectors(Collectors.counting(), Collectors.mapping(Object::toString, Collectors.joining(", ")))));
        Assert.assertEquals(tuple(3L, "1, 2, 3", 2.0), Stream.of(1, 2, 3).collect(collectors(Collectors.counting(), Collectors.mapping(Object::toString, Collectors.joining(", ")), Collectors.averagingInt(Integer::intValue))));
    }

    @Test
    public void testLimit() {
        Assert.assertEquals(tuple(), tuple(1, "A", 2, "B").limit0());
        Assert.assertEquals(tuple(1), tuple(1, "A", 2, "B").limit1());
        Assert.assertEquals(tuple(1, "A"), tuple(1, "A", 2, "B").limit2());
        Assert.assertEquals(tuple(1, "A", 2), tuple(1, "A", 2, "B").limit3());
        Assert.assertEquals(tuple(1, "A", 2, "B"), tuple(1, "A", 2, "B").limit4());
    }

    @Test
    public void testSkip() {
        Assert.assertEquals(tuple(), tuple(1, "A", 2, "B").skip4());
        Assert.assertEquals(tuple("B"), tuple(1, "A", 2, "B").skip3());
        Assert.assertEquals(tuple(2, "B"), tuple(1, "A", 2, "B").skip2());
        Assert.assertEquals(tuple("A", 2, "B"), tuple(1, "A", 2, "B").skip1());
        Assert.assertEquals(tuple(1, "A", 2, "B"), tuple(1, "A", 2, "B").skip0());
    }

    @Test
    public void testSplit() {
        Assert.assertEquals(tuple(tuple(), tuple(1, "A", 2, "B")), tuple(1, "A", 2, "B").split0());
        Assert.assertEquals(tuple(tuple(1), tuple("A", 2, "B")), tuple(1, "A", 2, "B").split1());
        Assert.assertEquals(// Strange IntelliJ Bug here
        tuple(tuple(1, "A"), new Tuple2(2, "B")), tuple(1, "A", 2, "B").split2());
        Assert.assertEquals(tuple(tuple(1, "A", 2), tuple("B")), tuple(1, "A", 2, "B").split3());
        Assert.assertEquals(tuple(tuple(1, "A", 2, "B"), tuple()), tuple(1, "A", 2, "B").split4());
    }
}

