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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AliasedDiscoveryConfigUtilsTest {
    @Test
    public void supports() {
        Assert.assertTrue(AliasedDiscoveryConfigUtils.supports("gcp"));
        Assert.assertFalse(AliasedDiscoveryConfigUtils.supports("unknown"));
    }

    @Test
    public void tagFor() {
        Assert.assertEquals("gcp", AliasedDiscoveryConfigUtils.tagFor(new GcpConfig()));
        Assert.assertEquals("aws", AliasedDiscoveryConfigUtils.tagFor(new AwsConfig()));
        Assert.assertEquals("aws", AliasedDiscoveryConfigUtils.tagFor(new AwsConfig() {}));
        Assert.assertNull(AliasedDiscoveryConfigUtils.tagFor(new AliasedDiscoveryConfigUtilsTest.DummyAliasedDiscoveryConfig(null)));
    }

    @Test
    public void createDiscoveryStrategyConfigsFromJoinConfig() {
        // given
        JoinConfig config = new JoinConfig();
        setEnabled(true);
        // when
        List<DiscoveryStrategyConfig> result = AliasedDiscoveryConfigUtils.createDiscoveryStrategyConfigs(config);
        // then
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("com.hazelcast.gcp.GcpDiscoveryStrategy", result.get(0).getClassName());
    }

    @Test
    public void createDiscoveryStrategyConfigsFromWanPublisherConfig() {
        // given
        WanPublisherConfig config = new WanPublisherConfig();
        setEnabled(true);
        // when
        List<DiscoveryStrategyConfig> result = AliasedDiscoveryConfigUtils.createDiscoveryStrategyConfigs(config);
        // then
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("com.hazelcast.gcp.GcpDiscoveryStrategy", result.get(0).getClassName());
    }

    @Test
    public void map() {
        // given
        List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigs = new ArrayList<AliasedDiscoveryConfig<?>>();
        aliasedDiscoveryConfigs.add(new GcpConfig().setEnabled(true).setProperty("projects", "hazelcast-33").setProperty("zones", "us-east1-b"));
        aliasedDiscoveryConfigs.add(setEnabled(true).setProperty("access-key", "someAccessKey").setProperty("secret-key", "someSecretKey").setProperty("region", "eu-central-1"));
        // when
        List<DiscoveryStrategyConfig> discoveryConfigs = AliasedDiscoveryConfigUtils.map(aliasedDiscoveryConfigs);
        // then
        DiscoveryStrategyConfig gcpConfig = discoveryConfigs.get(0);
        Assert.assertEquals("com.hazelcast.gcp.GcpDiscoveryStrategy", gcpConfig.getClassName());
        Assert.assertEquals("hazelcast-33", gcpConfig.getProperties().get("projects"));
        Assert.assertEquals("us-east1-b", gcpConfig.getProperties().get("zones"));
        DiscoveryStrategyConfig awsConfig = discoveryConfigs.get(1);
        Assert.assertEquals("com.hazelcast.aws.AwsDiscoveryStrategy", awsConfig.getClassName());
        Assert.assertEquals("someAccessKey", awsConfig.getProperties().get("access-key"));
        Assert.assertEquals("someSecretKey", awsConfig.getProperties().get("secret-key"));
        Assert.assertEquals("eu-central-1", awsConfig.getProperties().get("region"));
    }

    @Test
    public void skipNotEnabledConfigs() {
        // given
        List<AliasedDiscoveryConfig<?>> configs = new ArrayList<AliasedDiscoveryConfig<?>>();
        configs.add(new GcpConfig().setEnabled(false));
        // when
        List<DiscoveryStrategyConfig> discoveryConfigs = AliasedDiscoveryConfigUtils.map(configs);
        // then
        Assert.assertTrue(discoveryConfigs.isEmpty());
    }

    @Test
    public void skipPropertyWithNullKey() {
        // given
        List<AliasedDiscoveryConfig<?>> configs = new ArrayList<AliasedDiscoveryConfig<?>>();
        configs.add(new GcpConfig().setEnabled(true).setProperty(null, "value"));
        // when
        List<DiscoveryStrategyConfig> discoveryConfigs = AliasedDiscoveryConfigUtils.map(configs);
        // then
        Assert.assertTrue(discoveryConfigs.get(0).getProperties().isEmpty());
    }

    @Test(expected = InvalidConfigurationException.class)
    public void validateUnknownEnvironments() {
        // given
        AliasedDiscoveryConfig aliasedDiscoveryConfig = new AliasedDiscoveryConfigUtilsTest.DummyAliasedDiscoveryConfig("invalid-tag") {}.setEnabled(true);
        List<AliasedDiscoveryConfig<?>> configs = new ArrayList<AliasedDiscoveryConfig<?>>();
        configs.add(aliasedDiscoveryConfig);
        // when
        AliasedDiscoveryConfigUtils.map(configs);
        // then
        // throws exception
    }

    @Test
    public void getConfigByTagFromJoinConfig() {
        // given
        JoinConfig config = new JoinConfig();
        // when
        AliasedDiscoveryConfig result = AliasedDiscoveryConfigUtils.getConfigByTag(config, "gcp");
        // then
        Assert.assertEquals(GcpConfig.class, result.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getConfigByInvalidTagFromJoinConfig() {
        // given
        JoinConfig config = new JoinConfig();
        // when
        AliasedDiscoveryConfigUtils.getConfigByTag(config, "unknown");
        // then
        // throw exception
    }

    @Test
    public void getConfigByTagFromWanPublisherConfig() {
        // given
        WanPublisherConfig config = new WanPublisherConfig();
        // when
        AliasedDiscoveryConfig result = AliasedDiscoveryConfigUtils.getConfigByTag(config, "gcp");
        // then
        Assert.assertEquals(GcpConfig.class, result.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getConfigByInvalidTagFromWanPublisherConfig() {
        // given
        WanPublisherConfig config = new WanPublisherConfig();
        // when
        AliasedDiscoveryConfigUtils.getConfigByTag(config, "unknown");
        // then
        // throw exception
    }

    @Test
    public void allUsePublicAddressTrue() {
        // given
        AwsConfig awsConfig = new AwsConfig().setEnabled(true).setUsePublicIp(true);
        GcpConfig gcpConfig = new GcpConfig().setEnabled(true).setUsePublicIp(true);
        List<AliasedDiscoveryConfig<?>> configs = Arrays.asList(awsConfig, gcpConfig);
        // when
        boolean result = AliasedDiscoveryConfigUtils.allUsePublicAddress(configs);
        // then
        Assert.assertTrue(result);
    }

    @Test
    public void allUsePublicAddressFalse() {
        // given
        AwsConfig awsConfig = new AwsConfig().setEnabled(true).setUsePublicIp(true);
        GcpConfig gcpConfig = new GcpConfig().setEnabled(true).setUsePublicIp(false);
        List<AliasedDiscoveryConfig<?>> configs = Arrays.asList(awsConfig, gcpConfig);
        // when
        boolean result = AliasedDiscoveryConfigUtils.allUsePublicAddress(configs);
        // then
        Assert.assertFalse(result);
    }

    @Test
    public void allUsePublicAddressEmpty() {
        // given
        List<AliasedDiscoveryConfig<?>> configs = new ArrayList<AliasedDiscoveryConfig<?>>();
        // when
        boolean result = AliasedDiscoveryConfigUtils.allUsePublicAddress(configs);
        // then
        Assert.assertFalse(result);
    }

    private static class DummyAliasedDiscoveryConfig extends AliasedDiscoveryConfig {
        protected DummyAliasedDiscoveryConfig(String tag) {
            super(tag);
        }

        @Override
        public int getId() {
            throw new UnsupportedOperationException("Deserialization not supported!");
        }
    }
}

