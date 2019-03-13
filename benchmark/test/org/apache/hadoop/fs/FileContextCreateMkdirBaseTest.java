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


import FileContext.DEFAULT_PERM;
import FileSystem.LOG;
import java.io.IOException;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * <p>
 * A collection of tests for the {@link FileContext}, create method
 * This test should be used for testing an instance of FileContext
 *  that has been initialized to a specific default FileSystem such a
 *  LocalFileSystem, HDFS,S3, etc.
 * </p>
 * <p>
 * To test a given {@link FileSystem} implementation create a subclass of this
 * test and override {@link #setUp()} to initialize the <code>fc</code>
 * {@link FileContext} instance variable.
 *
 * Since this a junit 4 you can also do a single setup before
 * the start of any tests.
 * E.g.
 *
 * @unknown public static void clusterSetupAtBegining()
 * @unknown public static void ClusterShutdownAtEnd()
</p>
 */
public abstract class FileContextCreateMkdirBaseTest {
    protected final FileContextTestHelper fileContextTestHelper;

    protected static FileContext fc;

    static {
        GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
    }

    public FileContextCreateMkdirBaseTest() {
        fileContextTestHelper = createFileContextHelper();
    }

    // /////////////////////
    // Test Mkdir
    // //////////////////////
    @Test
    public void testMkdirNonRecursiveWithExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "aDir");
        FileContextCreateMkdirBaseTest.fc.mkdir(f, DEFAULT_PERM, false);
        Assert.assertTrue(FileContextTestHelper.isDir(FileContextCreateMkdirBaseTest.fc, f));
    }

    @Test
    public void testMkdirNonRecursiveWithNonExistingDir() {
        try {
            FileContextCreateMkdirBaseTest.fc.mkdir(getTestRootPath(FileContextCreateMkdirBaseTest.fc, "NonExistant/aDir"), DEFAULT_PERM, false);
            Assert.fail("Mkdir with non existing parent dir should have failed");
        } catch (IOException e) {
            // failed As expected
        }
    }

    @Test
    public void testMkdirRecursiveWithExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "aDir");
        FileContextCreateMkdirBaseTest.fc.mkdir(f, DEFAULT_PERM, true);
        Assert.assertTrue(FileContextTestHelper.isDir(FileContextCreateMkdirBaseTest.fc, f));
    }

    @Test
    public void testMkdirRecursiveWithNonExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "NonExistant2/aDir");
        FileContextCreateMkdirBaseTest.fc.mkdir(f, DEFAULT_PERM, true);
        Assert.assertTrue(FileContextTestHelper.isDir(FileContextCreateMkdirBaseTest.fc, f));
    }

    @Test
    public void testMkdirsRecursiveWithExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "aDir/bDir/cDir");
        FileContextCreateMkdirBaseTest.fc.mkdir(f, DEFAULT_PERM, true);
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(f));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(f.getParent()));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(f.getParent().getParent()));
    }

    @Test
    public void testMkdirRecursiveWithExistingFile() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "NonExistant3/aDir");
        FileContextCreateMkdirBaseTest.fc.mkdir(f, DEFAULT_PERM, true);
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(f));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(f.getParent()));
        // create a sample file
        Path filePath = new Path(f.getParent(), "test.txt");
        FileContextTestHelper.createFile(FileContextCreateMkdirBaseTest.fc, filePath);
        ContractTestUtils.assertIsFile(filePath, FileContextCreateMkdirBaseTest.fc.getFileStatus(filePath));
        // try creating another folder which conflicts with filePath
        Path dirPath = new Path(filePath, "bDir/cDir");
        try {
            FileContextCreateMkdirBaseTest.fc.mkdir(dirPath, DEFAULT_PERM, true);
            Assert.fail((("Mkdir for " + dirPath) + " should have failed as a file was present"));
        } catch (IOException e) {
            // failed as expected
        }
    }

    @Test
    public void testWithRename() throws IOException, InterruptedException {
        Path root = getTestRootPath(FileContextCreateMkdirBaseTest.fc);
        Path f = new Path(root, "d1/d2/d3");
        FileContextCreateMkdirBaseTest.fc.mkdir(f, DEFAULT_PERM, true);
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d2")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d2/d3")));
        // create a sample file f.txt
        Path fPath = new Path(root, "d1/d2/f.txt");
        FileContextTestHelper.createFile(FileContextCreateMkdirBaseTest.fc, fPath);
        ContractTestUtils.assertIsFile(fPath, FileContextCreateMkdirBaseTest.fc.getFileStatus(fPath));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d2")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d2/d3")));
        // create a sample file f2.txt
        Path f2Path = new Path(getTestRootPath(FileContextCreateMkdirBaseTest.fc), "d1/d2/d3/f2.txt");
        FileContextTestHelper.createFile(FileContextCreateMkdirBaseTest.fc, f2Path);
        ContractTestUtils.assertIsFile(fPath, FileContextCreateMkdirBaseTest.fc.getFileStatus(f2Path));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d2")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d2/d3")));
        // rename d1/d2/d3 d1/d4
        FileContextCreateMkdirBaseTest.fc.rename(new Path(root, "d1/d2/d3"), new Path(root, "d1/d4"));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1")));
        ContractTestUtils.assertIsDirectory(FileContextCreateMkdirBaseTest.fc.getFileStatus(new Path(root, "d1/d4")));
        Path f2NewPath = new Path(root, "d1/d4/f2.txt");
        ContractTestUtils.assertIsFile(f2NewPath, FileContextCreateMkdirBaseTest.fc.getFileStatus(f2NewPath));
    }

    // /////////////////////
    // Test Create
    // //////////////////////
    @Test
    public void testCreateNonRecursiveWithExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "foo");
        FileContextTestHelper.createFile(FileContextCreateMkdirBaseTest.fc, f);
        Assert.assertTrue(FileContextTestHelper.isFile(FileContextCreateMkdirBaseTest.fc, f));
    }

    @Test
    public void testCreateNonRecursiveWithNonExistingDir() {
        try {
            FileContextTestHelper.createFileNonRecursive(FileContextCreateMkdirBaseTest.fc, getTestRootPath(FileContextCreateMkdirBaseTest.fc, "NonExisting/foo"));
            Assert.fail("Create with non existing parent dir should have failed");
        } catch (IOException e) {
            // As expected
        }
    }

    @Test
    public void testCreateRecursiveWithExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "foo");
        FileContextTestHelper.createFile(FileContextCreateMkdirBaseTest.fc, f);
        Assert.assertTrue(FileContextTestHelper.isFile(FileContextCreateMkdirBaseTest.fc, f));
    }

    @Test
    public void testCreateRecursiveWithNonExistingDir() throws IOException {
        Path f = getTestRootPath(FileContextCreateMkdirBaseTest.fc, "NonExisting/foo");
        FileContextTestHelper.createFile(FileContextCreateMkdirBaseTest.fc, f);
        Assert.assertTrue(FileContextTestHelper.isFile(FileContextCreateMkdirBaseTest.fc, f));
    }
}

