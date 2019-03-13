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
package org.apache.dubbo.registry.zookeeper;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.test.TestingServer;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


public class ZookeeperRegistryTest {
    private TestingServer zkServer;

    private ZookeeperRegistry zookeeperRegistry;

    private String service = "org.apache.dubbo.test.injvmServie";

    private URL serviceUrl = URL.valueOf((("zookeeper://zookeeper/" + (service)) + "?notify=false&methods=test1,test2"));

    private URL anyUrl = URL.valueOf("zookeeper://zookeeper/*");

    private URL registryUrl;

    private ZookeeperRegistryFactory zookeeperRegistryFactory;

    @Test
    public void testAnyHost() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            URL errorUrl = URL.valueOf("multicast://0.0.0.0/");
            new ZookeeperRegistryFactory().createRegistry(errorUrl);
        });
    }

    @Test
    public void testRegister() {
        Set<URL> registered;
        for (int i = 0; i < 2; i++) {
            zookeeperRegistry.register(serviceUrl);
            registered = zookeeperRegistry.getRegistered();
            MatcherAssert.assertThat(registered.contains(serviceUrl), CoreMatchers.is(true));
        }
        registered = zookeeperRegistry.getRegistered();
        MatcherAssert.assertThat(registered.size(), CoreMatchers.is(1));
    }

    @Test
    public void testSubscribe() {
        NotifyListener listener = Mockito.mock(NotifyListener.class);
        zookeeperRegistry.subscribe(serviceUrl, listener);
        Map<URL, Set<NotifyListener>> subscribed = zookeeperRegistry.getSubscribed();
        MatcherAssert.assertThat(subscribed.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(subscribed.get(serviceUrl).size(), CoreMatchers.is(1));
        zookeeperRegistry.unsubscribe(serviceUrl, listener);
        subscribed = zookeeperRegistry.getSubscribed();
        MatcherAssert.assertThat(subscribed.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(subscribed.get(serviceUrl).size(), CoreMatchers.is(0));
    }

    @Test
    public void testAvailable() {
        zookeeperRegistry.register(serviceUrl);
        MatcherAssert.assertThat(zookeeperRegistry.isAvailable(), CoreMatchers.is(true));
        zookeeperRegistry.destroy();
        MatcherAssert.assertThat(zookeeperRegistry.isAvailable(), CoreMatchers.is(false));
    }

    @Test
    public void testLookup() {
        List<URL> lookup = zookeeperRegistry.lookup(serviceUrl);
        MatcherAssert.assertThat(lookup.size(), CoreMatchers.is(0));
        zookeeperRegistry.register(serviceUrl);
        lookup = zookeeperRegistry.lookup(serviceUrl);
        MatcherAssert.assertThat(lookup.size(), CoreMatchers.is(1));
    }

    @Test
    public void testSubscribeAnyValue() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        zookeeperRegistry.register(serviceUrl);
        zookeeperRegistry.subscribe(anyUrl, ( urls) -> latch.countDown());
        zookeeperRegistry.register(serviceUrl);
        latch.await();
    }
}

