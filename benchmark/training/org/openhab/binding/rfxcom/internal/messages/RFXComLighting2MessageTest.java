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


import RFXComLighting2Message.Commands.OFF;
import RFXComLighting2Message.SubType.AC;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComLighting2MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0B11000600109B520B000080";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComLighting2Message msg = ((RFXComLighting2Message) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", AC, msg.subType);
        Assert.assertEquals("Seq Number", 6, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "1088338.11", msg.getDeviceId());
        Assert.assertEquals("Command", OFF, msg.command);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

