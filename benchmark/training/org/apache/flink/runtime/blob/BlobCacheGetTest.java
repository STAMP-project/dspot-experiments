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
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for GET-specific parts of the {@link BlobCacheService}.
 *
 * <p>This includes access of transient BLOBs from the {@link PermanentBlobCache}, permanent BLOBS from
 * the {@link TransientBlobCache}, and how failing GET requests behave in the presence of failures
 * when used with a {@link BlobCacheService}.
 *
 * <p>Most successful GET requests are tested in conjunction wit the PUT requests by {@link BlobCachePutTest}.
 */
public class BlobCacheGetTest extends TestLogger {
    private final Random rnd = new Random();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetTransientFailsDuringLookup1() throws IOException, InterruptedException {
        testGetFailsDuringLookup(null, new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetTransientFailsDuringLookup2() throws IOException, InterruptedException {
        testGetFailsDuringLookup(new JobID(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetTransientFailsDuringLookup3() throws IOException, InterruptedException {
        testGetFailsDuringLookup(new JobID(), null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetFailsDuringLookupHa() throws IOException, InterruptedException {
        testGetFailsDuringLookup(new JobID(), new JobID(), BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testGetFailsIncomingNoJob() throws IOException {
        testGetFailsIncoming(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetFailsIncomingForJob() throws IOException {
        testGetFailsIncoming(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetFailsIncomingForJobHa() throws IOException {
        testGetFailsIncoming(new JobID(), BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testGetTransientFailsStoreNoJob() throws IOException, InterruptedException {
        testGetFailsStore(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetTransientFailsStoreForJob() throws IOException, InterruptedException {
        testGetFailsStore(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetPermanentFailsStoreForJob() throws IOException, InterruptedException {
        testGetFailsStore(new JobID(), BlobType.PERMANENT_BLOB);
    }

    /**
     * Retrieves a BLOB from the HA store to a {@link BlobServer} whose HA store does not contain
     * the file. File transfers should fail.
     */
    @Test
    public void testGetFailsHaStoreForJobHa() throws IOException {
        final JobID jobId = new JobID();
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        try (BlobServer server = new BlobServer(config, new VoidBlobStore());BlobCacheService cache = new BlobCacheService(config, new VoidBlobStore(), new InetSocketAddress("localhost", server.getPort()))) {
            server.start();
            // store the data on the server (and blobStore), remove from local server store
            byte[] data = new byte[2000000];
            rnd.nextBytes(data);
            PermanentBlobKey blobKey = ((PermanentBlobKey) (BlobServerPutTest.put(server, jobId, data, BlobType.PERMANENT_BLOB)));
            Assert.assertTrue(server.getStorageLocation(jobId, blobKey).delete());
            File tempFileDir = server.createTemporaryFilename().getParentFile();
            // request the file from the server via the cache
            exception.expect(IOException.class);
            exception.expectMessage("Failed to fetch BLOB ");
            try {
                BlobServerGetTest.get(cache, jobId, blobKey);
            } finally {
                HashSet<String> expectedDirs = new HashSet<>();
                expectedDirs.add("incoming");
                expectedDirs.add(((BlobUtils.JOB_DIR_PREFIX) + jobId));
                // only the incoming and job directory should exist (no job directory!)
                File storageDir = tempFileDir.getParentFile();
                String[] actualDirs = storageDir.list();
                Assert.assertNotNull(actualDirs);
                Assert.assertEquals(expectedDirs, new HashSet<>(Arrays.asList(actualDirs)));
                // job directory should be empty
                File jobDir = new File(tempFileDir.getParentFile(), ((BlobUtils.JOB_DIR_PREFIX) + jobId));
                Assert.assertArrayEquals(new String[]{  }, jobDir.list());
            }
        }
    }

    @Test
    public void testGetTransientRemoteDeleteFailsNoJob() throws IOException {
        testGetTransientRemoteDeleteFails(null);
    }

    @Test
    public void testGetTransientRemoteDeleteFailsForJob() throws IOException {
        testGetTransientRemoteDeleteFails(new JobID());
    }

    /**
     * FLINK-6020
     *
     * <p>Tests that concurrent get operations don't concurrently access the BlobStore to download a blob.
     */
    @Test
    public void testConcurrentGetOperationsNoJob() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(null, BlobType.TRANSIENT_BLOB, false);
    }

    @Test
    public void testConcurrentGetOperationsForJob() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(new JobID(), BlobType.TRANSIENT_BLOB, false);
    }

    @Test
    public void testConcurrentGetOperationsForJobHa() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(new JobID(), BlobType.PERMANENT_BLOB, false);
    }

    @Test
    public void testConcurrentGetOperationsForJobHa2() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(new JobID(), BlobType.PERMANENT_BLOB, true);
    }
}

