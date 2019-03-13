/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.servicescommon.statistics;


import ConnectionState.RECONNECTED;
import ConnectionState.SUSPENDED;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ZooKeeperMetricsImplTest {
    private final MetricRegistry registry = new MetricRegistry();

    private final ZooKeeperMetrics metrics = new ZooKeeperMetricsImpl("group", registry);

    @Test
    public void testTimer() throws Exception {
        metrics.updateTimer("timer", 100, TimeUnit.NANOSECONDS);
        final String name = "group.zookeeper.timer";
        Assert.assertThat(registry.getTimers(), Matchers.hasKey(name));
        final Timer timer = registry.timer(name);
        Assert.assertEquals(1, timer.getCount());
        Assert.assertArrayEquals(new long[]{ 100 }, timer.getSnapshot().getValues());
    }

    @Test
    public void testConnectionStateChanged() throws Exception {
        metrics.connectionStateChanged(SUSPENDED);
        metrics.connectionStateChanged(RECONNECTED);
        Assert.assertThat(registry.getMeters(), Matchers.allOf(Matchers.hasKey("group.zookeeper.connection_state_changed"), Matchers.hasKey("group.zookeeper.connection_state_SUSPENDED"), Matchers.hasKey("group.zookeeper.connection_state_RECONNECTED")));
        Assert.assertEquals(2, registry.meter("group.zookeeper.connection_state_changed").getCount());
        Assert.assertEquals(1, registry.meter("group.zookeeper.connection_state_SUSPENDED").getCount());
        Assert.assertEquals(1, registry.meter("group.zookeeper.connection_state_RECONNECTED").getCount());
    }
}

