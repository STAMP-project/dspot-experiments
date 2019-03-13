/**
 * Copyright 2014-2018 Real Logic Ltd.
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


import CommonContext.MDC_CONTROL_MODE_DYNAMIC;
import CommonContext.MDC_CONTROL_MODE_MANUAL;
import CommonContext.UDP_MEDIA;
import MediaDriver.Context;
import ReplayMerge.State.MERGED;
import io.aeron.archive.client.AeronArchive;
import io.aeron.archive.client.ReplayMerge;
import io.aeron.archive.status.RecordingPos;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.protocol.DataHeaderFlyweight;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.status.CountersReader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ReplayMergeTest {
    private static final String MESSAGE_PREFIX = "Message-Prefix-";

    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final int FRAGMENT_LIMIT = 10;

    private static final int TERM_BUFFER_LENGTH = 64 * 1024;

    private static final int MIN_MESSAGES_PER_TERM = (ReplayMergeTest.TERM_BUFFER_LENGTH) / ((ReplayMergeTest.MESSAGE_PREFIX.length()) + (DataHeaderFlyweight.HEADER_LENGTH));

    private static final int PUBLICATION_TAG = 2;

    private static final int STREAM_ID = 33;

    private static final String CONTROL_ENDPOINT = "localhost:43265";

    private static final String RECORDING_ENDPOINT = "localhost:43266";

    private static final String LIVE_ENDPOINT = "localhost:43267";

    private static final String REPLAY_ENDPOINT = "localhost:43268";

    private final ChannelUriStringBuilder publicationChannel = new ChannelUriStringBuilder().media(UDP_MEDIA).tags(("1," + (ReplayMergeTest.PUBLICATION_TAG))).controlEndpoint(ReplayMergeTest.CONTROL_ENDPOINT).controlMode(MDC_CONTROL_MODE_DYNAMIC).termLength(ReplayMergeTest.TERM_BUFFER_LENGTH);

    private ChannelUriStringBuilder recordingChannel = new ChannelUriStringBuilder().media(UDP_MEDIA).endpoint(ReplayMergeTest.RECORDING_ENDPOINT).controlEndpoint(ReplayMergeTest.CONTROL_ENDPOINT);

    private ChannelUriStringBuilder subscriptionChannel = new ChannelUriStringBuilder().media(UDP_MEDIA).controlMode(MDC_CONTROL_MODE_MANUAL);

    private final ChannelUriStringBuilder liveDestination = new ChannelUriStringBuilder().media(UDP_MEDIA).endpoint(ReplayMergeTest.LIVE_ENDPOINT).controlEndpoint(ReplayMergeTest.CONTROL_ENDPOINT);

    private final ChannelUriStringBuilder replayDestination = new ChannelUriStringBuilder().media(UDP_MEDIA).endpoint(ReplayMergeTest.REPLAY_ENDPOINT);

    private final ChannelUriStringBuilder replayChannel = new ChannelUriStringBuilder().media(UDP_MEDIA).isSessionIdTagged(true).sessionId(ReplayMergeTest.PUBLICATION_TAG).endpoint(ReplayMergeTest.REPLAY_ENDPOINT);

    private final ExpandableArrayBuffer buffer = new ExpandableArrayBuffer();

    private final MutableInteger received = new MutableInteger(0);

    private final Context mediaDriverContext = new MediaDriver.Context();

    private final int maxReceiverWindow = mediaDriverContext.initialWindowLength();

    private ArchivingMediaDriver archivingMediaDriver;

    private Aeron aeron;

    private AeronArchive aeronArchive;

    @Test(timeout = 20000)
    public void shouldMergeFromReplayToLive() {
        final int initialMessageCount = (ReplayMergeTest.MIN_MESSAGES_PER_TERM) * 3;
        final int subsequentMessageCount = (ReplayMergeTest.MIN_MESSAGES_PER_TERM) * 3;
        final int totalMessageCount = initialMessageCount + subsequentMessageCount;
        try (Publication publication = aeron.addPublication(publicationChannel.build(), ReplayMergeTest.STREAM_ID)) {
            final int sessionId = publication.sessionId();
            final String subscriptionChannel = this.subscriptionChannel.sessionId(sessionId).build();
            final String recordingChannel = this.recordingChannel.sessionId(sessionId).build();
            aeronArchive.startRecording(recordingChannel, ReplayMergeTest.STREAM_ID, REMOTE);
            try (Subscription subscription = aeron.addSubscription(subscriptionChannel, ReplayMergeTest.STREAM_ID)) {
                offerMessages(publication, 0, initialMessageCount, ReplayMergeTest.MESSAGE_PREFIX);
                final CountersReader counters = aeron.countersReader();
                final int counterId = ReplayMergeTest.awaitCounterId(counters, publication.sessionId());
                final long recordingId = RecordingPos.getRecordingId(counters, counterId);
                ReplayMergeTest.awaitPosition(counters, counterId, publication.position());
                try (ReplayMerge replayMerge = new ReplayMerge(subscription, aeronArchive, replayChannel.build(), replayDestination.build(), liveDestination.build(), recordingId, 0, sessionId, maxReceiverWindow)) {
                    final FragmentHandler fragmentHandler = new FragmentAssembler(( buffer, offset, length, header) -> {
                        final String expected = (MESSAGE_PREFIX) + received.value;
                        final String actual = buffer.getStringWithoutLengthAscii(offset, length);
                        assertEquals(expected, actual);
                        received.value++;
                    });
                    for (int i = initialMessageCount; i < totalMessageCount; i++) {
                        offer(publication, i, ReplayMergeTest.MESSAGE_PREFIX);
                        if (0 == (replayMerge.poll(fragmentHandler, ReplayMergeTest.FRAGMENT_LIMIT))) {
                            ReplayMergeTest.checkInterruptedStatus();
                            Thread.yield();
                        }
                    }
                    while (((received.get()) < totalMessageCount) || (!(replayMerge.isMerged()))) {
                        if (0 == (replayMerge.poll(fragmentHandler, ReplayMergeTest.FRAGMENT_LIMIT))) {
                            ReplayMergeTest.checkInterruptedStatus();
                            Thread.yield();
                        }
                    } 
                    Assert.assertThat(received.get(), CoreMatchers.is(totalMessageCount));
                    Assert.assertTrue(replayMerge.isMerged());
                    Assert.assertEquals(MERGED, replayMerge.state());
                }
            } finally {
                aeronArchive.stopRecording(recordingChannel, ReplayMergeTest.STREAM_ID);
            }
        }
    }
}

