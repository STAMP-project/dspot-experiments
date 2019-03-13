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
package org.apache.druid.server.coordination;


import DataSegmentChangeCallback.NOOP;
import SegmentLoadDropHandler.DataSegmentChangeRequestAndStatus;
import SegmentLoadDropHandler.Status.PENDING;
import SegmentLoadDropHandler.Status.SUCCESS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.loading.CacheTestSegmentLoader;
import org.apache.druid.segment.loading.SegmentLoaderConfig;
import org.apache.druid.server.SegmentManager;
import org.apache.druid.timeline.DataSegment;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SegmentLoadDropHandlerTest {
    public static final int COUNT = 50;

    private static final Logger log = new Logger(ZkCoordinatorTest.class);

    private final ObjectMapper jsonMapper = TestHelper.makeJsonMapper();

    private SegmentLoadDropHandler segmentLoadDropHandler;

    private DataSegmentAnnouncer announcer;

    private File infoDir;

    private AtomicInteger announceCount;

    private ConcurrentSkipListSet<DataSegment> segmentsAnnouncedByMe;

    private CacheTestSegmentLoader segmentLoader;

    private SegmentManager segmentManager;

    private List<Runnable> scheduledRunnable;

    /**
     * Steps:
     * 1. removeSegment() schedules a delete runnable that deletes segment files,
     * 2. addSegment() succesfully loads the segment and annouces it
     * 3. scheduled delete task executes and realizes it should not delete the segment files.
     */
    @Test
    public void testSegmentLoading1() throws Exception {
        segmentLoadDropHandler.start();
        final DataSegment segment = makeSegment("test", "1", Intervals.of("P1d/2011-04-01"));
        segmentLoadDropHandler.removeSegment(segment, NOOP);
        Assert.assertFalse(segmentsAnnouncedByMe.contains(segment));
        segmentLoadDropHandler.addSegment(segment, NOOP);
        /* make sure the scheduled runnable that "deletes" segment files has been executed.
        Because another addSegment() call is executed, which removes the segment from segmentsToDelete field in
        ZkCoordinator, the scheduled runnable will not actually delete segment files.
         */
        for (Runnable runnable : scheduledRunnable) {
            runnable.run();
        }
        Assert.assertTrue(segmentsAnnouncedByMe.contains(segment));
        Assert.assertFalse("segment files shouldn't be deleted", segmentLoader.getSegmentsInTrash().contains(segment));
        segmentLoadDropHandler.stop();
    }

    /**
     * Steps:
     * 1. addSegment() succesfully loads the segment and annouces it
     * 2. removeSegment() unannounces the segment and schedules a delete runnable that deletes segment files
     * 3. addSegment() calls loadSegment() and annouces it again
     * 4. scheduled delete task executes and realizes it should not delete the segment files.
     */
    @Test
    public void testSegmentLoading2() throws Exception {
        segmentLoadDropHandler.start();
        final DataSegment segment = makeSegment("test", "1", Intervals.of("P1d/2011-04-01"));
        segmentLoadDropHandler.addSegment(segment, NOOP);
        Assert.assertTrue(segmentsAnnouncedByMe.contains(segment));
        segmentLoadDropHandler.removeSegment(segment, NOOP);
        Assert.assertFalse(segmentsAnnouncedByMe.contains(segment));
        segmentLoadDropHandler.addSegment(segment, NOOP);
        /* make sure the scheduled runnable that "deletes" segment files has been executed.
        Because another addSegment() call is executed, which removes the segment from segmentsToDelete field in
        ZkCoordinator, the scheduled runnable will not actually delete segment files.
         */
        for (Runnable runnable : scheduledRunnable) {
            runnable.run();
        }
        Assert.assertTrue(segmentsAnnouncedByMe.contains(segment));
        Assert.assertFalse("segment files shouldn't be deleted", segmentLoader.getSegmentsInTrash().contains(segment));
        segmentLoadDropHandler.stop();
    }

    @Test
    public void testLoadCache() throws Exception {
        Set<DataSegment> segments = new HashSet<>();
        for (int i = 0; i < (SegmentLoadDropHandlerTest.COUNT); ++i) {
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-01")));
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-02")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("P1d/2011-04-02")));
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-03")));
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-04")));
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-05")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("PT1h/2011-04-04T01")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("PT1h/2011-04-04T02")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("PT1h/2011-04-04T03")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("PT1h/2011-04-04T05")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("PT1h/2011-04-04T06")));
            segments.add(makeSegment(("test_two" + i), "1", Intervals.of("P1d/2011-04-01")));
            segments.add(makeSegment(("test_two" + i), "1", Intervals.of("P1d/2011-04-02")));
        }
        for (DataSegment segment : segments) {
            writeSegmentToCache(segment);
        }
        checkCache(segments);
        Assert.assertTrue(segmentManager.getDataSourceCounts().isEmpty());
        segmentLoadDropHandler.start();
        Assert.assertTrue((!(segmentManager.getDataSourceCounts().isEmpty())));
        for (int i = 0; i < (SegmentLoadDropHandlerTest.COUNT); ++i) {
            Assert.assertEquals(11L, segmentManager.getDataSourceCounts().get(("test" + i)).longValue());
            Assert.assertEquals(2L, segmentManager.getDataSourceCounts().get(("test_two" + i)).longValue());
        }
        Assert.assertEquals((13 * (SegmentLoadDropHandlerTest.COUNT)), announceCount.get());
        segmentLoadDropHandler.stop();
        for (DataSegment segment : segments) {
            deleteSegmentFromCache(segment);
        }
        Assert.assertEquals(0, infoDir.listFiles().length);
        Assert.assertTrue(infoDir.delete());
    }

    @Test
    public void testStartStop() throws Exception {
        SegmentLoadDropHandler handler = new SegmentLoadDropHandler(jsonMapper, new SegmentLoaderConfig() {
            @Override
            public File getInfoDir() {
                return infoDir;
            }

            @Override
            public int getNumLoadingThreads() {
                return 5;
            }

            @Override
            public int getAnnounceIntervalMillis() {
                return 50;
            }
        }, announcer, EasyMock.createNiceMock(DataSegmentServerAnnouncer.class), segmentManager);
        Set<DataSegment> segments = new HashSet<>();
        for (int i = 0; i < (SegmentLoadDropHandlerTest.COUNT); ++i) {
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-01")));
            segments.add(makeSegment(("test" + i), "1", Intervals.of("P1d/2011-04-02")));
            segments.add(makeSegment(("test" + i), "2", Intervals.of("P1d/2011-04-02")));
            segments.add(makeSegment(("test_two" + i), "1", Intervals.of("P1d/2011-04-01")));
            segments.add(makeSegment(("test_two" + i), "1", Intervals.of("P1d/2011-04-02")));
        }
        for (DataSegment segment : segments) {
            writeSegmentToCache(segment);
        }
        checkCache(segments);
        Assert.assertTrue(segmentManager.getDataSourceCounts().isEmpty());
        handler.start();
        Assert.assertTrue((!(segmentManager.getDataSourceCounts().isEmpty())));
        for (int i = 0; i < (SegmentLoadDropHandlerTest.COUNT); ++i) {
            Assert.assertEquals(3L, segmentManager.getDataSourceCounts().get(("test" + i)).longValue());
            Assert.assertEquals(2L, segmentManager.getDataSourceCounts().get(("test_two" + i)).longValue());
        }
        Assert.assertEquals((5 * (SegmentLoadDropHandlerTest.COUNT)), announceCount.get());
        handler.stop();
        for (DataSegment segment : segments) {
            deleteSegmentFromCache(segment);
        }
        Assert.assertEquals(0, infoDir.listFiles().length);
        Assert.assertTrue(infoDir.delete());
    }

    @Test(timeout = 60000L)
    public void testProcessBatch() throws Exception {
        segmentLoadDropHandler.start();
        DataSegment segment1 = makeSegment("batchtest1", "1", Intervals.of("P1d/2011-04-01"));
        DataSegment segment2 = makeSegment("batchtest2", "1", Intervals.of("P1d/2011-04-01"));
        List<DataSegmentChangeRequest> batch = ImmutableList.of(new SegmentChangeRequestLoad(segment1), new SegmentChangeRequestDrop(segment2));
        ListenableFuture<List<SegmentLoadDropHandler.DataSegmentChangeRequestAndStatus>> future = segmentLoadDropHandler.processBatch(batch);
        List<SegmentLoadDropHandler.DataSegmentChangeRequestAndStatus> result = future.get();
        Assert.assertEquals(PENDING, result.get(0).getStatus());
        Assert.assertEquals(SUCCESS, result.get(1).getStatus());
        for (Runnable runnable : scheduledRunnable) {
            runnable.run();
        }
        result = segmentLoadDropHandler.processBatch(batch).get();
        Assert.assertEquals(SUCCESS, result.get(0).getStatus());
        Assert.assertEquals(SUCCESS, result.get(1).getStatus());
        for (SegmentLoadDropHandler.DataSegmentChangeRequestAndStatus e : segmentLoadDropHandler.processBatch(batch).get()) {
            Assert.assertEquals(SUCCESS, e.getStatus());
        }
        segmentLoadDropHandler.stop();
    }
}

