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
package com.hazelcast.config;


import InMemoryFormat.OBJECT;
import MapConfig.DEFAULT_IN_MEMORY_FORMAT;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigTest extends HazelcastTestSupport {
    private Config config;

    /**
     * Tests that the order of configuration creation matters.
     * <ul>
     * <li>Configurations which are created before the "default" configuration do not inherit from it.</li>
     * <li>Configurations which are created after the "default" configuration do inherit from it.</li>
     * </ul>
     */
    @Test
    public void testInheritanceFromDefaultConfig() {
        Assert.assertNotEquals("Expected that the default in-memory format is not OBJECT", DEFAULT_IN_MEMORY_FORMAT, OBJECT);
        config.getMapConfig("myBinaryMap").setBackupCount(3);
        config.getMapConfig("default").setInMemoryFormat(OBJECT);
        config.getMapConfig("myObjectMap").setBackupCount(5);
        HazelcastInstance hz = createHazelcastInstance(config);
        MapConfig binaryMapConfig = hz.getConfig().findMapConfig("myBinaryMap");
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d sync backups, but found %d", 3, binaryMapConfig.getBackupCount());
        HazelcastTestSupport.assertEqualsStringFormat("Expected %s in-memory format, but found %s", DEFAULT_IN_MEMORY_FORMAT, binaryMapConfig.getInMemoryFormat());
        MapConfig objectMapConfig = hz.getConfig().findMapConfig("myObjectMap");
        HazelcastTestSupport.assertEqualsStringFormat("Expected %d sync backups, but found %d", 5, objectMapConfig.getBackupCount());
        HazelcastTestSupport.assertEqualsStringFormat("Expected %s in-memory format, but found %s", OBJECT, objectMapConfig.getInMemoryFormat());
    }

    @Test
    public void testReturnNullMapConfig_whenThereIsNoMatch() {
        MapConfig mapConfig = new MapConfig("hz-map");
        config.addMapConfig(mapConfig);
        Assert.assertNotNull(config.getMapConfigOrNull("hz-map"));
        Assert.assertNull(config.getMapConfigOrNull("@invalid"));
    }

    @Test
    public void testReturnNullCacheConfig_whenThereIsNoMatch() {
        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        cacheConfig.setName("hz-cache");
        config.addCacheConfig(cacheConfig);
        Assert.assertNotNull(config.findCacheConfigOrNull("hz-cache"));
        Assert.assertNull(config.findCacheConfigOrNull("@invalid"));
    }

    @Test
    public void testQueueConfigReturnDefault_whenThereIsNoMatch() {
        QueueConfig queueConfig = config.findQueueConfig("test");
        Assert.assertEquals("default", queueConfig.getName());
    }

    @Test
    public void testLockConfigReturnDefault_whenThereIsNoMatch() {
        LockConfig lockConfig = config.findLockConfig("test");
        Assert.assertEquals("default", lockConfig.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConfigThrow_whenConfigPatternMatcherIsNull() {
        config.setConfigPatternMatcher(null);
    }

    @Test
    public void testEndpointConfig() {
        String name = HazelcastTestSupport.randomName();
        EndpointQualifier qualifier = EndpointQualifier.resolve(ProtocolType.WAN, name);
        ServerSocketEndpointConfig endpointConfig = new ServerSocketEndpointConfig();
        endpointConfig.setName(name);
        endpointConfig.setProtocolType(ProtocolType.WAN);
        config.getAdvancedNetworkConfig().addWanEndpointConfig(endpointConfig);
        Assert.assertEquals(endpointConfig, config.getAdvancedNetworkConfig().getEndpointConfigs().get(qualifier));
    }
}

