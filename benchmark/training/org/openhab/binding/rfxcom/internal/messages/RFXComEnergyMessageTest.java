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
public class RFXComEnergyMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "115A01071A7300000003F600000000350B89";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComEnergyMessage msg = ((RFXComEnergyMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.ELEC2, msg.subType);
        Assert.assertEquals("Seq Number", 7, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "6771", msg.getDeviceId());
        Assert.assertEquals("Count", 0, msg.count);
        Assert.assertEquals("Instant usage", (1014.0 / 230), msg.instantAmp, 0.01);
        Assert.assertEquals("Total usage", (60.7 / 230), msg.totalAmpHour, 0.01);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        Assert.assertEquals("Battery Level", ((byte) (9)), msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

