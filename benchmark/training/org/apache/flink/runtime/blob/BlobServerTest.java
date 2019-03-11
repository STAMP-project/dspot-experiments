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
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link BlobServer}.
 */
public class BlobServerTest extends TestLogger {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests that the {@link BlobServer} fails if the blob storage directory
     * cannot be created.
     */
    @Test
    public void testFailureIfStorageDirectoryCannotBeCreated() throws IOException {
        final Configuration configuration = new Configuration();
        final File blobStorageDirectory = createNonWritableDirectory();
        final String nonExistDirectory = new File(blobStorageDirectory, "does_not_exist_for_sure").getAbsolutePath();
        configuration.setString(STORAGE_DIRECTORY, nonExistDirectory);
        try (BlobServer ignored = new BlobServer(configuration, new VoidBlobStore())) {
            Assert.fail("Expected that the BlobServer initialization fails.");
        } catch (IOException expected) {
            // expected
        }
    }
}

