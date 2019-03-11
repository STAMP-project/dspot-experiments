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
package org.apache.geode.internal.cache;


import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.geode.internal.cache.entries.DiskEntry.Helper.Flushable;
import org.apache.geode.internal.cache.entries.DiskEntry.Helper.OffHeapValueWrapper;
import org.junit.Assert;
import org.junit.Test;


public class OffHeapValueWrapperJUnitTest {
    @Test
    public void testIsSerialized() {
        Assert.assertEquals(true, createChunkValueWrapper(new byte[16], true).isSerialized());
        Assert.assertEquals(false, createChunkValueWrapper(new byte[16], false).isSerialized());
    }

    @Test
    public void testGetUserBits() {
        Assert.assertEquals(((byte) (1)), createChunkValueWrapper(new byte[16], true).getUserBits());
        Assert.assertEquals(((byte) (0)), createChunkValueWrapper(new byte[16], false).getUserBits());
    }

    @Test
    public void testGetLength() {
        Assert.assertEquals(32, createChunkValueWrapper(new byte[32], true).getLength());
        Assert.assertEquals(17, createChunkValueWrapper(new byte[17], false).getLength());
    }

    @Test
    public void testGetBytesAsString() {
        Assert.assertEquals("byte[0, 0, 0, 0, 0, 0, 0, 0]", createChunkValueWrapper(new byte[8], false).getBytesAsString());
    }

    @Test
    public void testSendTo() throws IOException {
        final ByteBuffer bb = ByteBuffer.allocateDirect(18);
        bb.limit(8);
        OffHeapValueWrapper vw = createChunkValueWrapper(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, false);
        vw.sendTo(bb, new Flushable() {
            @Override
            public void flush() throws IOException {
                Assert.fail("should not have been called");
            }

            @Override
            public void flush(ByteBuffer bb, ByteBuffer chunkbb) throws IOException {
                Assert.fail("should not have been called");
            }
        });
        Assert.assertEquals(8, bb.position());
        bb.flip();
        Assert.assertEquals(1, bb.get());
        Assert.assertEquals(2, bb.get());
        Assert.assertEquals(3, bb.get());
        Assert.assertEquals(4, bb.get());
        Assert.assertEquals(5, bb.get());
        Assert.assertEquals(6, bb.get());
        Assert.assertEquals(7, bb.get());
        Assert.assertEquals(8, bb.get());
        bb.clear();
        bb.limit(8);
        vw = createChunkValueWrapper(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 }, false);
        final int[] flushCalls = new int[1];
        vw.sendTo(bb, new Flushable() {
            @Override
            public void flush() throws IOException {
                if ((flushCalls[0]) != 0) {
                    Assert.fail("expected flush to only be called once");
                }
                (flushCalls[0])++;
                Assert.assertEquals(8, bb.position());
                for (int i = 0; i < 8; i++) {
                    Assert.assertEquals((i + 1), bb.get(i));
                }
                bb.clear();
                bb.limit(8);
            }

            @Override
            public void flush(ByteBuffer bb, ByteBuffer chunkbb) throws IOException {
                Assert.fail("should not have been called");
            }
        });
        Assert.assertEquals(1, bb.position());
        bb.flip();
        Assert.assertEquals(9, bb.get());
        bb.clear();
        bb.limit(8);
        flushCalls[0] = 0;
        vw = createChunkValueWrapper(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }, false);
        vw.sendTo(bb, new Flushable() {
            @Override
            public void flush() throws IOException {
                if ((flushCalls[0]) > 1) {
                    Assert.fail("expected flush to only be called twice");
                }
                Assert.assertEquals(8, bb.position());
                for (int i = 0; i < 8; i++) {
                    Assert.assertEquals(((((flushCalls[0]) * 8) + i) + 1), bb.get(i));
                }
                (flushCalls[0])++;
                bb.clear();
                bb.limit(8);
            }

            @Override
            public void flush(ByteBuffer bb, ByteBuffer chunkbb) throws IOException {
                Assert.fail("should not have been called");
            }
        });
        Assert.assertEquals(1, bb.position());
        bb.flip();
        Assert.assertEquals(17, bb.get());
        // now test with a chunk that will not fit in bb.
        bb.clear();
        flushCalls[0] = 0;
        bb.put(((byte) (0)));
        vw = createChunkValueWrapper(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }, false);
        vw.sendTo(bb, new Flushable() {
            @Override
            public void flush() throws IOException {
                Assert.fail("should not have been called");
            }

            @Override
            public void flush(ByteBuffer bb, ByteBuffer chunkbb) throws IOException {
                (flushCalls[0])++;
                Assert.assertEquals(1, bb.position());
                bb.flip();
                Assert.assertEquals(0, bb.get());
                Assert.assertEquals(19, chunkbb.remaining());
                for (int i = 1; i <= 19; i++) {
                    Assert.assertEquals(i, chunkbb.get());
                }
            }
        });
        Assert.assertEquals(1, flushCalls[0]);
    }
}

