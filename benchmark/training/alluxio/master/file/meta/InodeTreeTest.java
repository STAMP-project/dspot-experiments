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
package alluxio.master.file.meta;


import Constants.KB;
import ExceptionMessage.FILE_ALREADY_EXISTS;
import ExceptionMessage.INODE_DOES_NOT_EXIST;
import LockPattern.READ;
import LockPattern.WRITE_INODE;
import NoopJournalContext.INSTANCE;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_ENABLED;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP;
import RpcContext.NOOP;
import alluxio.AlluxioURI;
import alluxio.ConfigurationRule;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.BlockInfoException;
import alluxio.exception.FileAlreadyExistsException;
import alluxio.exception.FileDoesNotExistException;
import alluxio.exception.InvalidPathException;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.grpc.CreateFilePOptions;
import alluxio.master.MasterRegistry;
import alluxio.master.file.contexts.CreateDirectoryContext;
import alluxio.master.file.contexts.CreateFileContext;
import alluxio.master.metastore.InodeStore;
import alluxio.master.metrics.MetricsMaster;
import alluxio.security.authorization.Mode;
import alluxio.util.CommonUtils;
import alluxio.util.StreamUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link InodeTree}.
 */
public final class InodeTreeTest {
    private static final String TEST_PATH = "test";

    private static final AlluxioURI TEST_URI = new AlluxioURI("/test");

    private static final AlluxioURI NESTED_URI = new AlluxioURI("/nested/test");

    private static final AlluxioURI NESTED_DIR_URI = new AlluxioURI("/nested/test/dir");

    private static final AlluxioURI NESTED_DIR_FILE_URI = new AlluxioURI("/nested/test/dir/file1");

    private static final AlluxioURI NESTED_FILE_URI = new AlluxioURI("/nested/test/file");

    private static final AlluxioURI NESTED_MULTIDIR_FILE_URI = new AlluxioURI("/nested/test/dira/dirb/file");

    public static final String TEST_OWNER = "user1";

    public static final String TEST_GROUP = "group1";

    public static final Mode TEST_DIR_MODE = new Mode(((short) (493)));

    public static final Mode TEST_FILE_MODE = new Mode(((short) (420)));

    private static CreateFileContext sFileContext;

    private static CreateDirectoryContext sDirectoryContext;

    private static CreateFileContext sNestedFileContext;

    private static CreateDirectoryContext sNestedDirectoryContext;

    private InodeStore mInodeStore;

    private InodeTree mTree;

    private MasterRegistry mRegistry;

    private MetricsMaster mMetricsMaster;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(new ImmutableMap.Builder<alluxio.conf.PropertyKey, String>().put(SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true").put(SECURITY_AUTHORIZATION_PERMISSION_SUPERGROUP, "test-supergroup").build(), ServerConfiguration.global());

    /**
     * Tests that initializing the root twice results in the same root.
     */
    @Test
    public void initializeRootTwice() throws Exception {
        MutableInode<?> root = getInodeByPath(new AlluxioURI("/"));
        // initializeRoot call does nothing
        mTree.initializeRoot(InodeTreeTest.TEST_OWNER, InodeTreeTest.TEST_GROUP, InodeTreeTest.TEST_DIR_MODE, INSTANCE);
        Assert.assertEquals(InodeTreeTest.TEST_OWNER, root.getOwner());
        MutableInode<?> newRoot = getInodeByPath(new AlluxioURI("/"));
        Assert.assertEquals(root, newRoot);
    }

    /**
     * Tests the {@link InodeTree#createPath(RpcContext, LockedInodePath, CreatePathContext)}
     * method for creating directories.
     */
    @Test
    public void createDirectory() throws Exception {
        // create directory
        createPath(mTree, InodeTreeTest.TEST_URI, InodeTreeTest.sDirectoryContext);
        Assert.assertTrue(mTree.inodePathExists(InodeTreeTest.TEST_URI));
        MutableInode<?> test = getInodeByPath(InodeTreeTest.TEST_URI);
        Assert.assertEquals(InodeTreeTest.TEST_PATH, test.getName());
        Assert.assertTrue(test.isDirectory());
        Assert.assertEquals("user1", test.getOwner());
        Assert.assertEquals("group1", test.getGroup());
        Assert.assertEquals(InodeTreeTest.TEST_DIR_MODE.toShort(), test.getMode());
        // create nested directory
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        Assert.assertTrue(mTree.inodePathExists(InodeTreeTest.NESTED_URI));
        MutableInode<?> nested = getInodeByPath(InodeTreeTest.NESTED_URI);
        Assert.assertEquals(InodeTreeTest.TEST_PATH, nested.getName());
        Assert.assertEquals(2, nested.getParentId());
        Assert.assertTrue(test.isDirectory());
        Assert.assertEquals("user1", test.getOwner());
        Assert.assertEquals("group1", test.getGroup());
        Assert.assertEquals(InodeTreeTest.TEST_DIR_MODE.toShort(), test.getMode());
    }

    /**
     * Tests that an exception is thrown when trying to create an already existing directory with the
     * {@code allowExists} flag set to {@code false}.
     */
    @Test
    public void createExistingDirectory() throws Exception {
        // create directory
        createPath(mTree, InodeTreeTest.TEST_URI, InodeTreeTest.sDirectoryContext);
        // create again with allowExists true
        createPath(mTree, InodeTreeTest.TEST_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setAllowExists(true)));
        // create again with allowExists false
        mThrown.expect(FileAlreadyExistsException.class);
        mThrown.expectMessage(FILE_ALREADY_EXISTS.getMessage(InodeTreeTest.TEST_URI));
        createPath(mTree, InodeTreeTest.TEST_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setAllowExists(false)));
    }

    /**
     * Tests that creating a file under a pinned directory works.
     */
    @Test
    public void createFileUnderPinnedDirectory() throws Exception {
        // create nested directory
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        // pin nested folder
        try (LockedInodePath inodePath = mTree.lockFullInodePath(InodeTreeTest.NESTED_URI, WRITE_INODE)) {
            mTree.setPinned(NOOP, inodePath, true, 0);
        }
        // create nested file under pinned folder
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        // the nested file is pinned
        Assert.assertEquals(1, mTree.getPinIdSet().size());
    }

    /**
     * Tests the {@link InodeTree#createPath(RpcContext, LockedInodePath, CreatePathContext)}
     * method for creating a file.
     */
    @Test
    public void createFile() throws Exception {
        // created nested file
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        MutableInode<?> nestedFile = getInodeByPath(InodeTreeTest.NESTED_FILE_URI);
        Assert.assertEquals("file", nestedFile.getName());
        Assert.assertEquals(2, nestedFile.getParentId());
        Assert.assertTrue(nestedFile.isFile());
        Assert.assertEquals("user1", nestedFile.getOwner());
        Assert.assertEquals("group1", nestedFile.getGroup());
        Assert.assertEquals(InodeTreeTest.TEST_FILE_MODE.toShort(), nestedFile.getMode());
    }

    /**
     * Tests the {@link InodeTree#createPath(RpcContext, LockedInodePath, CreatePathContext)}
     * method.
     */
    @Test
    public void createPathTest() throws Exception {
        // save the last mod time of the root
        long lastModTime = mTree.getRoot().getLastModificationTimeMs();
        // sleep to ensure a different last modification time
        CommonUtils.sleepMs(10);
        // Need to use updated options to set the correct last mod time.
        CreateDirectoryContext dirContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true).setMode(InodeTreeTest.TEST_DIR_MODE.toProto())).setOwner(InodeTreeTest.TEST_OWNER).setGroup(InodeTreeTest.TEST_GROUP);
        // create nested directory
        List<Inode> created = createPath(mTree, InodeTreeTest.NESTED_URI, dirContext);
        // 1 modified directory
        Assert.assertNotEquals(lastModTime, getInodeByPath(InodeTreeTest.NESTED_URI.getParent()).getLastModificationTimeMs());
        // 2 created directories
        Assert.assertEquals(2, created.size());
        Assert.assertEquals("nested", created.get(0).getName());
        Assert.assertEquals("test", created.get(1).getName());
        // save the last mod time of 'test'
        lastModTime = created.get(1).getLastModificationTimeMs();
        // sleep to ensure a different last modification time
        CommonUtils.sleepMs(10);
        // creating the directory path again results in no new inodes.
        try {
            createPath(mTree, InodeTreeTest.NESTED_URI, dirContext);
            Assert.assertTrue("createPath should throw FileAlreadyExistsException", false);
        } catch (FileAlreadyExistsException e) {
            Assert.assertEquals(e.getMessage(), FILE_ALREADY_EXISTS.getMessage(InodeTreeTest.NESTED_URI));
        }
        // create a file
        CreateFileContext options = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB).setRecursive(true));
        created = createPath(mTree, InodeTreeTest.NESTED_FILE_URI, options);
        // test directory was modified
        Assert.assertNotEquals(lastModTime, getInodeByPath(InodeTreeTest.NESTED_URI).getLastModificationTimeMs());
        // file was created
        Assert.assertEquals(1, created.size());
        Assert.assertEquals("file", created.get(0).getName());
    }

    /**
     * Tests the {@link InodeTree#createPath(RpcContext, LockedInodePath, CreatePathContext)} method
     * for inheriting owner and group when empty.
     */
    @Test
    public void createPathInheritanceTest() throws Exception {
        // create nested directory
        CreateDirectoryContext dirContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true).setMode(InodeTreeTest.TEST_DIR_MODE.toProto())).setOwner(InodeTreeTest.TEST_OWNER).setGroup(InodeTreeTest.TEST_GROUP);
        List<Inode> created = createPath(mTree, InodeTreeTest.NESTED_URI, dirContext);
        Assert.assertEquals(2, created.size());
        // 1. create a nested directory with empty owner and group
        CreateDirectoryContext nestedDirContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true).setMode(InodeTreeTest.TEST_DIR_MODE.toProto())).setOwner("").setGroup("");
        created = createPath(mTree, InodeTreeTest.NESTED_DIR_URI, nestedDirContext);
        Assert.assertEquals(1, created.size());
        Assert.assertEquals("dir", created.get(0).getName());
        Assert.assertEquals(InodeTreeTest.TEST_OWNER, created.get(0).getOwner());
        Assert.assertEquals(InodeTreeTest.TEST_GROUP, created.get(0).getGroup());
        // 2. create a file with empty owner and group
        CreateFileContext nestedDirFileContext = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB).setRecursive(true)).setOwner("").setGroup("");
        created = createPath(mTree, InodeTreeTest.NESTED_DIR_FILE_URI, nestedDirFileContext);
        Assert.assertEquals(1, created.size());
        Assert.assertEquals("file1", created.get(0).getName());
        Assert.assertEquals(InodeTreeTest.TEST_OWNER, created.get(0).getOwner());
        Assert.assertEquals(InodeTreeTest.TEST_GROUP, created.get(0).getGroup());
    }

    /**
     * Tests that an exception is thrown when trying to create the root path twice.
     */
    @Test
    public void createRootPath() throws Exception {
        mThrown.expect(FileAlreadyExistsException.class);
        mThrown.expectMessage("/");
        createPath(mTree, new AlluxioURI("/"), InodeTreeTest.sFileContext);
    }

    /**
     * Tests that an exception is thrown when trying to create a file with invalid block size.
     */
    @Test
    public void createFileWithInvalidBlockSize() throws Exception {
        mThrown.expect(BlockInfoException.class);
        mThrown.expectMessage("Invalid block size 0");
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(0));
        createPath(mTree, InodeTreeTest.TEST_URI, context);
    }

    /**
     * Tests that an exception is thrown when trying to create a file with a negative block size.
     */
    @Test
    public void createFileWithNegativeBlockSize() throws Exception {
        mThrown.expect(BlockInfoException.class);
        mThrown.expectMessage("Invalid block size -1");
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes((-1)));
        createPath(mTree, InodeTreeTest.TEST_URI, context);
    }

    /**
     * Tests that an exception is thrown when trying to create a file under a non-existing directory.
     */
    @Test
    public void createFileUnderNonexistingDir() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mThrown.expectMessage("File /nested/test creation failed. Component 1(nested) does not exist");
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sFileContext);
    }

    /**
     * Tests that an exception is thrown when trying to create a file twice.
     */
    @Test
    public void createFileTwice() throws Exception {
        mThrown.expect(FileAlreadyExistsException.class);
        mThrown.expectMessage("/nested/test");
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedFileContext);
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedFileContext);
    }

    /**
     * Tests that an exception is thrown when trying to create a file under a file path.
     */
    @Test
    public void createFileUnderFile() throws Exception {
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedFileContext);
        mThrown.expect(InvalidPathException.class);
        mThrown.expectMessage(("Traversal failed for path /nested/test/test. " + "Component 2(test) is a file, not a directory"));
        createPath(mTree, new AlluxioURI("/nested/test/test"), InodeTreeTest.sNestedFileContext);
    }

    /**
     * Tests {@link InodeTree#inodeIdExists(long)}.
     */
    @Test
    public void inodeIdExists() throws Exception {
        Assert.assertTrue(mTree.inodeIdExists(0));
        Assert.assertFalse(mTree.inodeIdExists(1));
        createPath(mTree, InodeTreeTest.TEST_URI, InodeTreeTest.sFileContext);
        MutableInode<?> inode = getInodeByPath(InodeTreeTest.TEST_URI);
        Assert.assertTrue(mTree.inodeIdExists(inode.getId()));
        InodeTreeTest.deleteInodeByPath(mTree, InodeTreeTest.TEST_URI);
        Assert.assertFalse(mTree.inodeIdExists(inode.getId()));
    }

    /**
     * Tests {@link InodeTree#inodePathExists(AlluxioURI)}.
     */
    @Test
    public void inodePathExists() throws Exception {
        Assert.assertFalse(mTree.inodePathExists(InodeTreeTest.TEST_URI));
        createPath(mTree, InodeTreeTest.TEST_URI, InodeTreeTest.sFileContext);
        Assert.assertTrue(mTree.inodePathExists(InodeTreeTest.TEST_URI));
        InodeTreeTest.deleteInodeByPath(mTree, InodeTreeTest.TEST_URI);
        Assert.assertFalse(mTree.inodePathExists(InodeTreeTest.TEST_URI));
    }

    /**
     * Tests that an exception is thrown when trying to get an Inode by a non-existing path.
     */
    @Test
    public void getInodeByNonexistingPath() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mThrown.expectMessage("Path \"/test\" does not exist");
        Assert.assertFalse(mTree.inodePathExists(InodeTreeTest.TEST_URI));
        getInodeByPath(InodeTreeTest.TEST_URI);
    }

    /**
     * Tests that an exception is thrown when trying to get an Inode by a non-existing, nested
     * path.
     */
    @Test
    public void getInodeByNonexistingNestedPath() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mThrown.expectMessage("Path \"/nested/test/file\" does not exist");
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        Assert.assertFalse(mTree.inodePathExists(InodeTreeTest.NESTED_FILE_URI));
        getInodeByPath(InodeTreeTest.NESTED_FILE_URI);
    }

    /**
     * Tests that an exception is thrown when trying to get an Inode with an invalid id.
     */
    @Test
    public void getInodeByInvalidId() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mThrown.expectMessage(INODE_DOES_NOT_EXIST.getMessage(1));
        Assert.assertFalse(mTree.inodeIdExists(1));
        try (LockedInodePath inodePath = mTree.lockFullInodePath(1, READ)) {
            // inode exists
        }
    }

    /**
     * Tests the {@link InodeTree#isRootId(long)} method.
     */
    @Test
    public void isRootId() {
        Assert.assertTrue(mTree.isRootId(0));
        Assert.assertFalse(mTree.isRootId(1));
    }

    /**
     * Tests the {@link InodeTree#getPath(InodeView)} method.
     */
    @Test
    public void getPath() throws Exception {
        try (LockedInodePath inodePath = mTree.lockFullInodePath(0, READ)) {
            Inode root = inodePath.getInode();
            // test root path
            Assert.assertEquals(new AlluxioURI("/"), mTree.getPath(root));
        }
        // test one level
        createPath(mTree, InodeTreeTest.TEST_URI, InodeTreeTest.sDirectoryContext);
        try (LockedInodePath inodePath = mTree.lockFullInodePath(InodeTreeTest.TEST_URI, READ)) {
            Assert.assertEquals(new AlluxioURI("/test"), mTree.getPath(inodePath.getInode()));
        }
        // test nesting
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        try (LockedInodePath inodePath = mTree.lockFullInodePath(InodeTreeTest.NESTED_URI, READ)) {
            Assert.assertEquals(new AlluxioURI("/nested/test"), mTree.getPath(inodePath.getInode()));
        }
    }

    @Test
    public void getInodeChildrenRecursive() throws Exception {
        createPath(mTree, InodeTreeTest.TEST_URI, InodeTreeTest.sDirectoryContext);
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        // add nested file
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        // all inodes under root
        try (LockedInodePath inodePath = mTree.lockFullInodePath(0, WRITE_INODE)) {
            // /test, /nested, /nested/test, /nested/test/file
            Assert.assertEquals(4, mTree.getImplicitlyLockedDescendants(inodePath).size());
        }
    }

    /**
     * Tests deleting a nested inode.
     */
    @Test
    public void deleteInode() throws Exception {
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        // all inodes under root
        try (LockedInodePath inodePath = mTree.lockFullInodePath(0, WRITE_INODE)) {
            // /nested, /nested/test
            Assert.assertEquals(2, mTree.getImplicitlyLockedDescendants(inodePath).size());
            // delete the nested inode
            InodeTreeTest.deleteInodeByPath(mTree, InodeTreeTest.NESTED_URI);
            // only /nested left
            Assert.assertEquals(1, mTree.getImplicitlyLockedDescendants(inodePath).size());
        }
    }

    @Test
    public void setPinned() throws Exception {
        createPath(mTree, InodeTreeTest.NESTED_URI, InodeTreeTest.sNestedDirectoryContext);
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        // no inodes pinned
        Assert.assertEquals(0, mTree.getPinIdSet().size());
        // pin nested folder
        try (LockedInodePath inodePath = mTree.lockFullInodePath(InodeTreeTest.NESTED_URI, WRITE_INODE)) {
            mTree.setPinned(NOOP, inodePath, true, 0);
        }
        // nested file pinned
        Assert.assertEquals(1, mTree.getPinIdSet().size());
        // unpin nested folder
        try (LockedInodePath inodePath = mTree.lockFullInodePath(InodeTreeTest.NESTED_URI, WRITE_INODE)) {
            mTree.setPinned(NOOP, inodePath, false, 0);
        }
        Assert.assertEquals(0, mTree.getPinIdSet().size());
    }

    /**
     * Tests that streaming to a journal checkpoint works.
     */
    @Test
    public void streamToJournalCheckpoint() throws Exception {
        InodeTreeTest.verifyJournal(mTree, Arrays.asList(getInodeByPath("/")));
        // test nested URI
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        InodeTreeTest.verifyJournal(mTree, StreamUtils.map(( path) -> getInodeByPath(path), Arrays.asList("/", "/nested", "/nested/test", "/nested/test/file")));
        // add a sibling of test and verify journaling is in correct order (breadth first)
        createPath(mTree, new AlluxioURI("/nested/test1/file1"), InodeTreeTest.sNestedFileContext);
        InodeTreeTest.verifyJournal(mTree, StreamUtils.map(( path) -> getInodeByPath(path), Arrays.asList("/", "/nested", "/nested/test", "/nested/test1", "/nested/test/file", "/nested/test1/file1")));
    }

    @Test
    public void addInodeFromJournal() throws Exception {
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        createPath(mTree, new AlluxioURI("/nested/test1/file1"), InodeTreeTest.sNestedFileContext);
        MutableInode<?> root = getInodeByPath("/");
        MutableInode<?> nested = getInodeByPath("/nested");
        MutableInode<?> test = getInodeByPath("/nested/test");
        MutableInode<?> file = getInodeByPath("/nested/test/file");
        MutableInode<?> test1 = getInodeByPath("/nested/test1");
        MutableInode<?> file1 = getInodeByPath("/nested/test1/file1");
        // reset the tree
        mTree.processJournalEntry(root.toJournalEntry());
        // re-init the root since the tree was reset above
        mTree.getRoot();
        try (LockedInodePath inodePath = mTree.lockFullInodePath(new AlluxioURI("/"), WRITE_INODE)) {
            Assert.assertEquals(0, mTree.getImplicitlyLockedDescendants(inodePath).size());
            mTree.processJournalEntry(nested.toJournalEntry());
            InodeTreeTest.verifyChildrenNames(mTree, inodePath, Sets.newHashSet("nested"));
            mTree.processJournalEntry(test.toJournalEntry());
            InodeTreeTest.verifyChildrenNames(mTree, inodePath, Sets.newHashSet("nested", "test"));
            mTree.processJournalEntry(test1.toJournalEntry());
            InodeTreeTest.verifyChildrenNames(mTree, inodePath, Sets.newHashSet("nested", "test", "test1"));
            mTree.processJournalEntry(file.toJournalEntry());
            InodeTreeTest.verifyChildrenNames(mTree, inodePath, Sets.newHashSet("nested", "test", "test1", "file"));
            mTree.processJournalEntry(file1.toJournalEntry());
            InodeTreeTest.verifyChildrenNames(mTree, inodePath, Sets.newHashSet("nested", "test", "test1", "file", "file1"));
        }
    }

    @Test
    public void addInodeModeFromJournalWithEmptyOwnership() throws Exception {
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        MutableInode<?> root = getInodeByPath("/");
        MutableInode<?> nested = getInodeByPath("/nested");
        MutableInode<?> test = getInodeByPath("/nested/test");
        MutableInode<?> file = getInodeByPath("/nested/test/file");
        MutableInode<?>[] inodeChildren = new MutableInode<?>[]{ nested, test, file };
        for (MutableInode<?> child : inodeChildren) {
            child.setOwner("");
            child.setGroup("");
            child.setMode(((short) (384)));
        }
        // reset the tree
        mTree.processJournalEntry(root.toJournalEntry());
        // re-init the root since the tree was reset above
        mTree.getRoot();
        try (LockedInodePath inodePath = mTree.lockFullInodePath(new AlluxioURI("/"), WRITE_INODE)) {
            Assert.assertEquals(0, mTree.getImplicitlyLockedDescendants(inodePath).size());
            mTree.processJournalEntry(nested.toJournalEntry());
            mTree.processJournalEntry(test.toJournalEntry());
            mTree.processJournalEntry(file.toJournalEntry());
            List<LockedInodePath> descendants = mTree.getImplicitlyLockedDescendants(inodePath);
            Assert.assertEquals(inodeChildren.length, descendants.size());
            for (LockedInodePath childPath : descendants) {
                Inode child = childPath.getInodeOrNull();
                Assert.assertNotNull(child);
                Assert.assertEquals("", child.getOwner());
                Assert.assertEquals("", child.getGroup());
                Assert.assertEquals(((short) (384)), child.getMode());
            }
        }
    }

    @Test
    public void getInodePathById() throws Exception {
        try (LockedInodePath rootPath = mTree.lockFullInodePath(0, READ)) {
            Assert.assertEquals(0, rootPath.getInode().getId());
        }
        List<Inode> created = createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        for (Inode inode : created) {
            long id = inode.getId();
            try (LockedInodePath inodePath = mTree.lockFullInodePath(id, READ)) {
                Assert.assertEquals(id, inodePath.getInode().getId());
            }
        }
    }

    @Test
    public void getInodePathByPath() throws Exception {
        try (LockedInodePath rootPath = mTree.lockFullInodePath(new AlluxioURI("/"), READ)) {
            Assert.assertTrue(mTree.isRootId(rootPath.getInode().getId()));
        }
        // Create a nested file.
        createPath(mTree, InodeTreeTest.NESTED_FILE_URI, InodeTreeTest.sNestedFileContext);
        AlluxioURI uri = new AlluxioURI("/nested");
        try (LockedInodePath inodePath = mTree.lockFullInodePath(uri, READ)) {
            Assert.assertEquals(uri.getName(), inodePath.getInode().getName());
        }
        uri = InodeTreeTest.NESTED_URI;
        try (LockedInodePath inodePath = mTree.lockFullInodePath(uri, READ)) {
            Assert.assertEquals(uri.getName(), inodePath.getInode().getName());
        }
        uri = InodeTreeTest.NESTED_FILE_URI;
        try (LockedInodePath inodePath = mTree.lockFullInodePath(uri, READ)) {
            Assert.assertEquals(uri.getName(), inodePath.getInode().getName());
        }
    }
}

