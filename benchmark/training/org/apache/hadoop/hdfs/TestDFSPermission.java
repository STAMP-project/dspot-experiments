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
package org.apache.hadoop.hdfs;


import CommonConfigurationKeys.FS_TRASH_INTERVAL_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import FsAction.EXECUTE;
import FsAction.READ;
import FsAction.READ_WRITE;
import FsAction.WRITE;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Trash;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for permission
 */
public class TestDFSPermission {
    public static final Logger LOG = LoggerFactory.getLogger(TestDFSPermission.class);

    private static final Configuration conf = new HdfsConfiguration();

    private static final String GROUP1_NAME = "group1";

    private static final String GROUP2_NAME = "group2";

    private static final String GROUP3_NAME = "group3";

    private static final String GROUP4_NAME = "group4";

    private static final String USER1_NAME = "user1";

    private static final String USER2_NAME = "user2";

    private static final String USER3_NAME = "user3";

    private static final UserGroupInformation SUPERUSER;

    private static final UserGroupInformation USER1;

    private static final UserGroupInformation USER2;

    private static final UserGroupInformation USER3;

    private static final short MAX_PERMISSION = 511;

    private static final short DEFAULT_UMASK = 18;

    private static final FsPermission DEFAULT_PERMISSION = FsPermission.createImmutable(((short) (511)));

    private static final int NUM_TEST_PERMISSIONS = ((TestDFSPermission.conf.getInt("test.dfs.permission.num", 10)) * ((TestDFSPermission.MAX_PERMISSION) + 1)) / 100;

    private static final String PATH_NAME = "xx";

    private static final Path FILE_DIR_PATH = new Path("/", TestDFSPermission.PATH_NAME);

    private static final Path NON_EXISTENT_PATH = new Path("/parent", TestDFSPermission.PATH_NAME);

    private static final Path NON_EXISTENT_FILE = new Path("/NonExistentFile");

    private FileSystem fs;

    private MiniDFSCluster cluster;

    private static final Random r;

    static {
        try {
            // Initiate the random number generator and logging the seed
            long seed = Time.now();
            r = new Random(seed);
            TestDFSPermission.LOG.info(("Random number generator uses seed " + seed));
            TestDFSPermission.LOG.info(("NUM_TEST_PERMISSIONS=" + (TestDFSPermission.NUM_TEST_PERMISSIONS)));
            // explicitly turn on permission checking
            TestDFSPermission.conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, true);
            // create fake mapping for the groups
            Map<String, String[]> u2g_map = new HashMap<String, String[]>(3);
            u2g_map.put(TestDFSPermission.USER1_NAME, new String[]{ TestDFSPermission.GROUP1_NAME, TestDFSPermission.GROUP2_NAME });
            u2g_map.put(TestDFSPermission.USER2_NAME, new String[]{ TestDFSPermission.GROUP2_NAME, TestDFSPermission.GROUP3_NAME });
            u2g_map.put(TestDFSPermission.USER3_NAME, new String[]{ TestDFSPermission.GROUP3_NAME, TestDFSPermission.GROUP4_NAME });
            DFSTestUtil.updateConfWithFakeGroupMapping(TestDFSPermission.conf, u2g_map);
            // Initiate all four users
            SUPERUSER = UserGroupInformation.getCurrentUser();
            USER1 = UserGroupInformation.createUserForTesting(TestDFSPermission.USER1_NAME, new String[]{ TestDFSPermission.GROUP1_NAME, TestDFSPermission.GROUP2_NAME });
            USER2 = UserGroupInformation.createUserForTesting(TestDFSPermission.USER2_NAME, new String[]{ TestDFSPermission.GROUP2_NAME, TestDFSPermission.GROUP3_NAME });
            USER3 = UserGroupInformation.createUserForTesting(TestDFSPermission.USER3_NAME, new String[]{ TestDFSPermission.GROUP3_NAME, TestDFSPermission.GROUP4_NAME });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This tests if permission setting in create, mkdir, and
     * setPermission works correctly
     */
    @Test
    public void testPermissionSetting() throws Exception {
        testPermissionSetting(TestDFSPermission.OpType.CREATE);// test file creation

        testPermissionSetting(TestDFSPermission.OpType.MKDIRS);// test directory creation

    }

    /**
     * check that ImmutableFsPermission can be used as the argument
     * to setPermission
     */
    @Test
    public void testImmutableFsPermission() throws IOException {
        fs = FileSystem.get(TestDFSPermission.conf);
        // set the permission of the root to be world-wide rwx
        fs.setPermission(new Path("/"), FsPermission.createImmutable(((short) (511))));
    }

    @Test(timeout = 30000)
    public void testTrashPermission() throws Exception {
        // /BSS                  user1:group2 777
        // /BSS/user1            user1:group2 755
        // /BSS/user1/test       user1:group1 600
        Path rootDir = new Path("/BSS");
        Path user1Dir = new Path("/BSS/user1");
        Path user1File = new Path("/BSS/user1/test");
        try {
            TestDFSPermission.conf.set(FS_TRASH_INTERVAL_KEY, "10");
            fs = FileSystem.get(TestDFSPermission.conf);
            fs.mkdirs(rootDir);
            fs.setPermission(rootDir, new FsPermission(((short) (511))));
            fs = DFSTestUtil.login(fs, TestDFSPermission.conf, TestDFSPermission.USER1);
            fs.mkdirs(user1Dir);
            fs.setPermission(user1Dir, new FsPermission(((short) (493))));
            fs.setOwner(user1Dir, TestDFSPermission.USER1.getShortUserName(), TestDFSPermission.GROUP2_NAME);
            create(TestDFSPermission.OpType.CREATE, user1File);
            fs.setOwner(user1File, TestDFSPermission.USER1.getShortUserName(), TestDFSPermission.GROUP1_NAME);
            fs.setPermission(user1File, new FsPermission(((short) (384))));
            try {
                // login as user2, attempt to delete /BSS/user1
                // this should fail because user2 has no permission to
                // its sub directory.
                fs = DFSTestUtil.login(fs, TestDFSPermission.conf, TestDFSPermission.USER2);
                fs.delete(user1Dir, true);
                Assert.fail("User2 should not be allowed to delete user1's dir.");
            } catch (AccessControlException e) {
                e.printStackTrace();
                Assert.assertTrue("Permission denied messages must carry the username", e.getMessage().contains(TestDFSPermission.USER2_NAME));
            }
            // ensure the /BSS/user1 still exists
            Assert.assertTrue(fs.exists(user1Dir));
            try {
                fs = DFSTestUtil.login(fs, TestDFSPermission.conf, TestDFSPermission.SUPERUSER);
                Trash trash = new Trash(fs, TestDFSPermission.conf);
                Path trashRoot = trash.getCurrentTrashDir(user1Dir);
                while (true) {
                    trashRoot = trashRoot.getParent();
                    if (trashRoot.getParent().isRoot()) {
                        break;
                    }
                } 
                fs.mkdirs(trashRoot);
                fs.setPermission(trashRoot, new FsPermission(((short) (511))));
                // login as user2, attempt to move /BSS/user1 to trash
                // this should also fail otherwise the directory will be
                // removed by trash emptier (emptier is running by superuser)
                fs = DFSTestUtil.login(fs, TestDFSPermission.conf, TestDFSPermission.USER2);
                Trash userTrash = new Trash(fs, TestDFSPermission.conf);
                Assert.assertTrue(userTrash.isEnabled());
                userTrash.moveToTrash(user1Dir);
                Assert.fail(("User2 should not be allowed to move" + "user1's dir to trash"));
            } catch (IOException e) {
                // expect the exception is caused by permission denied
                Assert.assertTrue(((e.getCause()) instanceof AccessControlException));
                e.printStackTrace();
                Assert.assertTrue("Permission denied messages must carry the username", e.getCause().getMessage().contains(TestDFSPermission.USER2_NAME));
            }
            // ensure /BSS/user1 still exists
            Assert.assertEquals(fs.exists(user1Dir), true);
        } finally {
            fs = DFSTestUtil.login(fs, TestDFSPermission.conf, TestDFSPermission.SUPERUSER);
            fs.delete(rootDir, true);
            TestDFSPermission.conf.set(FS_TRASH_INTERVAL_KEY, "0");
        }
    }

    /* check if the ownership of a file/directory is set correctly */
    @Test
    public void testOwnership() throws Exception {
        testOwnership(TestDFSPermission.OpType.CREATE);// test file creation

        testOwnership(TestDFSPermission.OpType.MKDIRS);// test directory creation

    }

    private static final String ANCESTOR_NAME = "/ancestor";

    private static final String PARENT_NAME = "parent";

    private static final String FILE_NAME = "file";

    private static final String DIR_NAME = "dir";

    private static final String FILE_DIR_NAME = "filedir";

    enum OpType {

        CREATE,
        MKDIRS,
        OPEN,
        SET_REPLICATION,
        GET_FILEINFO,
        IS_DIR,
        EXISTS,
        GET_CONTENT_LENGTH,
        LIST,
        RENAME,
        DELETE;}

    /* Check if namenode performs permission checking correctly for
    superuser, file owner, group owner, and other users
     */
    @Test
    public void testPermissionChecking() throws Exception {
        try {
            fs = FileSystem.get(TestDFSPermission.conf);
            // set the permission of the root to be world-wide rwx
            fs.setPermission(new Path("/"), new FsPermission(((short) (511))));
            // create a directory hierarchy and sets random permission for each inode
            TestDFSPermission.PermissionGenerator ancestorPermissionGenerator = new TestDFSPermission.PermissionGenerator(TestDFSPermission.r);
            TestDFSPermission.PermissionGenerator dirPermissionGenerator = new TestDFSPermission.PermissionGenerator(TestDFSPermission.r);
            TestDFSPermission.PermissionGenerator filePermissionGenerator = new TestDFSPermission.PermissionGenerator(TestDFSPermission.r);
            short[] ancestorPermissions = new short[TestDFSPermission.NUM_TEST_PERMISSIONS];
            short[] parentPermissions = new short[TestDFSPermission.NUM_TEST_PERMISSIONS];
            short[] permissions = new short[TestDFSPermission.NUM_TEST_PERMISSIONS];
            Path[] ancestorPaths = new Path[TestDFSPermission.NUM_TEST_PERMISSIONS];
            Path[] parentPaths = new Path[TestDFSPermission.NUM_TEST_PERMISSIONS];
            Path[] filePaths = new Path[TestDFSPermission.NUM_TEST_PERMISSIONS];
            Path[] dirPaths = new Path[TestDFSPermission.NUM_TEST_PERMISSIONS];
            for (int i = 0; i < (TestDFSPermission.NUM_TEST_PERMISSIONS); i++) {
                // create ancestor directory
                ancestorPaths[i] = new Path(((TestDFSPermission.ANCESTOR_NAME) + i));
                create(TestDFSPermission.OpType.MKDIRS, ancestorPaths[i]);
                fs.setOwner(ancestorPaths[i], TestDFSPermission.USER1_NAME, TestDFSPermission.GROUP2_NAME);
                // create parent directory
                parentPaths[i] = new Path(ancestorPaths[i], ((TestDFSPermission.PARENT_NAME) + i));
                create(TestDFSPermission.OpType.MKDIRS, parentPaths[i]);
                // change parent directory's ownership to be user1
                fs.setOwner(parentPaths[i], TestDFSPermission.USER1_NAME, TestDFSPermission.GROUP2_NAME);
                filePaths[i] = new Path(parentPaths[i], ((TestDFSPermission.FILE_NAME) + i));
                dirPaths[i] = new Path(parentPaths[i], ((TestDFSPermission.DIR_NAME) + i));
                // makes sure that each inode at the same level
                // has a different permission
                ancestorPermissions[i] = ancestorPermissionGenerator.next();
                parentPermissions[i] = dirPermissionGenerator.next();
                permissions[i] = filePermissionGenerator.next();
                fs.setPermission(ancestorPaths[i], new FsPermission(ancestorPermissions[i]));
                fs.setPermission(parentPaths[i], new FsPermission(parentPermissions[i]));
            }
            /* file owner */
            testPermissionCheckingPerUser(TestDFSPermission.USER1, ancestorPermissions, parentPermissions, permissions, parentPaths, filePaths, dirPaths);
            /* group owner */
            testPermissionCheckingPerUser(TestDFSPermission.USER2, ancestorPermissions, parentPermissions, permissions, parentPaths, filePaths, dirPaths);
            /* other owner */
            testPermissionCheckingPerUser(TestDFSPermission.USER3, ancestorPermissions, parentPermissions, permissions, parentPaths, filePaths, dirPaths);
            /* super owner */
            testPermissionCheckingPerUser(TestDFSPermission.SUPERUSER, ancestorPermissions, parentPermissions, permissions, parentPaths, filePaths, dirPaths);
        } finally {
            fs.close();
        }
    }

    @Test
    public void testAccessOwner() throws IOException, InterruptedException {
        FileSystem rootFs = FileSystem.get(TestDFSPermission.conf);
        Path p1 = new Path("/p1");
        rootFs.mkdirs(p1);
        rootFs.setOwner(p1, TestDFSPermission.USER1_NAME, TestDFSPermission.GROUP1_NAME);
        fs = TestDFSPermission.USER1.doAs(new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws Exception {
                return FileSystem.get(TestDFSPermission.conf);
            }
        });
        fs.setPermission(p1, new FsPermission(((short) (292))));
        fs.access(p1, READ);
        try {
            fs.access(p1, WRITE);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            Assert.assertTrue("Permission denied messages must carry the username", e.getMessage().contains(TestDFSPermission.USER1_NAME));
            Assert.assertTrue("Permission denied messages must carry the path parent", e.getMessage().contains(p1.getParent().toUri().getPath()));
        }
        Path badPath = new Path("/bad/bad");
        try {
            fs.access(badPath, READ);
            Assert.fail("The access call should have failed");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testAccessGroupMember() throws IOException, InterruptedException {
        FileSystem rootFs = FileSystem.get(TestDFSPermission.conf);
        Path p2 = new Path("/p2");
        rootFs.mkdirs(p2);
        rootFs.setOwner(p2, UserGroupInformation.getCurrentUser().getShortUserName(), TestDFSPermission.GROUP1_NAME);
        rootFs.setPermission(p2, new FsPermission(((short) (480))));
        fs = TestDFSPermission.USER1.doAs(new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws Exception {
                return FileSystem.get(TestDFSPermission.conf);
            }
        });
        fs.access(p2, READ);
        try {
            fs.access(p2, EXECUTE);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            Assert.assertTrue("Permission denied messages must carry the username", e.getMessage().contains(TestDFSPermission.USER1_NAME));
            Assert.assertTrue("Permission denied messages must carry the path parent", e.getMessage().contains(p2.getParent().toUri().getPath()));
        }
    }

    @Test
    public void testAccessOthers() throws IOException, InterruptedException {
        FileSystem rootFs = FileSystem.get(TestDFSPermission.conf);
        Path p3 = new Path("/p3");
        rootFs.mkdirs(p3);
        rootFs.setPermission(p3, new FsPermission(((short) (508))));
        fs = TestDFSPermission.USER1.doAs(new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws Exception {
                return FileSystem.get(TestDFSPermission.conf);
            }
        });
        fs.access(p3, READ);
        try {
            fs.access(p3, READ_WRITE);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            Assert.assertTrue("Permission denied messages must carry the username", e.getMessage().contains(TestDFSPermission.USER1_NAME));
            Assert.assertTrue("Permission denied messages must carry the path parent", e.getMessage().contains(p3.getParent().toUri().getPath()));
        }
    }

    @Test
    public void testPermissionMessageOnNonDirAncestor() throws IOException, InterruptedException {
        FileSystem rootFs = FileSystem.get(TestDFSPermission.conf);
        Path p4 = new Path("/p4");
        rootFs.mkdirs(p4);
        rootFs.setOwner(p4, TestDFSPermission.USER1_NAME, TestDFSPermission.GROUP1_NAME);
        final Path fpath = new Path("/p4/file");
        DataOutputStream out = rootFs.create(fpath);
        out.writeBytes(("dhruba: " + fpath));
        out.close();
        rootFs.setOwner(fpath, TestDFSPermission.USER1_NAME, TestDFSPermission.GROUP1_NAME);
        Assert.assertTrue(rootFs.exists(fpath));
        fs = TestDFSPermission.USER1.doAs(new PrivilegedExceptionAction<FileSystem>() {
            @Override
            public FileSystem run() throws Exception {
                return FileSystem.get(TestDFSPermission.conf);
            }
        });
        final Path nfpath = new Path("/p4/file/nonexisting");
        Assert.assertFalse(rootFs.exists(nfpath));
        try {
            fs.exists(nfpath);
            Assert.fail("The exists call should have failed.");
        } catch (AccessControlException e) {
            Assert.assertTrue("Permission denied messages must carry file path", e.getMessage().contains(fpath.getName()));
            Assert.assertTrue(("Permission denied messages must specify existing_file is not " + "a directory, when checked on /existing_file/non_existing_name"), e.getMessage().contains("is not a directory"));
        }
        rootFs.setPermission(p4, new FsPermission("600"));
        try {
            fs.exists(nfpath);
            Assert.fail("The exists call should have failed.");
        } catch (AccessControlException e) {
            Assert.assertFalse((("Permission denied messages must not carry full file path," + "since the user does not have permission on /p4: ") + (e.getMessage())), e.getMessage().contains(fpath.getName()));
            Assert.assertFalse((("Permission denied messages must not specify /p4" + " is not a directory: ") + (e.getMessage())), e.getMessage().contains("is not a directory"));
        }
    }

    /* A random permission generator that guarantees that each permission
    value is generated only once.
     */
    static class PermissionGenerator {
        private final Random r;

        private final short[] permissions = new short[(TestDFSPermission.MAX_PERMISSION) + 1];

        private int numLeft = (TestDFSPermission.MAX_PERMISSION) + 1;

        PermissionGenerator(Random r) {
            this.r = r;
            for (int i = 0; i <= (TestDFSPermission.MAX_PERMISSION); i++) {
                permissions[i] = ((short) (i));
            }
        }

        short next() throws IOException {
            if ((numLeft) == 0) {
                throw new IOException("No more permission is avaialbe");
            }
            int index = r.nextInt(numLeft);// choose which permission to return

            (numLeft)--;// decrement the counter

            // swap the chosen permission with last available permission in the array
            short temp = permissions[numLeft];
            permissions[numLeft] = permissions[index];
            permissions[index] = temp;
            return permissions[numLeft];
        }
    }

    /* A base class that verifies the permission checking is correct 
    for an operation
     */
    abstract class PermissionVerifier {
        protected Path path;

        protected short ancestorPermission;

        protected short parentPermission;

        private short permission;

        protected short requiredAncestorPermission;

        protected short requiredParentPermission;

        protected short requiredPermission;

        protected static final short opAncestorPermission = TestDFSPermission.SEARCH_MASK;

        protected short opParentPermission;

        protected short opPermission;

        protected UserGroupInformation ugi;

        /* initialize */
        protected void set(Path path, short ancestorPermission, short parentPermission, short permission) {
            this.path = path;
            this.ancestorPermission = ancestorPermission;
            this.parentPermission = parentPermission;
            this.permission = permission;
            setOpPermission();
            this.ugi = null;
        }

        /* Perform an operation and verify if the permission checking is correct */
        void verifyPermission(UserGroupInformation ugi) throws IOException {
            if ((this.ugi) != ugi) {
                setRequiredPermissions(ugi);
                this.ugi = ugi;
            }
            try {
                try {
                    call();
                    Assert.assertFalse(expectPermissionDeny());
                } catch (AccessControlException e) {
                    Assert.assertTrue(expectPermissionDeny());
                }
            } catch (AssertionError ae) {
                logPermissions();
                throw ae;
            }
        }

        /**
         * Log the permissions and required permissions
         */
        protected void logPermissions() {
            TestDFSPermission.LOG.info(("required ancestor permission:" + (Integer.toOctalString(requiredAncestorPermission))));
            TestDFSPermission.LOG.info(("ancestor permission: " + (Integer.toOctalString(ancestorPermission))));
            TestDFSPermission.LOG.info(("required parent permission:" + (Integer.toOctalString(requiredParentPermission))));
            TestDFSPermission.LOG.info(("parent permission: " + (Integer.toOctalString(parentPermission))));
            TestDFSPermission.LOG.info(("required permission:" + (Integer.toOctalString(requiredPermission))));
            TestDFSPermission.LOG.info(("permission: " + (Integer.toOctalString(permission))));
        }

        /* Return true if an AccessControlException is expected */
        protected boolean expectPermissionDeny() {
            return ((((requiredPermission) & (permission)) != (requiredPermission)) || (((requiredParentPermission) & (parentPermission)) != (requiredParentPermission))) || (((requiredAncestorPermission) & (ancestorPermission)) != (requiredAncestorPermission));
        }

        /* Set the permissions required to pass the permission checking */
        protected void setRequiredPermissions(UserGroupInformation ugi) {
            if (TestDFSPermission.SUPERUSER.equals(ugi)) {
                requiredAncestorPermission = TestDFSPermission.SUPER_MASK;
                requiredParentPermission = TestDFSPermission.SUPER_MASK;
                requiredPermission = TestDFSPermission.SUPER_MASK;
            } else
                if (TestDFSPermission.USER1.equals(ugi)) {
                    requiredAncestorPermission = ((short) ((TestDFSPermission.PermissionVerifier.opAncestorPermission) & (TestDFSPermission.OWNER_MASK)));
                    requiredParentPermission = ((short) ((opParentPermission) & (TestDFSPermission.OWNER_MASK)));
                    requiredPermission = ((short) ((opPermission) & (TestDFSPermission.OWNER_MASK)));
                } else
                    if (TestDFSPermission.USER2.equals(ugi)) {
                        requiredAncestorPermission = ((short) ((TestDFSPermission.PermissionVerifier.opAncestorPermission) & (TestDFSPermission.GROUP_MASK)));
                        requiredParentPermission = ((short) ((opParentPermission) & (TestDFSPermission.GROUP_MASK)));
                        requiredPermission = ((short) ((opPermission) & (TestDFSPermission.GROUP_MASK)));
                    } else
                        if (TestDFSPermission.USER3.equals(ugi)) {
                            requiredAncestorPermission = ((short) ((TestDFSPermission.PermissionVerifier.opAncestorPermission) & (TestDFSPermission.OTHER_MASK)));
                            requiredParentPermission = ((short) ((opParentPermission) & (TestDFSPermission.OTHER_MASK)));
                            requiredPermission = ((short) ((opPermission) & (TestDFSPermission.OTHER_MASK)));
                        } else {
                            throw new IllegalArgumentException(("Non-supported user: " + ugi));
                        }



        }

        /* Set the rwx permissions required for the operation */
        abstract void setOpPermission();

        /* Perform the operation */
        abstract void call() throws IOException;
    }

    private static final short SUPER_MASK = 0;

    private static final short READ_MASK = 292;

    private static final short WRITE_MASK = 146;

    private static final short SEARCH_MASK = 73;

    private static final short NULL_MASK = 0;

    private static final short OWNER_MASK = 448;

    private static final short GROUP_MASK = 56;

    private static final short OTHER_MASK = 7;

    /* A class that verifies the permission checking is correct for create/mkdir */
    private class CreatePermissionVerifier extends TestDFSPermission.PermissionVerifier {
        private TestDFSPermission.OpType opType;

        private boolean cleanup = true;

        /* initialize */
        protected void set(Path path, TestDFSPermission.OpType opType, short ancestorPermission, short parentPermission) {
            super.set(path, ancestorPermission, parentPermission, TestDFSPermission.NULL_MASK);
            setOpType(opType);
        }

        void setCleanup(boolean cleanup) {
            this.cleanup = cleanup;
        }

        /* set if the operation mkdir/create */
        void setOpType(TestDFSPermission.OpType opType) {
            this.opType = opType;
        }

        @Override
        void setOpPermission() {
            this.opParentPermission = (TestDFSPermission.SEARCH_MASK) | (TestDFSPermission.WRITE_MASK);
        }

        @Override
        void call() throws IOException {
            create(opType, path);
            if (cleanup) {
                fs.delete(path, true);
            }
        }
    }

    private final TestDFSPermission.CreatePermissionVerifier createVerifier = new TestDFSPermission.CreatePermissionVerifier();

    /* A class that verifies the permission checking is correct for open */
    private class OpenPermissionVerifier extends TestDFSPermission.PermissionVerifier {
        @Override
        void setOpPermission() {
            this.opParentPermission = TestDFSPermission.SEARCH_MASK;
            this.opPermission = TestDFSPermission.READ_MASK;
        }

        @Override
        void call() throws IOException {
            FSDataInputStream in = fs.open(path);
            in.close();
        }
    }

    private final TestDFSPermission.OpenPermissionVerifier openVerifier = new TestDFSPermission.OpenPermissionVerifier();

    /* A class that verifies the permission checking is correct for 
    setReplication
     */
    private class SetReplicationPermissionVerifier extends TestDFSPermission.PermissionVerifier {
        @Override
        void setOpPermission() {
            this.opParentPermission = TestDFSPermission.SEARCH_MASK;
            this.opPermission = TestDFSPermission.WRITE_MASK;
        }

        @Override
        void call() throws IOException {
            fs.setReplication(path, ((short) (1)));
        }
    }

    private final TestDFSPermission.SetReplicationPermissionVerifier replicatorVerifier = new TestDFSPermission.SetReplicationPermissionVerifier();

    /* A class that verifies the permission checking is correct for 
    setTimes
     */
    private class SetTimesPermissionVerifier extends TestDFSPermission.PermissionVerifier {
        @Override
        void setOpPermission() {
            this.opParentPermission = TestDFSPermission.SEARCH_MASK;
            this.opPermission = TestDFSPermission.WRITE_MASK;
        }

        @Override
        void call() throws IOException {
            fs.setTimes(path, 100, 100);
            fs.setTimes(path, (-1), 100);
            fs.setTimes(path, 100, (-1));
        }
    }

    private final TestDFSPermission.SetTimesPermissionVerifier timesVerifier = new TestDFSPermission.SetTimesPermissionVerifier();

    /* A class that verifies the permission checking is correct for isDirectory,
    exist,  getFileInfo, getContentSummary
     */
    private class StatsPermissionVerifier extends TestDFSPermission.PermissionVerifier {
        TestDFSPermission.OpType opType;

        /* initialize */
        void set(Path path, TestDFSPermission.OpType opType, short ancestorPermission, short parentPermission) {
            super.set(path, ancestorPermission, parentPermission, TestDFSPermission.NULL_MASK);
            setOpType(opType);
        }

        /* set if operation is getFileInfo, isDirectory, exist, getContenSummary */
        void setOpType(TestDFSPermission.OpType opType) {
            this.opType = opType;
        }

        @Override
        void setOpPermission() {
            this.opParentPermission = TestDFSPermission.SEARCH_MASK;
        }

        @Override
        void call() throws IOException {
            switch (opType) {
                case GET_FILEINFO :
                    fs.getFileStatus(path);
                    break;
                case IS_DIR :
                    fs.isDirectory(path);
                    break;
                case EXISTS :
                    fs.exists(path);
                    break;
                case GET_CONTENT_LENGTH :
                    fs.getContentSummary(path).getLength();
                    break;
                default :
                    throw new IllegalArgumentException(("Unexpected operation type: " + (opType)));
            }
        }
    }

    private final TestDFSPermission.StatsPermissionVerifier statsVerifier = new TestDFSPermission.StatsPermissionVerifier();

    private enum InodeType {

        FILE,
        DIR;}

    /* A class that verifies the permission checking is correct for list */
    private class ListPermissionVerifier extends TestDFSPermission.PermissionVerifier {
        private TestDFSPermission.InodeType inodeType;

        /* initialize */
        void set(Path path, TestDFSPermission.InodeType inodeType, short ancestorPermission, short parentPermission, short permission) {
            this.inodeType = inodeType;
            super.set(path, ancestorPermission, parentPermission, permission);
        }

        /* set if the given path is a file/directory */
        void setInodeType(Path path, TestDFSPermission.InodeType inodeType) {
            this.path = path;
            this.inodeType = inodeType;
            setOpPermission();
            this.ugi = null;
        }

        @Override
        void setOpPermission() {
            this.opParentPermission = TestDFSPermission.SEARCH_MASK;
            switch (inodeType) {
                case FILE :
                    this.opPermission = 0;
                    break;
                case DIR :
                    this.opPermission = (TestDFSPermission.READ_MASK) | (TestDFSPermission.SEARCH_MASK);
                    break;
                default :
                    throw new IllegalArgumentException(("Illegal inode type: " + (inodeType)));
            }
        }

        @Override
        void call() throws IOException {
            fs.listStatus(path);
        }
    }

    final TestDFSPermission.ListPermissionVerifier listVerifier = new TestDFSPermission.ListPermissionVerifier();

    /* A class that verifies the permission checking is correct for rename */
    private class RenamePermissionVerifier extends TestDFSPermission.PermissionVerifier {
        private Path dst;

        private short dstAncestorPermission;

        private short dstParentPermission;

        /* initialize */
        void set(Path src, short srcAncestorPermission, short srcParentPermission, Path dst, short dstAncestorPermission, short dstParentPermission) {
            super.set(src, srcAncestorPermission, srcParentPermission, TestDFSPermission.NULL_MASK);
            this.dst = dst;
            this.dstAncestorPermission = dstAncestorPermission;
            this.dstParentPermission = dstParentPermission;
        }

        @Override
        void setOpPermission() {
            opParentPermission = (TestDFSPermission.SEARCH_MASK) | (TestDFSPermission.WRITE_MASK);
        }

        @Override
        void call() throws IOException {
            fs.rename(path, dst);
        }

        @Override
        protected boolean expectPermissionDeny() {
            return ((super.expectPermissionDeny()) || (((requiredParentPermission) & (dstParentPermission)) != (requiredParentPermission))) || (((requiredAncestorPermission) & (dstAncestorPermission)) != (requiredAncestorPermission));
        }

        @Override
        protected void logPermissions() {
            super.logPermissions();
            TestDFSPermission.LOG.info(("dst ancestor permission: " + (Integer.toOctalString(dstAncestorPermission))));
            TestDFSPermission.LOG.info(("dst parent permission: " + (Integer.toOctalString(dstParentPermission))));
        }
    }

    final TestDFSPermission.RenamePermissionVerifier renameVerifier = new TestDFSPermission.RenamePermissionVerifier();

    /* A class that verifies the permission checking is correct for delete */
    private class DeletePermissionVerifier extends TestDFSPermission.PermissionVerifier {
        void set(Path path, short ancestorPermission, short parentPermission) {
            super.set(path, ancestorPermission, parentPermission, TestDFSPermission.NULL_MASK);
        }

        @Override
        void setOpPermission() {
            this.opParentPermission = (TestDFSPermission.SEARCH_MASK) | (TestDFSPermission.WRITE_MASK);
        }

        @Override
        void call() throws IOException {
            fs.delete(path, true);
        }
    }

    /* A class that verifies the permission checking is correct for
    directory deletion
     */
    private class DeleteDirPermissionVerifier extends TestDFSPermission.DeletePermissionVerifier {
        private short[] childPermissions;

        /* initialize */
        void set(Path path, short ancestorPermission, short parentPermission, short permission, short[] childPermissions) {
            set(path, ancestorPermission, parentPermission, permission);
            this.childPermissions = childPermissions;
        }

        @Override
        void setOpPermission() {
            this.opParentPermission = (TestDFSPermission.SEARCH_MASK) | (TestDFSPermission.WRITE_MASK);
            this.opPermission = ((TestDFSPermission.SEARCH_MASK) | (TestDFSPermission.WRITE_MASK)) | (TestDFSPermission.READ_MASK);
        }

        @Override
        protected boolean expectPermissionDeny() {
            if (super.expectPermissionDeny()) {
                return true;
            } else {
                if ((childPermissions) != null) {
                    for (short childPermission : childPermissions) {
                        if (((requiredPermission) & childPermission) != (requiredPermission)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }

    /* A class that verifies the permission checking is correct for
    empty-directory deletion
     */
    private class DeleteEmptyDirPermissionVerifier extends TestDFSPermission.DeleteDirPermissionVerifier {
        @Override
        void setOpPermission() {
            this.opParentPermission = (TestDFSPermission.SEARCH_MASK) | (TestDFSPermission.WRITE_MASK);
            this.opPermission = TestDFSPermission.NULL_MASK;
        }
    }

    final TestDFSPermission.DeletePermissionVerifier fileDeletionVerifier = new TestDFSPermission.DeletePermissionVerifier();

    final TestDFSPermission.DeleteDirPermissionVerifier dirDeletionVerifier = new TestDFSPermission.DeleteDirPermissionVerifier();

    final TestDFSPermission.DeleteEmptyDirPermissionVerifier emptyDirDeletionVerifier = new TestDFSPermission.DeleteEmptyDirPermissionVerifier();
}

