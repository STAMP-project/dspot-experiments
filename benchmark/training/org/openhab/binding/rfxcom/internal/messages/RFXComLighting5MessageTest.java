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


import OnOffType.ON;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComBindingConstants;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;

import static Commands.ON;
import static RFXComLighting5Message.SubType.LIGHTWAVERF;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
// TODO please add more tests for different messages
public class RFXComLighting5MessageTest {
    @Test
    public void convertFromStateItMessage() throws RFXComException {
        RFXComDeviceMessage itMessageObject = ((RFXComDeviceMessage) (RFXComMessageFactory.createMessage(PacketType.LIGHTING5)));
        itMessageObject.setDeviceId("2061.1");
        itMessageObject.setSubType(SubType.IT);
        itMessageObject.convertFromState(RFXComBindingConstants.CHANNEL_COMMAND, ON);
        byte[] message = itMessageObject.decodeMessage();
        String hexMessage = HexUtils.bytesToHex(message);
        Assert.assertEquals("Message is not as expected", "0A140F0000080D01010000", hexMessage);
        RFXComLighting5Message msg = ((RFXComLighting5Message) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", SubType.IT, msg.subType);
        Assert.assertEquals("Sensor Id", "2061.1", msg.getDeviceId());
        Assert.assertEquals("Command", Commands.ON, msg.command);
    }

    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComLighting5Message message = ((RFXComLighting5Message) (RFXComMessageFactory.createMessage(PacketType.LIGHTING5)));
        message.subType = LIGHTWAVERF;
        message.command = ON;
        RFXComTestHelper.basicBoundaryCheck(PacketType.LIGHTING5, message);
    }
}

