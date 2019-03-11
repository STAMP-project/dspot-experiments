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
import org.openhab.binding.onewire.test.AbstractThingHandlerTest;


/**
 * Tests cases for {@link MultisensorThingHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class MultisensorThingHandlerTest extends AbstractThingHandlerTest {
    private static final String TEST_ID = "00.000000000000";

    @Test
    public void testInitializationEndsWithUnknown() {
        thingHandler.initialize();
        waitForAssert(() -> assertEquals(ThingStatus.UNKNOWN, thingHandler.getThing().getStatusInfo().getStatus()));
    }

    @Test
    public void testRefresh() throws OwException {
        thingHandler.initialize();
        // needed to determine initialization is finished
        waitForAssert(() -> assertEquals(ThingStatus.UNKNOWN, thingHandler.getThing().getStatusInfo().getStatus()));
        thingHandler.refresh(bridgeHandler, System.currentTimeMillis());
        inOrder.verify(bridgeHandler, Mockito.times(1)).checkPresence(new SensorId(MultisensorThingHandlerTest.TEST_ID));
        inOrder.verify(bridgeHandler, Mockito.times(3)).readDecimalType(ArgumentMatchers.eq(new SensorId(MultisensorThingHandlerTest.TEST_ID)), ArgumentMatchers.any());
        inOrder.verifyNoMoreInteractions();
    }
}

