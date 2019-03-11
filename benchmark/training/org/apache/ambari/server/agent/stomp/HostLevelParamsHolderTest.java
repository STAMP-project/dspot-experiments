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
package org.apache.ambari.server.agent.stomp;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.agent.RecoveryConfig;
import org.apache.ambari.server.agent.stomp.dto.HostLevelParamsCluster;
import org.apache.ambari.server.events.HostLevelParamsUpdateEvent;
import org.apache.ambari.server.events.publishers.AmbariEventPublisher;
import org.junit.Assert;
import org.junit.Test;


public class HostLevelParamsHolderTest {
    private final Long HOST_ID = 1L;

    @Test
    public void testHandleUpdateEmptyCurrent() {
        HostLevelParamsUpdateEvent current = new HostLevelParamsUpdateEvent(HOST_ID, Collections.emptyMap());
        Map<String, HostLevelParamsCluster> clusters = new HashMap<>();
        HostLevelParamsCluster cluster = new HostLevelParamsCluster(new RecoveryConfig(null), Collections.emptyMap());
        clusters.put("1", cluster);
        HostLevelParamsUpdateEvent update = new HostLevelParamsUpdateEvent(HOST_ID, clusters);
        HostLevelParamsHolder levelParamsHolder = new HostLevelParamsHolder(createNiceMock(AmbariEventPublisher.class));
        HostLevelParamsUpdateEvent result = levelParamsHolder.handleUpdate(current, update);
        Assert.assertFalse((result == update));
        Assert.assertFalse((result == current));
        Assert.assertEquals(result, update);
    }

    @Test
    public void testHandleUpdateEmptyUpdate() {
        Map<String, HostLevelParamsCluster> clusters = new HashMap<>();
        HostLevelParamsCluster cluster = new HostLevelParamsCluster(new RecoveryConfig(null), Collections.emptyMap());
        clusters.put("1", cluster);
        HostLevelParamsUpdateEvent current = new HostLevelParamsUpdateEvent(HOST_ID, clusters);
        HostLevelParamsUpdateEvent update = new HostLevelParamsUpdateEvent(HOST_ID, Collections.emptyMap());
        HostLevelParamsHolder levelParamsHolder = new HostLevelParamsHolder(createNiceMock(AmbariEventPublisher.class));
        HostLevelParamsUpdateEvent result = levelParamsHolder.handleUpdate(current, update);
        Assert.assertFalse((result == update));
        Assert.assertFalse((result == current));
        Assert.assertEquals(result, null);
    }

    @Test
    public void testHandleUpdateNoChanges() {
        Map<String, HostLevelParamsCluster> currentClusters = new HashMap<>();
        HostLevelParamsCluster currentCluster = new HostLevelParamsCluster(new RecoveryConfig(null), Collections.emptyMap());
        currentClusters.put("1", currentCluster);
        HostLevelParamsUpdateEvent current = new HostLevelParamsUpdateEvent(HOST_ID, currentClusters);
        Map<String, HostLevelParamsCluster> updateClusters = new HashMap<>();
        HostLevelParamsCluster updateCluster = new HostLevelParamsCluster(new RecoveryConfig(null), Collections.emptyMap());
        updateClusters.put("1", updateCluster);
        HostLevelParamsUpdateEvent update = new HostLevelParamsUpdateEvent(HOST_ID, updateClusters);
        HostLevelParamsHolder levelParamsHolder = new HostLevelParamsHolder(createNiceMock(AmbariEventPublisher.class));
        HostLevelParamsUpdateEvent result = levelParamsHolder.handleUpdate(current, update);
        Assert.assertFalse((result == update));
        Assert.assertFalse((result == current));
        Assert.assertEquals(result, null);
    }

    @Test
    public void testHandleUpdateOnChanges() {
        Map<String, HostLevelParamsCluster> currentClusters = new HashMap<>();
        HostLevelParamsCluster currentCluster = new HostLevelParamsCluster(new RecoveryConfig(null), Collections.emptyMap());
        currentClusters.put("1", currentCluster);
        HostLevelParamsUpdateEvent current = new HostLevelParamsUpdateEvent(HOST_ID, currentClusters);
        Map<String, HostLevelParamsCluster> updateClusters = new HashMap<>();
        HostLevelParamsCluster updateCluster = new HostLevelParamsCluster(new RecoveryConfig(null), Collections.emptyMap());
        updateClusters.put("2", updateCluster);
        HostLevelParamsUpdateEvent update = new HostLevelParamsUpdateEvent(HOST_ID, updateClusters);
        HostLevelParamsHolder levelParamsHolder = new HostLevelParamsHolder(createNiceMock(AmbariEventPublisher.class));
        HostLevelParamsUpdateEvent result = levelParamsHolder.handleUpdate(current, update);
        Assert.assertFalse((result == update));
        Assert.assertFalse((result == current));
        Assert.assertEquals(2, result.getHostLevelParamsClusters().size());
        Assert.assertTrue(result.getHostLevelParamsClusters().containsKey("1"));
        Assert.assertTrue(result.getHostLevelParamsClusters().containsKey("2"));
    }
}

