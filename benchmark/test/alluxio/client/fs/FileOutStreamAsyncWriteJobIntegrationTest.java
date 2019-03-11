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
package alluxio.client.fs;


import PersistenceState.PERSISTED;
import PersistenceState.TO_BE_PERSISTED;
import TtlAction.DELETE;
import UnderFileSystem.Factory;
import alluxio.AlluxioURI;
import alluxio.client.file.URIStatus;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.AlluxioException;
import alluxio.grpc.FileSystemMasterCommonPOptions;
import alluxio.grpc.SetAttributePOptions;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.security.authorization.Mode;
import alluxio.testutils.IntegrationTestUtils;
import alluxio.testutils.PersistenceTestUtils;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.ModeUtils;
import alluxio.util.io.PathUtils;
import alluxio.worker.block.BlockWorker;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Integration tests for {@link FileOutStream} of under storage type being async persist.
 */
public final class FileOutStreamAsyncWriteJobIntegrationTest extends AbstractFileOutStreamIntegrationTest {
    private static final int LEN = 1024;

    private static final FileSystemMasterCommonPOptions COMMON_OPTIONS = FileSystemMasterCommonPOptions.newBuilder().setTtl(12345678L).setTtlAction(DELETE).build();

    private static final SetAttributePOptions TEST_OPTIONS = SetAttributePOptions.newBuilder().setMode(new Mode(((short) (365))).toProto()).setCommonOptions(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS).build();

    private AlluxioURI mUri = new AlluxioURI(PathUtils.uniqPath());

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.WORKER_BLOCK_SYNC);

    @Test
    public void simpleDurableWrite() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        // check the file is completed but not persisted
        Assert.assertEquals(TO_BE_PERSISTED.toString(), status.getPersistenceState());
        Assert.assertTrue(status.isCompleted());
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        status = mFileSystem.getStatus(mUri);
        Assert.assertEquals(PERSISTED.toString(), status.getPersistenceState());
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
    }

    @Test
    public void exists() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        Assert.assertTrue(mFileSystem.exists(mUri));
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        Assert.assertTrue(mFileSystem.exists(mUri));
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        Assert.assertTrue(mFileSystem.exists(mUri));
    }

    @Test
    public void deleteBeforeJobScheduled() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        mFileSystem.delete(mUri);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobComplete(mLocalAlluxioClusterResource, status.getFileId());
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
    }

    @Test
    public void deleteAfterJobScheduled() throws Exception {
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        mFileSystem.delete(mUri);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobComplete(mLocalAlluxioClusterResource, status.getFileId());
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
    }

    @Test
    public void deleteAfterPersist() throws Exception {
        URIStatus status = createAsyncFile();
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        mFileSystem.delete(mUri);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
    }

    @Test
    public void freeBeforeJobScheduled() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        createAsyncFile();
        try {
            mFileSystem.free(mUri);
            Assert.fail("Expect free to fail before file is persisted");
        } catch (AlluxioException e) {
            // Expected
        }
        IntegrationTestUtils.waitForBlocksToBeFreed(mLocalAlluxioClusterResource.get().getWorkerProcess().getWorker(BlockWorker.class));
        URIStatus status = mFileSystem.getStatus(mUri);
        // free for non-persisted file is no-op
        Assert.assertEquals(100, status.getInMemoryPercentage());
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(status.getUfsPath());
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        status = mFileSystem.getStatus(mUri);
        // free for non-persisted file is no-op
        Assert.assertEquals(100, status.getInMemoryPercentage());
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
    }

    @Test
    public void freeAfterJobScheduled() throws Exception {
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        try {
            mFileSystem.free(mUri);
            Assert.fail("Expect free to fail before file is persisted");
        } catch (AlluxioException e) {
            // Expected
        }
        IntegrationTestUtils.waitForBlocksToBeFreed(mLocalAlluxioClusterResource.get().getWorkerProcess().getWorker(BlockWorker.class));
        status = mFileSystem.getStatus(mUri);
        // free for non-persisted file is no-op
        Assert.assertEquals(100, status.getInMemoryPercentage());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        status = mFileSystem.getStatus(mUri);
        // free for non-persisted file is no-op
        Assert.assertEquals(100, status.getInMemoryPercentage());
    }

    @Test
    public void freeAfterFilePersisted() throws Exception {
        URIStatus status = createAsyncFile();
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        mFileSystem.free(mUri);
        IntegrationTestUtils.waitForBlocksToBeFreed(mLocalAlluxioClusterResource.get().getWorkerProcess().getWorker(BlockWorker.class), status.getBlockIds().toArray(new Long[status.getBlockIds().size()]));
        status = mFileSystem.getStatus(mUri);
        // file persisted, free is no more a no-op
        Assert.assertEquals(0, status.getInMemoryPercentage());
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
    }

    @Test
    public void getStatus() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        Assert.assertEquals(TO_BE_PERSISTED.toString(), status.getPersistenceState());
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        Assert.assertEquals(TO_BE_PERSISTED.toString(), status.getPersistenceState());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        URIStatus statusAfter = mFileSystem.getStatus(mUri);
        Assert.assertEquals(PERSISTED.toString(), statusAfter.getPersistenceState());
    }

    @Test
    public void openFile() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
    }

    @Test
    public void renameBeforeJobScheduled() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        AlluxioURI newUri = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri.getParent());
        mFileSystem.rename(mUri, newUri);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
        checkFileNotInUnderStorage(mFileSystem.getStatus(newUri).getUfsPath());
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, newUri);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
    }

    @Test
    public void renameAfterJobScheduled() throws Exception {
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        AlluxioURI newUri = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri.getParent());
        mFileSystem.rename(mUri, newUri);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
        checkFileNotInUnderStorage(mFileSystem.getStatus(newUri).getUfsPath());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, newUri);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
    }

    @Test
    public void renameAfterFilePersisted() throws Exception {
        URIStatus status = createAsyncFile();
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        AlluxioURI newUri = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri.getParent());
        mFileSystem.rename(mUri, newUri);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(status.getUfsPath());
    }

    @Test
    public void setAttributeBeforeJobScheduled() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        String ufsPath = status.getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsPath, ServerConfiguration.global());
        mFileSystem.setAttribute(mUri, FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(status.getUfsPath());
        status = mFileSystem.getStatus(mUri);
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        status = mFileSystem.getStatus(mUri);
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        // Skip checking mode for object stores
        Assume.assumeFalse(ufs.isObjectStorage());
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), ufs.getFileStatus(ufsPath).getMode());
    }

    @Test
    public void setAttributeAfterJobScheduled() throws Exception {
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        String ufsPath = status.getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsPath, ServerConfiguration.global());
        mFileSystem.setAttribute(mUri, FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(status.getUfsPath());
        status = mFileSystem.getStatus(mUri);
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        status = mFileSystem.getStatus(mUri);
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        // Skip checking mode for object stores
        Assume.assumeFalse(ufs.isObjectStorage());
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), ufs.getFileStatus(ufsPath).getMode());
    }

    @Test
    public void setAttributeAfterFilePersisted() throws Exception {
        createAsyncFile();
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, mUri);
        mFileSystem.setAttribute(mUri, FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS);
        checkFileInAlluxio(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(mUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        URIStatus status = mFileSystem.getStatus(mUri);
        String ufsPath = status.getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsPath, ServerConfiguration.global());
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        // Skip checking mode for object stores
        Assume.assumeFalse(ufs.isObjectStorage());
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), ufs.getFileStatus(ufsPath).getMode());
    }

    @Test
    public void renameScheduleRename() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        String ufsPath = status.getUfsPath();
        AlluxioURI newUri1 = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri1.getParent());
        mFileSystem.rename(mUri, newUri1);
        String ufsPath1 = mFileSystem.getStatus(newUri1).getUfsPath();
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri1, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(ufsPath1);
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        AlluxioURI newUri2 = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.rename(newUri1, newUri2);
        String ufsPath2 = mFileSystem.getStatus(newUri2).getUfsPath();
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileNotInAlluxio(newUri1);
        checkFileNotInUnderStorage(ufsPath1);
        checkFileInAlluxio(newUri2, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(ufsPath2);
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, newUri2);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileNotInAlluxio(newUri1);
        checkFileNotInUnderStorage(ufsPath1);
        checkFileInAlluxio(newUri2, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(newUri2, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
    }

    @Test
    public void renameScheduleFree() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        String ufsPath = status.getUfsPath();
        AlluxioURI newUri = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri.getParent());
        mFileSystem.rename(mUri, newUri);
        String newUfsPath = mFileSystem.getStatus(newUri).getUfsPath();
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(newUfsPath);
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        try {
            mFileSystem.free(newUri);
            Assert.fail("Expect free to fail before file is persisted");
        } catch (AlluxioException e) {
            // Expected
        }
        IntegrationTestUtils.waitForBlocksToBeFreed(mLocalAlluxioClusterResource.get().getWorkerProcess().getWorker(BlockWorker.class));
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(newUfsPath);
        // free for non-persisted file is no-op
        Assert.assertEquals(100, mFileSystem.getStatus(newUri).getInMemoryPercentage());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, newUri);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        // free for non-persisted file is no-op
        Assert.assertEquals(100, mFileSystem.getStatus(newUri).getInMemoryPercentage());
    }

    @Test
    public void renameScheduleSetAttribute() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        String ufsPath = status.getUfsPath();
        UnderFileSystem ufs = Factory.create(ufsPath, ServerConfiguration.global());
        AlluxioURI newUri = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri.getParent());
        mFileSystem.rename(mUri, newUri);
        String newUfsPath = mFileSystem.getStatus(newUri).getUfsPath();
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(newUfsPath);
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        mFileSystem.setAttribute(newUri, FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(newUfsPath);
        status = mFileSystem.getStatus(newUri);
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, newUri);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileInUnderStorage(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        status = mFileSystem.getStatus(newUri);
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), status.getMode());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtl(), status.getTtl());
        Assert.assertEquals(FileOutStreamAsyncWriteJobIntegrationTest.COMMON_OPTIONS.getTtlAction(), status.getTtlAction());
        // Skip checking mode for object stores
        Assume.assumeFalse(ufs.isObjectStorage());
        Assert.assertEquals(ModeUtils.protoToShort(FileOutStreamAsyncWriteJobIntegrationTest.TEST_OPTIONS.getMode()), ufs.getFileStatus(newUfsPath).getMode());
    }

    @Test
    public void renameScheduleDelete() throws Exception {
        PersistenceTestUtils.pauseScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.pauseChecker(mLocalAlluxioClusterResource);
        URIStatus status = createAsyncFile();
        String ufsPath = status.getUfsPath();
        AlluxioURI newUri = new AlluxioURI(PathUtils.uniqPath());
        mFileSystem.createDirectory(newUri.getParent());
        mFileSystem.rename(mUri, newUri);
        String newUfsPath = mFileSystem.getStatus(newUri).getUfsPath();
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileInAlluxio(newUri, FileOutStreamAsyncWriteJobIntegrationTest.LEN);
        checkFileNotInUnderStorage(newUfsPath);
        PersistenceTestUtils.resumeScheduler(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobScheduled(mLocalAlluxioClusterResource, status.getFileId());
        mFileSystem.delete(newUri);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(newUfsPath);
        PersistenceTestUtils.resumeChecker(mLocalAlluxioClusterResource);
        PersistenceTestUtils.waitForJobComplete(mLocalAlluxioClusterResource, status.getFileId());
        checkFileNotInAlluxio(mUri);
        checkFileNotInUnderStorage(ufsPath);
        checkFileNotInAlluxio(newUri);
        checkFileNotInUnderStorage(newUfsPath);
    }
}

