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


import DataNode.LOG;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import static org.slf4j.event.Level.TRACE;


/**
 * This class tests that pipelines survive data node death and recovery.
 */
public class TestDatanodeDeath {
    {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
        GenericTestUtils.setLogLevel(LOG, Level.ALL);
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(InterDatanodeProtocol.LOG, TRACE);
    }

    static final int blockSize = 8192;

    static final int numBlocks = 2;

    static final int fileSize = ((TestDatanodeDeath.numBlocks) * (TestDatanodeDeath.blockSize)) + 1;

    static final int numDatanodes = 15;

    static final short replication = 3;

    final int numberOfFiles = 3;

    final int numThreads = 5;

    TestDatanodeDeath.Workload[] workload = null;

    // 
    // an object that does a bunch of transactions
    // 
    static class Workload extends Thread {
        private final short replication;

        private final int numberOfFiles;

        private final int id;

        private final FileSystem fs;

        private long stamp;

        private final long myseed;

        Workload(long myseed, FileSystem fs, int threadIndex, int numberOfFiles, short replication, long stamp) {
            this.myseed = myseed;
            id = threadIndex;
            this.fs = fs;
            this.numberOfFiles = numberOfFiles;
            this.replication = replication;
            this.stamp = stamp;
        }

        // create a bunch of files. Write to them and then verify.
        @Override
        public void run() {
            System.out.println("Workload starting ");
            for (int i = 0; i < (numberOfFiles); i++) {
                Path filename = new Path((((id) + ".") + i));
                try {
                    System.out.println(("Workload processing file " + filename));
                    FSDataOutputStream stm = TestDatanodeDeath.createFile(fs, filename, replication);
                    DFSOutputStream dfstream = ((DFSOutputStream) (stm.getWrappedStream()));
                    dfstream.setArtificialSlowdown(1000);
                    TestDatanodeDeath.writeFile(stm, myseed);
                    stm.close();
                    TestDatanodeDeath.checkFile(fs, filename, replication, TestDatanodeDeath.numBlocks, TestDatanodeDeath.fileSize, myseed);
                } catch (Throwable e) {
                    System.out.println(("Workload exception " + e));
                    Assert.assertTrue(e.toString(), false);
                }
                // increment the stamp to indicate that another file is done.
                synchronized(this) {
                    (stamp)++;
                }
            }
        }

        public synchronized void resetStamp() {
            this.stamp = 0;
        }

        public synchronized long getStamp() {
            return stamp;
        }
    }

    /**
     * A class that kills one datanode and recreates a new one. It waits to
     * ensure that that all workers have finished at least one file since the
     * last kill of a datanode. This guarantees that all three replicas of
     * a block do not get killed (otherwise the file will be corrupt and the
     * test will fail).
     */
    class Modify extends Thread {
        volatile boolean running;

        final MiniDFSCluster cluster;

        final Configuration conf;

        Modify(Configuration conf, MiniDFSCluster cluster) {
            running = true;
            this.cluster = cluster;
            this.conf = conf;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    continue;
                }
                // check if all threads have a new stamp.
                // If so, then all workers have finished at least one file
                // since the last stamp.
                boolean loop = false;
                for (int i = 0; i < (numThreads); i++) {
                    if ((workload[i].getStamp()) == 0) {
                        loop = true;
                        break;
                    }
                }
                if (loop) {
                    continue;
                }
                // Now it is guaranteed that there will be at least one valid
                // replica of a file.
                for (int i = 0; i < ((TestDatanodeDeath.replication) - 1); i++) {
                    // pick a random datanode to shutdown
                    int victim = AppendTestUtil.nextInt(TestDatanodeDeath.numDatanodes);
                    try {
                        System.out.println(("Stopping datanode " + victim));
                        cluster.restartDataNode(victim);
                        // cluster.startDataNodes(conf, 1, true, null, null);
                    } catch (IOException e) {
                        System.out.println(("TestDatanodeDeath Modify exception " + e));
                        Assert.assertTrue(("TestDatanodeDeath Modify exception " + e), false);
                        running = false;
                    }
                }
                // set a new stamp for all workers
                for (int i = 0; i < (numThreads); i++) {
                    workload[i].resetStamp();
                }
            } 
        }

        // Make the thread exit.
        void close() {
            running = false;
            this.interrupt();
        }
    }

    @Test
    public void testSimple0() throws IOException {
        simpleTest(0);
    }

    @Test
    public void testSimple1() throws IOException {
        simpleTest(1);
    }

    @Test
    public void testSimple2() throws IOException {
        simpleTest(2);
    }

    @Test
    public void testComplex() throws IOException {
        complexTest();
    }
}

