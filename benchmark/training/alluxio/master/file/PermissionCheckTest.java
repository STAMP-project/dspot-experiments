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
package alluxio.master.file;


import ExceptionMessage.PATH_DOES_NOT_EXIST;
import ExceptionMessage.PERMISSION_DENIED;
import Mode.Bits.EXECUTE;
import Mode.Bits.READ;
import Mode.Bits.WRITE;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP;
import PropertyKey.SECURITY_GROUP_MAPPING_CLASS;
import alluxio.AlluxioTestDirectory;
import alluxio.AlluxioURI;
import alluxio.AuthenticatedUserRule;
import alluxio.ConfigurationRule;
import alluxio.LoginUserRule;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.AccessControlException;
import alluxio.exception.FileDoesNotExistException;
import alluxio.grpc.SetAttributePOptions;
import alluxio.master.MasterRegistry;
import alluxio.master.file.contexts.CompleteFileContext;
import alluxio.master.metrics.MetricsMaster;
import alluxio.security.authorization.Mode;
import alluxio.security.group.GroupMappingService;
import alluxio.util.io.PathUtils;
import alluxio.wire.FileInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Unit test for {@link FileSystemMaster} when permission check is enabled by configure
 * alluxio.security.authorization.permission.enabled=true.
 */
public final class PermissionCheckTest {
    private static final String TEST_SUPER_GROUP = "test-supergroup";

    /* The user and group mappings for testing are:
       admin -> admin
       user1 -> group1
       user2 -> group2
       user3 -> group1
       user4 -> test-supergroup
     */
    private static final PermissionCheckTest.TestUser TEST_USER_ADMIN = new PermissionCheckTest.TestUser("admin", "admin");

    private static final PermissionCheckTest.TestUser TEST_USER_1 = new PermissionCheckTest.TestUser("user1", "group1");

    private static final PermissionCheckTest.TestUser TEST_USER_2 = new PermissionCheckTest.TestUser("user2", "group2");

    private static final PermissionCheckTest.TestUser TEST_USER_3 = new PermissionCheckTest.TestUser("user3", "group1");

    private static final PermissionCheckTest.TestUser TEST_USER_SUPERGROUP = new PermissionCheckTest.TestUser("user4", PermissionCheckTest.TEST_SUPER_GROUP);

    /* The file structure for testing is:
       /               admin     admin       755
       /testDir        user1     group1      755
       /testDir/file   user1     group1      644
       /testFile       user2     group2      644
     */
    private static final String TEST_DIR_URI = "/testDir";

    private static final String TEST_DIR_FILE_URI = "/testDir/file";

    private static final String TEST_FILE_URI = "/testFile";

    private static final Mode TEST_DIR_MODE = new Mode(((short) (493)));

    private static final Mode TEST_FILE_MODE = new Mode(((short) (493)));

    private MasterRegistry mRegistry;

    private MetricsMaster mMetricsMaster;

    private FileSystemMaster mFileSystemMaster;

    @Rule
    public ConfigurationRule mConfiguration = new ConfigurationRule(new ImmutableMap.Builder<alluxio.conf.PropertyKey, String>().put(SECURITY_GROUP_MAPPING_CLASS, PermissionCheckTest.FakeUserGroupsMapping.class.getName()).put(SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, PermissionCheckTest.TEST_SUPER_GROUP).put(MASTER_MOUNT_TABLE_ROOT_UFS, AlluxioTestDirectory.createTemporaryDirectory("PermissionCheckTest").getAbsolutePath()).build(), ServerConfiguration.global());

    @Rule
    public AuthenticatedUserRule mAuthenticatedUser = new AuthenticatedUserRule(PermissionCheckTest.TEST_USER_ADMIN.getUser(), ServerConfiguration.global());

    @Rule
    public LoginUserRule mLoginUserRule = new LoginUserRule(PermissionCheckTest.TEST_USER_ADMIN.getUser(), ServerConfiguration.global());

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * A simple structure to represent a user and its groups.
     */
    private static final class TestUser {
        private String mUser;

        private String mGroup;

        TestUser(String user, String group) {
            mUser = user;
            mGroup = group;
        }

        String getUser() {
            return mUser;
        }

        String getGroup() {
            return mGroup;
        }
    }

    /**
     * A mapping from a user to its corresponding group.
     */
    public static class FakeUserGroupsMapping implements GroupMappingService {
        private HashMap<String, String> mUserGroups = new HashMap<>();

        public FakeUserGroupsMapping() {
            mUserGroups.put(PermissionCheckTest.TEST_USER_ADMIN.getUser(), PermissionCheckTest.TEST_USER_ADMIN.getGroup());
            mUserGroups.put(PermissionCheckTest.TEST_USER_1.getUser(), PermissionCheckTest.TEST_USER_1.getGroup());
            mUserGroups.put(PermissionCheckTest.TEST_USER_2.getUser(), PermissionCheckTest.TEST_USER_2.getGroup());
            mUserGroups.put(PermissionCheckTest.TEST_USER_3.getUser(), PermissionCheckTest.TEST_USER_3.getGroup());
            mUserGroups.put(PermissionCheckTest.TEST_USER_SUPERGROUP.getUser(), PermissionCheckTest.TEST_USER_SUPERGROUP.getGroup());
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            if (mUserGroups.containsKey(user)) {
                return Lists.newArrayList(mUserGroups.get(user).split(","));
            }
            return new ArrayList<>();
        }
    }

    /**
     * Tests superuser and supergroup to create directories under root.
     */
    @Test
    public void createUnderRootAsAdmin() throws Exception {
        // create "/file_admin" for superuser
        verifyCreateFile(PermissionCheckTest.TEST_USER_ADMIN, "/file_admin", false);
        // create "/file_supergroup" for user in supergroup
        verifyCreateFile(PermissionCheckTest.TEST_USER_SUPERGROUP, "/file_supergroup", false);
    }

    /**
     * Tests user1 to create directories under root.
     */
    @Test
    public void createUnderRootFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_1.getUser(), WRITE, "/file1", "/")));
        // create "/file1" for user1
        verifyCreateFile(PermissionCheckTest.TEST_USER_1, "/file1", false);
    }

    @Test
    public void createSuccess() throws Exception {
        // create "/testDir/file1" for user1
        verifyCreateFile(PermissionCheckTest.TEST_USER_1, ((PermissionCheckTest.TEST_DIR_URI) + "/file1"), false);
    }

    @Test
    public void createFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, ((PermissionCheckTest.TEST_DIR_URI) + "/file1"), "testDir")));
        // create "/testDir/file1" for user2
        verifyCreateFile(PermissionCheckTest.TEST_USER_2, ((PermissionCheckTest.TEST_DIR_URI) + "/file1"), false);
    }

    @Test
    public void mkdirUnderRootByAdmin() throws Exception {
        // createDirectory "/dir_admin" for superuser
        verifyCreateDirectory(PermissionCheckTest.TEST_USER_ADMIN, "/dir_admin", false);
    }

    @Test
    public void mkdirUnderRootBySupergroup() throws Exception {
        // createDirectory "/dir_admin" for superuser
        verifyCreateDirectory(PermissionCheckTest.TEST_USER_SUPERGROUP, "/dir_admin", false);
    }

    @Test
    public void mkdirUnderRootByUser() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_1.getUser(), WRITE, "/dir1", "/")));
        // createDirectory "/dir1" for user1
        verifyCreateDirectory(PermissionCheckTest.TEST_USER_1, "/dir1", false);
    }

    @Test
    public void mkdirSuccess() throws Exception {
        // createDirectory "/testDir/dir1" for user1
        verifyCreateDirectory(PermissionCheckTest.TEST_USER_1, ((PermissionCheckTest.TEST_DIR_URI) + "/dir1"), false);
    }

    @Test
    public void mkdirFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, ((PermissionCheckTest.TEST_DIR_URI) + "/dir1"), "testDir")));
        // createDirectory "/testDir/dir1" for user2
        verifyCreateDirectory(PermissionCheckTest.TEST_USER_2, ((PermissionCheckTest.TEST_DIR_URI) + "/dir1"), false);
    }

    @Test
    public void renameUnderRootAsAdmin() throws Exception {
        // rename "/testFile" to "/testFileRenamed" for superuser
        verifyRename(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_FILE_URI, "/testFileRenamed");
    }

    @Test
    public void renameUnderRootAsSupergroup() throws Exception {
        // rename "/testFile" to "/testFileRenamed" for user in supergroup
        verifyRename(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_FILE_URI, "/testFileRenamed");
    }

    @Test
    public void renameUnderRootFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_1.getUser(), WRITE, PermissionCheckTest.TEST_FILE_URI, "/")));
        // rename "/testFile" to "/testFileRenamed" for user1
        verifyRename(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_FILE_URI, "/testFileRenamed");
    }

    @Test
    public void renameSuccess() throws Exception {
        // rename "/testDir/file" to "/testDir/fileRenamed" for user1
        verifyRename(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_FILE_URI, "/testDir/fileRenamed");
    }

    @Test
    public void renameFailNotByPermission() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mThrown.expectMessage(PATH_DOES_NOT_EXIST.getMessage("/testDir/notExistDir"));
        // rename "/testDir/file" to "/testDir/notExistDir/fileRenamed" for user1
        // This is permitted by permission checking model, but failed during renaming procedure,
        // since the impl cannot rename a file to a dst path whose parent does not exist.
        verifyRename(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_FILE_URI, "/testDir/notExistDir/fileRenamed");
    }

    @Test
    public void renameFailBySrc() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, PermissionCheckTest.TEST_DIR_FILE_URI, "testDir")));
        // rename "/testDir/file" to "/file" for user2
        verifyRename(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_DIR_FILE_URI, "/file");
    }

    @Test
    public void renameFailByDst() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_1.getUser(), WRITE, "/fileRenamed", "/")));
        // rename "/testDir/file" to "/fileRenamed" for user2
        verifyRename(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_FILE_URI, "/fileRenamed");
    }

    @Test
    public void deleteUnderRootFailed() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_1.getUser(), WRITE, PermissionCheckTest.TEST_DIR_URI, "/")));
        // delete file and dir under root by owner
        verifyDelete(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_URI, true);
    }

    @Test
    public void deleteSuccessBySuperuser() throws Exception {
        // delete file and dir by superuser
        verifyDelete(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_DIR_FILE_URI, false);
        verifyDelete(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_DIR_URI, true);
        verifyDelete(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_FILE_URI, false);
    }

    @Test
    public void deleteSuccessBySupergroup() throws Exception {
        // delete file and dir by user in supergroup
        verifyDelete(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_DIR_FILE_URI, false);
        verifyDelete(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_DIR_URI, true);
        verifyDelete(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_FILE_URI, false);
    }

    @Test
    public void deleteUnderRootFailOnDir() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, PermissionCheckTest.TEST_DIR_URI, "/")));
        // user2 cannot delete "/testDir" under root
        verifyDelete(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_DIR_URI, true);
    }

    @Test
    public void deleteUnderRootFailOnFile() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_1.getUser(), WRITE, PermissionCheckTest.TEST_FILE_URI, "/")));
        // user2 cannot delete "/testFile" under root
        verifyDelete(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_FILE_URI, true);
    }

    @Test
    public void deleteSuccess() throws Exception {
        // user1 can delete its file
        verifyDelete(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_FILE_URI, false);
    }

    @Test
    public void deleteFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, PermissionCheckTest.TEST_DIR_FILE_URI, "testDir")));
        // user 2 cannot delete "/testDir/file"
        verifyDelete(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_DIR_FILE_URI, false);
    }

    @Test
    public void readSuccess() throws Exception {
        verifyRead(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_FILE_URI, true);
        verifyRead(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_URI, false);
        verifyRead(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_FILE_URI, true);
        verifyRead(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_DIR_FILE_URI, true);
    }

    @Test
    public void readFileIdFail() throws Exception {
        String file = createUnreadableFileOrDir(true);
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), READ, file, "onlyReadByUser1")));
        verifyGetFileId(PermissionCheckTest.TEST_USER_2, file);
    }

    @Test
    public void readFileInfoFail() throws Exception {
        String file = createUnreadableFileOrDir(true);
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), READ, file, "onlyReadByUser1")));
        verifyGetFileInfoOrList(PermissionCheckTest.TEST_USER_2, file, true);
    }

    @Test
    public void readDirIdFail() throws Exception {
        String dir = createUnreadableFileOrDir(false);
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), READ, dir, "onlyReadByUser1")));
        verifyGetFileId(PermissionCheckTest.TEST_USER_2, dir);
    }

    @Test
    public void readDirInfoFail() throws Exception {
        String dir = createUnreadableFileOrDir(false);
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), READ, dir, "onlyReadByUser1")));
        try (Closeable r = toResource()) {
            verifyGetFileInfoOrList(PermissionCheckTest.TEST_USER_2, dir, false);
        }
    }

    @Test
    public void readNotExecuteDir() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String dir = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "/notExecuteDir");
            // create dir "/testDir/notExecuteDir" [user1, group1, drwxr--r--]
            verifyCreateDirectory(PermissionCheckTest.TEST_USER_1, dir, false);
            verifyRead(PermissionCheckTest.TEST_USER_1, dir, false);
            mThrown.expect(AccessControlException.class);
            mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), EXECUTE, dir, "notExecuteDir")));
            verifyGetFileInfoOrList(PermissionCheckTest.TEST_USER_2, dir, false);
        }
    }

    @Test
    public void setStateSuccess() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String file = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "testState1");
            verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
            SetAttributePOptions expect = getNonDefaultSetState();
            SetAttributePOptions result = verifySetState(PermissionCheckTest.TEST_USER_2, file, expect);
            Assert.assertEquals(expect.getCommonOptions().getTtl(), result.getCommonOptions().getTtl());
            Assert.assertEquals(expect.getCommonOptions().getTtlAction(), result.getCommonOptions().getTtlAction());
            Assert.assertEquals(expect.getPinned(), result.getPinned());
        }
    }

    @Test
    public void setStateFail() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String file = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "testState1");
            verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
            SetAttributePOptions expect = getNonDefaultSetState();
            mThrown.expect(AccessControlException.class);
            mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, file, "testState1")));
            verifySetState(PermissionCheckTest.TEST_USER_2, file, expect);
        }
    }

    @Test
    public void completeFileSuccess() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String file = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "/testState1");
            verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
            CompleteFileContext expect = getNonDefaultCompleteFileContext();
            verifyCompleteFile(PermissionCheckTest.TEST_USER_2, file, expect);
        }
    }

    @Test
    public void completeFileFail() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String file = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "/testComplete1");
            verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
            CompleteFileContext expect = getNonDefaultCompleteFileContext();
            mThrown.expect(AccessControlException.class);
            mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), WRITE, file, "testComplete1")));
            verifyCompleteFile(PermissionCheckTest.TEST_USER_2, file, expect);
        }
    }

    @Test
    public void freeFileSuccess() throws Exception {
        String file = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "testState1");
        verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
        verifyFree(PermissionCheckTest.TEST_USER_2, file, false);
    }

    @Test
    public void freeNonNullDirectorySuccess() throws Exception {
        String subDir = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "testState");
        verifyCreateDirectory(PermissionCheckTest.TEST_USER_1, subDir, false);
        String file = subDir + "/testState1";
        verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
        verifyFree(PermissionCheckTest.TEST_USER_2, subDir, true);
    }

    @Test
    public void freeFileFail() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String file = PathUtils.concatPath(PermissionCheckTest.TEST_DIR_URI, "testComplete1");
            verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
            mThrown.expect(AccessControlException.class);
            mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), READ, file, "testComplete1")));
            verifyFree(PermissionCheckTest.TEST_USER_2, file, false);
        }
    }

    @Test
    public void freeNonNullDirectoryFail() throws Exception {
        // set unmask
        try (Closeable c = toResource()) {
            String file = PathUtils.concatPath(((PermissionCheckTest.TEST_DIR_URI) + "/testComplete1"));
            verifyCreateFile(PermissionCheckTest.TEST_USER_1, file, false);
            mThrown.expect(AccessControlException.class);
            mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckTest.TEST_USER_2.getUser(), READ, file, "testComplete1")));
            verifyFree(PermissionCheckTest.TEST_USER_2, file, false);
        }
    }

    @Test
    public void setOwnerSuccess() throws Exception {
        verifySetAcl(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_FILE_URI, PermissionCheckTest.TEST_USER_1.getUser(), null, ((short) (-1)), false);
        verifySetAcl(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_DIR_URI, PermissionCheckTest.TEST_USER_2.getUser(), null, ((short) (-1)), true);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(mFileSystemMaster.getFileId(new AlluxioURI(PermissionCheckTest.TEST_DIR_FILE_URI)));
        Assert.assertEquals(PermissionCheckTest.TEST_USER_2.getUser(), fileInfo.getOwner());
    }

    @Test
    public void setOwnerFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(((PermissionCheckTest.TEST_USER_2.getUser()) + " is not a super user or in super group"));
        verifySetAcl(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_FILE_URI, PermissionCheckTest.TEST_USER_1.getUser(), null, ((short) (-1)), false);
    }

    @Test
    public void setGroupSuccess() throws Exception {
        // super user
        verifySetAcl(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_FILE_URI, null, PermissionCheckTest.TEST_USER_1.getGroup(), ((short) (-1)), false);
        // super group
        verifySetAcl(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_DIR_URI, null, PermissionCheckTest.TEST_USER_2.getGroup(), ((short) (-1)), true);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(mFileSystemMaster.getFileId(new AlluxioURI(PermissionCheckTest.TEST_DIR_FILE_URI)));
        Assert.assertEquals(PermissionCheckTest.TEST_USER_2.getGroup(), fileInfo.getGroup());
        // owner
        verifySetAcl(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_URI, null, PermissionCheckTest.TEST_USER_2.getGroup(), ((short) (-1)), true);
        fileInfo = mFileSystemMaster.getFileInfo(mFileSystemMaster.getFileId(new AlluxioURI(PermissionCheckTest.TEST_DIR_FILE_URI)));
        Assert.assertEquals(PermissionCheckTest.TEST_USER_2.getGroup(), fileInfo.getGroup());
    }

    @Test
    public void setGroupFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(((("user=" + (PermissionCheckTest.TEST_USER_1.getUser())) + " is not the owner of path=") + (PermissionCheckTest.TEST_FILE_URI))));
        verifySetAcl(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_FILE_URI, null, PermissionCheckTest.TEST_USER_1.getGroup(), ((short) (-1)), false);
    }

    @Test
    public void setPermissionSuccess() throws Exception {
        // super user
        verifySetAcl(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_FILE_URI, null, null, ((short) (384)), false);
        // super group
        verifySetAcl(PermissionCheckTest.TEST_USER_SUPERGROUP, PermissionCheckTest.TEST_DIR_URI, null, null, ((short) (448)), true);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(mFileSystemMaster.getFileId(new AlluxioURI(PermissionCheckTest.TEST_DIR_FILE_URI)));
        Assert.assertEquals(((short) (448)), fileInfo.getMode());
        // owner enlarge the permission
        verifySetAcl(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_URI, null, null, ((short) (511)), true);
        fileInfo = mFileSystemMaster.getFileInfo(mFileSystemMaster.getFileId(new AlluxioURI(PermissionCheckTest.TEST_DIR_FILE_URI)));
        Assert.assertEquals(((short) (511)), fileInfo.getMode());
        // other user can operate under this enlarged permission
        verifyCreateFile(PermissionCheckTest.TEST_USER_2, ((PermissionCheckTest.TEST_DIR_URI) + "/newFile"), false);
        verifyDelete(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_DIR_FILE_URI, false);
    }

    @Test
    public void setPermissionFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(((("user=" + (PermissionCheckTest.TEST_USER_1.getUser())) + " is not the owner of path=") + (PermissionCheckTest.TEST_FILE_URI))));
        verifySetAcl(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_FILE_URI, null, null, ((short) (511)), false);
    }

    @Test
    public void setAclSuccess() throws Exception {
        // super user sets owner, group, and permission
        verifySetAcl(PermissionCheckTest.TEST_USER_ADMIN, PermissionCheckTest.TEST_FILE_URI, PermissionCheckTest.TEST_USER_1.getUser(), PermissionCheckTest.TEST_USER_1.getGroup(), ((short) (384)), false);
        // owner sets group and permission
        verifySetAcl(PermissionCheckTest.TEST_USER_1, PermissionCheckTest.TEST_DIR_URI, null, PermissionCheckTest.TEST_USER_2.getGroup(), ((short) (511)), true);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(mFileSystemMaster.getFileId(new AlluxioURI(PermissionCheckTest.TEST_DIR_FILE_URI)));
        Assert.assertEquals(PermissionCheckTest.TEST_USER_2.getGroup(), fileInfo.getGroup());
        Assert.assertEquals(((short) (511)), fileInfo.getMode());
    }

    @Test
    public void setAclFailByNotSuperUser() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(((PermissionCheckTest.TEST_USER_2.getUser()) + " is not a super user or in super group"));
        verifySetAcl(PermissionCheckTest.TEST_USER_2, PermissionCheckTest.TEST_FILE_URI, PermissionCheckTest.TEST_USER_1.getUser(), PermissionCheckTest.TEST_USER_1.getGroup(), ((short) (384)), false);
    }
}

