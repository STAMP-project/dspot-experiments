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
package org.openhab.binding.onewire.device;


import OnOffType.ON;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.onewire.internal.OwException;
import org.openhab.binding.onewire.test.AbstractDeviceTest;


/**
 * Tests cases for {@link DS18x20}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class DS18x20Test extends AbstractDeviceTest {
    @Test
    public void temperatureTest() {
        instantiateDevice();
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(15.0));
            testDevice.enableChannel(CHANNEL_TEMPERATURE);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler, Mockito.times(1)).readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_TEMPERATURE), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("15.0 ?C")));
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void temperatureIgnorePORTest() {
        instantiateDevice();
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(85.0));
            testDevice.enableChannel(CHANNEL_TEMPERATURE);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler, Mockito.times(1)).readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler, Mockito.times(0)).postUpdate(ArgumentMatchers.eq(CHANNEL_TEMPERATURE), ArgumentMatchers.any());
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }
}

