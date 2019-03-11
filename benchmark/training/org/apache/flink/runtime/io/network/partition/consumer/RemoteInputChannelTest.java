/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network.partition.consumer;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.flink.runtime.execution.CancelTaskException;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.ConnectionManager;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferPool;
import org.apache.flink.runtime.io.network.buffer.NetworkBufferPool;
import org.apache.flink.runtime.io.network.netty.PartitionRequestClient;
import org.apache.flink.runtime.io.network.partition.ProducerFailedException;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.io.network.util.TestBufferFactory;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import scala.Tuple2;


/**
 * Tests for the {@link RemoteInputChannel}.
 */
public class RemoteInputChannelTest {
    @Test
    public void testExceptionOnReordering() throws Exception {
        // Setup
        final SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        final Buffer buffer = TestBufferFactory.createBuffer(TestBufferFactory.BUFFER_SIZE);
        // The test
        inputChannel.onBuffer(buffer.retainBuffer(), 0, (-1));
        // This does not yet throw the exception, but sets the error at the channel.
        inputChannel.onBuffer(buffer, 29, (-1));
        try {
            inputChannel.getNextBuffer();
            Assert.fail("Did not throw expected exception after enqueuing an out-of-order buffer.");
        } catch (Exception expected) {
            Assert.assertFalse(buffer.isRecycled());
            // free remaining buffer instances
            inputChannel.releaseAllResources();
            Assert.assertTrue(buffer.isRecycled());
        }
        // Need to notify the input gate for the out-of-order buffer as well. Otherwise the
        // receiving task will not notice the error.
        Mockito.verify(inputGate, Mockito.times(2)).notifyChannelNonEmpty(ArgumentMatchers.eq(inputChannel));
    }

    @Test
    public void testConcurrentOnBufferAndRelease() throws Exception {
        testConcurrentReleaseAndSomething(8192, ( inputChannel, buffer, j) -> {
            inputChannel.onBuffer(buffer, j, (-1));
            return null;
        });
    }

    @Test
    public void testConcurrentNotifyBufferAvailableAndRelease() throws Exception {
        testConcurrentReleaseAndSomething(1024, ( inputChannel, buffer, j) -> inputChannel.notifyBufferAvailable(buffer));
    }

    private interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v) throws Exception;
    }

    @Test(expected = IllegalStateException.class)
    public void testRetriggerWithoutPartitionRequest() throws Exception {
        Tuple2<Integer, Integer> backoff = new Tuple2<Integer, Integer>(500, 3000);
        PartitionRequestClient connClient = Mockito.mock(PartitionRequestClient.class);
        SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        RemoteInputChannel ch = createRemoteInputChannel(inputGate, connClient, backoff);
        ch.retriggerSubpartitionRequest(0);
    }

    @Test
    public void testPartitionRequestExponentialBackoff() throws Exception {
        // Config
        Tuple2<Integer, Integer> backoff = new Tuple2<Integer, Integer>(500, 3000);
        // Start with initial backoff, then keep doubling, and cap at max.
        int[] expectedDelays = new int[]{ backoff._1(), 1000, 2000, backoff._2() };
        // Setup
        PartitionRequestClient connClient = Mockito.mock(PartitionRequestClient.class);
        SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        RemoteInputChannel ch = createRemoteInputChannel(inputGate, connClient, backoff);
        // Initial request
        ch.requestSubpartition(0);
        Mockito.verify(connClient).requestSubpartition(ArgumentMatchers.eq(ch.partitionId), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ch), ArgumentMatchers.eq(0));
        // Request subpartition and verify that the actual requests are delayed.
        for (int expected : expectedDelays) {
            ch.retriggerSubpartitionRequest(0);
            Mockito.verify(connClient).requestSubpartition(ArgumentMatchers.eq(ch.partitionId), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ch), ArgumentMatchers.eq(expected));
        }
        // Exception after backoff is greater than the maximum backoff.
        try {
            ch.retriggerSubpartitionRequest(0);
            ch.getNextBuffer();
            Assert.fail("Did not throw expected exception.");
        } catch (Exception expected) {
        }
    }

    @Test
    public void testPartitionRequestSingleBackoff() throws Exception {
        // Config
        Tuple2<Integer, Integer> backoff = new Tuple2<Integer, Integer>(500, 500);
        // Setup
        PartitionRequestClient connClient = Mockito.mock(PartitionRequestClient.class);
        SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        RemoteInputChannel ch = createRemoteInputChannel(inputGate, connClient, backoff);
        // No delay for first request
        ch.requestSubpartition(0);
        Mockito.verify(connClient).requestSubpartition(ArgumentMatchers.eq(ch.partitionId), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ch), ArgumentMatchers.eq(0));
        // Initial delay for second request
        ch.retriggerSubpartitionRequest(0);
        Mockito.verify(connClient).requestSubpartition(ArgumentMatchers.eq(ch.partitionId), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ch), ArgumentMatchers.eq(backoff._1()));
        // Exception after backoff is greater than the maximum backoff.
        try {
            ch.retriggerSubpartitionRequest(0);
            ch.getNextBuffer();
            Assert.fail("Did not throw expected exception.");
        } catch (Exception expected) {
        }
    }

    @Test
    public void testPartitionRequestNoBackoff() throws Exception {
        // Config
        Tuple2<Integer, Integer> backoff = new Tuple2<Integer, Integer>(0, 0);
        // Setup
        PartitionRequestClient connClient = Mockito.mock(PartitionRequestClient.class);
        SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        RemoteInputChannel ch = createRemoteInputChannel(inputGate, connClient, backoff);
        // No delay for first request
        ch.requestSubpartition(0);
        Mockito.verify(connClient).requestSubpartition(ArgumentMatchers.eq(ch.partitionId), ArgumentMatchers.eq(0), ArgumentMatchers.eq(ch), ArgumentMatchers.eq(0));
        // Exception, because backoff is disabled.
        try {
            ch.retriggerSubpartitionRequest(0);
            ch.getNextBuffer();
            Assert.fail("Did not throw expected exception.");
        } catch (Exception expected) {
        }
    }

    @Test
    public void testOnFailedPartitionRequest() throws Exception {
        final ConnectionManager connectionManager = Mockito.mock(ConnectionManager.class);
        Mockito.when(connectionManager.createPartitionRequestClient(ArgumentMatchers.any(ConnectionID.class))).thenReturn(Mockito.mock(PartitionRequestClient.class));
        final ResultPartitionID partitionId = new ResultPartitionID();
        final SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        final RemoteInputChannel ch = new RemoteInputChannel(inputGate, 0, partitionId, Mockito.mock(ConnectionID.class), connectionManager, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        ch.onFailedPartitionRequest();
        Mockito.verify(inputGate).triggerPartitionStateCheck(ArgumentMatchers.eq(partitionId));
    }

    @Test(expected = CancelTaskException.class)
    public void testProducerFailedException() throws Exception {
        ConnectionManager connManager = Mockito.mock(ConnectionManager.class);
        Mockito.when(connManager.createPartitionRequestClient(ArgumentMatchers.any(ConnectionID.class))).thenReturn(Mockito.mock(PartitionRequestClient.class));
        final RemoteInputChannel ch = new RemoteInputChannel(Mockito.mock(SingleInputGate.class), 0, new ResultPartitionID(), Mockito.mock(ConnectionID.class), connManager, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        ch.onError(new ProducerFailedException(new RuntimeException("Expected test exception.")));
        ch.requestSubpartition(0);
        // Should throw an instance of CancelTaskException.
        ch.getNextBuffer();
    }

    /**
     * Tests to verify the behaviours of three different processes if the number of available
     * buffers is less than required buffers.
     *
     * <ol>
     * <li>Recycle the floating buffer</li>
     * <li>Recycle the exclusive buffer</li>
     * <li>Decrease the sender's backlog</li>
     * </ol>
     */
    @Test
    public void testAvailableBuffersLessThanRequiredBuffers() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(16, 32);
        final int numExclusiveBuffers = 2;
        final int numFloatingBuffers = 14;
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = Mockito.spy(networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers));
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel.requestSubpartition(0);
            // Prepare the exclusive and floating buffers to verify recycle logic later
            final Buffer exclusiveBuffer = inputChannel.requestBuffer();
            Assert.assertNotNull(exclusiveBuffer);
            final int numRecycleFloatingBuffers = 2;
            final ArrayDeque<Buffer> floatingBufferQueue = new ArrayDeque<>(numRecycleFloatingBuffers);
            for (int i = 0; i < numRecycleFloatingBuffers; i++) {
                Buffer floatingBuffer = bufferPool.requestBuffer();
                Assert.assertNotNull(floatingBuffer);
                floatingBufferQueue.add(floatingBuffer);
            }
            Mockito.verify(bufferPool, Mockito.times(numRecycleFloatingBuffers)).requestBuffer();
            // Receive the producer's backlog more than the number of available floating buffers
            inputChannel.onSenderBacklog(14);
            // The channel requests (backlog + numExclusiveBuffers) floating buffers from local pool.
            // It does not get enough floating buffers and register as buffer listener
            Mockito.verify(bufferPool, Mockito.times(15)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 13 buffers available in the channel", 13, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 16 buffers required in the channel", 16, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertTrue(inputChannel.isWaitingForFloatingBuffers());
            // Increase the backlog
            inputChannel.onSenderBacklog(16);
            // The channel is already in the status of waiting for buffers and will not request any more
            Mockito.verify(bufferPool, Mockito.times(15)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 13 buffers available in the channel", 13, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 18 buffers required in the channel", 18, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertTrue(inputChannel.isWaitingForFloatingBuffers());
            // Recycle one exclusive buffer
            exclusiveBuffer.recycleBuffer();
            // The exclusive buffer is returned to the channel directly
            Mockito.verify(bufferPool, Mockito.times(15)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 18 buffers required in the channel", 18, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertTrue(inputChannel.isWaitingForFloatingBuffers());
            // Recycle one floating buffer
            floatingBufferQueue.poll().recycleBuffer();
            // Assign the floating buffer to the listener and the channel is still waiting for more floating buffers
            Mockito.verify(bufferPool, Mockito.times(15)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 15 buffers available in the channel", 15, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 18 buffers required in the channel", 18, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertTrue(inputChannel.isWaitingForFloatingBuffers());
            // Decrease the backlog
            inputChannel.onSenderBacklog(13);
            // Only the number of required buffers is changed by (backlog + numExclusiveBuffers)
            Mockito.verify(bufferPool, Mockito.times(15)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 15 buffers available in the channel", 15, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 15 buffers required in the channel", 15, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertTrue(inputChannel.isWaitingForFloatingBuffers());
            // Recycle one more floating buffer
            floatingBufferQueue.poll().recycleBuffer();
            // Return the floating buffer to the buffer pool and the channel is not waiting for more floating buffers
            Mockito.verify(bufferPool, Mockito.times(15)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 15 buffers available in the channel", 15, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 15 buffers required in the channel", 15, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 1 buffers available in local pool", 1, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertFalse(inputChannel.isWaitingForFloatingBuffers());
            // Increase the backlog again
            inputChannel.onSenderBacklog(15);
            // The floating buffer is requested from the buffer pool and the channel is registered as listener again.
            Mockito.verify(bufferPool, Mockito.times(17)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(2)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 16 buffers available in the channel", 16, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 17 buffers required in the channel", 17, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertTrue(inputChannel.isWaitingForFloatingBuffers());
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, null, null, thrown, inputChannel);
        }
    }

    /**
     * Tests to verify the behaviours of recycling floating and exclusive buffers if the number of available
     * buffers equals to required buffers.
     */
    @Test
    public void testAvailableBuffersEqualToRequiredBuffers() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(16, 32);
        final int numExclusiveBuffers = 2;
        final int numFloatingBuffers = 14;
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = Mockito.spy(networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers));
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel.requestSubpartition(0);
            // Prepare the exclusive and floating buffers to verify recycle logic later
            final Buffer exclusiveBuffer = inputChannel.requestBuffer();
            Assert.assertNotNull(exclusiveBuffer);
            final Buffer floatingBuffer = bufferPool.requestBuffer();
            Assert.assertNotNull(floatingBuffer);
            Mockito.verify(bufferPool, Mockito.times(1)).requestBuffer();
            // Receive the producer's backlog
            inputChannel.onSenderBacklog(12);
            // The channel requests (backlog + numExclusiveBuffers) floating buffers from local pool
            // and gets enough floating buffers
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 14 buffers required in the channel", 14, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            // Recycle one floating buffer
            floatingBuffer.recycleBuffer();
            // The floating buffer is returned to local buffer directly because the channel is not waiting
            // for floating buffers
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 14 buffers required in the channel", 14, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 1 buffer available in local pool", 1, bufferPool.getNumberOfAvailableMemorySegments());
            // Recycle one exclusive buffer
            exclusiveBuffer.recycleBuffer();
            // Return one extra floating buffer to the local pool because the number of available buffers
            // already equals to required buffers
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 14 buffers required in the channel", 14, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 2 buffers available in local pool", 2, bufferPool.getNumberOfAvailableMemorySegments());
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, null, null, thrown, inputChannel);
        }
    }

    /**
     * Tests to verify the behaviours of recycling floating and exclusive buffers if the number of available
     * buffers is more than required buffers by decreasing the sender's backlog.
     */
    @Test
    public void testAvailableBuffersMoreThanRequiredBuffers() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(16, 32);
        final int numExclusiveBuffers = 2;
        final int numFloatingBuffers = 14;
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = Mockito.spy(networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers));
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel.requestSubpartition(0);
            // Prepare the exclusive and floating buffers to verify recycle logic later
            final Buffer exclusiveBuffer = inputChannel.requestBuffer();
            Assert.assertNotNull(exclusiveBuffer);
            final Buffer floatingBuffer = bufferPool.requestBuffer();
            Assert.assertNotNull(floatingBuffer);
            Mockito.verify(bufferPool, Mockito.times(1)).requestBuffer();
            // Receive the producer's backlog
            inputChannel.onSenderBacklog(12);
            // The channel gets enough floating buffers from local pool
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 14 buffers required in the channel", 14, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            // Decrease the backlog to make the number of available buffers more than required buffers
            inputChannel.onSenderBacklog(10);
            // Only the number of required buffers is changed by (backlog + numExclusiveBuffers)
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 12 buffers required in the channel", 12, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 0 buffers available in local pool", 0, bufferPool.getNumberOfAvailableMemorySegments());
            // Recycle one exclusive buffer
            exclusiveBuffer.recycleBuffer();
            // Return one extra floating buffer to the local pool because the number of available buffers
            // is more than required buffers
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 12 buffers required in the channel", 12, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 1 buffer available in local pool", 1, bufferPool.getNumberOfAvailableMemorySegments());
            // Recycle one floating buffer
            floatingBuffer.recycleBuffer();
            // The floating buffer is returned to local pool directly because the channel is not waiting for
            // floating buffers
            Mockito.verify(bufferPool, Mockito.times(14)).requestBuffer();
            Mockito.verify(bufferPool, Mockito.times(0)).addBufferListener(inputChannel);
            Assert.assertEquals("There should be 14 buffers available in the channel", 14, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 12 buffers required in the channel", 12, inputChannel.getNumberOfRequiredBuffers());
            Assert.assertEquals("There should be 2 buffers available in local pool", 2, bufferPool.getNumberOfAvailableMemorySegments());
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, null, null, thrown, inputChannel);
        }
    }

    /**
     * Tests to verify that the buffer pool will distribute available floating buffers among
     * all the channel listeners in a fair way.
     */
    @Test
    public void testFairDistributionFloatingBuffers() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(12, 32);
        final int numExclusiveBuffers = 2;
        final int numFloatingBuffers = 3;
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel channel1 = Mockito.spy(createRemoteInputChannel(inputGate));
        final RemoteInputChannel channel2 = Mockito.spy(createRemoteInputChannel(inputGate));
        final RemoteInputChannel channel3 = Mockito.spy(createRemoteInputChannel(inputGate));
        inputGate.setInputChannel(channel1.partitionId.getPartitionId(), channel1);
        inputGate.setInputChannel(channel2.partitionId.getPartitionId(), channel2);
        inputGate.setInputChannel(channel3.partitionId.getPartitionId(), channel3);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = Mockito.spy(networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers));
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            channel1.requestSubpartition(0);
            channel2.requestSubpartition(0);
            channel3.requestSubpartition(0);
            // Exhaust all the floating buffers
            final List<Buffer> floatingBuffers = new ArrayList<>(numFloatingBuffers);
            for (int i = 0; i < numFloatingBuffers; i++) {
                Buffer buffer = bufferPool.requestBuffer();
                Assert.assertNotNull(buffer);
                floatingBuffers.add(buffer);
            }
            // Receive the producer's backlog to trigger request floating buffers from pool
            // and register as listeners as a result
            channel1.onSenderBacklog(8);
            channel2.onSenderBacklog(8);
            channel3.onSenderBacklog(8);
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(channel1);
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(channel2);
            Mockito.verify(bufferPool, Mockito.times(1)).addBufferListener(channel3);
            Assert.assertEquals((("There should be " + numExclusiveBuffers) + " buffers available in the channel"), numExclusiveBuffers, channel1.getNumberOfAvailableBuffers());
            Assert.assertEquals((("There should be " + numExclusiveBuffers) + " buffers available in the channel"), numExclusiveBuffers, channel2.getNumberOfAvailableBuffers());
            Assert.assertEquals((("There should be " + numExclusiveBuffers) + " buffers available in the channel"), numExclusiveBuffers, channel3.getNumberOfAvailableBuffers());
            // Recycle three floating buffers to trigger notify buffer available
            for (Buffer buffer : floatingBuffers) {
                buffer.recycleBuffer();
            }
            Mockito.verify(channel1, Mockito.times(1)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
            Mockito.verify(channel2, Mockito.times(1)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
            Mockito.verify(channel3, Mockito.times(1)).notifyBufferAvailable(ArgumentMatchers.any(Buffer.class));
            Assert.assertEquals("There should be 3 buffers available in the channel", 3, channel1.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 3 buffers available in the channel", 3, channel2.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 3 buffers available in the channel", 3, channel3.getNumberOfAvailableBuffers());
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, null, null, thrown, channel1, channel2, channel3);
        }
    }

    /**
     * Tests that failures are propagated correctly if
     * {@link RemoteInputChannel#notifyBufferAvailable(Buffer)} throws an exception. Also tests that
     * a second listener will be notified in this case.
     */
    @Test
    public void testFailureInNotifyBufferAvailable() throws Exception {
        // Setup
        final int numExclusiveBuffers = 0;
        final int numFloatingBuffers = 1;
        final int numTotalBuffers = numExclusiveBuffers + numFloatingBuffers;
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(numTotalBuffers, 32);
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel successfulRemoteIC = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(successfulRemoteIC.partitionId.getPartitionId(), successfulRemoteIC);
        successfulRemoteIC.requestSubpartition(0);
        // late creation -> no exclusive buffers, also no requested subpartition in successfulRemoteIC
        // (to trigger a failure in RemoteInputChannel#notifyBufferAvailable())
        final RemoteInputChannel failingRemoteIC = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(failingRemoteIC.partitionId.getPartitionId(), failingRemoteIC);
        Buffer buffer = null;
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers);
            inputGate.setBufferPool(bufferPool);
            buffer = bufferPool.requestBufferBlocking();
            // trigger subscription to buffer pool
            failingRemoteIC.onSenderBacklog(1);
            successfulRemoteIC.onSenderBacklog((numExclusiveBuffers + 1));
            // recycling will call RemoteInputChannel#notifyBufferAvailable() which will fail and
            // this exception will be swallowed and set as an error in failingRemoteIC
            buffer.recycleBuffer();
            buffer = null;
            try {
                failingRemoteIC.checkError();
                Assert.fail("The input channel should have an error based on the failure in RemoteInputChannel#notifyBufferAvailable()");
            } catch (IOException e) {
                MatcherAssert.assertThat(e, Matchers.hasProperty("cause", Matchers.isA(IllegalStateException.class)));
            }
            // currently, the buffer is still enqueued in the bufferQueue of failingRemoteIC
            Assert.assertEquals(0, bufferPool.getNumberOfAvailableMemorySegments());
            buffer = successfulRemoteIC.requestBuffer();
            Assert.assertNull("buffer should still remain in failingRemoteIC", buffer);
            // releasing resources in failingRemoteIC should free the buffer again and immediately
            // recycle it into successfulRemoteIC
            failingRemoteIC.releaseAllResources();
            Assert.assertEquals(0, bufferPool.getNumberOfAvailableMemorySegments());
            buffer = successfulRemoteIC.requestBuffer();
            Assert.assertNotNull("no buffer given to successfulRemoteIC", buffer);
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, null, buffer, thrown, failingRemoteIC, successfulRemoteIC);
        }
    }

    /**
     * Tests to verify that there is no race condition with two things running in parallel:
     * requesting floating buffers on sender backlog and some other thread releasing
     * the input channel.
     */
    @Test
    public void testConcurrentOnSenderBacklogAndRelease() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(130, 32);
        final int numExclusiveBuffers = 2;
        final int numFloatingBuffers = 128;
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers);
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel.requestSubpartition(0);
            final Callable<Void> requestBufferTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    while (true) {
                        for (int j = 1; j <= numFloatingBuffers; j++) {
                            inputChannel.onSenderBacklog(j);
                        }
                        if (inputChannel.isReleased()) {
                            return null;
                        }
                    } 
                }
            };
            final Callable<Void> releaseTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    inputChannel.releaseAllResources();
                    return null;
                }
            };
            // Submit tasks and wait to finish
            submitTasksAndWaitForResults(executor, new Callable[]{ requestBufferTask, releaseTask });
            Assert.assertEquals("There should be no buffers available in the channel.", 0, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be 130 buffers available in local pool.", 130, ((bufferPool.getNumberOfAvailableMemorySegments()) + (networkBufferPool.getNumberOfAvailableMemorySegments())));
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, executor, null, thrown, inputChannel);
        }
    }

    /**
     * Tests to verify that there is no race condition with two things running in parallel:
     * requesting floating buffers on sender backlog and some other thread recycling
     * floating or exclusive buffers.
     */
    @Test
    public void testConcurrentOnSenderBacklogAndRecycle() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(248, 32);
        final int numExclusiveSegments = 120;
        final int numFloatingBuffers = 128;
        final int backlog = 128;
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers);
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveSegments);
            inputChannel.requestSubpartition(0);
            final Callable<Void> requestBufferTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (int j = 1; j <= backlog; j++) {
                        inputChannel.onSenderBacklog(j);
                    }
                    return null;
                }
            };
            // Submit tasks and wait to finish
            submitTasksAndWaitForResults(executor, new Callable[]{ recycleExclusiveBufferTask(inputChannel, numExclusiveSegments), recycleFloatingBufferTask(bufferPool, numFloatingBuffers), requestBufferTask });
            Assert.assertEquals((("There should be " + (inputChannel.getNumberOfRequiredBuffers())) + " buffers available in channel."), inputChannel.getNumberOfRequiredBuffers(), inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals("There should be no buffers available in local pool.", 0, bufferPool.getNumberOfAvailableMemorySegments());
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, executor, null, thrown, inputChannel);
        }
    }

    /**
     * Tests to verify that there is no race condition with two things running in parallel:
     * recycling the exclusive or floating buffers and some other thread releasing the
     * input channel.
     */
    @Test
    public void testConcurrentRecycleAndRelease() throws Exception {
        // Setup
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(248, 32);
        final int numExclusiveSegments = 120;
        final int numFloatingBuffers = 128;
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers);
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveSegments);
            inputChannel.requestSubpartition(0);
            final Callable<Void> releaseTask = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    inputChannel.releaseAllResources();
                    return null;
                }
            };
            // Submit tasks and wait to finish
            submitTasksAndWaitForResults(executor, new Callable[]{ recycleExclusiveBufferTask(inputChannel, numExclusiveSegments), recycleFloatingBufferTask(bufferPool, numFloatingBuffers), releaseTask });
            Assert.assertEquals("There should be no buffers available in the channel.", 0, inputChannel.getNumberOfAvailableBuffers());
            Assert.assertEquals((("There should be " + numFloatingBuffers) + " buffers available in local pool."), numFloatingBuffers, bufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertEquals((("There should be " + numExclusiveSegments) + " buffers available in global pool."), numExclusiveSegments, networkBufferPool.getNumberOfAvailableMemorySegments());
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, executor, null, thrown, inputChannel);
        }
    }

    /**
     * Tests to verify that there is no race condition with two things running in parallel:
     * recycling exclusive buffers and recycling external buffers to the buffer pool while the
     * recycling of the exclusive buffer triggers recycling a floating buffer (FLINK-9676).
     */
    @Test
    public void testConcurrentRecycleAndRelease2() throws Exception {
        // Setup
        final int retries = 1000;
        final int numExclusiveBuffers = 2;
        final int numFloatingBuffers = 2;
        final int numTotalBuffers = numExclusiveBuffers + numFloatingBuffers;
        final NetworkBufferPool networkBufferPool = new NetworkBufferPool(numTotalBuffers, 32);
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final SingleInputGate inputGate = createSingleInputGate();
        final RemoteInputChannel inputChannel = createRemoteInputChannel(inputGate);
        inputGate.setInputChannel(inputChannel.partitionId.getPartitionId(), inputChannel);
        Throwable thrown = null;
        try {
            final BufferPool bufferPool = networkBufferPool.createBufferPool(numFloatingBuffers, numFloatingBuffers);
            inputGate.setBufferPool(bufferPool);
            inputGate.assignExclusiveSegments(networkBufferPool, numExclusiveBuffers);
            inputChannel.requestSubpartition(0);
            final Callable<Void> bufferPoolInteractionsTask = () -> {
                for (int i = 0; i < retries; ++i) {
                    Buffer buffer = bufferPool.requestBufferBlocking();
                    buffer.recycleBuffer();
                }
                return null;
            };
            final Callable<Void> channelInteractionsTask = () -> {
                ArrayList<Buffer> exclusiveBuffers = new ArrayList<>(numExclusiveBuffers);
                ArrayList<Buffer> floatingBuffers = new ArrayList<>(numExclusiveBuffers);
                try {
                    for (int i = 0; i < retries; ++i) {
                        // note: we may still have a listener on the buffer pool and receive
                        // floating buffers as soon as we take exclusive ones
                        for (int j = 0; j < numTotalBuffers; ++j) {
                            Buffer buffer = inputChannel.requestBuffer();
                            if (buffer == null) {
                                break;
                            } else {
                                // noinspection ObjectEquality
                                if ((buffer.getRecycler()) == inputChannel) {
                                    exclusiveBuffers.add(buffer);
                                } else {
                                    floatingBuffers.add(buffer);
                                }
                            }
                        }
                        // recycle excess floating buffers (will go back into the channel)
                        floatingBuffers.forEach(Buffer::recycleBuffer);
                        floatingBuffers.clear();
                        Assert.assertEquals(numExclusiveBuffers, exclusiveBuffers.size());
                        inputChannel.onSenderBacklog(0);// trigger subscription to buffer pool

                        // note: if we got a floating buffer by increasing the backlog, it will be released again when recycling the exclusive buffer, if not, we should release it once we get it
                        exclusiveBuffers.forEach(Buffer::recycleBuffer);
                        exclusiveBuffers.clear();
                    }
                } finally {
                    inputChannel.releaseAllResources();
                }
                return null;
            };
            // Submit tasks and wait to finish
            submitTasksAndWaitForResults(executor, new Callable[]{ bufferPoolInteractionsTask, channelInteractionsTask });
        } catch (Throwable t) {
            thrown = t;
        } finally {
            cleanup(networkBufferPool, executor, null, thrown, inputChannel);
        }
    }
}

