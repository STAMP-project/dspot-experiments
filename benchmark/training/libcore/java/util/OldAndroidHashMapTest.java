/**
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util;


import java.util.HashMap;
import junit.framework.TestCase;


public final class OldAndroidHashMapTest extends TestCase {
    private static final Integer ONE = 1;

    private static final Integer TWO = 2;

    private static final Integer THREE = 3;

    private static final Integer FOUR = 4;

    public void testAdd() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        addItems(map);
    }

    public void testClear() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        addItems(map);
        map.clear();
        TestCase.assertEquals(0, map.size());
    }

    public void testRemove() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        addItems(map);
        map.remove("three");
        TestCase.assertNull(map.get("three"));
    }

    public void testManipulate() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        TestCase.assertTrue(map.isEmpty());
        TestCase.assertEquals(0, map.size());
        TestCase.assertNull(map.get(null));
        TestCase.assertNull(map.get("one"));
        TestCase.assertFalse(map.containsKey("one"));
        TestCase.assertFalse(map.containsValue(new Integer(1)));
        TestCase.assertNull(map.remove(null));
        TestCase.assertNull(map.remove("one"));
        TestCase.assertNull(map.put(null, (-1)));
        TestCase.assertNull(map.put("one", 1));
        TestCase.assertNull(map.put("two", 2));
        TestCase.assertNull(map.put("three", 3));
        TestCase.assertEquals((-1), map.put(null, 0).intValue());
        TestCase.assertEquals(0, map.get(null).intValue());
        TestCase.assertEquals(1, map.get("one").intValue());
        TestCase.assertEquals(2, map.get("two").intValue());
        TestCase.assertEquals(3, map.get("three").intValue());
        TestCase.assertTrue(map.containsKey(null));
        TestCase.assertTrue(map.containsKey("one"));
        TestCase.assertTrue(map.containsKey("two"));
        TestCase.assertTrue(map.containsKey("three"));
        TestCase.assertTrue(map.containsValue(new Integer(0)));
        TestCase.assertTrue(map.containsValue(new Integer(1)));
        TestCase.assertTrue(map.containsValue(new Integer(2)));
        TestCase.assertTrue(map.containsValue(new Integer(3)));
        TestCase.assertEquals(0, map.remove(null).intValue());
        TestCase.assertEquals(1, map.remove("one").intValue());
        TestCase.assertEquals(2, map.remove("two").intValue());
        TestCase.assertEquals(3, map.remove("three").intValue());
        TestCase.assertTrue(map.isEmpty());
        TestCase.assertEquals(0, map.size());
        TestCase.assertNull(map.get(null));
        TestCase.assertNull(map.get("one"));
        TestCase.assertFalse(map.containsKey("one"));
        TestCase.assertFalse(map.containsValue(new Integer(1)));
        TestCase.assertNull(map.remove(null));
        TestCase.assertNull(map.remove("one"));
    }

    public void testKeyIterator() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        boolean[] slots = new boolean[4];
        addItems(map);
        for (String s : map.keySet()) {
            int slot = 0;
            if (s.equals("one")) {
                slot = 0;
            } else
                if (s.equals("two")) {
                    slot = 1;
                } else
                    if (s.equals("three")) {
                        slot = 2;
                    } else
                        if (s.equals("four")) {
                            slot = 3;
                        } else {
                            TestCase.fail("Unknown key in HashMap");
                        }



            if (slots[slot]) {
                TestCase.fail("key returned more than once");
            } else {
                slots[slot] = true;
            }
        }
        TestCase.assertTrue(slots[0]);
        TestCase.assertTrue(slots[1]);
        TestCase.assertTrue(slots[2]);
        TestCase.assertTrue(slots[3]);
    }

    public void testValueIterator() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        boolean[] slots = new boolean[4];
        addItems(map);
        for (Object o : map.values()) {
            int slot = 0;
            if (o.equals(OldAndroidHashMapTest.ONE)) {
                slot = 0;
            } else
                if (o.equals(OldAndroidHashMapTest.TWO)) {
                    slot = 1;
                } else
                    if (o.equals(OldAndroidHashMapTest.THREE)) {
                        slot = 2;
                    } else
                        if (o.equals(OldAndroidHashMapTest.FOUR)) {
                            slot = 3;
                        } else {
                            TestCase.fail("Unknown value in HashMap");
                        }



            if (slots[slot]) {
                TestCase.fail("value returned more than once");
            } else {
                slots[slot] = true;
            }
        }
        TestCase.assertTrue(slots[0]);
        TestCase.assertTrue(slots[1]);
        TestCase.assertTrue(slots[2]);
        TestCase.assertTrue(slots[3]);
    }

    public void testEntryIterator() throws Exception {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        boolean[] slots = new boolean[4];
        addItems(map);
        for (Object o : map.entrySet()) {
            int slot = 0;
            if (o.toString().equals("one=1")) {
                slot = 0;
            } else
                if (o.toString().equals("two=2")) {
                    slot = 1;
                } else
                    if (o.toString().equals("three=3")) {
                        slot = 2;
                    } else
                        if (o.toString().equals("four=4")) {
                            slot = 3;
                        } else {
                            TestCase.fail("Unknown entry in HashMap");
                        }



            if (slots[slot]) {
                TestCase.fail("entry returned more than once");
            } else {
                slots[slot] = true;
            }
        }
        TestCase.assertTrue(slots[0]);
        TestCase.assertTrue(slots[1]);
        TestCase.assertTrue(slots[2]);
        TestCase.assertTrue(slots[3]);
    }

    public void testEquals() throws Exception {
        HashMap<String, String> map1 = new HashMap<String, String>();
        HashMap<String, String> map2 = new HashMap<String, String>();
        HashMap<String, String> map3 = new HashMap<String, String>();
        map1.put("one", "1");
        map1.put("two", "2");
        map1.put("three", "3");
        map2.put("one", "1");
        map2.put("two", "2");
        map2.put("three", "3");
        TestCase.assertTrue(map1.equals(map2));
        map3.put("one", "1");
        map3.put("two", "1");
        map3.put("three", "1");
        TestCase.assertFalse(map1.equals(map3));
        TestCase.assertFalse(map2.equals(map3));
    }
}

