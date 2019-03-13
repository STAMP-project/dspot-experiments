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


import SimpleMonitoringInfoBuilder.ELEMENT_COUNT_URN;
import java.io.Serializable;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static SimpleMonitoringInfoBuilder.ELEMENT_COUNT_URN;


/**
 * Tests for {@link MonitoringInfoMetricName}.
 */
public class MonitoringInfoMetricNameTest implements Serializable {
    @Rule
    public final transient ExpectedException thrown = ExpectedException.none();

    @Test
    public void testElementCountConstruction() {
        HashMap<String, String> labels = new HashMap<String, String>();
        String urn = ELEMENT_COUNT_URN;
        MonitoringInfoMetricName name = MonitoringInfoMetricName.named(urn, labels);
        Assert.assertEquals(null, name.getName());
        Assert.assertEquals(null, name.getNamespace());
        Assert.assertEquals(labels, name.getLabels());
        Assert.assertEquals(urn, name.getUrn());
        Assert.assertEquals(name, name);// test self equals;

        // Reconstruct and test equality and hash code equivalence
        urn = ELEMENT_COUNT_URN;
        labels = new HashMap<String, String>();
        MonitoringInfoMetricName name2 = MonitoringInfoMetricName.named(urn, labels);
        Assert.assertEquals(name, name2);
        Assert.assertEquals(name.hashCode(), name2.hashCode());
    }

    @Test
    public void testUserCounterUrnConstruction() {
        String urn = SimpleMonitoringInfoBuilder.userMetricUrn("namespace", "name");
        HashMap<String, String> labels = new HashMap<String, String>();
        MonitoringInfoMetricName name = MonitoringInfoMetricName.named(urn, labels);
        Assert.assertEquals("name", name.getName());
        Assert.assertEquals("namespace", name.getNamespace());
        Assert.assertEquals(labels, name.getLabels());
        Assert.assertEquals(urn, name.getUrn());
        Assert.assertEquals(name, name);// test self equals;

        // Reconstruct and test equality and hash code equivalence
        urn = SimpleMonitoringInfoBuilder.userMetricUrn("namespace", "name");
        labels = new HashMap<String, String>();
        MonitoringInfoMetricName name2 = MonitoringInfoMetricName.named(urn, labels);
        Assert.assertEquals(name, name2);
        Assert.assertEquals(name.hashCode(), name2.hashCode());
    }

    @Test
    public void testNotEqualsDiffLabels() {
        HashMap<String, String> labels = new HashMap<String, String>();
        String urn = ELEMENT_COUNT_URN;
        MonitoringInfoMetricName name = MonitoringInfoMetricName.named(urn, labels);
        // Reconstruct and test equality and hash code equivalence
        urn = ELEMENT_COUNT_URN;
        labels = new HashMap<String, String>();
        labels.put("label", "value1");
        MonitoringInfoMetricName name2 = MonitoringInfoMetricName.named(urn, labels);
        Assert.assertNotEquals(name, name2);
        Assert.assertNotEquals(name.hashCode(), name2.hashCode());
    }

    @Test
    public void testNotEqualsDiffUrn() {
        HashMap<String, String> labels = new HashMap<String, String>();
        String urn = ELEMENT_COUNT_URN;
        MonitoringInfoMetricName name = MonitoringInfoMetricName.named(urn, labels);
        // Reconstruct and test equality and hash code equivalence
        urn = "differentUrn";
        labels = new HashMap<String, String>();
        MonitoringInfoMetricName name2 = MonitoringInfoMetricName.named(urn, labels);
        Assert.assertNotEquals(name, name2);
        Assert.assertNotEquals(name.hashCode(), name2.hashCode());
    }

    @Test
    public void testNullLabelsThrows() {
        thrown.expect(IllegalArgumentException.class);
        HashMap<String, String> labels = null;
        MonitoringInfoMetricName.named(ELEMENT_COUNT_URN, labels);
    }

    @Test
    public void testNullUrnThrows() {
        HashMap<String, String> labels = new HashMap<String, String>();
        thrown.expect(IllegalArgumentException.class);
        MonitoringInfoMetricName.named(null, labels);
    }

    @Test
    public void testEmptyUrnThrows() {
        HashMap<String, String> labels = new HashMap<String, String>();
        thrown.expect(IllegalArgumentException.class);
        MonitoringInfoMetricName.named("", labels);
    }
}

