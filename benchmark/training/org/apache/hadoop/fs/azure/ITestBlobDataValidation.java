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
package org.apache.hadoop.fs.azure;


import Constants.HeaderConstants.CONTENT_MD5;
import Constants.HeaderConstants.STORAGE_RANGE_HEADER;
import com.microsoft.azure.storage.ResponseReceivedEvent;
import com.microsoft.azure.storage.StorageEvent;
import java.net.HttpURLConnection;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test that we do proper data integrity validation with MD5 checks as
 * configured.
 */
public class ITestBlobDataValidation extends AbstractWasbTestWithTimeout {
    private AzureBlobStorageTestAccount testAccount;

    /**
     * Test that by default we don't store the blob-level MD5.
     */
    @Test
    public void testBlobMd5StoreOffByDefault() throws Exception {
        testAccount = AzureBlobStorageTestAccount.create();
        testStoreBlobMd5(false);
    }

    /**
     * Test that we get blob-level MD5 storage and validation if we specify that
     * in the configuration.
     */
    @Test
    public void testStoreBlobMd5() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(AzureNativeFileSystemStore.KEY_STORE_BLOB_MD5, true);
        testAccount = AzureBlobStorageTestAccount.create(conf);
        testStoreBlobMd5(true);
    }

    /**
     * Test that by default we check block-level MD5.
     */
    @Test
    public void testCheckBlockMd5() throws Exception {
        testAccount = AzureBlobStorageTestAccount.create();
        testCheckBlockMd5(true);
    }

    /**
     * Test that we don't check block-level MD5 if we specify that in the
     * configuration.
     */
    @Test
    public void testDontCheckBlockMd5() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(AzureNativeFileSystemStore.KEY_CHECK_BLOCK_MD5, false);
        testAccount = AzureBlobStorageTestAccount.create(conf);
        testCheckBlockMd5(false);
    }

    /**
     * Connection inspector to check that MD5 fields for content is set/not set as
     * expected.
     */
    private static class ContentMD5Checker extends StorageEvent<ResponseReceivedEvent> {
        private final boolean expectMd5;

        public ContentMD5Checker(boolean expectMd5) {
            this.expectMd5 = expectMd5;
        }

        @Override
        public void eventOccurred(ResponseReceivedEvent eventArg) {
            HttpURLConnection connection = ((HttpURLConnection) (eventArg.getConnectionObject()));
            if (ITestBlobDataValidation.ContentMD5Checker.isGetRange(connection)) {
                checkObtainedMd5(connection.getHeaderField(CONTENT_MD5));
            } else
                if (ITestBlobDataValidation.ContentMD5Checker.isPutBlock(connection)) {
                    checkObtainedMd5(connection.getRequestProperty(CONTENT_MD5));
                }

        }

        private void checkObtainedMd5(String obtainedMd5) {
            if (expectMd5) {
                Assert.assertNotNull(obtainedMd5);
            } else {
                Assert.assertNull(("Expected no MD5, found: " + obtainedMd5), obtainedMd5);
            }
        }

        private static boolean isPutBlock(HttpURLConnection connection) {
            return ((connection.getRequestMethod().equals("PUT")) && ((connection.getURL().getQuery()) != null)) && (connection.getURL().getQuery().contains("blockid"));
        }

        private static boolean isGetRange(HttpURLConnection connection) {
            return (connection.getRequestMethod().equals("GET")) && ((connection.getHeaderField(STORAGE_RANGE_HEADER)) != null);
        }
    }
}

