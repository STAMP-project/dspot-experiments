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
package com.hazelcast.quorum;


import GroupProperty.HEARTBEAT_INTERVAL_SECONDS;
import GroupProperty.MAX_NO_HEARTBEAT_SECONDS;
import QuorumServiceImpl.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.quorum.impl.ProbabilisticQuorumFunction;
import com.hazelcast.quorum.impl.RecentlyActiveQuorumFunction;
import com.hazelcast.spi.MemberAttributeServiceEvent;
import com.hazelcast.spi.MembershipAwareService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * Tests quorum related configurations.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QuorumTest extends HazelcastTestSupport {
    @Test
    public void testQuorumIsSetCorrectlyOnNodeInitialization() {
        String quorumName1 = HazelcastTestSupport.randomString();
        String quorumName2 = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig1 = new QuorumConfig().setName(quorumName1).setEnabled(true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return true;
            }
        });
        QuorumConfig quorumConfig2 = new QuorumConfig().setName(quorumName2).setEnabled(true).setSize(2);
        Config config = new Config().addQuorumConfig(quorumConfig1).addQuorumConfig(quorumConfig2);
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        final Quorum quorum1 = hazelcastInstance.getQuorumService().getQuorum(quorumName1);
        final Quorum quorum2 = hazelcastInstance.getQuorumService().getQuorum(quorumName2);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(quorum1.isPresent());
                Assert.assertFalse(quorum2.isPresent());
            }
        });
    }

    @Test
    public void testProbabilisticQuorumConsidersLocalMember() {
        String quorumName = HazelcastTestSupport.randomString();
        QuorumFunction quorumFunction = new ProbabilisticQuorumFunction(1, 100, 1250, 20, 100, 20);
        QuorumConfig quorumConfig = new QuorumConfig().setName(quorumName).setEnabled(true).setQuorumFunctionImplementation(quorumFunction);
        Config config = new Config().addQuorumConfig(quorumConfig).setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "1");
        HazelcastInstance instance = createHazelcastInstance(config);
        final Quorum quorum = instance.getQuorumService().getQuorum(quorumName);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(quorum.isPresent());
            }
        });
    }

    @Test
    public void testRecentlyActiveQuorumConsidersLocalMember() {
        final String quorumName = HazelcastTestSupport.randomString();
        QuorumFunction quorumFunction = new RecentlyActiveQuorumFunction(1, 10000);
        QuorumConfig quorumConfig = new QuorumConfig().setName(quorumName).setEnabled(true).setQuorumFunctionImplementation(quorumFunction);
        Config config = new Config().addQuorumConfig(quorumConfig).setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "1");
        HazelcastInstance instance = createHazelcastInstance(config);
        final Quorum quorum = instance.getQuorumService().getQuorum(quorumName);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(quorum.isPresent());
            }
        });
    }

    @Test
    public void testQuorumIgnoresMemberAttributeEvents() {
        final QuorumTest.RecordingQuorumFunction function = new QuorumTest.RecordingQuorumFunction();
        QuorumConfig quorumConfig = new QuorumConfig().setName(HazelcastTestSupport.randomString()).setEnabled(true).setQuorumFunctionImplementation(function);
        Config config = new Config().addQuorumConfig(quorumConfig);
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hazelcastInstance);
        MembershipAwareService service = nodeEngine.getService(SERVICE_NAME);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(function.wasCalled);
            }
        });
        function.wasCalled = false;
        MemberAttributeServiceEvent event = Mockito.mock(MemberAttributeServiceEvent.class);
        service.memberAttributeChanged(event);
        Assert.assertFalse(function.wasCalled);
    }

    @Test(expected = QuorumException.class)
    public void testCustomQuorumFunctionFails() {
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        MapConfig mapConfig = new MapConfig(mapName).setQuorumName(quorumName);
        QuorumConfig quorumConfig = new QuorumConfig().setName(quorumName).setEnabled(true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return false;
            }
        });
        Config config = new Config().addQuorumConfig(quorumConfig).addMapConfig(mapConfig);
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        IMap<Object, Object> map = hazelcastInstance.getMap(mapName);
        map.put("1", "1");
    }

    @Test
    public void testCustomQuorumFunctionIsPresent() {
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        MapConfig mapConfig = new MapConfig(mapName).setQuorumName(quorumName);
        QuorumConfig quorumConfig = new QuorumConfig().setName(quorumName).setEnabled(true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return false;
            }
        });
        Config config = new Config().addQuorumConfig(quorumConfig).addMapConfig(mapConfig);
        HazelcastInstance hazelcastInstance = createHazelcastInstance(config);
        IMap<Object, Object> map = hazelcastInstance.getMap(mapName);
        try {
            map.put("1", "1");
            Assert.fail();
        } catch (Exception ignored) {
        }
        Quorum quorum = hazelcastInstance.getQuorumService().getQuorum(quorumName);
        Assert.assertFalse(quorum.isPresent());
    }

    @Test(expected = QuorumException.class)
    public void testCustomQuorumFunctionFailsForAllNodes() {
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        MapConfig mapConfig = new MapConfig(mapName).setQuorumName(quorumName);
        QuorumConfig quorumConfig = new QuorumConfig().setName(quorumName).setEnabled(true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return false;
            }
        });
        Config config = new Config().addMapConfig(mapConfig).addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        factory.newHazelcastInstance(config);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        IMap<Object, Object> map2 = hz.getMap(mapName);
        map2.put("1", "1");
    }

    @Test
    public void testCustomQuorumFunctionFailsThenSuccess() {
        final AtomicInteger count = new AtomicInteger(1);
        String mapName = HazelcastTestSupport.randomMapName();
        String quorumName = HazelcastTestSupport.randomString();
        MapConfig mapConfig = new MapConfig(mapName).setQuorumName(quorumName);
        QuorumConfig quorumConfig = new QuorumConfig().setName(quorumName).setEnabled(true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                if ((count.get()) == 1) {
                    count.incrementAndGet();
                    return false;
                } else {
                    return true;
                }
            }
        });
        Config config = new Config().addMapConfig(mapConfig).addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory(2);
        HazelcastInstance hazelcastInstance = factory.newHazelcastInstance(config);
        IMap<Object, Object> map = hazelcastInstance.getMap(mapName);
        try {
            map.put("1", "1");
            Assert.fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        factory.newHazelcastInstance(config);
        map.put("1", "1");
        factory.shutdownAll();
    }

    @Test
    public void testOneQuorumsFailsOneQuorumSuccessForDifferentMaps() {
        String fourNodeQuorum = HazelcastTestSupport.randomString();
        String threeNodeQuorum = HazelcastTestSupport.randomString();
        MapConfig fourNodeMapConfig = new MapConfig("fourNode").setQuorumName(fourNodeQuorum);
        MapConfig threeNodeMapConfig = new MapConfig("threeNode").setQuorumName(threeNodeQuorum);
        QuorumConfig fourNodeQuorumConfig = new QuorumConfig(fourNodeQuorum, true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return (members.size()) == 4;
            }
        });
        QuorumConfig threeNodeQuorumConfig = new QuorumConfig(threeNodeQuorum, true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return (members.size()) == 3;
            }
        });
        Config config = new Config().addMapConfig(fourNodeMapConfig).addMapConfig(threeNodeMapConfig).addQuorumConfig(threeNodeQuorumConfig).addQuorumConfig(fourNodeQuorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        IMap<Object, Object> fourNode = hz.getMap("fourNode");
        IMap<Object, Object> threeNode = hz.getMap("threeNode");
        threeNode.put(HazelcastTestSupport.generateKeyOwnedBy(hz), "bar");
        try {
            fourNode.put(HazelcastTestSupport.generateKeyOwnedBy(hz), "bar");
            Assert.fail();
        } catch (Exception ignored) {
        }
    }

    /**
     * https://github.com/hazelcast/hazelcast/issues/9792
     */
    @Test
    public void oneQuorumShouldNotAffectQuorumAwareOperationsOnDataStructuresWithoutQuorumConfiguration() {
        String quorumName = HazelcastTestSupport.randomString();
        QuorumConfig quorumConfig = new QuorumConfig(quorumName, true).setQuorumFunctionImplementation(new QuorumFunction() {
            @Override
            public boolean apply(Collection<Member> members) {
                return (members.size()) == 3;
            }
        });
        MapConfig mapConfig = new MapConfig("quorumMap").setQuorumName(quorumName);
        Config config = new Config().addMapConfig(mapConfig).addQuorumConfig(quorumConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        IMap<Object, Object> quorumMap = hz.getMap("quorumMap");
        quorumMap.put(HazelcastTestSupport.generateKeyOwnedBy(hz), "bar");
        ILock lock = hz.getLock("noQuorumLock");
        try {
            lock.lock();
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void givenQuorumFunctionConfigured_whenImplementsHazelcastInstanceAware_thenHazelcastInjectsItsInstance() {
        QuorumConfig quorumConfig = new QuorumConfig(HazelcastTestSupport.randomString(), true).setQuorumFunctionClassName(QuorumTest.HazelcastInstanceAwareQuorumFunction.class.getName());
        Config config = new Config().addQuorumConfig(quorumConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        Assert.assertEquals(instance, QuorumTest.HazelcastInstanceAwareQuorumFunction.instance);
    }

    @Test
    public void givenQuorumFunctionInstanceConfigured_whenImplementsHazelcastInstanceAware_thenHazelcastInjectsItsInstance() {
        QuorumConfig quorumConfig = new QuorumConfig(HazelcastTestSupport.randomString(), true).setQuorumFunctionImplementation(new QuorumTest.HazelcastInstanceAwareQuorumFunction());
        Config config = new Config().addQuorumConfig(quorumConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        Assert.assertEquals(instance, QuorumTest.HazelcastInstanceAwareQuorumFunction.instance);
    }

    @Test(expected = ConfigurationException.class)
    public void givenProbabilisticQuorum_whenAcceptableHeartbeatPause_greaterThanMaxNoHeartbeat_exceptionIsThrown() {
        Config config = new Config();
        config.setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "10");
        QuorumConfig probabilisticQuorumConfig = QuorumConfig.newProbabilisticQuorumConfigBuilder("prob-quorum", 3).withAcceptableHeartbeatPauseMillis(13000).build();
        config.addQuorumConfig(probabilisticQuorumConfig);
        createHazelcastInstance(config);
    }

    @Test(expected = ConfigurationException.class)
    public void givenProbabilisticQuorum_whenAcceptableHeartbeatPause_lessThanHeartbeatInterval_exceptionIsThrown() {
        Config config = new Config();
        config.setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "5");
        QuorumConfig probabilisticQuorumConfig = QuorumConfig.newProbabilisticQuorumConfigBuilder("prob-quorum", 3).withAcceptableHeartbeatPauseMillis(3000).build();
        config.addQuorumConfig(probabilisticQuorumConfig);
        createHazelcastInstance(config);
    }

    @Test(expected = ConfigurationException.class)
    public void givenRecentlyActiveQuorum_whenHeartbeatTolerance_greaterThanMaxNoHeartbeat_exceptionIsThrown() {
        Config config = new Config();
        config.setProperty(MAX_NO_HEARTBEAT_SECONDS.getName(), "10");
        QuorumConfig recentlyActiveQuorumConfig = QuorumConfig.newRecentlyActiveQuorumConfigBuilder("test-quorum", 3, 13000).build();
        config.addQuorumConfig(recentlyActiveQuorumConfig);
        createHazelcastInstance(config);
    }

    @Test(expected = ConfigurationException.class)
    public void givenRecentlyActiveQuorum_whenHeartbeatTolerance_lessThanHeartbeatInterval_exceptionIsThrown() {
        Config config = new Config();
        config.setProperty(HEARTBEAT_INTERVAL_SECONDS.getName(), "5");
        QuorumConfig recentlyActiveQuorumConfig = QuorumConfig.newRecentlyActiveQuorumConfigBuilder("test-quorum", 3, 3000).build();
        config.addQuorumConfig(recentlyActiveQuorumConfig);
        createHazelcastInstance(config);
    }

    private static class HazelcastInstanceAwareQuorumFunction implements HazelcastInstanceAware , QuorumFunction {
        private static volatile HazelcastInstance instance;

        @Override
        public void setHazelcastInstance(HazelcastInstance instance) {
            QuorumTest.HazelcastInstanceAwareQuorumFunction.instance = instance;
        }

        @Override
        public boolean apply(Collection<Member> members) {
            return false;
        }
    }

    private static class RecordingQuorumFunction implements QuorumFunction {
        private volatile boolean wasCalled;

        @Override
        public boolean apply(Collection<Member> members) {
            wasCalled = true;
            return false;
        }
    }
}

