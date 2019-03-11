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
package org.openhab.binding.max.internal.message;


import Message.DELIMETER;
import MessageType.H;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.max.internal.Utils;


/**
 * Tests cases for {@link HMessage}.
 *
 * @author Marcel Verpaalen - Initial contribution
 */
public class HMessageTest {
    public static final String RAW_DATA = "H:KEQ0565026,0b5951,0113,00000000,4eed6795,01,32,12080a,070f,03,0000";

    private HMessage message;

    @Test
    public void getMessageTypeTest() {
        MessageType messageType = getType();
        Assert.assertEquals(H, messageType);
    }

    @Test
    public void getRFAddressTest() {
        String rfAddress = message.getRFAddress();
        Assert.assertEquals("0b5951", rfAddress);
    }

    @Test
    public void getFirmwareTest() {
        String firmware = message.getFirmwareVersion();
        Assert.assertEquals("01.13", firmware);
    }

    @Test
    public void getConnectionIdTest() {
        String connectionId = message.getConnectionId();
        Assert.assertEquals("4eed6795", connectionId);
    }

    @Test
    public void getCubeTimeStateTest() {
        String cubeTimeState = message.getCubeTimeState();
        Assert.assertEquals("03", cubeTimeState);
    }

    @Test
    public void testParseDateTime() {
        String[] tokens = HMessageTest.RAW_DATA.split(DELIMETER);
        String hexDate = tokens[7];
        String hexTime = tokens[8];
        int year = Utils.fromHex(hexDate.substring(0, 2));
        int month = Utils.fromHex(hexDate.substring(2, 4));
        int dayOfMonth = Utils.fromHex(hexDate.substring(4, 6));
        Assert.assertEquals(18, year);
        Assert.assertEquals(8, month);
        Assert.assertEquals(10, dayOfMonth);
        int hours = Utils.fromHex(hexTime.substring(0, 2));
        int minutes = Utils.fromHex(hexTime.substring(2, 4));
        Assert.assertEquals(7, hours);
        Assert.assertEquals(15, minutes);
    }

    @Test
    public void testGetDateTime() {
        Date dateTime = message.getDateTime();
        Assert.assertEquals(Date.from(ZonedDateTime.of(2018, 8, 10, 7, 15, 0, 0, ZoneId.systemDefault()).toInstant()), dateTime);
    }

    @Test
    public void getNTPCounterTest() {
        String ntpCounter = message.getNTPCounter();
        Assert.assertEquals("0", ntpCounter);
    }

    @Test
    public void getSerialNumberTest() {
        String serialNumber = message.getSerialNumber();
        Assert.assertEquals("KEQ0565026", serialNumber);
    }
}

