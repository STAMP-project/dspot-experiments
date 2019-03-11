/**
 * Copyright (C) 2012 Google Inc.
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
package com.google.gson.internal;


import com.google.gson.common.MoreAsserts;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import junit.framework.TestCase;


public final class LinkedHashTreeMapTest extends TestCase {
    public void testIterationOrder() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        map.put("a", "android");
        map.put("c", "cola");
        map.put("b", "bbq");
        assertIterationOrder(map.keySet(), "a", "c", "b");
        assertIterationOrder(map.values(), "android", "cola", "bbq");
    }

    public void testRemoveRootDoesNotDoubleUnlink() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        map.put("a", "android");
        map.put("c", "cola");
        map.put("b", "bbq");
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        it.next();
        it.next();
        it.next();
        it.remove();
        assertIterationOrder(map.keySet(), "a", "c");
    }

    public void testPutNullKeyFails() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        try {
            map.put(null, "android");
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testPutNonComparableKeyFails() {
        LinkedHashTreeMap<Object, String> map = new LinkedHashTreeMap<Object, String>();
        try {
            map.put(new Object(), "android");
            TestCase.fail();
        } catch (ClassCastException expected) {
        }
    }

    public void testContainsNonComparableKeyReturnsFalse() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        map.put("a", "android");
        TestCase.assertFalse(map.containsKey(new Object()));
    }

    public void testContainsNullKeyIsAlwaysFalse() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        map.put("a", "android");
        TestCase.assertFalse(map.containsKey(null));
    }

    public void testPutOverrides() throws Exception {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        TestCase.assertNull(map.put("d", "donut"));
        TestCase.assertNull(map.put("e", "eclair"));
        TestCase.assertNull(map.put("f", "froyo"));
        TestCase.assertEquals(3, map.size());
        TestCase.assertEquals("donut", map.get("d"));
        TestCase.assertEquals("donut", map.put("d", "done"));
        TestCase.assertEquals(3, map.size());
    }

    public void testEmptyStringValues() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        map.put("a", "");
        TestCase.assertTrue(map.containsKey("a"));
        TestCase.assertEquals("", map.get("a"));
    }

    // NOTE that this does not happen every time, but given the below predictable random,
    // this test will consistently fail (assuming the initial size is 16 and rehashing
    // size remains at 3/4)
    public void testForceDoublingAndRehash() throws Exception {
        Random random = new Random(1367593214724L);
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        String[] keys = new String[1000];
        for (int i = 0; i < (keys.length); i++) {
            keys[i] = ((Integer.toString(Math.abs(random.nextInt()), 36)) + "-") + i;
            map.put(keys[i], ("" + i));
        }
        for (int i = 0; i < (keys.length); i++) {
            String key = keys[i];
            TestCase.assertTrue(map.containsKey(key));
            TestCase.assertEquals(("" + i), map.get(key));
        }
    }

    public void testClear() {
        LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
        map.put("a", "android");
        map.put("c", "cola");
        map.put("b", "bbq");
        map.clear();
        assertIterationOrder(map.keySet());
        TestCase.assertEquals(0, map.size());
    }

    public void testEqualsAndHashCode() throws Exception {
        LinkedHashTreeMap<String, Integer> map1 = new LinkedHashTreeMap<String, Integer>();
        map1.put("A", 1);
        map1.put("B", 2);
        map1.put("C", 3);
        map1.put("D", 4);
        LinkedHashTreeMap<String, Integer> map2 = new LinkedHashTreeMap<String, Integer>();
        map2.put("C", 3);
        map2.put("B", 2);
        map2.put("D", 4);
        map2.put("A", 1);
        MoreAsserts.assertEqualsAndHashCode(map1, map2);
    }

    public void testAvlWalker() {
        assertAvlWalker(node(node("a"), "b", node("c")), "a", "b", "c");
        assertAvlWalker(node(node(node("a"), "b", node("c")), "d", node(node("e"), "f", node("g"))), "a", "b", "c", "d", "e", "f", "g");
        assertAvlWalker(node(node(null, "a", node("b")), "c", node(node("d"), "e", null)), "a", "b", "c", "d", "e");
        assertAvlWalker(node(null, "a", node(null, "b", node(null, "c", node("d")))), "a", "b", "c", "d");
        assertAvlWalker(node(node(node(node("a"), "b", null), "c", null), "d", null), "a", "b", "c", "d");
    }

    public void testAvlBuilder() {
        assertAvlBuilder(1, "a");
        assertAvlBuilder(2, "(. a b)");
        assertAvlBuilder(3, "(a b c)");
        assertAvlBuilder(4, "(a b (. c d))");
        assertAvlBuilder(5, "(a b (c d e))");
        assertAvlBuilder(6, "((. a b) c (d e f))");
        assertAvlBuilder(7, "((a b c) d (e f g))");
        assertAvlBuilder(8, "((a b c) d (e f (. g h)))");
        assertAvlBuilder(9, "((a b c) d (e f (g h i)))");
        assertAvlBuilder(10, "((a b c) d ((. e f) g (h i j)))");
        assertAvlBuilder(11, "((a b c) d ((e f g) h (i j k)))");
        assertAvlBuilder(12, "((a b (. c d)) e ((f g h) i (j k l)))");
        assertAvlBuilder(13, "((a b (c d e)) f ((g h i) j (k l m)))");
        assertAvlBuilder(14, "(((. a b) c (d e f)) g ((h i j) k (l m n)))");
        assertAvlBuilder(15, "(((a b c) d (e f g)) h ((i j k) l (m n o)))");
        assertAvlBuilder(16, "(((a b c) d (e f g)) h ((i j k) l (m n (. o p))))");
        assertAvlBuilder(30, ("((((. a b) c (d e f)) g ((h i j) k (l m n))) o " + "(((p q r) s (t u v)) w ((x y z) A (B C D))))"));
        assertAvlBuilder(31, ("((((a b c) d (e f g)) h ((i j k) l (m n o))) p " + "(((q r s) t (u v w)) x ((y z A) B (C D E))))"));
    }

    public void testDoubleCapacity() {
        // Arrays and generics don't get along.
        @SuppressWarnings("unchecked")
        LinkedHashTreeMap.Node<String, String>[] oldTable = new LinkedHashTreeMap.Node[1];
        oldTable[0] = node(node(node("a"), "b", node("c")), "d", node(node("e"), "f", node("g")));
        LinkedHashTreeMap.Node<String, String>[] newTable = LinkedHashTreeMap.doubleCapacity(oldTable);
        assertTree("(b d f)", newTable[0]);// Even hash codes!

        assertTree("(a c (. e g))", newTable[1]);// Odd hash codes!

    }

    public void testDoubleCapacityAllNodesOnLeft() {
        // Arrays and generics don't get along.
        @SuppressWarnings("unchecked")
        LinkedHashTreeMap.Node<String, String>[] oldTable = new LinkedHashTreeMap.Node[1];
        oldTable[0] = node(node("b"), "d", node("f"));
        LinkedHashTreeMap.Node<String, String>[] newTable = LinkedHashTreeMap.doubleCapacity(oldTable);
        assertTree("(b d f)", newTable[0]);// Even hash codes!

        TestCase.assertNull(newTable[1]);// Odd hash codes!

        for (LinkedHashTreeMap.Node<?, ?> node : newTable) {
            if (node != null) {
                assertConsistent(node);
            }
        }
    }

    private static final LinkedHashTreeMap.Node<String, String> head = new LinkedHashTreeMap.Node<String, String>();
}

