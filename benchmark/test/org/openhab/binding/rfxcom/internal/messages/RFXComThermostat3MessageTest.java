/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComThermostat3Message.Commands.UP;
import RFXComThermostat3Message.SubType.MERTIK_G6RH4TB;
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
public class RFXComThermostat3MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "08420101019FAB0281";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComThermostat3Message msg = ((RFXComThermostat3Message) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", MERTIK_G6RH4TB, msg.subType);
        Assert.assertEquals("Seq Number", 1, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "106411", msg.generateDeviceId());
        Assert.assertEquals("Command", UP, msg.command);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

