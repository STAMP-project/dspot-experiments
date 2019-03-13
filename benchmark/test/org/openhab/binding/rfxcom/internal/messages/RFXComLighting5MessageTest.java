/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import OnOffType.ON;
import RFXComValueSelector.COMMAND;
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
// TODO please add more tests for different messages
public class RFXComLighting5MessageTest {
    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComTestHelper.basicBoundaryCheck(LIGHTING5);
    }

    @Test
    public void convertFromStateItMessage() throws RFXComException {
        RFXComMessageInterface itMessageObject = RFXComMessageFactory.getMessageInterface(LIGHTING5);
        itMessageObject.convertFromState(COMMAND, "2061.1", SubType.IT, ON, ((byte) (0)));
        byte[] message = itMessageObject.decodeMessage();
        String hexMessage = DatatypeConverter.printHexBinary(message);
        Assert.assertEquals("Message is not as expected", "0A140F0000080D01010000", hexMessage);
        RFXComLighting5Message msg = ((RFXComLighting5Message) (RFXComMessageFactory.getMessageInterface(message)));
        Assert.assertEquals("SubType", SubType.IT, msg.subType);
        Assert.assertEquals("Sensor Id", "2061.1", msg.generateDeviceId());
        Assert.assertEquals("Command", Commands.ON, msg.command);
    }
}

