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


import com.google.protobuf.Internal.LongList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import junit.framework.TestCase;


/**
 * Tests for {@link LongArrayList}.
 *
 * @author dweis@google.com (Daniel Weis)
 */
public class LongArrayListTest extends TestCase {
    private static final LongArrayList UNARY_LIST = LongArrayListTest.newImmutableLongArrayList(1);

    private static final LongArrayList TERTIARY_LIST = LongArrayListTest.newImmutableLongArrayList(1, 2, 3);

    private LongArrayList list;

    public void testEmptyListReturnsSameInstance() {
        TestCase.assertSame(LongArrayList.emptyList(), LongArrayList.emptyList());
    }

    public void testEmptyListIsImmutable() {
        assertImmutable(LongArrayList.emptyList());
    }

    public void testMakeImmutable() {
        list.addLong(3);
        list.addLong(4);
        list.addLong(5);
        list.addLong(7);
        list.makeImmutable();
        assertImmutable(list);
    }

    public void testModificationWithIteration() {
        list.addAll(Arrays.asList(1L, 2L, 3L, 4L));
        Iterator<Long> iterator = list.iterator();
        TestCase.assertEquals(4, list.size());
        TestCase.assertEquals(1L, ((long) (list.get(0))));
        TestCase.assertEquals(1L, ((long) (iterator.next())));
        list.set(0, 1L);
        TestCase.assertEquals(2L, ((long) (iterator.next())));
        list.remove(0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
        iterator = list.iterator();
        list.add(0, 0L);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testGet() {
        TestCase.assertEquals(1L, ((long) (LongArrayListTest.TERTIARY_LIST.get(0))));
        TestCase.assertEquals(2L, ((long) (LongArrayListTest.TERTIARY_LIST.get(1))));
        TestCase.assertEquals(3L, ((long) (LongArrayListTest.TERTIARY_LIST.get(2))));
        try {
            LongArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            LongArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testGetLong() {
        TestCase.assertEquals(1L, LongArrayListTest.TERTIARY_LIST.getLong(0));
        TestCase.assertEquals(2L, LongArrayListTest.TERTIARY_LIST.getLong(1));
        TestCase.assertEquals(3L, LongArrayListTest.TERTIARY_LIST.getLong(2));
        try {
            LongArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            LongArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSize() {
        TestCase.assertEquals(0, LongArrayList.emptyList().size());
        TestCase.assertEquals(1, LongArrayListTest.UNARY_LIST.size());
        TestCase.assertEquals(3, LongArrayListTest.TERTIARY_LIST.size());
        list.addLong(3);
        list.addLong(4);
        list.addLong(6);
        list.addLong(8);
        TestCase.assertEquals(4, list.size());
        list.remove(0);
        TestCase.assertEquals(3, list.size());
        list.add(17L);
        TestCase.assertEquals(4, list.size());
    }

    public void testSet() {
        list.addLong(2);
        list.addLong(4);
        TestCase.assertEquals(2L, ((long) (list.set(0, 3L))));
        TestCase.assertEquals(3L, list.getLong(0));
        TestCase.assertEquals(4L, ((long) (list.set(1, 0L))));
        TestCase.assertEquals(0L, list.getLong(1));
        try {
            list.set((-1), 0L);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.set(2, 0L);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSetLong() {
        list.addLong(1);
        list.addLong(3);
        TestCase.assertEquals(1L, list.setLong(0, 0));
        TestCase.assertEquals(0L, list.getLong(0));
        TestCase.assertEquals(3L, list.setLong(1, 0));
        TestCase.assertEquals(0L, list.getLong(1));
        try {
            list.setLong((-1), 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.setLong(2, 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAdd() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.add(2L));
        TestCase.assertEquals(Arrays.asList(2L), list);
        TestCase.assertTrue(list.add(3L));
        list.add(0, 4L);
        TestCase.assertEquals(Arrays.asList(4L, 2L, 3L), list);
        list.add(0, 1L);
        list.add(0, 0L);
        // Force a resize by getting up to 11 elements.
        for (int i = 0; i < 6; i++) {
            list.add(Long.valueOf((5 + i)));
        }
        TestCase.assertEquals(Arrays.asList(0L, 1L, 4L, 2L, 3L, 5L, 6L, 7L, 8L, 9L, 10L), list);
        try {
            list.add((-1), 5L);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.add(4, 5L);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAddLong() {
        TestCase.assertEquals(0, list.size());
        list.addLong(2);
        TestCase.assertEquals(Arrays.asList(2L), list);
        list.addLong(3);
        TestCase.assertEquals(Arrays.asList(2L, 3L), list);
    }

    public void testAddAll() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.addAll(Collections.singleton(1L)));
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(1L, ((long) (list.get(0))));
        TestCase.assertEquals(1L, list.getLong(0));
        TestCase.assertTrue(list.addAll(Arrays.asList(2L, 3L, 4L, 5L, 6L)));
        TestCase.assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L), list);
        TestCase.assertTrue(list.addAll(LongArrayListTest.TERTIARY_LIST));
        TestCase.assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 1L, 2L, 3L), list);
        TestCase.assertFalse(list.addAll(Collections.<Long>emptyList()));
        TestCase.assertFalse(list.addAll(LongArrayList.emptyList()));
    }

    public void testRemove() {
        list.addAll(LongArrayListTest.TERTIARY_LIST);
        TestCase.assertEquals(1L, ((long) (list.remove(0))));
        TestCase.assertEquals(Arrays.asList(2L, 3L), list);
        TestCase.assertTrue(list.remove(Long.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2L), list);
        TestCase.assertFalse(list.remove(Long.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2L), list);
        TestCase.assertEquals(2L, ((long) (list.remove(0))));
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
        LongList toRemove = LongArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addLong(3);
        toRemove.remove(0);
        TestCase.assertEquals(0, toRemove.size());
    }

    public void testSublistRemoveEndOfCapacity() {
        LongList toRemove = LongArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addLong(3);
        toRemove.subList(0, 1).clear();
        TestCase.assertEquals(0, toRemove.size());
    }
}

