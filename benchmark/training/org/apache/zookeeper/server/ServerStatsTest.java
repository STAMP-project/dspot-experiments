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
package org.apache.zookeeper.server;


import ServerStats.Provider;
import org.apache.zookeeper.ZKTestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ServerStatsTest extends ZKTestCase {
    private Provider providerMock;

    @Test
    public void testPacketsMetrics() {
        // Given ...
        ServerStats serverStats = new ServerStats(providerMock);
        int incrementCount = 20;
        // When increment ...
        for (int i = 0; i < incrementCount; i++) {
            serverStats.incrementPacketsSent();
            serverStats.incrementPacketsReceived();
            serverStats.incrementPacketsReceived();
        }
        // Then ...
        Assert.assertEquals(incrementCount, serverStats.getPacketsSent());
        Assert.assertEquals((incrementCount * 2), serverStats.getPacketsReceived());
        // When reset ...
        serverStats.resetRequestCounters();
        // Then ...
        assertAllPacketsZero(serverStats);
    }

    @Test
    public void testLatencyMetrics() {
        // Given ...
        ServerStats serverStats = new ServerStats(providerMock);
        // When incremented...
        Request fakeRequest = new Request(0, 0, 0, null, null, 0);
        serverStats.updateLatency(fakeRequest, ((fakeRequest.createTime) + 1000));
        serverStats.updateLatency(fakeRequest, ((fakeRequest.createTime) + 2000));
        // Then ...
        MatcherAssert.assertThat("Max latency check", 2000L, Matchers.lessThanOrEqualTo(serverStats.getMaxLatency()));
        MatcherAssert.assertThat("Min latency check", 1000L, Matchers.lessThanOrEqualTo(serverStats.getMinLatency()));
        Assert.assertEquals(((double) (1500)), serverStats.getAvgLatency(), ((double) (200)));
        // When reset...
        serverStats.resetLatency();
        // Then ...
        assertAllLatencyZero(serverStats);
    }

    @Test
    public void testFsyncThresholdExceedMetrics() {
        // Given ...
        ServerStats serverStats = new ServerStats(providerMock);
        int incrementCount = 30;
        // When increment ...
        for (int i = 0; i < incrementCount; i++) {
            serverStats.incrementFsyncThresholdExceedCount();
        }
        // Then ...
        Assert.assertEquals(incrementCount, serverStats.getFsyncThresholdExceedCount());
        // When reset ...
        serverStats.resetFsyncThresholdExceedCount();
        // Then ...
        assertFsyncThresholdExceedCountZero(serverStats);
    }

    @Test
    public void testReset() {
        // Given ...
        ServerStats serverStats = new ServerStats(providerMock);
        assertAllPacketsZero(serverStats);
        assertAllLatencyZero(serverStats);
        // When ...
        Request fakeRequest = new Request(0, 0, 0, null, null, 0);
        serverStats.incrementPacketsSent();
        serverStats.incrementPacketsReceived();
        serverStats.updateLatency(fakeRequest, ((fakeRequest.createTime) + 1000));
        serverStats.reset();
        // Then ...
        assertAllPacketsZero(serverStats);
        assertAllLatencyZero(serverStats);
    }
}

