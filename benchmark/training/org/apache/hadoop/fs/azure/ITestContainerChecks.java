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


import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static org.apache.hadoop.fs.azure.AzureBlobStorageTestAccount.CreateOptions.UseSas;


/**
 * Tests that WASB creates containers only if needed.
 */
public class ITestContainerChecks extends AbstractWasbTestWithTimeout {
    private AzureBlobStorageTestAccount testAccount;

    private boolean runningInSASMode = false;

    @Test
    public void testContainerExistAfterDoesNotExist() throws Exception {
        testAccount = blobStorageTestAccount();
        Assume.assumeNotNull(testAccount);
        CloudBlobContainer container = testAccount.getRealContainer();
        FileSystem fs = testAccount.getFileSystem();
        // Starting off with the container not there
        Assert.assertFalse(container.exists());
        // A list shouldn't create the container and will set file system store
        // state to DoesNotExist
        try {
            fs.listStatus(new Path("/"));
            Assert.assertTrue("Should've thrown.", false);
        } catch (FileNotFoundException ex) {
            Assert.assertTrue(("Unexpected exception: " + ex), ex.getMessage().contains("is not found"));
        }
        Assert.assertFalse(container.exists());
        // Create a container outside of the WASB FileSystem
        container.create();
        // Add a file to the container outside of the WASB FileSystem
        CloudBlockBlob blob = testAccount.getBlobReference("foo");
        BlobOutputStream outputStream = blob.openOutputStream();
        outputStream.write(new byte[10]);
        outputStream.close();
        // Make sure the file is visible
        Assert.assertTrue(fs.exists(new Path("/foo")));
        Assert.assertTrue(container.exists());
    }

    @Test
    public void testContainerCreateAfterDoesNotExist() throws Exception {
        testAccount = blobStorageTestAccount();
        Assume.assumeNotNull(testAccount);
        CloudBlobContainer container = testAccount.getRealContainer();
        FileSystem fs = testAccount.getFileSystem();
        // Starting off with the container not there
        Assert.assertFalse(container.exists());
        // A list shouldn't create the container and will set file system store
        // state to DoesNotExist
        try {
            Assert.assertNull(fs.listStatus(new Path("/")));
            Assert.assertTrue("Should've thrown.", false);
        } catch (FileNotFoundException ex) {
            Assert.assertTrue(("Unexpected exception: " + ex), ex.getMessage().contains("is not found"));
        }
        Assert.assertFalse(container.exists());
        // Write should succeed
        Assert.assertTrue(fs.createNewFile(new Path("/foo")));
        Assert.assertTrue(container.exists());
    }

    @Test
    public void testContainerCreateOnWrite() throws Exception {
        testAccount = blobStorageTestAccount();
        Assume.assumeNotNull(testAccount);
        CloudBlobContainer container = testAccount.getRealContainer();
        FileSystem fs = testAccount.getFileSystem();
        // Starting off with the container not there
        Assert.assertFalse(container.exists());
        // A list shouldn't create the container.
        try {
            fs.listStatus(new Path("/"));
            Assert.assertTrue("Should've thrown.", false);
        } catch (FileNotFoundException ex) {
            Assert.assertTrue(("Unexpected exception: " + ex), ex.getMessage().contains("is not found"));
        }
        Assert.assertFalse(container.exists());
        // Neither should a read.
        Path foo = new Path("/testContainerCreateOnWrite-foo");
        Path bar = new Path("/testContainerCreateOnWrite-bar");
        LambdaTestUtils.intercept(FileNotFoundException.class, new Callable<String>() {
            @Override
            public String call() throws Exception {
                fs.open(foo).close();
                return "Stream to " + foo;
            }
        });
        Assert.assertFalse(container.exists());
        // Neither should a rename
        Assert.assertFalse(fs.rename(foo, bar));
        Assert.assertFalse(container.exists());
        // But a write should.
        Assert.assertTrue(fs.createNewFile(foo));
        Assert.assertTrue(container.exists());
    }

    @Test
    public void testContainerChecksWithSas() throws Exception {
        Assume.assumeFalse(runningInSASMode);
        testAccount = AzureBlobStorageTestAccount.create("", EnumSet.of(UseSas));
        Assume.assumeNotNull(testAccount);
        CloudBlobContainer container = testAccount.getRealContainer();
        FileSystem fs = testAccount.getFileSystem();
        // The container shouldn't be there
        Assert.assertFalse(container.exists());
        // A write should just fail
        try {
            fs.createNewFile(new Path("/testContainerChecksWithSas-foo"));
            Assert.assertFalse("Should've thrown.", true);
        } catch (AzureException ex) {
        }
        Assert.assertFalse(container.exists());
    }
}

