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


import FileContext.FILE_DEFAULT_PERM;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;


public class TestLocalFSFileContextMainOperations extends FileContextMainOperationsBaseTest {
    static Path wd = null;

    @Test
    public void testFileContextNoCache() throws UnsupportedFileSystemException {
        FileContext fc1 = FileContext.getLocalFSFileContext();
        Assert.assertTrue((fc1 != (FileContextMainOperationsBaseTest.fc)));
    }

    @Test
    public void testDefaultFilePermission() throws IOException {
        Path file = fileContextTestHelper.getTestRootPath(FileContextMainOperationsBaseTest.fc, "testDefaultFilePermission");
        FileContextTestHelper.createFile(FileContextMainOperationsBaseTest.fc, file);
        FsPermission expect = FILE_DEFAULT_PERM.applyUMask(FileContextMainOperationsBaseTest.fc.getUMask());
        Assert.assertEquals(expect, FileContextMainOperationsBaseTest.fc.getFileStatus(file).getPermission());
    }
}

