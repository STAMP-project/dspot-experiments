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
package org.apache.hadoop.hbase.ipc;


import java.nio.ByteBuffer;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.io.ByteBufferPool;
import org.apache.hadoop.hbase.ipc.RpcServer.CallCleanup;
import org.apache.hadoop.hbase.nio.ByteBuff;
import org.apache.hadoop.hbase.nio.MultiByteBuff;
import org.apache.hadoop.hbase.nio.SingleByteBuff;
import org.apache.hadoop.hbase.testclassification.RPCTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RPCTests.class, SmallTests.class })
public class TestRpcServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRpcServer.class);

    @Test
    public void testAllocateByteBuffToReadInto() throws Exception {
        int maxBuffersInPool = 10;
        ByteBufferPool pool = new ByteBufferPool((6 * 1024), maxBuffersInPool);
        initPoolWithAllBuffers(pool, maxBuffersInPool);
        ByteBuff buff = null;
        Pair<ByteBuff, CallCleanup> pair;
        // When the request size is less than 1/6th of the pool buffer size. We should use on demand
        // created on heap Buffer
        pair = RpcServer.allocateByteBuffToReadInto(pool, RpcServer.getMinSizeForReservoirUse(pool), 200);
        buff = pair.getFirst();
        Assert.assertTrue(buff.hasArray());
        Assert.assertEquals(maxBuffersInPool, pool.getQueueSize());
        Assert.assertNull(pair.getSecond());
        // When the request size is > 1/6th of the pool buffer size.
        pair = RpcServer.allocateByteBuffToReadInto(pool, RpcServer.getMinSizeForReservoirUse(pool), 1024);
        buff = pair.getFirst();
        Assert.assertFalse(buff.hasArray());
        Assert.assertEquals((maxBuffersInPool - 1), pool.getQueueSize());
        Assert.assertNotNull(pair.getSecond());
        pair.getSecond().run();// CallCleanup#run should put back the BB to pool.

        Assert.assertEquals(maxBuffersInPool, pool.getQueueSize());
        // Request size> pool buffer size
        pair = RpcServer.allocateByteBuffToReadInto(pool, RpcServer.getMinSizeForReservoirUse(pool), (7 * 1024));
        buff = pair.getFirst();
        Assert.assertFalse(buff.hasArray());
        Assert.assertTrue((buff instanceof MultiByteBuff));
        ByteBuffer[] bbs = getEnclosingByteBuffers();
        Assert.assertEquals(2, bbs.length);
        Assert.assertTrue(bbs[0].isDirect());
        Assert.assertTrue(bbs[1].isDirect());
        Assert.assertEquals((6 * 1024), bbs[0].limit());
        Assert.assertEquals(1024, bbs[1].limit());
        Assert.assertEquals((maxBuffersInPool - 2), pool.getQueueSize());
        Assert.assertNotNull(pair.getSecond());
        pair.getSecond().run();// CallCleanup#run should put back the BB to pool.

        Assert.assertEquals(maxBuffersInPool, pool.getQueueSize());
        pair = RpcServer.allocateByteBuffToReadInto(pool, RpcServer.getMinSizeForReservoirUse(pool), ((6 * 1024) + 200));
        buff = pair.getFirst();
        Assert.assertFalse(buff.hasArray());
        Assert.assertTrue((buff instanceof MultiByteBuff));
        bbs = getEnclosingByteBuffers();
        Assert.assertEquals(2, bbs.length);
        Assert.assertTrue(bbs[0].isDirect());
        Assert.assertFalse(bbs[1].isDirect());
        Assert.assertEquals((6 * 1024), bbs[0].limit());
        Assert.assertEquals(200, bbs[1].limit());
        Assert.assertEquals((maxBuffersInPool - 1), pool.getQueueSize());
        Assert.assertNotNull(pair.getSecond());
        pair.getSecond().run();// CallCleanup#run should put back the BB to pool.

        Assert.assertEquals(maxBuffersInPool, pool.getQueueSize());
        ByteBuffer[] buffers = new ByteBuffer[maxBuffersInPool - 1];
        for (int i = 0; i < (maxBuffersInPool - 1); i++) {
            buffers[i] = pool.getBuffer();
        }
        pair = RpcServer.allocateByteBuffToReadInto(pool, RpcServer.getMinSizeForReservoirUse(pool), (20 * 1024));
        buff = pair.getFirst();
        Assert.assertFalse(buff.hasArray());
        Assert.assertTrue((buff instanceof MultiByteBuff));
        bbs = getEnclosingByteBuffers();
        Assert.assertEquals(2, bbs.length);
        Assert.assertTrue(bbs[0].isDirect());
        Assert.assertFalse(bbs[1].isDirect());
        Assert.assertEquals((6 * 1024), bbs[0].limit());
        Assert.assertEquals((14 * 1024), bbs[1].limit());
        Assert.assertEquals(0, pool.getQueueSize());
        Assert.assertNotNull(pair.getSecond());
        pair.getSecond().run();// CallCleanup#run should put back the BB to pool.

        Assert.assertEquals(1, pool.getQueueSize());
        pool.getBuffer();
        pair = RpcServer.allocateByteBuffToReadInto(pool, RpcServer.getMinSizeForReservoirUse(pool), (7 * 1024));
        buff = pair.getFirst();
        Assert.assertTrue(buff.hasArray());
        Assert.assertTrue((buff instanceof SingleByteBuff));
        Assert.assertEquals((7 * 1024), getEnclosingByteBuffer().limit());
        Assert.assertNull(pair.getSecond());
    }
}

