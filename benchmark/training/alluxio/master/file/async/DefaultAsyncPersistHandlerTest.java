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
package alluxio.master.file.async;


import alluxio.AlluxioURI;
import alluxio.exception.FileDoesNotExistException;
import alluxio.master.file.FileSystemMaster;
import alluxio.wire.BlockInfo;
import alluxio.wire.BlockLocation;
import alluxio.wire.FileBlockInfo;
import alluxio.wire.FileInfo;
import alluxio.wire.PersistFile;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link DefaultAsyncPersistHandler}.
 */
public class DefaultAsyncPersistHandlerTest {
    private FileSystemMaster mFileSystemMaster;

    @Test
    public void scheduleAsyncPersist() throws Exception {
        DefaultAsyncPersistHandler handler = new DefaultAsyncPersistHandler(new alluxio.master.file.meta.FileSystemMasterView(mFileSystemMaster));
        AlluxioURI path = new AlluxioURI("/test");
        long blockId = 0;
        long workerId = 1;
        long fileId = 2;
        List<FileBlockInfo> blockInfoList = new ArrayList<>();
        BlockLocation location = new BlockLocation().setWorkerId(workerId);
        blockInfoList.add(new FileBlockInfo().setBlockInfo(new BlockInfo().setBlockId(blockId).setLocations(Lists.newArrayList(location))));
        Mockito.when(mFileSystemMaster.getFileBlockInfoList(path)).thenReturn(blockInfoList);
        Mockito.when(mFileSystemMaster.getFileId(path)).thenReturn(fileId);
        Mockito.when(mFileSystemMaster.getPath(fileId)).thenReturn(path);
        Mockito.when(mFileSystemMaster.getFileInfo(fileId)).thenReturn(new FileInfo().setLength(1).setCompleted(true));
        handler.scheduleAsyncPersistence(path);
        List<PersistFile> persistFiles = handler.pollFilesToPersist(workerId);
        Assert.assertEquals(1, persistFiles.size());
        Assert.assertEquals(Lists.newArrayList(blockId), persistFiles.get(0).getBlockIds());
    }

    /**
     * Tests the persistence of file with block on multiple workers.
     */
    @Test
    public void persistenceFileWithBlocksOnMultipleWorkers() throws Exception {
        DefaultAsyncPersistHandler handler = new DefaultAsyncPersistHandler(new alluxio.master.file.meta.FileSystemMasterView(mFileSystemMaster));
        AlluxioURI path = new AlluxioURI("/test");
        List<FileBlockInfo> blockInfoList = new ArrayList<>();
        BlockLocation location1 = new BlockLocation().setWorkerId(1);
        blockInfoList.add(new FileBlockInfo().setBlockInfo(new BlockInfo().setLocations(Lists.newArrayList(location1))));
        BlockLocation location2 = new BlockLocation().setWorkerId(2);
        blockInfoList.add(new FileBlockInfo().setBlockInfo(new BlockInfo().setLocations(Lists.newArrayList(location2))));
        long fileId = 2;
        Mockito.when(mFileSystemMaster.getFileId(path)).thenReturn(fileId);
        Mockito.when(mFileSystemMaster.getFileInfo(fileId)).thenReturn(new FileInfo().setLength(1).setCompleted(true));
        Mockito.when(mFileSystemMaster.getFileBlockInfoList(path)).thenReturn(blockInfoList);
        // no persist scheduled on any worker
        Assert.assertEquals(0, handler.pollFilesToPersist(1).size());
        Assert.assertEquals(0, handler.pollFilesToPersist(2).size());
    }

    /**
     * Tests persistence after deletion of files.
     */
    @Test
    public void persistenceFileAfterDeletion() throws Exception {
        DefaultAsyncPersistHandler handler = new DefaultAsyncPersistHandler(new alluxio.master.file.meta.FileSystemMasterView(mFileSystemMaster));
        AlluxioURI path = new AlluxioURI("/test");
        long blockId = 0;
        long workerId = 1;
        long fileId = 2;
        List<FileBlockInfo> blockInfoList = new ArrayList<>();
        BlockLocation location = new BlockLocation().setWorkerId(workerId);
        blockInfoList.add(new FileBlockInfo().setBlockInfo(new BlockInfo().setBlockId(blockId).setLocations(Lists.newArrayList(location))));
        Mockito.when(mFileSystemMaster.getFileBlockInfoList(path)).thenReturn(blockInfoList);
        Mockito.when(mFileSystemMaster.getFileId(path)).thenReturn(fileId);
        Mockito.when(mFileSystemMaster.getPath(fileId)).thenReturn(path);
        Mockito.when(mFileSystemMaster.getFileInfo(fileId)).thenReturn(new FileInfo().setLength(1).setCompleted(true));
        handler.scheduleAsyncPersistence(path);
        Mockito.when(mFileSystemMaster.getFileInfo(fileId)).thenThrow(new FileDoesNotExistException("no file"));
        List<PersistFile> persistFiles = handler.pollFilesToPersist(workerId);
        Assert.assertEquals(0, persistFiles.size());
    }
}

