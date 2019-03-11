/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import com.google.j2objc.util.ReflectionUtil;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;
import tests.support.Support_MapTest2;
import tests.support.Support_UnmodifiableCollectionTest;


public class TreeMapTest extends TestCase {
    public static class ReversedComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return -(((Comparable) (o1)).compareTo(o2));
        }

        public boolean equals(Object o1, Object o2) {
            return (((Comparable) (o1)).compareTo(o2)) == 0;
        }
    }

    // Regression for Harmony-1026
    public static class MockComparator<T extends Comparable<T>> implements Serializable , Comparator<T> {
        public int compare(T o1, T o2) {
            if (o1 == o2) {
                return 0;
            }
            if ((null == o1) || (null == o2)) {
                return -1;
            }
            T c1 = o1;
            T c2 = o2;
            return c1.compareTo(c2);
        }
    }

    // Regression for Harmony-1161
    class MockComparatorNullTolerable implements Comparator<String> {
        public int compare(String o1, String o2) {
            if (o1 == o2) {
                return 0;
            }
            if (null == o1) {
                return -1;
            }
            if (null == o2) {
                // comparator should be symmetric
                return 1;
            }
            return o1.compareTo(o2);
        }
    }

    TreeMap tm;

    Object[] objArray = new Object[1000];

    /**
     * java.util.TreeMap#TreeMap()
     */
    public void test_Constructor() {
        // Test for method java.util.TreeMap()
        new Support_MapTest2(new TreeMap()).runTest();
        TestCase.assertTrue("New treeMap non-empty", new TreeMap().isEmpty());
    }

    /**
     * java.util.TreeMap#TreeMap(java.util.Comparator)
     */
    public void test_ConstructorLjava_util_Comparator() {
        // Test for method java.util.TreeMap(java.util.Comparator)
        Comparator comp = new TreeMapTest.ReversedComparator();
        TreeMap reversedTreeMap = new TreeMap(comp);
        TestCase.assertTrue("TreeMap answered incorrect comparator", ((reversedTreeMap.comparator()) == comp));
        reversedTreeMap.put(new Integer(1).toString(), new Integer(1));
        reversedTreeMap.put(new Integer(2).toString(), new Integer(2));
        TestCase.assertTrue("TreeMap does not use comparator (firstKey was incorrect)", reversedTreeMap.firstKey().equals(new Integer(2).toString()));
        TestCase.assertTrue("TreeMap does not use comparator (lastKey was incorrect)", reversedTreeMap.lastKey().equals(new Integer(1).toString()));
    }

    /**
     * java.util.TreeMap#TreeMap(java.util.Map)
     */
    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.TreeMap(java.util.Map)
        TreeMap myTreeMap = new TreeMap(new HashMap(tm));
        TestCase.assertTrue("Map is incorrect size", ((myTreeMap.size()) == (objArray.length)));
        for (Object element : objArray) {
            TestCase.assertTrue("Map has incorrect mappings", myTreeMap.get(element.toString()).equals(element));
        }
    }

    /**
     * java.util.TreeMap#TreeMap(java.util.SortedMap)
     */
    public void test_ConstructorLjava_util_SortedMap() {
        // Test for method java.util.TreeMap(java.util.SortedMap)
        Comparator comp = new TreeMapTest.ReversedComparator();
        TreeMap reversedTreeMap = new TreeMap(comp);
        reversedTreeMap.put(new Integer(1).toString(), new Integer(1));
        reversedTreeMap.put(new Integer(2).toString(), new Integer(2));
        TreeMap anotherTreeMap = new TreeMap(reversedTreeMap);
        TestCase.assertTrue("New tree map does not answer correct comparator", ((anotherTreeMap.comparator()) == comp));
        TestCase.assertTrue("TreeMap does not use comparator (firstKey was incorrect)", anotherTreeMap.firstKey().equals(new Integer(2).toString()));
        TestCase.assertTrue("TreeMap does not use comparator (lastKey was incorrect)", anotherTreeMap.lastKey().equals(new Integer(1).toString()));
    }

    /**
     * java.util.TreeMap#clear()
     */
    public void test_clear() {
        // Test for method void java.util.TreeMap.clear()
        tm.clear();
        TestCase.assertEquals("Cleared map returned non-zero size", 0, tm.size());
    }

    /**
     * java.util.TreeMap#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.TreeMap.clone()
        TreeMap clonedMap = ((TreeMap) (tm.clone()));
        TestCase.assertTrue("Cloned map does not equal the original map", clonedMap.equals(tm));
        TestCase.assertTrue("Cloned map is the same reference as the original map", (clonedMap != (tm)));
        for (Object element : objArray) {
            TestCase.assertTrue("Cloned map contains incorrect elements", ((clonedMap.get(element.toString())) == (tm.get(element.toString()))));
        }
        TreeMap map = new TreeMap();
        map.put("key", "value");
        // get the keySet() and values() on the original Map
        Set keys = map.keySet();
        Collection values = map.values();
        TestCase.assertEquals("values() does not work", "value", values.iterator().next());
        TestCase.assertEquals("keySet() does not work", "key", keys.iterator().next());
        AbstractMap map2 = ((AbstractMap) (map.clone()));
        map2.put("key", "value2");
        Collection values2 = map2.values();
        TestCase.assertTrue("values() is identical", (values2 != values));
        // values() and keySet() on the cloned() map should be different
        TestCase.assertEquals("values() was not cloned", "value2", values2.iterator().next());
        map2.clear();
        map2.put("key2", "value3");
        Set key2 = map2.keySet();
        TestCase.assertTrue("keySet() is identical", (key2 != keys));
        TestCase.assertEquals("keySet() was not cloned", "key2", key2.iterator().next());
    }

    /**
     * java.util.TreeMap#comparator()
     */
    public void test_comparator() {
        // Test for method java.util.Comparator java.util.TreeMap.comparator()\
        Comparator comp = new TreeMapTest.ReversedComparator();
        TreeMap reversedTreeMap = new TreeMap(comp);
        TestCase.assertTrue("TreeMap answered incorrect comparator", ((reversedTreeMap.comparator()) == comp));
        reversedTreeMap.put(new Integer(1).toString(), new Integer(1));
        reversedTreeMap.put(new Integer(2).toString(), new Integer(2));
        TestCase.assertTrue("TreeMap does not use comparator (firstKey was incorrect)", reversedTreeMap.firstKey().equals(new Integer(2).toString()));
        TestCase.assertTrue("TreeMap does not use comparator (lastKey was incorrect)", reversedTreeMap.lastKey().equals(new Integer(1).toString()));
    }

    /**
     * java.util.TreeMap#containsKey(java.lang.Object)
     */
    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.TreeMap.containsKey(java.lang.Object)
        TestCase.assertTrue("Returned false for valid key", tm.containsKey("95"));
        TestCase.assertTrue("Returned true for invalid key", (!(tm.containsKey("XXXXX"))));
    }

    /**
     * java.util.TreeMap#containsValue(java.lang.Object)
     */
    public void test_containsValueLjava_lang_Object() {
        // Test for method boolean
        // java.util.TreeMap.containsValue(java.lang.Object)
        TestCase.assertTrue("Returned false for valid value", tm.containsValue(objArray[986]));
        TestCase.assertTrue("Returned true for invalid value", (!(tm.containsValue(new Object()))));
    }

    /**
     * java.util.TreeMap#entrySet()
     */
    public void test_entrySet() {
        // Test for method java.util.Set java.util.TreeMap.entrySet()
        Set anEntrySet = tm.entrySet();
        Iterator entrySetIterator = anEntrySet.iterator();
        TestCase.assertTrue("EntrySet is incorrect size", ((anEntrySet.size()) == (objArray.length)));
        Map.Entry entry;
        while (entrySetIterator.hasNext()) {
            entry = ((Map.Entry) (entrySetIterator.next()));
            TestCase.assertTrue("EntrySet does not contain correct mappings", ((tm.get(entry.getKey())) == (entry.getValue())));
        } 
    }

    /**
     * java.util.TreeMap#firstKey()
     */
    public void test_firstKey() {
        // Test for method java.lang.Object java.util.TreeMap.firstKey()
        TestCase.assertEquals("Returned incorrect first key", "0", tm.firstKey());
    }

    /**
     * java.util.TreeMap#get(java.lang.Object)
     */
    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.TreeMap.get(java.lang.Object)
        Object o = new Object();
        tm.put("Hello", o);
        TestCase.assertTrue("Failed to get mapping", ((tm.get("Hello")) == o));
        // Test for the same key & same value
        tm = new TreeMap();
        Object o2 = new Object();
        Integer key1 = 1;
        Integer key2 = 2;
        TestCase.assertNull(tm.put(key1, o));
        TestCase.assertNull(tm.put(key2, o));
        TestCase.assertEquals(2, tm.values().size());
        TestCase.assertEquals(2, tm.keySet().size());
        TestCase.assertSame(tm.get(key1), tm.get(key2));
        TestCase.assertSame(o, tm.put(key1, o2));
        TestCase.assertSame(o2, tm.get(key1));
    }

    /**
     * java.util.TreeMap#headMap(java.lang.Object)
     */
    public void test_headMapLjava_lang_Object() {
        // Test for method java.util.SortedMap
        // java.util.TreeMap.headMap(java.lang.Object)
        Map head = tm.headMap("100");
        TestCase.assertEquals("Returned map of incorrect size", 3, head.size());
        TestCase.assertTrue("Returned incorrect elements", (((head.containsKey("0")) && (head.containsValue(new Integer("1")))) && (head.containsKey("10"))));
        // Regression for Harmony-1026
        TreeMap<Integer, Double> map = new TreeMap<Integer, Double>(new TreeMapTest.MockComparator());
        map.put(1, 2.1);
        map.put(2, 3.1);
        map.put(3, 4.5);
        map.put(7, 21.3);
        map.put(null, null);
        SortedMap<Integer, Double> smap = map.headMap(null);
        TestCase.assertEquals(0, smap.size());
        Set<Integer> keySet = smap.keySet();
        TestCase.assertEquals(0, keySet.size());
        Set<Map.Entry<Integer, Double>> entrySet = smap.entrySet();
        TestCase.assertEquals(0, entrySet.size());
        Collection<Double> valueCollection = smap.values();
        TestCase.assertEquals(0, valueCollection.size());
        // Regression for Harmony-1066
        TestCase.assertTrue((head instanceof Serializable));
        TreeMap<String, String> treemap = new TreeMap();
        SortedMap<String, String> headMap = treemap.headMap("100");
        headMap.headMap("100");
        SortedMap<Integer, Integer> intMap;
        SortedMap<Integer, Integer> sub;
        int size = 16;
        intMap = new TreeMap<Integer, Integer>();
        for (int i = 0; i < size; i++) {
            intMap.put(i, i);
        }
        sub = intMap.headMap((-1));
        TestCase.assertEquals("size should be zero", sub.size(), 0);
        TestCase.assertTrue("submap should be empty", sub.isEmpty());
        try {
            sub.firstKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
        TreeMap t = new TreeMap();
        try {
            SortedMap th = t.headMap(null);
            TestCase.fail("Should throw a NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            sub.lastKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
        size = 256;
        intMap = new TreeMap<Integer, Integer>();
        for (int i = 0; i < size; i++) {
            intMap.put(i, i);
        }
        sub = intMap.headMap((-1));
        TestCase.assertEquals("size should be zero", sub.size(), 0);
        TestCase.assertTrue("submap should be empty", sub.isEmpty());
        try {
            sub.firstKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
        try {
            sub.lastKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * java.util.TreeMap#keySet()
     */
    public void test_keySet() {
        // Test for method java.util.Set java.util.TreeMap.keySet()
        Set ks = tm.keySet();
        TestCase.assertTrue("Returned set of incorrect size", ((ks.size()) == (objArray.length)));
        for (int i = 0; i < (tm.size()); i++) {
            TestCase.assertTrue("Returned set is missing keys", ks.contains(new Integer(i).toString()));
        }
    }

    /**
     * java.util.TreeMap#lastKey()
     */
    public void test_lastKey() {
        // Test for method java.lang.Object java.util.TreeMap.lastKey()
        TestCase.assertTrue("Returned incorrect last key", tm.lastKey().equals(objArray[((objArray.length) - 1)].toString()));
    }

    public void test_lastKey_after_subMap() {
        TreeMap<String, String> tm = new TreeMap<String, String>();
        tm.put("001", "VAL001");
        tm.put("003", "VAL003");
        tm.put("002", "VAL002");
        SortedMap<String, String> sm = tm;
        String firstKey = ((String) (sm.firstKey()));
        String lastKey = "";
        for (int i = 1; i <= (tm.size()); i++) {
            try {
                lastKey = ((String) (sm.lastKey()));
            } catch (NoSuchElementException excep) {
                TestCase.fail("NoSuchElementException thrown when there are elements in the map");
            }
            sm = sm.subMap(firstKey, lastKey);
        }
    }

    /**
     * java.util.TreeMap#put(java.lang.Object, java.lang.Object)
     */
    public void test_remove_throwsWhenNotComparable() {
        // Test for method java.lang.Object
        // java.util.TreeMap.put(java.lang.Object, java.lang.Object)
        Object o = new Object();
        tm = new TreeMap();
        try {
            tm.remove(o);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }

    /**
     * java.util.TreeMap#putAll(java.util.Map)
     */
    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.TreeMap.putAll(java.util.Map)
        TreeMap x = new TreeMap();
        x.putAll(tm);
        TestCase.assertTrue("Map incorrect size after put", ((x.size()) == (tm.size())));
        for (Object element : objArray) {
            TestCase.assertTrue("Failed to put all elements", x.get(element.toString()).equals(element));
        }
    }

    /**
     * java.util.TreeMap#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.TreeMap.remove(java.lang.Object)
        tm.remove("990");
        TestCase.assertTrue("Failed to remove mapping", (!(tm.containsKey("990"))));
    }

    /**
     * java.util.TreeMap#size()
     */
    public void test_size() {
        // Test for method int java.util.TreeMap.size()
        TestCase.assertEquals("Returned incorrect size", 1000, tm.size());
        TestCase.assertEquals("Returned incorrect size", 447, tm.headMap("500").size());
        TestCase.assertEquals("Returned incorrect size", 1000, tm.headMap("null").size());
        TestCase.assertEquals("Returned incorrect size", 0, tm.headMap("").size());
        TestCase.assertEquals("Returned incorrect size", 448, tm.headMap("500a").size());
        TestCase.assertEquals("Returned incorrect size", 553, tm.tailMap("500").size());
        TestCase.assertEquals("Returned incorrect size", 0, tm.tailMap("null").size());
        TestCase.assertEquals("Returned incorrect size", 1000, tm.tailMap("").size());
        TestCase.assertEquals("Returned incorrect size", 552, tm.tailMap("500a").size());
        TestCase.assertEquals("Returned incorrect size", 111, tm.subMap("500", "600").size());
        try {
            tm.subMap("null", "600");
            TestCase.fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        TestCase.assertEquals("Returned incorrect size", 1000, tm.subMap("", "null").size());
    }

    /**
     * java.util.TreeMap#subMap(java.lang.Object, java.lang.Object)
     */
    public void test_subMapLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.util.SortedMap
        // java.util.TreeMap.subMap(java.lang.Object, java.lang.Object)
        SortedMap subMap = tm.subMap(objArray[100].toString(), objArray[109].toString());
        TestCase.assertEquals("subMap is of incorrect size", 9, subMap.size());
        for (int counter = 100; counter < 109; counter++) {
            TestCase.assertTrue("SubMap contains incorrect elements", subMap.get(objArray[counter].toString()).equals(objArray[counter]));
        }
        try {
            tm.subMap(objArray[9].toString(), objArray[1].toString());
            TestCase.fail("end key less than start key should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        // Regression for Harmony-1161
        TreeMap<String, String> treeMapWithNull = new TreeMap<String, String>(new TreeMapTest.MockComparatorNullTolerable());
        treeMapWithNull.put("key1", "value1");// $NON-NLS-1$ //$NON-NLS-2$

        treeMapWithNull.put(null, "value2");// $NON-NLS-1$

        SortedMap<String, String> subMapWithNull = treeMapWithNull.subMap(null, "key1");// $NON-NLS-1$

        TestCase.assertEquals("Size of subMap should be 1:", 1, subMapWithNull.size());// $NON-NLS-1$

        // Regression test for typo in lastKey method
        SortedMap<String, String> map = new TreeMap<String, String>();
        map.put("1", "one");// $NON-NLS-1$ //$NON-NLS-2$

        map.put("2", "two");// $NON-NLS-1$ //$NON-NLS-2$

        map.put("3", "three");// $NON-NLS-1$ //$NON-NLS-2$

        TestCase.assertEquals("3", map.lastKey());
        SortedMap<String, String> sub = map.subMap("1", "3");// $NON-NLS-1$ //$NON-NLS-2$

        TestCase.assertEquals("2", sub.lastKey());// $NON-NLS-1$

        // NOTE: The contract of this method allows us to throw either
        // an NPE or a class cast exception.
        TreeMap t = new TreeMap();
        try {
            t.subMap(null, new Object());
            TestCase.fail("Should throw a ClassCastException");
        } catch (ClassCastException cce) {
            // expected
        } catch (NullPointerException npe) {
            // expected
        }
    }

    /**
     * java.util.TreeMap#subMap(java.lang.Object, java.lang.Object)
     */
    public void test_subMap_Iterator() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        String[] keys = new String[]{ "1", "2", "3" };
        String[] values = new String[]{ "one", "two", "three" };
        for (int i = 0; i < (keys.length); i++) {
            map.put(keys[i], values[i]);
        }
        TestCase.assertEquals(3, map.size());
        Map subMap = map.subMap("", "test");
        TestCase.assertEquals(3, subMap.size());
        Set entrySet = subMap.entrySet();
        Iterator iter = entrySet.iterator();
        int size = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = ((Map.Entry<String, String>) (iter.next()));
            TestCase.assertTrue(map.containsKey(entry.getKey()));
            TestCase.assertTrue(map.containsValue(entry.getValue()));
            size++;
        } 
        TestCase.assertEquals(map.size(), size);
        Set<String> keySet = subMap.keySet();
        iter = keySet.iterator();
        size = 0;
        while (iter.hasNext()) {
            String key = ((String) (iter.next()));
            TestCase.assertTrue(map.containsKey(key));
            size++;
        } 
        TestCase.assertEquals(map.size(), size);
    }

    /**
     * java.util.TreeMap#tailMap(java.lang.Object)
     */
    public void test_tailMapLjava_lang_Object() {
        // Test for method java.util.SortedMap
        // java.util.TreeMap.tailMap(java.lang.Object)
        Map tail = tm.tailMap(objArray[900].toString());
        TestCase.assertTrue(("Returned map of incorrect size : " + (tail.size())), ((tail.size()) == (((objArray.length) - 900) + 9)));
        for (int i = 900; i < (objArray.length); i++) {
            TestCase.assertTrue("Map contains incorrect entries", tail.containsValue(objArray[i]));
        }
        // Regression for Harmony-1066
        TestCase.assertTrue((tail instanceof Serializable));
        SortedMap<Integer, Integer> intMap;
        SortedMap<Integer, Integer> sub;
        int size = 16;
        intMap = new TreeMap<Integer, Integer>();
        for (int i = 0; i < size; i++) {
            intMap.put(i, i);
        }
        sub = intMap.tailMap(size);
        TestCase.assertEquals("size should be zero", sub.size(), 0);
        TestCase.assertTrue("submap should be empty", sub.isEmpty());
        try {
            sub.firstKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
        TreeMap t = new TreeMap();
        try {
            SortedMap th = t.tailMap(null);
            TestCase.fail("Should throw a NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }
        try {
            sub.lastKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
        size = 256;
        intMap = new TreeMap<Integer, Integer>();
        for (int i = 0; i < size; i++) {
            intMap.put(i, i);
        }
        sub = intMap.tailMap(size);
        TestCase.assertEquals("size should be zero", sub.size(), 0);
        TestCase.assertTrue("submap should be empty", sub.isEmpty());
        try {
            sub.firstKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
        try {
            sub.lastKey();
            TestCase.fail("java.util.NoSuchElementException should be thrown");
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * java.util.TreeMap#values()
     */
    public void test_values() {
        // Test for method java.util.Collection java.util.TreeMap.values()
        Collection vals = tm.values();
        vals.iterator();
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == (objArray.length)));
        for (Object element : objArray) {
            TestCase.assertTrue("Collection contains incorrect elements", vals.contains(element));
        }
        TestCase.assertEquals(1000, vals.size());
        int j = 0;
        for (Iterator iter = vals.iterator(); iter.hasNext();) {
            Object element = ((Object) (iter.next()));
            j++;
        }
        TestCase.assertEquals(1000, j);
        vals = tm.descendingMap().values();
        vals.iterator();
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == (objArray.length)));
        for (Object element : objArray) {
            TestCase.assertTrue("Collection contains incorrect elements", vals.contains(element));
        }
        TestCase.assertEquals(1000, vals.size());
        j = 0;
        for (Iterator iter = vals.iterator(); iter.hasNext();) {
            Object element = ((Object) (iter.next()));
            j++;
        }
        TestCase.assertEquals(1000, j);
        TreeMap myTreeMap = new TreeMap();
        for (int i = 0; i < 100; i++) {
            myTreeMap.put(objArray[i], objArray[i]);
        }
        Collection values = myTreeMap.values();
        new Support_UnmodifiableCollectionTest("Test Returned Collection From TreeMap.values()", values).runTest();
        values.remove(new Integer(0));
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myTreeMap.containsValue(new Integer(0)))));
        TestCase.assertEquals(99, values.size());
        j = 0;
        for (Iterator iter = values.iterator(); iter.hasNext();) {
            Object element = ((Object) (iter.next()));
            j++;
        }
        TestCase.assertEquals(99, j);
    }

    /**
     * java.util.TreeMap the values() method in sub maps
     */
    public void test_subMap_values_size() {
        TreeMap myTreeMap = new TreeMap();
        for (int i = 0; i < 1000; i++) {
            myTreeMap.put(i, objArray[i]);
        }
        // Test for method values() in subMaps
        Collection vals = myTreeMap.subMap(200, 400).values();
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == 200));
        for (int i = 200; i < 400; i++) {
            TestCase.assertTrue(("Collection contains incorrect elements" + i), vals.contains(objArray[i]));
        }
        TestCase.assertEquals(200, vals.toArray().length);
        vals.remove(objArray[300]);
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myTreeMap.containsValue(objArray[300]))));
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == 199));
        TestCase.assertEquals(199, vals.toArray().length);
        myTreeMap.put(300, objArray[300]);
        // Test for method values() in subMaps
        vals = myTreeMap.headMap(400).values();
        TestCase.assertEquals("Returned collection of incorrect size", vals.size(), 400);
        for (int i = 0; i < 400; i++) {
            TestCase.assertTrue(("Collection contains incorrect elements " + i), vals.contains(objArray[i]));
        }
        TestCase.assertEquals(400, vals.toArray().length);
        vals.remove(objArray[300]);
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myTreeMap.containsValue(objArray[300]))));
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == 399));
        TestCase.assertEquals(399, vals.toArray().length);
        myTreeMap.put(300, objArray[300]);
        // Test for method values() in subMaps
        vals = myTreeMap.tailMap(400).values();
        TestCase.assertEquals("Returned collection of incorrect size", vals.size(), 600);
        for (int i = 400; i < 1000; i++) {
            TestCase.assertTrue(("Collection contains incorrect elements " + i), vals.contains(objArray[i]));
        }
        TestCase.assertEquals(600, vals.toArray().length);
        vals.remove(objArray[600]);
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myTreeMap.containsValue(objArray[600]))));
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == 599));
        TestCase.assertEquals(599, vals.toArray().length);
        myTreeMap.put(600, objArray[600]);
        // Test for method values() in subMaps
        vals = myTreeMap.descendingMap().headMap(400).values();
        TestCase.assertEquals("Returned collection of incorrect size", vals.size(), 599);
        for (int i = 401; i < 1000; i++) {
            TestCase.assertTrue(("Collection contains incorrect elements " + i), vals.contains(objArray[i]));
        }
        TestCase.assertEquals(599, vals.toArray().length);
        vals.remove(objArray[600]);
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myTreeMap.containsValue(objArray[600]))));
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == 598));
        TestCase.assertEquals(598, vals.toArray().length);
        myTreeMap.put(600, objArray[600]);
        // Test for method values() in subMaps
        vals = myTreeMap.descendingMap().tailMap(400).values();
        TestCase.assertEquals("Returned collection of incorrect size", vals.size(), 401);
        for (int i = 0; i <= 400; i++) {
            TestCase.assertTrue(("Collection contains incorrect elements " + i), vals.contains(objArray[i]));
        }
        TestCase.assertEquals(401, vals.toArray().length);
        vals.remove(objArray[300]);
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myTreeMap.containsValue(objArray[300]))));
        TestCase.assertTrue("Returned collection of incorrect size", ((vals.size()) == 400));
        TestCase.assertEquals(400, vals.toArray().length);
    }

    /**
     * java.util.TreeMap#subMap()
     */
    public void test_subMap_Iterator2() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        String[] keys = new String[]{ "1", "2", "3" };
        String[] values = new String[]{ "one", "two", "three" };
        for (int i = 0; i < (keys.length); i++) {
            map.put(keys[i], values[i]);
        }
        TestCase.assertEquals(3, map.size());
        Map subMap = map.subMap("", "test");
        TestCase.assertEquals(3, subMap.size());
        Set entrySet = subMap.entrySet();
        Iterator iter = entrySet.iterator();
        int size = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = ((Map.Entry<String, String>) (iter.next()));
            TestCase.assertTrue(map.containsKey(entry.getKey()));
            TestCase.assertTrue(map.containsValue(entry.getValue()));
            size++;
        } 
        TestCase.assertEquals(map.size(), size);
        Set<String> keySet = subMap.keySet();
        iter = keySet.iterator();
        size = 0;
        while (iter.hasNext()) {
            String key = ((String) (iter.next()));
            TestCase.assertTrue(map.containsKey(key));
            size++;
        } 
        TestCase.assertEquals(map.size(), size);
    }

    /**
     * java.util.TreeMap#SerializationTest()
     */
    // Regression for Harmony-1066
    public void test_SubMap_Serializable() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
        map.put(1, 2.1);
        map.put(2, 3.1);
        map.put(3, 4.5);
        map.put(7, 21.3);
        SortedMap<Integer, Double> headMap = map.headMap(3);
        TestCase.assertTrue((headMap instanceof Serializable));
        TestCase.assertFalse((headMap instanceof TreeMap));
        TestCase.assertTrue((headMap instanceof SortedMap));
        TestCase.assertFalse(((headMap.entrySet()) instanceof Serializable));
        TestCase.assertFalse(((headMap.keySet()) instanceof Serializable));
        TestCase.assertFalse(((headMap.values()) instanceof Serializable));
        // This assertion will fail on RI. This is a bug of RI.
        SerializationTest.verifySelf(headMap);
    }

    /**
     * {@link java.util.TreeMap#firstEntry()}
     */
    public void test_firstEntry() throws Exception {
        Integer testint = new Integer((-1));
        Integer testint10000 = new Integer((-10000));
        Integer testint9999 = new Integer((-9999));
        TestCase.assertEquals(objArray[0].toString(), tm.firstEntry().getKey());
        TestCase.assertEquals(objArray[0], tm.firstEntry().getValue());
        tm.put(testint.toString(), testint);
        TestCase.assertEquals(testint.toString(), tm.firstEntry().getKey());
        TestCase.assertEquals(testint, tm.firstEntry().getValue());
        tm.put(testint10000.toString(), testint10000);
        TestCase.assertEquals(testint.toString(), tm.firstEntry().getKey());
        TestCase.assertEquals(testint, tm.firstEntry().getValue());
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(testint.toString(), tm.firstEntry().getKey());
        Map.Entry entry = tm.firstEntry();
        TestCase.assertEquals(testint, entry.getValue());
        assertEntry(entry);
        tm.clear();
        TestCase.assertNull(tm.firstEntry());
    }

    /**
     * {@link java.util.TreeMap#lastEntry()
     */
    public void test_lastEntry() throws Exception {
        Integer testint10000 = new Integer(10000);
        Integer testint9999 = new Integer(9999);
        TestCase.assertEquals(objArray[999].toString(), tm.lastEntry().getKey());
        TestCase.assertEquals(objArray[999], tm.lastEntry().getValue());
        tm.put(testint10000.toString(), testint10000);
        TestCase.assertEquals(objArray[999].toString(), tm.lastEntry().getKey());
        TestCase.assertEquals(objArray[999], tm.lastEntry().getValue());
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(testint9999.toString(), tm.lastEntry().getKey());
        Map.Entry entry = tm.lastEntry();
        TestCase.assertEquals(testint9999, entry.getValue());
        assertEntry(entry);
        tm.clear();
        TestCase.assertNull(tm.lastEntry());
    }

    /**
     * {@link java.util.TreeMap#pollFirstEntry()
     */
    public void test_pollFirstEntry() throws Exception {
        Integer testint = new Integer((-1));
        Integer testint10000 = new Integer((-10000));
        Integer testint9999 = new Integer((-9999));
        TestCase.assertEquals(objArray[0].toString(), tm.pollFirstEntry().getKey());
        TestCase.assertEquals(objArray[1], tm.pollFirstEntry().getValue());
        TestCase.assertEquals(objArray[10], tm.pollFirstEntry().getValue());
        tm.put(testint.toString(), testint);
        tm.put(testint10000.toString(), testint10000);
        TestCase.assertEquals(testint.toString(), tm.pollFirstEntry().getKey());
        TestCase.assertEquals(testint10000, tm.pollFirstEntry().getValue());
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(testint9999.toString(), tm.pollFirstEntry().getKey());
        Map.Entry entry = tm.pollFirstEntry();
        assertEntry(entry);
        TestCase.assertEquals(objArray[100], entry.getValue());
        tm.clear();
        TestCase.assertNull(tm.pollFirstEntry());
    }

    /**
     * {@link java.util.TreeMap#pollLastEntry()
     */
    public void test_pollLastEntry() throws Exception {
        Integer testint10000 = new Integer(10000);
        Integer testint9999 = new Integer(9999);
        TestCase.assertEquals(objArray[999].toString(), tm.pollLastEntry().getKey());
        TestCase.assertEquals(objArray[998], tm.pollLastEntry().getValue());
        TestCase.assertEquals(objArray[997], tm.pollLastEntry().getValue());
        tm.put(testint10000.toString(), testint10000);
        TestCase.assertEquals(objArray[996], tm.pollLastEntry().getValue());
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(testint9999.toString(), tm.pollLastEntry().getKey());
        Map.Entry entry = tm.pollLastEntry();
        TestCase.assertEquals(objArray[995], entry.getValue());
        assertEntry(entry);
        tm.clear();
        TestCase.assertNull(tm.pollLastEntry());
    }

    /**
     * {@link java.util.TreeMap#lowerEntry(Object)
     */
    public void test_lowerEntry() throws Exception {
        Integer testint10000 = new Integer(10000);
        Integer testint9999 = new Integer(9999);
        TestCase.assertEquals(objArray[999], tm.lowerEntry(testint9999.toString()).getValue());
        TestCase.assertEquals(objArray[100], tm.lowerEntry(testint10000.toString()).getValue());
        tm.put(testint10000.toString(), testint10000);
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(objArray[999], tm.lowerEntry(testint9999.toString()).getValue());
        Map.Entry entry = tm.lowerEntry(testint10000.toString());
        TestCase.assertEquals(objArray[100], entry.getValue());
        assertEntry(entry);
        try {
            tm.lowerEntry(testint10000);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.lowerEntry(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.lowerEntry(testint9999.toString()));
    }

    /**
     * {@link java.util.TreeMap#lowerKey(Object)
     */
    public void test_lowerKey() throws Exception {
        Integer testint10000 = new Integer(10000);
        Integer testint9999 = new Integer(9999);
        TestCase.assertEquals(objArray[999].toString(), tm.lowerKey(testint9999.toString()));
        TestCase.assertEquals(objArray[100].toString(), tm.lowerKey(testint10000.toString()));
        tm.put(testint10000.toString(), testint10000);
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(objArray[999].toString(), tm.lowerKey(testint9999.toString()));
        TestCase.assertEquals(objArray[100].toString(), tm.lowerKey(testint10000.toString()));
        try {
            tm.lowerKey(testint10000);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.lowerKey(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.lowerKey(testint9999.toString()));
    }

    /**
     * {@link java.util.TreeMap#floorEntry(Object)
     */
    public void test_floorEntry() throws Exception {
        Integer testint10000 = new Integer(10000);
        Integer testint9999 = new Integer(9999);
        TestCase.assertEquals(objArray[999], tm.floorEntry(testint9999.toString()).getValue());
        TestCase.assertEquals(objArray[100], tm.floorEntry(testint10000.toString()).getValue());
        tm.put(testint10000.toString(), testint10000);
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(testint9999, tm.floorEntry(testint9999.toString()).getValue());
        Map.Entry entry = tm.floorEntry(testint10000.toString());
        TestCase.assertEquals(testint10000, entry.getValue());
        assertEntry(entry);
        try {
            tm.floorEntry(testint10000);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.floorEntry(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.floorEntry(testint9999.toString()));
    }

    /**
     * {@link java.util.TreeMap#floorKey(Object)
     */
    public void test_floorKey() throws Exception {
        Integer testint10000 = new Integer(10000);
        Integer testint9999 = new Integer(9999);
        TestCase.assertEquals(objArray[999].toString(), tm.floorKey(testint9999.toString()));
        TestCase.assertEquals(objArray[100].toString(), tm.floorKey(testint10000.toString()));
        tm.put(testint10000.toString(), testint10000);
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertEquals(testint9999.toString(), tm.floorKey(testint9999.toString()));
        TestCase.assertEquals(testint10000.toString(), tm.floorKey(testint10000.toString()));
        try {
            tm.floorKey(testint10000);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.floorKey(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.floorKey(testint9999.toString()));
    }

    /**
     * {@link java.util.TreeMap#ceilingEntry(Object)
     */
    public void test_ceilingEntry() throws Exception {
        Integer testint100 = new Integer(100);
        Integer testint = new Integer((-1));
        TestCase.assertEquals(objArray[0], tm.ceilingEntry(testint.toString()).getValue());
        TestCase.assertEquals(objArray[100], tm.ceilingEntry(testint100.toString()).getValue());
        tm.put(testint.toString(), testint);
        tm.put(testint100.toString(), testint);
        TestCase.assertEquals(testint, tm.ceilingEntry(testint.toString()).getValue());
        Map.Entry entry = tm.ceilingEntry(testint100.toString());
        TestCase.assertEquals(testint, entry.getValue());
        assertEntry(entry);
        try {
            tm.ceilingEntry(testint100);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.ceilingEntry(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.ceilingEntry(testint.toString()));
    }

    /**
     * {@link java.util.TreeMap#ceilingKey(Object)
     */
    public void test_ceilingKey() throws Exception {
        Integer testint100 = new Integer(100);
        Integer testint = new Integer((-1));
        TestCase.assertEquals(objArray[0].toString(), tm.ceilingKey(testint.toString()));
        TestCase.assertEquals(objArray[100].toString(), tm.ceilingKey(testint100.toString()));
        tm.put(testint.toString(), testint);
        tm.put(testint100.toString(), testint);
        TestCase.assertEquals(testint.toString(), tm.ceilingKey(testint.toString()));
        TestCase.assertEquals(testint100.toString(), tm.ceilingKey(testint100.toString()));
        try {
            tm.ceilingKey(testint100);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.ceilingKey(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.ceilingKey(testint.toString()));
    }

    /**
     * {@link java.util.TreeMap#higherEntry(Object)
     */
    public void test_higherEntry() throws Exception {
        Integer testint9999 = new Integer(9999);
        Integer testint10000 = new Integer(10000);
        Integer testint100 = new Integer(100);
        Integer testint = new Integer((-1));
        TestCase.assertEquals(objArray[0], tm.higherEntry(testint.toString()).getValue());
        TestCase.assertEquals(objArray[101], tm.higherEntry(testint100.toString()).getValue());
        TestCase.assertEquals(objArray[101], tm.higherEntry(testint10000.toString()).getValue());
        tm.put(testint9999.toString(), testint);
        tm.put(testint100.toString(), testint);
        tm.put(testint10000.toString(), testint);
        TestCase.assertEquals(objArray[0], tm.higherEntry(testint.toString()).getValue());
        TestCase.assertEquals(testint, tm.higherEntry(testint100.toString()).getValue());
        Map.Entry entry = tm.higherEntry(testint10000.toString());
        TestCase.assertEquals(objArray[101], entry.getValue());
        assertEntry(entry);
        TestCase.assertNull(tm.higherEntry(testint9999.toString()));
        try {
            tm.higherEntry(testint100);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.higherEntry(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.higherEntry(testint.toString()));
    }

    /**
     * {@link java.util.TreeMap#higherKey(Object)
     */
    public void test_higherKey() throws Exception {
        Integer testint9999 = new Integer(9999);
        Integer testint10000 = new Integer(10000);
        Integer testint100 = new Integer(100);
        Integer testint = new Integer((-1));
        TestCase.assertEquals(objArray[0].toString(), tm.higherKey(testint.toString()));
        TestCase.assertEquals(objArray[101].toString(), tm.higherKey(testint100.toString()));
        TestCase.assertEquals(objArray[101].toString(), tm.higherKey(testint10000.toString()));
        tm.put(testint9999.toString(), testint);
        tm.put(testint100.toString(), testint);
        tm.put(testint10000.toString(), testint);
        TestCase.assertEquals(objArray[0].toString(), tm.higherKey(testint.toString()));
        TestCase.assertEquals(testint10000.toString(), tm.higherKey(testint100.toString()));
        TestCase.assertEquals(objArray[101].toString(), tm.higherKey(testint10000.toString()));
        TestCase.assertNull(tm.higherKey(testint9999.toString()));
        try {
            tm.higherKey(testint100);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.higherKey(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        tm.clear();
        TestCase.assertNull(tm.higherKey(testint.toString()));
    }

    public void test_navigableKeySet() throws Exception {
        Integer testint9999 = new Integer(9999);
        Integer testint10000 = new Integer(10000);
        Integer testint100 = new Integer(100);
        Integer testint0 = new Integer(0);
        NavigableSet set = tm.navigableKeySet();
        TestCase.assertFalse(set.contains(testint9999.toString()));
        tm.put(testint9999.toString(), testint9999);
        TestCase.assertTrue(set.contains(testint9999.toString()));
        tm.remove(testint9999.toString());
        TestCase.assertFalse(set.contains(testint9999.toString()));
        try {
            set.add(new Object());
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            set.add(null);
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            set.addAll(null);
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (NullPointerException e) {
            // expected
        }
        Collection collection = new LinkedList();
        set.addAll(collection);
        try {
            collection.add(new Object());
            set.addAll(collection);
            TestCase.fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        set.remove(testint100.toString());
        TestCase.assertFalse(tm.containsKey(testint100.toString()));
        TestCase.assertTrue(tm.containsKey(testint0.toString()));
        Iterator iter = set.iterator();
        iter.next();
        iter.remove();
        TestCase.assertFalse(tm.containsKey(testint0.toString()));
        collection.add(new Integer(200).toString());
        set.retainAll(collection);
        TestCase.assertEquals(1, tm.size());
        set.removeAll(collection);
        TestCase.assertEquals(0, tm.size());
        tm.put(testint10000.toString(), testint10000);
        TestCase.assertEquals(1, tm.size());
        set.clear();
        TestCase.assertEquals(0, tm.size());
    }

    /**
     * java.util.TreeMap#subMap(java.lang.Object, boolean,
     * java.lang.Object, boolean)
     */
    public void test_subMapLjava_lang_ObjectZLjava_lang_ObjectZ() {
        // normal case
        SortedMap subMap = tm.subMap(objArray[100].toString(), true, objArray[109].toString(), true);
        TestCase.assertEquals("subMap is of incorrect size", 10, subMap.size());
        subMap = tm.subMap(objArray[100].toString(), true, objArray[109].toString(), false);
        TestCase.assertEquals("subMap is of incorrect size", 9, subMap.size());
        for (int counter = 100; counter < 109; counter++) {
            TestCase.assertTrue("SubMap contains incorrect elements", subMap.get(objArray[counter].toString()).equals(objArray[counter]));
        }
        subMap = tm.subMap(objArray[100].toString(), false, objArray[109].toString(), true);
        TestCase.assertEquals("subMap is of incorrect size", 9, subMap.size());
        TestCase.assertNull(subMap.get(objArray[100].toString()));
        // Exceptions
        try {
            tm.subMap(objArray[9].toString(), true, objArray[1].toString(), true);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            tm.subMap(objArray[9].toString(), false, objArray[1].toString(), false);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            tm.subMap(null, true, null, true);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.subMap(null, false, objArray[100], true);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.subMap(new LinkedList(), false, objArray[100], true);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        // use integer elements to test
        TreeMap<Integer, String> treeMapInt = new TreeMap<Integer, String>();
        TestCase.assertEquals(0, treeMapInt.subMap(new Integer((-1)), true, new Integer(100), true).size());
        for (int i = 0; i < 100; i++) {
            treeMapInt.put(new Integer(i), new Integer(i).toString());
        }
        SortedMap<Integer, String> result = treeMapInt.subMap(new Integer((-1)), true, new Integer(100), true);
        TestCase.assertEquals(100, result.size());
        result.put(new Integer((-1)), new Integer((-1)).toString());
        TestCase.assertEquals(101, result.size());
        TestCase.assertEquals(101, treeMapInt.size());
        result = treeMapInt.subMap(new Integer(50), true, new Integer(60), true);
        TestCase.assertEquals(11, result.size());
        try {
            result.put(new Integer((-2)), new Integer((-2)).toString());
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        TestCase.assertEquals(11, result.size());
        treeMapInt.remove(new Integer(50));
        TestCase.assertEquals(100, treeMapInt.size());
        TestCase.assertEquals(10, result.size());
        result.remove(new Integer(60));
        TestCase.assertEquals(99, treeMapInt.size());
        TestCase.assertEquals(9, result.size());
        SortedMap<Integer, String> result2 = null;
        try {
            result2 = result.subMap(new Integer((-2)), new Integer(100));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        result2 = result.subMap(new Integer(50), new Integer(60));
        TestCase.assertEquals(9, result2.size());
        // sub map of sub map
        NavigableMap<Integer, Object> mapIntObj = new TreeMap<Integer, Object>();
        for (int i = 0; i < 10; ++i) {
            mapIntObj.put(i, new Object());
        }
        mapIntObj = mapIntObj.subMap(5, false, 9, true);
        TestCase.assertEquals(4, mapIntObj.size());
        mapIntObj = mapIntObj.subMap(5, false, 9, true);
        TestCase.assertEquals(4, mapIntObj.size());
        mapIntObj = mapIntObj.subMap(5, false, 6, false);
        TestCase.assertEquals(0, mapIntObj.size());
        // a special comparator dealing with null key
        tm = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return ((String) (o1)).compareTo(((String) (o2)));
            }
        });
        tm.put(null, (-1));
        tm.put(new String("1st"), 1);
        tm.put(new String("2nd"), 2);
        tm.put(new String("3rd"), 3);
        SortedMap s = tm.subMap(null, "3rd");
        TestCase.assertEquals(3, s.size());
        TestCase.assertTrue(s.containsValue((-1)));
        TestCase.assertTrue(s.containsValue(1));
        TestCase.assertTrue(s.containsValue(2));
        TestCase.assertFalse(s.containsKey(null));
        s = tm.descendingMap();
        s = s.subMap("3rd", null);
        TestCase.assertFalse(s.containsKey(null));
        TestCase.assertTrue(s.containsKey("1st"));
        TestCase.assertTrue(s.containsKey("2nd"));
        TestCase.assertTrue(s.containsKey("3rd"));
    }

    public void test_subMap_NullTolerableComparator() {
        // Null Tolerable Comparator
        TreeMap<String, String> treeMapWithNull = new TreeMap<String, String>(new TreeMapTest.MockComparatorNullTolerable());
        treeMapWithNull.put("key1", "value1");// $NON-NLS-1$ //$NON-NLS-2$

        treeMapWithNull.put(null, "value2");// $NON-NLS-1$

        SortedMap<String, String> subMapWithNull = treeMapWithNull.subMap(null, true, "key1", true);// $NON-NLS-1$

        // RI fails here
        TestCase.assertEquals("Size of subMap should be 2:", 2, subMapWithNull.size());// $NON-NLS-1$

        TestCase.assertEquals("value1", subMapWithNull.get("key1"));
        TestCase.assertEquals("value2", subMapWithNull.get(null));
        treeMapWithNull.put("key0", "value2");
        treeMapWithNull.put("key3", "value3");
        treeMapWithNull.put("key4", "value4");
        treeMapWithNull.put("key5", "value5");
        treeMapWithNull.put("key6", "value6");
        TestCase.assertEquals("Size of subMap should be 3:", 3, subMapWithNull.size());// $NON-NLS-1$

        subMapWithNull = treeMapWithNull.subMap(null, false, "key1", true);// $NON-NLS-1$

        TestCase.assertEquals("Size of subMap should be 2:", 2, subMapWithNull.size());// $NON-NLS-1$

    }

    /**
     * java.util.TreeMap#headMap(java.lang.Object, boolea)
     */
    public void test_headMapLjava_lang_ObjectZL() {
        // normal case
        SortedMap subMap = tm.headMap(objArray[100].toString(), true);
        TestCase.assertEquals("subMap is of incorrect size", 4, subMap.size());
        subMap = tm.headMap(objArray[109].toString(), true);
        TestCase.assertEquals("subMap is of incorrect size", 13, subMap.size());
        for (int counter = 100; counter < 109; counter++) {
            TestCase.assertTrue("SubMap contains incorrect elements", subMap.get(objArray[counter].toString()).equals(objArray[counter]));
        }
        subMap = tm.headMap(objArray[100].toString(), false);
        TestCase.assertEquals("subMap is of incorrect size", 3, subMap.size());
        TestCase.assertNull(subMap.get(objArray[100].toString()));
        // Exceptions
        TestCase.assertEquals(0, tm.headMap("", true).size());
        TestCase.assertEquals(0, tm.headMap("", false).size());
        try {
            tm.headMap(null, true);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.headMap(null, false);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.headMap(new Object(), true);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.headMap(new Object(), false);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        // use integer elements to test
        TreeMap<Integer, String> treeMapInt = new TreeMap<Integer, String>();
        TestCase.assertEquals(0, treeMapInt.headMap(new Integer((-1)), true).size());
        for (int i = 0; i < 100; i++) {
            treeMapInt.put(new Integer(i), new Integer(i).toString());
        }
        SortedMap<Integer, String> result = treeMapInt.headMap(new Integer(101));
        TestCase.assertEquals(100, result.size());
        try {
            result.put(new Integer(101), new Integer(101).toString());
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        TestCase.assertEquals(100, result.size());
        TestCase.assertEquals(100, treeMapInt.size());
        result = treeMapInt.headMap(new Integer(50), true);
        TestCase.assertEquals(51, result.size());
        result.put(new Integer((-1)), new Integer((-1)).toString());
        TestCase.assertEquals(52, result.size());
        treeMapInt.remove(new Integer(40));
        TestCase.assertEquals(100, treeMapInt.size());
        TestCase.assertEquals(51, result.size());
        result.remove(new Integer(30));
        TestCase.assertEquals(99, treeMapInt.size());
        TestCase.assertEquals(50, result.size());
        SortedMap<Integer, String> result2 = null;
        try {
            result.subMap(new Integer((-2)), new Integer(100));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            result.subMap(new Integer(1), new Integer(100));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        result2 = result.subMap(new Integer((-2)), new Integer(48));
        TestCase.assertEquals(47, result2.size());
        result2 = result.subMap(new Integer(40), new Integer(50));
        TestCase.assertEquals(9, result2.size());
        // Null Tolerable Comparator
        TreeMap<String, String> treeMapWithNull = new TreeMap<String, String>(new TreeMapTest.MockComparatorNullTolerable());
        treeMapWithNull.put("key1", "value1");// $NON-NLS-1$ //$NON-NLS-2$

        treeMapWithNull.put(null, "value2");// $NON-NLS-1$

        SortedMap<String, String> subMapWithNull = treeMapWithNull.headMap(null, true);// $NON-NLS-1$

        TestCase.assertEquals("Size of subMap should be 1:", 1, subMapWithNull.size());// $NON-NLS-1$

        TestCase.assertEquals(null, subMapWithNull.get("key1"));
        TestCase.assertEquals("value2", subMapWithNull.get(null));
        treeMapWithNull.put("key0", "value2");
        treeMapWithNull.put("key3", "value3");
        treeMapWithNull.put("key4", "value4");
        treeMapWithNull.put("key5", "value5");
        treeMapWithNull.put("key6", "value6");
        TestCase.assertEquals("Size of subMap should be 1:", 1, subMapWithNull.size());// $NON-NLS-1$

        subMapWithNull = treeMapWithNull.subMap(null, false, "key1", true);// $NON-NLS-1$

        TestCase.assertEquals("Size of subMap should be 2:", 2, subMapWithNull.size());// $NON-NLS-1$

        // head map of head map
        NavigableMap<Integer, Object> original = new TreeMap<Integer, Object>();
        for (int i = 0; i < 10; ++i) {
            original.put(i, new Object());
        }
        NavigableMap<Integer, Object> mapIntObj = original.headMap(5, false);
        TestCase.assertEquals(5, mapIntObj.size());
        mapIntObj = mapIntObj.headMap(5, false);
        TestCase.assertEquals(5, mapIntObj.size());
        try {
            mapIntObj = mapIntObj.tailMap(5, false);
            TestCase.fail("IllegalArgumentException expected: key falls outside restricted range");
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(0, original.headMap(0, false).size());
    }

    /**
     * java.util.TreeMap#tailMap(java.lang.Object, boolea)
     */
    public void test_tailMapLjava_lang_ObjectZL() {
        // normal case
        SortedMap subMap = tm.tailMap(objArray[100].toString(), true);
        TestCase.assertEquals("subMap is of incorrect size", 997, subMap.size());
        subMap = tm.tailMap(objArray[109].toString(), true);
        TestCase.assertEquals("subMap is of incorrect size", 988, subMap.size());
        for (int counter = 119; counter > 110; counter--) {
            TestCase.assertTrue("SubMap contains incorrect elements", subMap.get(objArray[counter].toString()).equals(objArray[counter]));
        }
        subMap = tm.tailMap(objArray[100].toString(), false);
        TestCase.assertEquals("subMap is of incorrect size", 996, subMap.size());
        TestCase.assertNull(subMap.get(objArray[100].toString()));
        // Exceptions
        TestCase.assertEquals(1000, tm.tailMap("", true).size());
        TestCase.assertEquals(1000, tm.tailMap("", false).size());
        try {
            tm.tailMap(null, true);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.tailMap(null, false);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.tailMap(new Object(), true);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.tailMap(new Object(), false);
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        // use integer elements to test
        TreeMap<Integer, String> treeMapInt = new TreeMap<Integer, String>();
        TestCase.assertEquals(0, treeMapInt.tailMap(new Integer((-1)), true).size());
        for (int i = 0; i < 100; i++) {
            treeMapInt.put(new Integer(i), new Integer(i).toString());
        }
        SortedMap<Integer, String> result = treeMapInt.tailMap(new Integer(1));
        TestCase.assertEquals(99, result.size());
        try {
            result.put(new Integer((-1)), new Integer((-1)).toString());
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        TestCase.assertEquals(99, result.size());
        TestCase.assertEquals(100, treeMapInt.size());
        result = treeMapInt.tailMap(new Integer(50), true);
        TestCase.assertEquals(50, result.size());
        result.put(new Integer(101), new Integer(101).toString());
        TestCase.assertEquals(51, result.size());
        treeMapInt.remove(new Integer(60));
        TestCase.assertEquals(100, treeMapInt.size());
        TestCase.assertEquals(50, result.size());
        result.remove(new Integer(70));
        TestCase.assertEquals(99, treeMapInt.size());
        TestCase.assertEquals(49, result.size());
        SortedMap<Integer, String> result2 = null;
        try {
            result2 = result.subMap(new Integer((-2)), new Integer(100));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        result2 = result.subMap(new Integer(60), new Integer(70));
        TestCase.assertEquals(9, result2.size());
        // Null Tolerable Comparator
        TreeMap<String, String> treeMapWithNull = new TreeMap<String, String>(new TreeMapTest.MockComparatorNullTolerable());
        treeMapWithNull.put("key1", "value1");// $NON-NLS-1$ //$NON-NLS-2$

        treeMapWithNull.put(null, "value2");// $NON-NLS-1$

        SortedMap<String, String> subMapWithNull = treeMapWithNull.tailMap("key1", true);// $NON-NLS-1$

        TestCase.assertEquals("Size of subMap should be 1:", 1, subMapWithNull.size());// $NON-NLS-1$

        TestCase.assertEquals("value1", subMapWithNull.get("key1"));
        TestCase.assertEquals(null, subMapWithNull.get(null));
        treeMapWithNull.put("key0", "value2");
        treeMapWithNull.put("key3", "value3");
        treeMapWithNull.put("key4", "value4");
        treeMapWithNull.put("key5", "value5");
        treeMapWithNull.put("key6", "value6");
        TestCase.assertEquals("Size of subMap should be 5:", 5, subMapWithNull.size());// $NON-NLS-1$

        subMapWithNull = treeMapWithNull.subMap(null, false, "key1", true);// $NON-NLS-1$

        TestCase.assertEquals("Size of subMap should be 2:", 2, subMapWithNull.size());// $NON-NLS-1$

        // tail map of tail map
        NavigableMap<Integer, Object> original = new TreeMap<Integer, Object>();
        for (int i = 0; i < 10; ++i) {
            original.put(i, new Object());
        }
        NavigableMap<Integer, Object> mapIntObj = original.tailMap(5, false);
        TestCase.assertEquals(4, mapIntObj.size());
        mapIntObj = mapIntObj.tailMap(5, false);
        TestCase.assertEquals(4, mapIntObj.size());
        try {
            mapIntObj = mapIntObj.headMap(5, false);
            TestCase.fail("IllegalArgumentException expected: key falls outside restricted range");
        } catch (IllegalArgumentException expected) {
        }
        TestCase.assertEquals(0, original.headMap(0, false).size());
    }

    public void test_descendingMap_subMap() throws Exception {
        TreeMap<Integer, Object> tm = new TreeMap<Integer, Object>();
        for (int i = 0; i < 10; ++i) {
            tm.put(i, new Object());
        }
        NavigableMap<Integer, Object> descMap = tm.descendingMap();
        TestCase.assertEquals(7, descMap.subMap(8, true, 1, false).size());
        TestCase.assertEquals(4, descMap.headMap(6, true).size());
        TestCase.assertEquals(2, descMap.tailMap(2, false).size());
        // sub map of sub map of descendingMap
        NavigableMap<Integer, Object> mapIntObj = new TreeMap<Integer, Object>();
        for (int i = 0; i < 10; ++i) {
            mapIntObj.put(i, new Object());
        }
        mapIntObj = mapIntObj.descendingMap();
        NavigableMap<Integer, Object> subMapIntObj = mapIntObj.subMap(9, true, 5, false);
        TestCase.assertEquals(4, subMapIntObj.size());
        subMapIntObj = subMapIntObj.subMap(9, true, 5, false);
        TestCase.assertEquals(4, subMapIntObj.size());
        subMapIntObj = subMapIntObj.subMap(6, false, 5, false);
        TestCase.assertEquals(0, subMapIntObj.size());
        subMapIntObj = mapIntObj.headMap(5, false);
        TestCase.assertEquals(4, subMapIntObj.size());
        subMapIntObj = subMapIntObj.headMap(5, false);
        TestCase.assertEquals(4, subMapIntObj.size());
        try {
            subMapIntObj = subMapIntObj.tailMap(5, false);
            TestCase.fail("IllegalArgumentException expected: key falls outside restricted range");
        } catch (IllegalArgumentException expected) {
        }
        subMapIntObj = mapIntObj.tailMap(5, false);
        TestCase.assertEquals(5, subMapIntObj.size());
        subMapIntObj = subMapIntObj.tailMap(5, false);
        TestCase.assertEquals(5, subMapIntObj.size());
        try {
            subMapIntObj = subMapIntObj.headMap(5, false);
            TestCase.fail("IllegalArgumentException expected: key falls outside restricted range");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * Tests equals() method.
     * Tests that no ClassCastException will be thrown in all cases.
     * Regression test for HARMONY-1639.
     */
    public void test_equals() throws Exception {
        // comparing TreeMaps with different object types
        Map m1 = new TreeMap();
        Map m2 = new TreeMap();
        m1.put("key1", "val1");
        m1.put("key2", "val2");
        m2.put(new Integer(1), "val1");
        m2.put(new Integer(2), "val2");
        TestCase.assertFalse("Maps should not be equal 1", m1.equals(m2));
        TestCase.assertFalse("Maps should not be equal 2", m2.equals(m1));
        // comparing TreeMap with HashMap
        m1 = new TreeMap();
        m2 = new HashMap();
        m1.put("key", "val");
        m2.put(new Object(), "val");
        TestCase.assertFalse("Maps should not be equal 3", m1.equals(m2));
        TestCase.assertFalse("Maps should not be equal 4", m2.equals(m1));
    }

    public void test_invalidKeys() throws Exception {
        // comparing TreeMaps with not-comparable objects inside
        TreeMap m1 = new TreeMap();
        try {
            m1.put(new Object(), "val1");
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException expected) {
        }
    }

    public void test_remove_from_iterator() throws Exception {
        Set set = tm.keySet();
        Iterator iter = set.iterator();
        iter.next();
        iter.remove();
        try {
            iter.remove();
            TestCase.fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    /**
     * Tests entrySet().contains() method behaviour with respect to entries
     * with null values.
     * Regression test for HARMONY-5788.
     */
    public void test_entrySet_contains() throws Exception {
        TreeMap master = new TreeMap<String, String>();
        TreeMap test_map = new TreeMap<String, String>();
        master.put("null", null);
        Object[] entry = master.entrySet().toArray();
        TestCase.assertFalse("Empty map should not contain the null-valued entry", test_map.entrySet().contains(entry[0]));
        Map<String, String> submap = test_map.subMap("a", "z");
        entry = master.entrySet().toArray();
        TestCase.assertFalse("Empty submap should not contain the null-valued entry", submap.entrySet().contains(entry[0]));
        test_map.put("null", null);
        TestCase.assertTrue("entrySet().containsAll(...) should work with null values", test_map.entrySet().containsAll(master.entrySet()));
        master.clear();
        master.put("null", '0');
        entry = master.entrySet().toArray();
        TestCase.assertFalse("Null-valued entry should not equal non-null-valued entry", test_map.entrySet().contains(entry[0]));
    }

    public void test_iterator_next_() {
        Map m = tm.subMap("0", "1");
        Iterator it = m.entrySet().iterator();
        TestCase.assertEquals("0=0", it.next().toString());
        while (it.hasNext()) {
        } 
        try {
            it.next();
            TestCase.fail("should throw java.util.NoSuchElementException");
        } catch (Exception e) {
            TestCase.assertTrue((e instanceof NoSuchElementException));
        }
    }

    public void test_empty_subMap() throws Exception {
        TreeMap<Float, List<Integer>> tm = new TreeMap<Float, List<Integer>>();
        SortedMap<Float, List<Integer>> sm = tm.tailMap(1.1F);
        TestCase.assertTrue(((sm.values().size()) == 0));
    }

    public static TreeMap treeMap = new TreeMap();

    public void test_values_1() {
        TreeMapTest.treeMap.put("firstKey", "firstValue");
        TreeMapTest.treeMap.put("secondKey", "secondValue");
        TreeMapTest.treeMap.put("thirdKey", "thirdValue");
        Object firstKey = TreeMapTest.treeMap.firstKey();
        SortedMap subMap = ((SortedMap) (TreeMapTest.treeMap)).subMap(firstKey, firstKey);
        Iterator iter = subMap.values().iterator();
    }

    public void test_forEach() throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("one", "1");
        map.put("two", "2");
        map.put("three", "3");
        TreeMap<String, String> output = new TreeMap<>();
        map.forEach(( k, v) -> output.put(k, v));
        TestCase.assertEquals(map, output);
        HashSet<String> setOutput = new HashSet<>();
        map.keySet().forEach(( k) -> setOutput.add(k));
        TestCase.assertEquals(map.keySet(), setOutput);
        setOutput.clear();
        map.values().forEach(( v) -> setOutput.add(v));
        TestCase.assertEquals(new HashSet<>(map.values()), setOutput);
        HashSet<Map.Entry<String, String>> entrySetOutput = new HashSet<>();
        map.entrySet().forEach(( v) -> entrySetOutput.add(v));
        TestCase.assertEquals(map.entrySet(), entrySetOutput);
    }

    public void test_forEach_NPE() throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            map.forEach(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            map.keySet().forEach(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            map.values().forEach(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        try {
            map.entrySet().forEach(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_forEach_CME() throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("one", "1");
        map.put("two", "2");
        try {
            map.forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String k, String v) {
                    map.put("foo", v);
                }
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        try {
            map.keySet().forEach(new Consumer<String>() {
                @Override
                public void accept(String k) {
                    map.put("foo2", "boo");
                }
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        try {
            map.values().forEach(new Consumer<String>() {
                @Override
                public void accept(String k) {
                    map.put("foo3", "boo");
                }
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        try {
            map.entrySet().forEach(new Consumer<Map.Entry<String, String>>() {
                @Override
                public void accept(Map.Entry<String, String> k) {
                    map.put("foo4", "boo");
                }
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }
}

