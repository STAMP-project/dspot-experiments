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
package org.apache.flink.runtime.checkpoint;


import org.junit.Assert;
import org.junit.Test;


public class CompletedCheckpointStatsSummaryTest {
    /**
     * Tests simple updates of the completed checkpoint stats.
     */
    @Test
    public void testSimpleUpdates() throws Exception {
        long triggerTimestamp = 123123L;
        long ackTimestamp = 123123 + 1212312399L;
        long stateSize = (Integer.MAX_VALUE) + 17787L;
        long alignmentBuffered = (Integer.MAX_VALUE) + 123123L;
        CompletedCheckpointStatsSummary summary = new CompletedCheckpointStatsSummary();
        Assert.assertEquals(0, summary.getStateSizeStats().getCount());
        Assert.assertEquals(0, summary.getEndToEndDurationStats().getCount());
        Assert.assertEquals(0, summary.getAlignmentBufferedStats().getCount());
        int numCheckpoints = 10;
        for (int i = 0; i < numCheckpoints; i++) {
            CompletedCheckpointStats completed = createCompletedCheckpoint(i, triggerTimestamp, (ackTimestamp + i), (stateSize + i), (alignmentBuffered + i));
            summary.updateSummary(completed);
            Assert.assertEquals((i + 1), summary.getStateSizeStats().getCount());
            Assert.assertEquals((i + 1), summary.getEndToEndDurationStats().getCount());
            Assert.assertEquals((i + 1), summary.getAlignmentBufferedStats().getCount());
        }
        MinMaxAvgStats stateSizeStats = summary.getStateSizeStats();
        Assert.assertEquals(stateSize, stateSizeStats.getMinimum());
        Assert.assertEquals(((stateSize + numCheckpoints) - 1), stateSizeStats.getMaximum());
        MinMaxAvgStats durationStats = summary.getEndToEndDurationStats();
        Assert.assertEquals((ackTimestamp - triggerTimestamp), durationStats.getMinimum());
        Assert.assertEquals((((ackTimestamp - triggerTimestamp) + numCheckpoints) - 1), durationStats.getMaximum());
        MinMaxAvgStats alignmentBufferedStats = summary.getAlignmentBufferedStats();
        Assert.assertEquals(alignmentBuffered, alignmentBufferedStats.getMinimum());
        Assert.assertEquals(((alignmentBuffered + numCheckpoints) - 1), alignmentBufferedStats.getMaximum());
    }
}

