/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.rfxcom.internal.messages;


import PacketType.UNDECODED_RF_MESSAGE;
import RFXComUndecodedRFMessage.SubType;
import RFXComUndecodedRFMessage.SubType.ARC;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComMessageTooLongException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @author James Hewitt-Thomas
 */
public class RFXComUndecodedRFMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("070301271356ECC0", ARC, 39, "1356ECC0");
    }

    @Test(expected = RFXComMessageTooLongException.class)
    public void testLongMessage() throws RFXComException {
        RFXComUndecodedRFMessage msg = ((RFXComUndecodedRFMessage) (RFXComMessageFactory.createMessage(UNDECODED_RF_MESSAGE)));
        msg.subType = SubType.ARC;
        msg.seqNbr = 1;
        msg.rawPayload = HexUtils.hexToBytes("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F2021");
        msg.decodeMessage();
    }
}

