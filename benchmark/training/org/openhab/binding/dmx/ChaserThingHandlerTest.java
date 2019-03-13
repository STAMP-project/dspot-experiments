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


import OnOffType.ON;
import java.util.Map;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.junit.Test;
import org.openhab.binding.dmx.internal.handler.ChaserThingHandler;
import org.openhab.binding.dmx.test.TestBridgeHandler;


/**
 * Tests cases for {@link ChaserThingHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class ChaserThingHandlerTest extends AbstractDmxThingTest {
    private static final String TEST_CHANNEL = "100";

    private static final String TEST_STEPS_INFINITE = "1000:100:1000|1000:200:-1";

    private static final String TEST_STEPS_REPEAT = "1000:115:1000|1000:210:1000";

    private final ThingUID THING_UID_CHASER = new ThingUID(THING_TYPE_CHASER, "testchaser");

    private final ChannelUID CHANNEL_UID_SWITCH = new ChannelUID(THING_UID_CHASER, CHANNEL_SWITCH);

    Map<String, Object> bridgeProperties;

    Map<String, Object> thingProperties;

    private Thing chaserThing;

    private TestBridgeHandler dmxBridgeHandler;

    private ChaserThingHandler chaserThingHandler;

    @Test
    public void testThingStatus() {
        thingProperties.put(CONFIG_CHASER_STEPS, ChaserThingHandlerTest.TEST_STEPS_INFINITE);
        initialize();
        assertThingStatus(chaserThing);
    }

    @Test
    public void testThingStatus_noBridge() {
        thingProperties.put(CONFIG_CHASER_STEPS, ChaserThingHandlerTest.TEST_STEPS_INFINITE);
        initialize();
        // check that thing is offline if no bridge found
        ChaserThingHandler chaserHandlerWithoutBridge = new ChaserThingHandler(chaserThing) {
            @Override
            @Nullable
            protected Bridge getBridge() {
                return null;
            }
        };
        assertThingStatusWithoutBridge(chaserHandlerWithoutBridge);
    }

    @Test
    public void holdInfiniteChaser() {
        initializeTestBridge();
        thingProperties.put(CONFIG_CHASER_STEPS, ChaserThingHandlerTest.TEST_STEPS_INFINITE);
        initialize();
        long currentTime = System.currentTimeMillis();
        chaserThingHandler.handleCommand(new ChannelUID(chaserThing.getUID(), CHANNEL_SWITCH), ON);
        // step I
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(100))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(100))));
        // step II (holds forever)
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(200))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 2000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(200))));
    }

    @Test
    public void runningChaser() {
        initializeTestBridge();
        thingProperties.put(CONFIG_CHASER_STEPS, ChaserThingHandlerTest.TEST_STEPS_REPEAT);
        initialize();
        long currentTime = System.currentTimeMillis();
        chaserThingHandler.handleCommand(new ChannelUID(chaserThing.getUID(), CHANNEL_SWITCH), ON);
        // step I
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(115))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(115))));
        // step II
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(210))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(210))));
        // step I (repeated)
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(115))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(115))));
    }

    @Test
    public void runningChaserWithResume() {
        initializeTestBridge();
        thingProperties.put(CONFIG_CHASER_STEPS, ChaserThingHandlerTest.TEST_STEPS_REPEAT);
        thingProperties.put(CONFIG_CHASER_RESUME_AFTER, true);
        initialize();
        dmxBridgeHandler.setDmxChannelValue(100, 193);
        long currentTime = System.currentTimeMillis();
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 0);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(193))));
        chaserThingHandler.handleCommand(new ChannelUID(chaserThing.getUID(), CHANNEL_SWITCH), ON);
        // step I
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(115))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(115))));
        // step II
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(210))));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(210))));
        // resume old state
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, 1000);
        waitForAssert(() -> assertThat(dmxBridgeHandler.getDmxChannelValue(100), is(equalTo(193))));
    }
}

