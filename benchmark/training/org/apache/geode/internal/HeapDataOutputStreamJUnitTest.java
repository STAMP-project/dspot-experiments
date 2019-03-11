/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal;


import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.geode.DataSerializer;
import org.apache.geode.internal.cache.UnitTestValueHolder;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static HeapDataOutputStream.MIN_TO_COPY;
import static Version.CURRENT;


/**
 * Test of methods on HeapDataOutputStream
 *
 * TODO right now this just tests the new write(ByteBuffer) method. We might want to add some unit
 * tests for the existing methods.
 */
@Category({ SerializationTest.class })
public class HeapDataOutputStreamJUnitTest {
    @Test
    public void testWriteByteBuffer() {
        HeapDataOutputStream out = new HeapDataOutputStream(64, CURRENT);
        byte[] bytes = "1234567890qwertyuiopasdfghjklzxcvbnm,./;'".getBytes();
        out.write(ByteBuffer.wrap(bytes, 0, 2));
        out.write(ByteBuffer.wrap(bytes, 2, ((bytes.length) - 2)));
        ByteBuffer unused = out.finishWritingAndReturnUnusedBuffer();
        Assert.assertEquals((64 - (bytes.length)), unused.capacity());
        Assert.assertEquals(0, unused.position());
        Assert.assertEquals(unused.capacity(), unused.limit());
        byte[] actual = out.toByteArray();
        Assert.assertEquals(new String(bytes), new String(actual));
    }

    @Test
    public void testWriteByteBufferCopyUseBuffer() {
        ByteBuffer buf = ByteBuffer.allocate(32);
        HeapDataOutputStream out = new HeapDataOutputStream(buf, CURRENT, true);
        out.write(0);
        byte[] bytes = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
        out.write(ByteBuffer.wrap(bytes));
        out.write(34);
        ByteBuffer unused = out.finishWritingAndReturnUnusedBuffer();
        Assert.assertEquals(((buf.capacity()) - 3), unused.capacity());
        Assert.assertEquals(0, unused.position());
        Assert.assertEquals(unused.capacity(), unused.limit());
        int bbCount = out.getByteBufferCount();
        Assert.assertEquals(2, bbCount);
        ByteBuffer[] bbs = new ByteBuffer[bbCount];
        out.fillByteBufferArray(bbs, 0);
        byte[] bbsBytes = new byte[out.size()];
        ByteBuffer tmp = ByteBuffer.wrap(bbsBytes);
        for (int i = 0; i < bbCount; i++) {
            tmp.put(bbs[i]);
        }
        tmp.flip();
        byte[] expectedBytes = new byte[(bytes.length) + 2];
        ByteBuffer expected = ByteBuffer.wrap(expectedBytes);
        expected.put(((byte) (0)));
        expected.put(bytes);
        expected.put(((byte) (34)));
        expected.flip();
        Assert.assertEquals(expected, tmp);
    }

    @Test
    public void testWriteByteBufferNoCopyUseBuffer() {
        ByteBuffer buf = ByteBuffer.allocate(32);
        HeapDataOutputStream out = new HeapDataOutputStream(buf, CURRENT, true);
        out.write(0);
        byte[] bytes = new byte[(MIN_TO_COPY) + 1];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) ((i + 1) % 128));
        }
        ByteBuffer bytesBB = ByteBuffer.wrap(bytes);
        out.write(bytesBB);
        out.write(((byte) (((bytes.length) + 1) % 128)));
        ByteBuffer unused = out.finishWritingAndReturnUnusedBuffer();
        Assert.assertEquals(((buf.capacity()) - 2), unused.capacity());
        Assert.assertEquals(0, unused.position());
        Assert.assertEquals(unused.capacity(), unused.limit());
        int bbCount = out.getByteBufferCount();
        Assert.assertEquals(3, bbCount);
        ByteBuffer[] bbs = new ByteBuffer[bbCount];
        out.fillByteBufferArray(bbs, 0);
        Assert.assertEquals(true, ((bbs[1]) == bytesBB));
        byte[] bbsBytes = new byte[out.size()];
        ByteBuffer tmp = ByteBuffer.wrap(bbsBytes);
        for (int i = 0; i < bbCount; i++) {
            tmp.put(bbs[i]);
        }
        tmp.flip();
        byte[] expectedBytes = new byte[(bytes.length) + 2];
        for (int i = 0; i < (expectedBytes.length); i++) {
            expectedBytes[i] = ((byte) (i % 128));
        }
        ByteBuffer expected = ByteBuffer.wrap(expectedBytes);
        Assert.assertEquals(expected, tmp);
    }

    @Test
    public void testWriteJavaSerializeNoCopy() throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[2000];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = ((byte) (i));
        }
        UnitTestValueHolder vh = new UnitTestValueHolder(bytes);
        ByteBuffer buf = ByteBuffer.allocate(32);
        HeapDataOutputStream out = new HeapDataOutputStream(buf, CURRENT, true);
        DataSerializer.writeObject(vh, out);
        UnitTestValueHolder vh2 = DataSerializer.readObject(new DataInputStream(out.getInputStream()));
        if (!(Arrays.equals(bytes, ((byte[]) (vh2.getValue()))))) {
            Assert.fail(((("expected " + (Arrays.toString(bytes))) + " but found ") + (Arrays.toString(((byte[]) (vh2.getValue()))))));
        }
    }
}

