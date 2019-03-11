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
package libcore.java.util;


import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import junit.framework.TestCase;
import tests.support.Support_MapTest2;


public class OldTreeMapTest extends TestCase {
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
                return 1;
            }
            return o1.compareTo(o2);
        }
    }

    TreeMap tm;

    Object[] objArray = new Object[1000];

    public void test_Constructor() {
        // Test for method java.util.TreeMap()
        new Support_MapTest2(new TreeMap()).runTest();
        TestCase.assertTrue("New treeMap non-empty", new TreeMap().isEmpty());
    }

    public void test_ConstructorLjava_util_Comparator() {
        // Test for method java.util.TreeMap(java.util.Comparator)
        Comparator comp = new OldTreeMapTest.ReversedComparator();
        TreeMap reversedTreeMap = new TreeMap(comp);
        TestCase.assertTrue("TreeMap answered incorrect comparator", ((reversedTreeMap.comparator()) == comp));
        reversedTreeMap.put(new Integer(1).toString(), new Integer(1));
        reversedTreeMap.put(new Integer(2).toString(), new Integer(2));
        TestCase.assertTrue("TreeMap does not use comparator (firstKey was incorrect)", reversedTreeMap.firstKey().equals(new Integer(2).toString()));
        TestCase.assertTrue("TreeMap does not use comparator (lastKey was incorrect)", reversedTreeMap.lastKey().equals(new Integer(1).toString()));
    }

    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.TreeMap(java.util.Map)
        TreeMap myTreeMap = new TreeMap(new HashMap(tm));
        TestCase.assertTrue("Map is incorrect size", ((myTreeMap.size()) == (objArray.length)));
        for (Object element : objArray) {
            TestCase.assertTrue("Map has incorrect mappings", myTreeMap.get(element.toString()).equals(element));
        }
        HashMap hm = new HashMap();
        hm.put(new Integer(1), "one");
        hm.put("one", new Integer(1));
        try {
            new TreeMap(hm);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            new TreeMap(((Map) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_ConstructorLjava_util_SortedMap() {
        // Test for method java.util.TreeMap(java.util.SortedMap)
        Comparator comp = new OldTreeMapTest.ReversedComparator();
        TreeMap reversedTreeMap = new TreeMap(comp);
        reversedTreeMap.put(new Integer(1).toString(), new Integer(1));
        reversedTreeMap.put(new Integer(2).toString(), new Integer(2));
        TreeMap anotherTreeMap = new TreeMap(reversedTreeMap);
        TestCase.assertTrue("New tree map does not answer correct comparator", ((anotherTreeMap.comparator()) == comp));
        TestCase.assertTrue("TreeMap does not use comparator (firstKey was incorrect)", anotherTreeMap.firstKey().equals(new Integer(2).toString()));
        TestCase.assertTrue("TreeMap does not use comparator (lastKey was incorrect)", anotherTreeMap.lastKey().equals(new Integer(1).toString()));
        try {
            new TreeMap(((SortedMap) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.TreeMap.containsKey(java.lang.Object)
        TestCase.assertTrue("Returned false for valid key", tm.containsKey("95"));
        TestCase.assertTrue("Returned true for invalid key", (!(tm.containsKey("XXXXX"))));
        try {
            tm.containsKey(new Double(3.14));
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.containsKey(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_firstKey() {
        // Test for method java.lang.Object java.util.TreeMap.firstKey()
        TestCase.assertEquals("Returned incorrect first key", "0", tm.firstKey());
        tm = new TreeMap();
        try {
            tm.firstKey();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.TreeMap.get(java.lang.Object)
        Object o = new Object();
        tm.put("Hello", o);
        TestCase.assertTrue("Failed to get mapping", ((tm.get("Hello")) == o));
        try {
            tm.get(new Double(3.14));
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.get(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_headMapLjava_lang_Object() {
        // Test for method java.util.SortedMap
        // java.util.TreeMap.headMap(java.lang.Object)
        Map head = tm.headMap("100");
        TestCase.assertEquals("Returned map of incorrect size", 3, head.size());
        TestCase.assertTrue("Returned incorrect elements", (((head.containsKey("0")) && (head.containsValue(new Integer("1")))) && (head.containsKey("10"))));
        SortedMap sort = tm.headMap("100");
        try {
            sort.headMap("50");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            tm.headMap(this);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.headMap(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        // Regression for Harmony-1026
        TreeMap<Integer, Double> map = new TreeMap<Integer, Double>(new OldTreeMapTest.MockComparator());
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
        // Regression for ill-behaved collator
        Collator c = new Collator() {
            @Override
            public int compare(String o1, String o2) {
                if (o1 == null) {
                    return 0;
                }
                return o1.compareTo(o2);
            }

            @Override
            public CollationKey getCollationKey(String string) {
                return null;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
        TreeMap<String, String> treemap = new TreeMap<String, String>(c);
        TestCase.assertEquals(0, treemap.headMap(null).size());
    }

    public void test_lastKey() {
        // Test for method java.lang.Object java.util.TreeMap.lastKey()
        TestCase.assertTrue("Returned incorrect last key", tm.lastKey().equals(objArray[((objArray.length) - 1)].toString()));
        tm = new TreeMap();
        try {
            tm.lastKey();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.TreeMap.put(java.lang.Object, java.lang.Object)
        Object o = new Object();
        tm.put("Hello", o);
        TestCase.assertTrue("Failed to put mapping", ((tm.get("Hello")) == o));
        try {
            tm.put(null, "null");
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        // regression for Harmony-780
        tm = new TreeMap();
        try {
            TestCase.assertNull(tm.put(new Object(), new Object()));
            tm.put(new Integer(1), new Object());
            TestCase.fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
        tm = new TreeMap();
        TestCase.assertNull(tm.put(new Integer(1), new Object()));
    }

    /**
     * Android's TreeMap requires every element to be comparable, even if
     * there's no other element to compare it to. This is more strict than the
     * RI. See Harmony-2474.
     */
    public void testRemoveNonComparableFromEmptyMap() {
        tm = new TreeMap();
        try {
            tm.remove(new Object());// succeeds on RI

        } catch (ClassCastException expected) {
            // expected on libcore only
        }
    }

    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.TreeMap.putAll(java.util.Map)
        TreeMap x = new TreeMap();
        x.putAll(tm);
        TestCase.assertTrue("Map incorrect size after put", ((x.size()) == (tm.size())));
        for (Object element : objArray) {
            TestCase.assertTrue("Failed to put all elements", x.get(element.toString()).equals(element));
        }
        x = new TreeMap();
        x.put(new Integer(1), "one");
        x.put(new Integer(2), "two");
        try {
            tm.putAll(x);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.putAll(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_removeLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.TreeMap.remove(java.lang.Object)
        tm.remove("990");
        TestCase.assertTrue("Failed to remove mapping", (!(tm.containsKey("990"))));
        try {
            tm.remove(new Double(3.14));
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.remove(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

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
        TreeMap<String, String> treeMapWithNull = new TreeMap<String, String>(new OldTreeMapTest.MockComparatorNullTolerable());
        treeMapWithNull.put("key1", "value1");
        treeMapWithNull.put(null, "value2");
        SortedMap<String, String> subMapWithNull = treeMapWithNull.subMap(null, "key1");
        TestCase.assertEquals("Size of subMap should be 1:", 1, subMapWithNull.size());
        // Regression test for typo in lastKey method
        SortedMap<String, String> map = new TreeMap<String, String>();
        map.put("1", "one");
        map.put("2", "two");
        map.put("3", "three");
        TestCase.assertEquals("3", map.lastKey());
        SortedMap<String, String> sub = map.subMap("1", "3");
        TestCase.assertEquals("2", sub.lastKey());
        try {
            tm.subMap(this, this);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.subMap(objArray[9].toString(), null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            tm.subMap(null, objArray[9].toString());
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_tailMapLjava_lang_Object() {
        // Test for method java.util.SortedMap
        // java.util.TreeMap.tailMap(java.lang.Object)
        Map tail = tm.tailMap(objArray[900].toString());
        TestCase.assertTrue(("Returned map of incorrect size : " + (tail.size())), ((tail.size()) == (((objArray.length) - 900) + 9)));
        for (int i = 900; i < (objArray.length); i++) {
            TestCase.assertTrue("Map contains incorrect entries", tail.containsValue(objArray[i]));
        }
        SortedMap sort = tm.tailMap("99");
        try {
            sort.tailMap("101");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            tm.tailMap(this);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            tm.tailMap(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        // Regression for Harmony-1066
        TestCase.assertTrue((tail instanceof Serializable));
    }
}

