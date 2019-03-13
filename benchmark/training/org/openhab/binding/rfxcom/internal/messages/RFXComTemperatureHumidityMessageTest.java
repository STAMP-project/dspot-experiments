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
 * @author Ivan F. Martinez
 * @author Martin van Wingerden
 */
public class RFXComTemperatureHumidityMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("0A5201800F0201294C0349", TH1, 128, 3842, 29.7, 76, WET, 4, 9);
        testMessage("0A520211700200A72D0089", TH2, 17, 28674, 16.7, 45, NORMAL, 8, 9);
        testMessage("0A5205D42F000082590379", TH5, 212, 12032, 13, 89, WET, 7, 9);
    }
}

