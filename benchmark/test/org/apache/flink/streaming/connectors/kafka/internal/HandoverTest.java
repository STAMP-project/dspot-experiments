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
package org.apache.flink.streaming.connectors.kafka.internal;


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import org.apache.flink.streaming.connectors.kafka.internal.Handover.WakeupException;
import org.apache.flink.util.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;


/**
 * Tests for the {@link Handover} between Kafka Consumer Thread and the fetcher's main thread.
 */
public class HandoverTest {
    // ------------------------------------------------------------------------
    // test produce / consumer
    // ------------------------------------------------------------------------
    @Test
    public void testWithVariableProducer() throws Exception {
        runProducerConsumerTest(500, 2, 0);
    }

    @Test
    public void testWithVariableConsumer() throws Exception {
        runProducerConsumerTest(500, 0, 2);
    }

    @Test
    public void testWithVariableBoth() throws Exception {
        runProducerConsumerTest(500, 2, 2);
    }

    // ------------------------------------------------------------------------
    // test error propagation
    // ------------------------------------------------------------------------
    @Test
    public void testPublishErrorOnEmptyHandover() throws Exception {
        final Handover handover = new Handover();
        Exception error = new Exception();
        handover.reportError(error);
        try {
            handover.pollNext();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            Assert.assertEquals(error, e);
        }
    }

    @Test
    public void testPublishErrorOnFullHandover() throws Exception {
        final Handover handover = new Handover();
        handover.produce(HandoverTest.createTestRecords());
        IOException error = new IOException();
        handover.reportError(error);
        try {
            handover.pollNext();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            Assert.assertEquals(error, e);
        }
    }

    @Test
    public void testExceptionMarksClosedOnEmpty() throws Exception {
        final Handover handover = new Handover();
        IllegalStateException error = new IllegalStateException();
        handover.reportError(error);
        try {
            handover.produce(HandoverTest.createTestRecords());
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
    }

    @Test
    public void testExceptionMarksClosedOnFull() throws Exception {
        final Handover handover = new Handover();
        handover.produce(HandoverTest.createTestRecords());
        LinkageError error = new LinkageError();
        handover.reportError(error);
        try {
            handover.produce(HandoverTest.createTestRecords());
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
    }

    // ------------------------------------------------------------------------
    // test closing behavior
    // ------------------------------------------------------------------------
    @Test
    public void testCloseEmptyForConsumer() throws Exception {
        final Handover handover = new Handover();
        handover.close();
        try {
            handover.pollNext();
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
    }

    @Test
    public void testCloseFullForConsumer() throws Exception {
        final Handover handover = new Handover();
        handover.produce(HandoverTest.createTestRecords());
        handover.close();
        try {
            handover.pollNext();
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
    }

    @Test
    public void testCloseEmptyForProducer() throws Exception {
        final Handover handover = new Handover();
        handover.close();
        try {
            handover.produce(HandoverTest.createTestRecords());
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
    }

    @Test
    public void testCloseFullForProducer() throws Exception {
        final Handover handover = new Handover();
        handover.produce(HandoverTest.createTestRecords());
        handover.close();
        try {
            handover.produce(HandoverTest.createTestRecords());
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
    }

    // ------------------------------------------------------------------------
    // test wake up behavior
    // ------------------------------------------------------------------------
    @Test
    public void testWakeupDoesNotWakeWhenEmpty() throws Exception {
        Handover handover = new Handover();
        handover.wakeupProducer();
        // produce into a woken but empty handover
        try {
            handover.produce(HandoverTest.createTestRecords());
        } catch (Handover e) {
            Assert.fail();
        }
        // handover now has records, next time we wakeup and produce it needs
        // to throw an exception
        handover.wakeupProducer();
        try {
            handover.produce(HandoverTest.createTestRecords());
            Assert.fail("should throw an exception");
        } catch (Handover e) {
            // expected
        }
        // empty the handover
        Assert.assertNotNull(handover.pollNext());
        // producing into an empty handover should work
        try {
            handover.produce(HandoverTest.createTestRecords());
        } catch (Handover e) {
            Assert.fail();
        }
    }

    @Test
    public void testWakeupWakesOnlyOnce() throws Exception {
        // create a full handover
        final Handover handover = new Handover();
        handover.produce(HandoverTest.createTestRecords());
        handover.wakeupProducer();
        try {
            handover.produce(HandoverTest.createTestRecords());
            Assert.fail();
        } catch (WakeupException e) {
            // expected
        }
        HandoverTest.CheckedThread producer = new HandoverTest.CheckedThread() {
            @Override
            public void go() throws Exception {
                handover.produce(HandoverTest.createTestRecords());
            }
        };
        producer.start();
        // the producer must go blocking
        producer.waitUntilThreadHoldsLock(10000);
        // release the thread by consuming something
        Assert.assertNotNull(handover.pollNext());
        producer.sync();
    }

    // ------------------------------------------------------------------------
    private abstract static class CheckedThread extends Thread {
        private volatile Throwable error;

        public abstract void go() throws Exception;

        @Override
        public void run() {
            try {
                go();
            } catch (Throwable t) {
                error = t;
            }
        }

        public void sync() throws Exception {
            join();
            if ((error) != null) {
                ExceptionUtils.rethrowException(error, error.getMessage());
            }
        }

        public void waitUntilThreadHoldsLock(long timeoutMillis) throws InterruptedException, TimeoutException {
            final long deadline = (System.nanoTime()) + (timeoutMillis * 1000000);
            while ((!(isBlockedOrWaiting())) && ((System.nanoTime()) < deadline)) {
                Thread.sleep(1);
            } 
            if (!(isBlockedOrWaiting())) {
                throw new TimeoutException();
            }
        }

        private boolean isBlockedOrWaiting() {
            Thread.State state = getState();
            return ((state == (BLOCKED)) || (state == (WAITING))) || (state == (TIMED_WAITING));
        }
    }

    private static class ProducerThread extends HandoverTest.CheckedThread {
        private final Random rnd = new Random();

        private final Handover handover;

        private final ConsumerRecords<byte[], byte[]>[] data;

        private final int maxDelay;

        private ProducerThread(Handover handover, ConsumerRecords<byte[], byte[]>[] data, int maxDelay) {
            this.handover = handover;
            this.data = data;
            this.maxDelay = maxDelay;
        }

        @Override
        public void go() throws Exception {
            for (ConsumerRecords<byte[], byte[]> rec : data) {
                handover.produce(rec);
                if ((maxDelay) > 0) {
                    int delay = rnd.nextInt(maxDelay);
                    Thread.sleep(delay);
                }
            }
        }
    }

    private static class ConsumerThread extends HandoverTest.CheckedThread {
        private final Random rnd = new Random();

        private final Handover handover;

        private final ConsumerRecords<byte[], byte[]>[] data;

        private final int maxDelay;

        private ConsumerThread(Handover handover, ConsumerRecords<byte[], byte[]>[] data, int maxDelay) {
            this.handover = handover;
            this.data = data;
            this.maxDelay = maxDelay;
        }

        @Override
        public void go() throws Exception {
            for (ConsumerRecords<byte[], byte[]> rec : data) {
                ConsumerRecords<byte[], byte[]> next = handover.pollNext();
                Assert.assertEquals(rec, next);
                if ((maxDelay) > 0) {
                    int delay = rnd.nextInt(maxDelay);
                    Thread.sleep(delay);
                }
            }
        }
    }
}

