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


import SubType.RAW_AES_KEELOQ;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden - Initial contribution of empty test
 * @author Mike Jagdis - added message handling and real test
 */
public class RFXComSecurity2MessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "1C21020000000000131211C30000000000000000000000000000000045";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComSecurity2Message msg = ((RFXComSecurity2Message) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", RAW_AES_KEELOQ, msg.subType);
        Assert.assertEquals("Seq Number", 0, msg.seqNbr);
        Assert.assertEquals("Sensor Id", "51450387", msg.getDeviceId());
        Assert.assertEquals("Button Status", 12, msg.buttonStatus);
        Assert.assertEquals("Battery Level", 4, msg.batteryLevel);
        Assert.assertEquals("Signal Level", 5, msg.signalLevel);
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

