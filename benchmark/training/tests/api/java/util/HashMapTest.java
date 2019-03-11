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


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import junit.framework.TestCase;
import tests.support.Support_MapTest2;
import tests.support.Support_UnmodifiableCollectionTest;


public class HashMapTest extends TestCase {
    class MockMap extends AbstractMap {
        public Set entrySet() {
            return Collections.EMPTY_SET;
        }

        public int size() {
            return 0;
        }
    }

    private static class MockMapNull extends AbstractMap {
        public Set entrySet() {
            return null;
        }

        public int size() {
            return 10;
        }
    }

    HashMap hm;

    static final int hmSize = 1000;

    Object[] objArray;

    Object[] objArray2;

    /**
     * java.util.HashMap#HashMap()
     */
    public void test_Constructor() {
        // Test for method java.util.HashMap()
        new Support_MapTest2(new HashMap()).runTest();
        HashMap hm2 = new HashMap();
        TestCase.assertEquals("Created incorrect HashMap", 0, hm2.size());
    }

    /**
     * java.util.HashMap#HashMap(int)
     */
    public void test_ConstructorI() {
        // Test for method java.util.HashMap(int)
        HashMap hm2 = new HashMap(5);
        TestCase.assertEquals("Created incorrect HashMap", 0, hm2.size());
        try {
            new HashMap((-1));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        HashMap empty = new HashMap(0);
        TestCase.assertNull("Empty hashmap access", empty.get("nothing"));
        empty.put("something", "here");
        TestCase.assertTrue("cannot get element", ((empty.get("something")) == "here"));
    }

    /**
     * java.util.HashMap#HashMap(int, float)
     */
    public void test_ConstructorIF() {
        // Test for method java.util.HashMap(int, float)
        HashMap hm2 = new HashMap(5, ((float) (0.5)));
        TestCase.assertEquals("Created incorrect HashMap", 0, hm2.size());
        try {
            new HashMap(0, 0);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        HashMap empty = new HashMap(0, 0.75F);
        TestCase.assertNull("Empty hashtable access", empty.get("nothing"));
        empty.put("something", "here");
        TestCase.assertTrue("cannot get element", ((empty.get("something")) == "here"));
    }

    /**
     * java.util.HashMap#HashMap(java.util.Map)
     */
    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.HashMap(java.util.Map)
        Map myMap = new TreeMap();
        for (int counter = 0; counter < (HashMapTest.hmSize); counter++)
            myMap.put(objArray2[counter], objArray[counter]);

        HashMap hm2 = new HashMap(myMap);
        for (int counter = 0; counter < (HashMapTest.hmSize); counter++)
            TestCase.assertTrue("Failed to construct correct HashMap", ((hm.get(objArray2[counter])) == (hm2.get(objArray2[counter]))));

        Map mockMap = new HashMapTest.MockMap();
        hm = new HashMap(mockMap);
        TestCase.assertEquals(hm, mockMap);
    }

    /**
     * java.util.HashMap#clear()
     */
    public void test_clear() {
        // Test for method void java.util.HashMap.clear()
        hm.clear();
        TestCase.assertEquals("Clear failed to reset size", 0, hm.size());
        for (int i = 0; i < (HashMapTest.hmSize); i++)
            TestCase.assertNull("Failed to clear all elements", hm.get(objArray2[i]));

    }

    /**
     * java.util.HashMap#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.HashMap.clone()
        HashMap hm2 = ((HashMap) (hm.clone()));
        TestCase.assertTrue("Clone answered equivalent HashMap", (hm2 != (hm)));
        for (int counter = 0; counter < (HashMapTest.hmSize); counter++)
            TestCase.assertTrue("Clone answered unequal HashMap", ((hm.get(objArray2[counter])) == (hm2.get(objArray2[counter]))));

        HashMap map = new HashMap();
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
        // regresion test for HARMONY-4603
        HashMap hashmap = new HashMap();
        HashMapTest.MockClonable mock = new HashMapTest.MockClonable(1);
        hashmap.put(1, mock);
        TestCase.assertEquals(1, ((HashMapTest.MockClonable) (hashmap.get(1))).i);
        HashMap hm3 = ((HashMap) (hashmap.clone()));
        TestCase.assertEquals(1, ((HashMapTest.MockClonable) (hm3.get(1))).i);
        mock.i = 0;
        TestCase.assertEquals(0, ((HashMapTest.MockClonable) (hashmap.get(1))).i);
        TestCase.assertEquals(0, ((HashMapTest.MockClonable) (hm3.get(1))).i);
    }

    /**
     * java.util.HashMap#containsKey(java.lang.Object)
     */
    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.HashMap.containsKey(java.lang.Object)
        TestCase.assertTrue("Returned false for valid key", hm.containsKey(new Integer(876).toString()));
        TestCase.assertTrue("Returned true for invalid key", (!(hm.containsKey("KKDKDKD"))));
        HashMap m = new HashMap();
        m.put(null, "test");
        TestCase.assertTrue("Failed with null key", m.containsKey(null));
        TestCase.assertTrue("Failed with missing key matching null hash", (!(m.containsKey(new Integer(0)))));
    }

    /**
     * java.util.HashMap#containsValue(java.lang.Object)
     */
    public void test_containsValueLjava_lang_Object() {
        // Test for method boolean
        // java.util.HashMap.containsValue(java.lang.Object)
        TestCase.assertTrue("Returned false for valid value", hm.containsValue(new Integer(875)));
        TestCase.assertTrue("Returned true for invalid valie", (!(hm.containsValue(new Integer((-9))))));
    }

    /**
     * java.util.HashMap#entrySet()
     */
    public void test_entrySet() {
        // Test for method java.util.Set java.util.HashMap.entrySet()
        Set s = hm.entrySet();
        Iterator i = s.iterator();
        TestCase.assertTrue("Returned set of incorrect size", ((hm.size()) == (s.size())));
        while (i.hasNext()) {
            Map.Entry m = ((Map.Entry) (i.next()));
            TestCase.assertTrue("Returned incorrect entry set", ((hm.containsKey(m.getKey())) && (hm.containsValue(m.getValue()))));
        } 
    }

    /**
     * java.util.HashMap#entrySet()
     */
    public void test_entrySetEquals() {
        Set s1 = hm.entrySet();
        Set s2 = new HashMap(hm).entrySet();
        TestCase.assertEquals(s1, s2);
    }

    /**
     * java.util.HashMap#entrySet()
     */
    public void test_removeFromViews() {
        hm.put("A", null);
        hm.put("B", null);
        TestCase.assertTrue(hm.keySet().remove("A"));
        Map<String, String> m2 = new HashMap<String, String>();
        m2.put("B", null);
        TestCase.assertTrue(hm.entrySet().remove(m2.entrySet().iterator().next()));
    }

    /**
     * java.util.HashMap#get(java.lang.Object)
     */
    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.HashMap.get(java.lang.Object)
        TestCase.assertNull("Get returned non-null for non existent key", hm.get("T"));
        hm.put("T", "HELLO");
        TestCase.assertEquals("Get returned incorrect value for existing key", "HELLO", hm.get("T"));
        HashMap m = new HashMap();
        m.put(null, "test");
        TestCase.assertEquals("Failed with null key", "test", m.get(null));
        TestCase.assertNull("Failed with missing key matching null hash", m.get(new Integer(0)));
        // Regression for HARMONY-206
        HashMapTest.ReusableKey k = new HashMapTest.ReusableKey();
        HashMap map = new HashMap();
        k.setKey(1);
        map.put(k, "value1");
        k.setKey(18);
        TestCase.assertNull(map.get(k));
        k.setKey(17);
        TestCase.assertNull(map.get(k));
    }

    /**
     * java.util.HashMap#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.HashMap.isEmpty()
        TestCase.assertTrue("Returned false for new map", new HashMap().isEmpty());
        TestCase.assertTrue("Returned true for non-empty", (!(hm.isEmpty())));
    }

    /**
     * java.util.HashMap#keySet()
     */
    public void test_keySet() {
        // Test for method java.util.Set java.util.HashMap.keySet()
        Set s = hm.keySet();
        TestCase.assertTrue("Returned set of incorrect size()", ((s.size()) == (hm.size())));
        for (int i = 0; i < (objArray.length); i++)
            TestCase.assertTrue("Returned set does not contain all keys", s.contains(objArray[i].toString()));

        HashMap m = new HashMap();
        m.put(null, "test");
        TestCase.assertTrue("Failed with null key", m.keySet().contains(null));
        TestCase.assertNull("Failed with null key", m.keySet().iterator().next());
        Map map = new HashMap(101);
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
        Map map2 = new HashMap(101);
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
     * java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.HashMap.put(java.lang.Object, java.lang.Object)
        hm.put("KEY", "VALUE");
        TestCase.assertEquals("Failed to install key/value pair", "VALUE", hm.get("KEY"));
        HashMap m = new HashMap();
        m.put(new Short(((short) (0))), "short");
        m.put(null, "test");
        m.put(new Integer(0), "int");
        TestCase.assertEquals("Failed adding to bucket containing null", "short", m.get(new Short(((short) (0)))));
        TestCase.assertEquals("Failed adding to bucket containing null2", "int", m.get(new Integer(0)));
    }

    /**
     * java.util.HashMap#putAll(java.util.Map)
     */
    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.HashMap.putAll(java.util.Map)
        HashMap hm2 = new HashMap();
        hm2.putAll(hm);
        for (int i = 0; i < 1000; i++)
            TestCase.assertTrue("Failed to clear all elements", hm2.get(new Integer(i).toString()).equals(new Integer(i)));

        Map mockMap = new HashMapTest.MockMap();
        hm2 = new HashMap();
        hm2.putAll(mockMap);
        TestCase.assertEquals("Size should be 0", 0, hm2.size());
    }

    /**
     * java.util.HashMap#putAll(java.util.Map)
     */
    public void test_putAllLjava_util_Map_Null() {
        HashMap hashMap = new HashMap();
        try {
            hashMap.putAll(new HashMapTest.MockMapNull());
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }
        try {
            hashMap = new HashMap(new HashMapTest.MockMapNull());
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }
    }

    public void test_putAllLjava_util_Map_Resize() {
        Random rnd = new Random(666);
        Map<Integer, Integer> m1 = new HashMap<Integer, Integer>();
        int MID = 10000;
        for (int i = 0; i < MID; i++) {
            Integer j = rnd.nextInt();
            m1.put(j, j);
        }
        Map<Integer, Integer> m2 = new HashMap<Integer, Integer>();
        int HI = 30000;
        for (int i = MID; i < HI; i++) {
            Integer j = rnd.nextInt();
            m2.put(j, j);
        }
        m1.putAll(m2);
        rnd = new Random(666);
        for (int i = 0; i < HI; i++) {
            Integer j = rnd.nextInt();
            TestCase.assertEquals(j, m1.get(j));
        }
    }

    /**
     * java.util.HashMap#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.HashMap.remove(java.lang.Object)
        int size = hm.size();
        Integer y = new Integer(9);
        Integer x = ((Integer) (hm.remove(y.toString())));
        TestCase.assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
        TestCase.assertNull("Failed to remove given key", hm.get(new Integer(9)));
        TestCase.assertTrue("Failed to decrement size", ((hm.size()) == (size - 1)));
        TestCase.assertNull("Remove of non-existent key returned non-null", hm.remove("LCLCLC"));
        HashMap m = new HashMap();
        m.put(null, "test");
        TestCase.assertNull("Failed with same hash as null", m.remove(new Integer(0)));
        TestCase.assertEquals("Failed with null key", "test", m.remove(null));
    }

    /**
     * java.util.HashMap#size()
     */
    public void test_size() {
        // Test for method int java.util.HashMap.size()
        TestCase.assertTrue("Returned incorrect size", ((hm.size()) == ((objArray.length) + 2)));
    }

    /**
     * java.util.HashMap#values()
     */
    public void test_values() {
        // Test for method java.util.Collection java.util.HashMap.values()
        Collection c = hm.values();
        TestCase.assertTrue("Returned collection of incorrect size()", ((c.size()) == (hm.size())));
        for (int i = 0; i < (objArray.length); i++)
            TestCase.assertTrue("Returned collection does not contain all keys", c.contains(objArray[i]));

        HashMap myHashMap = new HashMap();
        for (int i = 0; i < 100; i++)
            myHashMap.put(objArray2[i], objArray[i]);

        Collection values = myHashMap.values();
        new Support_UnmodifiableCollectionTest("Test Returned Collection From HashMap.values()", values).runTest();
        values.remove(new Integer(0));
        TestCase.assertTrue("Removing from the values collection should remove from the original map", (!(myHashMap.containsValue(new Integer(0)))));
    }

    static class ReusableKey {
        private int key = 0;

        public void setKey(int key) {
            this.key = key;
        }

        public int hashCode() {
            return key;
        }

        public boolean equals(Object o) {
            if (o == (this)) {
                return true;
            }
            if (!(o instanceof HashMapTest.ReusableKey)) {
                return false;
            }
            return (key) == (((HashMapTest.ReusableKey) (o)).key);
        }
    }

    public void test_Map_Entry_hashCode() {
        // Related to HARMONY-403
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(10);
        Integer key = new Integer(1);
        Integer val = new Integer(2);
        map.put(key, val);
        int expected = (key.hashCode()) ^ (val.hashCode());
        TestCase.assertEquals(expected, map.hashCode());
        key = new Integer(4);
        val = new Integer(8);
        map.put(key, val);
        expected += (key.hashCode()) ^ (val.hashCode());
        TestCase.assertEquals(expected, map.hashCode());
    }

    class MockClonable implements Cloneable {
        public int i;

        public MockClonable(int i) {
            this.i = i;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new HashMapTest.MockClonable(i);
        }
    }
}

