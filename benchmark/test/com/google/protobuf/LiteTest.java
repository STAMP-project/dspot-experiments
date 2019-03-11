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
import Foo.MyGroup;
import ForeignEnumLite.FOREIGN_LITE_BAR;
import ForeignEnumLite.FOREIGN_LITE_FOO;
import ImportEnumLite.IMPORT_LITE_BAR;
import Internal.UTF_8;
import NestedEnum.BAR;
import OneofFieldCase.ONEOFFIELD_NOT_SET;
import OneofFieldCase.ONEOF_STRING;
import OneofFieldCase.ONEOF_UINT32;
import TestAllTypesLite.Builder;
import TestAllTypesLite.NestedEnum.BAZ;
import TestAllTypesLite.NestedMessage;
import TestAllTypesLite.REPEATED_NESTED_ENUM_FIELD_NUMBER;
import UnittestLite.optionalFixed32ExtensionLite;
import UnittestLite.optionalInt32ExtensionLite;
import UnittestLite.optionalNestedEnumExtensionLite;
import UnittestLite.optionalNestedMessageExtensionLite;
import UnittestLite.repeatedInt32ExtensionLite;
import UnittestLite.repeatedStringExtensionLite;
import WireFormat.WIRETYPE_LENGTH_DELIMITED;
import com.google.protobuf.FieldPresenceTestProto.TestAllTypes;
import com.google.protobuf.UnittestImportPublicLite.PublicImportMessageLite;
import com.google.protobuf.UnittestLite.ForeignMessageLite;
import com.google.protobuf.UnittestLite.TestAllExtensionsLite;
import com.google.protobuf.UnittestLite.TestAllTypesLite;
import com.google.protobuf.UnittestLite.TestAllTypesLite.OptionalGroup;
import com.google.protobuf.UnittestLite.TestAllTypesLite.RepeatedGroup;
import com.google.protobuf.UnittestLite.TestAllTypesLiteOrBuilder;
import com.google.protobuf.UnittestLite.TestHugeFieldNumbersLite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import map_lite_test.MapTestProto.TestMap;
import map_lite_test.MapTestProto.TestMap.MessageValue;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.Bar;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.BarPrime;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.Foo;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.TestOneofEquals;
import protobuf_unittest.lite_equals_and_hash.LiteEqualsAndHash.TestRecursiveOneof;

import static AbstractProtobufList.DEFAULT_CAPACITY;
import static ByteString.EMPTY;


/**
 * Test lite runtime.
 *
 * @author kenton@google.com Kenton Varda
 */
public class LiteTest extends TestCase {
    public void testLite() throws Exception {
        // Since lite messages are a subset of regular messages, we can mostly
        // assume that the functionality of lite messages is already thoroughly
        // tested by the regular tests.  All this test really verifies is that
        // a proto with optimize_for = LITE_RUNTIME compiles correctly when
        // linked only against the lite library.  That is all tested at compile
        // time, leaving not much to do in this method.  Let's just do some random
        // stuff to make sure the lite message is actually here and usable.
        TestAllTypesLite message = TestAllTypesLite.newBuilder().setOptionalInt32(123).addRepeatedString("hello").setOptionalNestedMessage(NestedMessage.newBuilder().setBb(7)).build();
        ByteString data = message.toByteString();
        TestAllTypesLite message2 = TestAllTypesLite.parseFrom(data);
        TestCase.assertEquals(123, message2.getOptionalInt32());
        TestCase.assertEquals(1, message2.getRepeatedStringCount());
        TestCase.assertEquals("hello", message2.getRepeatedString(0));
        TestCase.assertEquals(7, message2.getOptionalNestedMessage().getBb());
    }

    public void testLite_unknownEnumAtListBoundary() throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(byteStream);
        for (int i = 0; i < (DEFAULT_CAPACITY); i++) {
            output.writeInt32(REPEATED_NESTED_ENUM_FIELD_NUMBER, 1);
        }
        // 0 is not a valid enum value for NestedEnum
        output.writeInt32(REPEATED_NESTED_ENUM_FIELD_NUMBER, 0);
        output.flush();
        // This tests a bug we had once with removal right at the boundary of the array. It would throw
        // at runtime so no need to assert.
        TestAllTypesLite.parseFrom(new ByteArrayInputStream(byteStream.toByteArray()));
    }

    public void testLiteExtensions() throws Exception {
        // TODO(kenton):  Unlike other features of the lite library, extensions are
        // implemented completely differently from the regular library.  We
        // should probably test them more thoroughly.
        TestAllExtensionsLite message = TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123).addExtension(repeatedStringExtensionLite, "hello").setExtension(optionalNestedEnumExtensionLite, BAZ).setExtension(optionalNestedMessageExtensionLite, NestedMessage.newBuilder().setBb(7).build()).build();
        // Test copying a message, since coping extensions actually does use a
        // different code path between lite and regular libraries, and as of this
        // writing, parsing hasn't been implemented yet.
        TestAllExtensionsLite message2 = message.toBuilder().build();
        TestCase.assertEquals(123, ((int) (message2.getExtension(optionalInt32ExtensionLite))));
        TestCase.assertEquals(1, message2.getExtensionCount(repeatedStringExtensionLite));
        TestCase.assertEquals(1, message2.getExtension(repeatedStringExtensionLite).size());
        TestCase.assertEquals("hello", message2.getExtension(repeatedStringExtensionLite, 0));
        TestCase.assertEquals(BAZ, message2.getExtension(optionalNestedEnumExtensionLite));
        TestCase.assertEquals(7, message2.getExtension(optionalNestedMessageExtensionLite).getBb());
    }

    public void testClone() {
        TestAllTypesLite.Builder expected = TestAllTypesLite.newBuilder().setOptionalInt32(123);
        TestCase.assertEquals(expected.getOptionalInt32(), expected.clone().getOptionalInt32());
        TestAllExtensionsLite.Builder expected2 = TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123);
        TestCase.assertEquals(expected2.getExtension(optionalInt32ExtensionLite), expected2.clone().getExtension(optionalInt32ExtensionLite));
    }

    public void testAddAll() {
        try {
            TestAllTypesLite.newBuilder().addAllRepeatedBytes(null);
            TestCase.fail();
        } catch (NullPointerException e) {
            // expected.
        }
    }

    public void testMemoization() throws Exception {
        TestAllExtensionsLite message = TestUtilLite.getAllLiteExtensionsSet();
        // Test serialized size is memoized
        message.memoizedSerializedSize = -1;
        int size = message.getSerializedSize();
        TestCase.assertTrue((size > 0));
        TestCase.assertEquals(size, message.memoizedSerializedSize);
        // Test hashCode is memoized
        TestCase.assertEquals(0, message.memoizedHashCode);
        int hashCode = message.hashCode();
        TestCase.assertTrue((hashCode != 0));
        TestCase.assertEquals(hashCode, message.memoizedHashCode);
        // Test isInitialized is memoized
        Field memo = message.getClass().getDeclaredField("memoizedIsInitialized");
        memo.setAccessible(true);
        memo.set(message, ((byte) (-1)));
        boolean initialized = message.isInitialized();
        TestCase.assertTrue(initialized);
        // We have to cast to Byte first. Casting to byte causes a type error
        TestCase.assertEquals(1, ((Byte) (memo.get(message))).intValue());
    }

    public void testSanityCopyOnWrite() throws InvalidProtocolBufferException {
        // Since builders are implemented as a thin wrapper around a message
        // instance, we attempt to verify that we can't cause the builder to modify
        // a produced message.
        TestAllTypesLite.Builder builder = TestAllTypesLite.newBuilder();
        TestAllTypesLite message = builder.build();
        TestAllTypesLite messageAfterBuild;
        builder.setOptionalBool(true);
        TestCase.assertEquals(false, message.getOptionalBool());
        TestCase.assertEquals(true, builder.getOptionalBool());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(true, messageAfterBuild.getOptionalBool());
        TestCase.assertEquals(false, message.getOptionalBool());
        builder.clearOptionalBool();
        TestCase.assertEquals(false, builder.getOptionalBool());
        TestCase.assertEquals(true, messageAfterBuild.getOptionalBool());
        message = builder.build();
        builder.setOptionalBytes(ByteString.copyFromUtf8("hi"));
        TestCase.assertEquals(EMPTY, message.getOptionalBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), builder.getOptionalBytes());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), messageAfterBuild.getOptionalBytes());
        TestCase.assertEquals(EMPTY, message.getOptionalBytes());
        builder.clearOptionalBytes();
        TestCase.assertEquals(EMPTY, builder.getOptionalBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), messageAfterBuild.getOptionalBytes());
        message = builder.build();
        builder.setOptionalCord("hi");
        TestCase.assertEquals("", message.getOptionalCord());
        TestCase.assertEquals("hi", builder.getOptionalCord());
        messageAfterBuild = builder.build();
        TestCase.assertEquals("hi", messageAfterBuild.getOptionalCord());
        TestCase.assertEquals("", message.getOptionalCord());
        builder.clearOptionalCord();
        TestCase.assertEquals("", builder.getOptionalCord());
        TestCase.assertEquals("hi", messageAfterBuild.getOptionalCord());
        message = builder.build();
        builder.setOptionalCordBytes(ByteString.copyFromUtf8("no"));
        TestCase.assertEquals(EMPTY, message.getOptionalCordBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), builder.getOptionalCordBytes());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), messageAfterBuild.getOptionalCordBytes());
        TestCase.assertEquals(EMPTY, message.getOptionalCordBytes());
        builder.clearOptionalCord();
        TestCase.assertEquals(EMPTY, builder.getOptionalCordBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), messageAfterBuild.getOptionalCordBytes());
        message = builder.build();
        builder.setOptionalDouble(1);
        TestCase.assertEquals(0.0, message.getOptionalDouble(), 0.0);
        TestCase.assertEquals(1.0, builder.getOptionalDouble(), 0.0);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1.0, messageAfterBuild.getOptionalDouble(), 0.0);
        TestCase.assertEquals(0.0, message.getOptionalDouble(), 0.0);
        builder.clearOptionalDouble();
        TestCase.assertEquals(0.0, builder.getOptionalDouble(), 0.0);
        TestCase.assertEquals(1.0, messageAfterBuild.getOptionalDouble(), 0.0);
        message = builder.build();
        builder.setOptionalFixed32(1);
        TestCase.assertEquals(0, message.getOptionalFixed32());
        TestCase.assertEquals(1, builder.getOptionalFixed32());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1, messageAfterBuild.getOptionalFixed32());
        TestCase.assertEquals(0, message.getOptionalFixed32());
        builder.clearOptionalFixed32();
        TestCase.assertEquals(0, builder.getOptionalFixed32());
        TestCase.assertEquals(1, messageAfterBuild.getOptionalFixed32());
        message = builder.build();
        builder.setOptionalFixed64(1);
        TestCase.assertEquals(0L, message.getOptionalFixed64());
        TestCase.assertEquals(1L, builder.getOptionalFixed64());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalFixed64());
        TestCase.assertEquals(0L, message.getOptionalFixed64());
        builder.clearOptionalFixed64();
        TestCase.assertEquals(0L, builder.getOptionalFixed64());
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalFixed64());
        message = builder.build();
        builder.setOptionalFloat(1);
        TestCase.assertEquals(0.0F, message.getOptionalFloat(), 0.0F);
        TestCase.assertEquals(1.0F, builder.getOptionalFloat(), 0.0F);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1.0F, messageAfterBuild.getOptionalFloat(), 0.0F);
        TestCase.assertEquals(0.0F, message.getOptionalFloat(), 0.0F);
        builder.clearOptionalFloat();
        TestCase.assertEquals(0.0F, builder.getOptionalFloat(), 0.0F);
        TestCase.assertEquals(1.0F, messageAfterBuild.getOptionalFloat(), 0.0F);
        message = builder.build();
        builder.setOptionalForeignEnum(FOREIGN_LITE_BAR);
        TestCase.assertEquals(FOREIGN_LITE_FOO, message.getOptionalForeignEnum());
        TestCase.assertEquals(FOREIGN_LITE_BAR, builder.getOptionalForeignEnum());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(FOREIGN_LITE_BAR, messageAfterBuild.getOptionalForeignEnum());
        TestCase.assertEquals(FOREIGN_LITE_FOO, message.getOptionalForeignEnum());
        builder.clearOptionalForeignEnum();
        TestCase.assertEquals(FOREIGN_LITE_FOO, builder.getOptionalForeignEnum());
        TestCase.assertEquals(FOREIGN_LITE_BAR, messageAfterBuild.getOptionalForeignEnum());
        message = builder.build();
        ForeignMessageLite foreignMessage = ForeignMessageLite.newBuilder().setC(1).build();
        builder.setOptionalForeignMessage(foreignMessage);
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), message.getOptionalForeignMessage());
        TestCase.assertEquals(foreignMessage, builder.getOptionalForeignMessage());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(foreignMessage, messageAfterBuild.getOptionalForeignMessage());
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), message.getOptionalForeignMessage());
        builder.clearOptionalForeignMessage();
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), builder.getOptionalForeignMessage());
        TestCase.assertEquals(foreignMessage, messageAfterBuild.getOptionalForeignMessage());
        message = builder.build();
        ForeignMessageLite.Builder foreignMessageBuilder = ForeignMessageLite.newBuilder().setC(3);
        builder.setOptionalForeignMessage(foreignMessageBuilder);
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), message.getOptionalForeignMessage());
        TestCase.assertEquals(foreignMessageBuilder.build(), builder.getOptionalForeignMessage());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(foreignMessageBuilder.build(), messageAfterBuild.getOptionalForeignMessage());
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), message.getOptionalForeignMessage());
        builder.clearOptionalForeignMessage();
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), builder.getOptionalForeignMessage());
        TestCase.assertEquals(foreignMessageBuilder.build(), messageAfterBuild.getOptionalForeignMessage());
        message = builder.build();
        OptionalGroup optionalGroup = OptionalGroup.newBuilder().setA(1).build();
        builder.setOptionalGroup(optionalGroup);
        TestCase.assertEquals(OptionalGroup.getDefaultInstance(), message.getOptionalGroup());
        TestCase.assertEquals(optionalGroup, builder.getOptionalGroup());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(optionalGroup, messageAfterBuild.getOptionalGroup());
        TestCase.assertEquals(OptionalGroup.getDefaultInstance(), message.getOptionalGroup());
        builder.clearOptionalGroup();
        TestCase.assertEquals(OptionalGroup.getDefaultInstance(), builder.getOptionalGroup());
        TestCase.assertEquals(optionalGroup, messageAfterBuild.getOptionalGroup());
        message = builder.build();
        OptionalGroup.Builder optionalGroupBuilder = OptionalGroup.newBuilder().setA(3);
        builder.setOptionalGroup(optionalGroupBuilder);
        TestCase.assertEquals(OptionalGroup.getDefaultInstance(), message.getOptionalGroup());
        TestCase.assertEquals(optionalGroupBuilder.build(), builder.getOptionalGroup());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(optionalGroupBuilder.build(), messageAfterBuild.getOptionalGroup());
        TestCase.assertEquals(OptionalGroup.getDefaultInstance(), message.getOptionalGroup());
        builder.clearOptionalGroup();
        TestCase.assertEquals(OptionalGroup.getDefaultInstance(), builder.getOptionalGroup());
        TestCase.assertEquals(optionalGroupBuilder.build(), messageAfterBuild.getOptionalGroup());
        message = builder.build();
        builder.setOptionalInt32(1);
        TestCase.assertEquals(0, message.getOptionalInt32());
        TestCase.assertEquals(1, builder.getOptionalInt32());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1, messageAfterBuild.getOptionalInt32());
        TestCase.assertEquals(0, message.getOptionalInt32());
        builder.clearOptionalInt32();
        TestCase.assertEquals(0, builder.getOptionalInt32());
        TestCase.assertEquals(1, messageAfterBuild.getOptionalInt32());
        message = builder.build();
        builder.setOptionalInt64(1);
        TestCase.assertEquals(0L, message.getOptionalInt64());
        TestCase.assertEquals(1L, builder.getOptionalInt64());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalInt64());
        TestCase.assertEquals(0L, message.getOptionalInt64());
        builder.clearOptionalInt64();
        TestCase.assertEquals(0L, builder.getOptionalInt64());
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalInt64());
        message = builder.build();
        com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage nestedMessage = com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setBb(1).build();
        builder.setOptionalLazyMessage(nestedMessage);
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), message.getOptionalLazyMessage());
        TestCase.assertEquals(nestedMessage, builder.getOptionalLazyMessage());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(nestedMessage, messageAfterBuild.getOptionalLazyMessage());
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), message.getOptionalLazyMessage());
        builder.clearOptionalLazyMessage();
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), builder.getOptionalLazyMessage());
        TestCase.assertEquals(nestedMessage, messageAfterBuild.getOptionalLazyMessage());
        message = builder.build();
        com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.Builder nestedMessageBuilder = com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setBb(3);
        builder.setOptionalLazyMessage(nestedMessageBuilder);
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), message.getOptionalLazyMessage());
        TestCase.assertEquals(nestedMessageBuilder.build(), builder.getOptionalLazyMessage());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(nestedMessageBuilder.build(), messageAfterBuild.getOptionalLazyMessage());
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), message.getOptionalLazyMessage());
        builder.clearOptionalLazyMessage();
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), builder.getOptionalLazyMessage());
        TestCase.assertEquals(nestedMessageBuilder.build(), messageAfterBuild.getOptionalLazyMessage());
        message = builder.build();
        builder.setOptionalSfixed32(1);
        TestCase.assertEquals(0, message.getOptionalSfixed32());
        TestCase.assertEquals(1, builder.getOptionalSfixed32());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1, messageAfterBuild.getOptionalSfixed32());
        TestCase.assertEquals(0, message.getOptionalSfixed32());
        builder.clearOptionalSfixed32();
        TestCase.assertEquals(0, builder.getOptionalSfixed32());
        TestCase.assertEquals(1, messageAfterBuild.getOptionalSfixed32());
        message = builder.build();
        builder.setOptionalSfixed64(1);
        TestCase.assertEquals(0L, message.getOptionalSfixed64());
        TestCase.assertEquals(1L, builder.getOptionalSfixed64());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalSfixed64());
        TestCase.assertEquals(0L, message.getOptionalSfixed64());
        builder.clearOptionalSfixed64();
        TestCase.assertEquals(0L, builder.getOptionalSfixed64());
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalSfixed64());
        message = builder.build();
        builder.setOptionalSint32(1);
        TestCase.assertEquals(0, message.getOptionalSint32());
        TestCase.assertEquals(1, builder.getOptionalSint32());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1, messageAfterBuild.getOptionalSint32());
        builder.clearOptionalSint32();
        TestCase.assertEquals(0, builder.getOptionalSint32());
        TestCase.assertEquals(1, messageAfterBuild.getOptionalSint32());
        message = builder.build();
        builder.setOptionalSint64(1);
        TestCase.assertEquals(0L, message.getOptionalSint64());
        TestCase.assertEquals(1L, builder.getOptionalSint64());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalSint64());
        TestCase.assertEquals(0L, message.getOptionalSint64());
        builder.clearOptionalSint64();
        TestCase.assertEquals(0L, builder.getOptionalSint64());
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalSint64());
        message = builder.build();
        builder.setOptionalString("hi");
        TestCase.assertEquals("", message.getOptionalString());
        TestCase.assertEquals("hi", builder.getOptionalString());
        messageAfterBuild = builder.build();
        TestCase.assertEquals("hi", messageAfterBuild.getOptionalString());
        TestCase.assertEquals("", message.getOptionalString());
        builder.clearOptionalString();
        TestCase.assertEquals("", builder.getOptionalString());
        TestCase.assertEquals("hi", messageAfterBuild.getOptionalString());
        message = builder.build();
        builder.setOptionalStringBytes(ByteString.copyFromUtf8("no"));
        TestCase.assertEquals(EMPTY, message.getOptionalStringBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), builder.getOptionalStringBytes());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), messageAfterBuild.getOptionalStringBytes());
        TestCase.assertEquals(EMPTY, message.getOptionalStringBytes());
        builder.clearOptionalString();
        TestCase.assertEquals(EMPTY, builder.getOptionalStringBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), messageAfterBuild.getOptionalStringBytes());
        message = builder.build();
        builder.setOptionalStringPiece("hi");
        TestCase.assertEquals("", message.getOptionalStringPiece());
        TestCase.assertEquals("hi", builder.getOptionalStringPiece());
        messageAfterBuild = builder.build();
        TestCase.assertEquals("hi", messageAfterBuild.getOptionalStringPiece());
        TestCase.assertEquals("", message.getOptionalStringPiece());
        builder.clearOptionalStringPiece();
        TestCase.assertEquals("", builder.getOptionalStringPiece());
        TestCase.assertEquals("hi", messageAfterBuild.getOptionalStringPiece());
        message = builder.build();
        builder.setOptionalStringPieceBytes(ByteString.copyFromUtf8("no"));
        TestCase.assertEquals(EMPTY, message.getOptionalStringPieceBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), builder.getOptionalStringPieceBytes());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), messageAfterBuild.getOptionalStringPieceBytes());
        TestCase.assertEquals(EMPTY, message.getOptionalStringPieceBytes());
        builder.clearOptionalStringPiece();
        TestCase.assertEquals(EMPTY, builder.getOptionalStringPieceBytes());
        TestCase.assertEquals(ByteString.copyFromUtf8("no"), messageAfterBuild.getOptionalStringPieceBytes());
        message = builder.build();
        builder.setOptionalUint32(1);
        TestCase.assertEquals(0, message.getOptionalUint32());
        TestCase.assertEquals(1, builder.getOptionalUint32());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1, messageAfterBuild.getOptionalUint32());
        TestCase.assertEquals(0, message.getOptionalUint32());
        builder.clearOptionalUint32();
        TestCase.assertEquals(0, builder.getOptionalUint32());
        TestCase.assertEquals(1, messageAfterBuild.getOptionalUint32());
        message = builder.build();
        builder.setOptionalUint64(1);
        TestCase.assertEquals(0L, message.getOptionalUint64());
        TestCase.assertEquals(1L, builder.getOptionalUint64());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalUint64());
        TestCase.assertEquals(0L, message.getOptionalUint64());
        builder.clearOptionalUint64();
        TestCase.assertEquals(0L, builder.getOptionalUint64());
        TestCase.assertEquals(1L, messageAfterBuild.getOptionalUint64());
        message = builder.build();
        builder.addAllRepeatedBool(Collections.singletonList(true));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBoolList());
        TestCase.assertEquals(Collections.singletonList(true), builder.getRepeatedBoolList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBoolList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedBool();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedBoolList());
        TestCase.assertEquals(Collections.singletonList(true), messageAfterBuild.getRepeatedBoolList());
        message = builder.build();
        builder.addAllRepeatedBytes(Collections.singletonList(ByteString.copyFromUtf8("hi")));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBytesList());
        TestCase.assertEquals(Collections.singletonList(ByteString.copyFromUtf8("hi")), builder.getRepeatedBytesList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBytesList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedBytes();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedBytesList());
        TestCase.assertEquals(Collections.singletonList(ByteString.copyFromUtf8("hi")), messageAfterBuild.getRepeatedBytesList());
        message = builder.build();
        builder.addAllRepeatedCord(Collections.singletonList("hi"));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedCordList());
        TestCase.assertEquals(Collections.singletonList("hi"), builder.getRepeatedCordList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedCordList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedCord();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedCordList());
        TestCase.assertEquals(Collections.singletonList("hi"), messageAfterBuild.getRepeatedCordList());
        message = builder.build();
        builder.addAllRepeatedDouble(Collections.singletonList(1.0));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedDoubleList());
        TestCase.assertEquals(Collections.singletonList(1.0), builder.getRepeatedDoubleList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedDoubleList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedDouble();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedDoubleList());
        TestCase.assertEquals(Collections.singletonList(1.0), messageAfterBuild.getRepeatedDoubleList());
        message = builder.build();
        builder.addAllRepeatedFixed32(Collections.singletonList(1));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedFixed32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedFixed32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedFixed32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedFixed32List());
        message = builder.build();
        builder.addAllRepeatedFixed64(Collections.singletonList(1L));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedFixed64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedFixed64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedFixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedFixed64List());
        message = builder.build();
        builder.addAllRepeatedFloat(Collections.singletonList(1.0F));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFloatList());
        TestCase.assertEquals(Collections.singletonList(1.0F), builder.getRepeatedFloatList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFloatList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedFloat();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedFloatList());
        TestCase.assertEquals(Collections.singletonList(1.0F), messageAfterBuild.getRepeatedFloatList());
        message = builder.build();
        builder.addAllRepeatedForeignEnum(Collections.singletonList(FOREIGN_LITE_BAR));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignEnumList());
        TestCase.assertEquals(Collections.singletonList(FOREIGN_LITE_BAR), builder.getRepeatedForeignEnumList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignEnumList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedForeignEnum();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedForeignEnumList());
        TestCase.assertEquals(Collections.singletonList(FOREIGN_LITE_BAR), messageAfterBuild.getRepeatedForeignEnumList());
        message = builder.build();
        builder.addAllRepeatedForeignMessage(Collections.singletonList(foreignMessage));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignMessageList());
        TestCase.assertEquals(Collections.singletonList(foreignMessage), builder.getRepeatedForeignMessageList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignMessageList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedForeignMessage();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedForeignMessageList());
        TestCase.assertEquals(Collections.singletonList(foreignMessage), messageAfterBuild.getRepeatedForeignMessageList());
        message = builder.build();
        builder.addAllRepeatedGroup(Collections.singletonList(RepeatedGroup.getDefaultInstance()));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedGroupList());
        TestCase.assertEquals(Collections.singletonList(RepeatedGroup.getDefaultInstance()), builder.getRepeatedGroupList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedGroupList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedGroup();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedGroupList());
        TestCase.assertEquals(Collections.singletonList(RepeatedGroup.getDefaultInstance()), messageAfterBuild.getRepeatedGroupList());
        message = builder.build();
        builder.addAllRepeatedInt32(Collections.singletonList(1));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedInt32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedInt32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedInt32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedInt32List());
        message = builder.build();
        builder.addAllRepeatedInt64(Collections.singletonList(1L));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedInt64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedInt64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedInt64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedInt64List());
        message = builder.build();
        builder.addAllRepeatedLazyMessage(Collections.singletonList(nestedMessage));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedLazyMessageList());
        TestCase.assertEquals(Collections.singletonList(nestedMessage), builder.getRepeatedLazyMessageList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedLazyMessageList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedLazyMessage();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedLazyMessageList());
        TestCase.assertEquals(Collections.singletonList(nestedMessage), messageAfterBuild.getRepeatedLazyMessageList());
        message = builder.build();
        builder.addAllRepeatedSfixed32(Collections.singletonList(1));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedSfixed32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSfixed32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSfixed32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedSfixed32List());
        message = builder.build();
        builder.addAllRepeatedSfixed64(Collections.singletonList(1L));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedSfixed64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSfixed64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSfixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedSfixed64List());
        message = builder.build();
        builder.addAllRepeatedSint32(Collections.singletonList(1));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedSint32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSint32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSint32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedSint32List());
        message = builder.build();
        builder.addAllRepeatedSint64(Collections.singletonList(1L));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedSint64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSint64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSint64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedSint64List());
        message = builder.build();
        builder.addAllRepeatedString(Collections.singletonList("hi"));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringList());
        TestCase.assertEquals(Collections.singletonList("hi"), builder.getRepeatedStringList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedString();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedStringList());
        TestCase.assertEquals(Collections.singletonList("hi"), messageAfterBuild.getRepeatedStringList());
        message = builder.build();
        builder.addAllRepeatedStringPiece(Collections.singletonList("hi"));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringPieceList());
        TestCase.assertEquals(Collections.singletonList("hi"), builder.getRepeatedStringPieceList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringPieceList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedStringPiece();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedStringPieceList());
        TestCase.assertEquals(Collections.singletonList("hi"), messageAfterBuild.getRepeatedStringPieceList());
        message = builder.build();
        builder.addAllRepeatedUint32(Collections.singletonList(1));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedUint32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedUint32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedUint32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedUint32List());
        message = builder.build();
        builder.addAllRepeatedUint64(Collections.singletonList(1L));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedUint64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedUint64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedUint64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedUint64List());
        message = builder.build();
        builder.addRepeatedBool(true);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBoolList());
        TestCase.assertEquals(Collections.singletonList(true), builder.getRepeatedBoolList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBoolList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedBool();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedBoolList());
        TestCase.assertEquals(Collections.singletonList(true), messageAfterBuild.getRepeatedBoolList());
        message = builder.build();
        builder.addRepeatedBytes(ByteString.copyFromUtf8("hi"));
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBytesList());
        TestCase.assertEquals(Collections.singletonList(ByteString.copyFromUtf8("hi")), builder.getRepeatedBytesList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedBytesList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedBytes();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedBytesList());
        TestCase.assertEquals(Collections.singletonList(ByteString.copyFromUtf8("hi")), messageAfterBuild.getRepeatedBytesList());
        message = builder.build();
        builder.addRepeatedCord("hi");
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedCordList());
        TestCase.assertEquals(Collections.singletonList("hi"), builder.getRepeatedCordList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedCordList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedCord();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedCordList());
        TestCase.assertEquals(Collections.singletonList("hi"), messageAfterBuild.getRepeatedCordList());
        message = builder.build();
        builder.addRepeatedDouble(1.0);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedDoubleList());
        TestCase.assertEquals(Collections.singletonList(1.0), builder.getRepeatedDoubleList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedDoubleList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedDouble();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedDoubleList());
        TestCase.assertEquals(Collections.singletonList(1.0), messageAfterBuild.getRepeatedDoubleList());
        message = builder.build();
        builder.addRepeatedFixed32(1);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedFixed32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedFixed32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedFixed32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedFixed32List());
        message = builder.build();
        builder.addRepeatedFixed64(1L);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedFixed64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFixed64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedFixed64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedFixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedFixed64List());
        message = builder.build();
        builder.addRepeatedFloat(1.0F);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFloatList());
        TestCase.assertEquals(Collections.singletonList(1.0F), builder.getRepeatedFloatList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedFloatList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedFloat();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedFloatList());
        TestCase.assertEquals(Collections.singletonList(1.0F), messageAfterBuild.getRepeatedFloatList());
        message = builder.build();
        builder.addRepeatedForeignEnum(FOREIGN_LITE_BAR);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignEnumList());
        TestCase.assertEquals(Collections.singletonList(FOREIGN_LITE_BAR), builder.getRepeatedForeignEnumList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignEnumList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedForeignEnum();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedForeignEnumList());
        TestCase.assertEquals(Collections.singletonList(FOREIGN_LITE_BAR), messageAfterBuild.getRepeatedForeignEnumList());
        message = builder.build();
        builder.addRepeatedForeignMessage(foreignMessage);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignMessageList());
        TestCase.assertEquals(Collections.singletonList(foreignMessage), builder.getRepeatedForeignMessageList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedForeignMessageList());
        messageAfterBuild = builder.build();
        builder.removeRepeatedForeignMessage(0);
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedForeignMessageList());
        TestCase.assertEquals(Collections.singletonList(foreignMessage), messageAfterBuild.getRepeatedForeignMessageList());
        message = builder.build();
        builder.addRepeatedGroup(RepeatedGroup.getDefaultInstance());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedGroupList());
        TestCase.assertEquals(Collections.singletonList(RepeatedGroup.getDefaultInstance()), builder.getRepeatedGroupList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedGroupList());
        messageAfterBuild = builder.build();
        builder.removeRepeatedGroup(0);
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedGroupList());
        TestCase.assertEquals(Collections.singletonList(RepeatedGroup.getDefaultInstance()), messageAfterBuild.getRepeatedGroupList());
        message = builder.build();
        builder.addRepeatedInt32(1);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedInt32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedInt32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedInt32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedInt32List());
        message = builder.build();
        builder.addRepeatedInt64(1L);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedInt64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedInt64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedInt64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedInt64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedInt64List());
        message = builder.build();
        builder.addRepeatedLazyMessage(nestedMessage);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedLazyMessageList());
        TestCase.assertEquals(Collections.singletonList(nestedMessage), builder.getRepeatedLazyMessageList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedLazyMessageList());
        messageAfterBuild = builder.build();
        builder.removeRepeatedLazyMessage(0);
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedLazyMessageList());
        TestCase.assertEquals(Collections.singletonList(nestedMessage), messageAfterBuild.getRepeatedLazyMessageList());
        message = builder.build();
        builder.addRepeatedSfixed32(1);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedSfixed32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSfixed32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSfixed32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedSfixed32List());
        message = builder.build();
        builder.addRepeatedSfixed64(1L);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedSfixed64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSfixed64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSfixed64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSfixed64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedSfixed64List());
        message = builder.build();
        builder.addRepeatedSint32(1);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedSint32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSint32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSint32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedSint32List());
        message = builder.build();
        builder.addRepeatedSint64(1L);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedSint64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedSint64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedSint64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedSint64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedSint64List());
        message = builder.build();
        builder.addRepeatedString("hi");
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringList());
        TestCase.assertEquals(Collections.singletonList("hi"), builder.getRepeatedStringList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedString();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedStringList());
        TestCase.assertEquals(Collections.singletonList("hi"), messageAfterBuild.getRepeatedStringList());
        message = builder.build();
        builder.addRepeatedStringPiece("hi");
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringPieceList());
        TestCase.assertEquals(Collections.singletonList("hi"), builder.getRepeatedStringPieceList());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedStringPieceList());
        messageAfterBuild = builder.build();
        builder.clearRepeatedStringPiece();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedStringPieceList());
        TestCase.assertEquals(Collections.singletonList("hi"), messageAfterBuild.getRepeatedStringPieceList());
        message = builder.build();
        builder.addRepeatedUint32(1);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint32List());
        TestCase.assertEquals(Collections.singletonList(1), builder.getRepeatedUint32List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint32List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedUint32();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedUint32List());
        TestCase.assertEquals(Collections.singletonList(1), messageAfterBuild.getRepeatedUint32List());
        message = builder.build();
        builder.addRepeatedUint64(1L);
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint64List());
        TestCase.assertEquals(Collections.singletonList(1L), builder.getRepeatedUint64List());
        TestCase.assertEquals(Collections.emptyList(), message.getRepeatedUint64List());
        messageAfterBuild = builder.build();
        builder.clearRepeatedUint64();
        TestCase.assertEquals(Collections.emptyList(), builder.getRepeatedUint64List());
        TestCase.assertEquals(Collections.singletonList(1L), messageAfterBuild.getRepeatedUint64List());
        message = builder.build();
        builder.addRepeatedBool(true);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedBoolCount());
        builder.setRepeatedBool(0, false);
        TestCase.assertEquals(true, messageAfterBuild.getRepeatedBool(0));
        TestCase.assertEquals(false, builder.getRepeatedBool(0));
        builder.clearRepeatedBool();
        message = builder.build();
        builder.addRepeatedBytes(ByteString.copyFromUtf8("hi"));
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedBytesCount());
        builder.setRepeatedBytes(0, EMPTY);
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), messageAfterBuild.getRepeatedBytes(0));
        TestCase.assertEquals(EMPTY, builder.getRepeatedBytes(0));
        builder.clearRepeatedBytes();
        message = builder.build();
        builder.addRepeatedCord("hi");
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedCordCount());
        builder.setRepeatedCord(0, "");
        TestCase.assertEquals("hi", messageAfterBuild.getRepeatedCord(0));
        TestCase.assertEquals("", builder.getRepeatedCord(0));
        builder.clearRepeatedCord();
        message = builder.build();
        builder.addRepeatedCordBytes(ByteString.copyFromUtf8("hi"));
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedCordCount());
        builder.setRepeatedCord(0, "");
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), messageAfterBuild.getRepeatedCordBytes(0));
        TestCase.assertEquals(EMPTY, builder.getRepeatedCordBytes(0));
        builder.clearRepeatedCord();
        message = builder.build();
        builder.addRepeatedDouble(1.0);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedDoubleCount());
        builder.setRepeatedDouble(0, 0.0);
        TestCase.assertEquals(1.0, messageAfterBuild.getRepeatedDouble(0), 0.0);
        TestCase.assertEquals(0.0, builder.getRepeatedDouble(0), 0.0);
        builder.clearRepeatedDouble();
        message = builder.build();
        builder.addRepeatedFixed32(1);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedFixed32Count());
        builder.setRepeatedFixed32(0, 0);
        TestCase.assertEquals(1, messageAfterBuild.getRepeatedFixed32(0));
        TestCase.assertEquals(0, builder.getRepeatedFixed32(0));
        builder.clearRepeatedFixed32();
        message = builder.build();
        builder.addRepeatedFixed64(1L);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedFixed64Count());
        builder.setRepeatedFixed64(0, 0L);
        TestCase.assertEquals(1L, messageAfterBuild.getRepeatedFixed64(0));
        TestCase.assertEquals(0L, builder.getRepeatedFixed64(0));
        builder.clearRepeatedFixed64();
        message = builder.build();
        builder.addRepeatedFloat(1.0F);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedFloatCount());
        builder.setRepeatedFloat(0, 0.0F);
        TestCase.assertEquals(1.0F, messageAfterBuild.getRepeatedFloat(0), 0.0F);
        TestCase.assertEquals(0.0F, builder.getRepeatedFloat(0), 0.0F);
        builder.clearRepeatedFloat();
        message = builder.build();
        builder.addRepeatedForeignEnum(FOREIGN_LITE_BAR);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedForeignEnumCount());
        builder.setRepeatedForeignEnum(0, FOREIGN_LITE_FOO);
        TestCase.assertEquals(FOREIGN_LITE_BAR, messageAfterBuild.getRepeatedForeignEnum(0));
        TestCase.assertEquals(FOREIGN_LITE_FOO, builder.getRepeatedForeignEnum(0));
        builder.clearRepeatedForeignEnum();
        message = builder.build();
        builder.addRepeatedForeignMessage(foreignMessage);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedForeignMessageCount());
        builder.setRepeatedForeignMessage(0, ForeignMessageLite.getDefaultInstance());
        TestCase.assertEquals(foreignMessage, messageAfterBuild.getRepeatedForeignMessage(0));
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), builder.getRepeatedForeignMessage(0));
        builder.clearRepeatedForeignMessage();
        message = builder.build();
        builder.addRepeatedForeignMessage(foreignMessageBuilder);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedForeignMessageCount());
        builder.setRepeatedForeignMessage(0, ForeignMessageLite.getDefaultInstance());
        TestCase.assertEquals(foreignMessageBuilder.build(), messageAfterBuild.getRepeatedForeignMessage(0));
        TestCase.assertEquals(ForeignMessageLite.getDefaultInstance(), builder.getRepeatedForeignMessage(0));
        builder.clearRepeatedForeignMessage();
        message = builder.build();
        builder.addRepeatedForeignMessage(0, foreignMessage);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedForeignMessageCount());
        builder.setRepeatedForeignMessage(0, foreignMessageBuilder);
        TestCase.assertEquals(foreignMessage, messageAfterBuild.getRepeatedForeignMessage(0));
        TestCase.assertEquals(foreignMessageBuilder.build(), builder.getRepeatedForeignMessage(0));
        builder.clearRepeatedForeignMessage();
        message = builder.build();
        RepeatedGroup repeatedGroup = RepeatedGroup.newBuilder().setA(1).build();
        builder.addRepeatedGroup(repeatedGroup);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedGroupCount());
        builder.setRepeatedGroup(0, RepeatedGroup.getDefaultInstance());
        TestCase.assertEquals(repeatedGroup, messageAfterBuild.getRepeatedGroup(0));
        TestCase.assertEquals(RepeatedGroup.getDefaultInstance(), builder.getRepeatedGroup(0));
        builder.clearRepeatedGroup();
        message = builder.build();
        builder.addRepeatedGroup(0, repeatedGroup);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedGroupCount());
        builder.setRepeatedGroup(0, RepeatedGroup.getDefaultInstance());
        TestCase.assertEquals(repeatedGroup, messageAfterBuild.getRepeatedGroup(0));
        TestCase.assertEquals(RepeatedGroup.getDefaultInstance(), builder.getRepeatedGroup(0));
        builder.clearRepeatedGroup();
        message = builder.build();
        RepeatedGroup.Builder repeatedGroupBuilder = RepeatedGroup.newBuilder().setA(3);
        builder.addRepeatedGroup(repeatedGroupBuilder);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedGroupCount());
        builder.setRepeatedGroup(0, RepeatedGroup.getDefaultInstance());
        TestCase.assertEquals(repeatedGroupBuilder.build(), messageAfterBuild.getRepeatedGroup(0));
        TestCase.assertEquals(RepeatedGroup.getDefaultInstance(), builder.getRepeatedGroup(0));
        builder.clearRepeatedGroup();
        message = builder.build();
        builder.addRepeatedGroup(0, repeatedGroupBuilder);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedGroupCount());
        builder.setRepeatedGroup(0, RepeatedGroup.getDefaultInstance());
        TestCase.assertEquals(repeatedGroupBuilder.build(), messageAfterBuild.getRepeatedGroup(0));
        TestCase.assertEquals(RepeatedGroup.getDefaultInstance(), builder.getRepeatedGroup(0));
        builder.clearRepeatedGroup();
        message = builder.build();
        builder.addRepeatedInt32(1);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedInt32Count());
        builder.setRepeatedInt32(0, 0);
        TestCase.assertEquals(1, messageAfterBuild.getRepeatedInt32(0));
        TestCase.assertEquals(0, builder.getRepeatedInt32(0));
        builder.clearRepeatedInt32();
        message = builder.build();
        builder.addRepeatedInt64(1L);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedInt64Count());
        builder.setRepeatedInt64(0, 0L);
        TestCase.assertEquals(1L, messageAfterBuild.getRepeatedInt64(0));
        TestCase.assertEquals(0L, builder.getRepeatedInt64(0));
        builder.clearRepeatedInt64();
        message = builder.build();
        builder.addRepeatedLazyMessage(nestedMessage);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedLazyMessageCount());
        builder.setRepeatedLazyMessage(0, com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance());
        TestCase.assertEquals(nestedMessage, messageAfterBuild.getRepeatedLazyMessage(0));
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), builder.getRepeatedLazyMessage(0));
        builder.clearRepeatedLazyMessage();
        message = builder.build();
        builder.addRepeatedLazyMessage(0, nestedMessage);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedLazyMessageCount());
        builder.setRepeatedLazyMessage(0, com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance());
        TestCase.assertEquals(nestedMessage, messageAfterBuild.getRepeatedLazyMessage(0));
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), builder.getRepeatedLazyMessage(0));
        builder.clearRepeatedLazyMessage();
        message = builder.build();
        builder.addRepeatedLazyMessage(nestedMessageBuilder);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedLazyMessageCount());
        builder.setRepeatedLazyMessage(0, com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance());
        TestCase.assertEquals(nestedMessageBuilder.build(), messageAfterBuild.getRepeatedLazyMessage(0));
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), builder.getRepeatedLazyMessage(0));
        builder.clearRepeatedLazyMessage();
        message = builder.build();
        builder.addRepeatedLazyMessage(0, nestedMessageBuilder);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedLazyMessageCount());
        builder.setRepeatedLazyMessage(0, com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance());
        TestCase.assertEquals(nestedMessageBuilder.build(), messageAfterBuild.getRepeatedLazyMessage(0));
        TestCase.assertEquals(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance(), builder.getRepeatedLazyMessage(0));
        builder.clearRepeatedLazyMessage();
        message = builder.build();
        builder.addRepeatedSfixed32(1);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedSfixed32Count());
        builder.setRepeatedSfixed32(0, 0);
        TestCase.assertEquals(1, messageAfterBuild.getRepeatedSfixed32(0));
        TestCase.assertEquals(0, builder.getRepeatedSfixed32(0));
        builder.clearRepeatedSfixed32();
        message = builder.build();
        builder.addRepeatedSfixed64(1L);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedSfixed64Count());
        builder.setRepeatedSfixed64(0, 0L);
        TestCase.assertEquals(1L, messageAfterBuild.getRepeatedSfixed64(0));
        TestCase.assertEquals(0L, builder.getRepeatedSfixed64(0));
        builder.clearRepeatedSfixed64();
        message = builder.build();
        builder.addRepeatedSint32(1);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedSint32Count());
        builder.setRepeatedSint32(0, 0);
        TestCase.assertEquals(1, messageAfterBuild.getRepeatedSint32(0));
        TestCase.assertEquals(0, builder.getRepeatedSint32(0));
        builder.clearRepeatedSint32();
        message = builder.build();
        builder.addRepeatedSint64(1L);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedSint64Count());
        builder.setRepeatedSint64(0, 0L);
        TestCase.assertEquals(1L, messageAfterBuild.getRepeatedSint64(0));
        TestCase.assertEquals(0L, builder.getRepeatedSint64(0));
        builder.clearRepeatedSint64();
        message = builder.build();
        builder.addRepeatedString("hi");
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedStringCount());
        builder.setRepeatedString(0, "");
        TestCase.assertEquals("hi", messageAfterBuild.getRepeatedString(0));
        TestCase.assertEquals("", builder.getRepeatedString(0));
        builder.clearRepeatedString();
        message = builder.build();
        builder.addRepeatedStringBytes(ByteString.copyFromUtf8("hi"));
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedStringCount());
        builder.setRepeatedString(0, "");
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), messageAfterBuild.getRepeatedStringBytes(0));
        TestCase.assertEquals(EMPTY, builder.getRepeatedStringBytes(0));
        builder.clearRepeatedString();
        message = builder.build();
        builder.addRepeatedStringPiece("hi");
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedStringPieceCount());
        builder.setRepeatedStringPiece(0, "");
        TestCase.assertEquals("hi", messageAfterBuild.getRepeatedStringPiece(0));
        TestCase.assertEquals("", builder.getRepeatedStringPiece(0));
        builder.clearRepeatedStringPiece();
        message = builder.build();
        builder.addRepeatedStringPieceBytes(ByteString.copyFromUtf8("hi"));
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedStringPieceCount());
        builder.setRepeatedStringPiece(0, "");
        TestCase.assertEquals(ByteString.copyFromUtf8("hi"), messageAfterBuild.getRepeatedStringPieceBytes(0));
        TestCase.assertEquals(EMPTY, builder.getRepeatedStringPieceBytes(0));
        builder.clearRepeatedStringPiece();
        message = builder.build();
        builder.addRepeatedUint32(1);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0, message.getRepeatedUint32Count());
        builder.setRepeatedUint32(0, 0);
        TestCase.assertEquals(1, messageAfterBuild.getRepeatedUint32(0));
        TestCase.assertEquals(0, builder.getRepeatedUint32(0));
        builder.clearRepeatedUint32();
        message = builder.build();
        builder.addRepeatedUint64(1L);
        messageAfterBuild = builder.build();
        TestCase.assertEquals(0L, message.getRepeatedUint64Count());
        builder.setRepeatedUint64(0, 0L);
        TestCase.assertEquals(1L, messageAfterBuild.getRepeatedUint64(0));
        TestCase.assertEquals(0L, builder.getRepeatedUint64(0));
        builder.clearRepeatedUint64();
        message = builder.build();
        TestCase.assertEquals(0, message.getSerializedSize());
        builder.mergeFrom(TestAllTypesLite.newBuilder().setOptionalBool(true).build());
        TestCase.assertEquals(0, message.getSerializedSize());
        TestCase.assertEquals(true, builder.build().getOptionalBool());
        builder.clearOptionalBool();
        message = builder.build();
        TestCase.assertEquals(0, message.getSerializedSize());
        builder.mergeFrom(TestAllTypesLite.newBuilder().setOptionalBool(true).build());
        TestCase.assertEquals(0, message.getSerializedSize());
        TestCase.assertEquals(true, builder.build().getOptionalBool());
        builder.clear();
        TestCase.assertEquals(0, builder.build().getSerializedSize());
        message = builder.build();
        TestCase.assertEquals(0, message.getSerializedSize());
        builder.mergeOptionalForeignMessage(foreignMessage);
        TestCase.assertEquals(0, message.getSerializedSize());
        TestCase.assertEquals(foreignMessage.getC(), builder.build().getOptionalForeignMessage().getC());
        builder.clearOptionalForeignMessage();
        message = builder.build();
        TestCase.assertEquals(0, message.getSerializedSize());
        builder.mergeOptionalLazyMessage(nestedMessage);
        TestCase.assertEquals(0, message.getSerializedSize());
        TestCase.assertEquals(nestedMessage.getBb(), builder.build().getOptionalLazyMessage().getBb());
        builder.clearOptionalLazyMessage();
        message = builder.build();
        builder.setOneofString("hi");
        TestCase.assertEquals(ONEOFFIELD_NOT_SET, message.getOneofFieldCase());
        TestCase.assertEquals(ONEOF_STRING, builder.getOneofFieldCase());
        TestCase.assertEquals("hi", builder.getOneofString());
        messageAfterBuild = builder.build();
        TestCase.assertEquals(ONEOF_STRING, messageAfterBuild.getOneofFieldCase());
        TestCase.assertEquals("hi", messageAfterBuild.getOneofString());
        builder.setOneofUint32(1);
        TestCase.assertEquals(ONEOF_STRING, messageAfterBuild.getOneofFieldCase());
        TestCase.assertEquals("hi", messageAfterBuild.getOneofString());
        TestCase.assertEquals(ONEOF_UINT32, builder.getOneofFieldCase());
        TestCase.assertEquals(1, builder.getOneofUint32());
        TestAllTypesLiteOrBuilder messageOrBuilder = builder;
        TestCase.assertEquals(ONEOF_UINT32, messageOrBuilder.getOneofFieldCase());
        TestAllExtensionsLite.Builder extendableMessageBuilder = TestAllExtensionsLite.newBuilder();
        TestAllExtensionsLite extendableMessage = extendableMessageBuilder.build();
        extendableMessageBuilder.setExtension(optionalInt32ExtensionLite, 1);
        TestCase.assertFalse(extendableMessage.hasExtension(optionalInt32ExtensionLite));
        extendableMessage = extendableMessageBuilder.build();
        TestCase.assertEquals(1, ((int) (extendableMessageBuilder.getExtension(optionalInt32ExtensionLite))));
        TestCase.assertEquals(1, ((int) (extendableMessage.getExtension(optionalInt32ExtensionLite))));
        extendableMessageBuilder.setExtension(optionalInt32ExtensionLite, 3);
        TestCase.assertEquals(3, ((int) (extendableMessageBuilder.getExtension(optionalInt32ExtensionLite))));
        TestCase.assertEquals(1, ((int) (extendableMessage.getExtension(optionalInt32ExtensionLite))));
        extendableMessage = extendableMessageBuilder.build();
        TestCase.assertEquals(3, ((int) (extendableMessageBuilder.getExtension(optionalInt32ExtensionLite))));
        TestCase.assertEquals(3, ((int) (extendableMessage.getExtension(optionalInt32ExtensionLite))));
        // No extension registry, so it should be in unknown fields.
        extendableMessage = TestAllExtensionsLite.parseFrom(extendableMessage.toByteArray());
        TestCase.assertFalse(extendableMessage.hasExtension(optionalInt32ExtensionLite));
        extendableMessageBuilder = extendableMessage.toBuilder();
        extendableMessageBuilder.mergeFrom(TestAllExtensionsLite.newBuilder().setExtension(optionalFixed32ExtensionLite, 11).build());
        extendableMessage = extendableMessageBuilder.build();
        ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
        UnittestLite.registerAllExtensions(registry);
        extendableMessage = TestAllExtensionsLite.parseFrom(extendableMessage.toByteArray(), registry);
        // The unknown field was preserved.
        TestCase.assertEquals(3, ((int) (extendableMessage.getExtension(optionalInt32ExtensionLite))));
        TestCase.assertEquals(11, ((int) (extendableMessage.getExtension(optionalFixed32ExtensionLite))));
    }

    public void testBuilderMergeFromNull() throws Exception {
        try {
            TestAllTypesLite.newBuilder().mergeFrom(((TestAllTypesLite) (null)));
            TestCase.fail("Expected exception");
        } catch (NullPointerException e) {
            // Pass.
        }
    }

    // Builder.mergeFrom() should keep existing extensions.
    public void testBuilderMergeFromWithExtensions() throws Exception {
        TestAllExtensionsLite message = TestAllExtensionsLite.newBuilder().addExtension(repeatedInt32ExtensionLite, 12).build();
        ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
        UnittestLite.registerAllExtensions(registry);
        TestAllExtensionsLite.Builder builder = TestAllExtensionsLite.newBuilder();
        builder.mergeFrom(message.toByteArray(), registry);
        builder.mergeFrom(message.toByteArray(), registry);
        TestAllExtensionsLite result = builder.build();
        TestCase.assertEquals(2, result.getExtensionCount(repeatedInt32ExtensionLite));
        TestCase.assertEquals(12, result.getExtension(repeatedInt32ExtensionLite, 0).intValue());
        TestCase.assertEquals(12, result.getExtension(repeatedInt32ExtensionLite, 1).intValue());
    }

    // Builder.mergeFrom() should keep existing unknown fields.
    public void testBuilderMergeFromWithUnknownFields() throws Exception {
        TestAllTypesLite message = TestAllTypesLite.newBuilder().addRepeatedInt32(1).build();
        com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.Builder builder = com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder();
        builder.mergeFrom(message.toByteArray());
        builder.mergeFrom(message.toByteArray());
        com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage result = builder.build();
        TestCase.assertEquals(((message.getSerializedSize()) * 2), result.getSerializedSize());
    }

    public void testToStringDefaultInstance() throws Exception {
        LiteTest.assertToStringEquals("", TestAllTypesLite.getDefaultInstance());
    }

    public void testToStringScalarFieldsSuffixedWithList() throws Exception {
        LiteTest.assertToStringEquals("deceptively_named_list: 7", TestAllTypesLite.newBuilder().setDeceptivelyNamedList(7).build());
    }

    public void testToStringPrimitives() throws Exception {
        TestAllTypesLite proto = TestAllTypesLite.newBuilder().setOptionalInt32(1).setOptionalInt64(9223372036854775807L).build();
        LiteTest.assertToStringEquals("optional_int32: 1\noptional_int64: 9223372036854775807", proto);
        proto = TestAllTypesLite.newBuilder().setOptionalBool(true).setOptionalNestedEnum(BAZ).build();
        LiteTest.assertToStringEquals("optional_bool: true\noptional_nested_enum: BAZ", proto);
        proto = TestAllTypesLite.newBuilder().setOptionalFloat(2.72F).setOptionalDouble(3.14).build();
        LiteTest.assertToStringEquals("optional_double: 3.14\noptional_float: 2.72", proto);
    }

    public void testToStringStringFields() throws Exception {
        TestAllTypesLite proto = TestAllTypesLite.newBuilder().setOptionalString("foo\"bar\nbaz\\").build();
        LiteTest.assertToStringEquals("optional_string: \"foo\\\"bar\\nbaz\\\\\"", proto);
        proto = TestAllTypesLite.newBuilder().setOptionalString("\u6587").build();
        LiteTest.assertToStringEquals("optional_string: \"\\346\\226\\207\"", proto);
    }

    public void testToStringNestedMessage() throws Exception {
        TestAllTypesLite proto = TestAllTypesLite.newBuilder().setOptionalNestedMessage(NestedMessage.getDefaultInstance()).build();
        LiteTest.assertToStringEquals("optional_nested_message {\n}", proto);
        proto = TestAllTypesLite.newBuilder().setOptionalNestedMessage(NestedMessage.newBuilder().setBb(7)).build();
        LiteTest.assertToStringEquals("optional_nested_message {\n  bb: 7\n}", proto);
    }

    public void testToStringRepeatedFields() throws Exception {
        TestAllTypesLite proto = TestAllTypesLite.newBuilder().addRepeatedInt32(32).addRepeatedInt32(32).addRepeatedInt64(64).build();
        LiteTest.assertToStringEquals("repeated_int32: 32\nrepeated_int32: 32\nrepeated_int64: 64", proto);
        proto = TestAllTypesLite.newBuilder().addRepeatedLazyMessage(NestedMessage.newBuilder().setBb(7)).addRepeatedLazyMessage(NestedMessage.newBuilder().setBb(8)).build();
        LiteTest.assertToStringEquals("repeated_lazy_message {\n  bb: 7\n}\nrepeated_lazy_message {\n  bb: 8\n}", proto);
    }

    public void testToStringForeignFields() throws Exception {
        TestAllTypesLite proto = TestAllTypesLite.newBuilder().setOptionalForeignEnum(FOREIGN_LITE_BAR).setOptionalForeignMessage(ForeignMessageLite.newBuilder().setC(3)).build();
        LiteTest.assertToStringEquals("optional_foreign_enum: FOREIGN_LITE_BAR\noptional_foreign_message {\n  c: 3\n}", proto);
    }

    public void testToStringExtensions() throws Exception {
        TestAllExtensionsLite message = TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123).addExtension(repeatedStringExtensionLite, "spam").addExtension(repeatedStringExtensionLite, "eggs").setExtension(optionalNestedEnumExtensionLite, BAZ).setExtension(optionalNestedMessageExtensionLite, NestedMessage.newBuilder().setBb(7).build()).build();
        LiteTest.assertToStringEquals("[1]: 123\n[18] {\n  bb: 7\n}\n[21]: 3\n[44]: \"spam\"\n[44]: \"eggs\"", message);
    }

    public void testToStringUnknownFields() throws Exception {
        TestAllExtensionsLite messageWithExtensions = TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123).addExtension(repeatedStringExtensionLite, "spam").addExtension(repeatedStringExtensionLite, "eggs").setExtension(optionalNestedEnumExtensionLite, BAZ).setExtension(optionalNestedMessageExtensionLite, NestedMessage.newBuilder().setBb(7).build()).build();
        TestAllExtensionsLite messageWithUnknownFields = TestAllExtensionsLite.parseFrom(messageWithExtensions.toByteArray());
        LiteTest.assertToStringEquals("1: 123\n18: \"\\b\\a\"\n21: 3\n44: \"spam\"\n44: \"eggs\"", messageWithUnknownFields);
    }

    public void testToStringLazyMessage() throws Exception {
        TestAllTypesLite message = TestAllTypesLite.newBuilder().setOptionalLazyMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setBb(1).build()).build();
        LiteTest.assertToStringEquals("optional_lazy_message {\n  bb: 1\n}", message);
    }

    public void testToStringGroup() throws Exception {
        TestAllTypesLite message = TestAllTypesLite.newBuilder().setOptionalGroup(OptionalGroup.newBuilder().setA(1).build()).build();
        LiteTest.assertToStringEquals("optional_group {\n  a: 1\n}", message);
    }

    public void testToStringOneof() throws Exception {
        TestAllTypesLite message = TestAllTypesLite.newBuilder().setOneofString("hello").build();
        LiteTest.assertToStringEquals("oneof_string: \"hello\"", message);
    }

    public void testToStringMapFields() throws Exception {
        TestMap message1 = TestMap.newBuilder().putInt32ToStringField(1, "alpha").putInt32ToStringField(2, "beta").build();
        LiteTest.assertToStringEquals(("int32_to_string_field {\n" + (((((("  key: 1\n" + "  value: \"alpha\"\n") + "}\n") + "int32_to_string_field {\n") + "  key: 2\n") + "  value: \"beta\"\n") + "}")), message1);
        TestMap message2 = TestMap.newBuilder().putInt32ToMessageField(1, MessageValue.newBuilder().setValue(10).build()).putInt32ToMessageField(2, MessageValue.newBuilder().setValue(20).build()).build();
        LiteTest.assertToStringEquals(("int32_to_message_field {\n" + (((((((((("  key: 1\n" + "  value {\n") + "    value: 10\n") + "  }\n") + "}\n") + "int32_to_message_field {\n") + "  key: 2\n") + "  value {\n") + "    value: 20\n") + "  }\n") + "}")), message2);
    }

    public void testParseLazy() throws Exception {
        ByteString bb = TestAllTypesLite.newBuilder().setOptionalLazyMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setBb(11).build()).build().toByteString();
        ByteString cc = TestAllTypesLite.newBuilder().setOptionalLazyMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setCc(22).build()).build().toByteString();
        ByteString concat = bb.concat(cc);
        TestAllTypesLite message = TestAllTypesLite.parseFrom(concat);
        TestCase.assertEquals(11, message.getOptionalLazyMessage().getBb());
        TestCase.assertEquals(22L, message.getOptionalLazyMessage().getCc());
    }

    public void testParseLazy_oneOf() throws Exception {
        ByteString bb = TestAllTypesLite.newBuilder().setOneofLazyNestedMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setBb(11).build()).build().toByteString();
        ByteString cc = TestAllTypesLite.newBuilder().setOneofLazyNestedMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.newBuilder().setCc(22).build()).build().toByteString();
        ByteString concat = bb.concat(cc);
        TestAllTypesLite message = TestAllTypesLite.parseFrom(concat);
        TestCase.assertEquals(11, message.getOneofLazyNestedMessage().getBb());
        TestCase.assertEquals(22L, message.getOneofLazyNestedMessage().getCc());
    }

    public void testMergeFromStream_repeatedField() throws Exception {
        TestAllTypesLite.Builder builder = TestAllTypesLite.newBuilder().addRepeatedString("hello");
        builder.mergeFrom(CodedInputStream.newInstance(builder.build().toByteArray()));
        TestCase.assertEquals(2, builder.getRepeatedStringCount());
    }

    public void testMergeFromStream_invalidBytes() throws Exception {
        TestAllTypesLite.Builder builder = TestAllTypesLite.newBuilder().setDefaultBool(true);
        try {
            builder.mergeFrom(CodedInputStream.newInstance("Invalid bytes".getBytes(UTF_8)));
            TestCase.fail();
        } catch (InvalidProtocolBufferException expected) {
        }
    }

    public void testMergeFrom_sanity() throws Exception {
        TestAllTypesLite one = TestUtilLite.getAllLiteSetBuilder().build();
        byte[] bytes = one.toByteArray();
        TestAllTypesLite two = TestAllTypesLite.parseFrom(bytes);
        one = one.toBuilder().mergeFrom(one).build();
        two = two.toBuilder().mergeFrom(bytes).build();
        TestCase.assertEquals(one, two);
        TestCase.assertEquals(two, one);
        TestCase.assertEquals(one.hashCode(), two.hashCode());
    }

    public void testMergeFromNoLazyFieldSharing() throws Exception {
        TestAllTypesLite.Builder sourceBuilder = TestAllTypesLite.newBuilder().setOptionalLazyMessage(NestedMessage.newBuilder().setBb(1));
        TestAllTypesLite.Builder targetBuilder = TestAllTypesLite.newBuilder().mergeFrom(sourceBuilder.build());
        TestCase.assertEquals(1, sourceBuilder.getOptionalLazyMessage().getBb());
        // now change the sourceBuilder, and target value shouldn't be affected.
        sourceBuilder.setOptionalLazyMessage(NestedMessage.newBuilder().setBb(2));
        TestCase.assertEquals(1, targetBuilder.getOptionalLazyMessage().getBb());
    }

    public void testEquals_notEqual() throws Exception {
        TestAllTypesLite one = TestUtilLite.getAllLiteSetBuilder().build();
        byte[] bytes = one.toByteArray();
        TestAllTypesLite two = one.toBuilder().mergeFrom(one).mergeFrom(bytes).build();
        TestCase.assertFalse(one.equals(two));
        TestCase.assertFalse(two.equals(one));
        TestCase.assertFalse(one.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(one));
        TestAllTypesLite oneFieldSet = TestAllTypesLite.newBuilder().setDefaultBool(true).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultCord("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultCordBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultDouble(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultFixed32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultFixed64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultFloat(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultForeignEnum(FOREIGN_LITE_BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultImportEnum(IMPORT_LITE_BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultInt32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultInt64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultNestedEnum(BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultSfixed32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultSfixed64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultSint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultSint64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultString("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultStringBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultStringPiece("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultStringPieceBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultUint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setDefaultUint64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedBool(true).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedCord("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedCordBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedDouble(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedFixed32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedFixed64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedFloat(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedForeignEnum(FOREIGN_LITE_BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedImportEnum(IMPORT_LITE_BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedInt32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedInt64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedNestedEnum(BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedSfixed32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedSfixed64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedSint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedSint64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedString("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedStringBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedStringPiece("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedStringPieceBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedUint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedUint64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalBool(true).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalCord("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalCordBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalDouble(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalFixed32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalFixed64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalFloat(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalForeignEnum(FOREIGN_LITE_BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalImportEnum(IMPORT_LITE_BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalInt32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalInt64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalNestedEnum(BAR).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalSfixed32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalSfixed64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalSint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalSint64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalString("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalStringBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalStringPiece("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalStringPieceBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalUint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalUint64(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOneofBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOneofLazyNestedMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOneofNestedMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOneofString("").build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOneofStringBytes(EMPTY).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOneofUint32(0).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalForeignMessage(ForeignMessageLite.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalGroup(OptionalGroup.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalPublicImportMessage(PublicImportMessageLite.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().setOptionalLazyMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
        oneFieldSet = TestAllTypesLite.newBuilder().addRepeatedLazyMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance()).build();
        TestCase.assertFalse(oneFieldSet.equals(TestAllTypesLite.getDefaultInstance()));
        TestCase.assertFalse(TestAllTypesLite.getDefaultInstance().equals(oneFieldSet));
    }

    public void testEquals() throws Exception {
        // Check that two identical objs are equal.
        Foo foo1a = Foo.newBuilder().setValue(1).addBar(Bar.newBuilder().setName("foo1")).build();
        Foo foo1b = Foo.newBuilder().setValue(1).addBar(Bar.newBuilder().setName("foo1")).build();
        Foo foo2 = Foo.newBuilder().setValue(1).addBar(Bar.newBuilder().setName("foo2")).build();
        // Check that equals is doing value rather than object equality.
        TestCase.assertEquals(foo1a, foo1b);
        TestCase.assertEquals(foo1a.hashCode(), foo1b.hashCode());
        // Check that a diffeent object is not equal.
        TestCase.assertFalse(foo1a.equals(foo2));
        // Check that two objects which have different types but the same field values are not
        // considered to be equal.
        Bar bar = Bar.newBuilder().setName("bar").build();
        BarPrime barPrime = BarPrime.newBuilder().setName("bar").build();
        TestCase.assertFalse(bar.equals(barPrime));
    }

    public void testEqualsAndHashCodeForTrickySchemaTypes() {
        Foo foo1 = Foo.getDefaultInstance();
        Foo foo2 = Foo.newBuilder().setSint64(1).build();
        Foo foo3 = Foo.newBuilder().putMyMap("key", "value2").build();
        Foo foo4 = Foo.newBuilder().setMyGroup(MyGroup.newBuilder().setValue(4).build()).build();
        assertEqualsAndHashCodeAreFalse(foo1, foo2);
        assertEqualsAndHashCodeAreFalse(foo1, foo3);
        assertEqualsAndHashCodeAreFalse(foo1, foo4);
    }

    public void testOneofEquals() throws Exception {
        TestOneofEquals.Builder builder = TestOneofEquals.newBuilder();
        TestOneofEquals message1 = builder.build();
        // Set message2's name field to default value. The two messages should be different when we
        // check with the oneof case.
        builder.setName("");
        TestOneofEquals message2 = builder.build();
        TestCase.assertFalse(message1.equals(message2));
    }

    public void testEquals_sanity() throws Exception {
        TestAllTypesLite one = TestUtilLite.getAllLiteSetBuilder().build();
        TestAllTypesLite two = TestAllTypesLite.parseFrom(one.toByteArray());
        TestCase.assertEquals(one, two);
        TestCase.assertEquals(one.hashCode(), two.hashCode());
        TestCase.assertEquals(one.toBuilder().mergeFrom(two).build(), two.toBuilder().mergeFrom(two.toByteArray()).build());
    }

    public void testEqualsAndHashCodeWithUnknownFields() throws InvalidProtocolBufferException {
        Foo fooWithOnlyValue = Foo.newBuilder().setValue(1).build();
        Foo fooWithValueAndExtension = fooWithOnlyValue.toBuilder().setValue(1).setExtension(fooExt, Bar.newBuilder().setName("name").build()).build();
        Foo fooWithValueAndUnknownFields = Foo.parseFrom(fooWithValueAndExtension.toByteArray());
        assertEqualsAndHashCodeAreFalse(fooWithOnlyValue, fooWithValueAndUnknownFields);
        assertEqualsAndHashCodeAreFalse(fooWithValueAndExtension, fooWithValueAndUnknownFields);
    }

    public void testEqualsAndHashCodeWithExtensions() throws InvalidProtocolBufferException {
        Foo fooWithOnlyValue = Foo.newBuilder().setValue(1).build();
        Foo fooWithValueAndExtension = fooWithOnlyValue.toBuilder().setValue(1).setExtension(fooExt, Bar.newBuilder().setName("name").build()).build();
        assertEqualsAndHashCodeAreFalse(fooWithOnlyValue, fooWithValueAndExtension);
    }

    // Test to ensure we avoid a class cast exception with oneofs.
    public void testEquals_oneOfMessages() {
        TestAllTypesLite mine = TestAllTypesLite.newBuilder().setOneofString("Hello").build();
        TestAllTypesLite other = TestAllTypesLite.newBuilder().setOneofNestedMessage(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance()).build();
        TestCase.assertFalse(mine.equals(other));
        TestCase.assertFalse(other.equals(mine));
    }

    public void testHugeFieldNumbers() throws InvalidProtocolBufferException {
        TestHugeFieldNumbersLite message = TestHugeFieldNumbersLite.newBuilder().setOptionalInt32(1).addRepeatedInt32(2).setOptionalEnum(FOREIGN_LITE_FOO).setOptionalString("xyz").setOptionalMessage(ForeignMessageLite.newBuilder().setC(3).build()).build();
        TestHugeFieldNumbersLite parsedMessage = TestHugeFieldNumbersLite.parseFrom(message.toByteArray());
        TestCase.assertEquals(1, parsedMessage.getOptionalInt32());
        TestCase.assertEquals(2, parsedMessage.getRepeatedInt32(0));
        TestCase.assertEquals(FOREIGN_LITE_FOO, parsedMessage.getOptionalEnum());
        TestCase.assertEquals("xyz", parsedMessage.getOptionalString());
        TestCase.assertEquals(3, parsedMessage.getOptionalMessage().getC());
    }

    public void testRecursiveHashcode() {
        // This tests that we don't infinite loop.
        TestRecursiveOneof.getDefaultInstance().hashCode();
    }

    public void testParseFromByteBuffer() throws Exception {
        TestAllTypesLite message = TestAllTypesLite.newBuilder().setOptionalInt32(123).addRepeatedString("hello").setOptionalNestedMessage(NestedMessage.newBuilder().setBb(7)).build();
        TestAllTypesLite copy = TestAllTypesLite.parseFrom(message.toByteString().asReadOnlyByteBuffer());
        TestCase.assertEquals(message, copy);
    }

    public void testParseFromByteBufferThrows() {
        try {
            TestAllTypesLite.parseFrom(ByteBuffer.wrap(new byte[]{ 5 }));
            TestCase.fail();
        } catch (InvalidProtocolBufferException expected) {
        }
        TestAllTypesLite message = TestAllTypesLite.newBuilder().setOptionalInt32(123).addRepeatedString("hello").build();
        ByteBuffer buffer = ByteBuffer.wrap(message.toByteArray(), 0, ((message.getSerializedSize()) - 1));
        try {
            TestAllTypesLite.parseFrom(buffer);
            TestCase.fail();
        } catch (InvalidProtocolBufferException expected) {
            TestCase.assertEquals(TestAllTypesLite.newBuilder().setOptionalInt32(123).build(), expected.getUnfinishedMessage());
        }
    }

    public void testParseFromByteBuffer_extensions() throws Exception {
        TestAllExtensionsLite message = TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123).addExtension(repeatedStringExtensionLite, "hello").setExtension(optionalNestedEnumExtensionLite, BAZ).setExtension(optionalNestedMessageExtensionLite, NestedMessage.newBuilder().setBb(7).build()).build();
        ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
        UnittestLite.registerAllExtensions(registry);
        TestAllExtensionsLite copy = TestAllExtensionsLite.parseFrom(message.toByteString().asReadOnlyByteBuffer(), registry);
        TestCase.assertEquals(message, copy);
    }

    public void testParseFromByteBufferThrows_extensions() {
        ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
        UnittestLite.registerAllExtensions(registry);
        try {
            TestAllExtensionsLite.parseFrom(ByteBuffer.wrap(new byte[]{ 5 }), registry);
            TestCase.fail();
        } catch (InvalidProtocolBufferException expected) {
        }
        TestAllExtensionsLite message = TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123).addExtension(repeatedStringExtensionLite, "hello").build();
        ByteBuffer buffer = ByteBuffer.wrap(message.toByteArray(), 0, ((message.getSerializedSize()) - 1));
        try {
            TestAllExtensionsLite.parseFrom(buffer, registry);
            TestCase.fail();
        } catch (InvalidProtocolBufferException expected) {
            TestCase.assertEquals(TestAllExtensionsLite.newBuilder().setExtension(optionalInt32ExtensionLite, 123).build(), expected.getUnfinishedMessage());
        }
    }

    // Make sure we haven't screwed up the code generation for packing fields by default.
    public void testPackedSerialization() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        builder.addRepeatedInt32(4321);
        builder.addRepeatedNestedEnum(TestAllTypes.NestedEnum.BAZ);
        TestAllTypes message = builder.build();
        CodedInputStream in = CodedInputStream.newInstance(message.toByteArray());
        while (!(in.isAtEnd())) {
            int tag = in.readTag();
            TestCase.assertEquals(WIRETYPE_LENGTH_DELIMITED, WireFormat.getTagWireType(tag));
            in.skipField(tag);
        } 
    }

    public void testAddAllIteratesOnce() {
        TestAllTypesLite.newBuilder().addAllRepeatedBool(new LiteTest.OneTimeIterableList(false)).addAllRepeatedInt32(new LiteTest.OneTimeIterableList(0)).addAllRepeatedInt64(new LiteTest.OneTimeIterableList(0L)).addAllRepeatedFloat(new LiteTest.OneTimeIterableList(0.0F)).addAllRepeatedDouble(new LiteTest.OneTimeIterableList(0.0)).addAllRepeatedBytes(new LiteTest.OneTimeIterableList(EMPTY)).addAllRepeatedString(new LiteTest.OneTimeIterableList("")).addAllRepeatedNestedMessage(new LiteTest.OneTimeIterableList(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance())).addAllRepeatedBool(new LiteTest.OneTimeIterable(false)).addAllRepeatedInt32(new LiteTest.OneTimeIterable(0)).addAllRepeatedInt64(new LiteTest.OneTimeIterable(0L)).addAllRepeatedFloat(new LiteTest.OneTimeIterable(0.0F)).addAllRepeatedDouble(new LiteTest.OneTimeIterable(0.0)).addAllRepeatedBytes(new LiteTest.OneTimeIterable(EMPTY)).addAllRepeatedString(new LiteTest.OneTimeIterable("")).addAllRepeatedNestedMessage(new LiteTest.OneTimeIterable(com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage.getDefaultInstance())).build();
    }

    public void testAddAllIteratesOnce_throwsOnNull() {
        TestAllTypesLite.Builder builder = TestAllTypesLite.newBuilder();
        try {
            builder.addAllRepeatedBool(new LiteTest.OneTimeIterableList(true, false, ((Boolean) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 2 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedBoolCount());
        }
        try {
            builder.addAllRepeatedBool(new LiteTest.OneTimeIterable(true, false, ((Boolean) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 2 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedBoolCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedBool(new LiteTest.OneTimeIterableList(((Boolean) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedBoolCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedInt32(new LiteTest.OneTimeIterableList(((Integer) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedInt32Count());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedInt64(new LiteTest.OneTimeIterableList(((Long) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedInt64Count());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedFloat(new LiteTest.OneTimeIterableList(((Float) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedFloatCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedDouble(new LiteTest.OneTimeIterableList(((Double) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedDoubleCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedBytes(new LiteTest.OneTimeIterableList(((ByteString) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedBytesCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedString(new LiteTest.OneTimeIterableList("", "", ((String) (null)), ""));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 2 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedStringCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedString(new LiteTest.OneTimeIterable("", "", ((String) (null)), ""));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 2 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedStringCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedString(new LiteTest.OneTimeIterableList(((String) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedStringCount());
        }
        try {
            builder = TestAllTypesLite.newBuilder();
            builder.addAllRepeatedNestedMessage(new LiteTest.OneTimeIterableList(((com.google.protobuf.UnittestLite.TestAllTypesLite.NestedMessage) (null))));
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("Element at index 0 is null.", expected.getMessage());
            TestCase.assertEquals(0, builder.getRepeatedNestedMessageCount());
        }
    }

    private static final class OneTimeIterableList<T> extends ArrayList<T> {
        private boolean wasIterated = false;

        OneTimeIterableList(T... contents) {
            addAll(Arrays.asList(contents));
        }

        @Override
        public Iterator<T> iterator() {
            if (wasIterated) {
                TestCase.fail();
            }
            wasIterated = true;
            return super.iterator();
        }
    }

    private static final class OneTimeIterable<T> implements Iterable<T> {
        private final List<T> list;

        private boolean wasIterated = false;

        OneTimeIterable(T... contents) {
            list = Arrays.asList(contents);
        }

        @Override
        public Iterator<T> iterator() {
            if (wasIterated) {
                TestCase.fail();
            }
            wasIterated = true;
            return list.iterator();
        }
    }

    public void testNullExtensionRegistry() throws Exception {
        try {
            TestAllTypesLite.parseFrom(new byte[]{  }, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

