/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.util;


import java.math.BigInteger;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;


/**
 *
 *
 * @author Roman Mandeleil
 * @since 17.05.14
 */
public class UtilsTest {
    private Locale defaultLocale;

    @Test
    public void testGetValueShortString1() {
        int aaa;
        String expected = "123\u00b7(10^24)";
        String result = Utils.getValueShortString(new BigInteger("123456789123445654363653463"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetValueShortString2() {
        String expected = "123\u00b7(10^3)";
        String result = Utils.getValueShortString(new BigInteger("123456"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetValueShortString3() {
        String expected = "1\u00b7(10^3)";
        String result = Utils.getValueShortString(new BigInteger("1234"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetValueShortString4() {
        String expected = "123\u00b7(10^0)";
        String result = Utils.getValueShortString(new BigInteger("123"));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGetValueShortString5() {
        byte[] decimal = Hex.decode("3913517ebd3c0c65000000");
        String expected = "69\u00b7(10^24)";
        String result = Utils.getValueShortString(new BigInteger(decimal));
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testAddressStringToBytes() {
        // valid address
        String HexStr = "6c386a4b26f73c802f34673f7248bb118f97424a";
        byte[] expected = Hex.decode(HexStr);
        byte[] result = Utils.addressStringToBytes(HexStr);
        Assert.assertEquals(Arrays.areEqual(expected, result), true);
        // invalid address, we removed the last char so it cannot decode
        HexStr = "6c386a4b26f73c802f34673f7248bb118f97424";
        expected = null;
        result = Utils.addressStringToBytes(HexStr);
        Assert.assertEquals(expected, result);
        // invalid address, longer than 20 bytes
        HexStr = new String(Hex.encode("I am longer than 20 bytes, i promise".getBytes()));
        expected = null;
        result = Utils.addressStringToBytes(HexStr);
        Assert.assertEquals(expected, result);
        // invalid address, shorter than 20 bytes
        HexStr = new String(Hex.encode("I am short".getBytes()));
        expected = null;
        result = Utils.addressStringToBytes(HexStr);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testIsHexEncoded() {
        Assert.assertTrue(Utils.isHexEncoded("AAA"));
        Assert.assertTrue(Utils.isHexEncoded("6c386a4b26f73c802f34673f7248bb118f97424a"));
        Assert.assertFalse(Utils.isHexEncoded(null));
        Assert.assertFalse(Utils.isHexEncoded("I am not hex"));
        Assert.assertTrue(Utils.isHexEncoded(""));
        Assert.assertTrue(Utils.isHexEncoded(("6c386a4b26f73c802f34673f7248bb118f97424a" + ((((("6c386a4b26f73c802f34673f7248bb118f97424a" + "6c386a4b26f73c802f34673f7248bb118f97424a") + "6c386a4b26f73c802f34673f7248bb118f97424a") + "6c386a4b26f73c802f34673f7248bb118f97424a") + "6c386a4b26f73c802f34673f7248bb118f97424a") + "6c386a4b26f73c802f34673f7248bb118f97424a"))));
    }

    @Test
    public void testLongToTimePeriod() {
        Assert.assertEquals("2.99s", Utils.longToTimePeriod((3000 - 12)));
        Assert.assertEquals("1d21h", Utils.longToTimePeriod(((45L * 3600) * 1000)));
    }
}

