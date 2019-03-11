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
package org.apache.beam.runners.core.metrics;


import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link MetricsMap}.
 */
@RunWith(JUnit4.class)
public class MetricsMapTest {
    public MetricsMap<String, AtomicLong> metricsMap = new MetricsMap(( unusedKey) -> new AtomicLong());

    @Test
    public void testCreateSeparateInstances() {
        AtomicLong foo = metricsMap.get("foo");
        AtomicLong bar = metricsMap.get("bar");
        Assert.assertThat(foo, Matchers.not(Matchers.sameInstance(bar)));
    }

    @Test
    public void testReuseInstances() {
        AtomicLong foo1 = metricsMap.get("foo");
        AtomicLong foo2 = metricsMap.get("foo");
        Assert.assertThat(foo1, Matchers.sameInstance(foo2));
    }

    @Test
    public void testGet() {
        Assert.assertThat(metricsMap.tryGet("foo"), Matchers.nullValue(AtomicLong.class));
        AtomicLong foo = metricsMap.get("foo");
        Assert.assertThat(metricsMap.tryGet("foo"), Matchers.sameInstance(foo));
    }

    @Test
    public void testGetEntries() {
        AtomicLong foo = metricsMap.get("foo");
        AtomicLong bar = metricsMap.get("bar");
        Assert.assertThat(metricsMap.entries(), Matchers.containsInAnyOrder(MetricsMapTest.hasEntry("foo", foo), MetricsMapTest.hasEntry("bar", bar)));
    }
}

