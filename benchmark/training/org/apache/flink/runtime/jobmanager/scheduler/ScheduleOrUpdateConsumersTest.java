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
package org.apache.flink.runtime.jobmanager.scheduler;


import DistributionPattern.ALL_TO_ALL;
import ResultPartitionType.BLOCKING;
import ResultPartitionType.PIPELINED;
import java.util.List;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.io.network.api.writer.RecordWriter;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobmanager.SlotCountExceedingParallelismTest;
import org.apache.flink.runtime.testutils.MiniClusterResource;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.types.IntValue;
import org.apache.flink.util.TestLogger;
import org.junit.ClassRule;
import org.junit.Test;

import static org.apache.flink.runtime.jobmanager.SlotCountExceedingParallelismTest.SubtaskIndexReceiver.CONFIG_KEY;


/**
 * Tests for the lazy scheduling/updating of consumers depending on the
 * producers result.
 */
public class ScheduleOrUpdateConsumersTest extends TestLogger {
    private static final int NUMBER_OF_TMS = 2;

    private static final int NUMBER_OF_SLOTS_PER_TM = 2;

    private static final int PARALLELISM = (ScheduleOrUpdateConsumersTest.NUMBER_OF_TMS) * (ScheduleOrUpdateConsumersTest.NUMBER_OF_SLOTS_PER_TM);

    @ClassRule
    public static final MiniClusterResource MINI_CLUSTER_RESOURCE = new MiniClusterResource(new MiniClusterResourceConfiguration.Builder().setConfiguration(ScheduleOrUpdateConsumersTest.getFlinkConfiguration()).setNumberTaskManagers(ScheduleOrUpdateConsumersTest.NUMBER_OF_TMS).setNumberSlotsPerTaskManager(ScheduleOrUpdateConsumersTest.NUMBER_OF_SLOTS_PER_TM).build());

    /**
     * Tests notifications of multiple receivers when a task produces both a pipelined and blocking
     * result.
     *
     * <pre>
     *                             +----------+
     *            +-- pipelined -> | Receiver |
     * +--------+ |                +----------+
     * | Sender |-|
     * +--------+ |                +----------+
     *            +-- blocking --> | Receiver |
     *                             +----------+
     * </pre>
     *
     * <p>The pipelined receiver gets deployed after the first buffer is available and the blocking
     * one after all subtasks are finished.
     */
    @Test
    public void testMixedPipelinedAndBlockingResults() throws Exception {
        final JobVertex sender = new JobVertex("Sender");
        sender.setInvokableClass(ScheduleOrUpdateConsumersTest.BinaryRoundRobinSubtaskIndexSender.class);
        sender.getConfiguration().setInteger(ScheduleOrUpdateConsumersTest.BinaryRoundRobinSubtaskIndexSender.CONFIG_KEY, ScheduleOrUpdateConsumersTest.PARALLELISM);
        sender.setParallelism(ScheduleOrUpdateConsumersTest.PARALLELISM);
        final JobVertex pipelinedReceiver = new JobVertex("Pipelined Receiver");
        pipelinedReceiver.setInvokableClass(SlotCountExceedingParallelismTest.SubtaskIndexReceiver.class);
        pipelinedReceiver.getConfiguration().setInteger(CONFIG_KEY, ScheduleOrUpdateConsumersTest.PARALLELISM);
        pipelinedReceiver.setParallelism(ScheduleOrUpdateConsumersTest.PARALLELISM);
        pipelinedReceiver.connectNewDataSetAsInput(sender, ALL_TO_ALL, PIPELINED);
        final JobVertex blockingReceiver = new JobVertex("Blocking Receiver");
        blockingReceiver.setInvokableClass(SlotCountExceedingParallelismTest.SubtaskIndexReceiver.class);
        blockingReceiver.getConfiguration().setInteger(CONFIG_KEY, ScheduleOrUpdateConsumersTest.PARALLELISM);
        blockingReceiver.setParallelism(ScheduleOrUpdateConsumersTest.PARALLELISM);
        blockingReceiver.connectNewDataSetAsInput(sender, ALL_TO_ALL, BLOCKING);
        SlotSharingGroup slotSharingGroup = new SlotSharingGroup(sender.getID(), pipelinedReceiver.getID(), blockingReceiver.getID());
        sender.setSlotSharingGroup(slotSharingGroup);
        pipelinedReceiver.setSlotSharingGroup(slotSharingGroup);
        blockingReceiver.setSlotSharingGroup(slotSharingGroup);
        final JobGraph jobGraph = new JobGraph("Mixed pipelined and blocking result", sender, pipelinedReceiver, blockingReceiver);
        ScheduleOrUpdateConsumersTest.MINI_CLUSTER_RESOURCE.getMiniCluster().executeJobBlocking(jobGraph);
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Invokable which writes a configurable number of events to a pipelined
     * and blocking partition alternatingly.
     */
    public static class BinaryRoundRobinSubtaskIndexSender extends AbstractInvokable {
        static final String CONFIG_KEY = "number-of-times-to-send";

        public BinaryRoundRobinSubtaskIndexSender(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            List<RecordWriter<IntValue>> writers = Lists.newArrayListWithCapacity(2);
            // The order of intermediate result creation in the job graph specifies which produced
            // result partition is pipelined/blocking.
            final RecordWriter<IntValue> pipelinedWriter = new RecordWriter(getEnvironment().getWriter(0));
            final RecordWriter<IntValue> blockingWriter = new RecordWriter(getEnvironment().getWriter(1));
            writers.add(pipelinedWriter);
            writers.add(blockingWriter);
            final int numberOfTimesToSend = getTaskConfiguration().getInteger(ScheduleOrUpdateConsumersTest.BinaryRoundRobinSubtaskIndexSender.CONFIG_KEY, 0);
            final IntValue subtaskIndex = new IntValue(getEnvironment().getTaskInfo().getIndexOfThisSubtask());
            // Produce the first intermediate result and then the second in a serial fashion.
            for (RecordWriter<IntValue> writer : writers) {
                try {
                    for (int i = 0; i < numberOfTimesToSend; i++) {
                        writer.emit(subtaskIndex);
                    }
                    writer.flushAll();
                } finally {
                    writer.clearBuffers();
                }
            }
        }
    }
}

