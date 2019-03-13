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
package com.hazelcast.client.config;


import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvalidConfigurationClientTest {
    @Test
    public void testWhenXmlValid() {
        String xml = getDraftXml();
        buildConfig(xml);
    }

    @Test
    public void testWhenValid_SmartRoutingEnabled() {
        buildConfig("smart-routing-enabled", "false");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInValid_SmartRoutingEnabled() {
        buildConfig("smart-routing-enabled", "false1");
    }

    @Test
    public void testWhenValid_RedoOperationEnabled() {
        buildConfig("redo-operation-enabled", "true");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInValid_RedoOperationEnabled() {
        buildConfig("redo-operation-enabled", "tr1ue");
    }

    @Test
    public void testWhenValid_SocketInterceptorEnabled() {
        buildConfig("socket-interceptor-enabled", "true");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInvalid_SocketInterceptorEnabled() {
        buildConfig("socket-interceptor-enabled", "ttrue");
    }

    @Test
    public void testWhenValid_AwsEnabled() {
        buildConfig("aws-enabled", "true");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInvalid_AwsEnabled() {
        buildConfig("aws-enabled", "falsee");
    }

    @Test
    public void testWhenValid_InsideAwsEnabled() {
        buildConfig("inside-aws-enabled", "true");
    }

    @Test
    public void WhenValid_ExecutorPoolSize() {
        buildConfig("executor-pool-size", "17");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInvalid_ExecutorPoolSize() {
        buildConfig("executor-pool-size", "0");
    }

    @Test
    public void WhenValid_CredentialsClassName() {
        buildConfig("credentials-class-name", "com.hazelcast.client.config.Credentials");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInvalid_CredentialsClassName() {
        buildConfig("credentials-class-name", " com.hazelcast.client.config.Credentials");
    }

    @Test
    public void WhenValid_ListenerClassName() {
        buildConfig("listener-class-name", "com.hazelcast.client.config.Listener");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInvalid_ListenerClassName() {
        buildConfig("listener-class-name", " com.hazelcast.client.config.Listener");
    }

    @Test
    public void WhenValid_NativeByteOrder() {
        buildConfig("use-native-byte-order", "true");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInValid_NativeByteOrder() {
        buildConfig("use-native-byte-order", "truue");
    }

    @Test
    public void WhenValid_LoadBalancerType() {
        buildConfig("load-balancer-type", "round-robin");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInvalid_LoadBalancerType() {
        buildConfig("load-balancer-type", "roundrobin");
    }

    @Test
    public void WhenValid_EvictionPolicy() {
        buildConfig("eviction-policy", "LRU");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInvalid_EvictionPolicy() {
        buildConfig("eviction-policy", "none");
    }

    @Test
    public void WhenValid_NearCacheInMemoryFormat() {
        buildConfig("near-cache-in-memory-format", "OBJECT");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenInvalid_NearCacheInMemoryFormat() {
        buildConfig("near-cache-in-memory-format", "binaryyy");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInvalid_NearCacheTTLSeconds() {
        buildConfig("near-cache-time-to-live-seconds", "-1");
    }

    @Test
    public void testWhenValid_NearCacheTTLSeconds() {
        buildConfig("near-cache-time-to-live-seconds", "100");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInvalid_NearCacheMaxIdleSeconds() {
        buildConfig("near-cache-max-idle-seconds", "-1");
    }

    @Test
    public void testWhenValid_NearCacheMaxIdleSeconds() {
        buildConfig("near-cache-max-idle-seconds", "100");
    }

    @Test(expected = InvalidConfigurationException.class)
    public void testWhenInvalid_NearCacheEvictionSize() {
        buildConfig("near-cache-eviction-size", "-100");
    }

    @Test
    public void testWhenValid_NearCacheEvictionSize() {
        buildConfig("near-cache-eviction-size", "100");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassPathConfigWhenNullClassLoader() {
        new ClientClasspathXmlConfig(null, "my_config.xml", System.getProperties());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassPathConfigWhenNullProperties() {
        new ClientClasspathXmlConfig(Thread.currentThread().getContextClassLoader(), "my_config.xml", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassPathConfigWhenNullResource() {
        new ClientClasspathXmlConfig(Thread.currentThread().getContextClassLoader(), null, System.getProperties());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassPathConfigWhenNonExistentConfig() {
        new ClientClasspathXmlConfig(Thread.currentThread().getContextClassLoader(), "non-existent-client-config.xml", System.getProperties());
    }

    @Test(expected = InvalidConfigurationException.class)
    public void WhenDuplicateTagsAdded() {
        String xml = "<hazelcast-client xmlns=\"http://www.hazelcast.com/schema/client-config\">\n" + (((((((((("  <network>\n" + "    <cluster-members>\n") + "      <address>127.0.0.1</address>\n") + "    </cluster-members>\n") + "  </network>\n") + "  <network>\n") + "    <cluster-members>\n") + "      <address>127.0.0.1</address>\n") + "    </cluster-members>\n") + "  </network>\n") + "</hazelcast-client>\n");
        buildConfig(xml);
    }
}

