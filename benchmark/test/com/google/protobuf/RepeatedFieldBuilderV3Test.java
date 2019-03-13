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
import java.util.List;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestAllTypesOrBuilder;


/**
 * Tests for {@link RepeatedFieldBuilderV3}. This tests basic functionality.
 * More extensive testing is provided via other tests that exercise the
 * builder.
 *
 * @author jonp@google.com (Jon Perlow)
 */
public class RepeatedFieldBuilderV3Test extends TestCase {
    public void testBasicUse() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        RepeatedFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = newRepeatedFieldBuilderV3(mockParent);
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(0).build());
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(1).build());
        TestCase.assertEquals(0, builder.getMessage(0).getOptionalInt32());
        TestCase.assertEquals(1, builder.getMessage(1).getOptionalInt32());
        List<TestAllTypes> list = builder.build();
        TestCase.assertEquals(2, list.size());
        TestCase.assertEquals(0, list.get(0).getOptionalInt32());
        TestCase.assertEquals(1, list.get(1).getOptionalInt32());
        assertIsUnmodifiable(list);
        // Make sure it doesn't change.
        List<TestAllTypes> list2 = builder.build();
        TestCase.assertSame(list, list2);
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
    }

    public void testGoingBackAndForth() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        RepeatedFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = newRepeatedFieldBuilderV3(mockParent);
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(0).build());
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(1).build());
        TestCase.assertEquals(0, builder.getMessage(0).getOptionalInt32());
        TestCase.assertEquals(1, builder.getMessage(1).getOptionalInt32());
        // Convert to list
        List<TestAllTypes> list = builder.build();
        TestCase.assertEquals(2, list.size());
        TestCase.assertEquals(0, list.get(0).getOptionalInt32());
        TestCase.assertEquals(1, list.get(1).getOptionalInt32());
        assertIsUnmodifiable(list);
        // Update 0th item
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
        builder.getBuilder(0).setOptionalString("foo");
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
        list = builder.build();
        TestCase.assertEquals(2, list.size());
        TestCase.assertEquals(0, list.get(0).getOptionalInt32());
        TestCase.assertEquals("foo", list.get(0).getOptionalString());
        TestCase.assertEquals(1, list.get(1).getOptionalInt32());
        assertIsUnmodifiable(list);
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
    }

    public void testVariousMethods() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        RepeatedFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = newRepeatedFieldBuilderV3(mockParent);
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(1).build());
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(2).build());
        builder.addBuilder(0, TestAllTypes.getDefaultInstance()).setOptionalInt32(0);
        builder.addBuilder(TestAllTypes.getDefaultInstance()).setOptionalInt32(3);
        TestCase.assertEquals(0, builder.getMessage(0).getOptionalInt32());
        TestCase.assertEquals(1, builder.getMessage(1).getOptionalInt32());
        TestCase.assertEquals(2, builder.getMessage(2).getOptionalInt32());
        TestCase.assertEquals(3, builder.getMessage(3).getOptionalInt32());
        TestCase.assertEquals(0, mockParent.getInvalidationCount());
        List<TestAllTypes> messages = builder.build();
        TestCase.assertEquals(4, messages.size());
        TestCase.assertSame(messages, builder.build());// expect same list

        // Remove a message.
        builder.remove(2);
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
        TestCase.assertEquals(3, builder.getCount());
        TestCase.assertEquals(0, builder.getMessage(0).getOptionalInt32());
        TestCase.assertEquals(1, builder.getMessage(1).getOptionalInt32());
        TestCase.assertEquals(3, builder.getMessage(2).getOptionalInt32());
        // Remove a builder.
        builder.remove(0);
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
        TestCase.assertEquals(2, builder.getCount());
        TestCase.assertEquals(1, builder.getMessage(0).getOptionalInt32());
        TestCase.assertEquals(3, builder.getMessage(1).getOptionalInt32());
        // Test clear.
        builder.clear();
        TestCase.assertEquals(1, mockParent.getInvalidationCount());
        TestCase.assertEquals(0, builder.getCount());
        TestCase.assertTrue(builder.isEmpty());
    }

    public void testLists() {
        TestUtil.MockBuilderParent mockParent = new TestUtil.MockBuilderParent();
        RepeatedFieldBuilderV3<TestAllTypes, TestAllTypes.Builder, TestAllTypesOrBuilder> builder = newRepeatedFieldBuilderV3(mockParent);
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(1).build());
        builder.addMessage(0, TestAllTypes.newBuilder().setOptionalInt32(0).build());
        TestCase.assertEquals(0, builder.getMessage(0).getOptionalInt32());
        TestCase.assertEquals(1, builder.getMessage(1).getOptionalInt32());
        // Use list of builders.
        List<TestAllTypes.Builder> builders = builder.getBuilderList();
        TestCase.assertEquals(0, builders.get(0).getOptionalInt32());
        TestCase.assertEquals(1, builders.get(1).getOptionalInt32());
        builders.get(0).setOptionalInt32(10);
        builders.get(1).setOptionalInt32(11);
        // Use list of protos
        List<TestAllTypes> protos = builder.getMessageList();
        TestCase.assertEquals(10, protos.get(0).getOptionalInt32());
        TestCase.assertEquals(11, protos.get(1).getOptionalInt32());
        // Add an item to the builders and verify it's updated in both
        builder.addMessage(TestAllTypes.newBuilder().setOptionalInt32(12).build());
        TestCase.assertEquals(3, builders.size());
        TestCase.assertEquals(3, protos.size());
    }
}

