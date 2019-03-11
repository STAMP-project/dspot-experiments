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
package org.openhab.binding.rfxcom.internal.messages;


import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComMessageNotImplementedException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComTemperatureHumidityBarometricMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException, RFXComMessageNotImplementedException {
        String hexMessage = "0D54020EE90000C9270203E70439";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComTemperatureHumidityBarometricMessage msg = ((RFXComTemperatureHumidityBarometricMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.THB2, msg.subType);
        Assert.assertEquals("Seq Number", 14, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "59648", msg.getDeviceId());
        Assert.assertEquals("Temperature", 20.1, msg.temperature, 0.01);
        Assert.assertEquals("Humidity", 39, msg.humidity);
        Assert.assertEquals("Humidity status", HumidityStatus.DRY, msg.humidityStatus);
        Assert.assertEquals("Barometer", 999.0, msg.pressure, 0.001);
        Assert.assertEquals("Forecast", ForecastStatus.RAIN, msg.forecastStatus);
        Assert.assertEquals("Signal Level", 3, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

