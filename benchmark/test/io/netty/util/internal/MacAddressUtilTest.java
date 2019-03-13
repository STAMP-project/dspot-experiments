/**
 * Copyright 2016 The Netty Project
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


public class MacAddressUtilTest {
    @Test
    public void testCompareAddresses() {
        // should not prefer empty address when candidate is not globally unique
        Assert.assertEquals(0, MacAddressUtil.compareAddresses(EmptyArrays.EMPTY_BYTES, new byte[]{ ((byte) (82)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }));
        // only candidate is globally unique
        Assert.assertEquals((-1), MacAddressUtil.compareAddresses(EmptyArrays.EMPTY_BYTES, new byte[]{ ((byte) (80)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }));
        // only candidate is globally unique
        Assert.assertEquals((-1), MacAddressUtil.compareAddresses(new byte[]{ ((byte) (82)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }, new byte[]{ ((byte) (80)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }));
        // only current is globally unique
        Assert.assertEquals(1, MacAddressUtil.compareAddresses(new byte[]{ ((byte) (82)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }, EmptyArrays.EMPTY_BYTES));
        // only current is globally unique
        Assert.assertEquals(1, MacAddressUtil.compareAddresses(new byte[]{ ((byte) (80)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }, new byte[]{ ((byte) (82)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }));
        // both are globally unique
        Assert.assertEquals(0, MacAddressUtil.compareAddresses(new byte[]{ ((byte) (80)), ((byte) (84)), ((byte) (0)), ((byte) (249)), ((byte) (50)), ((byte) (189)) }, new byte[]{ ((byte) (80)), ((byte) (85)), ((byte) (1)), ((byte) (250)), ((byte) (51)), ((byte) (190)) }));
    }

    @Test
    public void testParseMacEUI48() {
        Assert.assertArrayEquals(new byte[]{ 0, ((byte) (170)), 17, ((byte) (187)), 34, ((byte) (204)) }, MacAddressUtil.parseMAC("00-AA-11-BB-22-CC"));
        Assert.assertArrayEquals(new byte[]{ 0, ((byte) (170)), 17, ((byte) (187)), 34, ((byte) (204)) }, MacAddressUtil.parseMAC("00:AA:11:BB:22:CC"));
    }

    @Test
    public void testParseMacMAC48ToEUI64() {
        // MAC-48 into an EUI-64
        Assert.assertArrayEquals(new byte[]{ 0, ((byte) (170)), 17, ((byte) (255)), ((byte) (255)), ((byte) (187)), 34, ((byte) (204)) }, MacAddressUtil.parseMAC("00-AA-11-FF-FF-BB-22-CC"));
        Assert.assertArrayEquals(new byte[]{ 0, ((byte) (170)), 17, ((byte) (255)), ((byte) (255)), ((byte) (187)), 34, ((byte) (204)) }, MacAddressUtil.parseMAC("00:AA:11:FF:FF:BB:22:CC"));
    }

    @Test
    public void testParseMacEUI48ToEUI64() {
        // EUI-48 into an EUI-64
        Assert.assertArrayEquals(new byte[]{ 0, ((byte) (170)), 17, ((byte) (255)), ((byte) (254)), ((byte) (187)), 34, ((byte) (204)) }, MacAddressUtil.parseMAC("00-AA-11-FF-FE-BB-22-CC"));
        Assert.assertArrayEquals(new byte[]{ 0, ((byte) (170)), 17, ((byte) (255)), ((byte) (254)), ((byte) (187)), 34, ((byte) (204)) }, MacAddressUtil.parseMAC("00:AA:11:FF:FE:BB:22:CC"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalid7HexGroupsA() {
        MacAddressUtil.parseMAC("00-AA-11-BB-22-CC-FF");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalid7HexGroupsB() {
        MacAddressUtil.parseMAC("00:AA:11:BB:22:CC:FF");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI48MixedSeparatorA() {
        MacAddressUtil.parseMAC("00-AA:11-BB-22-CC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI48MixedSeparatorB() {
        MacAddressUtil.parseMAC("00:AA-11:BB:22:CC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI64MixedSeparatorA() {
        MacAddressUtil.parseMAC("00-AA-11-FF-FE-BB-22:CC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI64MixedSeparatorB() {
        MacAddressUtil.parseMAC("00:AA:11:FF:FE:BB:22-CC");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI48TrailingSeparatorA() {
        MacAddressUtil.parseMAC("00-AA-11-BB-22-CC-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI48TrailingSeparatorB() {
        MacAddressUtil.parseMAC("00:AA:11:BB:22:CC:");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI64TrailingSeparatorA() {
        MacAddressUtil.parseMAC("00-AA-11-FF-FE-BB-22-CC-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseMacInvalidEUI64TrailingSeparatorB() {
        MacAddressUtil.parseMAC("00:AA:11:FF:FE:BB:22:CC:");
    }
}

