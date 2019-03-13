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
package org.apache.flink.runtime.jobmanager;


import java.util.BitSet;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.io.network.api.reader.RecordReader;
import org.apache.flink.runtime.io.network.api.writer.RecordWriter;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.testutils.MiniClusterResource;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.types.IntValue;
import org.apache.flink.util.TestLogger;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests that Flink can execute jobs with a higher parallelism than available number
 * of slots. This effectively tests that Flink can execute jobs with blocking results
 * in a staged fashion.
 */
public class SlotCountExceedingParallelismTest extends TestLogger {
    // Test configuration
    private static final int NUMBER_OF_TMS = 2;

    private static final int NUMBER_OF_SLOTS_PER_TM = 2;

    private static final int PARALLELISM = (SlotCountExceedingParallelismTest.NUMBER_OF_TMS) * (SlotCountExceedingParallelismTest.NUMBER_OF_SLOTS_PER_TM);

    public static final String JOB_NAME = "SlotCountExceedingParallelismTest (no slot sharing, blocking results)";

    @ClassRule
    public static final MiniClusterResource MINI_CLUSTER_RESOURCE = new MiniClusterResource(new MiniClusterResourceConfiguration.Builder().setConfiguration(SlotCountExceedingParallelismTest.getFlinkConfiguration()).setNumberTaskManagers(SlotCountExceedingParallelismTest.NUMBER_OF_TMS).setNumberSlotsPerTaskManager(SlotCountExceedingParallelismTest.NUMBER_OF_SLOTS_PER_TM).build());

    @Test
    public void testNoSlotSharingAndBlockingResultSender() throws Exception {
        // Sender with higher parallelism than available slots
        JobGraph jobGraph = createTestJobGraph(SlotCountExceedingParallelismTest.JOB_NAME, ((SlotCountExceedingParallelismTest.PARALLELISM) * 2), SlotCountExceedingParallelismTest.PARALLELISM);
        submitJobGraphAndWait(jobGraph);
    }

    @Test
    public void testNoSlotSharingAndBlockingResultReceiver() throws Exception {
        // Receiver with higher parallelism than available slots
        JobGraph jobGraph = createTestJobGraph(SlotCountExceedingParallelismTest.JOB_NAME, SlotCountExceedingParallelismTest.PARALLELISM, ((SlotCountExceedingParallelismTest.PARALLELISM) * 2));
        submitJobGraphAndWait(jobGraph);
    }

    @Test
    public void testNoSlotSharingAndBlockingResultBoth() throws Exception {
        // Both sender and receiver with higher parallelism than available slots
        JobGraph jobGraph = createTestJobGraph(SlotCountExceedingParallelismTest.JOB_NAME, ((SlotCountExceedingParallelismTest.PARALLELISM) * 2), ((SlotCountExceedingParallelismTest.PARALLELISM) * 2));
        submitJobGraphAndWait(jobGraph);
    }

    /**
     * Sends the subtask index a configurable number of times in a round-robin fashion.
     */
    public static class RoundRobinSubtaskIndexSender extends AbstractInvokable {
        public static final String CONFIG_KEY = "number-of-times-to-send";

        public RoundRobinSubtaskIndexSender(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            RecordWriter<IntValue> writer = new RecordWriter(getEnvironment().getWriter(0));
            final int numberOfTimesToSend = getTaskConfiguration().getInteger(SlotCountExceedingParallelismTest.RoundRobinSubtaskIndexSender.CONFIG_KEY, 0);
            final IntValue subtaskIndex = new IntValue(getEnvironment().getTaskInfo().getIndexOfThisSubtask());
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

    /**
     * Expects to receive the subtask index from a configurable number of sender tasks.
     */
    public static class SubtaskIndexReceiver extends AbstractInvokable {
        public static final String CONFIG_KEY = "number-of-indexes-to-receive";

        public SubtaskIndexReceiver(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            RecordReader<IntValue> reader = new RecordReader(getEnvironment().getInputGate(0), IntValue.class, getEnvironment().getTaskManagerInfo().getTmpDirectories());
            try {
                final int numberOfSubtaskIndexesToReceive = getTaskConfiguration().getInteger(SlotCountExceedingParallelismTest.SubtaskIndexReceiver.CONFIG_KEY, 0);
                final BitSet receivedSubtaskIndexes = new BitSet(numberOfSubtaskIndexesToReceive);
                IntValue record;
                int numberOfReceivedSubtaskIndexes = 0;
                while ((record = reader.next()) != null) {
                    // Check that we don't receive more than expected
                    numberOfReceivedSubtaskIndexes++;
                    if (numberOfReceivedSubtaskIndexes > numberOfSubtaskIndexesToReceive) {
                        throw new IllegalStateException("Received more records than expected.");
                    }
                    int subtaskIndex = record.getValue();
                    // Check that we only receive each subtask index once
                    if (receivedSubtaskIndexes.get(subtaskIndex)) {
                        throw new IllegalStateException("Received expected subtask index twice.");
                    } else {
                        receivedSubtaskIndexes.set(subtaskIndex, true);
                    }
                } 
                // Check that we have received all expected subtask indexes
                if ((receivedSubtaskIndexes.cardinality()) != numberOfSubtaskIndexesToReceive) {
                    throw new IllegalStateException(("Finished receive, but did not receive " + "all expected subtask indexes."));
                }
            } finally {
                reader.clearBuffers();
            }
        }
    }
}

