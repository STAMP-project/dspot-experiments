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
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link CompositeReadableBuffer}.
 */
@RunWith(JUnit4.class)
public class CompositeReadableBufferTest {
    private static final String EXPECTED_VALUE = "hello world";

    private CompositeReadableBuffer composite;

    @Test
    public void singleBufferShouldSucceed() {
        composite = new CompositeReadableBuffer();
        composite.addBuffer(ReadableBuffers.wrap(CompositeReadableBufferTest.EXPECTED_VALUE.getBytes(Charsets.UTF_8)));
        Assert.assertEquals(CompositeReadableBufferTest.EXPECTED_VALUE.length(), composite.readableBytes());
        Assert.assertEquals(CompositeReadableBufferTest.EXPECTED_VALUE, ReadableBuffers.readAsStringUtf8(composite));
        Assert.assertEquals(0, composite.readableBytes());
    }

    @Test
    public void readUnsignedByteShouldSucceed() {
        for (int ix = 0; ix < (CompositeReadableBufferTest.EXPECTED_VALUE.length()); ++ix) {
            int c = composite.readUnsignedByte();
            Assert.assertEquals(CompositeReadableBufferTest.EXPECTED_VALUE.charAt(ix), ((char) (c)));
        }
        Assert.assertEquals(0, composite.readableBytes());
    }

    @Test
    public void readUnsignedByteShouldSkipZeroLengthBuffer() {
        composite = new CompositeReadableBuffer();
        composite.addBuffer(ReadableBuffers.wrap(new byte[0]));
        byte[] in = new byte[]{ 1 };
        composite.addBuffer(ReadableBuffers.wrap(in));
        Assert.assertEquals(1, composite.readUnsignedByte());
        Assert.assertEquals(0, composite.readableBytes());
    }

    @Test
    public void skipBytesShouldSucceed() {
        int remaining = CompositeReadableBufferTest.EXPECTED_VALUE.length();
        composite.skipBytes(1);
        remaining--;
        Assert.assertEquals(remaining, composite.readableBytes());
        composite.skipBytes(5);
        remaining -= 5;
        Assert.assertEquals(remaining, composite.readableBytes());
        composite.skipBytes(remaining);
        Assert.assertEquals(0, composite.readableBytes());
    }

    @Test
    public void readByteArrayShouldSucceed() {
        byte[] bytes = new byte[composite.readableBytes()];
        int writeIndex = 0;
        composite.readBytes(bytes, writeIndex, 1);
        writeIndex++;
        Assert.assertEquals(((CompositeReadableBufferTest.EXPECTED_VALUE.length()) - writeIndex), composite.readableBytes());
        composite.readBytes(bytes, writeIndex, 5);
        writeIndex += 5;
        Assert.assertEquals(((CompositeReadableBufferTest.EXPECTED_VALUE.length()) - writeIndex), composite.readableBytes());
        int remaining = composite.readableBytes();
        composite.readBytes(bytes, writeIndex, remaining);
        writeIndex += remaining;
        Assert.assertEquals(0, composite.readableBytes());
        Assert.assertEquals(bytes.length, writeIndex);
        Assert.assertEquals(CompositeReadableBufferTest.EXPECTED_VALUE, new String(bytes, Charsets.UTF_8));
    }

    @Test
    public void readByteBufferShouldSucceed() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(CompositeReadableBufferTest.EXPECTED_VALUE.length());
        int remaining = CompositeReadableBufferTest.EXPECTED_VALUE.length();
        byteBuffer.limit(1);
        composite.readBytes(byteBuffer);
        remaining--;
        Assert.assertEquals(remaining, composite.readableBytes());
        byteBuffer.limit(((byteBuffer.limit()) + 5));
        composite.readBytes(byteBuffer);
        remaining -= 5;
        Assert.assertEquals(remaining, composite.readableBytes());
        byteBuffer.limit(((byteBuffer.limit()) + remaining));
        composite.readBytes(byteBuffer);
        Assert.assertEquals(0, composite.readableBytes());
        Assert.assertEquals(CompositeReadableBufferTest.EXPECTED_VALUE, new String(byteBuffer.array(), Charsets.UTF_8));
    }

    @Test
    public void readStreamShouldSucceed() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int remaining = CompositeReadableBufferTest.EXPECTED_VALUE.length();
        composite.readBytes(bos, 1);
        remaining--;
        Assert.assertEquals(remaining, composite.readableBytes());
        composite.readBytes(bos, 5);
        remaining -= 5;
        Assert.assertEquals(remaining, composite.readableBytes());
        composite.readBytes(bos, remaining);
        Assert.assertEquals(0, composite.readableBytes());
        Assert.assertEquals(CompositeReadableBufferTest.EXPECTED_VALUE, new String(bos.toByteArray(), Charsets.UTF_8));
    }

    @Test
    public void closeShouldCloseBuffers() {
        composite = new CompositeReadableBuffer();
        ReadableBuffer mock1 = Mockito.mock(ReadableBuffer.class);
        ReadableBuffer mock2 = Mockito.mock(ReadableBuffer.class);
        composite.addBuffer(mock1);
        composite.addBuffer(mock2);
        composite.close();
        Mockito.verify(mock1).close();
        Mockito.verify(mock2).close();
    }
}

