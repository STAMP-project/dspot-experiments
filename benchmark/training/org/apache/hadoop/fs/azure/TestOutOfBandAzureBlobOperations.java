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


import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that WASB handles things gracefully when users add blobs to the Azure
 * Storage container from outside WASB's control.
 */
public class TestOutOfBandAzureBlobOperations extends AbstractWasbTestWithTimeout {
    private AzureBlobStorageTestAccount testAccount;

    private FileSystem fs;

    private InMemoryBlockBlobStore backingStore;

    @SuppressWarnings("deprecation")
    @Test
    public void testImplicitFolderListed() throws Exception {
        createEmptyBlobOutOfBand("root/b");
        // List the blob itself.
        FileStatus[] obtained = fs.listStatus(new Path("/root/b"));
        Assert.assertNotNull(obtained);
        Assert.assertEquals(1, obtained.length);
        Assert.assertFalse(obtained[0].isDirectory());
        Assert.assertEquals("/root/b", obtained[0].getPath().toUri().getPath());
        // List the directory
        obtained = fs.listStatus(new Path("/root"));
        Assert.assertNotNull(obtained);
        Assert.assertEquals(1, obtained.length);
        Assert.assertFalse(obtained[0].isDirectory());
        Assert.assertEquals("/root/b", obtained[0].getPath().toUri().getPath());
        // Get the directory's file status
        FileStatus dirStatus = fs.getFileStatus(new Path("/root"));
        Assert.assertNotNull(dirStatus);
        Assert.assertTrue(dirStatus.isDirectory());
        Assert.assertEquals("/root", dirStatus.getPath().toUri().getPath());
    }

    @Test
    public void testImplicitFolderDeleted() throws Exception {
        createEmptyBlobOutOfBand("root/b");
        Assert.assertTrue(fs.exists(new Path("/root")));
        Assert.assertTrue(fs.delete(new Path("/root"), true));
        Assert.assertFalse(fs.exists(new Path("/root")));
    }

    @Test
    public void testFileInImplicitFolderDeleted() throws Exception {
        createEmptyBlobOutOfBand("root/b");
        Assert.assertTrue(fs.exists(new Path("/root")));
        Assert.assertTrue(fs.delete(new Path("/root/b"), true));
        Assert.assertTrue(fs.exists(new Path("/root")));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFileAndImplicitFolderSameName() throws Exception {
        createEmptyBlobOutOfBand("root/b");
        createEmptyBlobOutOfBand("root/b/c");
        FileStatus[] listResult = fs.listStatus(new Path("/root/b"));
        // File should win.
        Assert.assertEquals(1, listResult.length);
        Assert.assertFalse(listResult[0].isDirectory());
        try {
            // Trying to delete root/b/c would cause a dilemma for WASB, so
            // it should throw.
            fs.delete(new Path("/root/b/c"), true);
            Assert.assertTrue("Should've thrown.", false);
        } catch (AzureException e) {
            Assert.assertEquals(("File /root/b/c has a parent directory /root/b" + " which is also a file. Can't resolve."), e.getMessage());
        }
    }

    private static enum DeepCreateTestVariation {

        File,
        Folder;}

    /**
     * Tests that when we create the file (or folder) x/y/z, we also create
     * explicit folder blobs for x and x/y
     */
    @Test
    public void testCreatingDeepFileCreatesExplicitFolder() throws Exception {
        for (TestOutOfBandAzureBlobOperations.DeepCreateTestVariation variation : TestOutOfBandAzureBlobOperations.DeepCreateTestVariation.values()) {
            switch (variation) {
                case File :
                    Assert.assertTrue(fs.createNewFile(new Path("/x/y/z")));
                    break;
                case Folder :
                    Assert.assertTrue(fs.mkdirs(new Path("/x/y/z")));
                    break;
            }
            Assert.assertTrue(backingStore.exists(AzureBlobStorageTestAccount.toMockUri("x")));
            Assert.assertTrue(backingStore.exists(AzureBlobStorageTestAccount.toMockUri("x/y")));
            fs.delete(new Path("/x"), true);
        }
    }

    @Test
    public void testSetPermissionOnImplicitFolder() throws Exception {
        createEmptyBlobOutOfBand("root/b");
        FsPermission newPermission = new FsPermission(((short) (384)));
        fs.setPermission(new Path("/root"), newPermission);
        FileStatus newStatus = fs.getFileStatus(new Path("/root"));
        Assert.assertNotNull(newStatus);
        Assert.assertEquals(newPermission, newStatus.getPermission());
    }

    @Test
    public void testSetOwnerOnImplicitFolder() throws Exception {
        createEmptyBlobOutOfBand("root/b");
        fs.setOwner(new Path("/root"), "newOwner", null);
        FileStatus newStatus = fs.getFileStatus(new Path("/root"));
        Assert.assertNotNull(newStatus);
        Assert.assertEquals("newOwner", newStatus.getOwner());
    }
}

