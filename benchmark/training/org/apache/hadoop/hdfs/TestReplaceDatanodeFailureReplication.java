/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs;


import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify the behaviours of HdfsClientConfigKeys.BlockWrite.
 * ReplaceDatanodeOnFailure.MIN_REPLICATION.if live block location datanodes is
 * greater than or equal to
 * 'dfs.client.block.write.replace-datanode-on-failure.min.replication'
 * threshold value, if yes continue writing to the two remaining nodes.
 * Otherwise it will throw exception.
 * <p>
 * If this HdfsClientConfigKeys.BlockWrite.ReplaceDatanodeOnFailure.
 * MIN_REPLICATION is set to 0 or less than zero, an exception will be thrown
 * if a replacement could not be found.
 */
public class TestReplaceDatanodeFailureReplication {
    static final Logger LOG = LoggerFactory.getLogger(TestReplaceDatanodeFailureReplication.class);

    static final String DIR = ("/" + (TestReplaceDatanodeFailureReplication.class.getSimpleName())) + "/";

    static final short REPLICATION = 3;

    private static final String RACK0 = "/rack0";

    /**
     * Test fail last datanode in the pipeline.
     */
    @Test
    public void testLastDatanodeFailureInPipeline() throws Exception {
        testWriteFileAndVerifyAfterDNStop(2, 1, 10, false);
    }

    /**
     * Test fail first datanode in the pipeline.
     */
    @Test
    public void testFirstDatanodeFailureInPipeline() throws Exception {
        testWriteFileAndVerifyAfterDNStop(2, 0, 10, false);
    }

    /**
     * Test fail all the datanodes except first in the pipeline.
     */
    @Test
    public void testWithOnlyFirstDatanodeIsAlive() throws Exception {
        testWriteFileAndVerifyAfterDNStop(1, 1, 1, true);
    }

    /**
     * Test fail all the datanodes except lastnode in the pipeline.
     */
    @Test
    public void testWithOnlyLastDatanodeIsAlive() throws Exception {
        testWriteFileAndVerifyAfterDNStop(1, 0, 1, true);
    }

    /**
     * Test when number of live nodes are less than the
     * "dfs.client.block.write.replace-datanode-on-failure.min.replication".
     */
    @Test
    public void testLessNumberOfLiveDatanodesThanWriteReplaceDatanodeOnFailureRF() throws Exception {
        final MiniDFSCluster cluster = setupCluster(2);
        try {
            final DistributedFileSystem fs = cluster.getFileSystem();
            final Path dir = new Path(TestReplaceDatanodeFailureReplication.DIR);
            final TestReplaceDatanodeFailureReplication.SlowWriter[] slowwriters = new TestReplaceDatanodeFailureReplication.SlowWriter[1];
            for (int i = 1; i <= (slowwriters.length); i++) {
                // create slow writers in different speed
                slowwriters[(i - 1)] = new TestReplaceDatanodeFailureReplication.SlowWriter(fs, new Path(dir, ("file" + i)), (i * 200L));
            }
            for (TestReplaceDatanodeFailureReplication.SlowWriter s : slowwriters) {
                s.start();
            }
            // Let slow writers write something.
            // Some of them are too slow and will be not yet started.
            TestReplaceDatanodeFailureReplication.sleepSeconds(1);
            // stop an old datanode
            cluster.stopDataNode(0);
            cluster.stopDataNode(0);
            // Let the slow writer writes a few more seconds
            // Everyone should have written something.
            TestReplaceDatanodeFailureReplication.sleepSeconds(20);
            // check replication and interrupt.
            for (TestReplaceDatanodeFailureReplication.SlowWriter s : slowwriters) {
                try {
                    s.out.getCurrentBlockReplication();
                    Assert.fail(("Must throw exception as failed to add a new datanode for write " + "pipeline, minimum failure replication"));
                } catch (IOException e) {
                    // expected
                }
                s.interruptRunning();
            }
            // close files
            for (TestReplaceDatanodeFailureReplication.SlowWriter s : slowwriters) {
                s.joinAndClose();
            }
            // Verify the file
            verifyFileContent(fs, slowwriters);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    static class SlowWriter extends Thread {
        private final Path filepath;

        private final HdfsDataOutputStream out;

        private final long sleepms;

        private volatile boolean running = true;

        SlowWriter(DistributedFileSystem fs, Path filepath, final long sleepms) throws IOException {
            super((((TestReplaceDatanodeFailureReplication.SlowWriter.class.getSimpleName()) + ":") + filepath));
            this.filepath = filepath;
            this.out = ((HdfsDataOutputStream) (fs.create(filepath, TestReplaceDatanodeFailureReplication.REPLICATION)));
            this.sleepms = sleepms;
        }

        @Override
        public void run() {
            int i = 0;
            try {
                Thread.sleep(sleepms);
                for (; running; i++) {
                    TestReplaceDatanodeFailureReplication.LOG.info((((getName()) + " writes ") + i));
                    out.write(i);
                    out.hflush();
                    Thread.sleep(sleepms);
                }
            } catch (InterruptedException e) {
                TestReplaceDatanodeFailureReplication.LOG.info((((getName()) + " interrupted:") + e));
            } catch (IOException e) {
                throw new RuntimeException(getName(), e);
            } finally {
                TestReplaceDatanodeFailureReplication.LOG.info((((getName()) + " terminated: i=") + i));
            }
        }

        void interruptRunning() {
            running = false;
            interrupt();
        }

        void joinAndClose() throws InterruptedException {
            TestReplaceDatanodeFailureReplication.LOG.info(((getName()) + " join and close"));
            join();
            IOUtils.closeStream(out);
        }
    }
}

