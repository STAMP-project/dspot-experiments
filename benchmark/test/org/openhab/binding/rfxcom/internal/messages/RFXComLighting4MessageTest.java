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
import OpenClosedType.OPEN;
import PacketType.LIGHTING4;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;
import org.openhab.core.library.types.DecimalType;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
// TODO please add more tests for real messages
public class RFXComLighting4MessageTest {
    @Test
    public void testPirSensorMessages() throws RFXComException {
        RFXComLighting4Message msg = testMessage("0913000145DD99018870", 1, "286169", 7, ON_9, true, 392);
        Assert.assertEquals("convert to Contact", OPEN, msg.convertToState(CONTACT));
        Assert.assertEquals("convert to Command", ON, msg.convertToState(COMMAND));
        Assert.assertEquals("convert to Number", new DecimalType(7), msg.convertToState(SIGNAL_LEVEL));
    }

    @Test
    public void testContactSensorMessages() throws RFXComException {
        testMessage("0913002B455157016560", 43, "283925", 6, ON_7, true, 357);
        testMessage("0913002C455154016260", 44, "283925", 6, OFF_4, false, 354);
        testMessage("09130047BAEAAC017E70", 71, "765610", 7, ON_12, true, 382);
    }

    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComTestHelper.basicBoundaryCheck(LIGHTING4);
    }
}

