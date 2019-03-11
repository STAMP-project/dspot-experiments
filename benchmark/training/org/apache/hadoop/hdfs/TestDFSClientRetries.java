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
import DFSClient.LOG;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import HdfsClientConfigKeys.BlockWrite.LOCATEFOLLOWINGBLOCK_INITIAL_DELAY_MS_KEY;
import HdfsClientConfigKeys.BlockWrite.LOCATEFOLLOWINGBLOCK_MAX_DELAY_MS_KEY;
import HdfsClientConfigKeys.BlockWrite.LOCATEFOLLOWINGBLOCK_RETRIES_KEY;
import HdfsClientConfigKeys.DFS_CLIENT_MAX_BLOCK_ACQUIRE_FAILURES_KEY;
import HdfsClientConfigKeys.DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.apache.hadoop.hdfs.client.impl.LeaseRenewer;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ClientDatanodeProtocol;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.namenode.NotReplicatedYetException;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * These tests make sure that DFSClient retries fetching data from DFS
 * properly in case of errors.
 */
public class TestDFSClientRetries {
    private static final String ADDRESS = "0.0.0.0";

    private static final int PING_INTERVAL = 1000;

    private static final int MIN_SLEEP_TIME = 1000;

    public static final Logger LOG = LoggerFactory.getLogger(TestDFSClientRetries.class.getName());

    private static Configuration conf = null;

    private static class TestServer extends Server {
        private boolean sleep;

        private Class<? extends Writable> responseClass;

        public TestServer(int handlerCount, boolean sleep) throws IOException {
            this(handlerCount, sleep, LongWritable.class, null);
        }

        public TestServer(int handlerCount, boolean sleep, Class<? extends Writable> paramClass, Class<? extends Writable> responseClass) throws IOException {
            super(TestDFSClientRetries.ADDRESS, 0, paramClass, handlerCount, TestDFSClientRetries.conf);
            this.sleep = sleep;
            this.responseClass = responseClass;
        }

        @Override
        public Writable call(RPC.RpcKind rpcKind, String protocol, Writable param, long receiveTime) throws IOException {
            if (sleep) {
                // sleep a bit
                try {
                    Thread.sleep(((TestDFSClientRetries.PING_INTERVAL) + (TestDFSClientRetries.MIN_SLEEP_TIME)));
                } catch (InterruptedException e) {
                }
            }
            if ((responseClass) != null) {
                try {
                    return responseClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                return param;// echo param as result

            }
        }
    }

    /**
     * This makes sure that when DN closes clients socket after client had
     * successfully connected earlier, the data can still be fetched.
     */
    @Test
    public void testWriteTimeoutAtDataNode() throws IOException, InterruptedException {
        final int writeTimeout = 100;// milliseconds.

        // set a very short write timeout for datanode, so that tests runs fast.
        TestDFSClientRetries.conf.setInt(DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY, writeTimeout);
        // set a smaller block size
        final int blockSize = (10 * 1024) * 1024;
        TestDFSClientRetries.conf.setInt(DFS_BLOCK_SIZE_KEY, blockSize);
        TestDFSClientRetries.conf.setInt(DFS_CLIENT_MAX_BLOCK_ACQUIRE_FAILURES_KEY, 1);
        // set a small buffer size
        final int bufferSize = 4096;
        TestDFSClientRetries.conf.setInt(IO_FILE_BUFFER_SIZE_KEY, bufferSize);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).numDataNodes(3).build();
        try {
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            Path filePath = new Path("/testWriteTimeoutAtDataNode");
            OutputStream out = fs.create(filePath, true, bufferSize);
            // write a 2 block file.
            TestDFSClientRetries.writeData(out, (2 * blockSize));
            out.close();
            byte[] buf = new byte[1024 * 1024];// enough to empty TCP buffers.

            InputStream in = fs.open(filePath, bufferSize);
            // first read a few bytes
            IOUtils.readFully(in, buf, 0, (bufferSize / 2));
            // now read few more chunks of data by sleeping in between :
            for (int i = 0; i < 10; i++) {
                Thread.sleep((2 * writeTimeout));// force write timeout at the datanode.

                // read enough to empty out socket buffers.
                IOUtils.readFully(in, buf, 0, buf.length);
            }
            // successfully read with write timeout on datanodes.
            in.close();
        } finally {
            cluster.shutdown();
        }
    }

    // more tests related to different failure cases can be added here.
    /**
     * Verify that client will correctly give up after the specified number
     * of times trying to add a block
     */
    @SuppressWarnings({ "serial", "unchecked" })
    @Test
    public void testNotYetReplicatedErrors() throws IOException {
        final String exceptionMsg = "Nope, not replicated yet...";
        final int maxRetries = 1;// Allow one retry (total of two calls)

        TestDFSClientRetries.conf.setInt(LOCATEFOLLOWINGBLOCK_RETRIES_KEY, maxRetries);
        NamenodeProtocols mockNN = Mockito.mock(NamenodeProtocols.class);
        Answer<Object> answer = new ThrowsException(new IOException()) {
            int retryCount = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                (retryCount)++;
                System.out.println((("addBlock has been called " + (retryCount)) + " times"));
                // First call was not a retry
                if ((retryCount) > (maxRetries + 1))
                    throw new IOException(("Retried too many times: " + (retryCount)));
                else
                    throw new RemoteException(NotReplicatedYetException.class.getName(), exceptionMsg);

            }
        };
        Mockito.when(mockNN.addBlock(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(answer);
        Mockito.doReturn(new HdfsFileStatus.Builder().replication(1).blocksize(1024).perm(new FsPermission(((short) (777)))).owner("owner").group("group").symlink(new byte[0]).fileId(1010).build()).when(mockNN).getFileInfo(ArgumentMatchers.anyString());
        Mockito.doReturn(new HdfsFileStatus.Builder().replication(1).blocksize(1024).perm(new FsPermission(((short) (777)))).owner("owner").group("group").symlink(new byte[0]).fileId(1010).build()).when(mockNN).create(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        final DFSClient client = new DFSClient(null, mockNN, TestDFSClientRetries.conf, null);
        OutputStream os = client.create("testfile", true);
        os.write(20);// write one random byte

        try {
            os.close();
        } catch (Exception e) {
            Assert.assertTrue(("Retries are not being stopped correctly: " + (e.getMessage())), e.getMessage().equals(exceptionMsg));
        }
    }

    /**
     * This tests that DFSInputStream failures are counted for a given read
     * operation, and not over the lifetime of the stream. It is a regression
     * test for HDFS-127.
     */
    @Test
    public void testFailuresArePerOperation() throws Exception {
        long fileSize = 4096;
        Path file = new Path("/testFile");
        // Set short retry timeouts so this test runs faster
        TestDFSClientRetries.conf.setInt(WINDOW_BASE_KEY, 10);
        TestDFSClientRetries.conf.setInt(HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY, (2 * 1000));
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).build();
        try {
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            NamenodeProtocols preSpyNN = cluster.getNameNodeRpc();
            NamenodeProtocols spyNN = Mockito.spy(preSpyNN);
            DFSClient client = new DFSClient(null, spyNN, TestDFSClientRetries.conf, null);
            int maxBlockAcquires = client.getConf().getMaxBlockAcquireFailures();
            Assert.assertTrue((maxBlockAcquires > 0));
            /* seed */
            DFSTestUtil.createFile(fs, file, fileSize, ((short) (1)), 12345L);
            // If the client will retry maxBlockAcquires times, then if we fail
            // any more than that number of times, the operation should entirely
            // fail.
            Mockito.doAnswer(new TestDFSClientRetries.FailNTimesAnswer(preSpyNN, (maxBlockAcquires + 1))).when(spyNN).getBlockLocations(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
            try {
                IOUtils.copyBytes(client.open(file.toString()), new IOUtils.NullOutputStream(), TestDFSClientRetries.conf, true);
                Assert.fail("Didn't get exception");
            } catch (IOException ioe) {
                DFSClient.LOG.info("Got expected exception", ioe);
            }
            // If we fail exactly that many times, then it should succeed.
            Mockito.doAnswer(new TestDFSClientRetries.FailNTimesAnswer(preSpyNN, maxBlockAcquires)).when(spyNN).getBlockLocations(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
            IOUtils.copyBytes(client.open(file.toString()), new IOUtils.NullOutputStream(), TestDFSClientRetries.conf, true);
            DFSClient.LOG.info("Starting test case for failure reset");
            // Now the tricky case - if we fail a few times on one read, then succeed,
            // then fail some more on another read, it shouldn't fail.
            Mockito.doAnswer(new TestDFSClientRetries.FailNTimesAnswer(preSpyNN, maxBlockAcquires)).when(spyNN).getBlockLocations(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
            DFSInputStream is = client.open(file.toString());
            byte[] buf = new byte[10];
            IOUtils.readFully(is, buf, 0, buf.length);
            DFSClient.LOG.info("First read successful after some failures.");
            // Further reads at this point will succeed since it has the good block locations.
            // So, force the block locations on this stream to be refreshed from bad info.
            // When reading again, it should start from a fresh failure count, since
            // we're starting a new operation on the user level.
            Mockito.doAnswer(new TestDFSClientRetries.FailNTimesAnswer(preSpyNN, maxBlockAcquires)).when(spyNN).getBlockLocations(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
            is.openInfo(true);
            // Seek to beginning forces a reopen of the BlockReader - otherwise it'll
            // just keep reading on the existing stream and the fact that we've poisoned
            // the block info won't do anything.
            is.seek(0);
            IOUtils.readFully(is, buf, 0, buf.length);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test DFSClient can continue to function after renewLease RPC
     * receives SocketTimeoutException.
     */
    @Test
    public void testLeaseRenewSocketTimeout() throws Exception {
        String file1 = "/testFile1";
        String file2 = "/testFile2";
        // Set short retry timeouts so this test runs faster
        TestDFSClientRetries.conf.setInt(WINDOW_BASE_KEY, 10);
        TestDFSClientRetries.conf.setInt(HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY, (2 * 1000));
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).build();
        try {
            cluster.waitActive();
            NamenodeProtocols spyNN = Mockito.spy(cluster.getNameNodeRpc());
            Mockito.doThrow(new SocketTimeoutException()).when(spyNN).renewLease(Mockito.anyString());
            DFSClient client = new DFSClient(null, spyNN, TestDFSClientRetries.conf, null);
            // Get hold of the lease renewer instance used by the client
            LeaseRenewer leaseRenewer = client.getLeaseRenewer();
            leaseRenewer.setRenewalTime(100);
            OutputStream out1 = client.create(file1, false);
            Mockito.verify(spyNN, Mockito.timeout(10000).times(1)).renewLease(Mockito.anyString());
            verifyEmptyLease(leaseRenewer);
            try {
                out1.write(new byte[256]);
                Assert.fail("existing output stream should be aborted");
            } catch (IOException e) {
            }
            // Verify DFSClient can do read operation after renewLease aborted.
            client.exists(file2);
            // Verify DFSClient can do write operation after renewLease no longer
            // throws SocketTimeoutException.
            Mockito.doNothing().when(spyNN).renewLease(Mockito.anyString());
            leaseRenewer = client.getLeaseRenewer();
            leaseRenewer.setRenewalTime(100);
            OutputStream out2 = client.create(file2, false);
            Mockito.verify(spyNN, Mockito.timeout(10000).times(2)).renewLease(Mockito.anyString());
            out2.write(new byte[256]);
            out2.close();
            verifyEmptyLease(leaseRenewer);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test that getAdditionalBlock() and close() are idempotent. This allows
     * a client to safely retry a call and still produce a correct
     * file. See HDFS-3031.
     */
    @Test
    public void testIdempotentAllocateBlockAndClose() throws Exception {
        final String src = "/testIdempotentAllocateBlock";
        Path file = new Path(src);
        TestDFSClientRetries.conf.setInt(DFS_BLOCK_SIZE_KEY, 4096);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).build();
        try {
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            NamenodeProtocols preSpyNN = cluster.getNameNodeRpc();
            NamenodeProtocols spyNN = Mockito.spy(preSpyNN);
            DFSClient client = new DFSClient(null, spyNN, TestDFSClientRetries.conf, null);
            // Make the call to addBlock() get called twice, as if it were retried
            // due to an IPC issue.
            Mockito.doAnswer(new Answer<LocatedBlock>() {
                private int getBlockCount(LocatedBlock ret) throws IOException {
                    LocatedBlocks lb = cluster.getNameNodeRpc().getBlockLocations(src, 0, Long.MAX_VALUE);
                    Assert.assertEquals(lb.getLastLocatedBlock().getBlock(), ret.getBlock());
                    return lb.getLocatedBlocks().size();
                }

                @Override
                public LocatedBlock answer(InvocationOnMock invocation) throws Throwable {
                    TestDFSClientRetries.LOG.info(("Called addBlock: " + (Arrays.toString(invocation.getArguments()))));
                    // call first time
                    // warp NotReplicatedYetException with RemoteException as rpc does.
                    final LocatedBlock ret;
                    try {
                        ret = ((LocatedBlock) (invocation.callRealMethod()));
                    } catch (NotReplicatedYetException e) {
                        throw new RemoteException(e.getClass().getName(), e.getMessage());
                    }
                    final int blockCount = getBlockCount(ret);
                    // Retrying should result in a new block at the end of the file.
                    // (abandoning the old one)
                    // It should not have NotReplicatedYetException.
                    final LocatedBlock ret2;
                    try {
                        ret2 = ((LocatedBlock) (invocation.callRealMethod()));
                    } catch (NotReplicatedYetException e) {
                        throw new AssertionError("Unexpected exception", e);
                    }
                    final int blockCount2 = getBlockCount(ret2);
                    // We shouldn't have gained an extra block by the RPC.
                    Assert.assertEquals(blockCount, blockCount2);
                    return ret2;
                }
            }).when(spyNN).addBlock(Mockito.anyString(), Mockito.anyString(), Mockito.<ExtendedBlock>any(), Mockito.<DatanodeInfo[]>any(), Mockito.anyLong(), Mockito.<String[]>any(), Mockito.<EnumSet<AddBlockFlag>>any());
            Mockito.doAnswer(new Answer<Boolean>() {
                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    // complete() may return false a few times before it returns
                    // true. We want to wait until it returns true, and then
                    // make it retry one more time after that.
                    TestDFSClientRetries.LOG.info("Called complete:");
                    if (!((Boolean) (invocation.callRealMethod()))) {
                        TestDFSClientRetries.LOG.info("Complete call returned false, not faking a retry RPC");
                        return false;
                    }
                    // We got a successful close. Call it again to check idempotence.
                    try {
                        boolean ret = ((Boolean) (invocation.callRealMethod()));
                        TestDFSClientRetries.LOG.info((("Complete call returned true, faked second RPC. " + "Returned: ") + ret));
                        return ret;
                    } catch (Throwable t) {
                        TestDFSClientRetries.LOG.error("Idempotent retry threw exception", t);
                        throw t;
                    }
                }
            }).when(spyNN).complete(Mockito.anyString(), Mockito.anyString(), Mockito.<ExtendedBlock>any(), ArgumentMatchers.anyLong());
            OutputStream stm = client.create(file.toString(), true);
            try {
                AppendTestUtil.write(stm, 0, 10000);
                stm.close();
                stm = null;
            } finally {
                IOUtils.cleanupWithLogger(TestDFSClientRetries.LOG, stm);
            }
            // Make sure the mock was actually properly injected.
            Mockito.verify(spyNN, Mockito.atLeastOnce()).addBlock(Mockito.anyString(), Mockito.anyString(), Mockito.<ExtendedBlock>any(), Mockito.<DatanodeInfo[]>any(), Mockito.anyLong(), Mockito.<String[]>any(), Mockito.<EnumSet<AddBlockFlag>>any());
            Mockito.verify(spyNN, Mockito.atLeastOnce()).complete(Mockito.anyString(), Mockito.anyString(), Mockito.<ExtendedBlock>any(), ArgumentMatchers.anyLong());
            AppendTestUtil.check(fs, file, 10000);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Mock Answer implementation of NN.getBlockLocations that will return
     * a poisoned block list a certain number of times before returning
     * a proper one.
     */
    private static class FailNTimesAnswer implements Answer<LocatedBlocks> {
        private int failuresLeft;

        private final NamenodeProtocols realNN;

        public FailNTimesAnswer(NamenodeProtocols preSpyNN, int timesToFail) {
            failuresLeft = timesToFail;
            this.realNN = preSpyNN;
        }

        @Override
        public LocatedBlocks answer(InvocationOnMock invocation) throws IOException {
            Object[] args = invocation.getArguments();
            LocatedBlocks realAnswer = realNN.getBlockLocations(((String) (args[0])), ((Long) (args[1])), ((Long) (args[2])));
            if (((failuresLeft)--) > 0) {
                NameNode.LOG.info("FailNTimesAnswer injecting failure.");
                return makeBadBlockList(realAnswer);
            }
            NameNode.LOG.info("FailNTimesAnswer no longer failing.");
            return realAnswer;
        }

        private LocatedBlocks makeBadBlockList(LocatedBlocks goodBlockList) {
            LocatedBlock goodLocatedBlock = goodBlockList.get(0);
            LocatedBlock badLocatedBlock = new LocatedBlock(goodLocatedBlock.getBlock(), new DatanodeInfo[]{ DFSTestUtil.getDatanodeInfo("1.2.3.4", "bogus", 1234) });
            badLocatedBlock.setStartOffset(goodLocatedBlock.getStartOffset());
            List<LocatedBlock> badBlocks = new ArrayList<LocatedBlock>();
            badBlocks.add(badLocatedBlock);
            return new LocatedBlocks(goodBlockList.getFileLength(), false, badBlocks, null, true, null, null);
        }
    }

    /**
     * Test that a DFSClient waits for random time before retry on busy blocks.
     */
    @Test
    public void testDFSClientRetriesOnBusyBlocks() throws IOException {
        System.out.println("Testing DFSClient random waiting on busy blocks.");
        // 
        // Test settings:
        // 
        // xcievers    fileLen   #clients  timeWindow    #retries
        // ========    =======   ========  ==========    ========
        // Test 1:          2       6 MB         50      300 ms           3
        // Test 2:          2       6 MB         50      300 ms          50
        // Test 3:          2       6 MB         50     1000 ms           3
        // Test 4:          2       6 MB         50     1000 ms          50
        // 
        // Minimum xcievers is 2 since 1 thread is reserved for registry.
        // Test 1 & 3 may fail since # retries is low.
        // Test 2 & 4 should never fail since (#threads)/(xcievers-1) is the upper
        // bound for guarantee to not throw BlockMissingException.
        // 
        int xcievers = 2;
        int fileLen = (6 * 1024) * 1024;
        int threads = 50;
        int retries = 3;
        int timeWin = 300;
        // 
        // Test 1: might fail
        // 
        long timestamp = Time.now();
        boolean pass = busyTest(xcievers, threads, fileLen, timeWin, retries);
        long timestamp2 = Time.now();
        if (pass) {
            TestDFSClientRetries.LOG.info((("Test 1 succeeded! Time spent: " + ((timestamp2 - timestamp) / 1000.0)) + " sec."));
        } else {
            TestDFSClientRetries.LOG.warn((("Test 1 failed, but relax. Time spent: " + ((timestamp2 - timestamp) / 1000.0)) + " sec."));
        }
        // 
        // Test 2: should never fail
        // 
        retries = 50;
        timestamp = Time.now();
        pass = busyTest(xcievers, threads, fileLen, timeWin, retries);
        timestamp2 = Time.now();
        Assert.assertTrue("Something wrong! Test 2 got Exception with maxmum retries!", pass);
        TestDFSClientRetries.LOG.info((("Test 2 succeeded! Time spent: " + ((timestamp2 - timestamp) / 1000.0)) + " sec."));
        // 
        // Test 3: might fail
        // 
        retries = 3;
        timeWin = 1000;
        timestamp = Time.now();
        pass = busyTest(xcievers, threads, fileLen, timeWin, retries);
        timestamp2 = Time.now();
        if (pass) {
            TestDFSClientRetries.LOG.info((("Test 3 succeeded! Time spent: " + ((timestamp2 - timestamp) / 1000.0)) + " sec."));
        } else {
            TestDFSClientRetries.LOG.warn((("Test 3 failed, but relax. Time spent: " + ((timestamp2 - timestamp) / 1000.0)) + " sec."));
        }
        // 
        // Test 4: should never fail
        // 
        retries = 50;
        timeWin = 1000;
        timestamp = Time.now();
        pass = busyTest(xcievers, threads, fileLen, timeWin, retries);
        timestamp2 = Time.now();
        Assert.assertTrue("Something wrong! Test 4 got Exception with maxmum retries!", pass);
        TestDFSClientRetries.LOG.info((("Test 4 succeeded! Time spent: " + ((timestamp2 - timestamp) / 1000.0)) + " sec."));
    }

    class DFSClientReader implements Runnable {
        DFSClient client;

        final Configuration conf;

        final byte[] expected_sha;

        FileSystem fs;

        final Path filePath;

        final MiniDFSCluster cluster;

        final int len;

        final TestDFSClientRetries.Counter counter;

        DFSClientReader(Path file, MiniDFSCluster cluster, byte[] hash_sha, int fileLen, TestDFSClientRetries.Counter cnt) {
            filePath = file;
            this.cluster = cluster;
            counter = cnt;
            len = fileLen;
            conf = new HdfsConfiguration();
            expected_sha = hash_sha;
            try {
                cluster.waitActive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                fs = cluster.getNewFileSystemInstance(0);
                int bufferSize = len;
                byte[] buf = new byte[bufferSize];
                InputStream in = fs.open(filePath, bufferSize);
                // read the whole file
                IOUtils.readFully(in, buf, 0, bufferSize);
                // compare with the expected input
                MessageDigest m = MessageDigest.getInstance("SHA");
                m.update(buf, 0, bufferSize);
                byte[] hash_sha = m.digest();
                buf = null;// GC if needed since there may be too many threads

                in.close();
                fs.close();
                Assert.assertTrue("hashed keys are not the same size", ((hash_sha.length) == (expected_sha.length)));
                Assert.assertTrue("hashed keys are not equal", Arrays.equals(hash_sha, expected_sha));
                counter.inc();// count this thread as successful

                TestDFSClientRetries.LOG.info("Thread correctly read the block.");
            } catch (BlockMissingException e) {
                TestDFSClientRetries.LOG.info("Bad - BlockMissingException is caught.");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Counter {
        int counter;

        Counter(int n) {
            counter = n;
        }

        public synchronized void inc() {
            ++(counter);
        }

        public int get() {
            return counter;
        }
    }

    @Test
    public void testGetFileChecksum() throws Exception {
        final String f = "/testGetFileChecksum";
        final Path p = new Path(f);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).numDataNodes(3).build();
        try {
            cluster.waitActive();
            // create a file
            final FileSystem fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, p, (1L << 20), ((short) (3)), 20100402L);
            // get checksum
            final FileChecksum cs1 = fs.getFileChecksum(p);
            Assert.assertTrue((cs1 != null));
            // stop the first datanode
            final List<LocatedBlock> locatedblocks = DFSClient.callGetBlockLocations(cluster.getNameNodeRpc(), f, 0, Long.MAX_VALUE).getLocatedBlocks();
            final DatanodeInfo first = locatedblocks.get(0).getLocations()[0];
            cluster.stopDataNode(first.getXferAddr());
            // get checksum again
            final FileChecksum cs2 = fs.getFileChecksum(p);
            Assert.assertEquals(cs1, cs2);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test that timeout occurs when DN does not respond to RPC.
     * Start up a server and ask it to sleep for n seconds. Make an
     * RPC to the server and set rpcTimeout to less than n and ensure
     * that socketTimeoutException is obtained
     */
    @Test
    public void testClientDNProtocolTimeout() throws IOException {
        final Server server = new TestDFSClientRetries.TestServer(1, true);
        server.start();
        final InetSocketAddress addr = NetUtils.getConnectAddress(server);
        DatanodeID fakeDnId = DFSTestUtil.getLocalDatanodeID(addr.getPort());
        ExtendedBlock b = new ExtendedBlock("fake-pool", new Block(12345L));
        LocatedBlock fakeBlock = new LocatedBlock(b, new DatanodeInfo[0]);
        ClientDatanodeProtocol proxy = null;
        try {
            proxy = DFSUtilClient.createClientDatanodeProtocolProxy(fakeDnId, TestDFSClientRetries.conf, 500, false, fakeBlock);
            proxy.getReplicaVisibleLength(new ExtendedBlock("bpid", 1));
            Assert.fail("Did not get expected exception: SocketTimeoutException");
        } catch (SocketTimeoutException e) {
            TestDFSClientRetries.LOG.info("Got the expected Exception: SocketTimeoutException");
        } finally {
            if (proxy != null) {
                RPC.stopProxy(proxy);
            }
            server.stop();
        }
    }

    /**
     * Test that checksum failures are recovered from by the next read on the same
     * DFSInputStream. Corruption information is not persisted from read call to
     * read call, so the client should expect consecutive calls to behave the same
     * way. See HDFS-3067.
     */
    @Test
    public void testRetryOnChecksumFailure() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            final short REPL_FACTOR = 1;
            final long FILE_LENGTH = 512L;
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            Path path = new Path("/corrupted");
            DFSTestUtil.createFile(fs, path, FILE_LENGTH, REPL_FACTOR, 12345L);
            DFSTestUtil.waitReplication(fs, path, REPL_FACTOR);
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, path);
            int blockFilesCorrupted = cluster.corruptBlockOnDataNodes(block);
            Assert.assertEquals("All replicas not corrupted", REPL_FACTOR, blockFilesCorrupted);
            InetSocketAddress nnAddr = new InetSocketAddress("localhost", cluster.getNameNodePort());
            DFSClient client = new DFSClient(nnAddr, conf);
            DFSInputStream dis = client.open(path.toString());
            byte[] arr = new byte[((int) (FILE_LENGTH))];
            for (int i = 0; i < 2; ++i) {
                try {
                    dis.read(arr, 0, ((int) (FILE_LENGTH)));
                    Assert.fail("Expected ChecksumException not thrown");
                } catch (ChecksumException ex) {
                    GenericTestUtils.assertExceptionContains("Checksum", ex);
                }
            }
            client.close();
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test client retry with namenode restarting.
     */
    @Test(timeout = 300000)
    public void testNamenodeRestart() throws Exception {
        TestDFSClientRetries.namenodeRestartTest(new Configuration(), false);
    }

    @Test
    public void testMultipleLinearRandomRetry() {
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "11");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "11,22,33");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "11,22,33,44,55");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "AA");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "11,AA");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "11,22,33,FF");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "11,-22");
        TestDFSClientRetries.parseMultipleLinearRandomRetry(null, "-11,22");
        TestDFSClientRetries.parseMultipleLinearRandomRetry("[22x11ms]", "11,22");
        TestDFSClientRetries.parseMultipleLinearRandomRetry("[22x11ms, 44x33ms]", "11,22,33,44");
        TestDFSClientRetries.parseMultipleLinearRandomRetry("[22x11ms, 44x33ms, 66x55ms]", "11,22,33,44,55,66");
        TestDFSClientRetries.parseMultipleLinearRandomRetry("[22x11ms, 44x33ms, 66x55ms]", "   11,   22, 33,  44, 55,  66   ");
    }

    /**
     * Tests default configuration values and configuration setting
     * of locate following block delays and number of retries.
     *
     * Configuration values tested:
     * - dfs.client.block.write.locateFollowingBlock.initial.delay.ms
     * - dfs.client.block.write.locateFollowingBlock.max.delay.ms
     * - dfs.client.block.write.locateFollowingBlock.retries
     */
    @Test
    public void testDFSClientConfigurationLocateFollowingBlock() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).build();
        final int initialDelayTestValue = 1000;
        final int maxDelayTestValue = 35000;
        final int retryTestValue = 7;
        final int defaultInitialDelay = 400;
        final int defaultMaxDelay = 60000;
        final int defultRetry = 5;
        try {
            cluster.waitActive();
            NamenodeProtocols nn = cluster.getNameNodeRpc();
            DFSClient client = new DFSClient(null, nn, TestDFSClientRetries.conf, null);
            Assert.assertEquals(defaultInitialDelay, client.getConf().getBlockWriteLocateFollowingInitialDelayMs());
            Assert.assertEquals(defaultMaxDelay, client.getConf().getBlockWriteLocateFollowingMaxDelayMs());
            Assert.assertEquals(defultRetry, client.getConf().getNumBlockWriteLocateFollowingRetry());
            TestDFSClientRetries.conf.setInt(LOCATEFOLLOWINGBLOCK_INITIAL_DELAY_MS_KEY, initialDelayTestValue);
            TestDFSClientRetries.conf.setInt(LOCATEFOLLOWINGBLOCK_MAX_DELAY_MS_KEY, maxDelayTestValue);
            TestDFSClientRetries.conf.setInt(LOCATEFOLLOWINGBLOCK_RETRIES_KEY, retryTestValue);
            client = new DFSClient(null, nn, TestDFSClientRetries.conf, null);
            Assert.assertEquals(initialDelayTestValue, client.getConf().getBlockWriteLocateFollowingInitialDelayMs());
            Assert.assertEquals(maxDelayTestValue, client.getConf().getBlockWriteLocateFollowingMaxDelayMs());
            Assert.assertEquals(retryTestValue, client.getConf().getNumBlockWriteLocateFollowingRetry());
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 120000)
    public void testLeaseRenewAndDFSOutputStreamDeadLock() throws Exception {
        CountDownLatch testLatch = new CountDownLatch(1);
        DFSClientFaultInjector.set(new DFSClientFaultInjector() {
            public void delayWhenRenewLeaseTimeout() {
                try {
                    testLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        String file1 = "/testFile1";
        // Set short retry timeouts so this test runs faster
        TestDFSClientRetries.conf.setInt(HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY, 1000);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestDFSClientRetries.conf).build();
        try {
            cluster.waitActive();
            final NamenodeProtocols spyNN = Mockito.spy(cluster.getNameNodeRpc());
            Mockito.doAnswer(new TestDFSClientRetries.SleepFixedTimeAnswer(1500, testLatch)).when(spyNN).complete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ExtendedBlock.class), ArgumentMatchers.anyLong());
            DFSClient client = new DFSClient(null, spyNN, TestDFSClientRetries.conf, null);
            // Get hold of the lease renewer instance used by the client
            LeaseRenewer leaseRenewer = client.getLeaseRenewer();
            leaseRenewer.setRenewalTime(100);
            final OutputStream out1 = client.create(file1, false);
            out1.write(new byte[256]);
            Thread closeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 1. trigger get LeaseRenewer lock
                        Mockito.doThrow(new SocketTimeoutException()).when(spyNN).renewLease(Mockito.anyString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            closeThread.start();
            // 2. trigger get DFSOutputStream lock
            out1.close();
        } finally {
            cluster.shutdown();
        }
    }

    private static class SleepFixedTimeAnswer implements Answer<Object> {
        private final int sleepTime;

        private final CountDownLatch testLatch;

        SleepFixedTimeAnswer(int sleepTime, CountDownLatch latch) {
            this.sleepTime = sleepTime;
            this.testLatch = latch;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            boolean interrupted = false;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {
                interrupted = true;
            }
            try {
                return invocation.callRealMethod();
            } finally {
                testLatch.countDown();
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

