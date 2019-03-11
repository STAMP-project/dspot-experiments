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


import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComTemperatureMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("08500110000180BC69", TEMP1, 16, "1", (-18.8), 6, 9);
        testMessage("0850021DFB0100D770", TEMP2, 29, "64257", 21.5, 7, 0);
        testMessage("08500502770000D389", TEMP5, 2, "30464", 21.1, 8, 9);
        testMessage("0850091A00C3800689", TEMP9, 26, "195", (-0.6), 8, 9);
        testMessage("0850097200C300E089", TEMP9, 114, "195", 22.4, 8, 9);
    }
}

