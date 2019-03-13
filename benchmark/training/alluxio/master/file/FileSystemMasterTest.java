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


import CommandType.Nothing;
import Constants.HOUR_MS;
import Constants.KB;
import Constants.NO_TTL;
import ExceptionMessage.CANNOT_FREE_NON_EMPTY_DIR;
import ExceptionMessage.CANNOT_FREE_NON_PERSISTED_FILE;
import ExceptionMessage.CANNOT_FREE_PINNED_FILE;
import ExceptionMessage.DELETE_FAILED_DIR_CHILDREN;
import ExceptionMessage.DELETE_NONEMPTY_DIRECTORY_NONRECURSIVE;
import ExceptionMessage.DELETE_ROOT_DIRECTORY;
import ExceptionMessage.FILE_ALREADY_EXISTS;
import ExceptionMessage.PATH_DOES_NOT_EXIST;
import ExceptionMessage.PERMISSION_DENIED;
import ExceptionMessage.RENAME_CANNOT_BE_TO_ROOT;
import ExceptionMessage.ROOT_CANNOT_BE_RENAMED;
import HeartbeatContext.MASTER_LOST_FILES_DETECTION;
import HeartbeatContext.MASTER_TTL_CHECK;
import IdUtils.INVALID_FILE_ID;
import LoadDescendantPType.ONE;
import LoadMetadataPType.ALWAYS;
import LoadMetadataPType.NEVER;
import LoadMetadataPType.ONCE;
import PersistenceState.LOST;
import PersistenceState.NOT_PERSISTED;
import PersistenceState.PERSISTED;
import PropertyKey.MASTER_JOURNAL_TAILER_SHUTDOWN_QUIET_WAIT_TIME_MS;
import PropertyKey.MASTER_JOURNAL_TAILER_SLEEP_TIME_MS;
import PropertyKey.MASTER_JOURNAL_TYPE;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_UMASK;
import PropertyKey.WORK_DIR;
import SetAclAction.MODIFY;
import SetAclAction.REMOVE;
import SetAclAction.REMOVE_ALL;
import SetAclAction.REMOVE_DEFAULT;
import SetAclAction.REPLACE;
import alluxio.AlluxioTestDirectory;
import alluxio.AlluxioURI;
import alluxio.AuthenticatedClientUserResource;
import alluxio.AuthenticatedUserRule;
import alluxio.ConfigurationRule;
import alluxio.LoginUserRule;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.AccessControlException;
import alluxio.exception.BlockInfoException;
import alluxio.exception.DirectoryNotEmptyException;
import alluxio.exception.FileAlreadyExistsException;
import alluxio.exception.FileDoesNotExistException;
import alluxio.exception.InvalidPathException;
import alluxio.exception.UnexpectedAlluxioException;
import alluxio.grpc.Command;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.DeletePOptions;
import alluxio.grpc.FileSystemMasterCommonPOptions;
import alluxio.grpc.FreePOptions;
import alluxio.grpc.GetStatusPOptions;
import alluxio.grpc.ListStatusPOptions;
import alluxio.grpc.LoadMetadataPOptions;
import alluxio.grpc.MountPOptions;
import alluxio.grpc.SetAclPOptions;
import alluxio.grpc.SetAttributePOptions;
import alluxio.grpc.TtlAction.FREE;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.master.MasterRegistry;
import alluxio.master.block.BlockMaster;
import alluxio.master.file.contexts.CompleteFileContext;
import alluxio.master.file.contexts.CreateDirectoryContext;
import alluxio.master.file.contexts.CreateFileContext;
import alluxio.master.file.contexts.DeleteContext;
import alluxio.master.file.contexts.FreeContext;
import alluxio.master.file.contexts.GetStatusContext;
import alluxio.master.file.contexts.ListStatusContext;
import alluxio.master.file.contexts.LoadMetadataContext;
import alluxio.master.file.contexts.MountContext;
import alluxio.master.file.contexts.RenameContext;
import alluxio.master.file.contexts.SetAclContext;
import alluxio.master.file.contexts.SetAttributeContext;
import alluxio.master.file.contexts.WorkerHeartbeatContext;
import alluxio.master.file.meta.TtlIntervalRule;
import alluxio.master.journal.JournalSystem;
import alluxio.master.metastore.ReadOnlyInodeStore;
import alluxio.master.metrics.MetricsMaster;
import alluxio.metrics.Metric;
import alluxio.security.authorization.AclEntry;
import alluxio.security.authorization.Mode;
import alluxio.util.FileSystemOptions;
import alluxio.util.io.FileUtils;
import alluxio.wire.CommandType.Persist;
import alluxio.wire.FileBlockInfo;
import alluxio.wire.FileInfo;
import alluxio.wire.FileSystemCommand;
import alluxio.wire.UfsInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for {@link FileSystemMaster}.
 */
public final class FileSystemMasterTest {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemMasterTest.class);

    private static final AlluxioURI NESTED_URI = new AlluxioURI("/nested/test");

    private static final AlluxioURI NESTED_FILE_URI = new AlluxioURI("/nested/test/file");

    private static final AlluxioURI NESTED_FILE2_URI = new AlluxioURI("/nested/test/file2");

    private static final AlluxioURI NESTED_DIR_URI = new AlluxioURI("/nested/test/dir");

    private static final AlluxioURI ROOT_URI = new AlluxioURI("/");

    private static final AlluxioURI ROOT_FILE_URI = new AlluxioURI("/file");

    private static final AlluxioURI TEST_URI = new AlluxioURI("/test");

    private static final String TEST_USER = "test";

    private static final GetStatusContext GET_STATUS_CONTEXT = GetStatusContext.defaults();

    // Constants for tests on persisted directories.
    private static final String DIR_PREFIX = "dir";

    private static final String DIR_TOP_LEVEL = "top";

    private static final String FILE_PREFIX = "file";

    private static final String MOUNT_PARENT_URI = "/mnt";

    private static final String MOUNT_URI = "/mnt/local";

    private static final int DIR_WIDTH = 2;

    private CreateFileContext mNestedFileContext;

    private MasterRegistry mRegistry;

    private JournalSystem mJournalSystem;

    private BlockMaster mBlockMaster;

    private ExecutorService mExecutorService;

    private DefaultFileSystemMaster mFileSystemMaster;

    private ReadOnlyInodeStore mInodeStore;

    private MetricsMaster mMetricsMaster;

    private List<Metric> mMetrics;

    private long mWorkerId1;

    private long mWorkerId2;

    private String mJournalFolder;

    private String mUnderFS;

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Rule
    public AuthenticatedUserRule mAuthenticatedUser = new AuthenticatedUserRule(FileSystemMasterTest.TEST_USER, ServerConfiguration.global());

    @Rule
    public LoginUserRule mLoginUser = new LoginUserRule(FileSystemMasterTest.TEST_USER, ServerConfiguration.global());

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(new HashMap() {
        {
            put(MASTER_JOURNAL_TYPE, "UFS");
            put(SECURITY_AUTHORIZATION_PERMISSION_UMASK, "000");
            put(MASTER_JOURNAL_TAILER_SLEEP_TIME_MS, "20");
            put(MASTER_JOURNAL_TAILER_SHUTDOWN_QUIET_WAIT_TIME_MS, "0");
            put(WORK_DIR, AlluxioTestDirectory.createTemporaryDirectory("workdir").getAbsolutePath());
            put(MASTER_MOUNT_TABLE_ROOT_UFS, AlluxioTestDirectory.createTemporaryDirectory("FileSystemMasterTest").getAbsolutePath());
        }
    }, ServerConfiguration.global());

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.MASTER_TTL_CHECK, HeartbeatContext.MASTER_LOST_FILES_DETECTION);

    // Set ttl interval to 0 so that there is no delay in detecting expired files.
    @ClassRule
    public static TtlIntervalRule sTtlIntervalRule = new TtlIntervalRule(0);

    @Test
    public void createPathWithWhiteSpaces() throws Exception {
        String[] paths = new String[]{ "/ ", "/  ", "/ path", "/path ", "/pa th", "/ pa th ", "/pa/ th", "/pa / th", "/ pa / th " };
        for (String path : paths) {
            AlluxioURI uri = new AlluxioURI(path);
            long id = mFileSystemMaster.createFile(uri, CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setRecursive(true))).getFileId();
            Assert.assertEquals(id, mFileSystemMaster.getFileId(uri));
            mFileSystemMaster.delete(uri, DeleteContext.defaults());
            id = mFileSystemMaster.createDirectory(uri, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
            Assert.assertEquals(id, mFileSystemMaster.getFileId(uri));
        }
    }

    @Test
    public void createFileMustCacheThenCacheThrough() throws Exception {
        File file = mTestFolder.newFile();
        AlluxioURI path = new AlluxioURI("/test");
        mFileSystemMaster.createFile(path, CreateFileContext.defaults().setPersisted(false));
        mThrown.expect(FileAlreadyExistsException.class);
        mFileSystemMaster.createFile(path, CreateFileContext.defaults().setPersisted(true));
    }

    @Test
    public void createFileUsesOperationTime() throws Exception {
        AlluxioURI path = new AlluxioURI("/test");
        mFileSystemMaster.createFile(path, CreateFileContext.defaults().setOperationTimeMs(100));
        Assert.assertEquals(100, mFileSystemMaster.getFileInfo(path, GetStatusContext.defaults()).getLastModificationTimeMs());
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method.
     */
    @Test
    public void deleteFile() throws Exception {
        // cannot delete root
        try {
            mFileSystemMaster.delete(FileSystemMasterTest.ROOT_URI, DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
            Assert.fail("Should not have been able to delete the root");
        } catch (InvalidPathException e) {
            Assert.assertEquals(DELETE_ROOT_DIRECTORY.getMessage(), e.getMessage());
        }
        // delete the file
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.delete(FileSystemMasterTest.NESTED_FILE_URI, DeleteContext.defaults());
        try {
            mBlockMaster.getBlockInfo(blockId);
            Assert.fail("Expected blockInfo to fail");
        } catch (BlockInfoException e) {
            // expected
        }
        // Update the heartbeat of removedBlockId received from worker 1.
        Command heartbeat1 = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat1);
        Assert.assertFalse(mBlockMaster.getLostBlocks().contains(blockId));
        // verify the file is deleted
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI));
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createDirectory(Paths.get(ufsMount.join("dir1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file1").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        AlluxioURI uri = new AlluxioURI("/mnt/local/dir1");
        mFileSystemMaster.listStatus(uri, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS)));
        mFileSystemMaster.delete(new AlluxioURI("/mnt/local/dir1/file1"), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setAlluxioOnly(true)));
        // ufs file still exists
        Assert.assertTrue(Files.exists(Paths.get(ufsMount.join("dir1").join("file1").getPath())));
        // verify the file is deleted
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local/dir1/file1"), GetStatusContext.mergeFrom(GetStatusPOptions.newBuilder().setLoadMetadataType(NEVER)));
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method with a
     * non-empty directory.
     */
    @Test
    public void deleteNonemptyDirectory() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        String dirName = mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getName();
        try {
            mFileSystemMaster.delete(FileSystemMasterTest.NESTED_URI, DeleteContext.defaults());
            Assert.fail("Deleting a non-empty directory without setting recursive should fail");
        } catch (DirectoryNotEmptyException e) {
            String expectedMessage = DELETE_NONEMPTY_DIRECTORY_NONRECURSIVE.getMessage(dirName);
            Assert.assertEquals(expectedMessage, e.getMessage());
        }
        // Now delete with recursive set to true.
        mFileSystemMaster.delete(FileSystemMasterTest.NESTED_URI, DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a directory.
     */
    @Test
    public void deleteDir() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        // delete the dir
        mFileSystemMaster.delete(FileSystemMasterTest.NESTED_URI, DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
        // verify the dir is deleted
        Assert.assertEquals((-1), mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_URI));
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createDirectory(Paths.get(ufsMount.join("dir1").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // load the dir1 to alluxio
        mFileSystemMaster.listStatus(new AlluxioURI("/mnt/local"), ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS)));
        mFileSystemMaster.delete(new AlluxioURI("/mnt/local/dir1"), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true).setAlluxioOnly(true)));
        // ufs directory still exists
        Assert.assertTrue(Files.exists(Paths.get(ufsMount.join("dir1").getPath())));
        // verify the directory is deleted
        Files.delete(Paths.get(ufsMount.join("dir1").getPath()));
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(new AlluxioURI("/mnt/local/dir1")));
    }

    @Test
    public void deleteRecursiveClearsInnerInodesAndEdges() throws Exception {
        createFileWithSingleBlock(new AlluxioURI("/a/b/c/d/e"));
        createFileWithSingleBlock(new AlluxioURI("/a/b/x/y/z"));
        mFileSystemMaster.delete(new AlluxioURI("/a/b"), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
        Assert.assertEquals(1, mInodeStore.allEdges().size());
        Assert.assertEquals(2, mInodeStore.allInodes().size());
    }

    @Test
    public void deleteDirRecursiveWithPermissions() throws Exception {
        // userA has permissions to delete directory and nested file
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (511))).toProto())));
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (511))).toProto())));
        try (AuthenticatedClientUserResource userA = new AuthenticatedClientUserResource("userA", ServerConfiguration.global())) {
            mFileSystemMaster.delete(FileSystemMasterTest.NESTED_URI, DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
        }
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_URI));
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI));
    }

    @Test
    public void deleteDirRecursiveWithInsufficientPermissions() throws Exception {
        // userA has permissions to delete directory but not one of the nested files
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE2_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (511))).toProto())));
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (448))).toProto())));
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE2_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (511))).toProto())));
        try (AuthenticatedClientUserResource userA = new AuthenticatedClientUserResource("userA", ServerConfiguration.global())) {
            mFileSystemMaster.delete(FileSystemMasterTest.NESTED_URI, DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true)));
            Assert.fail("Deleting a directory w/ insufficient permission on child should fail");
        } catch (AccessControlException e) {
            String expectedChildMessage = PERMISSION_DENIED.getMessage((("user=userA, access=-w-, path=" + (FileSystemMasterTest.NESTED_FILE_URI)) + ": failed at file"));
            Assert.assertTrue(e.getMessage().startsWith(DELETE_FAILED_DIR_CHILDREN.getMessage(FileSystemMasterTest.NESTED_URI, expectedChildMessage)));
        }
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_URI));
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI));
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE2_URI));
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a directory with persistent entries with a sync check.
     */
    @Test
    public void deleteSyncedPersistedDirectoryWithCheck() throws Exception {
        deleteSyncedPersistedDirectory(1, false);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a directory with persistent entries without a sync check.
     */
    @Test
    public void deleteSyncedPersistedDirectoryWithoutCheck() throws Exception {
        deleteSyncedPersistedDirectory(1, true);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a multi-level directory with persistent entries with a sync check.
     */
    @Test
    public void deleteSyncedPersistedMultilevelDirectoryWithCheck() throws Exception {
        deleteSyncedPersistedDirectory(3, false);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a multi-level directory with persistent entries without a sync check.
     */
    @Test
    public void deleteSyncedPersistedMultilevelDirectoryWithoutCheck() throws Exception {
        deleteSyncedPersistedDirectory(3, true);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a directory with un-synced persistent entries with a sync check.
     */
    @Test
    public void deleteUnsyncedPersistedDirectoryWithCheck() throws Exception {
        AlluxioURI ufsMount = createPersistedDirectories(1);
        mountPersistedDirectories(ufsMount);
        loadPersistedDirectories(1);
        // Add a file to the UFS.
        Files.createFile(Paths.get(ufsMount.join(FileSystemMasterTest.DIR_TOP_LEVEL).join(((FileSystemMasterTest.FILE_PREFIX) + (FileSystemMasterTest.DIR_WIDTH))).getPath()));
        // delete top-level directory
        try {
            mFileSystemMaster.delete(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true).setAlluxioOnly(false).setUnchecked(false)));
            Assert.fail();
        } catch (IOException e) {
            // Expected
        }
        // Check all that could be deleted.
        List<AlluxioURI> except = new ArrayList<>();
        except.add(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL));
        checkPersistedDirectoriesDeleted(1, ufsMount, except);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a directory with un-synced persistent entries without a sync check.
     */
    @Test
    public void deleteUnsyncedPersistedDirectoryWithoutCheck() throws Exception {
        AlluxioURI ufsMount = createPersistedDirectories(1);
        mountPersistedDirectories(ufsMount);
        loadPersistedDirectories(1);
        // Add a file to the UFS.
        Files.createFile(Paths.get(ufsMount.join(FileSystemMasterTest.DIR_TOP_LEVEL).join(((FileSystemMasterTest.FILE_PREFIX) + (FileSystemMasterTest.DIR_WIDTH))).getPath()));
        // delete top-level directory
        mFileSystemMaster.delete(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true).setAlluxioOnly(false).setUnchecked(true)));
        checkPersistedDirectoriesDeleted(1, ufsMount, Collections.EMPTY_LIST);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a multi-level directory with un-synced persistent entries with a sync check.
     */
    @Test
    public void deleteUnsyncedPersistedMultilevelDirectoryWithCheck() throws Exception {
        AlluxioURI ufsMount = createPersistedDirectories(3);
        mountPersistedDirectories(ufsMount);
        loadPersistedDirectories(3);
        // Add a file to the UFS down the tree.
        Files.createFile(Paths.get(ufsMount.join(FileSystemMasterTest.DIR_TOP_LEVEL).join(((FileSystemMasterTest.DIR_PREFIX) + 0)).join(((FileSystemMasterTest.FILE_PREFIX) + (FileSystemMasterTest.DIR_WIDTH))).getPath()));
        mThrown.expect(IOException.class);
        // delete top-level directory
        mFileSystemMaster.delete(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true).setAlluxioOnly(false).setUnchecked(false)));
        // Check all that could be deleted.
        List<AlluxioURI> except = new ArrayList<>();
        except.add(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL));
        except.add(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL).join(((FileSystemMasterTest.DIR_PREFIX) + 0)));
        checkPersistedDirectoriesDeleted(3, ufsMount, except);
    }

    /**
     * Tests the {@link FileSystemMaster#delete(AlluxioURI, DeleteContext)} method for
     * a multi-level directory with un-synced persistent entries without a sync check.
     */
    @Test
    public void deleteUnsyncedPersistedMultilevelDirectoryWithoutCheck() throws Exception {
        AlluxioURI ufsMount = createPersistedDirectories(3);
        mountPersistedDirectories(ufsMount);
        loadPersistedDirectories(3);
        // Add a file to the UFS down the tree.
        Files.createFile(Paths.get(ufsMount.join(FileSystemMasterTest.DIR_TOP_LEVEL).join(((FileSystemMasterTest.DIR_PREFIX) + 0)).join(((FileSystemMasterTest.FILE_PREFIX) + (FileSystemMasterTest.DIR_WIDTH))).getPath()));
        // delete top-level directory
        mFileSystemMaster.delete(new AlluxioURI(FileSystemMasterTest.MOUNT_URI).join(FileSystemMasterTest.DIR_TOP_LEVEL), DeleteContext.mergeFrom(DeletePOptions.newBuilder().setRecursive(true).setAlluxioOnly(false).setUnchecked(true)));
        checkPersistedDirectoriesDeleted(3, ufsMount, Collections.EMPTY_LIST);
    }

    /**
     * Tests the {@link FileSystemMaster#getNewBlockIdForFile(AlluxioURI)} method.
     */
    @Test
    public void getNewBlockIdForFile() throws Exception {
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, mNestedFileContext);
        long blockId = mFileSystemMaster.getNewBlockIdForFile(FileSystemMasterTest.NESTED_FILE_URI);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        Assert.assertEquals(Lists.newArrayList(blockId), fileInfo.getBlockIds());
    }

    @Test
    public void getPath() throws Exception {
        AlluxioURI rootUri = new AlluxioURI("/");
        long rootId = mFileSystemMaster.getFileId(rootUri);
        Assert.assertEquals(rootUri, mFileSystemMaster.getPath(rootId));
        // get non-existent id
        try {
            mFileSystemMaster.getPath((rootId + 1234));
            Assert.fail("getPath() for a non-existent id should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    /**
     * Tests the {@link FileSystemMaster#getPersistenceState(long)} method.
     */
    @Test
    public void getPersistenceState() throws Exception {
        AlluxioURI rootUri = new AlluxioURI("/");
        long rootId = mFileSystemMaster.getFileId(rootUri);
        Assert.assertEquals(PERSISTED, mFileSystemMaster.getPersistenceState(rootId));
        // get non-existent id
        try {
            mFileSystemMaster.getPersistenceState((rootId + 1234));
            Assert.fail("getPath() for a non-existent id should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    /**
     * Tests the {@link FileSystemMaster#getFileId(AlluxioURI)} method.
     */
    @Test
    public void getFileId() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        // These URIs exist.
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.ROOT_URI));
        Assert.assertEquals(FileSystemMasterTest.ROOT_URI, mFileSystemMaster.getPath(mFileSystemMaster.getFileId(FileSystemMasterTest.ROOT_URI)));
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_URI));
        Assert.assertEquals(FileSystemMasterTest.NESTED_URI, mFileSystemMaster.getPath(mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_URI)));
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI));
        Assert.assertEquals(FileSystemMasterTest.NESTED_FILE_URI, mFileSystemMaster.getPath(mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI)));
        // These URIs do not exist.
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.ROOT_FILE_URI));
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.TEST_URI));
        Assert.assertEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI.join("DNE")));
    }

    /**
     * Tests the {@link FileSystemMaster#getFileInfo(AlluxioURI, GetStatusContext)} method.
     */
    @Test
    public void getFileInfo() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        long fileId;
        FileInfo info;
        fileId = mFileSystemMaster.getFileId(FileSystemMasterTest.ROOT_URI);
        info = mFileSystemMaster.getFileInfo(fileId);
        Assert.assertEquals(FileSystemMasterTest.ROOT_URI.getPath(), info.getPath());
        Assert.assertEquals(FileSystemMasterTest.ROOT_URI.getPath(), mFileSystemMaster.getFileInfo(FileSystemMasterTest.ROOT_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getPath());
        fileId = mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_URI);
        info = mFileSystemMaster.getFileInfo(fileId);
        Assert.assertEquals(FileSystemMasterTest.NESTED_URI.getPath(), info.getPath());
        Assert.assertEquals(FileSystemMasterTest.NESTED_URI.getPath(), mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getPath());
        fileId = mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI);
        info = mFileSystemMaster.getFileInfo(fileId);
        Assert.assertEquals(FileSystemMasterTest.NESTED_FILE_URI.getPath(), info.getPath());
        Assert.assertEquals(FileSystemMasterTest.NESTED_FILE_URI.getPath(), mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getPath());
        // Test non-existent id.
        try {
            mFileSystemMaster.getFileInfo((fileId + 1234));
            Assert.fail("getFileInfo() for a non-existent id should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        // Test non-existent URIs.
        try {
            mFileSystemMaster.getFileInfo(FileSystemMasterTest.ROOT_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
            Assert.fail("getFileInfo() for a non-existent URI should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        try {
            mFileSystemMaster.getFileInfo(FileSystemMasterTest.TEST_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
            Assert.fail("getFileInfo() for a non-existent URI should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        try {
            mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI.join("DNE"), FileSystemMasterTest.GET_STATUS_CONTEXT);
            Assert.fail("getFileInfo() for a non-existent URI should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    @Test
    public void getFileInfoWithLoadMetadata() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createFile(Paths.get(ufsMount.join("file").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // 3 directories exist.
        Assert.assertEquals(3, countPaths());
        // getFileInfo should load metadata automatically.
        AlluxioURI uri = new AlluxioURI("/mnt/local/file");
        Assert.assertEquals(uri.getPath(), mFileSystemMaster.getFileInfo(uri, FileSystemMasterTest.GET_STATUS_CONTEXT).getPath());
        // getFileInfo should have loaded another file, so now 4 paths exist.
        Assert.assertEquals(4, countPaths());
    }

    @Test
    public void getFileIdWithLoadMetadata() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createFile(Paths.get(ufsMount.join("file").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // 3 directories exist.
        Assert.assertEquals(3, countPaths());
        // getFileId should load metadata automatically.
        AlluxioURI uri = new AlluxioURI("/mnt/local/file");
        Assert.assertNotEquals(INVALID_FILE_ID, mFileSystemMaster.getFileId(uri));
        // getFileId should have loaded another file, so now 4 paths exist.
        Assert.assertEquals(4, countPaths());
    }

    @Test
    public void listStatusWithLoadMetadataNever() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createDirectory(Paths.get(ufsMount.join("dir1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file2").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // 3 directories exist.
        Assert.assertEquals(3, countPaths());
        // getFileId should load metadata automatically.
        AlluxioURI uri = new AlluxioURI("/mnt/local/dir1");
        try {
            mFileSystemMaster.listStatus(uri, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(NEVER)));
            Assert.fail("Exception expected");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        Assert.assertEquals(3, countPaths());
    }

    @Test
    public void listStatusWithLoadMetadataOnce() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createDirectory(Paths.get(ufsMount.join("dir1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file2").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // 3 directories exist.
        Assert.assertEquals(3, countPaths());
        // getFileId should load metadata automatically.
        AlluxioURI uri = new AlluxioURI("/mnt/local/dir1");
        List<FileInfo> fileInfoList = mFileSystemMaster.listStatus(uri, ListStatusContext.defaults());
        Set<String> paths = new HashSet<>();
        for (FileInfo fileInfo : fileInfoList) {
            paths.add(fileInfo.getPath());
        }
        Assert.assertEquals(2, paths.size());
        Assert.assertTrue(paths.contains("/mnt/local/dir1/file1"));
        Assert.assertTrue(paths.contains("/mnt/local/dir1/file2"));
        // listStatus should have loaded another 3 files (dir1, dir1/file1, dir1/file2), so now 6
        // paths exist.
        Assert.assertEquals(6, countPaths());
    }

    @Test
    public void listStatusWithLoadMetadataAlways() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createDirectory(Paths.get(ufsMount.join("dir1").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // 3 directories exist.
        Assert.assertEquals(3, countPaths());
        // getFileId should load metadata automatically.
        AlluxioURI uri = new AlluxioURI("/mnt/local/dir1");
        List<FileInfo> fileInfoList = mFileSystemMaster.listStatus(uri, ListStatusContext.defaults());
        Assert.assertEquals(0, fileInfoList.size());
        // listStatus should have loaded another files (dir1), so now 4 paths exist.
        Assert.assertEquals(4, countPaths());
        // Add two files.
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("dir1").join("file2").getPath()));
        fileInfoList = mFileSystemMaster.listStatus(uri, ListStatusContext.defaults());
        Assert.assertEquals(0, fileInfoList.size());
        // No file is loaded since dir1 has been loaded once.
        Assert.assertEquals(4, countPaths());
        fileInfoList = mFileSystemMaster.listStatus(uri, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS)));
        Set<String> paths = new HashSet<>();
        for (FileInfo fileInfo : fileInfoList) {
            paths.add(fileInfo.getPath());
        }
        Assert.assertEquals(2, paths.size());
        Assert.assertTrue(paths.contains("/mnt/local/dir1/file1"));
        Assert.assertTrue(paths.contains("/mnt/local/dir1/file2"));
        // listStatus should have loaded another 2 files (dir1/file1, dir1/file2), so now 6
        // paths exist.
        Assert.assertEquals(6, countPaths());
    }

    /**
     * Tests listing status on a non-persisted directory.
     */
    @Test
    public void listStatusWithLoadMetadataNonPersistedDir() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // 3 directories exist.
        Assert.assertEquals(3, countPaths());
        // Create a drectory in alluxio which is not persisted.
        AlluxioURI folder = new AlluxioURI("/mnt/local/folder");
        mFileSystemMaster.createDirectory(folder, CreateDirectoryContext.defaults());
        Assert.assertFalse(mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local/folder"), FileSystemMasterTest.GET_STATUS_CONTEXT).isPersisted());
        // Create files in ufs.
        Files.createDirectory(Paths.get(ufsMount.join("folder").getPath()));
        Files.createFile(Paths.get(ufsMount.join("folder").join("file1").getPath()));
        Files.createFile(Paths.get(ufsMount.join("folder").join("file2").getPath()));
        // getStatus won't mark folder as persisted.
        Assert.assertFalse(mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local/folder"), FileSystemMasterTest.GET_STATUS_CONTEXT).isPersisted());
        List<FileInfo> fileInfoList = mFileSystemMaster.listStatus(folder, ListStatusContext.defaults());
        Assert.assertEquals(2, fileInfoList.size());
        // listStatus should have loaded files (folder, folder/file1, folder/file2), so now 6 paths
        // exist.
        Assert.assertEquals(6, countPaths());
        Set<String> paths = new HashSet<>();
        for (FileInfo f : fileInfoList) {
            paths.add(f.getPath());
        }
        Assert.assertEquals(2, paths.size());
        Assert.assertTrue(paths.contains("/mnt/local/folder/file1"));
        Assert.assertTrue(paths.contains("/mnt/local/folder/file2"));
        Assert.assertTrue(mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local/folder"), FileSystemMasterTest.GET_STATUS_CONTEXT).isPersisted());
    }

    @Test
    public void listStatus() throws Exception {
        final int files = 10;
        List<FileInfo> infos;
        List<String> filenames;
        // Test files in root directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))));
        }
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(NEVER)));
        Assert.assertEquals(files, infos.size());
        // Copy out filenames to use List contains.
        filenames = new ArrayList<>();
        for (FileInfo info : infos) {
            filenames.add(info.getPath());
        }
        // Compare all filenames.
        for (int i = 0; i < files; i++) {
            Assert.assertTrue(filenames.contains(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))).toString()));
        }
        // Test single file.
        createFileWithSingleBlock(FileSystemMasterTest.ROOT_FILE_URI);
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_FILE_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(NEVER)));
        Assert.assertEquals(1, infos.size());
        Assert.assertEquals(FileSystemMasterTest.ROOT_FILE_URI.getPath(), infos.get(0).getPath());
        // Test files in nested directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))));
        }
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.NESTED_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(NEVER)));
        Assert.assertEquals(files, infos.size());
        // Copy out filenames to use List contains.
        filenames = new ArrayList<>();
        for (FileInfo info : infos) {
            filenames.add(info.getPath());
        }
        // Compare all filenames.
        for (int i = 0; i < files; i++) {
            Assert.assertTrue(filenames.contains(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))).toString()));
        }
        // Test non-existent URIs.
        try {
            mFileSystemMaster.listStatus(FileSystemMasterTest.NESTED_URI.join("DNE"), ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(NEVER)));
            Assert.fail("listStatus() for a non-existent URI should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    @Test
    public void listStatusRecursive() throws Exception {
        final int files = 10;
        List<FileInfo> infos;
        List<String> filenames;
        // Test files in root directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))));
        }
        // Test files in nested directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))));
        }
        // Test recursive listStatus
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).setRecursive(true)));
        // 10 files in each directory, 2 levels of directories
        Assert.assertEquals(((files + files) + 2), infos.size());
        filenames = new ArrayList<>();
        for (FileInfo info : infos) {
            filenames.add(info.getPath());
        }
        for (int i = 0; i < files; i++) {
            Assert.assertTrue(filenames.contains(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))).toString()));
        }
        for (int i = 0; i < files; i++) {
            Assert.assertTrue(filenames.contains(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))).toString()));
        }
    }

    @Test
    public void listStatusRecursivePermissions() throws Exception {
        final int files = 10;
        List<FileInfo> infos;
        List<String> filenames;
        // Test files in root directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))));
        }
        // Test files in nested directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))));
        }
        // Test with permissions
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (256))).toProto()).setRecursive(true)));
        try (Closeable r = toResource()) {
            // Test recursive listStatus
            infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).setRecursive(true)));
            // 10 files in each directory, 1 level of directories
            Assert.assertEquals((files + 1), infos.size());
        }
    }

    @Test
    public void listStatusRecursiveLoadMetadata() throws Exception {
        final int files = 10;
        List<FileInfo> infos;
        // Test files in root directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))));
        }
        FileUtils.createFile(Paths.get(mUnderFS).resolve("ufsfile1").toString());
        // Test interaction between recursive and loadMetadata
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ONCE).setRecursive(false)));
        Assert.assertEquals((files + 1), infos.size());
        FileUtils.createFile(Paths.get(mUnderFS).resolve("ufsfile2").toString());
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ONCE).setRecursive(false)));
        Assert.assertEquals((files + 1), infos.size());
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).setRecursive(false)));
        Assert.assertEquals((files + 2), infos.size());
        // Test files in nested directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))));
        }
        FileUtils.createFile(Paths.get(mUnderFS).resolve("nested/test/ufsnestedfile1").toString());
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ONCE).setRecursive(true)));
        // 2 sets of files, 2 files inserted at root, 2 directories nested and test,
        // 1 file ufsnestedfile1
        Assert.assertEquals(((((files + files) + 2) + 2) + 1), infos.size());
        FileUtils.createFile(Paths.get(mUnderFS).resolve("nested/test/ufsnestedfile2").toString());
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ONCE).setRecursive(true)));
        Assert.assertEquals(((((files + files) + 2) + 2) + 1), infos.size());
        infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).setRecursive(true)));
        Assert.assertEquals(((((files + files) + 2) + 2) + 2), infos.size());
    }

    @Test
    public void getFileBlockInfoList() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.ROOT_FILE_URI);
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        List<FileBlockInfo> blockInfo;
        blockInfo = mFileSystemMaster.getFileBlockInfoList(FileSystemMasterTest.ROOT_FILE_URI);
        Assert.assertEquals(1, blockInfo.size());
        blockInfo = mFileSystemMaster.getFileBlockInfoList(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, blockInfo.size());
        // Test directory URI.
        try {
            mFileSystemMaster.getFileBlockInfoList(FileSystemMasterTest.NESTED_URI);
            Assert.fail("getFileBlockInfoList() for a directory URI should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        // Test non-existent URI.
        try {
            mFileSystemMaster.getFileBlockInfoList(FileSystemMasterTest.TEST_URI);
            Assert.fail("getFileBlockInfoList() for a non-existent URI should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    @Test
    public void mountUnmount() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Alluxio mount point should not exist before mounting.
        try {
            mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local"), FileSystemMasterTest.GET_STATUS_CONTEXT);
            Assert.fail("getFileInfo() for a non-existent URI (before mounting) should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // Alluxio mount point should exist after mounting.
        Assert.assertNotNull(mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local"), FileSystemMasterTest.GET_STATUS_CONTEXT));
        mFileSystemMaster.unmount(new AlluxioURI("/mnt/local"));
        // Alluxio mount point should not exist after unmounting.
        try {
            mFileSystemMaster.getFileInfo(new AlluxioURI("/mnt/local"), FileSystemMasterTest.GET_STATUS_CONTEXT);
            Assert.fail("getFileInfo() for a non-existent URI (after mounting) should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    @Test
    public void loadMetadata() throws Exception {
        AlluxioURI ufsMount = new AlluxioURI(mTestFolder.newFolder().getAbsolutePath());
        mFileSystemMaster.createDirectory(new AlluxioURI("/mnt/"), CreateDirectoryContext.defaults());
        // Create ufs file.
        Files.createFile(Paths.get(ufsMount.join("file").getPath()));
        // Created nested file.
        Files.createDirectory(Paths.get(ufsMount.join("nested").getPath()));
        Files.createFile(Paths.get(ufsMount.join("nested").join("file").getPath()));
        mFileSystemMaster.mount(new AlluxioURI("/mnt/local"), ufsMount, MountContext.defaults());
        // Test simple file.
        AlluxioURI uri = new AlluxioURI("/mnt/local/file");
        mFileSystemMaster.loadMetadata(uri, LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(false)));
        Assert.assertNotNull(mFileSystemMaster.getFileInfo(uri, FileSystemMasterTest.GET_STATUS_CONTEXT));
        // Test nested file.
        uri = new AlluxioURI("/mnt/local/nested/file");
        try {
            mFileSystemMaster.loadMetadata(uri, LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(false)));
            Assert.fail("loadMetadata() without recursive, for a nested file should fail.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
        // Test the nested file with recursive flag.
        mFileSystemMaster.loadMetadata(uri, LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(true)));
        Assert.assertNotNull(mFileSystemMaster.getFileInfo(uri, FileSystemMasterTest.GET_STATUS_CONTEXT));
    }

    @Test
    public void setDefaultAcl() throws Exception {
        SetAclContext context = SetAclContext.defaults();
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Set<String> entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertEquals(0, entries.size());
        // replace
        Set<String> newEntries = Sets.newHashSet("default:user::rwx", "default:group::rwx", "default:other::r-x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertEquals(newEntries, entries);
        // replace
        newEntries = Sets.newHashSet("default:user::rw-", "default:group::r--", "default:other::r--");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertEquals(newEntries, entries);
        // modify existing
        newEntries = Sets.newHashSet("default:user::rwx", "default:group::rw-", "default:other::r-x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertEquals(newEntries, entries);
        // modify add
        Set<String> oldEntries = new HashSet<>(entries);
        newEntries = Sets.newHashSet("default:user:usera:---", "default:group:groupa:--x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertTrue(entries.containsAll(oldEntries));
        Assert.assertTrue(entries.containsAll(newEntries));
        Assert.assertTrue(entries.contains("default:mask::rwx"));
        // modify existing and add
        newEntries = Sets.newHashSet("default:user:usera:---", "default:group:groupa:--x", "default:other::r-x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertTrue(entries.containsAll(newEntries));
        // remove default
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, REMOVE_DEFAULT, Collections.emptyList(), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertEquals(0, entries.size());
        // remove
        newEntries = Sets.newHashSet("default:user:usera:---", "default:user:userb:rwx", "default:group:groupa:--x", "default:group:groupb:-wx");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        oldEntries = new HashSet<>(entries);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Assert.assertTrue(entries.containsAll(oldEntries));
        Set<String> deleteEntries = Sets.newHashSet("default:user:userb:rwx", "default:group:groupa:--x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, REMOVE, deleteEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertDefaultAclToStringEntries());
        Set<String> remainingEntries = new HashSet<>(newEntries);
        Assert.assertTrue(remainingEntries.removeAll(deleteEntries));
        Assert.assertTrue(entries.containsAll(remainingEntries));
        final Set<String> finalEntries = entries;
        Assert.assertTrue(deleteEntries.stream().noneMatch(finalEntries::contains));
    }

    @Test
    public void setAcl() throws Exception {
        SetAclContext context = SetAclContext.defaults();
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Set<String> entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(3, entries.size());
        // replace
        Set<String> newEntries = Sets.newHashSet("user::rwx", "group::rwx", "other::rwx");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(newEntries, entries);
        // replace
        newEntries = Sets.newHashSet("user::rw-", "group::r--", "other::r--");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(newEntries, entries);
        // modify existing
        newEntries = Sets.newHashSet("user::rwx", "group::r--", "other::r-x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(newEntries, entries);
        // modify add
        Set<String> oldEntries = new HashSet<>(entries);
        newEntries = Sets.newHashSet("user:usera:---", "group:groupa:--x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertTrue(entries.containsAll(oldEntries));
        Assert.assertTrue(entries.containsAll(newEntries));
        // check if the mask got updated correctly
        Assert.assertTrue(entries.contains("mask::r-x"));
        // modify existing and add
        newEntries = Sets.newHashSet("user:usera:---", "group:groupa:--x", "other::r-x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertTrue(entries.containsAll(newEntries));
        // remove all
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, REMOVE_ALL, Collections.emptyList(), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(3, entries.size());
        // remove
        newEntries = Sets.newHashSet("user:usera:---", "user:userb:rwx", "group:groupa:--x", "group:groupb:-wx");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, MODIFY, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        oldEntries = new HashSet<>(entries);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertTrue(entries.containsAll(oldEntries));
        Set<String> deleteEntries = Sets.newHashSet("user:userb:rwx", "group:groupa:--x");
        mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, REMOVE, deleteEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Set<String> remainingEntries = new HashSet<>(newEntries);
        Assert.assertTrue(remainingEntries.removeAll(deleteEntries));
        Assert.assertTrue(entries.containsAll(remainingEntries));
        final Set<String> finalEntries = entries;
        Assert.assertTrue(deleteEntries.stream().noneMatch(finalEntries::contains));
    }

    @Test
    public void setRecursiveAcl() throws Exception {
        final int files = 10;
        SetAclContext context = SetAclContext.mergeFrom(SetAclPOptions.newBuilder().setRecursive(true));
        // Test files in root directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.ROOT_URI.join(("file" + (String.format("%05d", i)))));
        }
        // Test files in nested directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.NESTED_URI.join(("file" + (String.format("%05d", i)))));
        }
        // Test files in nested directory.
        for (int i = 0; i < files; i++) {
            createFileWithSingleBlock(FileSystemMasterTest.NESTED_DIR_URI.join(("file" + (String.format("%05d", i)))));
        }
        // replace
        Set<String> newEntries = Sets.newHashSet("user::rw-", "group::r-x", "other::-wx");
        mFileSystemMaster.setAcl(FileSystemMasterTest.ROOT_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), context);
        List<FileInfo> infos = mFileSystemMaster.listStatus(FileSystemMasterTest.ROOT_URI, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ONCE).setRecursive(true)));
        Assert.assertEquals(((files * 3) + 3), infos.size());
        for (FileInfo info : infos) {
            Assert.assertEquals(newEntries, Sets.newHashSet(info.convertAclToStringEntries()));
        }
    }

    @Test
    public void inheritExtendedDefaultAcl() throws Exception {
        AlluxioURI dir = new AlluxioURI("/dir");
        mFileSystemMaster.createDirectory(dir, CreateDirectoryContext.defaults());
        String aclString = "default:user:foo:-w-";
        mFileSystemMaster.setAcl(dir, MODIFY, Arrays.asList(AclEntry.fromCliString(aclString)), SetAclContext.defaults());
        AlluxioURI inner = new AlluxioURI("/dir/inner");
        mFileSystemMaster.createDirectory(inner, CreateDirectoryContext.defaults());
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(inner, GetStatusContext.defaults());
        List<String> accessEntries = fileInfo.getAcl().toStringEntries();
        Assert.assertTrue(accessEntries.toString(), accessEntries.contains("user:foo:-w-"));
        List<String> defaultEntries = fileInfo.getDefaultAcl().toStringEntries();
        Assert.assertTrue(defaultEntries.toString(), defaultEntries.contains(aclString));
    }

    @Test
    public void inheritNonExtendedDefaultAcl() throws Exception {
        AlluxioURI dir = new AlluxioURI("/dir");
        mFileSystemMaster.createDirectory(dir, CreateDirectoryContext.defaults());
        String aclString = "default:user::-w-";
        mFileSystemMaster.setAcl(dir, MODIFY, Arrays.asList(AclEntry.fromCliString(aclString)), SetAclContext.defaults());
        AlluxioURI inner = new AlluxioURI("/dir/inner");
        mFileSystemMaster.createDirectory(inner, CreateDirectoryContext.defaults());
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(inner, GetStatusContext.defaults());
        List<String> accessEntries = fileInfo.getAcl().toStringEntries();
        Assert.assertTrue(accessEntries.toString(), accessEntries.contains("user::-w-"));
        List<String> defaultEntries = fileInfo.getDefaultAcl().toStringEntries();
        Assert.assertTrue(defaultEntries.toString(), defaultEntries.contains(aclString));
    }

    @Test
    public void setAclWithoutOwner() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (511))).toProto())));
        Set<String> entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(3, entries.size());
        try (AuthenticatedClientUserResource userA = new AuthenticatedClientUserResource("userA", ServerConfiguration.global())) {
            Set<String> newEntries = Sets.newHashSet("user::rwx", "group::rwx", "other::rwx");
            mThrown.expect(AccessControlException.class);
            mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_FILE_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), SetAclContext.defaults());
        }
    }

    @Test
    public void setAclNestedWithoutOwner() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setMode(new Mode(((short) (511))).toProto()).setOwner("userA")));
        Set<String> entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
        Assert.assertEquals(3, entries.size());
        // recursive setAcl should fail if one of the child is not owned by the user
        mThrown.expect(AccessControlException.class);
        try (AuthenticatedClientUserResource userA = new AuthenticatedClientUserResource("userA", ServerConfiguration.global())) {
            Set<String> newEntries = Sets.newHashSet("user::rwx", "group::rwx", "other::rwx");
            mFileSystemMaster.setAcl(FileSystemMasterTest.NESTED_URI, REPLACE, newEntries.stream().map(AclEntry::fromCliString).collect(Collectors.toList()), SetAclContext.mergeFrom(SetAclPOptions.newBuilder().setRecursive(true)));
            entries = Sets.newHashSet(mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).convertAclToStringEntries());
            Assert.assertEquals(newEntries, entries);
        }
    }

    @Test
    public void removeExtendedAclMask() throws Exception {
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
        AclEntry newAcl = AclEntry.fromCliString("user:newuser:rwx");
        // Add an ACL
        addAcl(FileSystemMasterTest.NESTED_URI, newAcl);
        Assert.assertThat(getInfo(FileSystemMasterTest.NESTED_URI).getAcl().getEntries(), CoreMatchers.hasItem(newAcl));
        // Attempt to remove the ACL mask
        AclEntry maskEntry = AclEntry.fromCliString("mask::rwx");
        Assert.assertThat(getInfo(FileSystemMasterTest.NESTED_URI).getAcl().getEntries(), CoreMatchers.hasItem(maskEntry));
        try {
            removeAcl(FileSystemMasterTest.NESTED_URI, maskEntry);
            Assert.fail("Expected removing the mask from an extended ACL to fail");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("mask"));
        }
        // Remove the extended ACL
        removeAcl(FileSystemMasterTest.NESTED_URI, newAcl);
        // Now we can add and remove a mask
        addAcl(FileSystemMasterTest.NESTED_URI, maskEntry);
        removeAcl(FileSystemMasterTest.NESTED_URI, maskEntry);
    }

    @Test
    public void removeExtendedDefaultAclMask() throws Exception {
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
        AclEntry newAcl = AclEntry.fromCliString("default:user:newuser:rwx");
        // Add an ACL
        addAcl(FileSystemMasterTest.NESTED_URI, newAcl);
        Assert.assertThat(getInfo(FileSystemMasterTest.NESTED_URI).getDefaultAcl().getEntries(), CoreMatchers.hasItem(newAcl));
        // Attempt to remove the ACL mask
        AclEntry maskEntry = AclEntry.fromCliString("default:mask::rwx");
        Assert.assertThat(getInfo(FileSystemMasterTest.NESTED_URI).getDefaultAcl().getEntries(), CoreMatchers.hasItem(maskEntry));
        try {
            removeAcl(FileSystemMasterTest.NESTED_URI, maskEntry);
            Assert.fail("Expected removing the mask from an extended ACL to fail");
        } catch (IOException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("mask"));
        }
        // Remove the extended ACL
        removeAcl(FileSystemMasterTest.NESTED_URI, newAcl);
        // Now we can add and remove a mask
        addAcl(FileSystemMasterTest.NESTED_URI, maskEntry);
        removeAcl(FileSystemMasterTest.NESTED_URI, maskEntry);
    }

    /**
     * Tests that an exception is in the
     * {@link FileSystemMaster#createFile(AlluxioURI, CreateFileContext)} with a
     * TTL set in the {@link CreateFileContext} after the TTL check was done once.
     */
    @Test
    public void ttlFileDelete() throws Exception {
        CreateFileContext context = CreateFileContext.defaults();
        context.getOptions().setBlockSizeBytes(KB);
        context.getOptions().setRecursive(true);
        context.getOptions().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, context).getFileId();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(fileId);
        Assert.assertEquals(fileInfo.getFileId(), fileId);
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(fileId);
    }

    /**
     * Tests that TTL delete of a file is not forgotten across restarts.
     */
    @Test
    public void ttlFileDeleteReplay() throws Exception {
        CreateFileContext context = CreateFileContext.defaults();
        context.getOptions().setBlockSizeBytes(KB);
        context.getOptions().setRecursive(true);
        context.getOptions().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, context).getFileId();
        // Simulate restart.
        stopServices();
        startServices();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(fileId);
        Assert.assertEquals(fileInfo.getFileId(), fileId);
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(fileId);
    }

    /**
     * Tests that an exception is in the
     * {@literal FileSystemMaster#createDirectory(AlluxioURI, CreateDirectoryOptions)}
     * with a TTL set in the {@link CreateDirectoryContext} after the TTL check was done once.
     */
    @Test
    public void ttlDirectoryDelete() throws Exception {
        CreateDirectoryContext context = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true).setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0)));
        long dirId = mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_DIR_URI, context);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(dirId);
        Assert.assertEquals(fileInfo.getFileId(), dirId);
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(dirId);
    }

    /**
     * Tests that TTL delete of a directory is not forgotten across restarts.
     */
    @Test
    public void ttlDirectoryDeleteReplay() throws Exception {
        CreateDirectoryContext context = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true).setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0)));
        long dirId = mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_DIR_URI, context);
        // Simulate restart.
        stopServices();
        startServices();
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(dirId);
        Assert.assertEquals(fileInfo.getFileId(), dirId);
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(dirId);
    }

    /**
     * Tests that file information is still present after it has been freed after the TTL has been set
     * to 0.
     */
    @Test
    public void ttlFileFree() throws Exception {
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // Set ttl & operation.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemOptions.commonDefaults(ServerConfiguration.global()).toBuilder().setTtl(0).setTtlAction(FREE))));
        Command heartbeat = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests that TTL free of a file is not forgotten across restarts.
     */
    @Test
    public void ttlFileFreeReplay() throws Exception {
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // Set ttl & operation.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemOptions.commonDefaults(ServerConfiguration.global()).toBuilder().setTtl(0).setTtlAction(FREE))));
        // Simulate restart.
        stopServices();
        startServices();
        Command heartbeat = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests that file information is still present after it has been freed after the parent
     * directory's TTL has been set to 0.
     */
    @Test
    public void ttlDirectoryFree() throws Exception {
        CreateDirectoryContext directoryContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true));
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, directoryContext);
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // Set ttl & operation.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0).setTtlAction(FREE))));
        Command heartbeat = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests that TTL free of a directory is not forgotten across restarts.
     */
    @Test
    public void ttlDirectoryFreeReplay() throws Exception {
        CreateDirectoryContext directoryContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true));
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, directoryContext);
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // Set ttl & operation.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemOptions.commonDefaults(ServerConfiguration.global()).toBuilder().setTtl(0).setTtlAction(FREE))));
        // Simulate restart.
        stopServices();
        startServices();
        Command heartbeat = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests that an exception is thrown when trying to get information about a file after it
     * has been deleted because of a TTL of 0.
     */
    @Test
    public void setTtlForFileWithNoTtl() throws Exception {
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB).setRecursive(true));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, context).getFileId();
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // Since no TTL is set, the file should not be deleted.
        Assert.assertEquals(fileId, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getFileId());
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // TTL is set to 0, the file should have been deleted during last TTL check.
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(fileId);
    }

    /**
     * Tests that an exception is thrown when trying to get information about a Directory after
     * it has been deleted because of a TTL of 0.
     */
    @Test
    public void setTtlForDirectoryWithNoTtl() throws Exception {
        CreateDirectoryContext directoryContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true));
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, directoryContext);
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_DIR_URI, directoryContext);
        CreateFileContext createFileContext = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB).setRecursive(true));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, createFileContext).getFileId();
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // Since no TTL is set, the file should not be deleted.
        Assert.assertEquals(fileId, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getFileId());
        // Set ttl.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // TTL is set to 0, the file and directory should have been deleted during last TTL check.
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_DIR_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
    }

    /**
     * Tests that an exception is thrown when trying to get information about a file after it
     * has been deleted after the TTL has been set to 0.
     */
    @Test
    public void setSmallerTtlForFileWithTtl() throws Exception {
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(HOUR_MS)).setBlockSizeBytes(KB).setRecursive(true));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, context).getFileId();
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // Since TTL is 1 hour, the file won't be deleted during last TTL check.
        Assert.assertEquals(fileId, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getFileId());
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // TTL is reset to 0, the file should have been deleted during last TTL check.
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(fileId);
    }

    /**
     * Tests that an exception is thrown when trying to get information about a Directory after
     * it has been deleted after the TTL has been set to 0.
     */
    @Test
    public void setSmallerTtlForDirectoryWithTtl() throws Exception {
        CreateDirectoryContext directoryContext = CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(HOUR_MS)).setRecursive(true));
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, directoryContext);
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        Assert.assertTrue(((mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getName()) != null));
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // TTL is reset to 0, the file should have been deleted during last TTL check.
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
    }

    /**
     * Tests that a file has not been deleted after the TTL has been reset to a valid value.
     */
    @Test
    public void setLargerTtlForFileWithTtl() throws Exception {
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0)).setBlockSizeBytes(KB).setRecursive(true));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, context).getFileId();
        Assert.assertEquals(fileId, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getFileId());
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(HOUR_MS))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // TTL is reset to 1 hour, the file should not be deleted during last TTL check.
        Assert.assertEquals(fileId, mFileSystemMaster.getFileInfo(fileId).getFileId());
    }

    /**
     * Tests that a directory has not been deleted after the TTL has been reset to a valid value.
     */
    @Test
    public void setLargerTtlForDirectoryWithTtl() throws Exception {
        mFileSystemMaster.createDirectory(new AlluxioURI("/nested"), CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0)).setRecursive(true)));
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(HOUR_MS))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        // TTL is reset to 1 hour, the directory should not be deleted during last TTL check.
        Assert.assertEquals(FileSystemMasterTest.NESTED_URI.getName(), mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getName());
    }

    /**
     * Tests that the original TTL is removed after setting it to {@link Constants#NO_TTL} for a file.
     */
    @Test
    public void setNoTtlForFileWithTtl() throws Exception {
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0)).setBlockSizeBytes(KB).setRecursive(true));
        long fileId = mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, context).getFileId();
        // After setting TTL to NO_TTL, the original TTL will be removed, and the file will not be
        // deleted during next TTL check.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(NO_TTL))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        Assert.assertEquals(fileId, mFileSystemMaster.getFileInfo(fileId).getFileId());
    }

    /**
     * Tests that the original TTL is removed after setting it to {@link Constants#NO_TTL} for
     * a directory.
     */
    @Test
    public void setNoTtlForDirectoryWithTtl() throws Exception {
        mFileSystemMaster.createDirectory(new AlluxioURI("/nested"), CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setRecursive(true)));
        mFileSystemMaster.createDirectory(FileSystemMasterTest.NESTED_URI, CreateDirectoryContext.mergeFrom(CreateDirectoryPOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(0)).setRecursive(true)));
        // After setting TTL to NO_TTL, the original TTL will be removed, and the file will not be
        // deleted during next TTL check.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(NO_TTL))));
        HeartbeatScheduler.execute(MASTER_TTL_CHECK);
        Assert.assertEquals(FileSystemMasterTest.NESTED_URI.getName(), mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getName());
    }

    /**
     * Tests the {@link FileSystemMaster#setAttribute(AlluxioURI, SetAttributeContext)} method and
     * that an exception is thrown when trying to set a TTL for a directory.
     */
    @Test
    public void setAttribute() throws Exception {
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, mNestedFileContext);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        Assert.assertFalse(fileInfo.isPinned());
        Assert.assertEquals(NO_TTL, fileInfo.getTtl());
        // No State.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.defaults());
        fileInfo = mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        Assert.assertFalse(fileInfo.isPinned());
        Assert.assertEquals(NO_TTL, fileInfo.getTtl());
        // Just set pinned flag.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPinned(true)));
        fileInfo = mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        Assert.assertTrue(fileInfo.isPinned());
        Assert.assertEquals(NO_TTL, fileInfo.getTtl());
        // Both pinned flag and ttl value.
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPinned(false).setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(1))));
        fileInfo = mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT);
        Assert.assertFalse(fileInfo.isPinned());
        Assert.assertEquals(1, fileInfo.getTtl());
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setCommonOptions(FileSystemMasterCommonPOptions.newBuilder().setTtl(1))));
    }

    /**
     * Tests the permission bits are 0777 for directories and 0666 for files with UMASK 000.
     */
    @Test
    public void permission() throws Exception {
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, mNestedFileContext);
        Assert.assertEquals(511, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getMode());
        Assert.assertEquals(438, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getMode());
    }

    /**
     * Tests that a file is fully written to memory.
     */
    @Test
    public void isFullyInMemory() throws Exception {
        // add nested file
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, mNestedFileContext);
        // add in-memory block
        long blockId = mFileSystemMaster.getNewBlockIdForFile(FileSystemMasterTest.NESTED_FILE_URI);
        mBlockMaster.commitBlock(mWorkerId1, KB, "MEM", blockId, KB);
        // add SSD block
        blockId = mFileSystemMaster.getNewBlockIdForFile(FileSystemMasterTest.NESTED_FILE_URI);
        mBlockMaster.commitBlock(mWorkerId1, KB, "SSD", blockId, KB);
        mFileSystemMaster.completeFile(FileSystemMasterTest.NESTED_FILE_URI, CompleteFileContext.defaults());
        // Create 2 files in memory.
        createFileWithSingleBlock(FileSystemMasterTest.ROOT_FILE_URI);
        AlluxioURI nestedMemUri = FileSystemMasterTest.NESTED_URI.join("mem_file");
        createFileWithSingleBlock(nestedMemUri);
        Assert.assertEquals(2, mFileSystemMaster.getInMemoryFiles().size());
        Assert.assertTrue(mFileSystemMaster.getInMemoryFiles().contains(FileSystemMasterTest.ROOT_FILE_URI));
        Assert.assertTrue(mFileSystemMaster.getInMemoryFiles().contains(nestedMemUri));
    }

    /**
     * Tests the {@link FileSystemMaster#rename(AlluxioURI, AlluxioURI, RenameContext)} method.
     */
    @Test
    public void rename() throws Exception {
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_FILE_URI, mNestedFileContext);
        // try to rename a file to root
        try {
            mFileSystemMaster.rename(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.ROOT_URI, RenameContext.defaults());
            Assert.fail("Renaming to root should fail.");
        } catch (InvalidPathException e) {
            Assert.assertEquals(RENAME_CANNOT_BE_TO_ROOT.getMessage(), e.getMessage());
        }
        // move root to another path
        try {
            mFileSystemMaster.rename(FileSystemMasterTest.ROOT_URI, FileSystemMasterTest.TEST_URI, RenameContext.defaults());
            Assert.fail("Should not be able to rename root");
        } catch (InvalidPathException e) {
            Assert.assertEquals(ROOT_CANNOT_BE_RENAMED.getMessage(), e.getMessage());
        }
        // move to existing path
        try {
            mFileSystemMaster.rename(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.NESTED_URI, RenameContext.defaults());
            Assert.fail("Should not be able to overwrite existing file.");
        } catch (FileAlreadyExistsException e) {
            Assert.assertEquals(FILE_ALREADY_EXISTS.getMessage(FileSystemMasterTest.NESTED_URI.getPath()), e.getMessage());
        }
        // move a nested file to a root file
        mFileSystemMaster.rename(FileSystemMasterTest.NESTED_FILE_URI, FileSystemMasterTest.TEST_URI, RenameContext.defaults());
        Assert.assertEquals(mFileSystemMaster.getFileInfo(FileSystemMasterTest.TEST_URI, FileSystemMasterTest.GET_STATUS_CONTEXT).getPath(), FileSystemMasterTest.TEST_URI.getPath());
        // move a file where the dst is lexicographically earlier than the source
        AlluxioURI newDst = new AlluxioURI("/abc_test");
        mFileSystemMaster.rename(FileSystemMasterTest.TEST_URI, newDst, RenameContext.defaults());
        Assert.assertEquals(mFileSystemMaster.getFileInfo(newDst, FileSystemMasterTest.GET_STATUS_CONTEXT).getPath(), newDst.getPath());
    }

    /**
     * Tests that an exception is thrown when trying to create a file in a non-existing directory
     * without setting the {@code recursive} flag.
     */
    @Test
    public void renameUnderNonexistingDir() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mThrown.expectMessage(PATH_DOES_NOT_EXIST.getMessage("/nested/test"));
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB));
        mFileSystemMaster.createFile(FileSystemMasterTest.TEST_URI, context);
        // nested dir
        mFileSystemMaster.rename(FileSystemMasterTest.TEST_URI, FileSystemMasterTest.NESTED_FILE_URI, RenameContext.defaults());
    }

    @Test
    public void renameToNonExistentParent() throws Exception {
        CreateFileContext context = CreateFileContext.mergeFrom(CreateFilePOptions.newBuilder().setBlockSizeBytes(KB).setRecursive(true));
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_URI, context);
        try {
            mFileSystemMaster.rename(FileSystemMasterTest.NESTED_URI, new AlluxioURI("/testDNE/b"), RenameContext.defaults());
            Assert.fail("Rename to a non-existent parent path should not succeed.");
        } catch (FileDoesNotExistException e) {
            // Expected case.
        }
    }

    /**
     * Tests that an exception is thrown when trying to rename a file to a prefix of the original
     * file.
     */
    @Test
    public void renameToSubpath() throws Exception {
        mFileSystemMaster.createFile(FileSystemMasterTest.NESTED_URI, mNestedFileContext);
        mThrown.expect(InvalidPathException.class);
        mThrown.expectMessage(("Traversal failed for path /nested/test/file. " + "Component 2(test) is a file, not a directory"));
        mFileSystemMaster.rename(FileSystemMasterTest.NESTED_URI, FileSystemMasterTest.NESTED_FILE_URI, RenameContext.defaults());
    }

    /**
     * Tests {@link FileSystemMaster#free} on persisted file.
     */
    @Test
    public void free() throws Exception {
        mNestedFileContext.setPersisted(true);
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // free the file
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI, FreeContext.mergeFrom(FreePOptions.newBuilder().setForced(false).setRecursive(false)));
        // Update the heartbeat of removedBlockId received from worker 1.
        Command heartbeat2 = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat2);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests {@link FileSystemMaster#free} on non-persisted file.
     */
    @Test
    public void freeNonPersistedFile() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mThrown.expect(UnexpectedAlluxioException.class);
        mThrown.expectMessage(CANNOT_FREE_NON_PERSISTED_FILE.getMessage(FileSystemMasterTest.NESTED_FILE_URI.getPath()));
        // cannot free a non-persisted file
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI, FreeContext.defaults());
    }

    /**
     * Tests {@link FileSystemMaster#free} on pinned file when forced flag is false.
     */
    @Test
    public void freePinnedFileWithoutForce() throws Exception {
        mNestedFileContext.setPersisted(true);
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPinned(true)));
        mThrown.expect(UnexpectedAlluxioException.class);
        mThrown.expectMessage(CANNOT_FREE_PINNED_FILE.getMessage(FileSystemMasterTest.NESTED_FILE_URI.getPath()));
        // cannot free a pinned file without "forced"
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI, FreeContext.defaults());
    }

    /**
     * Tests {@link FileSystemMaster#free} on pinned file when forced flag is true.
     */
    @Test
    public void freePinnedFileWithForce() throws Exception {
        mNestedFileContext.setPersisted(true);
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPinned(true)));
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // free the file
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI, FreeContext.mergeFrom(FreePOptions.newBuilder().setForced(true)));
        // Update the heartbeat of removedBlockId received from worker 1.
        Command heartbeat = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests the {@link FileSystemMaster#free} method with a directory but recursive to false.
     */
    @Test
    public void freeDirNonRecursive() throws Exception {
        mNestedFileContext.setPersisted(true);
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mThrown.expect(UnexpectedAlluxioException.class);
        mThrown.expectMessage(CANNOT_FREE_NON_EMPTY_DIR.getMessage(FileSystemMasterTest.NESTED_URI));
        // cannot free directory with recursive argument to false
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI.getParent(), FreeContext.mergeFrom(FreePOptions.newBuilder().setRecursive(false)));
    }

    /**
     * Tests the {@link FileSystemMaster#free} method with a directory.
     */
    @Test
    public void freeDir() throws Exception {
        mNestedFileContext.setPersisted(true);
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        Assert.assertEquals(1, mBlockMaster.getBlockInfo(blockId).getLocations().size());
        // free the dir
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI.getParent(), FreeContext.mergeFrom(FreePOptions.newBuilder().setForced(true).setRecursive(true)));
        // Update the heartbeat of removedBlockId received from worker 1.
        Command heartbeat3 = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat3);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests the {@link FileSystemMaster#free} method with a directory with a file non-persisted.
     */
    @Test
    public void freeDirWithNonPersistedFile() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mThrown.expect(UnexpectedAlluxioException.class);
        mThrown.expectMessage(CANNOT_FREE_NON_PERSISTED_FILE.getMessage(FileSystemMasterTest.NESTED_FILE_URI.getPath()));
        // cannot free the parent dir of a non-persisted file
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI.getParent(), FreeContext.mergeFrom(FreePOptions.newBuilder().setForced(false).setRecursive(true)));
    }

    /**
     * Tests the {@link FileSystemMaster#free} method with a directory with a file pinned when
     * forced flag is false.
     */
    @Test
    public void freeDirWithPinnedFileAndNotForced() throws Exception {
        mNestedFileContext.setPersisted(true);
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPinned(true)));
        mThrown.expect(UnexpectedAlluxioException.class);
        mThrown.expectMessage(CANNOT_FREE_PINNED_FILE.getMessage(FileSystemMasterTest.NESTED_FILE_URI.getPath()));
        // cannot free the parent dir of a pinned file without "forced"
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI.getParent(), FreeContext.mergeFrom(FreePOptions.newBuilder().setForced(false).setRecursive(true)));
    }

    /**
     * Tests the {@link FileSystemMaster#free} method with a directory with a file pinned when
     * forced flag is true.
     */
    @Test
    public void freeDirWithPinnedFileAndForced() throws Exception {
        mNestedFileContext.setPersisted(true);
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        mFileSystemMaster.setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPinned(true)));
        // free the parent dir of a pinned file with "forced"
        mFileSystemMaster.free(FileSystemMasterTest.NESTED_FILE_URI.getParent(), FreeContext.mergeFrom(FreePOptions.newBuilder().setForced(true).setRecursive(true)));
        // Update the heartbeat of removedBlockId received from worker 1.
        Command heartbeat = mBlockMaster.workerHeartbeat(mWorkerId1, null, ImmutableMap.of("MEM", ((long) (KB))), ImmutableList.of(blockId), ImmutableMap.<String, List<Long>>of(), mMetrics);
        // Verify the muted Free command on worker1.
        Assert.assertEquals(Command.newBuilder().setCommandType(Nothing).build(), heartbeat);
        Assert.assertEquals(0, mBlockMaster.getBlockInfo(blockId).getLocations().size());
    }

    /**
     * Tests the {@link FileSystemMaster#mount(AlluxioURI, AlluxioURI, MountContext)} method.
     */
    @Test
    public void mount() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.defaults());
    }

    /**
     * Tests mounting an existing dir.
     */
    @Test
    public void mountExistingDir() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        mFileSystemMaster.createDirectory(alluxioURI, CreateDirectoryContext.defaults());
        mThrown.expect(InvalidPathException.class);
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.defaults());
    }

    /**
     * Tests mounting to an Alluxio path whose parent dir does not exist.
     */
    @Test
    public void mountNonExistingParentDir() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/non-existing/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.defaults());
    }

    /**
     * Tests a readOnly mount for the create directory op.
     */
    @Test
    public void mountReadOnlyCreateDirectory() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.mergeFrom(MountPOptions.newBuilder().setReadOnly(true)));
        mThrown.expect(AccessControlException.class);
        AlluxioURI path = new AlluxioURI("/hello/dir1");
        mFileSystemMaster.createDirectory(path, CreateDirectoryContext.defaults());
    }

    /**
     * Tests a readOnly mount for the create file op.
     */
    @Test
    public void mountReadOnlyCreateFile() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.mergeFrom(MountPOptions.newBuilder().setReadOnly(true)));
        mThrown.expect(AccessControlException.class);
        AlluxioURI path = new AlluxioURI("/hello/file1");
        mFileSystemMaster.createFile(path, CreateFileContext.defaults());
    }

    /**
     * Tests a readOnly mount for the delete op.
     */
    @Test
    public void mountReadOnlyDeleteFile() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        createTempUfsFile("ufs/hello/file1");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.mergeFrom(MountPOptions.newBuilder().setReadOnly(true)));
        mThrown.expect(AccessControlException.class);
        AlluxioURI path = new AlluxioURI("/hello/file1");
        mFileSystemMaster.delete(path, DeleteContext.defaults());
    }

    /**
     * Tests a readOnly mount for the rename op.
     */
    @Test
    public void mountReadOnlyRenameFile() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        createTempUfsFile("ufs/hello/file1");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.mergeFrom(MountPOptions.newBuilder().setReadOnly(true)));
        mThrown.expect(AccessControlException.class);
        AlluxioURI src = new AlluxioURI("/hello/file1");
        AlluxioURI dst = new AlluxioURI("/hello/file2");
        mFileSystemMaster.rename(src, dst, RenameContext.defaults());
    }

    /**
     * Tests a readOnly mount for the set attribute op.
     */
    @Test
    public void mountReadOnlySetAttribute() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        createTempUfsFile("ufs/hello/file1");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.mergeFrom(MountPOptions.newBuilder().setReadOnly(true)));
        mThrown.expect(AccessControlException.class);
        AlluxioURI path = new AlluxioURI("/hello/file1");
        mFileSystemMaster.setAttribute(path, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setOwner("owner")));
    }

    /**
     * Tests mounting a shadow Alluxio dir.
     */
    @Test
    public void mountShadowDir() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello/shadow");
        mFileSystemMaster.mount(alluxioURI, ufsURI.getParent(), MountContext.defaults());
        AlluxioURI shadowAlluxioURI = new AlluxioURI("/hello/shadow");
        AlluxioURI notShadowAlluxioURI = new AlluxioURI("/hello/notshadow");
        AlluxioURI shadowUfsURI = createTempUfsDir("ufs/hi");
        AlluxioURI notShadowUfsURI = createTempUfsDir("ufs/notshadowhi");
        mFileSystemMaster.mount(notShadowAlluxioURI, notShadowUfsURI, MountContext.defaults());
        mThrown.expect(IOException.class);
        mFileSystemMaster.mount(shadowAlluxioURI, shadowUfsURI, MountContext.defaults());
    }

    /**
     * Tests mounting a prefix UFS dir.
     */
    @Test
    public void mountPrefixUfsDir() throws Exception {
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello/shadow");
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.defaults());
        AlluxioURI preUfsURI = ufsURI.getParent();
        AlluxioURI anotherAlluxioURI = new AlluxioURI("/hi");
        mThrown.expect(InvalidPathException.class);
        mFileSystemMaster.mount(anotherAlluxioURI, preUfsURI, MountContext.defaults());
    }

    /**
     * Tests mounting a suffix UFS dir.
     */
    @Test
    public void mountSuffixUfsDir() throws Exception {
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello/shadow");
        AlluxioURI preUfsURI = ufsURI.getParent();
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        mFileSystemMaster.mount(alluxioURI, preUfsURI, MountContext.defaults());
        AlluxioURI anotherAlluxioURI = new AlluxioURI("/hi");
        mThrown.expect(InvalidPathException.class);
        mFileSystemMaster.mount(anotherAlluxioURI, ufsURI, MountContext.defaults());
    }

    /**
     * Tests unmount operation.
     */
    @Test
    public void unmount() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.defaults());
        mFileSystemMaster.createDirectory(alluxioURI.join("dir"), CreateDirectoryContext.defaults().setPersisted(true));
        mFileSystemMaster.unmount(alluxioURI);
        // after unmount, ufs path under previous mount point should still exist
        File file = new File(ufsURI.join("dir").toString());
        Assert.assertTrue(file.exists());
        // after unmount, alluxio path under previous mount point should not exist
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.getFileInfo(alluxioURI.join("dir"), FileSystemMasterTest.GET_STATUS_CONTEXT);
    }

    /**
     * Tests unmount operation failed when unmounting root.
     */
    @Test
    public void unmountRootWithException() throws Exception {
        mThrown.expect(InvalidPathException.class);
        mFileSystemMaster.unmount(new AlluxioURI("/"));
    }

    /**
     * Tests unmount operation failed when unmounting non-mount point.
     */
    @Test
    public void unmountNonMountPointWithException() throws Exception {
        AlluxioURI alluxioURI = new AlluxioURI("/hello");
        AlluxioURI ufsURI = createTempUfsDir("ufs/hello");
        mFileSystemMaster.mount(alluxioURI, ufsURI, MountContext.defaults());
        AlluxioURI dirURI = alluxioURI.join("dir");
        mFileSystemMaster.createDirectory(dirURI, CreateDirectoryContext.defaults().setPersisted(true));
        mThrown.expect(InvalidPathException.class);
        mFileSystemMaster.unmount(dirURI);
    }

    /**
     * Tests unmount operation failed when unmounting non-existing dir.
     */
    @Test
    public void unmountNonExistingPathWithException() throws Exception {
        mThrown.expect(FileDoesNotExistException.class);
        mFileSystemMaster.unmount(new AlluxioURI("/FileNotExists"));
    }

    /**
     * Tests the {@link DefaultFileSystemMaster#stop()} method.
     */
    @Test
    public void stop() throws Exception {
        mRegistry.stop();
        Assert.assertTrue(mExecutorService.isShutdown());
        Assert.assertTrue(mExecutorService.isTerminated());
    }

    /**
     * Tests the {@link FileSystemMaster#workerHeartbeat} method.
     */
    @Test
    public void workerHeartbeat() throws Exception {
        long blockId = createFileWithSingleBlock(FileSystemMasterTest.ROOT_FILE_URI);
        long fileId = mFileSystemMaster.getFileId(FileSystemMasterTest.ROOT_FILE_URI);
        mFileSystemMaster.scheduleAsyncPersistence(FileSystemMasterTest.ROOT_FILE_URI);
        FileSystemCommand command = mFileSystemMaster.workerHeartbeat(mWorkerId1, Lists.newArrayList(fileId), WorkerHeartbeatContext.defaults());
        Assert.assertEquals(Persist, command.getCommandType());
        Assert.assertEquals(0, command.getCommandOptions().getPersistOptions().getFilesToPersist().size());
    }

    /**
     * Tests that lost files can successfully be detected.
     */
    @Test
    public void lostFilesDetection() throws Exception {
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        long fileId = mFileSystemMaster.getFileId(FileSystemMasterTest.NESTED_FILE_URI);
        FileInfo fileInfo = mFileSystemMaster.getFileInfo(fileId);
        mBlockMaster.reportLostBlocks(fileInfo.getBlockIds());
        Assert.assertEquals(NOT_PERSISTED.name(), fileInfo.getPersistenceState());
        // Check with getPersistenceState.
        Assert.assertEquals(NOT_PERSISTED, mFileSystemMaster.getPersistenceState(fileId));
        // run the detector
        HeartbeatScheduler.execute(MASTER_LOST_FILES_DETECTION);
        fileInfo = mFileSystemMaster.getFileInfo(fileId);
        Assert.assertEquals(LOST.name(), fileInfo.getPersistenceState());
        // Check with getPersistenceState.
        Assert.assertEquals(LOST, mFileSystemMaster.getPersistenceState(fileId));
    }

    /**
     * Tests load metadata logic.
     */
    @Test
    public void testLoadMetadata() throws Exception {
        FileUtils.createDir(Paths.get(mUnderFS).resolve("a").toString());
        mFileSystemMaster.loadMetadata(new AlluxioURI("alluxio:/a"), LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(true)));
        mFileSystemMaster.loadMetadata(new AlluxioURI("alluxio:/a"), LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(true)));
        // TODO(peis): Avoid this hack by adding an option in getFileInfo to skip loading metadata.
        try {
            mFileSystemMaster.createDirectory(new AlluxioURI("alluxio:/a"), CreateDirectoryContext.defaults());
            Assert.fail("createDirectory was expected to fail with FileAlreadyExistsException");
        } catch (FileAlreadyExistsException e) {
            Assert.assertEquals(FILE_ALREADY_EXISTS.getMessage(new AlluxioURI("alluxio:/a")), e.getMessage());
        }
        FileUtils.createFile(Paths.get(mUnderFS).resolve("a/f1").toString());
        FileUtils.createFile(Paths.get(mUnderFS).resolve("a/f2").toString());
        mFileSystemMaster.loadMetadata(new AlluxioURI("alluxio:/a/f1"), LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(true)));
        // This should not throw file exists exception those a/f1 is loaded.
        mFileSystemMaster.loadMetadata(new AlluxioURI("alluxio:/a"), LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(false).setLoadDescendantType(ONE)));
        // TODO(peis): Avoid this hack by adding an option in getFileInfo to skip loading metadata.
        try {
            mFileSystemMaster.createFile(new AlluxioURI("alluxio:/a/f2"), CreateFileContext.defaults());
            Assert.fail("createDirectory was expected to fail with FileAlreadyExistsException");
        } catch (FileAlreadyExistsException e) {
            Assert.assertEquals(FILE_ALREADY_EXISTS.getMessage(new AlluxioURI("alluxio:/a/f2")), e.getMessage());
        }
        mFileSystemMaster.loadMetadata(new AlluxioURI("alluxio:/a"), LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(false).setLoadDescendantType(ONE)));
    }

    /**
     * Tests ufs load with ACL.
     * Currently, it respects the ufs permissions instead of the default ACL for loadMetadata.
     * We may change that in the future, and change this test.
     * TODO(david): update this test when we respect default ACL for loadmetadata
     */
    @Test
    public void loadMetadataWithACL() throws Exception {
        FileUtils.createDir(Paths.get(mUnderFS).resolve("a").toString());
        AlluxioURI uri = new AlluxioURI("/a");
        mFileSystemMaster.loadMetadata(uri, LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(true)));
        List<AclEntry> aclEntryList = new ArrayList<>();
        aclEntryList.add(AclEntry.fromCliString("default:user::r-x"));
        mFileSystemMaster.setAcl(uri, MODIFY, aclEntryList, SetAclContext.defaults());
        FileInfo infoparent = mFileSystemMaster.getFileInfo(uri, GetStatusContext.defaults());
        FileUtils.createFile(Paths.get(mUnderFS).resolve("a/b/file1").toString());
        uri = new AlluxioURI("/a/b/file1");
        mFileSystemMaster.loadMetadata(uri, LoadMetadataContext.mergeFrom(LoadMetadataPOptions.newBuilder().setCreateAncestors(true)));
        FileInfo info = mFileSystemMaster.getFileInfo(uri, GetStatusContext.defaults());
        Assert.assertTrue(info.convertAclToStringEntries().contains("user::r-x"));
    }

    /**
     * Tests load root metadata. It should not fail.
     */
    @Test
    public void loadRoot() throws Exception {
        mFileSystemMaster.loadMetadata(new AlluxioURI("alluxio:/"), LoadMetadataContext.defaults());
    }

    @Test
    public void getUfsInfo() throws Exception {
        FileInfo alluxioRootInfo = mFileSystemMaster.getFileInfo(new AlluxioURI("alluxio://"), FileSystemMasterTest.GET_STATUS_CONTEXT);
        UfsInfo ufsRootInfo = mFileSystemMaster.getUfsInfo(alluxioRootInfo.getMountId());
        Assert.assertEquals(mUnderFS, ufsRootInfo.getUri().getPath());
        Assert.assertTrue(ufsRootInfo.getMountOptions().getPropertiesMap().isEmpty());
    }

    @Test
    public void getUfsInfoNotExist() throws Exception {
        UfsInfo noSuchUfsInfo = mFileSystemMaster.getUfsInfo(100L);
        Assert.assertNull(noSuchUfsInfo.getUri());
        Assert.assertNull(noSuchUfsInfo.getMountOptions());
    }

    /**
     * Tests that setting the ufs fingerprint persists across restarts.
     */
    @Test
    public void setUfsFingerprintReplay() throws Exception {
        String fingerprint = "FINGERPRINT";
        createFileWithSingleBlock(FileSystemMasterTest.NESTED_FILE_URI);
        ((DefaultFileSystemMaster) (mFileSystemMaster)).setAttribute(FileSystemMasterTest.NESTED_FILE_URI, SetAttributeContext.defaults().setUfsFingerprint(fingerprint));
        // Simulate restart.
        stopServices();
        startServices();
        Assert.assertEquals(fingerprint, mFileSystemMaster.getFileInfo(FileSystemMasterTest.NESTED_FILE_URI, GetStatusContext.defaults()).getUfsFingerprint());
    }

    @Test
    public void ignoreInvalidFiles() throws Exception {
        FileUtils.createDir(Paths.get(mUnderFS, "test").toString());
        FileUtils.createFile(Paths.get(mUnderFS, "test", "a?b=C").toString());
        FileUtils.createFile(Paths.get(mUnderFS, "test", "valid").toString());
        List<FileInfo> listing = mFileSystemMaster.listStatus(new AlluxioURI("/test"), ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(ALWAYS).setRecursive(true)));
        Assert.assertEquals(1, listing.size());
        Assert.assertEquals("valid", listing.get(0).getName());
    }

    @Test
    public void propagatePersisted() throws Exception {
        AlluxioURI nestedFile = new AlluxioURI("/nested1/nested2/file");
        AlluxioURI parent1 = new AlluxioURI("/nested1/");
        AlluxioURI parent2 = new AlluxioURI("/nested1/nested2/");
        createFileWithSingleBlock(nestedFile);
        // Nothing is persisted yet.
        Assert.assertEquals(NOT_PERSISTED.toString(), mFileSystemMaster.getFileInfo(nestedFile, GetStatusContext.defaults()).getPersistenceState());
        Assert.assertEquals(NOT_PERSISTED.toString(), mFileSystemMaster.getFileInfo(parent1, GetStatusContext.defaults()).getPersistenceState());
        Assert.assertEquals(NOT_PERSISTED.toString(), mFileSystemMaster.getFileInfo(parent2, GetStatusContext.defaults()).getPersistenceState());
        // Persist the file.
        mFileSystemMaster.setAttribute(nestedFile, SetAttributeContext.mergeFrom(SetAttributePOptions.newBuilder().setPersisted(true)));
        // Everything component should be persisted.
        Assert.assertEquals(PERSISTED.toString(), mFileSystemMaster.getFileInfo(nestedFile, GetStatusContext.defaults()).getPersistenceState());
        Assert.assertEquals(PERSISTED.toString(), mFileSystemMaster.getFileInfo(parent1, GetStatusContext.defaults()).getPersistenceState());
        Assert.assertEquals(PERSISTED.toString(), mFileSystemMaster.getFileInfo(parent2, GetStatusContext.defaults()).getPersistenceState());
        // Simulate restart.
        stopServices();
        startServices();
        // Everything component should be persisted.
        Assert.assertEquals(PERSISTED.toString(), mFileSystemMaster.getFileInfo(nestedFile, GetStatusContext.defaults()).getPersistenceState());
        Assert.assertEquals(PERSISTED.toString(), mFileSystemMaster.getFileInfo(parent1, GetStatusContext.defaults()).getPersistenceState());
        Assert.assertEquals(PERSISTED.toString(), mFileSystemMaster.getFileInfo(parent2, GetStatusContext.defaults()).getPersistenceState());
    }
}

