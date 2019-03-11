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
package org.apache.hadoop.fs.azurebfs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test copy operation.
 */
public class ITestAzureBlobFileSystemCopy extends AbstractAbfsIntegrationTest {
    public ITestAzureBlobFileSystemCopy() throws Exception {
        super();
    }

    @Test
    public void testCopyFromLocalFileSystem() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Path localFilePath = new Path(System.getProperty("test.build.data", "azure_test"));
        FileSystem localFs = FileSystem.getLocal(new Configuration());
        localFs.delete(localFilePath, true);
        try {
            writeString(localFs, localFilePath, "Testing");
            Path dstPath = new Path("copiedFromLocal");
            Assert.assertTrue(FileUtil.copy(localFs, localFilePath, fs, dstPath, false, fs.getConf()));
            assertIsFile(fs, dstPath);
            Assert.assertEquals("Testing", readString(fs, dstPath));
            fs.delete(dstPath, true);
        } finally {
            localFs.delete(localFilePath, true);
        }
    }
}

