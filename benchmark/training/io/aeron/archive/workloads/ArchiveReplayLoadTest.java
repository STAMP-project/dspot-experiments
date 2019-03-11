/**
 * Copyright 2014-2019 Real Logic Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.archive.workloads;


import Configuration.INITIAL_WINDOW_LENGTH_PROP_NAME;
import Configuration.SOCKET_RCVBUF_LENGTH_PROP_NAME;
import Configuration.SOCKET_SNDBUF_LENGTH_PROP_NAME;
import SourceLocation.LOCAL;
import io.aeron.Aeron;
import io.aeron.ChannelUriStringBuilder;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.archive.Archive;
import io.aeron.archive.TestUtil;
import io.aeron.archive.client.AeronArchive;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;


@Ignore
public class ArchiveReplayLoadTest {
    private static final String CONTROL_RESPONSE_URI = "aeron:udp?endpoint=localhost:54327";

    private static final int CONTROL_RESPONSE_STREAM_ID = 100;

    private static final int TEST_DURATION_SEC = 60;

    private static final String REPLAY_URI = "aeron:udp?endpoint=localhost:54326";

    private static final String PUBLISH_URI = new ChannelUriStringBuilder().media("ipc").mtu((16 * 1024)).termLength(((8 * 1024) * 1024)).build();

    private static final int PUBLISH_STREAM_ID = 1;

    private static final int MAX_FRAGMENT_SIZE = 1024;

    private static final double MEGABYTE = 1024.0 * 1024.0;

    private static final int MESSAGE_COUNT = 1500000;

    static {
        System.setProperty(SOCKET_RCVBUF_LENGTH_PROP_NAME, Integer.toString(((2 * 1024) * 1024)));
        System.setProperty(SOCKET_SNDBUF_LENGTH_PROP_NAME, Integer.toString(((2 * 1024) * 1024)));
        System.setProperty(INITIAL_WINDOW_LENGTH_PROP_NAME, Integer.toString(((2 * 1024) * 1024)));
    }

    private final UnsafeBuffer buffer = new UnsafeBuffer(allocateDirectAligned(4096, FrameDescriptor.FRAME_ALIGNMENT));

    private final Random rnd = new Random();

    private final long seed = System.nanoTime();

    @Rule
    public final TestWatcher testWatcher = TestUtil.newWatcher(this.getClass(), seed);

    private Aeron aeron;

    private Archive archive;

    private MediaDriver driver;

    private AeronArchive aeronArchive;

    private long startPosition;

    private long recordingId = Aeron.NULL_VALUE;

    private long totalPayloadLength;

    private volatile long expectedRecordingLength;

    private long recordedLength = 0;

    private Throwable trackerError;

    private long receivedPosition = 0;

    private long remaining;

    private int fragmentCount;

    private final FragmentHandler validatingFragmentHandler = this::validateFragment;

    @Test(timeout = (ArchiveReplayLoadTest.TEST_DURATION_SEC) * 2000)
    public void replay() throws InterruptedException {
        final String channel = archive.context().recordingEventsChannel();
        final int streamId = archive.context().recordingEventsStreamId();
        try (Publication publication = aeron.addPublication(ArchiveReplayLoadTest.PUBLISH_URI, ArchiveReplayLoadTest.PUBLISH_STREAM_ID);Subscription recordingEvents = aeron.addSubscription(channel, streamId)) {
            TestUtil.await(recordingEvents::isConnected);
            aeronArchive.startRecording(ArchiveReplayLoadTest.PUBLISH_URI, ArchiveReplayLoadTest.PUBLISH_STREAM_ID, LOCAL);
            TestUtil.await(publication::isConnected);
            final CountDownLatch recordingStopped = prepAndSendMessages(recordingEvents, publication);
            Assert.assertNull(trackerError);
            recordingStopped.await();
            aeronArchive.stopRecording(ArchiveReplayLoadTest.PUBLISH_URI, ArchiveReplayLoadTest.PUBLISH_STREAM_ID);
            Assert.assertNull(trackerError);
            Assert.assertNotEquals((-1L), recordingId);
            Assert.assertEquals(expectedRecordingLength, recordedLength);
        }
        final long deadlineMs = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(ArchiveReplayLoadTest.TEST_DURATION_SEC));
        int i = 0;
        while ((System.currentTimeMillis()) < deadlineMs) {
            final long start = System.currentTimeMillis();
            replay(i);
            printScore((++i), ((System.currentTimeMillis()) - start));
            Thread.sleep(100);
        } 
    }
}

