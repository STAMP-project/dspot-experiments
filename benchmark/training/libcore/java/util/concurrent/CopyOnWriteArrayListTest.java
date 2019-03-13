/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.util.concurrent;


import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import junit.framework.TestCase;
import libcore.util.SerializationTester;


public final class CopyOnWriteArrayListTest extends TestCase {
    public void testIteratorAndNonStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        Iterator<String> abcde = list.iterator();
        TestCase.assertEquals("a", abcde.next());
        list.set(1, "B");
        TestCase.assertEquals("b", abcde.next());
        TestCase.assertEquals("c", abcde.next());
        TestCase.assertEquals("d", abcde.next());
        TestCase.assertEquals("e", abcde.next());
    }

    /**
     * The sub list throws on non-structural changes, even though that disagrees
     * with the subList() documentation which suggests that only size-changing
     * operations will trigger ConcurrentModificationException.
     */
    public void testSubListAndNonStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        list.set(2, "C");
        try {
            bcd.get(1);
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void testSubListAndStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        list.clear();
        try {
            bcd.get(1);
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void testSubListAndSizePreservingStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        list.clear();
        list.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        try {
            bcd.get(1);
            TestCase.fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void testRemoveAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        list.removeAll(Arrays.asList());
        TestCase.assertEquals(Arrays.asList("a", "b", "c", "d", "e"), list);
        list.removeAll(Arrays.asList("e"));
        TestCase.assertEquals(Arrays.asList("a", "b", "c", "d"), list);
        list.removeAll(Arrays.asList("b", "c"));
        TestCase.assertEquals(Arrays.asList("a", "d"), list);
    }

    public void testSubListClear() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.clear();
        TestCase.assertEquals(Arrays.asList("a", "e"), list);
        bcd.addAll(Arrays.asList("B", "C", "D"));
        TestCase.assertEquals(Arrays.asList("a", "B", "C", "D", "e"), list);
    }

    public void testSubListClearWhenEmpty() {
        new CopyOnWriteArrayList<String>().subList(0, 0).clear();// the RI fails here

    }

    public void testSubListIteratorGetsSnapshot() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        Iterator<String> bcd = list.subList(1, 4).iterator();
        list.clear();
        TestCase.assertEquals("b", bcd.next());
        TestCase.assertEquals("c", bcd.next());
        TestCase.assertEquals("d", bcd.next());
        TestCase.assertFalse(bcd.hasNext());
    }

    public void testSubListRemoveByValue() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.remove("c");// the RI fails here

        TestCase.assertEquals(Arrays.asList("b", "d"), bcd);
        TestCase.assertEquals(Arrays.asList("a", "b", "d", "e"), list);
    }

    public void testSubListRemoveByIndex() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.remove(1);
        TestCase.assertEquals(Arrays.asList("b", "d"), bcd);
        TestCase.assertEquals(Arrays.asList("a", "b", "d", "e"), list);
    }

    public void testSubListRetainAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        List<String> def = list.subList(3, 6);
        def.retainAll(Arrays.asList("c", "e", "h"));// the RI fails here

        TestCase.assertEquals(Arrays.asList("a", "b", "c", "e", "g", "h", "i"), list);
        TestCase.assertEquals(Arrays.asList("e"), def);
    }

    public void testSubListRemoveAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        List<String> def = list.subList(3, 6);
        def.removeAll(Arrays.asList("c", "e", "h"));// the RI fails here

        TestCase.assertEquals(Arrays.asList("a", "b", "c", "d", "f", "g", "h", "i"), list);
        TestCase.assertEquals(Arrays.asList("d", "f"), def);
    }

    public void testAtomicAdds() throws Exception {
        testAddAllIsAtomic(new CopyOnWriteArrayList<Object>());
    }

    public void testSubListAtomicAdds() throws Exception {
        testAddAllIsAtomic(new CopyOnWriteArrayList<Object>().subList(0, 0));
    }

    public void testSubListAddIsAtEnd() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.add("f");
        TestCase.assertEquals(Arrays.asList("a", "b", "c", "d", "f", "e"), list);
        TestCase.assertEquals(Arrays.asList("b", "c", "d", "f"), bcd);
    }

    public void testSubListAddAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.addAll(1, Arrays.asList("f", "g", "h", "i"));
        TestCase.assertEquals(Arrays.asList("a", "b", "f", "g", "h", "i", "c", "d", "e"), list);
        TestCase.assertEquals(Arrays.asList("b", "f", "g", "h", "i", "c", "d"), bcd);
    }

    public void testListIterator() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        ListIterator<String> i = list.listIterator(5);
        list.clear();
        TestCase.assertEquals(5, i.nextIndex());
        TestCase.assertEquals(4, i.previousIndex());
        TestCase.assertEquals("e", i.previous());
        TestCase.assertEquals(4, i.nextIndex());
        TestCase.assertEquals(3, i.previousIndex());
        TestCase.assertTrue(i.hasNext());
        TestCase.assertTrue(i.hasPrevious());
        TestCase.assertEquals("d", i.previous());
        TestCase.assertEquals(3, i.nextIndex());
        TestCase.assertEquals(2, i.previousIndex());
        TestCase.assertTrue(i.hasNext());
        TestCase.assertTrue(i.hasPrevious());
        TestCase.assertEquals("c", i.previous());
        TestCase.assertEquals(2, i.nextIndex());
        TestCase.assertEquals(1, i.previousIndex());
        TestCase.assertTrue(i.hasNext());
        TestCase.assertTrue(i.hasPrevious());
        TestCase.assertEquals("b", i.previous());
        TestCase.assertEquals(1, i.nextIndex());
        TestCase.assertEquals(0, i.previousIndex());
        TestCase.assertTrue(i.hasNext());
        TestCase.assertTrue(i.hasPrevious());
        TestCase.assertEquals("a", i.previous());
        TestCase.assertEquals(0, i.nextIndex());
        TestCase.assertEquals((-1), i.previousIndex());
        TestCase.assertTrue(i.hasNext());
        TestCase.assertFalse(i.hasPrevious());
        try {
            i.previous();
            TestCase.fail();
        } catch (NoSuchElementException expected) {
        }
    }

    public void testSerialize() {
        String s = "aced0005737200296a6176612e7574696c2e636f6e63757272656e742e436f70" + ("794f6e577269746541727261794c697374785d9fd546ab90c3030000787077040" + "0000005740001617400016274000163707400016578");
        List<String> contents = Arrays.asList("a", "b", "c", null, "e");
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(contents);
        new SerializationTester<CopyOnWriteArrayList<String>>(list, s).test();
    }

    /**
     * Test that we don't retain the array returned by toArray() on the copy
     * constructor. That array may not be of the required type!
     */
    public void testDoesNotRetainToArray() {
        String[] strings = new String[]{ "a", "b", "c" };
        List<String> asList = Arrays.asList(strings);
        TestCase.assertEquals(String[].class, asList.toArray().getClass());
        CopyOnWriteArrayList<Object> objects = new CopyOnWriteArrayList<Object>(asList);
        objects.add(Boolean.TRUE);
    }
}

