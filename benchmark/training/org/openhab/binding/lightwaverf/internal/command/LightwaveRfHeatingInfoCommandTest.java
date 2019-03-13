/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal.command;


import LightwaveRfType.HEATING_BATTERY;
import LightwaveRfType.HEATING_SET_TEMP;
import LightwaveRfType.SIGNAL;
import LightwaveRfType.VERSION;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;


public class LightwaveRfHeatingInfoCommandTest {
    /* Commands Like
    *!{
    "trans":1232,
    "mac":"03:02:71",
    "time":1423827547,
    "prod":"valve",
    "serial":"5A4F02",
    "signal":0,
    "type":"temp",
    "batt":2.72,
    "ver":56,
    "state":"run",
    "cTemp":17.8,
    "cTarg":19.0,
    "output":80,
    "nTarg":24.0,
    "nSlot":"06:00",
    "prof":5
    }
     */
    @Test
    public void test() throws Exception {
        String message = "*!{\"trans\":1506,\"mac\":\"03:02:71\",\"time\":1423850746,\"prod\":\"valve\",\"serial\":\"064402\",\"signal\":54,\"type\":\"temp\",\"batt\":2.99,\"ver\":56,\"state\":\"boost\",\"cTemp\":22.3,\"cTarg\":24.0,\"output\":100,\"nTarg\":20.0,\"nSlot\":\"18:45\",\"prof\":5}";
        LightwaveRfSerialMessage command = new LightwaveRfHeatingInfoResponse(message);
        // LightwaveRfHeatingInfoResponse
        Assert.assertEquals(new DecimalType("2.99"), command.getState(HEATING_BATTERY));
        Assert.assertEquals(new DecimalType("22.3").doubleValue(), doubleValue(), 0.001);
        Assert.assertEquals(new DecimalType("24"), command.getState(HEATING_SET_TEMP));
        Assert.assertEquals(new DecimalType("54"), command.getState(SIGNAL));
        Assert.assertEquals(new StringType("56"), command.getState(VERSION));
        Assert.assertEquals(message, command.getLightwaveRfCommandString());
        Assert.assertEquals("064402", command.getSerial());
        Assert.assertEquals("1506", command.getMessageId().getMessageIdString());
        LightwaveRfHeatingInfoResponse heatingInfoCommand = ((LightwaveRfHeatingInfoResponse) (command));
        Assert.assertEquals("03:02:71", heatingInfoCommand.getMac());
        Assert.assertEquals("valve", heatingInfoCommand.getProd());
        Assert.assertEquals("54", heatingInfoCommand.getSignal());
        Assert.assertEquals("temp", heatingInfoCommand.getType());
        Assert.assertEquals("2.99", heatingInfoCommand.getBatteryLevel());
        Assert.assertEquals("56", heatingInfoCommand.getVersion());
        Assert.assertEquals("boost", heatingInfoCommand.getState());
        Assert.assertEquals("22.3", heatingInfoCommand.getCurrentTemperature());
        Assert.assertEquals("24.0", heatingInfoCommand.getCurrentTargetTemperature());
        Assert.assertEquals("100", heatingInfoCommand.getOutput());
        Assert.assertEquals("20.0", heatingInfoCommand.getNextTargetTeperature());
        Assert.assertEquals("18:45", heatingInfoCommand.getNextSlot());
        Assert.assertEquals("5", heatingInfoCommand.getProf());
    }
}

