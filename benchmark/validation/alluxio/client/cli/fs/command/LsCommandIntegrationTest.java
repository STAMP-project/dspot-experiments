/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.cli.fs.command;


import PropertyKey.Name;
import SetAclAction.MODIFY;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.client.cli.fs.AbstractFileSystemShellTest;
import alluxio.client.cli.fs.AbstractShellIntegrationTest;
import alluxio.client.cli.fs.FileSystemShellUtilsTest;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.exception.AlluxioException;
import alluxio.grpc.SetAttributePOptions;
import alluxio.security.authorization.AclEntry;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for ls command.
 */
public final class LsCommandIntegrationTest extends AbstractFileSystemShellTest {
    /**
     * Tests ls command when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsNoAcl() {
        createFiles();
        mFsShell.run("ls", "/testRoot");
        checkOutput("              1   NOT_PERSISTED .+ .+  DIR /testRoot/testDir", "             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "             30       PERSISTED .+ .+   0% /testRoot/testFileC");
    }

    /**
     * Tests ls -h command when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsHumanReadable() throws AlluxioException, IOException {
        createFiles();
        mFsShell.run("ls", "-h", "/testRoot");
        checkOutput("              1   NOT_PERSISTED .+ .+  DIR /testRoot/testDir", "            10B   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "            30B       PERSISTED .+ .+   0% /testRoot/testFileC");
    }

    /**
     * Tests ls -p command when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsPinned() throws AlluxioException, IOException {
        createFiles();
        AlluxioURI fileURI1 = new AlluxioURI("/testRoot/testDir/testFileB");
        AlluxioURI fileURI2 = new AlluxioURI("/testRoot/testFileA");
        mFileSystem.setAttribute(fileURI1, SetAttributePOptions.newBuilder().setPinned(true).build());
        mFileSystem.setAttribute(fileURI2, SetAttributePOptions.newBuilder().setPinned(true).build());
        mFsShell.run("ls", "-pR", "/testRoot");
        checkOutput("             20   NOT_PERSISTED .+ .+ 100% /testRoot/testDir/testFileB", "             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA");
    }

    /**
     * Tests ls -d command when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsDirectoryAsPlainFileNoAcl() throws AlluxioException, IOException {
        createFiles();
        mFsShell.run("ls", "-d", "/testRoot");
        checkOutput("              3       PERSISTED .+ .+  DIR /testRoot");
    }

    /**
     * Tests ls -d command on root directory when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsRootNoAcl() throws AlluxioException, IOException {
        mFsShell.run("ls", "-d", "/");
        checkOutput("              0       PERSISTED .+ .+  DIR /    ");
    }

    /**
     * Tests ls command when security is enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_GROUP_MAPPING_CLASS, "alluxio.security.group.provider.IdentityUserGroupsMapping", Name.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test_user_ls" })
    public void ls() throws Exception {
        String testUser = "test_user_ls";
        clearAndLogin(testUser);
        createFiles();
        mFsShell.run("ls", "/testRoot");
        // CHECKSTYLE.OFF: LineLengthExceed - Improve readability
        checkOutput("drwxr-xr-x  test_user_ls   test_user_ls                 1   NOT_PERSISTED .+ .+  DIR /testRoot/testDir", "-rw-r--r--  test_user_ls   test_user_ls                10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "-rw-r--r--  test_user_ls   test_user_ls                30       PERSISTED .+ .+   0% /testRoot/testFileC");
        // CHECKSTYLE.ON: LineLengthExceed
    }

    /**
     * Tests ls command with wildcard when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsWildcardNoAcl() throws AlluxioException, IOException {
        String testDir = FileSystemShellUtilsTest.resetFileHierarchy(mFileSystem);
        mFsShell.run("ls", (testDir + "/*/foo*"));
        checkOutput("             30   NOT_PERSISTED .+ .+ 100% /testDir/bar/foobar3", "             10   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar1", "             20   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar2");
        mOutput.reset();
        mFsShell.run("ls", (testDir + "/*"));
        checkOutput("             30   NOT_PERSISTED .+ .+ 100% /testDir/bar/foobar3", "             10   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar1", "             20   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar2", "             40   NOT_PERSISTED .+ .+ 100% /testDir/foobar4");
    }

    /**
     * Tests ls command with wildcard when security is enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_GROUP_MAPPING_CLASS, "alluxio.security.group.provider.IdentityUserGroupsMapping", Name.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test_user_lsWildcard" })
    public void lsWildcard() throws Exception {
        String testUser = "test_user_lsWildcard";
        clearAndLogin(testUser);
        String testDir = FileSystemShellUtilsTest.resetFileHierarchy(mFileSystem);
        mFsShell.run("ls", (testDir + "/*/foo*"));
        // CHECKSTYLE.OFF: LineLengthExceed - Improve readability
        checkOutput("-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             30   NOT_PERSISTED .+ .+ 100% /testDir/bar/foobar3", "-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             10   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar1", "-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             20   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar2");
        mOutput.reset();
        mFsShell.run("ls", (testDir + "/*"));
        checkOutput("-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             30   NOT_PERSISTED .+ .+ 100% /testDir/bar/foobar3", "-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             10   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar1", "-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             20   NOT_PERSISTED .+ .+ 100% /testDir/foo/foobar2", "-rw-r--r--  test_user_lsWildcardtest_user_lsWildcard             40   NOT_PERSISTED .+ .+ 100% /testDir/foobar4");
        // CHECKSTYLE.ON: LineLengthExceed
    }

    /**
     * Tests ls -R command with wildcard when security is not enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsrNoAcl() throws AlluxioException, IOException {
        createFiles();
        mFsShell.run("ls", "-R", "/testRoot");
        checkOutput("              1   NOT_PERSISTED .+ .+  DIR /testRoot/testDir", "             20   NOT_PERSISTED .+ .+ 100% /testRoot/testDir/testFileB", "             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "             30       PERSISTED .+ .+   0% /testRoot/testFileC");
    }

    /**
     * Tests ls -R command with wildcard when security is enabled.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_GROUP_MAPPING_CLASS, "alluxio.security.group.provider.IdentityUserGroupsMapping", Name.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test_user_lsr" })
    public void lsr() throws Exception {
        String testUser = "test_user_lsr";
        clearAndLogin(testUser);
        createFiles();
        mFsShell.run("ls", "-R", "/testRoot");
        // CHECKSTYLE.OFF: LineLengthExceed - Improve readability
        checkOutput("drwxr-xr-x  test_user_lsr  test_user_lsr                1   NOT_PERSISTED .+ .+  DIR /testRoot/testDir", "-rw-r--r--  test_user_lsr  test_user_lsr               20   NOT_PERSISTED .+ .+ 100% /testRoot/testDir/testFileB", "-rw-r--r--  test_user_lsr  test_user_lsr               10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "-rw-r--r--  test_user_lsr  test_user_lsr               30       PERSISTED .+ .+   0% /testRoot/testFileC");
        // CHECKSTYLE.ON: LineLengthExceed
    }

    /**
     * Tests ls command with a file where the file name includes a specifier character.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsWithFormatSpecifierCharacter() throws AlluxioException, IOException {
        String fileName = "/localhost%2C61764%2C1476207067267..meta.1476207073442.meta";
        FileSystemTestUtils.createByteFile(mFileSystem, fileName, MUST_CACHE, 10);
        mFsShell.run("ls", "/");
        // CHECKSTYLE.OFF: LineLengthExceed - Improve readability
        checkOutput("             10   NOT_PERSISTED .+ .+ 100% /localhost%2C61764%2C1476207067267..meta.1476207073442.meta");
        // CHECKSTYLE.ON: LineLengthExceed
    }

    /**
     * Tests ls command with sort by path option.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsWithSortByPath() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testLongFile", MUST_CACHE, 100);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileZ", MUST_CACHE, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileA", MUST_CACHE, 50);
        mFsShell.run("ls", "--sort", "path", "/testRoot");
        checkOutput("             50   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileZ", "            100   NOT_PERSISTED .+ .+ 100% /testRoot/testLongFile");
    }

    /**
     * Tests ls command with sort by size option.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsWithSortBySize() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileA", MUST_CACHE, 50, 50);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileZ", MUST_CACHE, 10, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testLongFile", MUST_CACHE, 100, 100);
        mFsShell.run("ls", "--sort", "size", "/testRoot");
        checkOutput("             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileZ", "             50   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "            100   NOT_PERSISTED .+ .+ /testRoot/testLongFile");
    }

    /**
     * Tests ls command with sort by size and reverse order option.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsWithSortBySizeAndReverse() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileA", MUST_CACHE, 50, 50);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileZ", MUST_CACHE, 10, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testLongFile", MUST_CACHE, 100, 100);
        mFsShell.run("ls", "--sort", "size", "-r", "/testRoot");
        checkOutput("            100   NOT_PERSISTED .+ .+ 100% /testRoot/testLongFile", "             50   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA", "             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileZ");
    }

    /**
     * Tests ls command with an invalid sort option.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsWithInvalidSortOption() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileA", MUST_CACHE, 50, 50);
        mFsShell.run("ls", "--sort", "unknownfield", "/testRoot");
        String expected = "Invalid sort option `unknownfield` for --sort\n";
        Assert.assertEquals(expected, mOutput.toString());
    }

    /**
     * Tests ls command with reverse sort order option.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false", Name.SECURITY_AUTHENTICATION_TYPE, "NOSASL" })
    public void lsReverseWithoutSort() throws AlluxioException, IOException {
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileA", MUST_CACHE, 50, 50);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFileZ", MUST_CACHE, 10, 10);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testLongFile", MUST_CACHE, 100, 100);
        mFsShell.run("ls", "-r", "/testRoot");
        checkOutput("            100   NOT_PERSISTED .+ .+ 100% /testRoot/testLongFile", "             10   NOT_PERSISTED .+ .+ 100% /testRoot/testFileZ", "             50   NOT_PERSISTED .+ .+ 100% /testRoot/testFileA");
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_GROUP_MAPPING_CLASS, "alluxio.security.group.provider.IdentityUserGroupsMapping", Name.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test_user_extended" })
    public void lsWithExtendedAcl() throws AlluxioException, IOException {
        String testUser = "test_user_extended";
        int size = 50;
        clearAndLogin(testUser);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testDir/testFileB", MUST_CACHE, 20);
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testFile", MUST_CACHE, size, size);
        mFsShell.run("ls", "--sort", "path", "/testRoot");
        // CHECKSTYLE.OFF: LineLengthExceed - Improve readability
        checkOutput("drwxr-xr-x  test_user_extendedtest_user_extended              1   NOT_PERSISTED .+ .+ DIR /testRoot/testDir", "-rw-r--r--  test_user_extendedtest_user_extended             50   NOT_PERSISTED .+ .+ 100% /testRoot/testFile");
        // CHECKSTYLE.ON: LineLengthExceed
        mOutput.reset();
        mFileSystem.setAcl(new AlluxioURI("/testRoot/testDir"), MODIFY, Arrays.asList(AclEntry.fromCliString("default:user:nameduser:rwx")));
        mFileSystem.setAcl(new AlluxioURI("/testRoot/testFile"), MODIFY, Arrays.asList(AclEntry.fromCliString("user:nameduser:rwx")));
        mFsShell.run("ls", "--sort", "path", "/testRoot");
        // CHECKSTYLE.OFF: LineLengthExceed - Improve readability
        checkOutput("drwxr-xr-x\\+ test_user_extendedtest_user_extended              1   NOT_PERSISTED .+ .+  DIR /testRoot/testDir", "-rw-r--r--\\+ test_user_extendedtest_user_extended             50   NOT_PERSISTED .+ .+ 100% /testRoot/testFile");
        // CHECKSTYLE.ON: LineLengthExceed
    }
}

