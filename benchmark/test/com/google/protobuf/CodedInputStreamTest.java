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


import ByteString.Output;
import UnknownFieldSet.Builder;
import WireFormat.WIRETYPE_LENGTH_DELIMITED;
import WireFormat.WIRETYPE_VARINT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto.BoolMessage;
import protobuf_unittest.UnittestProto.Int32Message;
import protobuf_unittest.UnittestProto.Int64Message;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestRecursiveMessage;


/**
 * Unit test for {@link CodedInputStream}.
 *
 * @author kenton@google.com Kenton Varda
 */
public class CodedInputStreamTest extends TestCase {
    private static final int DEFAULT_BLOCK_SIZE = 4096;

    private enum InputType {

        ARRAY() {
            @Override
            CodedInputStream newDecoder(byte[] data, int blockSize) {
                return CodedInputStream.newInstance(data);
            }
        },
        NIO_HEAP() {
            @Override
            CodedInputStream newDecoder(byte[] data, int blockSize) {
                return CodedInputStream.newInstance(ByteBuffer.wrap(data));
            }
        },
        NIO_DIRECT() {
            @Override
            CodedInputStream newDecoder(byte[] data, int blockSize) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
                buffer.put(data);
                buffer.flip();
                return CodedInputStream.newInstance(buffer);
            }
        },
        STREAM() {
            @Override
            CodedInputStream newDecoder(byte[] data, int blockSize) {
                return CodedInputStream.newInstance(new CodedInputStreamTest.SmallBlockInputStream(data, blockSize));
            }
        },
        ITER_DIRECT() {
            @Override
            CodedInputStream newDecoder(byte[] data, int blockSize) {
                if (blockSize > (CodedInputStreamTest.DEFAULT_BLOCK_SIZE)) {
                    blockSize = CodedInputStreamTest.DEFAULT_BLOCK_SIZE;
                }
                ArrayList<ByteBuffer> input = new ArrayList<ByteBuffer>();
                for (int i = 0; i < (data.length); i += blockSize) {
                    int rl = Math.min(blockSize, ((data.length) - i));
                    ByteBuffer rb = ByteBuffer.allocateDirect(rl);
                    rb.put(data, i, rl);
                    rb.flip();
                    input.add(rb);
                }
                return CodedInputStream.newInstance(input);
            }
        },
        STREAM_ITER_DIRECT() {
            @Override
            CodedInputStream newDecoder(byte[] data, int blockSize) {
                if (blockSize > (CodedInputStreamTest.DEFAULT_BLOCK_SIZE)) {
                    blockSize = CodedInputStreamTest.DEFAULT_BLOCK_SIZE;
                }
                ArrayList<ByteBuffer> input = new ArrayList<ByteBuffer>();
                for (int i = 0; i < (data.length); i += blockSize) {
                    int rl = Math.min(blockSize, ((data.length) - i));
                    ByteBuffer rb = ByteBuffer.allocateDirect(rl);
                    rb.put(data, i, rl);
                    rb.flip();
                    input.add(rb);
                }
                return CodedInputStream.newInstance(new IterableByteBufferInputStream(input));
            }
        };
        CodedInputStream newDecoder(byte[] data) {
            return newDecoder(data, data.length);
        }

        abstract CodedInputStream newDecoder(byte[] data, int blockSize);
    }

    /**
     * An InputStream which limits the number of bytes it reads at a time. We use this to make sure
     * that CodedInputStream doesn't screw up when reading in small blocks.
     */
    private static final class SmallBlockInputStream extends FilterInputStream {
        private final int blockSize;

        public SmallBlockInputStream(byte[] data, int blockSize) {
            super(new ByteArrayInputStream(data));
            this.blockSize = blockSize;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return super.read(b, 0, Math.min(b.length, blockSize));
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return super.read(b, off, Math.min(len, blockSize));
        }
    }

    /**
     * Tests readRawVarint32() and readRawVarint64().
     */
    public void testReadVarint() throws Exception {
        assertReadVarint(bytes(0), 0);
        assertReadVarint(bytes(1), 1);
        assertReadVarint(bytes(127), 127);
        // 14882
        assertReadVarint(bytes(162, 116), ((34 << 0) | (116 << 7)));
        // 2961488830
        assertReadVarint(bytes(190, 247, 146, 132, 11), (((((62 << 0) | (119 << 7)) | (18 << 14)) | (4 << 21)) | (11L << 28)));
        // 64-bit
        // 7256456126
        assertReadVarint(bytes(190, 247, 146, 132, 27), (((((62 << 0) | (119 << 7)) | (18 << 14)) | (4 << 21)) | (27L << 28)));
        // 41256202580718336
        assertReadVarint(bytes(128, 230, 235, 156, 195, 201, 164, 73), ((((((((0 << 0) | (102 << 7)) | (107 << 14)) | (28 << 21)) | (67L << 28)) | (73L << 35)) | (36L << 42)) | (73L << 49)));
        // 11964378330978735131
        assertReadVarint(bytes(155, 168, 249, 194, 187, 214, 128, 133, 166, 1), ((((((((((27 << 0) | (40 << 7)) | (121 << 14)) | (66 << 21)) | (59L << 28)) | (86L << 35)) | (0L << 42)) | (5L << 49)) | (38L << 56)) | (1L << 63)));
        // Failures
        assertReadVarintFailure(InvalidProtocolBufferException.malformedVarint(), bytes(128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 0));
        assertReadVarintFailure(InvalidProtocolBufferException.truncatedMessage(), bytes(128));
    }

    /**
     * Tests readRawLittleEndian32() and readRawLittleEndian64().
     */
    public void testReadLittleEndian() throws Exception {
        assertReadLittleEndian32(bytes(120, 86, 52, 18), 305419896);
        assertReadLittleEndian32(bytes(240, 222, 188, 154), -1698898192);
        assertReadLittleEndian64(bytes(240, 222, 188, 154, 120, 86, 52, 18), 1311768467463790320L);
        assertReadLittleEndian64(bytes(120, 86, 52, 18, 240, 222, 188, 154), -7296712173568108936L);
    }

    /**
     * Test decodeZigZag32() and decodeZigZag64().
     */
    public void testDecodeZigZag() throws Exception {
        TestCase.assertEquals(0, CodedInputStream.decodeZigZag32(0));
        TestCase.assertEquals((-1), CodedInputStream.decodeZigZag32(1));
        TestCase.assertEquals(1, CodedInputStream.decodeZigZag32(2));
        TestCase.assertEquals((-2), CodedInputStream.decodeZigZag32(3));
        TestCase.assertEquals(1073741823, CodedInputStream.decodeZigZag32(2147483646));
        TestCase.assertEquals(-1073741824, CodedInputStream.decodeZigZag32(2147483647));
        TestCase.assertEquals(2147483647, CodedInputStream.decodeZigZag32(-2));
        TestCase.assertEquals(-2147483648, CodedInputStream.decodeZigZag32(-1));
        TestCase.assertEquals(0, CodedInputStream.decodeZigZag64(0));
        TestCase.assertEquals((-1), CodedInputStream.decodeZigZag64(1));
        TestCase.assertEquals(1, CodedInputStream.decodeZigZag64(2));
        TestCase.assertEquals((-2), CodedInputStream.decodeZigZag64(3));
        TestCase.assertEquals(1073741823L, CodedInputStream.decodeZigZag64(2147483646L));
        TestCase.assertEquals(-1073741824L, CodedInputStream.decodeZigZag64(2147483647L));
        TestCase.assertEquals(2147483647L, CodedInputStream.decodeZigZag64(4294967294L));
        TestCase.assertEquals(-2147483648L, CodedInputStream.decodeZigZag64(4294967295L));
        TestCase.assertEquals(9223372036854775807L, CodedInputStream.decodeZigZag64(-2L));
        TestCase.assertEquals(-9223372036854775808L, CodedInputStream.decodeZigZag64(-1L));
    }

    /**
     * Tests reading and parsing a whole message with every field type.
     */
    public void testReadWholeMessage() throws Exception {
        TestAllTypes message = TestUtil.getAllSet();
        byte[] rawBytes = message.toByteArray();
        TestCase.assertEquals(rawBytes.length, message.getSerializedSize());
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            // Try different block sizes.
            for (int blockSize = 1; blockSize < 256; blockSize *= 2) {
                TestAllTypes message2 = TestAllTypes.parseFrom(inputType.newDecoder(rawBytes, blockSize));
                TestUtil.assertAllFieldsSet(message2);
            }
        }
    }

    /**
     * Tests skipField().
     */
    public void testSkipWholeMessage() throws Exception {
        TestAllTypes message = TestUtil.getAllSet();
        byte[] rawBytes = message.toByteArray();
        CodedInputStreamTest.InputType[] inputTypes = CodedInputStreamTest.InputType.values();
        CodedInputStream[] inputs = new CodedInputStream[inputTypes.length];
        for (int i = 0; i < (inputs.length); ++i) {
            inputs[i] = inputTypes[i].newDecoder(rawBytes);
        }
        UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
        while (true) {
            CodedInputStream input1 = inputs[0];
            int tag = input1.readTag();
            // Ensure that the rest match.
            for (int i = 1; i < (inputs.length); ++i) {
                TestCase.assertEquals(inputTypes[i].name(), tag, inputs[i].readTag());
            }
            if (tag == 0) {
                break;
            }
            unknownFields.mergeFieldFrom(tag, input1);
            // Skip the field for the rest of the inputs.
            for (int i = 1; i < (inputs.length); ++i) {
                inputs[i].skipField(tag);
            }
        } 
    }

    /**
     * Test that a bug in skipRawBytes() has been fixed: if the skip skips exactly up to a limit, this
     * should not break things.
     */
    public void testSkipRawBytesBug() throws Exception {
        byte[] rawBytes = new byte[]{ 1, 2 };
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(rawBytes);
            int limit = input.pushLimit(1);
            input.skipRawBytes(1);
            input.popLimit(limit);
            TestCase.assertEquals(inputType.name(), 2, input.readRawByte());
        }
    }

    /**
     * Test that a bug in skipRawBytes() has been fixed: if the skip skips past the end of a buffer
     * with a limit that has been set past the end of that buffer, this should not break things.
     */
    public void testSkipRawBytesPastEndOfBufferWithLimit() throws Exception {
        byte[] rawBytes = new byte[]{ 1, 2, 3, 4, 5 };
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(rawBytes);
            int limit = input.pushLimit(4);
            // In order to expose the bug we need to read at least one byte to prime the
            // buffer inside the CodedInputStream.
            TestCase.assertEquals(inputType.name(), 1, input.readRawByte());
            // Skip to the end of the limit.
            input.skipRawBytes(3);
            TestCase.assertTrue(inputType.name(), input.isAtEnd());
            input.popLimit(limit);
            TestCase.assertEquals(inputType.name(), 5, input.readRawByte());
        }
    }

    public void testReadHugeBlob() throws Exception {
        // Allocate and initialize a 1MB blob.
        byte[] blob = new byte[1 << 20];
        for (int i = 0; i < (blob.length); i++) {
            blob[i] = ((byte) (i));
        }
        // Make a message containing it.
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TestUtil.setAllFields(builder);
        builder.setOptionalBytes(ByteString.copyFrom(blob));
        TestAllTypes message = builder.build();
        byte[] data = message.toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            // Serialize and parse it.  Make sure to parse from an InputStream, not
            // directly from a ByteString, so that CodedInputStream uses buffered
            // reading.
            TestAllTypes message2 = TestAllTypes.parseFrom(inputType.newDecoder(data));
            TestCase.assertEquals(inputType.name(), message.getOptionalBytes(), message2.getOptionalBytes());
            // Make sure all the other fields were parsed correctly.
            TestAllTypes message3 = TestAllTypes.newBuilder(message2).setOptionalBytes(TestUtil.getAllSet().getOptionalBytes()).build();
            TestUtil.assertAllFieldsSet(message3);
        }
    }

    public void testReadMaliciouslyLargeBlob() throws Exception {
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(2147483647);
        output.writeRawBytes(new byte[32]);// Pad with a few random bytes.

        output.flush();
        byte[] data = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(data);
            TestCase.assertEquals(tag, input.readTag());
            try {
                input.readBytes();
                TestCase.fail(((inputType.name()) + ": Should have thrown an exception!"));
            } catch (InvalidProtocolBufferException e) {
                // success.
            }
        }
    }

    /**
     * Test we can do messages that are up to CodedInputStream#DEFAULT_SIZE_LIMIT
     * in size (2G or Integer#MAX_SIZE).
     *
     * @throws IOException
     * 		
     */
    public void testParseMessagesCloseTo2G() throws IOException {
        byte[] serializedMessage = CodedInputStreamTest.getBigSerializedMessage();
        // How many of these big messages do we need to take us near our 2G limit?
        int count = (Integer.MAX_VALUE) / (serializedMessage.length);
        // Now make an inputstream that will fake a near 2G message of messages
        // returning our big serialized message 'count' times.
        InputStream is = new CodedInputStreamTest.RepeatingInputStream(serializedMessage, count);
        // Parse should succeed!
        TestAllTypes.parseFrom(is);
    }

    /**
     * Test there is an exception if a message exceeds
     * CodedInputStream#DEFAULT_SIZE_LIMIT in size (2G or Integer#MAX_SIZE).
     *
     * @throws IOException
     * 		
     */
    public void testParseMessagesOver2G() throws IOException {
        byte[] serializedMessage = CodedInputStreamTest.getBigSerializedMessage();
        // How many of these big messages do we need to take us near our 2G limit?
        int count = (Integer.MAX_VALUE) / (serializedMessage.length);
        // Now add one to take us over the limit
        count++;
        // Now make an inputstream that will fake a near 2G message of messages
        // returning our big serialized message 'count' times.
        InputStream is = new CodedInputStreamTest.RepeatingInputStream(serializedMessage, count);
        try {
            TestAllTypes.parseFrom(is);
            TestCase.fail("Should have thrown an exception!");
        } catch (InvalidProtocolBufferException e) {
            TestCase.assertTrue(e.getMessage().contains("too large"));
        }
    }

    /* An input stream that repeats a byte arrays' content a number of times.
    Simulates really large input without consuming loads of memory. Used above
    to test the parsing behavior when the input size exceeds 2G or close to it.
     */
    private static class RepeatingInputStream extends InputStream {
        private final byte[] serializedMessage;

        private final int count;

        private int index = 0;

        private int offset = 0;

        RepeatingInputStream(byte[] serializedMessage, int count) {
            this.serializedMessage = serializedMessage;
            this.count = count;
        }

        @Override
        public int read() throws IOException {
            if ((this.offset) == (this.serializedMessage.length)) {
                (this.index)++;
                this.offset = 0;
            }
            if ((this.index) == (this.count)) {
                return -1;
            }
            return this.serializedMessage[((offset)++)];
        }
    }

    public void testMaliciousRecursion() throws Exception {
        byte[] data100 = makeRecursiveMessage(100).toByteArray();
        byte[] data101 = makeRecursiveMessage(101).toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            assertMessageDepth(inputType.name(), TestRecursiveMessage.parseFrom(inputType.newDecoder(data100)), 100);
            try {
                TestRecursiveMessage.parseFrom(inputType.newDecoder(data101));
                TestCase.fail("Should have thrown an exception!");
            } catch (InvalidProtocolBufferException e) {
                // success.
            }
            CodedInputStream input = inputType.newDecoder(data100);
            input.setRecursionLimit(8);
            try {
                TestRecursiveMessage.parseFrom(input);
                TestCase.fail(((inputType.name()) + ": Should have thrown an exception!"));
            } catch (InvalidProtocolBufferException e) {
                // success.
            }
        }
    }

    public void testSizeLimit() throws Exception {
        // NOTE: Size limit only applies to the stream-backed CIS.
        CodedInputStream input = CodedInputStream.newInstance(new CodedInputStreamTest.SmallBlockInputStream(TestUtil.getAllSet().toByteArray(), 16));
        input.setSizeLimit(16);
        try {
            TestAllTypes.parseFrom(input);
            TestCase.fail("Should have thrown an exception!");
        } catch (InvalidProtocolBufferException expected) {
            checkSizeLimitExceeded(expected);
        }
    }

    public void testResetSizeCounter() throws Exception {
        // NOTE: Size limit only applies to the stream-backed CIS.
        CodedInputStream input = CodedInputStream.newInstance(new CodedInputStreamTest.SmallBlockInputStream(new byte[256], 8));
        input.setSizeLimit(16);
        input.readRawBytes(16);
        TestCase.assertEquals(16, input.getTotalBytesRead());
        try {
            input.readRawByte();
            TestCase.fail("Should have thrown an exception!");
        } catch (InvalidProtocolBufferException expected) {
            checkSizeLimitExceeded(expected);
        }
        input.resetSizeCounter();
        TestCase.assertEquals(0, input.getTotalBytesRead());
        input.readRawByte();// No exception thrown.

        input.resetSizeCounter();
        TestCase.assertEquals(0, input.getTotalBytesRead());
        input.readRawBytes(16);
        TestCase.assertEquals(16, input.getTotalBytesRead());
        input.resetSizeCounter();
        try {
            input.readRawBytes(17);// Hits limit again.

            TestCase.fail("Should have thrown an exception!");
        } catch (InvalidProtocolBufferException expected) {
            checkSizeLimitExceeded(expected);
        }
    }

    public void testRefillBufferWithCorrectSize() throws Exception {
        // NOTE: refillBuffer only applies to the stream-backed CIS.
        byte[] bytes = "123456789".getBytes("UTF-8");
        ByteArrayOutputStream rawOutput = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput, bytes.length);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.writeRawByte(4);
        output.flush();
        // Input is two string with length 9 and one raw byte.
        byte[] rawInput = rawOutput.toByteArray();
        for (int inputStreamBufferLength = 8; inputStreamBufferLength <= ((rawInput.length) + 1); inputStreamBufferLength++) {
            CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(rawInput), inputStreamBufferLength);
            input.setSizeLimit(((rawInput.length) - 1));
            input.readString();
            input.readString();
            try {
                input.readRawByte();// Hits limit.

                TestCase.fail("Should have thrown an exception!");
            } catch (InvalidProtocolBufferException expected) {
                checkSizeLimitExceeded(expected);
            }
        }
    }

    public void testIsAtEnd() throws Exception {
        CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(new byte[5]));
        try {
            for (int i = 0; i < 5; i++) {
                TestCase.assertEquals(false, input.isAtEnd());
                input.readRawByte();
            }
            TestCase.assertEquals(true, input.isAtEnd());
        } catch (Exception e) {
            TestCase.fail("Catch exception in the testIsAtEnd");
        }
    }

    public void testCurrentLimitExceeded() throws Exception {
        byte[] bytes = "123456789".getBytes("UTF-8");
        ByteArrayOutputStream rawOutput = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput, bytes.length);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] rawInput = rawOutput.toByteArray();
        CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(rawInput));
        // The length of the whole rawInput
        input.setSizeLimit(11);
        // Some number that is smaller than the rawInput's length
        // but larger than 2
        input.pushLimit(5);
        try {
            input.readString();
            TestCase.fail("Should have thrown an exception");
        } catch (InvalidProtocolBufferException expected) {
            TestCase.assertEquals(expected.getMessage(), InvalidProtocolBufferException.truncatedMessage().getMessage());
        }
    }

    public void testSizeLimitMultipleMessages() throws Exception {
        // NOTE: Size limit only applies to the stream-backed CIS.
        byte[] bytes = new byte[256];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) (i));
        }
        CodedInputStream input = CodedInputStream.newInstance(new CodedInputStreamTest.SmallBlockInputStream(bytes, 7));
        input.setSizeLimit(16);
        for (int i = 0; i < (256 / 16); i++) {
            byte[] message = input.readRawBytes(16);
            for (int j = 0; j < (message.length); j++) {
                TestCase.assertEquals(((i * 16) + j), ((message[j]) & 255));
            }
            TestCase.assertEquals(16, input.getTotalBytesRead());
            input.resetSizeCounter();
            TestCase.assertEquals(0, input.getTotalBytesRead());
        }
    }

    public void testReadString() throws Exception {
        String lorem = "Lorem ipsum dolor sit amet ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4096; i += lorem.length()) {
            builder.append(lorem);
        }
        lorem = builder.toString().substring(0, 4096);
        byte[] bytes = lorem.getBytes("UTF-8");
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput, bytes.length);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] rawInput = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(rawInput);
            TestCase.assertEquals(inputType.name(), tag, input.readTag());
            String text = input.readString();
            TestCase.assertEquals(inputType.name(), lorem, text);
        }
    }

    public void testReadStringRequireUtf8() throws Exception {
        String lorem = "Lorem ipsum dolor sit amet ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4096; i += lorem.length()) {
            builder.append(lorem);
        }
        lorem = builder.toString().substring(0, 4096);
        byte[] bytes = lorem.getBytes("UTF-8");
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput, bytes.length);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] rawInput = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(rawInput);
            TestCase.assertEquals(inputType.name(), tag, input.readTag());
            String text = input.readStringRequireUtf8();
            TestCase.assertEquals(inputType.name(), lorem, text);
        }
    }

    /**
     * Tests that if we readString invalid UTF-8 bytes, no exception is thrown. Instead, the invalid
     * bytes are replaced with the Unicode "replacement character" U+FFFD.
     */
    public void testReadStringInvalidUtf8() throws Exception {
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (128)) });
        output.flush();
        byte[] rawInput = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(rawInput);
            TestCase.assertEquals(inputType.name(), tag, input.readTag());
            String text = input.readString();
            TestCase.assertEquals(inputType.name(), 65533, text.charAt(0));
        }
    }

    /**
     * Tests that if we readStringRequireUtf8 invalid UTF-8 bytes, an InvalidProtocolBufferException
     * is thrown.
     */
    public void testReadStringRequireUtf8InvalidUtf8() throws Exception {
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        int tag = WireFormat.makeTag(1, WIRETYPE_LENGTH_DELIMITED);
        output.writeRawVarint32(tag);
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (128)) });
        output.flush();
        byte[] rawInput = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream input = inputType.newDecoder(rawInput);
            TestCase.assertEquals(tag, input.readTag());
            try {
                input.readStringRequireUtf8();
                TestCase.fail(((inputType.name()) + ": Expected invalid UTF-8 exception."));
            } catch (InvalidProtocolBufferException exception) {
                TestCase.assertEquals(inputType.name(), "Protocol message had invalid UTF-8.", exception.getMessage());
            }
        }
    }

    public void testReadFromSlice() throws Exception {
        byte[] bytes = bytes(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        CodedInputStream in = CodedInputStream.newInstance(bytes, 3, 5);
        TestCase.assertEquals(0, in.getTotalBytesRead());
        for (int i = 3; i < 8; i++) {
            TestCase.assertEquals(i, in.readRawByte());
            TestCase.assertEquals((i - 2), in.getTotalBytesRead());
        }
        // eof
        TestCase.assertEquals(0, in.readTag());
        TestCase.assertEquals(5, in.getTotalBytesRead());
    }

    public void testInvalidTag() throws Exception {
        // Any tag number which corresponds to field number zero is invalid and
        // should throw InvalidProtocolBufferException.
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            for (int i = 0; i < 8; i++) {
                try {
                    inputType.newDecoder(bytes(i)).readTag();
                    TestCase.fail(((inputType.name()) + ": Should have thrown an exception."));
                } catch (InvalidProtocolBufferException e) {
                    TestCase.assertEquals(inputType.name(), InvalidProtocolBufferException.invalidTag().getMessage(), e.getMessage());
                }
            }
        }
    }

    public void testReadByteArray() throws Exception {
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        // Zero-sized bytes field.
        output.writeRawVarint32(0);
        // One one-byte bytes field
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (23)) });
        // Another one-byte bytes field
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (45)) });
        // A bytes field large enough that won't fit into the 4K buffer.
        final int bytesLength = 16 * 1024;
        byte[] bytes = new byte[bytesLength];
        bytes[0] = ((byte) (67));
        bytes[(bytesLength - 1)] = ((byte) (89));
        output.writeRawVarint32(bytesLength);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] rawInput = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream inputStream = inputType.newDecoder(rawInput);
            byte[] result = inputStream.readByteArray();
            TestCase.assertEquals(inputType.name(), 0, result.length);
            result = inputStream.readByteArray();
            TestCase.assertEquals(inputType.name(), 1, result.length);
            TestCase.assertEquals(inputType.name(), ((byte) (23)), result[0]);
            result = inputStream.readByteArray();
            TestCase.assertEquals(inputType.name(), 1, result.length);
            TestCase.assertEquals(inputType.name(), ((byte) (45)), result[0]);
            result = inputStream.readByteArray();
            TestCase.assertEquals(inputType.name(), bytesLength, result.length);
            TestCase.assertEquals(inputType.name(), ((byte) (67)), result[0]);
            TestCase.assertEquals(inputType.name(), ((byte) (89)), result[(bytesLength - 1)]);
        }
    }

    public void testReadLargeByteStringFromInputStream() throws Exception {
        byte[] bytes = new byte[1024 * 1024];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) (i & 255));
        }
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] data = rawOutput.toByteString().toByteArray();
        CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(data) {
            @Override
            public synchronized int available() {
                return 0;
            }
        });
        ByteString result = input.readBytes();
        TestCase.assertEquals(ByteString.copyFrom(bytes), result);
    }

    public void testReadLargeByteArrayFromInputStream() throws Exception {
        byte[] bytes = new byte[1024 * 1024];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) (i & 255));
        }
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        output.writeRawVarint32(bytes.length);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] data = rawOutput.toByteString().toByteArray();
        CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(data) {
            @Override
            public synchronized int available() {
                return 0;
            }
        });
        byte[] result = input.readByteArray();
        TestCase.assertTrue(Arrays.equals(bytes, result));
    }

    public void testReadByteBuffer() throws Exception {
        ByteString.Output rawOutput = ByteString.newOutput();
        CodedOutputStream output = CodedOutputStream.newInstance(rawOutput);
        // Zero-sized bytes field.
        output.writeRawVarint32(0);
        // One one-byte bytes field
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (23)) });
        // Another one-byte bytes field
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (45)) });
        // A bytes field large enough that won't fit into the 4K buffer.
        final int bytesLength = 16 * 1024;
        byte[] bytes = new byte[bytesLength];
        bytes[0] = ((byte) (67));
        bytes[(bytesLength - 1)] = ((byte) (89));
        output.writeRawVarint32(bytesLength);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] rawInput = rawOutput.toByteString().toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream inputStream = inputType.newDecoder(rawInput);
            ByteBuffer result = inputStream.readByteBuffer();
            TestCase.assertEquals(inputType.name(), 0, result.capacity());
            result = inputStream.readByteBuffer();
            TestCase.assertEquals(inputType.name(), 1, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (23)), result.get());
            result = inputStream.readByteBuffer();
            TestCase.assertEquals(inputType.name(), 1, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (45)), result.get());
            result = inputStream.readByteBuffer();
            TestCase.assertEquals(inputType.name(), bytesLength, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (67)), result.get());
            result.position((bytesLength - 1));
            TestCase.assertEquals(inputType.name(), ((byte) (89)), result.get());
        }
    }

    public void testReadByteBufferAliasing() throws Exception {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(byteArrayStream);
        // Zero-sized bytes field.
        output.writeRawVarint32(0);
        // One one-byte bytes field
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (23)) });
        // Another one-byte bytes field
        output.writeRawVarint32(1);
        output.writeRawBytes(new byte[]{ ((byte) (45)) });
        // A bytes field large enough that won't fit into the 4K buffer.
        final int bytesLength = 16 * 1024;
        byte[] bytes = new byte[bytesLength];
        bytes[0] = ((byte) (67));
        bytes[(bytesLength - 1)] = ((byte) (89));
        output.writeRawVarint32(bytesLength);
        output.writeRawBytes(bytes);
        output.flush();
        byte[] data = byteArrayStream.toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            if (((inputType == (CodedInputStreamTest.InputType.STREAM)) || (inputType == (CodedInputStreamTest.InputType.STREAM_ITER_DIRECT))) || (inputType == (CodedInputStreamTest.InputType.ITER_DIRECT))) {
                // Aliasing doesn't apply to stream-backed CIS.
                continue;
            }
            // Without aliasing
            CodedInputStream inputStream = inputType.newDecoder(data);
            ByteBuffer result = inputStream.readByteBuffer();
            TestCase.assertEquals(inputType.name(), 0, result.capacity());
            result = inputStream.readByteBuffer();
            TestCase.assertTrue(inputType.name(), ((result.array()) != data));
            TestCase.assertEquals(inputType.name(), 1, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (23)), result.get());
            result = inputStream.readByteBuffer();
            TestCase.assertTrue(inputType.name(), ((result.array()) != data));
            TestCase.assertEquals(inputType.name(), 1, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (45)), result.get());
            result = inputStream.readByteBuffer();
            TestCase.assertTrue(inputType.name(), ((result.array()) != data));
            TestCase.assertEquals(inputType.name(), bytesLength, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (67)), result.get());
            result.position((bytesLength - 1));
            TestCase.assertEquals(inputType.name(), ((byte) (89)), result.get());
            // Enable aliasing
            inputStream = inputType.newDecoder(data, data.length);
            inputStream.enableAliasing(true);
            result = inputStream.readByteBuffer();
            TestCase.assertEquals(inputType.name(), 0, result.capacity());
            result = inputStream.readByteBuffer();
            if (result.hasArray()) {
                TestCase.assertTrue(inputType.name(), ((result.array()) == data));
            }
            TestCase.assertEquals(inputType.name(), 1, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (23)), result.get());
            result = inputStream.readByteBuffer();
            if (result.hasArray()) {
                TestCase.assertTrue(inputType.name(), ((result.array()) == data));
            }
            TestCase.assertEquals(inputType.name(), 1, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (45)), result.get());
            result = inputStream.readByteBuffer();
            if (result.hasArray()) {
                TestCase.assertTrue(inputType.name(), ((result.array()) == data));
            }
            TestCase.assertEquals(inputType.name(), bytesLength, result.capacity());
            TestCase.assertEquals(inputType.name(), ((byte) (67)), result.get());
            result.position((bytesLength - 1));
            TestCase.assertEquals(inputType.name(), ((byte) (89)), result.get());
        }
    }

    public void testCompatibleTypes() throws Exception {
        long data = 4294967296L;
        Int64Message message = Int64Message.newBuilder().setData(data).build();
        byte[] serialized = message.toByteArray();
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            CodedInputStream inputStream = inputType.newDecoder(serialized);
            // Test int64(long) is compatible with bool(boolean)
            BoolMessage msg2 = BoolMessage.parseFrom(inputStream);
            TestCase.assertTrue(msg2.getData());
            // Test int64(long) is compatible with int32(int)
            inputStream = inputType.newDecoder(serialized);
            Int32Message msg3 = Int32Message.parseFrom(inputStream);
            TestCase.assertEquals(((int) (data)), msg3.getData());
        }
    }

    public void testSkipInvalidVarint_FastPath() throws Exception {
        // Fast path: We have >= 10 bytes available. Ensure we properly recognize a non-ending varint.
        byte[] data = new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 };
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            try {
                CodedInputStream input = inputType.newDecoder(data);
                input.skipField(WireFormat.makeTag(1, WIRETYPE_VARINT));
                TestCase.fail(((inputType.name()) + ": Should have thrown an exception."));
            } catch (InvalidProtocolBufferException e) {
                // Expected
            }
        }
    }

    public void testSkipInvalidVarint_SlowPath() throws Exception {
        // Slow path: < 10 bytes available. Ensure we properly recognize a non-ending varint.
        byte[] data = new byte[]{ -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        for (CodedInputStreamTest.InputType inputType : CodedInputStreamTest.InputType.values()) {
            try {
                CodedInputStream input = inputType.newDecoder(data);
                input.skipField(WireFormat.makeTag(1, WIRETYPE_VARINT));
                TestCase.fail(((inputType.name()) + ": Should have thrown an exception."));
            } catch (InvalidProtocolBufferException e) {
                // Expected
            }
        }
    }
}

