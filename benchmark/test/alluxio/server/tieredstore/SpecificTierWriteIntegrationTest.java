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


import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_ALIAS;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_PATH;
import PropertyKey.Template.WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.WORKER_MEMORY_SIZE;
import PropertyKey.WORKER_TIERED_STORE_LEVELS;
import PropertyKey.WORKER_TIERED_STORE_RESERVER_ENABLED;
import alluxio.Constants;
import alluxio.client.file.FileSystem;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.master.block.BlockMaster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import com.google.common.io.Files;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for writing to various storage tiers.
 */
public class SpecificTierWriteIntegrationTest extends BaseIntegrationTest {
    private static final int CAPACITY_BYTES = Constants.KB;

    private static final int FILE_SIZE = SpecificTierWriteIntegrationTest.CAPACITY_BYTES;

    private static final String BLOCK_SIZE_BYTES = "1KB";

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, SpecificTierWriteIntegrationTest.BLOCK_SIZE_BYTES).setProperty(USER_FILE_BUFFER_BYTES, SpecificTierWriteIntegrationTest.BLOCK_SIZE_BYTES).setProperty(WORKER_MEMORY_SIZE, SpecificTierWriteIntegrationTest.CAPACITY_BYTES).setProperty(WORKER_TIERED_STORE_LEVELS, "3").setProperty(WORKER_TIERED_STORE_RESERVER_ENABLED, false).setProperty(WORKER_TIERED_STORE_LEVEL_ALIAS.format(1), "SSD").setProperty(WORKER_TIERED_STORE_LEVEL_ALIAS.format(2), "HDD").setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(0), Files.createTempDir().getAbsolutePath()).setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(1), Files.createTempDir().getAbsolutePath()).setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA.format(1), String.valueOf(SpecificTierWriteIntegrationTest.CAPACITY_BYTES)).setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_PATH.format(2), Files.createTempDir().getAbsolutePath()).setProperty(WORKER_TIERED_STORE_LEVEL_DIRS_QUOTA.format(2), String.valueOf(SpecificTierWriteIntegrationTest.CAPACITY_BYTES)).build();

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.WORKER_BLOCK_SYNC);

    private FileSystem mFileSystem = null;

    private BlockMaster mBlockMaster;

    @Test
    public void topTierWrite() throws Exception {
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0, 0);
        deleteAllFiles();
        writeFileAndCheckUsage((-3), SpecificTierWriteIntegrationTest.FILE_SIZE, 0, 0);
        deleteAllFiles();
        writeFileAndCheckUsage((-4), SpecificTierWriteIntegrationTest.FILE_SIZE, 0, 0);
    }

    @Test
    public void midTierWrite() throws Exception {
        writeFileAndCheckUsage(1, 0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0);
        deleteAllFiles();
        writeFileAndCheckUsage((-2), 0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0);
    }

    @Test
    public void bottomTierWrite() throws Exception {
        writeFileAndCheckUsage(2, 0, 0, SpecificTierWriteIntegrationTest.FILE_SIZE);
        deleteAllFiles();
        writeFileAndCheckUsage(3, 0, 0, SpecificTierWriteIntegrationTest.FILE_SIZE);
        deleteAllFiles();
        writeFileAndCheckUsage((-1), 0, 0, SpecificTierWriteIntegrationTest.FILE_SIZE);
    }

    @Test
    public void allTierWrite() throws Exception {
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0, 0);
        writeFileAndCheckUsage(1, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, 0);
        writeFileAndCheckUsage(2, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        deleteAllFiles();
        writeFileAndCheckUsage((-1), 0, 0, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage((-2), 0, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage((-3), SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
    }

    @Test
    public void topTierWriteWithEviction() throws Exception {
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0, 0);
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, 0);
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
    }

    @Test
    public void midTierWriteWithEviction() throws Exception {
        writeFileAndCheckUsage(1, 0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0);
        writeFileAndCheckUsage(1, 0, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage(1, 0, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
    }

    @Test
    public void bottomTierWriteWithEviction() throws Exception {
        writeFileAndCheckUsage(2, 0, 0, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage(2, 0, 0, SpecificTierWriteIntegrationTest.FILE_SIZE);
    }

    @Test
    public void allTierWriteWithEviction() throws Exception {
        writeFileAndCheckUsage(0, SpecificTierWriteIntegrationTest.FILE_SIZE, 0, 0);
        writeFileAndCheckUsage(1, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, 0);
        writeFileAndCheckUsage(2, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage((-1), SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage((-2), SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
        writeFileAndCheckUsage((-3), SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE, SpecificTierWriteIntegrationTest.FILE_SIZE);
    }
}

