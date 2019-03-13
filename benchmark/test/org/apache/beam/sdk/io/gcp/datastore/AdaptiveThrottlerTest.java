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
package org.apache.beam.sdk.io.gcp.datastore;


import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link AdaptiveThrottler}.
 */
@RunWith(JUnit4.class)
public class AdaptiveThrottlerTest {
    static final long START_TIME_MS = 0;

    static final long SAMPLE_PERIOD_MS = 60000;

    static final long SAMPLE_BUCKET_MS = 1000;

    static final double OVERLOAD_RATIO = 2;

    @Test
    public void testNoInitialThrottling() throws Exception {
        AdaptiveThrottler throttler = getThrottler();
        Assert.assertThat(throttler.throttlingProbability(AdaptiveThrottlerTest.START_TIME_MS), Matchers.equalTo(0.0));
        Assert.assertThat("first request is not throttled", throttler.throttleRequest(AdaptiveThrottlerTest.START_TIME_MS), Matchers.equalTo(false));
    }

    @Test
    public void testNoThrottlingIfNoErrors() throws Exception {
        AdaptiveThrottler throttler = getThrottler();
        long t = AdaptiveThrottlerTest.START_TIME_MS;
        for (; t < ((AdaptiveThrottlerTest.START_TIME_MS) + 20); t++) {
            Assert.assertFalse(throttler.throttleRequest(t));
            throttler.successfulRequest(t);
        }
        Assert.assertThat(throttler.throttlingProbability(t), Matchers.equalTo(0.0));
    }

    @Test
    public void testNoThrottlingAfterErrorsExpire() throws Exception {
        AdaptiveThrottler throttler = getThrottler();
        long t = AdaptiveThrottlerTest.START_TIME_MS;
        for (; t < ((AdaptiveThrottlerTest.START_TIME_MS) + (AdaptiveThrottlerTest.SAMPLE_PERIOD_MS)); t++) {
            throttler.throttleRequest(t);
            // and no successfulRequest.
        }
        Assert.assertThat("check that we set up a non-zero probability of throttling", throttler.throttlingProbability(t), Matchers.greaterThan(0.0));
        for (; t < ((AdaptiveThrottlerTest.START_TIME_MS) + (2 * (AdaptiveThrottlerTest.SAMPLE_PERIOD_MS))); t++) {
            throttler.throttleRequest(t);
            throttler.successfulRequest(t);
        }
        Assert.assertThat(throttler.throttlingProbability(t), Matchers.equalTo(0.0));
    }

    @Test
    public void testThrottlingAfterErrors() throws Exception {
        Random mockRandom = Mockito.mock(Random.class);
        Mockito.when(mockRandom.nextDouble()).thenReturn(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
        AdaptiveThrottler throttler = new AdaptiveThrottler(AdaptiveThrottlerTest.SAMPLE_PERIOD_MS, AdaptiveThrottlerTest.SAMPLE_BUCKET_MS, AdaptiveThrottlerTest.OVERLOAD_RATIO, mockRandom);
        for (int i = 0; i < 20; i++) {
            boolean throttled = throttler.throttleRequest(((AdaptiveThrottlerTest.START_TIME_MS) + i));
            // 1/3rd of requests succeeding.
            if ((i % 3) == 1) {
                throttler.successfulRequest(((AdaptiveThrottlerTest.START_TIME_MS) + i));
            }
            // Once we have some history in place, check what throttling happens.
            if (i >= 10) {
                // Expect 1/3rd of requests to be throttled. (So 1/3rd throttled, 1/3rd succeeding, 1/3rd
                // tried and failing).
                Assert.assertThat(String.format("for i=%d", i), throttler.throttlingProbability(((AdaptiveThrottlerTest.START_TIME_MS) + i)), /* error= */
                Matchers.closeTo(0.33, 0.1));
                // Requests 10..13 should be throttled, 14..19 not throttled given the mocked random numbers
                // that we fed to throttler.
                Assert.assertThat(String.format("for i=%d", i), throttled, Matchers.equalTo((i < 14)));
            }
        }
    }
}

