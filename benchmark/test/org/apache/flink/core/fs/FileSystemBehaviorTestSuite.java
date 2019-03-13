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
package org.apache.flink.core.fs;


import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static FileSystemKind.OBJECT_STORE;


/**
 * Common tests for the behavior of {@link FileSystem} methods.
 */
public abstract class FileSystemBehaviorTestSuite {
    private static final Random RND = new Random();

    /**
     * The cached file system instance.
     */
    private FileSystem fs;

    /**
     * The cached base path.
     */
    private Path basePath;

    // ------------------------------------------------------------------------
    // Suite of Tests
    // ------------------------------------------------------------------------
    // --- file system kind
    @Test
    public void testFileSystemKind() {
        Assert.assertEquals(getFileSystemKind(), fs.getKind());
    }

    // --- access and scheme
    @Test
    public void testPathAndScheme() throws Exception {
        Assert.assertEquals(fs.getUri(), getBasePath().getFileSystem().getUri());
        Assert.assertEquals(fs.getUri().getScheme(), getBasePath().toUri().getScheme());
    }

    @Test
    public void testHomeAndWorkDir() {
        Assert.assertEquals(fs.getUri().getScheme(), fs.getWorkingDirectory().toUri().getScheme());
        Assert.assertEquals(fs.getUri().getScheme(), fs.getHomeDirectory().toUri().getScheme());
    }

    // --- mkdirs
    @Test
    public void testMkdirsReturnsTrueWhenCreatingDirectory() throws Exception {
        // this test applies to object stores as well, as rely on the fact that they
        // return true when things are not bad
        final Path directory = new Path(basePath, FileSystemBehaviorTestSuite.randomName());
        Assert.assertTrue(fs.mkdirs(directory));
        if ((getFileSystemKind()) != (OBJECT_STORE)) {
            Assert.assertTrue(fs.exists(directory));
        }
    }

    @Test
    public void testMkdirsCreatesParentDirectories() throws Exception {
        // this test applies to object stores as well, as rely on the fact that they
        // return true when things are not bad
        final Path directory = new Path(new Path(new Path(basePath, FileSystemBehaviorTestSuite.randomName()), FileSystemBehaviorTestSuite.randomName()), FileSystemBehaviorTestSuite.randomName());
        Assert.assertTrue(fs.mkdirs(directory));
        if ((getFileSystemKind()) != (OBJECT_STORE)) {
            Assert.assertTrue(fs.exists(directory));
        }
    }

    @Test
    public void testMkdirsReturnsTrueForExistingDirectory() throws Exception {
        // this test applies to object stores as well, as rely on the fact that they
        // return true when things are not bad
        final Path directory = new Path(basePath, FileSystemBehaviorTestSuite.randomName());
        // make sure the directory exists
        createRandomFileInDirectory(directory);
        Assert.assertTrue(fs.mkdirs(directory));
    }

    @Test
    public void testMkdirsFailsForExistingFile() throws Exception {
        // test is not defined for object stores, they have no proper notion
        // of directories
        assumeNotObjectStore();
        final Path file = new Path(getBasePath(), FileSystemBehaviorTestSuite.randomName());
        createFile(file);
        try {
            fs.mkdirs(file);
            Assert.fail("should fail with an IOException");
        } catch (IOException e) {
            // good!
        }
    }

    @Test
    public void testMkdirsFailsWithExistingParentFile() throws Exception {
        // test is not defined for object stores, they have no proper notion
        // of directories
        assumeNotObjectStore();
        final Path file = new Path(getBasePath(), FileSystemBehaviorTestSuite.randomName());
        createFile(file);
        final Path dirUnderFile = new Path(file, FileSystemBehaviorTestSuite.randomName());
        try {
            fs.mkdirs(dirUnderFile);
            Assert.fail("should fail with an IOException");
        } catch (IOException e) {
            // good!
        }
    }
}

