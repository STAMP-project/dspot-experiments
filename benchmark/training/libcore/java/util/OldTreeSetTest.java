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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import junit.framework.TestCase;


public class OldTreeSetTest extends TestCase {
    TreeSet ts;

    Object[] objArray = new Object[1000];

    public void test_ConstructorLjava_util_Collection() {
        // Test for method java.util.TreeSet(java.util.Collection)
        TreeSet myTreeSet = new TreeSet(Arrays.asList(objArray));
        TestCase.assertTrue("TreeSet incorrect size", ((myTreeSet.size()) == (objArray.length)));
        for (int counter = 0; counter < (objArray.length); counter++)
            TestCase.assertTrue("TreeSet does not contain correct elements", myTreeSet.contains(objArray[counter]));

        HashMap hm = new HashMap();
        hm.put("First", new Integer(1));
        hm.put(new Integer(2), "two");
        try {
            new TreeSet(hm.values());
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            new TreeSet(((Collection) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_ConstructorLjava_util_SortedSet() {
        try {
            new TreeSet(((SortedSet) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_addLjava_lang_Object() {
        // Test for method boolean java.util.TreeSet.add(java.lang.Object)
        ts.add(new Integer((-8)));
        TestCase.assertTrue("Failed to add Object", ts.contains(new Integer((-8))));
        ts.add(objArray[0]);
        TestCase.assertTrue("Added existing element", ((ts.size()) == ((objArray.length) + 1)));
        HashMap hm = new HashMap();
        hm.put("First", new Integer(1));
        hm.put(new Integer(2), "two");
        try {
            ts.add("Wrong element");
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void test_addAllLjava_util_Collection() {
        // Test for method boolean
        // java.util.TreeSet.addAll(java.util.Collection)
        TreeSet s = new TreeSet();
        s.addAll(ts);
        TestCase.assertTrue("Incorrect size after add", ((s.size()) == (ts.size())));
        Iterator i = ts.iterator();
        while (i.hasNext())
            TestCase.assertTrue("Returned incorrect set", s.contains(i.next()));

        HashMap hm = new HashMap();
        hm.put("First", new Integer(1));
        hm.put(new Integer(2), "two");
        try {
            s.addAll(hm.values());
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            s.addAll(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_first() {
        // Test for method java.lang.Object java.util.TreeSet.first()
        TestCase.assertTrue("Returned incorrect first element", ((ts.first()) == (objArray[0])));
        ts = new TreeSet();
        try {
            ts.first();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void test_headSetLjava_lang_Object() {
        // Test for method java.util.SortedSet
        // java.util.TreeSet.headSet(java.lang.Object)
        Set s = ts.headSet(new Integer(100));
        TestCase.assertEquals("Returned set of incorrect size", 100, s.size());
        for (int i = 0; i < 100; i++)
            TestCase.assertTrue("Returned incorrect set", s.contains(objArray[i]));

        SortedSet sort = ts.headSet(new Integer(100));
        try {
            sort.headSet(new Integer(101));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            ts.headSet(this);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            ts.headSet(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void test_last() {
        // Test for method java.lang.Object java.util.TreeSet.last()
        TestCase.assertTrue("Returned incorrect last element", ((ts.last()) == (objArray[((objArray.length) - 1)])));
        ts = new TreeSet();
        try {
            ts.last();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void test_subSetLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.util.SortedSet
        // java.util.TreeSet.subSet(java.lang.Object, java.lang.Object)
        final int startPos = (objArray.length) / 4;
        final int endPos = (3 * (objArray.length)) / 4;
        SortedSet aSubSet = ts.subSet(objArray[startPos], objArray[endPos]);
        TestCase.assertTrue("Subset has wrong number of elements", ((aSubSet.size()) == (endPos - startPos)));
        for (int counter = startPos; counter < endPos; counter++)
            TestCase.assertTrue("Subset does not contain all the elements it should", aSubSet.contains(objArray[counter]));

        try {
            ts.subSet(objArray[3], objArray[0]);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            ts.subSet(null, objArray[3]);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            ts.subSet(objArray[3], null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            ts.subSet(objArray[3], this);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void test_tailSetLjava_lang_Object() {
        // Test for method java.util.SortedSet
        // java.util.TreeSet.tailSet(java.lang.Object)
        Set s = ts.tailSet(new Integer(900));
        TestCase.assertEquals("Returned set of incorrect size", 100, s.size());
        for (int i = 900; i < (objArray.length); i++)
            TestCase.assertTrue("Returned incorrect set", s.contains(objArray[i]));

        SortedSet sort = ts.tailSet(new Integer(101));
        try {
            sort.tailSet(new Integer(100));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            ts.tailSet(this);
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            ts.tailSet(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }
}

