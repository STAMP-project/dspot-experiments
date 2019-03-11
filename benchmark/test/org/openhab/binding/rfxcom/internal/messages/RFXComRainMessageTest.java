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
public class RFXComRainMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0B550217B6000000004D3C69";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComRainMessage msg = ((RFXComRainMessage) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", SubType.PCR800, msg.subType);
        Assert.assertEquals("Seq Number", 23, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "46592", msg.generateDeviceId());
        Assert.assertEquals("Rain rate", 0.0, msg.rainRate, 0.001);
        Assert.assertEquals("Total rain", 1977.2, msg.rainTotal, 0.001);
        Assert.assertEquals("Signal Level", 6, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

