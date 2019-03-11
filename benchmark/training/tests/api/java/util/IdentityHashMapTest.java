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
package tests.api.java.util;


import SerializationTest.SerializableAssert;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;
import tests.support.Support_MapTest2;


public class IdentityHashMapTest extends TestCase {
    private static final String ID = "hello";

    class MockMap extends AbstractMap {
        public Set entrySet() {
            return null;
        }

        public int size() {
            return 0;
        }
    }

    /* TODO: change all the statements testing the keys and values with equals()
    method to check for reference equality instead
     */
    IdentityHashMap hm;

    static final int hmSize = 1000;

    Object[] objArray;

    Object[] objArray2;

    /**
     * java.util.IdentityHashMap#IdentityHashMap()
     */
    public void test_Constructor() {
        // Test for method java.util.IdentityHashMap()
        new Support_MapTest2(new IdentityHashMap()).runTest();
        IdentityHashMap hm2 = new IdentityHashMap();
        TestCase.assertEquals("Created incorrect IdentityHashMap", 0, hm2.size());
    }

    /**
     * java.util.IdentityHashMap#IdentityHashMap(int)
     */
    public void test_ConstructorI() {
        // Test for method java.util.IdentityHashMap(int)
        IdentityHashMap hm2 = new IdentityHashMap(5);
        TestCase.assertEquals("Created incorrect IdentityHashMap", 0, hm2.size());
        try {
            new IdentityHashMap((-1));
            TestCase.fail("Failed to throw IllegalArgumentException for initial capacity < 0");
        } catch (IllegalArgumentException e) {
            // expected
        }
        IdentityHashMap empty = new IdentityHashMap(0);
        TestCase.assertNull("Empty IdentityHashMap access", empty.get("nothing"));
        empty.put("something", "here");
        TestCase.assertTrue("cannot get element", ((empty.get("something")) == "here"));
    }

    /**
     * java.util.IdentityHashMap#IdentityHashMap(java.util.Map)
     */
    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.IdentityHashMap(java.util.Map)
        Map myMap = new TreeMap();
        for (int counter = 0; counter < (IdentityHashMapTest.hmSize); counter++)
            myMap.put(objArray2[counter], objArray[counter]);

        IdentityHashMap hm2 = new IdentityHashMap(myMap);
        for (int counter = 0; counter < (IdentityHashMapTest.hmSize); counter++)
            TestCase.assertTrue("Failed to construct correct IdentityHashMap", ((hm.get(objArray2[counter])) == (hm2.get(objArray2[counter]))));

        Map mockMap = new IdentityHashMapTest.MockMap();
        hm2 = new IdentityHashMap(mockMap);
        TestCase.assertEquals("Size should be 0", 0, hm2.size());
        try {
            new IdentityHashMap(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.IdentityHashMap#clear()
     */
    public void test_clear() {
        // Test for method void java.util.IdentityHashMap.clear()
        hm.clear();
        TestCase.assertEquals("Clear failed to reset size", 0, hm.size());
        for (int i = 0; i < (IdentityHashMapTest.hmSize); i++)
            TestCase.assertNull("Failed to clear all elements", hm.get(objArray2[i]));

    }

    /**
     * java.util.IdentityHashMap#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.IdentityHashMap.clone()
        IdentityHashMap hm2 = ((IdentityHashMap) (hm.clone()));
        TestCase.assertTrue("Clone answered equivalent IdentityHashMap", (hm2 != (hm)));
        for (int counter = 0; counter < (IdentityHashMapTest.hmSize); counter++)
            TestCase.assertTrue("Clone answered unequal IdentityHashMap", ((hm.get(objArray2[counter])) == (hm2.get(objArray2[counter]))));

        IdentityHashMap map = new IdentityHashMap();
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
     * java.util.IdentityHashMap#containsKey(java.lang.Object)
     */
    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.IdentityHashMap.containsKey(java.lang.Object)
        TestCase.assertTrue("Returned false for valid key", hm.containsKey(objArray2[23]));
        TestCase.assertTrue("Returned true for copy of valid key", (!(hm.containsKey(new Integer(23).toString()))));
        TestCase.assertTrue("Returned true for invalid key", (!(hm.containsKey("KKDKDKD"))));
        IdentityHashMap m = new IdentityHashMap();
        m.put(null, "test");
        TestCase.assertTrue("Failed with null key", m.containsKey(null));
        TestCase.assertTrue("Failed with missing key matching null hash", (!(m.containsKey(new Integer(0)))));
    }

    /**
     * java.util.IdentityHashMap#containsValue(java.lang.Object)
     */
    public void test_containsValueLjava_lang_Object() {
        // Test for method boolean
        // java.util.IdentityHashMap.containsValue(java.lang.Object)
        TestCase.assertTrue("Returned false for valid value", hm.containsValue(objArray[19]));
        TestCase.assertTrue("Returned true for invalid valie", (!(hm.containsValue(new Integer((-9))))));
    }

    /**
     * java.util.IdentityHashMap#entrySet()
     */
    public void test_entrySet() {
        // Test for method java.util.Set java.util.IdentityHashMap.entrySet()
        Set s = hm.entrySet();
        Iterator i = s.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((hm.size()) == (s.size())));
        while (i.hasNext()) {
            Map.Entry m = ((Map.Entry) (i.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((hm.containsKey(m.getKey())) && (hm.containsValue(m.getValue()))));
        } 
    }

    /**
     * java.util.IdentityHashMap#get(java.lang.Object)
     */
    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.IdentityHashMap.get(java.lang.Object)
        TestCase.assertNull("Get returned non-null for non existent key", hm.get("T"));
        hm.put("T", "HELLO");
        TestCase.assertEquals("Get returned incorecct value for existing key", "HELLO", hm.get("T"));
        IdentityHashMap m = new IdentityHashMap();
        m.put(null, "test");
        TestCase.assertEquals("Failed with null key", "test", m.get(null));
        TestCase.assertNull("Failed with missing key matching null hash", m.get(new Integer(0)));
    }

    /**
     * java.util.IdentityHashMap#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.IdentityHashMap.isEmpty()
        TestCase.assertTrue("Returned false for new map", new IdentityHashMap().isEmpty());
        TestCase.assertTrue("Returned true for non-empty", (!(hm.isEmpty())));
    }

    /**
     * java.util.IdentityHashMap#keySet()
     */
    public void test_keySet() {
        // Test for method java.util.Set java.util.IdentityHashMap.keySet()
        Set s = hm.keySet();
        TestCase.assertTrue("Returned set of incorrect size()", ((s.size()) == (hm.size())));
        for (int i = 0; i < (objArray.length); i++) {
            TestCase.assertTrue("Returned set does not contain all keys", s.contains(objArray2[i]));
        }
        IdentityHashMap m = new IdentityHashMap();
        m.put(null, "test");
        TestCase.assertTrue("Failed with null key", m.keySet().contains(null));
        TestCase.assertNull("Failed with null key", m.keySet().iterator().next());
        Map map = new IdentityHashMap(101);
        map.put(new Integer(1), "1");
        map.put(new Integer(102), "102");
        map.put(new Integer(203), "203");
        Iterator it = map.keySet().iterator();
        Integer remove1 = ((Integer) (it.next()));
        it.hasNext();
        it.remove();
        Integer remove2 = ((Integer) (it.next()));
        it.remove();
        ArrayList list = new ArrayList(Arrays.asList(new Integer[]{ new Integer(1), new Integer(102), new Integer(203) }));
        list.remove(remove1);
        list.remove(remove2);
        TestCase.assertTrue("Wrong result", it.next().equals(list.get(0)));
        TestCase.assertEquals("Wrong size", 1, map.size());
        TestCase.assertTrue("Wrong contents", map.keySet().iterator().next().equals(list.get(0)));
        Map map2 = new IdentityHashMap(101);
        map2.put(new Integer(1), "1");
        map2.put(new Integer(4), "4");
        Iterator it2 = map2.keySet().iterator();
        Integer remove3 = ((Integer) (it2.next()));
        Integer next;
        if ((remove3.intValue()) == 1)
            next = new Integer(4);
        else
            next = new Integer(1);

        it2.hasNext();
        it2.remove();
        TestCase.assertTrue("Wrong result 2", it2.next().equals(next));
        TestCase.assertEquals("Wrong size 2", 1, map2.size());
        TestCase.assertTrue("Wrong contents 2", map2.keySet().iterator().next().equals(next));
    }

    /**
     * java.util.IdentityHashMap#put(java.lang.Object, java.lang.Object)
     */
    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.IdentityHashMap.put(java.lang.Object, java.lang.Object)
        hm.put("KEY", "VALUE");
        TestCase.assertEquals("Failed to install key/value pair", "VALUE", hm.get("KEY"));
        IdentityHashMap m = new IdentityHashMap();
        Short s0 = new Short(((short) (0)));
        m.put(s0, "short");
        m.put(null, "test");
        Integer i0 = new Integer(0);
        m.put(i0, "int");
        TestCase.assertEquals("Failed adding to bucket containing null", "short", m.get(s0));
        TestCase.assertEquals("Failed adding to bucket containing null2", "int", m.get(i0));
    }

    /**
     * java.util.IdentityHashMap#putAll(java.util.Map)
     */
    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.IdentityHashMap.putAll(java.util.Map)
        IdentityHashMap hm2 = new IdentityHashMap();
        hm2.putAll(hm);
        for (int i = 0; i < 1000; i++)
            TestCase.assertTrue("Failed to clear all elements", hm2.get(objArray2[i]).equals(new Integer(i)));

        hm2 = new IdentityHashMap();
        Map mockMap = new IdentityHashMapTest.MockMap();
        hm2.putAll(mockMap);
        TestCase.assertEquals("Size should be 0", 0, hm2.size());
        try {
            hm2.putAll(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.IdentityHashMap#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.IdentityHashMap.remove(java.lang.Object)
        int size = hm.size();
        Integer x = ((Integer) (hm.remove(objArray2[9])));
        TestCase.assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
        TestCase.assertNull("Failed to remove given key", hm.get(objArray2[9]));
        TestCase.assertTrue("Failed to decrement size", ((hm.size()) == (size - 1)));
        TestCase.assertNull("Remove of non-existent key returned non-null", hm.remove("LCLCLC"));
        IdentityHashMap m = new IdentityHashMap();
        m.put(null, "test");
        TestCase.assertNull("Failed with same hash as null", m.remove(objArray[0]));
        TestCase.assertEquals("Failed with null key", "test", m.remove(null));
    }

    /**
     * java.util.IdentityHashMap#size()
     */
    public void test_size() {
        // Test for method int java.util.IdentityHashMap.size()
        TestCase.assertEquals("Returned incorrect size, ", ((objArray.length) + 2), hm.size());
    }

    /**
     * java.util.IdentityHashMap#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        IdentityHashMap mapOne = new IdentityHashMap();
        IdentityHashMap mapTwo = new IdentityHashMap();
        IdentityHashMap mapThree = new IdentityHashMap();
        IdentityHashMap mapFour = new IdentityHashMap();
        String one = "one";
        String alsoOne = new String(one);// use the new operator to ensure a

        // new reference is constructed
        String two = "two";
        String alsoTwo = new String(two);// use the new operator to ensure a

        // new reference is constructed
        mapOne.put(one, two);
        mapFour.put(one, two);
        // these two are not equal to the above two
        mapTwo.put(alsoOne, two);
        mapThree.put(one, alsoTwo);
        TestCase.assertEquals("failure of equality of IdentityHashMaps", mapOne, mapFour);
        TestCase.assertTrue("failure of non-equality of IdentityHashMaps one and two", (!(mapOne.equals(mapTwo))));
        TestCase.assertTrue("failure of non-equality of IdentityHashMaps one and three", (!(mapOne.equals(mapThree))));
        TestCase.assertTrue("failure of non-equality of IdentityHashMaps two and three", (!(mapTwo.equals(mapThree))));
        HashMap hashMapTwo = new HashMap();
        HashMap hashMapThree = new HashMap();
        hashMapTwo.put(alsoOne, two);
        hashMapThree.put(one, alsoTwo);
        TestCase.assertTrue("failure of non-equality of IdentityHashMaps one and Hashmap two", (!(mapOne.equals(hashMapTwo))));
        TestCase.assertTrue("failure of non-equality of IdentityHashMaps one and Hashmap three", (!(mapOne.equals(hashMapThree))));
    }

    /**
     * java.util.IdentityHashMap#values()
     */
    public void test_values() {
        // Test for method java.util.Collection
        // java.util.IdentityHashMap.values()
        Collection c = hm.values();
        TestCase.assertTrue("Returned collection of incorrect size()", ((c.size()) == (hm.size())));
        for (int i = 0; i < (objArray.length); i++)
            TestCase.assertTrue("Returned collection does not contain all keys", c.contains(objArray[i]));

        IdentityHashMap myIdentityHashMap = new IdentityHashMap();
        for (int i = 0; i < 100; i++)
            myIdentityHashMap.put(objArray2[i], objArray[i]);

        Collection values = myIdentityHashMap.values();
        values.remove(objArray[0]);
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myIdentityHashMap.containsValue(objArray2[0]))));
    }

    /**
     * java.util.IdentityHashMap#Serialization()
     */
    public void test_Serialization() throws Exception {
        IdentityHashMap<String, String> map = new IdentityHashMap<String, String>();
        map.put(IdentityHashMapTest.ID, "world");
        // BEGIN android-added
        // Regression test for null key in serialized IdentityHashMap (1178549)
        // Together with this change the IdentityHashMap.golden.ser resource
        // was replaced by a version that contains a map with a null key.
        map.put(null, "null");
        // END android-added
        SerializationTest.verifySelf(map, IdentityHashMapTest.comparator);
        SerializationTest.verifyGolden(this, map, IdentityHashMapTest.comparator);
    }

    private static final SerializableAssert comparator = new SerializationTest.SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            IdentityHashMap<String, String> initialMap = ((IdentityHashMap<String, String>) (initial));
            IdentityHashMap<String, String> deseriaMap = ((IdentityHashMap<String, String>) (deserialized));
            TestCase.assertEquals("should be equal", initialMap.size(), deseriaMap.size());
        }
    };
}

