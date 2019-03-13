/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;


import Commands.OPEN;
import Commands.STOP;
import SubType.MEDIAMOUNT;
import SubType.YR1326;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 * @since 1.9.0
 */
public class RFXComBlinds1MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0919040600A21B010280", YR1326, 6, "41499.1", 8, STOP);
        testMessage("091905021A6280010000", MEDIAMOUNT, 2, "1729152.1", 0, OPEN);
    }
}

