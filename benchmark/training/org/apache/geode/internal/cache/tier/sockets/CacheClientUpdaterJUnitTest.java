/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets;


import CacheClientUpdater.CCUStats;
import CacheClientUpdater.StatisticsProvider;
import java.net.SocketException;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.client.internal.Endpoint;
import org.apache.geode.cache.client.internal.EndpointManager;
import org.apache.geode.cache.client.internal.QueueManager;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.ServerLocation;
import org.apache.geode.internal.cache.tier.ClientSideHandshake;
import org.apache.geode.internal.net.SocketCreator;
import org.apache.geode.test.junit.categories.ClientSubscriptionTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientSubscriptionTest.class })
public class CacheClientUpdaterJUnitTest {
    @Test
    public void failureToConnectClosesStatistics() throws Exception {
        // CacheClientUpdater's constructor takes a lot of parameters that we need to mock
        ServerLocation location = new ServerLocation("localhost", 1234);
        ClientSideHandshake handshake = Mockito.mock(ClientSideHandshake.class);
        Mockito.when(handshake.isDurable()).thenReturn(Boolean.FALSE);
        QueueManager queueManager = null;
        Mockito.mock(QueueManager.class);
        EndpointManager endpointManager = Mockito.mock(EndpointManager.class);
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        // shutdown checks
        DistributedSystem distributedSystem = Mockito.mock(DistributedSystem.class);
        CancelCriterion cancelCriterion = Mockito.mock(CancelCriterion.class);
        Mockito.when(distributedSystem.getCancelCriterion()).thenReturn(cancelCriterion);
        Mockito.when(cancelCriterion.isCancelInProgress()).thenReturn(Boolean.FALSE);
        // engineer a failure to connect via SocketCreator
        SocketCreator socketCreator = Mockito.mock(SocketCreator.class);
        Mockito.when(socketCreator.connectForClient(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(Integer.class))).thenThrow(new SocketException("ouch"));
        // mock some stats that we can then use to ensure that they're closed when the problem occurs
        CacheClientUpdater.StatisticsProvider statisticsProvider = Mockito.mock(StatisticsProvider.class);
        CacheClientUpdater.CCUStats ccuStats = Mockito.mock(CCUStats.class);
        Mockito.when(statisticsProvider.createStatistics(distributedSystem, location)).thenReturn(ccuStats);
        // CCU's constructor fails to connect
        CacheClientUpdater clientUpdater = new CacheClientUpdater("testUpdater", location, false, distributedSystem, handshake, queueManager, endpointManager, endpoint, 10000, socketCreator, statisticsProvider);
        // now introspect to make sure the right actions were taken
        // The updater should not be in a connected state
        assertThat(clientUpdater.isConnected()).isFalse();
        // The statistics should be closed
        Mockito.verify(ccuStats).close();
        // The endpoint should be reported as having crashed
        Mockito.verify(endpointManager).serverCrashed(endpoint);
    }
}

