/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state.internals;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class KeyValueSegmentsTest {
    private static final int NUM_SEGMENTS = 5;

    private static final long SEGMENT_INTERVAL = 100L;

    private static final long RETENTION_PERIOD = 4 * (KeyValueSegmentsTest.SEGMENT_INTERVAL);

    private InternalMockProcessorContext context;

    private KeyValueSegments segments;

    private File stateDirectory;

    private final String storeName = "test";

    @Test
    public void shouldGetSegmentIdsFromTimestamp() {
        Assert.assertEquals(0, segments.segmentId(0));
        Assert.assertEquals(1, segments.segmentId(KeyValueSegmentsTest.SEGMENT_INTERVAL));
        Assert.assertEquals(2, segments.segmentId((2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL))));
        Assert.assertEquals(3, segments.segmentId((3 * (KeyValueSegmentsTest.SEGMENT_INTERVAL))));
    }

    @Test
    public void shouldBaseSegmentIntervalOnRetentionAndNumSegments() {
        final KeyValueSegments segments = new KeyValueSegments("test", (8 * (KeyValueSegmentsTest.SEGMENT_INTERVAL)), (2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL)));
        Assert.assertEquals(0, segments.segmentId(0));
        Assert.assertEquals(0, segments.segmentId(KeyValueSegmentsTest.SEGMENT_INTERVAL));
        Assert.assertEquals(1, segments.segmentId((2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL))));
    }

    @Test
    public void shouldGetSegmentNameFromId() {
        Assert.assertEquals("test.0", segments.segmentName(0));
        Assert.assertEquals(("test." + (KeyValueSegmentsTest.SEGMENT_INTERVAL)), segments.segmentName(1));
        Assert.assertEquals(("test." + (2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL))), segments.segmentName(2));
    }

    @Test
    public void shouldCreateSegments() {
        final KeyValueSegment segment1 = segments.getOrCreateSegmentIfLive(0, context, (-1L));
        final KeyValueSegment segment2 = segments.getOrCreateSegmentIfLive(1, context, (-1L));
        final KeyValueSegment segment3 = segments.getOrCreateSegmentIfLive(2, context, (-1L));
        Assert.assertTrue(new File(context.stateDir(), "test/test.0").isDirectory());
        Assert.assertTrue(new File(context.stateDir(), ("test/test." + (KeyValueSegmentsTest.SEGMENT_INTERVAL))).isDirectory());
        Assert.assertTrue(new File(context.stateDir(), ("test/test." + (2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL)))).isDirectory());
        Assert.assertTrue(segment1.isOpen());
        Assert.assertTrue(segment2.isOpen());
        Assert.assertTrue(segment3.isOpen());
    }

    @Test
    public void shouldNotCreateSegmentThatIsAlreadyExpired() {
        final long streamTime = updateStreamTimeAndCreateSegment(7);
        Assert.assertNull(segments.getOrCreateSegmentIfLive(0, context, streamTime));
        Assert.assertFalse(new File(context.stateDir(), "test/test.0").exists());
    }

    @Test
    public void shouldCleanupSegmentsThatHaveExpired() {
        final KeyValueSegment segment1 = segments.getOrCreateSegmentIfLive(0, context, (-1L));
        final KeyValueSegment segment2 = segments.getOrCreateSegmentIfLive(1, context, (-1L));
        final KeyValueSegment segment3 = segments.getOrCreateSegmentIfLive(7, context, ((KeyValueSegmentsTest.SEGMENT_INTERVAL) * 7L));
        Assert.assertFalse(segment1.isOpen());
        Assert.assertFalse(segment2.isOpen());
        Assert.assertTrue(segment3.isOpen());
        Assert.assertFalse(new File(context.stateDir(), "test/test.0").exists());
        Assert.assertFalse(new File(context.stateDir(), ("test/test." + (KeyValueSegmentsTest.SEGMENT_INTERVAL))).exists());
        Assert.assertTrue(new File(context.stateDir(), ("test/test." + (7 * (KeyValueSegmentsTest.SEGMENT_INTERVAL)))).exists());
    }

    @Test
    public void shouldGetSegmentForTimestamp() {
        final KeyValueSegment segment = segments.getOrCreateSegmentIfLive(0, context, (-1L));
        segments.getOrCreateSegmentIfLive(1, context, (-1L));
        Assert.assertEquals(segment, segments.getSegmentForTimestamp(0L));
    }

    @Test
    public void shouldGetCorrectSegmentString() {
        final KeyValueSegment segment = segments.getOrCreateSegmentIfLive(0, context, (-1L));
        Assert.assertEquals("KeyValueSegment(id=0, name=test.0)", segment.toString());
    }

    @Test
    public void shouldCloseAllOpenSegments() {
        final KeyValueSegment first = segments.getOrCreateSegmentIfLive(0, context, (-1L));
        final KeyValueSegment second = segments.getOrCreateSegmentIfLive(1, context, (-1L));
        final KeyValueSegment third = segments.getOrCreateSegmentIfLive(2, context, (-1L));
        segments.close();
        Assert.assertFalse(first.isOpen());
        Assert.assertFalse(second.isOpen());
        Assert.assertFalse(third.isOpen());
    }

    @Test
    public void shouldOpenExistingSegments() {
        segments = new KeyValueSegments("test", 4, 1);
        segments.getOrCreateSegmentIfLive(0, context, (-1L));
        segments.getOrCreateSegmentIfLive(1, context, (-1L));
        segments.getOrCreateSegmentIfLive(2, context, (-1L));
        segments.getOrCreateSegmentIfLive(3, context, (-1L));
        segments.getOrCreateSegmentIfLive(4, context, (-1L));
        // close existing.
        segments.close();
        segments = new KeyValueSegments("test", 4, 1);
        segments.openExisting(context, (-1L));
        Assert.assertTrue(segments.getSegmentForTimestamp(0).isOpen());
        Assert.assertTrue(segments.getSegmentForTimestamp(1).isOpen());
        Assert.assertTrue(segments.getSegmentForTimestamp(2).isOpen());
        Assert.assertTrue(segments.getSegmentForTimestamp(3).isOpen());
        Assert.assertTrue(segments.getSegmentForTimestamp(4).isOpen());
    }

    @Test
    public void shouldGetSegmentsWithinTimeRange() {
        updateStreamTimeAndCreateSegment(0);
        updateStreamTimeAndCreateSegment(1);
        updateStreamTimeAndCreateSegment(2);
        updateStreamTimeAndCreateSegment(3);
        final long streamTime = updateStreamTimeAndCreateSegment(4);
        segments.getOrCreateSegmentIfLive(0, context, streamTime);
        segments.getOrCreateSegmentIfLive(1, context, streamTime);
        segments.getOrCreateSegmentIfLive(2, context, streamTime);
        segments.getOrCreateSegmentIfLive(3, context, streamTime);
        segments.getOrCreateSegmentIfLive(4, context, streamTime);
        final List<KeyValueSegment> segments = this.segments.segments(0, (2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL)));
        Assert.assertEquals(3, segments.size());
        Assert.assertEquals(0, segments.get(0).id);
        Assert.assertEquals(1, segments.get(1).id);
        Assert.assertEquals(2, segments.get(2).id);
    }

    @Test
    public void shouldGetSegmentsWithinTimeRangeOutOfOrder() {
        updateStreamTimeAndCreateSegment(4);
        updateStreamTimeAndCreateSegment(2);
        updateStreamTimeAndCreateSegment(0);
        updateStreamTimeAndCreateSegment(1);
        updateStreamTimeAndCreateSegment(3);
        final List<KeyValueSegment> segments = this.segments.segments(0, (2 * (KeyValueSegmentsTest.SEGMENT_INTERVAL)));
        Assert.assertEquals(3, segments.size());
        Assert.assertEquals(0, segments.get(0).id);
        Assert.assertEquals(1, segments.get(1).id);
        Assert.assertEquals(2, segments.get(2).id);
    }

    @Test
    public void shouldRollSegments() {
        updateStreamTimeAndCreateSegment(0);
        verifyCorrectSegments(0, 1);
        updateStreamTimeAndCreateSegment(1);
        verifyCorrectSegments(0, 2);
        updateStreamTimeAndCreateSegment(2);
        verifyCorrectSegments(0, 3);
        updateStreamTimeAndCreateSegment(3);
        verifyCorrectSegments(0, 4);
        updateStreamTimeAndCreateSegment(4);
        verifyCorrectSegments(0, 5);
        updateStreamTimeAndCreateSegment(5);
        verifyCorrectSegments(1, 5);
        updateStreamTimeAndCreateSegment(6);
        verifyCorrectSegments(2, 5);
    }

    @Test
    public void futureEventsShouldNotCauseSegmentRoll() {
        updateStreamTimeAndCreateSegment(0);
        verifyCorrectSegments(0, 1);
        updateStreamTimeAndCreateSegment(1);
        verifyCorrectSegments(0, 2);
        updateStreamTimeAndCreateSegment(2);
        verifyCorrectSegments(0, 3);
        updateStreamTimeAndCreateSegment(3);
        verifyCorrectSegments(0, 4);
        final long streamTime = updateStreamTimeAndCreateSegment(4);
        verifyCorrectSegments(0, 5);
        segments.getOrCreateSegmentIfLive(5, context, streamTime);
        verifyCorrectSegments(0, 6);
        segments.getOrCreateSegmentIfLive(6, context, streamTime);
        verifyCorrectSegments(0, 7);
    }

    @Test
    public void shouldUpdateSegmentFileNameFromOldDateFormatToNewFormat() throws Exception {
        final long segmentInterval = 60000L;// the old segment file's naming system maxes out at 1 minute granularity.

        segments = new KeyValueSegments(storeName, ((KeyValueSegmentsTest.NUM_SEGMENTS) * segmentInterval), segmentInterval);
        final String storeDirectoryPath = ((stateDirectory.getAbsolutePath()) + (File.separator)) + (storeName);
        final File storeDirectory = new File(storeDirectoryPath);
        // noinspection ResultOfMethodCallIgnored
        storeDirectory.mkdirs();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        formatter.setTimeZone(new SimpleTimeZone(0, "UTC"));
        for (int segmentId = 0; segmentId < (KeyValueSegmentsTest.NUM_SEGMENTS); ++segmentId) {
            final File oldSegment = new File(((((storeDirectoryPath + (File.separator)) + (storeName)) + "-") + (formatter.format(new Date((segmentId * segmentInterval))))));
            // noinspection ResultOfMethodCallIgnored
            oldSegment.createNewFile();
        }
        segments.openExisting(context, (-1L));
        for (int segmentId = 0; segmentId < (KeyValueSegmentsTest.NUM_SEGMENTS); ++segmentId) {
            final String segmentName = ((storeName) + ".") + (((long) (segmentId)) * segmentInterval);
            final File newSegment = new File(((storeDirectoryPath + (File.separator)) + segmentName));
            Assert.assertTrue(newSegment.exists());
        }
    }

    @Test
    public void shouldUpdateSegmentFileNameFromOldColonFormatToNewFormat() throws Exception {
        final String storeDirectoryPath = ((stateDirectory.getAbsolutePath()) + (File.separator)) + (storeName);
        final File storeDirectory = new File(storeDirectoryPath);
        // noinspection ResultOfMethodCallIgnored
        storeDirectory.mkdirs();
        for (int segmentId = 0; segmentId < (KeyValueSegmentsTest.NUM_SEGMENTS); ++segmentId) {
            final File oldSegment = new File(((((storeDirectoryPath + (File.separator)) + (storeName)) + ":") + (segmentId * ((KeyValueSegmentsTest.RETENTION_PERIOD) / ((KeyValueSegmentsTest.NUM_SEGMENTS) - 1)))));
            // noinspection ResultOfMethodCallIgnored
            oldSegment.createNewFile();
        }
        segments.openExisting(context, (-1L));
        for (int segmentId = 0; segmentId < (KeyValueSegmentsTest.NUM_SEGMENTS); ++segmentId) {
            final File newSegment = new File(((((storeDirectoryPath + (File.separator)) + (storeName)) + ".") + (segmentId * ((KeyValueSegmentsTest.RETENTION_PERIOD) / ((KeyValueSegmentsTest.NUM_SEGMENTS) - 1)))));
            Assert.assertTrue(newSegment.exists());
        }
    }

    @Test
    public void shouldClearSegmentsOnClose() {
        segments.getOrCreateSegmentIfLive(0, context, (-1L));
        segments.close();
        MatcherAssert.assertThat(segments.getSegmentForTimestamp(0), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

