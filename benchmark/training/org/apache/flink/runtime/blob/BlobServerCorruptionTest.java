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
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests how GET requests react to corrupt files when downloaded via a {@link BlobServer}.
 *
 * <p>Successful GET requests are tested in conjunction wit the PUT requests.
 */
public class BlobServerCorruptionTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Checks the GET operation fails when the downloaded file (from {@link BlobServer} or HA store)
     * is corrupt, i.e. its content's hash does not match the {@link BlobKey}'s hash.
     */
    @Test
    public void testGetFailsFromCorruptFile() throws IOException {
        final Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        BlobStoreService blobStoreService = null;
        try {
            blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
            BlobServerCorruptionTest.testGetFailsFromCorruptFile(config, blobStoreService, exception);
        } finally {
            if (blobStoreService != null) {
                blobStoreService.closeAndCleanupAllData();
            }
        }
    }
}

