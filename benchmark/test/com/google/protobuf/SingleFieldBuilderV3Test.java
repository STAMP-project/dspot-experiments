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


import TestAllTypes.Builder;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestAllTypesOrBuilder;


/**
 * Tests for {@link SingleFieldBuilderV3}. This tests basic functionality.
 * More extensive testing is provided via other tests that exercise the
 * builder.
 *
 * @author jonp@google.com (Jon Perlow)
 */
public class SingleFieldBuilderV3Test extends TestCase {
    public void testBasicUseAndInvalidations() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = new SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder>(TestAllTypes.getDefaultInstance(), mockParent, false);
        TestCase.assertSame(TestAllTypes.getDefaultInstance(), builder.getMessage());
        TestCase.assertEquals(TestAllTypes.getDefaultInstance(), builder.getBuilder().buildPartial());
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
        builder.getBuilder().setOptionalInt32(10);
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
        TestAllTypes message = builder.build();
        TestCase.assertEquals(10, message.getOptionalInt32());
        // Test that we receive invalidations now that build has been called.
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
        builder.getBuilder().setOptionalInt32(20);
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
        // Test that we don't keep getting invalidations on every change
        builder.getBuilder().setOptionalInt32(30);
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
    }

    public void testSetMessage() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = new SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder>(TestAllTypes.getDefaultInstance(), mockParent, false);
        builder.setMessage(TestAllTypes.newBuilder().setOptionalInt32(0).build());
        TestCase.assertEquals(0, builder.getMessage().getOptionalInt32());
        // Update message using the builder
        builder.getBuilder().setOptionalInt32(1);
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
        TestCase.assertEquals(1, builder.getBuilder().getOptionalInt32());
        TestCase.assertEquals(1, builder.getMessage().getOptionalInt32());
        builder.build();
        builder.getBuilder().setOptionalInt32(2);
        TestCase.assertEquals(2, builder.getBuilder().getOptionalInt32());
        TestCase.assertEquals(2, builder.getMessage().getOptionalInt32());
        // Make sure message stays cached
        TestCase.assertSame(builder.getMessage(), builder.getMessage());
    }

    public void testClear() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = new SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder>(TestAllTypes.getDefaultInstance(), mockParent, false);
        builder.setMessage(TestAllTypes.newBuilder().setOptionalInt32(0).build());
        TestCase.assertNotSame(TestAllTypes.getDefaultInstance(), builder.getMessage());
        builder.clear();
        TestCase.assertSame(TestAllTypes.getDefaultInstance(), builder.getMessage());
        builder.getBuilder().setOptionalInt32(1);
        TestCase.assertNotSame(TestAllTypes.getDefaultInstance(), builder.getMessage());
        builder.clear();
        TestCase.assertSame(TestAllTypes.getDefaultInstance(), builder.getMessage());
    }

    public void testMerge() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = new SingleFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder>(TestAllTypes.getDefaultInstance(), mockParent, false);
        // Merge into default field.
        builder.mergeFrom(TestAllTypes.getDefaultInstance());
        TestCase.assertSame(TestAllTypes.getDefaultInstance(), builder.getMessage());
        // Merge into non-default field on existing builder.
        builder.getBuilder().setOptionalInt32(2);
        builder.mergeFrom(TestAllTypes.newBuilder().setOptionalDouble(4.0).buildPartial());
        TestCase.assertEquals(2, builder.getMessage().getOptionalInt32());
        TestCase.assertEquals(4.0, builder.getMessage().getOptionalDouble());
        // Merge into non-default field on existing message
        builder.setMessage(TestAllTypes.newBuilder().setOptionalInt32(10).buildPartial());
        builder.mergeFrom(TestAllTypes.newBuilder().setOptionalDouble(5.0).buildPartial());
        TestCase.assertEquals(10, builder.getMessage().getOptionalInt32());
        TestCase.assertEquals(5.0, builder.getMessage().getOptionalDouble());
    }
}

