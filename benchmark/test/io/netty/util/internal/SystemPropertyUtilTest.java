/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import org.junit.Assert;
import org.junit.Test;


public class SystemPropertyUtilTest {
    @Test(expected = NullPointerException.class)
    public void testGetWithKeyNull() {
        SystemPropertyUtil.get(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWithKeyEmpty() {
        SystemPropertyUtil.get("", null);
    }

    @Test
    public void testGetDefaultValueWithPropertyNull() {
        Assert.assertEquals("default", SystemPropertyUtil.get("key", "default"));
    }

    @Test
    public void testGetPropertyValue() {
        System.setProperty("key", "value");
        Assert.assertEquals("value", SystemPropertyUtil.get("key"));
    }

    @Test
    public void testGetBooleanDefaultValueWithPropertyNull() {
        Assert.assertTrue(SystemPropertyUtil.getBoolean("key", true));
        Assert.assertFalse(SystemPropertyUtil.getBoolean("key", false));
    }

    @Test
    public void testGetBooleanDefaultValueWithEmptyString() {
        System.setProperty("key", "");
        Assert.assertTrue(SystemPropertyUtil.getBoolean("key", true));
        Assert.assertFalse(SystemPropertyUtil.getBoolean("key", false));
    }

    @Test
    public void testGetBooleanWithTrueValue() {
        System.setProperty("key", "true");
        Assert.assertTrue(SystemPropertyUtil.getBoolean("key", false));
        System.setProperty("key", "yes");
        Assert.assertTrue(SystemPropertyUtil.getBoolean("key", false));
        System.setProperty("key", "1");
        Assert.assertTrue(SystemPropertyUtil.getBoolean("key", true));
    }

    @Test
    public void testGetBooleanWithFalseValue() {
        System.setProperty("key", "false");
        Assert.assertFalse(SystemPropertyUtil.getBoolean("key", true));
        System.setProperty("key", "no");
        Assert.assertFalse(SystemPropertyUtil.getBoolean("key", false));
        System.setProperty("key", "0");
        Assert.assertFalse(SystemPropertyUtil.getBoolean("key", true));
    }

    @Test
    public void testGetBooleanDefaultValueWithWrongValue() {
        System.setProperty("key", "abc");
        Assert.assertTrue(SystemPropertyUtil.getBoolean("key", true));
        System.setProperty("key", "123");
        Assert.assertFalse(SystemPropertyUtil.getBoolean("key", false));
    }

    @Test
    public void getIntDefaultValueWithPropertyNull() {
        Assert.assertEquals(1, SystemPropertyUtil.getInt("key", 1));
    }

    @Test
    public void getIntWithPropertValueIsInt() {
        System.setProperty("key", "123");
        Assert.assertEquals(123, SystemPropertyUtil.getInt("key", 1));
    }

    @Test
    public void getIntDefaultValueWithPropertValueIsNotInt() {
        System.setProperty("key", "NotInt");
        Assert.assertEquals(1, SystemPropertyUtil.getInt("key", 1));
    }

    @Test
    public void getLongDefaultValueWithPropertyNull() {
        Assert.assertEquals(1, SystemPropertyUtil.getLong("key", 1));
    }

    @Test
    public void getLongWithPropertValueIsLong() {
        System.setProperty("key", "123");
        Assert.assertEquals(123, SystemPropertyUtil.getLong("key", 1));
    }

    @Test
    public void getLongDefaultValueWithPropertValueIsNotLong() {
        System.setProperty("key", "NotInt");
        Assert.assertEquals(1, SystemPropertyUtil.getLong("key", 1));
    }
}

