/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComLighting6Message.Commands.OFF;
import RFXComLighting6Message.SubType.BLYSS;
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
public class RFXComLighting6MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0B150005D950450101011D80";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComLighting6Message msg = ((RFXComLighting6Message) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", BLYSS, msg.subType);
        Assert.assertEquals("Seq Number", 5, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "55632.E.1", msg.generateDeviceId());
        Assert.assertEquals("Command", OFF, msg.command);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        // the openhab plugin is not (yet) using the cmndseqnbr & seqnbr2
        String acceptedHexMessage = "0B150005D950450101000080";
        Assert.assertEquals("Message converted back", acceptedHexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

