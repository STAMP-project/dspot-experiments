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
package org.apache.hadoop.hdfs.server.namenode;


import java.util.Iterator;
import java.util.Random;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.BlockProto;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Validate fixed-size block partitioning.
 */
public class TestFixedBlockResolver {
    @Rule
    public TestName name = new TestName();

    private final FixedBlockResolver blockId = new FixedBlockResolver();

    @Test
    public void testExactBlock() throws Exception {
        FileStatus f = file(512, 256);
        int nblocks = 0;
        for (BlockProto b : blockId.resolve(f)) {
            ++nblocks;
            Assert.assertEquals((512L * (1L << 20)), b.getNumBytes());
        }
        Assert.assertEquals(1, nblocks);
        FileStatus g = file(1024, 256);
        nblocks = 0;
        for (BlockProto b : blockId.resolve(g)) {
            ++nblocks;
            Assert.assertEquals((512L * (1L << 20)), b.getNumBytes());
        }
        Assert.assertEquals(2, nblocks);
        FileStatus h = file(5120, 256);
        nblocks = 0;
        for (BlockProto b : blockId.resolve(h)) {
            ++nblocks;
            Assert.assertEquals((512L * (1L << 20)), b.getNumBytes());
        }
        Assert.assertEquals(10, nblocks);
    }

    @Test
    public void testEmpty() throws Exception {
        FileStatus f = file(0, 100);
        Iterator<BlockProto> b = blockId.resolve(f).iterator();
        Assert.assertTrue(b.hasNext());
        Assert.assertEquals(0, b.next().getNumBytes());
        Assert.assertFalse(b.hasNext());
    }

    @Test
    public void testRandomFile() throws Exception {
        Random r = new Random();
        long seed = r.nextLong();
        System.out.println(("seed: " + seed));
        r.setSeed(seed);
        int len = (r.nextInt(4096)) + 512;
        int blk = (r.nextInt((len - 128))) + 128;
        FileStatus s = file(len, blk);
        long nbytes = 0;
        for (BlockProto b : blockId.resolve(s)) {
            nbytes += b.getNumBytes();
            Assert.assertTrue(((512L * (1L << 20)) >= (b.getNumBytes())));
        }
        Assert.assertEquals(s.getLen(), nbytes);
    }
}

