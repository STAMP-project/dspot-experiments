/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.management.providers.statistics;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.core.statistics.CacheOperationOutcomes;
import org.ehcache.management.ManagementRegistryService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.terracotta.management.model.context.Context;
import org.terracotta.statistics.derived.latency.LatencyHistogramStatistic;

import static org.ehcache.core.statistics.CacheOperationOutcomes.GetOutcome.HIT;
import static org.ehcache.core.statistics.CacheOperationOutcomes.GetOutcome.MISS;


public class StandardEhcacheStatisticsTest {
    private static final int HISTOGRAM_WINDOW_MILLIS = 400;

    private static final int NEXT_WINDOW_SLEEP_MILLIS = 500;

    @Rule
    public final Timeout globalTimeout = Timeout.seconds(10);

    private CacheManager cacheManager;

    private Cache<Long, String> cache;

    private ManagementRegistryService managementRegistry;

    private Context context;

    private long latency;

    private final Map<Long, String> systemOfRecords = new HashMap<>();

    @Test
    public void statTest() throws InterruptedException {
        cache.get(1L);// miss

        cache.put(1L, "one");// put

        cache.get(1L);// hit

        cache.remove(1L);// removal

        IntStream.of(50, 95, 99, 100).forEach(( i) -> {
            assertStatistic("Cache:MissCount").isEqualTo(1L);
            assertStatistic(("Cache:GetMissLatency#" + i)).isGreaterThan(0);
            assertStatistic("Cache:HitCount").isEqualTo(1L);
            assertStatistic(("Cache:GetHitLatency#" + i)).isGreaterThan(0);
            assertStatistic("Cache:PutCount").isEqualTo(1L);
            assertStatistic(("Cache:PutLatency#" + i)).isGreaterThan(0);
            assertStatistic("Cache:RemovalCount").isEqualTo(1L);
            assertStatistic(("Cache:RemoveLatency#" + i)).isGreaterThan(0);
        });
    }

    @Test
    public void getCacheGetHitMissLatencies() {
        Consumer<LatencyHistogramStatistic> verifier = ( histogram) -> {
            assertThat(histogram.count()).isEqualTo(0L);
            latency = 100;
            cache.get(1L);
            latency = 50;
            cache.get(2L);
            assertThat(histogram.count()).isEqualTo(2L);
            assertThat(histogram.maximum()).isGreaterThanOrEqualTo(TimeUnit.MILLISECONDS.toNanos(100L));
            minimumSleep(StandardEhcacheStatisticsTest.NEXT_WINDOW_SLEEP_MILLIS);
            latency = 50;
            cache.get(3L);
            latency = 150;
            cache.get(4L);
            assertThat(histogram.count()).isEqualTo(2L);
            assertThat(histogram.maximum()).isGreaterThanOrEqualTo(TimeUnit.MILLISECONDS.toNanos(150L));
        };
        verifier.accept(getHistogram(MISS, "get"));
        systemOfRecords.put(1L, "a");
        systemOfRecords.put(2L, "b");
        systemOfRecords.put(3L, "c");
        systemOfRecords.put(4L, "d");
        systemOfRecords.put(5L, "e");
        verifier.accept(getHistogram(HIT, "get"));
    }
}

