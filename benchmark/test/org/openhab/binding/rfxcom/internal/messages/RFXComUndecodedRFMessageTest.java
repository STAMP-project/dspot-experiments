/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import PacketType.UNDECODED_RF_MESSAGE;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
// TODO please add tests for real messages
public class RFXComUndecodedRFMessageTest {
    @Test
    public void checkForSupportTest() throws RFXComException {
        RFXComMessageFactory.getMessageInterface(UNDECODED_RF_MESSAGE);
    }

    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComTestHelper.basicBoundaryCheck(UNDECODED_RF_MESSAGE);
    }
}

