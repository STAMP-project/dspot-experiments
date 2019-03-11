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
package org.apache.hadoop.fs.azure;


import FsAction.ALL;
import FsAction.EXECUTE;
import FsAction.NONE;
import FsAction.READ_EXECUTE;
import FsAction.READ_WRITE;
import FsAction.WRITE_EXECUTE;
import NativeAzureFileSystem.FolderRenamePending;
import WasbAuthorizationOperations.READ;
import WasbAuthorizationOperations.WRITE;
import com.google.common.annotations.VisibleForTesting;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test class to hold all WASB authorization tests.
 */
public class TestNativeAzureFileSystemAuthorization extends AbstractWasbTestBase {
    private static final short FULL_PERMISSION_WITH_STICKYBIT = 1777;

    @VisibleForTesting
    protected MockWasbAuthorizerImpl authorizer;

    @VisibleForTesting
    protected static final short STICKYBIT_PERMISSION_CONSTANT = 1700;

    @VisibleForTesting
    protected static final String READ = WasbAuthorizationOperations.READ.toString();

    @VisibleForTesting
    protected static final String WRITE = WasbAuthorizationOperations.WRITE.toString();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Positive test to verify Create access check.
     * The file is created directly under an existing folder.
     * No intermediate folders need to be created.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testCreateAccessWithoutCreateIntermediateFoldersCheckPositive() throws Throwable {
        Path parentDir = new Path("/");
        Path testPath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Positive test to verify Create access check.
     * The test tries to create a file whose parent is non-existent to ensure that
     * the intermediate folders between ancestor and direct parent are being created
     * when proper ranger policies are configured.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testCreateAccessWithCreateIntermediateFoldersCheckPositive() throws Throwable {
        Path parentDir = new Path("/testCreateAccessCheckPositive/1/2/3");
        Path testPath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
        } finally {
            allowRecursiveDelete(fs, "/testCreateAccessCheckPositive");
            fs.delete(new Path("/testCreateAccessCheckPositive"), true);
        }
    }

    /**
     * Negative test to verify that create fails when trying to overwrite an existing file.
     * without proper write permissions on the file being overwritten.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testCreateAccessWithOverwriteCheckNegative() throws Throwable {
        Path parentDir = new Path("/");
        Path testPath = new Path(parentDir, "test.dat");
        setExpectedFailureMessage("create", testPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
            fs.create(testPath, true);
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Positive test to verify that create succeeds when trying to overwrite an existing file.
     * when proper write permissions on the file being overwritten are provided.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testCreateAccessWithOverwriteCheckPositive() throws Throwable {
        Path parentDir = new Path("/");
        Path testPath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
            fs.create(testPath, true);
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Negative test to verify that Create fails when appropriate permissions are not provided.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testCreateAccessCheckNegative() throws Throwable {
        Path parentDir = new Path("/testCreateAccessCheckNegative");
        Path testPath = new Path(parentDir, "test.dat");
        setExpectedFailureMessage("create", testPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, false);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
        } finally {
            /* Provide permissions to cleanup in case the file got created */
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Positive test to verify listStatus access check.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testListAccessCheckPositive() throws Throwable {
        Path parentDir = new Path("/testListAccessCheckPositive");
        Path intermediateFolders = new Path(parentDir, "1/2/3/");
        Path testPath = new Path(intermediateFolders, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            fs.listStatus(testPath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Negative test to verify listStatus access check.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testListAccessCheckNegative() throws Throwable {
        Path parentDir = new Path("/testListAccessCheckNegative");
        Path testPath = new Path(parentDir, "test.dat");
        setExpectedFailureMessage("liststatus", testPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, false);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            fs.listStatus(testPath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Positive test to verify rename access check.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testRenameAccessCheckPositive() throws Throwable {
        Path parentDir = new Path("/testRenameAccessCheckPositive");
        Path srcPath = new Path(parentDir, "test1.dat");
        Path dstPath = new Path(parentDir, "test2.dat");
        /* to create parentDir */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* for rename */
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcPath);
            assertPathExists(fs, "sourcePath does not exist", srcPath);
            assertRenameOutcome(fs, srcPath, dstPath, true);
            assertPathExists(fs, "destPath does not exist", dstPath);
            assertPathDoesNotExist(fs, "sourcePath exists after rename!", srcPath);
        } finally {
            recursiveDelete(parentDir);
        }
    }

    /**
     * Negative test to verify rename access check.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testRenameAccessCheckNegative() throws Throwable {
        Path parentDir = new Path("/testRenameAccessCheckNegative");
        Path srcPath = new Path(parentDir, "test1.dat");
        Path dstPath = new Path(parentDir, "test2.dat");
        setExpectedFailureMessage("rename", srcPath);
        /* to create parent dir */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, false);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(srcPath);
            ContractTestUtils.assertPathExists(fs, "sourcePath does not exist", srcPath);
            fs.rename(srcPath, dstPath);
            ContractTestUtils.assertPathExists(fs, "destPath does not exist", dstPath);
        } finally {
            ContractTestUtils.assertPathExists(fs, "sourcePath does not exist after rename failure!", srcPath);
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Negative test to verify rename access check - the dstFolder disallows rename.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testRenameAccessCheckNegativeOnDstFolder() throws Throwable {
        Path parentSrcDir = new Path("/testRenameAccessCheckNegativeSrc");
        Path srcPath = new Path(parentSrcDir, "test1.dat");
        Path parentDstDir = new Path("/testRenameAccessCheckNegativeDst");
        Path dstPath = new Path(parentDstDir, "test2.dat");
        setExpectedFailureMessage("rename", dstPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);/* to create parent dir */

        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, false);
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcPath);
            ContractTestUtils.assertPathExists(fs, "sourcePath does not exist", srcPath);
            fs.mkdirs(parentDstDir);
            fs.rename(srcPath, dstPath);
            ContractTestUtils.assertPathDoesNotExist(fs, "destPath does not exist", dstPath);
        } finally {
            ContractTestUtils.assertPathExists(fs, "sourcePath does not exist after rename !", srcPath);
            recursiveDelete(parentSrcDir);
        }
    }

    /**
     * Positive test to verify rename access check - the dstFolder allows rename.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testRenameAccessCheckPositiveOnDstFolder() throws Throwable {
        Path parentSrcDir = new Path("/testRenameAccessCheckPositiveSrc");
        Path srcPath = new Path(parentSrcDir, "test1.dat");
        Path parentDstDir = new Path("/testRenameAccessCheckPositiveDst");
        Path dstPath = new Path(parentDstDir, "test2.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);/* to create parent dirs */

        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcPath);
            ContractTestUtils.assertPathExists(fs, "sourcePath does not exist", srcPath);
            fs.mkdirs(parentDstDir);
            assertRenameOutcome(fs, srcPath, dstPath, true);
            ContractTestUtils.assertPathDoesNotExist(fs, "sourcePath does not exist", srcPath);
            ContractTestUtils.assertPathExists(fs, "destPath does not exist", dstPath);
        } finally {
            recursiveDelete(parentSrcDir);
            recursiveDelete(parentDstDir);
        }
    }

    /**
     * Positive test to check rename succeeds for hierarchy of
     * files and folders under a src directory when destination
     * folder already exists.
     */
    @Test
    public void testRenamePositiveWhenDestinationFolderExists() throws Throwable {
        Path parentSrcDir = new Path("/testRenamePositiveForFolderSrc");
        Path srcFilePath = new Path(parentSrcDir, "test1.dat");
        Path srcFolderPath = new Path(parentSrcDir, "testFolder");
        Path dstDir = new Path("/testRenamePositiveForFolderDst");
        Path finalDstDir = new Path(dstDir, "testRenamePositiveForFolderSrc");
        Path dstFilePath = new Path(finalDstDir, "test1.dat");
        Path dstFolderPath = new Path(finalDstDir, "testFolder");
        /* to create parent dirs */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(dstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* Required for assertPathExists calls */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(finalDstDir.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcFilePath);
            assertPathExists(fs, "srcFilePath does not exist", srcFilePath);
            fs.mkdirs(srcFolderPath);
            assertIsDirectory(fs, srcFolderPath);
            fs.mkdirs(dstDir);
            assertIsDirectory(fs, dstDir);
            assertRenameOutcome(fs, parentSrcDir, dstDir, true);
            assertPathDoesNotExist(fs, "parentSrcDir exists", parentSrcDir);
            assertPathDoesNotExist(fs, "srcFilePath exists", srcFilePath);
            assertPathDoesNotExist(fs, "srcFolderPath exists", srcFolderPath);
            assertPathExists(fs, "destPath does not exist", dstDir);
            assertPathExists(fs, "dstFilePath does not exist", dstFilePath);
            assertPathExists(fs, "dstFolderPath does not exist", dstFolderPath);
        } finally {
            recursiveDelete(parentSrcDir);
            recursiveDelete(dstDir);
        }
    }

    /**
     * Positive test to check rename succeeds for hierarchy of
     * files and folders under a src directory and when the destination
     * folder does not exist.
     */
    @Test
    public void testRenamePositiveWhenDestinationFolderDoesNotExist() throws Throwable {
        Path srcParentDir = new Path("/testRenamePositiveWhenDestinationFolderDoesNotExist");
        Path srcDir = new Path(srcParentDir, "srcDir");
        Path srcFilePath = new Path(srcDir, "test1.dat");
        Path srcSubDirPath = new Path(srcDir, "testFolder");
        Path srcSubDirFilePath = new Path(srcSubDirPath, "test2.dat");
        Path dstDir = new Path(srcParentDir, "dstDir");
        Path dstFilePath = new Path(dstDir, "test1.dat");
        Path dstSubDirPath = new Path(dstDir, "testFolder");
        Path dstSubDirFilePath = new Path(dstSubDirPath, "test2.dat");
        /* to create parent dirs */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(srcParentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(srcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(srcSubDirPath.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* Required for asserPathExists calls */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(srcParentDir.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(srcDir.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(srcSubDirPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(dstDir.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(dstSubDirPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcFilePath);
            assertPathExists(fs, "srcFilePath does not exist", srcFilePath);
            fs.mkdirs(srcSubDirPath);
            assertIsDirectory(fs, srcSubDirPath);
            touch(fs, srcSubDirFilePath);
            assertPathExists(fs, "srcSubDirFilePath does not exist", srcSubDirFilePath);
            assertRenameOutcome(fs, srcDir, dstDir, true);
            assertPathDoesNotExist(fs, "srcDir exists", srcDir);
            assertPathDoesNotExist(fs, "srcFilePath exists", srcFilePath);
            assertPathDoesNotExist(fs, "srcSubDirPath exists", srcSubDirPath);
            assertPathDoesNotExist(fs, "srcSubDirFilePath exists", srcSubDirFilePath);
            assertPathExists(fs, "destPath does not exist", dstDir);
            assertPathExists(fs, "dstFilePath does not exist", dstFilePath);
            assertPathExists(fs, "dstSubDirPath does not exist", dstSubDirPath);
            assertPathExists(fs, "dstSubDirFilePath does not exist", dstSubDirFilePath);
        } finally {
            recursiveDelete(srcParentDir);
        }
    }

    /**
     * Test to verify rename fails and returns false when
     * the source to be renamed does not exist.
     */
    @Test
    public void testRenameOnNonExistentSource() throws Throwable {
        Path parentSrcDir = new Path("/testRenameOnNonExistentSourceFolderSrc");
        Path srcPath = new Path(parentSrcDir, "test1.dat");
        Path parentDstDir = new Path("/testRenameOnNonExistentSourceFolderDst");
        Path dstPath = new Path(parentDstDir, "test2.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);/* to create parent dirs */

        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        // required for assertpathExists calls
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(parentSrcDir);
            assertIsDirectory(fs, parentSrcDir);
            fs.mkdirs(parentDstDir);
            // should return false
            assertRenameOutcome(fs, srcPath, dstPath, false);
            assertPathDoesNotExist(fs, "destPath exists!", dstPath);
        } finally {
            recursiveDelete(parentSrcDir);
            recursiveDelete(parentDstDir);
        }
    }

    /**
     * Positive test to check rename succeeds when sticky bit is set on
     * source parent directory and user owns the source directory.
     */
    @Test
    public void testRenameWithStickyBitPositive() throws Throwable {
        Path parentSrcDir = new Path("/testRenameWithStickyBitPositiveSrc");
        Path srcPath = new Path(parentSrcDir, "test1.dat");
        Path parentDstDir = new Path("/testRenameWithStickyBitPositiveDst");
        Path dstPath = new Path(parentDstDir, "test2.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);/* to create parent dirs */

        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* Required for asserPathExists calls */
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcPath);
            assertPathExists(fs, "sourcePath does not exist", srcPath);
            fs.mkdirs(parentDstDir);
            assertIsDirectory(fs, parentDstDir);
            // set stickybit on parent directory
            fs.setPermission(parentSrcDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            assertRenameOutcome(fs, srcPath, dstPath, true);
            assertPathDoesNotExist(fs, "sourcePath exists", srcPath);
            assertPathExists(fs, "destPath does not exist", dstPath);
        } finally {
            recursiveDelete(parentSrcDir);
            recursiveDelete(parentDstDir);
        }
    }

    /**
     * Test to check rename fails when sticky bit is set on
     * parent of source directory and the user is not owner
     * of parent or the source directory.
     */
    @Test
    public void testRenameWithStickyBitNegative() throws Throwable {
        final Path parentSrcDir = new Path("/testRenameWithStickyBitNegativeSrc");
        final Path srcPath = new Path(parentSrcDir, "test1.dat");
        final Path parentDstDir = new Path("/testRenameWithStickyBitNegativeDst");
        final Path dstPath = new Path(parentDstDir, "test2.dat");
        expectedEx.expect(WasbAuthorizationException.class);
        expectedEx.expectMessage(String.format(("Rename operation for %s is not permitted." + " Details : Stickybit check failed."), srcPath.toString()));
        /* to create parent dirs */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* Required for asserPathExists calls */
        fs.updateWasbAuthorizer(authorizer);
        try {
            touch(fs, srcPath);
            assertPathExists(fs, "sourcePath does not exist", srcPath);
            fs.mkdirs(parentDstDir);
            assertIsDirectory(fs, parentDstDir);
            // set stickybit on parent of source folder
            fs.setPermission(parentSrcDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            UserGroupInformation dummyUser = UserGroupInformation.createUserForTesting("dummyUser", new String[]{ "dummygroup" });
            dummyUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // Add auth rules for dummyuser
                    authorizer.addAuthRule(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    authorizer.addAuthRule(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    try {
                        fs.rename(srcPath, dstPath);
                    } catch (WasbAuthorizationException wae) {
                        assertPathExists(fs, "sourcePath does not exist", srcPath);
                        assertPathDoesNotExist(fs, "destPath exists", dstPath);
                        throw wae;
                    }
                    return null;
                }
            });
        } finally {
            recursiveDelete(parentSrcDir);
            recursiveDelete(parentDstDir);
        }
    }

    /**
     * Test to check rename returns false when sticky bit is set on
     * parent of source parent directory and the source does not exist
     */
    @Test
    public void testRenameOnNonExistentSourceWithStickyBit() throws Throwable {
        final Path parentSrcDir = new Path("/testRenameOnNonExistentSourceWithStickyBitSrc");
        final Path srcPath = new Path(parentSrcDir, "test1.dat");
        final Path parentDstDir = new Path("/testRenameOnNonExistentSourceWithStickyBitDest");
        final Path dstPath = new Path(parentDstDir, "test2.dat");
        /* to create parent dirs */
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* Required for asserPathExists calls */
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(parentSrcDir);
            assertIsDirectory(fs, parentSrcDir);
            fs.mkdirs(parentDstDir);
            assertIsDirectory(fs, parentDstDir);
            // set stickybit on parent of source folder
            fs.setPermission(parentSrcDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            UserGroupInformation dummyUser = UserGroupInformation.createUserForTesting("dummyUser", new String[]{ "dummygroup" });
            dummyUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // Add auth rules for dummyuser
                    authorizer.addAuthRule(parentSrcDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    authorizer.addAuthRule(parentDstDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    // should return false since srcPath does not exist.
                    assertRenameOutcome(fs, srcPath, dstPath, false);
                    assertPathDoesNotExist(fs, "destPath exists", dstPath);
                    return null;
                }
            });
        } finally {
            recursiveDelete(parentSrcDir);
            recursiveDelete(parentDstDir);
        }
    }

    /**
     * Positive test for read access check.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testReadAccessCheckPositive() throws Throwable {
        Path parentDir = new Path("/testReadAccessCheckPositive");
        Path testPath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        FSDataInputStream inputStream = null;
        FSDataOutputStream fso = null;
        try {
            fso = fs.create(testPath);
            String data = "Hello World";
            fso.writeBytes(data);
            fso.close();
            inputStream = fs.open(testPath);
            ContractTestUtils.verifyRead(inputStream, data.getBytes(), 0, data.length());
        } finally {
            if (fso != null) {
                fso.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Negative test to verify read access check.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testReadAccessCheckNegative() throws Throwable {
        Path parentDir = new Path("/testReadAccessCheckNegative");
        Path testPath = new Path(parentDir, "test.dat");
        setExpectedFailureMessage("read", testPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, false);
        fs.updateWasbAuthorizer(authorizer);
        FSDataInputStream inputStream = null;
        FSDataOutputStream fso = null;
        try {
            fso = fs.create(testPath);
            String data = "Hello World";
            fso.writeBytes(data);
            fso.close();
            inputStream = fs.open(testPath);
            ContractTestUtils.verifyRead(inputStream, data.getBytes(), 0, data.length());
        } finally {
            if (fso != null) {
                fso.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Positive test to verify file delete access check.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testFileDeleteAccessCheckPositive() throws Throwable {
        Path parentDir = new Path("/");
        Path testPath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
        } finally {
            fs.delete(testPath, false);
            ContractTestUtils.assertPathDoesNotExist(fs, "testPath exists after deletion!", testPath);
        }
    }

    /**
     * Negative test to verify file delete access check.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testFileDeleteAccessCheckNegative() throws Throwable {
        Path parentDir = new Path("/");
        Path testPath = new Path(parentDir, "test.dat");
        setExpectedFailureMessage("delete", testPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
            /* Remove permissions for delete to force failure */
            authorizer.deleteAllAuthRules();
            authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, false);
            fs.updateWasbAuthorizer(authorizer);
            fs.delete(testPath, false);
        } finally {
            /* Restore permissions to force a successful delete */
            authorizer.deleteAllAuthRules();
            authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
            fs.updateWasbAuthorizer(authorizer);
            fs.delete(testPath, false);
            ContractTestUtils.assertPathDoesNotExist(fs, "testPath exists after deletion!", testPath);
        }
    }

    /**
     * Positive test to verify file delete access check, with intermediate folders
     * Uses wildcard recursive permissions.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testFileDeleteAccessWithIntermediateFoldersCheckPositive() throws Throwable {
        Path parentDir = new Path("/testDeleteIntermediateFolder");
        Path childPath = new Path(parentDir, "1/2");
        Path testPath = new Path(childPath, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);// for create and delete

        authorizer.addAuthRuleForOwner("/testDeleteIntermediateFolder*", TestNativeAzureFileSystemAuthorization.WRITE, true);// for recursive delete

        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
            fs.delete(parentDir, true);
            ContractTestUtils.assertPathDoesNotExist(fs, "testPath exists after deletion!", parentDir);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Test to verify access check failure leaves intermediate folders undeleted.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testDeleteAuthCheckFailureLeavesFilesUndeleted() throws Throwable {
        Path parentDir = new Path("/testDeleteAuthCheckFailureLeavesFilesUndeleted");
        Path childPath1 = new Path(parentDir, "child1");
        Path childPath2 = new Path(parentDir, "child2");
        Path testPath1 = new Path(childPath1, "test.dat");
        Path testPath2 = new Path(childPath2, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner("/testDeleteAuthCheckFailureLeavesFilesUndeleted*", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath1);
            fs.create(testPath2);
            ContractTestUtils.assertPathExists(fs, "testPath1 was not created", testPath1);
            ContractTestUtils.assertPathExists(fs, "testPath2 was not created", testPath2);
            // revoke write on one of the child folders
            authorizer.deleteAllAuthRules();
            authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
            authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
            authorizer.addAuthRuleForOwner(childPath2.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
            authorizer.addAuthRuleForOwner(childPath1.toString(), TestNativeAzureFileSystemAuthorization.WRITE, false);
            Assert.assertFalse(fs.delete(parentDir, true));
            // Assert that only child2 contents are deleted
            ContractTestUtils.assertPathExists(fs, "child1 is deleted!", testPath1);
            ContractTestUtils.assertPathDoesNotExist(fs, "child2 exists after deletion!", testPath2);
            ContractTestUtils.assertPathDoesNotExist(fs, "child2 exists after deletion!", childPath2);
            ContractTestUtils.assertPathExists(fs, "parentDir is deleted!", parentDir);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Positive test to verify file delete with sticky bit on parent.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testSingleFileDeleteWithStickyBitPositive() throws Throwable {
        Path parentDir = new Path("/testSingleFileDeleteWithStickyBitPositive");
        Path testPath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
            // set stickybit on parent directory
            fs.setPermission(parentDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            Assert.assertTrue(fs.delete(testPath, true));
            ContractTestUtils.assertPathDoesNotExist(fs, "testPath exists after deletion!", testPath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Negative test to verify file delete fails when sticky bit is set on parent
     * and non-owner user performs delete
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testSingleFileDeleteWithStickyBitNegative() throws Throwable {
        Path parentDir = new Path("/testSingleFileDeleteWithStickyBitNegative");
        Path testPath = new Path(parentDir, "test.dat");
        expectedEx.expect(WasbAuthorizationException.class);
        expectedEx.expectMessage(String.format("%s has sticky bit set. File %s cannot be deleted.", parentDir.toString(), testPath.toString()));
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath was not created", testPath);
            // set stickybit on parent directory
            fs.setPermission(parentDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            UserGroupInformation dummyUser = UserGroupInformation.createUserForTesting("dummyUser", new String[]{ "dummygroup" });
            dummyUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try {
                        authorizer.addAuthRule(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                        fs.delete(testPath, true);
                        return null;
                    } catch (WasbAuthorizationException wae) {
                        ContractTestUtils.assertPathExists(fs, "testPath should not be deleted!", testPath);
                        throw wae;
                    }
                }
            });
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Positive test to verify file and folder delete succeeds with stickybit
     * when the owner of the files deletes the file.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testRecursiveDeleteSucceedsWithStickybit() throws Throwable {
        Path parentDir = new Path("/testRecursiveDeleteSucceedsWithStickybit");
        Path childDir = new Path(parentDir, "child");
        Path testFilePath = new Path(childDir, "test.dat");
        Path testFolderPath = new Path(childDir, "testDirectory");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner("/testRecursiveDeleteSucceedsWithStickybit*", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testFilePath);
            ContractTestUtils.assertPathExists(fs, "file was not created", testFilePath);
            fs.mkdirs(testFolderPath);
            ContractTestUtils.assertPathExists(fs, "folder was not created", testFolderPath);
            // set stickybit on child directory
            fs.setPermission(new Path(parentDir, "child"), new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            // perform delete as owner of the files
            Assert.assertTrue(fs.delete(parentDir, true));
            ContractTestUtils.assertPathDoesNotExist(fs, "parentDir exists after deletion!", parentDir);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Test to verify delete fails for child files and folders when
     * non-owner user performs delete and stickybit is set on parent
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testRecursiveDeleteFailsWithStickybit() throws Throwable {
        Path parentDir = new Path("/testRecursiveDeleteFailsWithStickybit");
        Path childDir = new Path(parentDir, "child");
        Path testFilePath = new Path(childDir, "test.dat");
        Path testFolderPath = new Path(childDir, "testDirectory");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner("/testRecursiveDeleteFailsWithStickybit*", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testFilePath);
            ContractTestUtils.assertPathExists(fs, "file was not created", testFilePath);
            fs.mkdirs(testFolderPath);
            ContractTestUtils.assertPathExists(fs, "folder was not created", testFolderPath);
            // set stickybit on child directory
            fs.setPermission(new Path(parentDir, "child"), new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            UserGroupInformation dummyUser = UserGroupInformation.createUserForTesting("dummyUser", new String[]{ "dummygroup" });
            dummyUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // Add auth rules for dummyuser
                    authorizer.addAuthRule("/", TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    authorizer.addAuthRule("/testRecursiveDeleteFailsWithStickybit*", TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    Assert.assertFalse(fs.delete(parentDir, true));
                    return null;
                }
            });
            ContractTestUtils.assertPathExists(fs, "parentDir is deleted!", parentDir);
            ContractTestUtils.assertPathExists(fs, "file is deleted!", testFilePath);
            ContractTestUtils.assertPathExists(fs, "folder is deleted!", testFolderPath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Test delete scenario where sticky bit check leaves files/folders not owned
     * by a specific user intact and the files owned by him/her are deleted
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testDeleteSucceedsForOnlyFilesOwnedByUserWithStickybitSet() throws Throwable {
        Path parentDir = new Path("/testDeleteSucceedsForOnlyFilesOwnedByUserWithStickybitSet");
        Path testFilePath = new Path(parentDir, "test.dat");
        Path testFolderPath = new Path(parentDir, "testDirectory");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner("/testDeleteSucceedsForOnlyFilesOwnedByUserWithStickybitSet*", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testFilePath);
            ContractTestUtils.assertPathExists(fs, "file was not created", testFilePath);
            fs.setPermission(parentDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
            UserGroupInformation dummyUser = UserGroupInformation.createUserForTesting("dummyuser", new String[]{ "dummygroup" });
            dummyUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    authorizer.addAuthRule("/", TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    authorizer.addAuthRule("/testDeleteSucceedsForOnlyFilesOwnedByUserWithStickybitSet*", TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    fs.create(testFolderPath);// the folder will have owner as dummyuser

                    ContractTestUtils.assertPathExists(fs, "folder was not created", testFolderPath);
                    Assert.assertFalse(fs.delete(parentDir, true));
                    ContractTestUtils.assertPathDoesNotExist(fs, "folder should have been deleted!", testFolderPath);
                    ContractTestUtils.assertPathExists(fs, "parentDir is deleted!", parentDir);
                    ContractTestUtils.assertPathExists(fs, "file is deleted!", testFilePath);
                    return null;
                }
            });
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Test delete scenario where sticky bit is set and the owner of parent
     * directory can delete child files/folders which he does not own.
     * This is according to the sticky bit behaviour specified in hdfs permission
     * guide which is as follows - The sticky bit can be set on directories,
     * preventing anyone except the superuser, directory owner or file owner
     * from deleting or moving the files within the directory
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testDeleteSucceedsForParentDirectoryOwnerUserWithStickybit() throws Throwable {
        Path parentDir = new Path("/testDeleteSucceedsForParentDirectoryOwnerUserWithStickybit");
        Path testFilePath = new Path(parentDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner("/testDeleteSucceedsForParentDirectoryOwnerUserWithStickybit*", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            // create folder with owner as current user
            fs.mkdirs(parentDir);
            ContractTestUtils.assertPathExists(fs, "folder was not created", parentDir);
            // create child with owner as dummyUser
            UserGroupInformation dummyUser = UserGroupInformation.createUserForTesting("user1", new String[]{ "dummygroup" });
            dummyUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    authorizer.addAuthRule(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, getCurrentUserShortName(), true);
                    fs.create(testFilePath);
                    ContractTestUtils.assertPathExists(fs, "file was not created", testFilePath);
                    fs.setPermission(parentDir, new FsPermission(TestNativeAzureFileSystemAuthorization.STICKYBIT_PERMISSION_CONSTANT));
                    return null;
                }
            });
            // invoke delete as current user
            Assert.assertTrue(fs.delete(parentDir, true));
            ContractTestUtils.assertPathDoesNotExist(fs, "parentDir is not deleted!", parentDir);
            ContractTestUtils.assertPathDoesNotExist(fs, "file is not deleted!", testFilePath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Test to verify delete of root succeeds with proper permissions and
     * leaves root after delete.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testDeleteScenarioForRoot() throws Throwable {
        Path rootPath = new Path("/");
        Path parentDir = new Path("/testDeleteScenarioForRoot");
        Path childPath1 = new Path(parentDir, "child1");
        Path childPath2 = new Path(parentDir, "child2");
        Path testPath1 = new Path(childPath1, "test.dat");
        Path testPath2 = new Path(childPath2, "testFolder");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner("/testDeleteScenarioForRoot*", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(testPath1);
            fs.create(testPath2);
            ContractTestUtils.assertPathExists(fs, "testPath1 was not created", testPath1);
            ContractTestUtils.assertPathExists(fs, "testPath2 was not created", testPath2);
            Assert.assertFalse(fs.delete(rootPath, true));
            ContractTestUtils.assertPathDoesNotExist(fs, "file exists after deletion!", testPath1);
            ContractTestUtils.assertPathDoesNotExist(fs, "folder exists after deletion!", testPath2);
            ContractTestUtils.assertPathDoesNotExist(fs, "parentDir exists after deletion!", parentDir);
            ContractTestUtils.assertPathExists(fs, "Root should not have been deleted!", rootPath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Positive test for getFileStatus.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testGetFileStatusPositive() throws Throwable {
        Path testPath = new Path("/");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.READ, true);
        ContractTestUtils.assertIsDirectory(fs, testPath);
    }

    /**
     * Positive test for mkdirs access check.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testMkdirsCheckPositive() throws Throwable {
        Path testPath = new Path("/testMkdirsAccessCheckPositive/1/2/3");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertIsDirectory(fs, testPath);
        } finally {
            allowRecursiveDelete(fs, "/testMkdirsAccessCheckPositive");
            fs.delete(new Path("/testMkdirsAccessCheckPositive"), true);
        }
    }

    /**
     * Positive test for mkdirs -p with existing hierarchy
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testMkdirsWithExistingHierarchyCheckPositive1() throws Throwable {
        Path testPath = new Path("/testMkdirsWithExistingHierarchyCheckPositive1");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertIsDirectory(fs, testPath);
            /* Don't need permissions to create a directory that already exists */
            authorizer.deleteAllAuthRules();
            authorizer.addAuthRuleForOwner(testPath.getParent().toString(), TestNativeAzureFileSystemAuthorization.READ, true);// for assert

            fs.mkdirs(testPath);
            ContractTestUtils.assertIsDirectory(fs, testPath);
        } finally {
            allowRecursiveDelete(fs, testPath.toString());
            fs.delete(testPath, true);
        }
    }

    @Test
    public void testMkdirsWithExistingHierarchyCheckPositive2() throws Throwable {
        Path testPath = new Path("/testMkdirsWithExistingHierarchyCheckPositive2");
        Path childPath1 = new Path(testPath, "1");
        Path childPath2 = new Path(childPath1, "2");
        Path childPath3 = new Path(childPath2, "3");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(childPath1.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(childPath1);
            ContractTestUtils.assertIsDirectory(fs, childPath1);
            // Path already exists => no-op.
            fs.mkdirs(testPath);
            ContractTestUtils.assertIsDirectory(fs, testPath);
            // Path already exists => no-op.
            fs.mkdirs(childPath1);
            ContractTestUtils.assertIsDirectory(fs, childPath1);
            // Check permissions against existing ancestor childPath1
            fs.mkdirs(childPath3);
            ContractTestUtils.assertIsDirectory(fs, childPath3);
        } finally {
            allowRecursiveDelete(fs, testPath.toString());
            fs.delete(testPath, true);
        }
    }

    /**
     * Negative test for mkdirs access check.
     *
     * @throws Throwable
     * 		
     */
    // (expected=WasbAuthorizationException.class)
    @Test
    public void testMkdirsCheckNegative() throws Throwable {
        Path testPath = new Path("/testMkdirsAccessCheckNegative/1/2/3");
        setExpectedFailureMessage("mkdirs", testPath);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, false);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertPathDoesNotExist(fs, "testPath was not created", testPath);
        } finally {
            allowRecursiveDelete(fs, "/testMkdirsAccessCheckNegative");
            fs.delete(new Path("/testMkdirsAccessCheckNegative"), true);
        }
    }

    /**
     * Positive test triple slash format (wasb:///) access check.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testListStatusWithTripleSlashCheckPositive() throws Throwable {
        Path testPath = new Path("/");
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        Path testPathWithTripleSlash = new Path(("wasb:///" + testPath));
        fs.listStatus(testPathWithTripleSlash);
    }

    /**
     * Test case when owner matches current user
     */
    @Test
    public void testOwnerPermissionPositive() throws Throwable {
        Path parentDir = new Path("/testOwnerPermissionPositive");
        Path testPath = new Path(parentDir, "test.data");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        // additional rule used for assertPathExists
        fs.updateWasbAuthorizer(authorizer);
        try {
            // creates parentDir with owner as current user
            fs.mkdirs(parentDir);
            ContractTestUtils.assertPathExists(fs, "parentDir does not exist", parentDir);
            fs.create(testPath);
            fs.getFileStatus(testPath);
            ContractTestUtils.assertPathExists(fs, "testPath does not exist", testPath);
            fs.delete(parentDir, true);
            ContractTestUtils.assertPathDoesNotExist(fs, "testPath does not exist", testPath);
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Negative test case for owner does not match current user
     */
    @Test
    public void testOwnerPermissionNegative() throws Throwable {
        Path parentDir = new Path("/testOwnerPermissionNegative");
        Path childDir = new Path(parentDir, "childDir");
        setExpectedFailureMessage("mkdirs", childDir);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(parentDir);
            UserGroupInformation ugiSuperUser = UserGroupInformation.createUserForTesting("testuser", new String[]{  });
            ugiSuperUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    fs.mkdirs(childDir);
                    return null;
                }
            });
        } finally {
            allowRecursiveDelete(fs, parentDir.toString());
            fs.delete(parentDir, true);
        }
    }

    /**
     * Test to verify that retrieving owner information does not
     * throw when file/folder does not exist
     */
    @Test
    public void testRetrievingOwnerDoesNotFailWhenFileDoesNotExist() throws Throwable {
        Path testdirectory = new Path("/testDirectory123454565");
        String owner = fs.getOwnerForPath(testdirectory);
        Assert.assertEquals("", owner);
    }

    /**
     * Negative test for setOwner when Authorization is enabled.
     */
    @Test
    public void testSetOwnerThrowsForUnauthorisedUsers() throws Throwable {
        Path testPath = new Path("/testSetOwnerNegative");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        final String owner;
        UserGroupInformation unauthorisedUser = UserGroupInformation.createUserForTesting("unauthoriseduser", new String[]{ "group1" });
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertPathExists(fs, "test path does not exist", testPath);
            owner = fs.getFileStatus(testPath).getOwner();
            unauthorisedUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try {
                        fs.setOwner(testPath, "newowner", null);
                        Assert.fail("Failing test because setOwner call was expected to throw");
                    } catch (WasbAuthorizationException wex) {
                        // check that the owner is not modified
                        assertOwnerEquals(testPath, owner);
                    }
                    return null;
                }
            });
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Test for setOwner when Authorization is enabled and
     * the user is specified in chown allowed user list.
     */
    @Test
    public void testSetOwnerSucceedsForAuthorisedUsers() throws Throwable {
        Path testPath = new Path("/testSetOwnerPositive");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        String newOwner = "user2";
        String newGroup = "newgroup";
        UserGroupInformation authorisedUser = UserGroupInformation.createUserForTesting("user2", new String[]{ "group1" });
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertPathExists(fs, "test path does not exist", testPath);
            String owner = fs.getFileStatus(testPath).getOwner();
            Assume.assumeTrue("changing owner requires original and new owner to be different", (!(StringUtils.equalsIgnoreCase(owner, newOwner))));
            authorisedUser.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    fs.setOwner(testPath, newOwner, newGroup);
                    assertOwnerEquals(testPath, newOwner);
                    Assert.assertEquals(newGroup, fs.getFileStatus(testPath).getGroup());
                    return null;
                }
            });
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Test for setOwner when Authorization is enabled and
     * the userlist is specified as '*'.
     */
    @Test
    public void testSetOwnerSucceedsForAnyUserWhenWildCardIsSpecified() throws Throwable {
        fs.updateChownAllowedUsers(Collections.singletonList("*"));
        final Path testPath = new Path("/testSetOwnerPositiveWildcard");
        Configuration conf = fs.getConf();
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        final String newOwner = "newowner";
        final String newGroup = "newgroup";
        UserGroupInformation user = UserGroupInformation.createUserForTesting("anyuser", new String[]{ "group1" });
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertPathExists(fs, "test path does not exist", testPath);
            String owner = fs.getFileStatus(testPath).getOwner();
            Assume.assumeTrue("changing owner requires original and new owner to be different", (!(StringUtils.equalsIgnoreCase(owner, newOwner))));
            user.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    fs.setOwner(testPath, newOwner, newGroup);
                    assertOwnerEquals(testPath, newOwner);
                    Assert.assertEquals(newGroup, fs.getFileStatus(testPath).getGroup());
                    return null;
                }
            });
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Test for setOwner  throws for illegal setup of chown
     * allowed testSetOwnerSucceedsForAuthorisedUsers.
     */
    @Test
    public void testSetOwnerFailsForIllegalSetup() throws Throwable {
        fs.updateChownAllowedUsers(Arrays.asList("user1", "*"));
        final Path testPath = new Path("/testSetOwnerFailsForIllegalSetup");
        Configuration conf = fs.getConf();
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        UserGroupInformation user = UserGroupInformation.createUserForTesting("anyuser", new String[]{ "group1" });
        try {
            fs.mkdirs(testPath);
            ContractTestUtils.assertPathExists(fs, "test path does not exist", testPath);
            final String owner = fs.getFileStatus(testPath).getOwner();
            user.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try {
                        fs.setOwner(testPath, "newowner", null);
                        Assert.fail("Failing test because setOwner call was expected to throw");
                    } catch (IllegalArgumentException iex) {
                        // check that the owner is not modified
                        assertOwnerEquals(testPath, owner);
                    }
                    return null;
                }
            });
        } finally {
            fs.delete(testPath, false);
        }
    }

    /**
     * Test to ensure that the internal RenamePending mechanism
     * does not make authorization calls.
     */
    @Test
    public void testRenamePendingAuthorizationCalls() throws Throwable {
        Path testPath = new Path("/testRenamePendingAuthorizationCalls");
        Path srcPath = new Path(testPath, "srcPath");
        Path dstPath = new Path(testPath, "dstPath");
        Path srcFilePath = new Path(srcPath, "file.txt");
        Path dstFilePath = new Path(dstPath, "file.txt");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        /* Remove nextline after fixing createInternal from FolderRenamePending */
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.create(srcFilePath);
            String srcKey = fs.pathToKey(srcPath);
            String dstKey = fs.pathToKey(dstPath);
            // Create a -RenamePendingFile
            NativeAzureFileSystem.FolderRenamePending renamePending = new NativeAzureFileSystem.FolderRenamePending(srcKey, dstKey, null, fs);
            renamePending.writeFile(fs);
            // Initiate the pending-rename
            fs.getFileStatus(srcPath);
        } catch (FileNotFoundException fnfe) {
            // This is expected because getFileStatus would complete the pending "rename"
            // represented by the -RenamePending file.
            GenericTestUtils.assertExceptionContains(((srcPath.toString()) + ": No such file or directory."), fnfe);
            // The pending rename should have completed
            ContractTestUtils.assertPathExists(fs, "dstFilePath does not exist -- pending rename failed", dstFilePath);
        } finally {
            allowRecursiveDelete(fs, testPath.toString());
            fs.delete(testPath, true);
        }
    }

    /**
     * Negative test for setPermission when Authorization is enabled.
     */
    @Test
    public void testSetPermissionThrowsForUnauthorisedUsers() throws Throwable {
        // setPermission is called by a user who is not a daemon user
        // and not chmodAllowedUsers and not owner of the file/folder.
        // This test validates a authorization exception during setPermission call
        testSetPermission("/testSetPermissionNegative", null, null, "unauthorizeduser", true, false);
    }

    /**
     * Positive test for setPermission when Authorization is enabled.
     */
    @Test
    public void testSetPermissionForAuthorisedUsers() throws Throwable {
        // user1 is already part of chmodAllowedUsers.
        // This test validates the allowed user can do setPermission
        testSetPermission("/testSetPermissionPositive", null, null, "user1", false, false);
    }

    /**
     * Positive test for setPermission as owner when Authorization is enabled.
     */
    @Test
    public void testSetPermissionForOwner() throws Throwable {
        // setPermission is called by the owner and expect a success
        // during setPermission call
        testSetPermission("/testSetPermissionPositiveOwner", null, null, null, false, false);
    }

    /**
     * Test setPermission when wildcard is specified in allowed user list.
     */
    @Test
    public void testSetPermissionWhenWildCardInAllowedUserList() throws Throwable {
        // Allow all to setPermission and expect a success
        // during setPermission call
        List<String> chmodAllowedUsers = Collections.singletonList("*");
        testSetPermission("/testSetPermissionWhenWildCardInAllowedUserList", chmodAllowedUsers, null, "testuser", false, false);
    }

    /**
     * Test setPermission when invalid configuration value for allowed user list
     * i.e. wildcard character and a username.
     */
    @Test
    public void testSetPermissionForInvalidAllowedUserList() throws Throwable {
        // Setting up an invalid chmodAllowedUsers and expects a failure
        // during setPermission call
        List<String> chmodAllowedUsers = Arrays.asList("*", "testuser");
        testSetPermission("/testSetPermissionForInvalidAllowedUserList", chmodAllowedUsers, null, "testuser", true, true);
    }

    /**
     * Test setPermission for a daemon user.
     */
    @Test
    public void testSetPermissionForDaemonUser() throws Throwable {
        // hive user is already setup as daemon user.
        // This test validates the daemon user can do setPermission
        testSetPermission("/testSetPermissionForDaemonUser", null, null, "hive", false, false);
    }

    /**
     * Test setPermission when invalid configuration value for daemon user list
     * i.e. wildcard character and a daemon username.
     */
    @Test
    public void testSetPermissionForInvalidDaemonUserList() throws Throwable {
        List<String> daemonUsers = Arrays.asList("*", "hive");
        testSetPermission("/testSetPermissionForInvalidDaemonUserList", null, daemonUsers, "testuser", true, true);
    }

    /**
     * Test access when requested permissions match the existing permissions.
     */
    @Test
    public void testAccessWhenPermissionsMatchForAllAndReadWrite() throws Throwable {
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessWhenPermissionsMatchForAllAndReadWrite");
        // For All and Read-Write FsAction.
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            assertPathExists(fs, "test path does not exist", testPath);
            fs.access(testPath, ALL);
            fs.access(testPath, READ_WRITE);
        } finally {
            recursiveDelete(testPath);
        }
    }

    /**
     * Test access when Write and Write-Execute match the existing permissions.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testAccessWhenPermissionsMatchForWriteAndWriteExecute() throws Throwable {
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessWhenPermissionsMatchForWriteAndWriteExecute");
        // For Write and Write-Execute FsAction.
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            assertPathExists(fs, "test path does not exist", testPath);
            fs.access(testPath, FsAction.WRITE);
            fs.access(testPath, WRITE_EXECUTE);
        } finally {
            recursiveDelete(testPath);
        }
    }

    /**
     * Test access when Read and Read-Execute match the existing permissions.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testAccessWhenPermissionsMatchForReadAndReadExecute() throws Throwable {
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessWhenPermissionsMatchForReadAndReadExecute");
        // For Read and Read-Execute FsAction.
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            assertPathExists(fs, "test path does not exist", testPath);
            fs.access(testPath, FsAction.READ);
            fs.access(testPath, READ_EXECUTE);
        } finally {
            recursiveDelete(testPath);
        }
    }

    /**
     * Test access when Execute and None match the existing permissions.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testAccessWhenPermissionsMatchForExecuteAndNone() throws Throwable {
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessWhenPermissionsMatchForExecuteAndNone");
        // For Execute and None FsAction.
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            assertPathExists(fs, "test path does not exist", testPath);
            fs.access(testPath, EXECUTE);
            fs.access(testPath, NONE);
        } finally {
            recursiveDelete(testPath);
        }
    }

    /**
     * Test access when requested permissions do not match existing permissions.
     */
    @Test
    public void testAccessWhenPermissionsDoNotMatch() throws Throwable {
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessWhenPermissionsDoNotMatch");
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(testPath);
            assertPathExists(fs, "test path does not exist", testPath);
            assertNoAccess(testPath, ALL);
            assertNoAccess(testPath, FsAction.WRITE);
            assertNoAccess(testPath, WRITE_EXECUTE);
        } finally {
            recursiveDelete(testPath);
        }
    }

    /**
     * Test access when file does not exist and permissions match.
     */
    @Test
    public void testAccessFileDoesNotExist() throws Throwable {
        expectedEx.expect(FileNotFoundException.class);
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessFileDoesNotExist");
        authorizer.init(conf);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.READ, true);
        authorizer.addAuthRuleForOwner(testPath.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        fs.updateWasbAuthorizer(authorizer);
        assertPathDoesNotExist(fs, "test path exists", testPath);
        fs.access(testPath, ALL);
    }

    /**
     * Test access when file does not exist and permissions do not match.
     */
    @Test
    public void testAccessFileDoesNotExistWhenNoAccessPermission() throws Throwable {
        expectedEx.expect(FileNotFoundException.class);
        Configuration conf = fs.getConf();
        fs.setConf(conf);
        final Path testPath = new Path("/testAccessFileDoesNotExistWhenNoAccessPermission");
        authorizer.init(conf);
        fs.updateWasbAuthorizer(authorizer);
        assertPathDoesNotExist(fs, "test path exists", testPath);
        fs.access(testPath, ALL);
    }

    /**
     * Test access for file and intermediate directory after creating
     * file with intermediate directory.
     */
    @Test
    public void testAccessForFileAndIntermediateDirectoryCreated() throws Throwable {
        Path parentDir = new Path("/testAccessDirectory");
        Path intermediateDir = new Path(parentDir, "intermediateDir");
        Path testPath = new Path(intermediateDir, "test.dat");
        authorizer.addAuthRuleForOwner("/", TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(parentDir.toString(), TestNativeAzureFileSystemAuthorization.WRITE, true);
        // Recursive owner Rule is to determine the permission on intermediate dirs.
        authorizer.addAuthRuleForOwner(((parentDir.toString()) + "/*"), TestNativeAzureFileSystemAuthorization.WRITE, true);
        authorizer.addAuthRuleForOwner(((parentDir.toString()) + "/*"), TestNativeAzureFileSystemAuthorization.READ, true);
        fs.updateWasbAuthorizer(authorizer);
        try {
            fs.mkdirs(parentDir);
            fs.create(testPath);
            assertPathExists(fs, "testPath was not created", testPath);
            fs.access(parentDir, FsAction.WRITE);
            fs.access(parentDir, WRITE_EXECUTE);
            fs.access(intermediateDir, ALL);
            fs.access(intermediateDir, READ_WRITE);
            fs.access(testPath, ALL);
            fs.access(testPath, READ_WRITE);
        } finally {
            recursiveDelete(testPath);
        }
    }
}

