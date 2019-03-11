/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
public class RFXComTemperatureHumidityBarometricMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0D54020EE90000C9270203E70439";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComTemperatureHumidityBarometricMessage msg = ((RFXComTemperatureHumidityBarometricMessage) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", SubType.BTHR918N_BTHR968, msg.subType);
        Assert.assertEquals("Seq Number", 14, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "59648", msg.generateDeviceId());
        Assert.assertEquals("Temperature", 20.1, msg.temperature, 0.01);
        Assert.assertEquals("Humidity", 39, msg.humidity);
        Assert.assertEquals("Humidity status", HumidityStatus.DRY, msg.humidityStatus);
        Assert.assertEquals("Barometer", 999.0, msg.pressure, 0.001);
        Assert.assertEquals("Forecast", ForecastStatus.RAIN, msg.forecastStatus);
        Assert.assertEquals("Signal Level", 3, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

