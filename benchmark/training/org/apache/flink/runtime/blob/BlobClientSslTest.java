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
import org.apache.flink.configuration.Configuration;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * This class contains unit tests for the {@link BlobClient} with ssl enabled.
 */
public class BlobClientSslTest extends BlobClientTest {
    /**
     * The instance of the SSL BLOB server used during the tests.
     */
    private static BlobServer blobSslServer;

    /**
     * Instance of a non-SSL BLOB server with SSL-enabled security options.
     */
    private static BlobServer blobNonSslServer;

    /**
     * The SSL blob service client configuration.
     */
    private static Configuration sslClientConfig;

    /**
     * The non-SSL blob service client configuration with SSL-enabled security options.
     */
    private static Configuration nonSslClientConfig;

    @ClassRule
    public static TemporaryFolder temporarySslFolder = new TemporaryFolder();

    /**
     * Verify ssl client to ssl server upload.
     */
    @Test
    public void testUploadJarFilesHelper() throws Exception {
        BlobClientTest.uploadJarFile(BlobClientSslTest.blobSslServer, BlobClientSslTest.sslClientConfig);
    }

    /**
     * Verify ssl client to non-ssl server failure.
     */
    @Test(expected = IOException.class)
    public void testSSLClientFailure() throws Exception {
        // SSL client connected to non-ssl server
        BlobClientTest.uploadJarFile(BlobClientTest.blobServer, BlobClientSslTest.sslClientConfig);
    }

    /**
     * Verify ssl client to non-ssl server failure.
     */
    @Test(expected = IOException.class)
    public void testSSLClientFailure2() throws Exception {
        // SSL client connected to non-ssl server
        BlobClientTest.uploadJarFile(BlobClientSslTest.blobNonSslServer, BlobClientSslTest.sslClientConfig);
    }

    /**
     * Verify non-ssl client to ssl server failure.
     */
    @Test(expected = IOException.class)
    public void testSSLServerFailure() throws Exception {
        // Non-SSL client connected to ssl server
        BlobClientTest.uploadJarFile(BlobClientSslTest.blobSslServer, BlobClientTest.clientConfig);
    }

    /**
     * Verify non-ssl client to ssl server failure.
     */
    @Test(expected = IOException.class)
    public void testSSLServerFailure2() throws Exception {
        // Non-SSL client connected to ssl server
        BlobClientTest.uploadJarFile(BlobClientSslTest.blobSslServer, BlobClientSslTest.nonSslClientConfig);
    }

    /**
     * Verify non-ssl connection sanity.
     */
    @Test
    public void testNonSSLConnection() throws Exception {
        BlobClientTest.uploadJarFile(BlobClientTest.blobServer, BlobClientTest.clientConfig);
    }

    /**
     * Verify non-ssl connection sanity.
     */
    @Test
    public void testNonSSLConnection2() throws Exception {
        BlobClientTest.uploadJarFile(BlobClientTest.blobServer, BlobClientSslTest.nonSslClientConfig);
    }

    /**
     * Verify non-ssl connection sanity.
     */
    @Test
    public void testNonSSLConnection3() throws Exception {
        BlobClientTest.uploadJarFile(BlobClientSslTest.blobNonSslServer, BlobClientTest.clientConfig);
    }

    /**
     * Verify non-ssl connection sanity.
     */
    @Test
    public void testNonSSLConnection4() throws Exception {
        BlobClientTest.uploadJarFile(BlobClientSslTest.blobNonSslServer, BlobClientSslTest.nonSslClientConfig);
    }
}

