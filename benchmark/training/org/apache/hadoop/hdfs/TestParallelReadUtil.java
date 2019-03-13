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


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.impl.BlockReaderTestUtil;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Driver class for testing the use of DFSInputStream by multiple concurrent
 * readers, using the different read APIs.
 *
 * This class is marked as @Ignore so that junit doesn't try to execute the
 * tests in here directly.  They are executed from subclasses.
 */
@Ignore
public class TestParallelReadUtil {
    static final Logger LOG = LoggerFactory.getLogger(TestParallelReadUtil.class);

    static BlockReaderTestUtil util = null;

    static DFSClient dfsClient = null;

    static final int FILE_SIZE_K = 256;

    static Random rand = null;

    static final int DEFAULT_REPLICATION_FACTOR = 2;

    protected boolean verifyChecksums = true;

    static {
        // The client-trace log ends up causing a lot of blocking threads
        // in this when it's being used as a performance benchmark.
        LogManager.getLogger(((DataNode.class.getName()) + ".clienttrace")).setLevel(Level.WARN);
    }

    private class TestFileInfo {
        public DFSInputStream dis;

        public Path filepath;

        public byte[] authenticData;
    }

    /**
     * Providers of this interface implement two different read APIs. Instances of
     * this interface are shared across all ReadWorkerThreads, so should be stateless.
     */
    static interface ReadWorkerHelper {
        public int read(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException;

        public int pRead(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException;
    }

    /**
     * Uses read(ByteBuffer...) style APIs
     */
    static class DirectReadWorkerHelper implements TestParallelReadUtil.ReadWorkerHelper {
        @Override
        public int read(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException {
            ByteBuffer bb = ByteBuffer.allocateDirect(target.length);
            int cnt = 0;
            synchronized(dis) {
                dis.seek(startOff);
                while (cnt < len) {
                    int read = dis.read(bb);
                    if (read == (-1)) {
                        return read;
                    }
                    cnt += read;
                } 
            }
            bb.clear();
            bb.get(target);
            return cnt;
        }

        @Override
        public int pRead(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException {
            // No pRead for bb read path
            return read(dis, target, startOff, len);
        }
    }

    /**
     * Uses the read(byte[]...) style APIs
     */
    static class CopyingReadWorkerHelper implements TestParallelReadUtil.ReadWorkerHelper {
        @Override
        public int read(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException {
            int cnt = 0;
            synchronized(dis) {
                dis.seek(startOff);
                while (cnt < len) {
                    int read = dis.read(target, cnt, (len - cnt));
                    if (read == (-1)) {
                        return read;
                    }
                    cnt += read;
                } 
            }
            return cnt;
        }

        @Override
        public int pRead(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException {
            int cnt = 0;
            while (cnt < len) {
                int read = dis.read(startOff, target, cnt, (len - cnt));
                if (read == (-1)) {
                    return read;
                }
                cnt += read;
            } 
            return cnt;
        }
    }

    /**
     * Uses a mix of both copying
     */
    static class MixedWorkloadHelper implements TestParallelReadUtil.ReadWorkerHelper {
        private final TestParallelReadUtil.DirectReadWorkerHelper bb = new TestParallelReadUtil.DirectReadWorkerHelper();

        private final TestParallelReadUtil.CopyingReadWorkerHelper copy = new TestParallelReadUtil.CopyingReadWorkerHelper();

        private final double COPYING_PROBABILITY = 0.5;

        @Override
        public int read(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException {
            double p = TestParallelReadUtil.rand.nextDouble();
            if (p > (COPYING_PROBABILITY)) {
                return bb.read(dis, target, startOff, len);
            } else {
                return copy.read(dis, target, startOff, len);
            }
        }

        @Override
        public int pRead(DFSInputStream dis, byte[] target, int startOff, int len) throws IOException {
            double p = TestParallelReadUtil.rand.nextDouble();
            if (p > (COPYING_PROBABILITY)) {
                return bb.pRead(dis, target, startOff, len);
            } else {
                return copy.pRead(dis, target, startOff, len);
            }
        }
    }

    /**
     * A worker to do one "unit" of read.
     */
    static class ReadWorker extends Thread {
        public static final int N_ITERATIONS = 1024;

        private static final double PROPORTION_NON_POSITIONAL_READ = 0.1;

        private final TestParallelReadUtil.TestFileInfo testInfo;

        private final long fileSize;

        private long bytesRead;

        private boolean error;

        private final TestParallelReadUtil.ReadWorkerHelper helper;

        ReadWorker(TestParallelReadUtil.TestFileInfo testInfo, int id, TestParallelReadUtil.ReadWorkerHelper helper) {
            super(((("ReadWorker-" + id) + "-") + (testInfo.filepath.toString())));
            this.testInfo = testInfo;
            this.helper = helper;
            fileSize = testInfo.dis.getFileLength();
            Assert.assertEquals(fileSize, testInfo.authenticData.length);
            bytesRead = 0;
            error = false;
        }

        /**
         * Randomly do one of (1) Small read; and (2) Large Pread.
         */
        @Override
        public void run() {
            for (int i = 0; i < (TestParallelReadUtil.ReadWorker.N_ITERATIONS); ++i) {
                int startOff = TestParallelReadUtil.rand.nextInt(((int) (fileSize)));
                int len = 0;
                try {
                    double p = TestParallelReadUtil.rand.nextDouble();
                    if (p < (TestParallelReadUtil.ReadWorker.PROPORTION_NON_POSITIONAL_READ)) {
                        // Do a small regular read. Very likely this will leave unread
                        // data on the socket and make the socket uncacheable.
                        len = Math.min(TestParallelReadUtil.rand.nextInt(64), (((int) (fileSize)) - startOff));
                        read(startOff, len);
                        bytesRead += len;
                    } else {
                        // Do a positional read most of the time.
                        len = TestParallelReadUtil.rand.nextInt(((int) ((fileSize) - startOff)));
                        pRead(startOff, len);
                        bytesRead += len;
                    }
                } catch (Throwable t) {
                    TestParallelReadUtil.LOG.error((((((getName()) + ": Error while testing read at ") + startOff) + " length ") + len), t);
                    error = true;
                    Assert.fail(t.getMessage());
                }
            }
        }

        public long getBytesRead() {
            return bytesRead;
        }

        /**
         * Raising error in a thread doesn't seem to fail the test.
         * So check afterwards.
         */
        public boolean hasError() {
            return error;
        }

        static int readCount = 0;

        /**
         * Seek to somewhere random and read.
         */
        private void read(int start, int len) throws Exception {
            Assert.assertTrue(((((("Bad args: " + start) + " + ") + len) + " should be <= ") + (fileSize)), ((start + len) <= (fileSize)));
            (TestParallelReadUtil.ReadWorker.readCount)++;
            DFSInputStream dis = testInfo.dis;
            byte[] buf = new byte[len];
            helper.read(dis, buf, start, len);
            verifyData("Read data corrupted", buf, start, (start + len));
        }

        /**
         * Positional read.
         */
        private void pRead(int start, int len) throws Exception {
            Assert.assertTrue(((((("Bad args: " + start) + " + ") + len) + " should be <= ") + (fileSize)), ((start + len) <= (fileSize)));
            DFSInputStream dis = testInfo.dis;
            byte[] buf = new byte[len];
            helper.pRead(dis, buf, start, len);
            verifyData("Pread data corrupted", buf, start, (start + len));
        }

        /**
         * Verify read data vs authentic data
         */
        private void verifyData(String msg, byte[] actual, int start, int end) throws Exception {
            byte[] auth = testInfo.authenticData;
            if (end > (auth.length)) {
                throw new Exception((((((msg + ": Actual array (") + end) + ") is past the end of authentic data (") + (auth.length)) + ")"));
            }
            int j = start;
            for (int i = 0; i < (actual.length); ++i , ++j) {
                if ((auth[j]) != (actual[i])) {
                    throw new Exception(((((((((msg + ": Arrays byte ") + i) + " (at offset ") + j) + ") differs: expect ") + (auth[j])) + " got ") + (actual[i])));
                }
            }
        }
    }

    /**
     * Do parallel read several times with different number of files and threads.
     *
     * Note that while this is the only "test" in a junit sense, we're actually
     * dispatching a lot more. Failures in the other methods (and other threads)
     * need to be manually collected, which is inconvenient.
     */
    @Test
    public void testParallelReadCopying() throws IOException {
        runTestWorkload(new TestParallelReadUtil.CopyingReadWorkerHelper());
    }

    @Test
    public void testParallelReadByteBuffer() throws IOException {
        runTestWorkload(new TestParallelReadUtil.DirectReadWorkerHelper());
    }

    @Test
    public void testParallelReadMixed() throws IOException {
        runTestWorkload(new TestParallelReadUtil.MixedWorkloadHelper());
    }

    @Test
    public void testParallelNoChecksums() throws IOException {
        verifyChecksums = false;
        runTestWorkload(new TestParallelReadUtil.MixedWorkloadHelper());
    }
}

