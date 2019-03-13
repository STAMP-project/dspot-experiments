/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal.command;


import LightwaveRfType.ENERGY_CURRENT_USAGE;
import LightwaveRfType.ENERGY_MAX_USAGE;
import LightwaveRfType.ENERGY_TODAY_USAGE;
import LightwaveRfType.ENERGY_YESTERDAY_USAGE;
import LightwaveRfType.SIGNAL;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.lightwaverf.internal.message.LightwaveRfJsonMessageId;
import org.openhab.core.library.types.DecimalType;


public class LightwaveRfEnergyMonitorMessageTest {
    private String messageString = "*!{\"trans\":215955,\"mac\":\"03:41:C4\",\"time\":1435620183,\"prod\":\"pwrMtr\",\"serial\":\"9470FE\"," + ("\"signal\":79,\"type\":\"energy\",\"cUse\":271,\"maxUse\":2812," + "\"todUse\":8414,\"yesUse\":8377}");

    @Test
    public void testDecodingMessage() throws Exception {
        LightwaveRfEnergyMonitorMessage message = new LightwaveRfEnergyMonitorMessage(messageString);
        Assert.assertEquals(new LightwaveRfJsonMessageId(215955), message.getMessageId());
        Assert.assertEquals("03:41:C4", message.getMac());
        Assert.assertEquals("pwrMtr", message.getProd());
        Assert.assertEquals("9470FE", message.getSerial());
        Assert.assertEquals(79, message.getSignal());
        Assert.assertEquals("energy", message.getType());
        Assert.assertEquals(271, message.getcUse());
        Assert.assertEquals(2812, message.getMaxUse());
        Assert.assertEquals(8414, message.getTodUse());
        Assert.assertEquals(8377, message.getYesUse());
    }

    @Test
    public void testMatches() {
        boolean matches = LightwaveRfEnergyMonitorMessage.matches(messageString);
        Assert.assertTrue(matches);
    }

    @Test
    public void testGetState() throws Exception {
        LightwaveRfEnergyMonitorMessage message = new LightwaveRfEnergyMonitorMessage(messageString);
        Assert.assertEquals(new DecimalType(271), message.getState(ENERGY_CURRENT_USAGE));
        Assert.assertEquals(new DecimalType(2812), message.getState(ENERGY_MAX_USAGE));
        Assert.assertEquals(new DecimalType(8414), message.getState(ENERGY_TODAY_USAGE));
        Assert.assertEquals(new DecimalType(8377), message.getState(ENERGY_YESTERDAY_USAGE));
        Assert.assertEquals(new DecimalType(79), message.getState(SIGNAL));
    }
}

