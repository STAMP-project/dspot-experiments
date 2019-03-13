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
package alluxio.server.ft.journal.ufs;


import Constants.FILE_SYSTEM_MASTER_NAME;
import Constants.KB;
import LoadMetadataPType.NEVER;
import PropertyKey.MASTER_JOURNAL_CHECKPOINT_PERIOD_ENTRIES;
import PropertyKey.MASTER_JOURNAL_LOG_SIZE_BYTES_MAX;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.Name;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_ENABLED;
import PropertyKey.SECURITY_LOGIN_USERNAME;
import PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT;
import UnderFileSystem.Factory;
import WritePType.CACHE_THROUGH;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.CreateDirectoryPOptions;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.DeletePOptions;
import alluxio.grpc.ListStatusPOptions;
import alluxio.grpc.SetAttributePOptions;
import alluxio.master.LocalAlluxioCluster;
import alluxio.master.MasterRegistry;
import alluxio.master.NoopMaster;
import alluxio.master.file.FileSystemMaster;
import alluxio.master.file.contexts.ListStatusContext;
import alluxio.master.journal.ufs.UfsJournal;
import alluxio.security.authorization.Mode;
import alluxio.security.group.GroupMappingService;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.underfs.UfsStatus;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.IdUtils;
import alluxio.util.io.PathUtils;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test master journal, including checkpoint and entry log.
 */
public class UfsJournalIntegrationTest extends BaseIntegrationTest {
    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(MASTER_JOURNAL_LOG_SIZE_BYTES_MAX, Integer.toString(KB)).setProperty(MASTER_JOURNAL_CHECKPOINT_PERIOD_ENTRIES, "2").setProperty(SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "false").setProperty(USER_FILE_WRITE_TYPE_DEFAULT, "CACHE_THROUGH").build();

    @Rule
    public TemporaryFolder mTestFolder = new TemporaryFolder();

    private LocalAlluxioCluster mLocalAlluxioCluster;

    private FileSystem mFileSystem;

    private AlluxioURI mRootUri = new AlluxioURI(AlluxioURI.SEPARATOR);

    /**
     * Tests adding a block.
     */
    @Test
    public void addBlock() throws Exception {
        AlluxioURI uri = new AlluxioURI("/xyz");
        CreateFilePOptions options = CreateFilePOptions.newBuilder().setBlockSizeBytes(64).setRecursive(true).build();
        FileOutStream os = mFileSystem.createFile(uri, options);
        for (int k = 0; k < 1000; k++) {
            os.write(k);
        }
        os.close();
        URIStatus status = mFileSystem.getStatus(uri);
        mLocalAlluxioCluster.stopFS();
        addBlockTestUtil(status);
    }

    /**
     * Tests flushing the journal multiple times, without writing any data.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.MASTER_JOURNAL_LOG_SIZE_BYTES_MAX, "0" })
    public void multipleFlush() throws Exception {
        String journalFolder = mLocalAlluxioCluster.getLocalAlluxioMaster().getJournalFolder();
        mLocalAlluxioCluster.stop();
        UfsJournal journal = new UfsJournal(new URI(PathUtils.concatPath(journalFolder, FILE_SYSTEM_MASTER_NAME)), new NoopMaster(), 0);
        journal.start();
        journal.gainPrimacy();
        UfsStatus[] paths = Factory.create(journalFolder, ServerConfiguration.global()).listStatus(journal.getLogDir().toString());
        int expectedSize = (paths == null) ? 0 : paths.length;
        journal.flush();
        journal.flush();
        journal.flush();
        journal.close();
        paths = Factory.create(journalFolder, ServerConfiguration.global()).listStatus(journal.getLogDir().toString());
        int actualSize = (paths == null) ? 0 : paths.length;
        // No new files are created.
        Assert.assertEquals(expectedSize, actualSize);
    }

    /**
     * Tests loading metadata.
     */
    @Test
    public void loadMetadata() throws Exception {
        String ufsRoot = ServerConfiguration.get(MASTER_MOUNT_TABLE_ROOT_UFS);
        UnderFileSystem ufs = Factory.createForRoot(ServerConfiguration.global());
        ufs.create((ufsRoot + "/xyz")).close();
        mFileSystem.loadMetadata(new AlluxioURI("/xyz"));
        URIStatus status = mFileSystem.getStatus(new AlluxioURI("/xyz"));
        mLocalAlluxioCluster.stopFS();
        loadMetadataTestUtil(status);
        deleteFsMasterJournalLogs();
        loadMetadataTestUtil(status);
    }

    /**
     * Tests completed edit log deletion.
     */
    @Test
    public void completedEditLogDeletion() throws Exception {
        for (int i = 0; i < 124; i++) {
            mFileSystem.createFile(new AlluxioURI(("/a" + i)), CreateFilePOptions.newBuilder().setBlockSizeBytes((((i + 10) / 10) * 64)).build()).close();
        }
        mLocalAlluxioCluster.stopFS();
        String journalFolder = PathUtils.concatPath(mLocalAlluxioCluster.getLocalAlluxioMaster().getJournalFolder(), FILE_SYSTEM_MASTER_NAME);
        UfsJournal journal = new UfsJournal(new URI(journalFolder), new NoopMaster(), 0);
        URI completedLocation = journal.getLogDir();
        Assert.assertTrue(((Factory.create(completedLocation, ServerConfiguration.global()).listStatus(completedLocation.toString()).length) > 1));
        multiEditLogTestUtil();
        Assert.assertTrue(((Factory.create(completedLocation, ServerConfiguration.global()).listStatus(completedLocation.toString()).length) > 1));
        multiEditLogTestUtil();
    }

    /**
     * Tests file and directory creation and deletion.
     */
    @Test
    public void delete() throws Exception {
        CreateDirectoryPOptions recMkdir = CreateDirectoryPOptions.newBuilder().setRecursive(true).build();
        DeletePOptions recDelete = DeletePOptions.newBuilder().setRecursive(true).build();
        for (int i = 0; i < 10; i++) {
            String dirPath = "/i" + i;
            mFileSystem.createDirectory(new AlluxioURI(dirPath), recMkdir);
            for (int j = 0; j < 10; j++) {
                CreateFilePOptions option = CreateFilePOptions.newBuilder().setBlockSizeBytes((((i + j) + 1) * 64)).build();
                String filePath = (dirPath + "/j") + j;
                mFileSystem.createFile(new AlluxioURI(filePath), option).close();
                if (j >= 5) {
                    mFileSystem.delete(new AlluxioURI(filePath), recDelete);
                }
            }
            if (i >= 5) {
                mFileSystem.delete(new AlluxioURI(dirPath), recDelete);
            }
        }
        mLocalAlluxioCluster.stopFS();
        deleteTestUtil();
        deleteFsMasterJournalLogs();
        deleteTestUtil();
    }

    @Test
    public void emptyFileSystem() throws Exception {
        Assert.assertEquals(0, mFileSystem.listStatus(mRootUri).size());
        mLocalAlluxioCluster.stopFS();
        MasterRegistry registry = createFsMasterFromJournal();
        FileSystemMaster fsMaster = registry.get(FileSystemMaster.class);
        long rootId = fsMaster.getFileId(mRootUri);
        Assert.assertTrue((rootId != (IdUtils.INVALID_FILE_ID)));
        Assert.assertEquals(0, fsMaster.listStatus(mRootUri, ListStatusContext.mergeFrom(ListStatusPOptions.newBuilder().setLoadMetadataType(NEVER))).size());
        registry.stop();
    }

    /**
     * Tests file and directory creation.
     */
    @Test
    public void fileDirectory() throws Exception {
        for (int i = 0; i < 10; i++) {
            mFileSystem.createDirectory(new AlluxioURI(("/i" + i)));
            for (int j = 0; j < 10; j++) {
                CreateFilePOptions option = CreateFilePOptions.newBuilder().setBlockSizeBytes((((i + j) + 1) * 64)).build();
                mFileSystem.createFile(new AlluxioURI(((("/i" + i) + "/j") + j)), option).close();
            }
        }
        mLocalAlluxioCluster.stopFS();
        fileDirectoryTestUtil();
        deleteFsMasterJournalLogs();
        fileDirectoryTestUtil();
    }

    /**
     * Tests file creation.
     */
    @Test
    public void file() throws Exception {
        CreateFilePOptions option = CreateFilePOptions.newBuilder().setBlockSizeBytes(64).build();
        AlluxioURI filePath = new AlluxioURI("/xyz");
        mFileSystem.createFile(filePath, option).close();
        URIStatus status = mFileSystem.getStatus(filePath);
        mLocalAlluxioCluster.stopFS();
        fileTestUtil(status);
        deleteFsMasterJournalLogs();
        fileTestUtil(status);
    }

    /**
     * Tests journalling of inodes being pinned.
     */
    @Test
    public void pin() throws Exception {
        SetAttributePOptions setPinned = SetAttributePOptions.newBuilder().setPinned(true).build();
        SetAttributePOptions setUnpinned = SetAttributePOptions.newBuilder().setPinned(false).build();
        AlluxioURI dirUri = new AlluxioURI("/myFolder");
        mFileSystem.createDirectory(dirUri);
        mFileSystem.setAttribute(dirUri, setPinned);
        AlluxioURI file0Path = new AlluxioURI("/myFolder/file0");
        CreateFilePOptions op = CreateFilePOptions.newBuilder().setBlockSizeBytes(64).build();
        mFileSystem.createFile(file0Path, op).close();
        mFileSystem.setAttribute(file0Path, setUnpinned);
        AlluxioURI file1Path = new AlluxioURI("/myFolder/file1");
        mFileSystem.createFile(file1Path, op).close();
        URIStatus directoryStatus = mFileSystem.getStatus(dirUri);
        URIStatus file0Status = mFileSystem.getStatus(file0Path);
        URIStatus file1Status = mFileSystem.getStatus(file1Path);
        mLocalAlluxioCluster.stopFS();
        pinTestUtil(directoryStatus, file0Status, file1Status);
        deleteFsMasterJournalLogs();
        pinTestUtil(directoryStatus, file0Status, file1Status);
    }

    /**
     * Tests directory creation.
     */
    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.MASTER_PERSISTENCE_INITIAL_WAIT_TIME_MS, "0" })
    public void directory() throws Exception {
        AlluxioURI directoryPath = new AlluxioURI("/xyz");
        mFileSystem.createDirectory(directoryPath);
        URIStatus status = mFileSystem.getStatus(directoryPath);
        mLocalAlluxioCluster.stopFS();
        directoryTestUtil(status);
        deleteFsMasterJournalLogs();
        directoryTestUtil(status);
    }

    /**
     * Tests journalling of creating directories with MUST_CACHE, then creating the same directories
     * again with CACHE_THROUGH and AllowExists=true.
     */
    @Test
    public void persistDirectoryLater() throws Exception {
        String[] directories = new String[]{ "/d11", "/d11/d21", "/d11/d22", "/d12", "/d12/d21", "/d12/d22" };
        CreateDirectoryPOptions options = CreateDirectoryPOptions.newBuilder().setRecursive(true).setWriteType(MUST_CACHE).build();
        for (String directory : directories) {
            mFileSystem.createDirectory(new AlluxioURI(directory), options);
        }
        options = options.toBuilder().setWriteType(CACHE_THROUGH).setAllowExists(true).build();
        for (String directory : directories) {
            mFileSystem.createDirectory(new AlluxioURI(directory), options);
        }
        Map<String, URIStatus> directoryStatuses = new HashMap<>();
        for (String directory : directories) {
            directoryStatuses.put(directory, mFileSystem.getStatus(new AlluxioURI(directory)));
        }
        mLocalAlluxioCluster.stopFS();
        persistDirectoryLaterTestUtil(directoryStatuses);
        deleteFsMasterJournalLogs();
        persistDirectoryLaterTestUtil(directoryStatuses);
    }

    /**
     * Tests files creation.
     */
    @Test
    public void manyFile() throws Exception {
        for (int i = 0; i < 10; i++) {
            CreateFilePOptions option = CreateFilePOptions.newBuilder().setBlockSizeBytes(((i + 1) * 64)).build();
            mFileSystem.createFile(new AlluxioURI(("/a" + i)), option).close();
        }
        mLocalAlluxioCluster.stopFS();
        manyFileTestUtil();
        deleteFsMasterJournalLogs();
        manyFileTestUtil();
    }

    /**
     * Tests reading multiple edit logs.
     */
    @Test
    public void multiEditLog() throws Exception {
        for (int i = 0; i < 124; i++) {
            CreateFilePOptions op = CreateFilePOptions.newBuilder().setBlockSizeBytes((((i + 10) / 10) * 64)).build();
            mFileSystem.createFile(new AlluxioURI(("/a" + i)), op).close();
        }
        mLocalAlluxioCluster.stopFS();
        multiEditLogTestUtil();
        deleteFsMasterJournalLogs();
        multiEditLogTestUtil();
    }

    /**
     * Tests file and directory creation, and rename.
     */
    @Test
    public void rename() throws Exception {
        for (int i = 0; i < 10; i++) {
            mFileSystem.createDirectory(new AlluxioURI(("/i" + i)));
            for (int j = 0; j < 10; j++) {
                CreateFilePOptions option = CreateFilePOptions.newBuilder().setBlockSizeBytes((((i + j) + 1) * 64)).build();
                AlluxioURI path = new AlluxioURI(((("/i" + i) + "/j") + j));
                mFileSystem.createFile(path, option).close();
                mFileSystem.rename(path, new AlluxioURI(((("/i" + i) + "/jj") + j)));
            }
            mFileSystem.rename(new AlluxioURI(("/i" + i)), new AlluxioURI(("/ii" + i)));
        }
        mLocalAlluxioCluster.stopFS();
        renameTestUtil();
        deleteFsMasterJournalLogs();
        renameTestUtil();
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.SECURITY_AUTHENTICATION_TYPE, "SIMPLE", Name.SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true", Name.SECURITY_GROUP_MAPPING_CLASS, UfsJournalIntegrationTest.FakeUserGroupsMapping.FULL_CLASS_NAME })
    public void setAcl() throws Exception {
        AlluxioURI filePath = new AlluxioURI("/file");
        String user = "alluxio";
        ServerConfiguration.set(SECURITY_LOGIN_USERNAME, user);
        CreateFilePOptions op = CreateFilePOptions.newBuilder().setBlockSizeBytes(64).build();
        mFileSystem.createFile(filePath, op).close();
        // TODO(chaomin): also setOwner and setGroup once there's a way to fake the owner/group in UFS.
        mFileSystem.setAttribute(filePath, SetAttributePOptions.newBuilder().setMode(new Mode(((short) (256))).toProto()).setRecursive(false).build());
        URIStatus status = mFileSystem.getStatus(filePath);
        mLocalAlluxioCluster.stopFS();
        aclTestUtil(status, user);
        deleteFsMasterJournalLogs();
        aclTestUtil(status, user);
    }

    /**
     * Test class implements {@link GroupMappingService} providing user-to-groups mapping.
     */
    public static class FakeUserGroupsMapping implements GroupMappingService {
        // The fullly qualified class name of this group mapping service. This is needed to configure
        // the alluxio cluster
        public static final String FULL_CLASS_NAME = "alluxio.server.ft.journal.ufs.UfsJournalIntegrationTest$FakeUserGroupsMapping";

        private HashMap<String, String> mUserGroups = new HashMap<>();

        /**
         * Constructor of {@link FakeUserGroupsMapping} to put the user and groups in user-to-groups
         * HashMap.
         */
        public FakeUserGroupsMapping() {
            mUserGroups.put("alluxio", "supergroup");
            mUserGroups.put("user1", "group1");
            mUserGroups.put("others", "anygroup");
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            if (mUserGroups.containsKey(user)) {
                return Lists.newArrayList(mUserGroups.get(user).split(","));
            }
            return Lists.newArrayList(mUserGroups.get("others").split(","));
        }
    }
}

