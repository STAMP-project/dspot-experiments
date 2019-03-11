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
public class RFXComWindMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "105601122F000087000000140000000079";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComWindMessage msg = ((RFXComWindMessage) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", SubType.WTGR800, msg.subType);
        Assert.assertEquals("Seq Number", 18, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "12032", msg.generateDeviceId());
        Assert.assertEquals("Direction", 135.0, msg.windDirection, 0.001);
        Assert.assertEquals("Average speed", 0.0, msg.windAvSpeed, 0.001);
        Assert.assertEquals("Wind Gust", 2.0, msg.windSpeed, 0.001);
        Assert.assertEquals("Signal Level", 7, msg.signalLevel);
        Assert.assertEquals("Battery Level", 9, msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

