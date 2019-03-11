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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import junit.framework.TestCase;
import libcore.java.util.ForEachRemainingTester;
import libcore.java.util.SpliteratorTester;
import org.apache.harmony.testframework.serialization.SerializationTest;
import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;
import tests.support.Support_ListTest;


public class LinkedListTest extends TestCase {
    LinkedList ll;

    LinkedList<Object> testList;

    private Object testObjOne;

    private Object testObjTwo;

    private Object testObjThree;

    private Object testObjFour;

    private Object testObjLast;

    Object[] objArray;

    /**
     * java.util.LinkedList#LinkedList()
     */
    public void test_Constructor() {
        // Test for method java.util.LinkedList()
        new Support_ListTest("", ll).runTest();
        LinkedList subList = new LinkedList();
        for (int i = -50; i < 150; i++)
            subList.add(new Integer(i));

        new Support_ListTest("", subList.subList(50, 150)).runTest();
    }

    /**
     * java.util.LinkedList#LinkedList(java.util.Collection)
     */
    public void test_ConstructorLjava_util_Collection() {
        // Test for method java.util.LinkedList(java.util.Collection)
        TestCase.assertTrue("Incorrect LinkedList constructed", new LinkedList(ll).equals(ll));
        try {
            new LinkedList(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedList#add(int, java.lang.Object)
     */
    public void test_addILjava_lang_Object() {
        // Test for method void java.util.LinkedList.add(int, java.lang.Object)
        Object o;
        ll.add(50, (o = "Test"));
        TestCase.assertTrue(("Failed to add Object>: " + (ll.get(50).toString())), ((ll.get(50)) == o));
        TestCase.assertTrue("Failed to fix up list after insert", (((ll.get(51)) == (objArray[50])) && ((ll.get(52)) == (objArray[51]))));
        ll.add(50, null);
        TestCase.assertNull("Did not add null correctly", ll.get(50));
        try {
            ll.add((-1), "Test");
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Excepted
        }
        try {
            ll.add((-1), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Excepted
        }
        try {
            ll.add(((ll.size()) + 1), "Test");
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Excepted
        }
        try {
            ll.add(((ll.size()) + 1), null);
            TestCase.fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Excepted
        }
    }

    /**
     * java.util.LinkedList#add(java.lang.Object)
     */
    public void test_addLjava_lang_Object() {
        // Test for method boolean java.util.LinkedList.add(java.lang.Object)
        Object o;
        ll.add((o = new Object()));
        TestCase.assertTrue("Failed to add Object", ((ll.getLast()) == o));
        ll.add(null);
        TestCase.assertNull("Did not add null correctly", ll.get(((ll.size()) - 1)));
    }

    /**
     * java.util.LinkedList#addAll(int, java.util.Collection)
     */
    public void test_addAllILjava_util_Collection() {
        // Test for method boolean java.util.LinkedList.addAll(int,
        // java.util.Collection)
        ll.addAll(50, ((Collection) (ll.clone())));
        TestCase.assertEquals("Returned incorrect size after adding to existing list", 200, ll.size());
        for (int i = 0; i < 50; i++)
            TestCase.assertTrue("Manipulated elements < index", ((ll.get(i)) == (objArray[i])));

        for (int i = 0; (i >= 50) && (i < 150); i++)
            TestCase.assertTrue("Failed to ad elements properly", ((ll.get(i)) == (objArray[(i - 50)])));

        for (int i = 0; (i >= 150) && (i < 200); i++)
            TestCase.assertTrue("Failed to ad elements properly", ((ll.get(i)) == (objArray[(i - 100)])));

        List myList = new LinkedList();
        myList.add(null);
        myList.add("Blah");
        myList.add(null);
        myList.add("Booga");
        myList.add(null);
        ll.addAll(50, myList);
        TestCase.assertNull("a) List w/nulls not added correctly", ll.get(50));
        TestCase.assertEquals("b) List w/nulls not added correctly", "Blah", ll.get(51));
        TestCase.assertNull("c) List w/nulls not added correctly", ll.get(52));
        TestCase.assertEquals("d) List w/nulls not added correctly", "Booga", ll.get(53));
        TestCase.assertNull("e) List w/nulls not added correctly", ll.get(54));
        try {
            ll.addAll((-1), ((Collection) (null)));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ll.addAll(((ll.size()) + 1), ((Collection) (null)));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ll.addAll(0, null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedList#addAll(int, java.util.Collection)
     */
    public void test_addAllILjava_util_Collection_2() {
        // Regression for HARMONY-467
        LinkedList obj = new LinkedList();
        try {
            obj.addAll((-1), ((Collection) (null)));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.util.LinkedList#addAll(java.util.Collection)
     */
    public void test_addAllLjava_util_Collection() {
        // Test for method boolean
        // java.util.LinkedList.addAll(java.util.Collection)
        List l = new ArrayList();
        l.addAll(((Collection) (ll.clone())));
        for (int i = 0; i < (ll.size()); i++)
            TestCase.assertTrue("Failed to add elements properly", l.get(i).equals(ll.get(i)));

        ll.addAll(((Collection) (ll.clone())));
        TestCase.assertEquals("Returned incorrect siZe after adding to existing list", 200, ll.size());
        for (int i = 0; i < 100; i++) {
            TestCase.assertTrue("Added to list in incorrect order", ll.get(i).equals(l.get(i)));
            TestCase.assertTrue("Failed to add to existing list", ll.get((i + 100)).equals(l.get(i)));
        }
        List myList = new LinkedList();
        myList.add(null);
        myList.add("Blah");
        myList.add(null);
        myList.add("Booga");
        myList.add(null);
        ll.addAll(myList);
        TestCase.assertNull("a) List w/nulls not added correctly", ll.get(200));
        TestCase.assertEquals("b) List w/nulls not added correctly", "Blah", ll.get(201));
        TestCase.assertNull("c) List w/nulls not added correctly", ll.get(202));
        TestCase.assertEquals("d) List w/nulls not added correctly", "Booga", ll.get(203));
        TestCase.assertNull("e) List w/nulls not added correctly", ll.get(204));
        try {
            ll.addAll(null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Excepted
        }
    }

    public void test_addAll_Self_Ljava_util_Collection() {
        LinkedList linkedList = new LinkedList();
        linkedList.addLast(1);
        TestCase.assertEquals(1, linkedList.size());
        TestCase.assertTrue(linkedList.addAll(linkedList));
        TestCase.assertEquals(2, linkedList.size());
    }

    public void test_addAll_Self_ILjava_util_Collection() {
        LinkedList linkedList = new LinkedList();
        linkedList.addLast(1);
        TestCase.assertEquals(1, linkedList.size());
        TestCase.assertTrue(linkedList.addAll(1, linkedList));
        TestCase.assertEquals(2, linkedList.size());
    }

    /**
     * java.util.LinkedList#addFirst(java.lang.Object)
     */
    public void test_addFirstLjava_lang_Object() {
        // Test for method void java.util.LinkedList.addFirst(java.lang.Object)
        Object o;
        ll.addFirst((o = new Object()));
        TestCase.assertTrue("Failed to add Object", ((ll.getFirst()) == o));
        ll.addFirst(null);
        TestCase.assertNull("Failed to add null", ll.getFirst());
    }

    /**
     * java.util.LinkedList#addLast(java.lang.Object)
     */
    public void test_addLastLjava_lang_Object() {
        // Test for method void java.util.LinkedList.addLast(java.lang.Object)
        Object o;
        ll.addLast((o = new Object()));
        TestCase.assertTrue("Failed to add Object", ((ll.getLast()) == o));
        ll.addLast(null);
        TestCase.assertNull("Failed to add null", ll.getLast());
    }

    /**
     * java.util.LinkedList#clear()
     */
    public void test_clear() {
        // Test for method void java.util.LinkedList.clear()
        ll.clear();
        for (int i = 0; i < (ll.size()); i++)
            TestCase.assertNull("Failed to clear list", ll.get(i));

    }

    /**
     * java.util.LinkedList#clone()
     */
    public void test_clone() {
        // Test for method java.lang.Object java.util.LinkedList.clone()
        Object x = ll.clone();
        TestCase.assertTrue("Cloned list was inequal to cloned", x.equals(ll));
        for (int i = 0; i < (ll.size()); i++)
            TestCase.assertTrue("Cloned list contains incorrect elements", ll.get(i).equals(((LinkedList) (x)).get(i)));

        ll.addFirst(null);
        x = ll.clone();
        TestCase.assertTrue("List with a null did not clone properly", ll.equals(x));
    }

    /**
     * java.util.LinkedList#contains(java.lang.Object)
     */
    public void test_containsLjava_lang_Object() {
        // Test for method boolean
        // java.util.LinkedList.contains(java.lang.Object)
        TestCase.assertTrue("Returned false for valid element", ll.contains(objArray[99]));
        TestCase.assertTrue("Returned false for equal element", ll.contains(new Integer(8)));
        TestCase.assertTrue("Returned true for invalid element", (!(ll.contains(new Object()))));
        TestCase.assertTrue("Should not contain null", (!(ll.contains(null))));
        ll.add(25, null);
        TestCase.assertTrue("Should contain null", ll.contains(null));
    }

    /**
     * java.util.LinkedList#get(int)
     */
    public void test_getI() {
        // Test for method java.lang.Object java.util.LinkedList.get(int)
        TestCase.assertTrue("Returned incorrect element", ((ll.get(22)) == (objArray[22])));
        try {
            ll.get(8765);
            TestCase.fail("Failed to throw expected exception for index > size");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * {@link java.util.LinkedList#peek()}
     */
    public void test_peek() {
        LinkedList list = new LinkedList();
        TestCase.assertNull("Should return null if this list is empty", list.peek());
        TestCase.assertEquals("Returned incorrect first element", ll.peek(), objArray[0]);
        TestCase.assertEquals("Peek remove the head (first element) of this list", ll.getFirst(), objArray[0]);
    }

    /**
     * java.util.LinkedList#getFirst()
     */
    public void test_getFirst() {
        // Test for method java.lang.Object java.util.LinkedList.getFirst()
        TestCase.assertTrue("Returned incorrect first element", ll.getFirst().equals(objArray[0]));
        LinkedList list = new LinkedList();
        try {
            list.getFirst();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // Excepted
        }
    }

    /**
     * java.util.LinkedList#getLast()
     */
    public void test_getLast() {
        // Test for method java.lang.Object java.util.LinkedList.getLast()
        TestCase.assertTrue("Returned incorrect first element", ll.getLast().equals(objArray[((objArray.length) - 1)]));
        LinkedList list = new LinkedList();
        try {
            list.getLast();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // Excepted
        }
    }

    /**
     * java.util.LinkedList#indexOf(java.lang.Object)
     */
    public void test_indexOfLjava_lang_Object() {
        // Test for method int java.util.LinkedList.indexOf(java.lang.Object)
        TestCase.assertEquals("Returned incorrect index", 87, ll.indexOf(objArray[87]));
        TestCase.assertEquals("Returned index for invalid Object", (-1), ll.indexOf(new Object()));
        ll.add(20, null);
        ll.add(24, null);
        TestCase.assertTrue(("Index of null should be 20, but got: " + (ll.indexOf(null))), ((ll.indexOf(null)) == 20));
    }

    /**
     * java.util.LinkedList#lastIndexOf(java.lang.Object)
     */
    public void test_lastIndexOfLjava_lang_Object() {
        // Test for method int
        // java.util.LinkedList.lastIndexOf(java.lang.Object)
        ll.add(new Integer(99));
        TestCase.assertEquals("Returned incorrect index", 100, ll.lastIndexOf(objArray[99]));
        TestCase.assertEquals("Returned index for invalid Object", (-1), ll.lastIndexOf(new Object()));
        ll.add(20, null);
        ll.add(24, null);
        TestCase.assertTrue(("Last index of null should be 20, but got: " + (ll.lastIndexOf(null))), ((ll.lastIndexOf(null)) == 24));
    }

    /**
     * java.util.LinkedList#listIterator(int)
     */
    public void test_listIteratorI() {
        // Test for method java.util.ListIterator
        // java.util.LinkedList.listIterator(int)
        ListIterator i1 = ll.listIterator();
        ListIterator i2 = ll.listIterator(0);
        Object elm;
        int n = 0;
        while (i2.hasNext()) {
            if ((n == 0) || (n == ((objArray.length) - 1))) {
                if (n == 0)
                    TestCase.assertTrue("First element claimed to have a previous", (!(i2.hasPrevious())));

                if (n == (objArray.length))
                    TestCase.assertTrue("Last element claimed to have next", (!(i2.hasNext())));

            }
            elm = i2.next();
            TestCase.assertTrue("Iterator returned elements in wrong order", (elm == (objArray[n])));
            if ((n > 0) && (n < ((objArray.length) - 1))) {
                TestCase.assertTrue("Next index returned incorrect value", ((i2.nextIndex()) == (n + 1)));
                TestCase.assertTrue(((("previousIndex returned incorrect value : " + (i2.previousIndex())) + ", n val: ") + n), ((i2.previousIndex()) == n));
            }
            elm = i1.next();
            TestCase.assertTrue("Iterator returned elements in wrong order", (elm == (objArray[n])));
            ++n;
        } 
        i2 = ll.listIterator(((ll.size()) / 2));
        TestCase.assertTrue((((Integer) (i2.next())) == ((ll.size()) / 2)));
        List myList = new LinkedList();
        myList.add(null);
        myList.add("Blah");
        myList.add(null);
        myList.add("Booga");
        myList.add(null);
        ListIterator li = myList.listIterator();
        TestCase.assertTrue("li.hasPrevious() should be false", (!(li.hasPrevious())));
        TestCase.assertNull("li.next() should be null", li.next());
        TestCase.assertTrue("li.hasPrevious() should be true", li.hasPrevious());
        TestCase.assertNull("li.prev() should be null", li.previous());
        TestCase.assertNull("li.next() should be null", li.next());
        TestCase.assertEquals("li.next() should be Blah", "Blah", li.next());
        TestCase.assertNull("li.next() should be null", li.next());
        TestCase.assertEquals("li.next() should be Booga", "Booga", li.next());
        TestCase.assertTrue("li.hasNext() should be true", li.hasNext());
        TestCase.assertNull("li.next() should be null", li.next());
        TestCase.assertTrue("li.hasNext() should be false", (!(li.hasNext())));
        try {
            ll.listIterator((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ll.listIterator(((ll.size()) + 1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedList#remove(int)
     */
    public void test_removeI() {
        // Test for method java.lang.Object java.util.LinkedList.remove(int)
        ll.remove(10);
        TestCase.assertEquals("Failed to remove element", (-1), ll.indexOf(objArray[10]));
        try {
            ll.remove(999);
            TestCase.fail("Failed to throw expected exception when index out of range");
        } catch (IndexOutOfBoundsException e) {
            // Correct
        }
        ll.add(20, null);
        ll.remove(20);
        TestCase.assertNotNull("Should have removed null", ll.get(20));
    }

    /**
     * java.util.LinkedList#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method boolean java.util.LinkedList.remove(java.lang.Object)
        TestCase.assertTrue("Failed to remove valid Object", ll.remove(objArray[87]));
        TestCase.assertTrue("Removed invalid object", (!(ll.remove(new Object()))));
        TestCase.assertEquals("Found Object after removal", (-1), ll.indexOf(objArray[87]));
        ll.add(null);
        ll.remove(null);
        TestCase.assertTrue("Should not contain null afrer removal", (!(ll.contains(null))));
    }

    /**
     * java.util.LinkedList#removeFirst()
     */
    public void test_removeFirst() {
        // Test for method java.lang.Object java.util.LinkedList.removeFirst()
        ll.removeFirst();
        TestCase.assertTrue("Failed to remove first element", ((ll.getFirst()) != (objArray[0])));
        LinkedList list = new LinkedList();
        try {
            list.removeFirst();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // Excepted
        }
    }

    /**
     * java.util.LinkedList#removeLast()
     */
    public void test_removeLast() {
        // Test for method java.lang.Object java.util.LinkedList.removeLast()
        ll.removeLast();
        TestCase.assertTrue("Failed to remove last element", ((ll.getLast()) != (objArray[((objArray.length) - 1)])));
        LinkedList list = new LinkedList();
        try {
            list.removeLast();
            TestCase.fail("Should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // Excepted
        }
    }

    /**
     * java.util.LinkedList#set(int, java.lang.Object)
     */
    public void test_setILjava_lang_Object() {
        // Test for method java.lang.Object java.util.LinkedList.set(int,
        // java.lang.Object)
        Object obj;
        ll.set(65, (obj = new Object()));
        TestCase.assertTrue("Failed to set object", ((ll.get(65)) == obj));
        try {
            ll.set((-1), (obj = new Object()));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ll.set(((ll.size()) + 1), (obj = new Object()));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.util.LinkedList#size()
     */
    public void test_size() {
        // Test for method int java.util.LinkedList.size()
        TestCase.assertTrue("Returned incorrect size", ((ll.size()) == (objArray.length)));
        ll.removeFirst();
        TestCase.assertTrue("Returned incorrect size", ((ll.size()) == ((objArray.length) - 1)));
    }

    /**
     * java.util.LinkedList#toArray()
     */
    public void test_toArray() {
        // Test for method java.lang.Object [] java.util.LinkedList.toArray()
        ll.add(null);
        Object[] obj = ll.toArray();
        TestCase.assertEquals("Returned array of incorrect size", ((objArray.length) + 1), obj.length);
        for (int i = 0; i < ((obj.length) - 1); i++)
            TestCase.assertTrue(("Returned incorrect array: " + i), ((obj[i]) == (objArray[i])));

        TestCase.assertNull("Returned incorrect array--end isn't null", obj[((obj.length) - 1)]);
    }

    /**
     * java.util.LinkedList#toArray(java.lang.Object[])
     */
    public void test_toArray$Ljava_lang_Object() {
        // Test for method java.lang.Object []
        // java.util.LinkedList.toArray(java.lang.Object [])
        Integer[] argArray = new Integer[100];
        Object[] retArray;
        retArray = ll.toArray(argArray);
        TestCase.assertTrue("Returned different array than passed", (retArray == argArray));
        List retList = new LinkedList(Arrays.asList(retArray));
        Iterator li = ll.iterator();
        Iterator ri = retList.iterator();
        while (li.hasNext())
            TestCase.assertTrue("Lists are not equal", ((li.next()) == (ri.next())));

        argArray = new Integer[1000];
        retArray = ll.toArray(argArray);
        TestCase.assertNull("Failed to set first extra element to null", argArray[ll.size()]);
        for (int i = 0; i < (ll.size()); i++)
            TestCase.assertTrue(("Returned incorrect array: " + i), ((retArray[i]) == (objArray[i])));

        ll.add(50, null);
        argArray = new Integer[101];
        retArray = ll.toArray(argArray);
        TestCase.assertTrue("Returned different array than passed", (retArray == argArray));
        retArray = ll.toArray(argArray);
        TestCase.assertTrue("Returned different array than passed", (retArray == argArray));
        retList = new LinkedList(Arrays.asList(retArray));
        li = ll.iterator();
        ri = retList.iterator();
        while (li.hasNext())
            TestCase.assertTrue("Lists are not equal", ((li.next()) == (ri.next())));

        try {
            ll.toArray(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
        LinkedList<String> lls = new LinkedList<String>();
        lls.add("First");
        lls.add("Second");
        try {
            lls.toArray(argArray);
            TestCase.fail("ArrayStoreException expected");
        } catch (ArrayStoreException e) {
            // expected
        }
    }

    public void test_offer() {
        int origSize = ll.size();
        TestCase.assertTrue("offer() should return true'", ll.offer(objArray[0]));
        TestCase.assertEquals("offer() should add an element as the last one", origSize, ll.lastIndexOf(objArray[0]));
    }

    public void test_poll() {
        for (int i = 0; i < (objArray.length); i++) {
            TestCase.assertEquals("should remove the head", objArray[i], ll.poll());
        }
        TestCase.assertEquals("should be empty", 0, ll.size());
        TestCase.assertNull("should return 'null' if list is empty", ll.poll());
    }

    public void test_remove() {
        for (int i = 0; i < (objArray.length); i++) {
            TestCase.assertEquals("should remove the head", objArray[i], ll.remove());
        }
        TestCase.assertEquals("should be empty", 0, ll.size());
        try {
            ll.remove();
            TestCase.fail("NoSuchElementException is expected when removing from the empty list");
        } catch (NoSuchElementException e) {
            // -- expected
        }
    }

    public void test_element() {
        TestCase.assertEquals("should return the head", objArray[0], ll.element());
        TestCase.assertEquals("element() should remove nothing", objArray.length, ll.size());
        try {
            new LinkedList().remove();
            TestCase.fail("NoSuchElementException is expected when the list is empty");
        } catch (NoSuchElementException e) {
            // -- expected
        }
    }

    /**
     * {@link java.util.LinkedList#removeFirstOccurrence(Object)}
     */
    public void test_removeFirstOccurrence() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertEquals(5, testList.size());
        TestCase.assertTrue(testList.removeFirstOccurrence(testObjOne));
        TestCase.assertFalse(testList.removeFirstOccurrence(testObjFour));
        TestCase.assertEquals(testObjTwo, testList.peekFirst());
        TestCase.assertEquals(testObjOne, testList.peekLast());
        TestCase.assertEquals(4, testList.size());
        TestCase.assertTrue(testList.removeFirstOccurrence(testObjOne));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.peekLast());
        TestCase.assertTrue(testList.removeFirstOccurrence(testObjOne));
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjThree, testList.peekLast());
        TestCase.assertFalse(testList.removeFirstOccurrence(testObjOne));
    }

    /**
     * {@link java.util.LinkedList#removeLastOccurrence(Object)}
     */
    public void test_removeLastOccurrence() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertEquals(5, testList.size());
        TestCase.assertTrue(testList.removeLastOccurrence(testObjOne));
        TestCase.assertFalse(testList.removeLastOccurrence(testObjFour));
        TestCase.assertEquals(testObjOne, testList.peekFirst());
        TestCase.assertEquals(testObjThree, testList.peekLast());
        TestCase.assertEquals(4, testList.size());
        TestCase.assertTrue(testList.removeLastOccurrence(testObjOne));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.peekFirst());
        TestCase.assertEquals(testObjThree, testList.peekLast());
        TestCase.assertTrue(testList.removeLastOccurrence(testObjOne));
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjThree, testList.peekLast());
        TestCase.assertFalse(testList.removeLastOccurrence(testObjOne));
    }

    /**
     * {@link java.util.LinkedList#offerFirst(Object)}
     */
    public void test_offerFirst() throws Exception {
        TestCase.assertTrue(testList.offerFirst(testObjOne));
        TestCase.assertEquals(1, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        TestCase.assertTrue(testList.offerFirst(testObjOne));
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        TestCase.assertTrue(testList.offerFirst(testObjTwo));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjTwo, testList.peek());
        TestCase.assertEquals(testObjOne, testList.getLast());
        TestCase.assertTrue(testList.offerFirst(null));
        TestCase.assertEquals(4, testList.size());
    }

    /**
     * {@link java.util.LinkedList#offerLast(Object)}
     */
    public void test_offerLast() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertEquals(1, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        TestCase.assertEquals(testObjTwo, testList.getLast());
        TestCase.assertTrue(testList.offerLast(null));
        TestCase.assertEquals(4, testList.size());
    }

    /**
     * {@link java.util.LinkedList#push(Object)}
     */
    public void test_push() throws Exception {
        testList.push(testObjOne);
        TestCase.assertEquals(1, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        testList.push(testObjOne);
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjOne, testList.peek());
        testList.push(testObjTwo);
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjTwo, testList.peek());
        TestCase.assertEquals(testObjOne, testList.getLast());
        testList.push(null);
        TestCase.assertEquals(4, testList.size());
    }

    /**
     * {@link java.util.LinkedList#pop()}
     */
    public void test_pop() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.pop());
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjTwo, testList.pop());
        TestCase.assertEquals(testObjThree, testList.pop());
        TestCase.assertEquals(0, testList.size());
        testList.push(null);
        TestCase.assertEquals(1, testList.size());
        TestCase.assertNull(testList.pop());
        try {
            testList.pop();
            TestCase.fail("should throw NoSuchElementException ");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * {@link java.util.LinkedList#descendingIterator()}
     */
    public void test_descendingIterator() throws Exception {
        TestCase.assertFalse(testList.descendingIterator().hasNext());
        TestCase.assertTrue(testList.add(testObjOne));
        TestCase.assertTrue(testList.add(testObjTwo));
        TestCase.assertTrue(testList.add(testObjOne));
        TestCase.assertTrue(testList.add(testObjThree));
        TestCase.assertTrue(testList.add(testObjLast));
        Iterator result = testList.descendingIterator();
        TestCase.assertEquals(5, testList.size());
        try {
            result.remove();
            TestCase.fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        TestCase.assertTrue(testList.add(testObjFour));
        try {
            TestCase.assertEquals(testObjLast, result.next());
            TestCase.fail("should throw ConcurrentModificationException");
        } catch (ConcurrentModificationException e) {
            // expected
        }
        result = testList.descendingIterator();
        TestCase.assertEquals(testObjFour, result.next());
        TestCase.assertEquals(testObjLast, result.next());
        TestCase.assertEquals(testObjThree, result.next());
        TestCase.assertEquals(testObjOne, result.next());
        TestCase.assertEquals(testObjTwo, result.next());
        TestCase.assertTrue(result.hasNext());
        result.remove();
        TestCase.assertEquals(testObjOne, result.next());
        TestCase.assertFalse(result.hasNext());
        try {
            result.next();
            TestCase.fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * {@link java.util.LinkedList#pollFirst()}
     */
    public void test_pollFirst() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.pollFirst());
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjTwo, testList.pollFirst());
        TestCase.assertEquals(testObjThree, testList.pollFirst());
        TestCase.assertEquals(0, testList.size());
        TestCase.assertNull(testList.pollFirst());
    }

    /**
     * {@link java.util.LinkedList#pollLast()}
     */
    public void test_pollLast() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjThree, testList.pollLast());
        TestCase.assertEquals(2, testList.size());
        TestCase.assertEquals(testObjTwo, testList.pollLast());
        TestCase.assertEquals(testObjOne, testList.pollLast());
        TestCase.assertEquals(0, testList.size());
        TestCase.assertNull(testList.pollFirst());
    }

    /**
     * {@link java.util.LinkedList#peekFirst()}
     */
    public void test_peekFirst() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.peekFirst());
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjOne, testList.pollFirst());
        TestCase.assertEquals(testObjTwo, testList.peekFirst());
        TestCase.assertEquals(testObjTwo, testList.pollFirst());
        TestCase.assertEquals(testObjThree, testList.pollFirst());
        TestCase.assertEquals(0, testList.size());
        TestCase.assertEquals(null, testList.peekFirst());
    }

    /**
     * {@link java.util.LinkedList#peek()}
     */
    public void test_peekLast() throws Exception {
        TestCase.assertTrue(testList.offerLast(testObjOne));
        TestCase.assertTrue(testList.offerLast(testObjTwo));
        TestCase.assertTrue(testList.offerLast(testObjThree));
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjThree, testList.peekLast());
        TestCase.assertEquals(3, testList.size());
        TestCase.assertEquals(testObjThree, testList.pollLast());
        TestCase.assertEquals(testObjTwo, testList.peekLast());
        TestCase.assertEquals(testObjTwo, testList.pollLast());
        TestCase.assertEquals(testObjOne, testList.pollLast());
        TestCase.assertEquals(0, testList.size());
        TestCase.assertNull(testList.peekLast());
    }

    public void test_forEachRemaining_iterator() throws Exception {
        ForEachRemainingTester.runTests(LinkedList.class, new String[]{ "foo", "bar", "baz " });
        ForEachRemainingTester.runTests(LinkedList.class, new String[]{ "foo" });
    }

    public void test_spliterator() throws Exception {
        ArrayList<Integer> testElements = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
        LinkedList<Integer> list = new LinkedList<>();
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
        LinkedList<Integer> list = new LinkedList<>();
        list.add(52);
        Spliterator<Integer> sp = list.spliterator();
        try {
            sp.tryAdvance(( value) -> list.add(value));
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
        try {
            sp.forEachRemaining(( value) -> list.add(value));
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void test_removeIf() {
        runBasicRemoveIfTests(LinkedList<Integer>::new);
        runBasicRemoveIfTestsUnordered(LinkedList<Integer>::new);
        runRemoveIfOnEmpty(LinkedList<Integer>::new);
        testRemoveIfNPE(LinkedList<Integer>::new);
        testRemoveIfCME(LinkedList<Integer>::new);
    }

    /**
     * java.util.LinkedList#Serialization()
     */
    public void test_serialization() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        TestCase.assertTrue(ll.add(new Integer(1)));
        TestCase.assertTrue(ll.add(new Integer(2)));
        TestCase.assertTrue(ll.add(new Integer(3)));
        TestCase.assertTrue(ll.add(new Integer(4)));
        TestCase.assertTrue(ll.add(new Integer(5)));
        SerializationTest.verifySelf(ll, new SerializableAssert() {
            public void assertDeserialized(Serializable initial, Serializable deserialized) {
                LinkedList<Object> formerQue = ((LinkedList) (initial));
                LinkedList<Object> deserializedQue = ((LinkedList) (deserialized));
                TestCase.assertEquals(formerQue.remove(), deserializedQue.remove());
            }
        });
    }
}

