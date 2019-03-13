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


import com.google.protobuf.Internal.DoubleList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import junit.framework.TestCase;


/**
 * Tests for {@link DoubleArrayList}.
 *
 * @author dweis@google.com (Daniel Weis)
 */
public class DoubleArrayListTest extends TestCase {
    private static final DoubleArrayList UNARY_LIST = DoubleArrayListTest.newImmutableDoubleArrayList(1);

    private static final DoubleArrayList TERTIARY_LIST = DoubleArrayListTest.newImmutableDoubleArrayList(1, 2, 3);

    private DoubleArrayList list;

    public void testEmptyListReturnsSameInstance() {
        TestCase.assertSame(DoubleArrayList.emptyList(), DoubleArrayList.emptyList());
    }

    public void testEmptyListIsImmutable() {
        assertImmutable(DoubleArrayList.emptyList());
    }

    public void testMakeImmutable() {
        list.addDouble(3);
        list.addDouble(4);
        list.addDouble(5);
        list.addDouble(7);
        list.makeImmutable();
        assertImmutable(list);
    }

    public void testModificationWithIteration() {
        list.addAll(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Iterator<Double> iterator = list.iterator();
        TestCase.assertEquals(4, list.size());
        TestCase.assertEquals(1.0, ((double) (list.get(0))));
        TestCase.assertEquals(1.0, ((double) (iterator.next())));
        list.set(0, 1.0);
        TestCase.assertEquals(2.0, ((double) (iterator.next())));
        list.remove(0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
        iterator = list.iterator();
        list.add(0, 0.0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testGet() {
        TestCase.assertEquals(1.0, ((double) (DoubleArrayListTest.TERTIARY_LIST.get(0))));
        TestCase.assertEquals(2.0, ((double) (DoubleArrayListTest.TERTIARY_LIST.get(1))));
        TestCase.assertEquals(3.0, ((double) (DoubleArrayListTest.TERTIARY_LIST.get(2))));
        try {
            DoubleArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            DoubleArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testGetDouble() {
        TestCase.assertEquals(1.0, DoubleArrayListTest.TERTIARY_LIST.getDouble(0));
        TestCase.assertEquals(2.0, DoubleArrayListTest.TERTIARY_LIST.getDouble(1));
        TestCase.assertEquals(3.0, DoubleArrayListTest.TERTIARY_LIST.getDouble(2));
        try {
            DoubleArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            DoubleArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSize() {
        TestCase.assertEquals(0, DoubleArrayList.emptyList().size());
        TestCase.assertEquals(1, DoubleArrayListTest.UNARY_LIST.size());
        TestCase.assertEquals(3, DoubleArrayListTest.TERTIARY_LIST.size());
        list.addDouble(3);
        list.addDouble(4);
        list.addDouble(6);
        list.addDouble(8);
        TestCase.assertEquals(4, list.size());
        list.remove(0);
        TestCase.assertEquals(3, list.size());
        list.add(17.0);
        TestCase.assertEquals(4, list.size());
    }

    public void testSet() {
        list.addDouble(2);
        list.addDouble(4);
        TestCase.assertEquals(2.0, ((double) (list.set(0, 3.0))));
        TestCase.assertEquals(3.0, list.getDouble(0));
        TestCase.assertEquals(4.0, ((double) (list.set(1, 0.0))));
        TestCase.assertEquals(0.0, list.getDouble(1));
        try {
            list.set((-1), 0.0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.set(2, 0.0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSetDouble() {
        list.addDouble(1);
        list.addDouble(3);
        TestCase.assertEquals(1.0, list.setDouble(0, 0));
        TestCase.assertEquals(0.0, list.getDouble(0));
        TestCase.assertEquals(3.0, list.setDouble(1, 0));
        TestCase.assertEquals(0.0, list.getDouble(1));
        try {
            list.setDouble((-1), 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.setDouble(2, 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAdd() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.add(2.0));
        TestCase.assertEquals(Arrays.asList(2.0), list);
        TestCase.assertTrue(list.add(3.0));
        list.add(0, 4.0);
        TestCase.assertEquals(Arrays.asList(4.0, 2.0, 3.0), list);
        list.add(0, 1.0);
        list.add(0, 0.0);
        // Force a resize by getting up to 11 elements.
        for (int i = 0; i < 6; i++) {
            list.add(Double.valueOf((5 + i)));
        }
        TestCase.assertEquals(Arrays.asList(0.0, 1.0, 4.0, 2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0), list);
        try {
            list.add((-1), 5.0);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.add(4, 5.0);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAddDouble() {
        TestCase.assertEquals(0, list.size());
        list.addDouble(2);
        TestCase.assertEquals(Arrays.asList(2.0), list);
        list.addDouble(3);
        TestCase.assertEquals(Arrays.asList(2.0, 3.0), list);
    }

    public void testAddAll() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.addAll(Collections.singleton(1.0)));
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(1.0, ((double) (list.get(0))));
        TestCase.assertEquals(1.0, list.getDouble(0));
        TestCase.assertTrue(list.addAll(Arrays.asList(2.0, 3.0, 4.0, 5.0, 6.0)));
        TestCase.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0), list);
        TestCase.assertTrue(list.addAll(DoubleArrayListTest.TERTIARY_LIST));
        TestCase.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 1.0, 2.0, 3.0), list);
        TestCase.assertFalse(list.addAll(Collections.<Double>emptyList()));
        TestCase.assertFalse(list.addAll(DoubleArrayList.emptyList()));
    }

    public void testRemove() {
        list.addAll(DoubleArrayListTest.TERTIARY_LIST);
        TestCase.assertEquals(1.0, ((double) (list.remove(0))));
        TestCase.assertEquals(Arrays.asList(2.0, 3.0), list);
        TestCase.assertTrue(list.remove(Double.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2.0), list);
        TestCase.assertFalse(list.remove(Double.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2.0), list);
        TestCase.assertEquals(2.0, ((double) (list.remove(0))));
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
        DoubleList toRemove = DoubleArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addDouble(3);
        toRemove.remove(0);
        TestCase.assertEquals(0, toRemove.size());
    }

    public void testSublistRemoveEndOfCapacity() {
        DoubleList toRemove = DoubleArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addDouble(3);
        toRemove.subList(0, 1).clear();
        TestCase.assertEquals(0, toRemove.size());
    }
}

