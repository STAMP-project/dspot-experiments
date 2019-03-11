/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
public class RFXComTemperatureMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("08500110000180BC69", THR128_138_THC138, 16, "1", (-18.8), 6, 9);
        testMessage("0850021DFB0100D770", THC238_268_THN122_132_THWR288_THRN122_AW129_131, 29, "64257", 21.5, 7, 0);
        testMessage("08500502770000D389", LACROSSE_TX2_TX3_TX4_TX17, 2, "30464", 21.1, 8, 9);
        testMessage("0850091A00C3800689", RUBICSON, 26, "195", (-0.6), 8, 9);
        testMessage("0850097200C300E089", RUBICSON, 114, "195", 22.4, 8, 9);
    }
}

