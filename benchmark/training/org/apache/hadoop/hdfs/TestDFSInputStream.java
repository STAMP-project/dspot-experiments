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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_DOMAIN_SOCKET_PATH_KEY;
import HdfsClientConfigKeys.Read.ShortCircuit.KEY;
import Retry.TIMES_GET_LAST_BLOCK_LENGTH_KEY;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.impl.DfsClientConf;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.net.unix.DomainSocket;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static DFSInputStream.tcpReadsDisabledForTesting;


public class TestDFSInputStream {
    @Test(timeout = 60000)
    public void testSkipWithRemoteBlockReader() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            testSkipInner(cluster);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testSkipWithRemoteBlockReader2() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            testSkipInner(cluster);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testSkipWithLocalBlockReader() throws IOException {
        Assume.assumeThat(DomainSocket.getLoadingFailureReason(), CoreMatchers.equalTo(null));
        TemporarySocketDirectory sockDir = new TemporarySocketDirectory();
        DomainSocket.disableBindPathValidation();
        Configuration conf = new Configuration();
        conf.setBoolean(KEY, true);
        conf.set(DFS_DOMAIN_SOCKET_PATH_KEY, new File(sockDir.getDir(), "TestShortCircuitLocalRead._PORT.sock").getAbsolutePath());
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        try {
            tcpReadsDisabledForTesting = true;
            testSkipInner(cluster);
        } finally {
            tcpReadsDisabledForTesting = false;
            cluster.shutdown();
            sockDir.close();
        }
    }

    @Test(timeout = 60000)
    public void testSeekToNewSource() throws IOException {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        DistributedFileSystem fs = cluster.getFileSystem();
        Path path = new Path("/testfile");
        DFSTestUtil.createFile(fs, path, 1024, ((short) (3)), 0);
        DFSInputStream fin = fs.dfs.open("/testfile");
        try {
            fin.seekToNewSource(100);
            Assert.assertEquals(100, fin.getPos());
            DatanodeInfo firstNode = fin.getCurrentDatanode();
            Assert.assertNotNull(firstNode);
            fin.seekToNewSource(100);
            Assert.assertEquals(100, fin.getPos());
            Assert.assertFalse(firstNode.equals(fin.getCurrentDatanode()));
        } finally {
            fin.close();
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testOpenInfo() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(TIMES_GET_LAST_BLOCK_LENGTH_KEY, 0);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
        cluster.waitActive();
        try {
            DistributedFileSystem fs = cluster.getFileSystem();
            int chunkSize = 512;
            Random r = new Random(12345L);
            byte[] data = new byte[chunkSize];
            r.nextBytes(data);
            Path file = new Path("/testfile");
            try (FSDataOutputStream fout = fs.create(file)) {
                fout.write(data);
            }
            DfsClientConf dcconf = new DfsClientConf(conf);
            int retryTimesForGetLastBlockLength = dcconf.getRetryTimesForGetLastBlockLength();
            Assert.assertEquals(0, retryTimesForGetLastBlockLength);
            try (DFSInputStream fin = fs.dfs.open("/testfile")) {
                long flen = fin.getFileLength();
                Assert.assertEquals(chunkSize, flen);
                long lastBlockBeingWrittenLength = fin.getlastBlockBeingWrittenLengthForTesting();
                Assert.assertEquals(0, lastBlockBeingWrittenLength);
            }
        } finally {
            cluster.shutdown();
        }
    }
}

