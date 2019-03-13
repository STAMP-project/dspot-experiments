/**
 * Copyright 2013 MovingBlocks
 *
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
 */
package org.terasology.network.internal;


import NetworkComponent.ReplicateMode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.network.NetworkComponent;


/**
 *
 */
public class NetworkOwnershipTest extends TerasologyTestingEnvironment {
    private static EngineEntityManager entityManager;

    private NetworkSystemImpl networkSystem;

    private NetClient client;

    private EntityRef clientEntity;

    @Test
    public void testClientSentNetInitialForNewNetworkEntity() {
        connectClient();
        EntityRef entity = NetworkOwnershipTest.entityManager.create(new NetworkComponent());
        networkSystem.registerNetworkEntity(entity);
        Assert.assertTrue(((entity.getComponent(NetworkComponent.class).getNetworkId()) != 0));
        Mockito.verify(client).setNetInitial(entity.getComponent(NetworkComponent.class).getNetworkId());
    }

    @Test
    public void testClientSentNetInitialForExistingNetworkEntityOnConnect() {
        EntityRef entity = NetworkOwnershipTest.entityManager.create(new NetworkComponent());
        networkSystem.registerNetworkEntity(entity);
        connectClient();
        Assert.assertTrue(((entity.getComponent(NetworkComponent.class).getNetworkId()) != 0));
        Mockito.verify(client).setNetInitial(entity.getComponent(NetworkComponent.class).getNetworkId());
    }

    @Test
    public void testClientNoInitialEntityIfNotOwnedAndReplicateToOwner() {
        connectClient();
        NetworkComponent netComp = new NetworkComponent();
        netComp.replicateMode = ReplicateMode.OWNER;
        EntityRef entity = NetworkOwnershipTest.entityManager.create(netComp);
        networkSystem.registerNetworkEntity(entity);
        Assert.assertTrue(((entity.getComponent(NetworkComponent.class).getNetworkId()) != 0));
        Mockito.verify(client, Mockito.times(0)).setNetInitial(entity.getComponent(NetworkComponent.class).getNetworkId());
    }

    @Test
    public void testClientSentInitialIfOwnedEntityRegistered() {
        connectClient();
        EntityBuilder builder = NetworkOwnershipTest.entityManager.newBuilder();
        NetworkComponent netComp = builder.addComponent(new NetworkComponent());
        netComp.replicateMode = ReplicateMode.OWNER;
        builder.setOwner(clientEntity);
        EntityRef entity = builder.build();
        networkSystem.registerNetworkEntity(entity);
        Assert.assertTrue(((entity.getComponent(NetworkComponent.class).getNetworkId()) != 0));
        Mockito.verify(client).setNetInitial(entity.getComponent(NetworkComponent.class).getNetworkId());
    }

    @Test
    public void testClientSentInitialOnlyOnce() {
        EntityBuilder builder = NetworkOwnershipTest.entityManager.newBuilder();
        NetworkComponent netComp = builder.addComponent(new NetworkComponent());
        netComp.replicateMode = ReplicateMode.OWNER;
        builder.setOwner(clientEntity);
        EntityRef entity = builder.build();
        networkSystem.registerNetworkEntity(entity);
        connectClient();
        networkSystem.updateOwnership(entity);
        Mockito.verify(client, Mockito.times(1)).setNetInitial(entity.getComponent(NetworkComponent.class).getNetworkId());
    }

    @Test
    public void testClientSentInitialForOwnershipChain() {
        NetworkComponent netCompA = new NetworkComponent();
        netCompA.replicateMode = ReplicateMode.OWNER;
        EntityRef entityA = NetworkOwnershipTest.entityManager.create(netCompA);
        EntityBuilder builder = NetworkOwnershipTest.entityManager.newBuilder();
        NetworkComponent netCompB = builder.addComponent(new NetworkComponent());
        netCompB.replicateMode = ReplicateMode.OWNER;
        builder.setOwner(entityA);
        EntityRef entityB = builder.build();
        networkSystem.registerNetworkEntity(entityA);
        networkSystem.registerNetworkEntity(entityB);
        connectClient();
        Mockito.verify(client, Mockito.times(0)).setNetInitial(entityA.getComponent(NetworkComponent.class).getNetworkId());
        Mockito.verify(client, Mockito.times(0)).setNetInitial(entityB.getComponent(NetworkComponent.class).getNetworkId());
        entityA.setOwner(clientEntity);
        networkSystem.updateOwnership(entityA);
        Mockito.verify(client, Mockito.times(1)).setNetInitial(entityA.getComponent(NetworkComponent.class).getNetworkId());
        Mockito.verify(client, Mockito.times(1)).setNetInitial(entityB.getComponent(NetworkComponent.class).getNetworkId());
    }

    @Test
    public void testClientSendInitialForRelevantOwnedItems() {
        EntityBuilder builder = NetworkOwnershipTest.entityManager.newBuilder();
        NetworkComponent netCompA = builder.addComponent(new NetworkComponent());
        netCompA.replicateMode = ReplicateMode.RELEVANT;
        builder.setOwner(clientEntity);
        EntityRef entityA = builder.build();
        networkSystem.registerNetworkEntity(entityA);
        connectClient();
        Mockito.verify(client, Mockito.times(1)).setNetInitial(entityA.getComponent(NetworkComponent.class).getNetworkId());
    }
}

