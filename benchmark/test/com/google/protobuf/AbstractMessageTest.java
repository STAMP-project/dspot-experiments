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


import Descriptors.Descriptor;
import UnittestProto.TestEmptyMessage;
import UnittestProto.repeatedInt32Extension;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.util.Map;
import junit.framework.TestCase;
import protobuf_unittest.UnittestOptimizeFor.TestOptimizedForSize;
import protobuf_unittest.UnittestProto;
import protobuf_unittest.UnittestProto.ForeignMessage;
import protobuf_unittest.UnittestProto.TestAllExtensions;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestPackedTypes;
import protobuf_unittest.UnittestProto.TestRequired;
import protobuf_unittest.UnittestProto.TestRequiredForeign;
import protobuf_unittest.UnittestProto.TestUnpackedTypes;


/**
 * Unit test for {@link AbstractMessage}.
 *
 * @author kenton@google.com Kenton Varda
 */
public class AbstractMessageTest extends TestCase {
    /**
     * Extends AbstractMessage and wraps some other message object.  The methods
     * of the Message interface which aren't explicitly implemented by
     * AbstractMessage are forwarded to the wrapped object.  This allows us to
     * test that AbstractMessage's implementations work even if the wrapped
     * object does not use them.
     */
    private static class AbstractMessageWrapper extends AbstractMessage {
        private final Message wrappedMessage;

        public AbstractMessageWrapper(Message wrappedMessage) {
            this.wrappedMessage = wrappedMessage;
        }

        @Override
        public Descriptor getDescriptorForType() {
            return wrappedMessage.getDescriptorForType();
        }

        @Override
        public AbstractMessageTest.AbstractMessageWrapper getDefaultInstanceForType() {
            return new AbstractMessageTest.AbstractMessageWrapper(wrappedMessage.getDefaultInstanceForType());
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            return wrappedMessage.getAllFields();
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            return wrappedMessage.hasField(field);
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            return wrappedMessage.getField(field);
        }

        @Override
        public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
            return wrappedMessage.getRepeatedFieldCount(field);
        }

        @Override
        public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
            return wrappedMessage.getRepeatedField(field, index);
        }

        @Override
        public UnknownFieldSet getUnknownFields() {
            return wrappedMessage.getUnknownFields();
        }

        @Override
        public AbstractMessageTest.AbstractMessageWrapper.Builder newBuilderForType() {
            return new AbstractMessageTest.AbstractMessageWrapper.Builder(wrappedMessage.newBuilderForType());
        }

        @Override
        public AbstractMessageTest.AbstractMessageWrapper.Builder toBuilder() {
            return new AbstractMessageTest.AbstractMessageWrapper.Builder(wrappedMessage.toBuilder());
        }

        static class Builder extends AbstractMessage.Builder<AbstractMessageTest.AbstractMessageWrapper.Builder> {
            private final Message.Builder wrappedBuilder;

            public Builder(Message.Builder wrappedBuilder) {
                this.wrappedBuilder = wrappedBuilder;
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper build() {
                return new AbstractMessageTest.AbstractMessageWrapper(wrappedBuilder.build());
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper buildPartial() {
                return new AbstractMessageTest.AbstractMessageWrapper(wrappedBuilder.buildPartial());
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder clone() {
                return new AbstractMessageTest.AbstractMessageWrapper.Builder(wrappedBuilder.clone());
            }

            @Override
            public boolean isInitialized() {
                return clone().buildPartial().isInitialized();
            }

            @Override
            public Descriptor getDescriptorForType() {
                return wrappedBuilder.getDescriptorForType();
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper getDefaultInstanceForType() {
                return new AbstractMessageTest.AbstractMessageWrapper(wrappedBuilder.getDefaultInstanceForType());
            }

            @Override
            public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
                return wrappedBuilder.getAllFields();
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
                return new AbstractMessageTest.AbstractMessageWrapper.Builder(wrappedBuilder.newBuilderForField(field));
            }

            @Override
            public boolean hasField(Descriptors.FieldDescriptor field) {
                return wrappedBuilder.hasField(field);
            }

            @Override
            public Object getField(Descriptors.FieldDescriptor field) {
                return wrappedBuilder.getField(field);
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder setField(Descriptors.FieldDescriptor field, Object value) {
                wrappedBuilder.setField(field, value);
                return this;
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder clearField(Descriptors.FieldDescriptor field) {
                wrappedBuilder.clearField(field);
                return this;
            }

            @Override
            public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
                return wrappedBuilder.getRepeatedFieldCount(field);
            }

            @Override
            public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
                return wrappedBuilder.getRepeatedField(field, index);
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                wrappedBuilder.setRepeatedField(field, index, value);
                return this;
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                wrappedBuilder.addRepeatedField(field, value);
                return this;
            }

            @Override
            public UnknownFieldSet getUnknownFields() {
                return wrappedBuilder.getUnknownFields();
            }

            @Override
            public AbstractMessageTest.AbstractMessageWrapper.Builder setUnknownFields(UnknownFieldSet unknownFields) {
                wrappedBuilder.setUnknownFields(unknownFields);
                return this;
            }

            @Override
            public Message.Builder getFieldBuilder(FieldDescriptor field) {
                return wrappedBuilder.getFieldBuilder(field);
            }
        }

        @Override
        public Parser<? extends Message> getParserForType() {
            return wrappedMessage.getParserForType();
        }
    }

    // =================================================================
    TestUtil.ReflectionTester reflectionTester = new TestUtil.ReflectionTester(TestAllTypes.getDescriptor(), null);

    TestUtil.ReflectionTester extensionsReflectionTester = new TestUtil.ReflectionTester(TestAllExtensions.getDescriptor(), TestUtil.getExtensionRegistry());

    public void testClear() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper message = clear().build();
        TestUtil.assertClear(((TestAllTypes) (message.wrappedMessage)));
    }

    public void testCopy() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper message = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestAllTypes.newBuilder()).mergeFrom(TestUtil.getAllSet()).build();
        TestUtil.assertAllFieldsSet(((TestAllTypes) (message.wrappedMessage)));
    }

    public void testSerializedSize() throws Exception {
        TestAllTypes message = TestUtil.getAllSet();
        Message abstractMessage = new AbstractMessageTest.AbstractMessageWrapper(TestUtil.getAllSet());
        TestCase.assertEquals(message.getSerializedSize(), abstractMessage.getSerializedSize());
    }

    public void testSerialization() throws Exception {
        Message abstractMessage = new AbstractMessageTest.AbstractMessageWrapper(TestUtil.getAllSet());
        TestUtil.assertAllFieldsSet(TestAllTypes.parseFrom(abstractMessage.toByteString()));
        TestCase.assertEquals(TestUtil.getAllSet().toByteString(), abstractMessage.toByteString());
    }

    public void testParsing() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper.Builder builder = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestAllTypes.newBuilder());
        AbstractMessageTest.AbstractMessageWrapper message = builder.mergeFrom(TestUtil.getAllSet().toByteString()).build();
        TestUtil.assertAllFieldsSet(((TestAllTypes) (message.wrappedMessage)));
    }

    public void testParsingUninitialized() throws Exception {
        TestRequiredForeign.Builder builder = TestRequiredForeign.newBuilder();
        builder.getOptionalMessageBuilder().setDummy2(10);
        ByteString bytes = builder.buildPartial().toByteString();
        Message.Builder abstractMessageBuilder = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestRequiredForeign.newBuilder());
        // mergeFrom() should not throw initialization error.
        abstractMessageBuilder.mergeFrom(bytes).buildPartial();
        try {
            abstractMessageBuilder.mergeFrom(bytes).build();
            TestCase.fail();
        } catch (UninitializedMessageException ex) {
            // pass
        }
        // test DynamicMessage directly.
        Message.Builder dynamicMessageBuilder = DynamicMessage.newBuilder(TestRequiredForeign.getDescriptor());
        // mergeFrom() should not throw initialization error.
        dynamicMessageBuilder.mergeFrom(bytes).buildPartial();
        try {
            dynamicMessageBuilder.mergeFrom(bytes).build();
            TestCase.fail();
        } catch (UninitializedMessageException ex) {
            // pass
        }
    }

    public void testPackedSerialization() throws Exception {
        Message abstractMessage = new AbstractMessageTest.AbstractMessageWrapper(TestUtil.getPackedSet());
        TestUtil.assertPackedFieldsSet(TestPackedTypes.parseFrom(abstractMessage.toByteString()));
        TestCase.assertEquals(TestUtil.getPackedSet().toByteString(), abstractMessage.toByteString());
    }

    public void testPackedParsing() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper.Builder builder = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestPackedTypes.newBuilder());
        AbstractMessageTest.AbstractMessageWrapper message = builder.mergeFrom(TestUtil.getPackedSet().toByteString()).build();
        TestUtil.assertPackedFieldsSet(((TestPackedTypes) (message.wrappedMessage)));
    }

    public void testUnpackedSerialization() throws Exception {
        Message abstractMessage = new AbstractMessageTest.AbstractMessageWrapper(TestUtil.getUnpackedSet());
        TestUtil.assertUnpackedFieldsSet(TestUnpackedTypes.parseFrom(abstractMessage.toByteString()));
        TestCase.assertEquals(TestUtil.getUnpackedSet().toByteString(), abstractMessage.toByteString());
    }

    public void testParsePackedToUnpacked() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper.Builder builder = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestUnpackedTypes.newBuilder());
        AbstractMessageTest.AbstractMessageWrapper message = builder.mergeFrom(TestUtil.getPackedSet().toByteString()).build();
        TestUtil.assertUnpackedFieldsSet(((TestUnpackedTypes) (message.wrappedMessage)));
    }

    public void testParseUnpackedToPacked() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper.Builder builder = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestPackedTypes.newBuilder());
        AbstractMessageTest.AbstractMessageWrapper message = builder.mergeFrom(TestUtil.getUnpackedSet().toByteString()).build();
        TestUtil.assertPackedFieldsSet(((TestPackedTypes) (message.wrappedMessage)));
    }

    public void testUnpackedParsing() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper.Builder builder = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestUnpackedTypes.newBuilder());
        AbstractMessageTest.AbstractMessageWrapper message = builder.mergeFrom(TestUtil.getUnpackedSet().toByteString()).build();
        TestUtil.assertUnpackedFieldsSet(((TestUnpackedTypes) (message.wrappedMessage)));
    }

    public void testOptimizedForSize() throws Exception {
        // We're mostly only checking that this class was compiled successfully.
        TestOptimizedForSize message = TestOptimizedForSize.newBuilder().setI(1).build();
        message = TestOptimizedForSize.parseFrom(message.toByteString());
        TestCase.assertEquals(2, message.getSerializedSize());
    }

    // -----------------------------------------------------------------
    // Tests for isInitialized().
    public void testIsInitialized() throws Exception {
        TestRequired.Builder builder = TestRequired.newBuilder();
        AbstractMessageTest.AbstractMessageWrapper.Builder abstractBuilder = new AbstractMessageTest.AbstractMessageWrapper.Builder(builder);
        TestCase.assertFalse(abstractBuilder.isInitialized());
        TestCase.assertEquals("a, b, c", getInitializationErrorString());
        builder.setA(1);
        TestCase.assertFalse(abstractBuilder.isInitialized());
        TestCase.assertEquals("b, c", getInitializationErrorString());
        builder.setB(1);
        TestCase.assertFalse(abstractBuilder.isInitialized());
        TestCase.assertEquals("c", getInitializationErrorString());
        builder.setC(1);
        TestCase.assertTrue(abstractBuilder.isInitialized());
        TestCase.assertEquals("", getInitializationErrorString());
    }

    public void testForeignIsInitialized() throws Exception {
        TestRequiredForeign.Builder builder = TestRequiredForeign.newBuilder();
        AbstractMessageTest.AbstractMessageWrapper.Builder abstractBuilder = new AbstractMessageTest.AbstractMessageWrapper.Builder(builder);
        TestCase.assertTrue(abstractBuilder.isInitialized());
        TestCase.assertEquals("", getInitializationErrorString());
        builder.setOptionalMessage(TestUtil.TEST_REQUIRED_UNINITIALIZED);
        TestCase.assertFalse(abstractBuilder.isInitialized());
        TestCase.assertEquals("optional_message.b, optional_message.c", getInitializationErrorString());
        builder.setOptionalMessage(TestUtil.TEST_REQUIRED_INITIALIZED);
        TestCase.assertTrue(abstractBuilder.isInitialized());
        TestCase.assertEquals("", getInitializationErrorString());
        builder.addRepeatedMessage(TestUtil.TEST_REQUIRED_UNINITIALIZED);
        TestCase.assertFalse(abstractBuilder.isInitialized());
        TestCase.assertEquals("repeated_message[0].b, repeated_message[0].c", getInitializationErrorString());
        builder.setRepeatedMessage(0, TestUtil.TEST_REQUIRED_INITIALIZED);
        TestCase.assertTrue(abstractBuilder.isInitialized());
        TestCase.assertEquals("", getInitializationErrorString());
    }

    // -----------------------------------------------------------------
    // Tests for mergeFrom
    static final TestAllTypes MERGE_SOURCE = TestAllTypes.newBuilder().setOptionalInt32(1).setOptionalString("foo").setOptionalForeignMessage(ForeignMessage.getDefaultInstance()).addRepeatedString("bar").build();

    static final TestAllTypes MERGE_DEST = TestAllTypes.newBuilder().setOptionalInt64(2).setOptionalString("baz").setOptionalForeignMessage(ForeignMessage.newBuilder().setC(3).build()).addRepeatedString("qux").build();

    static final String MERGE_RESULT_TEXT = "optional_int32: 1\n" + (((((("optional_int64: 2\n" + "optional_string: \"foo\"\n") + "optional_foreign_message {\n") + "  c: 3\n") + "}\n") + "repeated_string: \"qux\"\n") + "repeated_string: \"bar\"\n");

    public void testMergeFrom() throws Exception {
        AbstractMessageTest.AbstractMessageWrapper result = new AbstractMessageTest.AbstractMessageWrapper.Builder(TestAllTypes.newBuilder(AbstractMessageTest.MERGE_DEST)).mergeFrom(AbstractMessageTest.MERGE_SOURCE).build();
        TestCase.assertEquals(AbstractMessageTest.MERGE_RESULT_TEXT, result.toString());
    }

    // -----------------------------------------------------------------
    // Tests for equals and hashCode
    public void testEqualsAndHashCode() throws Exception {
        TestAllTypes a = TestUtil.getAllSet();
        TestAllTypes b = TestAllTypes.newBuilder().build();
        TestAllTypes c = TestAllTypes.newBuilder(b).addRepeatedString("x").build();
        TestAllTypes d = TestAllTypes.newBuilder(c).addRepeatedString("y").build();
        TestAllExtensions e = TestUtil.getAllExtensionsSet();
        TestAllExtensions f = TestAllExtensions.newBuilder(e).addExtension(repeatedInt32Extension, 999).build();
        checkEqualsIsConsistent(a);
        checkEqualsIsConsistent(b);
        checkEqualsIsConsistent(c);
        checkEqualsIsConsistent(d);
        checkEqualsIsConsistent(e);
        checkEqualsIsConsistent(f);
        checkNotEqual(a, b);
        checkNotEqual(a, c);
        checkNotEqual(a, d);
        checkNotEqual(a, e);
        checkNotEqual(a, f);
        checkNotEqual(b, c);
        checkNotEqual(b, d);
        checkNotEqual(b, e);
        checkNotEqual(b, f);
        checkNotEqual(c, d);
        checkNotEqual(c, e);
        checkNotEqual(c, f);
        checkNotEqual(d, e);
        checkNotEqual(d, f);
        checkNotEqual(e, f);
        // Deserializing into the TestEmptyMessage such that every field
        // is an {@link UnknownFieldSet.Field}.
        UnittestProto.TestEmptyMessage eUnknownFields = TestEmptyMessage.parseFrom(e.toByteArray());
        UnittestProto.TestEmptyMessage fUnknownFields = TestEmptyMessage.parseFrom(f.toByteArray());
        checkNotEqual(eUnknownFields, fUnknownFields);
        checkEqualsIsConsistent(eUnknownFields);
        checkEqualsIsConsistent(fUnknownFields);
        // Subsequent reconstitutions should be identical
        UnittestProto.TestEmptyMessage eUnknownFields2 = TestEmptyMessage.parseFrom(e.toByteArray());
        checkEqualsIsConsistent(eUnknownFields, eUnknownFields2);
    }

    public void testCheckByteStringIsUtf8OnUtf8() {
        ByteString byteString = ByteString.copyFromUtf8("some text");
        AbstractMessageLite.checkByteStringIsUtf8(byteString);
        // No exception thrown.
    }

    public void testCheckByteStringIsUtf8OnNonUtf8() {
        ByteString byteString = ByteString.copyFrom(new byte[]{ ((byte) (128)) });// A lone continuation byte.

        try {
            AbstractMessageLite.checkByteStringIsUtf8(byteString);
            TestCase.fail("Expected AbstractMessageLite.checkByteStringIsUtf8 to throw IllegalArgumentException");
        } catch (IllegalArgumentException exception) {
            TestCase.assertEquals("Byte string is not UTF-8.", exception.getMessage());
        }
    }
}

