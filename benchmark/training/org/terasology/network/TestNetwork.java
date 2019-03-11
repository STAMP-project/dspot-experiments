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
package org.terasology.network;


import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.engine.EngineTime;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.entitySystem.metadata.EventLibrary;
import org.terasology.network.exceptions.HostingFailedException;


/**
 *
 */
public class TestNetwork extends TerasologyTestingEnvironment {
    private List<NetworkSystem> netSystems = Lists.newArrayList();

    @Test
    public void testNetwork() throws Exception {
        EngineEntityManager entityManager = getEntityManager();
        EngineTime time = Mockito.mock(EngineTime.class);
        NetworkSystem server = new org.terasology.network.internal.NetworkSystemImpl(time, context);
        server.setContext(context);
        netSystems.add(server);
        server.connectToEntitySystem(entityManager, context.get(EventLibrary.class), null);
        server.host(7777, true);
        Thread.sleep(500);
        NetworkSystem client = new org.terasology.network.internal.NetworkSystemImpl(time, context);
        client.setContext(context);
        netSystems.add(client);
        client.join("localhost", 7777);
        Thread.sleep(500);
        server.shutdown();
        client.shutdown();
    }

    @Test
    public void testEntityNetworkIdChangedOnServerStart() throws HostingFailedException {
        EngineEntityManager entityManager = getEntityManager();
        NetworkComponent netComp = new NetworkComponent();
        netComp.setNetworkId(122);
        EntityRef entity = entityManager.create(netComp);
        EngineTime time = Mockito.mock(EngineTime.class);
        NetworkSystem server = new org.terasology.network.internal.NetworkSystemImpl(time, context);
        server.setContext(context);
        netSystems.add(server);
        server.connectToEntitySystem(entityManager, context.get(EventLibrary.class), null);
        server.host(7777, true);
        Assert.assertFalse((122 == (entity.getComponent(NetworkComponent.class).getNetworkId())));
        server.shutdown();
    }
}

