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
import java.io.IOException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for the blob cache retrying the connection to the server.
 */
public class BlobCacheRetriesTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * A test where the connection fails twice and then the get operation succeeds
     * (job-unrelated blob).
     */
    @Test
    public void testBlobFetchRetries() throws IOException {
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        BlobCacheRetriesTest.testBlobFetchRetries(config, new VoidBlobStore(), null, BlobType.TRANSIENT_BLOB);
    }

    /**
     * A test where the connection fails twice and then the get operation succeeds
     * (job-related blob).
     */
    @Test
    public void testBlobForJobFetchRetries() throws IOException {
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        BlobCacheRetriesTest.testBlobFetchRetries(config, new VoidBlobStore(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    /**
     * A test where the connection fails twice and then the get operation succeeds
     * (with high availability set, job-related job).
     */
    @Test
    public void testBlobFetchRetriesHa() throws IOException {
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        BlobStoreService blobStoreService = null;
        try {
            blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
            BlobCacheRetriesTest.testBlobFetchRetries(config, blobStoreService, new JobID(), BlobType.PERMANENT_BLOB);
        } finally {
            if (blobStoreService != null) {
                blobStoreService.closeAndCleanupAllData();
            }
        }
    }

    /**
     * A test where the connection fails too often and eventually fails the GET request
     * (job-unrelated blob).
     */
    @Test
    public void testBlobNoJobFetchWithTooManyFailures() throws IOException {
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        BlobCacheRetriesTest.testBlobFetchWithTooManyFailures(config, new VoidBlobStore(), null, BlobType.TRANSIENT_BLOB);
    }

    /**
     * A test where the connection fails too often and eventually fails the GET request (job-related
     * blob).
     */
    @Test
    public void testBlobForJobFetchWithTooManyFailures() throws IOException {
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        BlobCacheRetriesTest.testBlobFetchWithTooManyFailures(config, new VoidBlobStore(), new JobID(), BlobType.TRANSIENT_BLOB);
    }

    /**
     * A test where the connection fails too often and eventually fails the GET request
     * (with high availability set, job-related blob).
     */
    @Test
    public void testBlobForJobFetchWithTooManyFailuresHa() throws IOException {
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(HA_STORAGE_PATH, temporaryFolder.getRoot().getPath());
        BlobStoreService blobStoreService = null;
        try {
            blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
            BlobCacheRetriesTest.testBlobFetchWithTooManyFailures(config, blobStoreService, new JobID(), BlobType.PERMANENT_BLOB);
        } finally {
            if (blobStoreService != null) {
                blobStoreService.closeAndCleanupAllData();
            }
        }
    }
}

