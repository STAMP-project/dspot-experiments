/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayTest {
    private static final String STRING_CONTENT = "Hello, ByteArray!";

    private static final byte[] BYTES_CONTENT = ByteArrayTest.STRING_CONTENT.getBytes(StandardCharsets.UTF_8);

    private static final ByteBuffer BYTE_BUFFER_CONTENT = ByteBuffer.wrap(ByteArrayTest.BYTES_CONTENT);

    private static final InputStream STREAM_CONTENT = new ByteArrayInputStream(ByteArrayTest.BYTES_CONTENT);

    private static final ByteArray STRING_ARRAY = ByteArray.copyFrom(ByteArrayTest.STRING_CONTENT);

    private static final ByteArray BYTES_ARRAY = ByteArray.copyFrom(ByteArrayTest.BYTES_CONTENT);

    private static final ByteArray BYTE_BUFFER_ARRAY = ByteArray.copyFrom(ByteArrayTest.BYTE_BUFFER_CONTENT);

    private static final ByteArray ARRAY = new ByteArray(ByteString.copyFrom(ByteArrayTest.BYTES_CONTENT));

    private static ByteArray streamArray;

    @Test
    public void testCopyFromString() throws IOException {
        Assert.assertEquals(ByteArrayTest.STRING_CONTENT, ByteArrayTest.STRING_ARRAY.toStringUtf8());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteArrayTest.STRING_ARRAY.toByteArray());
        Assert.assertEquals(ByteArrayTest.BYTE_BUFFER_CONTENT.asReadOnlyBuffer(), ByteArrayTest.STRING_ARRAY.asReadOnlyByteBuffer());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteStreams.toByteArray(ByteArrayTest.STRING_ARRAY.asInputStream()));
    }

    @Test
    public void testCopyFromByteArray() throws IOException {
        Assert.assertEquals(ByteArrayTest.STRING_CONTENT, ByteArrayTest.BYTES_ARRAY.toStringUtf8());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteArrayTest.BYTES_ARRAY.toByteArray());
        Assert.assertEquals(ByteArrayTest.BYTE_BUFFER_CONTENT.asReadOnlyBuffer(), ByteArrayTest.BYTES_ARRAY.asReadOnlyByteBuffer());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteStreams.toByteArray(ByteArrayTest.BYTES_ARRAY.asInputStream()));
    }

    @Test
    public void testCopyFromByteBuffer() throws IOException {
        Assert.assertEquals(ByteArrayTest.STRING_CONTENT, ByteArrayTest.BYTE_BUFFER_ARRAY.toStringUtf8());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteArrayTest.BYTE_BUFFER_ARRAY.toByteArray());
        Assert.assertEquals(ByteArrayTest.BYTE_BUFFER_CONTENT.asReadOnlyBuffer(), ByteArrayTest.BYTE_BUFFER_ARRAY.asReadOnlyByteBuffer());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteStreams.toByteArray(ByteArrayTest.BYTE_BUFFER_ARRAY.asInputStream()));
    }

    @Test
    public void testCopyFromStream() throws IOException {
        Assert.assertEquals(ByteArrayTest.STRING_CONTENT, ByteArrayTest.streamArray.toStringUtf8());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteArrayTest.streamArray.toByteArray());
        Assert.assertEquals(ByteArrayTest.BYTE_BUFFER_CONTENT.asReadOnlyBuffer(), ByteArrayTest.streamArray.asReadOnlyByteBuffer());
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteStreams.toByteArray(ByteArrayTest.streamArray.asInputStream()));
    }

    @Test
    public void testLength() {
        Assert.assertEquals(ByteArrayTest.BYTES_CONTENT.length, ByteArrayTest.ARRAY.length());
    }

    @Test
    public void testToStringUtf8() {
        Assert.assertEquals(ByteArrayTest.STRING_CONTENT, ByteArrayTest.ARRAY.toStringUtf8());
    }

    @Test
    public void testToByteArray() {
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteArrayTest.ARRAY.toByteArray());
    }

    @Test
    public void testAsReadOnlyByteBuffer() {
        Assert.assertEquals(ByteArrayTest.BYTE_BUFFER_CONTENT.asReadOnlyBuffer(), ByteArrayTest.ARRAY.asReadOnlyByteBuffer());
    }

    @Test
    public void testAsInputStream() throws IOException {
        Assert.assertArrayEquals(ByteArrayTest.BYTES_CONTENT, ByteStreams.toByteArray(ByteArrayTest.ARRAY.asInputStream()));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(ByteArrayTest.STRING_ARRAY.hashCode(), ByteArrayTest.BYTES_ARRAY.hashCode());
        Assert.assertEquals(ByteArrayTest.BYTES_ARRAY.hashCode(), ByteArrayTest.BYTE_BUFFER_ARRAY.hashCode());
        Assert.assertEquals(ByteArrayTest.BYTE_BUFFER_ARRAY.hashCode(), ByteArrayTest.streamArray.hashCode());
    }
}

