/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.util;


import java.util.ListIterator;
import java.util.NoSuchElementException;
import junit.framework.TestCase;


public class OldListIteratorTest extends TestCase {
    ListIterator<Integer> l = null;

    static Object[] objArray;

    {
        OldListIteratorTest.objArray = new Object[100];
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++)
            OldListIteratorTest.objArray[i] = new Integer(i);

    }

    public void testHasNext() {
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            TestCase.assertTrue(l.hasNext());
            l.next();
        }
        TestCase.assertFalse(l.hasNext());
    }

    public void testNext() {
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            TestCase.assertTrue(OldListIteratorTest.objArray[i].equals(l.next()));
        }
        try {
            l.next();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    class Mock_ListIterator implements ListIterator {
        public void add(Object o) {
            if (((String) (o)).equals("Wrong element"))
                throw new IllegalArgumentException();

            if ((o.getClass()) == (Double.class))
                throw new ClassCastException();

            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return false;
        }

        public boolean hasPrevious() {
            return false;
        }

        public Object next() {
            return null;
        }

        public int nextIndex() {
            return 0;
        }

        public Object previous() {
            return null;
        }

        public int previousIndex() {
            return 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(Object o) {
            if (((String) (o)).equals("Wrong element"))
                throw new IllegalArgumentException();

            if ((o.getClass()) == (Double.class))
                throw new ClassCastException();

            throw new UnsupportedOperationException();
        }
    }

    public void testRemove() {
        try {
            l.remove();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            l.next();
            l.remove();
            TestCase.assertFalse(l.hasPrevious());
        }
        try {
            l.remove();
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        OldListIteratorTest.Mock_ListIterator ml = new OldListIteratorTest.Mock_ListIterator();
        try {
            ml.remove();
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testHasPrevious() {
        TestCase.assertFalse(l.hasPrevious());
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            l.next();
            TestCase.assertTrue(l.hasPrevious());
        }
    }

    public void testPrevious() {
        try {
            l.previous();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
        while (l.hasNext()) {
            l.next();
        } 
        for (int i = (OldListIteratorTest.objArray.length) - 1; i > (-1); i--) {
            TestCase.assertTrue(OldListIteratorTest.objArray[i].equals(l.previous()));
        }
        try {
            l.previous();
            TestCase.fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testNextIndex() {
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            TestCase.assertTrue(OldListIteratorTest.objArray[i].equals(l.nextIndex()));
            l.next();
        }
    }

    public void testPreviousIndex() {
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            TestCase.assertTrue(OldListIteratorTest.objArray[i].equals(((l.previousIndex()) + 1)));
            l.next();
        }
    }

    public void testSet() {
        try {
            l.set(new Integer(1));
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        for (int i = 0; i < (OldListIteratorTest.objArray.length); i++) {
            l.next();
            l.set(((Integer) (OldListIteratorTest.objArray[(((OldListIteratorTest.objArray.length) - i) - 1)])));
        }
        l.remove();
        try {
            l.set(new Integer(1));
            TestCase.fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // expected
        }
        OldListIteratorTest.Mock_ListIterator ml = new OldListIteratorTest.Mock_ListIterator();
        ml.next();
        try {
            ml.set("Wrong element");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            ml.set(new Double("3.14"));
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            ml.set("");
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testAdd() {
        l.add(new Integer(33));
        OldListIteratorTest.Mock_ListIterator ml = new OldListIteratorTest.Mock_ListIterator();
        ml.next();
        try {
            ml.add("Wrong element");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            ml.add(new Double("3.14"));
            TestCase.fail("ClassCastException expected");
        } catch (ClassCastException e) {
            // expected
        }
        try {
            ml.add("");
            TestCase.fail("UnsupportedOperationException expected");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
}

