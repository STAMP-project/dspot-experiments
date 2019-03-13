/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core;


import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


/**
 * Unit tests for {@link CollectionFactory}.
 *
 * @author Oliver Gierke
 * @author Sam Brannen
 * @since 4.1.4
 */
public class CollectionFactoryTests {
    /**
     * The test demonstrates that the generics-based API for
     * {@link CollectionFactory#createApproximateCollection(Object, int)}
     * is not type-safe.
     * <p>Specifically, the parameterized type {@code E} is not bound to
     * the type of elements contained in the {@code collection} argument
     * passed to {@code createApproximateCollection()}. Thus casting the
     * value returned by {@link EnumSet#copyOf(EnumSet)} to
     * {@code (Collection<E>)} cannot guarantee that the returned collection
     * actually contains elements of type {@code E}.
     */
    @Test
    public void createApproximateCollectionIsNotTypeSafeForEnumSet() {
        Collection<Integer> ints = createApproximateCollection(EnumSet.of(CollectionFactoryTests.Color.BLUE), 3);
        // Use a try-catch block to ensure that the exception is thrown as a result of the
        // next line and not as a result of the previous line.
        try {
            // Note that ints is of type Collection<Integer>, but the collection returned
            // by createApproximateCollection() is of type Collection<Color>. Thus, 42
            // cannot be cast to a Color.
            ints.add(42);
            Assert.fail("Should have thrown a ClassCastException");
        } catch (ClassCastException e) {
            /* expected */
        }
    }

    @Test
    public void createCollectionIsNotTypeSafeForEnumSet() {
        Collection<Integer> ints = createCollection(EnumSet.class, CollectionFactoryTests.Color.class, 3);
        // Use a try-catch block to ensure that the exception is thrown as a result of the
        // next line and not as a result of the previous line.
        try {
            // Note that ints is of type Collection<Integer>, but the collection returned
            // by createCollection() is of type Collection<Color>. Thus, 42 cannot be cast
            // to a Color.
            ints.add(42);
            Assert.fail("Should have thrown a ClassCastException");
        } catch (ClassCastException e) {
            /* expected */
        }
    }

    /**
     * The test demonstrates that the generics-based API for
     * {@link CollectionFactory#createApproximateMap(Object, int)}
     * is not type-safe.
     * <p>The reasoning is similar that described in
     * {@link #createApproximateCollectionIsNotTypeSafeForEnumSet}.
     */
    @Test
    public void createApproximateMapIsNotTypeSafeForEnumMap() {
        EnumMap<CollectionFactoryTests.Color, Integer> enumMap = new EnumMap<>(CollectionFactoryTests.Color.class);
        enumMap.put(CollectionFactoryTests.Color.RED, 1);
        enumMap.put(CollectionFactoryTests.Color.BLUE, 2);
        Map<String, Integer> map = createApproximateMap(enumMap, 3);
        // Use a try-catch block to ensure that the exception is thrown as a result of the
        // next line and not as a result of the previous line.
        try {
            // Note that the 'map' key must be of type String, but the keys in the map
            // returned by createApproximateMap() are of type Color. Thus "foo" cannot be
            // cast to a Color.
            map.put("foo", 1);
            Assert.fail("Should have thrown a ClassCastException");
        } catch (ClassCastException e) {
            /* expected */
        }
    }

    @Test
    public void createMapIsNotTypeSafeForEnumMap() {
        Map<String, Integer> map = createMap(EnumMap.class, CollectionFactoryTests.Color.class, 3);
        // Use a try-catch block to ensure that the exception is thrown as a result of the
        // next line and not as a result of the previous line.
        try {
            // Note that the 'map' key must be of type String, but the keys in the map
            // returned by createMap() are of type Color. Thus "foo" cannot be cast to a
            // Color.
            map.put("foo", 1);
            Assert.fail("Should have thrown a ClassCastException");
        } catch (ClassCastException e) {
            /* expected */
        }
    }

    @Test
    public void createMapIsNotTypeSafeForLinkedMultiValueMap() {
        Map<String, Integer> map = createMap(MultiValueMap.class, null, 3);
        // Use a try-catch block to ensure that the exception is thrown as a result of the
        // next line and not as a result of the previous line.
        try {
            // Note: 'map' values must be of type Integer, but the values in the map
            // returned by createMap() are of type java.util.List. Thus 1 cannot be
            // cast to a List.
            map.put("foo", 1);
            Assert.fail("Should have thrown a ClassCastException");
        } catch (ClassCastException e) {
            /* expected */
        }
    }

    @Test
    public void createApproximateCollectionFromEmptyHashSet() {
        Collection<String> set = createApproximateCollection(new HashSet<String>(), 2);
        Assert.assertThat(set, is(empty()));
    }

    @Test
    public void createApproximateCollectionFromNonEmptyHashSet() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("foo");
        Collection<String> set = createApproximateCollection(hashSet, 2);
        Assert.assertThat(set, is(empty()));
    }

    @Test
    public void createApproximateCollectionFromEmptyEnumSet() {
        Collection<CollectionFactoryTests.Color> colors = createApproximateCollection(EnumSet.noneOf(CollectionFactoryTests.Color.class), 2);
        Assert.assertThat(colors, is(empty()));
    }

    @Test
    public void createApproximateCollectionFromNonEmptyEnumSet() {
        Collection<CollectionFactoryTests.Color> colors = createApproximateCollection(EnumSet.of(CollectionFactoryTests.Color.BLUE), 2);
        Assert.assertThat(colors, is(empty()));
    }

    @Test
    public void createApproximateMapFromEmptyHashMap() {
        Map<String, String> map = createApproximateMap(new HashMap<String, String>(), 2);
        Assert.assertThat(map.size(), is(0));
    }

    @Test
    public void createApproximateMapFromNonEmptyHashMap() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("foo", "bar");
        Map<String, String> map = createApproximateMap(hashMap, 2);
        Assert.assertThat(map.size(), is(0));
    }

    @Test
    public void createApproximateMapFromEmptyEnumMap() {
        Map<CollectionFactoryTests.Color, String> colors = createApproximateMap(new EnumMap<CollectionFactoryTests.Color, String>(CollectionFactoryTests.Color.class), 2);
        Assert.assertThat(colors.size(), is(0));
    }

    @Test
    public void createApproximateMapFromNonEmptyEnumMap() {
        EnumMap<CollectionFactoryTests.Color, String> enumMap = new EnumMap<>(CollectionFactoryTests.Color.class);
        enumMap.put(CollectionFactoryTests.Color.BLUE, "blue");
        Map<CollectionFactoryTests.Color, String> colors = createApproximateMap(enumMap, 2);
        Assert.assertThat(colors.size(), is(0));
    }

    @Test
    public void createsCollectionsCorrectly() {
        // interfaces
        Assert.assertThat(createCollection(List.class, 0), is(instanceOf(ArrayList.class)));
        Assert.assertThat(createCollection(Set.class, 0), is(instanceOf(LinkedHashSet.class)));
        Assert.assertThat(createCollection(Collection.class, 0), is(instanceOf(LinkedHashSet.class)));
        Assert.assertThat(createCollection(SortedSet.class, 0), is(instanceOf(TreeSet.class)));
        Assert.assertThat(createCollection(NavigableSet.class, 0), is(instanceOf(TreeSet.class)));
        Assert.assertThat(createCollection(List.class, String.class, 0), is(instanceOf(ArrayList.class)));
        Assert.assertThat(createCollection(Set.class, String.class, 0), is(instanceOf(LinkedHashSet.class)));
        Assert.assertThat(createCollection(Collection.class, String.class, 0), is(instanceOf(LinkedHashSet.class)));
        Assert.assertThat(createCollection(SortedSet.class, String.class, 0), is(instanceOf(TreeSet.class)));
        Assert.assertThat(createCollection(NavigableSet.class, String.class, 0), is(instanceOf(TreeSet.class)));
        // concrete types
        Assert.assertThat(createCollection(HashSet.class, 0), is(instanceOf(HashSet.class)));
        Assert.assertThat(createCollection(HashSet.class, String.class, 0), is(instanceOf(HashSet.class)));
    }

    @Test
    public void createsEnumSet() {
        Assert.assertThat(createCollection(EnumSet.class, CollectionFactoryTests.Color.class, 0), is(instanceOf(EnumSet.class)));
    }

    // SPR-17619
    @Test
    public void createsEnumSetSubclass() {
        EnumSet<CollectionFactoryTests.Color> enumSet = EnumSet.noneOf(CollectionFactoryTests.Color.class);
        Assert.assertThat(createCollection(enumSet.getClass(), CollectionFactoryTests.Color.class, 0), is(instanceOf(enumSet.getClass())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidElementTypeForEnumSet() {
        createCollection(EnumSet.class, Object.class, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullElementTypeForEnumSet() {
        createCollection(EnumSet.class, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullCollectionType() {
        createCollection(null, Object.class, 0);
    }

    @Test
    public void createsMapsCorrectly() {
        // interfaces
        Assert.assertThat(createMap(Map.class, 0), is(instanceOf(LinkedHashMap.class)));
        Assert.assertThat(createMap(SortedMap.class, 0), is(instanceOf(TreeMap.class)));
        Assert.assertThat(createMap(NavigableMap.class, 0), is(instanceOf(TreeMap.class)));
        Assert.assertThat(createMap(MultiValueMap.class, 0), is(instanceOf(LinkedMultiValueMap.class)));
        Assert.assertThat(createMap(Map.class, String.class, 0), is(instanceOf(LinkedHashMap.class)));
        Assert.assertThat(createMap(SortedMap.class, String.class, 0), is(instanceOf(TreeMap.class)));
        Assert.assertThat(createMap(NavigableMap.class, String.class, 0), is(instanceOf(TreeMap.class)));
        Assert.assertThat(createMap(MultiValueMap.class, String.class, 0), is(instanceOf(LinkedMultiValueMap.class)));
        // concrete types
        Assert.assertThat(createMap(HashMap.class, 0), is(instanceOf(HashMap.class)));
        Assert.assertThat(createMap(HashMap.class, String.class, 0), is(instanceOf(HashMap.class)));
    }

    @Test
    public void createsEnumMap() {
        Assert.assertThat(createMap(EnumMap.class, CollectionFactoryTests.Color.class, 0), is(instanceOf(EnumMap.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidKeyTypeForEnumMap() {
        createMap(EnumMap.class, Object.class, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullKeyTypeForEnumMap() {
        createMap(EnumMap.class, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullMapType() {
        createMap(null, Object.class, 0);
    }

    enum Color {

        RED,
        BLUE;}
}

