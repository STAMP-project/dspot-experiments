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


import RFXComHumidityMessage.HumidityStatus.COMFORT;
import RFXComHumidityMessage.SubType.HUM1;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComHumidityMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "085101027700360189";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComHumidityMessage msg = ((RFXComHumidityMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", HUM1, msg.subType);
        Assert.assertEquals("Seq Number", 2, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "30464", msg.getDeviceId());
        Assert.assertEquals("Humidity", 54, msg.humidity);
        Assert.assertEquals("Humidity status", COMFORT, msg.humidityStatus);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        Assert.assertEquals("Battery Level", ((byte) (9)), msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

