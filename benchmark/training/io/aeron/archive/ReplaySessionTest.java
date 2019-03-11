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


import Archive.Context;
import FrameDescriptor.BEGIN_FRAG_FLAG;
import FrameDescriptor.END_FRAG_FLAG;
import FrameDescriptor.UNFRAGMENTED;
import ReplaySession.State.DONE;
import ReplaySession.State.INIT;
import ReplaySession.State.REPLAY;
import io.aeron.Counter;
import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.archive.client.AeronArchive;
import io.aeron.protocol.DataHeaderFlyweight;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static LogBufferDescriptor.TERM_MIN_LENGTH;


public class ReplaySessionTest {
    private static final int RECORDING_ID = 0;

    private static final int TERM_BUFFER_LENGTH = TERM_MIN_LENGTH;

    private static final int SEGMENT_LENGTH = ReplaySessionTest.TERM_BUFFER_LENGTH;

    private static final int INITIAL_TERM_ID = 8231773;

    private static final int INITIAL_TERM_OFFSET = 1024;

    private static final long REPLAY_ID = 1;

    private static final long START_POSITION = ReplaySessionTest.INITIAL_TERM_OFFSET;

    private static final long JOIN_POSITION = ReplaySessionTest.START_POSITION;

    private static final long RECORDING_POSITION = ReplaySessionTest.INITIAL_TERM_OFFSET;

    private static final long TIME = 0;

    private static final long CONNECT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);

    private static final int MTU_LENGTH = 4096;

    private static final int FRAME_LENGTH = 1024;

    private static final int SESSION_ID = 1;

    private static final int STREAM_ID = 1;

    private static final FileChannel ARCHIVE_DIR_CHANNEL = null;

    private final Image mockImage = Mockito.mock(Image.class);

    private final ExclusivePublication mockReplayPub = Mockito.mock(ExclusivePublication.class);

    private final ControlSession mockControlSession = Mockito.mock(ControlSession.class);

    private final ArchiveConductor mockArchiveConductor = Mockito.mock(ArchiveConductor.class);

    private final Counter recordingPositionCounter = Mockito.mock(Counter.class);

    private final UnsafeBuffer replayBuffer = new UnsafeBuffer(allocateDirectAligned(Archive.Configuration.MAX_BLOCK_LENGTH, 128));

    private int messageCounter = 0;

    private final RecordingSummary recordingSummary = new RecordingSummary();

    private final File archiveDir = TestUtil.makeTestDirectory();

    private final ControlResponseProxy proxy = Mockito.mock(ControlResponseProxy.class);

    private final EpochClock epochClock = Mockito.mock(EpochClock.class);

    private final Catalog mockCatalog = Mockito.mock(Catalog.class);

    private Context context;

    private long recordingPosition;

    @Test
    public void verifyRecordingFile() {
        try (RecordingReader reader = new RecordingReader(mockCatalog, recordingSummary, archiveDir, NULL_POSITION, AeronArchive.NULL_LENGTH, null)) {
            int fragments = reader.poll(( buffer, offset, length, frameType, flags, reservedValue) -> {
                final int frameOffset = offset - DataHeaderFlyweight.HEADER_LENGTH;
                assertEquals(offset, ((INITIAL_TERM_OFFSET) + HEADER_LENGTH));
                assertEquals(length, ((FRAME_LENGTH) - HEADER_LENGTH));
                assertEquals(FrameDescriptor.frameType(buffer, frameOffset), HDR_TYPE_DATA);
                assertEquals(FrameDescriptor.frameFlags(buffer, frameOffset), FrameDescriptor.UNFRAGMENTED);
            }, 1);
            Assert.assertEquals(1, fragments);
            fragments = reader.poll(( buffer, offset, length, frameType, flags, reservedValue) -> {
                final int frameOffset = offset - DataHeaderFlyweight.HEADER_LENGTH;
                assertEquals(offset, (((INITIAL_TERM_OFFSET) + (FRAME_LENGTH)) + HEADER_LENGTH));
                assertEquals(length, ((FRAME_LENGTH) - HEADER_LENGTH));
                assertEquals(FrameDescriptor.frameType(buffer, frameOffset), HDR_TYPE_DATA);
                assertEquals(FrameDescriptor.frameFlags(buffer, frameOffset), FrameDescriptor.BEGIN_FRAG_FLAG);
            }, 1);
            Assert.assertEquals(1, fragments);
            fragments = reader.poll(( buffer, offset, length, frameType, flags, reservedValue) -> {
                final int frameOffset = offset - DataHeaderFlyweight.HEADER_LENGTH;
                assertEquals(offset, (((INITIAL_TERM_OFFSET) + (2 * (FRAME_LENGTH))) + HEADER_LENGTH));
                assertEquals(length, ((FRAME_LENGTH) - HEADER_LENGTH));
                assertEquals(FrameDescriptor.frameType(buffer, frameOffset), HDR_TYPE_DATA);
                assertEquals(FrameDescriptor.frameFlags(buffer, frameOffset), FrameDescriptor.END_FRAG_FLAG);
            }, 1);
            Assert.assertEquals(1, fragments);
            fragments = reader.poll(( buffer, offset, length, frameType, flags, reservedValue) -> {
                final int frameOffset = offset - DataHeaderFlyweight.HEADER_LENGTH;
                assertEquals(offset, (((INITIAL_TERM_OFFSET) + (3 * (FRAME_LENGTH))) + HEADER_LENGTH));
                assertEquals(length, ((FRAME_LENGTH) - HEADER_LENGTH));
                assertEquals(FrameDescriptor.frameType(buffer, frameOffset), HDR_TYPE_PAD);
                assertEquals(FrameDescriptor.frameFlags(buffer, frameOffset), FrameDescriptor.UNFRAGMENTED);
            }, 1);
            Assert.assertEquals(1, fragments);
        }
    }

    @Test
    public void shouldReplayPartialDataFromFile() {
        final long correlationId = 1L;
        try (ReplaySession replaySession = replaySession(ReplaySessionTest.FRAME_LENGTH, correlationId, mockReplayPub, mockControlSession, recordingPositionCounter)) {
            Mockito.when(mockReplayPub.isClosed()).thenReturn(false);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(false);
            replaySession.doWork();
            Assert.assertEquals(replaySession.state(), INIT);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(true);
            replaySession.doWork();
            Assert.assertEquals(replaySession.state(), REPLAY);
            Mockito.verify(mockControlSession).sendOkResponse(ArgumentMatchers.eq(correlationId), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(proxy));
            final UnsafeBuffer termBuffer = new UnsafeBuffer(allocateDirectAligned(4096, 64));
            mockPublication(mockReplayPub, termBuffer);
            Assert.assertNotEquals(0, replaySession.doWork());
            Assert.assertThat(messageCounter, Matchers.is(1));
            ReplaySessionTest.validateFrame(termBuffer, 0, UNFRAGMENTED);
            Assert.assertTrue(replaySession.isDone());
        }
    }

    @Test
    public void shouldNotReplayPartialUnalignedDataFromFile() {
        final long correlationId = 1L;
        final ReplaySession replaySession = new ReplaySession(((ReplaySessionTest.RECORDING_POSITION) + 1), ReplaySessionTest.FRAME_LENGTH, ReplaySessionTest.REPLAY_ID, ReplaySessionTest.CONNECT_TIMEOUT_MS, correlationId, mockControlSession, proxy, replayBuffer, mockCatalog, archiveDir, null, epochClock, mockReplayPub, recordingSummary, recordingPositionCounter);
        replaySession.doWork();
        Assert.assertEquals(DONE, replaySession.state());
        final ControlResponseProxy proxy = Mockito.mock(ControlResponseProxy.class);
        replaySession.sendPendingError(proxy);
        Mockito.verify(mockControlSession).attemptErrorResponse(ArgumentMatchers.eq(correlationId), ArgumentMatchers.anyString(), ArgumentMatchers.eq(proxy));
    }

    @Test
    public void shouldReplayFullDataFromFile() {
        final long length = 4 * (ReplaySessionTest.FRAME_LENGTH);
        final long correlationId = 1L;
        try (ReplaySession replaySession = replaySession(length, correlationId, mockReplayPub, mockControlSession, null)) {
            Mockito.when(mockReplayPub.isClosed()).thenReturn(false);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(false);
            replaySession.doWork();
            Assert.assertEquals(replaySession.state(), INIT);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(true);
            replaySession.doWork();
            Assert.assertEquals(replaySession.state(), REPLAY);
            Mockito.verify(mockControlSession).sendOkResponse(ArgumentMatchers.eq(correlationId), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(proxy));
            final UnsafeBuffer termBuffer = new UnsafeBuffer(allocateDirectAligned(4096, 64));
            mockPublication(mockReplayPub, termBuffer);
            Assert.assertNotEquals(0, replaySession.doWork());
            Assert.assertThat(messageCounter, Matchers.is(4));
            ReplaySessionTest.validateFrame(termBuffer, 0, UNFRAGMENTED);
            ReplaySessionTest.validateFrame(termBuffer, 1, BEGIN_FRAG_FLAG);
            ReplaySessionTest.validateFrame(termBuffer, 2, END_FRAG_FLAG);
            Mockito.verify(mockReplayPub).appendPadding(((ReplaySessionTest.FRAME_LENGTH) - (HEADER_LENGTH)));
            Assert.assertTrue(replaySession.isDone());
        }
    }

    @Test
    public void shouldGiveUpIfPublishersAreNotConnectedAfterTimeout() {
        final long length = 1024L;
        final long correlationId = 1L;
        try (ReplaySession replaySession = replaySession(length, correlationId, mockReplayPub, mockControlSession, recordingPositionCounter)) {
            Mockito.when(mockReplayPub.isClosed()).thenReturn(false);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(false);
            replaySession.doWork();
            Mockito.when(epochClock.time()).thenReturn((((ReplaySessionTest.CONNECT_TIMEOUT_MS) + (ReplaySessionTest.TIME)) + 1L));
            replaySession.doWork();
            Assert.assertTrue(replaySession.isDone());
        }
    }

    @Test
    public void shouldReplayFromActiveRecording() throws IOException {
        final UnsafeBuffer termBuffer = new UnsafeBuffer(allocateDirectAligned(4096, 64));
        final int recordingId = (ReplaySessionTest.RECORDING_ID) + 1;
        recordingSummary.recordingId = recordingId;
        recordingSummary.stopPosition = NULL_POSITION;
        Mockito.when(mockCatalog.stopPosition(recordingId)).thenReturn(((ReplaySessionTest.START_POSITION) + ((ReplaySessionTest.FRAME_LENGTH) * 4)));
        recordingPosition = ReplaySessionTest.START_POSITION;
        final RecordingWriter writer = new RecordingWriter(recordingId, ReplaySessionTest.START_POSITION, ReplaySessionTest.SEGMENT_LENGTH, mockImage, context, ReplaySessionTest.ARCHIVE_DIR_CHANNEL);
        writer.init();
        final UnsafeBuffer buffer = new UnsafeBuffer(allocateDirectAligned(ReplaySessionTest.TERM_BUFFER_LENGTH, 64));
        final DataHeaderFlyweight headerFwt = new DataHeaderFlyweight();
        final Header header = new Header(ReplaySessionTest.INITIAL_TERM_ID, Integer.numberOfLeadingZeros(ReplaySessionTest.TERM_BUFFER_LENGTH));
        header.buffer(buffer);
        recordFragment(writer, buffer, headerFwt, header, 0, UNFRAGMENTED, HDR_TYPE_DATA);
        recordFragment(writer, buffer, headerFwt, header, 1, BEGIN_FRAG_FLAG, HDR_TYPE_DATA);
        final long length = 5 * (ReplaySessionTest.FRAME_LENGTH);
        final long correlationId = 1L;
        try (ReplaySession replaySession = replaySession(length, correlationId, mockReplayPub, mockControlSession, recordingPositionCounter)) {
            Mockito.when(mockReplayPub.isClosed()).thenReturn(false);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(false);
            replaySession.doWork();
            Assert.assertEquals(replaySession.state(), INIT);
            Mockito.when(mockReplayPub.isConnected()).thenReturn(true);
            replaySession.doWork();
            Assert.assertEquals(replaySession.state(), REPLAY);
            Mockito.verify(mockControlSession).sendOkResponse(ArgumentMatchers.eq(correlationId), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(proxy));
            mockPublication(mockReplayPub, termBuffer);
            Assert.assertNotEquals(0, replaySession.doWork());
            Assert.assertThat(messageCounter, Matchers.is(2));
            ReplaySessionTest.validateFrame(termBuffer, 0, UNFRAGMENTED);
            ReplaySessionTest.validateFrame(termBuffer, 1, BEGIN_FRAG_FLAG);
            Assert.assertEquals(0, replaySession.doWork());
            recordFragment(writer, buffer, headerFwt, header, 2, END_FRAG_FLAG, HDR_TYPE_DATA);
            recordFragment(writer, buffer, headerFwt, header, 3, UNFRAGMENTED, HDR_TYPE_PAD);
            writer.close();
            Mockito.when(recordingPositionCounter.isClosed()).thenReturn(true);
            Mockito.when(mockCatalog.stopPosition(recordingId)).thenReturn(((ReplaySessionTest.START_POSITION) + ((ReplaySessionTest.FRAME_LENGTH) * 4)));
            Assert.assertNotEquals(0, replaySession.doWork());
            ReplaySessionTest.validateFrame(termBuffer, 2, END_FRAG_FLAG);
            Mockito.verify(mockReplayPub).appendPadding(((ReplaySessionTest.FRAME_LENGTH) - (HEADER_LENGTH)));
            Assert.assertTrue(replaySession.isDone());
        }
    }
}

