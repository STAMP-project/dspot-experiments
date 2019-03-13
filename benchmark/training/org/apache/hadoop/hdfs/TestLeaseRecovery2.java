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
import DataNode.LOG;
import HdfsConstants.LEASE_HARDLIMIT_PERIOD;
import HdfsConstants.LEASE_SOFTLIMIT_PERIOD;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.AlreadyBeingCreatedException;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestLeaseRecovery2 {
    public static final Logger LOG = LoggerFactory.getLogger(TestLeaseRecovery2.class);

    {
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(LeaseManager.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(FSNamesystem.LOG, Level.TRACE);
    }

    private static final long BLOCK_SIZE = 1024;

    private static final int FILE_SIZE = ((int) (TestLeaseRecovery2.BLOCK_SIZE)) * 2;

    static final short REPLICATION_NUM = ((short) (3));

    static final byte[] buffer = new byte[TestLeaseRecovery2.FILE_SIZE];

    private static final String fakeUsername = "fakeUser1";

    private static final String fakeGroup = "supergroup";

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    private static final Configuration conf = new HdfsConfiguration();

    private static final int BUF_SIZE = TestLeaseRecovery2.conf.getInt(IO_FILE_BUFFER_SIZE_KEY, 4096);

    private static final long SHORT_LEASE_PERIOD = 1000L;

    private static final long LONG_LEASE_PERIOD = (60 * 60) * (TestLeaseRecovery2.SHORT_LEASE_PERIOD);

    /**
     * Test the NameNode's revoke lease on current lease holder function.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testImmediateRecoveryOfLease() throws Exception {
        // create a file
        // write bytes into the file.
        byte[] actual = new byte[TestLeaseRecovery2.FILE_SIZE];
        int size = AppendTestUtil.nextInt(TestLeaseRecovery2.FILE_SIZE);
        Path filepath = createFile("/immediateRecoverLease-shortlease", size, true);
        // set the soft limit to be 1 second so that the
        // namenode triggers lease recovery on next attempt to write-for-open.
        TestLeaseRecovery2.cluster.setLeasePeriod(TestLeaseRecovery2.SHORT_LEASE_PERIOD, TestLeaseRecovery2.LONG_LEASE_PERIOD);
        recoverLeaseUsingCreate(filepath);
        verifyFile(TestLeaseRecovery2.dfs, filepath, actual, size);
        // test recoverLease
        // set the soft limit to be 1 hour but recoverLease should
        // close the file immediately
        TestLeaseRecovery2.cluster.setLeasePeriod(TestLeaseRecovery2.LONG_LEASE_PERIOD, TestLeaseRecovery2.LONG_LEASE_PERIOD);
        size = AppendTestUtil.nextInt(TestLeaseRecovery2.FILE_SIZE);
        filepath = createFile("/immediateRecoverLease-longlease", size, false);
        // test recoverLease from a different client
        recoverLease(filepath, null);
        verifyFile(TestLeaseRecovery2.dfs, filepath, actual, size);
        // test recoverlease from the same client
        size = AppendTestUtil.nextInt(TestLeaseRecovery2.FILE_SIZE);
        filepath = createFile("/immediateRecoverLease-sameclient", size, false);
        // create another file using the same client
        Path filepath1 = new Path(((filepath.toString()) + (AppendTestUtil.nextInt())));
        FSDataOutputStream stm = TestLeaseRecovery2.dfs.create(filepath1, true, TestLeaseRecovery2.BUF_SIZE, TestLeaseRecovery2.REPLICATION_NUM, TestLeaseRecovery2.BLOCK_SIZE);
        // recover the first file
        recoverLease(filepath, TestLeaseRecovery2.dfs);
        verifyFile(TestLeaseRecovery2.dfs, filepath, actual, size);
        // continue to write to the second file
        stm.write(TestLeaseRecovery2.buffer, 0, size);
        stm.close();
        verifyFile(TestLeaseRecovery2.dfs, filepath1, actual, size);
    }

    @Test
    public void testCloseWhileRecoverLease() throws Exception {
        // test recoverLease
        // set the soft limit to be 1 hour but recoverLease should
        // close the file immediately
        TestLeaseRecovery2.cluster.setLeasePeriod(TestLeaseRecovery2.LONG_LEASE_PERIOD, TestLeaseRecovery2.LONG_LEASE_PERIOD);
        int size = AppendTestUtil.nextInt(((int) (TestLeaseRecovery2.BLOCK_SIZE)));
        String filestr = "/testCloseWhileRecoverLease";
        AppendTestUtil.LOG.info(("filestr=" + filestr));
        Path filepath = new Path(filestr);
        FSDataOutputStream stm = TestLeaseRecovery2.dfs.create(filepath, true, TestLeaseRecovery2.BUF_SIZE, TestLeaseRecovery2.REPLICATION_NUM, TestLeaseRecovery2.BLOCK_SIZE);
        Assert.assertTrue(TestLeaseRecovery2.dfs.dfs.exists(filestr));
        // hflush file
        AppendTestUtil.LOG.info("hflush");
        stm.hflush();
        // Pause DN block report.
        // Let client recover lease, and then close the file, and then let DN
        // report blocks.
        ArrayList<DataNode> dataNodes = TestLeaseRecovery2.cluster.getDataNodes();
        for (DataNode dn : dataNodes) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, false);
        }
        TestLeaseRecovery2.LOG.info("pause IBR");
        for (DataNode dn : dataNodes) {
            DataNodeTestUtils.pauseIBR(dn);
        }
        AppendTestUtil.LOG.info(("size=" + size));
        stm.write(TestLeaseRecovery2.buffer, 0, size);
        // hflush file
        AppendTestUtil.LOG.info("hflush");
        stm.hflush();
        TestLeaseRecovery2.LOG.info("recover lease");
        TestLeaseRecovery2.dfs.recoverLease(filepath);
        try {
            stm.close();
            Assert.fail("close() should fail because the file is under recovery.");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("whereas it is under recovery", ioe);
        }
        for (DataNode dn : dataNodes) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, false);
        }
        TestLeaseRecovery2.LOG.info("trigger heartbeats");
        // resume DN block report
        for (DataNode dn : dataNodes) {
            DataNodeTestUtils.triggerHeartbeat(dn);
        }
        stm.close();
        Assert.assertEquals(TestLeaseRecovery2.cluster.getNamesystem().getBlockManager().getMissingBlocksCount(), 0);
    }

    @Test
    public void testLeaseRecoverByAnotherUser() throws Exception {
        byte[] actual = new byte[TestLeaseRecovery2.FILE_SIZE];
        TestLeaseRecovery2.cluster.setLeasePeriod(TestLeaseRecovery2.SHORT_LEASE_PERIOD, TestLeaseRecovery2.LONG_LEASE_PERIOD);
        Path filepath = createFile("/immediateRecoverLease-x", 0, true);
        recoverLeaseUsingCreate2(filepath);
        verifyFile(TestLeaseRecovery2.dfs, filepath, actual, 0);
    }

    /**
     * This test makes the client does not renew its lease and also
     * set the hard lease expiration period to be short 1s. Thus triggering
     * lease expiration to happen while the client is still alive.
     *
     * The test makes sure that the lease recovery completes and the client
     * fails if it continues to write to the file.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHardLeaseRecovery() throws Exception {
        // create a file
        String filestr = "/hardLeaseRecovery";
        AppendTestUtil.LOG.info(("filestr=" + filestr));
        Path filepath = new Path(filestr);
        FSDataOutputStream stm = TestLeaseRecovery2.dfs.create(filepath, true, TestLeaseRecovery2.BUF_SIZE, TestLeaseRecovery2.REPLICATION_NUM, TestLeaseRecovery2.BLOCK_SIZE);
        Assert.assertTrue(TestLeaseRecovery2.dfs.dfs.exists(filestr));
        // write bytes into the file.
        int size = AppendTestUtil.nextInt(TestLeaseRecovery2.FILE_SIZE);
        AppendTestUtil.LOG.info(("size=" + size));
        stm.write(TestLeaseRecovery2.buffer, 0, size);
        // hflush file
        AppendTestUtil.LOG.info("hflush");
        stm.hflush();
        // kill the lease renewal thread
        AppendTestUtil.LOG.info("leasechecker.interruptAndJoin()");
        TestLeaseRecovery2.dfs.dfs.getLeaseRenewer().interruptAndJoin();
        // set the hard limit to be 1 second
        TestLeaseRecovery2.cluster.setLeasePeriod(TestLeaseRecovery2.LONG_LEASE_PERIOD, TestLeaseRecovery2.SHORT_LEASE_PERIOD);
        // wait for lease recovery to complete
        LocatedBlocks locatedBlocks;
        do {
            Thread.sleep(TestLeaseRecovery2.SHORT_LEASE_PERIOD);
            locatedBlocks = TestLeaseRecovery2.dfs.dfs.getLocatedBlocks(filestr, 0L, size);
        } while (locatedBlocks.isUnderConstruction() );
        Assert.assertEquals(size, locatedBlocks.getFileLength());
        // make sure that the writer thread gets killed
        try {
            stm.write('b');
            stm.close();
            Assert.fail("Writer thread should have been killed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // verify data
        AppendTestUtil.LOG.info("File size is good. Now validating sizes from datanodes...");
        AppendTestUtil.checkFullFile(TestLeaseRecovery2.dfs, filepath, size, TestLeaseRecovery2.buffer, filestr);
    }

    /**
     * This test makes the client does not renew its lease and also
     * set the soft lease expiration period to be short 1s. Thus triggering
     * soft lease expiration to happen immediately by having another client
     * trying to create the same file.
     *
     * The test makes sure that the lease recovery completes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSoftLeaseRecovery() throws Exception {
        Map<String, String[]> u2g_map = new HashMap<String, String[]>(1);
        u2g_map.put(TestLeaseRecovery2.fakeUsername, new String[]{ TestLeaseRecovery2.fakeGroup });
        DFSTestUtil.updateConfWithFakeGroupMapping(TestLeaseRecovery2.conf, u2g_map);
        // Reset default lease periods
        TestLeaseRecovery2.cluster.setLeasePeriod(LEASE_SOFTLIMIT_PERIOD, LEASE_HARDLIMIT_PERIOD);
        // create a file
        // create a random file name
        String filestr = "/foo" + (AppendTestUtil.nextInt());
        AppendTestUtil.LOG.info(("filestr=" + filestr));
        Path filepath = new Path(filestr);
        FSDataOutputStream stm = TestLeaseRecovery2.dfs.create(filepath, true, TestLeaseRecovery2.BUF_SIZE, TestLeaseRecovery2.REPLICATION_NUM, TestLeaseRecovery2.BLOCK_SIZE);
        Assert.assertTrue(TestLeaseRecovery2.dfs.dfs.exists(filestr));
        // write random number of bytes into it.
        int size = AppendTestUtil.nextInt(TestLeaseRecovery2.FILE_SIZE);
        AppendTestUtil.LOG.info(("size=" + size));
        stm.write(TestLeaseRecovery2.buffer, 0, size);
        // hflush file
        AppendTestUtil.LOG.info("hflush");
        stm.hflush();
        AppendTestUtil.LOG.info("leasechecker.interruptAndJoin()");
        TestLeaseRecovery2.dfs.dfs.getLeaseRenewer().interruptAndJoin();
        // set the soft limit to be 1 second so that the
        // namenode triggers lease recovery on next attempt to write-for-open.
        TestLeaseRecovery2.cluster.setLeasePeriod(TestLeaseRecovery2.SHORT_LEASE_PERIOD, TestLeaseRecovery2.LONG_LEASE_PERIOD);
        // try to re-open the file before closing the previous handle. This
        // should fail but will trigger lease recovery.
        {
            UserGroupInformation ugi = UserGroupInformation.createUserForTesting(TestLeaseRecovery2.fakeUsername, new String[]{ TestLeaseRecovery2.fakeGroup });
            FileSystem dfs2 = DFSTestUtil.getFileSystemAs(ugi, TestLeaseRecovery2.conf);
            boolean done = false;
            for (int i = 0; (i < 10) && (!done); i++) {
                AppendTestUtil.LOG.info(("i=" + i));
                try {
                    dfs2.create(filepath, false, TestLeaseRecovery2.BUF_SIZE, TestLeaseRecovery2.REPLICATION_NUM, TestLeaseRecovery2.BLOCK_SIZE);
                    Assert.fail("Creation of an existing file should never succeed.");
                } catch (FileAlreadyExistsException ex) {
                    done = true;
                } catch (AlreadyBeingCreatedException ex) {
                    AppendTestUtil.LOG.info(("GOOD! got " + (ex.getMessage())));
                } catch (IOException ioe) {
                    AppendTestUtil.LOG.warn("UNEXPECTED IOException", ioe);
                }
                if (!done) {
                    AppendTestUtil.LOG.info((("sleep " + 5000) + "ms"));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
            Assert.assertTrue(done);
        }
        AppendTestUtil.LOG.info(((("Lease for file " + filepath) + " is recovered. ") + "Validating its contents now..."));
        // verify that file-size matches
        long fileSize = TestLeaseRecovery2.dfs.getFileStatus(filepath).getLen();
        Assert.assertTrue(((((("File should be " + size) + " bytes, but is actually ") + " found to be ") + fileSize) + " bytes"), (fileSize == size));
        // verify data
        AppendTestUtil.LOG.info(("File size is good. " + "Now validating data and sizes from datanodes..."));
        AppendTestUtil.checkFullFile(TestLeaseRecovery2.dfs, filepath, size, TestLeaseRecovery2.buffer, filestr);
    }

    /**
     * This test makes it so the client does not renew its lease and also
     * set the hard lease expiration period to be short, thus triggering
     * lease expiration to happen while the client is still alive. The test
     * also causes the NN to restart after lease recovery has begun, but before
     * the DNs have completed the blocks. This test verifies that when the NN
     * comes back up, the client no longer holds the lease.
     *
     * The test makes sure that the lease recovery completes and the client
     * fails if it continues to write to the file, even after NN restart.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testHardLeaseRecoveryAfterNameNodeRestart() throws Exception {
        hardLeaseRecoveryRestartHelper(false, (-1));
    }

    @Test(timeout = 30000)
    public void testHardLeaseRecoveryAfterNameNodeRestart2() throws Exception {
        hardLeaseRecoveryRestartHelper(false, 1535);
    }

    @Test(timeout = 30000)
    public void testHardLeaseRecoveryWithRenameAfterNameNodeRestart() throws Exception {
        hardLeaseRecoveryRestartHelper(true, (-1));
    }
}

