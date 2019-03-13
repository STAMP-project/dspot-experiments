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
import LightwaveRfType.SWITCH;
import OnOffType.OFF;
import OnOffType.ON;
import PercentType.HUNDRED;
import org.junit.Assert;
import org.junit.Test;


public class LightwaveRfOnOffCommandTest {
    @Test
    public void testOnOffCommandAsMessageOn() throws Exception {
        String message = "104,!R2D3F1|Living Room|Angle Poise Off\n";
        LightwaveRfRoomDeviceMessage command = new LightwaveRfOnOffCommand(message);
        Assert.assertEquals("104,!R2D3F1\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("104", command.getMessageId().getMessageIdString());
        Assert.assertEquals(ON, command.getState(SWITCH));
        Assert.assertEquals(HUNDRED, command.getState(DIMMER));
    }

    @Test
    public void testOnOffCommandAsMessageOff() throws Exception {
        String message = "10,!R2D3F0";
        LightwaveRfRoomDeviceMessage command = new LightwaveRfOnOffCommand(message);
        Assert.assertEquals("010,!R2D3F0\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(OFF, command.getState(SWITCH));
        Assert.assertEquals(OFF, command.getState(DIMMER));
    }

    @Test
    public void testOnOffCommandAsParametersOn() throws Exception {
        LightwaveRfRoomDeviceMessage command = new LightwaveRfOnOffCommand(10, "2", "3", true);
        Assert.assertEquals("010,!R2D3F1\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(ON, command.getState(SWITCH));
        Assert.assertEquals(HUNDRED, command.getState(DIMMER));
    }

    @Test
    public void testOnOffCommandAsParametersOff() throws Exception {
        LightwaveRfRoomDeviceMessage command = new LightwaveRfOnOffCommand(10, "2", "3", false);
        Assert.assertEquals("010,!R2D3F0\n", command.getLightwaveRfCommandString());
        Assert.assertEquals("2", command.getRoomId());
        Assert.assertEquals("3", command.getDeviceId());
        Assert.assertEquals("010", command.getMessageId().getMessageIdString());
        Assert.assertEquals(OFF, command.getState(SWITCH));
        Assert.assertEquals(OFF, command.getState(DIMMER));
    }
}

