/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MonitoredResourceTest {
    private static final String TYPE = "cloudsql_database";

    private static final Map<String, String> LABELS = ImmutableMap.of("dataset-id", "myDataset", "zone", "myZone");

    private static final MonitoredResource MONITORED_RESOURCE = MonitoredResource.newBuilder(MonitoredResourceTest.TYPE).setLabels(MonitoredResourceTest.LABELS).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(MonitoredResourceTest.TYPE, MonitoredResourceTest.MONITORED_RESOURCE.getType());
        Assert.assertEquals(MonitoredResourceTest.LABELS, MonitoredResourceTest.MONITORED_RESOURCE.getLabels());
        MonitoredResource monitoredResource = MonitoredResource.newBuilder(MonitoredResourceTest.TYPE).addLabel("dataset-id", "myDataset").addLabel("zone", "myZone").build();
        Assert.assertEquals(MonitoredResourceTest.TYPE, monitoredResource.getType());
        Assert.assertEquals(MonitoredResourceTest.LABELS, monitoredResource.getLabels());
        compareMonitoredResource(MonitoredResourceTest.MONITORED_RESOURCE, monitoredResource);
        monitoredResource = MonitoredResource.newBuilder(MonitoredResourceTest.TYPE).setType("global").addLabel("dataset-id", "myDataset").addLabel("zone", "myZone").clearLabels().build();
        Assert.assertEquals("global", monitoredResource.getType());
        Assert.assertEquals(ImmutableMap.of(), monitoredResource.getLabels());
    }

    @Test
    public void testToBuilder() {
        compareMonitoredResource(MonitoredResourceTest.MONITORED_RESOURCE, MonitoredResourceTest.MONITORED_RESOURCE.toBuilder().build());
        MonitoredResource monitoredResource = MonitoredResourceTest.MONITORED_RESOURCE.toBuilder().setType("global").clearLabels().build();
        Assert.assertEquals("global", monitoredResource.getType());
        Assert.assertEquals(ImmutableMap.of(), monitoredResource.getLabels());
        monitoredResource = monitoredResource.toBuilder().setType(MonitoredResourceTest.TYPE).setLabels(ImmutableMap.of("dataset-id", "myDataset")).addLabel("zone", "myZone").build();
        compareMonitoredResource(MonitoredResourceTest.MONITORED_RESOURCE, monitoredResource);
    }

    @Test
    public void testOf() {
        MonitoredResource monitoredResource = MonitoredResource.of(MonitoredResourceTest.TYPE, MonitoredResourceTest.LABELS);
        Assert.assertEquals(MonitoredResourceTest.TYPE, monitoredResource.getType());
        Assert.assertEquals(MonitoredResourceTest.LABELS, monitoredResource.getLabels());
        compareMonitoredResource(MonitoredResourceTest.MONITORED_RESOURCE, monitoredResource);
    }

    @Test
    public void testToAndFromPb() {
        compareMonitoredResource(MonitoredResourceTest.MONITORED_RESOURCE, MonitoredResource.fromPb(MonitoredResourceTest.MONITORED_RESOURCE.toPb()));
        MonitoredResource monitoredResource = MonitoredResource.of(MonitoredResourceTest.TYPE, ImmutableMap.<String, String>of());
        compareMonitoredResource(monitoredResource, MonitoredResource.fromPb(monitoredResource.toPb()));
    }
}

