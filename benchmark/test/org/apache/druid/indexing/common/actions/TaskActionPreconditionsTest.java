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


import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.druid.indexing.common.TaskLock;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.overlord.TaskLockbox;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class TaskActionPreconditionsTest {
    private TaskLockbox lockbox;

    private Task task;

    private Set<DataSegment> segments;

    @Test
    public void testCheckLockCoversSegments() {
        final List<Interval> intervals = ImmutableList.of(Intervals.of("2017-01-01/2017-01-02"), Intervals.of("2017-01-02/2017-01-03"), Intervals.of("2017-01-03/2017-01-04"));
        final Map<Interval, TaskLock> locks = intervals.stream().collect(Collectors.toMap(Function.identity(), ( interval) -> {
            final TaskLock lock = lockbox.tryLock(TaskLockType.EXCLUSIVE, task, interval).getTaskLock();
            Assert.assertNotNull(lock);
            return lock;
        }));
        Assert.assertEquals(3, locks.size());
        Assert.assertTrue(TaskActionPreconditions.isLockCoversSegments(task, lockbox, segments));
    }

    @Test
    public void testCheckLargeLockCoversSegments() {
        final List<Interval> intervals = ImmutableList.of(Intervals.of("2017-01-01/2017-01-04"));
        final Map<Interval, TaskLock> locks = intervals.stream().collect(Collectors.toMap(Function.identity(), ( interval) -> {
            final TaskLock lock = lockbox.tryLock(TaskLockType.EXCLUSIVE, task, interval).getTaskLock();
            Assert.assertNotNull(lock);
            return lock;
        }));
        Assert.assertEquals(1, locks.size());
        Assert.assertTrue(TaskActionPreconditions.isLockCoversSegments(task, lockbox, segments));
    }

    @Test
    public void testCheckLockCoversSegmentsWithOverlappedIntervals() {
        final List<Interval> lockIntervals = ImmutableList.of(Intervals.of("2016-12-31/2017-01-01"), Intervals.of("2017-01-01/2017-01-02"), Intervals.of("2017-01-02/2017-01-03"));
        final Map<Interval, TaskLock> locks = lockIntervals.stream().collect(Collectors.toMap(Function.identity(), ( interval) -> {
            final TaskLock lock = lockbox.tryLock(TaskLockType.EXCLUSIVE, task, interval).getTaskLock();
            Assert.assertNotNull(lock);
            return lock;
        }));
        Assert.assertEquals(3, locks.size());
        Assert.assertFalse(TaskActionPreconditions.isLockCoversSegments(task, lockbox, segments));
    }
}

