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
import HighAvailabilityOptions.HA_MODE;
import HighAvailabilityOptions.HA_STORAGE_PATH;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.OperatingSystem;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests how failing GET requests behave in the presence of failures when used with a {@link BlobServer}.
 *
 * <p>Successful GET requests are tested in conjunction wit the PUT requests by {@link BlobServerPutTest}.
 */
public class BlobServerGetTest extends TestLogger {
    private final Random rnd = new Random();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetTransientFailsDuringLookup1() throws IOException {
        testGetFailsDuringLookup(null, new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetTransientFailsDuringLookup2() throws IOException {
        testGetFailsDuringLookup(new JobID(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetTransientFailsDuringLookup3() throws IOException {
        testGetFailsDuringLookup(new JobID(), null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetPermanentFailsDuringLookup() throws IOException {
        testGetFailsDuringLookup(new JobID(), new JobID(), BlobType.PERMANENT_BLOB);
    }

    /**
     * Retrieves a BLOB from the HA store to a {@link BlobServer} which cannot create incoming
     * files. File transfers should fail.
     */
    @Test
    public void testGetFailsIncomingForJobHa() throws IOException {
        Assume.assumeTrue((!(OperatingSystem.isWindows())));// setWritable doesn't work on Windows.

        final JobID jobId = new JobID();
        final Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        BlobStoreService blobStore = null;
        try {
            blobStore = BlobUtils.createBlobStoreFromConfig(config);
            File tempFileDir = null;
            try (BlobServer server = new BlobServer(config, blobStore)) {
                server.start();
                // store the data on the server (and blobStore), remove from local store
                byte[] data = new byte[2000000];
                rnd.nextBytes(data);
                BlobKey blobKey = BlobServerPutTest.put(server, jobId, data, BlobType.PERMANENT_BLOB);
                Assert.assertTrue(server.getStorageLocation(jobId, blobKey).delete());
                // make sure the blob server cannot create any files in its storage dir
                tempFileDir = server.createTemporaryFilename().getParentFile();
                Assert.assertTrue(tempFileDir.setExecutable(true, false));
                Assert.assertTrue(tempFileDir.setReadable(true, false));
                Assert.assertTrue(tempFileDir.setWritable(false, false));
                // request the file from the BlobStore
                exception.expect(IOException.class);
                exception.expectMessage("Permission denied");
                try {
                    BlobServerGetTest.get(server, jobId, blobKey);
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
            } finally {
                // set writable again to make sure we can remove the directory
                if (tempFileDir != null) {
                    // noinspection ResultOfMethodCallIgnored
                    tempFileDir.setWritable(true, false);
                }
            }
        } finally {
            if (blobStore != null) {
                blobStore.closeAndCleanupAllData();
            }
        }
    }

    /**
     * Retrieves a BLOB from the HA store to a {@link BlobServer} which cannot create the final
     * storage file. File transfers should fail.
     */
    @Test
    public void testGetFailsStoreForJobHa() throws IOException {
        Assume.assumeTrue((!(OperatingSystem.isWindows())));// setWritable doesn't work on Windows.

        final JobID jobId = new JobID();
        final Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        BlobStoreService blobStore = null;
        try {
            blobStore = BlobUtils.createBlobStoreFromConfig(config);
            File jobStoreDir = null;
            try (BlobServer server = new BlobServer(config, blobStore)) {
                server.start();
                // store the data on the server (and blobStore), remove from local store
                byte[] data = new byte[2000000];
                rnd.nextBytes(data);
                BlobKey blobKey = BlobServerPutTest.put(server, jobId, data, BlobType.PERMANENT_BLOB);
                Assert.assertTrue(server.getStorageLocation(jobId, blobKey).delete());
                // make sure the blob cache cannot create any files in its storage dir
                jobStoreDir = server.getStorageLocation(jobId, blobKey).getParentFile();
                Assert.assertTrue(jobStoreDir.setExecutable(true, false));
                Assert.assertTrue(jobStoreDir.setReadable(true, false));
                Assert.assertTrue(jobStoreDir.setWritable(false, false));
                // request the file from the BlobStore
                exception.expect(AccessDeniedException.class);
                try {
                    BlobServerGetTest.get(server, jobId, blobKey);
                } finally {
                    // there should be no remaining incoming files
                    File incomingFileDir = new File(jobStoreDir.getParent(), "incoming");
                    Assert.assertArrayEquals(new String[]{  }, incomingFileDir.list());
                    // there should be no files in the job directory
                    Assert.assertArrayEquals(new String[]{  }, jobStoreDir.list());
                }
            } finally {
                // set writable again to make sure we can remove the directory
                if (jobStoreDir != null) {
                    // noinspection ResultOfMethodCallIgnored
                    jobStoreDir.setWritable(true, false);
                }
            }
        } finally {
            if (blobStore != null) {
                blobStore.closeAndCleanupAllData();
            }
        }
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
        try (BlobServer server = new BlobServer(config, new VoidBlobStore())) {
            server.start();
            // store the data on the server (and blobStore), remove from local store
            byte[] data = new byte[2000000];
            rnd.nextBytes(data);
            BlobKey blobKey = BlobServerPutTest.put(server, jobId, data, BlobType.PERMANENT_BLOB);
            Assert.assertTrue(server.getStorageLocation(jobId, blobKey).delete());
            File tempFileDir = server.createTemporaryFilename().getParentFile();
            // request the file from the BlobStore
            exception.expect(NoSuchFileException.class);
            try {
                BlobServerGetTest.get(server, jobId, blobKey);
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
    public void testConcurrentGetOperationsNoJob() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testConcurrentGetOperationsForJob() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testConcurrentGetOperationsForJobHa() throws IOException, InterruptedException, ExecutionException {
        testConcurrentGetOperations(new JobID(), BlobType.PERMANENT_BLOB);
    }
}

