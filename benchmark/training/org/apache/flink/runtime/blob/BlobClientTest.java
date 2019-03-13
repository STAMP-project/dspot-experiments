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
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * This class contains unit tests for the {@link BlobClient}.
 */
public class BlobClientTest extends TestLogger {
    /**
     * The buffer size used during the tests in bytes.
     */
    private static final int TEST_BUFFER_SIZE = 17 * 1000;

    /**
     * The instance of the (non-ssl) BLOB server used during the tests.
     */
    static BlobServer blobServer;

    /**
     * The blob service (non-ssl) client configuration.
     */
    static Configuration clientConfig;

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testContentAddressableBufferTransientBlob() throws IOException, InterruptedException {
        testContentAddressableBuffer(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testContentAddressableBufferPermantBlob() throws IOException, InterruptedException {
        testContentAddressableBuffer(BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testContentAddressableStreamTransientBlob() throws IOException, InterruptedException {
        testContentAddressableStream(BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testContentAddressableStreamPermanentBlob() throws IOException, InterruptedException {
        testContentAddressableStream(BlobType.PERMANENT_BLOB);
    }

    @Test
    public void testGetFailsDuringStreamingNoJobTransientBlob() throws IOException {
        testGetFailsDuringStreaming(null, BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetFailsDuringStreamingForJobTransientBlob() throws IOException {
        testGetFailsDuringStreaming(new JobID(), BlobType.TRANSIENT_BLOB);
    }

    @Test
    public void testGetFailsDuringStreamingForJobPermanentBlob() throws IOException {
        testGetFailsDuringStreaming(new JobID(), BlobType.PERMANENT_BLOB);
    }

    /**
     * Tests the static {@link BlobClient#uploadFiles(InetSocketAddress, Configuration, JobID, List)} helper.
     */
    @Test
    public void testUploadJarFilesHelper() throws Exception {
        BlobClientTest.uploadJarFile(getBlobServer(), getBlobClientConfig());
    }
}

