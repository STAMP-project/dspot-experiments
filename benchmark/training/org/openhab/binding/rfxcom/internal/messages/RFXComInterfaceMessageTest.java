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


import Commands.START_RECEIVER;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;

import static TransceiverType._433_92MHZ_TRANSCEIVER;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComInterfaceMessageTest {
    @Test
    public void testWelcomeCopyRightMessage() throws RFXComException {
        RFXComInterfaceMessage msg = testMessage("1401070307436F7079726967687420524658434F4D", SubType.START_RECEIVER, 3, START_RECEIVER, true);
        Assert.assertEquals("text", "Copyright RFXCOM", msg.text);
    }

    @Test
    public void testRespondOnUnknownMessage() throws RFXComException {
        testMessage("0D01FF190053E2000C2701020000", UNKNOWN_COMMAND, 25, UNSUPPORTED_COMMAND, true);
    }

    @Test
    public void testStatusMessage() throws RFXComException {
        RFXComInterfaceMessage msg = testMessage("1401000102530C0800270001031C04524658434F4D", RESPONSE, 1, GET_STATUS, false);
        Assert.assertEquals("Command", _433_92MHZ_TRANSCEIVER, msg.transceiverType);
        // TODO this is not correct, improvements for this have been made in the OH1 repo
        Assert.assertEquals("firmwareVersion", 12, msg.firmwareVersion);
    }
}

