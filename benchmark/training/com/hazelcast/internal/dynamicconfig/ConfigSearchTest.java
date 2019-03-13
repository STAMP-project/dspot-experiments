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
package com.hazelcast.internal.dynamicconfig;


import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigSearchTest extends HazelcastTestSupport {
    private static final String STATIC_NAME = "my.custom.data.*";

    private static final String DYNAMIC_NAME = "my.custom.data.cache";

    private HazelcastInstance hazelcastInstance;

    @Test
    public void testMapConfig_Static() {
        ConfigSearchTest.TestCase<MapConfig> testCase = new ConfigSearchTest.TestCase<MapConfig>(new MapConfig(ConfigSearchTest.STATIC_NAME), new MapConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addMapConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addMapConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                MapConfig dataConfig = hazelcastInstance.getConfig().findMapConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testMapConfig_Dynamic() {
        ConfigSearchTest.TestCase<MapConfig> testCase = new ConfigSearchTest.TestCase<MapConfig>(new MapConfig(ConfigSearchTest.STATIC_NAME), new MapConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addMapConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addMapConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                MapConfig dataConfig = hazelcastInstance.getConfig().findMapConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testCacheConfig_Static() {
        ConfigSearchTest.TestCase<CacheSimpleConfig> testCase = new ConfigSearchTest.TestCase<CacheSimpleConfig>(new CacheSimpleConfig().setName(ConfigSearchTest.STATIC_NAME), new CacheSimpleConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addCacheConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addCacheConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                CacheSimpleConfig dataConfig = hazelcastInstance.getConfig().findCacheConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testCacheConfig_Dynamic() {
        ConfigSearchTest.TestCase<CacheSimpleConfig> testCase = new ConfigSearchTest.TestCase<CacheSimpleConfig>(new CacheSimpleConfig().setName(ConfigSearchTest.STATIC_NAME), new CacheSimpleConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addCacheConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addCacheConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                CacheSimpleConfig dataConfig = hazelcastInstance.getConfig().findCacheConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testQueueConfig_Static() {
        ConfigSearchTest.TestCase<QueueConfig> testCase = new ConfigSearchTest.TestCase<QueueConfig>(new QueueConfig().setName(ConfigSearchTest.STATIC_NAME), new QueueConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addQueueConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addQueueConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                QueueConfig dataConfig = hazelcastInstance.getConfig().findQueueConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testQueueConfig_Dynamic() {
        ConfigSearchTest.TestCase<QueueConfig> testCase = new ConfigSearchTest.TestCase<QueueConfig>(new QueueConfig().setName(ConfigSearchTest.STATIC_NAME), new QueueConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addQueueConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addQueueConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                QueueConfig dataConfig = hazelcastInstance.getConfig().findQueueConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testLockConfig_Static() {
        ConfigSearchTest.TestCase<LockConfig> testCase = new ConfigSearchTest.TestCase<LockConfig>(new LockConfig().setName(ConfigSearchTest.STATIC_NAME), new LockConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addLockConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addLockConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                LockConfig dataConfig = hazelcastInstance.getConfig().findLockConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testLockConfig_Dynamic() {
        ConfigSearchTest.TestCase<LockConfig> testCase = new ConfigSearchTest.TestCase<LockConfig>(new LockConfig().setName(ConfigSearchTest.STATIC_NAME), new LockConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addLockConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addLockConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                LockConfig dataConfig = hazelcastInstance.getConfig().findLockConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testListConfig_Static() {
        ConfigSearchTest.TestCase<ListConfig> testCase = new ConfigSearchTest.TestCase<ListConfig>(new ListConfig().setName(ConfigSearchTest.STATIC_NAME), new ListConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addListConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addListConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ListConfig dataConfig = hazelcastInstance.getConfig().findListConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testListConfig_Dynamic() {
        ConfigSearchTest.TestCase<ListConfig> testCase = new ConfigSearchTest.TestCase<ListConfig>(new ListConfig().setName(ConfigSearchTest.STATIC_NAME), new ListConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addListConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addListConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ListConfig dataConfig = hazelcastInstance.getConfig().findListConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testSetConfig_Static() {
        ConfigSearchTest.TestCase<SetConfig> testCase = new ConfigSearchTest.TestCase<SetConfig>(new SetConfig().setName(ConfigSearchTest.STATIC_NAME), new SetConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addSetConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addSetConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                SetConfig dataConfig = hazelcastInstance.getConfig().findSetConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testSetConfig_Dynamic() {
        ConfigSearchTest.TestCase<SetConfig> testCase = new ConfigSearchTest.TestCase<SetConfig>(new SetConfig().setName(ConfigSearchTest.STATIC_NAME), new SetConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addSetConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addSetConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                SetConfig dataConfig = hazelcastInstance.getConfig().findSetConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testMultiMapConfig_Static() {
        ConfigSearchTest.TestCase<MultiMapConfig> testCase = new ConfigSearchTest.TestCase<MultiMapConfig>(new MultiMapConfig().setName(ConfigSearchTest.STATIC_NAME), new MultiMapConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addMultiMapConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addMultiMapConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                MultiMapConfig dataConfig = hazelcastInstance.getConfig().findMultiMapConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testMultiMapConfig_Dynamic() {
        ConfigSearchTest.TestCase<MultiMapConfig> testCase = new ConfigSearchTest.TestCase<MultiMapConfig>(new MultiMapConfig().setName(ConfigSearchTest.STATIC_NAME), new MultiMapConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addMultiMapConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addMultiMapConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                MultiMapConfig dataConfig = hazelcastInstance.getConfig().findMultiMapConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testReplicatedMapConfig_Static() {
        ConfigSearchTest.TestCase<ReplicatedMapConfig> testCase = new ConfigSearchTest.TestCase<ReplicatedMapConfig>(new ReplicatedMapConfig().setName(ConfigSearchTest.STATIC_NAME), new ReplicatedMapConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addReplicatedMapConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addReplicatedMapConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ReplicatedMapConfig dataConfig = hazelcastInstance.getConfig().findReplicatedMapConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testReplicatedMapConfig_Dynamic() {
        ConfigSearchTest.TestCase<ReplicatedMapConfig> testCase = new ConfigSearchTest.TestCase<ReplicatedMapConfig>(new ReplicatedMapConfig().setName(ConfigSearchTest.STATIC_NAME), new ReplicatedMapConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addReplicatedMapConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addReplicatedMapConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ReplicatedMapConfig dataConfig = hazelcastInstance.getConfig().findReplicatedMapConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testRingbufferConfig_Static() {
        ConfigSearchTest.TestCase<RingbufferConfig> testCase = new ConfigSearchTest.TestCase<RingbufferConfig>(new RingbufferConfig().setName(ConfigSearchTest.STATIC_NAME), new RingbufferConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addRingBufferConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addRingBufferConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                RingbufferConfig dataConfig = hazelcastInstance.getConfig().findRingbufferConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testRingbufferConfig_Dynamic() {
        ConfigSearchTest.TestCase<RingbufferConfig> testCase = new ConfigSearchTest.TestCase<RingbufferConfig>(new RingbufferConfig().setName(ConfigSearchTest.STATIC_NAME), new RingbufferConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addRingBufferConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addRingBufferConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                RingbufferConfig dataConfig = hazelcastInstance.getConfig().findRingbufferConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testAtomicLongConfig_Static() {
        ConfigSearchTest.TestCase<AtomicLongConfig> testCase = new ConfigSearchTest.TestCase<AtomicLongConfig>(new AtomicLongConfig(ConfigSearchTest.STATIC_NAME), new AtomicLongConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addAtomicLongConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addAtomicLongConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                AtomicLongConfig dataConfig = hazelcastInstance.getConfig().findAtomicLongConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testAtomicLongConfig_Dynamic() {
        ConfigSearchTest.TestCase<AtomicLongConfig> testCase = new ConfigSearchTest.TestCase<AtomicLongConfig>(new AtomicLongConfig(ConfigSearchTest.STATIC_NAME), new AtomicLongConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addAtomicLongConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addAtomicLongConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                AtomicLongConfig dataConfig = hazelcastInstance.getConfig().findAtomicLongConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testAtomicReferenceConfig_Static() {
        ConfigSearchTest.TestCase<AtomicReferenceConfig> testCase = new ConfigSearchTest.TestCase<AtomicReferenceConfig>(new AtomicReferenceConfig(ConfigSearchTest.STATIC_NAME), new AtomicReferenceConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addAtomicReferenceConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addAtomicReferenceConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                AtomicReferenceConfig dataConfig = hazelcastInstance.getConfig().findAtomicReferenceConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testAtomicReferenceConfig_Dynamic() {
        ConfigSearchTest.TestCase<AtomicReferenceConfig> testCase = new ConfigSearchTest.TestCase<AtomicReferenceConfig>(new AtomicReferenceConfig(ConfigSearchTest.STATIC_NAME), new AtomicReferenceConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addAtomicReferenceConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addAtomicReferenceConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                AtomicReferenceConfig dataConfig = hazelcastInstance.getConfig().findAtomicReferenceConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testCountDownLatchConfig_Static() {
        ConfigSearchTest.TestCase<CountDownLatchConfig> testCase = new ConfigSearchTest.TestCase<CountDownLatchConfig>(new CountDownLatchConfig().setName(ConfigSearchTest.STATIC_NAME), new CountDownLatchConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addCountDownLatchConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addCountDownLatchConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                CountDownLatchConfig dataConfig = hazelcastInstance.getConfig().findCountDownLatchConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testCountDownLatchConfig_Dynamic() {
        ConfigSearchTest.TestCase<CountDownLatchConfig> testCase = new ConfigSearchTest.TestCase<CountDownLatchConfig>(new CountDownLatchConfig(ConfigSearchTest.STATIC_NAME), new CountDownLatchConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addCountDownLatchConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addCountDownLatchConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                CountDownLatchConfig dataConfig = hazelcastInstance.getConfig().findCountDownLatchConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testTopicConfig_Static() {
        ConfigSearchTest.TestCase<TopicConfig> testCase = new ConfigSearchTest.TestCase<TopicConfig>(new TopicConfig().setName(ConfigSearchTest.STATIC_NAME), new TopicConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addTopicConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addTopicConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                TopicConfig dataConfig = hazelcastInstance.getConfig().findTopicConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testTopicConfig_Dynamic() {
        ConfigSearchTest.TestCase<TopicConfig> testCase = new ConfigSearchTest.TestCase<TopicConfig>(new TopicConfig().setName(ConfigSearchTest.STATIC_NAME), new TopicConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addTopicConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addTopicConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                TopicConfig dataConfig = hazelcastInstance.getConfig().findTopicConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testReliableTopicConfig_Static() {
        ConfigSearchTest.TestCase<ReliableTopicConfig> testCase = new ConfigSearchTest.TestCase<ReliableTopicConfig>(new ReliableTopicConfig(ConfigSearchTest.STATIC_NAME), new ReliableTopicConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addReliableTopicConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addReliableTopicConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ReliableTopicConfig dataConfig = hazelcastInstance.getConfig().findReliableTopicConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testReliableTopicConfig_Dynamic() {
        ConfigSearchTest.TestCase<ReliableTopicConfig> testCase = new ConfigSearchTest.TestCase<ReliableTopicConfig>(new ReliableTopicConfig(ConfigSearchTest.STATIC_NAME), new ReliableTopicConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addReliableTopicConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addReliableTopicConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ReliableTopicConfig dataConfig = hazelcastInstance.getConfig().findReliableTopicConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testExecutorConfig_Static() {
        ConfigSearchTest.TestCase<ExecutorConfig> testCase = new ConfigSearchTest.TestCase<ExecutorConfig>(new ExecutorConfig(ConfigSearchTest.STATIC_NAME), new ExecutorConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addExecutorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addExecutorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ExecutorConfig dataConfig = hazelcastInstance.getConfig().findExecutorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testExecutorConfig_Dynamic() {
        ConfigSearchTest.TestCase<ExecutorConfig> testCase = new ConfigSearchTest.TestCase<ExecutorConfig>(new ExecutorConfig(ConfigSearchTest.STATIC_NAME), new ExecutorConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addExecutorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addExecutorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ExecutorConfig dataConfig = hazelcastInstance.getConfig().findExecutorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testDurableExecutorConfig_Static() {
        ConfigSearchTest.TestCase<DurableExecutorConfig> testCase = new ConfigSearchTest.TestCase<DurableExecutorConfig>(new DurableExecutorConfig().setName(ConfigSearchTest.STATIC_NAME), new DurableExecutorConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addDurableExecutorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addDurableExecutorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                DurableExecutorConfig dataConfig = hazelcastInstance.getConfig().findDurableExecutorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testDurableExecutorConfig_Dynamic() {
        ConfigSearchTest.TestCase<DurableExecutorConfig> testCase = new ConfigSearchTest.TestCase<DurableExecutorConfig>(new DurableExecutorConfig().setName(ConfigSearchTest.STATIC_NAME), new DurableExecutorConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addDurableExecutorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addDurableExecutorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                DurableExecutorConfig dataConfig = hazelcastInstance.getConfig().findDurableExecutorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testScheduledExecutorConfig_Static() {
        ConfigSearchTest.TestCase<ScheduledExecutorConfig> testCase = new ConfigSearchTest.TestCase<ScheduledExecutorConfig>(new ScheduledExecutorConfig(ConfigSearchTest.STATIC_NAME), new ScheduledExecutorConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addScheduledExecutorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addScheduledExecutorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ScheduledExecutorConfig dataConfig = hazelcastInstance.getConfig().findScheduledExecutorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testScheduledExecutorConfig_Dynamic() {
        ConfigSearchTest.TestCase<ScheduledExecutorConfig> testCase = new ConfigSearchTest.TestCase<ScheduledExecutorConfig>(new ScheduledExecutorConfig(ConfigSearchTest.STATIC_NAME), new ScheduledExecutorConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addScheduledExecutorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addScheduledExecutorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                ScheduledExecutorConfig dataConfig = hazelcastInstance.getConfig().findScheduledExecutorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testCardinalityEstimatorConfig_Static() {
        ConfigSearchTest.TestCase<CardinalityEstimatorConfig> testCase = new ConfigSearchTest.TestCase<CardinalityEstimatorConfig>(new CardinalityEstimatorConfig().setName(ConfigSearchTest.STATIC_NAME), new CardinalityEstimatorConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addCardinalityEstimatorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addCardinalityEstimatorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                CardinalityEstimatorConfig dataConfig = hazelcastInstance.getConfig().findCardinalityEstimatorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testCardinalityEstimatorConfig_Dynamic() {
        ConfigSearchTest.TestCase<CardinalityEstimatorConfig> testCase = new ConfigSearchTest.TestCase<CardinalityEstimatorConfig>(new CardinalityEstimatorConfig().setName(ConfigSearchTest.STATIC_NAME), new CardinalityEstimatorConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addCardinalityEstimatorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addCardinalityEstimatorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                CardinalityEstimatorConfig dataConfig = hazelcastInstance.getConfig().findCardinalityEstimatorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testSemaphoreConfig_Static() {
        ConfigSearchTest.TestCase<SemaphoreConfig> testCase = new ConfigSearchTest.TestCase<SemaphoreConfig>(new SemaphoreConfig().setName(ConfigSearchTest.STATIC_NAME), new SemaphoreConfig().setName(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addSemaphoreConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addSemaphoreConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                SemaphoreConfig dataConfig = hazelcastInstance.getConfig().findSemaphoreConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testSemaphoreConfig_Dynamic() {
        ConfigSearchTest.TestCase<SemaphoreConfig> testCase = new ConfigSearchTest.TestCase<SemaphoreConfig>(new SemaphoreConfig().setName(ConfigSearchTest.STATIC_NAME), new SemaphoreConfig().setName(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addSemaphoreConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addSemaphoreConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                SemaphoreConfig dataConfig = hazelcastInstance.getConfig().findSemaphoreConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testReliableIdGeneratorConfig_Static() {
        ConfigSearchTest.TestCase<FlakeIdGeneratorConfig> testCase = new ConfigSearchTest.TestCase<FlakeIdGeneratorConfig>(new FlakeIdGeneratorConfig(ConfigSearchTest.STATIC_NAME), new FlakeIdGeneratorConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addFlakeIdGeneratorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addFlakeIdGeneratorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                FlakeIdGeneratorConfig dataConfig = hazelcastInstance.getConfig().findFlakeIdGeneratorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testReliableIdGeneratorConfig_Dynamic() {
        ConfigSearchTest.TestCase<FlakeIdGeneratorConfig> testCase = new ConfigSearchTest.TestCase<FlakeIdGeneratorConfig>(new FlakeIdGeneratorConfig(ConfigSearchTest.STATIC_NAME), new FlakeIdGeneratorConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addFlakeIdGeneratorConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addFlakeIdGeneratorConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                FlakeIdGeneratorConfig dataConfig = hazelcastInstance.getConfig().findFlakeIdGeneratorConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testPNCounterConfig_Static() {
        ConfigSearchTest.TestCase<PNCounterConfig> testCase = new ConfigSearchTest.TestCase<PNCounterConfig>(new PNCounterConfig(ConfigSearchTest.STATIC_NAME), new PNCounterConfig(ConfigSearchTest.DYNAMIC_NAME), false) {
            @Override
            void addStaticConfig(Config config) {
                config.addPNCounterConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addPNCounterConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                PNCounterConfig dataConfig = hazelcastInstance.getConfig().findPNCounterConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.STATIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    @Test
    public void testPNCounterConfig_Dynamic() {
        ConfigSearchTest.TestCase<PNCounterConfig> testCase = new ConfigSearchTest.TestCase<PNCounterConfig>(new PNCounterConfig(ConfigSearchTest.STATIC_NAME), new PNCounterConfig(ConfigSearchTest.DYNAMIC_NAME), true) {
            @Override
            void addStaticConfig(Config config) {
                config.addPNCounterConfig(this.staticConfig);
            }

            @Override
            void addDynamicConfig(HazelcastInstance hazelcastInstance) {
                hazelcastInstance.getConfig().addPNCounterConfig(this.dynamicConfig);
            }

            @Override
            void asserts() {
                PNCounterConfig dataConfig = hazelcastInstance.getConfig().findPNCounterConfig(ConfigSearchTest.DYNAMIC_NAME);
                MatcherAssert.assertThat(dataConfig.getName(), Matchers.equalTo(ConfigSearchTest.DYNAMIC_NAME));
            }
        };
        testTemplate(testCase);
    }

    abstract class TestCase<T> {
        final T staticConfig;

        final T dynamicConfig;

        private final boolean isDynamicFirst;

        TestCase(T staticConfig, T dynamicConfig, boolean isDynamicFirst) {
            this.staticConfig = staticConfig;
            this.dynamicConfig = dynamicConfig;
            this.isDynamicFirst = isDynamicFirst;
        }

        boolean isDynamicFirst() {
            return isDynamicFirst;
        }

        abstract void addStaticConfig(Config config);

        abstract void addDynamicConfig(HazelcastInstance hazelcastInstance);

        abstract void asserts();
    }
}

