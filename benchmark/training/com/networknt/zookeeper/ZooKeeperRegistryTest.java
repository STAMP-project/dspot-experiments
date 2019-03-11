/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package com.networknt.zookeeper;


import ZkNodeType.AVAILABLE_SERVER;
import ZkNodeType.UNAVAILABLE_SERVER;
import com.networknt.registry.URL;
import com.networknt.registry.support.command.ServiceListener;
import com.networknt.zookeeper.client.ZooKeeperClient;
import java.util.List;
import org.apache.curator.test.TestingServer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Hu
 */
public class ZooKeeperRegistryTest {
    private static ZooKeeperRegistry registry;

    private static URL serviceUrl;

    private static URL clientUrl;

    private static ZooKeeperClient client;

    private static String service = "com.networknt.light.demoService";

    private static TestingServer zookeeper;

    @Test
    public void subAndUnsubService() throws Exception {
        ServiceListener serviceListener = new ServiceListener() {
            @Override
            public void notifyService(URL refUrl, URL registryUrl, List<URL> urls) {
                if (!(urls.isEmpty())) {
                    Assert.assertTrue(urls.contains(ZooKeeperRegistryTest.serviceUrl));
                }
            }
        };
        ZooKeeperRegistryTest.registry.subscribeService(ZooKeeperRegistryTest.clientUrl, serviceListener);
        Assert.assertTrue(containsServiceListener(ZooKeeperRegistryTest.clientUrl, serviceListener));
        ZooKeeperRegistryTest.registry.doRegister(ZooKeeperRegistryTest.serviceUrl);
        ZooKeeperRegistryTest.registry.doAvailable(ZooKeeperRegistryTest.serviceUrl);
        Thread.sleep(2000);
        ZooKeeperRegistryTest.registry.unsubscribeService(ZooKeeperRegistryTest.clientUrl, serviceListener);
        Assert.assertFalse(containsServiceListener(ZooKeeperRegistryTest.clientUrl, serviceListener));
    }

    @Test
    public void discoverService() throws Exception {
        ZooKeeperRegistryTest.registry.doRegister(ZooKeeperRegistryTest.serviceUrl);
        List<URL> results = ZooKeeperRegistryTest.registry.discoverService(ZooKeeperRegistryTest.clientUrl);
        Assert.assertTrue(results.isEmpty());
        ZooKeeperRegistryTest.registry.doAvailable(ZooKeeperRegistryTest.serviceUrl);
        results = ZooKeeperRegistryTest.registry.discoverService(ZooKeeperRegistryTest.clientUrl);
        Assert.assertTrue(results.contains(ZooKeeperRegistryTest.serviceUrl));
    }

    @Test
    public void doRegisterAndAvailable() throws Exception {
        String node = ZooKeeperRegistryTest.serviceUrl.getServerPortStr();
        List<String> available;
        List<String> unavailable;
        String unavailablePath = ZkUtils.toNodeTypePath(ZooKeeperRegistryTest.serviceUrl, UNAVAILABLE_SERVER);
        String availablePath = ZkUtils.toNodeTypePath(ZooKeeperRegistryTest.serviceUrl, AVAILABLE_SERVER);
        ZooKeeperRegistryTest.registry.doRegister(ZooKeeperRegistryTest.serviceUrl);
        unavailable = ZooKeeperRegistryTest.client.getChildren(unavailablePath);
        Assert.assertTrue(unavailable.contains(node));
        ZooKeeperRegistryTest.registry.doAvailable(ZooKeeperRegistryTest.serviceUrl);
        unavailable = ZooKeeperRegistryTest.client.getChildren(unavailablePath);
        Assert.assertFalse(unavailable.contains(node));
        available = ZooKeeperRegistryTest.client.getChildren(availablePath);
        Assert.assertTrue(available.contains(node));
        ZooKeeperRegistryTest.registry.doUnavailable(ZooKeeperRegistryTest.serviceUrl);
        unavailable = ZooKeeperRegistryTest.client.getChildren(unavailablePath);
        Assert.assertTrue(unavailable.contains(node));
        available = ZooKeeperRegistryTest.client.getChildren(availablePath);
        Assert.assertFalse(available.contains(node));
        ZooKeeperRegistryTest.registry.doUnregister(ZooKeeperRegistryTest.serviceUrl);
        unavailable = ZooKeeperRegistryTest.client.getChildren(unavailablePath);
        Assert.assertFalse(unavailable.contains(node));
        available = ZooKeeperRegistryTest.client.getChildren(availablePath);
        Assert.assertFalse(available.contains(node));
    }
}

