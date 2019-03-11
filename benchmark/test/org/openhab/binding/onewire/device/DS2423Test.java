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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.onewire.internal.OwException;
import org.openhab.binding.onewire.test.AbstractDeviceTest;


/**
 * Tests cases for {@link DS2423}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class DS2423Test extends AbstractDeviceTest {
    @Test
    public void counterChannelTest() {
        instantiateDevice();
        List<State> returnValue = new ArrayList<>();
        returnValue.add(new DecimalType(1408));
        returnValue.add(new DecimalType(3105));
        try {
            Mockito.when(mockBridgeHandler.checkPresence(testSensorId)).thenReturn(ON);
            Mockito.when(mockBridgeHandler.readDecimalTypeArray(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any())).thenReturn(returnValue);
            testDevice.configureChannels();
            testDevice.refresh(mockBridgeHandler, true);
            inOrder.verify(mockBridgeHandler, Mockito.times(1)).readDecimalTypeArray(ArgumentMatchers.eq(testSensorId), ArgumentMatchers.any());
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(channelName(0)), ArgumentMatchers.eq(returnValue.get(0)));
            inOrder.verify(mockThingHandler).postUpdate(ArgumentMatchers.eq(channelName(1)), ArgumentMatchers.eq(returnValue.get(1)));
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }
}

