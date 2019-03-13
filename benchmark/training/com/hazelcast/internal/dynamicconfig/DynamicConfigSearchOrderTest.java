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


import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
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
public class DynamicConfigSearchOrderTest extends HazelcastTestSupport {
    private static final String STATIC_WILDCARD_NAME = "my.custom.data.*";

    private static final String DYNAMIC_WILDCARD_NAME = "my.custom.data.cache.*";

    private static final String STATIC_NAME = "my.custom.data.cache.static";

    private static final String DYNAMIC_NAME = "my.custom.data.cache.dynamic";

    private static final String NON_EXISTENT_NAME = "my.custom.data.cache.none";

    private Config staticHazelcastConfig;

    private HazelcastInstance hazelcastInstance;

    @Test
    public void testSearchConfigForDynamicWildcardOnlyConfig() {
        hazelcastInstance = createHazelcastInstance(staticHazelcastConfig);
        hazelcastInstance.getConfig().addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.DYNAMIC_WILDCARD_NAME));
        Assert.assertEquals("Dynamic wildcard name should match", DynamicConfigSearchOrderTest.DYNAMIC_WILDCARD_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.DYNAMIC_NAME).getName());
    }

    @Test
    public void testSearchConfigOrderForDynamicWildcardAndExactConfig() {
        hazelcastInstance = createHazelcastInstance(staticHazelcastConfig);
        hazelcastInstance.getConfig().addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.DYNAMIC_WILDCARD_NAME));
        hazelcastInstance.getConfig().addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.DYNAMIC_NAME));
        Assert.assertEquals("Dynamic exact match should prepend wildcard settings", DynamicConfigSearchOrderTest.DYNAMIC_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.DYNAMIC_NAME).getName());
    }

    @Test
    public void testSearchConfigOrderForDynamicAndStaticConfigs() {
        staticHazelcastConfig.addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.STATIC_WILDCARD_NAME));
        staticHazelcastConfig.addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.STATIC_NAME));
        hazelcastInstance = createHazelcastInstance(staticHazelcastConfig);
        hazelcastInstance.getConfig().addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.DYNAMIC_WILDCARD_NAME));
        hazelcastInstance.getConfig().addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.DYNAMIC_NAME));
        Assert.assertEquals("Dynamic exact name should match", DynamicConfigSearchOrderTest.DYNAMIC_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.DYNAMIC_NAME).getName());
        Assert.assertEquals("Dynamic wildcard settings should prepend static settings", DynamicConfigSearchOrderTest.DYNAMIC_WILDCARD_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.STATIC_NAME).getName());
        Assert.assertEquals("Dynamic wildcard settings should prepend static settings", DynamicConfigSearchOrderTest.DYNAMIC_WILDCARD_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.NON_EXISTENT_NAME).getName());
    }

    @Test
    public void testSearchConfigOrderForStaticConfigs() {
        staticHazelcastConfig.addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.STATIC_WILDCARD_NAME));
        staticHazelcastConfig.addMapConfig(new MapConfig(DynamicConfigSearchOrderTest.STATIC_NAME));
        hazelcastInstance = createHazelcastInstance(staticHazelcastConfig);
        Assert.assertEquals("Static wildcard settings should match", DynamicConfigSearchOrderTest.STATIC_WILDCARD_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.NON_EXISTENT_NAME).getName());
        Assert.assertEquals("Static exact name should match", DynamicConfigSearchOrderTest.STATIC_NAME, hazelcastInstance.getConfig().getMapConfig(DynamicConfigSearchOrderTest.STATIC_NAME).getName());
    }
}

