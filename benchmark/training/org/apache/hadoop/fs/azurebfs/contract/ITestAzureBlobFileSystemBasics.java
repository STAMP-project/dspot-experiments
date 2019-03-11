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
package org.apache.hadoop.fs.azurebfs.contract;


import java.io.IOException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystemContractBaseTest;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic Contract test for Azure BlobFileSystem.
 */
public class ITestAzureBlobFileSystemBasics extends FileSystemContractBaseTest {
    private final ABFSContractTestBinding binding;

    public ITestAzureBlobFileSystemBasics() throws Exception {
        // If all contract tests are running in parallel, some root level tests in FileSystemContractBaseTest will fail
        // due to the race condition. Hence for this contract test it should be tested in different container
        binding = new ABFSContractTestBinding(false);
    }

    @Test
    public void testListOnFolderWithNoChildren() throws IOException {
        Assert.assertTrue(fs.mkdirs(path("testListStatus/c/1")));
        FileStatus[] paths;
        paths = fs.listStatus(path("testListStatus"));
        Assert.assertEquals(1, paths.length);
        // ListStatus on folder with child
        paths = fs.listStatus(path("testListStatus/c"));
        Assert.assertEquals(1, paths.length);
        // Remove the child and listStatus
        fs.delete(path("testListStatus/c/1"), true);
        paths = fs.listStatus(path("testListStatus/c"));
        Assert.assertEquals(0, paths.length);
        Assert.assertTrue(fs.delete(path("testListStatus"), true));
    }

    @Test
    public void testListOnfileAndFolder() throws IOException {
        Path folderPath = path("testListStatus/folder");
        Path filePath = path("testListStatus/file");
        Assert.assertTrue(fs.mkdirs(folderPath));
        ContractTestUtils.touch(fs, filePath);
        FileStatus[] listFolderStatus;
        listFolderStatus = fs.listStatus(path("testListStatus"));
        Assert.assertEquals(filePath, listFolderStatus[0].getPath());
        // List on file should return absolute path
        FileStatus[] listFileStatus = fs.listStatus(filePath);
        Assert.assertEquals(filePath, listFileStatus[0].getPath());
    }
}

