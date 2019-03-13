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
package com.hazelcast.internal.management.dto;


import WANQueueFullBehavior.THROW_EXCEPTION;
import WanPublisherState.STOPPED;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.config.ConfigCompatibilityChecker;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.EurekaConfig;
import com.hazelcast.config.GcpConfig;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WanPublisherConfigDTOTest {
    private static final ConfigCompatibilityChecker.WanPublisherConfigChecker WAN_PUBLISHER_CONFIG_CHECKER = new ConfigCompatibilityChecker.WanPublisherConfigChecker();

    @Test
    public void testSerialization() {
        Map<String, Comparable> properties = new HashMap<String, Comparable>();
        properties.put("key1", "value1");
        properties.put("key2", "value2");
        WanPublisherConfig expected = new WanPublisherConfig().setGroupName("myGroupName").setPublisherId("myPublisherId").setQueueCapacity(23).setQueueFullBehavior(THROW_EXCEPTION).setInitialPublisherState(STOPPED).setProperties(properties).setClassName("myClassName").setAwsConfig(new AwsConfig().setEnabled(true).setConnectionTimeoutSeconds(20)).setGcpConfig(new GcpConfig().setEnabled(true).setProperty("gcp", "gcp-val")).setAzureConfig(new AzureConfig().setEnabled(true).setProperty("azure", "azure-val")).setKubernetesConfig(new KubernetesConfig().setEnabled(true).setProperty("kubernetes", "kubernetes-val")).setEurekaConfig(new EurekaConfig().setEnabled(true).setProperty("eureka", "eureka-val")).setDiscoveryConfig(new DiscoveryConfig()).setWanSyncConfig(new WanSyncConfig());
        WanPublisherConfig actual = cloneThroughJson(expected);
        Assert.assertTrue(((("Expected: " + expected) + ", got:") + actual), WanPublisherConfigDTOTest.WAN_PUBLISHER_CONFIG_CHECKER.check(expected, actual));
    }

    @Test
    public void testDefault() {
        WanPublisherConfig expected = new WanPublisherConfig();
        WanPublisherConfig actual = cloneThroughJson(expected);
        Assert.assertTrue(((("Expected: " + expected) + ", got:") + actual), WanPublisherConfigDTOTest.WAN_PUBLISHER_CONFIG_CHECKER.check(expected, actual));
    }
}

