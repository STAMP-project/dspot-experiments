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
package org.apache.dubbo.admin.registry.config.impl;


import org.apache.curator.test.TestingServer;
import org.apache.dubbo.admin.common.util.Constants;
import org.apache.dubbo.common.URL;
import org.junit.Assert;
import org.junit.Test;


public class ZookeeperConfigurationTest {
    private TestingServer zkServer;

    private ZookeeperConfiguration configuration;

    private URL registryUrl;

    @Test
    public void testGetSetConfig() {
        configuration.setConfig("test_key", "test_value");
        Assert.assertEquals("test_value", configuration.getConfig("test_key"));
        Assert.assertEquals(null, configuration.getConfig("not_exist_key"));
        configuration.setConfig("test_group", "test_key", "test_group_value");
        Assert.assertEquals("test_group_value", configuration.getConfig("test_group", "test_key"));
        Assert.assertEquals(null, configuration.getConfig("test_group", "not_exist_key"));
        try {
            configuration.getConfig(null);
            Assert.fail("should throw IllegalArgumentException for null key");
        } catch (IllegalArgumentException e) {
        }
        try {
            configuration.setConfig("test_null", null);
            Assert.fail("should throw IllegalArgumentException for null key");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testDeleteConfig() {
        Assert.assertEquals(false, configuration.deleteConfig("not_exist_key"));
        configuration.setConfig("test_delete", "test_value");
        Assert.assertEquals("test_value", configuration.getConfig("test_delete"));
        configuration.deleteConfig("test_delete");
        Assert.assertEquals(null, configuration.getConfig("test_delete"));
        Assert.assertEquals(false, configuration.deleteConfig("test_group", "not_exist_key"));
        configuration.setConfig("test_group", "test_delete", "test_value");
        Assert.assertEquals("test_value", configuration.getConfig("test_group", "test_delete"));
        configuration.deleteConfig("test_group", "test_delete");
        Assert.assertEquals(null, configuration.getConfig("test_group", "test_delete"));
        try {
            configuration.deleteConfig(null);
            Assert.fail("should throw IllegalArgumentException for null key");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetPath() {
        Assert.assertEquals(((((Constants.PATH_SEPARATOR) + (Constants.DEFAULT_ROOT)) + (Constants.PATH_SEPARATOR)) + "test_key"), configuration.getPath("test_key"));
        try {
            configuration.getPath(null);
            Assert.fail("should throw IllegalArgumentException for null path");
        } catch (IllegalArgumentException e) {
        }
    }
}

