/**
 * Copyright 2012 The Netty Project
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
package org.jboss.netty.util.internal;


import org.junit.Assert;
import org.junit.Test;


public class ConversionUtilTest {
    @Test
    public void testNumberToInt() {
        Assert.assertEquals(42, ConversionUtil.toInt(Long.valueOf(42)));
    }

    @Test
    public void testStringToInt() {
        Assert.assertEquals(42, ConversionUtil.toInt("42"));
    }

    @Test
    public void testBooleanToBoolean() {
        Assert.assertTrue(ConversionUtil.toBoolean(Boolean.TRUE));
        Assert.assertFalse(ConversionUtil.toBoolean(Boolean.FALSE));
    }

    @Test
    public void testNumberToBoolean() {
        Assert.assertTrue(ConversionUtil.toBoolean(Integer.valueOf(42)));
        Assert.assertFalse(ConversionUtil.toBoolean(Integer.valueOf(0)));
    }

    @Test
    public void testStringToBoolean() {
        Assert.assertTrue(ConversionUtil.toBoolean("y"));
        Assert.assertTrue(ConversionUtil.toBoolean("Y"));
        Assert.assertTrue(ConversionUtil.toBoolean("yes"));
        Assert.assertTrue(ConversionUtil.toBoolean("YES"));
        Assert.assertTrue(ConversionUtil.toBoolean("yeah"));
        Assert.assertTrue(ConversionUtil.toBoolean("YEAH"));
        Assert.assertTrue(ConversionUtil.toBoolean("t"));
        Assert.assertTrue(ConversionUtil.toBoolean("T"));
        Assert.assertTrue(ConversionUtil.toBoolean("true"));
        Assert.assertTrue(ConversionUtil.toBoolean("TRUE"));
        Assert.assertTrue(ConversionUtil.toBoolean("42"));
        Assert.assertFalse(ConversionUtil.toBoolean(""));
        Assert.assertFalse(ConversionUtil.toBoolean("n"));
        Assert.assertFalse(ConversionUtil.toBoolean("no"));
        Assert.assertFalse(ConversionUtil.toBoolean("NO"));
        Assert.assertFalse(ConversionUtil.toBoolean("f"));
        Assert.assertFalse(ConversionUtil.toBoolean("false"));
        Assert.assertFalse(ConversionUtil.toBoolean("FALSE"));
        Assert.assertFalse(ConversionUtil.toBoolean("0"));
    }
}

