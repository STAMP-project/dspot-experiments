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


import Rename.OVERWRITE;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestSymlinkLocalFSFileSystem extends TestSymlinkLocalFS {
    /**
     * Rename a symlink to itself
     */
    @Override
    @Test(timeout = 10000)
    public void testRenameSymlinkToItself() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        SymlinkBaseTest.createAndWriteFile(file);
        Path link = new Path(testBaseDir1(), "linkToFile1");
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(link, link);
            Assert.fail("Failed to get expected IOException");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Fails with overwrite as well
        try {
            SymlinkBaseTest.wrapper.rename(link, link, OVERWRITE);
            Assert.fail("Failed to get expected IOException");
        } catch (IOException e) {
            // Todo: Fix this test when HADOOP-9819 is fixed.
            Assert.assertTrue((((unwrapException(e)) instanceof FileAlreadyExistsException) || ((unwrapException(e)) instanceof FileNotFoundException)));
        }
    }
}

