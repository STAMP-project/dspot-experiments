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
package alluxio.server.tieredstore;


import HeartbeatContext.WORKER_BLOCK_SYNC;
import HeartbeatContext.WORKER_PIN_LIST_SYNC;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.WORKER_FILE_BUFFER_SIZE;
import PropertyKey.WORKER_MEMORY_SIZE;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_HIGH_WATERMARK_RATIO;
import ReadPType.CACHE;
import ReadPType.CACHE_PROMOTE;
import WritePType.CACHE_THROUGH;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.URIStatus;
import alluxio.grpc.OpenFilePOptions;
import alluxio.grpc.SetAttributePOptions;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.io.BufferUtils;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration tests for {@link alluxio.worker.block.meta.StorageTier}.
 */
public class TieredStoreIntegrationTest extends BaseIntegrationTest {
    private static final int MEM_CAPACITY_BYTES = 1000;

    private FileSystem mFileSystem;

    private SetAttributePOptions mSetPinned;

    private SetAttributePOptions mSetUnpinned;

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.MASTER_TTL_CHECK, HeartbeatContext.WORKER_BLOCK_SYNC, HeartbeatContext.WORKER_PIN_LIST_SYNC);

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(WORKER_MEMORY_SIZE, TieredStoreIntegrationTest.MEM_CAPACITY_BYTES).setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, 1000).setProperty(USER_FILE_BUFFER_BYTES, String.valueOf(100)).setProperty(WORKER_FILE_BUFFER_SIZE, String.valueOf(100)).setProperty(WORKER_TIERED_STORE_LEVEL0_HIGH_WATERMARK_RATIO, 0.8).build();

    /**
     * Tests that deletes go through despite failing initially due to concurrent read.
     */
    @Test
    public void deleteWhileRead() throws Exception {
        int fileSize = (TieredStoreIntegrationTest.MEM_CAPACITY_BYTES) / 2;// Small enough not to trigger async eviction

        HeartbeatScheduler.await(WORKER_BLOCK_SYNC, 10, TimeUnit.SECONDS);
        AlluxioURI file = new AlluxioURI("/test1");
        FileSystemTestUtils.createByteFile(mFileSystem, file, MUST_CACHE, fileSize);
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        Assert.assertEquals(100, mFileSystem.getStatus(file).getInAlluxioPercentage());
        // Open the file
        OpenFilePOptions options = OpenFilePOptions.newBuilder().setReadType(CACHE).build();
        FileInStream in = mFileSystem.openFile(file, options);
        Assert.assertEquals(0, in.read());
        // Delete the file
        mFileSystem.delete(file);
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // After the delete, the master should no longer serve the file
        Assert.assertFalse(mFileSystem.exists(file));
        // However, the previous read should still be able to read it as the data still exists
        byte[] res = new byte[fileSize];
        Assert.assertEquals((fileSize - 1), in.read(res, 1, (fileSize - 1)));
        res[0] = 0;
        Assert.assertTrue(BufferUtils.equalIncreasingByteArray(fileSize, res));
        in.close();
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // After the file is closed, the master's delete should go through and new files can be created
        AlluxioURI newFile = new AlluxioURI("/test2");
        FileSystemTestUtils.createByteFile(mFileSystem, newFile, MUST_CACHE, TieredStoreIntegrationTest.MEM_CAPACITY_BYTES);
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        Assert.assertEquals(100, mFileSystem.getStatus(newFile).getInAlluxioPercentage());
    }

    /**
     * Tests that pinning a file prevents it from being evicted.
     */
    @Test
    public void pinFile() throws Exception {
        // Create a file that fills the entire Alluxio store
        AlluxioURI file = new AlluxioURI("/test1");
        // Half of mem capacity to avoid triggering async eviction
        FileSystemTestUtils.createByteFile(mFileSystem, file, MUST_CACHE, ((TieredStoreIntegrationTest.MEM_CAPACITY_BYTES) / 2));
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // Pin the file
        mFileSystem.setAttribute(file, mSetPinned);
        HeartbeatScheduler.execute(WORKER_PIN_LIST_SYNC);
        // Confirm the pin with master
        Assert.assertTrue(mFileSystem.getStatus(file).isPinned());
        // Try to create a file that cannot be stored unless the previous file is evicted, expect an
        // exception since worker cannot serve the request
        mThrown.expect(Exception.class);
        FileSystemTestUtils.createByteFile(mFileSystem, "/test2", MUST_CACHE, TieredStoreIntegrationTest.MEM_CAPACITY_BYTES);
    }

    /**
     * Tests that pinning a file and then unpinning.
     */
    @Test
    public void unpinFile() throws Exception {
        // Create a file that fills the entire Alluxio store
        AlluxioURI file1 = new AlluxioURI("/test1");
        FileSystemTestUtils.createByteFile(mFileSystem, file1, MUST_CACHE, TieredStoreIntegrationTest.MEM_CAPACITY_BYTES);
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // Pin the file
        mFileSystem.setAttribute(file1, mSetPinned);
        HeartbeatScheduler.execute(WORKER_PIN_LIST_SYNC);
        // Confirm the pin with master
        Assert.assertTrue(mFileSystem.getStatus(file1).isPinned());
        // Unpin the file
        mFileSystem.setAttribute(file1, mSetUnpinned);
        HeartbeatScheduler.execute(WORKER_PIN_LIST_SYNC);
        // Confirm the unpin
        Assert.assertFalse(mFileSystem.getStatus(file1).isPinned());
        // Try to create a file that cannot be stored unless the previous file is evicted, this
        // should succeed
        AlluxioURI file2 = new AlluxioURI("/test2");
        FileSystemTestUtils.createByteFile(mFileSystem, file2, MUST_CACHE, TieredStoreIntegrationTest.MEM_CAPACITY_BYTES);
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // File 2 should be in memory and File 1 should be evicted
        Assert.assertEquals(0, mFileSystem.getStatus(file1).getInAlluxioPercentage());
        Assert.assertEquals(100, mFileSystem.getStatus(file2).getInAlluxioPercentage());
    }

    /**
     * Tests the promotion of a file.
     */
    @Test
    public void promoteBlock() throws Exception {
        AlluxioURI uri1 = new AlluxioURI("/file1");
        AlluxioURI uri2 = new AlluxioURI("/file2");
        AlluxioURI uri3 = new AlluxioURI("/file3");
        FileSystemTestUtils.createByteFile(mFileSystem, uri1, CACHE_THROUGH, ((TieredStoreIntegrationTest.MEM_CAPACITY_BYTES) / 3));
        FileSystemTestUtils.createByteFile(mFileSystem, uri2, CACHE_THROUGH, ((TieredStoreIntegrationTest.MEM_CAPACITY_BYTES) / 2));
        FileSystemTestUtils.createByteFile(mFileSystem, uri3, CACHE_THROUGH, ((TieredStoreIntegrationTest.MEM_CAPACITY_BYTES) / 2));
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        AlluxioURI toPromote;
        int toPromoteLen;
        URIStatus file1Info = mFileSystem.getStatus(uri1);
        URIStatus file2Info = mFileSystem.getStatus(uri2);
        URIStatus file3Info = mFileSystem.getStatus(uri3);
        // We know some file will not be in memory, but not which one since we do not want to make
        // any assumptions on the eviction policy
        if ((file1Info.getInAlluxioPercentage()) < 100) {
            toPromote = uri1;
            toPromoteLen = ((int) (file1Info.getLength()));
            Assert.assertEquals(100, file2Info.getInAlluxioPercentage());
            Assert.assertEquals(100, file3Info.getInAlluxioPercentage());
        } else
            if ((file2Info.getInMemoryPercentage()) < 100) {
                toPromote = uri2;
                toPromoteLen = ((int) (file2Info.getLength()));
                Assert.assertEquals(100, file1Info.getInAlluxioPercentage());
                Assert.assertEquals(100, file3Info.getInAlluxioPercentage());
            } else {
                toPromote = uri3;
                toPromoteLen = ((int) (file3Info.getLength()));
                Assert.assertEquals(100, file1Info.getInAlluxioPercentage());
                Assert.assertEquals(100, file2Info.getInAlluxioPercentage());
            }

        FileInStream is = mFileSystem.openFile(toPromote, OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build());
        byte[] buf = new byte[toPromoteLen];
        int len = is.read(buf);
        is.close();
        HeartbeatScheduler.schedule(WORKER_BLOCK_SYNC);
        HeartbeatScheduler.await(WORKER_BLOCK_SYNC, 10, TimeUnit.SECONDS);
        Assert.assertEquals(toPromoteLen, len);
        Assert.assertEquals(100, mFileSystem.getStatus(toPromote).getInAlluxioPercentage());
    }
}

