/**
 * -\-\-
 * Spotify Apollo Metrics Module
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
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
package com.spotify.apollo.metrics.semantic;


import MetricsConfig.DEFAULT_ENABLED_METRICS;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.EnumSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricsConfigTest {
    @Test
    public void testDefaultreservoirTtl() {
        Assert.assertThat(reservoirTtl(), CoreMatchers.is(300));
    }

    @Test
    public void testOverrideDefaultreservoirTtl() {
        Assert.assertThat(reservoirTtl(), CoreMatchers.is(30));
    }

    @Test
    public void shouldReturnDefaultIfNoConfig() throws Exception {
        Assert.assertThat(serverMetrics(), CoreMatchers.is(DEFAULT_ENABLED_METRICS));
    }

    @Test
    public void shouldEnableMetricsIfConfigured() throws Exception {
        Config config = ConfigFactory.parseString("metrics.server: [REQUEST_PAYLOAD_SIZE, ENDPOINT_REQUEST_DURATION, ENDPOINT_REQUEST_RATE]");
        Assert.assertThat(serverMetrics(), CoreMatchers.is(EnumSet.of(What.REQUEST_PAYLOAD_SIZE, What.ENDPOINT_REQUEST_DURATION, What.ENDPOINT_REQUEST_RATE)));
    }

    @Test
    public void shouldEnablePrecreatingMetersForStatusCodesIfConfigured() throws Exception {
        Config config = ConfigFactory.parseString("metrics.precreate-codes: [300, 403, 404]");
        Assert.assertThat(Sets.newHashSet(precreateCodes()), CoreMatchers.is(Sets.newHashSet(300, 403, 404)));
    }
}

