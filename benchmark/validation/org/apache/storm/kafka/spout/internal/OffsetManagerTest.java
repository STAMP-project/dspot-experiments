/**
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka.spout.internal;


import java.util.NoSuchElementException;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


public class OffsetManagerTest {
    private static final String COMMIT_METADATA = "{\"topologyId\":\"tp1\",\"taskId\":3,\"threadName\":\"Thread-20\"}";

    private final long initialFetchOffset = 0;

    private final TopicPartition testTp = new TopicPartition("testTopic", 0);

    private final OffsetManager manager = new OffsetManager(testTp, initialFetchOffset);

    @Test
    public void testSkipMissingOffsetsWhenFindingNextCommitOffsetWithGapInMiddleOfAcked() {
        /* If topic compaction is enabled in Kafka, we sometimes need to commit past a gap of deleted offsets
        Since the Kafka consumer should return offsets in order, we can assume that if a message is acked
        then any prior message will have been emitted at least once.
        If we see an acked message and some of the offsets preceding it were not emitted, they must have been compacted away and should be skipped.
         */
        manager.addToEmitMsgs(0);
        manager.addToEmitMsgs(1);
        manager.addToEmitMsgs(2);
        // 3, 4 compacted away
        manager.addToEmitMsgs(((initialFetchOffset) + 5));
        manager.addToEmitMsgs(((initialFetchOffset) + 6));
        manager.addToAckMsgs(getMessageId(initialFetchOffset));
        manager.addToAckMsgs(getMessageId(((initialFetchOffset) + 1)));
        manager.addToAckMsgs(getMessageId(((initialFetchOffset) + 2)));
        manager.addToAckMsgs(getMessageId(((initialFetchOffset) + 6)));
        MatcherAssert.assertThat("The offset manager should not skip past offset 5 which is still pending", manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA).offset(), CoreMatchers.is(((initialFetchOffset) + 3)));
        manager.addToAckMsgs(getMessageId(((initialFetchOffset) + 5)));
        MatcherAssert.assertThat("The offset manager should skip past the gap in acked messages, since the messages were not emitted", manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA), CoreMatchers.is(new OffsetAndMetadata(((initialFetchOffset) + 7), OffsetManagerTest.COMMIT_METADATA)));
    }

    @Test
    public void testSkipMissingOffsetsWhenFindingNextCommitOffsetWithGapBeforeAcked() {
        // 0-4 compacted away
        manager.addToEmitMsgs(((initialFetchOffset) + 5));
        manager.addToEmitMsgs(((initialFetchOffset) + 6));
        manager.addToAckMsgs(getMessageId(((initialFetchOffset) + 6)));
        MatcherAssert.assertThat("The offset manager should not skip past offset 5 which is still pending", manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA), CoreMatchers.is(CoreMatchers.nullValue()));
        manager.addToAckMsgs(getMessageId(((initialFetchOffset) + 5)));
        MatcherAssert.assertThat("The offset manager should skip past the gap in acked messages, since the messages were not emitted", manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA), CoreMatchers.is(new OffsetAndMetadata(((initialFetchOffset) + 7), OffsetManagerTest.COMMIT_METADATA)));
    }

    @Test
    public void testFindNextCommittedOffsetWithNoAcks() {
        OffsetAndMetadata nextCommitOffset = manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("There shouldn't be a next commit offset when nothing has been acked", nextCommitOffset, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testFindNextCommitOffsetWithOneAck() {
        /* The KafkaConsumer commitSync API docs: "The committed offset should be the next message your application will consume, i.e.
        lastProcessedMessageOffset + 1. "
         */
        emitAndAckMessage(getMessageId(initialFetchOffset));
        OffsetAndMetadata nextCommitOffset = manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("The next commit offset should be one past the processed message offset", nextCommitOffset.offset(), CoreMatchers.is(((initialFetchOffset) + 1)));
    }

    @Test
    public void testFindNextCommitOffsetWithMultipleOutOfOrderAcks() {
        emitAndAckMessage(getMessageId(((initialFetchOffset) + 1)));
        emitAndAckMessage(getMessageId(initialFetchOffset));
        OffsetAndMetadata nextCommitOffset = manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("The next commit offset should be one past the processed message offset", nextCommitOffset.offset(), CoreMatchers.is(((initialFetchOffset) + 2)));
    }

    @Test
    public void testFindNextCommitOffsetWithAckedOffsetGap() {
        emitAndAckMessage(getMessageId(((initialFetchOffset) + 2)));
        manager.addToEmitMsgs(((initialFetchOffset) + 1));
        emitAndAckMessage(getMessageId(initialFetchOffset));
        OffsetAndMetadata nextCommitOffset = manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("The next commit offset should cover the sequential acked offsets", nextCommitOffset.offset(), CoreMatchers.is(((initialFetchOffset) + 1)));
    }

    @Test
    public void testFindNextOffsetWithAckedButNotEmittedOffsetGap() {
        /**
         * If topic compaction is enabled in Kafka some offsets may be deleted.
         * We distinguish this case from regular gaps in the acked offset sequence caused by out of order acking
         * by checking that offsets in the gap have been emitted at some point previously.
         * If they haven't then they can't exist in Kafka, since the spout emits tuples in order.
         */
        emitAndAckMessage(getMessageId(((initialFetchOffset) + 2)));
        emitAndAckMessage(getMessageId(initialFetchOffset));
        OffsetAndMetadata nextCommitOffset = manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("The next commit offset should cover all the acked offsets, since the offset in the gap hasn't been emitted and doesn't exist", nextCommitOffset.offset(), CoreMatchers.is(((initialFetchOffset) + 3)));
    }

    @Test
    public void testFindNextCommitOffsetWithUnackedOffsetGap() {
        manager.addToEmitMsgs(((initialFetchOffset) + 1));
        emitAndAckMessage(getMessageId(initialFetchOffset));
        OffsetAndMetadata nextCommitOffset = manager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("The next commit offset should cover the contiguously acked offsets", nextCommitOffset.offset(), CoreMatchers.is(((initialFetchOffset) + 1)));
    }

    @Test
    public void testFindNextCommitOffsetWhenTooLowOffsetIsAcked() {
        OffsetManager startAtHighOffsetManager = new OffsetManager(testTp, 10);
        emitAndAckMessage(getMessageId(0));
        OffsetAndMetadata nextCommitOffset = startAtHighOffsetManager.findNextCommitOffset(OffsetManagerTest.COMMIT_METADATA);
        MatcherAssert.assertThat("Acking an offset earlier than the committed offset should have no effect", nextCommitOffset, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testCommit() {
        emitAndAckMessage(getMessageId(initialFetchOffset));
        emitAndAckMessage(getMessageId(((initialFetchOffset) + 1)));
        emitAndAckMessage(getMessageId(((initialFetchOffset) + 2)));
        long committedMessages = manager.commit(new OffsetAndMetadata(((initialFetchOffset) + 2)));
        MatcherAssert.assertThat("Should have committed all messages to the left of the earliest uncommitted offset", committedMessages, CoreMatchers.is(2L));
        MatcherAssert.assertThat("The committed messages should not be in the acked list anymore", manager.contains(getMessageId(initialFetchOffset)), CoreMatchers.is(false));
        MatcherAssert.assertThat("The committed messages should not be in the emitted list anymore", manager.containsEmitted(initialFetchOffset), CoreMatchers.is(false));
        MatcherAssert.assertThat("The committed messages should not be in the acked list anymore", manager.contains(getMessageId(((initialFetchOffset) + 1))), CoreMatchers.is(false));
        MatcherAssert.assertThat("The committed messages should not be in the emitted list anymore", manager.containsEmitted(((initialFetchOffset) + 1)), CoreMatchers.is(false));
        MatcherAssert.assertThat("The uncommitted message should still be in the acked list", manager.contains(getMessageId(((initialFetchOffset) + 2))), CoreMatchers.is(true));
        MatcherAssert.assertThat("The uncommitted message should still be in the emitted list", manager.containsEmitted(((initialFetchOffset) + 2)), CoreMatchers.is(true));
    }

    @Test
    public void testGetNthUncommittedOffsetAfterCommittedOffset() {
        manager.addToEmitMsgs(((initialFetchOffset) + 1));
        manager.addToEmitMsgs(((initialFetchOffset) + 2));
        manager.addToEmitMsgs(((initialFetchOffset) + 5));
        manager.addToEmitMsgs(((initialFetchOffset) + 30));
        MatcherAssert.assertThat("The third uncommitted offset should be 5", manager.getNthUncommittedOffsetAfterCommittedOffset(3), CoreMatchers.is(((initialFetchOffset) + 5L)));
        MatcherAssert.assertThat("The fourth uncommitted offset should be 30", manager.getNthUncommittedOffsetAfterCommittedOffset(4), CoreMatchers.is(((initialFetchOffset) + 30L)));
        Assertions.assertThrows(NoSuchElementException.class, () -> manager.getNthUncommittedOffsetAfterCommittedOffset(5));
    }

    @Test
    public void testCommittedFlagSetOnCommit() throws Exception {
        Assertions.assertFalse(manager.hasCommitted());
        manager.commit(Mockito.mock(OffsetAndMetadata.class));
        Assertions.assertTrue(manager.hasCommitted());
    }
}

