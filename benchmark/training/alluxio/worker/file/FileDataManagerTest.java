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
package alluxio.worker.file;


import FileDataManager.PersistedFilesInfo;
import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import Sessions.CHECKPOINT_SESSION_ID;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.BlockDoesNotExistException;
import alluxio.exception.InvalidWorkerStateException;
import alluxio.underfs.UfsManager;
import alluxio.underfs.UnderFileSystem;
import alluxio.underfs.options.CreateOptions;
import alluxio.util.io.PathUtils;
import alluxio.wire.FileInfo;
import alluxio.worker.block.BlockWorker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MockRateLimiter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link FileDataManager}.
 */
public final class FileDataManagerTest {
    private UnderFileSystem mUfs;

    private UfsManager mUfsManager;

    private BlockWorker mBlockWorker;

    private MockRateLimiter mMockRateLimiter;

    private FileDataManager mManager;

    private FileSystem mMockFileSystem;

    private AtomicInteger mCopyCounter;

    /**
     * Tests that a file gets persisted.
     */
    @Test
    public void persistFile() throws Exception {
        long fileId = 1;
        List<Long> blockIds = Lists.newArrayList(1L, 2L);
        writeFileWithBlocks(fileId, blockIds);
        // verify file persisted
        FileDataManager.PersistedFilesInfo info = mManager.getPersistedFileInfos();
        Assert.assertEquals(Arrays.asList(fileId), info.idList());
        // verify fastCopy called twice, once per block
        Assert.assertEquals(2, mCopyCounter.get());
        // verify the file is not needed for another persistence
        Assert.assertFalse(mManager.needPersistence(fileId));
    }

    /**
     * Tests that persisted file are cleared in the manager.
     */
    @Test
    public void clearPersistedFiles() throws Exception {
        writeFileWithBlocks(1L, ImmutableList.of(2L, 3L));
        mManager.clearPersistedFiles(ImmutableList.of(1L));
        FileDataManager.PersistedFilesInfo info = mManager.getPersistedFileInfos();
        Assert.assertEquals(Collections.emptyList(), info.idList());
    }

    /**
     * Tests the blocks are unlocked correctly when exception is encountered in
     * {@link FileDataManager#lockBlocks(long, List)}.
     */
    @Test
    public void lockBlocksErrorHandling() throws Exception {
        long fileId = 1;
        List<Long> blockIds = Lists.newArrayList(1L, 2L, 3L);
        Mockito.when(mBlockWorker.lockBlock(CHECKPOINT_SESSION_ID, 1L)).thenReturn(1L);
        Mockito.when(mBlockWorker.lockBlock(CHECKPOINT_SESSION_ID, 2L)).thenReturn(2L);
        Mockito.when(mBlockWorker.lockBlock(CHECKPOINT_SESSION_ID, 3L)).thenThrow(new BlockDoesNotExistException("block 3 does not exist"));
        try {
            mManager.lockBlocks(fileId, blockIds);
            Assert.fail("the lock should fail");
        } catch (IOException e) {
            Assert.assertEquals(("failed to lock all blocks of file 1\n" + "alluxio.exception.BlockDoesNotExistException: block 3 does not exist\n"), e.getMessage());
            // verify the locks are all unlocked
            Mockito.verify(mBlockWorker).unlockBlock(1L);
            Mockito.verify(mBlockWorker).unlockBlock(2L);
        }
    }

    /**
     * Tests that the correct error message is provided when persisting a file fails.
     */
    @Test
    public void errorHandling() throws Exception {
        long fileId = 1;
        List<Long> blockIds = Lists.newArrayList(1L, 2L);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath("test");
        Mockito.when(mBlockWorker.getFileInfo(fileId)).thenReturn(fileInfo);
        for (long blockId : blockIds) {
            Mockito.when(mBlockWorker.lockBlock(CHECKPOINT_SESSION_ID, blockId)).thenReturn(blockId);
            Mockito.doThrow(new InvalidWorkerStateException("invalid worker")).when(mBlockWorker).readBlockRemote(CHECKPOINT_SESSION_ID, blockId, blockId);
        }
        String ufsRoot = ServerConfiguration.get(MASTER_MOUNT_TABLE_ROOT_UFS);
        Mockito.when(mUfs.isDirectory(ufsRoot)).thenReturn(true);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        String dstPath = PathUtils.concatPath(ufsRoot, fileInfo.getPath());
        fileInfo.setUfsPath(dstPath);
        Mockito.when(mUfs.create(dstPath)).thenReturn(outputStream);
        Mockito.when(mUfs.create(ArgumentMatchers.anyString(), ArgumentMatchers.any(CreateOptions.class))).thenReturn(outputStream);
        Mockito.when(mMockFileSystem.getStatus(ArgumentMatchers.any(AlluxioURI.class))).thenReturn(new alluxio.client.file.URIStatus(fileInfo));
        mManager.lockBlocks(fileId, blockIds);
        try {
            mManager.persistFile(fileId, blockIds);
            Assert.fail("the persist should fail");
        } catch (IOException e) {
            Assert.assertEquals(("the blocks of file1 are failed to persist\n" + "alluxio.exception.InvalidWorkerStateException: invalid worker\n"), e.getMessage());
            // verify the locks are all unlocked
            Mockito.verify(mBlockWorker).unlockBlock(1L);
            Mockito.verify(mBlockWorker).unlockBlock(2L);
        }
    }
}

