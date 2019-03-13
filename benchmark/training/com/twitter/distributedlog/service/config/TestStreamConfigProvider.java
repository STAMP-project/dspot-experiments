/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.service.config;


import com.google.common.base.Optional;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.config.DynamicDistributedLogConfiguration;
import com.twitter.distributedlog.config.PropertiesWriter;
import com.twitter.distributedlog.service.streamset.IdentityStreamPartitionConverter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Test;


public class TestStreamConfigProvider {
    private static final String DEFAULT_CONFIG_DIR = "conf";

    private final String defaultConfigPath;

    private final ScheduledExecutorService configExecutorService;

    public TestStreamConfigProvider() throws Exception {
        this.configExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("DistributedLogService-Dyncfg-%d").build());
        PropertiesWriter writer = new PropertiesWriter();
        writer.save();
        this.defaultConfigPath = writer.getFile().getPath();
    }

    @Test(timeout = 60000)
    public void testServiceProviderWithConfigRouters() throws Exception {
        getServiceProvider(new IdentityStreamPartitionConverter());
    }

    @Test(timeout = 60000)
    public void testServiceProviderWithMissingConfig() throws Exception {
        StreamConfigProvider provider = getServiceProvider(new IdentityStreamPartitionConverter());
        Optional<DynamicDistributedLogConfiguration> config = provider.getDynamicStreamConfig("stream1");
        Assert.assertTrue(config.isPresent());
    }

    @Test(timeout = 60000)
    public void testServiceProviderWithDefaultConfigPath() throws Exception {
        // Default config with property set.
        PropertiesWriter writer1 = new PropertiesWriter();
        writer1.setProperty("rpsStreamAcquireServiceLimit", "191919");
        writer1.save();
        String fallbackConfPath1 = writer1.getFile().getPath();
        StreamConfigProvider provider1 = getServiceProvider(new IdentityStreamPartitionConverter(), TestStreamConfigProvider.DEFAULT_CONFIG_DIR, fallbackConfPath1);
        Optional<DynamicDistributedLogConfiguration> config1 = provider1.getDynamicStreamConfig("stream1");
        // Empty default config.
        PropertiesWriter writer2 = new PropertiesWriter();
        writer2.save();
        String fallbackConfPath2 = writer2.getFile().getPath();
        StreamConfigProvider provider2 = getServiceProvider(new IdentityStreamPartitionConverter(), TestStreamConfigProvider.DEFAULT_CONFIG_DIR, fallbackConfPath2);
        Optional<DynamicDistributedLogConfiguration> config2 = provider2.getDynamicStreamConfig("stream1");
        Assert.assertEquals(191919, config1.get().getRpsStreamAcquireServiceLimit());
        Assert.assertEquals((-1), config2.get().getRpsStreamAcquireServiceLimit());
    }

    @Test(timeout = 60000)
    public void testDefaultProvider() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        writer.setProperty(DistributedLogConfiguration.BKDL_RETENTION_PERIOD_IN_HOURS, "99");
        writer.save();
        StreamConfigProvider provider = getDefaultProvider(writer.getFile().getPath());
        Optional<DynamicDistributedLogConfiguration> config1 = provider.getDynamicStreamConfig("stream1");
        Optional<DynamicDistributedLogConfiguration> config2 = provider.getDynamicStreamConfig("stream2");
        Assert.assertTrue(config1.isPresent());
        Assert.assertTrue(((config1.get()) == (config2.get())));
        Assert.assertEquals(99, config1.get().getRetentionPeriodHours());
    }

    @Test(timeout = 60000)
    public void testNullProvider() throws Exception {
        StreamConfigProvider provider = getNullProvider();
        Optional<DynamicDistributedLogConfiguration> config1 = provider.getDynamicStreamConfig("stream1");
        Optional<DynamicDistributedLogConfiguration> config2 = provider.getDynamicStreamConfig("stream2");
        Assert.assertFalse(config1.isPresent());
        Assert.assertTrue((config1 == config2));
    }
}

