/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import Segment.ROOT_SEGMENT;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Segment.ROOT_SEGMENT;


public class SegmentTest {
    private static final Logger LOG = LoggerFactory.getLogger(SegmentTest.class);

    private List<DomainEventMessage> domainEventMessages;

    @Test
    public void testSegmentSplitAddsUp() {
        final List<Long> identifiers = domainEventMessages.stream().map(( de) -> {
            final String aggregateIdentifier = de.getAggregateIdentifier();
            return UUID.fromString(aggregateIdentifier).getLeastSignificantBits();
        }).collect(Collectors.toList());
        // segment 0, mask 0;
        final long count = identifiers.stream().filter(ROOT_SEGMENT::matches).count();
        Assert.assertThat(count, CoreMatchers.is(((long) (identifiers.size()))));
        final Segment[] splitSegment = ROOT_SEGMENT.split();
        final long splitCount1 = identifiers.stream().filter(splitSegment[0]::matches).count();
        final long splitCount2 = identifiers.stream().filter(splitSegment[1]::matches).count();
        Assert.assertThat((splitCount1 + splitCount2), CoreMatchers.is(((long) (identifiers.size()))));
    }

    @Test
    public void testSegmentSplit() {
        // Split segment 0
        final Segment[] splitSegment0 = ROOT_SEGMENT.split();
        Assert.assertThat(ROOT_SEGMENT.splitSegmentId(), CoreMatchers.is(1));
        Assert.assertThat(splitSegment0[0].getSegmentId(), CoreMatchers.is(0));
        Assert.assertThat(splitSegment0[0].getMask(), CoreMatchers.is(1));
        Assert.assertThat(splitSegment0[1].getSegmentId(), CoreMatchers.is(1));
        Assert.assertThat(splitSegment0[1].getMask(), CoreMatchers.is(1));
        // Split segment 0 again
        final Segment[] splitSegment0_1 = splitSegment0[0].split();
        Assert.assertThat(splitSegment0[0].splitSegmentId(), CoreMatchers.is(2));
        Assert.assertThat(splitSegment0_1[0].getSegmentId(), CoreMatchers.is(0));
        Assert.assertThat(splitSegment0_1[0].getMask(), CoreMatchers.is(3));
        Assert.assertThat(splitSegment0_1[1].getSegmentId(), CoreMatchers.is(2));
        Assert.assertThat(splitSegment0_1[1].getMask(), CoreMatchers.is(3));
        // Split segment 0 again
        final Segment[] splitSegment0_2 = splitSegment0_1[0].split();
        Assert.assertThat(splitSegment0_1[0].splitSegmentId(), CoreMatchers.is(4));
        Assert.assertThat(splitSegment0_2[0].getSegmentId(), CoreMatchers.is(0));
        Assert.assertThat(splitSegment0_2[0].getMask(), CoreMatchers.is(7));
        Assert.assertThat(splitSegment0_2[1].getSegmentId(), CoreMatchers.is(4));
        Assert.assertThat(splitSegment0_2[1].getMask(), CoreMatchers.is(7));
        // Split segment 0 again
        final Segment[] splitSegment0_3 = splitSegment0_2[0].split();
        Assert.assertThat(splitSegment0_2[0].splitSegmentId(), CoreMatchers.is(8));
        Assert.assertThat(splitSegment0_3[0].getSegmentId(), CoreMatchers.is(0));
        Assert.assertThat(splitSegment0_3[0].getMask(), CoreMatchers.is(15));
        Assert.assertThat(splitSegment0_3[1].getSegmentId(), CoreMatchers.is(8));
        Assert.assertThat(splitSegment0_3[1].getMask(), CoreMatchers.is(15));
        // ////////////////////////////////////////////////////////////
        // Split segment 1
        final Segment[] splitSegment1 = splitSegment0[1].split();
        Assert.assertThat(splitSegment0[1].splitSegmentId(), CoreMatchers.is(3));
        Assert.assertThat(splitSegment1[0].getSegmentId(), CoreMatchers.is(1));
        Assert.assertThat(splitSegment1[0].getMask(), CoreMatchers.is(3));
        Assert.assertThat(splitSegment1[1].getSegmentId(), CoreMatchers.is(3));
        Assert.assertThat(splitSegment1[1].getMask(), CoreMatchers.is(3));
        // Split segment 3
        final Segment[] splitSegment3 = splitSegment1[1].split();
        Assert.assertThat(splitSegment1[1].splitSegmentId(), CoreMatchers.is(7));
        Assert.assertThat(splitSegment3[0].getSegmentId(), CoreMatchers.is(3));
        Assert.assertThat(splitSegment3[0].getMask(), CoreMatchers.is(7));
        Assert.assertThat(splitSegment3[1].getSegmentId(), CoreMatchers.is(7));
        Assert.assertThat(splitSegment3[1].getMask(), CoreMatchers.is(7));
    }

    @Test
    public void testSegmentSplitNTimes() {
        {
            // 
            final List<Segment> segmentMasks = Segment.splitBalanced(ROOT_SEGMENT, 5);
            Assert.assertThat(segmentMasks.size(), CoreMatchers.is(6));
            Assert.assertThat(segmentMasks.get(5), CoreMatchers.equalTo(new Segment(5, 7)));
            Assert.assertThat(segmentMasks.get(0), CoreMatchers.equalTo(new Segment(0, 7)));
            Assert.assertThat(segmentMasks.get(1), CoreMatchers.equalTo(new Segment(1, 7)));
            Assert.assertThat(segmentMasks.get(2), CoreMatchers.equalTo(new Segment(2, 3)));
            Assert.assertThat(segmentMasks.get(3), CoreMatchers.equalTo(new Segment(3, 3)));
            Assert.assertThat(segmentMasks.get(4), CoreMatchers.equalTo(new Segment(4, 7)));
        }
    }

    @Test
    public void testSplitFromRootSegmentAlwaysYieldsSequentialSegmentIds() {
        for (int i = 0; i < 500; i++) {
            List<Segment> segments = Segment.splitBalanced(ROOT_SEGMENT, i);
            Assert.assertEquals((i + 1), segments.size());
            for (int j = 0; j < i; j++) {
                Assert.assertEquals(j, segments.get(j).getSegmentId());
            }
        }
    }

    @Test
    public void testMergeable() {
        Segment[] segments = ROOT_SEGMENT.split();
        Assert.assertFalse(segments[0].isMergeableWith(segments[0]));
        Assert.assertFalse(segments[1].isMergeableWith(segments[1]));
        Assert.assertTrue(segments[0].isMergeableWith(segments[1]));
        Assert.assertTrue(segments[1].isMergeableWith(segments[0]));
        // these masks differ
        Segment[] segments2 = segments[0].split();
        Assert.assertFalse(segments[0].isMergeableWith(segments2[0]));
        Assert.assertFalse(segments[0].isMergeableWith(segments2[1]));
        Assert.assertFalse(segments[1].isMergeableWith(segments2[0]));
        // this should still work
        Assert.assertTrue(segments2[0].isMergeableWith(segments2[1]));
    }

    @Test
    public void testMergeableSegment() {
        Segment[] segments = ROOT_SEGMENT.split();
        Assert.assertEquals(segments[1].getSegmentId(), segments[0].mergeableSegmentId());
        Assert.assertEquals(segments[0].getSegmentId(), segments[1].mergeableSegmentId());
        Assert.assertEquals(0, ROOT_SEGMENT.mergeableSegmentId());
    }

    @Test
    public void testMergeSegments() {
        Segment[] segments = ROOT_SEGMENT.split();
        Segment[] segments2 = segments[0].split();
        Assert.assertEquals(segments[0], segments2[0].mergedWith(segments2[1]));
        Assert.assertEquals(segments[0], segments2[1].mergedWith(segments2[0]));
        Assert.assertEquals(ROOT_SEGMENT, segments[1].mergedWith(segments[0]));
        Assert.assertEquals(ROOT_SEGMENT, ROOT_SEGMENT.mergedWith(ROOT_SEGMENT));
    }

    @Test
    public void testSegmentResolve() {
        {
            final int[] segments = new int[]{  };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(0));
        }
        {
            final int[] segments = new int[]{ 0 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(1));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(ROOT_SEGMENT.getMask()));
        }
        {
            // balanced distribution
            final int[] segments = new int[]{ 0, 1 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(2));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(1));
            Assert.assertThat(segmentMasks[1].getMask(), CoreMatchers.is(1));
        }
        {
            // un-balanced distribution segment 0 is split.
            final int[] segments = new int[]{ 0, 1, 2 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[1].getMask(), CoreMatchers.is(1));
            Assert.assertThat(segmentMasks[2].getMask(), CoreMatchers.is(3));
        }
        {
            // un-balanced distribution segment 1 is split.
            final int[] segments = new int[]{ 0, 1, 3 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(1));
            Assert.assertThat(segmentMasks[1].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[2].getMask(), CoreMatchers.is(3));
        }
        {
            // balanced distribution segment 0 and 1 are split.
            final int[] segments = new int[]{ 0, 1, 2, 3 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(4));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[1].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[2].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[3].getMask(), CoreMatchers.is(3));
        }
        {
            // un-balanced distribution segment 1 is split and segment 3 is split.
            final int[] segments = new int[]{ 0, 1, 3, 7 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(4));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(1));
            Assert.assertThat(segmentMasks[1].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[2].getMask(), CoreMatchers.is(7));
            Assert.assertThat(segmentMasks[3].getMask(), CoreMatchers.is(7));
        }
        {
            // un-balanced distribution segment 0 is split, segment 3 is split.
            final int[] segments = new int[]{ 0, 1, 2, 3, 7 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(5));
            Assert.assertThat(segmentMasks[0].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[1].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[2].getMask(), CoreMatchers.is(3));
            Assert.assertThat(segmentMasks[3].getMask(), CoreMatchers.is(7));
            Assert.assertThat(segmentMasks[4].getMask(), CoreMatchers.is(7));
        }
        {
            // 
            final int[] segments = new int[]{ 0, 1, 2, 3, 4, 5 };
            final Segment[] segmentMasks = Segment.computeSegments(segments);
            Assert.assertThat(segmentMasks.length, CoreMatchers.is(6));
            Assert.assertThat(segmentMasks[0], CoreMatchers.equalTo(new Segment(0, 7)));
            Assert.assertThat(segmentMasks[1], CoreMatchers.equalTo(new Segment(1, 7)));
            Assert.assertThat(segmentMasks[2], CoreMatchers.equalTo(new Segment(2, 3)));
            Assert.assertThat(segmentMasks[3], CoreMatchers.equalTo(new Segment(3, 3)));
            Assert.assertThat(segmentMasks[4], CoreMatchers.equalTo(new Segment(4, 7)));
            Assert.assertThat(segmentMasks[5], CoreMatchers.equalTo(new Segment(5, 7)));
        }
    }

    @Test
    public void testComputeSegment() {
        for (int segmentCount = 0; segmentCount < 256; segmentCount++) {
            List<Segment> segments = Segment.splitBalanced(ROOT_SEGMENT, segmentCount);
            int[] segmentIds = new int[segments.size()];
            for (int i = 0; i < (segmentIds.length); i++) {
                segmentIds[i] = segments.get(i).getSegmentId();
            }
            for (Segment segment : segments) {
                Assert.assertEquals((("Got wrong segment for " + segmentCount) + " number of segments"), segment, Segment.computeSegment(segment.getSegmentId(), segmentIds));
            }
        }
    }

    @Test
    public void testComputeSegment_Imbalanced() {
        List<Segment> segments = new ArrayList<>();
        Segment initialSegment = ROOT_SEGMENT;
        for (int i = 0; i < 8; i++) {
            Segment[] split = initialSegment.split();
            initialSegment = split[0];
            segments.add(split[1]);
        }
        segments.add(initialSegment);
        int[] segmentIds = new int[segments.size()];
        for (int i = 0; i < (segmentIds.length); i++) {
            segmentIds[i] = segments.get(i).getSegmentId();
        }
        for (Segment segment : segments) {
            Assert.assertEquals(segment, Segment.computeSegment(segment.getSegmentId(), segmentIds));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSegmentSplitBeyondBoundary() {
        final Segment segment = new Segment(0, Integer.MAX_VALUE);
        segment.split();
    }

    @Test
    public void testSegmentSplitOnBoundary() {
        final Segment segment = new Segment(0, ((Integer.MAX_VALUE) >>> 1));
        final Segment[] splitSegment = segment.split();
        Assert.assertThat(splitSegment[0].getSegmentId(), CoreMatchers.is(0));
        Assert.assertThat(splitSegment[0].getMask(), CoreMatchers.is(Integer.MAX_VALUE));
        Assert.assertThat(splitSegment[1].getSegmentId(), CoreMatchers.is((((Integer.MAX_VALUE) / 2) + 1)));
        Assert.assertThat(splitSegment[1].getMask(), CoreMatchers.is(Integer.MAX_VALUE));
    }

    @Test
    public void testItemsAssignedToOnlyOneSegment() {
        for (int j = 0; j < 10; j++) {
            List<Segment> segments = Segment.splitBalanced(ROOT_SEGMENT, ((ThreadLocalRandom.current().nextInt(50)) + 1));
            for (int i = 0; i < 100000; i++) {
                String value = UUID.randomUUID().toString();
                Assert.assertEquals(1, segments.stream().filter(( s) -> s.matches(value)).count());
            }
        }
    }
}

