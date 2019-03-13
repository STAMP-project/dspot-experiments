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


import AeronArchive.AsyncConnect;
import AeronArchive.NULL_LENGTH;
import CommonContext.IPC_CHANNEL;
import io.aeron.Aeron;
import io.aeron.SystemTest;
import io.aeron.archive.client.AeronArchive;
import io.aeron.archive.status.RecordingPos;
import org.agrona.concurrent.status.CountersReader;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class BasicArchiveTest {
    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final int FRAGMENT_LIMIT = 10;

    private static final int TERM_BUFFER_LENGTH = 64 * 1024;

    private static final int RECORDING_STREAM_ID = 33;

    private static final String RECORDING_CHANNEL = new ChannelUriStringBuilder().media("udp").endpoint("localhost:3333").termLength(BasicArchiveTest.TERM_BUFFER_LENGTH).build();

    private static final int REPLAY_STREAM_ID = 66;

    private static final String REPLAY_CHANNEL = new ChannelUriStringBuilder().media("udp").endpoint("localhost:6666").build();

    private ArchivingMediaDriver archivingMediaDriver;

    private Aeron aeron;

    private AeronArchive aeronArchive;

    @Test(timeout = 10000)
    public void shouldPerformAsyncConnect() {
        final long lastControlSessionId = aeronArchive.controlSessionId();
        aeronArchive.close();
        aeronArchive = null;
        final AeronArchive.AsyncConnect asyncConnect = AeronArchive.asyncConnect(new AeronArchive.Context().aeron(aeron));
        AeronArchive archive;
        do {
            archive = asyncConnect.poll();
        } while (null == archive );
        Assert.assertThat(archive.controlSessionId(), Is.is((lastControlSessionId + 1)));
        archive.close();
    }

    @Test(timeout = 10000)
    public void shouldRecordThenReplayThenTruncate() {
        final String messagePrefix = "Message-Prefix-";
        final int messageCount = 10;
        final long stopPosition;
        final long subscriptionId = aeronArchive.startRecording(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID, LOCAL);
        final long recordingIdFromCounter;
        final int sessionId;
        try (Subscription subscription = aeron.addSubscription(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID);Publication publication = aeron.addPublication(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID)) {
            sessionId = publication.sessionId();
            final CountersReader counters = aeron.countersReader();
            final int counterId = getRecordingCounterId(sessionId, counters);
            recordingIdFromCounter = RecordingPos.getRecordingId(counters, counterId);
            Assert.assertThat(RecordingPos.getSourceIdentity(counters, counterId), Is.is(IPC_CHANNEL));
            BasicArchiveTest.offer(publication, messageCount, messagePrefix);
            BasicArchiveTest.consume(subscription, messageCount, messagePrefix);
            stopPosition = publication.position();
            while ((counters.getCounterValue(counterId)) < stopPosition) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            Assert.assertThat(aeronArchive.getRecordingPosition(recordingIdFromCounter), Is.is(stopPosition));
            Assert.assertThat(aeronArchive.getStopPosition(recordingIdFromCounter), Is.is(((long) (Aeron.NULL_VALUE))));
        }
        aeronArchive.stopRecording(subscriptionId);
        final long recordingId = aeronArchive.findLastMatchingRecording(0, "endpoint=localhost:3333", BasicArchiveTest.RECORDING_STREAM_ID, sessionId);
        Assert.assertEquals(recordingIdFromCounter, recordingId);
        Assert.assertThat(aeronArchive.getStopPosition(recordingIdFromCounter), Is.is(stopPosition));
        final long position = 0L;
        final long length = stopPosition - position;
        try (Subscription subscription = aeronArchive.replay(recordingId, position, length, BasicArchiveTest.REPLAY_CHANNEL, BasicArchiveTest.REPLAY_STREAM_ID)) {
            BasicArchiveTest.consume(subscription, messageCount, messagePrefix);
            Assert.assertEquals(stopPosition, subscription.imageAtIndex(0).position());
        }
        aeronArchive.truncateRecording(recordingId, position);
        final int count = aeronArchive.listRecording(recordingId, ( controlSessionId, correlationId, recordingId1, startTimestamp, stopTimestamp, startPosition, newStopPosition, initialTermId, segmentFileLength, termBufferLength, mtuLength, sessionId1, streamId, strippedChannel, originalChannel, sourceIdentity) -> assertEquals(startPosition, newStopPosition));
        Assert.assertEquals(1, count);
    }

    @Test(timeout = 10000)
    public void shouldRecordReplayAndCancelReplayEarly() {
        final String messagePrefix = "Message-Prefix-";
        final long stopPosition;
        final int messageCount = 10;
        final long recordingId;
        try (Subscription subscription = aeron.addSubscription(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID);Publication publication = aeronArchive.addRecordedPublication(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID)) {
            final CountersReader counters = aeron.countersReader();
            final int counterId = getRecordingCounterId(publication.sessionId(), counters);
            recordingId = RecordingPos.getRecordingId(counters, counterId);
            BasicArchiveTest.offer(publication, messageCount, messagePrefix);
            BasicArchiveTest.consume(subscription, messageCount, messagePrefix);
            stopPosition = publication.position();
            while ((counters.getCounterValue(counterId)) < stopPosition) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            Assert.assertThat(aeronArchive.getRecordingPosition(recordingId), Is.is(stopPosition));
            aeronArchive.stopRecording(publication);
            while ((NULL_POSITION) != (aeronArchive.getRecordingPosition(recordingId))) {
                SystemTest.checkInterruptedStatus();
            } 
        }
        final long position = 0L;
        final long length = stopPosition - position;
        final long replaySessionId = aeronArchive.startReplay(recordingId, position, length, BasicArchiveTest.REPLAY_CHANNEL, BasicArchiveTest.REPLAY_STREAM_ID);
        aeronArchive.stopReplay(replaySessionId);
    }

    @Test(timeout = 10000)
    public void shouldReplayRecordingFromLateJoinPosition() {
        final String messagePrefix = "Message-Prefix-";
        final int messageCount = 10;
        final long subscriptionId = aeronArchive.startRecording(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID, LOCAL);
        try (Subscription subscription = aeron.addSubscription(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID);Publication publication = aeron.addPublication(BasicArchiveTest.RECORDING_CHANNEL, BasicArchiveTest.RECORDING_STREAM_ID)) {
            final CountersReader counters = aeron.countersReader();
            final int counterId = getRecordingCounterId(publication.sessionId(), counters);
            final long recordingId = RecordingPos.getRecordingId(counters, counterId);
            BasicArchiveTest.offer(publication, messageCount, messagePrefix);
            BasicArchiveTest.consume(subscription, messageCount, messagePrefix);
            final long currentPosition = publication.position();
            while ((counters.getCounterValue(counterId)) < currentPosition) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            try (Subscription replaySubscription = aeronArchive.replay(recordingId, currentPosition, NULL_LENGTH, BasicArchiveTest.REPLAY_CHANNEL, BasicArchiveTest.REPLAY_STREAM_ID)) {
                BasicArchiveTest.offer(publication, messageCount, messagePrefix);
                BasicArchiveTest.consume(subscription, messageCount, messagePrefix);
                BasicArchiveTest.consume(replaySubscription, messageCount, messagePrefix);
                final long endPosition = publication.position();
                Assert.assertEquals(endPosition, replaySubscription.imageAtIndex(0).position());
            }
        }
        aeronArchive.stopRecording(subscriptionId);
    }
}

