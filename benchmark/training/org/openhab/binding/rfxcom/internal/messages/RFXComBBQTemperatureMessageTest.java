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


/**
 * Test for RFXCom-binding
 *
 * @author Mike Jagdis
 */
public class RFXComBBQTemperatureMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0A4E012B2955001A002179";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComBBQTemperatureMessage msg = ((RFXComBBQTemperatureMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.BBQ1, msg.subType);
        Assert.assertEquals("Seq Number", 43, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "10581", msg.getDeviceId());
        Assert.assertEquals("Food Temperature", 26, msg.foodTemperature, 0.1);
        Assert.assertEquals("BBQ Temperature", 33, msg.bbqTemperature, 0.1);
        Assert.assertEquals("Signal Level", 7, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

