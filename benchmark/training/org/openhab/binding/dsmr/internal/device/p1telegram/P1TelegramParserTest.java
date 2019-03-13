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
package org.openhab.binding.dsmr.internal.device.p1telegram;


import TelegramState.OK;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.binding.dsmr.internal.TelegramReaderUtil;


/**
 * Test class for {@link P1TelegramParser}.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
@RunWith(Parameterized.class)
public class P1TelegramParserTest {
    // @formatter:on
    @Parameterized.Parameter(0)
    public String telegramName;

    @Parameterized.Parameter(1)
    public int numberOfCosemObjects;

    @Test
    public void testParsing() {
        P1Telegram telegram = TelegramReaderUtil.readTelegram(telegramName, OK);
        Assert.assertEquals("Expected number of objects", numberOfCosemObjects, telegram.getCosemObjects().stream().mapToInt(( o) -> o.getCosemValues().size()).sum());
    }
}

