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


import BlobServerOptions.CLEANUP_INTERVAL;
import BlobServerOptions.STORAGE_DIRECTORY;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * A few tests for the cleanup of {@link PermanentBlobCache} and {@link TransientBlobCache}.
 */
public class BlobCacheCleanupTest extends TestLogger {
    private final Random rnd = new Random();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests that {@link PermanentBlobCache} cleans up after calling {@link PermanentBlobCache#releaseJob(JobID)}.
     */
    @Test
    public void testPermanentBlobCleanup() throws IOException, InterruptedException {
        JobID jobId = new JobID();
        List<PermanentBlobKey> keys = new ArrayList<>();
        BlobServer server = null;
        PermanentBlobCache cache = null;
        final byte[] buf = new byte[128];
        try {
            Configuration config = new Configuration();
            config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
            config.setLong(CLEANUP_INTERVAL, 1L);
            server = new BlobServer(config, new VoidBlobStore());
            server.start();
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", server.getPort());
            cache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress);
            // upload blobs
            keys.add(server.putPermanent(jobId, buf));
            buf[0] += 1;
            keys.add(server.putPermanent(jobId, buf));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId, cache);
            // register once
            cache.registerJob(jobId);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(0, jobId, cache);
            for (PermanentBlobKey key : keys) {
                cache.getFile(jobId, key);
            }
            // register again (let's say, from another thread or so)
            cache.registerJob(jobId);
            for (PermanentBlobKey key : keys) {
                cache.getFile(jobId, key);
            }
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            // after releasing once, nothing should change
            cache.releaseJob(jobId);
            Assert.assertEquals(2, BlobServerCleanupTest.checkFilesExist(jobId, keys, cache, true));
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, cache);
            // after releasing the second time, the job is up for deferred cleanup
            cache.releaseJob(jobId);
            BlobCacheCleanupTest.verifyJobCleanup(cache, jobId, keys);
            // server should be unaffected
            BlobServerCleanupTest.checkFileCountForJob(2, jobId, server);
        } finally {
            if (cache != null) {
                cache.close();
            }
            if (server != null) {
                server.close();
            }
            // now everything should be cleaned up
            BlobServerCleanupTest.checkFileCountForJob(0, jobId, server);
        }
    }

    /**
     * Tests that {@link PermanentBlobCache} sets the expected reference counts and cleanup timeouts
     * when registering, releasing, and re-registering jobs.
     */
    @Test
    public void testPermanentJobReferences() throws IOException, InterruptedException {
        JobID jobId = new JobID();
        Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setLong(CLEANUP_INTERVAL, 3600000L);// 1 hour should effectively prevent races

        // NOTE: use fake address - we will not connect to it here
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 12345);
        try (PermanentBlobCache cache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress)) {
            // register once
            cache.registerJob(jobId);
            Assert.assertEquals(1, cache.getJobRefCounters().get(jobId).references);
            Assert.assertEquals((-1), cache.getJobRefCounters().get(jobId).keepUntil);
            // register a second time
            cache.registerJob(jobId);
            Assert.assertEquals(2, cache.getJobRefCounters().get(jobId).references);
            Assert.assertEquals((-1), cache.getJobRefCounters().get(jobId).keepUntil);
            // release once
            cache.releaseJob(jobId);
            Assert.assertEquals(1, cache.getJobRefCounters().get(jobId).references);
            Assert.assertEquals((-1), cache.getJobRefCounters().get(jobId).keepUntil);
            // release a second time
            long cleanupLowerBound = (System.currentTimeMillis()) + (config.getLong(CLEANUP_INTERVAL));
            cache.releaseJob(jobId);
            Assert.assertEquals(0, cache.getJobRefCounters().get(jobId).references);
            Assert.assertThat(cache.getJobRefCounters().get(jobId).keepUntil, Matchers.greaterThanOrEqualTo(cleanupLowerBound));
            // register again
            cache.registerJob(jobId);
            Assert.assertEquals(1, cache.getJobRefCounters().get(jobId).references);
            Assert.assertEquals((-1), cache.getJobRefCounters().get(jobId).keepUntil);
            // finally release the job
            cleanupLowerBound = (System.currentTimeMillis()) + (config.getLong(CLEANUP_INTERVAL));
            cache.releaseJob(jobId);
            Assert.assertEquals(0, cache.getJobRefCounters().get(jobId).references);
            Assert.assertThat(cache.getJobRefCounters().get(jobId).keepUntil, Matchers.greaterThanOrEqualTo(cleanupLowerBound));
        }
    }

    @Test
    public void testTransientBlobNoJobCleanup() throws IOException, InterruptedException, ExecutionException {
        testTransientBlobCleanup(null);
    }

    @Test
    public void testTransientBlobForJobCleanup() throws IOException, InterruptedException, ExecutionException {
        testTransientBlobCleanup(new JobID());
    }
}

