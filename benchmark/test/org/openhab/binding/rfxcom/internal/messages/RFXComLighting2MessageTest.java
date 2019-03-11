/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComLighting2Message.Commands.OFF;
import RFXComLighting2Message.SubType.AC;
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
public class RFXComLighting2MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0B11000600109B520B000080";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComLighting2Message msg = ((RFXComLighting2Message) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", AC, msg.subType);
        Assert.assertEquals("Seq Number", 6, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "1088338.11", msg.generateDeviceId());
        Assert.assertEquals("Command", OFF, msg.command);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

