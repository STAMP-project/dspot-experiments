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
import alluxio.exception.status.UnavailableException;
import alluxio.grpc.FileSystemCommand;
import alluxio.grpc.FileSystemHeartbeatPOptions;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link FileWorkerMasterSyncExecutor}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileDataManager.class, FileSystemMasterClient.class })
public final class FileWorkerMasterSyncExecutorTest {
    private FileDataManager mFileDataManager;

    private FileSystemMasterClient mFileSystemMasterClient;

    private FileWorkerMasterSyncExecutor mFileWorkerMasterSyncExecutor;

    /**
     * {@link FileDataManager#clearPersistedFiles(java.util.List)} is not called when the heartbeat
     * of {@link FileSystemMasterClient} fails.
     */
    @Test
    public void heartbeatFailure() throws Exception {
        List<Long> persistedFiles = Lists.newArrayList(1L);
        List<String> ufsFingerprintList = Lists.newArrayList("ufs fingerprint");
        FileDataManager.PersistedFilesInfo filesInfo = new FileDataManager.PersistedFilesInfo(persistedFiles, ufsFingerprintList);
        Mockito.when(mFileDataManager.getPersistedFileInfos()).thenReturn(filesInfo);
        // first time fails, second time passes
        Mockito.when(mFileSystemMasterClient.heartbeat(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(persistedFiles), ArgumentMatchers.any(FileSystemHeartbeatPOptions.class))).thenThrow(new UnavailableException("failure"));
        mFileWorkerMasterSyncExecutor.heartbeat();
        Mockito.verify(mFileDataManager, Mockito.never()).clearPersistedFiles(persistedFiles);
    }

    /**
     * Verifies {@link FileDataManager#clearPersistedFiles(java.util.List)} is called when the
     * heartbeat is successful.
     */
    @Test
    public void heartbeat() throws Exception {
        List<Long> persistedFiles = Lists.newArrayList(1L);
        List<String> ufsFingerprintList = Lists.newArrayList("ufs fingerprint");
        FileDataManager.PersistedFilesInfo filesInfo = new FileDataManager.PersistedFilesInfo(persistedFiles, ufsFingerprintList);
        Mockito.when(mFileDataManager.getPersistedFileInfos()).thenReturn(filesInfo);
        // first time fails, second time passes
        Mockito.when(mFileSystemMasterClient.heartbeat(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(persistedFiles))).thenReturn(FileSystemCommand.newBuilder().build());
        mFileWorkerMasterSyncExecutor.heartbeat();
        Mockito.verify(mFileDataManager).clearPersistedFiles(persistedFiles);
    }
}

