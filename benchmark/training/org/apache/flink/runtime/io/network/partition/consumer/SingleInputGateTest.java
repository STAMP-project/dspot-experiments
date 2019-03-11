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


import ResultPartitionType.PIPELINED_BOUNDED;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.memory.MemorySegmentFactory;
import org.apache.flink.runtime.deployment.InputChannelDeploymentDescriptor;
import org.apache.flink.runtime.deployment.InputGateDeploymentDescriptor;
import org.apache.flink.runtime.deployment.ResultPartitionLocation;
import org.apache.flink.runtime.event.TaskEvent;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.ConnectionManager;
import org.apache.flink.runtime.io.network.LocalConnectionManager;
import org.apache.flink.runtime.io.network.NetworkEnvironment;
import org.apache.flink.runtime.io.network.TaskEventDispatcher;
import org.apache.flink.runtime.io.network.buffer.BufferPool;
import org.apache.flink.runtime.io.network.buffer.FreeingBufferRecycler;
import org.apache.flink.runtime.io.network.buffer.NetworkBufferPool;
import org.apache.flink.runtime.io.network.partition.BufferAvailabilityListener;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.io.network.partition.ResultPartitionManager;
import org.apache.flink.runtime.io.network.partition.ResultPartitionType;
import org.apache.flink.runtime.io.network.partition.ResultSubpartition.BufferAndBacklog;
import org.apache.flink.runtime.io.network.partition.ResultSubpartitionView;
import org.apache.flink.runtime.io.network.util.TestTaskEvent;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.IntermediateResultPartitionID;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.runtime.taskmanager.TaskActions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static java.lang.Thread.State.WAITING;


/**
 * Tests for {@link SingleInputGate}.
 */
@RunWith(Parameterized.class)
public class SingleInputGateTest {
    @Parameterized.Parameter
    public boolean enableCreditBasedFlowControl;

    /**
     * Tests basic correctness of buffer-or-event interleaving and correct <code>null</code> return
     * value after receiving all end-of-partition events.
     */
    @Test(timeout = 120 * 1000)
    public void testBasicGetNextLogic() throws Exception {
        // Setup
        final SingleInputGate inputGate = createInputGate();
        final TestInputChannel[] inputChannels = new TestInputChannel[]{ new TestInputChannel(inputGate, 0), new TestInputChannel(inputGate, 1) };
        inputGate.setInputChannel(new IntermediateResultPartitionID(), inputChannels[0]);
        inputGate.setInputChannel(new IntermediateResultPartitionID(), inputChannels[1]);
        // Test
        inputChannels[0].readBuffer();
        inputChannels[0].readBuffer();
        inputChannels[1].readBuffer();
        inputChannels[1].readEndOfPartitionEvent();
        inputChannels[0].readEndOfPartitionEvent();
        inputGate.notifyChannelNonEmpty(inputChannels[0]);
        inputGate.notifyChannelNonEmpty(inputChannels[1]);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, true, 0, true);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, true, 1, true);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, true, 0, true);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, false, 1, true);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, false, 0, false);
        // Return null when the input gate has received all end-of-partition events
        Assert.assertTrue(inputGate.isFinished());
    }

    @Test(timeout = 120 * 1000)
    public void testIsMoreAvailableReadingFromSingleInputChannel() throws Exception {
        // Setup
        final SingleInputGate inputGate = createInputGate();
        final TestInputChannel[] inputChannels = new TestInputChannel[]{ new TestInputChannel(inputGate, 0), new TestInputChannel(inputGate, 1) };
        inputGate.setInputChannel(new IntermediateResultPartitionID(), inputChannels[0]);
        inputGate.setInputChannel(new IntermediateResultPartitionID(), inputChannels[1]);
        // Test
        inputChannels[0].readBuffer();
        inputChannels[0].readBuffer(false);
        inputGate.notifyChannelNonEmpty(inputChannels[0]);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, true, 0, true);
        SingleInputGateTest.verifyBufferOrEvent(inputGate, true, 0, false);
    }

    @Test
    public void testBackwardsEventWithUninitializedChannel() throws Exception {
        // Setup environment
        final TaskEventDispatcher taskEventDispatcher = Mockito.mock(TaskEventDispatcher.class);
        Mockito.when(taskEventDispatcher.publish(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.any(TaskEvent.class))).thenReturn(true);
        final ResultSubpartitionView iterator = Mockito.mock(ResultSubpartitionView.class);
        Mockito.when(iterator.getNextBuffer()).thenReturn(new BufferAndBacklog(new org.apache.flink.runtime.io.network.buffer.NetworkBuffer(MemorySegmentFactory.allocateUnpooledSegment(1024), FreeingBufferRecycler.INSTANCE), false, 0, false));
        final ResultPartitionManager partitionManager = Mockito.mock(ResultPartitionManager.class);
        Mockito.when(partitionManager.createSubpartitionView(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(BufferAvailabilityListener.class))).thenReturn(iterator);
        // Setup reader with one local and one unknown input channel
        final SingleInputGate inputGate = createInputGate();
        final BufferPool bufferPool = Mockito.mock(BufferPool.class);
        Mockito.when(bufferPool.getNumberOfRequiredMemorySegments()).thenReturn(2);
        inputGate.setBufferPool(bufferPool);
        // Local
        ResultPartitionID localPartitionId = new ResultPartitionID(new IntermediateResultPartitionID(), new ExecutionAttemptID());
        InputChannel local = new LocalInputChannel(inputGate, 0, localPartitionId, partitionManager, taskEventDispatcher, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        // Unknown
        ResultPartitionID unknownPartitionId = new ResultPartitionID(new IntermediateResultPartitionID(), new ExecutionAttemptID());
        InputChannel unknown = new UnknownInputChannel(inputGate, 1, unknownPartitionId, partitionManager, taskEventDispatcher, Mockito.mock(ConnectionManager.class), 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        // Set channels
        inputGate.setInputChannel(localPartitionId.getPartitionId(), local);
        inputGate.setInputChannel(unknownPartitionId.getPartitionId(), unknown);
        // Request partitions
        inputGate.requestPartitions();
        // Only the local channel can request
        Mockito.verify(partitionManager, Mockito.times(1)).createSubpartitionView(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(BufferAvailabilityListener.class));
        // Send event backwards and initialize unknown channel afterwards
        final TaskEvent event = new TestTaskEvent();
        inputGate.sendTaskEvent(event);
        // Only the local channel can send out the event
        Mockito.verify(taskEventDispatcher, Mockito.times(1)).publish(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.any(TaskEvent.class));
        // After the update, the pending event should be send to local channel
        inputGate.updateInputChannel(new InputChannelDeploymentDescriptor(new ResultPartitionID(unknownPartitionId.getPartitionId(), unknownPartitionId.getProducerId()), ResultPartitionLocation.createLocal()));
        Mockito.verify(partitionManager, Mockito.times(2)).createSubpartitionView(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(BufferAvailabilityListener.class));
        Mockito.verify(taskEventDispatcher, Mockito.times(2)).publish(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.any(TaskEvent.class));
    }

    /**
     * Tests that an update channel does not trigger a partition request before the UDF has
     * requested any partitions. Otherwise, this can lead to races when registering a listener at
     * the gate (e.g. in UnionInputGate), which can result in missed buffer notifications at the
     * listener.
     */
    @Test
    public void testUpdateChannelBeforeRequest() throws Exception {
        SingleInputGate inputGate = createInputGate(1);
        ResultPartitionManager partitionManager = Mockito.mock(ResultPartitionManager.class);
        InputChannel unknown = new UnknownInputChannel(inputGate, 0, new ResultPartitionID(), partitionManager, new TaskEventDispatcher(), new LocalConnectionManager(), 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        inputGate.setInputChannel(unknown.partitionId.getPartitionId(), unknown);
        // Update to a local channel and verify that no request is triggered
        inputGate.updateInputChannel(new InputChannelDeploymentDescriptor(unknown.partitionId, ResultPartitionLocation.createLocal()));
        Mockito.verify(partitionManager, Mockito.never()).createSubpartitionView(ArgumentMatchers.any(ResultPartitionID.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(BufferAvailabilityListener.class));
    }

    /**
     * Tests that the release of the input gate is noticed while polling the
     * channels for available data.
     */
    @Test
    public void testReleaseWhilePollingChannel() throws Exception {
        final AtomicReference<Exception> asyncException = new AtomicReference<>();
        // Setup the input gate with a single channel that does nothing
        final SingleInputGate inputGate = createInputGate(1);
        InputChannel unknown = new UnknownInputChannel(inputGate, 0, new ResultPartitionID(), new ResultPartitionManager(), new TaskEventDispatcher(), new LocalConnectionManager(), 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        inputGate.setInputChannel(unknown.partitionId.getPartitionId(), unknown);
        // Start the consumer in a separate Thread
        Thread asyncConsumer = new Thread() {
            @Override
            public void run() {
                try {
                    inputGate.getNextBufferOrEvent();
                } catch (Exception e) {
                    asyncException.set(e);
                }
            }
        };
        asyncConsumer.start();
        // Wait for blocking queue poll call and release input gate
        boolean success = false;
        for (int i = 0; i < 50; i++) {
            if (asyncConsumer.isAlive()) {
                success = (asyncConsumer.getState()) == (WAITING);
            }
            if (success) {
                break;
            } else {
                // Retry
                Thread.sleep(100);
            }
        }
        // Verify that async consumer is in blocking request
        Assert.assertTrue("Did not trigger blocking buffer request.", success);
        // Release the input gate
        inputGate.releaseAllResources();
        // Wait for Thread to finish and verify expected Exceptions. If the
        // input gate status is not properly checked during requests, this
        // call will never return.
        asyncConsumer.join();
        Assert.assertNotNull(asyncException.get());
        Assert.assertEquals(IllegalStateException.class, asyncException.get().getClass());
    }

    /**
     * Tests request back off configuration is correctly forwarded to the channels.
     */
    @Test
    public void testRequestBackoffConfiguration() throws Exception {
        ResultPartitionID[] partitionIds = new ResultPartitionID[]{ new ResultPartitionID(), new ResultPartitionID(), new ResultPartitionID() };
        InputChannelDeploymentDescriptor[] channelDescs = new InputChannelDeploymentDescriptor[]{ // Local
        new InputChannelDeploymentDescriptor(partitionIds[0], ResultPartitionLocation.createLocal()), // Remote
        new InputChannelDeploymentDescriptor(partitionIds[1], ResultPartitionLocation.createRemote(new ConnectionID(new InetSocketAddress("localhost", 5000), 0))), // Unknown
        new InputChannelDeploymentDescriptor(partitionIds[2], ResultPartitionLocation.createUnknown()) };
        InputGateDeploymentDescriptor gateDesc = new InputGateDeploymentDescriptor(new IntermediateDataSetID(), ResultPartitionType.PIPELINED, 0, channelDescs);
        int initialBackoff = 137;
        int maxBackoff = 1001;
        final NetworkEnvironment netEnv = new NetworkEnvironment(100, 32, initialBackoff, maxBackoff, 2, 8, enableCreditBasedFlowControl);
        SingleInputGate gate = SingleInputGate.create("TestTask", new JobID(), new ExecutionAttemptID(), gateDesc, netEnv, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
        try {
            Assert.assertEquals(gateDesc.getConsumedPartitionType(), gate.getConsumedPartitionType());
            Map<IntermediateResultPartitionID, InputChannel> channelMap = gate.getInputChannels();
            Assert.assertEquals(3, channelMap.size());
            InputChannel localChannel = channelMap.get(partitionIds[0].getPartitionId());
            Assert.assertEquals(LocalInputChannel.class, localChannel.getClass());
            InputChannel remoteChannel = channelMap.get(partitionIds[1].getPartitionId());
            Assert.assertEquals(RemoteInputChannel.class, remoteChannel.getClass());
            InputChannel unknownChannel = channelMap.get(partitionIds[2].getPartitionId());
            Assert.assertEquals(UnknownInputChannel.class, unknownChannel.getClass());
            InputChannel[] channels = new InputChannel[]{ localChannel, remoteChannel, unknownChannel };
            for (InputChannel ch : channels) {
                Assert.assertEquals(0, ch.getCurrentBackoff());
                Assert.assertTrue(ch.increaseBackoff());
                Assert.assertEquals(initialBackoff, ch.getCurrentBackoff());
                Assert.assertTrue(ch.increaseBackoff());
                Assert.assertEquals((initialBackoff * 2), ch.getCurrentBackoff());
                Assert.assertTrue(ch.increaseBackoff());
                Assert.assertEquals(((initialBackoff * 2) * 2), ch.getCurrentBackoff());
                Assert.assertTrue(ch.increaseBackoff());
                Assert.assertEquals(maxBackoff, ch.getCurrentBackoff());
                Assert.assertFalse(ch.increaseBackoff());
            }
        } finally {
            gate.releaseAllResources();
            netEnv.shutdown();
        }
    }

    /**
     * Tests that input gate requests and assigns network buffers for remote input channel.
     */
    @Test
    public void testRequestBuffersWithRemoteInputChannel() throws Exception {
        final SingleInputGate inputGate = createInputGate(1, PIPELINED_BOUNDED);
        int buffersPerChannel = 2;
        int extraNetworkBuffersPerGate = 8;
        final NetworkEnvironment network = new NetworkEnvironment(100, 32, 0, 0, buffersPerChannel, extraNetworkBuffersPerGate, enableCreditBasedFlowControl);
        try {
            final ResultPartitionID resultPartitionId = new ResultPartitionID();
            final ConnectionID connectionId = new ConnectionID(new InetSocketAddress("localhost", 5000), 0);
            addRemoteInputChannel(network, inputGate, connectionId, resultPartitionId, 0);
            network.setupInputGate(inputGate);
            NetworkBufferPool bufferPool = network.getNetworkBufferPool();
            if (enableCreditBasedFlowControl) {
                RemoteInputChannel remote = ((RemoteInputChannel) (inputGate.getInputChannels().get(resultPartitionId.getPartitionId())));
                // only the exclusive buffers should be assigned/available now
                Assert.assertEquals(buffersPerChannel, remote.getNumberOfAvailableBuffers());
                Assert.assertEquals(((bufferPool.getTotalNumberOfMemorySegments()) - buffersPerChannel), bufferPool.getNumberOfAvailableMemorySegments());
                // note: exclusive buffers are not handed out into LocalBufferPool and are thus not counted
                Assert.assertEquals(extraNetworkBuffersPerGate, bufferPool.countBuffers());
            } else {
                Assert.assertEquals((buffersPerChannel + extraNetworkBuffersPerGate), bufferPool.countBuffers());
            }
        } finally {
            inputGate.releaseAllResources();
            network.shutdown();
        }
    }

    /**
     * Tests that input gate requests and assigns network buffers when unknown input channel
     * updates to remote input channel.
     */
    @Test
    public void testRequestBuffersWithUnknownInputChannel() throws Exception {
        final SingleInputGate inputGate = createInputGate(1, PIPELINED_BOUNDED);
        int buffersPerChannel = 2;
        int extraNetworkBuffersPerGate = 8;
        final NetworkEnvironment network = new NetworkEnvironment(100, 32, 0, 0, buffersPerChannel, extraNetworkBuffersPerGate, enableCreditBasedFlowControl);
        try {
            final ResultPartitionID resultPartitionId = new ResultPartitionID();
            addUnknownInputChannel(network, inputGate, resultPartitionId, 0);
            network.setupInputGate(inputGate);
            NetworkBufferPool bufferPool = network.getNetworkBufferPool();
            if (enableCreditBasedFlowControl) {
                Assert.assertEquals(bufferPool.getTotalNumberOfMemorySegments(), bufferPool.getNumberOfAvailableMemorySegments());
                // note: exclusive buffers are not handed out into LocalBufferPool and are thus not counted
                Assert.assertEquals(extraNetworkBuffersPerGate, bufferPool.countBuffers());
            } else {
                Assert.assertEquals((buffersPerChannel + extraNetworkBuffersPerGate), bufferPool.countBuffers());
            }
            // Trigger updates to remote input channel from unknown input channel
            final ConnectionID connectionId = new ConnectionID(new InetSocketAddress("localhost", 5000), 0);
            inputGate.updateInputChannel(new InputChannelDeploymentDescriptor(resultPartitionId, ResultPartitionLocation.createRemote(connectionId)));
            if (enableCreditBasedFlowControl) {
                RemoteInputChannel remote = ((RemoteInputChannel) (inputGate.getInputChannels().get(resultPartitionId.getPartitionId())));
                // only the exclusive buffers should be assigned/available now
                Assert.assertEquals(buffersPerChannel, remote.getNumberOfAvailableBuffers());
                Assert.assertEquals(((bufferPool.getTotalNumberOfMemorySegments()) - buffersPerChannel), bufferPool.getNumberOfAvailableMemorySegments());
                // note: exclusive buffers are not handed out into LocalBufferPool and are thus not counted
                Assert.assertEquals(extraNetworkBuffersPerGate, bufferPool.countBuffers());
            } else {
                Assert.assertEquals((buffersPerChannel + extraNetworkBuffersPerGate), bufferPool.countBuffers());
            }
        } finally {
            inputGate.releaseAllResources();
            network.shutdown();
        }
    }

    /**
     * Tests that input gate can successfully convert unknown input channels into local and remote
     * channels.
     */
    @Test
    public void testUpdateUnknownInputChannel() throws Exception {
        final SingleInputGate inputGate = createInputGate(2);
        int buffersPerChannel = 2;
        final NetworkEnvironment network = new NetworkEnvironment(100, 32, 0, 0, buffersPerChannel, 8, enableCreditBasedFlowControl);
        try {
            final ResultPartitionID localResultPartitionId = new ResultPartitionID();
            addUnknownInputChannel(network, inputGate, localResultPartitionId, 0);
            final ResultPartitionID remoteResultPartitionId = new ResultPartitionID();
            addUnknownInputChannel(network, inputGate, remoteResultPartitionId, 1);
            network.setupInputGate(inputGate);
            MatcherAssert.assertThat(inputGate.getInputChannels().get(remoteResultPartitionId.getPartitionId()), Matchers.is(Matchers.instanceOf(UnknownInputChannel.class)));
            MatcherAssert.assertThat(inputGate.getInputChannels().get(localResultPartitionId.getPartitionId()), Matchers.is(Matchers.instanceOf(UnknownInputChannel.class)));
            // Trigger updates to remote input channel from unknown input channel
            final ConnectionID remoteConnectionId = new ConnectionID(new InetSocketAddress("localhost", 5000), 0);
            inputGate.updateInputChannel(new InputChannelDeploymentDescriptor(remoteResultPartitionId, ResultPartitionLocation.createRemote(remoteConnectionId)));
            MatcherAssert.assertThat(inputGate.getInputChannels().get(remoteResultPartitionId.getPartitionId()), Matchers.is(Matchers.instanceOf(RemoteInputChannel.class)));
            MatcherAssert.assertThat(inputGate.getInputChannels().get(localResultPartitionId.getPartitionId()), Matchers.is(Matchers.instanceOf(UnknownInputChannel.class)));
            // Trigger updates to local input channel from unknown input channel
            inputGate.updateInputChannel(new InputChannelDeploymentDescriptor(localResultPartitionId, ResultPartitionLocation.createLocal()));
            MatcherAssert.assertThat(inputGate.getInputChannels().get(remoteResultPartitionId.getPartitionId()), Matchers.is(Matchers.instanceOf(RemoteInputChannel.class)));
            MatcherAssert.assertThat(inputGate.getInputChannels().get(localResultPartitionId.getPartitionId()), Matchers.is(Matchers.instanceOf(LocalInputChannel.class)));
        } finally {
            inputGate.releaseAllResources();
            network.shutdown();
        }
    }
}

