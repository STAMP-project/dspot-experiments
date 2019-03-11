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


import RFXComThermostat1Message.Mode.HEATING;
import RFXComThermostat1Message.Status.NO_DEMAND;
import RFXComThermostat1Message.SubType.DIGIMAX;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComThermostat1MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0940001B6B1816150270";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComThermostat1Message msg = ((RFXComThermostat1Message) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", DIGIMAX, msg.subType);
        Assert.assertEquals("Seq Number", 27, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "27416", msg.getDeviceId());
        Assert.assertEquals("Temperature", 22, msg.temperature);
        Assert.assertEquals("Set point", 21, msg.set);
        Assert.assertEquals("Mode", HEATING, msg.mode);
        Assert.assertEquals("Status", NO_DEMAND, msg.status);
        Assert.assertEquals("Signal Level", ((byte) (7)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

