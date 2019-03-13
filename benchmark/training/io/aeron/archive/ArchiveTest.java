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


import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.archive.client.ArchiveProxy;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CommonContext.IPC_CHANNEL;


@RunWith(Parameterized.class)
public class ArchiveTest {
    @Parameterized.Parameter
    public ThreadingMode threadingMode;

    @Parameterized.Parameter(1)
    public ArchiveThreadingMode archiveThreadingMode;

    private long controlSessionId;

    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final String CONTROL_RESPONSE_URI = IPC_CHANNEL;

    private static final int CONTROL_RESPONSE_STREAM_ID = 100;

    private static final String REPLAY_URI = IPC_CHANNEL;

    private static final int MESSAGE_COUNT = 5000;

    private static final int SYNC_LEVEL = 0;

    private static final int PUBLISH_STREAM_ID = 1;

    private static final int MAX_FRAGMENT_SIZE = 1024;

    private static final int REPLAY_STREAM_ID = 101;

    private final UnsafeBuffer buffer = new UnsafeBuffer(allocateDirectAligned(4096, FrameDescriptor.FRAME_ALIGNMENT));

    private final Random rnd = new Random();

    private final long seed = System.nanoTime();

    @Rule
    public final TestWatcher testWatcher = TestUtil.newWatcher(this.getClass(), seed);

    private String publishUri;

    private Aeron client;

    private Archive archive;

    private MediaDriver driver;

    private long recordingId;

    private long remaining;

    private int messageCount;

    private int[] messageLengths;

    private long totalDataLength;

    private long totalRecordingLength;

    private volatile long recorded;

    private long requestedStartPosition;

    private volatile long stopPosition = NULL_POSITION;

    private Throwable trackerError;

    private Subscription controlResponse;

    private long correlationId;

    private long startPosition;

    private int requestedInitialTermId;

    private Thread replayConsumer = null;

    private Thread progressTracker = null;

    @Test(timeout = 10000)
    public void recordAndReplayExclusivePublication() {
        final String controlChannel = archive.context().localControlChannel();
        final int controlStreamId = archive.context().localControlStreamId();
        final String recordingChannel = archive.context().recordingEventsChannel();
        final int recordingStreamId = archive.context().recordingEventsStreamId();
        final Publication controlPublication = client.addPublication(controlChannel, controlStreamId);
        final Subscription recordingEvents = client.addSubscription(recordingChannel, recordingStreamId);
        final ArchiveProxy archiveProxy = new ArchiveProxy(controlPublication);
        prePublicationActionsAndVerifications(archiveProxy, controlPublication, recordingEvents);
        final ExclusivePublication recordedPublication = client.addExclusivePublication(publishUri, ArchiveTest.PUBLISH_STREAM_ID);
        final int sessionId = recordedPublication.sessionId();
        final int termBufferLength = recordedPublication.termBufferLength();
        final int initialTermId = recordedPublication.initialTermId();
        final int maxPayloadLength = recordedPublication.maxPayloadLength();
        final long startPosition = recordedPublication.position();
        Assert.assertThat(startPosition, Is.is(requestedStartPosition));
        Assert.assertThat(recordedPublication.initialTermId(), Is.is(requestedInitialTermId));
        preSendChecks(archiveProxy, recordingEvents, sessionId, termBufferLength, startPosition);
        final int messageCount = prepAndSendMessages(recordingEvents, recordedPublication);
        postPublicationValidations(archiveProxy, recordingEvents, termBufferLength, initialTermId, maxPayloadLength, messageCount);
    }

    @Test(timeout = 10000)
    public void replayExclusivePublicationWhileRecording() {
        final String controlChannel = archive.context().localControlChannel();
        final int controlStreamId = archive.context().localControlStreamId();
        final String recordingChannel = archive.context().recordingEventsChannel();
        final int recordingStreamId = archive.context().recordingEventsStreamId();
        final Publication controlPublication = client.addPublication(controlChannel, controlStreamId);
        final Subscription recordingEvents = client.addSubscription(recordingChannel, recordingStreamId);
        final ArchiveProxy archiveProxy = new ArchiveProxy(controlPublication);
        prePublicationActionsAndVerifications(archiveProxy, controlPublication, recordingEvents);
        final ExclusivePublication recordedPublication = client.addExclusivePublication(publishUri, ArchiveTest.PUBLISH_STREAM_ID);
        final int sessionId = recordedPublication.sessionId();
        final int termBufferLength = recordedPublication.termBufferLength();
        final int initialTermId = recordedPublication.initialTermId();
        final int maxPayloadLength = recordedPublication.maxPayloadLength();
        final long startPosition = recordedPublication.position();
        Assert.assertThat(startPosition, Is.is(requestedStartPosition));
        Assert.assertThat(recordedPublication.initialTermId(), Is.is(requestedInitialTermId));
        preSendChecks(archiveProxy, recordingEvents, sessionId, termBufferLength, startPosition);
        final int messageCount = ArchiveTest.MESSAGE_COUNT;
        final CountDownLatch streamConsumed = new CountDownLatch(2);
        prepMessagesAndListener(recordingEvents, messageCount, streamConsumed);
        replayConsumer = validateActiveRecordingReplay(archiveProxy, termBufferLength, initialTermId, maxPayloadLength, messageCount, streamConsumed);
        publishDataToBeRecorded(recordedPublication, messageCount);
        await(streamConsumed);
    }

    @Test(timeout = 10000)
    public void recordAndReplayRegularPublication() {
        final String controlChannel = archive.context().localControlChannel();
        final int controlStreamId = archive.context().localControlStreamId();
        final String recordingChannel = archive.context().recordingEventsChannel();
        final int recordingStreamId = archive.context().recordingEventsStreamId();
        final Publication controlPublication = client.addPublication(controlChannel, controlStreamId);
        final Subscription recordingEvents = client.addSubscription(recordingChannel, recordingStreamId);
        final ArchiveProxy archiveProxy = new ArchiveProxy(controlPublication);
        prePublicationActionsAndVerifications(archiveProxy, controlPublication, recordingEvents);
        final Publication recordedPublication = client.addPublication(publishUri, ArchiveTest.PUBLISH_STREAM_ID);
        final int sessionId = recordedPublication.sessionId();
        final int termBufferLength = recordedPublication.termBufferLength();
        final int initialTermId = recordedPublication.initialTermId();
        final int maxPayloadLength = recordedPublication.maxPayloadLength();
        final long startPosition = recordedPublication.position();
        preSendChecks(archiveProxy, recordingEvents, sessionId, termBufferLength, startPosition);
        final int messageCount = prepAndSendMessages(recordingEvents, recordedPublication);
        postPublicationValidations(archiveProxy, recordingEvents, termBufferLength, initialTermId, maxPayloadLength, messageCount);
    }
}

