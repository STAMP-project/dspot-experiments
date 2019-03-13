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
package org.apache.druid.server.coordinator;


import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.LinearShardSpec;
import org.junit.Assert;
import org.junit.Test;


public class DruidCoordinatorSegmentMergerTest {
    private static final long mergeBytesLimit = 100;

    private static final int mergeSegmentsLimit = 8;

    @Test
    public void testNoMerges() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeAtStart() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(90).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeAtEnd() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(20).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(2), segments.get(3))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeInMiddle() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(10).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(20).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(1), segments.get(2))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeNoncontiguous() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(10).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(10).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(10).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1), segments.get(2))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeSeriesByteLimited() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(40).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(40).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(40).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(40).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("2").size(40).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(40).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1)), ImmutableList.of(segments.get(2), segments.get(3)), ImmutableList.of(segments.get(4), segments.get(5))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeSeriesSegmentLimited() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-07/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-08/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-09/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-10/P1D")).version("2").size(1).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1), segments.get(2), segments.get(3), segments.get(4), segments.get(5), segments.get(6), segments.get(7)), ImmutableList.of(segments.get(8), segments.get(9))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMergeWithBacktracking() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("2").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P4D")).version("2").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("3").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("4").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("3").size(20).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-07/P1D")).version("2").size(20).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1)), ImmutableList.of(segments.get(2), segments.get(3), segments.get(4), segments.get(5), segments.get(6))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMergeWithGapsAlignedStart() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P8D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("3").size(8).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("3").size(8).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-09/P1D")).version("3").size(8).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(1), segments.get(0), segments.get(2))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMergeWithGapsNonalignedStart() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P8D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("3").size(8).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("3").size(8).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-09/P1D")).version("3").size(8).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1), segments.get(2))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge1() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("1").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge2() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(15).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("4").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(2), segments.get(3), segments.get(4))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge3() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("1").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(1), segments.get(2), segments.get(4))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge4() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("4").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(1), segments.get(2), segments.get(3), segments.get(4))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge5() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("1").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge6() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("4").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(25).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(2), segments.get(3), segments.get(4))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge7() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(120).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("4").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(2), segments.get(3), segments.get(4), segments.get(5))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testOverlappingMerge8() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("2").size(80).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P4D")).version("2").size(120).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("1").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("3").size(1).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-06/P1D")).version("2").size(80).build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(4), segments.get(5))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeLinearShardSpecs() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("1").shardSpec(new LinearShardSpec(1)).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("1").shardSpec(new LinearShardSpec(7)).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("1").shardSpec(new LinearShardSpec(1500)).build());
        Assert.assertEquals(ImmutableList.of(), DruidCoordinatorSegmentMergerTest.merge(segments));
    }

    @Test
    public void testMergeMixedShardSpecs() {
        final List<DataSegment> segments = ImmutableList.of(DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-01/P1D")).version("1").build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-02/P1D")).version("1").build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-03/P1D")).version("1").shardSpec(new LinearShardSpec(1500)).build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-04/P1D")).version("1").build(), DataSegment.builder().dataSource("foo").interval(Intervals.of("2012-01-05/P1D")).version("1").build());
        Assert.assertEquals(ImmutableList.of(ImmutableList.of(segments.get(0), segments.get(1)), ImmutableList.of(segments.get(3), segments.get(4))), DruidCoordinatorSegmentMergerTest.merge(segments));
    }
}

