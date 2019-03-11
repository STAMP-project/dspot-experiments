/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.stream.zeromq;


import ZMQ.PUB;
import ZMQ.PUSH;
import ZeroMqTypeSocket.PAIR;
import ZeroMqTypeSocket.PULL;
import ZeroMqTypeSocket.SUB;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests {@link IgniteZeroMqStreamer}.
 */
public class IgniteZeroMqStreamerTest extends GridCommonAbstractTest {
    /**
     * Cache entries count.
     */
    private static final int CACHE_ENTRY_COUNT = 1000;

    /**
     * Local address for 0mq.
     */
    private final String ADDR = "tcp://localhost:5671";

    /**
     * Topic name for PUB-SUB.
     */
    private final byte[] TOPIC = "0mq".getBytes();

    /**
     * If pub-sub envelopes are used.
     */
    private static boolean multipart_pubsub;

    /**
     * Constructor.
     */
    public IgniteZeroMqStreamerTest() {
        super(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		Test exception.
     */
    @Test
    public void testZeroMqPairSocket() throws Exception {
        try (IgniteDataStreamer<Integer, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            try (IgniteZeroMqStreamer streamer = newStreamerInstance(dataStreamer, 1, PAIR, ADDR, null)) {
                executeStreamer(streamer, ZMQ.PAIR, null);
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		Test exception.
     */
    @Test
    public void testZeroMqSubSocketMultipart() throws Exception {
        try (IgniteDataStreamer<Integer, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            try (IgniteZeroMqStreamer streamer = newStreamerInstance(dataStreamer, 3, SUB, ADDR, TOPIC)) {
                IgniteZeroMqStreamerTest.multipart_pubsub = true;
                executeStreamer(streamer, PUB, TOPIC);
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		Test exception.
     */
    @Test
    public void testZeroMqSubSocket() throws Exception {
        try (IgniteDataStreamer<Integer, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            try (IgniteZeroMqStreamer streamer = newStreamerInstance(dataStreamer, 3, SUB, ADDR, TOPIC)) {
                executeStreamer(streamer, PUB, TOPIC);
            }
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		Test exception.
     */
    @Test
    public void testZeroMqPullSocket() throws Exception {
        try (IgniteDataStreamer<Integer, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            try (IgniteZeroMqStreamer streamer = newStreamerInstance(dataStreamer, 4, PULL, ADDR, null)) {
                executeStreamer(streamer, PUSH, null);
            }
        }
    }

    /**
     * Listener.
     */
    private class CacheListener implements IgnitePredicate<CacheEvent> {
        /**
         *
         */
        private final CountDownLatch latch = new CountDownLatch(IgniteZeroMqStreamerTest.CACHE_ENTRY_COUNT);

        /**
         *
         *
         * @return Latch.
         */
        public CountDownLatch getLatch() {
            return latch;
        }

        /**
         *
         *
         * @param evt
         * 		Cache Event.
         * @return {@code true}.
         */
        @Override
        public boolean apply(CacheEvent evt) {
            latch.countDown();
            return true;
        }
    }
}

