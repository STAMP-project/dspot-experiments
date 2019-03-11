/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ignite.stream.rocketmq;


import CachePeekMode.PRIMARY;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.stream.StreamMultipleTupleExtractor;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;


/**
 * Test for {@link RocketMQStreamer}.
 */
public class RocketMQStreamerTest extends GridCommonAbstractTest {
    /**
     * Test topic.
     */
    private static final String TOPIC_NAME = "testTopic";

    /**
     * Test consumer group.
     */
    private static final String CONSUMER_GRP = "testConsumerGrp";

    /**
     * Test server.
     */
    private static TestRocketMQServer testRocketMQServer;

    /**
     * Number of events to handle.
     */
    private static final int EVT_NUM = 1000;

    /**
     * Constructor.
     */
    public RocketMQStreamerTest() {
        super(true);
    }

    /**
     * Tests data is properly injected into the grid.
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testStreamer() throws Exception {
        RocketMQStreamer<String, byte[]> streamer = null;
        Ignite ignite = grid();
        try (IgniteDataStreamer<String, byte[]> dataStreamer = ignite.dataStreamer(DEFAULT_CACHE_NAME)) {
            dataStreamer.allowOverwrite(true);
            dataStreamer.autoFlushFrequency(10);
            streamer = new RocketMQStreamer();
            // configure.
            streamer.setIgnite(ignite);
            streamer.setStreamer(dataStreamer);
            streamer.setNameSrvAddr((((TestRocketMQServer.TEST_IP) + ":") + (TestRocketMQServer.NAME_SERVER_PORT)));
            streamer.setConsumerGrp(RocketMQStreamerTest.CONSUMER_GRP);
            streamer.setTopic(RocketMQStreamerTest.TOPIC_NAME);
            streamer.setMultipleTupleExtractor(new RocketMQStreamerTest.TestTupleExtractor());
            streamer.start();
            IgniteCache<String, String> cache = ignite.cache(DEFAULT_CACHE_NAME);
            assertEquals(0, cache.size(PRIMARY));
            final CountDownLatch latch = new CountDownLatch(RocketMQStreamerTest.EVT_NUM);
            IgniteBiPredicate<UUID, CacheEvent> putLsnr = new IgniteBiPredicate<UUID, CacheEvent>() {
                @Override
                public boolean apply(UUID uuid, CacheEvent evt) {
                    assert evt != null;
                    latch.countDown();
                    return true;
                }
            };
            ignite.events(ignite.cluster().forCacheNodes(DEFAULT_CACHE_NAME)).remoteListen(putLsnr, null, EVT_CACHE_OBJECT_PUT);
            produceData();
            assertTrue(latch.await(30, TimeUnit.SECONDS));
            assertEquals(RocketMQStreamerTest.EVT_NUM, cache.size(PRIMARY));
        } finally {
            if (streamer != null)
                streamer.stop();

        }
    }

    /**
     * Test tuple extractor.
     */
    public static class TestTupleExtractor implements StreamMultipleTupleExtractor<List<MessageExt>, String, byte[]> {
        /**
         * {@inheritDoc }
         */
        @Override
        public Map<String, byte[]> extract(List<MessageExt> msgs) {
            final Map<String, byte[]> map = new HashMap<>();
            for (MessageExt msg : msgs)
                map.put(msg.getMsgId(), msg.getBody());

            return map;
        }
    }
}

