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
import io.aeron.Counter;
import io.aeron.Image;
import io.aeron.archive.client.AeronArchive;
import io.aeron.logbuffer.BlockHandler;
import io.aeron.logbuffer.FrameDescriptor;
import io.aeron.logbuffer.LogBufferDescriptor;
import java.io.File;
import java.nio.channels.FileChannel;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RecordingSessionTest {
    private static final int RECORDED_BLOCK_LENGTH = 100;

    private static final long RECORDING_ID = 12345;

    private static final int TERM_BUFFER_LENGTH = LogBufferDescriptor.TERM_MIN_LENGTH;

    private static final int SEGMENT_LENGTH = RecordingSessionTest.TERM_BUFFER_LENGTH;

    private static final String CHANNEL = "channel";

    private static final String SOURCE_IDENTITY = "sourceIdentity";

    private static final int STREAM_ID = 54321;

    private static final int SESSION_ID = 12345;

    private static final int TERM_OFFSET = 1024;

    private static final int MTU_LENGTH = 1024;

    private static final long START_POSITION = RecordingSessionTest.TERM_OFFSET;

    private static final int INITIAL_TERM_ID = 0;

    public static final FileChannel ARCHIVE_CHANNEL = null;

    private final RecordingEventsProxy recordingEventsProxy = Mockito.mock(RecordingEventsProxy.class);

    private final Counter mockPosition = Mockito.mock(Counter.class);

    private final Image image = RecordingSessionTest.mockImage(RecordingSessionTest.mockSubscription());

    private final File archiveDir = TestUtil.makeTestDirectory();

    private FileChannel mockLogBufferChannel;

    private UnsafeBuffer mockLogBufferMapped;

    private File termFile;

    private final EpochClock epochClock = Mockito.mock(EpochClock.class);

    private final Catalog mockCatalog = Mockito.mock(Catalog.class);

    private Context context;

    private long positionLong;

    @Test
    public void shouldRecordFragmentsFromImage() {
        final RecordingSession session = new RecordingSession(RecordingSessionTest.RECORDING_ID, RecordingSessionTest.START_POSITION, RecordingSessionTest.SEGMENT_LENGTH, RecordingSessionTest.CHANNEL, recordingEventsProxy, image, mockPosition, RecordingSessionTest.ARCHIVE_CHANNEL, context);
        Assert.assertEquals(RecordingSessionTest.RECORDING_ID, session.sessionId());
        session.doWork();
        Mockito.when(image.blockPoll(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenAnswer(( invocation) -> {
            final BlockHandler handle = invocation.getArgument(0);
            if (handle == null) {
                return 0;
            }
            handle.onBlock(mockLogBufferMapped, TERM_OFFSET, RECORDED_BLOCK_LENGTH, SESSION_ID, 0);
            return RECORDED_BLOCK_LENGTH;
        });
        Assert.assertNotEquals("Expect some work", 0, session.doWork());
        final File segmentFile = new File(archiveDir, Archive.segmentFileName(RecordingSessionTest.RECORDING_ID, 0));
        Assert.assertTrue(segmentFile.exists());
        final RecordingSummary recordingSummary = new RecordingSummary();
        recordingSummary.recordingId = RecordingSessionTest.RECORDING_ID;
        recordingSummary.startPosition = RecordingSessionTest.START_POSITION;
        recordingSummary.segmentFileLength = context.segmentFileLength();
        recordingSummary.initialTermId = RecordingSessionTest.INITIAL_TERM_ID;
        recordingSummary.termBufferLength = RecordingSessionTest.TERM_BUFFER_LENGTH;
        recordingSummary.streamId = RecordingSessionTest.STREAM_ID;
        recordingSummary.sessionId = RecordingSessionTest.SESSION_ID;
        recordingSummary.stopPosition = (RecordingSessionTest.START_POSITION) + (RecordingSessionTest.RECORDED_BLOCK_LENGTH);
        try (RecordingReader reader = new RecordingReader(mockCatalog, recordingSummary, archiveDir, NULL_POSITION, AeronArchive.NULL_LENGTH, null)) {
            final int fragments = reader.poll(( buffer, offset, length, frameType, flags, reservedValue) -> {
                final int frameOffset = offset - DataHeaderFlyweight.HEADER_LENGTH;
                assertEquals(TERM_OFFSET, frameOffset);
                assertEquals(RECORDED_BLOCK_LENGTH, FrameDescriptor.frameLength(buffer, frameOffset));
                assertEquals(((RECORDED_BLOCK_LENGTH) - DataHeaderFlyweight.HEADER_LENGTH), length);
            }, 1);
            Assert.assertEquals(1, fragments);
        }
        Mockito.when(image.blockPoll(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(0);
        Assert.assertEquals("Expect no work", 0, session.doWork());
        Mockito.when(image.isClosed()).thenReturn(true);
        session.doWork();
        session.doWork();
        Assert.assertTrue(session.isDone());
        session.close();
    }
}

