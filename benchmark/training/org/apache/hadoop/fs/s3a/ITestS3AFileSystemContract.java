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
package org.apache.hadoop.fs.s3a;


import org.apache.hadoop.fs.FileSystemContractBaseTest;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests a live S3 system. If your keys and bucket aren't specified, all tests
 *  are marked as passed.
 *
 *  This uses BlockJUnit4ClassRunner because FileSystemContractBaseTest from
 *  TestCase which uses the old Junit3 runner that doesn't ignore assumptions
 *  properly making it impossible to skip the tests if we don't have a valid
 *  bucket.
 */
public class ITestS3AFileSystemContract extends FileSystemContractBaseTest {
    protected static final Logger LOG = LoggerFactory.getLogger(ITestS3AFileSystemContract.class);

    private Path basePath;

    @Rule
    public TestName methodName = new TestName();

    @Test
    public void testMkdirsWithUmask() throws Exception {
        // not supported
    }

    @Test
    public void testRenameDirectoryAsExistingDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameDirectoryAsExisting/dir");
        fs.mkdirs(src);
        createFile(path((src + "/file1")));
        createFile(path((src + "/subdir/file2")));
        Path dst = path("testRenameDirectoryAsExistingNew/newdir");
        fs.mkdirs(dst);
        rename(src, dst, true, false, true);
        Assert.assertFalse("Nested file1 exists", fs.exists(path((src + "/file1"))));
        Assert.assertFalse("Nested file2 exists", fs.exists(path((src + "/subdir/file2"))));
        Assert.assertTrue("Renamed nested file1 exists", fs.exists(path((dst + "/file1"))));
        Assert.assertTrue("Renamed nested exists", fs.exists(path((dst + "/subdir/file2"))));
    }

    @Test
    public void testMoveDirUnderParent() throws Throwable {
        // not support because
        // Fails if dst is a directory that is not empty.
    }
}

