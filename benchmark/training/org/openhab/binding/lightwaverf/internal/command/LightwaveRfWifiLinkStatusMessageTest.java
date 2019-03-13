/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal.command;


import LightwaveRfType.WIFILINK_DAWN_TIME;
import LightwaveRfType.WIFILINK_DUSK_TIME;
import LightwaveRfType.WIFILINK_FIRMWARE;
import LightwaveRfType.WIFILINK_IP;
import LightwaveRfType.WIFILINK_LATITUDE;
import LightwaveRfType.WIFILINK_LONGITUDE;
import LightwaveRfType.WIFILINK_UPTIME;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.lightwaverf.internal.message.LightwaveRfJsonMessageId;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;


public class LightwaveRfWifiLinkStatusMessageTest {
    private String messageString = "*!{\"trans\":452,\"mac\":\"ab:cd:ef\",\u001c\"time\":1447712274,\"type\":\"hub\",\"prod\":\"wfl\",\"fw\":\"U2.91Y\"," + ("\"uptime\":1386309,\"timeZone\":0,\"lat\":52.48,\"long\":-87.89,\"duskTime\":1447690400," + "\"dawnTime\":1447659083,\"tmrs\":0,\"evns\":1,\"run\":0,\"macs\":8,\"ip\":\"192.168.0.1\",\"devs\":0}");

    @Test
    public void testDecodingMessage() throws Exception {
        LightwaveRfWifiLinkStatusMessage message = new LightwaveRfWifiLinkStatusMessage(messageString);
        Assert.assertEquals(new LightwaveRfJsonMessageId(452), message.getMessageId());
        Assert.assertEquals("ab:cd:ef", message.getMac());
        Assert.assertEquals(new Date(1447712274000L), message.getTime());
        Assert.assertEquals("hub", message.getType());
        Assert.assertEquals("wfl", message.getProd());
        Assert.assertEquals("U2.91Y", message.getFirmware());
        Assert.assertEquals(1386309, message.getUptime());
        Assert.assertEquals("0", message.getTimeZone());
        Assert.assertEquals("52.48", message.getLatitude());
        Assert.assertEquals("-87.89", message.getLongitude());
        Assert.assertEquals(new Date(1447690400000L), message.getDuskTime());
        Assert.assertEquals(new Date(1447659083000L), message.getDawnTime());
        Assert.assertEquals("0", message.getTmrs());
        Assert.assertEquals("1", message.getEnvs());
        Assert.assertEquals("0", message.getRun());
        Assert.assertEquals("8", message.getMacs());
        Assert.assertEquals("192.168.0.1", message.getIp());
        Assert.assertEquals("0", message.getDevs());
        Assert.assertEquals("wifilink", message.getSerial());
    }

    @Test
    public void testMatches() {
        boolean matches = LightwaveRfWifiLinkStatusMessage.matches(messageString);
        Assert.assertTrue(matches);
    }

    @Test
    public void testGetState() throws Exception {
        LightwaveRfWifiLinkStatusMessage message = new LightwaveRfWifiLinkStatusMessage(messageString);
        Calendar dawnTime = Calendar.getInstance();
        dawnTime.setTime(new Date(1447659083000L));
        Calendar duskTime = Calendar.getInstance();
        duskTime.setTime(new Date(1447690400000L));
        Assert.assertEquals(new DateTimeType(dawnTime), message.getState(WIFILINK_DAWN_TIME));
        Assert.assertEquals(new DateTimeType(duskTime), message.getState(WIFILINK_DUSK_TIME));
        Assert.assertEquals(new StringType("52.48"), message.getState(WIFILINK_LATITUDE));
        Assert.assertEquals(new StringType("-87.89"), message.getState(WIFILINK_LONGITUDE));
        Assert.assertEquals(new StringType("U2.91Y"), message.getState(WIFILINK_FIRMWARE));
        Assert.assertEquals(new StringType("192.168.0.1"), message.getState(WIFILINK_IP));
        Assert.assertEquals(new DecimalType(1386309), message.getState(WIFILINK_UPTIME));
    }
}

