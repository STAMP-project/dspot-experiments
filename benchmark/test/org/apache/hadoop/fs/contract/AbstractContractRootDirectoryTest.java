/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class does things to the root directory.
 * Only subclass this for tests against transient filesystems where
 * you don't care about the data.
 */
public abstract class AbstractContractRootDirectoryTest extends AbstractFSContractTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractContractRootDirectoryTest.class);

    public static final int OBJECTSTORE_RETRY_TIMEOUT = 30000;

    @Test
    public void testMkDirDepth1() throws Throwable {
        FileSystem fs = getFileSystem();
        Path dir = new Path("/testmkdirdepth1");
        assertPathDoesNotExist("directory already exists", dir);
        fs.mkdirs(dir);
        assertIsDirectory(dir);
        assertPathExists("directory already exists", dir);
        assertDeleted(dir, true);
    }

    @Test
    public void testRmEmptyRootDirRecursive() throws Throwable {
        // extra sanity checks here to avoid support calls about complete loss of data
        skipIfUnsupported(ContractOptions.TEST_ROOT_TESTS_ENABLED);
        Path root = new Path("/");
        assertIsDirectory(root);
        boolean deleted = getFileSystem().delete(root, true);
        AbstractContractRootDirectoryTest.LOG.info("rm -r / of empty dir result is {}", deleted);
        assertIsDirectory(root);
    }

    @Test
    public void testRmEmptyRootDirNonRecursive() throws Throwable {
        // extra sanity checks here to avoid support calls about complete loss
        // of data
        skipIfUnsupported(ContractOptions.TEST_ROOT_TESTS_ENABLED);
        final Path root = new Path("/");
        assertIsDirectory(root);
        // make sure the directory is clean. This includes some retry logic
        // to forgive blobstores whose listings can be out of sync with the file
        // status;
        final FileSystem fs = getFileSystem();
        final AtomicInteger iterations = new AtomicInteger(0);
        final FileStatus[] originalChildren = ContractTestUtils.listChildren(fs, root);
        LambdaTestUtils.eventually(AbstractContractRootDirectoryTest.OBJECTSTORE_RETRY_TIMEOUT, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                FileStatus[] deleted = ContractTestUtils.deleteChildren(fs, root, true);
                FileStatus[] children = ContractTestUtils.listChildren(fs, root);
                if ((children.length) > 0) {
                    Assert.fail(String.format(("After %d attempts: listing after rm /* not empty" + "\n%s\n%s\n%s"), iterations.incrementAndGet(), ContractTestUtils.dumpStats("final", children), ContractTestUtils.dumpStats("deleted", deleted), ContractTestUtils.dumpStats("original", originalChildren)));
                }
                return null;
            }
        }, new LambdaTestUtils.ProportionalRetryInterval(50, 1000));
        // then try to delete the empty one
        boolean deleted = fs.delete(root, false);
        AbstractContractRootDirectoryTest.LOG.info("rm / of empty dir result is {}", deleted);
        assertIsDirectory(root);
    }

    @Test
    public void testRmNonEmptyRootDirNonRecursive() throws Throwable {
        // extra sanity checks here to avoid support calls about complete loss of data
        skipIfUnsupported(ContractOptions.TEST_ROOT_TESTS_ENABLED);
        Path root = new Path("/");
        String touchfile = "/testRmNonEmptyRootDirNonRecursive";
        Path file = new Path(touchfile);
        ContractTestUtils.touch(getFileSystem(), file);
        assertIsDirectory(root);
        try {
            boolean deleted = getFileSystem().delete(root, false);
            Assert.fail((("non recursive delete should have raised an exception," + " but completed with exit code ") + deleted));
        } catch (IOException e) {
            // expected
            handleExpectedException(e);
            // and the file must still be present
            assertIsFile(file);
        } finally {
            getFileSystem().delete(file, false);
        }
        assertIsDirectory(root);
    }

    @Test
    public void testRmRootRecursive() throws Throwable {
        // extra sanity checks here to avoid support calls about complete loss of data
        skipIfUnsupported(ContractOptions.TEST_ROOT_TESTS_ENABLED);
        Path root = new Path("/");
        assertIsDirectory(root);
        Path file = new Path("/testRmRootRecursive");
        ContractTestUtils.touch(getFileSystem(), file);
        boolean deleted = getFileSystem().delete(root, true);
        assertIsDirectory(root);
        AbstractContractRootDirectoryTest.LOG.info("rm -rf / result is {}", deleted);
        if (deleted) {
            assertPathDoesNotExist("expected file to be deleted", file);
        } else {
            assertPathExists("expected file to be preserved", file);
        }
    }

    @Test
    public void testCreateFileOverRoot() throws Throwable {
        // extra sanity checks here to avoid support calls about complete loss of data
        skipIfUnsupported(ContractOptions.TEST_ROOT_TESTS_ENABLED);
        Path root = new Path("/");
        byte[] dataset = ContractTestUtils.dataset(1024, ' ', 'z');
        try {
            ContractTestUtils.createFile(getFileSystem(), root, false, dataset);
            Assert.fail(("expected an exception, got a file created over root: " + (ls(root))));
        } catch (IOException e) {
            // expected
            handleExpectedException(e);
        }
        assertIsDirectory(root);
    }

    @Test
    public void testListEmptyRootDirectory() throws IOException {
        skipIfUnsupported(ContractOptions.TEST_ROOT_TESTS_ENABLED);
        FileSystem fs = getFileSystem();
        Path root = new Path("/");
        FileStatus[] statuses = fs.listStatus(root);
        for (FileStatus status : statuses) {
            ContractTestUtils.assertDeleted(fs, status.getPath(), true);
        }
        Assert.assertEquals("listStatus on empty root-directory returned a non-empty list", 0, fs.listStatus(root).length);
        Assert.assertFalse("listFiles(/, false).hasNext", fs.listFiles(root, false).hasNext());
        Assert.assertFalse("listFiles(/, true).hasNext", fs.listFiles(root, true).hasNext());
        Assert.assertFalse("listLocatedStatus(/).hasNext", fs.listLocatedStatus(root).hasNext());
        assertIsDirectory(root);
    }

    @Test
    public void testSimpleRootListing() throws IOException {
        describe("test the nonrecursive root listing calls");
        FileSystem fs = getFileSystem();
        Path root = new Path("/");
        FileStatus[] statuses = fs.listStatus(root);
        List<LocatedFileStatus> locatedStatusList = ContractTestUtils.toList(fs.listLocatedStatus(root));
        Assert.assertEquals(statuses.length, locatedStatusList.size());
        List<LocatedFileStatus> fileList = ContractTestUtils.toList(fs.listFiles(root, false));
        Assert.assertTrue(((fileList.size()) <= (statuses.length)));
    }

    @Test
    public void testRecursiveRootListing() throws IOException {
        describe("test a recursive root directory listing");
        FileSystem fs = getFileSystem();
        Path root = new Path("/");
        ContractTestUtils.TreeScanResults listing = new ContractTestUtils.TreeScanResults(fs.listFiles(root, true));
        describe("verifying consistency with treewalk's files");
        ContractTestUtils.TreeScanResults treeWalk = ContractTestUtils.treeWalk(fs, root);
        treeWalk.assertFieldsEquivalent("files", listing, treeWalk.getFiles(), listing.getFiles());
    }
}

