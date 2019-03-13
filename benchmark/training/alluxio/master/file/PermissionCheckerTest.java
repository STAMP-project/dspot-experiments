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


import ExceptionMessage.PERMISSION_DENIED;
import LockPattern.READ;
import Mode.Bits;
import Mode.Bits.ALL;
import Mode.Bits.WRITE;
import alluxio.AlluxioURI;
import alluxio.exception.AccessControlException;
import alluxio.exception.InvalidPathException;
import alluxio.master.MasterRegistry;
import alluxio.master.file.contexts.CreateFileContext;
import alluxio.master.file.meta.InodeTree;
import alluxio.master.file.meta.LockedInodePath;
import alluxio.master.metastore.InodeStore;
import alluxio.master.metrics.MetricsMaster;
import alluxio.security.authentication.AuthenticatedClientUser;
import alluxio.security.authorization.Mode;
import alluxio.security.group.GroupMappingService;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link PermissionChecker}.
 */
public final class PermissionCheckerTest {
    private static final String TEST_SUPER_GROUP = "test-supergroup";

    /* The user and group mappings for testing are:
       admin -> admin
       user1 -> group1
       user2 -> group2
       user3 -> group1
       user4 -> group2,test-supergroup
     */
    private static final PermissionCheckerTest.TestUser TEST_USER_ADMIN = new PermissionCheckerTest.TestUser("admin", "admin");

    private static final PermissionCheckerTest.TestUser TEST_USER_1 = new PermissionCheckerTest.TestUser("user1", "group1");

    private static final PermissionCheckerTest.TestUser TEST_USER_2 = new PermissionCheckerTest.TestUser("user2", "group2");

    private static final PermissionCheckerTest.TestUser TEST_USER_3 = new PermissionCheckerTest.TestUser("user3", "group1");

    private static final PermissionCheckerTest.TestUser TEST_USER_SUPERGROUP = new PermissionCheckerTest.TestUser("user4", "group2,test-supergroup");

    /* The file structure for testing is:
       /               admin     admin       755
       /testDir        user1     group1      755
       /testDir/file   user1     group1      644
       /testFile       user2     group2      644
       /testWeirdFile  user1     group1      046
     */
    private static final String TEST_DIR_URI = "/testDir";

    private static final String TEST_DIR_FILE_URI = "/testDir/file";

    private static final String TEST_FILE_URI = "/testFile";

    private static final String TEST_NOT_EXIST_URI = "/testDir/notExistDir/notExistFile";

    private static final String TEST_WEIRD_FILE_URI = "/testWeirdFile";

    private static final Mode TEST_NORMAL_MODE = new Mode(((short) (493)));

    private static final Mode TEST_WEIRD_MODE = new Mode(((short) (111)));

    private static CreateFileContext sFileContext;

    private static CreateFileContext sWeirdFileContext;

    private static CreateFileContext sNestedFileContext;

    private static InodeStore sInodeStore;

    private static InodeTree sTree;

    private static MasterRegistry sRegistry;

    private static MetricsMaster sMetricsMaster;

    private PermissionChecker mPermissionChecker;

    @ClassRule
    public static TemporaryFolder sTestFolder = new TemporaryFolder();

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
     * Test class implements {@link GroupMappingService} providing user-to-groups mapping.
     */
    public static class FakeUserGroupsMapping implements GroupMappingService {
        private HashMap<String, String> mUserGroups = new HashMap<>();

        /**
         * Constructor of {@link FakeUserGroupsMapping} to put the user and groups in user-to-groups
         * HashMap.
         */
        public FakeUserGroupsMapping() {
            mUserGroups.put(PermissionCheckerTest.TEST_USER_ADMIN.getUser(), PermissionCheckerTest.TEST_USER_ADMIN.getGroup());
            mUserGroups.put(PermissionCheckerTest.TEST_USER_1.getUser(), PermissionCheckerTest.TEST_USER_1.getGroup());
            mUserGroups.put(PermissionCheckerTest.TEST_USER_2.getUser(), PermissionCheckerTest.TEST_USER_2.getGroup());
            mUserGroups.put(PermissionCheckerTest.TEST_USER_3.getUser(), PermissionCheckerTest.TEST_USER_3.getGroup());
            mUserGroups.put(PermissionCheckerTest.TEST_USER_SUPERGROUP.getUser(), PermissionCheckerTest.TEST_USER_SUPERGROUP.getGroup());
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            if (mUserGroups.containsKey(user)) {
                return Lists.newArrayList(mUserGroups.get(user).split(","));
            }
            return new ArrayList<>();
        }
    }

    @Test
    public void createFileAndDirs() throws Exception {
        try (LockedInodePath inodePath = PermissionCheckerTest.sTree.lockInodePath(new AlluxioURI(PermissionCheckerTest.TEST_DIR_FILE_URI), READ)) {
            PermissionCheckerTest.verifyInodesList(PermissionCheckerTest.TEST_DIR_FILE_URI.split("/"), inodePath.getInodeList());
        }
        try (LockedInodePath inodePath = PermissionCheckerTest.sTree.lockInodePath(new AlluxioURI(PermissionCheckerTest.TEST_FILE_URI), READ)) {
            PermissionCheckerTest.verifyInodesList(PermissionCheckerTest.TEST_FILE_URI.split("/"), inodePath.getInodeList());
        }
        try (LockedInodePath inodePath = PermissionCheckerTest.sTree.lockInodePath(new AlluxioURI(PermissionCheckerTest.TEST_WEIRD_FILE_URI), READ)) {
            PermissionCheckerTest.verifyInodesList(PermissionCheckerTest.TEST_WEIRD_FILE_URI.split("/"), inodePath.getInodeList());
        }
        try (LockedInodePath inodePath = PermissionCheckerTest.sTree.lockInodePath(new AlluxioURI(PermissionCheckerTest.TEST_NOT_EXIST_URI), READ)) {
            PermissionCheckerTest.verifyInodesList(new String[]{ "", "testDir" }, inodePath.getInodeList());
        }
    }

    @Test
    public void fileSystemOwner() throws Exception {
        checkPermission(PermissionCheckerTest.TEST_USER_ADMIN, ALL, PermissionCheckerTest.TEST_DIR_FILE_URI);
        checkPermission(PermissionCheckerTest.TEST_USER_ADMIN, ALL, PermissionCheckerTest.TEST_DIR_URI);
        checkPermission(PermissionCheckerTest.TEST_USER_ADMIN, ALL, PermissionCheckerTest.TEST_FILE_URI);
    }

    @Test
    public void fileSystemSuperGroup() throws Exception {
        checkPermission(PermissionCheckerTest.TEST_USER_SUPERGROUP, ALL, PermissionCheckerTest.TEST_DIR_FILE_URI);
        checkPermission(PermissionCheckerTest.TEST_USER_SUPERGROUP, ALL, PermissionCheckerTest.TEST_DIR_URI);
        checkPermission(PermissionCheckerTest.TEST_USER_SUPERGROUP, ALL, PermissionCheckerTest.TEST_FILE_URI);
    }

    @Test
    public void selfCheckSuccess() throws Exception {
        // the same owner
        checkPermission(PermissionCheckerTest.TEST_USER_1, Mode.Bits.READ, PermissionCheckerTest.TEST_DIR_FILE_URI);
        checkPermission(PermissionCheckerTest.TEST_USER_1, WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI);
        // not the owner and in other group
        checkPermission(PermissionCheckerTest.TEST_USER_2, Mode.Bits.READ, PermissionCheckerTest.TEST_DIR_FILE_URI);
        // not the owner but in same group
        checkPermission(PermissionCheckerTest.TEST_USER_3, Mode.Bits.READ, PermissionCheckerTest.TEST_DIR_FILE_URI);
    }

    @Test
    public void checkNoFallThroughFromOwnerToGroup() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_1.getUser(), Mode.Bits.READ, PermissionCheckerTest.TEST_WEIRD_FILE_URI, "testWeirdFile")));
        // user cannot read although group can
        checkPermission(PermissionCheckerTest.TEST_USER_1, Mode.Bits.READ, PermissionCheckerTest.TEST_WEIRD_FILE_URI);
    }

    @Test
    public void checkNoFallThroughFromOwnerToOther() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_1.getUser(), WRITE, PermissionCheckerTest.TEST_WEIRD_FILE_URI, "testWeirdFile")));
        // user and group cannot write although other can
        checkPermission(PermissionCheckerTest.TEST_USER_1, WRITE, PermissionCheckerTest.TEST_WEIRD_FILE_URI);
    }

    @Test
    public void checkNoFallThroughFromGroupToOther() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_3.getUser(), WRITE, PermissionCheckerTest.TEST_WEIRD_FILE_URI, "testWeirdFile")));
        // group cannot write although other can
        checkPermission(PermissionCheckerTest.TEST_USER_3, WRITE, PermissionCheckerTest.TEST_WEIRD_FILE_URI);
    }

    @Test
    public void selfCheckFailByOtherGroup() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_2.getUser(), WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI, "file")));
        // not the owner and in other group
        checkPermission(PermissionCheckerTest.TEST_USER_2, WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI);
    }

    @Test
    public void selfCheckFailBySameGroup() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_3.getUser(), WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI, "file")));
        // not the owner but in same group
        checkPermission(PermissionCheckerTest.TEST_USER_3, WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI);
    }

    @Test
    public void parentCheckSuccess() throws Exception {
        checkParentOrAncestorPermission(PermissionCheckerTest.TEST_USER_1, WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI);
    }

    @Test
    public void parentCheckFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_2.getUser(), WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI, "testDir")));
        checkParentOrAncestorPermission(PermissionCheckerTest.TEST_USER_2, WRITE, PermissionCheckerTest.TEST_DIR_FILE_URI);
    }

    @Test
    public void ancestorCheckSuccess() throws Exception {
        checkParentOrAncestorPermission(PermissionCheckerTest.TEST_USER_1, WRITE, PermissionCheckerTest.TEST_NOT_EXIST_URI);
    }

    @Test
    public void ancestorCheckFail() throws Exception {
        mThrown.expect(AccessControlException.class);
        mThrown.expectMessage(PERMISSION_DENIED.getMessage(toExceptionMessage(PermissionCheckerTest.TEST_USER_2.getUser(), WRITE, PermissionCheckerTest.TEST_NOT_EXIST_URI, "testDir")));
        checkParentOrAncestorPermission(PermissionCheckerTest.TEST_USER_2, WRITE, PermissionCheckerTest.TEST_NOT_EXIST_URI);
    }

    @Test
    public void invalidPath() throws Exception {
        mThrown.expect(InvalidPathException.class);
        try (LockedInodePath inodePath = PermissionCheckerTest.sTree.lockInodePath(new AlluxioURI(""), READ)) {
            mPermissionChecker.checkPermission(WRITE, inodePath);
        }
    }

    @Test
    public void getPermission() throws Exception {
        try (LockedInodePath path = PermissionCheckerTest.sTree.lockInodePath(new AlluxioURI(PermissionCheckerTest.TEST_WEIRD_FILE_URI), READ)) {
            // user is admin
            AuthenticatedClientUser.set(PermissionCheckerTest.TEST_USER_ADMIN.getUser());
            Mode.Bits perm = mPermissionChecker.getPermission(path);
            Assert.assertEquals(ALL, perm);
            // user is owner
            AuthenticatedClientUser.set(PermissionCheckerTest.TEST_USER_1.getUser());
            perm = mPermissionChecker.getPermission(path);
            Assert.assertEquals(PermissionCheckerTest.TEST_WEIRD_MODE.getOwnerBits(), perm);
            // user is not owner but in group
            AuthenticatedClientUser.set(PermissionCheckerTest.TEST_USER_3.getUser());
            perm = mPermissionChecker.getPermission(path);
            Assert.assertEquals(PermissionCheckerTest.TEST_WEIRD_MODE.getGroupBits(), perm);
            // user is other
            AuthenticatedClientUser.set(PermissionCheckerTest.TEST_USER_2.getUser());
            perm = mPermissionChecker.getPermission(path);
            Assert.assertEquals(PermissionCheckerTest.TEST_WEIRD_MODE.getOtherBits(), perm);
        }
    }
}

