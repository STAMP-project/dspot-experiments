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
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto;
import protobuf_unittest.UnittestProto.TestAllExtensions;
import protobuf_unittest.UnittestProto.TestAllTypes;


/**
 * Unit test for {@link LazyFieldLite}.
 *
 * @author xiangl@google.com (Xiang Li)
 */
public class LazyFieldLiteTest extends TestCase {
    public void testGetValue() {
        MessageLite message = TestUtil.getAllSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        TestCase.assertEquals(message, lazyField.getValue(TestAllTypes.getDefaultInstance()));
        changeValue(lazyField);
        assertNotEqual(message, lazyField.getValue(TestAllTypes.getDefaultInstance()));
    }

    public void testGetValueEx() throws Exception {
        TestAllExtensions message = TestUtil.getAllExtensionsSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        TestCase.assertEquals(message, lazyField.getValue(TestAllExtensions.getDefaultInstance()));
        changeValue(lazyField);
        assertNotEqual(message, lazyField.getValue(TestAllExtensions.getDefaultInstance()));
    }

    public void testSetValue() {
        MessageLite message = TestUtil.getAllSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        changeValue(lazyField);
        assertNotEqual(message, lazyField.getValue(TestAllTypes.getDefaultInstance()));
        message = lazyField.getValue(TestAllTypes.getDefaultInstance());
        changeValue(lazyField);
        TestCase.assertEquals(message, lazyField.getValue(TestAllTypes.getDefaultInstance()));
    }

    public void testSetValueEx() throws Exception {
        TestAllExtensions message = TestUtil.getAllExtensionsSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        changeValue(lazyField);
        assertNotEqual(message, lazyField.getValue(TestAllExtensions.getDefaultInstance()));
        MessageLite value = lazyField.getValue(TestAllExtensions.getDefaultInstance());
        changeValue(lazyField);
        TestCase.assertEquals(value, lazyField.getValue(TestAllExtensions.getDefaultInstance()));
    }

    public void testGetSerializedSize() {
        MessageLite message = TestUtil.getAllSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        TestCase.assertEquals(message.getSerializedSize(), lazyField.getSerializedSize());
        changeValue(lazyField);
        assertNotEqual(message.getSerializedSize(), lazyField.getSerializedSize());
    }

    public void testGetSerializedSizeEx() throws Exception {
        TestAllExtensions message = TestUtil.getAllExtensionsSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        TestCase.assertEquals(message.getSerializedSize(), lazyField.getSerializedSize());
        changeValue(lazyField);
        assertNotEqual(message.getSerializedSize(), lazyField.getSerializedSize());
    }

    public void testGetByteString() {
        MessageLite message = TestUtil.getAllSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        TestCase.assertEquals(message.toByteString(), lazyField.toByteString());
        changeValue(lazyField);
        assertNotEqual(message.toByteString(), lazyField.toByteString());
    }

    public void testGetByteStringEx() throws Exception {
        TestAllExtensions message = TestUtil.getAllExtensionsSet();
        LazyFieldLite lazyField = createLazyFieldLiteFromMessage(message);
        TestCase.assertEquals(message.toByteString(), lazyField.toByteString());
        changeValue(lazyField);
        assertNotEqual(message.toByteString(), lazyField.toByteString());
    }

    public void testMergeExtensions() throws Exception {
        TestAllExtensions message = TestUtil.getAllExtensionsSet();
        LazyFieldLite original = createLazyFieldLiteFromMessage(message);
        LazyFieldLite merged = new LazyFieldLite();
        merged.merge(original);
        TestAllExtensions value = ((TestAllExtensions) (merged.getValue(TestAllExtensions.getDefaultInstance())));
        TestCase.assertEquals(message, value);
    }

    public void testEmptyLazyField() throws Exception {
        LazyFieldLite field = new LazyFieldLite();
        TestCase.assertEquals(0, field.getSerializedSize());
        TestCase.assertEquals(EMPTY, field.toByteString());
    }

    public void testInvalidProto() throws Exception {
        // Silently fails and uses the default instance.
        LazyFieldLite field = new LazyFieldLite(TestUtil.getExtensionRegistry(), ByteString.copyFromUtf8("invalid"));
        TestCase.assertEquals(TestAllTypes.getDefaultInstance(), field.getValue(TestAllTypes.getDefaultInstance()));
        TestCase.assertEquals(0, field.getSerializedSize());
        TestCase.assertEquals(EMPTY, field.toByteString());
    }

    public void testMergeBeforeParsing() throws Exception {
        TestAllTypes message1 = TestAllTypes.newBuilder().setOptionalInt32(1).build();
        LazyFieldLite field1 = createLazyFieldLiteFromMessage(message1);
        TestAllTypes message2 = TestAllTypes.newBuilder().setOptionalInt64(2).build();
        LazyFieldLite field2 = createLazyFieldLiteFromMessage(message2);
        field1.merge(field2);
        TestAllTypes expected = TestAllTypes.newBuilder().setOptionalInt32(1).setOptionalInt64(2).build();
        TestCase.assertEquals(expected, field1.getValue(TestAllTypes.getDefaultInstance()));
    }

    public void testMergeOneNotParsed() throws Exception {
        // Test a few different paths that involve one message that was not parsed.
        TestAllTypes message1 = TestAllTypes.newBuilder().setOptionalInt32(1).build();
        TestAllTypes message2 = TestAllTypes.newBuilder().setOptionalInt64(2).build();
        TestAllTypes expected = TestAllTypes.newBuilder().setOptionalInt32(1).setOptionalInt64(2).build();
        LazyFieldLite field1 = LazyFieldLite.fromValue(message1);
        field1.getValue(TestAllTypes.getDefaultInstance());// Force parsing.

        LazyFieldLite field2 = createLazyFieldLiteFromMessage(message2);
        field1.merge(field2);
        TestCase.assertEquals(expected, field1.getValue(TestAllTypes.getDefaultInstance()));
        // Now reverse which one is parsed first.
        field1 = LazyFieldLite.fromValue(message1);
        field2 = createLazyFieldLiteFromMessage(message2);
        field2.getValue(TestAllTypes.getDefaultInstance());// Force parsing.

        field1.merge(field2);
        TestCase.assertEquals(expected, field1.getValue(TestAllTypes.getDefaultInstance()));
    }

    public void testMergeInvalid() throws Exception {
        // Test a few different paths that involve one message that was not parsed.
        TestAllTypes message = TestAllTypes.newBuilder().setOptionalInt32(1).build();
        LazyFieldLite valid = LazyFieldLite.fromValue(message);
        LazyFieldLite invalid = new LazyFieldLite(TestUtil.getExtensionRegistry(), ByteString.copyFromUtf8("invalid"));
        invalid.merge(valid);
        // We swallow the exception and just use the set field.
        TestCase.assertEquals(message, invalid.getValue(TestAllTypes.getDefaultInstance()));
    }

    public void testMergeKeepsExtensionsWhenPossible() throws Exception {
        // In this test we attempt to only use the empty registry, which will strip out all extensions
        // when serializing and then parsing. We verify that each code path will attempt to not
        // serialize and parse a message that was set directly without going through the
        // extensionRegistry.
        TestAllExtensions messageWithExtensions = TestAllExtensions.newBuilder().setExtension(UnittestProto.optionalInt32Extension, 42).build();
        TestAllExtensions emptyMessage = TestAllExtensions.newBuilder().build();
        ExtensionRegistryLite emptyRegistry = ExtensionRegistryLite.getEmptyRegistry();
        LazyFieldLite field = LazyFieldLite.fromValue(messageWithExtensions);
        field.merge(createLazyFieldLiteFromMessage(emptyRegistry, emptyMessage));
        TestCase.assertEquals(messageWithExtensions, field.getValue(TestAllExtensions.getDefaultInstance()));
        // Now reverse the order of the merging.
        field = createLazyFieldLiteFromMessage(emptyRegistry, emptyMessage);
        field.merge(LazyFieldLite.fromValue(messageWithExtensions));
        TestCase.assertEquals(messageWithExtensions, field.getValue(TestAllExtensions.getDefaultInstance()));
        // Now try parsing the empty field first.
        field = LazyFieldLite.fromValue(messageWithExtensions);
        LazyFieldLite other = createLazyFieldLiteFromMessage(emptyRegistry, emptyMessage);
        other.getValue(TestAllExtensions.getDefaultInstance());// Force parsing.

        field.merge(other);
        TestCase.assertEquals(messageWithExtensions, field.getValue(TestAllExtensions.getDefaultInstance()));
        // And again reverse.
        field = createLazyFieldLiteFromMessage(emptyRegistry, emptyMessage);
        field.getValue(TestAllExtensions.getDefaultInstance());// Force parsing.

        other = LazyFieldLite.fromValue(messageWithExtensions);
        field.merge(other);
        TestCase.assertEquals(messageWithExtensions, field.getValue(TestAllExtensions.getDefaultInstance()));
    }
}

