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


import SerializationTest.SerializableAssert;
import com.google.j2objc.util.ReflectionUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import junit.framework.TestCase;
import libcore.java.util.SpliteratorTester;
import org.apache.harmony.testframework.serialization.SerializationTest;


public class HashSetTest extends TestCase {
    HashSet hs;

    Object[] objArray;

    /**
     * java.util.HashSet#HashSet()
     */
    public void test_Constructor() {
        // Test for method java.util.HashSet()
        HashSet hs2 = new HashSet();
        TestCase.assertEquals("Created incorrect HashSet", 0, hs2.size());
    }

    /**
     * java.util.HashSet#HashSet(int)
     */
    public void test_ConstructorI() {
        // Test for method java.util.HashSet(int)
        HashSet hs2 = new HashSet(5);
        TestCase.assertEquals("Created incorrect HashSet", 0, hs2.size());
        try {
            new HashSet((-1));
        } catch (IllegalArgumentException e) {
            return;
        }
        TestCase.fail("Failed to throw IllegalArgumentException for capacity < 0");
    }

    /**
     * java.util.HashSet#HashSet(int, float)
     */
    public void test_ConstructorIF() {
        // Test for method java.util.HashSet(int, float)
        HashSet hs2 = new HashSet(5, ((float) (0.5)));
        TestCase.assertEquals("Created incorrect HashSet", 0, hs2.size());
        try {
            new HashSet(0, 0);
        } catch (IllegalArgumentException e) {
            return;
        }
        TestCase.fail("Failed to throw IllegalArgumentException for initial load factor <= 0");
    }

    /**
     * java.util.HashSet#HashSet(java.util.Collection)
     */
    public void test_ConstructorLjava_util_Collection() {
        // Test for method java.util.HashSet(java.util.Collection)
        HashSet hs2 = new HashSet(Arrays.asList(objArray));
        for (int counter = 0; counter < (objArray.length); counter++)
            TestCase.assertTrue("HashSet does not contain correct elements", hs.contains(objArray[counter]));

        TestCase.assertTrue("HashSet created from collection incorrect size", ((hs2.size()) == (objArray.length)));
        try {
            new HashSet(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.HashSet#add(java.lang.Object)
     */
    public void test_addLjava_lang_Object() {
        // Test for method boolean java.util.HashSet.add(java.lang.Object)
        int size = hs.size();
        hs.add(new Integer(8));
        TestCase.assertTrue("Added element already contained by set", ((hs.size()) == size));
        hs.add(new Integer((-9)));
        TestCase.assertTrue("Failed to increment set size after add", ((hs.size()) == (size + 1)));
        TestCase.assertTrue("Failed to add element to set", hs.contains(new Integer((-9))));
    }

    /**
     * java.util.HashSet#clear()
     */
    public void test_clear() {
        // Test for method void java.util.HashSet.clear()
        Set orgSet = ((Set) (hs.clone()));
        hs.clear();
        Iterator i = orgSet.iterator();
        TestCase.assertEquals("Returned non-zero size after clear", 0, hs.size());
        while (i.hasNext())
            TestCase.assertTrue("Failed to clear set", (!(hs.contains(i.next()))));

    }

    /**
     * java.util.HashSet#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.HashSet.clone()
        HashSet hs2 = ((HashSet) (hs.clone()));
        TestCase.assertTrue("clone returned an equivalent HashSet", ((hs) != hs2));
        TestCase.assertTrue("clone did not return an equal HashSet", hs.equals(hs2));
    }

    /**
     * java.util.HashSet#contains(java.lang.Object)
     */
    public void test_containsLjava_lang_Object() {
        // Test for method boolean java.util.HashSet.contains(java.lang.Object)
        TestCase.assertTrue("Returned false for valid object", hs.contains(objArray[90]));
        TestCase.assertTrue("Returned true for invalid Object", (!(hs.contains(new Object()))));
        HashSet s = new HashSet();
        s.add(null);
        TestCase.assertTrue("Cannot handle null", s.contains(null));
    }

    /**
     * java.util.HashSet#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.HashSet.isEmpty()
        TestCase.assertTrue("Empty set returned false", new HashSet().isEmpty());
        TestCase.assertTrue("Non-empty set returned true", (!(hs.isEmpty())));
    }

    /**
     * java.util.HashSet#iterator()
     */
    public void test_iterator() {
        // Test for method java.util.Iterator java.util.HashSet.iterator()
        Iterator i = hs.iterator();
        int x = 0;
        while (i.hasNext()) {
            TestCase.assertTrue("Failed to iterate over all elements", hs.contains(i.next()));
            ++x;
        } 
        TestCase.assertTrue("Returned iteration of incorrect size", ((hs.size()) == x));
        HashSet s = new HashSet();
        s.add(null);
        TestCase.assertNull("Cannot handle null", s.iterator().next());
    }

    /**
     * java.util.HashSet#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method boolean java.util.HashSet.remove(java.lang.Object)
        int size = hs.size();
        hs.remove(new Integer(98));
        TestCase.assertTrue("Failed to remove element", (!(hs.contains(new Integer(98)))));
        TestCase.assertTrue("Failed to decrement set size", ((hs.size()) == (size - 1)));
        HashSet s = new HashSet();
        s.add(null);
        TestCase.assertTrue("Cannot handle null", s.remove(null));
        TestCase.assertFalse(hs.remove(new Integer((-98))));
    }

    /**
     * java.util.HashSet#size()
     */
    public void test_size() {
        // Test for method int java.util.HashSet.size()
        TestCase.assertTrue("Returned incorrect size", ((hs.size()) == ((objArray.length) + 1)));
        hs.clear();
        TestCase.assertEquals("Cleared set returned non-zero size", 0, hs.size());
    }

    /**
     * java.util.HashSet#SerializationTest
     */
    public void test_Serialization() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        HashSet<String> hs = new HashSet<String>();
        hs.add("hello");
        hs.add("world");
        SerializationTest.verifySelf(hs, HashSetTest.comparator);
        SerializationTest.verifyGolden(this, hs, HashSetTest.comparator);
    }

    /* Bug 26294011 */
    public void test_empty_clone() throws Exception {
        HashSet<Integer> emptyHs = new HashSet<Integer>();
        HashSet<Integer> cloned = ((HashSet) (emptyHs.clone()));
        cloned.add(new Integer(8));
    }

    public void test_forEach() throws Exception {
        HashSet<Integer> hs = new HashSet<>();
        hs.add(0);
        hs.add(1);
        hs.add(2);
        HashSet<Integer> output = new HashSet<>();
        hs.forEach(( k) -> output.add(k));
        TestCase.assertEquals(hs, output);
    }

    public void test_forEach_NPE() throws Exception {
        HashSet<String> set = new HashSet<>();
        try {
            set.forEach(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_forEach_CME() throws Exception {
        HashSet<String> set = new HashSet<>();
        set.add("one");
        set.add("two");
        try {
            set.forEach(new Consumer<String>() {
                @Override
                public void accept(String k) {
                    set.add("foo");
                }
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void test_spliterator() throws Exception {
        HashSet<String> hashSet = new HashSet<>();
        List<String> keys = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p");
        hashSet.addAll(keys);
        ArrayList<String> expectedKeys = new ArrayList<>(keys);
        SpliteratorTester.runBasicIterationTests_unordered(hashSet.spliterator(), expectedKeys, String::compareTo);
        SpliteratorTester.runBasicSplitTests(hashSet, expectedKeys);
        SpliteratorTester.testSpliteratorNPE(hashSet.spliterator());
        TestCase.assertTrue(hashSet.spliterator().hasCharacteristics(Spliterator.DISTINCT));
        SpliteratorTester.runDistinctTests(keys);
        SpliteratorTester.assertSupportsTrySplit(hashSet);
    }

    private static final SerializableAssert comparator = new SerializationTest.SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            HashSet<String> initialHs = ((HashSet<String>) (initial));
            HashSet<String> deseriaHs = ((HashSet<String>) (deserialized));
            TestCase.assertEquals("should be equal", initialHs.size(), deseriaHs.size());
            TestCase.assertEquals("should be equal", initialHs, deseriaHs);
        }
    };
}

