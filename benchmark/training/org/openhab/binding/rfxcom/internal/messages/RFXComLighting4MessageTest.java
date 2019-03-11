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


import OnOffType.ON;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.config.RFXComDeviceConfiguration;
import org.openhab.binding.rfxcom.internal.config.RFXComDeviceConfigurationBuilder;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComLighting4MessageTest {
    @Test
    public void basicBoundaryCheck() throws RFXComException {
        RFXComLighting4Message message = ((RFXComLighting4Message) (RFXComMessageFactory.createMessage(PacketType.LIGHTING4)));
        RFXComDeviceConfiguration build = new RFXComDeviceConfigurationBuilder().withDeviceId("90000").withPulse(300).withSubType("PT2262").build();
        message.setConfig(build);
        message.convertFromState(CHANNEL_COMMAND, ON);
        byte[] binaryMessage = message.decodeMessage();
        RFXComLighting4Message msg = ((RFXComLighting4Message) (RFXComMessageFactory.createMessage(binaryMessage)));
        Assert.assertEquals("Sensor Id", "90000", msg.getDeviceId());
    }

    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("091300E1D8AD59018F70", SubType.PT2262, "887509", 399, ON_9, 225, 2, 4, 9);
        testMessage("0913005FA9A9C901A170", SubType.PT2262, "694940", 417, ON_9, 95, 2, 4, 9);
        testMessage("091300021D155C01E960", SubType.PT2262, "119125", 489, ON_12, 2, 2, 4, 12);
        testMessage("091300D345DD99018C50", SubType.PT2262, "286169", 396, ON_9, 211, 2, 4, 9);
        testMessage("09130035D149A2017750", SubType.PT2262, "857242", 375, OFF_2, 53, 2, 2, 1);
        testMessage("0913000B4E462A012280", SubType.PT2262, "320610", 290, ON_10, 11, 3, 4, 10);
        testMessage("09130009232D2E013970", SubType.PT2262, "144082", 313, OFF_14, 9, 2, 14, 1);
        testMessage("091300CA0F8D2801AA70", SubType.PT2262, "63698", 426, ((byte) (8)), 202, 2, 8, 1);
    }

    @Test
    public void testSomeConradMessages() throws RFXComException {
        testMessage("0913003554545401A150", SubType.PT2262, "345413", 417, OFF_4, 53, 2, 4, 1);
    }

    @Test
    public void testPhenixMessages() throws RFXComException {
        List<String> onMessages = Arrays.asList("09130046044551013780", "09130048044551013780", "0913004A044551013980", "0913004C044551013780", "0913004E044551013780");
        for (String message : onMessages) {
            testMessage(message, SubType.PT2262, "17493", null, ON_1, null, 3, 4, 1);
        }
        List<String> offMessages = Arrays.asList("09130051044554013980", "09130053044554013680", "09130055044554013680", "09130057044554013680", "09130059044554013680", "0913005B044554013680", "0913005D044554013480", "09130060044554013980", "09130062044554013680", "09130064044554013280");
        for (String message : offMessages) {
            testMessage(message, SubType.PT2262, "17493", null, OFF_4, null, 3, 4, 1);
        }
    }
}

