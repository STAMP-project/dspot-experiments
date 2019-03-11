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
package org.openhab.binding.dsmr.internal.discovery;


import TelegramState.OK;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.binding.dsmr.internal.TelegramReaderUtil;
import org.openhab.binding.dsmr.internal.device.cosem.CosemObject;
import org.openhab.binding.dsmr.internal.device.cosem.CosemObjectType;
import org.openhab.binding.dsmr.internal.device.p1telegram.P1Telegram;
import org.openhab.binding.dsmr.internal.meter.DSMRMeterDescriptor;
import org.openhab.binding.dsmr.internal.meter.DSMRMeterType;


/**
 * Test class for {@link DSMRMeterDetector}.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
@RunWith(Parameterized.class)
public class DSMRMeterDetectorTest {
    // @formatter:on
    @Parameterized.Parameter(0)
    public String telegramName;

    @Parameterized.Parameter(1)
    public Set<DSMRMeterType.DSMRMeterType> expectedMeters;

    @Test
    public void testDetectMeters() {
        P1Telegram telegram = TelegramReaderUtil.readTelegram(telegramName, OK);
        DSMRMeterDetector detector = new DSMRMeterDetector();
        Map.Entry<Collection<DSMRMeterDescriptor>, Map<CosemObjectType, CosemObject>> entry = detector.detectMeters(telegram);
        Collection<DSMRMeterDescriptor> detectMeters = entry.getKey();
        Assert.assertEquals("Should detect correct number of meters", expectedMeters.size(), detectMeters.size());
        Assert.assertEquals("Should not have any undetected cosem objects", 0, entry.getValue().size());
        for (DSMRMeterType.DSMRMeterType meter : expectedMeters) {
            Assert.assertEquals(String.format("Meter '%s' not found: %s", meter, Arrays.toString(detectMeters.toArray(new DSMRMeterDescriptor[0]))), 1, detectMeters.stream().filter(( e) -> (e.getMeterType()) == meter).count());
        }
    }
}

