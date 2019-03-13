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
package com.alipay.sofa.rpc.registry.local;


import RpcConstants.ADDRESS_DEFAULT_GROUP;
import com.alipay.sofa.rpc.client.ProviderGroup;
import com.alipay.sofa.rpc.common.utils.FileUtils;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.listener.ProviderInfoListener;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import com.alipay.sofa.rpc.registry.RegistryFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class LocalRegistryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalRegistryTest.class);

    private static String filePath = (((System.getProperty("user.home")) + (File.separator)) + "localFileTest") + (new Random().nextInt(1000));

    private static String file = ((LocalRegistryTest.filePath) + (File.separator)) + "localRegistry.reg";

    private static RegistryConfig registryConfig;

    private static LocalRegistry registry;

    @Test
    public void testLoadFile() {
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setRegister(true).setRegistry(LocalRegistryTest.registryConfig).setServer(serverConfig);
        LocalRegistryTest.registry.register(provider);
        LocalRegistryTest.registry.destroy();
        // registry ????? provider ???????
        Assert.assertTrue(new File(LocalRegistryTest.file).exists());
        // ?????? localRegistry?????????
        RegistryConfig newRegistryConfig = // .setParameter("registry.local.scan.period", "1000")
        new RegistryConfig().setProtocol("local").setSubscribe(true).setFile(LocalRegistryTest.file).setRegister(true);
        LocalRegistry newRegistry = ((LocalRegistry) (RegistryFactory.getRegistry(newRegistryConfig)));
        newRegistry.init();
        Assert.assertFalse(newRegistry.memoryCache.isEmpty());
        // consumer ????????????
        ConsumerConfig<?> consumer = new ConsumerConfig();
        consumer.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setRegistry(LocalRegistryTest.registryConfig).setSubscribe(true);
        List<ProviderGroup> subscribe = newRegistry.subscribe(consumer);
        Assert.assertFalse(subscribe.isEmpty());
        Assert.assertFalse(subscribe.get(0).getProviderInfos().isEmpty());
    }

    @Test
    public void testAll() throws Exception {
        // test for notifyConsumer
        notifyConsumerTest();
        int timeoutPerSub = 5000;
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12200);
        ProviderConfig<?> provider = new ProviderConfig();
        provider.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setRegister(true).setRegistry(LocalRegistryTest.registryConfig).setSerialization("hessian2").setServer(serverConfig).setWeight(222).setTimeout(3000);
        // ??
        LocalRegistryTest.registry.register(provider);
        ConsumerConfig<?> consumer = new ConsumerConfig();
        consumer.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        String tag0 = LocalRegistryHelper.buildListDataId(provider, serverConfig.getProtocol());
        String tag1 = LocalRegistryHelper.buildListDataId(consumer, consumer.getProtocol());
        Assert.assertEquals(tag1, tag0);
        String content = FileUtils.file2String(new File(LocalRegistryTest.file));
        Assert.assertTrue(content.startsWith(tag0));
        // ??
        LocalRegistryTest.MockProviderInfoListener providerInfoListener = new LocalRegistryTest.MockProviderInfoListener();
        consumer.setProviderInfoListener(providerInfoListener);
        List<ProviderGroup> groups = LocalRegistryTest.registry.subscribe(consumer);
        providerInfoListener.updateAllProviders(groups);
        Map<String, ProviderGroup> ps = providerInfoListener.getData();
        Assert.assertTrue(((ps.size()) > 0));
        Assert.assertNotNull(ps.get(ADDRESS_DEFAULT_GROUP));
        Assert.assertTrue(((ps.get(ADDRESS_DEFAULT_GROUP).size()) == 1));
        // ???
        CountDownLatch latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        LocalRegistryTest.registry.unRegister(provider);
        latch.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((ps.size()) > 0));
        Assert.assertNotNull(ps.get(ADDRESS_DEFAULT_GROUP));
        Assert.assertTrue(((ps.get(ADDRESS_DEFAULT_GROUP).size()) == 0));
        // ???2????????
        latch = new CountDownLatch(1);
        providerInfoListener.setCountDownLatch(latch);
        provider.getServer().add(new ServerConfig().setProtocol("bolt").setHost("0.0.0.0").setPort(12201));
        LocalRegistryTest.registry.register(provider);
        latch.await((timeoutPerSub * 2), TimeUnit.MILLISECONDS);
        Assert.assertTrue(((ps.size()) > 0));
        Assert.assertNotNull(ps.get(ADDRESS_DEFAULT_GROUP));
        Assert.assertTrue(((ps.get(ADDRESS_DEFAULT_GROUP).size()) == 2));
        // ????
        ConsumerConfig<?> consumer2 = new ConsumerConfig();
        consumer2.setInterfaceId("com.alipay.xxx.TestService").setUniqueId("unique123Id").setApplication(new ApplicationConfig().setAppName("test-server")).setProxy("javassist").setSubscribe(true).setSerialization("java").setInvokeType("sync").setTimeout(4444);
        CountDownLatch latch2 = new CountDownLatch(1);
        LocalRegistryTest.MockProviderInfoListener providerInfoListener2 = new LocalRegistryTest.MockProviderInfoListener();
        providerInfoListener2.setCountDownLatch(latch2);
        consumer2.setProviderInfoListener(providerInfoListener2);
        List<ProviderGroup> groups2 = LocalRegistryTest.registry.subscribe(consumer2);
        providerInfoListener2.updateAllProviders(groups2);
        Map<String, ProviderGroup> ps2 = providerInfoListener2.getData();
        Assert.assertTrue(((ps2.size()) > 0));
        Assert.assertNotNull(ps2.get(ADDRESS_DEFAULT_GROUP));
        Assert.assertTrue(((ps2.get(ADDRESS_DEFAULT_GROUP).size()) == 2));
        Assert.assertTrue(((LocalRegistryTest.registry.memoryCache.get(tag1).size()) == 2));
        // ?????1
        LocalRegistryTest.registry.unSubscribe(consumer);
        List<ConsumerConfig> callback = LocalRegistryTest.registry.notifyListeners.get(tag1);
        Assert.assertFalse(callback.contains(consumer));
        Assert.assertTrue(((callback.size()) == 1));
        // ???????????2???
        latch = new CountDownLatch(1);
        providerInfoListener2.setCountDownLatch(latch);
        List<ProviderConfig> providerConfigList = new ArrayList<ProviderConfig>();
        providerConfigList.add(provider);
        LocalRegistryTest.registry.batchUnRegister(providerConfigList);
        latch.await(timeoutPerSub, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((ps2.size()) > 0));
        Assert.assertNotNull(ps2.get(ADDRESS_DEFAULT_GROUP));
        Assert.assertTrue(((ps2.get(ADDRESS_DEFAULT_GROUP).size()) == 0));
        Assert.assertTrue(((LocalRegistryTest.registry.notifyListeners.size()) == 1));
        // ??????
        List<ConsumerConfig> consumerConfigList = new ArrayList<ConsumerConfig>();
        consumerConfigList.add(consumer2);
        LocalRegistryTest.registry.batchUnSubscribe(consumerConfigList);
        Assert.assertTrue(((LocalRegistryTest.registry.notifyListeners.size()) == 0));
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

