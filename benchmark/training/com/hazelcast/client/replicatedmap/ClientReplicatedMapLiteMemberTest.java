/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.replicatedmap;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.replicatedmap.ReplicatedMapCantBeCreatedOnLiteMemberException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientReplicatedMapLiteMemberTest {
    private TestHazelcastFactory factory;

    private ClientConfig smartClientConfig;

    private ClientConfig dummyClientConfig;

    @Test
    public void testReplicatedMapIsCreatedBySmartClient() {
        testReplicatedMapCreated(2, 1, smartClientConfig);
    }

    @Test
    public void testReplicatedMapIsCreatedByDummyClient() {
        testReplicatedMapCreated(2, 1, dummyClientConfig);
    }

    @Test(expected = ReplicatedMapCantBeCreatedOnLiteMemberException.class)
    public void testReplicatedMapNotCreatedOnOnlyLiteMembersBySmartClient() {
        testReplicatedMapCreated(2, 0, smartClientConfig);
    }

    @Test(expected = ReplicatedMapCantBeCreatedOnLiteMemberException.class)
    public void testReplicatedMapNotCreatedOnOnlyLiteMembersByDummyClient() {
        testReplicatedMapCreated(2, 0, dummyClientConfig);
    }

    @Test(expected = ReplicatedMapCantBeCreatedOnLiteMemberException.class)
    public void testReplicatedMapNotCreatedOnSingleLiteMemberBySmartClient() {
        testReplicatedMapCreated(1, 0, smartClientConfig);
    }

    @Test(expected = ReplicatedMapCantBeCreatedOnLiteMemberException.class)
    public void testReplicatedMapNotCreatedOnSingleLiteMemberByDummyClient() {
        testReplicatedMapCreated(1, 0, dummyClientConfig);
    }

    @Test
    public void testReplicatedMapPutBySmartClient() {
        createNodes(3, 1);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<Object, Object> map = client.getReplicatedMap(HazelcastTestSupport.randomMapName());
        Assert.assertNull(map.put(1, 2));
    }

    @Test
    public void testReplicatedMapPutByDummyClient() throws UnknownHostException {
        List<HazelcastInstance> instances = createNodes(3, 1);
        configureDummyClientConnection(instances.get(0));
        HazelcastInstance client = factory.newHazelcastClient(dummyClientConfig);
        ReplicatedMap<Object, Object> map = client.getReplicatedMap(HazelcastTestSupport.randomMapName());
        map.put(1, 2);
    }
}

