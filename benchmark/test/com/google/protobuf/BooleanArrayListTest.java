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


import com.google.protobuf.Internal.BooleanList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import junit.framework.TestCase;


/**
 * Tests for {@link BooleanArrayList}.
 *
 * @author dweis@google.com (Daniel Weis)
 */
public class BooleanArrayListTest extends TestCase {
    private static final BooleanArrayList UNARY_LIST = BooleanArrayListTest.newImmutableBooleanArrayList(true);

    private static final BooleanArrayList TERTIARY_LIST = BooleanArrayListTest.newImmutableBooleanArrayList(true, false, true);

    private BooleanArrayList list;

    public void testEmptyListReturnsSameInstance() {
        TestCase.assertSame(BooleanArrayList.emptyList(), BooleanArrayList.emptyList());
    }

    public void testEmptyListIsImmutable() {
        assertImmutable(BooleanArrayList.emptyList());
    }

    public void testMakeImmutable() {
        list.addBoolean(true);
        list.addBoolean(false);
        list.addBoolean(true);
        list.addBoolean(true);
        list.makeImmutable();
        assertImmutable(list);
    }

    public void testModificationWithIteration() {
        list.addAll(Arrays.asList(true, false, true, false));
        Iterator<Boolean> iterator = list.iterator();
        TestCase.assertEquals(4, list.size());
        TestCase.assertEquals(true, ((boolean) (list.get(0))));
        TestCase.assertEquals(true, ((boolean) (iterator.next())));
        list.set(0, true);
        TestCase.assertEquals(false, ((boolean) (iterator.next())));
        list.remove(0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
        iterator = list.iterator();
        list.add(0, false);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testGet() {
        TestCase.assertEquals(true, ((boolean) (BooleanArrayListTest.TERTIARY_LIST.get(0))));
        TestCase.assertEquals(false, ((boolean) (BooleanArrayListTest.TERTIARY_LIST.get(1))));
        TestCase.assertEquals(true, ((boolean) (BooleanArrayListTest.TERTIARY_LIST.get(2))));
        try {
            BooleanArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            BooleanArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testGetBoolean() {
        TestCase.assertEquals(true, BooleanArrayListTest.TERTIARY_LIST.getBoolean(0));
        TestCase.assertEquals(false, BooleanArrayListTest.TERTIARY_LIST.getBoolean(1));
        TestCase.assertEquals(true, BooleanArrayListTest.TERTIARY_LIST.getBoolean(2));
        try {
            BooleanArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            BooleanArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSize() {
        TestCase.assertEquals(0, BooleanArrayList.emptyList().size());
        TestCase.assertEquals(1, BooleanArrayListTest.UNARY_LIST.size());
        TestCase.assertEquals(3, BooleanArrayListTest.TERTIARY_LIST.size());
        list.addBoolean(true);
        list.addBoolean(false);
        list.addBoolean(false);
        list.addBoolean(false);
        TestCase.assertEquals(4, list.size());
        list.remove(0);
        TestCase.assertEquals(3, list.size());
        list.add(true);
        TestCase.assertEquals(4, list.size());
    }

    public void testSet() {
        list.addBoolean(false);
        list.addBoolean(false);
        TestCase.assertEquals(false, ((boolean) (list.set(0, true))));
        TestCase.assertEquals(true, list.getBoolean(0));
        TestCase.assertEquals(false, ((boolean) (list.set(1, false))));
        TestCase.assertEquals(false, list.getBoolean(1));
        try {
            list.set((-1), false);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.set(2, false);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSetBoolean() {
        list.addBoolean(true);
        list.addBoolean(true);
        TestCase.assertEquals(true, list.setBoolean(0, false));
        TestCase.assertEquals(false, list.getBoolean(0));
        TestCase.assertEquals(true, list.setBoolean(1, false));
        TestCase.assertEquals(false, list.getBoolean(1));
        try {
            list.setBoolean((-1), false);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.setBoolean(2, false);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAdd() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.add(false));
        TestCase.assertEquals(Arrays.asList(false), list);
        TestCase.assertTrue(list.add(true));
        list.add(0, false);
        TestCase.assertEquals(Arrays.asList(false, false, true), list);
        list.add(0, true);
        list.add(0, false);
        // Force a resize by getting up to 11 elements.
        for (int i = 0; i < 6; i++) {
            list.add(((i % 2) == 0));
        }
        TestCase.assertEquals(Arrays.asList(false, true, false, false, true, true, false, true, false, true, false), list);
        try {
            list.add((-1), true);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.add(4, true);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAddBoolean() {
        TestCase.assertEquals(0, list.size());
        list.addBoolean(false);
        TestCase.assertEquals(Arrays.asList(false), list);
        list.addBoolean(true);
        TestCase.assertEquals(Arrays.asList(false, true), list);
    }

    public void testAddAll() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.addAll(Collections.singleton(true)));
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(true, ((boolean) (list.get(0))));
        TestCase.assertEquals(true, list.getBoolean(0));
        TestCase.assertTrue(list.addAll(Arrays.asList(false, true, false, true, false)));
        TestCase.assertEquals(Arrays.asList(true, false, true, false, true, false), list);
        TestCase.assertTrue(list.addAll(BooleanArrayListTest.TERTIARY_LIST));
        TestCase.assertEquals(Arrays.asList(true, false, true, false, true, false, true, false, true), list);
        TestCase.assertFalse(list.addAll(Collections.<Boolean>emptyList()));
        TestCase.assertFalse(list.addAll(BooleanArrayList.emptyList()));
    }

    public void testRemove() {
        list.addAll(BooleanArrayListTest.TERTIARY_LIST);
        TestCase.assertEquals(true, ((boolean) (list.remove(0))));
        TestCase.assertEquals(Arrays.asList(false, true), list);
        TestCase.assertTrue(list.remove(Boolean.TRUE));
        TestCase.assertEquals(Arrays.asList(false), list);
        TestCase.assertFalse(list.remove(Boolean.TRUE));
        TestCase.assertEquals(Arrays.asList(false), list);
        TestCase.assertEquals(false, ((boolean) (list.remove(0))));
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
        BooleanList toRemove = BooleanArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addBoolean(true);
        toRemove.remove(0);
        TestCase.assertEquals(0, toRemove.size());
    }

    public void testSublistRemoveEndOfCapacity() {
        BooleanList toRemove = BooleanArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addBoolean(true);
        toRemove.subList(0, 1).clear();
        TestCase.assertEquals(0, toRemove.size());
    }
}

