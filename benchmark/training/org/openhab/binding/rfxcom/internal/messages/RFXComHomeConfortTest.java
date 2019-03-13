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


import Commands.GROUP_ON;
import SubType.TEL_010;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden - Initial contribution of empty test
 * @author Mike Jagdis - added message handling and real test
 */
public class RFXComHomeConfortTest {
    @Test
    public void testMessage1() throws RFXComException {
        testMessage(TEL_010, GROUP_ON, "1118739.A.4", "0C1B0000111213410403000000");
    }
}

