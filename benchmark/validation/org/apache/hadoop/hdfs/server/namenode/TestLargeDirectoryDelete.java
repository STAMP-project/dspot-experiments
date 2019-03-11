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


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_NAMENODE_BLOCK_DELETION_INCREMENT_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Ensure during large directory delete, namenode does not block until the
 * deletion completes and handles new requests from other clients
 */
public class TestLargeDirectoryDelete {
    private static final Logger LOG = LoggerFactory.getLogger(TestLargeDirectoryDelete.class);

    private static final Configuration CONF = new HdfsConfiguration();

    private static final int TOTAL_BLOCKS = 10000;

    private MiniDFSCluster mc = null;

    private int createOps = 0;

    private int lockOps = 0;

    static {
        TestLargeDirectoryDelete.CONF.setLong(DFS_BLOCK_SIZE_KEY, 1);
        TestLargeDirectoryDelete.CONF.setInt(DFS_BYTES_PER_CHECKSUM_KEY, 1);
        TestLargeDirectoryDelete.CONF.setInt(DFS_NAMENODE_BLOCK_DELETION_INCREMENT_KEY, 1);
    }

    /**
     * An abstract class for tests that catches exceptions and can
     * rethrow them on a different thread, and has an {@link #endThread()}
     * operation that flips a volatile boolean before interrupting the thread.
     * Also: after running the implementation of {@link #execute()} in the
     * implementation class, the thread is notified: other threads can wait
     * for it to terminate
     */
    private abstract class TestThread extends Thread {
        volatile Throwable thrown;

        protected volatile boolean live = true;

        @Override
        public void run() {
            try {
                execute();
            } catch (Throwable throwable) {
                TestLargeDirectoryDelete.LOG.warn("{}", throwable);
                setThrown(throwable);
            } finally {
                synchronized(this) {
                    this.notify();
                }
            }
        }

        protected abstract void execute() throws Throwable;

        protected synchronized void setThrown(Throwable thrown) {
            this.thrown = thrown;
        }

        /**
         * Rethrow anything caught
         *
         * @throws Throwable
         * 		any non-null throwable raised by the execute method.
         */
        public synchronized void rethrow() throws Throwable {
            if ((thrown) != null) {
                throw thrown;
            }
        }

        /**
         * End the thread by setting the live p
         */
        public synchronized void endThread() {
            live = false;
            interrupt();
            try {
                wait();
            } catch (InterruptedException e) {
                if (TestLargeDirectoryDelete.LOG.isDebugEnabled()) {
                    TestLargeDirectoryDelete.LOG.debug(("Ignoring " + e), e);
                }
            }
        }
    }

    @Test
    public void largeDelete() throws Throwable {
        mc = new MiniDFSCluster.Builder(TestLargeDirectoryDelete.CONF).build();
        try {
            mc.waitActive();
            Assert.assertNotNull("No Namenode in cluster", mc.getNameNode());
            createFiles();
            Assert.assertEquals(TestLargeDirectoryDelete.TOTAL_BLOCKS, getBlockCount());
            runThreads();
        } finally {
            mc.shutdown();
        }
    }
}

