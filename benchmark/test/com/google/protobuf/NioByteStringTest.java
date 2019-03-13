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


import ByteString.ByteIterator;
import ByteString.EMPTY;
import ByteString.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import junit.framework.TestCase;


/**
 * Tests for {@link NioByteString}.
 */
public class NioByteStringTest extends TestCase {
    private static final ByteString EMPTY = new NioByteString(ByteBuffer.wrap(new byte[0]));

    private static final String CLASSNAME = NioByteString.class.getSimpleName();

    private static final byte[] BYTES = ByteStringTest.getTestBytes(1234, 11337766L);

    private static final int EXPECTED_HASH = ByteString.wrap(NioByteStringTest.BYTES).hashCode();

    private final ByteBuffer backingBuffer = ByteBuffer.wrap(NioByteStringTest.BYTES.clone());

    private final ByteString testString = new NioByteString(backingBuffer);

    public void testExpectedType() {
        String actualClassName = getActualClassName(testString);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " should match type exactly"), NioByteStringTest.CLASSNAME, actualClassName);
    }

    public void testByteAt() {
        boolean stillEqual = true;
        for (int i = 0; stillEqual && (i < (NioByteStringTest.BYTES.length)); ++i) {
            stillEqual = (NioByteStringTest.BYTES[i]) == (testString.byteAt(i));
        }
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " must capture the right bytes"), stillEqual);
    }

    public void testByteIterator() {
        boolean stillEqual = true;
        ByteString.ByteIterator iter = testString.iterator();
        for (int i = 0; stillEqual && (i < (NioByteStringTest.BYTES.length)); ++i) {
            stillEqual = (iter.hasNext()) && ((NioByteStringTest.BYTES[i]) == (iter.nextByte()));
        }
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " must capture the right bytes"), stillEqual);
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must have exhausted the itertor"), iter.hasNext());
        try {
            iter.nextByte();
            TestCase.fail("Should have thrown an exception.");
        } catch (NoSuchElementException e) {
            // This is success
        }
    }

    public void testByteIterable() {
        boolean stillEqual = true;
        int j = 0;
        for (byte quantum : testString) {
            stillEqual = (NioByteStringTest.BYTES[j]) == quantum;
            ++j;
        }
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " must capture the right bytes as Bytes"), stillEqual);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " iterable character count"), NioByteStringTest.BYTES.length, j);
    }

    public void testSize() {
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must have the expected size"), NioByteStringTest.BYTES.length, testString.size());
    }

    public void testGetTreeDepth() {
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must have depth 0"), 0, testString.getTreeDepth());
    }

    public void testIsBalanced() {
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " is technically balanced"), testString.isBalanced());
    }

    public void testCopyTo_ByteArrayOffsetLength() {
        int destinationOffset = 50;
        int length = 100;
        byte[] destination = new byte[destinationOffset + length];
        int sourceOffset = 213;
        testString.copyTo(destination, sourceOffset, destinationOffset, length);
        boolean stillEqual = true;
        for (int i = 0; stillEqual && (i < length); ++i) {
            stillEqual = (NioByteStringTest.BYTES[(i + sourceOffset)]) == (destination[(i + destinationOffset)]);
        }
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".copyTo(4 arg) must give the expected bytes"), stillEqual);
    }

    public void testCopyTo_ByteArrayOffsetLengthErrors() {
        int destinationOffset = 50;
        int length = 100;
        byte[] destination = new byte[destinationOffset + length];
        try {
            // Copy one too many bytes
            testString.copyTo(destination, (((testString.size()) + 1) - length), destinationOffset, length);
            TestCase.fail(("Should have thrown an exception when copying too many bytes of a " + (NioByteStringTest.CLASSNAME)));
        } catch (IndexOutOfBoundsException expected) {
            // This is success
        }
        try {
            // Copy with illegal negative sourceOffset
            testString.copyTo(destination, (-1), destinationOffset, length);
            TestCase.fail(("Should have thrown an exception when given a negative sourceOffset in " + (NioByteStringTest.CLASSNAME)));
        } catch (IndexOutOfBoundsException expected) {
            // This is success
        }
        try {
            // Copy with illegal negative destinationOffset
            testString.copyTo(destination, 0, (-1), length);
            TestCase.fail(("Should have thrown an exception when given a negative destinationOffset in " + (NioByteStringTest.CLASSNAME)));
        } catch (IndexOutOfBoundsException expected) {
            // This is success
        }
        try {
            // Copy with illegal negative size
            testString.copyTo(destination, 0, 0, (-1));
            TestCase.fail(("Should have thrown an exception when given a negative size in " + (NioByteStringTest.CLASSNAME)));
        } catch (IndexOutOfBoundsException expected) {
            // This is success
        }
        try {
            // Copy with illegal too-large sourceOffset
            testString.copyTo(destination, (2 * (testString.size())), 0, length);
            TestCase.fail(("Should have thrown an exception when the destinationOffset is too large in " + (NioByteStringTest.CLASSNAME)));
        } catch (IndexOutOfBoundsException expected) {
            // This is success
        }
        try {
            // Copy with illegal too-large destinationOffset
            testString.copyTo(destination, 0, (2 * (destination.length)), length);
            TestCase.fail(("Should have thrown an exception when the destinationOffset is too large in " + (NioByteStringTest.CLASSNAME)));
        } catch (IndexOutOfBoundsException expected) {
            // This is success
        }
    }

    public void testCopyTo_ByteBuffer() {
        // Same length.
        ByteBuffer myBuffer = ByteBuffer.allocate(NioByteStringTest.BYTES.length);
        testString.copyTo(myBuffer);
        myBuffer.flip();
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ".copyTo(ByteBuffer) must give back the same bytes"), backingBuffer, myBuffer);
        // Target buffer bigger than required.
        myBuffer = ByteBuffer.allocate(((testString.size()) + 1));
        testString.copyTo(myBuffer);
        myBuffer.flip();
        TestCase.assertEquals(backingBuffer, myBuffer);
        // Target buffer has no space.
        myBuffer = ByteBuffer.allocate(0);
        try {
            testString.copyTo(myBuffer);
            TestCase.fail("Should have thrown an exception when target ByteBuffer has insufficient capacity");
        } catch (BufferOverflowException e) {
            // Expected.
        }
        // Target buffer too small.
        myBuffer = ByteBuffer.allocate(1);
        try {
            testString.copyTo(myBuffer);
            TestCase.fail("Should have thrown an exception when target ByteBuffer has insufficient capacity");
        } catch (BufferOverflowException e) {
            // Expected.
        }
    }

    public void testMarkSupported() {
        InputStream stream = testString.newInput();
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".newInput() must support marking"), stream.markSupported());
    }

    public void testMarkAndReset() throws IOException {
        int fraction = (testString.size()) / 3;
        InputStream stream = testString.newInput();
        stream.mark(testString.size());// First, mark() the end.

        NioByteStringTest.skipFully(stream, fraction);// Skip a large fraction, but not all.

        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ": after skipping to the 'middle', half the bytes are available"), ((testString.size()) - fraction), stream.available());
        stream.reset();
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ": after resetting, all bytes are available"), testString.size(), stream.available());
        NioByteStringTest.skipFully(stream, testString.size());// Skip to the end.

        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ": after skipping to the end, no more bytes are available"), 0, stream.available());
    }

    public void testAsReadOnlyByteBuffer() {
        ByteBuffer byteBuffer = testString.asReadOnlyByteBuffer();
        byte[] roundTripBytes = new byte[NioByteStringTest.BYTES.length];
        TestCase.assertTrue(((byteBuffer.remaining()) == (NioByteStringTest.BYTES.length)));
        TestCase.assertTrue(byteBuffer.isReadOnly());
        byteBuffer.get(roundTripBytes);
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".asReadOnlyByteBuffer() must give back the same bytes"), Arrays.equals(NioByteStringTest.BYTES, roundTripBytes));
    }

    public void testAsReadOnlyByteBufferList() {
        List<ByteBuffer> byteBuffers = testString.asReadOnlyByteBufferList();
        int bytesSeen = 0;
        byte[] roundTripBytes = new byte[NioByteStringTest.BYTES.length];
        for (ByteBuffer byteBuffer : byteBuffers) {
            int thisLength = byteBuffer.remaining();
            TestCase.assertTrue(byteBuffer.isReadOnly());
            TestCase.assertTrue(((bytesSeen + thisLength) <= (NioByteStringTest.BYTES.length)));
            byteBuffer.get(roundTripBytes, bytesSeen, thisLength);
            bytesSeen += thisLength;
        }
        TestCase.assertTrue((bytesSeen == (NioByteStringTest.BYTES.length)));
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".asReadOnlyByteBufferTest() must give back the same bytes"), Arrays.equals(NioByteStringTest.BYTES, roundTripBytes));
    }

    public void testToByteArray() {
        byte[] roundTripBytes = testString.toByteArray();
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".toByteArray() must give back the same bytes"), Arrays.equals(NioByteStringTest.BYTES, roundTripBytes));
    }

    public void testWriteTo() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        testString.writeTo(bos);
        byte[] roundTripBytes = bos.toByteArray();
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".writeTo() must give back the same bytes"), Arrays.equals(NioByteStringTest.BYTES, roundTripBytes));
    }

    public void testWriteToShouldNotExposeInternalBufferToOutputStream() throws IOException {
        OutputStream os = new OutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                Arrays.fill(b, off, (off + len), ((byte) (0)));
            }

            @Override
            public void write(int b) {
                throw new UnsupportedOperationException();
            }
        };
        byte[] original = Arrays.copyOf(NioByteStringTest.BYTES, NioByteStringTest.BYTES.length);
        testString.writeTo(os);
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".writeTo() must NOT grant access to underlying buffer"), Arrays.equals(original, NioByteStringTest.BYTES));
    }

    public void testWriteToInternalShouldExposeInternalBufferToOutputStream() throws IOException {
        OutputStream os = new OutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                Arrays.fill(b, off, (off + len), ((byte) (0)));
            }

            @Override
            public void write(int b) {
                throw new UnsupportedOperationException();
            }
        };
        testString.writeToInternal(os, 0, testString.size());
        byte[] allZeros = new byte[testString.size()];
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".writeToInternal() must grant access to underlying buffer"), Arrays.equals(allZeros, backingBuffer.array()));
    }

    public void testWriteToShouldExposeInternalBufferToByteOutput() throws IOException {
        ByteOutput out = new ByteOutput() {
            @Override
            public void write(byte value) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(byte[] value, int offset, int length) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void writeLazy(byte[] value, int offset, int length) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(ByteBuffer value) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void writeLazy(ByteBuffer value) throws IOException {
                Arrays.fill(value.array(), value.arrayOffset(), ((value.arrayOffset()) + (value.limit())), ((byte) (0)));
            }
        };
        testString.writeTo(out);
        byte[] allZeros = new byte[testString.size()];
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + ".writeTo() must grant access to underlying buffer"), Arrays.equals(allZeros, backingBuffer.array()));
    }

    public void testNewOutput() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteString.Output output = ByteString.newOutput();
        testString.writeTo(output);
        TestCase.assertEquals("Output Size returns correct result", output.size(), testString.size());
        output.writeTo(bos);
        TestCase.assertTrue("Output.writeTo() must give back the same bytes", Arrays.equals(NioByteStringTest.BYTES, bos.toByteArray()));
        // write the output stream to itself! This should cause it to double
        output.writeTo(output);
        TestCase.assertEquals("Writing an output stream to itself is successful", testString.concat(testString), output.toByteString());
        output.reset();
        TestCase.assertEquals("Output.reset() resets the output", 0, output.size());
        TestCase.assertEquals("Output.reset() resets the output", NioByteStringTest.EMPTY, output.toByteString());
    }

    public void testToString() {
        String testString = "I love unicode \u1234\u5678 characters";
        ByteString unicode = NioByteStringTest.forString(testString);
        String roundTripString = unicode.toString(Internal.UTF_8);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " unicode must match"), testString, roundTripString);
    }

    public void testCharsetToString() {
        String testString = "I love unicode \u1234\u5678 characters";
        ByteString unicode = NioByteStringTest.forString(testString);
        String roundTripString = unicode.toString(Internal.UTF_8);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " unicode must match"), testString, roundTripString);
    }

    public void testToString_returnsCanonicalEmptyString() {
        TestCase.assertSame(((NioByteStringTest.CLASSNAME) + " must be the same string references"), NioByteStringTest.EMPTY.toString(Internal.UTF_8), new NioByteString(ByteBuffer.wrap(new byte[0])).toString(Internal.UTF_8));
    }

    public void testToString_raisesException() {
        try {
            NioByteStringTest.EMPTY.toString("invalid");
            TestCase.fail("Should have thrown an exception.");
        } catch (UnsupportedEncodingException expected) {
            // This is success
        }
        try {
            testString.toString("invalid");
            TestCase.fail("Should have thrown an exception.");
        } catch (UnsupportedEncodingException expected) {
            // This is success
        }
    }

    public void testEquals() {
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must not equal null"), false, testString.equals(null));
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must equal self"), testString, testString);
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal the empty string"), testString.equals(NioByteStringTest.EMPTY));
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " empty strings must be equal"), NioByteStringTest.EMPTY, testString.substring(55, 55));
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must equal another string with the same value"), testString, new NioByteString(backingBuffer));
        byte[] mungedBytes = mungedBytes();
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal every string with the same length"), testString.equals(new NioByteString(ByteBuffer.wrap(mungedBytes))));
    }

    public void testEqualsLiteralByteString() {
        ByteString literal = ByteString.copyFrom(NioByteStringTest.BYTES);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must equal LiteralByteString with same value"), literal, testString);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must equal LiteralByteString with same value"), testString, literal);
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal the empty string"), testString.equals(ByteString.EMPTY));
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " empty strings must be equal"), ByteString.EMPTY, testString.substring(55, 55));
        literal = ByteString.copyFrom(mungedBytes());
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal every LiteralByteString with the same length"), testString.equals(literal));
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal every LiteralByteString with the same length"), literal.equals(testString));
    }

    public void testEqualsRopeByteString() {
        ByteString p1 = ByteString.copyFrom(NioByteStringTest.BYTES, 0, 5);
        ByteString p2 = ByteString.copyFrom(NioByteStringTest.BYTES, 5, ((NioByteStringTest.BYTES.length) - 5));
        ByteString rope = p1.concat(p2);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must equal RopeByteString with same value"), rope, testString);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must equal RopeByteString with same value"), testString, rope);
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal the empty string"), testString.equals(ByteString.EMPTY.concat(ByteString.EMPTY)));
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " empty strings must be equal"), ByteString.EMPTY.concat(ByteString.EMPTY), testString.substring(55, 55));
        byte[] mungedBytes = mungedBytes();
        p1 = ByteString.copyFrom(mungedBytes, 0, 5);
        p2 = ByteString.copyFrom(mungedBytes, 5, ((mungedBytes.length) - 5));
        rope = p1.concat(p2);
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal every RopeByteString with the same length"), testString.equals(rope));
        TestCase.assertFalse(((NioByteStringTest.CLASSNAME) + " must not equal every RopeByteString with the same length"), rope.equals(testString));
    }

    public void testHashCode() {
        int hash = testString.hashCode();
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " must have expected hashCode"), NioByteStringTest.EXPECTED_HASH, hash);
    }

    public void testPeekCachedHashCode() {
        ByteString newString = new NioByteString(backingBuffer);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ".peekCachedHashCode() should return zero at first"), 0, newString.peekCachedHashCode());
        newString.hashCode();
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ".peekCachedHashCode should return zero at first"), NioByteStringTest.EXPECTED_HASH, newString.peekCachedHashCode());
    }

    public void testPartialHash() {
        // partialHash() is more strenuously tested elsewhere by testing hashes of substrings.
        // This test would fail if the expected hash were 1.  It's not.
        int hash = testString.partialHash(testString.size(), 0, testString.size());
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + ".partialHash() must yield expected hashCode"), NioByteStringTest.EXPECTED_HASH, hash);
    }

    public void testNewInput() throws IOException {
        InputStream input = testString.newInput();
        TestCase.assertEquals("InputStream.available() returns correct value", testString.size(), input.available());
        boolean stillEqual = true;
        for (byte referenceByte : NioByteStringTest.BYTES) {
            int expectedInt = referenceByte & 255;
            stillEqual = expectedInt == (input.read());
        }
        TestCase.assertEquals("InputStream.available() returns correct value", 0, input.available());
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " must give the same bytes from the InputStream"), stillEqual);
        TestCase.assertEquals(((NioByteStringTest.CLASSNAME) + " InputStream must now be exhausted"), (-1), input.read());
    }

    public void testNewInput_skip() throws IOException {
        InputStream input = testString.newInput();
        int stringSize = testString.size();
        int nearEndIndex = (stringSize * 2) / 3;
        long skipped1 = input.skip(nearEndIndex);
        TestCase.assertEquals("InputStream.skip()", skipped1, nearEndIndex);
        TestCase.assertEquals("InputStream.available()", (stringSize - skipped1), input.available());
        TestCase.assertTrue("InputStream.mark() is available", input.markSupported());
        input.mark(0);
        TestCase.assertEquals("InputStream.skip(), read()", ((testString.byteAt(nearEndIndex)) & 255), input.read());
        TestCase.assertEquals("InputStream.available()", ((stringSize - skipped1) - 1), input.available());
        long skipped2 = input.skip(stringSize);
        TestCase.assertEquals("InputStream.skip() incomplete", skipped2, ((stringSize - skipped1) - 1));
        TestCase.assertEquals("InputStream.skip(), no more input", 0, input.available());
        TestCase.assertEquals("InputStream.skip(), no more input", (-1), input.read());
        input.reset();
        TestCase.assertEquals("InputStream.reset() succeded", (stringSize - skipped1), input.available());
        TestCase.assertEquals("InputStream.reset(), read()", ((testString.byteAt(nearEndIndex)) & 255), input.read());
    }

    public void testNewCodedInput() throws IOException {
        CodedInputStream cis = testString.newCodedInput();
        byte[] roundTripBytes = cis.readRawBytes(NioByteStringTest.BYTES.length);
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " must give the same bytes back from the CodedInputStream"), Arrays.equals(NioByteStringTest.BYTES, roundTripBytes));
        TestCase.assertTrue(((NioByteStringTest.CLASSNAME) + " CodedInputStream must now be exhausted"), cis.isAtEnd());
    }

    /**
     * Make sure we keep things simple when concatenating with empty. See also
     * {@link ByteStringTest#testConcat_empty()}.
     */
    public void testConcat_empty() {
        TestCase.assertSame((((NioByteStringTest.CLASSNAME) + " concatenated with empty must give ") + (NioByteStringTest.CLASSNAME)), testString.concat(NioByteStringTest.EMPTY), testString);
        TestCase.assertSame(((("empty concatenated with " + (NioByteStringTest.CLASSNAME)) + " must give ") + (NioByteStringTest.CLASSNAME)), NioByteStringTest.EMPTY.concat(testString), testString);
    }

    public void testJavaSerialization() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(testString);
        oos.close();
        byte[] pickled = out.toByteArray();
        InputStream in = new ByteArrayInputStream(pickled);
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();
        TestCase.assertTrue("Didn't get a ByteString back", (o instanceof ByteString));
        TestCase.assertEquals("Should get an equal ByteString back", testString, o);
    }
}

