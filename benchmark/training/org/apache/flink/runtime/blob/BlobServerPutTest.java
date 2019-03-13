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
package org.apache.flink.runtime.blob;


import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for successful and failing PUT operations against the BLOB server,
 * and successful GET operations.
 */
public class BlobServerPutTest extends TestLogger {
    private final Random rnd = new Random();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // --- concurrency tests for utility methods which could fail during the put operation ---
    /**
     * Checked thread that calls {@link BlobServer#getStorageLocation(JobID, BlobKey)}.
     */
    public static class ContentAddressableGetStorageLocation extends CheckedThread {
        private final BlobServer server;

        private final JobID jobId;

        private final BlobKey key;

        ContentAddressableGetStorageLocation(BlobServer server, @Nullable
        JobID jobId, BlobKey key) {
            this.server = server;
            this.jobId = jobId;
            this.key = key;
        }

        @Override
        public void go() throws Exception {
            server.getStorageLocation(jobId, key);
        }
    }

    /**
     * Tests concurrent calls to {@link BlobServer#getStorageLocation(JobID, BlobKey)}.
     */
    @Test
    public void testServerContentAddressableGetStorageLocationConcurrentNoJob() throws Exception {
        testServerContentAddressableGetStorageLocationConcurrent(null);
    }

    /**
     * Tests concurrent calls to {@link BlobServer#getStorageLocation(JobID, BlobKey)}.
     */
    @Test
    public void testServerContentAddressableGetStorageLocationConcurrentForJob() throws Exception {
        testServerContentAddressableGetStorageLocationConcurrent(new JobID());
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutBufferSuccessfulGet1() throws IOException {
        testPutBufferSuccessfulGet(null, null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferSuccessfulGet2() throws IOException {
        testPutBufferSuccessfulGet(null, new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferSuccessfulGet3() throws IOException {
        testPutBufferSuccessfulGet(new JobID(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferSuccessfulGet4() throws IOException {
        testPutBufferSuccessfulGet(new JobID(), null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferSuccessfulGetHa() throws IOException {
        testPutBufferSuccessfulGet(new JobID(), new JobID(), BlobType.PERMANENT_BLOB);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutStreamSuccessfulGet1() throws IOException {
        testPutStreamSuccessfulGet(null, null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutStreamSuccessfulGet2() throws IOException {
        testPutStreamSuccessfulGet(null, new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutStreamSuccessfulGet3() throws IOException {
        testPutStreamSuccessfulGet(new JobID(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutStreamSuccessfulGet4() throws IOException {
        testPutStreamSuccessfulGet(new JobID(), null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutStreamSuccessfulGetHa() throws IOException {
        testPutStreamSuccessfulGet(new JobID(), new JobID(), BlobType.PERMANENT_BLOB);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutChunkedStreamSuccessfulGet1() throws IOException {
        testPutChunkedStreamSuccessfulGet(null, null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutChunkedStreamSuccessfulGet2() throws IOException {
        testPutChunkedStreamSuccessfulGet(null, new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutChunkedStreamSuccessfulGet3() throws IOException {
        testPutChunkedStreamSuccessfulGet(new JobID(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutChunkedStreamSuccessfulGet4() throws IOException {
        testPutChunkedStreamSuccessfulGet(new JobID(), null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutChunkedStreamSuccessfulGetHa() throws IOException {
        testPutChunkedStreamSuccessfulGet(new JobID(), new JobID(), BlobType.PERMANENT_BLOB);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutBufferFailsNoJob() throws IOException {
        testPutBufferFails(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferFailsForJob() throws IOException {
        testPutBufferFails(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferFailsForJobHa() throws IOException {
        testPutBufferFails(new JobID(), BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testPutBufferFailsIncomingNoJob() throws IOException {
        testPutBufferFailsIncoming(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferFailsIncomingForJob() throws IOException {
        testPutBufferFailsIncoming(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferFailsIncomingForJobHa() throws IOException {
        testPutBufferFailsIncoming(new JobID(), BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testPutBufferFailsStoreNoJob() throws IOException {
        testPutBufferFailsStore(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferFailsStoreForJob() throws IOException {
        testPutBufferFailsStore(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferFailsStoreForJobHa() throws IOException {
        testPutBufferFailsStore(new JobID(), BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testConcurrentPutOperationsNoJob() throws IOException, InterruptedException, ExecutionException {
        testConcurrentPutOperations(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testConcurrentPutOperationsForJob() throws IOException, InterruptedException, ExecutionException {
        testConcurrentPutOperations(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testConcurrentPutOperationsForJobHa() throws IOException, InterruptedException, ExecutionException {
        testConcurrentPutOperations(new JobID(), BlobType.PERMANENT_BLOB);
    }

    // --------------------------------------------------------------------------------------------
    static final class BlockingInputStream extends InputStream {
        private final CountDownLatch countDownLatch;

        private final byte[] data;

        private int index = 0;

        BlockingInputStream(CountDownLatch countDownLatch, byte[] data) {
            this.countDownLatch = Preconditions.checkNotNull(countDownLatch);
            this.data = Preconditions.checkNotNull(data);
        }

        @Override
        public int read() throws IOException {
            countDownLatch.countDown();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Blocking operation was interrupted.", e);
            }
            if ((index) >= (data.length)) {
                return -1;
            } else {
                return data[((index)++)];
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    static final class ChunkedInputStream extends InputStream {
        private final byte[][] data;

        private int x = 0;

        private int y = 0;

        ChunkedInputStream(byte[] data, int numChunks) {
            this.data = new byte[numChunks][];
            int bytesPerChunk = (data.length) / numChunks;
            int bytesTaken = 0;
            for (int i = 0; i < (numChunks - 1); i++ , bytesTaken += bytesPerChunk) {
                this.data[i] = new byte[bytesPerChunk];
                System.arraycopy(data, bytesTaken, this.data[i], 0, bytesPerChunk);
            }
            this.data[(numChunks - 1)] = new byte[(data.length) - bytesTaken];
            System.arraycopy(data, bytesTaken, this.data[(numChunks - 1)], 0, this.data[(numChunks - 1)].length);
        }

        @Override
        public int read() {
            if ((x) < (data.length)) {
                byte[] curr = data[x];
                if ((y) < (curr.length)) {
                    byte next = curr[y];
                    (y)++;
                    return next;
                } else {
                    y = 0;
                    (x)++;
                    return read();
                }
            } else {
                return -1;
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            if ((x) < (data.length)) {
                byte[] curr = data[x];
                if ((y) < (curr.length)) {
                    int toCopy = Math.min(len, ((curr.length) - (y)));
                    System.arraycopy(curr, y, b, off, toCopy);
                    y += toCopy;
                    return toCopy;
                } else {
                    y = 0;
                    (x)++;
                    return read(b, off, len);
                }
            } else {
                return -1;
            }
        }
    }
}

