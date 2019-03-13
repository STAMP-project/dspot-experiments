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
package org.apache.dubbo.configcenter.support.zookeeper;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.configcenter.ConfigChangeEvent;
import org.apache.dubbo.configcenter.ConfigurationListener;
import org.apache.dubbo.configcenter.DynamicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * TODO refactor using mockito
 */
public class ZookeeperDynamicConfigurationTest {
    private static CuratorFramework client;

    private static URL configUrl;

    private static int zkServerPort = NetUtils.getAvailablePort();

    private static TestingServer zkServer;

    private static DynamicConfiguration configuration;

    @Test
    public void testGetConfig() throws Exception {
        Assertions.assertEquals("Never change value from configurators", ZookeeperDynamicConfigurationTest.configuration.getConfig("never.change.DemoService.configurators"));
        Assertions.assertEquals("The content from dubbo.properties", ZookeeperDynamicConfigurationTest.configuration.getConfig("dubbo.properties", "dubbo"));
    }

    @Test
    public void testAddListener() throws Exception {
        CountDownLatch latch = new CountDownLatch(4);
        ZookeeperDynamicConfigurationTest.TestListener listener1 = new ZookeeperDynamicConfigurationTest.TestListener(latch);
        ZookeeperDynamicConfigurationTest.TestListener listener2 = new ZookeeperDynamicConfigurationTest.TestListener(latch);
        ZookeeperDynamicConfigurationTest.TestListener listener3 = new ZookeeperDynamicConfigurationTest.TestListener(latch);
        ZookeeperDynamicConfigurationTest.TestListener listener4 = new ZookeeperDynamicConfigurationTest.TestListener(latch);
        ZookeeperDynamicConfigurationTest.configuration.addListener("group*service:version.configurators", listener1);
        ZookeeperDynamicConfigurationTest.configuration.addListener("group*service:version.configurators", listener2);
        ZookeeperDynamicConfigurationTest.configuration.addListener("appname.tagrouters", listener3);
        ZookeeperDynamicConfigurationTest.configuration.addListener("appname.tagrouters", listener4);
        ZookeeperDynamicConfigurationTest.setData("/dubbo/config/group*service:version/configurators", "new value1");
        Thread.sleep(100);
        ZookeeperDynamicConfigurationTest.setData("/dubbo/config/appname/tagrouters", "new value2");
        Thread.sleep(100);
        ZookeeperDynamicConfigurationTest.setData("/dubbo/config/appname", "new value3");
        Thread.sleep(5000);
        latch.await();
        Assertions.assertEquals(1, listener1.getCount("group*service:version.configurators"));
        Assertions.assertEquals(1, listener2.getCount("group*service:version.configurators"));
        Assertions.assertEquals(1, listener3.getCount("appname.tagrouters"));
        Assertions.assertEquals(1, listener4.getCount("appname.tagrouters"));
        Assertions.assertEquals("new value1", listener1.getValue());
        Assertions.assertEquals("new value1", listener2.getValue());
        Assertions.assertEquals("new value2", listener3.getValue());
        Assertions.assertEquals("new value2", listener4.getValue());
    }

    private class TestListener implements ConfigurationListener {
        private CountDownLatch latch;

        private String value;

        private Map<String, Integer> countMap = new HashMap<>();

        public TestListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void process(ConfigChangeEvent event) {
            Integer count = countMap.computeIfAbsent(event.getKey(), ( k) -> new Integer(0));
            countMap.put(event.getKey(), (++count));
            value = event.getValue();
            latch.countDown();
        }

        public int getCount(String key) {
            return countMap.get(key);
        }

        public String getValue() {
            return value;
        }
    }
}

