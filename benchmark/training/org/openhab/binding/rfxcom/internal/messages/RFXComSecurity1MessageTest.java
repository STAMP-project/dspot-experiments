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


import Contact.NORMAL;
import Motion.UNKNOWN;
import Status.PANIC;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComUnsupportedValueException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComSecurity1MessageTest {
    @Test
    public void testX10SecurityMessage() throws RFXComException {
        testSomeMessages("0820004DD3DC540089", X10_SECURITY, 77, "13884500", 8, NORMAL, UNKNOWN, Status.NORMAL, 9);
    }

    @Test
    public void testRM174RFSecurityMessage() throws RFXComException {
        testSomeMessages("08200A0E8000200650", RM174RF, 14, "8388640", 5, Contact.UNKNOWN, UNKNOWN, PANIC, 0);
        testSomeMessages("08200A081450450650", RM174RF, 8, "1331269", 5, Contact.UNKNOWN, UNKNOWN, PANIC, 0);
    }

    @Test(expected = RFXComUnsupportedValueException.class)
    public void testSomeInvalidSecurityMessage() throws RFXComException {
        testSomeMessages("08FF0A1F0000000650", null, 0, null, 0, null, null, null, 0);
    }
}

