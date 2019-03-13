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
public class RFXComPowerMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0F5C0103002DE4000000000003003280", SubType.ELEC5, 3, "45", 228, 0, 0, 0.03, 0, 50, 8);
        testMessage("0F5C0104002DE40002002F0003643280", SubType.ELEC5, 4, "45", 228, 0.02, 4.7, 0.03, 1, 50, 8);
        testMessage("0F5C0105002DE3001401BD0003643280", SubType.ELEC5, 5, "45", 227, 0.2, 44.5, 0.03, 1, 50, 8);
        testMessage("0F5C0106002DE30005005700034D3280", SubType.ELEC5, 6, "45", 227, 0.05, 8.7, 0.03, 0.77, 50, 8);
    }
}

