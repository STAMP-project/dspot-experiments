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
package org.openhab.binding.dmx;


import java.util.Map;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.test.java.JavaTest;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.dmx.internal.DmxBridgeHandler;
import org.openhab.binding.dmx.internal.multiverse.BaseDmxChannel;
import org.openhab.binding.dmx.internal.multiverse.Universe;


/**
 * Tests cases for {@link DmxBridgeHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class DmxBridgeHandlerTest extends JavaTest {
    /**
     * simple DmxBridgeHandlerImplementation
     *
     * @author Jan N. Klug
     */
    public class DmxBridgeHandlerImpl extends DmxBridgeHandler {
        public DmxBridgeHandlerImpl(Bridge dmxBridge) {
            super(dmxBridge);
        }

        @Override
        public void openConnection() {
        }

        @Override
        protected void sendDmxData() {
        }

        @Override
        protected void closeConnection() {
        }

        @Override
        public void initialize() {
            universe = new Universe(DmxBridgeHandlerTest.TEST_UNIVERSE);
            updateConfiguration();
            updateStatus(ThingStatus.ONLINE);
        }
    }

    private static final int TEST_UNIVERSE = 1;

    private static final int TEST_CHANNEL = 100;

    private final ThingTypeUID THING_TYPE_TEST_BRIDGE = new ThingTypeUID(BINDING_ID, "testbridge");

    private final ThingUID BRIDGE_UID_TEST = new ThingUID(THING_TYPE_TEST_BRIDGE, "testbridge");

    private final ChannelUID CHANNEL_UID_MUTE = new ChannelUID(BRIDGE_UID_TEST, CHANNEL_MUTE);

    Map<String, Object> bridgeProperties;

    private Bridge bridge;

    private DmxBridgeHandlerTest.DmxBridgeHandlerImpl bridgeHandler;

    @Test
    public void assertBridgeStatus() {
        waitForAssert(() -> assertEquals(ThingStatus.ONLINE, bridge.getStatusInfo().getStatus()));
    }

    @Test
    public void assertRetrievingOfChannels() {
        BaseDmxChannel channel = new BaseDmxChannel(DmxBridgeHandlerTest.TEST_UNIVERSE, DmxBridgeHandlerTest.TEST_CHANNEL);
        BaseDmxChannel returnedChannel = bridgeHandler.getDmxChannel(channel, ThingBuilder.create(THING_TYPE_DIMMER, "testthing").build());
        Integer channelId = returnedChannel.getChannelId();
        Assert.assertThat(channelId, Is.is(DmxBridgeHandlerTest.TEST_CHANNEL));
    }
}

