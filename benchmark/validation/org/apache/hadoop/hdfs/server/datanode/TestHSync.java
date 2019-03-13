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
package org.apache.hadoop.hdfs.server.datanode;


import CompressionType.NONE;
import CreateFlag.CREATE;
import CreateFlag.OVERWRITE;
import CreateFlag.SYNC_BLOCK;
import RandomDatum.Generator;
import java.util.EnumSet;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.RandomDatum;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.junit.Test;


public class TestHSync {
    /**
     * Test basic hsync cases
     */
    @Test
    public void testHSync() throws Exception {
        testHSyncOperation(false);
    }

    @Test
    public void testHSyncWithAppend() throws Exception {
        testHSyncOperation(true);
    }

    /**
     * Test hsync on an exact block boundary
     */
    @Test
    public void testHSyncBlockBoundary() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        final FileSystem fs = cluster.getFileSystem();
        final Path p = new Path("/testHSyncBlockBoundary/foo");
        final int len = 1 << 16;
        final byte[] fileContents = AppendTestUtil.initBuffer(len);
        FSDataOutputStream out = fs.create(p, FsPermission.getDefault(), EnumSet.of(CREATE, OVERWRITE, SYNC_BLOCK), 4096, ((short) (1)), len, null);
        // fill exactly one block (tests the SYNC_BLOCK case) and flush
        out.write(fileContents, 0, len);
        out.hflush();
        // the full block should have caused a sync
        checkSyncMetric(cluster, 1);
        out.hsync();
        // first on block again
        checkSyncMetric(cluster, 1);
        // write one more byte and sync again
        out.write(1);
        out.hsync();
        checkSyncMetric(cluster, 2);
        out.close();
        checkSyncMetric(cluster, 3);
        cluster.shutdown();
    }

    /**
     * Test hsync via SequenceFiles
     */
    @Test
    public void testSequenceFileSync() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        final FileSystem fs = cluster.getFileSystem();
        final Path p = new Path("/testSequenceFileSync/foo");
        final int len = 1 << 16;
        FSDataOutputStream out = fs.create(p, FsPermission.getDefault(), EnumSet.of(CREATE, OVERWRITE, SYNC_BLOCK), 4096, ((short) (1)), len, null);
        Writer w = SequenceFile.createWriter(new Configuration(), Writer.stream(out), Writer.keyClass(RandomDatum.class), Writer.valueClass(RandomDatum.class), Writer.compression(NONE, new DefaultCodec()));
        w.hflush();
        checkSyncMetric(cluster, 0);
        w.hsync();
        checkSyncMetric(cluster, 1);
        int seed = new Random().nextInt();
        RandomDatum.Generator generator = new RandomDatum.Generator(seed);
        generator.next();
        w.append(generator.getKey(), generator.getValue());
        w.hsync();
        checkSyncMetric(cluster, 2);
        w.close();
        checkSyncMetric(cluster, 2);
        out.close();
        checkSyncMetric(cluster, 3);
        cluster.shutdown();
    }

    /**
     * Test that syncBlock is correctly performed at replicas
     */
    @Test
    public void testHSyncWithReplication() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        final FileSystem fs = cluster.getFileSystem();
        final Path p = new Path("/testHSyncWithReplication/foo");
        final int len = 1 << 16;
        FSDataOutputStream out = fs.create(p, FsPermission.getDefault(), EnumSet.of(CREATE, OVERWRITE, SYNC_BLOCK), 4096, ((short) (3)), len, null);
        out.write(1);
        out.hflush();
        checkSyncMetric(cluster, 0, 0);
        checkSyncMetric(cluster, 1, 0);
        checkSyncMetric(cluster, 2, 0);
        out.hsync();
        checkSyncMetric(cluster, 0, 1);
        checkSyncMetric(cluster, 1, 1);
        checkSyncMetric(cluster, 2, 1);
        out.hsync();
        checkSyncMetric(cluster, 0, 2);
        checkSyncMetric(cluster, 1, 2);
        checkSyncMetric(cluster, 2, 2);
        cluster.shutdown();
    }
}

