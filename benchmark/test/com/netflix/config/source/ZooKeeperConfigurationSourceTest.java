/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config.source;


import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the implementation of {@link ZooKeeperConfigurationSource}.
 *
 * @author cfregly
 */
public class ZooKeeperConfigurationSourceTest {
    private static final Logger logger = LoggerFactory.getLogger(ZooKeeperConfigurationSourceTest.class);

    private static final String CONFIG_ROOT_PATH = "/config";

    private static TestingServer server;

    private static CuratorFramework client;

    private static ZooKeeperConfigurationSource zkConfigSource;

    private static ConcurrentMapConfiguration mapConfig;

    @Test
    public void testZkPropertyOverride() throws Exception {
        ZooKeeperConfigurationSourceTest.setZkProperty("test.key1", "test.value1-zk");
        // there is an override from ZK, so make sure the overridden value is being returned
        Assert.assertEquals("test.value1-zk", DynamicPropertyFactory.getInstance().getStringProperty("test.key1", "default").get());
    }

    @Test
    public void testNoZkPropertyOverride() throws Exception {
        // there's no override, so the map config value should be returned
        Assert.assertEquals("test.value3-map", DynamicPropertyFactory.getInstance().getStringProperty("test.key3", "default").get());
    }

    @Test
    public void testDefault() throws Exception {
        // there's no property set, so the default should be returned
        Assert.assertEquals("default", DynamicPropertyFactory.getInstance().getStringProperty("test.key99", "default").get());
    }

    @Test
    public void testSystemPropertyOverride() throws Exception {
        // there's a system property set, but this should not trump the zk override
        Assert.assertEquals("test.value4-zk", DynamicPropertyFactory.getInstance().getStringProperty("test.key4", "default").get());
        // there's a system property set, but no other overrides, so should return the system property
        Assert.assertEquals("test.value5-system", DynamicPropertyFactory.getInstance().getStringProperty("test.key5", "default").get());
    }

    @Test
    public void testUpdatePropertyOverride() throws Exception {
        ZooKeeperConfigurationSourceTest.setZkProperty("test.key1", "test.value1-zk");
        // update the map config's property and assert that the value is still the overridden value
        ZooKeeperConfigurationSourceTest.mapConfig.setProperty("test.key1", "prop1");
        Assert.assertEquals("test.value1-zk", DynamicPropertyFactory.getInstance().getStringProperty("test.key1", "default").get());
    }

    @Test
    public void testUpdateZkProperty() throws Exception {
        ZooKeeperConfigurationSourceTest.setZkProperty("test.key1", "test.value1-zk-override");
        Assert.assertEquals("test.value1-zk-override", DynamicPropertyFactory.getInstance().getStringProperty("test.key1", "default").get());
    }
}

