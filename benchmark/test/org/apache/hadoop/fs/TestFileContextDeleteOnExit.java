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
package org.apache.hadoop.fs;


import FileContext.FINALIZER;
import org.apache.hadoop.util.ShutdownHookManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link FileContext.#deleteOnExit(Path)} functionality.
 */
public class TestFileContextDeleteOnExit {
    private static int blockSize = 1024;

    private static int numBlocks = 2;

    private final FileContextTestHelper helper = new FileContextTestHelper();

    private FileContext fc;

    @Test
    public void testDeleteOnExit() throws Exception {
        // Create deleteOnExit entries
        Path file1 = helper.getTestRootPath(fc, "file1");
        FileContextTestHelper.createFile(fc, file1, TestFileContextDeleteOnExit.numBlocks, TestFileContextDeleteOnExit.blockSize);
        fc.deleteOnExit(file1);
        checkDeleteOnExitData(1, fc, file1);
        // Ensure shutdown hook is added
        Assert.assertTrue(ShutdownHookManager.get().hasShutdownHook(FINALIZER));
        Path file2 = helper.getTestRootPath(fc, "dir1/file2");
        FileContextTestHelper.createFile(fc, file2, TestFileContextDeleteOnExit.numBlocks, TestFileContextDeleteOnExit.blockSize);
        fc.deleteOnExit(file2);
        checkDeleteOnExitData(1, fc, file1, file2);
        Path dir = helper.getTestRootPath(fc, "dir3/dir4/dir5/dir6");
        FileContextTestHelper.createFile(fc, dir, TestFileContextDeleteOnExit.numBlocks, TestFileContextDeleteOnExit.blockSize);
        fc.deleteOnExit(dir);
        checkDeleteOnExitData(1, fc, file1, file2, dir);
        // trigger deleteOnExit and ensure the registered
        // paths are cleaned up
        FINALIZER.run();
        checkDeleteOnExitData(0, fc, new Path[0]);
        Assert.assertFalse(FileContextTestHelper.exists(fc, file1));
        Assert.assertFalse(FileContextTestHelper.exists(fc, file2));
        Assert.assertFalse(FileContextTestHelper.exists(fc, dir));
    }
}

