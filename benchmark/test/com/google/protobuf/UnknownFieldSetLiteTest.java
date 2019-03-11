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


import Bar.fooExt;
import ByteString.EMPTY;
import ByteString.Output;
import Descriptors.FieldDescriptor;
import ForeignEnum.FOREIGN_BAZ;
import Internal.UTF_8;
import LiteEqualsAndHash.MyGroup;
import LiteEqualsAndHash.fixed32;
import LiteEqualsAndHash.fixed64;
import LiteEqualsAndHash.myGroup;
import LiteEqualsAndHash.varint;
import TestAllTypes.NestedEnum.BAR;
import TestAllTypes.NestedEnum.BAZ;
import TestAllTypes.NestedEnum.FOO;
import UnittestLite.TestEmptyMessageLite;
import UnittestLite.TestEmptyMessageWithExtensionsLite;
import UnittestLite.optionalInt32ExtensionLite;
import UnittestProto.optionalNestedEnumExtension;
import UnittestProto.repeatedNestedEnumExtension;
import UnknownFieldSet.Field;
import WireFormat.WIRETYPE_END_GROUP;
import WireFormat.WIRETYPE_START_GROUP;
import WireFormat.WIRETYPE_VARINT;
import com.google.protobuf.UnittestLite.TestAllExtensionsLite;
import com.google.protobuf.UnittestLite.TestAllTypesLite;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.TestAllExtensions;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestEmptyMessage;
import protobuf_unittest.UnittestProto.TestPackedExtensions;
import protobuf_unittest.UnittestProto.TestPackedTypes;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.Bar;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.Foo;


/**
 * Tests for {@link UnknownFieldSetLite}.
 *
 * @author dweis@google.com (Daniel Weis)
 */
public class UnknownFieldSetLiteTest extends TestCase {
    TestAllTypes allFields;

    ByteString allFieldsData;

    // An empty message that has been parsed from allFieldsData.  So, it has
    // unknown fields of every type.
    TestEmptyMessage emptyMessage;

    UnknownFieldSet unknownFields;

    public void testDefaultInstance() {
        UnknownFieldSetLite unknownFields = UnknownFieldSetLite.getDefaultInstance();
        TestCase.assertEquals(0, unknownFields.getSerializedSize());
        TestCase.assertEquals(EMPTY, toByteString(unknownFields));
    }

    public void testEmptyInstance() {
        UnknownFieldSetLite instance = UnknownFieldSetLite.newInstance();
        TestCase.assertEquals(0, instance.getSerializedSize());
        TestCase.assertEquals(EMPTY, toByteString(instance));
        TestCase.assertEquals(UnknownFieldSetLite.getDefaultInstance(), instance);
    }

    public void testMergeFieldFrom() throws IOException {
        Foo foo = Foo.newBuilder().setValue(2).build();
        CodedInputStream input = CodedInputStream.newInstance(foo.toByteArray());
        UnknownFieldSetLite instance = UnknownFieldSetLite.newInstance();
        instance.mergeFieldFrom(input.readTag(), input);
        TestCase.assertEquals(foo.toByteString(), toByteString(instance));
    }

    public void testSerializedSize() throws IOException {
        Foo foo = Foo.newBuilder().setValue(2).build();
        CodedInputStream input = CodedInputStream.newInstance(foo.toByteArray());
        UnknownFieldSetLite instance = UnknownFieldSetLite.newInstance();
        instance.mergeFieldFrom(input.readTag(), input);
        TestCase.assertEquals(foo.toByteString().size(), instance.getSerializedSize());
    }

    public void testHashCodeAfterDeserialization() throws IOException {
        Foo foo = Foo.newBuilder().setValue(2).build();
        Foo fooDeserialized = Foo.parseFrom(foo.toByteArray());
        TestCase.assertEquals(fooDeserialized, foo);
        TestCase.assertEquals(foo.hashCode(), fooDeserialized.hashCode());
    }

    public void testNewInstanceHashCode() {
        UnknownFieldSetLite emptyFieldSet = UnknownFieldSetLite.getDefaultInstance();
        UnknownFieldSetLite paddedFieldSet = UnknownFieldSetLite.newInstance();
        TestCase.assertEquals(emptyFieldSet, paddedFieldSet);
        TestCase.assertEquals(emptyFieldSet.hashCode(), paddedFieldSet.hashCode());
    }

    public void testMergeVarintField() throws IOException {
        UnknownFieldSetLite unknownFields = UnknownFieldSetLite.newInstance();
        unknownFields.mergeVarintField(10, 2);
        CodedInputStream input = CodedInputStream.newInstance(toByteString(unknownFields).toByteArray());
        int tag = input.readTag();
        TestCase.assertEquals(10, WireFormat.getTagFieldNumber(tag));
        TestCase.assertEquals(WIRETYPE_VARINT, WireFormat.getTagWireType(tag));
        TestCase.assertEquals(2, input.readUInt64());
        TestCase.assertTrue(input.isAtEnd());
    }

    public void testMergeVarintField_negative() throws IOException {
        UnknownFieldSetLite builder = UnknownFieldSetLite.newInstance();
        builder.mergeVarintField(10, (-6));
        CodedInputStream input = CodedInputStream.newInstance(toByteString(builder).toByteArray());
        int tag = input.readTag();
        TestCase.assertEquals(10, WireFormat.getTagFieldNumber(tag));
        TestCase.assertEquals(WIRETYPE_VARINT, WireFormat.getTagWireType(tag));
        TestCase.assertEquals((-6), input.readUInt64());
        TestCase.assertTrue(input.isAtEnd());
    }

    public void testEqualsAndHashCode() {
        UnknownFieldSetLite unknownFields1 = UnknownFieldSetLite.newInstance();
        unknownFields1.mergeVarintField(10, 2);
        UnknownFieldSetLite unknownFields2 = UnknownFieldSetLite.newInstance();
        unknownFields2.mergeVarintField(10, 2);
        TestCase.assertEquals(unknownFields1, unknownFields2);
        TestCase.assertEquals(unknownFields1.hashCode(), unknownFields2.hashCode());
        TestCase.assertFalse(unknownFields1.equals(UnknownFieldSetLite.getDefaultInstance()));
        TestCase.assertFalse(((unknownFields1.hashCode()) == (UnknownFieldSetLite.getDefaultInstance().hashCode())));
    }

    public void testMutableCopyOf() throws IOException {
        UnknownFieldSetLite unknownFields = UnknownFieldSetLite.newInstance();
        unknownFields.mergeVarintField(10, 2);
        unknownFields = UnknownFieldSetLite.mutableCopyOf(unknownFields, unknownFields);
        unknownFields.checkMutable();
        CodedInputStream input = CodedInputStream.newInstance(toByteString(unknownFields).toByteArray());
        int tag = input.readTag();
        TestCase.assertEquals(10, WireFormat.getTagFieldNumber(tag));
        TestCase.assertEquals(WIRETYPE_VARINT, WireFormat.getTagWireType(tag));
        TestCase.assertEquals(2, input.readUInt64());
        TestCase.assertFalse(input.isAtEnd());
        input.readTag();
        TestCase.assertEquals(10, WireFormat.getTagFieldNumber(tag));
        TestCase.assertEquals(WIRETYPE_VARINT, WireFormat.getTagWireType(tag));
        TestCase.assertEquals(2, input.readUInt64());
        TestCase.assertTrue(input.isAtEnd());
    }

    public void testMutableCopyOf_empty() {
        UnknownFieldSetLite unknownFields = UnknownFieldSetLite.mutableCopyOf(UnknownFieldSetLite.getDefaultInstance(), UnknownFieldSetLite.getDefaultInstance());
        unknownFields.checkMutable();
        TestCase.assertEquals(0, unknownFields.getSerializedSize());
        TestCase.assertEquals(EMPTY, toByteString(unknownFields));
    }

    public void testRoundTrips() throws InvalidProtocolBufferException {
        Foo foo = Foo.newBuilder().setValue(1).setExtension(fooExt, Bar.newBuilder().setName("name").build()).setExtension(varint, 22).setExtension(fixed32, 44).setExtension(fixed64, 66L).setExtension(myGroup, MyGroup.newBuilder().setGroupValue("value").build()).build();
        Foo copy = Foo.parseFrom(foo.toByteArray());
        TestCase.assertEquals(foo.getSerializedSize(), copy.getSerializedSize());
        TestCase.assertFalse(foo.equals(copy));
        Foo secondCopy = Foo.parseFrom(foo.toByteArray());
        TestCase.assertEquals(copy, secondCopy);
        ExtensionRegistryLite extensionRegistry = ExtensionRegistryLite.newInstance();
        LiteEqualsAndHash.registerAllExtensions(extensionRegistry);
        Foo copyOfCopy = Foo.parseFrom(copy.toByteArray(), extensionRegistry);
        TestCase.assertEquals(foo, copyOfCopy);
    }

    public void testMalformedBytes() throws Exception {
        try {
            Foo.parseFrom("this is a malformed protocol buffer".getBytes(UTF_8));
            TestCase.fail();
        } catch (InvalidProtocolBufferException e) {
            // Expected.
        }
    }

    public void testMissingStartGroupTag() throws IOException {
        ByteString.Output byteStringOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(byteStringOutput);
        output.writeGroupNoTag(Foo.newBuilder().setValue(11).build());
        output.writeTag(100, WIRETYPE_END_GROUP);
        output.flush();
        try {
            Foo.parseFrom(byteStringOutput.toByteString());
            TestCase.fail();
        } catch (InvalidProtocolBufferException e) {
            // Expected.
        }
    }

    public void testMissingEndGroupTag() throws IOException {
        ByteString.Output byteStringOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(byteStringOutput);
        output.writeTag(100, WIRETYPE_START_GROUP);
        output.writeGroupNoTag(Foo.newBuilder().setValue(11).build());
        output.flush();
        try {
            Foo.parseFrom(byteStringOutput.toByteString());
            TestCase.fail();
        } catch (InvalidProtocolBufferException e) {
            // Expected.
        }
    }

    public void testMismatchingGroupTags() throws IOException {
        ByteString.Output byteStringOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(byteStringOutput);
        output.writeTag(100, WIRETYPE_START_GROUP);
        output.writeGroupNoTag(Foo.newBuilder().setValue(11).build());
        output.writeTag(101, WIRETYPE_END_GROUP);
        output.flush();
        try {
            Foo.parseFrom(byteStringOutput.toByteString());
            TestCase.fail();
        } catch (InvalidProtocolBufferException e) {
            // Expected.
        }
    }

    public void testTruncatedInput() {
        Foo foo = Foo.newBuilder().setValue(1).setExtension(fooExt, Bar.newBuilder().setName("name").build()).setExtension(varint, 22).setExtension(myGroup, MyGroup.newBuilder().setGroupValue("value").build()).build();
        try {
            Foo.parseFrom(foo.toByteString().substring(0, ((foo.toByteString().size()) - 10)));
            TestCase.fail();
        } catch (InvalidProtocolBufferException e) {
            // Expected.
        }
    }

    public void testMakeImmutable() throws Exception {
        UnknownFieldSetLite unknownFields = UnknownFieldSetLite.newInstance();
        unknownFields.makeImmutable();
        try {
            unknownFields.mergeVarintField(1, 1);
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            unknownFields.mergeLengthDelimitedField(2, ByteString.copyFromUtf8("hello"));
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            unknownFields.mergeFieldFrom(1, CodedInputStream.newInstance(new byte[0]));
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testEndToEnd() throws Exception {
        TestAllTypesLite testAllTypes = TestAllTypesLite.getDefaultInstance();
        try {
            testAllTypes.unknownFields.checkMutable();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        testAllTypes = TestAllTypesLite.parseFrom(new byte[0]);
        try {
            testAllTypes.unknownFields.checkMutable();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        testAllTypes = TestAllTypesLite.newBuilder().build();
        try {
            testAllTypes.unknownFields.checkMutable();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        testAllTypes = TestAllTypesLite.newBuilder().setDefaultBool(true).build();
        try {
            testAllTypes.unknownFields.checkMutable();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
        TestAllExtensionsLite testAllExtensions = TestAllExtensionsLite.newBuilder().mergeFrom(TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 2).build().toByteArray()).build();
        try {
            testAllExtensions.unknownFields.checkMutable();
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    public void testSerializeLite() throws Exception {
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(allFieldsData);
        TestCase.assertEquals(allFieldsData.size(), emptyMessageLite.getSerializedSize());
        ByteString data = emptyMessageLite.toByteString();
        TestAllTypes message = TestAllTypes.parseFrom(data);
        TestUtil.assertAllFieldsSet(message);
        TestCase.assertEquals(allFieldsData, data);
    }

    public void testAllExtensionsLite() throws Exception {
        TestAllExtensions allExtensions = TestUtil.getAllExtensionsSet();
        ByteString allExtensionsData = allExtensions.toByteString();
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parser().parseFrom(allExtensionsData);
        ByteString data = emptyMessageLite.toByteString();
        TestAllExtensions message = TestAllExtensions.parseFrom(data, TestUtil.getExtensionRegistry());
        TestUtil.assertAllExtensionsSet(message);
        TestCase.assertEquals(allExtensionsData, data);
    }

    public void testAllPackedFieldsLite() throws Exception {
        TestPackedTypes allPackedFields = TestUtil.getPackedSet();
        ByteString allPackedData = allPackedFields.toByteString();
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(allPackedData);
        ByteString data = emptyMessageLite.toByteString();
        TestPackedTypes message = TestPackedTypes.parseFrom(data, TestUtil.getExtensionRegistry());
        TestUtil.assertPackedFieldsSet(message);
        TestCase.assertEquals(allPackedData, data);
    }

    public void testAllPackedExtensionsLite() throws Exception {
        TestPackedExtensions allPackedExtensions = TestUtil.getPackedExtensionsSet();
        ByteString allPackedExtensionsData = allPackedExtensions.toByteString();
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(allPackedExtensionsData);
        ByteString data = emptyMessageLite.toByteString();
        TestPackedExtensions message = TestPackedExtensions.parseFrom(data, TestUtil.getExtensionRegistry());
        TestUtil.assertPackedExtensionsSet(message);
        TestCase.assertEquals(allPackedExtensionsData, data);
    }

    public void testCopyFromLite() throws Exception {
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(allFieldsData);
        UnittestLite.TestEmptyMessageLite emptyMessageLite2 = TestEmptyMessageLite.newBuilder().mergeFrom(emptyMessageLite).build();
        TestCase.assertEquals(emptyMessageLite.toByteString(), emptyMessageLite2.toByteString());
    }

    public void testMergeFromLite() throws Exception {
        TestAllTypes message1 = TestAllTypes.newBuilder().setOptionalInt32(1).setOptionalString("foo").addRepeatedString("bar").setOptionalNestedEnum(BAZ).build();
        TestAllTypes message2 = TestAllTypes.newBuilder().setOptionalInt64(2).setOptionalString("baz").addRepeatedString("qux").setOptionalForeignEnum(FOREIGN_BAZ).build();
        ByteString data1 = message1.toByteString();
        UnittestLite.TestEmptyMessageLite emptyMessageLite1 = TestEmptyMessageLite.parseFrom(data1);
        ByteString data2 = message2.toByteString();
        UnittestLite.TestEmptyMessageLite emptyMessageLite2 = TestEmptyMessageLite.parseFrom(data2);
        message1 = TestAllTypes.newBuilder(message1).mergeFrom(message2).build();
        emptyMessageLite1 = TestEmptyMessageLite.newBuilder(emptyMessageLite1).mergeFrom(emptyMessageLite2).build();
        data1 = emptyMessageLite1.toByteString();
        message2 = TestAllTypes.parseFrom(data1);
        TestCase.assertEquals(message1, message2);
    }

    public void testWrongTypeTreatedAsUnknownLite() throws Exception {
        // Test that fields of the wrong wire type are treated like unknown fields
        // when parsing.
        ByteString bizarroData = getBizarroData();
        TestAllTypes allTypesMessage = TestAllTypes.parseFrom(bizarroData);
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(bizarroData);
        ByteString data = emptyMessageLite.toByteString();
        TestAllTypes allTypesMessage2 = TestAllTypes.parseFrom(data);
        TestCase.assertEquals(allTypesMessage.toString(), allTypesMessage2.toString());
    }

    public void testUnknownExtensionsLite() throws Exception {
        // Make sure fields are properly parsed to the UnknownFieldSet even when
        // they are declared as extension numbers.
        UnittestLite.TestEmptyMessageWithExtensionsLite message = TestEmptyMessageWithExtensionsLite.parseFrom(allFieldsData);
        TestCase.assertEquals(allFieldsData, message.toByteString());
    }

    public void testWrongExtensionTypeTreatedAsUnknownLite() throws Exception {
        // Test that fields of the wrong wire type are treated like unknown fields
        // when parsing extensions.
        ByteString bizarroData = getBizarroData();
        TestAllExtensions allExtensionsMessage = TestAllExtensions.parseFrom(bizarroData);
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(bizarroData);
        // All fields should have been interpreted as unknown, so the byte strings
        // should be the same.
        TestCase.assertEquals(emptyMessageLite.toByteString(), allExtensionsMessage.toByteString());
    }

    public void testParseUnknownEnumValueLite() throws Exception {
        Descriptors.FieldDescriptor singularField = TestAllTypes.getDescriptor().findFieldByName("optional_nested_enum");
        Descriptors.FieldDescriptor repeatedField = TestAllTypes.getDescriptor().findFieldByName("repeated_nested_enum");
        TestCase.assertNotNull(singularField);
        TestCase.assertNotNull(repeatedField);
        ByteString data = UnknownFieldSet.newBuilder().addField(singularField.getNumber(), // not valid
        Field.newBuilder().addVarint(BAR.getNumber()).addVarint(5).build()).addField(repeatedField.getNumber(), // not valid
        // not valid
        Field.newBuilder().addVarint(FOO.getNumber()).addVarint(4).addVarint(BAZ.getNumber()).addVarint(6).build()).build().toByteString();
        UnittestLite.TestEmptyMessageLite emptyMessageLite = TestEmptyMessageLite.parseFrom(data);
        data = emptyMessageLite.toByteString();
        {
            TestAllTypes message = TestAllTypes.parseFrom(data);
            TestCase.assertEquals(BAR, message.getOptionalNestedEnum());
            TestCase.assertEquals(Arrays.asList(FOO, BAZ), message.getRepeatedNestedEnumList());
            TestCase.assertEquals(Arrays.asList(5L), message.getUnknownFields().getField(singularField.getNumber()).getVarintList());
            TestCase.assertEquals(Arrays.asList(4L, 6L), message.getUnknownFields().getField(repeatedField.getNumber()).getVarintList());
        }
        {
            TestAllExtensions message = TestAllExtensions.parseFrom(data, TestUtil.getExtensionRegistry());
            TestCase.assertEquals(BAR, message.getExtension(optionalNestedEnumExtension));
            TestCase.assertEquals(Arrays.asList(FOO, BAZ), message.getExtension(repeatedNestedEnumExtension));
            TestCase.assertEquals(Arrays.asList(5L), message.getUnknownFields().getField(singularField.getNumber()).getVarintList());
            TestCase.assertEquals(Arrays.asList(4L, 6L), message.getUnknownFields().getField(repeatedField.getNumber()).getVarintList());
        }
    }

    public void testClearLite() throws Exception {
        UnittestLite.TestEmptyMessageLite emptyMessageLite1 = TestEmptyMessageLite.parseFrom(allFieldsData);
        UnittestLite.TestEmptyMessageLite emptyMessageLite2 = TestEmptyMessageLite.newBuilder().mergeFrom(emptyMessageLite1).clear().build();
        TestCase.assertEquals(0, emptyMessageLite2.getSerializedSize());
        ByteString data = emptyMessageLite2.toByteString();
        TestCase.assertEquals(0, data.size());
    }
}

