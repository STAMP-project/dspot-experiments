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


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import junit.framework.TestCase;


public class AbstractListTest extends TestCase {
    static class SimpleList extends AbstractList {
        ArrayList arrayList;

        SimpleList() {
            this.arrayList = new ArrayList();
        }

        public Object get(int index) {
            return this.arrayList.get(index);
        }

        public void add(int i, Object o) {
            this.arrayList.add(i, o);
        }

        public Object remove(int i) {
            return this.arrayList.remove(i);
        }

        public int size() {
            return this.arrayList.size();
        }
    }

    /**
     * java.util.AbstractList#hashCode()
     */
    public void test_hashCode() {
        List list = new ArrayList();
        list.add(new Integer(3));
        list.add(new Integer(15));
        list.add(new Integer(5));
        list.add(new Integer(1));
        list.add(new Integer(7));
        int hashCode = 1;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            hashCode = (31 * hashCode) + (obj == null ? 0 : obj.hashCode());
        } 
        TestCase.assertTrue(((("Incorrect hashCode returned.  Wanted: " + hashCode) + " got: ") + (list.hashCode())), (hashCode == (list.hashCode())));
    }

    /**
     * java.util.AbstractList#iterator()
     */
    public void test_iterator() {
        AbstractListTest.SimpleList list = new AbstractListTest.SimpleList();
        list.add(new Object());
        list.add(new Object());
        Iterator it = list.iterator();
        it.next();
        it.remove();
        it.next();
    }

    /**
     * java.util.AbstractList#listIterator()
     */
    public void test_listIterator() {
        Integer tempValue;
        List list = new ArrayList();
        list.add(new Integer(3));
        list.add(new Integer(15));
        list.add(new Integer(5));
        list.add(new Integer(1));
        list.add(new Integer(7));
        ListIterator lit = list.listIterator();
        TestCase.assertTrue("Should not have previous", (!(lit.hasPrevious())));
        TestCase.assertTrue("Should have next", lit.hasNext());
        tempValue = ((Integer) (lit.next()));
        TestCase.assertTrue(("next returned wrong value.  Wanted 3, got: " + tempValue), ((tempValue.intValue()) == 3));
        tempValue = ((Integer) (lit.previous()));
        AbstractListTest.SimpleList list2 = new AbstractListTest.SimpleList();
        list2.add(new Object());
        ListIterator lit2 = list2.listIterator();
        lit2.add(new Object());
        lit2.next();
    }

    /**
     * java.util.AbstractList#subList(int, int)
     */
    public void test_subListII() {
        // Test each of the SubList operations to ensure a
        // ConcurrentModificationException does not occur on an AbstractList
        // which does not update modCount
        AbstractListTest.SimpleList mList = new AbstractListTest.SimpleList();
        mList.add(new Object());
        mList.add(new Object());
        List sList = mList.subList(0, 2);
        sList.add(new Object());// calls add(int, Object)

        sList.get(0);
        sList.add(0, new Object());
        sList.get(0);
        sList.addAll(Arrays.asList(new String[]{ "1", "2" }));
        sList.get(0);
        sList.addAll(0, Arrays.asList(new String[]{ "3", "4" }));
        sList.get(0);
        sList.remove(0);
        sList.get(0);
        ListIterator lit = sList.listIterator();
        lit.add(new Object());
        lit.next();
        lit.remove();
        lit.next();
        sList.clear();// calls removeRange()

        sList.add(new Object());
        // test the type of sublist that is returned
        List al = new ArrayList();
        for (int i = 0; i < 10; i++) {
            al.add(new Integer(i));
        }
        TestCase.assertTrue("Sublist returned should have implemented Random Access interface", ((al.subList(3, 7)) instanceof RandomAccess));
        List ll = new LinkedList();
        for (int i = 0; i < 10; i++) {
            ll.add(new Integer(i));
        }
        TestCase.assertTrue("Sublist returned should not have implemented Random Access interface", (!((ll.subList(3, 7)) instanceof RandomAccess)));
    }

    /**
     * java.util.AbstractList#subList(int, int)
     */
    public void test_subList_empty() {
        // Regression for HARMONY-389
        List al = new ArrayList();
        al.add("one");
        List emptySubList = al.subList(0, 0);
        try {
            emptySubList.get(0);
            TestCase.fail("emptySubList.get(0) should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            emptySubList.set(0, "one");
            TestCase.fail("emptySubList.set(0,Object) should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            emptySubList.remove(0);
            TestCase.fail("emptySubList.remove(0) should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.util.AbstractList#subList(int, int)
     */
    public void test_subList_addAll() {
        // Regression for HARMONY-390
        List mainList = new ArrayList();
        Object[] mainObjects = new Object[]{ "a", "b", "c" };
        mainList.addAll(Arrays.asList(mainObjects));
        List subList = mainList.subList(1, 2);
        TestCase.assertFalse("subList should not contain \"a\"", subList.contains("a"));
        TestCase.assertFalse("subList should not contain \"c\"", subList.contains("c"));
        TestCase.assertTrue("subList should contain \"b\"", subList.contains("b"));
        Object[] subObjects = new Object[]{ "one", "two", "three" };
        subList.addAll(Arrays.asList(subObjects));
        TestCase.assertFalse("subList should not contain \"a\"", subList.contains("a"));
        TestCase.assertFalse("subList should not contain \"c\"", subList.contains("c"));
        Object[] expected = new Object[]{ "b", "one", "two", "three" };
        ListIterator iter = subList.listIterator();
        for (int i = 0; i < (expected.length); i++) {
            TestCase.assertTrue(("subList should contain " + (expected[i])), subList.contains(expected[i]));
            TestCase.assertTrue("should be more elements", iter.hasNext());
            TestCase.assertEquals("element in incorrect position", expected[i], iter.next());
        }
    }

    public void test_indexOfLjava_lang_Object() {
        AbstractList al = new ArrayList();
        al.add(0);
        al.add(1);
        al.add(2);
        al.add(3);
        al.add(4);
        TestCase.assertEquals((-1), al.indexOf(5));
        TestCase.assertEquals(2, al.indexOf(2));
    }

    public void test_lastIndexOfLjava_lang_Object() {
        AbstractList al = new ArrayList();
        al.add(0);
        al.add(1);
        al.add(2);
        al.add(2);
        al.add(2);
        al.add(2);
        al.add(2);
        al.add(3);
        al.add(4);
        TestCase.assertEquals((-1), al.lastIndexOf(5));
        TestCase.assertEquals(6, al.lastIndexOf(2));
    }

    public void test_listIteratorI() {
        AbstractList al1 = new ArrayList();
        AbstractList al2 = new ArrayList();
        al1.add(0);
        al1.add(1);
        al1.add(2);
        al1.add(3);
        al1.add(4);
        al2.add(2);
        al2.add(3);
        al2.add(4);
        Iterator li1 = al1.listIterator(2);
        Iterator li2 = al2.listIterator();
        while ((li1.hasNext()) && (li2.hasNext())) {
            TestCase.assertEquals(li1.next(), li2.next());
        } 
        TestCase.assertSame(li1.hasNext(), li2.hasNext());
        try {
            al1.listIterator((-1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException ee) {
            // expected
        }
        try {
            al1.listIterator(((al1.size()) + 1));
            TestCase.fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException ee) {
            // expected
        }
    }
}

