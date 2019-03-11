/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;


public class ByteBufferInputStreamTest extends ZKTestCase {
    private static final byte[] DATA_BYTES_0 = "Apache ZooKeeper".getBytes(Charset.forName("UTF-8"));

    private static byte[] DATA_BYTES;

    private ByteBuffer bb;

    private ByteBufferInputStream in;

    private byte[] bs;

    @Test
    public void testRead() throws Exception {
        for (int i = 0; i < (ByteBufferInputStreamTest.DATA_BYTES.length); i++) {
            int b = in.read();
            Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[i], ((byte) (b)));
        }
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void testReadArrayOffsetLength() throws Exception {
        Assert.assertEquals(1, in.read(bs, 2, 1));
        byte[] expected = new byte[]{ ((byte) (1)), ((byte) (2)), ByteBufferInputStreamTest.DATA_BYTES[0], ((byte) (4)) };
        Assert.assertArrayEquals(expected, bs);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadArrayOffsetLength_LengthTooLarge() throws Exception {
        in.read(bs, 2, 3);
    }

    @Test
    public void testReadArrayOffsetLength_HitEndOfStream() throws Exception {
        for (int i = 0; i < ((ByteBufferInputStreamTest.DATA_BYTES.length) - 1); i++) {
            in.read();
        }
        Assert.assertEquals(1, in.read(bs, 2, 2));
        byte[] expected = new byte[]{ ((byte) (1)), ((byte) (2)), ByteBufferInputStreamTest.DATA_BYTES[((ByteBufferInputStreamTest.DATA_BYTES.length) - 1)], ((byte) (4)) };
        Assert.assertArrayEquals(expected, bs);
    }

    @Test
    public void testReadArrayOffsetLength_AtEndOfStream() throws Exception {
        for (int i = 0; i < (ByteBufferInputStreamTest.DATA_BYTES.length); i++) {
            in.read();
        }
        byte[] expected = Arrays.copyOf(bs, bs.length);
        Assert.assertEquals((-1), in.read(bs, 2, 2));
        Assert.assertArrayEquals(expected, bs);
    }

    @Test
    public void testReadArrayOffsetLength_0Length() throws Exception {
        byte[] expected = Arrays.copyOf(bs, bs.length);
        Assert.assertEquals(0, in.read(bs, 2, 0));
        Assert.assertArrayEquals(expected, bs);
    }

    @Test
    public void testReadArray() throws Exception {
        byte[] expected = Arrays.copyOf(ByteBufferInputStreamTest.DATA_BYTES, 4);
        Assert.assertEquals(4, in.read(bs));
        Assert.assertArrayEquals(expected, bs);
    }

    @Test
    public void testSkip() throws Exception {
        in.read();
        Assert.assertEquals(2L, in.skip(2L));
        Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[3], in.read());
        Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[4], in.read());
    }

    @Test
    public void testSkip2() throws Exception {
        for (int i = 0; i < ((ByteBufferInputStreamTest.DATA_BYTES.length) / 2); i++) {
            in.read();
        }
        long skipAmount = (ByteBufferInputStreamTest.DATA_BYTES.length) / 4;
        Assert.assertEquals(skipAmount, in.skip(skipAmount));
        int idx = ((ByteBufferInputStreamTest.DATA_BYTES.length) / 2) + ((int) (skipAmount));
        Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[(idx++)], in.read());
        Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[(idx++)], in.read());
    }

    @Test
    public void testNegativeSkip() throws Exception {
        in.read();
        Assert.assertEquals(0L, in.skip((-2L)));
        Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[1], in.read());
        Assert.assertEquals(ByteBufferInputStreamTest.DATA_BYTES[2], in.read());
    }

    @Test
    public void testSkip_HitEnd() throws Exception {
        for (int i = 0; i < ((ByteBufferInputStreamTest.DATA_BYTES.length) - 1); i++) {
            in.read();
        }
        Assert.assertEquals(1L, in.skip(2L));
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void testSkip_AtEnd() throws Exception {
        for (int i = 0; i < (ByteBufferInputStreamTest.DATA_BYTES.length); i++) {
            in.read();
        }
        Assert.assertEquals(0L, in.skip(2L));
        Assert.assertEquals((-1), in.read());
    }

    @Test
    public void testAvailable() throws Exception {
        for (int i = ByteBufferInputStreamTest.DATA_BYTES.length; i > 0; i--) {
            Assert.assertEquals(i, in.available());
            in.read();
        }
        Assert.assertEquals(0, in.available());
    }
}

