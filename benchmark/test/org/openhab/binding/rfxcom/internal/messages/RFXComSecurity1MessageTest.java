/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComSecurity1Message.Contact.NORMAL;
import RFXComSecurity1Message.Motion.UNKNOWN;
import RFXComSecurity1Message.SubType.X10_SECURITY;
import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
public class RFXComSecurity1MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0820004DD3DC540089";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComSecurity1Message msg = ((RFXComSecurity1Message) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", X10_SECURITY, msg.subType);
        Assert.assertEquals("Seq Number", 77, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "13884500", msg.generateDeviceId());
        Assert.assertEquals("Battery level", 8, msg.batteryLevel);
        Assert.assertEquals("Contact", NORMAL, msg.contact);
        Assert.assertEquals("Motion", UNKNOWN, msg.motion);
        Assert.assertEquals("Status", RFXComSecurity1Message.Status.NORMAL, msg.status);
        Assert.assertEquals("Signal Level", 9, msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

