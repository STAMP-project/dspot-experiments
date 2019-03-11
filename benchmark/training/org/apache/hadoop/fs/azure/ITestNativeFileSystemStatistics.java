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


import AzureNativeFileSystemStore.KEY_ATOMIC_RENAME_DIRECTORIES;
import AzureNativeFileSystemStore.KEY_PAGE_BLOB_DIRECTORIES;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Because FileSystem.Statistics is per FileSystem, so statistics can not be ran in
 * parallel, hence in this test file, force them to run in sequential.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITestNativeFileSystemStatistics extends AbstractWasbTestWithTimeout {
    @Test
    public void test_001_NativeAzureFileSystemMocked() throws Exception {
        AzureBlobStorageTestAccount testAccount = AzureBlobStorageTestAccount.createMock();
        Assume.assumeNotNull(testAccount);
        testStatisticsWithAccount(testAccount);
    }

    @Test
    public void test_002_NativeAzureFileSystemPageBlobLive() throws Exception {
        Configuration conf = new Configuration();
        // Configure the page blob directories key so every file created is a page blob.
        conf.set(KEY_PAGE_BLOB_DIRECTORIES, "/");
        // Configure the atomic rename directories key so every folder will have
        // atomic rename applied.
        conf.set(KEY_ATOMIC_RENAME_DIRECTORIES, "/");
        AzureBlobStorageTestAccount testAccount = AzureBlobStorageTestAccount.create(conf);
        Assume.assumeNotNull(testAccount);
        testStatisticsWithAccount(testAccount);
    }

    @Test
    public void test_003_NativeAzureFileSystem() throws Exception {
        AzureBlobStorageTestAccount testAccount = AzureBlobStorageTestAccount.create();
        Assume.assumeNotNull(testAccount);
        testStatisticsWithAccount(testAccount);
    }
}

