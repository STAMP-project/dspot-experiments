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
package org.apache.hadoop.fs.s3a;


import MultipartUtils.UploadIterator;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


/**
 * Tests for {@link MultipartUtils}.
 */
public class ITestS3AMultipartUtils extends AbstractS3ATestBase {
    private static final int UPLOAD_LEN = 1024;

    private static final String PART_FILENAME_BASE = "pending-part";

    private static final int LIST_BATCH_SIZE = 2;

    private static final int NUM_KEYS = 5;

    /**
     * Main test case for upload part listing and iterator paging.
     *
     * @throws Exception
     * 		on failure.
     */
    @Test
    public void testListMultipartUploads() throws Exception {
        S3AFileSystem fs = getFileSystem();
        Set<MultipartTestUtils.IdKey> keySet = new HashSet<>();
        try {
            // 1. Create NUM_KEYS pending upload parts
            for (int i = 0; i < (ITestS3AMultipartUtils.NUM_KEYS); i++) {
                Path filePath = getPartFilename(i);
                String key = fs.pathToKey(filePath);
                describe("creating upload part with key %s", key);
                // create a multipart upload
                MultipartTestUtils.IdKey idKey = MultipartTestUtils.createPartUpload(fs, key, ITestS3AMultipartUtils.UPLOAD_LEN, 1);
                keySet.add(idKey);
            }
            // 2. Verify all uploads are found listing by prefix
            describe("Verifying upload list by prefix");
            MultipartUtils.UploadIterator uploads = fs.listUploads(getPartPrefix(fs));
            assertUploadsPresent(uploads, keySet);
            // 3. Verify all uploads are found listing without prefix
            describe("Verifying list all uploads");
            uploads = fs.listUploads(null);
            assertUploadsPresent(uploads, keySet);
        } finally {
            // 4. Delete all uploads we created
            MultipartTestUtils.cleanupParts(fs, keySet);
        }
    }
}

