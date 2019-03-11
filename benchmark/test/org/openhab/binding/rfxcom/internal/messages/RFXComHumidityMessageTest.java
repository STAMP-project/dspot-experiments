/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComHumidityMessage.HumidityStatus.COMFORT;
import RFXComHumidityMessage.SubType.LACROSSE_TX3;
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
public class RFXComHumidityMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "085101027700360189";
        byte[] message = DatatypeConverter.parseHexBinary(hexMessage);
        RFXComHumidityMessage msg = ((RFXComHumidityMessage) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", LACROSSE_TX3, msg.subType);
        Assert.assertEquals("Seq Number", 2, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "30464", msg.generateDeviceId());
        Assert.assertEquals("Humidity", 54, msg.humidity);
        Assert.assertEquals("Humidity status", COMFORT, msg.humidityStatus);
        Assert.assertEquals("Signal Level", ((byte) (8)), msg.signalLevel);
        Assert.assertEquals("Battery Level", ((byte) (9)), msg.batteryLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, DatatypeConverter.printHexBinary(decoded));
    }
}

