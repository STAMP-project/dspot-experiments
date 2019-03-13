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
package com.hazelcast.quorum.map;


import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.quorum.QuorumEvent;
import com.hazelcast.quorum.QuorumFunction;
import com.hazelcast.quorum.QuorumListener;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapQuorumListenerTest extends HazelcastTestSupport {
    @Test
    public void testQuorumFailureEventFiredWhenNodeCountBelowThreshold() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Config config = new Config();
        QuorumListenerConfig listenerConfig = new QuorumListenerConfig();
        listenerConfig.setImplementation(new QuorumListener() {
            public void onChange(QuorumEvent quorumEvent) {
                if (!(quorumEvent.isPresent())) {
                    countDownLatch.countDown();
                }
            }
        });
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig = new QuorumConfig(quorumName, true, 3);
        quorumConfig.addListenerConfig(listenerConfig);
        config.getMapConfig(mapName).setQuorumName(quorumName);
        config.addQuorumConfig(quorumConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap(mapName);
        try {
            map.put(HazelcastTestSupport.generateKeyOwnedBy(instance), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HazelcastTestSupport.assertOpenEventually(countDownLatch, 15);
    }

    @Test
    public void testQuorumFailureEventFiredWhenNodeCountDropsBelowThreshold() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Config config = new Config();
        QuorumListenerConfig listenerConfig = new QuorumListenerConfig();
        listenerConfig.setImplementation(new QuorumListener() {
            public void onChange(QuorumEvent quorumEvent) {
                if (!(quorumEvent.isPresent())) {
                    countDownLatch.countDown();
                }
            }
        });
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig = new QuorumConfig(quorumName, true, 3);
        quorumConfig.addListenerConfig(listenerConfig);
        config.getMapConfig(mapName).setQuorumName(quorumName);
        config.addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastInstance();
        hz.shutdown();
        HazelcastTestSupport.assertOpenEventually(countDownLatch, 15);
    }

    @Test
    public void testQuorumEventsFiredWhenNodeCountBelowThenAboveThreshold() {
        final CountDownLatch belowLatch = new CountDownLatch(1);
        final CountDownLatch aboveLatch = new CountDownLatch(1);
        Config config = new Config();
        QuorumListenerConfig listenerConfig = new QuorumListenerConfig();
        listenerConfig.setImplementation(new QuorumListener() {
            public void onChange(QuorumEvent quorumEvent) {
                if (quorumEvent.isPresent()) {
                    aboveLatch.countDown();
                } else {
                    belowLatch.countDown();
                }
            }
        });
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig = new QuorumConfig(quorumName, true, 3);
        quorumConfig.addListenerConfig(listenerConfig);
        config.getMapConfig(mapName).setQuorumName(quorumName);
        config.addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertOpenEventually(belowLatch, 15);
        factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertOpenEventually(aboveLatch);
    }

    @Test
    public void testDifferentQuorumsGetCorrectEvents() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final CountDownLatch quorumFailureLatch = new CountDownLatch(2);
        String fourNodeQuorumName = "fourNode";
        QuorumConfig fourNodeQuorumConfig = new QuorumConfig(fourNodeQuorumName, true, 4);
        fourNodeQuorumConfig.addListenerConfig(new QuorumListenerConfig(new QuorumListener() {
            public void onChange(QuorumEvent quorumEvent) {
                if (!(quorumEvent.isPresent())) {
                    quorumFailureLatch.countDown();
                }
            }
        }));
        String threeNodeQuorumName = "threeNode";
        QuorumConfig threeNodeQuorumConfig = new QuorumConfig(threeNodeQuorumName, true, 3);
        threeNodeQuorumConfig.addListenerConfig(new QuorumListenerConfig(new QuorumListener() {
            public void onChange(QuorumEvent quorumEvent) {
                if (!(quorumEvent.isPresent())) {
                    quorumFailureLatch.countDown();
                }
            }
        }));
        MapConfig fourNodeMapConfig = new MapConfig("fourNode");
        fourNodeMapConfig.setQuorumName(fourNodeQuorumName);
        MapConfig threeNodeMapConfig = new MapConfig("threeNode");
        threeNodeMapConfig.setQuorumName(threeNodeQuorumName);
        Config config = new Config();
        config.addMapConfig(fourNodeMapConfig);
        config.addQuorumConfig(fourNodeQuorumConfig);
        config.addMapConfig(threeNodeMapConfig);
        config.addQuorumConfig(threeNodeQuorumConfig);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertOpenEventually(quorumFailureLatch);
    }

    @Test
    public void testCustomResolverFiresQuorumFailureEvent() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Config config = new Config();
        QuorumListenerConfig listenerConfig = new QuorumListenerConfig();
        listenerConfig.setImplementation(new QuorumListener() {
            @Override
            public void onChange(QuorumEvent quorumEvent) {
                if (!(quorumEvent.isPresent())) {
                    countDownLatch.countDown();
                }
            }
        });
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig = new QuorumConfig();
        quorumConfig.setName(quorumName);
        quorumConfig.setEnabled(true);
        quorumConfig.addListenerConfig(listenerConfig);
        quorumConfig.setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return false;
            }
        });
        config.getMapConfig(mapName).setQuorumName(quorumName);
        config.addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance();
        HazelcastTestSupport.assertOpenEventually(countDownLatch, 15);
    }

    @Test
    public void testQuorumEventProvidesCorrectMemberListSize() {
        final CountDownLatch belowLatch = new CountDownLatch(2);
        Config config = new Config();
        QuorumListenerConfig listenerConfig = new QuorumListenerConfig();
        listenerConfig.setImplementation(new QuorumListener() {
            public void onChange(QuorumEvent quorumEvent) {
                if (!(quorumEvent.isPresent())) {
                    Collection<Member> currentMembers = quorumEvent.getCurrentMembers();
                    Assert.assertEquals(3, quorumEvent.getThreshold());
                    Assert.assertTrue(((currentMembers.size()) < (quorumEvent.getThreshold())));
                    belowLatch.countDown();
                }
            }
        });
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig = new QuorumConfig(quorumName, true, 3);
        quorumConfig.addListenerConfig(listenerConfig);
        config.getMapConfig(mapName).setQuorumName(quorumName);
        config.addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertOpenEventually(belowLatch);
    }
}

