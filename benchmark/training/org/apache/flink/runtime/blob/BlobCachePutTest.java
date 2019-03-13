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


import BlobServerOptions.STORAGE_DIRECTORY;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for successful and failing PUT operations against the BLOB server,
 * and successful GET operations.
 */
public class BlobCachePutTest extends TestLogger {
    private final Random rnd = new Random();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // --- concurrency tests for utility methods which could fail during the put operation ---
    /**
     * Checked thread that calls {@link TransientBlobCache#getStorageLocation(JobID, BlobKey)}.
     */
    public static class TransientBlobCacheGetStorageLocation extends CheckedThread {
        private final TransientBlobCache cache;

        private final JobID jobId;

        private final BlobKey key;

        TransientBlobCacheGetStorageLocation(TransientBlobCache cache, @Nullable
        JobID jobId, BlobKey key) {
            this.cache = cache;
            this.jobId = jobId;
            this.key = key;
        }

        @Override
        public void go() throws Exception {
            cache.getStorageLocation(jobId, key);
        }
    }

    /**
     * Checked thread that calls {@link PermanentBlobCache#getStorageLocation(JobID, BlobKey)}.
     */
    public static class PermanentBlobCacheGetStorageLocation extends CheckedThread {
        private final PermanentBlobCache cache;

        private final JobID jobId;

        private final BlobKey key;

        PermanentBlobCacheGetStorageLocation(PermanentBlobCache cache, JobID jobId, BlobKey key) {
            this.cache = cache;
            this.jobId = jobId;
            this.key = key;
        }

        @Override
        public void go() throws Exception {
            cache.getStorageLocation(jobId, key);
        }
    }

    /**
     * Tests concurrent calls to {@link TransientBlobCache#getStorageLocation(JobID, BlobKey)}.
     */
    @Test
    public void testTransientBlobCacheGetStorageLocationConcurrentNoJob() throws Exception {
        testTransientBlobCacheGetStorageLocationConcurrent(null);
    }

    /**
     * Tests concurrent calls to {@link TransientBlobCache#getStorageLocation(JobID, BlobKey)}.
     */
    @Test
    public void testTransientBlobCacheGetStorageLocationConcurrentForJob() throws Exception {
        testTransientBlobCacheGetStorageLocationConcurrent(new JobID());
    }

    /**
     * Tests concurrent calls to {@link PermanentBlobCache#getStorageLocation(JobID, BlobKey)}.
     */
    @Test
    public void testPermanentBlobCacheGetStorageLocationConcurrentForJob() throws Exception {
        final JobID jobId = new JobID();
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        try (BlobServer server = new BlobServer(config, new VoidBlobStore());final PermanentBlobCache cache = new PermanentBlobCache(config, new VoidBlobStore(), new InetSocketAddress("localhost", server.getPort()))) {
            server.start();
            BlobKey key = new PermanentBlobKey();
            CheckedThread[] threads = new CheckedThread[]{ new BlobCachePutTest.PermanentBlobCacheGetStorageLocation(cache, jobId, key), new BlobCachePutTest.PermanentBlobCacheGetStorageLocation(cache, jobId, key), new BlobCachePutTest.PermanentBlobCacheGetStorageLocation(cache, jobId, key) };
            checkedThreadSimpleTest(threads);
        }
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutBufferTransientSuccessfulGet1() throws IOException, InterruptedException {
        testPutBufferSuccessfulGet(null, null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferTransientSuccessfulGet2() throws IOException, InterruptedException {
        testPutBufferSuccessfulGet(null, new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferTransientSuccessfulGet3() throws IOException, InterruptedException {
        testPutBufferSuccessfulGet(new JobID(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferTransientSuccessfulGet4() throws IOException, InterruptedException {
        testPutBufferSuccessfulGet(new JobID(), null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testPutBufferPermanentSuccessfulGet() throws IOException, InterruptedException {
        testPutBufferSuccessfulGet(new JobID(), new JobID(), BlobType.PERMANENT_BLOB);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutStreamTransientSuccessfulGet1() throws IOException, InterruptedException {
        testPutStreamTransientSuccessfulGet(null, null);
    }

    @Test
    public void testPutStreamTransientSuccessfulGet2() throws IOException, InterruptedException {
        testPutStreamTransientSuccessfulGet(null, new JobID());
    }

    @Test
    public void testPutStreamTransientSuccessfulGet3() throws IOException, InterruptedException {
        testPutStreamTransientSuccessfulGet(new JobID(), new JobID());
    }

    @Test
    public void testPutStreamTransientSuccessfulGet4() throws IOException, InterruptedException {
        testPutStreamTransientSuccessfulGet(new JobID(), null);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testPutChunkedStreamTransientSuccessfulGet1() throws IOException, InterruptedException {
        testPutChunkedStreamTransientSuccessfulGet(null, null);
    }

    @Test
    public void testPutChunkedStreamTransientSuccessfulGet2() throws IOException, InterruptedException {
        testPutChunkedStreamTransientSuccessfulGet(null, new JobID());
    }

    @Test
    public void testPutChunkedStreamTransientSuccessfulGet3() throws IOException, InterruptedException {
        testPutChunkedStreamTransientSuccessfulGet(new JobID(), new JobID());
    }

    @Test
    public void testPutChunkedStreamTransientSuccessfulGet4() throws IOException, InterruptedException {
        testPutChunkedStreamTransientSuccessfulGet(new JobID(), null);
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
}

