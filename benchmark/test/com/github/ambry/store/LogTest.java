/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.store;


import com.codahale.metrics.MetricRegistry;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static LogSegment.HEADER_SIZE;


/**
 * Tests for {@link Log}.
 */
public class LogTest {
    private static final long SEGMENT_CAPACITY = 512;

    private static final long LOG_CAPACITY = 12 * (LogTest.SEGMENT_CAPACITY);

    private static final LogTest.Appender BUFFER_APPENDER = new LogTest.Appender() {
        @Override
        public void append(Log log, ByteBuffer buffer) throws IOException {
            int writeSize = buffer.remaining();
            int written = log.appendFrom(buffer);
            Assert.assertEquals("Size written did not match size of buffer provided", writeSize, written);
        }
    };

    private static final LogTest.Appender CHANNEL_APPENDER = new LogTest.Appender() {
        @Override
        public void append(Log log, ByteBuffer buffer) throws IOException {
            int writeSize = buffer.remaining();
            log.appendFrom(Channels.newChannel(new ByteBufferInputStream(buffer)), writeSize);
            Assert.assertFalse("The buffer was not completely written", buffer.hasRemaining());
        }
    };

    private final File tempDir;

    private final StoreMetrics metrics;

    private final Set<Long> positionsGenerated = new HashSet<>();

    /**
     * Creates a temporary directory to store the segment files.
     *
     * @throws IOException
     * 		
     */
    public LogTest() throws IOException {
        tempDir = Files.createTempDirectory(("logDir-" + (UtilsTest.getRandomString(10)))).toFile();
        tempDir.deleteOnExit();
        MetricRegistry metricRegistry = new MetricRegistry();
        metrics = new StoreMetrics(metricRegistry);
    }

    /**
     * Tests almost all the functions and cases of {@link Log}.
     * </p>
     * Each individual test has the following variable parameters.
     * 1. The capacity of each segment.
     * 2. The size of the write on the log.
     * 3. The segment that is being marked as active (beginning, middle, end etc).
     * 4. The number of segment files that have been created and already exist in the folder.
     * 5. The type of append operation being used.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void comprehensiveTest() throws IOException {
        LogTest.Appender[] appenders = new LogTest.Appender[]{ LogTest.BUFFER_APPENDER, LogTest.CHANNEL_APPENDER };
        for (LogTest.Appender appender : appenders) {
            // for single segment log
            setupAndDoComprehensiveTest(LogTest.LOG_CAPACITY, LogTest.LOG_CAPACITY, appender);
            setupAndDoComprehensiveTest(LogTest.LOG_CAPACITY, ((LogTest.LOG_CAPACITY) + 1), appender);
            // for multiple segment log
            setupAndDoComprehensiveTest(LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, appender);
        }
    }

    /**
     * Tests cases where bad arguments are provided to the {@link Log} constructor.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void constructionBadArgsTest() throws IOException {
        List<Pair<Long, Long>> logAndSegmentSizes = new ArrayList<>();
        // <=0 values for capacities
        logAndSegmentSizes.add(new Pair((-1L), LogTest.SEGMENT_CAPACITY));
        logAndSegmentSizes.add(new Pair(LogTest.LOG_CAPACITY, (-1L)));
        logAndSegmentSizes.add(new Pair(0L, LogTest.SEGMENT_CAPACITY));
        logAndSegmentSizes.add(new Pair(LogTest.LOG_CAPACITY, 0L));
        // log capacity is not perfectly divisible by segment capacity
        logAndSegmentSizes.add(new Pair(LogTest.LOG_CAPACITY, ((LogTest.LOG_CAPACITY) - 1)));
        for (Pair<Long, Long> logAndSegmentSize : logAndSegmentSizes) {
            try {
                new Log(tempDir.getAbsolutePath(), logAndSegmentSize.getFirst(), logAndSegmentSize.getSecond(), StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
                Assert.fail("Construction should have failed");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
        }
        // file which is not a directory
        File file = create(LogSegmentNameHelper.nameToFilename(LogSegmentNameHelper.generateFirstSegmentName(false)));
        try {
            new Log(file.getAbsolutePath(), 1, 1, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
            Assert.fail("Construction should have failed");
        } catch (IOException e) {
            // expected. Nothing to do.
        }
    }

    /**
     * Tests cases where bad arguments are provided to the append operations.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void appendErrorCasesTest() throws IOException {
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
        try {
            // write exceeds size of a single segment.
            ByteBuffer buffer = ByteBuffer.wrap(TestUtils.getRandomBytes(((int) (((LogTest.SEGMENT_CAPACITY) + 1) - (HEADER_SIZE)))));
            try {
                log.appendFrom(buffer);
                Assert.fail("Cannot append a write of size greater than log segment size");
            } catch (IllegalArgumentException e) {
                Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
            }
            try {
                log.appendFrom(Channels.newChannel(new ByteBufferInputStream(buffer)), buffer.remaining());
                Assert.fail("Cannot append a write of size greater than log segment size");
            } catch (IllegalArgumentException e) {
                Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
            }
        } finally {
            log.close();
            cleanDirectory(tempDir);
        }
    }

    /**
     * Tests cases where bad arguments are provided to {@link Log#setActiveSegment(String)}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void setActiveSegmentBadArgsTest() throws IOException {
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
        long numSegments = (LogTest.LOG_CAPACITY) / (LogTest.SEGMENT_CAPACITY);
        try {
            log.setActiveSegment(LogSegmentNameHelper.getName((numSegments + 1), 0));
            Assert.fail("Should have failed to set a non existent segment as active");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        } finally {
            log.close();
            cleanDirectory(tempDir);
        }
    }

    /**
     * Tests cases where bad arguments are provided to {@link Log#getNextSegment(LogSegment)}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void getNextSegmentBadArgsTest() throws IOException {
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
        LogSegment segment = getLogSegment(LogSegmentNameHelper.getName(1, 1), LogTest.SEGMENT_CAPACITY, true);
        try {
            log.getNextSegment(segment);
            Assert.fail("Getting next segment should have failed because provided segment does not exist in the log");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        } finally {
            log.close();
            cleanDirectory(tempDir);
        }
    }

    /**
     * Tests cases where bad arguments are provided to {@link Log#getPrevSegment(LogSegment)}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void getPrevSegmentBadArgsTest() throws IOException {
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
        LogSegment segment = getLogSegment(LogSegmentNameHelper.getName(1, 1), LogTest.SEGMENT_CAPACITY, true);
        try {
            log.getPrevSegment(segment);
            Assert.fail("Getting prev segment should have failed because provided segment does not exist in the log");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        } finally {
            log.close();
            cleanDirectory(tempDir);
        }
    }

    /**
     * Tests cases where bad arguments are provided to {@link Log#getFileSpanForMessage(Offset, long)}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void getFileSpanForMessageBadArgsTest() throws IOException {
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
        try {
            LogSegment firstSegment = log.getFirstSegment();
            log.setActiveSegment(firstSegment.getName());
            Offset endOffsetOfPrevMessage = new Offset(firstSegment.getName(), ((firstSegment.getEndOffset()) + 1));
            try {
                log.getFileSpanForMessage(endOffsetOfPrevMessage, 1);
                Assert.fail("Should have failed because endOffsetOfPrevMessage > endOffset of log segment");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
            // write a single byte into the log
            endOffsetOfPrevMessage = log.getEndOffset();
            LogTest.CHANNEL_APPENDER.append(log, ByteBuffer.allocate(1));
            try {
                // provide the wrong size
                log.getFileSpanForMessage(endOffsetOfPrevMessage, 2);
                Assert.fail("Should have failed because endOffsetOfPrevMessage + size > endOffset of log segment");
            } catch (IllegalStateException e) {
                // expected. Nothing to do.
            }
        } finally {
            log.close();
            cleanDirectory(tempDir);
        }
    }

    /**
     * Tests all cases of {@link Log#addSegment(LogSegment, boolean)} and {@link Log#dropSegment(String, boolean)}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void addAndDropSegmentTest() throws IOException {
        // start with a segment that has a high position to allow for addition of segments
        long activeSegmentPos = (2 * (LogTest.LOG_CAPACITY)) / (LogTest.SEGMENT_CAPACITY);
        LogSegment loadedSegment = getLogSegment(LogSegmentNameHelper.getName(activeSegmentPos, 0), LogTest.SEGMENT_CAPACITY, true);
        List<LogSegment> segmentsToLoad = Collections.singletonList(loadedSegment);
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics, true, segmentsToLoad, Collections.EMPTY_LIST.iterator());
        // add a segment
        String segmentName = LogSegmentNameHelper.getName(0, 0);
        LogSegment uncountedSegment = getLogSegment(segmentName, LogTest.SEGMENT_CAPACITY, true);
        log.addSegment(uncountedSegment, false);
        Assert.assertEquals("Log segment instance not as expected", uncountedSegment, log.getSegment(segmentName));
        // cannot add past the active segment
        segmentName = LogSegmentNameHelper.getName((activeSegmentPos + 1), 0);
        LogSegment segment = getLogSegment(segmentName, LogTest.SEGMENT_CAPACITY, true);
        try {
            log.addSegment(segment, false);
            Assert.fail("Should not be able to add past the active segment");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // since the previous one did not ask for count of segments to be increased, we should be able to add max - 1
        // more segments. This increments used segment count to the max.
        int max = ((int) ((LogTest.LOG_CAPACITY) / (LogTest.SEGMENT_CAPACITY)));
        for (int i = 1; i < max; i++) {
            segmentName = LogSegmentNameHelper.getName(i, 0);
            segment = getLogSegment(segmentName, LogTest.SEGMENT_CAPACITY, true);
            log.addSegment(segment, true);
        }
        // fill up the active segment
        ByteBuffer buffer = ByteBuffer.allocate(((int) ((loadedSegment.getCapacityInBytes()) - (loadedSegment.getStartOffset()))));
        LogTest.CHANNEL_APPENDER.append(log, buffer);
        // write fails because no more log segments can be allocated
        buffer = ByteBuffer.allocate(1);
        try {
            LogTest.CHANNEL_APPENDER.append(log, buffer);
            Assert.fail("Write should have failed because no more log segments should be allocated");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
        // drop the uncounted segment
        Assert.assertEquals("Segment not as expected", uncountedSegment, log.getSegment(uncountedSegment.getName()));
        File segmentFile = uncountedSegment.getView().getFirst();
        log.dropSegment(uncountedSegment.getName(), false);
        Assert.assertNull("Segment should not be present", log.getSegment(uncountedSegment.getName()));
        Assert.assertFalse("Segment file should not be present", segmentFile.exists());
        // cannot drop a segment that does not exist
        // cannot drop the active segment
        String[] segmentsToDrop = new String[]{ uncountedSegment.getName(), loadedSegment.getName() };
        for (String segmentToDrop : segmentsToDrop) {
            try {
                log.dropSegment(segmentToDrop, false);
                Assert.fail("Should have failed to drop segment");
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
        }
        // drop a segment and decrement total segment count
        log.dropSegment(log.getFirstSegment().getName(), true);
        // should be able to write now
        buffer = ByteBuffer.allocate(1);
        LogTest.CHANNEL_APPENDER.append(log, buffer);
    }

    /**
     * Checks that the constructor that receives segments and segment names iterator,
     * {@link Log#Log(String, long, long, DiskSpaceAllocator, StoreMetrics, boolean, List, Iterator)}, loads the segments
     * correctly and uses the iterator to name new segments and uses the default algorithm once the names run out.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void logSegmentCustomNamesTest() throws IOException {
        int numSegments = ((int) ((LogTest.LOG_CAPACITY) / (LogTest.SEGMENT_CAPACITY)));
        LogSegment segment = getLogSegment(LogSegmentNameHelper.getName(0, 0), LogTest.SEGMENT_CAPACITY, true);
        long startPos = 2 * numSegments;
        List<Pair<String, String>> expectedSegmentAndFileNames = new ArrayList<>(numSegments);
        expectedSegmentAndFileNames.add(new Pair(segment.getName(), segment.getView().getFirst().getName()));
        List<Pair<String, String>> segmentNameAndFileNamesDesired = new ArrayList<>();
        String lastName = null;
        for (int i = 0; i < 2; i++) {
            lastName = LogSegmentNameHelper.getName((startPos + i), 0);
            String fileName = (LogSegmentNameHelper.nameToFilename(lastName)) + "_modified";
            segmentNameAndFileNamesDesired.add(new Pair(lastName, fileName));
            expectedSegmentAndFileNames.add(new Pair(lastName, fileName));
        }
        for (int i = expectedSegmentAndFileNames.size(); i < numSegments; i++) {
            lastName = LogSegmentNameHelper.getNextPositionName(lastName);
            String fileName = LogSegmentNameHelper.nameToFilename(lastName);
            expectedSegmentAndFileNames.add(new Pair(lastName, fileName));
        }
        Log log = new Log(tempDir.getAbsolutePath(), LogTest.LOG_CAPACITY, LogTest.SEGMENT_CAPACITY, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics, true, Collections.singletonList(segment), segmentNameAndFileNamesDesired.iterator());
        // write enough so that all segments are allocated
        ByteBuffer buffer = ByteBuffer.allocate(((int) ((segment.getCapacityInBytes()) - (segment.getStartOffset()))));
        for (int i = 0; i < numSegments; i++) {
            buffer.rewind();
            LogTest.CHANNEL_APPENDER.append(log, buffer);
        }
        segment = log.getFirstSegment();
        for (Pair<String, String> nameAndFilename : expectedSegmentAndFileNames) {
            Assert.assertEquals("Segment name does not match", nameAndFilename.getFirst(), segment.getName());
            Assert.assertEquals("Segment file does not match", nameAndFilename.getSecond(), segment.getView().getFirst().getName());
            segment = log.getNextSegment(segment);
        }
        Assert.assertNull("There should be no more segments", segment);
    }

    /**
     * Interface for abstracting append operations.
     */
    private interface Appender {
        /**
         * Appends the data of {@code buffer} to {@code log}.
         *
         * @param log
         * 		the {@link Log} to append {@code buffer} to.
         * @param buffer
         * 		the data to append to {@code log}.
         * @throws IOException
         * 		
         */
        void append(Log log, ByteBuffer buffer) throws IOException;
    }
}

