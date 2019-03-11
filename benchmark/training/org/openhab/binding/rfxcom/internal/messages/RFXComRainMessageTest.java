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
public class RFXComRainMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0B550217B6000000004D3C69";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComRainMessage msg = ((RFXComRainMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.RAIN2, msg.subType);
        Assert.assertEquals("Seq Number", 23, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "46592", msg.getDeviceId());
        Assert.assertEquals("Rain rate", 0.0, msg.rainRate, 0.001);
        Assert.assertEquals("Total rain", 1977.2, msg.rainTotal, 0.001);
        Assert.assertEquals("Signal Level", 6, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

