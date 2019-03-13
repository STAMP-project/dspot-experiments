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


import RFXComCurtain1Message.Commands;
import RFXComCurtain1Message.SubType;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
// TODO please add tests for real messages
public class RFXComCurtain1MessageTest {
    @Test
    public void checkForSupportTest() throws RFXComException {
        RFXComMessageFactory.createMessage(PacketType.CURTAIN1);
    }

    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComCurtain1Message message = ((RFXComCurtain1Message) (RFXComMessageFactory.createMessage(PacketType.CURTAIN1)));
        message.subType = SubType.HARRISON;
        message.command = Commands.OPEN;
        RFXComTestHelper.basicBoundaryCheck(PacketType.CURTAIN1, message);
    }
}

