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
package com.hazelcast.map.impl.mapstore;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Properties;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLoaderLifecycleTest extends HazelcastTestSupport {
    private MapLoaderLifecycleSupport loader = MapLoaderLifecycleTest.mockMapLoaderWithLifecycle();

    private Config config = new Config();

    @Test
    public void testInitCalled_whenMapCreated() {
        HazelcastInstance hz = createHazelcastInstance(config);
        IMap<String, String> map = hz.getMap("map");
        // MapStore creation is deferred, so trigger map store creation by putting some data in the map
        map.put("a", "b");
        Mockito.verify(loader).init(ArgumentMatchers.eq(hz), ArgumentMatchers.eq(new Properties()), ArgumentMatchers.eq("map"));
    }

    @Test
    public void testDestroyCalled_whenNodeShutdown() {
        HazelcastInstance hz = createHazelcastInstance(config);
        IMap<String, String> map = hz.getMap("map");
        // MapStore creation is deferred, so trigger map store creation by putting some data in the map
        map.put("a", "b");
        hz.shutdown();
        Mockito.verify(loader).destroy();
    }
}

