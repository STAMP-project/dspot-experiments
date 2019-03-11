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
public class RFXComCurrentMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0D59010F860004001D0000000049";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComCurrentMessage msg = ((RFXComCurrentMessage) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", SubType.ELEC1, msg.subType);
        Assert.assertEquals("Seq Number", 15, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "34304", msg.generateDeviceId());
        Assert.assertEquals("Count", 4, msg.count);
        Assert.assertEquals("Channel 1", 2.9, msg.channel1Amps, 0.01);
        Assert.assertEquals("Channel 2", 0, msg.channel2Amps, 0.01);
        Assert.assertEquals("Channel 3", 0, msg.channel3Amps, 0.01);
        Assert.assertEquals("Signal Level", ((byte) (4)), msg.signalLevel);
        Assert.assertEquals("Battery Level", ((byte) (9)), msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

