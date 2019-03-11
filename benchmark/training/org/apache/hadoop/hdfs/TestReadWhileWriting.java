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


import CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import FSNamesystem.LOG;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * Test reading from hdfs while a file is being written.
 */
public class TestReadWhileWriting {
    {
        GenericTestUtils.setLogLevel(LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.TRACE);
    }

    private static final String DIR = ("/" + (TestReadWhileWriting.class.getSimpleName())) + "/";

    private static final int BLOCK_SIZE = 8192;

    // soft limit is short and hard limit is long, to test that
    // another thread can lease file after soft limit expired
    private static final long SOFT_LEASE_LIMIT = 500;

    private static final long HARD_LEASE_LIMIT = 1000 * 600;

    /**
     * Test reading while writing.
     */
    @Test
    public void pipeline_02_03() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1);
        // create cluster
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).build();
        try {
            // change the lease limits.
            cluster.setLeasePeriod(TestReadWhileWriting.SOFT_LEASE_LIMIT, TestReadWhileWriting.HARD_LEASE_LIMIT);
            // wait for the cluster
            cluster.waitActive();
            final FileSystem fs = cluster.getFileSystem();
            final Path p = new Path(TestReadWhileWriting.DIR, "file1");
            final int half = (TestReadWhileWriting.BLOCK_SIZE) / 2;
            // a. On Machine M1, Create file. Write half block of data.
            // Invoke DFSOutputStream.hflush() on the dfs file handle.
            // Do not close file yet.
            {
                final FSDataOutputStream out = fs.create(p, true, fs.getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, 4096), ((short) (3)), TestReadWhileWriting.BLOCK_SIZE);
                TestReadWhileWriting.write(out, 0, half);
                // hflush
                hflush();
            }
            // b. On another machine M2, open file and verify that the half-block
            // of data can be read successfully.
            TestReadWhileWriting.checkFile(p, half, conf);
            AppendTestUtil.LOG.info("leasechecker.interruptAndJoin()");
            ((DistributedFileSystem) (fs)).dfs.getLeaseRenewer().interruptAndJoin();
            // c. On M1, append another half block of data.  Close file on M1.
            {
                // sleep to let the lease is expired.
                Thread.sleep((2 * (TestReadWhileWriting.SOFT_LEASE_LIMIT)));
                final UserGroupInformation current = UserGroupInformation.getCurrentUser();
                final UserGroupInformation ugi = UserGroupInformation.createUserForTesting(((current.getShortUserName()) + "x"), new String[]{ "supergroup" });
                final DistributedFileSystem dfs = ugi.doAs(new PrivilegedExceptionAction<DistributedFileSystem>() {
                    @Override
                    public DistributedFileSystem run() throws Exception {
                        return ((DistributedFileSystem) (FileSystem.newInstance(conf)));
                    }
                });
                final FSDataOutputStream out = TestReadWhileWriting.append(dfs, p);
                TestReadWhileWriting.write(out, 0, half);
                out.close();
            }
            // d. On M2, open file and read 1 block of data from it. Close file.
            TestReadWhileWriting.checkFile(p, (2 * half), conf);
        } finally {
            cluster.shutdown();
        }
    }

    private static int userCount = 0;
}

