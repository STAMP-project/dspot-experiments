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
import WritePType.MUST_CACHE;
import alluxio.client.cli.fs.AbstractFileSystemShellTest;
import alluxio.client.cli.fs.AbstractShellIntegrationTest;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.URIStatus;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for setfacl command.
 */
public final class SetFaclCommandIntegrationTest extends AbstractFileSystemShellTest {
    private static final List<String> FACL_STRING_ENTRIES = Arrays.asList("user::rw-", "group::r--", "other::r--");

    private static final List<String> DIR_FACL_STRING_ENTRIES = Arrays.asList("user::rwx", "group::r-x", "other::r-x");

    private static final List<String> FILE_FACL_STRING_ENTRIES = Arrays.asList("user::rw-", "group::r-x", "other::r--");

    private static final List<String> DEFAULT_FACL_STRING_ENTRIES = Arrays.asList("default:user::rwx", "default:group::r-x", "default:other::r-x");

    /**
     * Tests setfacl command.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_GROUP_MAPPING_CLASS, "alluxio.security.group.provider.IdentityUserGroupsMapping", Name.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test_user_setfacl" })
    public void setfacl() throws Exception {
        String testOwner = "test_user_setfacl";
        String expected = "";
        clearAndLogin(testOwner);
        URIStatus[] files = createFiles();
        mFsShell.run("setfacl", "-m", "user:testuser:rwx", "/testRoot/testFileA");
        mFsShell.run("getfacl", "/testRoot/testFileA");
        List<String> stringEntries = new ArrayList<>(SetFaclCommandIntegrationTest.FACL_STRING_ENTRIES);
        stringEntries.add("user:testuser:rwx");
        stringEntries.add("mask::rwx");
        expected += getFaclResultStr(testOwner, testOwner, "/testRoot/testFileA", stringEntries);
        Assert.assertEquals(expected, mOutput.toString());
        mFsShell.run("setfacl", "-m", "user::rwx", "/testRoot/testFileC");
        mFsShell.run("getfacl", "/testRoot/testFileC");
        stringEntries = new ArrayList<>(SetFaclCommandIntegrationTest.FACL_STRING_ENTRIES);
        stringEntries.set(0, "user::rwx");
        expected += getFaclResultStr(testOwner, testOwner, "/testRoot/testFileC", stringEntries);
        Assert.assertEquals(expected, mOutput.toString());
    }

    /**
     * Tests setfacl command to set default facl.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_GROUP_MAPPING_CLASS, "alluxio.security.group.provider.IdentityUserGroupsMapping", Name.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test_user_setDefaultFacl" })
    public void setDefaultFacl() throws Exception {
        String testOwner = "test_user_setDefaultFacl";
        clearAndLogin(testOwner);
        URIStatus[] files = createFiles();
        mFsShell.run("setfacl", "-m", "default:user:testuser:rwx", "/testRoot/testDir");
        mFsShell.run("getfacl", "/testRoot/testDir");
        List<String> stringEntries = new ArrayList<>(SetFaclCommandIntegrationTest.DIR_FACL_STRING_ENTRIES);
        stringEntries.addAll(SetFaclCommandIntegrationTest.DEFAULT_FACL_STRING_ENTRIES);
        stringEntries.add("default:user:testuser:rwx");
        stringEntries.add("default:mask::rwx");
        String expected = getFaclResultStr(testOwner, testOwner, "/testRoot/testDir", stringEntries);
        Assert.assertEquals(expected, mOutput.toString());
        FileSystemTestUtils.createByteFile(mFileSystem, "/testRoot/testDir/testDir2/testFileD", MUST_CACHE, 10);
        mFsShell.run("getfacl", "/testRoot/testDir/testDir2");
        stringEntries = new ArrayList<>(SetFaclCommandIntegrationTest.DIR_FACL_STRING_ENTRIES);
        stringEntries.add("user:testuser:rwx");
        stringEntries.add("mask::r-x");
        stringEntries.addAll(SetFaclCommandIntegrationTest.DEFAULT_FACL_STRING_ENTRIES);
        stringEntries.add("default:user:testuser:rwx");
        stringEntries.add("default:mask::rwx");
        expected += getFaclResultStr(testOwner, testOwner, "/testRoot/testDir/testDir2", stringEntries);
        Assert.assertEquals(expected, mOutput.toString());
        mFsShell.run("getfacl", "/testRoot/testDir/testDir2/testFileD");
        stringEntries = new ArrayList<>(SetFaclCommandIntegrationTest.FILE_FACL_STRING_ENTRIES);
        stringEntries.add("user:testuser:rwx");
        stringEntries.add("mask::r--");
        expected += getFaclResultStr(testOwner, testOwner, "/testRoot/testDir/testDir2/testFileD", stringEntries);
        Assert.assertEquals(expected, mOutput.toString());
    }
}

