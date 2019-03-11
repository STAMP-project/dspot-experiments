/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.common.actions;


import Granularities.DAY;
import Granularities.FIFTEEN_MINUTE;
import Granularities.FIVE_MINUTE;
import Granularities.HOUR;
import Granularities.MINUTE;
import Granularities.NONE;
import Granularities.SECOND;
import Granularities.SIX_HOUR;
import Granularities.TEN_MINUTE;
import Granularities.THIRTY_MINUTE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.druid.indexing.common.TaskLock;
import org.apache.druid.indexing.common.task.NoopTask;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.segment.realtime.appenderator.SegmentIdWithShardSpec;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.LinearShardSpec;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.apache.druid.timeline.partition.SingleDimensionShardSpec;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SegmentAllocateActionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TaskActionTestKit taskActionTestKit = new TaskActionTestKit();

    private static final String DATA_SOURCE = "none";

    private static final DateTime PARTY_TIME = DateTimes.of("1999");

    private static final DateTime THE_DISTANT_FUTURE = DateTimes.of("3000");

    @Test
    public void testGranularitiesFinerThanDay() {
        Assert.assertEquals(ImmutableList.of(DAY, SIX_HOUR, HOUR, THIRTY_MINUTE, FIFTEEN_MINUTE, TEN_MINUTE, FIVE_MINUTE, MINUTE, SECOND), Granularity.granularitiesFinerThan(DAY));
    }

    @Test
    public void testGranularitiesFinerThanHour() {
        Assert.assertEquals(ImmutableList.of(HOUR, THIRTY_MINUTE, FIFTEEN_MINUTE, TEN_MINUTE, FIVE_MINUTE, MINUTE, SECOND), Granularity.granularitiesFinerThan(HOUR));
    }

    @Test
    public void testManySegmentsSameInterval() {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        final SegmentIdWithShardSpec id2 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id1.toString());
        final SegmentIdWithShardSpec id3 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id2.toString());
        final TaskLock partyLock = Iterables.getOnlyElement(FluentIterable.from(taskActionTestKit.getTaskLockbox().findLocksForTask(task)).filter(( input) -> input.getInterval().contains(PARTY_TIME)));
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(0, 0)));
        assertSameIdentifier(id2, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(1, 0)));
        assertSameIdentifier(id3, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(2, 0)));
    }

    @Test
    public void testResumeSequence() {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        final SegmentIdWithShardSpec id2 = allocate(task, SegmentAllocateActionTest.THE_DISTANT_FUTURE, NONE, HOUR, "s1", id1.toString());
        final SegmentIdWithShardSpec id3 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id2.toString());
        final SegmentIdWithShardSpec id4 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id1.toString());
        final SegmentIdWithShardSpec id5 = allocate(task, SegmentAllocateActionTest.THE_DISTANT_FUTURE, NONE, HOUR, "s1", id1.toString());
        final SegmentIdWithShardSpec id6 = allocate(task, SegmentAllocateActionTest.THE_DISTANT_FUTURE, NONE, MINUTE, "s1", id1.toString());
        final SegmentIdWithShardSpec id7 = allocate(task, SegmentAllocateActionTest.THE_DISTANT_FUTURE, NONE, DAY, "s1", id1.toString());
        final TaskLock partyLock = Iterables.getOnlyElement(FluentIterable.from(taskActionTestKit.getTaskLockbox().findLocksForTask(task)).filter(new Predicate<TaskLock>() {
            @Override
            public boolean apply(TaskLock input) {
                return input.getInterval().contains(SegmentAllocateActionTest.PARTY_TIME);
            }
        }));
        final TaskLock futureLock = Iterables.getOnlyElement(FluentIterable.from(taskActionTestKit.getTaskLockbox().findLocksForTask(task)).filter(new Predicate<TaskLock>() {
            @Override
            public boolean apply(TaskLock input) {
                return input.getInterval().contains(SegmentAllocateActionTest.THE_DISTANT_FUTURE);
            }
        }));
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(0, 0)));
        assertSameIdentifier(id2, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.THE_DISTANT_FUTURE), futureLock.getVersion(), new NumberedShardSpec(0, 0)));
        assertSameIdentifier(id3, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(1, 0)));
        Assert.assertNull(id4);
        assertSameIdentifier(id5, id2);
        Assert.assertNull(id6);
        assertSameIdentifier(id7, id2);
    }

    @Test
    public void testMultipleSequences() {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        final SegmentIdWithShardSpec id2 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s2", null);
        final SegmentIdWithShardSpec id3 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id1.toString());
        final SegmentIdWithShardSpec id4 = allocate(task, SegmentAllocateActionTest.THE_DISTANT_FUTURE, NONE, HOUR, "s1", id3.toString());
        final SegmentIdWithShardSpec id5 = allocate(task, SegmentAllocateActionTest.THE_DISTANT_FUTURE, NONE, HOUR, "s2", id2.toString());
        final SegmentIdWithShardSpec id6 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        final TaskLock partyLock = Iterables.getOnlyElement(FluentIterable.from(taskActionTestKit.getTaskLockbox().findLocksForTask(task)).filter(new Predicate<TaskLock>() {
            @Override
            public boolean apply(TaskLock input) {
                return input.getInterval().contains(SegmentAllocateActionTest.PARTY_TIME);
            }
        }));
        final TaskLock futureLock = Iterables.getOnlyElement(FluentIterable.from(taskActionTestKit.getTaskLockbox().findLocksForTask(task)).filter(new Predicate<TaskLock>() {
            @Override
            public boolean apply(TaskLock input) {
                return input.getInterval().contains(SegmentAllocateActionTest.THE_DISTANT_FUTURE);
            }
        }));
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(0, 0)));
        assertSameIdentifier(id2, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(1, 0)));
        assertSameIdentifier(id3, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), partyLock.getVersion(), new NumberedShardSpec(2, 0)));
        assertSameIdentifier(id4, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.THE_DISTANT_FUTURE), futureLock.getVersion(), new NumberedShardSpec(0, 0)));
        assertSameIdentifier(id5, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.THE_DISTANT_FUTURE), futureLock.getVersion(), new NumberedShardSpec(1, 0)));
        assertSameIdentifier(id6, id1);
    }

    @Test
    public void testAddToExistingLinearShardSpecsSameGranularity() throws Exception {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getMetadataStorageCoordinator().announceHistoricalSegments(ImmutableSet.of(DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new LinearShardSpec(0)).build(), DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new LinearShardSpec(1)).build()));
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        final SegmentIdWithShardSpec id2 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id1.toString());
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), SegmentAllocateActionTest.PARTY_TIME.toString(), new LinearShardSpec(2)));
        assertSameIdentifier(id2, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), SegmentAllocateActionTest.PARTY_TIME.toString(), new LinearShardSpec(3)));
    }

    @Test
    public void testAddToExistingNumberedShardSpecsSameGranularity() throws Exception {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getMetadataStorageCoordinator().announceHistoricalSegments(ImmutableSet.of(DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(0, 2)).build(), DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(1, 2)).build()));
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        final SegmentIdWithShardSpec id2 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", id1.toString());
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), SegmentAllocateActionTest.PARTY_TIME.toString(), new NumberedShardSpec(2, 2)));
        assertSameIdentifier(id2, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), SegmentAllocateActionTest.PARTY_TIME.toString(), new NumberedShardSpec(3, 2)));
    }

    @Test
    public void testAddToExistingNumberedShardSpecsCoarserPreferredGranularity() throws Exception {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getMetadataStorageCoordinator().announceHistoricalSegments(ImmutableSet.of(DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(0, 2)).build(), DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(1, 2)).build()));
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, DAY, "s1", null);
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), SegmentAllocateActionTest.PARTY_TIME.toString(), new NumberedShardSpec(2, 2)));
    }

    @Test
    public void testAddToExistingNumberedShardSpecsFinerPreferredGranularity() throws Exception {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getMetadataStorageCoordinator().announceHistoricalSegments(ImmutableSet.of(DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(0, 2)).build(), DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(1, 2)).build()));
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, MINUTE, "s1", null);
        assertSameIdentifier(id1, new SegmentIdWithShardSpec(SegmentAllocateActionTest.DATA_SOURCE, HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME), SegmentAllocateActionTest.PARTY_TIME.toString(), new NumberedShardSpec(2, 2)));
    }

    @Test
    public void testCannotAddToExistingNumberedShardSpecsWithCoarserQueryGranularity() throws Exception {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getMetadataStorageCoordinator().announceHistoricalSegments(ImmutableSet.of(DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(0, 2)).build(), DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new NumberedShardSpec(1, 2)).build()));
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, DAY, DAY, "s1", null);
        Assert.assertNull(id1);
    }

    @Test
    public void testCannotDoAnythingWithSillyQueryGranularity() {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, DAY, HOUR, "s1", null);
        Assert.assertNull(id1);
    }

    @Test
    public void testCannotAddToExistingSingleDimensionShardSpecs() throws Exception {
        final Task task = new NoopTask(null, null, 0, 0, null, null, null);
        taskActionTestKit.getMetadataStorageCoordinator().announceHistoricalSegments(ImmutableSet.of(DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new SingleDimensionShardSpec("foo", null, "bar", 0)).build(), DataSegment.builder().dataSource(SegmentAllocateActionTest.DATA_SOURCE).interval(HOUR.bucket(SegmentAllocateActionTest.PARTY_TIME)).version(SegmentAllocateActionTest.PARTY_TIME.toString()).shardSpec(new SingleDimensionShardSpec("foo", "bar", null, 1)).build()));
        taskActionTestKit.getTaskLockbox().add(task);
        final SegmentIdWithShardSpec id1 = allocate(task, SegmentAllocateActionTest.PARTY_TIME, NONE, HOUR, "s1", null);
        Assert.assertNull(id1);
    }

    @Test
    public void testSerde() throws Exception {
        final SegmentAllocateAction action = new SegmentAllocateAction(SegmentAllocateActionTest.DATA_SOURCE, SegmentAllocateActionTest.PARTY_TIME, Granularities.MINUTE, Granularities.HOUR, "s1", "prev", false);
        final ObjectMapper objectMapper = new DefaultObjectMapper();
        final SegmentAllocateAction action2 = ((SegmentAllocateAction) (objectMapper.readValue(objectMapper.writeValueAsBytes(action), TaskAction.class)));
        Assert.assertEquals(SegmentAllocateActionTest.DATA_SOURCE, action2.getDataSource());
        Assert.assertEquals(SegmentAllocateActionTest.PARTY_TIME, action2.getTimestamp());
        Assert.assertEquals(MINUTE, action2.getQueryGranularity());
        Assert.assertEquals(HOUR, action2.getPreferredSegmentGranularity());
        Assert.assertEquals("s1", action2.getSequenceName());
        Assert.assertEquals("prev", action2.getPreviousSegmentId());
    }
}

