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
package org.apache.ignite.stream.twitter;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.twitter.hbc.core.HttpHosts;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test for {@link TwitterStreamer}. Tests Public Status streaming API https://dev.twitter.com/streaming/public.
 */
public class IgniteTwitterStreamerTest extends GridCommonAbstractTest {
    /**
     * Cache entries count.
     */
    private static final int CACHE_ENTRY_COUNT = 100;

    /**
     * Mocked api in embedded server.
     */
    private static final String MOCK_TWEET_PATH = "/tweet/mock";

    /**
     * Sample tweet.
     */
    private static final String tweet = "{\"id\":647375831971590144,\"text\":\"sample tweet to test streamer\"}\n";

    /**
     * Constructor.
     */
    public IgniteTwitterStreamerTest() {
        super(true);
    }

    /**
     * See <a href="http://wiremock.org/docs/junit-rule/">The JUnit 4.x Rule</a>.
     */
    @ClassRule
    public static WireMockClassRule wireMockClsRule = new WireMockClassRule(WireMockConfiguration.DYNAMIC_PORT);

    /**
     * Embedded mock HTTP server's for Twitter API rule.
     */
    @Rule
    public WireMockClassRule wireMockRule = IgniteTwitterStreamerTest.wireMockClsRule;

    /**
     * Embedded mock HTTP server for Twitter API.
     */
    public final WireMockServer mockSrv = new WireMockServer();// Starts server on 8080 port.


    /**
     *
     *
     * @throws Exception
     * 		Test exception.
     */
    @Test
    public void testStatusesFilterEndpointOAuth1() throws Exception {
        init();
        try (IgniteDataStreamer<Long, String> dataStreamer = grid().dataStreamer(DEFAULT_CACHE_NAME)) {
            TwitterStreamerImpl streamer = newStreamerInstance(dataStreamer);
            Map<String, String> params = new HashMap<>();
            params.put("track", "apache, twitter");
            params.put("follow", "3004445758");// @ApacheIgnite id.

            setApiParams(params);
            setEndpointUrl(IgniteTwitterStreamerTest.MOCK_TWEET_PATH);
            streamer.setHosts(new HttpHosts("http://localhost:8080"));
            setThreadsCount(8);
            executeStreamer(streamer);
        } finally {
            cleanup();
        }
    }

    /**
     * Listener.
     */
    private static class CacheListener implements IgnitePredicate<CacheEvent> {
        /**
         *
         */
        private final CountDownLatch latch = new CountDownLatch(IgniteTwitterStreamerTest.CACHE_ENTRY_COUNT);

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

