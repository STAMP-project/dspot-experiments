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
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_ALIAS;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_PATH;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.USER_SHORT_CIRCUIT_ENABLED;
import PropertyKey.WORKER_FILE_BUFFER_SIZE;
import PropertyKey.WORKER_MEMORY_SIZE;
import PropertyKey.WORKER_TIERED_STORE_LEVELS;
import PropertyKey.WORKER_TIERED_STORE_RESERVER_ENABLED;
import ReadPType.CACHE_PROMOTE;
import alluxio.AlluxioURI;
import alluxio.Constants;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.grpc.CreateFilePOptions;
import alluxio.grpc.OpenFilePOptions;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.io.BufferUtils;
import alluxio.util.io.PathUtils;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TierPromoteIntegrationTest extends BaseIntegrationTest {
    private static final int CAPACITY_BYTES = Constants.KB;

    private static final String BLOCK_SIZE_BYTES = "1KB";

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource;

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.WORKER_BLOCK_SYNC);

    private FileSystem mFileSystem = null;

    /**
     * Constructor.
     *
     * @param shortCircuitEnabled
     * 		whether to enable the short circuit data paths
     */
    public TierPromoteIntegrationTest(String shortCircuitEnabled) {
        mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, TierPromoteIntegrationTest.BLOCK_SIZE_BYTES).setProperty(USER_FILE_BUFFER_BYTES, TierPromoteIntegrationTest.BLOCK_SIZE_BYTES).setProperty(WORKER_FILE_BUFFER_SIZE, TierPromoteIntegrationTest.BLOCK_SIZE_BYTES).setProperty(WORKER_MEMORY_SIZE, TierPromoteIntegrationTest.CAPACITY_BYTES).setProperty(USER_SHORT_CIRCUIT_ENABLED, shortCircuitEnabled).setProperty(WORKER_TIERED_STORE_LEVELS, "2").setProperty(WORKER_TIERED_STORE_RESERVER_ENABLED, false).setProperty(WORKER_TIERED_STORE_LEVEL_ALIAS.format(1), "SSD").setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(0), Files.createTempDir().getAbsolutePath()).setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(1), Files.createTempDir().getAbsolutePath()).setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA.format(1), String.valueOf(TierPromoteIntegrationTest.CAPACITY_BYTES)).build();
    }

    @Test
    public void promoteBlock() throws Exception {
        final int size = (TierPromoteIntegrationTest.CAPACITY_BYTES) / 2;
        AlluxioURI path1 = new AlluxioURI(PathUtils.uniqPath());
        AlluxioURI path2 = new AlluxioURI(PathUtils.uniqPath());
        AlluxioURI path3 = new AlluxioURI(PathUtils.uniqPath());
        // Write three files, first file should be in ssd, the others should be in memory
        FileOutStream os1 = mFileSystem.createFile(path1, CreateFilePOptions.newBuilder().setRecursive(true).build());
        os1.write(BufferUtils.getIncreasingByteArray(size));
        os1.close();
        FileOutStream os2 = mFileSystem.createFile(path2, CreateFilePOptions.newBuilder().setRecursive(true).build());
        os2.write(BufferUtils.getIncreasingByteArray(size));
        os2.close();
        FileOutStream os3 = mFileSystem.createFile(path3, CreateFilePOptions.newBuilder().setRecursive(true).build());
        os3.write(BufferUtils.getIncreasingByteArray(size));
        os3.close();
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // Not in memory but in Alluxio storage
        Assert.assertEquals(0, mFileSystem.getStatus(path1).getInMemoryPercentage());
        Assert.assertFalse(mFileSystem.getStatus(path1).getFileBlockInfos().isEmpty());
        // After reading with CACHE_PROMOTE, the file should be in memory
        FileInStream in = mFileSystem.openFile(path1, OpenFilePOptions.newBuilder().setReadType(CACHE_PROMOTE).build());
        byte[] buf = new byte[size];
        while ((in.read(buf)) != (-1)) {
            // read the entire file
        } 
        in.close();
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        // In memory
        Assert.assertEquals(100, mFileSystem.getStatus(path1).getInMemoryPercentage());
    }
}

