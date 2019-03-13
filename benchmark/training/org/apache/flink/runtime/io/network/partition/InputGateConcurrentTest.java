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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.ConnectionManager;
import org.apache.flink.runtime.io.network.TaskEventDispatcher;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.io.network.buffer.BufferConsumer;
import org.apache.flink.runtime.io.network.partition.consumer.LocalInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.RemoteInputChannel;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.IntermediateResultPartitionID;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.runtime.taskmanager.TaskActions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static ResultPartitionType.PIPELINED;


/**
 * Concurrency tests for input gates.
 */
public class InputGateConcurrentTest {
    @Test
    public void testConsumptionWithLocalChannels() throws Exception {
        final int numberOfChannels = 11;
        final int buffersPerChannel = 1000;
        final ResultPartition resultPartition = Mockito.mock(ResultPartition.class);
        final PipelinedSubpartition[] partitions = new PipelinedSubpartition[numberOfChannels];
        final InputGateConcurrentTest.Source[] sources = new InputGateConcurrentTest.Source[numberOfChannels];
        final ResultPartitionManager resultPartitionManager = InputChannelTestUtils.createResultPartitionManager(partitions);
        final SingleInputGate gate = new SingleInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), PIPELINED, 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
        for (int i = 0; i < numberOfChannels; i++) {
            LocalInputChannel channel = new LocalInputChannel(gate, i, new ResultPartitionID(), resultPartitionManager, Mockito.mock(TaskEventDispatcher.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
            gate.setInputChannel(new IntermediateResultPartitionID(), channel);
            partitions[i] = new PipelinedSubpartition(0, resultPartition);
            sources[i] = new InputGateConcurrentTest.PipelinedSubpartitionSource(partitions[i]);
        }
        InputGateConcurrentTest.ProducerThread producer = new InputGateConcurrentTest.ProducerThread(sources, (numberOfChannels * buffersPerChannel), 4, 10);
        InputGateConcurrentTest.ConsumerThread consumer = new InputGateConcurrentTest.ConsumerThread(gate, (numberOfChannels * buffersPerChannel));
        start();
        start();
        // the 'sync()' call checks for exceptions and failed assertions
        sync();
        sync();
    }

    @Test
    public void testConsumptionWithRemoteChannels() throws Exception {
        final int numberOfChannels = 11;
        final int buffersPerChannel = 1000;
        final ConnectionManager connManager = InputChannelTestUtils.createDummyConnectionManager();
        final InputGateConcurrentTest.Source[] sources = new InputGateConcurrentTest.Source[numberOfChannels];
        final SingleInputGate gate = new SingleInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), PIPELINED, 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
        for (int i = 0; i < numberOfChannels; i++) {
            RemoteInputChannel channel = new RemoteInputChannel(gate, i, new ResultPartitionID(), Mockito.mock(ConnectionID.class), connManager, 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
            gate.setInputChannel(new IntermediateResultPartitionID(), channel);
            sources[i] = new InputGateConcurrentTest.RemoteChannelSource(channel);
        }
        InputGateConcurrentTest.ProducerThread producer = new InputGateConcurrentTest.ProducerThread(sources, (numberOfChannels * buffersPerChannel), 4, 10);
        InputGateConcurrentTest.ConsumerThread consumer = new InputGateConcurrentTest.ConsumerThread(gate, (numberOfChannels * buffersPerChannel));
        start();
        start();
        // the 'sync()' call checks for exceptions and failed assertions
        sync();
        sync();
    }

    @Test
    public void testConsumptionWithMixedChannels() throws Exception {
        final int numberOfChannels = 61;
        final int numLocalChannels = 20;
        final int buffersPerChannel = 1000;
        // fill the local/remote decision
        List<Boolean> localOrRemote = new ArrayList<>(numberOfChannels);
        for (int i = 0; i < numberOfChannels; i++) {
            localOrRemote.add((i < numLocalChannels));
        }
        Collections.shuffle(localOrRemote);
        final ConnectionManager connManager = InputChannelTestUtils.createDummyConnectionManager();
        final ResultPartition resultPartition = Mockito.mock(ResultPartition.class);
        final PipelinedSubpartition[] localPartitions = new PipelinedSubpartition[numLocalChannels];
        final ResultPartitionManager resultPartitionManager = InputChannelTestUtils.createResultPartitionManager(localPartitions);
        final InputGateConcurrentTest.Source[] sources = new InputGateConcurrentTest.Source[numberOfChannels];
        final SingleInputGate gate = new SingleInputGate("Test Task Name", new JobID(), new IntermediateDataSetID(), PIPELINED, 0, numberOfChannels, Mockito.mock(TaskActions.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup(), true);
        for (int i = 0, local = 0; i < numberOfChannels; i++) {
            if (localOrRemote.get(i)) {
                // local channel
                PipelinedSubpartition psp = new PipelinedSubpartition(0, resultPartition);
                localPartitions[(local++)] = psp;
                sources[i] = new InputGateConcurrentTest.PipelinedSubpartitionSource(psp);
                LocalInputChannel channel = new LocalInputChannel(gate, i, new ResultPartitionID(), resultPartitionManager, Mockito.mock(TaskEventDispatcher.class), UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
                gate.setInputChannel(new IntermediateResultPartitionID(), channel);
            } else {
                // remote channel
                RemoteInputChannel channel = new RemoteInputChannel(gate, i, new ResultPartitionID(), Mockito.mock(ConnectionID.class), connManager, 0, 0, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup().getIOMetricGroup());
                gate.setInputChannel(new IntermediateResultPartitionID(), channel);
                sources[i] = new InputGateConcurrentTest.RemoteChannelSource(channel);
            }
        }
        InputGateConcurrentTest.ProducerThread producer = new InputGateConcurrentTest.ProducerThread(sources, (numberOfChannels * buffersPerChannel), 4, 10);
        InputGateConcurrentTest.ConsumerThread consumer = new InputGateConcurrentTest.ConsumerThread(gate, (numberOfChannels * buffersPerChannel));
        start();
        start();
        // the 'sync()' call checks for exceptions and failed assertions
        sync();
        sync();
    }

    // ------------------------------------------------------------------------
    // testing threads
    // ------------------------------------------------------------------------
    private abstract static class Source {
        abstract void addBufferConsumer(BufferConsumer bufferConsumer) throws Exception;

        abstract void flush();
    }

    private static class PipelinedSubpartitionSource extends InputGateConcurrentTest.Source {
        final PipelinedSubpartition partition;

        PipelinedSubpartitionSource(PipelinedSubpartition partition) {
            this.partition = partition;
        }

        @Override
        void addBufferConsumer(BufferConsumer bufferConsumer) throws Exception {
            partition.add(bufferConsumer);
        }

        @Override
        void flush() {
            partition.flush();
        }
    }

    private static class RemoteChannelSource extends InputGateConcurrentTest.Source {
        final RemoteInputChannel channel;

        private int seq = 0;

        RemoteChannelSource(RemoteInputChannel channel) {
            this.channel = channel;
        }

        @Override
        void addBufferConsumer(BufferConsumer bufferConsumer) throws Exception {
            try {
                Buffer buffer = bufferConsumer.build();
                checkState(bufferConsumer.isFinished(), "Handling of non finished buffers is not yet implemented");
                channel.onBuffer(buffer, ((seq)++), (-1));
            } finally {
                bufferConsumer.close();
            }
        }

        @Override
        void flush() {
        }
    }

    // ------------------------------------------------------------------------
    // testing threads
    // ------------------------------------------------------------------------
    private static class ProducerThread extends CheckedThread {
        private final Random rnd = new Random();

        private final InputGateConcurrentTest.Source[] sources;

        private final int numTotal;

        private final int maxChunk;

        private final int yieldAfter;

        ProducerThread(InputGateConcurrentTest.Source[] sources, int numTotal, int maxChunk, int yieldAfter) {
            super("producer");
            this.sources = sources;
            this.numTotal = numTotal;
            this.maxChunk = maxChunk;
            this.yieldAfter = yieldAfter;
        }

        @Override
        public void go() throws Exception {
            final BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(100);
            int nextYield = (numTotal) - (yieldAfter);
            for (int i = numTotal; i > 0;) {
                final int nextChannel = rnd.nextInt(sources.length);
                final int chunk = Math.min(i, ((rnd.nextInt(maxChunk)) + 1));
                final InputGateConcurrentTest.Source next = sources[nextChannel];
                for (int k = chunk; k > 0; --k) {
                    next.addBufferConsumer(bufferConsumer.copy());
                }
                i -= chunk;
                if (i <= nextYield) {
                    nextYield -= yieldAfter;
                    // noinspection CallToThreadYield
                    Thread.yield();
                }
            }
            for (InputGateConcurrentTest.Source source : sources) {
                source.flush();
            }
        }
    }

    private static class ConsumerThread extends CheckedThread {
        private final SingleInputGate gate;

        private final int numBuffers;

        ConsumerThread(SingleInputGate gate, int numBuffers) {
            super("consumer");
            this.gate = gate;
            this.numBuffers = numBuffers;
        }

        @Override
        public void go() throws Exception {
            for (int i = numBuffers; i > 0; --i) {
                Assert.assertNotNull(gate.getNextBufferOrEvent());
            }
        }
    }
}

