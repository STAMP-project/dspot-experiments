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


import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the scenario where a colon is included in the file/directory name.
 *
 * NativeAzureFileSystem#create(), #mkdir(), and #rename() disallow the
 * creation/rename of files/directories through WASB that have colons in the
 * names.
 */
public class TestNativeAzureFileSystemFileNameCheck extends AbstractWasbTestBase {
    private String root = null;

    @Test
    public void testCreate() throws Exception {
        // positive test
        Path testFile1 = new Path(((root) + "/testFile1"));
        Assert.assertTrue(fs.createNewFile(testFile1));
        // negative test
        Path testFile2 = new Path(((root) + "/testFile2:2"));
        try {
            fs.createNewFile(testFile2);
            Assert.fail("Should've thrown.");
        } catch (IOException e) {
            // ignore
        }
    }

    @Test
    public void testRename() throws Exception {
        // positive test
        Path testFile1 = new Path(((root) + "/testFile1"));
        Assert.assertTrue(fs.createNewFile(testFile1));
        Path testFile2 = new Path(((root) + "/testFile2"));
        fs.rename(testFile1, testFile2);
        Assert.assertTrue(((!(fs.exists(testFile1))) && (fs.exists(testFile2))));
        // negative test
        Path testFile3 = new Path(((root) + "/testFile3:3"));
        try {
            fs.rename(testFile2, testFile3);
            Assert.fail("Should've thrown.");
        } catch (IOException e) {
            // ignore
        }
        Assert.assertTrue(fs.exists(testFile2));
    }

    @Test
    public void testMkdirs() throws Exception {
        // positive test
        Path testFolder1 = new Path(((root) + "/testFolder1"));
        Assert.assertTrue(fs.mkdirs(testFolder1));
        // negative test
        Path testFolder2 = new Path(((root) + "/testFolder2:2"));
        try {
            Assert.assertTrue(fs.mkdirs(testFolder2));
            Assert.fail("Should've thrown.");
        } catch (IOException e) {
            // ignore
        }
    }

    @Test
    public void testWasbFsck() throws Exception {
        // positive test
        Path testFolder1 = new Path(((root) + "/testFolder1"));
        Assert.assertTrue(fs.mkdirs(testFolder1));
        Path testFolder2 = new Path(testFolder1, "testFolder2");
        Assert.assertTrue(fs.mkdirs(testFolder2));
        Path testFolder3 = new Path(testFolder1, "testFolder3");
        Assert.assertTrue(fs.mkdirs(testFolder3));
        Path testFile1 = new Path(testFolder2, "testFile1");
        Assert.assertTrue(fs.createNewFile(testFile1));
        Path testFile2 = new Path(testFolder1, "testFile2");
        Assert.assertTrue(fs.createNewFile(testFile2));
        Assert.assertFalse(runWasbFsck(testFolder1));
        // negative test
        InMemoryBlockBlobStore backingStore = testAccount.getMockStorage().getBackingStore();
        backingStore.setContent(AzureBlobStorageTestAccount.toMockUri("testFolder1/testFolder2/test2:2"), new byte[]{ 1, 2 }, new HashMap<String, String>(), false, 0);
        Assert.assertTrue(runWasbFsck(testFolder1));
    }
}

