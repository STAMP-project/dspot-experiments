/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.io;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import org.apache.flink.runtime.io.network.partition.consumer.BufferOrEvent;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link BufferBlocker}.
 */
public abstract class BufferBlockerTestBase {
    protected static final int PAGE_SIZE = 4096;

    @Test
    public void testRollOverEmptySequences() throws IOException {
        BufferBlocker bufferBlocker = createBufferBlocker();
        Assert.assertNull(bufferBlocker.rollOverReusingResources());
        Assert.assertNull(bufferBlocker.rollOverReusingResources());
        Assert.assertNull(bufferBlocker.rollOverReusingResources());
    }

    @Test
    public void testSpillAndRollOverSimple() throws IOException {
        final Random rnd = new Random();
        final Random bufferRnd = new Random();
        final int maxNumEventsAndBuffers = 3000;
        final int maxNumChannels = 1656;
        BufferBlocker bufferBlocker = createBufferBlocker();
        // do multiple spilling / rolling over rounds
        for (int round = 0; round < 5; round++) {
            final long bufferSeed = rnd.nextLong();
            bufferRnd.setSeed(bufferSeed);
            final int numEventsAndBuffers = (rnd.nextInt(maxNumEventsAndBuffers)) + 1;
            final int numberOfChannels = (rnd.nextInt(maxNumChannels)) + 1;
            final ArrayList<BufferOrEvent> events = new ArrayList<BufferOrEvent>(128);
            // generate sequence
            for (int i = 0; i < numEventsAndBuffers; i++) {
                boolean isEvent = (rnd.nextDouble()) < 0.05;
                BufferOrEvent evt;
                if (isEvent) {
                    evt = BufferBlockerTestBase.generateRandomEvent(rnd, numberOfChannels);
                    events.add(evt);
                } else {
                    evt = BufferBlockerTestBase.generateRandomBuffer(((bufferRnd.nextInt(BufferBlockerTestBase.PAGE_SIZE)) + 1), bufferRnd.nextInt(numberOfChannels));
                }
                bufferBlocker.add(evt);
            }
            // reset and create reader
            bufferRnd.setSeed(bufferSeed);
            BufferOrEventSequence seq = bufferBlocker.rollOverReusingResources();
            seq.open();
            // read and validate the sequence
            int numEvent = 0;
            for (int i = 0; i < numEventsAndBuffers; i++) {
                BufferOrEvent next = seq.getNext();
                Assert.assertNotNull(next);
                if (next.isEvent()) {
                    BufferOrEvent expected = events.get((numEvent++));
                    Assert.assertEquals(expected.getEvent(), next.getEvent());
                    Assert.assertEquals(expected.getChannelIndex(), next.getChannelIndex());
                } else {
                    BufferBlockerTestBase.validateBuffer(next, ((bufferRnd.nextInt(BufferBlockerTestBase.PAGE_SIZE)) + 1), bufferRnd.nextInt(numberOfChannels));
                }
            }
            // no further data
            Assert.assertNull(seq.getNext());
            // all events need to be consumed
            Assert.assertEquals(events.size(), numEvent);
            seq.cleanup();
        }
    }

    @Test
    public void testSpillWhileReading() throws IOException {
        final int sequences = 10;
        final Random rnd = new Random();
        final int maxNumEventsAndBuffers = 30000;
        final int maxNumChannels = 1656;
        int sequencesConsumed = 0;
        ArrayDeque<BufferBlockerTestBase.SequenceToConsume> pendingSequences = new ArrayDeque<BufferBlockerTestBase.SequenceToConsume>();
        BufferBlockerTestBase.SequenceToConsume currentSequence = null;
        int currentNumEvents = 0;
        int currentNumRecordAndEvents = 0;
        BufferBlocker bufferBlocker = createBufferBlocker();
        // do multiple spilling / rolling over rounds
        for (int round = 0; round < (2 * sequences); round++) {
            if ((round % 2) == 1) {
                // make this an empty sequence
                Assert.assertNull(bufferBlocker.rollOverReusingResources());
            } else {
                // proper spilled sequence
                final long bufferSeed = rnd.nextLong();
                final Random bufferRnd = new Random(bufferSeed);
                final int numEventsAndBuffers = (rnd.nextInt(maxNumEventsAndBuffers)) + 1;
                final int numberOfChannels = (rnd.nextInt(maxNumChannels)) + 1;
                final ArrayList<BufferOrEvent> events = new ArrayList<BufferOrEvent>(128);
                int generated = 0;
                while (generated < numEventsAndBuffers) {
                    if ((currentSequence == null) || ((rnd.nextDouble()) < 0.5)) {
                        // add a new record
                        boolean isEvent = (rnd.nextDouble()) < 0.05;
                        BufferOrEvent evt;
                        if (isEvent) {
                            evt = BufferBlockerTestBase.generateRandomEvent(rnd, numberOfChannels);
                            events.add(evt);
                        } else {
                            evt = BufferBlockerTestBase.generateRandomBuffer(((bufferRnd.nextInt(BufferBlockerTestBase.PAGE_SIZE)) + 1), bufferRnd.nextInt(numberOfChannels));
                        }
                        bufferBlocker.add(evt);
                        generated++;
                    } else {
                        // consume a record
                        BufferOrEvent next = currentSequence.sequence.getNext();
                        Assert.assertNotNull(next);
                        if (next.isEvent()) {
                            BufferOrEvent expected = currentSequence.events.get((currentNumEvents++));
                            Assert.assertEquals(expected.getEvent(), next.getEvent());
                            Assert.assertEquals(expected.getChannelIndex(), next.getChannelIndex());
                        } else {
                            Random validationRnd = currentSequence.bufferRnd;
                            BufferBlockerTestBase.validateBuffer(next, ((validationRnd.nextInt(BufferBlockerTestBase.PAGE_SIZE)) + 1), validationRnd.nextInt(currentSequence.numberOfChannels));
                        }
                        currentNumRecordAndEvents++;
                        if (currentNumRecordAndEvents == (currentSequence.numBuffersAndEvents)) {
                            // done with the sequence
                            currentSequence.sequence.cleanup();
                            sequencesConsumed++;
                            // validate we had all events
                            Assert.assertEquals(currentSequence.events.size(), currentNumEvents);
                            // reset
                            currentSequence = pendingSequences.pollFirst();
                            if (currentSequence != null) {
                                currentSequence.sequence.open();
                            }
                            currentNumRecordAndEvents = 0;
                            currentNumEvents = 0;
                        }
                    }
                } 
                // done generating a sequence. queue it for consumption
                bufferRnd.setSeed(bufferSeed);
                BufferOrEventSequence seq = bufferBlocker.rollOverReusingResources();
                BufferBlockerTestBase.SequenceToConsume stc = new BufferBlockerTestBase.SequenceToConsume(bufferRnd, events, seq, numEventsAndBuffers, numberOfChannels);
                if (currentSequence == null) {
                    currentSequence = stc;
                    stc.sequence.open();
                } else {
                    pendingSequences.addLast(stc);
                }
            }
        }
        // consume all the remainder
        while (currentSequence != null) {
            // consume a record
            BufferOrEvent next = currentSequence.sequence.getNext();
            Assert.assertNotNull(next);
            if (next.isEvent()) {
                BufferOrEvent expected = currentSequence.events.get((currentNumEvents++));
                Assert.assertEquals(expected.getEvent(), next.getEvent());
                Assert.assertEquals(expected.getChannelIndex(), next.getChannelIndex());
            } else {
                Random validationRnd = currentSequence.bufferRnd;
                BufferBlockerTestBase.validateBuffer(next, ((validationRnd.nextInt(BufferBlockerTestBase.PAGE_SIZE)) + 1), validationRnd.nextInt(currentSequence.numberOfChannels));
            }
            currentNumRecordAndEvents++;
            if (currentNumRecordAndEvents == (currentSequence.numBuffersAndEvents)) {
                // done with the sequence
                currentSequence.sequence.cleanup();
                sequencesConsumed++;
                // validate we had all events
                Assert.assertEquals(currentSequence.events.size(), currentNumEvents);
                // reset
                currentSequence = pendingSequences.pollFirst();
                if (currentSequence != null) {
                    currentSequence.sequence.open();
                }
                currentNumRecordAndEvents = 0;
                currentNumEvents = 0;
            }
        } 
        Assert.assertEquals(sequences, sequencesConsumed);
    }

    /**
     * Wrappers the buffered sequence and related elements for consuming and validation.
     */
    private static class SequenceToConsume {
        final BufferOrEventSequence sequence;

        final ArrayList<BufferOrEvent> events;

        final Random bufferRnd;

        final int numBuffersAndEvents;

        final int numberOfChannels;

        private SequenceToConsume(Random bufferRnd, ArrayList<BufferOrEvent> events, BufferOrEventSequence sequence, int numBuffersAndEvents, int numberOfChannels) {
            this.bufferRnd = bufferRnd;
            this.events = events;
            this.sequence = sequence;
            this.numBuffersAndEvents = numBuffersAndEvents;
            this.numberOfChannels = numberOfChannels;
        }
    }
}

