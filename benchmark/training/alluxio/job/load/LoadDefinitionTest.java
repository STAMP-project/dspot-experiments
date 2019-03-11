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
package alluxio.job.load;


import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.job.JobMasterContext;
import alluxio.job.load.LoadDefinition.LoadTask;
import alluxio.wire.WorkerInfo;
import alluxio.wire.WorkerNetAddress;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link LoadDefinition}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileSystem.class, JobMasterContext.class, FileSystemContext.class, AlluxioBlockStore.class })
public class LoadDefinitionTest {
    private static final String TEST_URI = "/test";

    private static final List<WorkerInfo> JOB_WORKERS = new ImmutableList.Builder<WorkerInfo>().add(new WorkerInfo().setId(0).setAddress(new WorkerNetAddress().setHost("host0"))).add(new WorkerInfo().setId(1).setAddress(new WorkerNetAddress().setHost("host1"))).add(new WorkerInfo().setId(2).setAddress(new WorkerNetAddress().setHost("host2"))).add(new WorkerInfo().setId(3).setAddress(new WorkerNetAddress().setHost("host3"))).build();

    private static final List<BlockWorkerInfo> BLOCK_WORKERS = new ImmutableList.Builder<BlockWorkerInfo>().add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host0"), 0, 0)).add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host1"), 0, 0)).add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host2"), 0, 0)).add(new BlockWorkerInfo(new WorkerNetAddress().setHost("host3"), 0, 0)).build();

    private FileSystem mMockFileSystem;

    private AlluxioBlockStore mMockBlockStore;

    private JobMasterContext mMockJobMasterContext;

    private FileSystemContext mMockFsContext;

    @Test
    public void replicationSatisfied() throws Exception {
        int numBlocks = 7;
        int replication = 3;
        createFileWithNoLocations(LoadDefinitionTest.TEST_URI, numBlocks);
        LoadConfig config = new LoadConfig(LoadDefinitionTest.TEST_URI, replication);
        Map<WorkerInfo, ArrayList<LoadTask>> assignments = new LoadDefinition(mMockFsContext, mMockFileSystem).selectExecutors(config, LoadDefinitionTest.JOB_WORKERS, mMockJobMasterContext);
        // Check that we are loading the right number of blocks.
        int totalBlockLoads = 0;
        for (List<LoadTask> blocks : assignments.values()) {
            totalBlockLoads += blocks.size();
        }
        Assert.assertEquals((numBlocks * replication), totalBlockLoads);
    }

    @Test
    public void skipJobWorkersWithoutLocalBlockWorkers() throws Exception {
        List<BlockWorkerInfo> blockWorkers = Arrays.asList(new BlockWorkerInfo(new WorkerNetAddress().setHost("host0"), 0, 0));
        Mockito.when(mMockBlockStore.getAllWorkers()).thenReturn(blockWorkers);
        createFileWithNoLocations(LoadDefinitionTest.TEST_URI, 10);
        LoadConfig config = new LoadConfig(LoadDefinitionTest.TEST_URI, 1);
        Map<WorkerInfo, ArrayList<LoadTask>> assignments = new LoadDefinition(mMockFsContext, mMockFileSystem).selectExecutors(config, LoadDefinitionTest.JOB_WORKERS, mMockJobMasterContext);
        Assert.assertEquals(1, assignments.size());
        Assert.assertEquals(10, assignments.values().iterator().next().size());
    }

    @Test
    public void notEnoughWorkersForReplication() throws Exception {
        createFileWithNoLocations(LoadDefinitionTest.TEST_URI, 1);
        LoadConfig config = new LoadConfig(LoadDefinitionTest.TEST_URI, 5);// set replication to 5

        try {
            new LoadDefinition(mMockFsContext, mMockFileSystem).selectExecutors(config, LoadDefinitionTest.JOB_WORKERS, mMockJobMasterContext);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Failed to find enough block workers to replicate to. Needed 5 but only found 4."));
        }
    }

    @Test
    public void notEnoughJobWorkersWithLocalBlockWorkers() throws Exception {
        List<BlockWorkerInfo> blockWorkers = Arrays.asList(new BlockWorkerInfo(new WorkerNetAddress().setHost("host0"), 0, 0), new BlockWorkerInfo(new WorkerNetAddress().setHost("otherhost"), 0, 0));
        Mockito.when(mMockBlockStore.getAllWorkers()).thenReturn(blockWorkers);
        createFileWithNoLocations(LoadDefinitionTest.TEST_URI, 1);
        LoadConfig config = new LoadConfig(LoadDefinitionTest.TEST_URI, 2);// set replication to 2

        try {
            new LoadDefinition(mMockFsContext, mMockFileSystem).selectExecutors(config, LoadDefinitionTest.JOB_WORKERS, mMockJobMasterContext);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Available workers without the block: [host0]"));
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(("The following workers could not be used because " + "they have no local job workers: [otherhost]")));
        }
    }
}

