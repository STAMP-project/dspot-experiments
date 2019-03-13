/**
 * Copyright 2018 The gRPC Authors
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
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link ReadableBuffers}.
 * See also: {@link ReadableBuffersArrayTest}, {@link ReadableBuffersByteBufferTest}.
 */
@RunWith(JUnit4.class)
public class ReadableBuffersTest {
    private static final byte[] MSG_BYTES = "hello".getBytes(Charsets.UTF_8);

    @Test
    public void empty_returnsEmptyBuffer() {
        ReadableBuffer buffer = ReadableBuffers.empty();
        Assert.assertArrayEquals(new byte[0], buffer.array());
    }

    @Test(expected = NullPointerException.class)
    public void readArray_checksNotNull() {
        ReadableBuffers.readArray(null);
    }

    @Test
    public void readArray_returnsBufferArray() {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        Assert.assertArrayEquals(new byte[]{ 'h', 'e', 'l', 'l', 'o' }, ReadableBuffers.readArray(buffer));
    }

    @Test
    public void readAsString_returnsString() {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        Assert.assertEquals("hello", ReadableBuffers.readAsString(buffer, Charsets.UTF_8));
    }

    @Test(expected = NullPointerException.class)
    public void readAsString_checksNotNull() {
        ReadableBuffers.readAsString(null, Charsets.UTF_8);
    }

    @Test
    public void readAsStringUtf8_returnsString() {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        Assert.assertEquals("hello", ReadableBuffers.readAsStringUtf8(buffer));
    }

    @Test(expected = NullPointerException.class)
    public void readAsStringUtf8_checksNotNull() {
        ReadableBuffers.readAsStringUtf8(null);
    }

    @Test
    public void openStream_ignoresClose() throws Exception {
        ReadableBuffer buffer = Mockito.mock(ReadableBuffer.class);
        InputStream stream = ReadableBuffers.openStream(buffer, false);
        stream.close();
        Mockito.verify(buffer, Mockito.never()).close();
    }

    @Test
    public void bufferInputStream_available_returnsReadableBytes() throws Exception {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        InputStream inputStream = ReadableBuffers.openStream(buffer, true);
        Assert.assertEquals(5, inputStream.available());
        while ((inputStream.available()) != 0) {
            inputStream.read();
        } 
        Assert.assertEquals((-1), inputStream.read());
    }

    @Test
    public void bufferInputStream_read_returnsUnsignedByte() throws Exception {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        InputStream inputStream = ReadableBuffers.openStream(buffer, true);
        Assert.assertEquals(((int) ('h')), inputStream.read());
    }

    @Test
    public void bufferInputStream_read_writes() throws Exception {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        InputStream inputStream = ReadableBuffers.openStream(buffer, true);
        byte[] dest = new byte[5];
        Assert.assertEquals(5, /* destOffset */
        /* length */
        inputStream.read(dest, 0, 5));
        Assert.assertArrayEquals(new byte[]{ 'h', 'e', 'l', 'l', 'o' }, dest);
        Assert.assertEquals((-1), /* dest */
        /* destOffset */
        /* length */
        inputStream.read(new byte[1], 0, 1));
    }

    @Test
    public void bufferInputStream_read_writesPartially() throws Exception {
        ReadableBuffer buffer = ReadableBuffers.wrap(ReadableBuffersTest.MSG_BYTES);
        InputStream inputStream = ReadableBuffers.openStream(buffer, true);
        byte[] dest = new byte[3];
        Assert.assertEquals(2, /* destOffset */
        /* length */
        inputStream.read(dest, 1, 2));
        Assert.assertArrayEquals(new byte[]{ 0, 'h', 'e' }, dest);
    }

    @Test
    public void bufferInputStream_close_closesBuffer() throws Exception {
        ReadableBuffer buffer = Mockito.mock(ReadableBuffer.class);
        InputStream inputStream = ReadableBuffers.openStream(buffer, true);
        inputStream.close();
        Mockito.verify(buffer, Mockito.times(1)).close();
    }
}

