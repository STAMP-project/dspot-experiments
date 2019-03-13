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


import RFXComThermostat3Message.Commands;
import RFXComThermostat3Message.Commands.UP;
import RFXComThermostat3Message.SubType;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden - Initial contribution
 */
// TODO please add tests for real messages
public class RFXComThermostat3MessageTest {
    @Test
    public void checkForSupportTest() throws RFXComException {
        RFXComMessageFactory.createMessage(PacketType.THERMOSTAT3);
    }

    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComThermostat3Message message = ((RFXComThermostat3Message) (RFXComMessageFactory.createMessage(PacketType.THERMOSTAT3)));
        message.subType = SubType.MERTIK__G6R_H4S_TRANSMIT_ONLY;
        message.command = Commands.ON;
        RFXComTestHelper.basicBoundaryCheck(PacketType.THERMOSTAT3, message);
    }

    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "08420101019FAB0280";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComThermostat3Message msg = ((RFXComThermostat3Message) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.MERTIK__G6R_H4TB__G6R_H4T__G6R_H4T21_Z22, msg.subType);
        Assert.assertEquals("Seq Number", 1, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "106411", msg.getDeviceId());
        Assert.assertEquals("Command", UP, msg.command);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

