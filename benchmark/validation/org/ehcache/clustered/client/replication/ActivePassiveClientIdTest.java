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
package org.ehcache.clustered.client.replication;


import java.util.Map;
import java.util.function.Predicate;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.client.internal.store.ServerStoreProxy;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityMessage;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityResponse;
import org.ehcache.clustered.server.ObservableEhcacheServerEntityService;
import org.ehcache.clustered.server.store.ObservableClusterTierServerEntityService;
import org.junit.Test;
import org.terracotta.client.message.tracker.OOOMessageHandler;
import org.terracotta.passthrough.PassthroughClusterControl;


public class ActivePassiveClientIdTest {
    private static final String STRIPENAME = "stripe";

    private static final String STRIPE_URI = "passthrough://" + (ActivePassiveClientIdTest.STRIPENAME);

    private static final int KEY_ENDS_UP_IN_SEGMENT_11 = 11;

    private PassthroughClusterControl clusterControl;

    private ObservableEhcacheServerEntityService observableEhcacheServerEntityService;

    private ObservableClusterTierServerEntityService observableClusterTierServerEntityService;

    private ClusteringService service;

    private ServerStoreProxy storeProxy;

    private ObservableClusterTierServerEntityService.ObservableClusterTierActiveEntity activeEntity;

    private ObservableClusterTierServerEntityService.ObservableClusterTierPassiveEntity passiveEntity;

    private OOOMessageHandler<EhcacheEntityMessage, EhcacheEntityResponse> activeMessageHandler;

    private OOOMessageHandler<EhcacheEntityMessage, EhcacheEntityResponse> passiveMessageHandler;

    @Test
    public void messageTrackedAndRemovedWhenClientLeaves() throws Exception {
        assertThat(activeMessageHandler.getTrackedClients().count()).isZero();// no client tracked

        storeProxy.getAndAppend(42L, ChainUtils.createPayload(42L));
        Map<Long, EhcacheEntityResponse> responses = activeMessageHandler.getTrackedResponsesForSegment(ActivePassiveClientIdTest.KEY_ENDS_UP_IN_SEGMENT_11, activeMessageHandler.getTrackedClients().findFirst().get());
        assertThat(responses).hasSize(1);// should now track one message

        assertThat(activeEntity.getConnectedClients()).hasSize(1);// make sure we currently have one client attached

        service.stop();// stop the service. It will remove the client

        activeEntity.notifyDestroyed(activeMessageHandler.getTrackedClients().iterator().next());// Notify that the client was removed. A real clustered server will do that. But the Passthrough doesn't. So we simulate it

        waitForPredicate(( e) -> (activeEntity.getConnectedClients().size()) > 0, 2000);// wait for the client to be removed, might be async, so we wait

        assertThat(activeMessageHandler.getTrackedClients().count()).isZero();// all tracked messages for this client should have been removed

    }

    @Test
    public void untrackedMessageAreNotStored() throws Exception {
        // Nothing tracked
        assertThat(activeMessageHandler.getTrackedClients().count()).isZero();
        // Send a replace message, those are not tracked
        storeProxy.replaceAtHead(44L, ChainUtils.chainOf(ChainUtils.createPayload(44L)), ChainUtils.chainOf());
        // Not tracked as well
        storeProxy.get(42L);
        assertThat(activeMessageHandler.getTrackedClients().count()).isZero();
    }

    @Test
    public void trackedMessagesReplicatedToPassive() throws Exception {
        clusterControl.terminateOnePassive();
        storeProxy.getAndAppend(42L, ChainUtils.createPayload(42L));
        clusterControl.startOneServer();
        clusterControl.waitForRunningPassivesInStandby();
        // Save the new handler from the freshly started passive
        passiveEntity = observableClusterTierServerEntityService.getServedPassiveEntitiesFor("test").get(1);
        passiveMessageHandler = passiveEntity.getMessageHandler();
        assertThat(passiveMessageHandler.getTrackedClients().count()).isEqualTo(1L);// one client tracked

        Map<Long, EhcacheEntityResponse> responses = passiveMessageHandler.getTrackedResponsesForSegment(ActivePassiveClientIdTest.KEY_ENDS_UP_IN_SEGMENT_11, passiveMessageHandler.getTrackedClients().findFirst().get());
        assertThat(responses).hasSize(1);// one message should have sync

    }

    @Test
    public void messageTrackedAndRemovedByPassiveWhenClientLeaves() throws Exception {
        assertThat(passiveMessageHandler.getTrackedClients().count()).isZero();// nothing tracked right now

        storeProxy.getAndAppend(42L, ChainUtils.createPayload(42L));
        Map<Long, EhcacheEntityResponse> responses = passiveMessageHandler.getTrackedResponsesForSegment(ActivePassiveClientIdTest.KEY_ENDS_UP_IN_SEGMENT_11, passiveMessageHandler.getTrackedClients().findFirst().get());
        assertThat(responses).hasSize(1);// should now track one message

        service.stop();// stop the service. It will remove the client

        activeEntity.notifyDestroyed(passiveMessageHandler.getTrackedClients().iterator().next());
        passiveEntity.notifyDestroyed(passiveMessageHandler.getTrackedClients().iterator().next());// Notify that the client was removed. A real clustered server will do that. But the Passthrough doesn't. So we simulate it

        waitForPredicate(( e) -> (activeEntity.getConnectedClients().size()) > 0, 2000);// wait for the client to be removed, might be async, so we wait

        assertThat(passiveMessageHandler.getTrackedClients().count()).isZero();// all tracked messages for this client should have been removed

    }
}

