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


import Commands.OPEN;
import Commands.STOP;
import SubType.T4;
import SubType.T5;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComBlinds1MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0919040600A21B010280", T4, 6, "41499.1", 8, STOP);
        testMessage("091905021A6280010000", T5, 2, "1729152.1", 0, OPEN);
    }
}

