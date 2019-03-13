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
import io.aeron.SystemTest;
import io.aeron.archive.client.AeronArchive;
import io.aeron.archive.status.RecordingPos;
import io.aeron.driver.Configuration;
import java.io.File;
import org.agrona.concurrent.status.CountersReader;
import org.junit.Assert;
import org.junit.Test;


public class ExtendRecordingTest {
    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final int FRAGMENT_LIMIT = 10;

    private static final int TERM_BUFFER_LENGTH = 64 * 1024;

    private static final int MTU_LENGTH = Configuration.mtuLength();

    private static final int RECORDING_STREAM_ID = 33;

    private static final String RECORDING_CHANNEL = new ChannelUriStringBuilder().media("udp").endpoint("localhost:3333").termLength(ExtendRecordingTest.TERM_BUFFER_LENGTH).build();

    private static final int REPLAY_STREAM_ID = 66;

    private static final String REPLAY_CHANNEL = new ChannelUriStringBuilder().media("udp").endpoint("localhost:6666").build();

    private ArchivingMediaDriver archivingMediaDriver;

    private Aeron aeron;

    private File archiveDir;

    private AeronArchive aeronArchive;

    public static final String MESSAGE_PREFIX = "Message-Prefix-";

    @Test(timeout = 10000)
    public void shouldExtendRecordingAndReplay() {
        final int messageCount = 10;
        final long firstStopPosition;
        final long recordingId;
        final int initialTermId;
        try (Publication publication = aeron.addPublication(ExtendRecordingTest.RECORDING_CHANNEL, ExtendRecordingTest.RECORDING_STREAM_ID);Subscription subscription = aeron.addSubscription(ExtendRecordingTest.RECORDING_CHANNEL, ExtendRecordingTest.RECORDING_STREAM_ID)) {
            initialTermId = publication.initialTermId();
            aeronArchive.startRecording(ExtendRecordingTest.RECORDING_CHANNEL, ExtendRecordingTest.RECORDING_STREAM_ID, LOCAL);
            try {
                ExtendRecordingTest.offer(publication, 0, messageCount, ExtendRecordingTest.MESSAGE_PREFIX);
                final CountersReader counters = aeron.countersReader();
                final int counterId = RecordingPos.findCounterIdBySession(counters, publication.sessionId());
                recordingId = RecordingPos.getRecordingId(counters, counterId);
                ExtendRecordingTest.consume(subscription, 0, messageCount, ExtendRecordingTest.MESSAGE_PREFIX);
                firstStopPosition = publication.position();
                while ((counters.getCounterValue(counterId)) < firstStopPosition) {
                    SystemTest.checkInterruptedStatus();
                    Thread.yield();
                } 
            } finally {
                aeronArchive.stopRecording(ExtendRecordingTest.RECORDING_CHANNEL, ExtendRecordingTest.RECORDING_STREAM_ID);
            }
        }
        closeDownAndCleanMediaDriver();
        launchAeronAndArchive();
        final String publicationExtendChannel = new ChannelUriStringBuilder().media("udp").endpoint("localhost:3333").initialPosition(firstStopPosition, initialTermId, ExtendRecordingTest.TERM_BUFFER_LENGTH).mtu(ExtendRecordingTest.MTU_LENGTH).build();
        final String subscriptionExtendChannel = new ChannelUriStringBuilder().media("udp").endpoint("localhost:3333").build();
        final long secondStopPosition;
        try (Publication publication = aeron.addExclusivePublication(publicationExtendChannel, ExtendRecordingTest.RECORDING_STREAM_ID);Subscription subscription = aeron.addSubscription(subscriptionExtendChannel, ExtendRecordingTest.RECORDING_STREAM_ID)) {
            aeronArchive.extendRecording(recordingId, subscriptionExtendChannel, ExtendRecordingTest.RECORDING_STREAM_ID, LOCAL);
            try {
                ExtendRecordingTest.offer(publication, messageCount, messageCount, ExtendRecordingTest.MESSAGE_PREFIX);
                final CountersReader counters = aeron.countersReader();
                final int counterId = RecordingPos.findCounterIdBySession(counters, publication.sessionId());
                ExtendRecordingTest.consume(subscription, messageCount, messageCount, ExtendRecordingTest.MESSAGE_PREFIX);
                secondStopPosition = publication.position();
                while ((counters.getCounterValue(counterId)) < secondStopPosition) {
                    SystemTest.checkInterruptedStatus();
                    Thread.yield();
                } 
            } finally {
                aeronArchive.stopRecording(subscriptionExtendChannel, ExtendRecordingTest.RECORDING_STREAM_ID);
            }
        }
        final long fromPosition = 0L;
        final long length = secondStopPosition - fromPosition;
        try (Subscription subscription = aeronArchive.replay(recordingId, fromPosition, length, ExtendRecordingTest.REPLAY_CHANNEL, ExtendRecordingTest.REPLAY_STREAM_ID)) {
            ExtendRecordingTest.consume(subscription, 0, (messageCount * 2), ExtendRecordingTest.MESSAGE_PREFIX);
            Assert.assertEquals(secondStopPosition, subscription.imageAtIndex(0).position());
        }
    }
}

