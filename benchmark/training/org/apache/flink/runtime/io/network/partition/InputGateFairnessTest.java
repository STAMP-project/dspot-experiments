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
package org.apache.flink.runtime.io.network.partition;


import EndOfPartitionEvent.INSTANCE;
import ResultPartitionType.PIPELINED;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.ConnectionManager;
import org.apache.flink.runtime.io.network.TaskEventDispatcher;
import org.apache.flink.runtime.io.network.api.serialization.EventSerializer;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.io.network.buffer.BufferConsumer;
import org.apache.flink.runtime.io.network.partition.consumer.BufferOrEvent;
import org.apache.flink.runtime.io.network.partition.consumer.InputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.LocalInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.RemoteInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.runtime.io.network.util.TestBufferFactory;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.IntermediateResultPartitionID;
import org.apache.flink.runtime.metrics.groups.TaskIOMetricGroup;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.runtime.taskmanager.TaskActions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests verifying fairness in input gates.
 */
public class InputGateFairnessTest {
    @Test
    public void testFairConsumptionLocalChannelsPreFilled() throws Exception {
        final int numberOfChannels = 37;
        final int buffersPerChannel = 27;
        final ResultPartition resultPartition = Mockito.mock(ResultPartition.class);
        final BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(42);
        // ----- create some source channels and fill them with buffers -----
        final PipelinedSubpartition[] sources = new PipelinedSubpartition[numberOfChannels];
        for (int i = 0; i < numberOfChannels; i++) {
            PipelinedSubpartition partition = new PipelinedSubpartition(0, resultPartition);
            for (int p = 0; p < buffersPerChannel; p++) {
                partition.add(bufferConsumer.copy());
            }
            partition.finish();
            sources[i] = partition;
        }
        // ----- create reading side -----
        ResultPartitionManager resultPartitionManager = InputChannelTestUtils.createResultPartitionManager(sources);
        SingleInputGate gate = new InputGateFairnessTest.FairnessVerifyingInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
        for (int i = 0; i < numberOfChannels; i++) {
            LocalInputChannel channel = new LocalInputChannel(gate, i, new ResultPartitionID(), resultPartitionManager, Mockito.mock(TaskEventDispatcher.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
            gate.setInputChannel(new IntermediateResultPartitionID(), channel);
        }
        // read all the buffers and the EOF event
        for (int i = numberOfChannels * (buffersPerChannel + 1); i > 0; --i) {
            Assert.assertNotNull(gate.getNextBufferOrEvent());
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (PipelinedSubpartition source : sources) {
                int size = source.getCurrentNumberOfBuffers();
                min = Math.min(min, size);
                max = Math.max(max, size);
            }
            Assert.assertTrue(((max == min) || (max == (min + 1))));
        }
        Assert.assertFalse(gate.getNextBufferOrEvent().isPresent());
    }

    @Test
    public void testFairConsumptionLocalChannels() throws Exception {
        final int numberOfChannels = 37;
        final int buffersPerChannel = 27;
        final ResultPartition resultPartition = Mockito.mock(ResultPartition.class);
        try (BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(42)) {
            // ----- create some source channels and fill them with one buffer each -----
            final PipelinedSubpartition[] sources = new PipelinedSubpartition[numberOfChannels];
            for (int i = 0; i < numberOfChannels; i++) {
                sources[i] = new PipelinedSubpartition(0, resultPartition);
            }
            // ----- create reading side -----
            ResultPartitionManager resultPartitionManager = InputChannelTestUtils.createResultPartitionManager(sources);
            SingleInputGate gate = new InputGateFairnessTest.FairnessVerifyingInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
            for (int i = 0; i < numberOfChannels; i++) {
                LocalInputChannel channel = new LocalInputChannel(gate, i, new ResultPartitionID(), resultPartitionManager, Mockito.mock(TaskEventDispatcher.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
                gate.setInputChannel(new IntermediateResultPartitionID(), channel);
            }
            // seed one initial buffer
            sources[12].add(bufferConsumer.copy());
            // read all the buffers and the EOF event
            for (int i = 0; i < (numberOfChannels * buffersPerChannel); i++) {
                Assert.assertNotNull(gate.getNextBufferOrEvent());
                int min = Integer.MAX_VALUE;
                int max = 0;
                for (PipelinedSubpartition source : sources) {
                    int size = source.getCurrentNumberOfBuffers();
                    min = Math.min(min, size);
                    max = Math.max(max, size);
                }
                Assert.assertTrue(((max == min) || (max == (min + 1))));
                if ((i % (2 * numberOfChannels)) == 0) {
                    // add three buffers to each channel, in random order
                    fillRandom(sources, 3, bufferConsumer);
                }
            }
            // there is still more in the queues
        }
    }

    @Test
    public void testFairConsumptionRemoteChannelsPreFilled() throws Exception {
        final int numberOfChannels = 37;
        final int buffersPerChannel = 27;
        final Buffer mockBuffer = TestBufferFactory.createBuffer(42);
        // ----- create some source channels and fill them with buffers -----
        SingleInputGate gate = new InputGateFairnessTest.FairnessVerifyingInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
        final ConnectionManager connManager = InputChannelTestUtils.createDummyConnectionManager();
        final RemoteInputChannel[] channels = new RemoteInputChannel[numberOfChannels];
        for (int i = 0; i < numberOfChannels; i++) {
            RemoteInputChannel channel = new RemoteInputChannel(gate, i, new ResultPartitionID(), Mockito.mock(ConnectionID.class), connManager, 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
            channels[i] = channel;
            for (int p = 0; p < buffersPerChannel; p++) {
                channel.onBuffer(mockBuffer, p, (-1));
            }
            channel.onBuffer(EventSerializer.toBuffer(INSTANCE), buffersPerChannel, (-1));
            gate.setInputChannel(new IntermediateResultPartitionID(), channel);
        }
        // read all the buffers and the EOF event
        for (int i = numberOfChannels * (buffersPerChannel + 1); i > 0; --i) {
            Assert.assertNotNull(gate.getNextBufferOrEvent());
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (RemoteInputChannel channel : channels) {
                int size = channel.getNumberOfQueuedBuffers();
                min = Math.min(min, size);
                max = Math.max(max, size);
            }
            Assert.assertTrue(((max == min) || (max == (min + 1))));
        }
        Assert.assertFalse(gate.getNextBufferOrEvent().isPresent());
    }

    @Test
    public void testFairConsumptionRemoteChannels() throws Exception {
        final int numberOfChannels = 37;
        final int buffersPerChannel = 27;
        final Buffer mockBuffer = TestBufferFactory.createBuffer(42);
        // ----- create some source channels and fill them with buffers -----
        SingleInputGate gate = new InputGateFairnessTest.FairnessVerifyingInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
        final ConnectionManager connManager = InputChannelTestUtils.createDummyConnectionManager();
        final RemoteInputChannel[] channels = new RemoteInputChannel[numberOfChannels];
        final int[] channelSequenceNums = new int[numberOfChannels];
        for (int i = 0; i < numberOfChannels; i++) {
            RemoteInputChannel channel = new RemoteInputChannel(gate, i, new ResultPartitionID(), Mockito.mock(ConnectionID.class), connManager, 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
            channels[i] = channel;
            gate.setInputChannel(new IntermediateResultPartitionID(), channel);
        }
        channels[11].onBuffer(mockBuffer, 0, (-1));
        (channelSequenceNums[11])++;
        // read all the buffers and the EOF event
        for (int i = 0; i < (numberOfChannels * buffersPerChannel); i++) {
            Assert.assertNotNull(gate.getNextBufferOrEvent());
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (RemoteInputChannel channel : channels) {
                int size = channel.getNumberOfQueuedBuffers();
                min = Math.min(min, size);
                max = Math.max(max, size);
            }
            Assert.assertTrue(((max == min) || (max == (min + 1))));
            if ((i % (2 * numberOfChannels)) == 0) {
                // add three buffers to each channel, in random order
                fillRandom(channels, channelSequenceNums, 3, mockBuffer);
            }
        }
    }

    // ------------------------------------------------------------------------
    private static class FairnessVerifyingInputGate extends SingleInputGate {
        private final ArrayDeque<InputChannel> channelsWithData;

        private final HashSet<InputChannel> uniquenessChecker;

        @SuppressWarnings("unchecked")
        public FairnessVerifyingInputGate(String owningTaskName, JobID jobId, IntermediateDataSetID consumedResultId, int consumedSubpartitionIndex, int numberOfInputChannels, TaskActions taskActions, TaskIOMetricGroup metrics, boolean isCreditBased) {
            super(owningTaskName, jobId, consumedResultId, PIPELINED, consumedSubpartitionIndex, numberOfInputChannels, taskActions, metrics, isCreditBased);
            try {
                Field f = SingleInputGate.class.getDeclaredField("inputChannelsWithData");
                f.setAccessible(true);
                channelsWithData = ((ArrayDeque<InputChannel>) (f.get(this)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.uniquenessChecker = new HashSet();
        }

        @Override
        public Optional<BufferOrEvent> getNextBufferOrEvent() throws IOException, InterruptedException {
            synchronized(channelsWithData) {
                Assert.assertTrue("too many input channels", ((channelsWithData.size()) <= (getNumberOfInputChannels())));
                ensureUnique(channelsWithData);
            }
            return super.getNextBufferOrEvent();
        }

        private void ensureUnique(Collection<InputChannel> channels) {
            HashSet<InputChannel> uniquenessChecker = this.uniquenessChecker;
            for (InputChannel channel : channels) {
                if (!(uniquenessChecker.add(channel))) {
                    Assert.fail(("Duplicate channel in input gate: " + channel));
                }
            }
            Assert.assertTrue("found duplicate input channels", ((uniquenessChecker.size()) == (channels.size())));
            uniquenessChecker.clear();
        }
    }
}

