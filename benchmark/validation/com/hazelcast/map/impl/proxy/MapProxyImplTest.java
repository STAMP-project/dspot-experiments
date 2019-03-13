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
package com.hazelcast.map.impl.proxy;


import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests the creation of a map proxy.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapProxyImplTest extends HazelcastTestSupport {
    @Test
    public void whenMapProxyIsCreated_mapContainerIsNotCreated() {
        HazelcastInstance hz = createHazelcastInstance();
        MapProxyImpl mapProxy = ((MapProxyImpl) (hz.getMap(HazelcastTestSupport.randomMapName())));
        assertNoMapContainersExist(mapProxy);
    }

    @Test
    public void whenMapProxyWithLazyMapStoreIsCreated_mapContainerIsNotCreated() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfigWithMapStore(mapName, InitialLoadMode.LAZY);
        HazelcastInstance hz = createHazelcastInstance(config);
        MapProxyImpl mapProxy = ((MapProxyImpl) (hz.getMap(HazelcastTestSupport.randomMapName())));
        assertNoMapContainersExist(mapProxy);
    }

    @Test
    public void whenMapProxyWithEagerMapStoreIsCreated_mapContainerIsNotCreated() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfigWithMapStore(mapName, InitialLoadMode.EAGER);
        HazelcastInstance hz = createHazelcastInstance(config);
        MapProxyImpl mapProxy = ((MapProxyImpl) (hz.getMap(HazelcastTestSupport.randomMapName())));
        assertNoMapContainersExist(mapProxy);
    }

    @Test
    public void whenNearCachedMapProxyIsCreated_mapContainerIsNotCreated() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = new Config();
        NearCacheConfig nearCacheConfig = new NearCacheConfig().setName(mapName).setInMemoryFormat(InMemoryFormat.BINARY).setInvalidateOnChange(false);
        config.getMapConfig(mapName).setNearCacheConfig(nearCacheConfig);
        HazelcastInstance hz = createHazelcastInstance(config);
        MapProxyImpl mapProxy = ((MapProxyImpl) (hz.getMap(mapName)));
        assertNoMapContainersExist(mapProxy);
    }
}

