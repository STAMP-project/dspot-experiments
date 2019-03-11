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
package org.apache.flink.runtime.taskexecutor.slot;


import ResourceProfile.UNKNOWN;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.collections.IteratorUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.shaded.guava18.com.google.common.collect.Sets;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link TaskSlotTable}.
 */
public class TaskSlotTableTest extends TestLogger {
    private static final Time SLOT_TIMEOUT = Time.seconds(100L);

    /**
     * Tests that one can can mark allocated slots as active.
     */
    @Test
    public void testTryMarkSlotActive() throws SlotNotFoundException {
        final TaskSlotTable taskSlotTable = createTaskSlotTable(Collections.nCopies(3, UNKNOWN));
        try {
            taskSlotTable.start(new TestingSlotActionsBuilder().build());
            final JobID jobId1 = new JobID();
            final AllocationID allocationId1 = new AllocationID();
            taskSlotTable.allocateSlot(0, jobId1, allocationId1, TaskSlotTableTest.SLOT_TIMEOUT);
            final AllocationID allocationId2 = new AllocationID();
            taskSlotTable.allocateSlot(1, jobId1, allocationId2, TaskSlotTableTest.SLOT_TIMEOUT);
            final AllocationID allocationId3 = new AllocationID();
            final JobID jobId2 = new JobID();
            taskSlotTable.allocateSlot(2, jobId2, allocationId3, TaskSlotTableTest.SLOT_TIMEOUT);
            taskSlotTable.markSlotActive(allocationId1);
            Assert.assertThat(taskSlotTable.isAllocated(0, jobId1, allocationId1), Matchers.is(true));
            Assert.assertThat(taskSlotTable.isAllocated(1, jobId1, allocationId2), Matchers.is(true));
            Assert.assertThat(taskSlotTable.isAllocated(2, jobId2, allocationId3), Matchers.is(true));
            Assert.assertThat(IteratorUtils.toList(taskSlotTable.getActiveSlots(jobId1)), Matchers.is(Matchers.equalTo(Arrays.asList(allocationId1))));
            Assert.assertThat(taskSlotTable.tryMarkSlotActive(jobId1, allocationId1), Matchers.is(true));
            Assert.assertThat(taskSlotTable.tryMarkSlotActive(jobId1, allocationId2), Matchers.is(true));
            Assert.assertThat(taskSlotTable.tryMarkSlotActive(jobId1, allocationId3), Matchers.is(false));
            Assert.assertThat(Sets.newHashSet(taskSlotTable.getActiveSlots(jobId1)), Matchers.is(Matchers.equalTo(new java.util.HashSet(Arrays.asList(allocationId2, allocationId1)))));
        } finally {
            taskSlotTable.stop();
        }
    }
}

