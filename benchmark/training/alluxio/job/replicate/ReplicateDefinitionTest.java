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


import ExceptionMessage.NO_LOCAL_BLOCK_WORKER_REPLICATE_TASK;
import PropertyKey.NETWORK_HOST_RESOLUTION_TIMEOUT_MS;
import alluxio.client.block.AlluxioBlockStore;
import alluxio.client.block.BlockWorkerInfo;
import alluxio.client.block.stream.BlockInStream;
import alluxio.client.block.stream.BlockInStream.BlockInStreamSource;
import alluxio.client.block.stream.BlockOutStream;
import alluxio.client.block.stream.TestBlockInStream;
import alluxio.client.block.stream.TestBlockOutStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.status.NotFoundException;
import alluxio.job.JobMasterContext;
import alluxio.job.util.SerializableVoid;
import alluxio.underfs.UfsManager;
import alluxio.util.io.BufferUtils;
import alluxio.util.network.NetworkAddressUtils;
import alluxio.wire.BlockLocation;
import alluxio.wire.WorkerInfo;
import alluxio.wire.WorkerNetAddress;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link ReplicateConfig}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AlluxioBlockStore.class, FileSystemContext.class, JobMasterContext.class })
public final class ReplicateDefinitionTest {
    private static final long TEST_BLOCK_ID = 1L;

    private static final long TEST_BLOCK_SIZE = 512L;

    private static final int MAX_BYTES = 1000;

    private static final WorkerNetAddress ADDRESS_1 = new WorkerNetAddress().setHost("host1").setDataPort(10);

    private static final WorkerNetAddress ADDRESS_2 = new WorkerNetAddress().setHost("host2").setDataPort(10);

    private static final WorkerNetAddress ADDRESS_3 = new WorkerNetAddress().setHost("host3").setDataPort(10);

    private static final WorkerNetAddress LOCAL_ADDRESS = new WorkerNetAddress().setHost(NetworkAddressUtils.getLocalHostName(((int) (ServerConfiguration.getMs(NETWORK_HOST_RESOLUTION_TIMEOUT_MS))))).setDataPort(10);

    private static final WorkerInfo WORKER_INFO_1 = new WorkerInfo().setAddress(ReplicateDefinitionTest.ADDRESS_1);

    private static final WorkerInfo WORKER_INFO_2 = new WorkerInfo().setAddress(ReplicateDefinitionTest.ADDRESS_2);

    private static final WorkerInfo WORKER_INFO_3 = new WorkerInfo().setAddress(ReplicateDefinitionTest.ADDRESS_3);

    private FileSystemContext mMockFileSystemContext;

    private AlluxioBlockStore mMockBlockStore;

    private FileSystem mMockFileSystem;

    private JobMasterContext mMockJobMasterContext;

    private UfsManager mMockUfsManager;

    @Rule
    public final ExpectedException mThrown = ExpectedException.none();

    @Test
    public void selectExecutorsOnlyOneWorkerAvailable() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.<BlockLocation>newArrayList(), 1, Lists.newArrayList(ReplicateDefinitionTest.WORKER_INFO_1));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(ReplicateDefinitionTest.WORKER_INFO_1, null);
        // select the only worker
        Assert.assertEquals(expected, result);
    }

    @Test
    public void selectExecutorsOnlyOneWorkerValid() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(ReplicateDefinitionTest.ADDRESS_1)), 1, Lists.newArrayList(ReplicateDefinitionTest.WORKER_INFO_1, ReplicateDefinitionTest.WORKER_INFO_2));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(ReplicateDefinitionTest.WORKER_INFO_2, null);
        // select one worker left
        Assert.assertEquals(expected, result);
    }

    @Test
    public void selectExecutorsTwoWorkersValid() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(ReplicateDefinitionTest.ADDRESS_1)), 2, Lists.newArrayList(ReplicateDefinitionTest.WORKER_INFO_1, ReplicateDefinitionTest.WORKER_INFO_2, ReplicateDefinitionTest.WORKER_INFO_3));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(ReplicateDefinitionTest.WORKER_INFO_2, null);
        expected.put(ReplicateDefinitionTest.WORKER_INFO_3, null);
        // select both workers left
        Assert.assertEquals(expected, result);
    }

    @Test
    public void selectExecutorsOneOutOFTwoWorkersValid() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(ReplicateDefinitionTest.ADDRESS_1)), 1, Lists.newArrayList(ReplicateDefinitionTest.WORKER_INFO_1, ReplicateDefinitionTest.WORKER_INFO_2, ReplicateDefinitionTest.WORKER_INFO_3));
        // select one worker out of two
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(null, result.values().iterator().next());
    }

    @Test
    public void selectExecutorsNoWorkerValid() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(ReplicateDefinitionTest.ADDRESS_1)), 1, Lists.newArrayList(ReplicateDefinitionTest.WORKER_INFO_1));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        // select none as no choice left
        Assert.assertEquals(expected, result);
    }

    @Test
    public void selectExecutorsInsufficientWorkerValid() throws Exception {
        Map<WorkerInfo, SerializableVoid> result = selectExecutorsTestHelper(Lists.newArrayList(new BlockLocation().setWorkerAddress(ReplicateDefinitionTest.ADDRESS_1)), 2, Lists.newArrayList(ReplicateDefinitionTest.WORKER_INFO_1, ReplicateDefinitionTest.WORKER_INFO_2));
        Map<WorkerInfo, SerializableVoid> expected = Maps.newHashMap();
        expected.put(ReplicateDefinitionTest.WORKER_INFO_2, null);
        // select the only worker left though more copies are requested
        Assert.assertEquals(expected, result);
    }

    @Test
    public void runTaskNoBlockWorker() throws Exception {
        byte[] input = BufferUtils.getIncreasingByteArray(0, ((int) (ReplicateDefinitionTest.TEST_BLOCK_SIZE)));
        TestBlockInStream mockInStream = new TestBlockInStream(input, ReplicateDefinitionTest.TEST_BLOCK_ID, input.length, false, BlockInStreamSource.LOCAL);
        TestBlockOutStream mockOutStream = new TestBlockOutStream(ByteBuffer.allocate(ReplicateDefinitionTest.MAX_BYTES), ReplicateDefinitionTest.TEST_BLOCK_SIZE);
        mThrown.expect(NotFoundException.class);
        mThrown.expectMessage(NO_LOCAL_BLOCK_WORKER_REPLICATE_TASK.getMessage(ReplicateDefinitionTest.TEST_BLOCK_ID));
        runTaskReplicateTestHelper(Lists.<BlockWorkerInfo>newArrayList(), mockInStream, mockOutStream);
    }

    @Test
    public void runTaskLocalBlockWorker() throws Exception {
        byte[] input = BufferUtils.getIncreasingByteArray(0, ((int) (ReplicateDefinitionTest.TEST_BLOCK_SIZE)));
        TestBlockInStream mockInStream = new TestBlockInStream(input, ReplicateDefinitionTest.TEST_BLOCK_ID, input.length, false, BlockInStreamSource.LOCAL);
        TestBlockOutStream mockOutStream = new TestBlockOutStream(ByteBuffer.allocate(ReplicateDefinitionTest.MAX_BYTES), ReplicateDefinitionTest.TEST_BLOCK_SIZE);
        BlockWorkerInfo localBlockWorker = new BlockWorkerInfo(ReplicateDefinitionTest.LOCAL_ADDRESS, ReplicateDefinitionTest.TEST_BLOCK_SIZE, 0);
        runTaskReplicateTestHelper(Lists.newArrayList(localBlockWorker), mockInStream, mockOutStream);
        Assert.assertTrue(Arrays.equals(input, mockOutStream.getWrittenData()));
    }

    @Test
    public void runTaskInputIOException() throws Exception {
        BlockInStream mockInStream = Mockito.mock(BlockInStream.class);
        BlockOutStream mockOutStream = Mockito.mock(BlockOutStream.class);
        BlockWorkerInfo localBlockWorker = new BlockWorkerInfo(ReplicateDefinitionTest.LOCAL_ADDRESS, ReplicateDefinitionTest.TEST_BLOCK_SIZE, 0);
        Mockito.doThrow(new IOException("test")).when(mockInStream).read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doThrow(new IOException("test")).when(mockInStream).read(ArgumentMatchers.any(byte[].class));
        try {
            runTaskReplicateTestHelper(Lists.newArrayList(localBlockWorker), mockInStream, mockOutStream);
            Assert.fail("Expected the task to throw and IOException");
        } catch (IOException e) {
            Assert.assertEquals("test", e.getMessage());
        }
        Mockito.verify(mockOutStream).cancel();
    }
}

