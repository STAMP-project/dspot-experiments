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
package org.openhab.binding.onewire.internal;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openhab.binding.onewire.internal.device.OwSensorType;
import org.openhab.binding.onewire.test.AbstractThingHandlerTest;


/**
 * Tests cases for {@link BasicThingeHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class BasicThingHandlerTest extends AbstractThingHandlerTest {
    private static final String TEST_ID = "00.000000000000";

    @Test
    public void testInitializationEndsWithUnknown() throws OwException {
        Mockito.doAnswer(( answer) -> {
            return OwSensorType.DS2401;
        }).when(secondBridgeHandler).getType(ArgumentMatchers.any());
        thingHandler.initialize();
        waitForAssert(() -> assertEquals(ThingStatus.UNKNOWN, thingHandler.getThing().getStatusInfo().getStatus()));
    }

    @Test
    public void testRefreshAnalog() throws OwException {
        Mockito.doAnswer(( answer) -> {
            return OwSensorType.DS18B20;
        }).when(secondBridgeHandler).getType(ArgumentMatchers.any());
        thingHandler.initialize();
        waitForAssert(() -> assertEquals(ThingStatus.UNKNOWN, thingHandler.getThing().getStatusInfo().getStatus()));
        thingHandler.refresh(bridgeHandler, System.currentTimeMillis());
        inOrder.verify(bridgeHandler, Mockito.times(1)).checkPresence(new SensorId(BasicThingHandlerTest.TEST_ID));
        inOrder.verify(bridgeHandler, Mockito.times(1)).readDecimalType(ArgumentMatchers.eq(new SensorId(BasicThingHandlerTest.TEST_ID)), ArgumentMatchers.any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRefreshDigital() throws OwException {
        Mockito.doAnswer(( answer) -> {
            return OwSensorType.DS2408;
        }).when(secondBridgeHandler).getType(ArgumentMatchers.any());
        thingHandler.initialize();
        waitForAssert(() -> assertEquals(ThingStatus.UNKNOWN, thingHandler.getThing().getStatusInfo().getStatus()));
        thingHandler.refresh(bridgeHandler, System.currentTimeMillis());
        inOrder.verify(bridgeHandler, Mockito.times(1)).checkPresence(new SensorId(BasicThingHandlerTest.TEST_ID));
        inOrder.verify(bridgeHandler, Mockito.times(2)).readBitSet(ArgumentMatchers.eq(new SensorId(BasicThingHandlerTest.TEST_ID)), ArgumentMatchers.any());
        inOrder.verifyNoMoreInteractions();
    }
}

