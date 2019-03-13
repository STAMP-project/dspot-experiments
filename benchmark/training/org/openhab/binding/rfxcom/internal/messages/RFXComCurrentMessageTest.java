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


import SubType.ELEC1;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden - Initial Contribution
 */
public class RFXComCurrentMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String message = "0D59010F860004001D0000000049";
        final RFXComCurrentMessage msg = ((RFXComCurrentMessage) (RFXComMessageFactory.createMessage(HexUtils.hexToBytes(message))));
        Assert.assertEquals("SubType", ELEC1, msg.subType);
        Assert.assertEquals("Seq Number", 15, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "34304", msg.getDeviceId());
        Assert.assertEquals("Count", 4, msg.count);
        Assert.assertEquals("Channel 1", 2.9, msg.channel1Amps, 0.01);
        Assert.assertEquals("Channel 2", 0.0, msg.channel2Amps, 0.01);
        Assert.assertEquals("Channel 3", 0.0, msg.channel3Amps, 0.01);
        Assert.assertEquals("Signal Level", 4, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", message, HexUtils.bytesToHex(decoded));
    }
}

