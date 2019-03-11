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


import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY;
import DataTransferProtocol.LOG;
import Policy.ALWAYS;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.datatransfer.ReplaceDatanodeOnFailure;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests that data nodes are correctly replaced on failure.
 */
public class TestReplaceDatanodeOnFailure {
    static final Logger LOG = LoggerFactory.getLogger(TestReplaceDatanodeOnFailure.class);

    static final String DIR = ("/" + (TestReplaceDatanodeOnFailure.class.getSimpleName())) + "/";

    static final short REPLICATION = 3;

    private static final String RACK0 = "/rack0";

    private static final String RACK1 = "/rack1";

    {
        GenericTestUtils.setLogLevel(DataTransferProtocol.LOG, Level.ALL);
    }

    /**
     * Test DEFAULT ReplaceDatanodeOnFailure policy.
     */
    @Test
    public void testDefaultPolicy() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        final ReplaceDatanodeOnFailure p = ReplaceDatanodeOnFailure.get(conf);
        final DatanodeInfo[] infos = new DatanodeInfo[5];
        final DatanodeInfo[][] datanodes = new DatanodeInfo[(infos.length) + 1][];
        datanodes[0] = new DatanodeInfo[0];
        for (int i = 0; i < (infos.length);) {
            infos[i] = DFSTestUtil.getLocalDatanodeInfo((9867 + i));
            i++;
            datanodes[i] = new DatanodeInfo[i];
            System.arraycopy(infos, 0, datanodes[i], 0, datanodes[i].length);
        }
        final boolean[] isAppend = new boolean[]{ true, true, false, false };
        final boolean[] isHflushed = new boolean[]{ true, false, true, false };
        for (short replication = 1; replication <= (infos.length); replication++) {
            for (int nExistings = 0; nExistings < (datanodes.length); nExistings++) {
                final DatanodeInfo[] existings = datanodes[nExistings];
                Assert.assertEquals(nExistings, existings.length);
                for (int i = 0; i < (isAppend.length); i++) {
                    for (int j = 0; j < (isHflushed.length); j++) {
                        final int half = replication / 2;
                        final boolean enoughReplica = replication <= nExistings;
                        final boolean noReplica = nExistings == 0;
                        final boolean replicationL3 = replication < 3;
                        final boolean existingsLEhalf = nExistings <= half;
                        final boolean isAH = (isAppend[i]) || (isHflushed[j]);
                        final boolean expected;
                        if ((enoughReplica || noReplica) || replicationL3) {
                            expected = false;
                        } else {
                            expected = isAH || existingsLEhalf;
                        }
                        final boolean computed = p.satisfy(replication, existings, isAppend[i], isHflushed[j]);
                        try {
                            Assert.assertEquals(expected, computed);
                        } catch (AssertionError e) {
                            final String s = (((((("replication=" + replication) + "\nnExistings =") + nExistings) + "\nisAppend   =") + (isAppend[i])) + "\nisHflushed =") + (isHflushed[j]);
                            throw new RuntimeException(s, e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Test replace datanode on failure.
     */
    @Test
    public void testReplaceDatanodeOnFailure() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        // do not consider load factor when selecting a data node
        conf.setBoolean(DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY, false);
        // always replace a datanode
        ReplaceDatanodeOnFailure.write(ALWAYS, true, conf);
        final String[] racks = new String[TestReplaceDatanodeOnFailure.REPLICATION];
        Arrays.fill(racks, TestReplaceDatanodeOnFailure.RACK0);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).racks(racks).numDataNodes(TestReplaceDatanodeOnFailure.REPLICATION).build();
        try {
            cluster.waitActive();
            final DistributedFileSystem fs = cluster.getFileSystem();
            final Path dir = new Path(TestReplaceDatanodeOnFailure.DIR);
            final int NUM_WRITERS = 10;
            final int FIRST_BATCH = 5;
            final TestReplaceDatanodeOnFailure.SlowWriter[] slowwriters = new TestReplaceDatanodeOnFailure.SlowWriter[NUM_WRITERS];
            for (int i = 1; i <= (slowwriters.length); i++) {
                // create slow writers in different speed
                slowwriters[(i - 1)] = new TestReplaceDatanodeOnFailure.SlowWriter(fs, new Path(dir, ("file" + i)), (i * 200L));
            }
            for (int i = 0; i < FIRST_BATCH; i++) {
                slowwriters[i].start();
            }
            // Let slow writers write something.
            // Some of them are too slow and will be not yet started.
            TestReplaceDatanodeOnFailure.sleepSeconds(3);
            // start new datanodes
            cluster.startDataNodes(conf, 2, true, null, new String[]{ TestReplaceDatanodeOnFailure.RACK1, TestReplaceDatanodeOnFailure.RACK1 });
            cluster.waitActive();
            // wait for first block reports for up to 10 seconds
            cluster.waitFirstBRCompleted(0, 10000);
            // stop an old datanode
            MiniDFSCluster.DataNodeProperties dnprop = cluster.stopDataNode(AppendTestUtil.nextInt(TestReplaceDatanodeOnFailure.REPLICATION));
            for (int i = FIRST_BATCH; i < (slowwriters.length); i++) {
                slowwriters[i].start();
            }
            waitForBlockReplication(slowwriters);
            // check replication and interrupt.
            for (TestReplaceDatanodeOnFailure.SlowWriter s : slowwriters) {
                s.checkReplication();
                s.interruptRunning();
            }
            // close files
            for (TestReplaceDatanodeOnFailure.SlowWriter s : slowwriters) {
                s.joinAndClose();
            }
            // Verify the file
            TestReplaceDatanodeOnFailure.LOG.info("Verify the file");
            for (int i = 0; i < (slowwriters.length); i++) {
                TestReplaceDatanodeOnFailure.LOG.info((((slowwriters[i].filepath) + ": length=") + (fs.getFileStatus(slowwriters[i].filepath).getLen())));
                FSDataInputStream in = null;
                try {
                    in = fs.open(slowwriters[i].filepath);
                    for (int j = 0, x; (x = in.read()) != (-1); j++) {
                        Assert.assertEquals(j, x);
                    }
                } finally {
                    IOUtils.closeStream(in);
                }
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    static class SlowWriter extends Thread {
        final Path filepath;

        final HdfsDataOutputStream out;

        final long sleepms;

        private volatile boolean running = true;

        SlowWriter(DistributedFileSystem fs, Path filepath, final long sleepms) throws IOException {
            super((((TestReplaceDatanodeOnFailure.SlowWriter.class.getSimpleName()) + ":") + filepath));
            this.filepath = filepath;
            this.out = ((HdfsDataOutputStream) (fs.create(filepath, TestReplaceDatanodeOnFailure.REPLICATION)));
            this.sleepms = sleepms;
        }

        @Override
        public void run() {
            int i = 0;
            try {
                Thread.sleep(sleepms);
                for (; running; i++) {
                    TestReplaceDatanodeOnFailure.LOG.info((((getName()) + " writes ") + i));
                    out.write(i);
                    out.hflush();
                    Thread.sleep(sleepms);
                }
            } catch (InterruptedException e) {
                TestReplaceDatanodeOnFailure.LOG.info((((getName()) + " interrupted:") + e));
            } catch (IOException e) {
                throw new RuntimeException(getName(), e);
            } finally {
                TestReplaceDatanodeOnFailure.LOG.info((((getName()) + " terminated: i=") + i));
            }
        }

        void interruptRunning() {
            running = false;
            interrupt();
        }

        void joinAndClose() throws InterruptedException {
            TestReplaceDatanodeOnFailure.LOG.info(((getName()) + " join and close"));
            join();
            IOUtils.closeStream(out);
        }

        void checkReplication() throws IOException {
            Assert.assertEquals(TestReplaceDatanodeOnFailure.REPLICATION, out.getCurrentBlockReplication());
        }
    }

    @Test
    public void testAppend() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        final short REPLICATION = ((short) (3));
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            final DistributedFileSystem fs = cluster.getFileSystem();
            final Path f = new Path(TestReplaceDatanodeOnFailure.DIR, "testAppend");
            {
                TestReplaceDatanodeOnFailure.LOG.info(("create an empty file " + f));
                fs.create(f, REPLICATION).close();
                final FileStatus status = fs.getFileStatus(f);
                Assert.assertEquals(REPLICATION, status.getReplication());
                Assert.assertEquals(0L, status.getLen());
            }
            final byte[] bytes = new byte[1000];
            {
                TestReplaceDatanodeOnFailure.LOG.info(((("append " + (bytes.length)) + " bytes to ") + f));
                final FSDataOutputStream out = fs.append(f);
                out.write(bytes);
                out.close();
                final FileStatus status = fs.getFileStatus(f);
                Assert.assertEquals(REPLICATION, status.getReplication());
                Assert.assertEquals(bytes.length, status.getLen());
            }
            {
                TestReplaceDatanodeOnFailure.LOG.info(((("append another " + (bytes.length)) + " bytes to ") + f));
                try {
                    final FSDataOutputStream out = fs.append(f);
                    out.write(bytes);
                    out.close();
                    Assert.fail();
                } catch (IOException ioe) {
                    TestReplaceDatanodeOnFailure.LOG.info("This exception is expected", ioe);
                }
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testBestEffort() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        // always replace a datanode but do not throw exception
        ReplaceDatanodeOnFailure.write(ALWAYS, true, conf);
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        try {
            final DistributedFileSystem fs = cluster.getFileSystem();
            final Path f = new Path(TestReplaceDatanodeOnFailure.DIR, "testIgnoreReplaceFailure");
            final byte[] bytes = new byte[1000];
            {
                TestReplaceDatanodeOnFailure.LOG.info(((("write " + (bytes.length)) + " bytes to ") + f));
                final FSDataOutputStream out = fs.create(f, TestReplaceDatanodeOnFailure.REPLICATION);
                out.write(bytes);
                out.close();
                final FileStatus status = fs.getFileStatus(f);
                Assert.assertEquals(TestReplaceDatanodeOnFailure.REPLICATION, status.getReplication());
                Assert.assertEquals(bytes.length, status.getLen());
            }
            {
                TestReplaceDatanodeOnFailure.LOG.info(((("append another " + (bytes.length)) + " bytes to ") + f));
                final FSDataOutputStream out = fs.append(f);
                out.write(bytes);
                out.close();
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

