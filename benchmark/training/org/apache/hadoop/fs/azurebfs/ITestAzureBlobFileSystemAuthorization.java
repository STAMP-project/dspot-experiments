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
package org.apache.hadoop.fs.azurebfs;


import FsAction.ALL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azurebfs.extensions.AbfsAuthorizationException;
import org.apache.hadoop.fs.azurebfs.extensions.MockAbfsAuthorizer;
import org.apache.hadoop.fs.azurebfs.utils.AclTestHelpers;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test Perform Authorization Check operation
 */
public class ITestAzureBlobFileSystemAuthorization extends AbstractAbfsIntegrationTest {
    private static final Path TEST_READ_ONLY_FILE_PATH_0 = new Path(MockAbfsAuthorizer.TEST_READ_ONLY_FILE_0);

    private static final Path TEST_READ_ONLY_FOLDER_PATH = new Path(MockAbfsAuthorizer.TEST_READ_ONLY_FOLDER);

    private static final Path TEST_WRITE_ONLY_FILE_PATH_0 = new Path(MockAbfsAuthorizer.TEST_WRITE_ONLY_FILE_0);

    private static final Path TEST_WRITE_ONLY_FILE_PATH_1 = new Path(MockAbfsAuthorizer.TEST_WRITE_ONLY_FILE_1);

    private static final Path TEST_READ_WRITE_FILE_PATH_0 = new Path(MockAbfsAuthorizer.TEST_READ_WRITE_FILE_0);

    private static final Path TEST_READ_WRITE_FILE_PATH_1 = new Path(MockAbfsAuthorizer.TEST_READ_WRITE_FILE_1);

    private static final Path TEST_WRITE_ONLY_FOLDER_PATH = new Path(MockAbfsAuthorizer.TEST_WRITE_ONLY_FOLDER);

    private static final Path TEST_WRITE_THEN_READ_ONLY_PATH = new Path(MockAbfsAuthorizer.TEST_WRITE_THEN_READ_ONLY);

    private static final String TEST_AUTHZ_CLASS = "org.apache.hadoop.fs.azurebfs.extensions.MockAbfsAuthorizer";

    private static final String TEST_USER = UUID.randomUUID().toString();

    private static final String TEST_GROUP = UUID.randomUUID().toString();

    private static final String BAR = UUID.randomUUID().toString();

    public ITestAzureBlobFileSystemAuthorization() throws Exception {
    }

    @Test
    public void testOpenFileWithInvalidPath() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        intercept(IllegalArgumentException.class, () -> {
            fs.open(new Path("")).close();
        });
    }

    @Test
    public void testOpenFileAuthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        fs.open(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
    }

    @Test
    public void testOpenFileUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.open(TEST_WRITE_ONLY_FILE_PATH_0).close();
        });
    }

    @Test
    public void testCreateFileAuthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
    }

    @Test
    public void testCreateFileUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.create(TEST_READ_ONLY_FILE_PATH_0).close();
        });
    }

    @Test
    public void testAppendFileAuthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        fs.append(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
    }

    @Test
    public void testAppendFileUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.append(TEST_WRITE_THEN_READ_ONLY_PATH).close();
        });
    }

    @Test
    public void testRenameAuthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.rename(ITestAzureBlobFileSystemAuthorization.TEST_READ_WRITE_FILE_PATH_0, ITestAzureBlobFileSystemAuthorization.TEST_READ_WRITE_FILE_PATH_1);
    }

    @Test
    public void testRenameUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.rename(TEST_WRITE_ONLY_FILE_PATH_0, TEST_WRITE_ONLY_FILE_PATH_1);
        });
    }

    @Test
    public void testDeleteFileAuthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        fs.delete(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0, false);
    }

    @Test
    public void testDeleteFileUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.delete(TEST_WRITE_THEN_READ_ONLY_PATH, false);
        });
    }

    @Test
    public void testListStatusAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        fs.listStatus(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH);
    }

    @Test
    public void testListStatusUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.listStatus(TEST_WRITE_ONLY_FILE_PATH_0);
        });
    }

    @Test
    public void testMkDirsAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        fs.mkdirs(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FOLDER_PATH, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE));
    }

    @Test
    public void testMkDirsUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.mkdirs(TEST_READ_ONLY_FOLDER_PATH, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE));
        });
    }

    @Test
    public void testGetFileStatusAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        fs.getFileStatus(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH);
    }

    @Test
    public void testGetFileStatusUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.getFileStatus(TEST_WRITE_ONLY_FILE_PATH_0);
        });
    }

    @Test
    public void testSetOwnerAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        fs.setOwner(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0, ITestAzureBlobFileSystemAuthorization.TEST_USER, ITestAzureBlobFileSystemAuthorization.TEST_GROUP);
    }

    @Test
    public void testSetOwnerUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.setOwner(TEST_WRITE_THEN_READ_ONLY_PATH, TEST_USER, TEST_GROUP);
        });
    }

    @Test
    public void testSetPermissionAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        fs.setPermission(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE));
    }

    @Test
    public void testSetPermissionUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.setPermission(TEST_WRITE_THEN_READ_ONLY_PATH, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE));
        });
    }

    @Test
    public void testModifyAclEntriesAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        fs.modifyAclEntries(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0, aclSpec);
    }

    @Test
    public void testModifyAclEntriesUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        intercept(AbfsAuthorizationException.class, () -> {
            fs.modifyAclEntries(TEST_WRITE_THEN_READ_ONLY_PATH, aclSpec);
        });
    }

    @Test
    public void testRemoveAclEntriesAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        fs.removeAclEntries(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0, aclSpec);
    }

    @Test
    public void testRemoveAclEntriesUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        intercept(AbfsAuthorizationException.class, () -> {
            fs.removeAclEntries(TEST_WRITE_THEN_READ_ONLY_PATH, aclSpec);
        });
    }

    @Test
    public void testRemoveDefaultAclAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        fs.removeDefaultAcl(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0);
    }

    @Test
    public void testRemoveDefaultAclUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.removeDefaultAcl(TEST_WRITE_THEN_READ_ONLY_PATH);
        });
    }

    @Test
    public void testRemoveAclAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        fs.removeAcl(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0);
    }

    @Test
    public void testRemoveAclUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        intercept(AbfsAuthorizationException.class, () -> {
            fs.removeAcl(TEST_WRITE_THEN_READ_ONLY_PATH);
        });
    }

    @Test
    public void testSetAclAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        fs.setAcl(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0, aclSpec);
    }

    @Test
    public void testSetAclUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        intercept(AbfsAuthorizationException.class, () -> {
            fs.setAcl(TEST_WRITE_THEN_READ_ONLY_PATH, aclSpec);
        });
    }

    @Test
    public void testGetAclStatusAuthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        fs.getAclStatus(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_THEN_READ_ONLY_PATH);
    }

    @Test
    public void testGetAclStatusUnauthorized() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Assume.assumeTrue("This test case only runs when namespace is enabled", fs.getIsNamespaceEnabled());
        fs.create(ITestAzureBlobFileSystemAuthorization.TEST_WRITE_ONLY_FILE_PATH_0).close();
        List<AclEntry> aclSpec = Arrays.asList(AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFileSystemAuthorization.BAR, ALL));
        intercept(AbfsAuthorizationException.class, () -> {
            fs.getAclStatus(TEST_WRITE_ONLY_FILE_PATH_0);
        });
    }
}

