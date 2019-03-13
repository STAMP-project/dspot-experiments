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
package com.alipay.sofa.rpc.registry.consul;


import com.alipay.sofa.rpc.client.ProviderGroup;
import com.alipay.sofa.rpc.client.ProviderInfo;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.listener.ProviderInfoListener;
import com.pszymczyk.consul.ConsulProcess;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * Test of ConsulRegistry
 *
 * @author <a href=mailto:preciousdp11@gmail.com>dingpeng</a>
 */
public class ConsulRegistryTest {
    private static ConsulProcess consul;

    private static RegistryConfig registryConfig;

    private static ConsulRegistry registry;

    @Test
    public void testAll() throws Exception {
        int timeoutPerSub = 1000;
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("localhost").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setRegister(true).setRegistry(ConsulRegistryTest.registryConfig).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        // ??
        ConsulRegistryTest.registry.register(provider);
        ConsumerConfig<?> consumer = new ConsumerConfig();
        consumer.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        // ??
        CountDownLatch latch = new CountDownLatch(1);
        ConsulRegistryTest.MockProviderInfoListener providerInfoListener = new ConsulRegistryTest.MockProviderInfoListener();
        providerInfoListener.setCountDownLatch(latch);
        consumer.setProviderInfoListener(providerInfoListener);
        List<ProviderGroup> all = ConsulRegistryTest.registry.subscribe(consumer);
        providerInfoListener.updateAllProviders(all);
        Map<String, ProviderInfo> ps = providerInfoListener.getData();
        // ?? ???uniqueId
        ConsumerConfig<?> consumerNoUniqueId = new ConsumerConfig();
        consumerNoUniqueId.setInterfaceId("com.alipay.xxx.TestService").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        consumerNoUniqueId.setProviderInfoListener(providerInfoListener);
        all = ConsulRegistryTest.registry.subscribe(consumerNoUniqueId);
        providerInfoListener.updateAllProviders(all);
        ps = providerInfoListener.getData();
        // ???
        latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        ConsulRegistryTest.registry.unRegister(provider);
        latch.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        // ???2????????
        latch = new CountDownLatch(2);
        providerInfoListener.setCountDownLatch(latch);
        provider.getServer().add(new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12201));
        ConsulRegistryTest.registry.register(provider);
        latch.await((timeoutPerSub * 2), TimeUnit.MILLISECONDS);
        // ????
        ConsumerConfig<?> consumer2 = new ConsumerConfig();
        consumer2.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        CountDownLatch latch2 = new CountDownLatch(1);
        ConsulRegistryTest.MockProviderInfoListener providerInfoListener2 = new ConsulRegistryTest.MockProviderInfoListener();
        providerInfoListener2.setCountDownLatch(latch2);
        consumer2.setProviderInfoListener(providerInfoListener2);
        providerInfoListener2.updateAllProviders(ConsulRegistryTest.registry.subscribe(consumer2));
        latch2.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        Map<String, ProviderInfo> ps2 = providerInfoListener2.getData();
        // ?????1
        ConsulRegistryTest.registry.unSubscribe(consumer);
        // ???????????2???
        latch = new CountDownLatch(2);
        providerInfoListener2.setCountDownLatch(latch);
        List<ProviderConfig> providerConfigList = new ArrayList<ProviderConfig>();
        providerConfigList.add(provider);
        ConsulRegistryTest.registry.batchUnRegister(providerConfigList);
        latch.await((timeoutPerSub * 2), TimeUnit.MILLISECONDS);
        // ??????
        List<ConsumerConfig> consumerConfigList = new ArrayList<ConsumerConfig>();
        consumerConfigList.add(consumer2);
        ConsulRegistryTest.registry.batchUnSubscribe(consumerConfigList);
    }

    private static class MockProviderInfoListener implements ProviderInfoListener {
        ConcurrentHashMap<String, ProviderInfo> ps = new ConcurrentHashMap<String, ProviderInfo>();

        private CountDownLatch countDownLatch;

        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void addProvider(ProviderGroup providerGroup) {
            for (ProviderInfo providerInfo : providerGroup.getProviderInfos()) {
                ps.put((((providerInfo.getHost()) + ":") + (providerInfo.getPort())), providerInfo);
            }
        }

        @Override
        public void removeProvider(ProviderGroup providerGroup) {
            for (ProviderInfo providerInfo : providerGroup.getProviderInfos()) {
                ps.remove((((providerInfo.getHost()) + ":") + (providerInfo.getPort())));
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
        }

        public Map<String, ProviderInfo> getData() {
            return ps;
        }
    }
}

