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
 * @author Ivan F. Martinez
 * @author Martin van Wingerden
 * @since 1.9.0
 */
public class RFXComTemperatureHumidityMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0A5201800F0201294C0349", THGN122_123_132_THGR122_228_238_268, 128, 3842, 29.7, 76, WET, 4, 9);
        testMessage("0A520211700200A72D0089", THGN800_THGR810, 17, 28674, 16.7, 45, NORMAL, 8, 9);
        testMessage("0A5205D42F000082590379", WTGR800, 212, 12032, 13, 89, WET, 7, 9);
    }
}

