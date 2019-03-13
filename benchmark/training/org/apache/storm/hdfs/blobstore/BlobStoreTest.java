/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.hdfs.blobstore;


import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.storm.hdfs.testing.MiniDFSClusterRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BlobStoreTest {
    @ClassRule
    public static final MiniDFSClusterRule DFS_CLUSTER_RULE = new MiniDFSClusterRule();

    private static final Logger LOG = LoggerFactory.getLogger(BlobStoreTest.class);

    URI base;

    File baseFile;

    private static final Map<String, Object> CONF = new HashMap<>();

    public static final int READ = 1;

    public static final int WRITE = 2;

    public static final int ADMIN = 4;

    private static class AutoCloseableBlobStoreContainer implements AutoCloseable {
        private final HdfsBlobStore blobStore;

        public AutoCloseableBlobStoreContainer(HdfsBlobStore blobStore) {
            this.blobStore = blobStore;
        }

        @Override
        public void close() throws Exception {
            this.blobStore.shutdown();
        }
    }

    @Test
    public void testHdfsReplication() throws Exception {
        try (BlobStoreTest.AutoCloseableBlobStoreContainer container = initHdfs("/storm/blobstoreReplication")) {
            testReplication("/storm/blobstoreReplication/test", container.blobStore);
        }
    }

    @Test
    public void testBasicHdfs() throws Exception {
        try (BlobStoreTest.AutoCloseableBlobStoreContainer container = initHdfs("/storm/blobstore1")) {
            testBasic(container.blobStore);
        }
    }

    @Test
    public void testMultipleHdfs() throws Exception {
        // use different blobstore dir so it doesn't conflict with other test
        try (BlobStoreTest.AutoCloseableBlobStoreContainer container = initHdfs("/storm/blobstore2")) {
            testMultiple(container.blobStore);
        }
    }

    @Test
    public void testHdfsWithAuth() throws Exception {
        // use different blobstore dir so it doesn't conflict with other tests
        try (BlobStoreTest.AutoCloseableBlobStoreContainer container = initHdfs("/storm/blobstore3")) {
            testWithAuthentication(container.blobStore);
        }
    }
}

