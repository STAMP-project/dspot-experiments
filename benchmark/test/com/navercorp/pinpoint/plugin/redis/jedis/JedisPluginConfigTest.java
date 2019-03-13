/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.redis.jedis;


import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class JedisPluginConfigTest {
    @Test
    public void compatibility() {
        Properties properties = new Properties();
        ProfilerConfig profilerConfig = new DefaultProfilerConfig(properties);
        JedisPluginConfig config = new JedisPluginConfig(profilerConfig);
        // Default
        Assert.assertTrue(config.isEnable());
        Assert.assertTrue(config.isPipeline());
        Assert.assertTrue(config.isIo());
        properties.setProperty("profiler.redis", "false");
        properties.setProperty("profiler.redis.pipeline", "false");
        properties.setProperty("profiler.redis.io", "false");
        profilerConfig = new DefaultProfilerConfig(properties);
        config = new JedisPluginConfig(profilerConfig);
        // Old
        Assert.assertFalse(config.isEnable());
        Assert.assertFalse(config.isPipeline());
        Assert.assertFalse(config.isIo());
        properties.setProperty("profiler.redis.jedis.enable", "true");
        properties.setProperty("profiler.redis.jedis.pipeline", "true");
        properties.setProperty("profiler.redis.jedis.io", "true");
        profilerConfig = new DefaultProfilerConfig(properties);
        config = new JedisPluginConfig(profilerConfig);
        // New
        Assert.assertTrue(config.isEnable());
        Assert.assertTrue(config.isPipeline());
        Assert.assertTrue(config.isIo());
    }
}

