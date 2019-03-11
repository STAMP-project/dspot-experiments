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


import RFXComDateTimeMessage.SubType.RTGR328N;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComDateTimeMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        String hexMessage = "0D580117B90003041D030D150A69";
        byte[] message = HexUtils.hexToBytes(hexMessage);
        RFXComDateTimeMessage msg = ((RFXComDateTimeMessage) (RFXComMessageFactory.createMessage(message)));
        Assert.assertEquals("SubType", RTGR328N, msg.subType);
        Assert.assertEquals("Seq Number", 23, ((short) ((msg.seqNbr) & 255)));
        Assert.assertEquals("Sensor Id", "47360", msg.getDeviceId());
        Assert.assertEquals("Date time", "2003-04-29T13:21:10", msg.dateTime);
        Assert.assertEquals("Signal Level", 2, RFXComTestHelper.getActualIntValue(msg, CHANNEL_SIGNAL_LEVEL));
        Assert.assertEquals("Converted value", DateTimeType.valueOf("2003-04-29T13:21:10"), msg.convertToState(CHANNEL_DATE_TIME));
        byte[] decoded = msg.decodeMessage();
        Assert.assertEquals("Message converted back", hexMessage, HexUtils.bytesToHex(decoded));
    }
}

