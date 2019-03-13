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


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestSymlinkHdfsFileSystem extends TestSymlinkHdfs {
    // Additional tests for DFS-only methods
    @Test(timeout = 10000)
    public void testRecoverLease() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "link");
        wrapper.setWorkingDirectory(dir);
        createAndWriteFile(file);
        wrapper.createSymlink(file, link, false);
        // Attempt recoverLease through a symlink
        boolean closed = TestSymlinkHdfs.dfs.recoverLease(link);
        Assert.assertTrue("Expected recoverLease to return true", closed);
    }

    @Test(timeout = 10000)
    public void testIsFileClosed() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "link");
        wrapper.setWorkingDirectory(dir);
        createAndWriteFile(file);
        wrapper.createSymlink(file, link, false);
        // Attempt recoverLease through a symlink
        boolean closed = TestSymlinkHdfs.dfs.isFileClosed(link);
        Assert.assertTrue("Expected isFileClosed to return true", closed);
    }

    @Test(timeout = 10000)
    public void testConcat() throws Exception {
        Path dir = new Path(testBaseDir1());
        Path link = new Path(testBaseDir1(), "link");
        Path dir2 = new Path(testBaseDir2());
        wrapper.createSymlink(dir2, link, false);
        wrapper.setWorkingDirectory(dir);
        // Concat with a target and srcs through a link
        Path target = new Path(link, "target");
        createAndWriteFile(target);
        Path[] srcs = new Path[3];
        for (int i = 0; i < (srcs.length); i++) {
            srcs[i] = new Path(link, ("src-" + i));
            createAndWriteFile(srcs[i]);
        }
        TestSymlinkHdfs.dfs.concat(target, srcs);
    }

    @Test(timeout = 10000)
    public void testSnapshot() throws Exception {
        Path dir = new Path(testBaseDir1());
        Path link = new Path(testBaseDir1(), "link");
        Path dir2 = new Path(testBaseDir2());
        wrapper.createSymlink(dir2, link, false);
        wrapper.setWorkingDirectory(dir);
        TestSymlinkHdfs.dfs.allowSnapshot(link);
        TestSymlinkHdfs.dfs.disallowSnapshot(link);
        TestSymlinkHdfs.dfs.allowSnapshot(link);
        TestSymlinkHdfs.dfs.createSnapshot(link, "mcmillan");
        TestSymlinkHdfs.dfs.renameSnapshot(link, "mcmillan", "seaborg");
        TestSymlinkHdfs.dfs.deleteSnapshot(link, "seaborg");
    }
}

