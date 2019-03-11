/**
 * Copyright 2014-2019 Real Logic Ltd.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.archive;


import ArrayUtil.EMPTY_BYTE_ARRAY;
import RecordingDescriptorDecoder.SCHEMA_VERSION;
import RecordingDescriptorHeaderDecoder.BLOCK_LENGTH;
import io.aeron.archive.codecs.RecordingDescriptorDecoder;
import io.aeron.protocol.DataHeaderFlyweight;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static Catalog.PAGE_SIZE;


public class CatalogTest {
    private static final long MAX_ENTRIES = 1024;

    private static final int TERM_LENGTH = 2 * (PAGE_SIZE);

    private static final int SEGMENT_LENGTH = 2 * (CatalogTest.TERM_LENGTH);

    private static final int MTU_LENGTH = 1024;

    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer();

    private final RecordingDescriptorDecoder recordingDescriptorDecoder = new RecordingDescriptorDecoder();

    private final File archiveDir = TestUtil.makeTestDirectory();

    private long currentTimeMs = 1;

    private final EpochClock clock = () -> currentTimeMs;

    private long recordingOneId;

    private long recordingTwoId;

    private long recordingThreeId;

    @Test
    public void shouldReloadExistingIndex() {
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            verifyRecordingForId(catalog, recordingOneId, 6, 1, "channelG", "sourceA");
            verifyRecordingForId(catalog, recordingTwoId, 7, 2, "channelH", "sourceV");
            verifyRecordingForId(catalog, recordingThreeId, 8, 3, "channelK", "sourceB");
        }
    }

    @Test
    public void shouldAppendToExistingIndex() {
        final long newRecordingId;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, CatalogTest.MAX_ENTRIES, () -> 3L)) {
            newRecordingId = catalog.addNewRecording(0L, 0L, 0, CatalogTest.SEGMENT_LENGTH, CatalogTest.TERM_LENGTH, CatalogTest.MTU_LENGTH, 9, 4, "channelJ", "channelJ?tag=f", "sourceN");
        }
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            verifyRecordingForId(catalog, recordingOneId, 6, 1, "channelG", "sourceA");
            verifyRecordingForId(catalog, newRecordingId, 9, 4, "channelJ", "sourceN");
        }
    }

    @Test
    public void shouldAllowMultipleInstancesForSameStream() {
        try (Catalog ignore = new Catalog(archiveDir, clock)) {
            final long newRecordingId = newRecording();
            Assert.assertNotEquals(recordingOneId, newRecordingId);
        }
    }

    @Test
    public void shouldIncreaseMaxEntries() {
        final long newMaxEntries = (CatalogTest.MAX_ENTRIES) * 2;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, newMaxEntries, clock)) {
            Assert.assertEquals(newMaxEntries, catalog.maxEntries());
        }
    }

    @Test
    public void shouldNotDecreaseMaxEntries() {
        final long newMaxEntries = 1;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, newMaxEntries, clock)) {
            Assert.assertEquals(CatalogTest.MAX_ENTRIES, catalog.maxEntries());
        }
    }

    @Test
    public void shouldFixTimestampForEmptyRecordingAfterFailure() {
        final long newRecordingId = newRecording();
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> assertThat(decoder.stopTimestamp(), is(NULL_TIMESTAMP)), newRecordingId);
        }
        currentTimeMs = 42L;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, CatalogTest.MAX_ENTRIES, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> assertThat(decoder.stopTimestamp(), is(42L)), newRecordingId);
        }
    }

    @Test
    public void shouldFixTimestampAndPositionAfterFailureSamePage() throws Exception {
        final long newRecordingId = newRecording();
        new File(archiveDir, Archive.segmentFileName(newRecordingId, 0)).createNewFile();
        new File(archiveDir, Archive.segmentFileName(newRecordingId, 1)).createNewFile();
        new File(archiveDir, Archive.segmentFileName(newRecordingId, 2)).createNewFile();
        final File segmentFile = new File(archiveDir, Archive.segmentFileName(newRecordingId, 3));
        try (FileChannel log = FileChannel.open(segmentFile.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            final ByteBuffer bb = ByteBuffer.allocateDirect(HEADER_LENGTH);
            final DataHeaderFlyweight flyweight = new DataHeaderFlyweight(bb);
            flyweight.frameLength(1024);
            log.write(bb);
            bb.clear();
            flyweight.frameLength(128);
            log.write(bb, 1024);
            bb.clear();
            flyweight.frameLength(0);
            log.write(bb, (1024 + 128));
        }
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> {
                assertThat(decoder.stopTimestamp(), is(NULL_TIMESTAMP));
                assertThat(decoder.stopPosition(), is(NULL_POSITION));
            }, newRecordingId);
        }
        currentTimeMs = 42L;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, CatalogTest.MAX_ENTRIES, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> {
                assertThat(decoder.stopTimestamp(), is(42L));
                assertThat(decoder.stopPosition(), is(((((SEGMENT_LENGTH) * 3) + 1024L) + 128L)));
            }, newRecordingId);
        }
    }

    @Test
    public void shouldFixTimestampAndPositionAfterFailurePageStraddle() throws Exception {
        final long newRecordingId = newRecording();
        createSegmentFile(newRecordingId);
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> {
                assertThat(decoder.stopTimestamp(), is(NULL_TIMESTAMP));
                assertThat(decoder.stopPosition(), is(NULL_POSITION));
            }, newRecordingId);
        }
        currentTimeMs = 42L;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, CatalogTest.MAX_ENTRIES, clock)) {
            Assert.assertTrue(catalog.forEntry(( he, hd, e, decoder) -> {
                assertThat(decoder.stopTimestamp(), is(42L));
                assertThat(decoder.stopPosition(), is((((long) (PAGE_SIZE)) - HEADER_LENGTH)));
            }, newRecordingId));
        }
    }

    @Test
    public void shouldFixTimestampAndPositionAfterFailureFullSegment() throws Exception {
        final long newRecordingId = newRecording();
        final long expectedLastFrame = (CatalogTest.SEGMENT_LENGTH) - 128;
        final File segmentFile = new File(archiveDir, Archive.segmentFileName(newRecordingId, 0));
        try (FileChannel log = FileChannel.open(segmentFile.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            final ByteBuffer bb = ByteBuffer.allocateDirect(HEADER_LENGTH);
            final DataHeaderFlyweight flyweight = new DataHeaderFlyweight(bb);
            flyweight.frameLength(((int) (expectedLastFrame)));
            log.write(bb);
            bb.clear();
            flyweight.frameLength(128);
            log.write(bb, expectedLastFrame);
            bb.clear();
            flyweight.frameLength(0);
            log.write(bb, (expectedLastFrame + 128));
            log.truncate(CatalogTest.SEGMENT_LENGTH);
        }
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> {
                assertThat(decoder.stopTimestamp(), is(NULL_TIMESTAMP));
                e.stopPosition(NULL_POSITION);
            }, newRecordingId);
        }
        currentTimeMs = 42L;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, CatalogTest.MAX_ENTRIES, clock)) {
            catalog.forEntry(( he, hd, e, decoder) -> {
                assertThat(decoder.stopTimestamp(), is(42L));
                assertThat(decoder.stopPosition(), is(expectedLastFrame));
            }, newRecordingId);
        }
    }

    @Test
    public void shouldBeAbleToCreateMaxEntries() {
        after();
        final File archiveDir = TestUtil.makeTestDirectory();
        final long maxEntries = 2;
        try (Catalog catalog = new Catalog(archiveDir, null, 0, maxEntries, clock)) {
            for (int i = 0; i < maxEntries; i++) {
                recordingOneId = catalog.addNewRecording(0L, 0L, 0, CatalogTest.SEGMENT_LENGTH, CatalogTest.TERM_LENGTH, CatalogTest.MTU_LENGTH, 6, 1, "channelG", "channelG?tag=f", "sourceA");
            }
        }
        try (Catalog catalog = new Catalog(archiveDir, null, 0, maxEntries, clock)) {
            Assert.assertEquals(maxEntries, catalog.countEntries());
        }
    }

    @Test
    public void shouldNotThrowWhenOldRecordingLogsAreDeleted() throws IOException {
        createSegmentFile(recordingThreeId);
        final Path segmentFilePath = Paths.get(Archive.segmentFileName(recordingThreeId, 0));
        final boolean segmentFileExists = Files.exists(archiveDir.toPath().resolve(segmentFilePath));
        Assume.assumeThat(segmentFileExists, Matchers.is(true));
        final Catalog catalog = new Catalog(archiveDir, null, 0, CatalogTest.MAX_ENTRIES, clock);
        catalog.close();
    }

    @Test
    public void shouldContainChannelFragment() {
        try (Catalog catalog = new Catalog(archiveDir, clock)) {
            final String originalChannel = "aeron:udp?endpoint=localhost:7777|tags=777|alias=TestString";
            final String strippedChannel = "strippedChannelUri";
            final long recordingId = catalog.addNewRecording(0L, 0L, 0, CatalogTest.SEGMENT_LENGTH, CatalogTest.TERM_LENGTH, CatalogTest.MTU_LENGTH, 6, 1, strippedChannel, originalChannel, "sourceA");
            Assert.assertTrue(catalog.wrapDescriptor(recordingId, unsafeBuffer));
            recordingDescriptorDecoder.wrap(unsafeBuffer, BLOCK_LENGTH, RecordingDescriptorDecoder.BLOCK_LENGTH, SCHEMA_VERSION);
            Assert.assertTrue(Catalog.originalChannelContains(recordingDescriptorDecoder, EMPTY_BYTE_ARRAY));
            final byte[] originalChannelBytes = originalChannel.getBytes(StandardCharsets.US_ASCII);
            Assert.assertTrue(Catalog.originalChannelContains(recordingDescriptorDecoder, originalChannelBytes));
            final byte[] tagsBytes = "tags=777".getBytes(StandardCharsets.US_ASCII);
            Assert.assertTrue(Catalog.originalChannelContains(recordingDescriptorDecoder, tagsBytes));
            final byte[] testBytes = "TestString".getBytes(StandardCharsets.US_ASCII);
            Assert.assertTrue(Catalog.originalChannelContains(recordingDescriptorDecoder, testBytes));
            final byte[] wrongBytes = "wrong".getBytes(StandardCharsets.US_ASCII);
            Assert.assertFalse(Catalog.originalChannelContains(recordingDescriptorDecoder, wrongBytes));
        }
    }
}

