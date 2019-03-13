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
 * @author Martin van Wingerden
 */
public class RFXComTemperatureRainMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0A4F01CCF001004F03B759";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComTemperatureRainMessage msg = ((RFXComTemperatureRainMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.WS1200, msg.subType);
        Assert.assertEquals("Seq Number", 204, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "61441", msg.getDeviceId());
        Assert.assertEquals("Temperature", 7.9, msg.temperature, 0.001);
        Assert.assertEquals("Rain total", 95.1, msg.rainTotal, 0.001);
        Assert.assertEquals("Signal Level", ((byte) (5)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

