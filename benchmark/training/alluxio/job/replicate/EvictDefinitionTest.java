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
package alluxio.job.replicate;


import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.file.FileSystemContext;
import alluxio.job.JobMasterContext;
import alluxio.job.util.SerializableVoid;
import alluxio.wire.BlockLocation;
import alluxio.wire.WorkerInfo;
import alluxio.wire.WorkerNetAddress;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link EvictDefinition}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AlluxioBlockStore.class, FileSystemContext.class, JobMasterContext.class })
public final class EvictDefinitionTest {
    private static final long TEST_BLOCK_ID = 1L;

    private static final WorkerNetAddress ADDRESS_1 = new WorkerNetAddress().setHost("host1").setDataPort(10);

    private static final WorkerNetAddress ADDRESS_2 = new WorkerNetAddress().setHost("host2").setDataPort(10);

    private static final WorkerNetAddress ADDRESS_3 = new WorkerNetAddress().setHost("host3").setDataPort(10);

    private static final WorkerInfo WORKER_INFO_1 = new WorkerInfo().setAddress(EvictDefinitionTest.ADDRESS_1);

    private static final WorkerInfo WORKER_INFO_2 = new WorkerInfo().setAddress(EvictDefinitionTest.ADDRESS_2);

    private static final WorkerInfo WORKER_INFO_3 = new WorkerInfo().setAddress(EvictDefinitionTest.ADDRESS_3);

    private static final Map<WorkerInfo, SerializableVoid> EMPTY = Maps.newHashMap();

    private FileSystemContext mMockFileSystemContext;

    private AlluxioBlockStore mMockBlockStore;

    private JobMasterContext mMockJobMasterContext;

    @Test
    public void selectExecutorsNoBlockWorkerHasBlock() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.<BlockLocation>newArrayList(), 1, Lists.newArrayList(EvictDefinitionTest.WORKER_INFO_1, EvictDefinitionTest.WORKER_INFO_2, EvictDefinitionTest.WORKER_INFO_3));
        // Expect none as no worker has this copy
        Assert.assertEquals(EvictDefinitionTest.EMPTY, result);
    }

    @Test
    public void selectExecutorsNoJobWorkerHasBlock() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_1)), 1, Lists.newArrayList(EvictDefinitionTest.WORKER_INFO_2, EvictDefinitionTest.WORKER_INFO_3));
        // Expect none as no worker that is available has this copy
        Assert.assertEquals(EvictDefinitionTest.EMPTY, result);
    }

    @Test
    public void selectExecutorsOnlyOneBlockWorkerHasBlock() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_1)), 1, Lists.newArrayList(EvictDefinitionTest.WORKER_INFO_1, EvictDefinitionTest.WORKER_INFO_2, EvictDefinitionTest.WORKER_INFO_3));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(EvictDefinitionTest.WORKER_INFO_1, null);
        // Expect the only worker 1 having this block
        Assert.assertEquals(expected, result);
    }

    @Test
    public void selectExecutorsAnyOneWorkers() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_1), new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_2), new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_3)), 1, Lists.newArrayList(EvictDefinitionTest.WORKER_INFO_1, EvictDefinitionTest.WORKER_INFO_2, EvictDefinitionTest.WORKER_INFO_3));
        // Expect one worker from all workers having this block
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(null, result.values().iterator().next());
    }

    @Test
    public void selectExecutorsAllWorkers() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_1), new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_2), new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_3)), 3, Lists.newArrayList(EvictDefinitionTest.WORKER_INFO_1, EvictDefinitionTest.WORKER_INFO_2, EvictDefinitionTest.WORKER_INFO_3));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(EvictDefinitionTest.WORKER_INFO_1, null);
        expected.put(EvictDefinitionTest.WORKER_INFO_2, null);
        expected.put(EvictDefinitionTest.WORKER_INFO_3, null);
        // Expect all workers are selected as they all have this block
        Assert.assertEquals(expected, result);
    }

    @Test
    public void selectExecutorsBothWorkers() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_1), new BlockLocation().setWorkerAddress(EvictDefinitionTest.ADDRESS_2)), 3, Lists.newArrayList(EvictDefinitionTest.WORKER_INFO_1, EvictDefinitionTest.WORKER_INFO_2, EvictDefinitionTest.WORKER_INFO_3));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(EvictDefinitionTest.WORKER_INFO_1, null);
        expected.put(EvictDefinitionTest.WORKER_INFO_2, null);
        // Expect both workers having this block should be selected
        Assert.assertEquals(expected, result);
    }
}

