/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client.statistics;


import LifecycleEvent.LifecycleState.CLIENT_CONNECTED;
import Statistics.ENABLED;
import Statistics.PERIOD_SECONDS;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.connection.nio.ClientConnection;
import com.hazelcast.client.impl.ClientEngineImpl;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.impl.statistics.Statistics;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientStatisticsTest extends ClientTestSupport {
    private static final int STATS_PERIOD_SECONDS = 1;

    private static final long STATS_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(ClientStatisticsTest.STATS_PERIOD_SECONDS);

    private static final String MAP_NAME = "StatTestMapFirst.First";

    private static final String CACHE_NAME = "StatTestICache.First";

    private static final String MAP_HITS_KEY = ("nc." + (ClientStatisticsTest.MAP_NAME)) + ".hits";

    private static final String CACHE_HITS_KEY = ("nc.hz/" + (ClientStatisticsTest.CACHE_NAME)) + ".hits";

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void testStatisticsCollectionNonDefaultPeriod() {
        HazelcastInstance hazelcastInstance = newHazelcastInstance();
        final HazelcastClientInstanceImpl client = createHazelcastClient();
        final ClientEngineImpl clientEngine = getClientEngineImpl(hazelcastInstance);
        long clientConnectionTime = System.currentTimeMillis();
        // wait enough time for statistics collection
        ClientStatisticsTest.waitForFirstStatisticsCollection(client, clientEngine);
        Map<String, String> stats = ClientStatisticsTest.getStats(client, clientEngine);
        String connStat = stats.get("clusterConnectionTimestamp");
        Assert.assertNotNull(String.format("clusterConnectionTimestamp should not be null (%s)", stats), connStat);
        Long connectionTimeStat = Long.valueOf(connStat);
        Assert.assertNotNull(String.format("connectionTimeStat should not be null (%s)", stats), connStat);
        ClientConnection ownerConnection = client.getConnectionManager().getOwnerConnection();
        String expectedClientAddress = String.format("%s:%d", ownerConnection.getLocalSocketAddress().getAddress().getHostAddress(), ownerConnection.getLocalSocketAddress().getPort());
        Assert.assertEquals(expectedClientAddress, stats.get("clientAddress"));
        Assert.assertEquals(BuildInfoProvider.getBuildInfo().getVersion(), stats.get("clientVersion"));
        // time measured by us after client connection should be greater than the connection time reported by the statistics
        Assert.assertTrue(String.format("connectionTimeStat was %d, clientConnectionTime was %d (%s)", connectionTimeStat, clientConnectionTime, stats), (clientConnectionTime >= connectionTimeStat));
        String queueSize = stats.get("executionService.userExecutorQueueSize");
        Assert.assertNotNull(String.format("executionService.userExecutorQueueSize should not be null (%s)", stats), queueSize);
        String mapHits = stats.get(ClientStatisticsTest.MAP_HITS_KEY);
        Assert.assertNull(String.format("%s should be null (%s)", ClientStatisticsTest.MAP_HITS_KEY, stats), mapHits);
        String cacheHits = stats.get(ClientStatisticsTest.CACHE_HITS_KEY);
        Assert.assertNull(String.format("%s should be null (%s)", ClientStatisticsTest.CACHE_HITS_KEY, stats), cacheHits);
        String lastStatisticsCollectionTimeString = stats.get("lastStatisticsCollectionTime");
        final long lastCollectionTime = Long.parseLong(lastStatisticsCollectionTimeString);
        // this creates empty map statistics
        client.getMap(ClientStatisticsTest.MAP_NAME);
        // wait enough time for statistics collection
        ClientStatisticsTest.waitForNextStatsCollection(client, clientEngine, lastStatisticsCollectionTimeString);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Map<String, String> stats = ClientStatisticsTest.getStats(client, clientEngine);
                String mapHits = stats.get(ClientStatisticsTest.MAP_HITS_KEY);
                Assert.assertNotNull(String.format("%s should not be null (%s)", ClientStatisticsTest.MAP_HITS_KEY, stats), mapHits);
                Assert.assertEquals(String.format("Expected 0 map hits (%s)", stats), "0", mapHits);
                String cacheHits = stats.get(ClientStatisticsTest.CACHE_HITS_KEY);
                Assert.assertNull(String.format("%s should be null (%s)", ClientStatisticsTest.CACHE_HITS_KEY, stats), cacheHits);
                // verify that collection is periodic
                ClientStatisticsTest.verifyThatCollectionIsPeriodic(stats, lastCollectionTime);
            }
        });
        // produce map and cache stat
        ClientStatisticsTest.produceSomeStats(hazelcastInstance, client);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Map<String, String> stats = ClientStatisticsTest.getStats(client, clientEngine);
                String mapHits = stats.get(ClientStatisticsTest.MAP_HITS_KEY);
                Assert.assertNotNull(String.format("%s should not be null (%s)", ClientStatisticsTest.MAP_HITS_KEY, stats), mapHits);
                Assert.assertEquals(String.format("Expected 1 map hits (%s)", stats), "1", mapHits);
                String cacheHits = stats.get(ClientStatisticsTest.CACHE_HITS_KEY);
                Assert.assertNotNull(String.format("%s should not be null (%s)", ClientStatisticsTest.CACHE_HITS_KEY, stats), cacheHits);
                Assert.assertEquals(String.format("Expected 1 cache hits (%s)", stats), "1", cacheHits);
            }
        });
    }

    @Test
    public void testStatisticsPeriod() {
        HazelcastInstance hazelcastInstance = newHazelcastInstance();
        HazelcastClientInstanceImpl client = createHazelcastClient();
        ClientEngineImpl clientEngine = getClientEngineImpl(hazelcastInstance);
        // wait enough time for statistics collection
        ClientStatisticsTest.waitForFirstStatisticsCollection(client, clientEngine);
        Map<String, String> initialStats = ClientStatisticsTest.getStats(client, clientEngine);
        // produce map and cache stat
        ClientStatisticsTest.produceSomeStats(hazelcastInstance, client);
        // wait enough time for statistics collection
        ClientStatisticsTest.waitForNextStatsCollection(client, clientEngine, initialStats.get("lastStatisticsCollectionTime"));
        Assert.assertNotEquals("initial statistics should not be the same as current stats", initialStats, ClientStatisticsTest.getStats(client, clientEngine));
    }

    @Test
    public void testStatisticsClusterReconnect() {
        HazelcastInstance hazelcastInstance = newHazelcastInstance();
        HazelcastClientInstanceImpl client = createHazelcastClient();
        hazelcastInstance.getLifecycleService().terminate();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getLifecycleService().addLifecycleListener(new LifecycleListener() {
            @Override
            public void stateChanged(LifecycleEvent event) {
                if (CLIENT_CONNECTED.equals(event.getState())) {
                    latch.countDown();
                }
            }
        });
        hazelcastInstance = hazelcastFactory.newHazelcastInstance();
        ClientEngineImpl clientEngine = getClientEngineImpl(hazelcastInstance);
        assertOpenEventually(latch);
        // wait enough time for statistics collection
        ClientStatisticsTest.waitForFirstStatisticsCollection(client, clientEngine);
        ClientStatisticsTest.getStats(client, clientEngine);
    }

    @Test
    public void testStatisticsTwoClients() {
        HazelcastInstance hazelcastInstance = newHazelcastInstance();
        final HazelcastClientInstanceImpl client1 = createHazelcastClient();
        final HazelcastClientInstanceImpl client2 = createHazelcastClient();
        final ClientEngineImpl clientEngine = getClientEngineImpl(hazelcastInstance);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Map<String, String> clientStatistics = clientEngine.getClientStatistics();
                Assert.assertNotNull(clientStatistics);
                Assert.assertEquals(2, clientStatistics.size());
                List<String> expectedUUIDs = new ArrayList<String>(2);
                expectedUUIDs.add(client1.getClientClusterService().getLocalClient().getUuid());
                expectedUUIDs.add(client2.getClientClusterService().getLocalClient().getUuid());
                for (Map.Entry<String, String> clientEntry : clientStatistics.entrySet()) {
                    Assert.assertTrue(expectedUUIDs.contains(clientEntry.getKey()));
                    String stats = clientEntry.getValue();
                    Assert.assertNotNull(stats);
                }
            }
        });
    }

    @Test
    public void testNoUpdateWhenDisabled() {
        HazelcastInstance hazelcastInstance = newHazelcastInstance();
        final ClientEngineImpl clientEngine = getClientEngineImpl(hazelcastInstance);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(ENABLED.getName(), "false");
        clientConfig.setProperty(PERIOD_SECONDS.getName(), Integer.toString(ClientStatisticsTest.STATS_PERIOD_SECONDS));
        hazelcastFactory.newHazelcastClient(clientConfig);
        assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Map<String, String> statistics = clientEngine.getClientStatistics();
                Assert.assertEquals(0, statistics.size());
            }
        }, ((ClientStatisticsTest.STATS_PERIOD_SECONDS) * 3));
    }

    @Test
    public void testEscapeSpecialCharacter() {
        String originalString = "stat1=value1.lastName,stat2=value2\\hello==";
        String escapedString = "stat1\\=value1\\.lastName\\,stat2\\=value2\\\\hello\\=\\=";
        StringBuilder buffer = new StringBuilder(originalString);
        Statistics.escapeSpecialCharacters(buffer);
        Assert.assertEquals(escapedString, buffer.toString());
        Assert.assertEquals(originalString, unescapeSpecialCharacters(escapedString));
    }

    @Test
    public void testSplit() {
        String escapedString = "stat1=value1.lastName,stat2=full\\name==hazel\\,ali,";
        String[] expectedStrings = new String[]{ "stat1=value1.lastName", "stat2=full\\name==hazel\\,ali" };
        List<String> strings = split(escapedString);
        Assert.assertArrayEquals(expectedStrings, strings.toArray());
    }
}

