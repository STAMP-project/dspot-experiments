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
package org.ehcache.clustered.server.store;


import ClusterTierActiveEntity.SYNC_DATA_GETS_PROP;
import ClusterTierActiveEntity.SYNC_DATA_SIZE_PROP;
import Consistency.STRONG;
import EhcacheEntityResponse.GetResponse;
import EhcacheEntityResponse.IteratorBatch;
import PassiveReplicationMessage.ChainReplicationMessage;
import ServerStoreOpMessage.AppendMessage;
import ServerStoreOpMessage.GetMessage;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.clustered.common.PoolAllocation;
import org.ehcache.clustered.common.PoolAllocation.Dedicated;
import org.ehcache.clustered.common.PoolAllocation.Shared;
import org.ehcache.clustered.common.ServerSideConfiguration;
import org.ehcache.clustered.common.internal.ServerStoreConfiguration;
import org.ehcache.clustered.common.internal.exceptions.InvalidOperationException;
import org.ehcache.clustered.common.internal.exceptions.InvalidServerStoreConfigurationException;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityMessage;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityResponse;
import org.ehcache.clustered.common.internal.messages.ServerStoreOpMessage;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.common.internal.store.ClusterTierEntityConfiguration;
import org.ehcache.clustered.server.EhcacheStateServiceImpl;
import org.ehcache.clustered.server.KeySegmentMapper;
import org.ehcache.clustered.server.ServerSideServerStore;
import org.ehcache.clustered.server.ServerStoreEvictionListener;
import org.ehcache.clustered.server.TestClientDescriptor;
import org.ehcache.clustered.server.TestInvokeContext;
import org.ehcache.clustered.server.internal.messages.EhcacheDataSyncMessage;
import org.ehcache.clustered.server.internal.messages.PassiveReplicationMessage;
import org.ehcache.clustered.server.state.EhcacheStateService;
import org.ehcache.clustered.server.state.InvalidationTracker;
import org.ehcache.clustered.server.store.ClusterTierActiveEntity.InvalidationHolder;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.CombinableMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.terracotta.client.message.tracker.OOOMessageHandlerConfiguration;
import org.terracotta.entity.ClientCommunicator;
import org.terracotta.entity.ClientDescriptor;
import org.terracotta.entity.ConfigurationException;
import org.terracotta.entity.EntityMessage;
import org.terracotta.entity.EntityResponse;
import org.terracotta.entity.IEntityMessenger;
import org.terracotta.entity.PassiveSynchronizationChannel;
import org.terracotta.entity.ServiceConfiguration;
import org.terracotta.entity.ServiceRegistry;
import org.terracotta.management.service.monitoring.EntityManagementRegistryConfiguration;
import org.terracotta.offheapresource.OffHeapResource;
import org.terracotta.offheapresource.OffHeapResourceIdentifier;
import org.terracotta.offheapresource.OffHeapResources;
import org.terracotta.offheapstore.util.MemoryUnit;

import static org.ehcache.clustered.Matchers.hasPayloads;


public class ClusterTierActiveEntityTest {
    private static final KeySegmentMapper DEFAULT_MAPPER = new KeySegmentMapper(16);

    private String defaultStoreName = "store";

    private String defaultResource = "default";

    private String defaultSharedPool = "defaultShared";

    private String identifier = "identifier";

    private ClusterTierActiveEntityTest.OffHeapIdentifierRegistry defaultRegistry;

    private ServerStoreConfiguration defaultStoreConfiguration;

    private ClusterTierEntityConfiguration defaultConfiguration;

    @Test(expected = ConfigurationException.class)
    public void testConfigNull() throws Exception {
        new ClusterTierActiveEntity(ClusterTierActiveEntityTest.mock(ServiceRegistry.class), null, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
    }

    @Test
    public void testConnected() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        ClientDescriptor client = new TestClientDescriptor();
        activeEntity.connected(client);
        Set<ClientDescriptor> connectedClients = activeEntity.getConnectedClients();
        Assert.assertThat(connectedClients, Matchers.hasSize(1));
        Assert.assertThat(connectedClients, Matchers.hasItem(client));
    }

    @Test
    public void testConnectedAgain() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        ClientDescriptor client = new TestClientDescriptor();
        activeEntity.connected(client);
        activeEntity.connected(client);
        Set<ClientDescriptor> connectedClients = activeEntity.getConnectedClients();
        Assert.assertThat(connectedClients, Matchers.hasSize(1));
        Assert.assertThat(connectedClients, Matchers.hasItem(client));
    }

    @Test
    public void testConnectedSecond() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        ClientDescriptor client1 = new TestClientDescriptor();
        activeEntity.connected(client1);
        ClientDescriptor client2 = new TestClientDescriptor();
        activeEntity.connected(client2);
        Set<ClientDescriptor> connectedClients = activeEntity.getConnectedClients();
        Assert.assertThat(connectedClients, Matchers.hasSize(2));
        Assert.assertThat(connectedClients, Matchers.hasItems(client1, client2));
    }

    @Test
    public void testDisconnectedNotConnected() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        ClientDescriptor client1 = new TestClientDescriptor();
        activeEntity.disconnected(client1);
        // Not expected to fail ...
    }

    /**
     * Ensures the disconnect of a connected client is properly tracked.
     */
    @Test
    public void testDisconnected() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        ClientDescriptor client1 = new TestClientDescriptor();
        activeEntity.connected(client1);
        activeEntity.disconnected(client1);
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.hasSize(0));
    }

    /**
     * Ensures the disconnect of a connected client is properly tracked and does not affect others.
     */
    @Test
    public void testDisconnectedSecond() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        ClientDescriptor client1 = new TestClientDescriptor();
        activeEntity.connected(client1);
        ClientDescriptor client2 = new TestClientDescriptor();
        activeEntity.connected(client2);
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.hasSize(2));
        activeEntity.disconnected(client1);
        Set<ClientDescriptor> connectedClients = activeEntity.getConnectedClients();
        Assert.assertThat(connectedClients, Matchers.hasSize(1));
        Assert.assertThat(connectedClients, Matchers.hasItem(client2));
    }

    @Test
    public void testLoadExistingRegistersEvictionListener() throws Exception {
        EhcacheStateService stateService = ClusterTierActiveEntityTest.mock(EhcacheStateService.class);
        ServerSideServerStore store = ClusterTierActiveEntityTest.mock(ServerSideServerStore.class);
        Mockito.when(stateService.loadStore(ArgumentMatchers.eq(defaultStoreName), ArgumentMatchers.any())).thenReturn(store);
        IEntityMessenger<EhcacheEntityMessage, EhcacheEntityResponse> entityMessenger = ClusterTierActiveEntityTest.mock(IEntityMessenger.class);
        ServiceRegistry registry = getCustomMockedServiceRegistry(stateService, null, entityMessenger, null, null);
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(registry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.loadExisting();
        Mockito.verify(store).setEvictionListener(ArgumentMatchers.any(ServerStoreEvictionListener.class));
    }

    @Test
    public void testAppendInvalidationAcksTakenIntoAccount() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context1 = new TestInvokeContext();
        TestInvokeContext context2 = new TestInvokeContext();
        TestInvokeContext context3 = new TestInvokeContext();
        activeEntity.connected(context1.getClientDescriptor());
        activeEntity.connected(context2.getClientDescriptor());
        activeEntity.connected(context3.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context1, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context3, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        // perform an append
        Assert.assertThat(activeEntity.invokeActive(context1, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L))), succeeds());
        // assert that an invalidation request is pending
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(1));
        InvalidationHolder invalidationHolder = activeEntity.getClientsWaitingForInvalidation().values().iterator().next();
        Assert.assertThat(invalidationHolder.clientDescriptorWaitingForInvalidation, Matchers.is(context1.getClientDescriptor()));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate.size(), Matchers.is(2));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate, Matchers.containsInAnyOrder(context2.getClientDescriptor(), context3.getClientDescriptor()));
        // client 2 acks
        Assert.assertThat(activeEntity.invokeActive(context2, new ServerStoreOpMessage.ClientInvalidationAck(1L, activeEntity.getClientsWaitingForInvalidation().keySet().iterator().next())), succeeds());
        // assert that client 2 is not waited for anymore
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(1));
        invalidationHolder = activeEntity.getClientsWaitingForInvalidation().values().iterator().next();
        Assert.assertThat(invalidationHolder.clientDescriptorWaitingForInvalidation, Matchers.is(context1.getClientDescriptor()));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate.size(), Matchers.is(1));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate, Matchers.contains(context3.getClientDescriptor()));
        // client 3 acks
        Assert.assertThat(activeEntity.invokeActive(context3, new ServerStoreOpMessage.ClientInvalidationAck(1L, activeEntity.getClientsWaitingForInvalidation().keySet().iterator().next())), succeeds());
        // assert that the invalidation request is done since all clients disconnected
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(0));
    }

    @Test
    public void testClearInvalidationAcksTakenIntoAccount() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context1 = new TestInvokeContext();
        TestInvokeContext context2 = new TestInvokeContext();
        TestInvokeContext context3 = new TestInvokeContext();
        activeEntity.connected(context1.getClientDescriptor());
        activeEntity.connected(context2.getClientDescriptor());
        activeEntity.connected(context3.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context1, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context3, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        // perform a clear
        Assert.assertThat(activeEntity.invokeActive(context1, new ServerStoreOpMessage.ClearMessage()), succeeds());
        // assert that an invalidation request is pending
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(1));
        InvalidationHolder invalidationHolder = activeEntity.getClientsWaitingForInvalidation().values().iterator().next();
        Assert.assertThat(invalidationHolder.clientDescriptorWaitingForInvalidation, Matchers.is(context1.getClientDescriptor()));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate.size(), Matchers.is(2));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate, Matchers.containsInAnyOrder(context2.getClientDescriptor(), context3.getClientDescriptor()));
        // client 2 acks
        Assert.assertThat(activeEntity.invokeActive(context2, new ServerStoreOpMessage.ClientInvalidationAllAck(activeEntity.getClientsWaitingForInvalidation().keySet().iterator().next())), succeeds());
        // assert that client 2 is not waited for anymore
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(1));
        invalidationHolder = activeEntity.getClientsWaitingForInvalidation().values().iterator().next();
        Assert.assertThat(invalidationHolder.clientDescriptorWaitingForInvalidation, Matchers.is(context1.getClientDescriptor()));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate.size(), Matchers.is(1));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate, Matchers.contains(context3.getClientDescriptor()));
        // client 3 acks
        Assert.assertThat(activeEntity.invokeActive(context3, new ServerStoreOpMessage.ClientInvalidationAllAck(activeEntity.getClientsWaitingForInvalidation().keySet().iterator().next())), succeeds());
        // assert that the invalidation request is done since all clients disconnected
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(0));
    }

    @Test
    public void testAppendInvalidationDisconnectionOfInvalidatingClientsTakenIntoAccount() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context1 = new TestInvokeContext();
        TestInvokeContext context2 = new TestInvokeContext();
        TestInvokeContext context3 = new TestInvokeContext();
        activeEntity.connected(context1.getClientDescriptor());
        activeEntity.connected(context2.getClientDescriptor());
        activeEntity.connected(context3.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context1, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context3, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        // perform an append
        Assert.assertThat(activeEntity.invokeActive(context1, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L))), succeeds());
        // disconnect client2
        activeEntity.disconnected(context2.getClientDescriptor());
        // assert that client 2 is not waited for anymore
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(1));
        InvalidationHolder invalidationHolder = activeEntity.getClientsWaitingForInvalidation().values().iterator().next();
        Assert.assertThat(invalidationHolder.clientDescriptorWaitingForInvalidation, Matchers.is(context1.getClientDescriptor()));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate.size(), Matchers.is(1));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate, Matchers.contains(context3.getClientDescriptor()));
        // disconnect client3
        activeEntity.disconnected(context3.getClientDescriptor());
        // assert that the invalidation request is done since all clients disconnected
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(0));
    }

    @Test
    public void testClearInvalidationDisconnectionOfInvalidatingClientsTakenIntoAccount() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context1 = new TestInvokeContext();
        TestInvokeContext context2 = new TestInvokeContext();
        TestInvokeContext context3 = new TestInvokeContext();
        activeEntity.connected(context1.getClientDescriptor());
        activeEntity.connected(context2.getClientDescriptor());
        activeEntity.connected(context3.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context1, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context3, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        // perform an append
        Assert.assertThat(activeEntity.invokeActive(context1, new ServerStoreOpMessage.ClearMessage()), succeeds());
        // disconnect client2
        activeEntity.disconnected(context2.getClientDescriptor());
        // assert that client 2 is not waited for anymore
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(1));
        InvalidationHolder invalidationHolder = activeEntity.getClientsWaitingForInvalidation().values().iterator().next();
        Assert.assertThat(invalidationHolder.clientDescriptorWaitingForInvalidation, Matchers.is(context1.getClientDescriptor()));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate.size(), Matchers.is(1));
        Assert.assertThat(invalidationHolder.clientsHavingToInvalidate, Matchers.contains(context3.getClientDescriptor()));
        // disconnect client3
        activeEntity.disconnected(context3.getClientDescriptor());
        // assert that the invalidation request is done since all clients disconnected
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(0));
    }

    @Test
    public void testAppendInvalidationDisconnectionOfBlockingClientTakenIntoAccount() throws Exception {
        ServerStoreConfiguration serverStoreConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().dedicated(defaultResource, 4, MemoryUnit.MEGABYTES).consistency(STRONG).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, serverStoreConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context1 = new TestInvokeContext();
        TestInvokeContext context2 = new TestInvokeContext();
        TestInvokeContext context3 = new TestInvokeContext();
        activeEntity.connected(context1.getClientDescriptor());
        activeEntity.connected(context2.getClientDescriptor());
        activeEntity.connected(context3.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context1, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, serverStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, serverStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context3, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, serverStoreConfiguration)), succeeds());
        // perform an append
        Assert.assertThat(activeEntity.invokeActive(context1, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L))), succeeds());
        // disconnect client1
        activeEntity.disconnected(context1.getClientDescriptor());
        // assert that the invalidation request is done since the originating client disconnected
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(0));
    }

    @Test
    public void testClearInvalidationDisconnectionOfBlockingClientTakenIntoAccount() throws Exception {
        ServerStoreConfiguration serverStoreConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().dedicated(defaultResource, 4, MemoryUnit.MEGABYTES).consistency(STRONG).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, serverStoreConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context1 = new TestInvokeContext();
        TestInvokeContext context2 = new TestInvokeContext();
        TestInvokeContext context3 = new TestInvokeContext();
        activeEntity.connected(context1.getClientDescriptor());
        activeEntity.connected(context2.getClientDescriptor());
        activeEntity.connected(context3.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context1, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, serverStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, serverStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context3, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, serverStoreConfiguration)), succeeds());
        // perform an append
        Assert.assertThat(activeEntity.invokeActive(context1, new ServerStoreOpMessage.ClearMessage()), succeeds());
        // disconnect client1
        activeEntity.disconnected(context1.getClientDescriptor());
        // assert that the invalidation request is done since the originating client disconnected
        Assert.assertThat(activeEntity.getClientsWaitingForInvalidation().size(), Matchers.is(0));
    }

    @Test
    public void testWithAttachmentSucceedsInvokingServerStoreOperation() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        // attach to the store
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L))), succeeds());
        EhcacheEntityResponse response = activeEntity.invokeActive(context, new ServerStoreOpMessage.GetMessage(1L));
        Assert.assertThat(response, Matchers.instanceOf(GetResponse.class));
        EhcacheEntityResponse.GetResponse getResponse = ((EhcacheEntityResponse.GetResponse) (response));
        Assert.assertThat(getResponse.getChain().isEmpty(), Matchers.is(false));
    }

    @Test
    public void testCreateDedicatedServerStore() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        Assert.assertThat(defaultRegistry.getStoreManagerService().getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(defaultStoreName));
        Assert.assertThat(defaultRegistry.getResource(defaultResource).getUsed(), Matchers.is(MemoryUnit.MEGABYTES.toBytes(1L)));
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.empty());
        Assert.assertThat(defaultRegistry.getStoreManagerService().getStores(), Matchers.containsInAnyOrder(defaultStoreName));
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.contains(context.getClientDescriptor()));
        /* Ensure the dedicated resource pool remains after client disconnect. */
        activeEntity.disconnected(context.getClientDescriptor());
        Assert.assertThat(defaultRegistry.getStoreManagerService().getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(defaultStoreName));
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.empty());
        Assert.assertThat(defaultRegistry.getStoreManagerService().getStores(), Matchers.containsInAnyOrder(defaultStoreName));
    }

    @Test
    public void testCreateDedicatedServerStoreExisting() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        ClusterTierActiveEntity otherEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        try {
            otherEntity.createNew();
            Assert.fail("Duplicate creation should fail with an exception");
        } catch (ConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("already exists"));
        }
    }

    @Test
    public void testValidateDedicatedServerStore() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        TestInvokeContext context2 = new TestInvokeContext();
        activeEntity.connected(context2.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context2, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Assert.assertThat(defaultRegistry.getStoreManagerService().getDedicatedResourcePoolIds(), Matchers.containsInAnyOrder(defaultStoreName));
        Assert.assertThat(defaultRegistry.getResource(defaultResource).getUsed(), Matchers.is(MemoryUnit.MEGABYTES.toBytes(1L)));
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.hasSize(2));
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.containsInAnyOrder(context.getClientDescriptor(), context2.getClientDescriptor()));
        Assert.assertThat(defaultRegistry.getStoreManagerService().getStores(), Matchers.contains(defaultStoreName));
    }

    @Test
    public void testValidateDedicatedServerStoreBad() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().dedicated(defaultResource, 8, MemoryUnit.MEGABYTES).build())), failsWith(Matchers.instanceOf(InvalidServerStoreConfigurationException.class)));
    }

    @Test
    public void testValidateUnknown() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().unknown().build())), succeeds());
    }

    @Test
    public void testCreateSharedServerStore() throws Exception {
        defaultRegistry.addSharedPool(defaultSharedPool, MemoryUnit.MEGABYTES.toBytes(2), defaultResource);
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().shared(defaultSharedPool).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        Assert.assertThat(defaultRegistry.getStoreManagerService().getStores(), Matchers.containsInAnyOrder(defaultStoreName));
        Assert.assertThat(defaultRegistry.getStoreManagerService().getSharedResourcePoolIds(), Matchers.containsInAnyOrder(defaultSharedPool));
        Assert.assertThat(defaultRegistry.getStoreManagerService().getDedicatedResourcePoolIds(), Matchers.empty());
        Assert.assertThat(defaultRegistry.getResource(defaultResource).getUsed(), Matchers.is(MemoryUnit.MEGABYTES.toBytes(2L)));
    }

    @Test
    public void testCreateSharedServerStoreExisting() throws Exception {
        defaultRegistry.addSharedPool(defaultSharedPool, MemoryUnit.MEGABYTES.toBytes(2), defaultResource);
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().shared(defaultSharedPool).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        ClusterTierActiveEntity otherEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        try {
            otherEntity.createNew();
            Assert.fail("Duplicate creation should fail with an exception");
        } catch (ConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("already exists"));
        }
    }

    @Test
    public void testValidateSharedServerStore() throws Exception {
        defaultRegistry.addSharedPool(defaultSharedPool, MemoryUnit.MEGABYTES.toBytes(2), defaultResource);
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().shared(defaultSharedPool).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, storeConfiguration)), succeeds());
        Assert.assertThat(activeEntity.getConnectedClients(), Matchers.contains(context.getClientDescriptor()));
    }

    @Test
    public void testValidateServerStore_DedicatedStoresDifferentSizes() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().dedicated(defaultResource, 2, MemoryUnit.MEGABYTES).build();
        String expectedMessageContent = ((("Existing ServerStore configuration is not compatible with the desired configuration: " + ("\n\t" + "resourcePoolType existing: ")) + (defaultStoreConfiguration.getPoolAllocation())) + ", desired: ") + (storeConfiguration.getPoolAllocation());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, storeConfiguration)), failsWith(CombinableMatcher.both(IsInstanceOf.any(InvalidServerStoreConfigurationException.class)).and(withMessage(Matchers.containsString(expectedMessageContent)))));
    }

    @Test
    public void testValidateServerStore_DedicatedStoreResourceNamesDifferent() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().dedicated("otherResource", 1, MemoryUnit.MEGABYTES).build();
        String expectedMessageContent = ((("Existing ServerStore configuration is not compatible with the desired configuration: " + ("\n\t" + "resourcePoolType existing: ")) + (defaultStoreConfiguration.getPoolAllocation())) + ", desired: ") + (storeConfiguration.getPoolAllocation());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, storeConfiguration)), failsWith(CombinableMatcher.both(IsInstanceOf.any(InvalidServerStoreConfigurationException.class)).and(withMessage(Matchers.containsString(expectedMessageContent)))));
    }

    @Test
    public void testValidateServerStore_DifferentSharedPools() throws Exception {
        defaultRegistry.addSharedPool(defaultSharedPool, MemoryUnit.MEGABYTES.toBytes(2), defaultResource);
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().shared(defaultSharedPool).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        ServerStoreConfiguration otherConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().shared("other").build();
        String expectedMessageContent = ((("Existing ServerStore configuration is not compatible with the desired configuration: " + ("\n\t" + "resourcePoolType existing: ")) + (storeConfiguration.getPoolAllocation())) + ", desired: ") + (otherConfiguration.getPoolAllocation());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, otherConfiguration)), failsWith(CombinableMatcher.both(IsInstanceOf.any(InvalidServerStoreConfigurationException.class)).and(withMessage(Matchers.containsString(expectedMessageContent)))));
    }

    @Test
    public void testDestroyServerStore() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        activeEntity.destroy();
        Assert.assertThat(defaultRegistry.getResource(defaultResource).getUsed(), Matchers.is(0L));
        Assert.assertThat(defaultRegistry.getStoreManagerService().getStores(), Matchers.empty());
        Assert.assertThat(defaultRegistry.getStoreManagerService().getDedicatedResourcePoolIds(), Matchers.empty());
    }

    /**
     * Ensures shared pool and store (cache) name spaces are independent.
     * The cache alias is used as the name for a {@code ServerStore} instance; this name can be
     * the same as, but is independent of, the shared pool name.  The
     */
    @Test
    public void testSharedPoolCacheNameCollision() throws Exception {
        defaultRegistry.addSharedPool(defaultStoreName, MemoryUnit.MEGABYTES.toBytes(2), defaultResource);
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        Assert.assertThat(defaultRegistry.getStoreManagerService().getSharedResourcePoolIds(), Matchers.contains(defaultStoreName));
        Assert.assertThat(defaultRegistry.getStoreManagerService().getDedicatedResourcePoolIds(), Matchers.contains(defaultStoreName));
        Assert.assertThat(defaultRegistry.getStoreManagerService().getStores(), Matchers.containsInAnyOrder(defaultStoreName));
    }

    @Test
    public void testCreateNonExistentSharedPool() throws Exception {
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().shared(defaultSharedPool).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        try {
            activeEntity.createNew();
            Assert.fail("Creation with non-existent shared pool should have failed");
        } catch (ConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("undefined"));
        }
    }

    @Test
    public void testCreateUnknownServerResource() throws Exception {
        ServerStoreConfiguration storeConfiguration = new ClusterTierActiveEntityTest.ServerStoreConfigBuilder().dedicated("unknown", 2, MemoryUnit.MEGABYTES).build();
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, new ClusterTierEntityConfiguration(identifier, defaultStoreName, storeConfiguration), ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        try {
            activeEntity.createNew();
            Assert.fail("Creation with non-existent shared pool should have failed");
        } catch (ConfigurationException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Non-existent server side resource"));
        }
    }

    @Test
    public void testSyncToPassiveNoData() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        @SuppressWarnings("unchecked")
        PassiveSynchronizationChannel<EhcacheEntityMessage> syncChannel = ClusterTierActiveEntityTest.mock(PassiveSynchronizationChannel.class);
        activeEntity.synchronizeKeyToPassive(syncChannel, 3);
        Mockito.verifyZeroInteractions(syncChannel);
    }

    @Test
    public void testSyncToPassiveBatchedByDefault() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        ByteBuffer payload = ByteBuffer.allocate(512);
        // Put keys that maps to the same concurrency key
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, payload)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage((-2L), payload)), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(17L, payload)), succeeds());
        @SuppressWarnings("unchecked")
        PassiveSynchronizationChannel<EhcacheEntityMessage> syncChannel = ClusterTierActiveEntityTest.mock(PassiveSynchronizationChannel.class);
        activeEntity.synchronizeKeyToPassive(syncChannel, 3);
        Mockito.verify(syncChannel).synchronizeToPassive(ArgumentMatchers.any(EhcacheDataSyncMessage.class));
    }

    @Test
    public void testDataSyncToPassiveCustomBatchSize() throws Exception {
        System.setProperty(SYNC_DATA_SIZE_PROP, "512");
        try {
            prepareAndRunActiveEntityForPassiveSync(( activeEntity, concurrencyKey) -> {
                @SuppressWarnings("unchecked")
                PassiveSynchronizationChannel<EhcacheEntityMessage> syncChannel = ClusterTierActiveEntityTest.mock(PassiveSynchronizationChannel.class);
                activeEntity.synchronizeKeyToPassive(syncChannel, concurrencyKey);
                Mockito.verify(syncChannel, Mockito.atLeast(2)).synchronizeToPassive(ArgumentMatchers.any(EhcacheDataSyncMessage.class));
            });
        } finally {
            System.clearProperty(SYNC_DATA_SIZE_PROP);
        }
    }

    @Test
    public void testDataSyncToPassiveCustomGets() throws Exception {
        System.setProperty(SYNC_DATA_GETS_PROP, "2");
        try {
            prepareAndRunActiveEntityForPassiveSync(( activeEntity, concurrencyKey) -> {
                @SuppressWarnings("unchecked")
                PassiveSynchronizationChannel<EhcacheEntityMessage> syncChannel = ClusterTierActiveEntityTest.mock(PassiveSynchronizationChannel.class);
                activeEntity.synchronizeKeyToPassive(syncChannel, concurrencyKey);
                Mockito.verify(syncChannel, Mockito.atLeast(2)).synchronizeToPassive(ArgumentMatchers.any(EhcacheDataSyncMessage.class));
            });
        } finally {
            System.clearProperty(SYNC_DATA_GETS_PROP);
        }
    }

    @Test
    public void testDataSyncToPassiveException() throws Exception {
        System.setProperty(SYNC_DATA_GETS_PROP, "1");
        try {
            prepareAndRunActiveEntityForPassiveSync(( activeEntity, concurrencyKey) -> {
                @SuppressWarnings("unchecked")
                PassiveSynchronizationChannel<EhcacheEntityMessage> syncChannel = ClusterTierActiveEntityTest.mock(PassiveSynchronizationChannel.class);
                activeEntity.destroy();
                try {
                    activeEntity.synchronizeKeyToPassive(syncChannel, concurrencyKey);
                    Assert.fail("Destroyed entity not expected to sync");
                } catch (RuntimeException e) {
                    Assert.assertThat(e.getCause(), Matchers.instanceOf(ExecutionException.class));
                }
            });
        } finally {
            System.clearProperty(SYNC_DATA_GETS_PROP);
        }
    }

    @Test
    public void testLoadExistingRecoversInflightInvalidationsForEventualCache() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        EhcacheStateServiceImpl ehcacheStateService = defaultRegistry.getStoreManagerService();
        ehcacheStateService.createStore(defaultStoreName, defaultStoreConfiguration, false);// Passive would have done this before failover

        InvalidationTracker invalidationTracker = ehcacheStateService.getInvalidationTracker(defaultStoreName);
        Random random = new Random();
        random.ints(0, 100).limit(10).forEach(invalidationTracker::trackHashInvalidation);
        activeEntity.loadExisting();
        Assert.assertThat(activeEntity.getInflightInvalidations().isEmpty(), Matchers.is(false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReplicationMessageAndOriginalServerStoreOpMessageHasSameConcurrency() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        IEntityMessenger<EhcacheEntityMessage, EhcacheEntityResponse> entityMessenger = defaultRegistry.getEntityMessenger();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        Mockito.reset(entityMessenger);
        EhcacheEntityMessage getAndAppend = new ServerStoreOpMessage.GetAndAppendMessage(1L, ChainUtils.createPayload(1L));
        activeEntity.invokeActive(context, getAndAppend);
        ArgumentCaptor<PassiveReplicationMessage.ChainReplicationMessage> captor = ArgumentCaptor.forClass(ChainReplicationMessage.class);
        Mockito.verify(entityMessenger).messageSelfAndDeferRetirement(ArgumentMatchers.isNotNull(), captor.capture());
        PassiveReplicationMessage.ChainReplicationMessage replicatedMessage = captor.getValue();
        Assert.assertThat(replicatedMessage.concurrencyKey(), Matchers.is(concurrencyKey()));
    }

    @Test
    public void testInvalidMessageThrowsError() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        try {
            activeEntity.invokeActive(context, new InvalidMessage());
            Assert.fail("Invalid message should result in AssertionError");
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Unsupported"));
        }
    }

    @Test
    public void testActiveTracksMessageDuplication() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        activeEntity.createNew();
        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        ServerStoreOpMessage.AppendMessage message = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        activeEntity.invokeActive(context, message);
        // create another message that has the same message ID
        message = new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(1L));
        activeEntity.invokeActive(context, message);// this invoke should be rejected due to duplicate message id

        ServerStoreOpMessage.GetMessage getMessage = new ServerStoreOpMessage.GetMessage(2L);
        EhcacheEntityResponse.GetResponse response = ((EhcacheEntityResponse.GetResponse) (activeEntity.invokeActive(context, getMessage)));
        Assert.assertThat(response.getChain().isEmpty(), Matchers.is(false));
    }

    @Test
    public void testActiveMessageTracking() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        EhcacheStateServiceImpl ehcacheStateService = defaultRegistry.getStoreManagerService();
        ehcacheStateService.createStore(defaultStoreName, defaultStoreConfiguration, false);// hack to enable message tracking on active

        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        context.incrementCurrentTransactionId();
        ServerStoreOpMessage.AppendMessage message = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        EhcacheEntityResponse expected = activeEntity.invokeActive(context, message);
        // create another message that has the same message ID
        message = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        EhcacheEntityResponse actual = activeEntity.invokeActive(context, message);// this invoke should be rejected due to duplicate message id

        Assert.assertThat(actual, Matchers.sameInstance(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShortIterationIsNotTracked() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        EhcacheStateServiceImpl ehcacheStateService = defaultRegistry.getStoreManagerService();
        ehcacheStateService.createStore(defaultStoreName, defaultStoreConfiguration, false);// hack to enable message tracking on active

        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(2L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(3L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(4L)));
        EhcacheEntityResponse.IteratorBatch iteratorBatch = ((EhcacheEntityResponse.IteratorBatch) (activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorOpenMessage(Integer.MAX_VALUE))));
        Assert.assertThat(iteratorBatch.isLast(), Matchers.is(true));
        Assert.assertThat(iteratorBatch.getChains(), Matchers.containsInAnyOrder(hasPayloads(1L, 2L), hasPayloads(3L, 4L)));
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorAdvanceMessage(iteratorBatch.getIdentity(), Integer.MAX_VALUE)), failsWith(Matchers.instanceOf(InvalidOperationException.class)));
    }

    @Test
    public void testLongIteration() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        EhcacheStateServiceImpl ehcacheStateService = defaultRegistry.getStoreManagerService();
        ehcacheStateService.createStore(defaultStoreName, defaultStoreConfiguration, false);// hack to enable message tracking on active

        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(2L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(3L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(4L)));
        EhcacheEntityResponse.IteratorBatch batchOne = ((EhcacheEntityResponse.IteratorBatch) (activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorOpenMessage(1))));
        Matcher<Chain> chainOne = org.ehcache.clustered.Matchers.hasPayloads(1L, 2L);
        Matcher<Chain> chainTwo = org.ehcache.clustered.Matchers.hasPayloads(3L, 4L);
        Assert.assertThat(batchOne.isLast(), Matchers.is(false));
        Assert.assertThat(batchOne.getChains(), CombinableMatcher.either(Matchers.contains(chainOne)).or(Matchers.contains(chainTwo)));
        EhcacheEntityResponse.IteratorBatch batchTwo = ((EhcacheEntityResponse.IteratorBatch) (activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorAdvanceMessage(batchOne.getIdentity(), Integer.MAX_VALUE))));
        Assert.assertThat(batchTwo.isLast(), Matchers.is(true));
        if (Matchers.contains(chainOne).matches(batchOne.getChains())) {
            Assert.assertThat(batchTwo.getChains(), Matchers.contains(chainTwo));
        } else {
            Assert.assertThat(batchTwo.getChains(), Matchers.contains(chainOne));
        }
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorAdvanceMessage(batchOne.getIdentity(), Integer.MAX_VALUE)), failsWith(Matchers.instanceOf(InvalidOperationException.class)));
    }

    @Test
    public void testExplicitIteratorClose() throws Exception {
        ClusterTierActiveEntity activeEntity = new ClusterTierActiveEntity(defaultRegistry, defaultConfiguration, ClusterTierActiveEntityTest.DEFAULT_MAPPER);
        EhcacheStateServiceImpl ehcacheStateService = defaultRegistry.getStoreManagerService();
        ehcacheStateService.createStore(defaultStoreName, defaultStoreConfiguration, false);// hack to enable message tracking on active

        TestInvokeContext context = new TestInvokeContext();
        activeEntity.connected(context.getClientDescriptor());
        Assert.assertThat(activeEntity.invokeActive(context, new org.ehcache.clustered.common.internal.messages.LifecycleMessage.ValidateServerStore(defaultStoreName, defaultStoreConfiguration)), succeeds());
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(2L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(3L)));
        activeEntity.invokeActive(context, new ServerStoreOpMessage.AppendMessage(2L, ChainUtils.createPayload(4L)));
        EhcacheEntityResponse.IteratorBatch batchOne = ((EhcacheEntityResponse.IteratorBatch) (activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorOpenMessage(1))));
        Matcher<Chain> chainOne = org.ehcache.clustered.Matchers.hasPayloads(1L, 2L);
        Matcher<Chain> chainTwo = org.ehcache.clustered.Matchers.hasPayloads(3L, 4L);
        Assert.assertThat(batchOne.isLast(), Matchers.is(false));
        Assert.assertThat(batchOne.getChains(), CombinableMatcher.either(Matchers.contains(chainOne)).or(Matchers.contains(chainTwo)));
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorCloseMessage(batchOne.getIdentity())), succeeds());
        Assert.assertThat(activeEntity.invokeActive(context, new ServerStoreOpMessage.IteratorAdvanceMessage(batchOne.getIdentity(), Integer.MAX_VALUE)), failsWith(Matchers.instanceOf(InvalidOperationException.class)));
    }

    /**
     * Builder for {@link ServerStoreConfiguration} instances.
     */
    private static final class ServerStoreConfigBuilder {
        private PoolAllocation poolAllocation;

        private String storedKeyType = "java.lang.Long";

        private String storedValueType = "java.lang.String";

        private String keySerializerType;

        private String valueSerializerType;

        private Consistency consistency = Consistency.EVENTUAL;

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder consistency(Consistency consistency) {
            this.consistency = consistency;
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder dedicated(String resourceName, int size, MemoryUnit unit) {
            this.poolAllocation = new Dedicated(resourceName, unit.toBytes(size));
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder shared(String resourcePoolName) {
            this.poolAllocation = new Shared(resourcePoolName);
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder unknown() {
            this.poolAllocation = new PoolAllocation.Unknown();
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder setStoredKeyType(Class<?> storedKeyType) {
            this.storedKeyType = storedKeyType.getName();
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder setStoredValueType(Class<?> storedValueType) {
            this.storedValueType = storedValueType.getName();
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder setKeySerializerType(Class<?> keySerializerType) {
            this.keySerializerType = keySerializerType.getName();
            return this;
        }

        ClusterTierActiveEntityTest.ServerStoreConfigBuilder setValueSerializerType(Class<?> valueSerializerType) {
            this.valueSerializerType = valueSerializerType.getName();
            return this;
        }

        ServerStoreConfiguration build() {
            return new ServerStoreConfiguration(poolAllocation, storedKeyType, storedValueType, keySerializerType, valueSerializerType, consistency, false, false);
        }
    }

    /**
     * Provides a {@link ServiceRegistry} for off-heap resources.  This is a "server-side" object.
     */
    private static final class OffHeapIdentifierRegistry implements ServiceRegistry {
        private final long offHeapSize;

        private final String defaultResource;

        private EhcacheStateServiceImpl storeManagerService;

        private IEntityMessenger<EhcacheEntityMessage, EhcacheEntityResponse> entityMessenger;

        private ClientCommunicator clientCommunicator;

        private final Map<OffHeapResourceIdentifier, ClusterTierActiveEntityTest.TestOffHeapResource> pools = new HashMap<>();

        private final Map<String, ServerSideConfiguration.Pool> sharedPools = new HashMap<>();

        /**
         * Instantiate an "open" {@code ServiceRegistry}.  Using this constructor creates a
         * registry that creates {@code OffHeapResourceIdentifier} entries as they are
         * referenced.
         */
        private OffHeapIdentifierRegistry(String defaultResource) {
            this.defaultResource = defaultResource;
            this.offHeapSize = 0;
        }

        /**
         * Instantiate a "closed" {@code ServiceRegistry}.  Using this constructor creates a
         * registry that only returns {@code OffHeapResourceIdentifier} entries supplied
         * through the {@link #addResource} method.
         */
        private OffHeapIdentifierRegistry() {
            this(null);
        }

        private void addSharedPool(String name, long size, String resourceName) {
            sharedPools.put(name, new ServerSideConfiguration.Pool(size, resourceName));
        }

        /**
         * Adds an off-heap resource of the given name to this registry.
         *
         * @param name
         * 		the name of the resource
         * @param offHeapSize
         * 		the off-heap size
         * @param unit
         * 		the size unit type
         * @return {@code this} {@code OffHeapIdentifierRegistry}
         */
        private ClusterTierActiveEntityTest.OffHeapIdentifierRegistry addResource(String name, int offHeapSize, MemoryUnit unit) {
            this.pools.put(OffHeapResourceIdentifier.identifier(name), new ClusterTierActiveEntityTest.TestOffHeapResource(unit.toBytes(offHeapSize)));
            return this;
        }

        private ClusterTierActiveEntityTest.TestOffHeapResource getResource(String resourceName) {
            return this.pools.get(OffHeapResourceIdentifier.identifier(resourceName));
        }

        private EhcacheStateServiceImpl getStoreManagerService() {
            return this.storeManagerService;
        }

        private IEntityMessenger<EhcacheEntityMessage, EhcacheEntityResponse> getEntityMessenger() {
            return entityMessenger;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getService(ServiceConfiguration<T> serviceConfiguration) {
            if (serviceConfiguration.getServiceType().equals(ClientCommunicator.class)) {
                if ((this.clientCommunicator) == null) {
                    this.clientCommunicator = ClusterTierActiveEntityTest.mock(ClientCommunicator.class);
                }
                return ((T) (this.clientCommunicator));
            } else
                if (serviceConfiguration.getServiceType().equals(EhcacheStateService.class)) {
                    if ((storeManagerService) == null) {
                        this.storeManagerService = new EhcacheStateServiceImpl(new OffHeapResources() {
                            @Override
                            public Set<OffHeapResourceIdentifier> getAllIdentifiers() {
                                return pools.keySet();
                            }

                            @Override
                            public OffHeapResource getOffHeapResource(OffHeapResourceIdentifier identifier) {
                                return pools.get(identifier);
                            }
                        }, new ServerSideConfiguration(sharedPools), ClusterTierActiveEntityTest.DEFAULT_MAPPER, ( service) -> {
                        });
                        try {
                            this.storeManagerService.configure();
                        } catch (ConfigurationException e) {
                            throw new AssertionError("Test setup failed!");
                        }
                    }
                    return ((T) (this.storeManagerService));
                } else
                    if (serviceConfiguration.getServiceType().equals(IEntityMessenger.class)) {
                        if ((this.entityMessenger) == null) {
                            this.entityMessenger = ClusterTierActiveEntityTest.mock(IEntityMessenger.class);
                        }
                        return ((T) (this.entityMessenger));
                    } else
                        if (serviceConfiguration instanceof EntityManagementRegistryConfiguration) {
                            return null;
                        } else
                            if (serviceConfiguration instanceof OOOMessageHandlerConfiguration) {
                                OOOMessageHandlerConfiguration<EntityMessage, EntityResponse> oooMessageHandlerConfiguration = ((OOOMessageHandlerConfiguration) (serviceConfiguration));
                                return ((T) (new org.terracotta.client.message.tracker.OOOMessageHandlerImpl(oooMessageHandlerConfiguration.getTrackerPolicy(), oooMessageHandlerConfiguration.getSegments(), oooMessageHandlerConfiguration.getSegmentationStrategy())));
                            }




            throw new UnsupportedOperationException(("Registry.getService does not support " + (serviceConfiguration.getClass().getName())));
        }

        @Override
        public <T> Collection<T> getServices(ServiceConfiguration<T> configuration) {
            return Collections.singleton(getService(configuration));
        }
    }

    /**
     * Testing implementation of {@link OffHeapResource}.  This is a "server-side" object.
     */
    private static final class TestOffHeapResource implements OffHeapResource {
        private long capacity;

        private long used;

        private TestOffHeapResource(long capacity) {
            this.capacity = capacity;
        }

        @Override
        public boolean reserve(long size) throws IllegalArgumentException {
            if (size < 0) {
                throw new IllegalArgumentException();
            }
            if (size > (available())) {
                return false;
            } else {
                this.used += size;
                return true;
            }
        }

        @Override
        public void release(long size) throws IllegalArgumentException {
            if (size < 0) {
                throw new IllegalArgumentException();
            }
            this.used -= size;
        }

        @Override
        public long available() {
            return (this.capacity) - (this.used);
        }

        @Override
        public long capacity() {
            return capacity;
        }

        @Override
        public boolean setCapacity(long size) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported");
        }

        private long getUsed() {
            return used;
        }
    }
}

