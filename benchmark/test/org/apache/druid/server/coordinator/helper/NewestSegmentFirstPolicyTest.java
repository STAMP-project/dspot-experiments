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
package org.apache.druid.server.coordinator.helper;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.VersionedIntervalTimeline;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class NewestSegmentFirstPolicyTest {
    private static final String DATA_SOURCE = "dataSource";

    private static final long DEFAULT_SEGMENT_SIZE = 1000;

    private static final int DEFAULT_NUM_SEGMENTS_PER_SHARD = 4;

    private final NewestSegmentFirstPolicy policy = new NewestSegmentFirstPolicy();

    private final boolean keepSegmentGranularity;

    public NewestSegmentFirstPolicyTest(boolean keepSegmentGranularity) {
        this.keepSegmentGranularity = keepSegmentGranularity;
    }

    @Test
    public void testLargeOffsetAndSmallSegmentInterval() {
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("P2D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T20:00:00/2017-11-17T04:00:00"), segmentPeriod), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-14T00:00:00/2017-11-16T07:00:00"), segmentPeriod))), Collections.emptyMap());
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-14T00:00:00/2017-11-14T01:00:00"), Intervals.of("2017-11-15T03:00:00/2017-11-15T04:00:00"), true);
    }

    @Test
    public void testSmallOffsetAndLargeSegmentInterval() {
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("PT1M"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T20:00:00/2017-11-17T04:00:00"), segmentPeriod), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-14T00:00:00/2017-11-16T07:00:00"), segmentPeriod))), Collections.emptyMap());
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-16T20:00:00/2017-11-16T21:00:00"), Intervals.of("2017-11-17T02:00:00/2017-11-17T03:00:00"), false);
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-14T00:00:00/2017-11-14T01:00:00"), Intervals.of("2017-11-16T06:00:00/2017-11-16T07:00:00"), true);
    }

    @Test
    public void testLargeGapInData() {
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("PT1H1M"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, // larger gap than SegmentCompactorUtil.LOOKUP_PERIOD (1 day)
        NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T20:00:00/2017-11-17T04:00:00"), segmentPeriod), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-14T00:00:00/2017-11-15T07:00:00"), segmentPeriod))), Collections.emptyMap());
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-16T20:00:00/2017-11-16T21:00:00"), Intervals.of("2017-11-17T01:00:00/2017-11-17T02:00:00"), false);
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-14T00:00:00/2017-11-14T01:00:00"), Intervals.of("2017-11-15T06:00:00/2017-11-15T07:00:00"), true);
    }

    @Test
    public void testSmallNumTargetCompactionSegments() {
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 5, new Period("PT1H1M"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, // larger gap than SegmentCompactorUtil.LOOKUP_PERIOD (1 day)
        NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T20:00:00/2017-11-17T04:00:00"), segmentPeriod), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-14T00:00:00/2017-11-15T07:00:00"), segmentPeriod))), Collections.emptyMap());
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-16T20:00:00/2017-11-16T21:00:00"), Intervals.of("2017-11-17T01:00:00/2017-11-17T02:00:00"), false);
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-14T00:00:00/2017-11-14T01:00:00"), Intervals.of("2017-11-15T06:00:00/2017-11-15T07:00:00"), true);
    }

    @Test
    public void testHugeShard() {
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-17T00:00:00/2017-11-18T03:00:00"), new Period("PT1H"), 200, NewestSegmentFirstPolicyTest.DEFAULT_NUM_SEGMENTS_PER_SHARD), // larger than target compact segment size
        new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-09T00:00:00/2017-11-17T00:00:00"), new Period("P2D"), 13000, 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-05T00:00:00/2017-11-09T00:00:00"), new Period("PT1H"), 200, NewestSegmentFirstPolicyTest.DEFAULT_NUM_SEGMENTS_PER_SHARD))), Collections.emptyMap());
        Interval lastInterval = null;
        while (iterator.hasNext()) {
            final List<DataSegment> segments = iterator.next();
            lastInterval = segments.get(0).getInterval();
            Interval prevInterval = null;
            for (DataSegment segment : segments) {
                if ((prevInterval != null) && (!(prevInterval.getStart().equals(segment.getInterval().getStart())))) {
                    Assert.assertEquals(prevInterval.getEnd(), segment.getInterval().getStart());
                }
                prevInterval = segment.getInterval();
            }
        } 
        Assert.assertNotNull(lastInterval);
        Assert.assertEquals(Intervals.of("2017-11-05T00:00:00/2017-11-05T01:00:00"), lastInterval);
    }

    @Test
    public void testManySegmentsPerShard() {
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(800000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-04T01:00:00/2017-12-05T03:00:00"), new Period("PT1H"), 375, 80), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-04T00:00:00/2017-12-04T01:00:00"), new Period("PT1H"), 200, 150), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-03T18:00:00/2017-12-04T00:00:00"), new Period("PT6H"), 200000, 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-03T11:00:00/2017-12-03T18:00:00"), new Period("PT1H"), 375, 80))), Collections.emptyMap());
        Interval lastInterval = null;
        while (iterator.hasNext()) {
            final List<DataSegment> segments = iterator.next();
            lastInterval = segments.get(0).getInterval();
            Interval prevInterval = null;
            for (DataSegment segment : segments) {
                if ((prevInterval != null) && (!(prevInterval.getStart().equals(segment.getInterval().getStart())))) {
                    Assert.assertEquals(prevInterval.getEnd(), segment.getInterval().getStart());
                }
                prevInterval = segment.getInterval();
            }
        } 
        Assert.assertNotNull(lastInterval);
        Assert.assertEquals(Intervals.of("2017-12-03T11:00:00/2017-12-03T12:00:00"), lastInterval);
    }

    @Test
    public void testManySegmentsPerShard2() {
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(800000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-04T11:00:00/2017-12-05T05:00:00"), new Period("PT1H"), 200, 150), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-04T06:00:00/2017-12-04T11:00:00"), new Period("PT1H"), 375, 80), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-03T18:00:00/2017-12-04T06:00:00"), new Period("PT12H"), 257000, 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-03T11:00:00/2017-12-03T18:00:00"), new Period("PT1H"), 200, 150), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-02T19:00:00/2017-12-03T11:00:00"), new Period("PT16H"), 257000, 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-02T11:00:00/2017-12-02T19:00:00"), new Period("PT1H"), 200, 150), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-01T18:00:00/2017-12-02T11:00:00"), new Period("PT17H"), 257000, 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-01T09:00:00/2017-12-01T18:00:00"), new Period("PT1H"), 200, 150))), Collections.emptyMap());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSkipUnknownDataSource() {
        final String unknownDataSource = "unknown";
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(unknownDataSource, createCompactionConfig(10000, 100, new Period("P2D")), NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("P2D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T20:00:00/2017-11-17T04:00:00"), segmentPeriod), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-14T00:00:00/2017-11-16T07:00:00"), segmentPeriod))), Collections.emptyMap());
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-14T00:00:00/2017-11-14T01:00:00"), Intervals.of("2017-11-15T03:00:00/2017-11-15T04:00:00"), true);
    }

    @Test
    public void testIgnoreSingleSegmentToCompact() {
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(800000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-02T00:00:00/2017-12-03T00:00:00"), new Period("P1D"), 200, 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-01T00:00:00/2017-12-02T00:00:00"), new Period("P1D"), 200, 1))), Collections.emptyMap());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testClearSegmentsToCompactWhenSkippingSegments() {
        final long maxSizeOfSegmentsToCompact = 800000;
        final VersionedIntervalTimeline<String, DataSegment> timeline = NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-03T00:00:00/2017-12-04T00:00:00"), new Period("P1D"), ((maxSizeOfSegmentsToCompact / 2) + 10), 1), // large segment
        new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-02T00:00:00/2017-12-03T00:00:00"), new Period("P1D"), (maxSizeOfSegmentsToCompact + 10), 1), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-01T00:00:00/2017-12-02T00:00:00"), new Period("P1D"), ((maxSizeOfSegmentsToCompact / 3) + 10), 2));
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(maxSizeOfSegmentsToCompact, 100, new Period("P0D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, timeline), Collections.emptyMap());
        final List<DataSegment> expectedSegmentsToCompact = timeline.lookup(Intervals.of("2017-12-01/2017-12-02")).stream().flatMap(( holder) -> StreamSupport.stream(holder.getObject().spliterator(), false)).map(PartitionChunk::getObject).collect(Collectors.toList());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(expectedSegmentsToCompact, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testIfFirstSegmentIsInSkipOffset() {
        final VersionedIntervalTimeline<String, DataSegment> timeline = NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-02T14:00:00/2017-12-03T00:00:00"), new Period("PT5H"), 40000, 1));
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(40000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, timeline), Collections.emptyMap());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testIfFirstSegmentOverlapsSkipOffset() {
        final VersionedIntervalTimeline<String, DataSegment> timeline = NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-12-01T23:00:00/2017-12-03T00:00:00"), new Period("PT5H"), 40000, 1));
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(40000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, timeline), Collections.emptyMap());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testWithSkipIntervals() {
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("P1D"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T20:00:00/2017-11-17T04:00:00"), segmentPeriod), new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-14T00:00:00/2017-11-16T07:00:00"), segmentPeriod))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, ImmutableList.of(Intervals.of("2017-11-16T00:00:00/2017-11-17T00:00:00"), Intervals.of("2017-11-15T00:00:00/2017-11-15T20:00:00"), Intervals.of("2017-11-13T00:00:00/2017-11-14T01:00:00"))));
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-15T20:00:00/2017-11-15T21:00:00"), Intervals.of("2017-11-15T23:00:00/2017-11-16T00:00:00"), false);
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-14T01:00:00/2017-11-14T02:00:00"), Intervals.of("2017-11-14T23:00:00/2017-11-15T00:00:00"), true);
    }

    @Test
    public void testHoleInSearchInterval() {
        final Period segmentPeriod = new Period("PT1H");
        final CompactionSegmentIterator iterator = policy.reset(ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, createCompactionConfig(10000, 100, new Period("PT1H"))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, NewestSegmentFirstPolicyTest.createTimeline(new NewestSegmentFirstPolicyTest.SegmentGenerateSpec(Intervals.of("2017-11-16T00:00:00/2017-11-17T00:00:00"), segmentPeriod))), ImmutableMap.of(NewestSegmentFirstPolicyTest.DATA_SOURCE, ImmutableList.of(Intervals.of("2017-11-16T04:00:00/2017-11-16T10:00:00"), Intervals.of("2017-11-16T14:00:00/2017-11-16T20:00:00"))));
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-16T20:00:00/2017-11-16T21:00:00"), Intervals.of("2017-11-16T22:00:00/2017-11-16T23:00:00"), false);
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-16T10:00:00/2017-11-16T11:00:00"), Intervals.of("2017-11-16T13:00:00/2017-11-16T14:00:00"), false);
        NewestSegmentFirstPolicyTest.assertCompactSegmentIntervals(iterator, segmentPeriod, Intervals.of("2017-11-16T00:00:00/2017-11-16T01:00:00"), Intervals.of("2017-11-16T03:00:00/2017-11-16T04:00:00"), true);
    }

    private static class SegmentGenerateSpec {
        private final Interval totalInterval;

        private final Period segmentPeriod;

        private final long segmentSize;

        private final int numSegmentsPerShard;

        SegmentGenerateSpec(Interval totalInterval, Period segmentPeriod) {
            this(totalInterval, segmentPeriod, NewestSegmentFirstPolicyTest.DEFAULT_SEGMENT_SIZE, NewestSegmentFirstPolicyTest.DEFAULT_NUM_SEGMENTS_PER_SHARD);
        }

        SegmentGenerateSpec(Interval totalInterval, Period segmentPeriod, long segmentSize, int numSegmentsPerShard) {
            Preconditions.checkArgument((numSegmentsPerShard >= 1));
            this.totalInterval = totalInterval;
            this.segmentPeriod = segmentPeriod;
            this.segmentSize = segmentSize;
            this.numSegmentsPerShard = numSegmentsPerShard;
        }
    }
}

