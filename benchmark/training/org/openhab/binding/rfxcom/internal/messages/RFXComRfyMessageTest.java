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


import Commands.DOWN_LONG;
import Commands.UP_SHORT;
import SubType.ASA;
import SubType.RFY;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.messages.RFXComRfyMessage.Commands;
import org.openhab.binding.rfxcom.internal.messages.RFXComRfyMessage.SubType;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComRfyMessageTest {
    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComRfyMessage message = ((RFXComRfyMessage) (RFXComMessageFactory.createMessage(PacketType.RFY)));
        message.subType = SubType.RFY;
        message.command = Commands.UP;
        RFXComTestHelper.basicBoundaryCheck(PacketType.RFY, message);
    }

    @Test
    public void testMessage1() throws RFXComException {
        testMessage(RFY, UP_SHORT, "66051.4", "0C1A0000010203040F00000000");
    }

    @Test
    public void testMessage2() throws RFXComException {
        testMessage(ASA, DOWN_LONG, "66051.4", "0C1A0300010203041200000000");
    }
}

