/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.registry.zk;


import RpcConstants.CONFIG_KEY_APP_NAME;
import RpcConstants.CONFIG_KEY_SERIALIZATION;
import RpcConstants.CONFIG_KEY_TIMEOUT;
import com.alipay.sofa.rpc.client.ProviderGroup;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.listener.ConfigListener;
import com.alipay.sofa.rpc.listener.ProviderInfoListener;
import com.alipay.sofa.rpc.registry.zk.base.BaseZkTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class ZookeeperRegistryTest extends BaseZkTest {
    private static RegistryConfig registryConfig;

    private static ZookeeperRegistry registry;

    /**
     * ??Zookeeper Provider Observer
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProviderObserver() throws Exception {
        int timeoutPerSub = 1000;
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setRegister(true).setRegistry(ZookeeperRegistryTest.registryConfig).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        // ??
        ZookeeperRegistryTest.registry.register(provider);
        ConsumerConfig<?> consumer = new ConsumerConfig();
        consumer.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        // ??
        CountDownLatch latch = new CountDownLatch(1);
        ZookeeperRegistryTest.MockProviderInfoListener providerInfoListener = new ZookeeperRegistryTest.MockProviderInfoListener();
        providerInfoListener.setCountDownLatch(latch);
        consumer.setProviderInfoListener(providerInfoListener);
        List<ProviderGroup> all = ZookeeperRegistryTest.registry.subscribe(consumer);
        providerInfoListener.updateAllProviders(all);
        Map<String, ProviderInfo> ps = providerInfoListener.getData();
        Assert.assertEquals("after register: 1", 1, ps.size());
        // ?? ???uniqueId
        ConsumerConfig<?> consumerNoUniqueId = new ConsumerConfig();
        consumerNoUniqueId.setInterfaceId("com.alipay.xxx.TestService").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        consumerNoUniqueId.setProviderInfoListener(providerInfoListener);
        all = ZookeeperRegistryTest.registry.subscribe(consumerNoUniqueId);
        providerInfoListener.updateAllProviders(all);
        ps = providerInfoListener.getData();
        Assert.assertEquals("wrong uniqueId: 0", 0, ps.size());
        // ???
        latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        ZookeeperRegistryTest.registry.unRegister(provider);
        latch.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        Assert.assertEquals("after unregister: 0", 0, ps.size());
        // ???2????????
        latch = new CountDownLatch(2);
        providerInfoListener.setCountDownLatch(latch);
        provider.getServer().add(new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12201));
        ZookeeperRegistryTest.registry.register(provider);
        latch.await((timeoutPerSub * 2), TimeUnit.MILLISECONDS);
        Assert.assertEquals("after register two servers: 2", 2, ps.size());
        // ????
        ConsumerConfig<?> consumer2 = new ConsumerConfig();
        consumer2.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        CountDownLatch latch2 = new CountDownLatch(1);
        ZookeeperRegistryTest.MockProviderInfoListener providerInfoListener2 = new ZookeeperRegistryTest.MockProviderInfoListener();
        providerInfoListener2.setCountDownLatch(latch2);
        consumer2.setProviderInfoListener(providerInfoListener2);
        providerInfoListener2.updateAllProviders(ZookeeperRegistryTest.registry.subscribe(consumer2));
        latch2.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        Map<String, ProviderInfo> ps2 = providerInfoListener2.getData();
        Assert.assertEquals("after register duplicate: 2", 2, ps2.size());
        // ?????1
        ZookeeperRegistryTest.registry.unSubscribe(consumer);
        // ???????????2???
        latch = new CountDownLatch(2);
        providerInfoListener2.setCountDownLatch(latch);
        List<ProviderConfig> providerConfigList = new ArrayList<ProviderConfig>();
        providerConfigList.add(provider);
        ZookeeperRegistryTest.registry.batchUnRegister(providerConfigList);
        latch.await((timeoutPerSub * 2), TimeUnit.MILLISECONDS);
        Assert.assertEquals("after unregister: 0", 0, ps2.size());
        // ??????
        List<ConsumerConfig> consumerConfigList = new ArrayList<ConsumerConfig>();
        consumerConfigList.add(consumer2);
        ZookeeperRegistryTest.registry.batchUnSubscribe(consumerConfigList);
    }

    /**
     * ??Zookeeper Config Observer
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConfigObserver() throws InterruptedException {
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> providerConfig = new ProviderConfig();
        providerConfig.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setRegister(true).setRegistry(ZookeeperRegistryTest.registryConfig).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        // ??Provider Config
        ZookeeperRegistryTest.registry.register(providerConfig);
        // ??Provider Config
        CountDownLatch latch = new CountDownLatch(1);
        ZookeeperRegistryTest.MockConfigListener configListener = new ZookeeperRegistryTest.MockConfigListener();
        configListener.setCountDownLatch(latch);
        ZookeeperRegistryTest.registry.subscribeConfig(providerConfig, configListener);
        configListener.attrUpdated(Collections.singletonMap("timeout", "2000"));
        Map<String, String> configData = configListener.getData();
        Assert.assertEquals(1, configData.size());
        configListener.attrUpdated(Collections.singletonMap("uniqueId", "unique234Id"));
        configData = configListener.getData();
        Assert.assertEquals(2, configData.size());
        ConsumerConfig<?> consumerConfig = new ConsumerConfig();
        consumerConfig.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        // ??Consumer Config
        latch = new CountDownLatch(1);
        configListener = new ZookeeperRegistryTest.MockConfigListener();
        configListener.setCountDownLatch(latch);
        ZookeeperRegistryTest.registry.subscribeConfig(consumerConfig, configListener);
        configListener.attrUpdated(Collections.singletonMap(CONFIG_KEY_TIMEOUT, "3333"));
        configData = configListener.getData();
        Assert.assertEquals(1, configData.size());
        configListener.attrUpdated(Collections.singletonMap("uniqueId", "unique234Id"));
        configData = configListener.getData();
        Assert.assertEquals(2, configData.size());
        latch.await(2000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, configData.size());
        ZookeeperRegistryTest.registry.unRegister(providerConfig);
    }

    /**
     * ??Zookeeper Override Observer
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOverrideObserver() throws InterruptedException {
        ConsumerConfig<?> consumerConfig = new ConsumerConfig();
        consumerConfig.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        // ??Consumer Config
        CountDownLatch latch = new CountDownLatch(1);
        ZookeeperRegistryTest.MockConfigListener configListener = new ZookeeperRegistryTest.MockConfigListener();
        configListener.setCountDownLatch(latch);
        ZookeeperRegistryTest.registry.subscribeOverride(consumerConfig, configListener);
        Map<String, String> attributes = new ConcurrentHashMap<String, String>();
        attributes.put(CONFIG_KEY_TIMEOUT, "3333");
        attributes.put(CONFIG_KEY_APP_NAME, "test-server");
        attributes.put(CONFIG_KEY_SERIALIZATION, "java");
        configListener.attrUpdated(attributes);
        Map<String, String> configData = configListener.getData();
        Assert.assertEquals(3, configData.size());
        consumerConfig.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server1")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(5555);
        configListener = new ZookeeperRegistryTest.MockConfigListener();
        configListener.setCountDownLatch(latch);
        ZookeeperRegistryTest.registry.subscribeOverride(consumerConfig, configListener);
        attributes.put(CONFIG_KEY_TIMEOUT, "4444");
        attributes.put(CONFIG_KEY_APP_NAME, "test-server2");
        configListener.attrUpdated(attributes);
        configData = configListener.getData();
        Assert.assertEquals(3, configData.size());
        latch.await(2000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(3, configData.size());
    }

    private static class MockProviderInfoListener implements ProviderInfoListener {
        ConcurrentMap<String, ProviderInfo> ps = new ConcurrentHashMap<String, ProviderInfo>();

        private CountDownLatch countDownLatch;

        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void addProvider(ProviderGroup providerGroup) {
            for (ProviderInfo providerInfo : providerGroup.getProviderInfos()) {
                ps.put((((providerInfo.getHost()) + ":") + (providerInfo.getPort())), providerInfo);
            }
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
                countDownLatch = null;
            }
        }

        @Override
        public void removeProvider(ProviderGroup providerGroup) {
            for (ProviderInfo providerInfo : providerGroup.getProviderInfos()) {
                ps.remove((((providerInfo.getHost()) + ":") + (providerInfo.getPort())));
            }
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
                countDownLatch = null;
            }
        }

        @Override
        public void updateProviders(ProviderGroup providerGroup) {
            ps.clear();
            for (ProviderInfo providerInfo : providerGroup.getProviderInfos()) {
                ps.put((((providerInfo.getHost()) + ":") + (providerInfo.getPort())), providerInfo);
            }
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
                countDownLatch = null;
            }
        }

        @Override
        public void updateAllProviders(List<ProviderGroup> providerGroups) {
            ps.clear();
            for (ProviderGroup providerGroup : providerGroups) {
                for (ProviderInfo providerInfo : providerGroup.getProviderInfos()) {
                    ps.put((((providerInfo.getHost()) + ":") + (providerInfo.getPort())), providerInfo);
                }
            }
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
                countDownLatch = null;
            }
        }

        public Map<String, ProviderInfo> getData() {
            return ps;
        }
    }

    private static class MockConfigListener implements ConfigListener {
        ConcurrentMap<String, String> concurrentHashMap = new ConcurrentHashMap<String, String>();

        private CountDownLatch countDownLatch;

        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void configChanged(Map newValue) {
        }

        @Override
        public void attrUpdated(Map newValue) {
            for (Object property : newValue.keySet()) {
                concurrentHashMap.put(StringUtils.toString(property), StringUtils.toString(newValue.get(property)));
                if ((countDownLatch) != null) {
                    countDownLatch.countDown();
                    countDownLatch = null;
                }
            }
        }

        public Map<String, String> getData() {
            return concurrentHashMap;
        }
    }
}

