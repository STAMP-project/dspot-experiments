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
package com.alipay.sofa.rpc.registry.mesh;


import com.alipay.sofa.rpc.client.ProviderGroup;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.listener.ProviderInfoListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class MeshRegistryTest extends BaseMeshTest {
    private static RegistryConfig registryConfig;

    private static MeshRegistry registry;

    @Test
    public void testOnlyPublish() {
        Field registedAppField = null;
        try {
            registedAppField = MeshRegistry.class.getDeclaredField("registedApp");
            registedAppField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Boolean registedAppValue = null;
        // in case of effected by other case.
        try {
            registedAppValue = ((Boolean) (registedAppField.get(MeshRegistryTest.registry)));
            registedAppField.set(MeshRegistryTest.registry, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        BaseMeshTest.LOGGER.info(("current registedAppValue is " + registedAppValue));
        Assert.assertTrue((!registedAppValue));
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setRegister(true).setRegistry(MeshRegistryTest.registryConfig).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        MeshRegistryTest.registry.register(provider);
        try {
            registedAppValue = ((Boolean) (registedAppField.get(MeshRegistryTest.registry)));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        BaseMeshTest.LOGGER.info(("final registedAppValue is " + registedAppValue));
        Assert.assertTrue(registedAppValue);
    }

    @Test
    public void testAll() throws Exception {
        int timeoutPerSub = 1000;
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setRegister(true).setRegistry(MeshRegistryTest.registryConfig).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        // ??
        MeshRegistryTest.registry.register(provider);
        ConsumerConfig<?> consumer = new ConsumerConfig();
        consumer.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        String tag0 = MeshRegistryHelper.buildMeshKey(provider, serverConfig.getProtocol());
        String tag1 = MeshRegistryHelper.buildMeshKey(consumer, consumer.getProtocol());
        Assert.assertEquals(tag1, tag0);
        // ??
        MeshRegistryTest.MockProviderInfoListener providerInfoListener = new MeshRegistryTest.MockProviderInfoListener();
        consumer.setProviderInfoListener(providerInfoListener);
        List<ProviderGroup> groups = MeshRegistryTest.registry.subscribe(consumer);
        providerInfoListener.updateAllProviders(groups);
        Map<String, ProviderGroup> ps = providerInfoListener.getData();
        Assert.assertTrue(((ps.size()) == 1));
        // ???
        CountDownLatch latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        MeshRegistryTest.registry.unRegister(provider);
        latch.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        // mesh ??????.
        Assert.assertTrue(((ps.size()) == 1));
        // ???2????????
        latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        provider.getServer().add(new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12201));
        MeshRegistryTest.registry.register(provider);
        latch.await((timeoutPerSub * 2), TimeUnit.MILLISECONDS);
        // ????
        ConsumerConfig<?> consumer2 = new ConsumerConfig();
        consumer2.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        CountDownLatch latch2 = new CountDownLatch(1);
        MeshRegistryTest.MockProviderInfoListener providerInfoListener2 = new MeshRegistryTest.MockProviderInfoListener();
        providerInfoListener2.setCountDownLatch(latch2);
        consumer2.setProviderInfoListener(providerInfoListener2);
        List<ProviderGroup> groups2 = MeshRegistryTest.registry.subscribe(consumer2);
        providerInfoListener2.updateAllProviders(groups2);
        Map<String, ProviderGroup> ps2 = providerInfoListener2.getData();
        Assert.assertTrue(((ps2.size()) == 1));
        // ?????1
        MeshRegistryTest.registry.unSubscribe(consumer);
        // ???????????2???
        latch = new CountDownLatch(1);
        providerInfoListener2.setCountDownLatch(latch);
        List<ProviderConfig> providerConfigList = new ArrayList<ProviderConfig>();
        providerConfigList.add(provider);
        MeshRegistryTest.registry.batchUnRegister(providerConfigList);
        latch.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((ps2.size()) == 1));
        // ??????
        List<ConsumerConfig> consumerConfigList = new ArrayList<ConsumerConfig>();
        consumerConfigList.add(consumer2);
        MeshRegistryTest.registry.batchUnSubscribe(consumerConfigList);
    }

    private static class MockProviderInfoListener implements ProviderInfoListener {
        Map<String, ProviderGroup> ps = new HashMap<String, ProviderGroup>();

        private CountDownLatch countDownLatch;

        public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void addProvider(ProviderGroup providerGroup) {
        }

        @Override
        public void removeProvider(ProviderGroup providerGroup) {
        }

        @Override
        public void updateProviders(ProviderGroup providerGroup) {
            ps.put(providerGroup.getName(), providerGroup);
            if ((countDownLatch) != null) {
                countDownLatch.countDown();
                countDownLatch = null;
            }
        }

        @Override
        public void updateAllProviders(List<ProviderGroup> providerGroups) {
            for (ProviderGroup providerGroup : providerGroups) {
                ps.put(providerGroup.getName(), providerGroup);
            }
        }

        public Map<String, ProviderGroup> getData() {
            return ps;
        }
    }
}

