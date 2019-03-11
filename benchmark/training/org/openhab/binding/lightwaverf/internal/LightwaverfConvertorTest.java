/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal;


import LightwaveRfType.DIMMER;
import LightwaveRfType.HEATING_SET_TEMP;
import LightwaveRfType.SWITCH;
import OnOffType.OFF;
import OnOffType.ON;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRFCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfDeviceRegistrationCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfDimCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfOnOffCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfSetHeatingTemperatureCommand;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;


public class LightwaverfConvertorTest {
    @Test
    public void testConvertToLightwaveRfMessageOnCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertToLightwaveRfMessage("2", "3", SWITCH, ON);
        LightwaveRFCommand expected = new LightwaveRfOnOffCommand("200,!R2D3F1");
        Assert.assertEquals(expected.getLightwaveRfCommandString(), command.getLightwaveRfCommandString());
    }

    @Test
    public void testConvertFromLightwaveRfMessageOnCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertFromLightwaveRfMessage("010,!R2D3F1");
        LightwaveRFCommand expected = new LightwaveRfOnOffCommand(10, "2", "3", true);
        Assert.assertEquals(expected, command);
    }

    @Test
    public void testConvertToLightwaveRfMessageOffCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertToLightwaveRfMessage("2", "3", SWITCH, OFF);
        LightwaveRFCommand expected = new LightwaveRfOnOffCommand("200,!R2D3F0");
        Assert.assertEquals(expected.getLightwaveRfCommandString(), command.getLightwaveRfCommandString());
    }

    @Test
    public void testConvertFromLightwaveRfMessageOffCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertFromLightwaveRfMessage("010,!R2D3F0");
        LightwaveRFCommand expected = new LightwaveRfOnOffCommand(10, "2", "3", false);
        Assert.assertEquals(expected, command);
    }

    @Test
    public void testConvertToLightwaveRfMessageDimCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertToLightwaveRfMessage("2", "3", DIMMER, new PercentType(75));
        LightwaveRFCommand expected = new LightwaveRfDimCommand("200,!R2D3FdP24");
        Assert.assertEquals(expected.getLightwaveRfCommandString(), command.getLightwaveRfCommandString());
    }

    @Test
    public void testConvertFromLightwaveRfMessageDimCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertFromLightwaveRfMessage("010,!R2D3FdP24");
        LightwaveRFCommand expected = new LightwaveRfDimCommand(10, "2", "3", 75);
        Assert.assertEquals(expected, command);
    }

    @Test
    public void testConvertToLightwaveRfMessageSetTempCommand() throws Exception {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.convertToLightwaveRfMessage("2", null, HEATING_SET_TEMP, new DecimalType(21.5));
        LightwaveRFCommand expected = new LightwaveRfSetHeatingTemperatureCommand("200,!R2DhF*tP21.5");
        Assert.assertEquals(expected.getLightwaveRfCommandString(), command.getLightwaveRfCommandString());
    }

    @Test
    public void testGetRegistrationCommand() {
        LightwaverfConvertor convertor = new LightwaverfConvertor();
        LightwaveRFCommand command = convertor.getRegistrationCommand();
        Assert.assertEquals(new LightwaveRfDeviceRegistrationCommand(), command);
    }
}

