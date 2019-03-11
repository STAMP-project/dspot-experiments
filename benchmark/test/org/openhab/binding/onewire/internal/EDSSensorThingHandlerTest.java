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


import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.onewire.test.AbstractThingHandlerTest;


/**
 * Tests cases for {@link EDSSensorThingHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class EDSSensorThingHandlerTest extends AbstractThingHandlerTest {
    private static final String TEST_ID = "00.000000000000";

    private static final ThingUID THING_UID = new ThingUID(THING_TYPE_EDS_ENV, "testthing");

    private static final ChannelUID CHANNEL_UID_TEMPERATURE = new ChannelUID(EDSSensorThingHandlerTest.THING_UID, CHANNEL_TEMPERATURE);

    private static final ChannelUID CHANNEL_UID_HUMIDITY = new ChannelUID(EDSSensorThingHandlerTest.THING_UID, CHANNEL_HUMIDITY);

    private static final ChannelUID CHANNEL_UID_ABSOLUTE_HUMIDITY = new ChannelUID(EDSSensorThingHandlerTest.THING_UID, CHANNEL_ABSOLUTE_HUMIDITY);

    private static final ChannelUID CHANNEL_UID_DEWPOINT = new ChannelUID(EDSSensorThingHandlerTest.THING_UID, CHANNEL_DEWPOINT);

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
        inOrder.verify(bridgeHandler, Mockito.times(1)).checkPresence(new SensorId(EDSSensorThingHandlerTest.TEST_ID));
        inOrder.verify(bridgeHandler, Mockito.times(2)).readDecimalType(ArgumentMatchers.eq(new SensorId(EDSSensorThingHandlerTest.TEST_ID)), ArgumentMatchers.any());
        inOrder.verifyNoMoreInteractions();
    }
}

