/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal.command;


import LightwaveRfType.DIMMER;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.types.PercentType;


public class LightwaveRfDimCommandTest {
    @Test
    public void testDimCommandAsMessageLow() throws Exception {
        String message = "109,!R2D1FdP1|Kitchen|Kitchen Ceiling 4%\n";
        LightwaveRfRoomDeviceMessage command = new LightwaveRfDimCommand(message);
        Assert.assertEquals("109,!R2D1FdP1\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("1", command.getDeviceId());
        Assert.assertEquals("109", command.getMessageId().getMessageIdString());
        Assert.assertEquals(PercentType.valueOf("4"), command.getState(DIMMER));
    }

    @Test
    public void testDimCommandAsMessageHigh() throws Exception {
        String message = "10,!R2D3FdP31|Kitchen|Kitchen Ceiling 96%\n";
        LightwaveRfRoomDeviceMessage command = new LightwaveRfDimCommand(message);
        Assert.assertEquals("010,!R2D3FdP31\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(PercentType.valueOf("97"), command.getState(DIMMER));
    }

    @Test
    public void testDimCommandAsParametersLow() throws Exception {
        LightwaveRfRoomDeviceMessage command = new LightwaveRfDimCommand(10, "2", "3", 1);
        Assert.assertEquals("010,!R2D3FdP1\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(PercentType.valueOf("1"), command.getState(DIMMER));
    }

    @Test
    public void testDimCommandAsParametersHigh() throws Exception {
        LightwaveRfRoomDeviceMessage command = new LightwaveRfDimCommand(10, "2", "3", 100);
        Assert.assertEquals("010,!R2D3FdP32\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(PercentType.valueOf("100"), command.getState(DIMMER));
    }

    @Test
    public void testDimCommandAsParametersOff() throws Exception {
        LightwaveRfRoomDeviceMessage command = new LightwaveRfDimCommand(10, "2", "3", 0);
        Assert.assertEquals("010,!R2D3F0\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(PercentType.valueOf("0"), command.getState(DIMMER));
    }
}

