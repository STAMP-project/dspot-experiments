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
 * This class contains unit tests for the {@link BlobCacheService}.
 */
public class BlobCacheSuccessTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * BlobCache with no HA, job-unrelated BLOBs. BLOBs need to be downloaded form a working
     * BlobServer.
     */
    @Test
    public void testBlobNoJobCache() throws IOException {
        Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        uploadFileGetTest(config, null, false, false, BlobType.TRANSIENT_BLOB);
    }

    /**
     * BlobCache with no HA, job-related BLOBS. BLOBs need to be downloaded form a working
     * BlobServer.
     */
    @Test
    public void testBlobForJobCache() throws IOException {
        Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        uploadFileGetTest(config, new JobID(), false, false, BlobType.TRANSIENT_BLOB);
    }

    /**
     * BlobCache is configured in HA mode and the cache can download files from
     * the file system directly and does not need to download BLOBs from the
     * BlobServer which remains active after the BLOB upload. Using job-related BLOBs.
     */
    @Test
    public void testBlobForJobCacheHa() throws IOException {
        Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        uploadFileGetTest(config, new JobID(), true, true, BlobType.PERMANENT_BLOB);
    }

    /**
     * BlobCache is configured in HA mode and the cache can download files from
     * the file system directly and does not need to download BLOBs from the
     * BlobServer which is shut down after the BLOB upload. Using job-related BLOBs.
     */
    @Test
    public void testBlobForJobCacheHa2() throws IOException {
        Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        uploadFileGetTest(config, new JobID(), false, true, BlobType.PERMANENT_BLOB);
    }

    /**
     * BlobCache is configured in HA mode but the cache itself cannot access the
     * file system and thus needs to download BLOBs from the BlobServer. Using job-related BLOBs.
     */
    @Test
    public void testBlobForJobCacheHaFallback() throws IOException {
        Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        uploadFileGetTest(config, new JobID(), false, false, BlobType.PERMANENT_BLOB);
    }
}

