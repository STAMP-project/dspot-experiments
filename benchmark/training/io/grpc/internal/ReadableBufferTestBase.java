/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Abstract base class for tests of {@link ReadableBuffer} subclasses.
 */
@RunWith(JUnit4.class)
public abstract class ReadableBufferTestBase {
    // Use a long string to ensure that any chunking/splitting works correctly.
    protected static final String msg = ReadableBufferTestBase.repeatUntilLength("hello", (8 * 1024));

    @Test
    public void bufferShouldReadAllBytes() {
        ReadableBuffer buffer = buffer();
        for (int ix = 0; ix < (ReadableBufferTestBase.msg.length()); ++ix) {
            Assert.assertEquals(((ReadableBufferTestBase.msg.length()) - ix), buffer.readableBytes());
            Assert.assertEquals(ReadableBufferTestBase.msg.charAt(ix), buffer.readUnsignedByte());
        }
        Assert.assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void readToArrayShouldSucceed() {
        ReadableBuffer buffer = buffer();
        byte[] array = new byte[ReadableBufferTestBase.msg.length()];
        buffer.readBytes(array, 0, array.length);
        Assert.assertArrayEquals(ReadableBufferTestBase.msg.getBytes(Charsets.UTF_8), array);
        Assert.assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void partialReadToArrayShouldSucceed() {
        ReadableBuffer buffer = buffer();
        byte[] array = new byte[ReadableBufferTestBase.msg.length()];
        buffer.readBytes(array, 1, 2);
        Assert.assertArrayEquals(new byte[]{ 'h', 'e' }, Arrays.copyOfRange(array, 1, 3));
        Assert.assertEquals(((ReadableBufferTestBase.msg.length()) - 2), buffer.readableBytes());
    }

    @Test
    public void readToStreamShouldSucceed() throws Exception {
        ReadableBuffer buffer = buffer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        buffer.readBytes(stream, ReadableBufferTestBase.msg.length());
        Assert.assertArrayEquals(ReadableBufferTestBase.msg.getBytes(Charsets.UTF_8), stream.toByteArray());
        Assert.assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void partialReadToStreamShouldSucceed() throws Exception {
        ReadableBuffer buffer = buffer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        buffer.readBytes(stream, 2);
        Assert.assertArrayEquals(new byte[]{ 'h', 'e' }, Arrays.copyOfRange(stream.toByteArray(), 0, 2));
        Assert.assertEquals(((ReadableBufferTestBase.msg.length()) - 2), buffer.readableBytes());
    }

    @Test
    public void readToByteBufferShouldSucceed() {
        ReadableBuffer buffer = buffer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(ReadableBufferTestBase.msg.length());
        buffer.readBytes(byteBuffer);
        byteBuffer.flip();
        byte[] array = new byte[ReadableBufferTestBase.msg.length()];
        byteBuffer.get(array);
        Assert.assertArrayEquals(ReadableBufferTestBase.msg.getBytes(Charsets.UTF_8), array);
        Assert.assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void partialReadToByteBufferShouldSucceed() {
        ReadableBuffer buffer = buffer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        buffer.readBytes(byteBuffer);
        byteBuffer.flip();
        byte[] array = new byte[2];
        byteBuffer.get(array);
        Assert.assertArrayEquals(new byte[]{ 'h', 'e' }, array);
        Assert.assertEquals(((ReadableBufferTestBase.msg.length()) - 2), buffer.readableBytes());
    }

    @Test
    public void partialReadToReadableBufferShouldSucceed() {
        ReadableBuffer buffer = buffer();
        ReadableBuffer newBuffer = buffer.readBytes(2);
        Assert.assertEquals(2, newBuffer.readableBytes());
        Assert.assertEquals(((ReadableBufferTestBase.msg.length()) - 2), buffer.readableBytes());
        byte[] array = new byte[2];
        newBuffer.readBytes(array, 0, 2);
        Assert.assertArrayEquals(new byte[]{ 'h', 'e' }, Arrays.copyOfRange(array, 0, 2));
    }
}

