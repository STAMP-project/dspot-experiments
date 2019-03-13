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
import OwSensorType.EDS0068;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.onewire.internal.OwException;
import org.openhab.binding.onewire.test.AbstractDeviceTest;


/**
 * Tests cases for {@link DS2438}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class EDS006xTest extends AbstractDeviceTest {
    @Test
    public void temperatureChannel() {
        instantiateDevice(EDS0068);
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(10.0));
            testDevice.enableChannel(CHANNEL_TEMPERATURE);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler).readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_TEMPERATURE), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("10.0 ?C")));
            inOrder.verifyNoMoreInteractions();
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void humidityChannel() {
        instantiateDevice(EDS0068);
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(10.0));
            testDevice.enableChannel(CHANNEL_HUMIDITY);
            testDevice.enableChannel(CHANNEL_ABSOLUTE_HUMIDITY);
            testDevice.enableChannel(CHANNEL_DEWPOINT);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler, Mockito.times(2)).readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_HUMIDITY), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("10.0 %")));
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_ABSOLUTE_HUMIDITY), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("0.9381970824113001000 g/m?")));
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_DEWPOINT), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("-20.31395053870025 ?C")));
            inOrder.verifyNoMoreInteractions();
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void pressureChannel() {
        instantiateDevice(EDS0068);
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(2.0));
            testDevice.enableChannel(CHANNEL_PRESSURE);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler).readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_PRESSURE), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("2.0 mbar")));
            inOrder.verifyNoMoreInteractions();
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void lightChannel() {
        instantiateDevice(EDS0068);
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(100));
            testDevice.enableChannel(CHANNEL_LIGHT);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler).readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(CHANNEL_LIGHT), ArgumentMatchers.eq(new org.eclipse.smarthome.core.library.types.QuantityType("100 lx")));
            inOrder.verifyNoMoreInteractions();
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void noChannel() {
        instantiateDevice(EDS0068);
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalType(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(new DecimalType(2.0));
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verifyNoMoreInteractions();
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }
}

