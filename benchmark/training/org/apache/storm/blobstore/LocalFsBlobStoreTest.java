/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.blobstore;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.KeyAlreadyExistsException;
import org.apache.storm.generated.KeyNotFoundException;
import org.apache.storm.generated.SettableBlobMeta;
import org.apache.storm.testing.InProcessZookeeper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BlobStoreAclHandler.WORLD_EVERYTHING;


public class LocalFsBlobStoreTest {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFsBlobStoreTest.class);

    URI base;

    File baseFile;

    private static Map<String, Object> conf = new HashMap();

    public static final int READ = 1;

    public static final int WRITE = 2;

    public static final int ADMIN = 4;

    private InProcessZookeeper zk;

    @Test
    public void testLocalFsWithAuth() throws Exception {
        testWithAuthentication(initLocalFs());
    }

    @Test
    public void testBasicLocalFs() throws Exception {
        testBasic(initLocalFs());
    }

    @Test
    public void testMultipleLocalFs() throws Exception {
        testMultiple(initLocalFs());
    }

    @Test
    public void testDeleteAfterFailedCreate() throws Exception {
        // Check that a blob can be deleted when a temporary file exists in the blob directory
        LocalFsBlobStore store = initLocalFs();
        String key = "test";
        SettableBlobMeta metadata = new SettableBlobMeta(WORLD_EVERYTHING);
        try (AtomicOutputStream out = store.createBlob(key, metadata, null)) {
            out.write(1);
            File blobDir = store.getKeyDataDir(key);
            Files.createFile(blobDir.toPath().resolve("tempFile.tmp"));
        }
        store.deleteBlob("test", null);
    }

    @Test
    public void testGetFileLength() throws IOException, AuthorizationException, KeyAlreadyExistsException, KeyNotFoundException {
        LocalFsBlobStore store = initLocalFs();
        try (AtomicOutputStream out = store.createBlob("test", new SettableBlobMeta(WORLD_EVERYTHING), null)) {
            out.write(1);
        }
        try (InputStreamWithMeta blobInputStream = store.getBlob("test", null)) {
            Assert.assertEquals(1, blobInputStream.getFileLength());
        }
    }
}

