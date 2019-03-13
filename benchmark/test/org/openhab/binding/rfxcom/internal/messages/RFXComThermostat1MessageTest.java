/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComThermostat1Message.Mode.HEATING;
import RFXComThermostat1Message.Status.NO_DEMAND;
import RFXComThermostat1Message.SubType.DIGIMAX_TLX7506;
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
public class RFXComThermostat1MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0940001B6B1816150270";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComThermostat1Message msg = ((RFXComThermostat1Message) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", DIGIMAX_TLX7506, msg.subType);
        Assert.assertEquals("Seq Number", 27, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "27416", msg.generateDeviceId());
        Assert.assertEquals("Temperature", 22, msg.temperature);
        Assert.assertEquals("Set point", 21, msg.set);
        Assert.assertEquals("Mode", HEATING, msg.mode);
        Assert.assertEquals("Status", NO_DEMAND, msg.status);
        Assert.assertEquals("Signal Level", ((byte) (7)), msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

