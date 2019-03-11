/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * import junit.framework.Assert;
 */
package org.openhab.binding.max.internal.message;


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.max.internal.Utils;


/**
 * Tests cases for {@link Utils}.
 *
 * @author Andreas Heil (info@aheil.de) - Initial contribution
 * @author Marcel Verpaalen - OH2 Version and updates
 */
public class UtilsTest {
    @Test
    public void fromHexTest() {
        Assert.assertEquals(0, Utils.fromHex("00"));
        Assert.assertEquals(1, Utils.fromHex("01"));
        Assert.assertEquals(31, Utils.fromHex("1F"));
        Assert.assertEquals(255, Utils.fromHex("FF"));
    }

    @Test
    public void fromByteTest() {
        byte b0 = 0;
        byte b127 = 127;
        byte b128 = ((byte) (128));// overflow due to

        byte bn128 = -128;// signed bytes

        byte bn1 = -1;
        int ar0 = Utils.fromByte(b0);
        int ar127 = Utils.fromByte(b127);
        int ar128 = Utils.fromByte(b128);
        int arn128 = Utils.fromByte(bn128);
        int arn1 = Utils.fromByte(bn1);
        Assert.assertEquals(0, ar0);
        Assert.assertEquals(127, ar127);
        Assert.assertEquals(128, ar128);
        Assert.assertEquals(128, arn128);
        Assert.assertEquals(255, arn1);
    }

    @Test
    public void toHexNoArgTest() {
        String actualResult = Utils.toHex();
        Assert.assertEquals("", actualResult);
    }

    @Test
    public void toHexOneArgTest() {
        String actualResult = Utils.toHex(15);
        Assert.assertEquals("0F", actualResult);
    }

    @Test
    public void toHexMultipleArgTest() {
        String actualResult = Utils.toHex(4863);
        Assert.assertEquals("12FF", actualResult);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void resolveDateTimeTest() {
        int date = Utils.fromHex("858B");// 05-09-2011

        int time = Utils.fromHex("2E");// 23:00

        Date result = Utils.resolveDateTime(date, time);
        Assert.assertEquals(5, result.getDate());
        Assert.assertEquals(9, result.getMonth());
        Assert.assertEquals(2011, result.getYear());
        Assert.assertEquals(23, result.getHours());
        Assert.assertEquals(0, result.getMinutes());
    }

    @Test
    public void getBitsTest() {
        boolean[] b1 = Utils.getBits(255);
        Assert.assertEquals(b1.length, 8);
        for (int i = 0; i < 8; i++) {
            Assert.assertEquals(true, b1[i]);
        }
        boolean[] b2 = Utils.getBits(90);
        Assert.assertEquals(b2.length, 8);
        Assert.assertEquals(false, b2[0]);
        Assert.assertEquals(true, b2[1]);
        Assert.assertEquals(false, b2[2]);
        Assert.assertEquals(true, b2[3]);
        Assert.assertEquals(true, b2[4]);
        Assert.assertEquals(false, b2[5]);
        Assert.assertEquals(true, b2[6]);
        Assert.assertEquals(false, b2[7]);
    }

    @Test
    public void hexStringToByteArrayTest() {
        String s = "000102030AFF";
        byte[] result = Utils.hexStringToByteArray(s);
        Assert.assertEquals(0, ((result[0]) & 255));
        Assert.assertEquals(1, ((result[1]) & 255));
        Assert.assertEquals(2, ((result[2]) & 255));
        Assert.assertEquals(3, ((result[3]) & 255));
        Assert.assertEquals(10, ((result[4]) & 255));
        Assert.assertEquals(255, ((result[5]) & 255));
    }
}

