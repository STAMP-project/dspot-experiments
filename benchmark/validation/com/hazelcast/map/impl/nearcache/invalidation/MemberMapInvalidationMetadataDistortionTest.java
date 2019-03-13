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
package com.hazelcast.map.impl.nearcache.invalidation;


import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.nearcache.NearCacheTestSupport;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.util.RandomPicker;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class MemberMapInvalidationMetadataDistortionTest extends NearCacheTestSupport {
    private static final int MAP_SIZE = 100000;

    private static final String MAP_NAME = "MemberMapInvalidationMetadataDistortionTest";

    private final TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory();

    private final AtomicBoolean stopTest = new AtomicBoolean();

    @Test
    public void lostInvalidation() {
        // members are created
        MapConfig mapConfig = createMapConfig(MemberMapInvalidationMetadataDistortionTest.MAP_NAME);
        Config config = createConfig().addMapConfig(mapConfig);
        final HazelcastInstance member = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        // map is populated form member
        final IMap<Integer, Integer> memberMap = member.getMap(MemberMapInvalidationMetadataDistortionTest.MAP_NAME);
        for (int i = 0; i < (MemberMapInvalidationMetadataDistortionTest.MAP_SIZE); i++) {
            memberMap.put(i, i);
        }
        // a new member comes with Near Cache configured
        MapConfig nearCachedMapConfig = createMapConfig(MemberMapInvalidationMetadataDistortionTest.MAP_NAME).setNearCacheConfig(createNearCacheConfig(MemberMapInvalidationMetadataDistortionTest.MAP_NAME));
        Config nearCachedConfig = createConfig().addMapConfig(nearCachedMapConfig);
        HazelcastInstance nearCachedMember = factory.newHazelcastInstance(nearCachedConfig);
        final IMap<Integer, Integer> nearCachedMap = nearCachedMember.getMap(MemberMapInvalidationMetadataDistortionTest.MAP_NAME);
        Thread populateNearCache = new Thread(new Runnable() {
            public void run() {
                while (!(stopTest.get())) {
                    for (int i = 0; i < (MemberMapInvalidationMetadataDistortionTest.MAP_SIZE); i++) {
                        nearCachedMap.get(i);
                    }
                } 
            }
        });
        Thread distortSequence = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    distortRandomPartitionSequence(MemberMapInvalidationMetadataDistortionTest.MAP_NAME, member);
                    HazelcastTestSupport.sleepSeconds(1);
                } 
            }
        });
        Thread distortUuid = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopTest.get())) {
                    distortRandomPartitionUuid(member);
                    HazelcastTestSupport.sleepSeconds(3);
                } 
            }
        });
        Thread put = new Thread(new Runnable() {
            public void run() {
                // change some data
                while (!(stopTest.get())) {
                    int key = RandomPicker.getInt(MemberMapInvalidationMetadataDistortionTest.MAP_SIZE);
                    int value = RandomPicker.getInt(Integer.MAX_VALUE);
                    memberMap.put(key, value);
                    HazelcastTestSupport.sleepAtLeastMillis(10);
                } 
            }
        });
        // start threads
        put.start();
        populateNearCache.start();
        distortSequence.start();
        distortUuid.start();
        HazelcastTestSupport.sleepSeconds(60);
        // stop threads
        stopTest.set(true);
        HazelcastTestSupport.assertJoinable(distortUuid, distortSequence, populateNearCache, put);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (MemberMapInvalidationMetadataDistortionTest.MAP_SIZE); i++) {
                    Integer valueSeenFromMember = memberMap.get(i);
                    Integer valueSeenFromNearCachedSide = nearCachedMap.get(i);
                    Assert.assertEquals(valueSeenFromMember, valueSeenFromNearCachedSide);
                }
            }
        });
    }
}

