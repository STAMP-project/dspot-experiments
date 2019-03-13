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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigValidatorNearCacheConfigTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "default";

    @Test
    public void checkNearCacheConfig_BINARY() {
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, getNearCacheConfig(InMemoryFormat.BINARY), null, false);
    }

    @Test
    public void checkNearCacheConfig_OBJECT() {
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, getNearCacheConfig(InMemoryFormat.OBJECT), null, false);
    }

    /**
     * Not supported in open source version, so test is expected to throw exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void checkNearCacheConfig_NATIVE() {
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, getNearCacheConfig(InMemoryFormat.NATIVE), null, false);
    }

    /**
     * Not supported client configuration, so test is expected to throw exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void checkNearCacheConfig_withUnsupportedClientConfig() {
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, getNearCacheConfig(InMemoryFormat.BINARY), null, true);
    }

    @Test
    public void checkNearCacheConfig_withPreLoaderConfig_onClients() {
        NearCacheConfig nearCacheConfig = getNearCacheConfig(InMemoryFormat.BINARY).setCacheLocalEntries(false);
        nearCacheConfig.getPreloaderConfig().setEnabled(true).setStoreInitialDelaySeconds(1).setStoreInitialDelaySeconds(1);
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, nearCacheConfig, null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNearCacheConfig_withPreloaderConfig_onMembers() {
        NearCacheConfig nearCacheConfig = getNearCacheConfig(InMemoryFormat.BINARY);
        nearCacheConfig.getPreloaderConfig().setEnabled(true).setStoreInitialDelaySeconds(1).setStoreInitialDelaySeconds(1);
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, nearCacheConfig, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNearCacheConfig_withLocalUpdatePolicy_CACHE_ON_UPDATE() {
        NearCacheConfig nearCacheConfig = new NearCacheConfig().setLocalUpdatePolicy(LocalUpdatePolicy.CACHE_ON_UPDATE);
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, nearCacheConfig, null, false);
    }

    @Test
    public void checkNearCacheConfig_withLocalUpdatePolicy_INVALIDATE() {
        NearCacheConfig nearCacheConfig = new NearCacheConfig().setLocalUpdatePolicy(LocalUpdatePolicy.INVALIDATE);
        ConfigValidator.checkNearCacheConfig(ConfigValidatorNearCacheConfigTest.MAP_NAME, nearCacheConfig, null, false);
    }
}

