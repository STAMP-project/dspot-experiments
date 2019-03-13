/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


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
public class RFXComEnergyMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "115A01071A7300000003F600000000350B89";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComEnergyMessage msg = ((RFXComEnergyMessage) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", SubType.ELEC2, msg.subType);
        Assert.assertEquals("Seq Number", 7, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "6771", msg.generateDeviceId());
        Assert.assertEquals("Count", 0, msg.count);
        Assert.assertEquals("Instant usage", (1014.0 / 230), msg.instantAmps, 0.01);
        Assert.assertEquals("Total usage", (60.7 / 230), msg.totalAmpHours, 0.01);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        Assert.assertEquals("Battery Level", ((byte) (9)), msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

