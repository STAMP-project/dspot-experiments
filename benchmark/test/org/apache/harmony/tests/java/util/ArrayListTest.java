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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Vector;
import java.util.function.Consumer;
import junit.framework.TestCase;
import libcore.java.util.ForEachRemainingTester;
import libcore.java.util.SpliteratorTester;
import tests.support.Support_ListTest;


public class ArrayListTest extends TestCase {
    List alist;

    Object[] objArray;

    /**
     * java.util.ArrayList#ArrayList()
     */
    public void test_Constructor() {
        // Test for method java.util.ArrayList()
        new Support_ListTest("", alist).runTest();
        ArrayList subList = new ArrayList();
        for (int i = -50; i < 150; i++)
            subList.add(new Integer(i));

        new Support_ListTest("", subList.subList(50, 150)).runTest();
    }

    /**
     * java.util.ArrayList#ArrayList(int)
     */
    public void test_ConstructorI() {
        // Test for method java.util.ArrayList(int)
        ArrayList al = new ArrayList(5);
        TestCase.assertEquals("Incorrect arrayList created", 0, al.size());
        al = new ArrayList(0);
        TestCase.assertEquals("Incorrect arrayList created", 0, al.size());
        try {
            new ArrayList((-1));
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    /**
     * java.util.ArrayList#ArrayList(java.util.Collection)
     */
    public void test_ConstructorLjava_util_Collection() {
        // Test for method java.util.ArrayList(java.util.Collection)
        ArrayList al = new ArrayList(Arrays.asList(objArray));
        TestCase.assertTrue("arrayList created from collection has incorrect size", ((al.size()) == (objArray.length)));
        for (int counter = 0; counter < (objArray.length); counter++)
            TestCase.assertTrue("arrayList created from collection has incorrect elements", ((al.get(counter)) == (objArray[counter])));

        try {
            new ArrayList(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testConstructorWithConcurrentCollection() {
        Collection<String> collection = shrinksOnSize("A", "B", "C", "D");
        ArrayList<String> list = new ArrayList<String>(collection);
        TestCase.assertFalse(list.contains(null));
    }

    /**
     * java.util.ArrayList#add(int, java.lang.Object)
     */
    public void test_addILjava_lang_Object() {
        // Test for method void java.util.ArrayList.add(int, java.lang.Object)
        Object o;
        alist.add(50, (o = new Object()));
        TestCase.assertTrue("Failed to add Object", ((alist.get(50)) == o));
        TestCase.assertTrue("Failed to fix up list after insert", (((alist.get(51)) == (objArray[50])) && ((alist.get(52)) == (objArray[51]))));
        Object oldItem = alist.get(25);
        alist.add(25, null);
        TestCase.assertNull("Should have returned null", alist.get(25));
        TestCase.assertTrue("Should have returned the old item from slot 25", ((alist.get(26)) == oldItem));
        alist.add(0, (o = new Object()));
        TestCase.assertEquals("Failed to add Object", alist.get(0), o);
        TestCase.assertEquals(alist.get(1), objArray[0]);
        TestCase.assertEquals(alist.get(2), objArray[1]);
        oldItem = alist.get(0);
        alist.add(0, null);
        TestCase.assertNull("Should have returned null", alist.get(0));
        TestCase.assertEquals("Should have returned the old item from slot 0", alist.get(1), oldItem);
        try {
            alist.add((-1), new Object());
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            alist.add((-1), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            alist.add(((alist.size()) + 1), new Object());
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            alist.add(((alist.size()) + 1), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
    }

    /**
     * java.util.ArrayList#add(int, java.lang.Object)
     */
    public void test_addILjava_lang_Object_2() {
        Object o = new Object();
        int size = alist.size();
        alist.add(size, o);
        TestCase.assertEquals("Failed to add Object", alist.get(size), o);
        TestCase.assertEquals(alist.get((size - 2)), objArray[(size - 2)]);
        TestCase.assertEquals(alist.get((size - 1)), objArray[(size - 1)]);
        alist.remove(size);
        size = alist.size();
        alist.add(size, null);
        TestCase.assertNull("Should have returned null", alist.get(size));
        TestCase.assertEquals(alist.get((size - 2)), objArray[(size - 2)]);
        TestCase.assertEquals(alist.get((size - 1)), objArray[(size - 1)]);
    }

    /**
     * java.util.ArrayList#add(java.lang.Object)
     */
    public void test_addLjava_lang_Object() {
        // Test for method boolean java.util.ArrayList.add(java.lang.Object)
        Object o = new Object();
        alist.add(o);
        TestCase.assertTrue("Failed to add Object", ((alist.get(((alist.size()) - 1))) == o));
        alist.add(null);
        TestCase.assertNull("Failed to add null", alist.get(((alist.size()) - 1)));
    }

    /**
     * java.util.ArrayList#addAll(int, java.util.Collection)
     */
    public void test_addAllILjava_util_Collection() {
        // Test for method boolean java.util.ArrayList.addAll(int,
        // java.util.Collection)
        alist.addAll(50, alist);
        TestCase.assertEquals("Returned incorrect size after adding to existing list", 200, alist.size());
        for (int i = 0; i < 50; i++)
            TestCase.assertTrue("Manipulated elements < index", ((alist.get(i)) == (objArray[i])));

        for (int i = 0; (i >= 50) && (i < 150); i++)
            TestCase.assertTrue("Failed to ad elements properly", ((alist.get(i)) == (objArray[(i - 50)])));

        for (int i = 0; (i >= 150) && (i < 200); i++)
            TestCase.assertTrue("Failed to ad elements properly", ((alist.get(i)) == (objArray[(i - 100)])));

        ArrayList listWithNulls = new ArrayList();
        listWithNulls.add(null);
        listWithNulls.add(null);
        listWithNulls.add("yoink");
        listWithNulls.add("kazoo");
        listWithNulls.add(null);
        alist.addAll(100, listWithNulls);
        TestCase.assertTrue(("Incorrect size: " + (alist.size())), ((alist.size()) == 205));
        TestCase.assertNull("Item at slot 100 should be null", alist.get(100));
        TestCase.assertNull("Item at slot 101 should be null", alist.get(101));
        TestCase.assertEquals("Item at slot 102 should be 'yoink'", "yoink", alist.get(102));
        TestCase.assertEquals("Item at slot 103 should be 'kazoo'", "kazoo", alist.get(103));
        TestCase.assertNull("Item at slot 104 should be null", alist.get(104));
        alist.addAll(205, listWithNulls);
        TestCase.assertTrue(("Incorrect size2: " + (alist.size())), ((alist.size()) == 210));
    }

    /**
     * java.util.ArrayList#addAll(int, java.util.Collection)
     */
    public void test_addAllILjava_util_Collection_3() {
        ArrayList obj = new ArrayList();
        obj.addAll(0, obj);
        obj.addAll(obj.size(), obj);
        try {
            obj.addAll((-1), obj);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            obj.addAll(((obj.size()) + 1), obj);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            obj.addAll(0, null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Excepted
        }
        try {
            obj.addAll(((obj.size()) + 1), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            obj.addAll(((int) (-1)), ((Collection) (null)));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
    }

    // BEGIN Android-removed
    // The spec does not mandate that IndexOutOfBoundsException be thrown in
    // preference to NullPointerException when the caller desserves both.
    // 
    // /**
    // * java.util.ArrayList#addAll(int, java.util.Collection)
    // */
    // public void test_addAllILjava_util_Collection_2() {
    // // Regression for HARMONY-467
    // ArrayList obj = new ArrayList();
    // try {
    // obj.addAll((int) -1, (Collection) null);
    // fail("IndexOutOfBoundsException expected");
    // } catch (IndexOutOfBoundsException e) {
    // }
    // }
    // END Android-removed
    /**
     * java.util.ArrayList#addAll(java.util.Collection)
     */
    public void test_addAllLjava_util_Collection() {
        // Test for method boolean
        // java.util.ArrayList.addAll(java.util.Collection)
        List l = new ArrayList();
        l.addAll(alist);
        for (int i = 0; i < (alist.size()); i++)
            TestCase.assertTrue("Failed to add elements properly", l.get(i).equals(alist.get(i)));

        alist.addAll(alist);
        TestCase.assertEquals("Returned incorrect size after adding to existing list", 200, alist.size());
        for (int i = 0; i < 100; i++) {
            TestCase.assertTrue("Added to list in incorrect order", alist.get(i).equals(l.get(i)));
            TestCase.assertTrue("Failed to add to existing list", alist.get((i + 100)).equals(l.get(i)));
        }
        Set setWithNulls = new HashSet();
        setWithNulls.add(null);
        setWithNulls.add(null);
        setWithNulls.add("yoink");
        setWithNulls.add("kazoo");
        setWithNulls.add(null);
        alist.addAll(100, setWithNulls);
        Iterator i = setWithNulls.iterator();
        TestCase.assertTrue(("Item at slot 100 is wrong: " + (alist.get(100))), ((alist.get(100)) == (i.next())));
        TestCase.assertTrue(("Item at slot 101 is wrong: " + (alist.get(101))), ((alist.get(101)) == (i.next())));
        TestCase.assertTrue(("Item at slot 103 is wrong: " + (alist.get(102))), ((alist.get(102)) == (i.next())));
        try {
            alist.addAll(null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Excepted
        }
        // Regression test for Harmony-3481
        ArrayList<Integer> originalList = new ArrayList<Integer>(12);
        for (int j = 0; j < 12; j++) {
            originalList.add(j);
        }
        originalList.remove(0);
        originalList.remove(0);
        ArrayList<Integer> additionalList = new ArrayList<Integer>(11);
        for (int j = 0; j < 11; j++) {
            additionalList.add(j);
        }
        TestCase.assertTrue(originalList.addAll(additionalList));
        TestCase.assertEquals(21, originalList.size());
    }

    public void test_ArrayList_addAll_scenario1() {
        ArrayList arrayListA = new ArrayList();
        arrayListA.add(1);
        ArrayList arrayListB = new ArrayList();
        arrayListB.add(1);
        arrayListA.addAll(1, arrayListB);
        int size = arrayListA.size();
        TestCase.assertEquals(2, size);
        for (int index = 0; index < size; index++) {
            TestCase.assertEquals(1, arrayListA.get(index));
        }
    }

    public void test_ArrayList_addAll_scenario2() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(1);
        arrayList.addAll(1, arrayList);
        int size = arrayList.size();
        TestCase.assertEquals(2, size);
        for (int index = 0; index < size; index++) {
            TestCase.assertEquals(1, arrayList.get(index));
        }
    }

    // Regression test for HARMONY-5839
    public void testaddAllHarmony5839() {
        Collection coll = Arrays.asList(new String[]{ "1", "2" });
        List list = new ArrayList();
        list.add("a");
        list.add(0, "b");
        list.add(0, "c");
        list.add(0, "d");
        list.add(0, "e");
        list.add(0, "f");
        list.add(0, "g");
        list.add(0, "h");
        list.add(0, "i");
        list.addAll(6, coll);
        TestCase.assertEquals(11, list.size());
        TestCase.assertFalse(list.contains(null));
    }

    /**
     * java.util.ArrayList#clear()
     */
    public void test_clear() {
        // Test for method void java.util.ArrayList.clear()
        alist.clear();
        TestCase.assertEquals("List did not clear", 0, alist.size());
        alist.add(null);
        alist.add(null);
        alist.add(null);
        alist.add("bam");
        alist.clear();
        TestCase.assertEquals("List with nulls did not clear", 0, alist.size());
        /* for (int i = 0; i < alist.size(); i++) assertNull("Failed to clear
        list", alist.get(i));
         */
    }

    /**
     * java.util.ArrayList#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.ArrayList.clone()
        ArrayList x = ((ArrayList) (((ArrayList) (alist)).clone()));
        TestCase.assertTrue("Cloned list was inequal to original", x.equals(alist));
        for (int i = 0; i < (alist.size()); i++)
            TestCase.assertTrue("Cloned list contains incorrect elements", ((alist.get(i)) == (x.get(i))));

        alist.add(null);
        alist.add(25, null);
        x = ((ArrayList) (((ArrayList) (alist)).clone()));
        TestCase.assertTrue("nulls test - Cloned list was inequal to original", x.equals(alist));
        for (int i = 0; i < (alist.size()); i++)
            TestCase.assertTrue("nulls test - Cloned list contains incorrect elements", ((alist.get(i)) == (x.get(i))));

    }

    /**
     * java.util.ArrayList#contains(java.lang.Object)
     */
    public void test_containsLjava_lang_Object() {
        // Test for method boolean
        // java.util.ArrayList.contains(java.lang.Object)
        TestCase.assertTrue("Returned false for valid element", alist.contains(objArray[99]));
        TestCase.assertTrue("Returned false for equal element", alist.contains(new Integer(8)));
        TestCase.assertTrue("Returned true for invalid element", (!(alist.contains(new Object()))));
        TestCase.assertTrue("Returned true for null but should have returned false", (!(alist.contains(null))));
        alist.add(null);
        TestCase.assertTrue("Returned false for null but should have returned true", alist.contains(null));
    }

    /**
     * java.util.ArrayList#ensureCapacity(int)
     */
    public void test_ensureCapacityI() throws Exception {
        // Test for method void java.util.ArrayList.ensureCapacity(int)
        // TODO : There is no good way to test this as it only really impacts on
        // the private implementation.
        Object testObject = new Object();
        int capacity = 20;
        ArrayList al = new ArrayList(capacity);
        int i;
        for (i = 0; i < (capacity / 2); i++) {
            al.add(i, new Object());
        }
        al.add(i, testObject);
        int location = al.indexOf(testObject);
        al.ensureCapacity(capacity);
        TestCase.assertTrue("EnsureCapacity moved objects around in array1.", (location == (al.indexOf(testObject))));
        al.remove(0);
        al.ensureCapacity(capacity);
        TestCase.assertTrue("EnsureCapacity moved objects around in array2.", ((--location) == (al.indexOf(testObject))));
        al.ensureCapacity((capacity + 2));
        TestCase.assertTrue("EnsureCapacity did not change location.", (location == (al.indexOf(testObject))));
        ArrayList<String> list = new ArrayList<String>(1);
        list.add("hello");
        list.ensureCapacity(Integer.MIN_VALUE);
    }

    /**
     * java.util.ArrayList#get(int)
     */
    public void test_getI() {
        // Test for method java.lang.Object java.util.ArrayList.get(int)
        TestCase.assertTrue("Returned incorrect element", ((alist.get(22)) == (objArray[22])));
        try {
            alist.get(8765);
            TestCase.fail("Failed to throw expected exception for index > size");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
    }

    /**
     * java.util.ArrayList#indexOf(java.lang.Object)
     */
    public void test_indexOfLjava_lang_Object() {
        // Test for method int java.util.ArrayList.indexOf(java.lang.Object)
        TestCase.assertEquals("Returned incorrect index", 87, alist.indexOf(objArray[87]));
        TestCase.assertEquals("Returned index for invalid Object", (-1), alist.indexOf(new Object()));
        alist.add(25, null);
        alist.add(50, null);
        TestCase.assertTrue(("Wrong indexOf for null.  Wanted 25 got: " + (alist.indexOf(null))), ((alist.indexOf(null)) == 25));
    }

    /**
     * java.util.ArrayList#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.ArrayList.isEmpty()
        TestCase.assertTrue("isEmpty returned false for new list", new ArrayList().isEmpty());
        TestCase.assertTrue("Returned true for existing list with elements", (!(alist.isEmpty())));
    }

    /**
     * java.util.ArrayList#lastIndexOf(java.lang.Object)
     */
    public void test_lastIndexOfLjava_lang_Object() {
        // Test for method int java.util.ArrayList.lastIndexOf(java.lang.Object)
        alist.add(new Integer(99));
        TestCase.assertEquals("Returned incorrect index", 100, alist.lastIndexOf(objArray[99]));
        TestCase.assertEquals("Returned index for invalid Object", (-1), alist.lastIndexOf(new Object()));
        alist.add(25, null);
        alist.add(50, null);
        TestCase.assertTrue(("Wrong lastIndexOf for null.  Wanted 50 got: " + (alist.lastIndexOf(null))), ((alist.lastIndexOf(null)) == 50));
    }

    /**
     * {@link java.util.ArrayList#removeRange(int, int)}
     */
    public void test_removeRange() {
        ArrayListTest.MockArrayList mylist = new ArrayListTest.MockArrayList();
        mylist.removeRange(0, 0);
        try {
            mylist.removeRange(0, 1);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        int[] data = new int[]{ 1, 2, 3 };
        for (int i = 0; i < (data.length); i++) {
            mylist.add(i, data[i]);
        }
        mylist.removeRange(0, 1);
        TestCase.assertEquals(data[1], mylist.get(0));
        TestCase.assertEquals(data[2], mylist.get(1));
        try {
            mylist.removeRange((-1), 1);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            mylist.removeRange(0, (-1));
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            mylist.removeRange(1, 0);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            mylist.removeRange(2, 1);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
    }

    /**
     * java.util.ArrayList#remove(int)
     */
    public void test_removeI() {
        // Test for method java.lang.Object java.util.ArrayList.remove(int)
        alist.remove(10);
        TestCase.assertEquals("Failed to remove element", (-1), alist.indexOf(objArray[10]));
        try {
            alist.remove(999);
            TestCase.fail("Failed to throw exception when index out of range");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        ArrayList myList = ((ArrayList) (((ArrayList) (alist)).clone()));
        alist.add(25, null);
        alist.add(50, null);
        alist.remove(50);
        alist.remove(25);
        TestCase.assertTrue("Removing nulls did not work", alist.equals(myList));
        List list = new ArrayList(Arrays.asList(new String[]{ "a", "b", "c", "d", "e", "f", "g" }));
        TestCase.assertTrue("Removed wrong element 1", ((list.remove(0)) == "a"));
        TestCase.assertTrue("Removed wrong element 2", ((list.remove(4)) == "f"));
        String[] result = new String[5];
        list.toArray(result);
        TestCase.assertTrue("Removed wrong element 3", Arrays.equals(result, new String[]{ "b", "c", "d", "e", "g" }));
        List l = new ArrayList(0);
        l.add(new Object());
        l.add(new Object());
        l.remove(0);
        l.remove(0);
        try {
            l.remove((-1));
            TestCase.fail("-1 should cause exception");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            l.remove(0);
            TestCase.fail("0 should case exception");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
    }

    /**
     * java.util.ArrayList#set(int, java.lang.Object)
     */
    public void test_setILjava_lang_Object() {
        // Test for method java.lang.Object java.util.ArrayList.set(int,
        // java.lang.Object)
        Object obj;
        alist.set(65, (obj = new Object()));
        TestCase.assertTrue("Failed to set object", ((alist.get(65)) == obj));
        alist.set(50, null);
        TestCase.assertNull("Setting to null did not work", alist.get(50));
        TestCase.assertTrue(("Setting increased the list's size to: " + (alist.size())), ((alist.size()) == 100));
        obj = new Object();
        alist.set(0, obj);
        TestCase.assertTrue("Failed to set object", ((alist.get(0)) == obj));
        try {
            alist.set((-1), obj);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            alist.set(alist.size(), obj);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            alist.set((-1), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
        try {
            alist.set(alist.size(), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
            TestCase.assertNotNull(e.getMessage());
        }
    }

    /**
     * java.util.ArrayList#size()
     */
    public void test_size() {
        // Test for method int java.util.ArrayList.size()
        TestCase.assertEquals("Returned incorrect size for exiting list", 100, alist.size());
        TestCase.assertEquals("Returned incorrect size for new list", 0, new ArrayList().size());
    }

    /**
     * java.util.AbstractCollection#toString()
     */
    public void test_toString() {
        ArrayList l = new ArrayList(1);
        l.add(l);
        String result = l.toString();
        TestCase.assertTrue("should contain self ref", ((result.indexOf("(this")) > (-1)));
    }

    /**
     * java.util.ArrayList#toArray()
     */
    public void test_toArray() {
        // Test for method java.lang.Object [] java.util.ArrayList.toArray()
        alist.set(25, null);
        alist.set(75, null);
        Object[] obj = alist.toArray();
        TestCase.assertEquals("Returned array of incorrect size", objArray.length, obj.length);
        for (int i = 0; i < (obj.length); i++) {
            if ((i == 25) || (i == 75))
                TestCase.assertNull(((("Should be null at: " + i) + " but instead got: ") + (obj[i])), obj[i]);
            else
                TestCase.assertTrue(("Returned incorrect array: " + i), ((obj[i]) == (objArray[i])));

        }
    }

    /**
     * java.util.ArrayList#toArray(java.lang.Object[])
     */
    public void test_toArray$Ljava_lang_Object() {
        // Test for method java.lang.Object []
        // java.util.ArrayList.toArray(java.lang.Object [])
        alist.set(25, null);
        alist.set(75, null);
        Integer[] argArray = new Integer[100];
        Object[] retArray;
        retArray = alist.toArray(argArray);
        TestCase.assertTrue("Returned different array than passed", (retArray == argArray));
        argArray = new Integer[1000];
        retArray = alist.toArray(argArray);
        TestCase.assertNull("Failed to set first extra element to null", argArray[alist.size()]);
        for (int i = 0; i < 100; i++) {
            if ((i == 25) || (i == 75))
                TestCase.assertNull(("Should be null: " + i), retArray[i]);
            else
                TestCase.assertTrue(("Returned incorrect array: " + i), ((retArray[i]) == (objArray[i])));

        }
        String[] strArray = new String[100];
        try {
            alist.toArray(strArray);
            TestCase.fail("ArrayStoreException expected");
        } catch (ArrayStoreException e) {
            // expected
        }
    }

    /**
     * java.util.ArrayList#trimToSize()
     */
    public void test_trimToSize() {
        // Test for method void java.util.ArrayList.trimToSize()
        for (int i = 99; i > 24; i--)
            alist.remove(i);

        ((ArrayList) (alist)).trimToSize();
        TestCase.assertEquals("Returned incorrect size after trim", 25, alist.size());
        for (int i = 0; i < (alist.size()); i++)
            TestCase.assertTrue("Trimmed list contained incorrect elements", ((alist.get(i)) == (objArray[i])));

        Vector v = new Vector();
        v.add("a");
        ArrayList al = new ArrayList(v);
        al.add("b");
        Iterator it = al.iterator();
        al.trimToSize();
        try {
            it.next();
            TestCase.fail("should throw a ConcurrentModificationException");
        } catch (ConcurrentModificationException ioobe) {
            // expected
        }
    }

    public void test_trimToSize_02() {
        ArrayList list = new ArrayList(Arrays.asList(new String[]{ "a", "b", "c", "d", "e", "f", "g" }));
        list.remove("a");
        list.remove("f");
        list.trimToSize();
    }

    public void test_addAll() {
        ArrayList list = new ArrayList();
        list.add("one");
        list.add("two");
        TestCase.assertEquals(2, list.size());
        list.remove(0);
        TestCase.assertEquals(1, list.size());
        ArrayList collection = new ArrayList();
        collection.add("1");
        collection.add("2");
        collection.add("3");
        TestCase.assertEquals(3, collection.size());
        list.addAll(0, collection);
        TestCase.assertEquals(4, list.size());
        list.remove(0);
        list.remove(0);
        TestCase.assertEquals(2, list.size());
        collection.add("4");
        collection.add("5");
        collection.add("6");
        collection.add("7");
        collection.add("8");
        collection.add("9");
        collection.add("10");
        collection.add("11");
        collection.add("12");
        TestCase.assertEquals(12, collection.size());
        list.addAll(0, collection);
        TestCase.assertEquals(14, list.size());
    }

    public void test_removeLjava_lang_Object() {
        List list = new ArrayList(Arrays.asList(new String[]{ "a", "b", "c", "d", "e", "f", "g" }));
        TestCase.assertTrue("Removed wrong element 1", list.remove("a"));
        TestCase.assertTrue("Removed wrong element 2", list.remove("f"));
        String[] result = new String[5];
        list.toArray(result);
        TestCase.assertTrue("Removed wrong element 3", Arrays.equals(result, new String[]{ "b", "c", "d", "e", "g" }));
    }

    public void testAddAllWithConcurrentCollection() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(shrinksOnSize("A", "B", "C", "D"));
        TestCase.assertFalse(list.contains(null));
    }

    public void testAddAllAtPositionWithConcurrentCollection() {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList("A", "B", "C", "D"));
        list.addAll(3, shrinksOnSize("E", "F", "G", "H"));
        TestCase.assertFalse(list.contains(null));
    }

    public void test_override_size() throws Exception {
        ArrayList testlist = new ArrayListTest.MockArrayList();
        // though size is overriden, it should passed without exception
        testlist.add("test_0");
        testlist.add("test_1");
        testlist.add("test_2");
        testlist.add(1, "test_3");
        testlist.get(1);
        testlist.remove(2);
        testlist.set(1, "test_4");
    }

    public class MockArrayList extends ArrayList {
        public int size() {
            return 0;
        }

        public void removeRange(int begin, int end) {
            super.removeRange(begin, end);
        }
    }

    public void test_removeRangeII() {
        ArrayListTest.MockArrayList mal = new ArrayListTest.MockArrayList();
        mal.add("a");
        mal.add("b");
        mal.add("c");
        mal.add("d");
        mal.add("e");
        mal.add("f");
        mal.add("g");
        mal.add("h");
        mal.removeRange(2, 4);
        String[] result = new String[6];
        mal.toArray(result);
        TestCase.assertTrue("Removed wrong element 3", Arrays.equals(result, new String[]{ "a", "b", "e", "f", "g", "h" }));
    }

    public static class ArrayListExtend extends ArrayList {
        private int size = 0;

        public ArrayListExtend() {
            super(10);
        }

        public boolean add(Object o) {
            (size)++;
            return super.add(o);
        }

        public int size() {
            return size;
        }
    }

    public void test_subclassing() {
        ArrayListTest.ArrayListExtend a = new ArrayListTest.ArrayListExtend();
        /* Regression test for subclasses that override size() (which used to
        cause an exception when growing 'a').
         */
        for (int i = 0; i < 100; i++) {
            a.add(new Object());
        }
    }

    // http://b/25867131 et al.
    public void testIteratorAddAfterCompleteIteration() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("string1");
        Iterator<String> it = strings.iterator();
        TestCase.assertTrue(it.hasNext());
        TestCase.assertEquals("string1", it.next());
        TestCase.assertFalse(it.hasNext());
        strings.add("string2");
        // The value of hasNext() must not flap between true and false. If we returned "true"
        // here, we'd fail with a CME on the next call to next() anyway.
        TestCase.assertFalse(it.hasNext());
    }

    public void testHasNextAfterRemoval() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("string1");
        Iterator<String> it = strings.iterator();
        it.next();
        it.remove();
        TestCase.assertFalse(it.hasNext());
        strings = new ArrayList<>();
        strings.add("string1");
        strings.add("string2");
        it = strings.iterator();
        it.next();
        it.remove();
        TestCase.assertTrue(it.hasNext());
        TestCase.assertEquals("string2", it.next());
    }

    // http://b/27430229
    public void testRemoveAllDuringIteration() {
        ArrayList<String> list = new ArrayList<>();
        list.add("food");
        Iterator<String> iterator = list.iterator();
        iterator.next();
        list.clear();
        TestCase.assertFalse(iterator.hasNext());
    }

    public void test_forEach() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        ArrayList<Integer> output = new ArrayList<>();
        list.forEach(( k) -> output.add(k));
        TestCase.assertEquals(list, output);
    }

    public void test_forEach_NPE() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        try {
            list.forEach(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void test_forEach_CME() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        ArrayList<Integer> processed = new ArrayList<>();
        try {
            list.forEach(( t) -> {
                processed.add(t);
                list.add(t);
            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        TestCase.assertEquals(1, processed.size());
    }

    public void test_forEach_CME_onLastElement() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        try {
            list.forEach(( t) -> {
                if (t == 3)
                    list.add(t);

            });
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void test_forEachRemaining_iterator() throws Exception {
        ForEachRemainingTester.runTests(ArrayList.class, new String[]{ "foo", "bar", "baz" });
        ForEachRemainingTester.runTests(ArrayList.class, new String[]{ "foo" });
    }

    public void test_spliterator() throws Exception {
        ArrayList<Integer> testElements = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(testElements);
        SpliteratorTester.runBasicIterationTests(list.spliterator(), testElements);
        SpliteratorTester.runBasicSplitTests(list, testElements);
        SpliteratorTester.testSpliteratorNPE(list.spliterator());
        TestCase.assertTrue(list.spliterator().hasCharacteristics((((Spliterator.ORDERED) | (Spliterator.SIZED)) | (Spliterator.SUBSIZED))));
        SpliteratorTester.runOrderedTests(list);
        /* expected size */
        SpliteratorTester.runSizedTests(list, 16);
        /* expected size */
        SpliteratorTester.runSubSizedTests(list, 16);
        SpliteratorTester.assertSupportsTrySplit(list);
    }

    public void test_spliterator_CME() throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(52);
        Spliterator<Integer> sp = list.spliterator();
        try {
            sp.tryAdvance(( value) -> list.add(value));
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(52);
        try {
            sp.forEachRemaining(( value) -> list2.add(value));
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void test_removeIf() {
        runBasicRemoveIfTests(ArrayList<Integer>::new);
        runBasicRemoveIfTestsUnordered(ArrayList<Integer>::new);
        runRemoveIfOnEmpty(ArrayList<Integer>::new);
        testRemoveIfNPE(ArrayList<Integer>::new);
        testRemoveIfCME(ArrayList<Integer>::new);
    }

    public void test_sublist_spliterator() {
        ArrayList<Integer> testElements = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        List<Integer> list = new ArrayList<>();
        list.addAll(testElements);
        testElements = new ArrayList<Integer>(list.subList(8, 16));
        list = list.subList(8, 16);
        SpliteratorTester.runBasicIterationTests(list.spliterator(), testElements);
        SpliteratorTester.runBasicSplitTests(list, testElements);
        SpliteratorTester.testSpliteratorNPE(list.spliterator());
        TestCase.assertTrue(list.spliterator().hasCharacteristics((((Spliterator.ORDERED) | (Spliterator.SIZED)) | (Spliterator.SUBSIZED))));
        SpliteratorTester.runOrderedTests(list);
        /* expected size */
        SpliteratorTester.runSizedTests(list, 8);
        /* expected size */
        SpliteratorTester.runSubSizedTests(list, 8);
        SpliteratorTester.assertSupportsTrySplit(list);
    }
}

