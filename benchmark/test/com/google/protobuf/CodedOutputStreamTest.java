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


import Internal.UTF_8;
import TestSparseEnum.SPARSE_E;
import com.google.protobuf.CodedOutputStream.OutOfSpaceException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.SparseEnumMessage;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestPackedTypes;

import static Utf8.MAX_BYTES_PER_CHAR;


/**
 * Unit test for {@link CodedOutputStream}.
 *
 * @author kenton@google.com Kenton Varda
 */
public class CodedOutputStreamTest extends TestCase {
    private interface Coder {
        CodedOutputStream stream();

        byte[] toByteArray();

        CodedOutputStreamTest.OutputType getOutputType();
    }

    private static final class OutputStreamCoder implements CodedOutputStreamTest.Coder {
        private final CodedOutputStream stream;

        private final ByteArrayOutputStream output;

        OutputStreamCoder(int size) {
            output = new ByteArrayOutputStream();
            stream = CodedOutputStream.newInstance(output, size);
        }

        @Override
        public CodedOutputStream stream() {
            return stream;
        }

        @Override
        public byte[] toByteArray() {
            return output.toByteArray();
        }

        @Override
        public CodedOutputStreamTest.OutputType getOutputType() {
            return CodedOutputStreamTest.OutputType.STREAM;
        }
    }

    private static final class ArrayCoder implements CodedOutputStreamTest.Coder {
        private final CodedOutputStream stream;

        private final byte[] bytes;

        ArrayCoder(int size) {
            bytes = new byte[size];
            stream = CodedOutputStream.newInstance(bytes);
        }

        @Override
        public CodedOutputStream stream() {
            return stream;
        }

        @Override
        public byte[] toByteArray() {
            return Arrays.copyOf(bytes, stream.getTotalBytesWritten());
        }

        @Override
        public CodedOutputStreamTest.OutputType getOutputType() {
            return CodedOutputStreamTest.OutputType.ARRAY;
        }
    }

    private static final class NioHeapCoder implements CodedOutputStreamTest.Coder {
        private final CodedOutputStream stream;

        private final ByteBuffer buffer;

        private final int initialPosition;

        NioHeapCoder(int size) {
            this(size, 0);
        }

        NioHeapCoder(int size, int initialPosition) {
            this.initialPosition = initialPosition;
            buffer = ByteBuffer.allocate(size);
            buffer.position(initialPosition);
            stream = CodedOutputStream.newInstance(buffer);
        }

        @Override
        public CodedOutputStream stream() {
            return stream;
        }

        @Override
        public byte[] toByteArray() {
            ByteBuffer dup = buffer.duplicate();
            dup.position(initialPosition);
            dup.limit(buffer.position());
            byte[] bytes = new byte[dup.remaining()];
            dup.get(bytes);
            return bytes;
        }

        @Override
        public CodedOutputStreamTest.OutputType getOutputType() {
            return CodedOutputStreamTest.OutputType.NIO_HEAP;
        }
    }

    private static final class NioDirectCoder implements CodedOutputStreamTest.Coder {
        private final int initialPosition;

        private final CodedOutputStream stream;

        private final ByteBuffer buffer;

        private final boolean unsafe;

        NioDirectCoder(int size, boolean unsafe) {
            this(size, 0, unsafe);
        }

        NioDirectCoder(int size, int initialPosition, boolean unsafe) {
            this.unsafe = unsafe;
            this.initialPosition = initialPosition;
            buffer = ByteBuffer.allocateDirect(size);
            buffer.position(initialPosition);
            stream = (unsafe) ? CodedOutputStream.newUnsafeInstance(buffer) : CodedOutputStream.newSafeInstance(buffer);
        }

        @Override
        public CodedOutputStream stream() {
            return stream;
        }

        @Override
        public byte[] toByteArray() {
            ByteBuffer dup = buffer.duplicate();
            dup.position(initialPosition);
            dup.limit(buffer.position());
            byte[] bytes = new byte[dup.remaining()];
            dup.get(bytes);
            return bytes;
        }

        @Override
        public CodedOutputStreamTest.OutputType getOutputType() {
            return unsafe ? CodedOutputStreamTest.OutputType.NIO_DIRECT_SAFE : CodedOutputStreamTest.OutputType.NIO_DIRECT_UNSAFE;
        }
    }

    private enum OutputType {

        ARRAY() {
            @Override
            CodedOutputStreamTest.Coder newCoder(int size) {
                return new CodedOutputStreamTest.ArrayCoder(size);
            }
        },
        NIO_HEAP() {
            @Override
            CodedOutputStreamTest.Coder newCoder(int size) {
                return new CodedOutputStreamTest.NioHeapCoder(size);
            }
        },
        NIO_DIRECT_SAFE() {
            @Override
            CodedOutputStreamTest.Coder newCoder(int size) {
                return new CodedOutputStreamTest.NioDirectCoder(size, false);
            }
        },
        NIO_DIRECT_UNSAFE() {
            @Override
            CodedOutputStreamTest.Coder newCoder(int size) {
                return new CodedOutputStreamTest.NioDirectCoder(size, true);
            }
        },
        STREAM() {
            @Override
            CodedOutputStreamTest.Coder newCoder(int size) {
                return new CodedOutputStreamTest.OutputStreamCoder(size);
            }
        };
        abstract CodedOutputStreamTest.Coder newCoder(int size);
    }

    /**
     * Checks that invariants are maintained for varint round trip input and output.
     */
    public void testVarintRoundTrips() throws Exception {
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            CodedOutputStreamTest.assertVarintRoundTrip(outputType, 0L);
            for (int bits = 0; bits < 64; bits++) {
                long value = 1L << bits;
                CodedOutputStreamTest.assertVarintRoundTrip(outputType, value);
                CodedOutputStreamTest.assertVarintRoundTrip(outputType, (value + 1));
                CodedOutputStreamTest.assertVarintRoundTrip(outputType, (value - 1));
                CodedOutputStreamTest.assertVarintRoundTrip(outputType, (-value));
            }
        }
    }

    /**
     * Tests writeRawVarint32() and writeRawVarint64().
     */
    public void testWriteVarint() throws Exception {
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(0), 0);
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(1), 1);
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(127), 127);
        // 14882
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(162, 116), ((34 << 0) | (116 << 7)));
        // 2961488830
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(190, 247, 146, 132, 11), (((((62 << 0) | (119 << 7)) | (18 << 14)) | (4 << 21)) | (11L << 28)));
        // 64-bit
        // 7256456126
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(190, 247, 146, 132, 27), (((((62 << 0) | (119 << 7)) | (18 << 14)) | (4 << 21)) | (27L << 28)));
        // 41256202580718336
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(128, 230, 235, 156, 195, 201, 164, 73), ((((((((0 << 0) | (102 << 7)) | (107 << 14)) | (28 << 21)) | (67L << 28)) | (73L << 35)) | (36L << 42)) | (73L << 49)));
        // 11964378330978735131
        CodedOutputStreamTest.assertWriteVarint(CodedOutputStreamTest.bytes(155, 168, 249, 194, 187, 214, 128, 133, 166, 1), ((((((((((27 << 0) | (40 << 7)) | (121 << 14)) | (66 << 21)) | (59L << 28)) | (86L << 35)) | (0L << 42)) | (5L << 49)) | (38L << 56)) | (1L << 63)));
    }

    /**
     * Tests writeRawLittleEndian32() and writeRawLittleEndian64().
     */
    public void testWriteLittleEndian() throws Exception {
        CodedOutputStreamTest.assertWriteLittleEndian32(CodedOutputStreamTest.bytes(120, 86, 52, 18), 305419896);
        CodedOutputStreamTest.assertWriteLittleEndian32(CodedOutputStreamTest.bytes(240, 222, 188, 154), -1698898192);
        CodedOutputStreamTest.assertWriteLittleEndian64(CodedOutputStreamTest.bytes(240, 222, 188, 154, 120, 86, 52, 18), 1311768467463790320L);
        CodedOutputStreamTest.assertWriteLittleEndian64(CodedOutputStreamTest.bytes(120, 86, 52, 18, 240, 222, 188, 154), -7296712173568108936L);
    }

    /**
     * Test encodeZigZag32() and encodeZigZag64().
     */
    public void testEncodeZigZag() throws Exception {
        TestCase.assertEquals(0, CodedOutputStream.encodeZigZag32(0));
        TestCase.assertEquals(1, CodedOutputStream.encodeZigZag32((-1)));
        TestCase.assertEquals(2, CodedOutputStream.encodeZigZag32(1));
        TestCase.assertEquals(3, CodedOutputStream.encodeZigZag32((-2)));
        TestCase.assertEquals(2147483646, CodedOutputStream.encodeZigZag32(1073741823));
        TestCase.assertEquals(2147483647, CodedOutputStream.encodeZigZag32(-1073741824));
        TestCase.assertEquals(-2, CodedOutputStream.encodeZigZag32(2147483647));
        TestCase.assertEquals(-1, CodedOutputStream.encodeZigZag32(-2147483648));
        TestCase.assertEquals(0, CodedOutputStream.encodeZigZag64(0));
        TestCase.assertEquals(1, CodedOutputStream.encodeZigZag64((-1)));
        TestCase.assertEquals(2, CodedOutputStream.encodeZigZag64(1));
        TestCase.assertEquals(3, CodedOutputStream.encodeZigZag64((-2)));
        TestCase.assertEquals(2147483646L, CodedOutputStream.encodeZigZag64(1073741823L));
        TestCase.assertEquals(2147483647L, CodedOutputStream.encodeZigZag64(-1073741824L));
        TestCase.assertEquals(4294967294L, CodedOutputStream.encodeZigZag64(2147483647L));
        TestCase.assertEquals(4294967295L, CodedOutputStream.encodeZigZag64(-2147483648L));
        TestCase.assertEquals(-2L, CodedOutputStream.encodeZigZag64(9223372036854775807L));
        TestCase.assertEquals(-1L, CodedOutputStream.encodeZigZag64(-9223372036854775808L));
        // Some easier-to-verify round-trip tests.  The inputs (other than 0, 1, -1)
        // were chosen semi-randomly via keyboard bashing.
        TestCase.assertEquals(0, CodedOutputStream.encodeZigZag32(CodedInputStream.decodeZigZag32(0)));
        TestCase.assertEquals(1, CodedOutputStream.encodeZigZag32(CodedInputStream.decodeZigZag32(1)));
        TestCase.assertEquals((-1), CodedOutputStream.encodeZigZag32(CodedInputStream.decodeZigZag32((-1))));
        TestCase.assertEquals(14927, CodedOutputStream.encodeZigZag32(CodedInputStream.decodeZigZag32(14927)));
        TestCase.assertEquals((-3612), CodedOutputStream.encodeZigZag32(CodedInputStream.decodeZigZag32((-3612))));
        TestCase.assertEquals(0, CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64(0)));
        TestCase.assertEquals(1, CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64(1)));
        TestCase.assertEquals((-1), CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64((-1))));
        TestCase.assertEquals(14927, CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64(14927)));
        TestCase.assertEquals((-3612), CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64((-3612))));
        TestCase.assertEquals(856912304801416L, CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64(856912304801416L)));
        TestCase.assertEquals((-75123905439571256L), CodedOutputStream.encodeZigZag64(CodedInputStream.decodeZigZag64((-75123905439571256L))));
    }

    /**
     * Tests writing a whole message with every field type.
     */
    public void testWriteWholeMessage() throws Exception {
        final byte[] expectedBytes = TestUtil.getGoldenMessage().toByteArray();
        TestAllTypes message = TestUtil.getAllSet();
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            CodedOutputStreamTest.Coder coder = outputType.newCoder(message.getSerializedSize());
            message.writeTo(coder.stream());
            coder.stream().flush();
            byte[] rawBytes = coder.toByteArray();
            CodedOutputStreamTest.assertEqualBytes(outputType, expectedBytes, rawBytes);
        }
        // Try different block sizes.
        for (int blockSize = 1; blockSize < 256; blockSize *= 2) {
            CodedOutputStreamTest.Coder coder = CodedOutputStreamTest.OutputType.STREAM.newCoder(blockSize);
            message.writeTo(coder.stream());
            coder.stream().flush();
            CodedOutputStreamTest.assertEqualBytes(CodedOutputStreamTest.OutputType.STREAM, expectedBytes, coder.toByteArray());
        }
    }

    /**
     * Tests writing a whole message with every packed field type. Ensures the
     * wire format of packed fields is compatible with C++.
     */
    public void testWriteWholePackedFieldsMessage() throws Exception {
        byte[] expectedBytes = TestUtil.getGoldenPackedFieldsMessage().toByteArray();
        TestPackedTypes message = TestUtil.getPackedSet();
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            CodedOutputStreamTest.Coder coder = outputType.newCoder(message.getSerializedSize());
            message.writeTo(coder.stream());
            coder.stream().flush();
            byte[] rawBytes = coder.toByteArray();
            CodedOutputStreamTest.assertEqualBytes(outputType, expectedBytes, rawBytes);
        }
    }

    /**
     * Test writing a message containing a negative enum value. This used to
     * fail because the size was not properly computed as a sign-extended varint.
     */
    public void testWriteMessageWithNegativeEnumValue() throws Exception {
        SparseEnumMessage message = SparseEnumMessage.newBuilder().setSparseEnum(SPARSE_E).build();
        TestCase.assertTrue(((message.getSparseEnum().getNumber()) < 0));
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            CodedOutputStreamTest.Coder coder = outputType.newCoder(message.getSerializedSize());
            message.writeTo(coder.stream());
            coder.stream().flush();
            byte[] rawBytes = coder.toByteArray();
            SparseEnumMessage message2 = SparseEnumMessage.parseFrom(rawBytes);
            TestCase.assertEquals(SPARSE_E, message2.getSparseEnum());
        }
    }

    /**
     * Test getTotalBytesWritten()
     */
    public void testGetTotalBytesWritten() throws Exception {
        CodedOutputStreamTest.Coder coder = CodedOutputStreamTest.OutputType.STREAM.newCoder((4 * 1024));
        // Write some some bytes (more than the buffer can hold) and verify that totalWritten
        // is correct.
        byte[] value = "abcde".getBytes(UTF_8);
        for (int i = 0; i < 1024; ++i) {
            coder.stream().writeRawBytes(value, 0, value.length);
        }
        TestCase.assertEquals(((value.length) * 1024), coder.stream().getTotalBytesWritten());
        // Now write an encoded string.
        String string = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        // Ensure we take the slower fast path.
        TestCase.assertTrue(((CodedOutputStream.computeUInt32SizeNoTag(string.length())) != (CodedOutputStream.computeUInt32SizeNoTag(((string.length()) * (MAX_BYTES_PER_CHAR))))));
        coder.stream().writeStringNoTag(string);
        coder.stream().flush();
        int stringSize = CodedOutputStream.computeStringSizeNoTag(string);
        // Verify that the total bytes written is correct
        TestCase.assertEquals((((value.length) * 1024) + stringSize), coder.stream().getTotalBytesWritten());
    }

    // TODO(dweis): Write a comprehensive test suite for CodedOutputStream that covers more than just
    // this case.
    public void testWriteStringNoTag_fastpath() throws Exception {
        int bufferSize = 153;
        String threeBytesPer = "\u0981";
        String string = threeBytesPer;
        for (int i = 0; i < 50; i++) {
            string += threeBytesPer;
        }
        // These checks ensure we will tickle the slower fast path.
        TestCase.assertEquals(1, CodedOutputStream.computeUInt32SizeNoTag(string.length()));
        TestCase.assertEquals(2, CodedOutputStream.computeUInt32SizeNoTag(((string.length()) * (MAX_BYTES_PER_CHAR))));
        TestCase.assertEquals(bufferSize, ((string.length()) * (MAX_BYTES_PER_CHAR)));
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            CodedOutputStreamTest.Coder coder = outputType.newCoder((bufferSize + 2));
            coder.stream().writeStringNoTag(string);
            coder.stream().flush();
        }
    }

    public void testWriteToByteBuffer() throws Exception {
        final int bufferSize = 16 * 1024;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        CodedOutputStream codedStream = CodedOutputStream.newInstance(buffer);
        // Write raw bytes into the ByteBuffer.
        final int length1 = 5000;
        for (int i = 0; i < length1; i++) {
            codedStream.writeRawByte(((byte) (1)));
        }
        final int length2 = 8 * 1024;
        byte[] data = new byte[length2];
        for (int i = 0; i < length2; i++) {
            data[i] = ((byte) (2));
        }
        codedStream.writeRawBytes(data);
        final int length3 = (bufferSize - length1) - length2;
        for (int i = 0; i < length3; i++) {
            codedStream.writeRawByte(((byte) (3)));
        }
        codedStream.flush();
        // Check that data is correctly written to the ByteBuffer.
        TestCase.assertEquals(0, buffer.remaining());
        buffer.flip();
        for (int i = 0; i < length1; i++) {
            TestCase.assertEquals(((byte) (1)), buffer.get());
        }
        for (int i = 0; i < length2; i++) {
            TestCase.assertEquals(((byte) (2)), buffer.get());
        }
        for (int i = 0; i < length3; i++) {
            TestCase.assertEquals(((byte) (3)), buffer.get());
        }
    }

    public void testWriteByteBuffer() throws Exception {
        byte[] value = "abcde".getBytes(UTF_8);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CodedOutputStream codedStream = CodedOutputStream.newInstance(outputStream);
        ByteBuffer byteBuffer = ByteBuffer.wrap(value, 0, 1);
        // This will actually write 5 bytes into the CodedOutputStream as the
        // ByteBuffer's capacity() is 5.
        codedStream.writeRawBytes(byteBuffer);
        // The above call shouldn't affect the ByteBuffer's state.
        TestCase.assertEquals(0, byteBuffer.position());
        TestCase.assertEquals(1, byteBuffer.limit());
        // The correct way to write part of an array using ByteBuffer.
        codedStream.writeRawBytes(ByteBuffer.wrap(value, 2, 1).slice());
        codedStream.flush();
        byte[] result = outputStream.toByteArray();
        TestCase.assertEquals(6, result.length);
        for (int i = 0; i < 5; i++) {
            TestCase.assertEquals(value[i], result[i]);
        }
        TestCase.assertEquals(value[2], result[5]);
    }

    public void testWriteByteArrayWithOffsets() throws Exception {
        byte[] fullArray = CodedOutputStreamTest.bytes(17, 34, 51, 68, 85, 102, 119, 136);
        for (CodedOutputStreamTest.OutputType type : new CodedOutputStreamTest.OutputType[]{ CodedOutputStreamTest.OutputType.ARRAY }) {
            CodedOutputStreamTest.Coder coder = type.newCoder(4);
            coder.stream().writeByteArrayNoTag(fullArray, 2, 2);
            CodedOutputStreamTest.assertEqualBytes(type, CodedOutputStreamTest.bytes(2, 51, 68), coder.toByteArray());
            TestCase.assertEquals(3, coder.stream().getTotalBytesWritten());
        }
    }

    public void testSerializeUtf8_MultipleSmallWrites() throws Exception {
        final String source = "abcdefghijklmnopqrstuvwxyz";
        // Generate the expected output if the source string is written 2 bytes at a time.
        ByteArrayOutputStream expectedBytesStream = new ByteArrayOutputStream();
        for (int pos = 0; pos < (source.length()); pos += 2) {
            String substr = source.substring(pos, (pos + 2));
            expectedBytesStream.write(2);
            expectedBytesStream.write(substr.getBytes(UTF_8));
        }
        final byte[] expectedBytes = expectedBytesStream.toByteArray();
        // For each output type, write the source string 2 bytes at a time and verify the output.
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            CodedOutputStreamTest.Coder coder = outputType.newCoder(expectedBytes.length);
            for (int pos = 0; pos < (source.length()); pos += 2) {
                String substr = source.substring(pos, (pos + 2));
                coder.stream().writeStringNoTag(substr);
            }
            coder.stream().flush();
            CodedOutputStreamTest.assertEqualBytes(outputType, expectedBytes, coder.toByteArray());
        }
    }

    public void testSerializeInvalidUtf8() throws Exception {
        String[] invalidStrings = new String[]{ CodedOutputStreamTest.newString(Character.MIN_HIGH_SURROGATE), "foobar" + (CodedOutputStreamTest.newString(Character.MIN_HIGH_SURROGATE)), CodedOutputStreamTest.newString(Character.MIN_LOW_SURROGATE), "foobar" + (CodedOutputStreamTest.newString(Character.MIN_LOW_SURROGATE)), CodedOutputStreamTest.newString(Character.MIN_HIGH_SURROGATE, Character.MIN_HIGH_SURROGATE) };
        CodedOutputStream outputWithStream = CodedOutputStream.newInstance(new ByteArrayOutputStream());
        CodedOutputStream outputWithArray = CodedOutputStream.newInstance(new byte[10000]);
        CodedOutputStream outputWithByteBuffer = CodedOutputStream.newInstance(ByteBuffer.allocate(10000));
        for (String s : invalidStrings) {
            // TODO(dweis): These should all fail; instead they are corrupting data.
            CodedOutputStream.computeStringSizeNoTag(s);
            outputWithStream.writeStringNoTag(s);
            outputWithArray.writeStringNoTag(s);
            outputWithByteBuffer.writeStringNoTag(s);
        }
    }

    // TODO(nathanmittler): This test can be deleted once we properly throw IOException while
    // encoding invalid UTF-8 strings.
    public void testSerializeInvalidUtf8FollowedByOutOfSpace() throws Exception {
        final int notEnoughBytes = 4;
        CodedOutputStream outputWithArray = CodedOutputStream.newInstance(new byte[notEnoughBytes]);
        CodedOutputStream outputWithByteBuffer = CodedOutputStream.newInstance(ByteBuffer.allocate(notEnoughBytes));
        String invalidString = CodedOutputStreamTest.newString(Character.MIN_HIGH_SURROGATE, 'f', 'o', 'o', 'b', 'a', 'r');
        try {
            outputWithArray.writeStringNoTag(invalidString);
            TestCase.fail("Expected OutOfSpaceException");
        } catch (OutOfSpaceException e) {
            TestCase.assertTrue(((e.getCause()) instanceof IndexOutOfBoundsException));
        }
        try {
            outputWithByteBuffer.writeStringNoTag(invalidString);
            TestCase.fail("Expected OutOfSpaceException");
        } catch (OutOfSpaceException e) {
            TestCase.assertTrue(((e.getCause()) instanceof IndexOutOfBoundsException));
        }
    }

    /**
     * Regression test for https://github.com/google/protobuf/issues/292
     */
    public void testCorrectExceptionThrowWhenEncodingStringsWithoutEnoughSpace() throws Exception {
        String testCase = "Foooooooo";
        TestCase.assertEquals(CodedOutputStream.computeUInt32SizeNoTag(testCase.length()), CodedOutputStream.computeUInt32SizeNoTag(((testCase.length()) * 3)));
        TestCase.assertEquals(11, CodedOutputStream.computeStringSize(1, testCase));
        // Tag is one byte, varint describing string length is 1 byte, string length is 9 bytes.
        // An array of size 1 will cause a failure when trying to write the varint.
        for (CodedOutputStreamTest.OutputType outputType : new CodedOutputStreamTest.OutputType[]{ CodedOutputStreamTest.OutputType.ARRAY, CodedOutputStreamTest.OutputType.NIO_HEAP, CodedOutputStreamTest.OutputType.NIO_DIRECT_SAFE, CodedOutputStreamTest.OutputType.NIO_DIRECT_UNSAFE }) {
            for (int i = 0; i < 11; i++) {
                CodedOutputStreamTest.Coder coder = outputType.newCoder(i);
                try {
                    coder.stream().writeString(1, testCase);
                    TestCase.fail("Should have thrown an out of space exception");
                } catch (CodedOutputStream expected) {
                }
            }
        }
    }

    public void testDifferentStringLengths() throws Exception {
        // Test string serialization roundtrip using strings of the following lengths,
        // with ASCII and Unicode characters requiring different UTF-8 byte counts per
        // char, hence causing the length delimiter varint to sometimes require more
        // bytes for the Unicode strings than the ASCII string of the same length.
        int[] lengths = // 3 bytes for ASCII and Unicode
        new int[]{ 0, 1, (1 << 4) - 1// 1 byte for ASCII and Unicode
        , (1 << 7) - 1// 1 byte for ASCII, 2 bytes for Unicode
        , (1 << 11) - 1// 2 bytes for ASCII and Unicode
        , (1 << 14) - 1// 2 bytes for ASCII, 3 bytes for Unicode
        , (1 << 17) - 1 }// 3 bytes for ASCII and Unicode
        ;
        for (CodedOutputStreamTest.OutputType outputType : CodedOutputStreamTest.OutputType.values()) {
            for (int i : lengths) {
                CodedOutputStreamTest.testEncodingOfString(outputType, 'q', i);// 1 byte per char

                CodedOutputStreamTest.testEncodingOfString(outputType, '\u07ff', i);// 2 bytes per char

                CodedOutputStreamTest.testEncodingOfString(outputType, '\u0981', i);// 3 bytes per char

            }
        }
    }

    public void testNioEncodersWithInitialOffsets() throws Exception {
        String value = "abc";
        for (CodedOutputStreamTest.Coder coder : new CodedOutputStreamTest.Coder[]{ new CodedOutputStreamTest.NioHeapCoder(10, 2), new CodedOutputStreamTest.NioDirectCoder(10, 2, false), new CodedOutputStreamTest.NioDirectCoder(10, 2, true) }) {
            coder.stream().writeStringNoTag(value);
            coder.stream().flush();
            CodedOutputStreamTest.assertEqualBytes(coder.getOutputType(), new byte[]{ 3, 'a', 'b', 'c' }, coder.toByteArray());
        }
    }
}

