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


import DFSClient.LOG;
import DFSConfigKeys.DFS_BLOCKREPORT_INTERVAL_MSEC_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JUnit test for corrupted file handling.
 * This test creates a bunch of files/directories with replication
 * factor of 2. Then verifies that a client can automatically
 * access the remaining valid replica inspite of the following
 * types of simulated errors:
 *
 *  1. Delete meta file on one replica
 *  2. Truncates meta file on one replica
 *  3. Corrupts the meta file header on one replica
 *  4. Corrupts any random offset and portion of the meta file
 *  5. Swaps two meta files, i.e the format of the meta files
 *     are valid but their CRCs do not match with their corresponding
 *     data blocks
 * The above tests are run for varied values of dfs.bytes-per-checksum
 * and dfs.blocksize. It tests for the case when the meta file is
 * multiple blocks.
 *
 * Another portion of the test is commented out till HADOOP-1557
 * is addressed:
 *  1. Create file with 2 replica, corrupt the meta file of replica,
 *     decrease replication factor from 2 to 1. Validate that the
 *     remaining replica is the good one.
 *  2. Create file with 2 replica, corrupt the meta file of one replica,
 *     increase replication factor of file to 3. verify that the new
 *     replica was created from the non-corrupted replica.
 */
public class TestCrcCorruption {
    public static final Logger LOG = LoggerFactory.getLogger(TestCrcCorruption.class);

    private DFSClientFaultInjector faultInjector;

    /**
     * Test case for data corruption during data transmission for
     * create/write. To recover from corruption while writing, at
     * least two replicas are needed.
     */
    @Test(timeout = 50000)
    public void testCorruptionDuringWrt() throws Exception {
        Configuration conf = new HdfsConfiguration();
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(10).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            Path file = new Path("/test_corruption_file");
            FSDataOutputStream out = fs.create(file, true, 8192, ((short) (3)), ((long) ((128 * 1024) * 1024)));
            byte[] data = new byte[65536];
            for (int i = 0; i < 65536; i++) {
                data[i] = ((byte) (i % 256));
            }
            for (int i = 0; i < 5; i++) {
                out.write(data, 0, 65535);
            }
            out.hflush();
            // corrupt the packet once
            Mockito.when(faultInjector.corruptPacket()).thenReturn(true, false);
            Mockito.when(faultInjector.uncorruptPacket()).thenReturn(true, false);
            for (int i = 0; i < 5; i++) {
                out.write(data, 0, 65535);
            }
            out.close();
            // read should succeed
            FSDataInputStream in = fs.open(file);
            for (int c; (c = in.read()) != (-1););
            in.close();
            // test the retry limit
            out = fs.create(file, true, 8192, ((short) (3)), ((long) ((128 * 1024) * 1024)));
            // corrupt the packet once and never fix it.
            Mockito.when(faultInjector.corruptPacket()).thenReturn(true, false);
            Mockito.when(faultInjector.uncorruptPacket()).thenReturn(false);
            // the client should give up pipeline reconstruction after retries.
            try {
                for (int i = 0; i < 5; i++) {
                    out.write(data, 0, 65535);
                }
                out.close();
                Assert.fail("Write did not fail");
            } catch (IOException ioe) {
                // we should get an ioe
                DFSClient.LOG.info("Got expected exception", ioe);
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
            Mockito.when(faultInjector.corruptPacket()).thenReturn(false);
            Mockito.when(faultInjector.uncorruptPacket()).thenReturn(false);
        }
    }

    @Test
    public void testCrcCorruption() throws Exception {
        // 
        // default parameters
        // 
        System.out.println("TestCrcCorruption with default parameters");
        Configuration conf1 = new HdfsConfiguration();
        conf1.setInt(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, (3 * 1000));
        DFSTestUtil util1 = new DFSTestUtil.Builder().setName("TestCrcCorruption").setNumFiles(40).build();
        thistest(conf1, util1);
        // 
        // specific parameters
        // 
        System.out.println("TestCrcCorruption with specific parameters");
        Configuration conf2 = new HdfsConfiguration();
        conf2.setInt(DFS_BYTES_PER_CHECKSUM_KEY, 17);
        conf2.setInt(DFS_BLOCK_SIZE_KEY, 34);
        DFSTestUtil util2 = new DFSTestUtil.Builder().setName("TestCrcCorruption").setNumFiles(40).setMaxSize(400).build();
        thistest(conf2, util2);
    }

    /**
     * Make a single-DN cluster, corrupt a block, and make sure
     * there's no infinite loop, but rather it eventually
     * reports the exception to the client.
     */
    // 5 min timeout
    @Test(timeout = 300000)
    public void testEntirelyCorruptFileOneNode() throws Exception {
        doTestEntirelyCorruptFile(1);
    }

    /**
     * Same thing with multiple datanodes - in history, this has
     * behaved differently than the above.
     *
     * This test usually completes in around 15 seconds - if it
     * times out, this suggests that the client is retrying
     * indefinitely.
     */
    // 5 min timeout
    @Test(timeout = 300000)
    public void testEntirelyCorruptFileThreeNodes() throws Exception {
        doTestEntirelyCorruptFile(3);
    }
}

