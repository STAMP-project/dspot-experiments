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
package org.apache.hadoop.mapred.gridmix;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileQueue {
    static final Logger LOG = LoggerFactory.getLogger(TestFileQueue.class);

    static final int NFILES = 4;

    static final int BLOCK = 256;

    static final Path[] paths = new Path[TestFileQueue.NFILES];

    static final String[] loc = new String[TestFileQueue.NFILES];

    static final long[] start = new long[TestFileQueue.NFILES];

    static final long[] len = new long[TestFileQueue.NFILES];

    @Test
    public void testRepeat() throws Exception {
        final Configuration conf = new Configuration();
        Arrays.fill(TestFileQueue.loc, "");
        Arrays.fill(TestFileQueue.start, 0L);
        Arrays.fill(TestFileQueue.len, TestFileQueue.BLOCK);
        final ByteArrayOutputStream out = TestFileQueue.fillVerif();
        final FileQueue q = new FileQueue(new org.apache.hadoop.mapreduce.lib.input.CombineFileSplit(TestFileQueue.paths, TestFileQueue.start, TestFileQueue.len, TestFileQueue.loc), conf);
        final byte[] verif = out.toByteArray();
        final byte[] check = new byte[(2 * (TestFileQueue.NFILES)) * (TestFileQueue.BLOCK)];
        q.read(check, 0, ((TestFileQueue.NFILES) * (TestFileQueue.BLOCK)));
        Assert.assertArrayEquals(verif, Arrays.copyOf(check, ((TestFileQueue.NFILES) * (TestFileQueue.BLOCK))));
        final byte[] verif2 = new byte[(2 * (TestFileQueue.NFILES)) * (TestFileQueue.BLOCK)];
        System.arraycopy(verif, 0, verif2, 0, verif.length);
        System.arraycopy(verif, 0, verif2, verif.length, verif.length);
        q.read(check, 0, ((2 * (TestFileQueue.NFILES)) * (TestFileQueue.BLOCK)));
        Assert.assertArrayEquals(verif2, check);
    }

    @Test
    public void testUneven() throws Exception {
        final Configuration conf = new Configuration();
        Arrays.fill(TestFileQueue.loc, "");
        Arrays.fill(TestFileQueue.start, 0L);
        Arrays.fill(TestFileQueue.len, TestFileQueue.BLOCK);
        final int B2 = (TestFileQueue.BLOCK) / 2;
        for (int i = 0; i < (TestFileQueue.NFILES); i += 2) {
            TestFileQueue.start[i] += B2;
            TestFileQueue.len[i] -= B2;
        }
        final FileQueue q = new FileQueue(new org.apache.hadoop.mapreduce.lib.input.CombineFileSplit(TestFileQueue.paths, TestFileQueue.start, TestFileQueue.len, TestFileQueue.loc), conf);
        final ByteArrayOutputStream out = TestFileQueue.fillVerif();
        final byte[] verif = out.toByteArray();
        final byte[] check = new byte[(((TestFileQueue.NFILES) / 2) * (TestFileQueue.BLOCK)) + (((TestFileQueue.NFILES) / 2) * B2)];
        q.read(check, 0, verif.length);
        Assert.assertArrayEquals(verif, Arrays.copyOf(check, verif.length));
        q.read(check, 0, verif.length);
        Assert.assertArrayEquals(verif, Arrays.copyOf(check, verif.length));
    }

    @Test
    public void testEmpty() throws Exception {
        final Configuration conf = new Configuration();
        // verify OK if unused
        final FileQueue q = new FileQueue(new org.apache.hadoop.mapreduce.lib.input.CombineFileSplit(new Path[0], new long[0], new long[0], new String[0]), conf);
    }
}

