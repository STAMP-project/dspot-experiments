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
package com.alipay.remoting.config;


import ConfigItem.NETTY_BUFFER_HIGH_WATER_MARK;
import ConfigItem.NETTY_BUFFER_LOW_WATER_MARK;
import ConfigType.CLIENT_SIDE;
import ConfigType.SERVER_SIDE;
import com.alipay.remoting.config.configs.ConfigContainer;
import com.alipay.remoting.config.configs.DefaultConfigContainer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author tsui
 * @version $Id: DefaultConfigContainerTest.java, v 0.1 2018-07-30 10:53 tsui Exp $$
 */
public class DefaultConfigContainerTest {
    @Test
    public void testNormalArgs() {
        ConfigContainer configContainer = new DefaultConfigContainer();
        // test set one
        int expected_int = 123;
        configContainer.set(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK, expected_int);
        Assert.assertEquals(expected_int, configContainer.get(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertNull(configContainer.get(CLIENT_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        Assert.assertNull(configContainer.get(SERVER_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertNull(configContainer.get(SERVER_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        Assert.assertTrue(configContainer.contains(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertFalse(configContainer.contains(CLIENT_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        Assert.assertFalse(configContainer.contains(SERVER_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertFalse(configContainer.contains(SERVER_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        // test set all
        configContainer.set(CLIENT_SIDE, NETTY_BUFFER_HIGH_WATER_MARK, expected_int);
        configContainer.set(SERVER_SIDE, NETTY_BUFFER_LOW_WATER_MARK, expected_int);
        configContainer.set(SERVER_SIDE, NETTY_BUFFER_HIGH_WATER_MARK, expected_int);
        Assert.assertTrue(configContainer.contains(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertTrue(configContainer.contains(CLIENT_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        Assert.assertTrue(configContainer.contains(SERVER_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertTrue(configContainer.contains(SERVER_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        // test overwrite
        int expected_int_overwrite = 456;
        configContainer.set(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK, expected_int_overwrite);
        Assert.assertEquals(expected_int_overwrite, configContainer.get(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertEquals(expected_int, configContainer.get(CLIENT_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
        Assert.assertEquals(expected_int, configContainer.get(SERVER_SIDE, NETTY_BUFFER_LOW_WATER_MARK));
        Assert.assertEquals(expected_int, configContainer.get(SERVER_SIDE, NETTY_BUFFER_HIGH_WATER_MARK));
    }

    @Test
    public void testNullArgs() {
        ConfigContainer configContainer = new DefaultConfigContainer();
        try {
            configContainer.set(null, null, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.set(CLIENT_SIDE, null, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.set(null, NETTY_BUFFER_LOW_WATER_MARK, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.set(null, null, 123);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.set(CLIENT_SIDE, NETTY_BUFFER_LOW_WATER_MARK, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.set(CLIENT_SIDE, null, "hehe");
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.set(null, NETTY_BUFFER_LOW_WATER_MARK, "hehe");
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.get(null, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.get(null, NETTY_BUFFER_LOW_WATER_MARK);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.get(CLIENT_SIDE, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.contains(null, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.contains(CLIENT_SIDE, null);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
        try {
            configContainer.contains(null, NETTY_BUFFER_LOW_WATER_MARK);
            Assert.fail("Should not reach here!");
        } catch (IllegalArgumentException e) {
        }
    }
}

