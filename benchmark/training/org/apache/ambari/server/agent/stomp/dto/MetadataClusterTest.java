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
package org.apache.ambari.server.agent.stomp.dto;


import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;


public class MetadataClusterTest {
    @Test
    public void shouldReturnFalseWhenUpdatingServiceLevelParamsWithoutNewOrRemovedServices() throws Exception {
        final SortedMap<String, MetadataServiceInfo> current = new TreeMap<>();
        current.put("service1", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service2", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service3", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        final MetadataCluster metadataCluster = MetadataCluster.serviceLevelParamsMetadataCluster(null, current, true);
        final SortedMap<String, MetadataServiceInfo> updated = new TreeMap(current);
        Assert.assertFalse(metadataCluster.updateServiceLevelParams(updated, true));
        Assert.assertEquals(current, metadataCluster.getServiceLevelParams());
    }

    @Test
    public void shouldReturnTrueWhenUpdatingServiceLevelParamsUponServiceAddition() throws Exception {
        final SortedMap<String, MetadataServiceInfo> current = new TreeMap<>();
        current.put("service1", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service2", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        final MetadataCluster metadataCluster = MetadataCluster.serviceLevelParamsMetadataCluster(null, current, true);
        final SortedMap<String, MetadataServiceInfo> updated = new TreeMap(current);
        updated.put("service3", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        Assert.assertTrue(metadataCluster.updateServiceLevelParams(updated, true));
        Assert.assertEquals(updated, metadataCluster.getServiceLevelParams());
    }

    @Test
    public void shouldReturnTrueWhenUpdatingServiceLevelParamsUponServiceRemoval() throws Exception {
        final SortedMap<String, MetadataServiceInfo> current = new TreeMap<>();
        current.put("service1", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service2", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service3", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        final MetadataCluster metadataCluster = MetadataCluster.serviceLevelParamsMetadataCluster(null, current, true);
        final SortedMap<String, MetadataServiceInfo> updated = new TreeMap(current);
        updated.remove("service2");
        Assert.assertTrue(metadataCluster.updateServiceLevelParams(updated, true));
        Assert.assertEquals(updated, metadataCluster.getServiceLevelParams());
    }

    @Test
    public void shouldReturnFalseWhenNullServiceLevelParamsArePassedBecauseOfPartialConfigurationUpdate() throws Exception {
        final SortedMap<String, MetadataServiceInfo> current = new TreeMap<>();
        current.put("service1", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service2", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service3", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        final MetadataCluster metadataCluster = MetadataCluster.serviceLevelParamsMetadataCluster(null, current, true);
        Assert.assertFalse(metadataCluster.updateServiceLevelParams(null, true));
        Assert.assertEquals(current, metadataCluster.getServiceLevelParams());
    }

    @Test
    public void shouldReturnTrueWhenUpdatingServiceLevelParamsWithoutFullServiceLevelMetadata() throws Exception {
        final SortedMap<String, MetadataServiceInfo> current = new TreeMap<>();
        current.put("service1", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service2", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        current.put("service3", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        final MetadataCluster metadataCluster = MetadataCluster.serviceLevelParamsMetadataCluster(null, current, true);
        final SortedMap<String, MetadataServiceInfo> updated = new TreeMap<>();
        updated.put("service3", new MetadataServiceInfo("v2", Boolean.TRUE, null, 2L, "servicePackageFolder2"));
        updated.put("service4", new MetadataServiceInfo("v1", Boolean.FALSE, null, 1L, "servicePackageFolder"));
        Assert.assertTrue(metadataCluster.updateServiceLevelParams(updated, false));
        final SortedMap<String, MetadataServiceInfo> expected = current;
        expected.putAll(updated);
        Assert.assertEquals(expected, metadataCluster.getServiceLevelParams());
    }

    @Test
    public void shouldReturnFalseWhenUpdatingClusterLevelParamsWithoutClusterLevelParameterAdditionOrRemoval() throws Exception {
        final SortedMap<String, String> current = new TreeMap<>();
        current.put("param1", "value1");
        current.put("param2", "value2");
        current.put("param3", "value3");
        final MetadataCluster metadataCluster = MetadataCluster.clusterLevelParamsMetadataCluster(null, current);
        final SortedMap<String, String> updated = new TreeMap<>(current);
        Assert.assertFalse(metadataCluster.updateClusterLevelParams(updated));
        Assert.assertEquals(current, metadataCluster.getClusterLevelParams());
    }

    @Test
    public void shouldReturnTrueWhenUpdatingClusterLevelParamsUponClusterLevelParameterAddition() throws Exception {
        final SortedMap<String, String> current = new TreeMap<>();
        current.put("param1", "value1");
        current.put("param2", "value2");
        final MetadataCluster metadataCluster = MetadataCluster.clusterLevelParamsMetadataCluster(null, current);
        final SortedMap<String, String> updated = new TreeMap<>(current);
        updated.put("param3", "value3");
        Assert.assertTrue(metadataCluster.updateClusterLevelParams(updated));
        Assert.assertEquals(updated, metadataCluster.getClusterLevelParams());
    }

    @Test
    public void shouldReturnTrueWhenUpdatingClusterLevelParamsUponClusterLevelParameterRemoval() throws Exception {
        final SortedMap<String, String> current = new TreeMap<>();
        current.put("param1", "value1");
        current.put("param2", "value2");
        current.put("param3", "value3");
        final MetadataCluster metadataCluster = MetadataCluster.clusterLevelParamsMetadataCluster(null, current);
        final SortedMap<String, String> updated = new TreeMap<>(current);
        updated.remove("param2");
        Assert.assertTrue(metadataCluster.updateClusterLevelParams(updated));
        Assert.assertEquals(updated, metadataCluster.getClusterLevelParams());
    }

    @Test
    public void shouldReturnFalseWhenNullClusterLevelParamsArePassedBecauseOfPartialConfigurationUpdate() throws Exception {
        final SortedMap<String, String> current = new TreeMap<>();
        current.put("param1", "value1");
        current.put("param2", "value2");
        current.put("param3", "value3");
        final MetadataCluster metadataCluster = MetadataCluster.clusterLevelParamsMetadataCluster(null, current);
        Assert.assertFalse(metadataCluster.updateClusterLevelParams(null));
        Assert.assertEquals(current, metadataCluster.getClusterLevelParams());
    }
}

