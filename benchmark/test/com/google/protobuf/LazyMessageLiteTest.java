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


import LazyExtension.extension;
import LazyMessageLite.Builder;
import java.util.ArrayList;
import junit.framework.TestCase;
import protobuf_unittest.LazyFieldsLite.LazyExtension;
import protobuf_unittest.LazyFieldsLite.LazyInnerMessageLite;
import protobuf_unittest.LazyFieldsLite.LazyMessageLite;
import protobuf_unittest.LazyFieldsLite.LazyNestedInnerMessageLite;


/**
 * Unit test for messages with lazy fields.
 *
 * @author niwasaki@google.com (Naoki Iwasaki)
 */
public class LazyMessageLiteTest extends TestCase {
    private Parser<LazyInnerMessageLite> originalLazyInnerMessageLiteParser;

    public void testSetValues() {
        LazyNestedInnerMessageLite nested = LazyNestedInnerMessageLite.newBuilder().setNum(3).build();
        LazyInnerMessageLite inner = LazyInnerMessageLite.newBuilder().setNum(2).setNested(nested).build();
        LazyMessageLite outer = LazyMessageLite.newBuilder().setNum(1).setInner(inner).setOneofNum(123).setOneofInner(inner).build();
        TestCase.assertEquals(1, outer.getNum());
        TestCase.assertEquals(421, outer.getNumWithDefault());
        TestCase.assertEquals(2, outer.getInner().getNum());
        TestCase.assertEquals(42, outer.getInner().getNumWithDefault());
        TestCase.assertEquals(3, outer.getInner().getNested().getNum());
        TestCase.assertEquals(4, outer.getInner().getNested().getNumWithDefault());
        TestCase.assertFalse(outer.hasOneofNum());
        TestCase.assertTrue(outer.hasOneofInner());
        TestCase.assertEquals(2, outer.getOneofInner().getNum());
        TestCase.assertEquals(42, outer.getOneofInner().getNumWithDefault());
        TestCase.assertEquals(3, outer.getOneofInner().getNested().getNum());
        TestCase.assertEquals(4, outer.getOneofInner().getNested().getNumWithDefault());
    }

    public void testSetRepeatedValues() {
        LazyMessageLite outer = LazyMessageLite.newBuilder().setNum(1).addRepeatedInner(LazyInnerMessageLite.newBuilder().setNum(119)).addRepeatedInner(LazyInnerMessageLite.newBuilder().setNum(122)).build();
        TestCase.assertEquals(1, outer.getNum());
        TestCase.assertEquals(2, outer.getRepeatedInnerCount());
        TestCase.assertEquals(119, outer.getRepeatedInner(0).getNum());
        TestCase.assertEquals(122, outer.getRepeatedInner(1).getNum());
    }

    public void testRepeatedMutability() throws Exception {
        LazyMessageLite outer = LazyMessageLite.newBuilder().addRepeatedInner(LazyInnerMessageLite.newBuilder().setNum(119)).addRepeatedInner(LazyInnerMessageLite.newBuilder().setNum(122)).build();
        outer = LazyMessageLite.parseFrom(outer.toByteArray());
        try {
            outer.getRepeatedInnerList().set(1, null);
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testAddAll() {
        ArrayList<LazyInnerMessageLite> inners = new ArrayList<LazyInnerMessageLite>();
        int count = 4;
        for (int i = 0; i < count; i++) {
            LazyInnerMessageLite inner = LazyInnerMessageLite.newBuilder().setNum(i).build();
            inners.add(inner);
        }
        LazyMessageLite outer = LazyMessageLite.newBuilder().addAllRepeatedInner(inners).build();
        TestCase.assertEquals(count, outer.getRepeatedInnerCount());
        for (int i = 0; i < count; i++) {
            TestCase.assertEquals(i, outer.getRepeatedInner(i).getNum());
        }
    }

    public void testGetDefaultValues() {
        LazyMessageLite outer = LazyMessageLite.newBuilder().build();
        TestCase.assertEquals(0, outer.getNum());
        TestCase.assertEquals(421, outer.getNumWithDefault());
        TestCase.assertEquals(0, outer.getInner().getNum());
        TestCase.assertEquals(42, outer.getInner().getNumWithDefault());
        TestCase.assertEquals(0, outer.getInner().getNested().getNum());
        TestCase.assertEquals(4, outer.getInner().getNested().getNumWithDefault());
        TestCase.assertEquals(0, outer.getOneofNum());
        TestCase.assertEquals(0, outer.getOneofInner().getNum());
        TestCase.assertEquals(42, outer.getOneofInner().getNumWithDefault());
        TestCase.assertEquals(0, outer.getOneofInner().getNested().getNum());
        TestCase.assertEquals(4, outer.getOneofInner().getNested().getNumWithDefault());
    }

    public void testClearValues() {
        LazyInnerMessageLite inner = LazyInnerMessageLite.newBuilder().setNum(115).build();
        LazyMessageLite.Builder outerBuilder = LazyMessageLite.newBuilder();
        TestCase.assertEquals(0, outerBuilder.build().getNum());
        // Set/Clear num
        outerBuilder.setNum(100);
        TestCase.assertEquals(100, outerBuilder.build().getNum());
        TestCase.assertEquals(421, outerBuilder.build().getNumWithDefault());
        TestCase.assertFalse(outerBuilder.build().hasInner());
        outerBuilder.clearNum();
        TestCase.assertEquals(0, outerBuilder.build().getNum());
        TestCase.assertEquals(421, outerBuilder.build().getNumWithDefault());
        TestCase.assertFalse(outerBuilder.build().hasInner());
        // Set/Clear all
        outerBuilder.setNum(100).setInner(inner).addRepeatedInner(LazyInnerMessageLite.newBuilder().setNum(119)).addRepeatedInner(LazyInnerMessageLite.newBuilder().setNum(122)).setOneofInner(LazyInnerMessageLite.newBuilder().setNum(123));
        LazyMessageLite outer = outerBuilder.build();
        TestCase.assertEquals(100, outer.getNum());
        TestCase.assertEquals(421, outer.getNumWithDefault());
        TestCase.assertTrue(outer.hasInner());
        TestCase.assertEquals(115, outer.getInner().getNum());
        TestCase.assertEquals(2, outer.getRepeatedInnerCount());
        TestCase.assertEquals(119, outer.getRepeatedInner(0).getNum());
        TestCase.assertEquals(122, outer.getRepeatedInner(1).getNum());
        TestCase.assertTrue(outer.hasOneofInner());
        TestCase.assertEquals(123, outer.getOneofInner().getNum());
        outerBuilder.clear();
        outer = outerBuilder.build();
        TestCase.assertEquals(0, outer.getNum());
        TestCase.assertEquals(421, outer.getNumWithDefault());
        TestCase.assertFalse(outer.hasInner());
        TestCase.assertEquals(0, outer.getRepeatedInnerCount());
        TestCase.assertFalse(outer.hasOneofInner());
        TestCase.assertEquals(0, outer.getOneofInner().getNum());
    }

    public void testMergeValues() {
        LazyMessageLite outerBase = LazyMessageLite.newBuilder().setNumWithDefault(122).build();
        LazyInnerMessageLite innerMerging = LazyInnerMessageLite.newBuilder().setNum(115).build();
        LazyMessageLite outerMerging = LazyMessageLite.newBuilder().setNum(119).setInner(innerMerging).setOneofInner(innerMerging).build();
        LazyMessageLite merged = LazyMessageLite.newBuilder(outerBase).mergeFrom(outerMerging).build();
        TestCase.assertEquals(119, merged.getNum());
        TestCase.assertEquals(122, merged.getNumWithDefault());
        TestCase.assertEquals(115, merged.getInner().getNum());
        TestCase.assertEquals(42, merged.getInner().getNumWithDefault());
        TestCase.assertEquals(115, merged.getOneofInner().getNum());
        TestCase.assertEquals(42, merged.getOneofInner().getNumWithDefault());
    }

    public void testMergeDefaultValues() {
        LazyInnerMessageLite innerBase = LazyInnerMessageLite.newBuilder().setNum(115).build();
        LazyMessageLite outerBase = LazyMessageLite.newBuilder().setNum(119).setNumWithDefault(122).setInner(innerBase).setOneofInner(innerBase).build();
        LazyMessageLite outerMerging = LazyMessageLite.newBuilder().build();
        LazyMessageLite merged = LazyMessageLite.newBuilder(outerBase).mergeFrom(outerMerging).build();
        // Merging default-instance shouldn't overwrite values in the base message.
        TestCase.assertEquals(119, merged.getNum());
        TestCase.assertEquals(122, merged.getNumWithDefault());
        TestCase.assertEquals(115, merged.getInner().getNum());
        TestCase.assertEquals(42, merged.getInner().getNumWithDefault());
        TestCase.assertEquals(115, merged.getOneofInner().getNum());
        TestCase.assertEquals(42, merged.getOneofInner().getNumWithDefault());
    }

    // Regression test for b/28198805.
    public void testMergeOneofMessages() throws Exception {
        LazyInnerMessageLite inner = LazyInnerMessageLite.newBuilder().build();
        LazyMessageLite outer = LazyMessageLite.newBuilder().setOneofInner(inner).build();
        ByteString data1 = outer.toByteString();
        // The following should not alter the content of the 'outer' message.
        LazyMessageLite.Builder merged = LazyMessageLite.newBuilder().mergeFrom(outer);
        LazyInnerMessageLite anotherInner = LazyInnerMessageLite.newBuilder().setNum(12345).build();
        merged.setOneofInner(anotherInner);
        // Check that the 'outer' stays the same.
        ByteString data2 = outer.toByteString();
        TestCase.assertEquals(data1, data2);
        TestCase.assertEquals(0, outer.getOneofInner().getNum());
    }

    public void testSerialize() throws InvalidProtocolBufferException {
        LazyNestedInnerMessageLite nested = LazyNestedInnerMessageLite.newBuilder().setNum(3).build();
        LazyInnerMessageLite inner = LazyInnerMessageLite.newBuilder().setNum(2).setNested(nested).build();
        LazyMessageLite outer = LazyMessageLite.newBuilder().setNum(1).setInner(inner).setOneofInner(inner).build();
        ByteString bytes = outer.toByteString();
        TestCase.assertEquals(bytes.size(), outer.getSerializedSize());
        LazyMessageLite deserialized = LazyMessageLite.parseFrom(bytes);
        TestCase.assertEquals(1, deserialized.getNum());
        TestCase.assertEquals(421, deserialized.getNumWithDefault());
        TestCase.assertEquals(2, deserialized.getInner().getNum());
        TestCase.assertEquals(42, deserialized.getInner().getNumWithDefault());
        TestCase.assertEquals(3, deserialized.getInner().getNested().getNum());
        TestCase.assertEquals(4, deserialized.getInner().getNested().getNumWithDefault());
        TestCase.assertEquals(2, deserialized.getOneofInner().getNum());
        TestCase.assertEquals(42, deserialized.getOneofInner().getNumWithDefault());
        TestCase.assertEquals(3, deserialized.getOneofInner().getNested().getNum());
        TestCase.assertEquals(4, deserialized.getOneofInner().getNested().getNumWithDefault());
        TestCase.assertEquals(bytes, deserialized.toByteString());
    }

    public void testExtensions() throws Exception {
        LazyInnerMessageLite.Builder innerBuilder = LazyInnerMessageLite.newBuilder();
        innerBuilder.setExtension(extension, LazyExtension.newBuilder().setName("name").build());
        TestCase.assertTrue(innerBuilder.hasExtension(extension));
        TestCase.assertEquals("name", innerBuilder.getExtension(extension).getName());
        LazyInnerMessageLite innerMessage = innerBuilder.build();
        TestCase.assertTrue(innerMessage.hasExtension(extension));
        TestCase.assertEquals("name", innerMessage.getExtension(extension).getName());
        LazyMessageLite lite = LazyMessageLite.newBuilder().setInner(innerMessage).build();
        TestCase.assertTrue(lite.getInner().hasExtension(extension));
        TestCase.assertEquals("name", lite.getInner().getExtension(extension).getName());
    }
}

