/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import com.google.protobuf.Internal.IntList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import junit.framework.TestCase;


/**
 * Tests for {@link IntArrayList}.
 *
 * @author dweis@google.com (Daniel Weis)
 */
public class IntArrayListTest extends TestCase {
    private static final IntArrayList UNARY_LIST = IntArrayListTest.newImmutableIntArrayList(1);

    private static final IntArrayList TERTIARY_LIST = IntArrayListTest.newImmutableIntArrayList(1, 2, 3);

    private IntArrayList list;

    public void testEmptyListReturnsSameInstance() {
        TestCase.assertSame(IntArrayList.emptyList(), IntArrayList.emptyList());
    }

    public void testEmptyListIsImmutable() {
        assertImmutable(IntArrayList.emptyList());
    }

    public void testMakeImmutable() {
        list.addInt(3);
        list.addInt(4);
        list.addInt(5);
        list.addInt(7);
        list.makeImmutable();
        assertImmutable(list);
    }

    public void testModificationWithIteration() {
        list.addAll(Arrays.asList(1, 2, 3, 4));
        Iterator<Integer> iterator = list.iterator();
        TestCase.assertEquals(4, list.size());
        TestCase.assertEquals(1, ((int) (list.get(0))));
        TestCase.assertEquals(1, ((int) (iterator.next())));
        list.set(0, 1);
        TestCase.assertEquals(2, ((int) (iterator.next())));
        list.remove(0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
        iterator = list.iterator();
        list.add(0, 0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testGet() {
        TestCase.assertEquals(1, ((int) (IntArrayListTest.TERTIARY_LIST.get(0))));
        TestCase.assertEquals(2, ((int) (IntArrayListTest.TERTIARY_LIST.get(1))));
        TestCase.assertEquals(3, ((int) (IntArrayListTest.TERTIARY_LIST.get(2))));
        try {
            IntArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            IntArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testGetInt() {
        TestCase.assertEquals(1, IntArrayListTest.TERTIARY_LIST.getInt(0));
        TestCase.assertEquals(2, IntArrayListTest.TERTIARY_LIST.getInt(1));
        TestCase.assertEquals(3, IntArrayListTest.TERTIARY_LIST.getInt(2));
        try {
            IntArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            IntArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSize() {
        TestCase.assertEquals(0, IntArrayList.emptyList().size());
        TestCase.assertEquals(1, IntArrayListTest.UNARY_LIST.size());
        TestCase.assertEquals(3, IntArrayListTest.TERTIARY_LIST.size());
        list.addInt(3);
        list.addInt(4);
        list.addInt(6);
        list.addInt(8);
        TestCase.assertEquals(4, list.size());
        list.remove(0);
        TestCase.assertEquals(3, list.size());
        list.add(17);
        TestCase.assertEquals(4, list.size());
    }

    public void testSet() {
        list.addInt(2);
        list.addInt(4);
        TestCase.assertEquals(2, ((int) (list.set(0, 3))));
        TestCase.assertEquals(3, list.getInt(0));
        TestCase.assertEquals(4, ((int) (list.set(1, 0))));
        TestCase.assertEquals(0, list.getInt(1));
        try {
            list.set((-1), 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.set(2, 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSetInt() {
        list.addInt(1);
        list.addInt(3);
        TestCase.assertEquals(1, list.setInt(0, 0));
        TestCase.assertEquals(0, list.getInt(0));
        TestCase.assertEquals(3, list.setInt(1, 0));
        TestCase.assertEquals(0, list.getInt(1));
        try {
            list.setInt((-1), 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.setInt(2, 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAdd() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.add(2));
        TestCase.assertEquals(Arrays.asList(2), list);
        TestCase.assertTrue(list.add(3));
        list.add(0, 4);
        TestCase.assertEquals(Arrays.asList(4, 2, 3), list);
        list.add(0, 1);
        list.add(0, 0);
        // Force a resize by getting up to 11 elements.
        for (int i = 0; i < 6; i++) {
            list.add(Integer.valueOf((5 + i)));
        }
        TestCase.assertEquals(Arrays.asList(0, 1, 4, 2, 3, 5, 6, 7, 8, 9, 10), list);
        try {
            list.add((-1), 5);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.add(4, 5);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAddInt() {
        TestCase.assertEquals(0, list.size());
        list.addInt(2);
        TestCase.assertEquals(Arrays.asList(2), list);
        list.addInt(3);
        TestCase.assertEquals(Arrays.asList(2, 3), list);
    }

    public void testAddAll() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.addAll(Collections.singleton(1)));
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(1, ((int) (list.get(0))));
        TestCase.assertEquals(1, list.getInt(0));
        TestCase.assertTrue(list.addAll(Arrays.asList(2, 3, 4, 5, 6)));
        TestCase.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), list);
        TestCase.assertTrue(list.addAll(IntArrayListTest.TERTIARY_LIST));
        TestCase.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 1, 2, 3), list);
        TestCase.assertFalse(list.addAll(Collections.<Integer>emptyList()));
        TestCase.assertFalse(list.addAll(IntArrayList.emptyList()));
    }

    public void testRemove() {
        list.addAll(IntArrayListTest.TERTIARY_LIST);
        TestCase.assertEquals(1, ((int) (list.remove(0))));
        TestCase.assertEquals(Arrays.asList(2, 3), list);
        TestCase.assertTrue(list.remove(Integer.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2), list);
        TestCase.assertFalse(list.remove(Integer.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2), list);
        TestCase.assertEquals(2, ((int) (list.remove(0))));
        TestCase.assertEquals(Arrays.asList(), list);
        try {
            list.remove((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.remove(0);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testRemoveEndOfCapacity() {
        IntList toRemove = IntArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addInt(3);
        toRemove.remove(0);
        TestCase.assertEquals(0, toRemove.size());
    }

    public void testSublistRemoveEndOfCapacity() {
        IntList toRemove = IntArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addInt(3);
        toRemove.subList(0, 1).clear();
        TestCase.assertEquals(0, toRemove.size());
    }
}

