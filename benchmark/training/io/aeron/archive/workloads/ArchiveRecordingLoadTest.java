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


import SourceLocation.LOCAL;
import io.aeron.archive.Archive;
import io.aeron.archive.TestUtil;
import io.aeron.archive.client.AeronArchive;
import io.aeron.driver.MediaDriver;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;


@Ignore
public class ArchiveRecordingLoadTest {
    private static final String CONTROL_RESPONSE_URI = "aeron:udp?endpoint=localhost:54327";

    private static final int CONTROL_RESPONSE_STREAM_ID = 100;

    private static final String PUBLISH_URI = new ChannelUriStringBuilder().media("ipc").mtu((16 * 1024)).termLength(((32 * 1024) * 1024)).build();

    private static final int TEST_DURATION_SEC = 30;

    private static final int PUBLISH_STREAM_ID = 1;

    private static final int MAX_FRAGMENT_SIZE = 1024;

    private static final double MEGABYTE = 1024.0 * 1024.0;

    private static final int MESSAGE_COUNT = 2000000;

    private final UnsafeBuffer buffer = new UnsafeBuffer(allocateDirectAligned(4096, FrameDescriptor.FRAME_ALIGNMENT));

    private final Random rnd = new Random();

    private final long seed = System.nanoTime();

    @Rule
    public final TestWatcher testWatcher = TestUtil.newWatcher(this.getClass(), seed);

    private Aeron aeron;

    private Archive archive;

    private MediaDriver driver;

    private AeronArchive aeronArchive;

    private long recordingId;

    private int[] messageLengths;

    private long totalDataLength;

    private long expectedRecordingLength;

    private long recordedLength;

    private boolean doneRecording;

    private BooleanSupplier recordingStarted;

    private BooleanSupplier recordingEnded;

    @Test
    public void archive() throws InterruptedException {
        try (Subscription recordingEvents = aeron.addSubscription(archive.context().recordingEventsChannel(), archive.context().recordingEventsStreamId())) {
            initRecordingStartIndicator(recordingEvents);
            initRecordingEndIndicator(recordingEvents);
            TestUtil.await(recordingEvents::isConnected);
            final long deadlineMs = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(ArchiveRecordingLoadTest.TEST_DURATION_SEC));
            while ((System.currentTimeMillis()) < deadlineMs) {
                aeronArchive.startRecording(ArchiveRecordingLoadTest.PUBLISH_URI, ArchiveRecordingLoadTest.PUBLISH_STREAM_ID, LOCAL);
                final long start;
                try (ExclusivePublication publication = aeron.addExclusivePublication(ArchiveRecordingLoadTest.PUBLISH_URI, ArchiveRecordingLoadTest.PUBLISH_STREAM_ID)) {
                    TestUtil.await(publication::isConnected);
                    TestUtil.await(recordingStarted);
                    doneRecording = false;
                    start = System.currentTimeMillis();
                    prepAndSendMessages(publication);
                }
                while (!(doneRecording)) {
                    TestUtil.await(recordingEnded);
                } 
                printScore(((System.currentTimeMillis()) - start));
                Assert.assertThat(expectedRecordingLength, Is.is(recordedLength));
                aeronArchive.stopRecording(ArchiveRecordingLoadTest.PUBLISH_URI, ArchiveRecordingLoadTest.PUBLISH_STREAM_ID);
                Thread.sleep(100);
            } 
        }
    }
}

