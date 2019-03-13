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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;


/**
 * Tests for {@link LazyStringArrayList}.
 *
 * @author jonp@google.com (Jon Perlow)
 */
public class LazyStringArrayListTest extends TestCase {
    private static String STRING_A = "A";

    private static String STRING_B = "B";

    private static String STRING_C = "C";

    private static ByteString BYTE_STRING_A = ByteString.copyFromUtf8("A");

    private static ByteString BYTE_STRING_B = ByteString.copyFromUtf8("B");

    private static ByteString BYTE_STRING_C = ByteString.copyFromUtf8("C");

    public void testJustStrings() {
        LazyStringArrayList list = new LazyStringArrayList();
        list.add(LazyStringArrayListTest.STRING_A);
        list.add(LazyStringArrayListTest.STRING_B);
        list.add(LazyStringArrayListTest.STRING_C);
        TestCase.assertEquals(3, list.size());
        TestCase.assertSame(LazyStringArrayListTest.STRING_A, list.get(0));
        TestCase.assertSame(LazyStringArrayListTest.STRING_B, list.get(1));
        TestCase.assertSame(LazyStringArrayListTest.STRING_C, list.get(2));
        list.set(1, LazyStringArrayListTest.STRING_C);
        TestCase.assertSame(LazyStringArrayListTest.STRING_C, list.get(1));
        list.remove(1);
        TestCase.assertSame(LazyStringArrayListTest.STRING_A, list.get(0));
        TestCase.assertSame(LazyStringArrayListTest.STRING_C, list.get(1));
        List<ByteString> byteStringList = list.asByteStringList();
        TestCase.assertEquals(LazyStringArrayListTest.BYTE_STRING_A, byteStringList.get(0));
        TestCase.assertEquals(LazyStringArrayListTest.BYTE_STRING_C, byteStringList.get(1));
        // Underlying list should be transformed.
        TestCase.assertSame(byteStringList.get(0), list.getByteString(0));
        TestCase.assertSame(byteStringList.get(1), list.getByteString(1));
    }

    public void testJustByteString() {
        LazyStringArrayList list = new LazyStringArrayList();
        list.add(LazyStringArrayListTest.BYTE_STRING_A);
        list.add(LazyStringArrayListTest.BYTE_STRING_B);
        list.add(LazyStringArrayListTest.BYTE_STRING_C);
        TestCase.assertEquals(3, list.size());
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_A, list.getByteString(0));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_B, list.getByteString(1));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_C, list.getByteString(2));
        list.remove(1);
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_A, list.getByteString(0));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_C, list.getByteString(1));
        List<ByteString> byteStringList = list.asByteStringList();
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_A, byteStringList.get(0));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_C, byteStringList.get(1));
    }

    public void testConversionBackAndForth() {
        LazyStringArrayList list = new LazyStringArrayList();
        list.add(LazyStringArrayListTest.STRING_A);
        list.add(LazyStringArrayListTest.BYTE_STRING_B);
        list.add(LazyStringArrayListTest.BYTE_STRING_C);
        // String a should be the same because it was originally a string
        TestCase.assertSame(LazyStringArrayListTest.STRING_A, list.get(0));
        // String b and c should be different because the string has to be computed
        // from the ByteString
        String bPrime = list.get(1);
        TestCase.assertNotSame(LazyStringArrayListTest.STRING_B, bPrime);
        TestCase.assertEquals(LazyStringArrayListTest.STRING_B, bPrime);
        String cPrime = list.get(2);
        TestCase.assertNotSame(LazyStringArrayListTest.STRING_C, cPrime);
        TestCase.assertEquals(LazyStringArrayListTest.STRING_C, cPrime);
        // String c and c should stay the same once cached.
        TestCase.assertSame(bPrime, list.get(1));
        TestCase.assertSame(cPrime, list.get(2));
        // ByteString needs to be computed from string for both a and b
        ByteString aPrimeByteString = list.getByteString(0);
        TestCase.assertEquals(LazyStringArrayListTest.BYTE_STRING_A, aPrimeByteString);
        ByteString bPrimeByteString = list.getByteString(1);
        TestCase.assertNotSame(LazyStringArrayListTest.BYTE_STRING_B, bPrimeByteString);
        TestCase.assertEquals(LazyStringArrayListTest.BYTE_STRING_B, list.getByteString(1));
        // Once cached, ByteString should stay cached.
        TestCase.assertSame(aPrimeByteString, list.getByteString(0));
        TestCase.assertSame(bPrimeByteString, list.getByteString(1));
    }

    public void testCopyConstructorCopiesByReference() {
        LazyStringArrayList list1 = new LazyStringArrayList();
        list1.add(LazyStringArrayListTest.STRING_A);
        list1.add(LazyStringArrayListTest.BYTE_STRING_B);
        list1.add(LazyStringArrayListTest.BYTE_STRING_C);
        LazyStringArrayList list2 = new LazyStringArrayList(list1);
        TestCase.assertEquals(3, list2.size());
        TestCase.assertSame(LazyStringArrayListTest.STRING_A, list2.get(0));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_B, list2.getByteString(1));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_C, list2.getByteString(2));
    }

    public void testListCopyConstructor() {
        List<String> list1 = new ArrayList<String>();
        list1.add(LazyStringArrayListTest.STRING_A);
        list1.add(LazyStringArrayListTest.STRING_B);
        list1.add(LazyStringArrayListTest.STRING_C);
        LazyStringArrayList list2 = new LazyStringArrayList(list1);
        TestCase.assertEquals(3, list2.size());
        TestCase.assertSame(LazyStringArrayListTest.STRING_A, list2.get(0));
        TestCase.assertSame(LazyStringArrayListTest.STRING_B, list2.get(1));
        TestCase.assertSame(LazyStringArrayListTest.STRING_C, list2.get(2));
    }

    public void testAddAllCopiesByReferenceIfPossible() {
        LazyStringArrayList list1 = new LazyStringArrayList();
        list1.add(LazyStringArrayListTest.STRING_A);
        list1.add(LazyStringArrayListTest.BYTE_STRING_B);
        list1.add(LazyStringArrayListTest.BYTE_STRING_C);
        LazyStringArrayList list2 = new LazyStringArrayList();
        list2.addAll(list1);
        TestCase.assertEquals(3, list2.size());
        TestCase.assertSame(LazyStringArrayListTest.STRING_A, list2.get(0));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_B, list2.getByteString(1));
        TestCase.assertSame(LazyStringArrayListTest.BYTE_STRING_C, list2.getByteString(2));
    }

    public void testModificationWithIteration() {
        LazyStringArrayList list = new LazyStringArrayList();
        list.addAll(Arrays.asList(LazyStringArrayListTest.STRING_A, LazyStringArrayListTest.STRING_B, LazyStringArrayListTest.STRING_C));
        Iterator<String> iterator = list.iterator();
        TestCase.assertEquals(3, list.size());
        TestCase.assertEquals(LazyStringArrayListTest.STRING_A, list.get(0));
        TestCase.assertEquals(LazyStringArrayListTest.STRING_A, iterator.next());
        // Does not structurally modify.
        iterator = list.iterator();
        list.set(0, LazyStringArrayListTest.STRING_B);
        iterator.next();
        list.remove(0);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
        iterator = list.iterator();
        list.add(0, LazyStringArrayListTest.STRING_C);
        try {
            iterator.next();
            TestCase.fail();
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testMakeImmutable() {
        LazyStringArrayList list = new LazyStringArrayList();
        list.add(LazyStringArrayListTest.STRING_A);
        list.add(LazyStringArrayListTest.STRING_B);
        list.add(LazyStringArrayListTest.STRING_C);
        list.makeImmutable();
        LazyStringArrayListTest.assertGenericListImmutable(list, LazyStringArrayListTest.STRING_A);
        // LazyStringArrayList has extra methods not covered in the generic
        // assertion.
        try {
            list.add(LazyStringArrayListTest.BYTE_STRING_A.toByteArray());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.add(LazyStringArrayListTest.BYTE_STRING_A);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.addAllByteArray(Collections.singletonList(LazyStringArrayListTest.BYTE_STRING_A.toByteArray()));
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.addAllByteString(Arrays.asList(LazyStringArrayListTest.BYTE_STRING_A));
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.mergeFrom(new LazyStringArrayList());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.set(0, LazyStringArrayListTest.BYTE_STRING_A.toByteArray());
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            list.set(0, LazyStringArrayListTest.BYTE_STRING_A);
            TestCase.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    public void testImmutabilityPropagation() {
        LazyStringArrayList list = new LazyStringArrayList();
        list.add(LazyStringArrayListTest.STRING_A);
        list.makeImmutable();
        LazyStringArrayListTest.assertGenericListImmutable(list.asByteStringList(), LazyStringArrayListTest.BYTE_STRING_A);
        // Arrays use reference equality so need to retrieve the underlying value
        // to properly test deep immutability.
        List<byte[]> byteArrayList = list.asByteArrayList();
        LazyStringArrayListTest.assertGenericListImmutable(byteArrayList, byteArrayList.get(0));
    }
}

