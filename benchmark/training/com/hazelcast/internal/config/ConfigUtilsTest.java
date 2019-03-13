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
package com.hazelcast.internal.config;


import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.matcher.MatchingPointConfigPatternMatcher;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigUtilsTest extends HazelcastTestSupport {
    private final Map<String, QueueConfig> queueConfigs = new ConcurrentHashMap<String, QueueConfig>();

    private final ConfigPatternMatcher configPatternMatcher = new MatchingPointConfigPatternMatcher();

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ConfigUtils.class);
    }

    @Test
    public void getNonExistingConfig_createNewWithCloningDefault() {
        QueueConfig aDefault = new QueueConfig("default");
        aDefault.setBackupCount(5);
        queueConfigs.put(aDefault.getName(), aDefault);
        QueueConfig newConfig = ConfigUtils.getConfig(configPatternMatcher, queueConfigs, "newConfig", QueueConfig.class);
        Assert.assertEquals("newConfig", newConfig.getName());
        Assert.assertEquals(5, newConfig.getBackupCount());
        Assert.assertEquals(2, queueConfigs.size());
        Assert.assertTrue(queueConfigs.containsKey("newConfig"));
        Assert.assertTrue(queueConfigs.containsKey("default"));
    }

    @Test
    public void getNonExistingConfig() {
        QueueConfig newConfig = ConfigUtils.getConfig(configPatternMatcher, queueConfigs, "newConfig", QueueConfig.class);
        Assert.assertEquals("newConfig", newConfig.getName());
        Assert.assertEquals(1, newConfig.getBackupCount());
        Assert.assertEquals(2, queueConfigs.size());
        Assert.assertTrue(queueConfigs.containsKey("newConfig"));
        Assert.assertTrue(queueConfigs.containsKey("default"));
    }

    @Test
    public void getExistingConfig() {
        QueueConfig aDefault = new QueueConfig("newConfig");
        aDefault.setBackupCount(5);
        queueConfigs.put(aDefault.getName(), aDefault);
        QueueConfig newConfig = ConfigUtils.getConfig(configPatternMatcher, queueConfigs, "newConfig", QueueConfig.class);
        Assert.assertEquals("newConfig", newConfig.getName());
        Assert.assertEquals(5, newConfig.getBackupCount());
        Assert.assertEquals(1, queueConfigs.size());
        Assert.assertTrue(queueConfigs.containsKey("newConfig"));
    }

    @Test
    public void getConfigWithDefaultNameMatcher() {
        QueueConfig aDefault = new QueueConfig("newConfig");
        aDefault.setBackupCount(5);
        queueConfigs.put(aDefault.getName(), aDefault);
        QueueConfig newConfig = ConfigUtils.getConfig(configPatternMatcher, queueConfigs, "newConfig@partition1", QueueConfig.class);
        Assert.assertEquals("newConfig", newConfig.getName());
        Assert.assertEquals(5, newConfig.getBackupCount());
        Assert.assertEquals(1, queueConfigs.size());
        Assert.assertTrue(queueConfigs.containsKey("newConfig"));
    }
}

