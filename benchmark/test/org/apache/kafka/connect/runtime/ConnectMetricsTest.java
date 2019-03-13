/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime;


import WorkerConfig.INTERNAL_KEY_CONVERTER_CLASS_CONFIG;
import WorkerConfig.INTERNAL_VALUE_CONVERTER_CLASS_CONFIG;
import WorkerConfig.KEY_CONVERTER_CLASS_CONFIG;
import WorkerConfig.VALUE_CONVERTER_CLASS_CONFIG;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.connect.runtime.ConnectMetrics.MetricGroup;
import org.apache.kafka.connect.runtime.ConnectMetrics.MetricGroupId;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class ConnectMetricsTest {
    private static final Map<String, String> DEFAULT_WORKER_CONFIG = new HashMap<>();

    static {
        ConnectMetricsTest.DEFAULT_WORKER_CONFIG.put(KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        ConnectMetricsTest.DEFAULT_WORKER_CONFIG.put(VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        ConnectMetricsTest.DEFAULT_WORKER_CONFIG.put(INTERNAL_KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        ConnectMetricsTest.DEFAULT_WORKER_CONFIG.put(INTERNAL_VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
    }

    private ConnectMetrics metrics;

    @Test
    public void testKafkaMetricsNotNull() {
        Assert.assertNotNull(metrics.metrics());
    }

    @Test
    public void testCreatingTags() {
        Map<String, String> tags = ConnectMetrics.tags("k1", "v1", "k2", "v2");
        Assert.assertEquals("v1", tags.get("k1"));
        Assert.assertEquals("v2", tags.get("k2"));
        Assert.assertEquals(2, tags.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatingTagsWithOddNumberOfTags() {
        ConnectMetrics.tags("k1", "v1", "k2", "v2", "extra");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingGroupWithOddNumberOfTags() {
        metrics.group("name", "k1", "v1", "k2", "v2", "extra");
    }

    @Test
    public void testGettingGroupWithTags() {
        MetricGroup group1 = metrics.group("name", "k1", "v1", "k2", "v2");
        Assert.assertEquals("v1", group1.tags().get("k1"));
        Assert.assertEquals("v2", group1.tags().get("k2"));
        Assert.assertEquals(2, group1.tags().size());
    }

    @Test
    public void testGettingGroupMultipleTimes() {
        MetricGroup group1 = metrics.group("name");
        MetricGroup group2 = metrics.group("name");
        Assert.assertNotNull(group1);
        Assert.assertSame(group1, group2);
        MetricGroup group3 = metrics.group("other");
        Assert.assertNotNull(group3);
        Assert.assertNotSame(group1, group3);
        // Now with tags
        MetricGroup group4 = metrics.group("name", "k1", "v1");
        Assert.assertNotNull(group4);
        Assert.assertNotSame(group1, group4);
        Assert.assertNotSame(group2, group4);
        Assert.assertNotSame(group3, group4);
        MetricGroup group5 = metrics.group("name", "k1", "v1");
        Assert.assertSame(group4, group5);
    }

    @Test
    public void testMetricGroupIdIdentity() {
        MetricGroupId id1 = metrics.groupId("name", "k1", "v1");
        MetricGroupId id2 = metrics.groupId("name", "k1", "v1");
        MetricGroupId id3 = metrics.groupId("name", "k1", "v1", "k2", "v2");
        Assert.assertEquals(id1.hashCode(), id2.hashCode());
        Assert.assertEquals(id1, id2);
        Assert.assertEquals(id1.toString(), id2.toString());
        Assert.assertEquals(id1.groupName(), id2.groupName());
        Assert.assertEquals(id1.tags(), id2.tags());
        Assert.assertNotNull(id1.tags());
        Assert.assertNotEquals(id1, id3);
    }

    @Test
    public void testMetricGroupIdWithoutTags() {
        MetricGroupId id1 = metrics.groupId("name");
        MetricGroupId id2 = metrics.groupId("name");
        Assert.assertEquals(id1.hashCode(), id2.hashCode());
        Assert.assertEquals(id1, id2);
        Assert.assertEquals(id1.toString(), id2.toString());
        Assert.assertEquals(id1.groupName(), id2.groupName());
        Assert.assertEquals(id1.tags(), id2.tags());
        Assert.assertNotNull(id1.tags());
        Assert.assertNotNull(id2.tags());
    }

    @Test
    public void testRecreateWithClose() {
        final Sensor originalSensor = addToGroup(metrics, false);
        final Sensor recreatedSensor = addToGroup(metrics, true);
        // because we closed the metricGroup, we get a brand-new sensor
        Assert.assertNotSame(originalSensor, recreatedSensor);
    }

    @Test
    public void testRecreateWithoutClose() {
        final Sensor originalSensor = addToGroup(metrics, false);
        final Sensor recreatedSensor = addToGroup(metrics, false);
        // since we didn't close the group, the second addToGroup is idempotent
        Assert.assertSame(originalSensor, recreatedSensor);
    }
}

