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
package org.apache.drill.exec.store;


import StorageStrategy.DEFAULT;
import java.io.IOException;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;


public class StorageStrategyTest {
    private static final FsPermission FULL_PERMISSION = FsPermission.getDirDefault();

    private static final StorageStrategy PERSISTENT_STRATEGY = new StorageStrategy("002", false);

    private static final StorageStrategy TEMPORARY_STRATEGY = new StorageStrategy("077", true);

    private static FileSystem fs;

    @Test
    public void testPermissionAndDeleteOnExitFalseForFileWithParent() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path file = addNLevelsAndFile(initialPath, 2, true);
        Path firstCreatedParentPath = addNLevelsAndFile(initialPath, 1, false);
        Path createdParentPath = StorageStrategyTest.PERSISTENT_STRATEGY.createFileAndApply(StorageStrategyTest.fs, file);
        Assert.assertEquals("Path should match", firstCreatedParentPath, createdParentPath);
        checkPathAndPermission(initialPath, file, true, 2, StorageStrategyTest.PERSISTENT_STRATEGY);
        checkDeleteOnExit(firstCreatedParentPath, false);
    }

    @Test
    public void testPermissionAndDeleteOnExitTrueForFileWithParent() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path file = addNLevelsAndFile(initialPath, 2, true);
        Path firstCreatedParentPath = addNLevelsAndFile(initialPath, 1, false);
        Path createdParentPath = StorageStrategyTest.TEMPORARY_STRATEGY.createFileAndApply(StorageStrategyTest.fs, file);
        Assert.assertEquals("Path should match", firstCreatedParentPath, createdParentPath);
        checkPathAndPermission(initialPath, file, true, 2, StorageStrategyTest.TEMPORARY_STRATEGY);
        checkDeleteOnExit(firstCreatedParentPath, true);
    }

    @Test
    public void testPermissionAndDeleteOnExitFalseForFileOnly() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path file = addNLevelsAndFile(initialPath, 0, true);
        Path createdFile = StorageStrategyTest.PERSISTENT_STRATEGY.createFileAndApply(StorageStrategyTest.fs, file);
        Assert.assertEquals("Path should match", file, createdFile);
        checkPathAndPermission(initialPath, file, true, 0, StorageStrategyTest.PERSISTENT_STRATEGY);
        checkDeleteOnExit(file, false);
    }

    @Test
    public void testPermissionAndDeleteOnExitTrueForFileOnly() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path file = addNLevelsAndFile(initialPath, 0, true);
        Path createdFile = StorageStrategyTest.TEMPORARY_STRATEGY.createFileAndApply(StorageStrategyTest.fs, file);
        Assert.assertEquals("Path should match", file, createdFile);
        checkPathAndPermission(initialPath, file, true, 0, StorageStrategyTest.TEMPORARY_STRATEGY);
        checkDeleteOnExit(file, true);
    }

    @Test(expected = IOException.class)
    public void testFailureOnExistentFile() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path file = addNLevelsAndFile(initialPath, 0, true);
        StorageStrategyTest.fs.createNewFile(file);
        Assert.assertTrue("File should exist", StorageStrategyTest.fs.exists(file));
        try {
            StorageStrategyTest.PERSISTENT_STRATEGY.createFileAndApply(StorageStrategyTest.fs, file);
        } catch (IOException e) {
            Assert.assertEquals("Error message should match", String.format("File [%s] already exists on file system [%s].", file.toUri().getPath(), StorageStrategyTest.fs.getUri()), e.getMessage());
            throw e;
        }
    }

    @Test
    public void testCreatePathAndDeleteOnExitFalse() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path resultPath = addNLevelsAndFile(initialPath, 2, false);
        Path firstCreatedParentPath = addNLevelsAndFile(initialPath, 1, false);
        Path createdParentPath = StorageStrategyTest.PERSISTENT_STRATEGY.createPathAndApply(StorageStrategyTest.fs, resultPath);
        Assert.assertEquals("Path should match", firstCreatedParentPath, createdParentPath);
        checkPathAndPermission(initialPath, resultPath, false, 2, StorageStrategyTest.PERSISTENT_STRATEGY);
        checkDeleteOnExit(firstCreatedParentPath, false);
    }

    @Test
    public void testCreatePathAndDeleteOnExitTrue() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path resultPath = addNLevelsAndFile(initialPath, 2, false);
        Path firstCreatedParentPath = addNLevelsAndFile(initialPath, 1, false);
        Path createdParentPath = StorageStrategyTest.TEMPORARY_STRATEGY.createPathAndApply(StorageStrategyTest.fs, resultPath);
        Assert.assertEquals("Path should match", firstCreatedParentPath, createdParentPath);
        checkPathAndPermission(initialPath, resultPath, false, 2, StorageStrategyTest.TEMPORARY_STRATEGY);
        checkDeleteOnExit(firstCreatedParentPath, true);
    }

    @Test
    public void testCreateNoPath() throws Exception {
        Path path = prepareStorageDirectory();
        Path createdParentPath = StorageStrategyTest.TEMPORARY_STRATEGY.createPathAndApply(StorageStrategyTest.fs, path);
        Assert.assertNull("Path should be null", createdParentPath);
        Assert.assertEquals("Permission should match", StorageStrategyTest.FULL_PERMISSION, StorageStrategyTest.fs.getFileStatus(path).getPermission());
    }

    @Test
    public void testStrategyForExistingFile() throws Exception {
        Path initialPath = prepareStorageDirectory();
        Path file = addNLevelsAndFile(initialPath, 0, true);
        StorageStrategyTest.fs.createNewFile(file);
        StorageStrategyTest.fs.setPermission(file, StorageStrategyTest.FULL_PERMISSION);
        Assert.assertTrue("File should exist", StorageStrategyTest.fs.exists(file));
        Assert.assertEquals("Permission should match", StorageStrategyTest.FULL_PERMISSION, StorageStrategyTest.fs.getFileStatus(file).getPermission());
        StorageStrategyTest.TEMPORARY_STRATEGY.applyToFile(StorageStrategyTest.fs, file);
        Assert.assertEquals("Permission should match", new FsPermission(StorageStrategyTest.TEMPORARY_STRATEGY.getFilePermission()), StorageStrategyTest.fs.getFileStatus(file).getPermission());
        checkDeleteOnExit(file, true);
    }

    @Test
    public void testInvalidUmask() throws Exception {
        for (String invalid : Lists.newArrayList("ABC", "999", null)) {
            StorageStrategy storageStrategy = new StorageStrategy(invalid, true);
            Assert.assertEquals("Umask value should match default", DEFAULT.getUmask(), storageStrategy.getUmask());
            Assert.assertTrue("deleteOnExit flag should be set to true", storageStrategy.isDeleteOnExit());
        }
    }
}

