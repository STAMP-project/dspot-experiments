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


import ByteString.EMPTY;
import TestAllTypes.NestedEnum.BAR;
import TestAllTypes.NestedEnum.FOO;
import TestAllTypes.NestedMessage;
import TestAllTypes.OneofFieldCase.ONEOF_INT32;
import UnittestProto.TestAllTypes.Builder;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.FieldPresenceTestProto.TestAllTypes;
import com.google.protobuf.FieldPresenceTestProto.TestOptionalFieldsOnly;
import com.google.protobuf.FieldPresenceTestProto.TestRepeatedFieldsOnly;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto;


/**
 * Unit tests for protos that doesn't support field presence test for optional
 * non-message fields.
 */
public class FieldPresenceTest extends TestCase {
    public void testHasMethod() {
        // Optional non-message fields don't have a hasFoo() method generated.
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OptionalInt32");
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OptionalString");
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OptionalBytes");
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OptionalNestedEnum");
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OptionalInt32");
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OptionalString");
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OptionalBytes");
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OptionalNestedEnum");
        // message fields still have the hasFoo() method generated.
        TestCase.assertFalse(TestAllTypes.newBuilder().build().hasOptionalNestedMessage());
        TestCase.assertFalse(TestAllTypes.newBuilder().hasOptionalNestedMessage());
        // oneof fields don't have hasFoo() methods for non-message types.
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OneofUint32");
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OneofString");
        FieldPresenceTest.assertHasMethodRemoved(TestAllTypes.class, TestAllTypes.class, "OneofBytes");
        TestCase.assertFalse(TestAllTypes.newBuilder().build().hasOneofNestedMessage());
        TestCase.assertFalse(TestAllTypes.newBuilder().hasOneofNestedMessage());
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OneofUint32");
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OneofString");
        FieldPresenceTest.assertHasMethodRemoved(Builder.class, TestAllTypes.Builder.class, "OneofBytes");
    }

    public void testOneofEquals() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TestAllTypes message1 = builder.build();
        // Set message2's oneof_uint32 field to defalut value. The two
        // messages should be different when check with oneof case.
        builder.setOneofUint32(0);
        TestAllTypes message2 = builder.build();
        TestCase.assertFalse(message1.equals(message2));
    }

    public void testLazyField() throws Exception {
        // Test default constructed message.
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TestAllTypes message = builder.build();
        TestCase.assertFalse(message.hasOptionalLazyMessage());
        TestCase.assertEquals(0, message.getSerializedSize());
        TestCase.assertEquals(EMPTY, message.toByteString());
        // Set default instance to the field.
        builder.setOptionalLazyMessage(NestedMessage.getDefaultInstance());
        message = builder.build();
        TestCase.assertTrue(message.hasOptionalLazyMessage());
        TestCase.assertEquals(2, message.getSerializedSize());
        // Test parse zero-length from wire sets the presence.
        TestAllTypes parsed = TestAllTypes.parseFrom(message.toByteString());
        TestCase.assertTrue(parsed.hasOptionalLazyMessage());
        TestCase.assertEquals(message.getOptionalLazyMessage(), parsed.getOptionalLazyMessage());
    }

    public void testFieldPresence() {
        // Optional non-message fields set to their default value are treated the
        // same way as not set.
        // Serialization will ignore such fields.
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        builder.setOptionalInt32(0);
        builder.setOptionalString("");
        builder.setOptionalBytes(EMPTY);
        builder.setOptionalNestedEnum(FOO);
        TestAllTypes message = builder.build();
        TestCase.assertEquals(0, message.getSerializedSize());
        // mergeFrom() will ignore such fields.
        TestAllTypes.Builder a = TestAllTypes.newBuilder();
        a.setOptionalInt32(1);
        a.setOptionalString("x");
        a.setOptionalBytes(ByteString.copyFromUtf8("y"));
        a.setOptionalNestedEnum(BAR);
        TestAllTypes.Builder b = TestAllTypes.newBuilder();
        b.setOptionalInt32(0);
        b.setOptionalString("");
        b.setOptionalBytes(EMPTY);
        b.setOptionalNestedEnum(FOO);
        a.mergeFrom(b.build());
        message = a.build();
        TestCase.assertEquals(1, message.getOptionalInt32());
        TestCase.assertEquals("x", message.getOptionalString());
        TestCase.assertEquals(ByteString.copyFromUtf8("y"), message.getOptionalBytes());
        TestCase.assertEquals(BAR, message.getOptionalNestedEnum());
        // equals()/hashCode() should produce the same results.
        TestAllTypes empty = TestAllTypes.newBuilder().build();
        message = builder.build();
        TestCase.assertTrue(empty.equals(message));
        TestCase.assertTrue(message.equals(empty));
        TestCase.assertEquals(empty.hashCode(), message.hashCode());
    }

    public void testFieldPresenceByReflection() {
        Descriptor descriptor = TestAllTypes.getDescriptor();
        FieldDescriptor optionalInt32Field = descriptor.findFieldByName("optional_int32");
        FieldDescriptor optionalStringField = descriptor.findFieldByName("optional_string");
        FieldDescriptor optionalBytesField = descriptor.findFieldByName("optional_bytes");
        FieldDescriptor optionalNestedEnumField = descriptor.findFieldByName("optional_nested_enum");
        // Field not present.
        TestAllTypes message = TestAllTypes.newBuilder().build();
        TestCase.assertFalse(message.hasField(optionalInt32Field));
        TestCase.assertFalse(message.hasField(optionalStringField));
        TestCase.assertFalse(message.hasField(optionalBytesField));
        TestCase.assertFalse(message.hasField(optionalNestedEnumField));
        TestCase.assertEquals(0, message.getAllFields().size());
        // Field set to default value is seen as not present.
        message = TestAllTypes.newBuilder().setOptionalInt32(0).setOptionalString("").setOptionalBytes(EMPTY).setOptionalNestedEnum(FOO).build();
        TestCase.assertFalse(message.hasField(optionalInt32Field));
        TestCase.assertFalse(message.hasField(optionalStringField));
        TestCase.assertFalse(message.hasField(optionalBytesField));
        TestCase.assertFalse(message.hasField(optionalNestedEnumField));
        TestCase.assertEquals(0, message.getAllFields().size());
        // Field set to non-default value is seen as present.
        message = TestAllTypes.newBuilder().setOptionalInt32(1).setOptionalString("x").setOptionalBytes(ByteString.copyFromUtf8("y")).setOptionalNestedEnum(BAR).build();
        TestCase.assertTrue(message.hasField(optionalInt32Field));
        TestCase.assertTrue(message.hasField(optionalStringField));
        TestCase.assertTrue(message.hasField(optionalBytesField));
        TestCase.assertTrue(message.hasField(optionalNestedEnumField));
        TestCase.assertEquals(4, message.getAllFields().size());
    }

    public void testFieldPresenceDynamicMessage() {
        Descriptor descriptor = TestAllTypes.getDescriptor();
        FieldDescriptor optionalInt32Field = descriptor.findFieldByName("optional_int32");
        FieldDescriptor optionalStringField = descriptor.findFieldByName("optional_string");
        FieldDescriptor optionalBytesField = descriptor.findFieldByName("optional_bytes");
        FieldDescriptor optionalNestedEnumField = descriptor.findFieldByName("optional_nested_enum");
        EnumDescriptor enumDescriptor = optionalNestedEnumField.getEnumType();
        EnumValueDescriptor defaultEnumValueDescriptor = enumDescriptor.getValues().get(0);
        EnumValueDescriptor nonDefaultEnumValueDescriptor = enumDescriptor.getValues().get(1);
        DynamicMessage defaultInstance = DynamicMessage.getDefaultInstance(descriptor);
        // Field not present.
        DynamicMessage message = defaultInstance.newBuilderForType().build();
        TestCase.assertFalse(message.hasField(optionalInt32Field));
        TestCase.assertFalse(message.hasField(optionalStringField));
        TestCase.assertFalse(message.hasField(optionalBytesField));
        TestCase.assertFalse(message.hasField(optionalNestedEnumField));
        TestCase.assertEquals(0, message.getAllFields().size());
        // Field set to non-default value is seen as present.
        message = defaultInstance.newBuilderForType().setField(optionalInt32Field, 1).setField(optionalStringField, "x").setField(optionalBytesField, ByteString.copyFromUtf8("y")).setField(optionalNestedEnumField, nonDefaultEnumValueDescriptor).build();
        TestCase.assertTrue(message.hasField(optionalInt32Field));
        TestCase.assertTrue(message.hasField(optionalStringField));
        TestCase.assertTrue(message.hasField(optionalBytesField));
        TestCase.assertTrue(message.hasField(optionalNestedEnumField));
        TestCase.assertEquals(4, message.getAllFields().size());
        // Field set to default value is seen as not present.
        message = message.toBuilder().setField(optionalInt32Field, 0).setField(optionalStringField, "").setField(optionalBytesField, EMPTY).setField(optionalNestedEnumField, defaultEnumValueDescriptor).build();
        TestCase.assertFalse(message.hasField(optionalInt32Field));
        TestCase.assertFalse(message.hasField(optionalStringField));
        TestCase.assertFalse(message.hasField(optionalBytesField));
        TestCase.assertFalse(message.hasField(optionalNestedEnumField));
        TestCase.assertEquals(0, message.getAllFields().size());
    }

    public void testMessageField() {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TestCase.assertFalse(builder.hasOptionalNestedMessage());
        TestCase.assertFalse(builder.build().hasOptionalNestedMessage());
        TestAllTypes.NestedMessage.Builder nestedBuilder = builder.getOptionalNestedMessageBuilder();
        TestCase.assertTrue(builder.hasOptionalNestedMessage());
        TestCase.assertTrue(builder.build().hasOptionalNestedMessage());
        nestedBuilder.setValue(1);
        TestCase.assertEquals(1, builder.build().getOptionalNestedMessage().getValue());
        builder.clearOptionalNestedMessage();
        TestCase.assertFalse(builder.hasOptionalNestedMessage());
        TestCase.assertFalse(builder.build().hasOptionalNestedMessage());
        // Unlike non-message fields, if we set a message field to its default value (i.e.,
        // default instance), the field should be seen as present.
        builder.setOptionalNestedMessage(NestedMessage.getDefaultInstance());
        TestCase.assertTrue(builder.hasOptionalNestedMessage());
        TestCase.assertTrue(builder.build().hasOptionalNestedMessage());
    }

    public void testSerializeAndParse() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        builder.setOptionalInt32(1234);
        builder.setOptionalString("hello");
        builder.setOptionalNestedMessage(NestedMessage.getDefaultInstance());
        // Set an oneof field to its default value and expect it to be serialized (i.e.,
        // an oneof field set to the default value should be treated as present).
        builder.setOneofInt32(0);
        ByteString data = builder.build().toByteString();
        TestAllTypes message = TestAllTypes.parseFrom(data);
        TestCase.assertEquals(1234, message.getOptionalInt32());
        TestCase.assertEquals("hello", message.getOptionalString());
        // Fields not set will have the default value.
        TestCase.assertEquals(EMPTY, message.getOptionalBytes());
        TestCase.assertEquals(FOO, message.getOptionalNestedEnum());
        // The message field is set despite that it's set with a default instance.
        TestCase.assertTrue(message.hasOptionalNestedMessage());
        TestCase.assertEquals(0, message.getOptionalNestedMessage().getValue());
        // The oneof field set to its default value is also present.
        TestCase.assertEquals(ONEOF_INT32, message.getOneofFieldCase());
    }

    // Regression test for b/16173397
    // Make sure we haven't screwed up the code generation for repeated fields.
    public void testRepeatedFields() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        builder.setOptionalInt32(1234);
        builder.setOptionalString("hello");
        builder.setOptionalNestedMessage(NestedMessage.getDefaultInstance());
        builder.addRepeatedInt32(4321);
        builder.addRepeatedString("world");
        builder.addRepeatedNestedMessage(NestedMessage.getDefaultInstance());
        ByteString data = builder.build().toByteString();
        TestOptionalFieldsOnly optionalOnlyMessage = TestOptionalFieldsOnly.parseFrom(data);
        TestCase.assertEquals(1234, optionalOnlyMessage.getOptionalInt32());
        TestCase.assertEquals("hello", optionalOnlyMessage.getOptionalString());
        TestCase.assertTrue(optionalOnlyMessage.hasOptionalNestedMessage());
        TestCase.assertEquals(0, optionalOnlyMessage.getOptionalNestedMessage().getValue());
        TestRepeatedFieldsOnly repeatedOnlyMessage = TestRepeatedFieldsOnly.parseFrom(data);
        TestCase.assertEquals(1, repeatedOnlyMessage.getRepeatedInt32Count());
        TestCase.assertEquals(4321, repeatedOnlyMessage.getRepeatedInt32(0));
        TestCase.assertEquals(1, repeatedOnlyMessage.getRepeatedStringCount());
        TestCase.assertEquals("world", repeatedOnlyMessage.getRepeatedString(0));
        TestCase.assertEquals(1, repeatedOnlyMessage.getRepeatedNestedMessageCount());
        TestCase.assertEquals(0, repeatedOnlyMessage.getRepeatedNestedMessage(0).getValue());
    }

    public void testIsInitialized() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        // Test optional proto2 message fields.
        UnittestProto.TestRequired.Builder proto2Builder = builder.getOptionalProto2MessageBuilder();
        TestCase.assertFalse(builder.isInitialized());
        TestCase.assertFalse(builder.buildPartial().isInitialized());
        proto2Builder.setA(1).setB(2).setC(3);
        TestCase.assertTrue(builder.isInitialized());
        TestCase.assertTrue(builder.buildPartial().isInitialized());
        // Test oneof proto2 message fields.
        proto2Builder = builder.getOneofProto2MessageBuilder();
        TestCase.assertFalse(builder.isInitialized());
        TestCase.assertFalse(builder.buildPartial().isInitialized());
        proto2Builder.setA(1).setB(2).setC(3);
        TestCase.assertTrue(builder.isInitialized());
        TestCase.assertTrue(builder.buildPartial().isInitialized());
        // Test repeated proto2 message fields.
        proto2Builder = builder.addRepeatedProto2MessageBuilder();
        TestCase.assertFalse(builder.isInitialized());
        TestCase.assertFalse(builder.buildPartial().isInitialized());
        proto2Builder.setA(1).setB(2).setC(3);
        TestCase.assertTrue(builder.isInitialized());
        TestCase.assertTrue(builder.buildPartial().isInitialized());
    }
}

