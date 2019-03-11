/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * The CollectionUtilsJUnitTest class is a test suite of test cases testing the contract and
 * functionality of the CollectionUtils class.
 * <p/>
 *
 * @see org.apache.geode.internal.util.CollectionUtils
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since GemFire 7.0
 */
public class CollectionUtilsJUnitTest {
    @Test
    public void testAsList() {
        final Integer[] numbers = new Integer[]{ 0, 1, 2, 1, 0 };
        final List<Integer> numberList = CollectionUtils.asList(numbers);
        Assert.assertNotNull(numberList);
        Assert.assertFalse(numberList.isEmpty());
        Assert.assertEquals(numbers.length, numberList.size());
        Assert.assertTrue(numberList.containsAll(Arrays.asList(numbers)));
        Assert.assertEquals(new Integer(0), numberList.remove(0));
        Assert.assertEquals(((numbers.length) - 1), numberList.size());
    }

    @Test
    public void testAsSet() {
        final Integer[] numbers = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final Set<Integer> numberSet = CollectionUtils.asSet(numbers);
        Assert.assertNotNull(numberSet);
        Assert.assertFalse(numberSet.isEmpty());
        Assert.assertEquals(numbers.length, numberSet.size());
        Assert.assertTrue(numberSet.containsAll(Arrays.asList(numbers)));
        Assert.assertTrue(numberSet.remove(1));
        Assert.assertEquals(((numbers.length) - 1), numberSet.size());
    }

    @Test
    public void testAsSetWithNonUniqueElements() {
        final Integer[] numbers = new Integer[]{ 0, 1, 2, 1, 0 };
        final Set<Integer> numberSet = CollectionUtils.asSet(numbers);
        Assert.assertNotNull(numberSet);
        Assert.assertFalse(numberSet.isEmpty());
        Assert.assertEquals(3, numberSet.size());
        Assert.assertTrue(numberSet.containsAll(Arrays.asList(numbers)));
    }

    @Test
    public void testCreateProperties() {
        Properties properties = CollectionUtils.createProperties(Collections.singletonMap("one", "two"));
        Assert.assertNotNull(properties);
        Assert.assertFalse(properties.isEmpty());
        Assert.assertTrue(properties.containsKey("one"));
        Assert.assertEquals("two", properties.getProperty("one"));
    }

    @Test
    public void testCreateMultipleProperties() {
        Map<String, String> map = new HashMap<>(3);
        map.put("one", "A");
        map.put("two", "B");
        map.put("six", "C");
        Properties properties = CollectionUtils.createProperties(map);
        Assert.assertNotNull(properties);
        Assert.assertFalse(properties.isEmpty());
        Assert.assertEquals(map.size(), properties.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Assert.assertTrue(properties.containsKey(entry.getKey()));
            Assert.assertEquals(entry.getValue(), properties.get(entry.getKey()));
        }
    }

    @Test
    public void testCreateEmptyProperties() {
        Properties properties = CollectionUtils.createProperties(null);
        Assert.assertNotNull(properties);
        Assert.assertTrue(properties.isEmpty());
    }

    @Test
    public void testEmptyListWithNullList() {
        final List<Object> actualList = CollectionUtils.emptyList(null);
        Assert.assertNotNull(actualList);
        Assert.assertTrue(actualList.isEmpty());
    }

    @Test
    public void testEmptyListWithEmptyList() {
        final List<Object> expectedList = new ArrayList<>(0);
        Assert.assertNotNull(expectedList);
        Assert.assertTrue(expectedList.isEmpty());
        final List<Object> actualList = CollectionUtils.emptyList(expectedList);
        Assert.assertSame(expectedList, actualList);
    }

    @Test
    public void testEmptyListWithList() {
        final List<String> expectedList = Arrays.asList("aardvark", "baboon", "cat", "dog", "eel", "ferret");
        Assert.assertNotNull(expectedList);
        Assert.assertFalse(expectedList.isEmpty());
        final List<String> actualList = CollectionUtils.emptyList(expectedList);
        Assert.assertSame(expectedList, actualList);
    }

    @Test
    public void testEmptySetWithNullSet() {
        final Set<Object> actualSet = CollectionUtils.emptySet(null);
        Assert.assertNotNull(actualSet);
        Assert.assertTrue(actualSet.isEmpty());
    }

    @Test
    public void testEmptySetWithEmptySet() {
        final Set<Object> expectedSet = new HashSet<>(0);
        Assert.assertNotNull(expectedSet);
        Assert.assertTrue(expectedSet.isEmpty());
        final Set<Object> actualSet = CollectionUtils.emptySet(expectedSet);
        Assert.assertSame(expectedSet, actualSet);
    }

    @Test
    public void testEmptySetWithSet() {
        final Set<String> expectedSet = new HashSet<>(Arrays.asList("aardvark", "baboon", "cat", "dog", "ferret"));
        Assert.assertNotNull(expectedSet);
        Assert.assertFalse(expectedSet.isEmpty());
        final Set<String> actualSet = CollectionUtils.emptySet(expectedSet);
        Assert.assertSame(expectedSet, actualSet);
    }

    @Test
    public void testFindAll() {
        final List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 7, 8, 9);
        // accept all even numbers
        final List<Integer> matches = CollectionUtils.findAll(numbers, ( number) -> (number % 2) == 0);
        Assert.assertNotNull(matches);
        Assert.assertFalse(matches.isEmpty());
        Assert.assertTrue(matches.containsAll(Arrays.asList(0, 2, 4, 6, 8)));
    }

    @Test
    public void testFindAllWhenMultipleElementsMatch() {
        final List<Integer> numbers = Arrays.asList(0, 1, 2, 1, 4, 1, 6, 1, 7, 1, 9);
        // accept 1
        final List<Integer> matches = CollectionUtils.findAll(numbers, ( number) -> number == 1);
        Assert.assertNotNull(matches);
        Assert.assertEquals(5, matches.size());
        Assert.assertEquals(matches, Arrays.asList(1, 1, 1, 1, 1));
    }

    @Test
    public void testFindAllWhenNoElementsMatch() {
        final List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // accept negative numbers
        final List<Integer> matches = CollectionUtils.findAll(numbers, ( number) -> number < 0);
        Assert.assertNotNull(matches);
        Assert.assertTrue(matches.isEmpty());
    }

    @Test
    public void testFindBy() {
        final List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 7, 8, 9);
        // accept 2
        final Integer match = CollectionUtils.findBy(numbers, ( number) -> number == 2);
        Assert.assertNotNull(match);
        Assert.assertEquals(2, match.intValue());
    }

    @Test
    public void testFindByWhenMultipleElementsMatch() {
        final List<Integer> numbers = Arrays.asList(0, 1, 2, 1, 4, 1, 6, 1, 7, 1, 9);
        // accept 1
        final Integer match = CollectionUtils.findBy(numbers, ( number) -> number == 1);
        Assert.assertNotNull(match);
        Assert.assertEquals(1, match.intValue());
    }

    @Test
    public void testFindByWhenNoElementsMatch() {
        final List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 7, 8, 9);
        // accept 10
        final Integer match = CollectionUtils.findBy(numbers, ( number) -> number == 10);
        Assert.assertNull(match);
    }

    @Test
    public void testRemoveKeys() {
        final Map<Object, String> expectedMap = new HashMap<>(6);
        expectedMap.put("key1", "value");
        expectedMap.put("key2", "null");
        expectedMap.put("key3", "nil");
        expectedMap.put("key4", null);
        expectedMap.put("key5", "");
        expectedMap.put("key6", "  ");
        Assert.assertFalse(expectedMap.isEmpty());
        Assert.assertEquals(6, expectedMap.size());
        final Map<Object, String> actualMap = CollectionUtils.removeKeys(expectedMap, ( entry) -> StringUtils.isNotBlank(entry.getValue()));
        Assert.assertSame(expectedMap, actualMap);
        Assert.assertFalse(actualMap.isEmpty());
        Assert.assertEquals(3, actualMap.size());
        Assert.assertTrue(actualMap.keySet().containsAll(Arrays.asList("key1", "key2", "key3")));
    }

    @Test
    public void testRemoveKeysWithNullValues() {
        final Map<Object, Object> expectedMap = new HashMap<>(3);
        expectedMap.put("one", "test");
        expectedMap.put("two", null);
        expectedMap.put(null, "null");
        expectedMap.put("null", "nil");
        Assert.assertFalse(expectedMap.isEmpty());
        Assert.assertEquals(4, expectedMap.size());
        final Map<Object, Object> actualMap = CollectionUtils.removeKeysWithNullValues(expectedMap);
        Assert.assertSame(expectedMap, actualMap);
        Assert.assertEquals(3, expectedMap.size());
        Assert.assertEquals("null", expectedMap.get(null));
    }

    @Test
    public void testRemoveKeysWithNullValuesFromEmptyMap() {
        final Map<?, ?> expectedMap = Collections.emptyMap();
        Assert.assertNotNull(expectedMap);
        Assert.assertTrue(expectedMap.isEmpty());
        final Map<?, ?> actualMap = CollectionUtils.removeKeysWithNullValues(expectedMap);
        Assert.assertSame(expectedMap, actualMap);
        Assert.assertTrue(actualMap.isEmpty());
    }

    @Test
    public void testRemoveKeysWithNullValuesFromMapWithNoNullValues() {
        final Map<String, Object> map = new HashMap<>(5);
        map.put("one", "test");
        map.put("null", "null");
        map.put("two", "testing");
        map.put(null, "nil");
        map.put("three", "tested");
        Assert.assertEquals(5, map.size());
        CollectionUtils.removeKeysWithNullValues(map);
        Assert.assertEquals(5, map.size());
    }

    @Test
    public void testAddAllCollectionEnumerationWithList() {
        final ArrayList<String> list = new ArrayList<>(4);
        list.add("one");
        list.add("two");
        final Vector<String> v = new Vector<>();
        v.add("three");
        v.add("four");
        boolean modified = CollectionUtils.addAll(list, v.elements());
        Assert.assertTrue(modified);
        Assert.assertEquals(4, list.size());
        Assert.assertSame(v.get(0), list.get(2));
        Assert.assertSame(v.get(1), list.get(3));
    }

    @Test
    public void testAddAllCollectionEnumerationWithUnmodified() {
        final HashSet<String> set = new HashSet<>();
        set.add("one");
        set.add("two");
        final Vector<String> v = new Vector<>();
        v.add("one");
        boolean modified = CollectionUtils.addAll(set, v.elements());
        Assert.assertTrue((!modified));
        Assert.assertEquals(2, set.size());
    }

    @Test
    public void testAddAllCollectionEnumerationWithEmpty() {
        final ArrayList<String> list = new ArrayList<>(2);
        list.add("one");
        list.add("two");
        boolean modified = CollectionUtils.addAll(list, new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                throw new NoSuchElementException();
            }
        });
        Assert.assertTrue((!modified));
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testAddAllCollectionEnumerationWithNull() {
        final ArrayList<String> list = new ArrayList<>(2);
        list.add("one");
        list.add("two");
        boolean modified = CollectionUtils.addAll(list, null);
        Assert.assertTrue((!modified));
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testUnmodifiableIterable() {
        final ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        final Iterable<Integer> iterable = CollectionUtils.unmodifiableIterable(list);
        Assert.assertNotNull(iterable);
        final Iterator<Integer> iterator1 = iterable.iterator();
        Assert.assertNotNull(iterator1);
        Assert.assertTrue(iterator1.hasNext());
        Assert.assertSame(list.get(0), iterator1.next());
        Assert.assertSame(list.get(1), iterator1.next());
        Assert.assertTrue((!(iterator1.hasNext())));
        try {
            iterator1.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // ignore
        }
        list.add(2);
        try {
            iterator1.next();
            Assert.fail("Expected ConcurrentModificationException");
        } catch (ConcurrentModificationException e) {
            // ignore
        }
        final Iterator<Integer> iterator2 = iterable.iterator();
        Assert.assertNotNull(iterator2);
        Assert.assertNotSame(iterator1, iterator2);
        Assert.assertTrue(iterator2.hasNext());
        Assert.assertSame(list.get(0), iterator2.next());
        Assert.assertSame(list.get(1), iterator2.next());
        Assert.assertSame(list.get(2), iterator2.next());
        Assert.assertTrue((!(iterator2.hasNext())));
        try {
            iterator2.next();
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // ignore
        }
        try {
            iterator2.remove();
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // ignore
        }
    }
}

