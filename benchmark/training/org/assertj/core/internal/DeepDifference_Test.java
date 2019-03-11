/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.util.BigDecimalComparator;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Based on the deep equals tests of https://github.com/jdereg/java-util
 *
 * @author John DeRegnaucourt
 * @author sapradhan8
 * @author Pascal Schumacher
 */
public class DeepDifference_Test {
    @Test
    public void testSameObject() {
        Date date1 = new Date();
        Date date2 = date1;
        assertHaveNoDifferences(date1, date2);
    }

    @Test
    public void testEqualsWithNull() {
        Date date1 = new Date();
        assertHaveDifferences(null, date1);
        assertHaveDifferences(date1, null);
    }

    @Test
    public void testBigDecimals() {
        BigDecimal bigDecimal1 = new BigDecimal("42.5");
        BigDecimal bigDecimal2 = new BigDecimal("42.5");
        assertHaveNoDifferences(bigDecimal1, bigDecimal2);
    }

    @Test
    public void testWithDifferentFields() {
        assertHaveDifferences("one", 1);
        assertHaveDifferences(new DeepDifference_Test.Wrapper(new DeepDifference_Test.Wrapper("one")), new DeepDifference_Test.Wrapper("one"));
    }

    @Test
    public void testPOJOequals() {
        DeepDifference_Test.Class1 x = new DeepDifference_Test.Class1(true, Math.tan(((Math.PI) / 4)), 1);
        DeepDifference_Test.Class1 y = new DeepDifference_Test.Class1(true, 1.0, 1);
        assertHaveNoDifferences(x, y);
        assertHaveDifferences(x, new DeepDifference_Test.Class1());
        DeepDifference_Test.Class2 a = new DeepDifference_Test.Class2(((float) (Math.atan(1.0))), "hello", ((short) (2)), new DeepDifference_Test.Class1(false, Math.sin(0.75), 5));
        DeepDifference_Test.Class2 b = new DeepDifference_Test.Class2((((float) (Math.PI)) / 4), "hello", ((short) (2)), new DeepDifference_Test.Class1(false, ((2 * (Math.cos((0.75 / 2)))) * (Math.sin((0.75 / 2)))), 5));
        assertHaveNoDifferences(a, b);
        assertHaveDifferences(a, new DeepDifference_Test.Class2(((float) (Math.atan(2.0))), "hello", ((short) (2)), new DeepDifference_Test.Class1(false, Math.sin(0.75), 5)));
    }

    @Test
    public void testPrimitiveArrays() {
        int[] array1 = new int[]{ 2, 4, 5, 6, 3, 1, 3, 3, 5, 22 };
        int[] array2 = new int[]{ 2, 4, 5, 6, 3, 1, 3, 3, 5, 22 };
        assertHaveNoDifferences(array1, array2);
        int[] array3 = new int[]{ 3, 4, 7 };
        assertHaveDifferences(array1, array3);
        float[] array4 = new float[]{ 3.4F, 5.5F };
        assertHaveDifferences(array1, array4);
    }

    @Test
    public void testOrderedCollection() {
        List<String> a = Lists.newArrayList("one", "two", "three", "four", "five");
        List<String> b = new LinkedList<>();
        b.addAll(a);
        assertHaveNoDifferences(a, b);
        List<Integer> c = Lists.newArrayList(1, 2, 3, 4, 5);
        assertHaveDifferences(a, c);
        List<Integer> d = Lists.newArrayList(4, 6);
        assertHaveDifferences(c, d);
        List<DeepDifference_Test.Class1> x1 = Lists.newArrayList(new DeepDifference_Test.Class1(true, Math.log(Math.pow(Math.E, 2)), 6), new DeepDifference_Test.Class1(true, Math.tan(((Math.PI) / 4)), 1));
        List<DeepDifference_Test.Class1> x2 = Lists.newArrayList(new DeepDifference_Test.Class1(true, 2, 6), new DeepDifference_Test.Class1(true, 1, 1));
        assertHaveNoDifferences(x1, x2);
    }

    @Test
    public void testUnorderedCollection() {
        Set<String> a = Sets.newLinkedHashSet("one", "two", "three", "four", "five");
        Set<String> b = Sets.newLinkedHashSet("three", "five", "one", "four", "two");
        assertHaveNoDifferences(a, b);
        assertHaveNoDifferences(a, b, ObjectsBaseTest.noFieldComparators(), new TypeComparators());
        Set<Integer> c = Sets.newLinkedHashSet(1, 2, 3, 4, 5);
        assertHaveDifferences(a, c);
        assertHaveDifferences(a, c, ObjectsBaseTest.noFieldComparators(), null);
        Set<Integer> d = Sets.newLinkedHashSet(4, 2, 6);
        assertHaveDifferences(c, d);
        assertHaveDifferences(c, d, ObjectsBaseTest.noFieldComparators(), null);
        Set<DeepDifference_Test.Class1> x1 = Sets.newLinkedHashSet(new DeepDifference_Test.Class1(true, Math.log(Math.pow(Math.E, 2)), 6), new DeepDifference_Test.Class1(true, Math.tan(((Math.PI) / 4)), 1));
        Set<DeepDifference_Test.Class1> x2 = Sets.newLinkedHashSet(new DeepDifference_Test.Class1(true, 1, 1), new DeepDifference_Test.Class1(true, 2, 6));
        assertHaveNoDifferences(x1, x2);
    }

    @Test
    public void testUnorderedCollectionWithCustomComparatorsByType() {
        TypeComparators comparatorsWithBigDecimalComparator = new TypeComparators();
        comparatorsWithBigDecimalComparator.put(BigDecimal.class, BigDecimalComparator.BIG_DECIMAL_COMPARATOR);
        Set<BigDecimal> a = Sets.newLinkedHashSet(new BigDecimal("1.0"), new BigDecimal("3"), new BigDecimal("2"), new BigDecimal("4"));
        Set<BigDecimal> b = Sets.newLinkedHashSet(new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("2.0"), new BigDecimal("3"));
        assertHaveNoDifferences(a, b, ObjectsBaseTest.noFieldComparators(), comparatorsWithBigDecimalComparator);
        Set<BigDecimal> c = Sets.newLinkedHashSet(new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("2.2"), new BigDecimal("3"));
        assertHaveDifferences(a, c, ObjectsBaseTest.noFieldComparators(), comparatorsWithBigDecimalComparator);
    }

    @Test
    public void testUnorderedCollectionWithCustomComparatorsByFieldName() {
        DeepDifference_Test.SetWrapper a = new DeepDifference_Test.SetWrapper(Sets.newLinkedHashSet(new DeepDifference_Test.Wrapper("one"), new DeepDifference_Test.Wrapper("two")));
        DeepDifference_Test.SetWrapper b = new DeepDifference_Test.SetWrapper(Sets.newLinkedHashSet(new DeepDifference_Test.Wrapper("1"), new DeepDifference_Test.Wrapper("2")));
        Map<String, Comparator<?>> fieldComparators = new HashMap<>();
        fieldComparators.put("set.o", AlwaysEqualComparator.ALWAY_EQUALS_STRING);
        assertHaveNoDifferences(a, b, fieldComparators, TypeComparators.defaultTypeComparators());
    }

    @Test
    public void testEquivalentMaps() {
        Map<String, Integer> map1 = new LinkedHashMap<>();
        fillMap(map1);
        Map<String, Integer> map2 = new HashMap<>();
        fillMap(map2);
        assertHaveNoDifferences(map1, map2);
        Assertions.assertThat(DeepDifference.deepHashCode(map1)).isEqualTo(DeepDifference.deepHashCode(map2));
        map1 = new TreeMap<>();
        fillMap(map1);
        map2 = new TreeMap<>();
        map2 = Collections.synchronizedSortedMap(((SortedMap<String, Integer>) (map2)));
        fillMap(map2);
        assertHaveNoDifferences(map1, map2);
        Assertions.assertThat(DeepDifference.deepHashCode(map1)).isEqualTo(DeepDifference.deepHashCode(map2));
    }

    @Test
    public void testInequivalentMaps() {
        Map<String, Integer> map1 = new TreeMap<>();
        fillMap(map1);
        Map<String, Integer> map2 = new HashMap<>();
        fillMap(map2);
        // Sorted versus non-sorted Map
        assertHaveDifferences(map1, map2);
        // Hashcodes are equals because the Maps have same elements
        Assertions.assertThat(DeepDifference.deepHashCode(map1)).isEqualTo(DeepDifference.deepHashCode(map2));
        map2 = new TreeMap<>();
        fillMap(map2);
        map2.remove("kilo");
        assertHaveDifferences(map1, map2);
        // Hashcodes are different because contents of maps are different
        Assertions.assertThat(DeepDifference.deepHashCode(map1)).isNotEqualTo(DeepDifference.deepHashCode(map2));
        // Inequality because ConcurrentSkipListMap is a SortedMap
        map1 = new HashMap<>();
        fillMap(map1);
        map2 = new ConcurrentSkipListMap<>();
        fillMap(map2);
        assertHaveDifferences(map1, map2);
        map1 = new TreeMap<>();
        fillMap(map1);
        map2 = new ConcurrentSkipListMap<>();
        fillMap(map2);
        assertHaveNoDifferences(map1, map2);
        map2.remove("papa");
        assertHaveDifferences(map1, map2);
    }

    @Test
    public void testEquivalentCollections() {
        // ordered Collection
        Collection<String> col1 = new ArrayList<>();
        fillCollection(col1);
        Collection<String> col2 = new LinkedList<>();
        fillCollection(col2);
        assertHaveNoDifferences(col1, col2);
        Assertions.assertThat(DeepDifference.deepHashCode(col1)).isEqualTo(DeepDifference.deepHashCode(col2));
        // unordered Collections (Set)
        col1 = new LinkedHashSet<>();
        fillCollection(col1);
        col2 = new HashSet<>();
        fillCollection(col2);
        assertHaveNoDifferences(col1, col2);
        Assertions.assertThat(DeepDifference.deepHashCode(col1)).isEqualTo(DeepDifference.deepHashCode(col2));
        col1 = new TreeSet<>();
        fillCollection(col1);
        col2 = new TreeSet<>();
        Collections.synchronizedSortedSet(((SortedSet<String>) (col2)));
        fillCollection(col2);
        assertHaveNoDifferences(col1, col2);
        Assertions.assertThat(DeepDifference.deepHashCode(col1)).isEqualTo(DeepDifference.deepHashCode(col2));
    }

    @Test
    public void testInequivalentCollections() {
        Collection<String> col1 = new TreeSet<>();
        fillCollection(col1);
        Collection<String> col2 = new HashSet<>();
        fillCollection(col2);
        assertHaveDifferences(col1, col2);
        Assertions.assertThat(DeepDifference.deepHashCode(col1)).isEqualTo(DeepDifference.deepHashCode(col2));
        col2 = new TreeSet<>();
        fillCollection(col2);
        col2.remove("lima");
        assertHaveDifferences(col1, col2);
        Assertions.assertThat(DeepDifference.deepHashCode(col1)).isNotEqualTo(DeepDifference.deepHashCode(col2));
        assertHaveDifferences(new HashMap<>(), new ArrayList<>());
        assertHaveDifferences(new ArrayList<>(), new HashMap<>());
    }

    @Test
    public void testArray() {
        Object[] a1 = new Object[]{ "alpha", "bravo", "charlie", "delta" };
        Object[] a2 = new Object[]{ "alpha", "bravo", "charlie", "delta" };
        assertHaveNoDifferences(a1, a2);
        Assertions.assertThat(DeepDifference.deepHashCode(a1)).isEqualTo(DeepDifference.deepHashCode(a2));
        a2[3] = "echo";
        assertHaveDifferences(a1, a2);
        Assertions.assertThat(DeepDifference.deepHashCode(a1)).isNotEqualTo(DeepDifference.deepHashCode(a2));
    }

    @Test
    public void testHasCustomMethod() {
        Assertions.assertThat(DeepDifference.hasCustomEquals(DeepDifference_Test.EmptyClass.class)).isFalse();
        Assertions.assertThat(DeepDifference.hasCustomHashCode(DeepDifference_Test.Class1.class)).isFalse();
        Assertions.assertThat(DeepDifference.hasCustomEquals(DeepDifference_Test.EmptyClassWithEquals.class)).isTrue();
        Assertions.assertThat(DeepDifference.hasCustomHashCode(DeepDifference_Test.EmptyClassWithEquals.class)).isTrue();
    }

    @Test
    public void shouldBeAbleToUseCustomComparatorForHashMap() {
        class ObjectWithMapField {
            Map<Integer, Boolean> map;
        }
        ObjectWithMapField a = new ObjectWithMapField();
        ObjectWithMapField b = new ObjectWithMapField();
        a.map = new HashMap<>();
        a.map.put(1, true);
        b.map = new HashMap<>();
        b.map.put(2, true);
        @SuppressWarnings("rawtypes")
        class AlwaysEqualMapComparator implements Comparator<Map> {
            @Override
            public int compare(Map o1, Map o2) {
                return 0;
            }
        }
        TypeComparators typeComparators = new TypeComparators();
        typeComparators.put(Map.class, new AlwaysEqualMapComparator());
        Assertions.assertThat(DeepDifference.determineDifferences(a, b, ObjectsBaseTest.noFieldComparators(), typeComparators)).isEmpty();
    }

    private static class EmptyClass {}

    private static class EmptyClassWithEquals {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof DeepDifference_Test.EmptyClassWithEquals;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

    private static class Class1 {
        @SuppressWarnings("unused")
        private boolean b;

        @SuppressWarnings("unused")
        private double d;

        @SuppressWarnings("unused")
        int i;

        public Class1() {
        }

        public Class1(boolean b, double d, int i) {
            this.b = b;
            this.d = d;
            this.i = i;
        }
    }

    private static class Class2 {
        @SuppressWarnings("unused")
        private Float f;

        @SuppressWarnings("unused")
        String s;

        @SuppressWarnings("unused")
        short ss;

        @SuppressWarnings("unused")
        DeepDifference_Test.Class1 c;

        public Class2(float f, String s, short ss, DeepDifference_Test.Class1 c) {
            this.f = f;
            this.s = s;
            this.ss = ss;
            this.c = c;
        }
    }

    private static class Wrapper {
        @SuppressWarnings("unused")
        private Object o;

        private Wrapper(Object o) {
            this.o = o;
        }
    }

    private static class SetWrapper {
        @SuppressWarnings("unused")
        private Set<?> set;

        private SetWrapper(Set<?> set) {
            this.set = set;
        }
    }
}

