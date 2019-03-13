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


import PacketType.BAROMETRIC;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComMessageNotImplementedException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComBarometricMessageTest {
    @Test(expected = RFXComMessageNotImplementedException.class)
    public void checkNotImplemented() throws Exception {
        RFXComMessageFactory.createMessage(BAROMETRIC);
    }
}

