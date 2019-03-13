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
package com.hazelcast.client.map.impl.nearcache.invalidation;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.nearcache.NearCacheTestSupport;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class ClientMapInvalidationMetadataDistortionTest extends NearCacheTestSupport {
    private static final int MAP_SIZE = 100000;

    private static final String MAP_NAME = "ClientMapInvalidationMetadataDistortionTest";

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private final AtomicBoolean stopTest = new AtomicBoolean();

    @Test
    public void lostInvalidation() {
        // members are created
        Config config = createConfig().addMapConfig(createMapConfig(ClientMapInvalidationMetadataDistortionTest.MAP_NAME));
        final HazelcastInstance member = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        // map is populated form member
        final IMap<Integer, Integer> memberMap = member.getMap(ClientMapInvalidationMetadataDistortionTest.MAP_NAME);
        for (int i = 0; i < (ClientMapInvalidationMetadataDistortionTest.MAP_SIZE); i++) {
            memberMap.put(i, i);
        }
        // a new client comes
        ClientConfig clientConfig = createClientConfig().addNearCacheConfig(createNearCacheConfig(ClientMapInvalidationMetadataDistortionTest.MAP_NAME));
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        final IMap<Integer, Integer> clientMap = client.getMap(ClientMapInvalidationMetadataDistortionTest.MAP_NAME);
        Thread populateNearCache = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    for (int i = 0; i < (ClientMapInvalidationMetadataDistortionTest.MAP_SIZE); i++) {
                        clientMap.get(i);
                    }
                } 
            }
        });
        Thread distortSequence = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    distortRandomPartitionSequence(ClientMapInvalidationMetadataDistortionTest.MAP_NAME, member);
                    sleepSeconds(1);
                } 
            }
        });
        Thread distortUuid = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    distortRandomPartitionUuid(member);
                    sleepSeconds(5);
                } 
            }
        });
        Thread put = new Thread(new Runnable() {
            public void run() {
                // change some data
                while (!(stopTest.get())) {
                    int key = getInt(ClientMapInvalidationMetadataDistortionTest.MAP_SIZE);
                    int value = getInt(Integer.MAX_VALUE);
                    memberMap.put(key, value);
                    sleepAtLeastMillis(100);
                } 
            }
        });
        // start threads
        put.start();
        populateNearCache.start();
        distortSequence.start();
        distortUuid.start();
        sleepSeconds(60);
        // stop threads
        stopTest.set(true);
        assertJoinable(distortUuid, distortSequence, populateNearCache, put);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (ClientMapInvalidationMetadataDistortionTest.MAP_SIZE); i++) {
                    Integer valueSeenFromMember = memberMap.get(i);
                    Integer valueSeenFromClient = clientMap.get(i);
                    Assert.assertEquals(valueSeenFromMember, valueSeenFromClient);
                }
            }
        });
    }
}

