/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.datastore;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class BlobTest {
    private static final byte[] bytes1 = new byte[10];

    private static final byte[] bytes2 = new byte[11];

    private Blob blob1;

    private Blob blob2;

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(blob1, blob1);
        Assert.assertEquals(blob1, Blob.copyFrom(BlobTest.bytes1));
        Assert.assertNotEquals(blob1, blob2);
    }

    @Test
    public void testLength() throws Exception {
        Assert.assertEquals(BlobTest.bytes1.length, blob1.getLength());
        Assert.assertEquals(BlobTest.bytes2.length, blob2.getLength());
    }

    @Test
    public void testToByteArray() throws Exception {
        Assert.assertArrayEquals(BlobTest.bytes1, blob1.toByteArray());
        Assert.assertArrayEquals(BlobTest.bytes2, blob2.toByteArray());
    }

    @Test
    public void testAsReadOnlyByteBuffer() throws Exception {
        ByteBuffer buffer = blob1.asReadOnlyByteBuffer();
        byte[] bytes = new byte[BlobTest.bytes1.length];
        buffer.get(bytes);
        Assert.assertFalse(buffer.hasRemaining());
        Assert.assertArrayEquals(BlobTest.bytes1, bytes);
    }

    @Test
    public void testAsInputStream() throws Exception {
        byte[] bytes = new byte[BlobTest.bytes1.length];
        InputStream in = blob1.asInputStream();
        Assert.assertEquals(BlobTest.bytes1.length, in.read(bytes));
        Assert.assertEquals((-1), in.read());
        Assert.assertArrayEquals(BlobTest.bytes1, bytes);
    }

    @Test
    public void testCopyTo() throws Exception {
        byte[] bytes = new byte[BlobTest.bytes1.length];
        blob1.copyTo(bytes);
        Assert.assertArrayEquals(BlobTest.bytes1, bytes);
        ByteBuffer buffer = ByteBuffer.allocate(BlobTest.bytes1.length);
        blob1.copyTo(buffer);
        buffer.flip();
        bytes = new byte[BlobTest.bytes1.length];
        buffer.get(bytes);
        Assert.assertFalse(buffer.hasRemaining());
        Assert.assertArrayEquals(BlobTest.bytes1, bytes);
    }

    @Test
    public void testCopyFrom() throws Exception {
        Blob blob = Blob.copyFrom(ByteBuffer.wrap(BlobTest.bytes1));
        Assert.assertEquals(blob1, blob);
        Assert.assertArrayEquals(BlobTest.bytes1, blob.toByteArray());
        blob = Blob.copyFrom(new ByteArrayInputStream(BlobTest.bytes2));
        Assert.assertEquals(blob2, blob);
        Assert.assertArrayEquals(BlobTest.bytes2, blob.toByteArray());
    }
}

