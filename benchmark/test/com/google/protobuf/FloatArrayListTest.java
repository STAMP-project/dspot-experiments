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


import com.google.protobuf.Internal.FloatList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import junit.framework.TestCase;


/**
 * Tests for {@link FloatArrayList}.
 *
 * @author dweis@google.com (Daniel Weis)
 */
public class FloatArrayListTest extends TestCase {
    private static final FloatArrayList UNARY_LIST = FloatArrayListTest.newImmutableFloatArrayList(1);

    private static final FloatArrayList TERTIARY_LIST = FloatArrayListTest.newImmutableFloatArrayList(1, 2, 3);

    private FloatArrayList list;

    public void testEmptyListReturnsSameInstance() {
        TestCase.assertSame(FloatArrayList.emptyList(), FloatArrayList.emptyList());
    }

    public void testEmptyListIsImmutable() {
        assertImmutable(FloatArrayList.emptyList());
    }

    public void testMakeImmutable() {
        list.addFloat(3);
        list.addFloat(4);
        list.addFloat(5);
        list.addFloat(7);
        list.makeImmutable();
        assertImmutable(list);
    }

    public void testModificationWithIteration() {
        list.addAll(Arrays.asList(1.0F, 2.0F, 3.0F, 4.0F));
        Iterator<Float> iterator = list.iterator();
        TestCase.assertEquals(4, list.size());
        TestCase.assertEquals(1.0F, ((float) (list.get(0))));
        TestCase.assertEquals(1.0F, ((float) (iterator.next())));
        list.set(0, 1.0F);
        TestCase.assertEquals(2.0F, ((float) (iterator.next())));
        list.remove(0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
        iterator = list.iterator();
        list.add(0, 0.0F);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testGet() {
        TestCase.assertEquals(1.0F, ((float) (FloatArrayListTest.TERTIARY_LIST.get(0))));
        TestCase.assertEquals(2.0F, ((float) (FloatArrayListTest.TERTIARY_LIST.get(1))));
        TestCase.assertEquals(3.0F, ((float) (FloatArrayListTest.TERTIARY_LIST.get(2))));
        try {
            FloatArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            FloatArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testGetFloat() {
        TestCase.assertEquals(1.0F, FloatArrayListTest.TERTIARY_LIST.getFloat(0));
        TestCase.assertEquals(2.0F, FloatArrayListTest.TERTIARY_LIST.getFloat(1));
        TestCase.assertEquals(3.0F, FloatArrayListTest.TERTIARY_LIST.getFloat(2));
        try {
            FloatArrayListTest.TERTIARY_LIST.get((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            FloatArrayListTest.TERTIARY_LIST.get(3);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSize() {
        TestCase.assertEquals(0, FloatArrayList.emptyList().size());
        TestCase.assertEquals(1, FloatArrayListTest.UNARY_LIST.size());
        TestCase.assertEquals(3, FloatArrayListTest.TERTIARY_LIST.size());
        list.addFloat(3);
        list.addFloat(4);
        list.addFloat(6);
        list.addFloat(8);
        TestCase.assertEquals(4, list.size());
        list.remove(0);
        TestCase.assertEquals(3, list.size());
        list.add(17.0F);
        TestCase.assertEquals(4, list.size());
    }

    public void testSet() {
        list.addFloat(2);
        list.addFloat(4);
        TestCase.assertEquals(2.0F, ((float) (list.set(0, 3.0F))));
        TestCase.assertEquals(3.0F, list.getFloat(0));
        TestCase.assertEquals(4.0F, ((float) (list.set(1, 0.0F))));
        TestCase.assertEquals(0.0F, list.getFloat(1));
        try {
            list.set((-1), 0.0F);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.set(2, 0.0F);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testSetFloat() {
        list.addFloat(1);
        list.addFloat(3);
        TestCase.assertEquals(1.0F, list.setFloat(0, 0));
        TestCase.assertEquals(0.0F, list.getFloat(0));
        TestCase.assertEquals(3.0F, list.setFloat(1, 0));
        TestCase.assertEquals(0.0F, list.getFloat(1));
        try {
            list.setFloat((-1), 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.setFloat(2, 0);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAdd() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.add(2.0F));
        TestCase.assertEquals(Arrays.asList(2.0F), list);
        TestCase.assertTrue(list.add(3.0F));
        list.add(0, 4.0F);
        TestCase.assertEquals(Arrays.asList(4.0F, 2.0F, 3.0F), list);
        list.add(0, 1.0F);
        list.add(0, 0.0F);
        // Force a resize by getting up to 11 elements.
        for (int i = 0; i < 6; i++) {
            list.add(Float.valueOf((5 + i)));
        }
        TestCase.assertEquals(Arrays.asList(0.0F, 1.0F, 4.0F, 2.0F, 3.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F), list);
        try {
            list.add((-1), 5.0F);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            list.add(4, 5.0F);
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    public void testAddFloat() {
        TestCase.assertEquals(0, list.size());
        list.addFloat(2);
        TestCase.assertEquals(Arrays.asList(2.0F), list);
        list.addFloat(3);
        TestCase.assertEquals(Arrays.asList(2.0F, 3.0F), list);
    }

    public void testAddAll() {
        TestCase.assertEquals(0, list.size());
        TestCase.assertTrue(list.addAll(Collections.singleton(1.0F)));
        TestCase.assertEquals(1, list.size());
        TestCase.assertEquals(1.0F, ((float) (list.get(0))));
        TestCase.assertEquals(1.0F, list.getFloat(0));
        TestCase.assertTrue(list.addAll(Arrays.asList(2.0F, 3.0F, 4.0F, 5.0F, 6.0F)));
        TestCase.assertEquals(Arrays.asList(1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F), list);
        TestCase.assertTrue(list.addAll(FloatArrayListTest.TERTIARY_LIST));
        TestCase.assertEquals(Arrays.asList(1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 1.0F, 2.0F, 3.0F), list);
        TestCase.assertFalse(list.addAll(Collections.<Float>emptyList()));
        TestCase.assertFalse(list.addAll(FloatArrayList.emptyList()));
    }

    public void testRemove() {
        list.addAll(FloatArrayListTest.TERTIARY_LIST);
        TestCase.assertEquals(1.0F, ((float) (list.remove(0))));
        TestCase.assertEquals(Arrays.asList(2.0F, 3.0F), list);
        TestCase.assertTrue(list.remove(Float.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2.0F), list);
        TestCase.assertFalse(list.remove(Float.valueOf(3)));
        TestCase.assertEquals(Arrays.asList(2.0F), list);
        TestCase.assertEquals(2.0F, ((float) (list.remove(0))));
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
        FloatList toRemove = FloatArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addFloat(3);
        toRemove.remove(0);
        TestCase.assertEquals(0, toRemove.size());
    }

    public void testSublistRemoveEndOfCapacity() {
        FloatList toRemove = FloatArrayList.emptyList().mutableCopyWithCapacity(1);
        toRemove.addFloat(3);
        toRemove.subList(0, 1).clear();
        TestCase.assertEquals(0, toRemove.size());
    }
}

