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
package org.openhab.binding.nest.handler;


import ThingStatus.OFFLINE;
import ThingStatusDetail.GONE;
import java.io.IOException;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.nest.internal.data.NestDataUtil;
import org.openhab.binding.nest.internal.handler.NestSmokeDetectorHandler;


/**
 * Tests for {@link NestSmokeDetectorHandler}.
 *
 * @author Wouter Born - Increase test coverage
 */
public class NestSmokeDetectorHandlerTest extends NestThingHandlerOSGiTest {
    private static final ThingUID SMOKE_DETECTOR_UID = new ThingUID(THING_TYPE_SMOKE_DETECTOR, "smoke1");

    private static final int CHANNEL_COUNT = 7;

    public NestSmokeDetectorHandlerTest() {
        super(NestSmokeDetectorHandler.class);
    }

    @Test
    public void completeSmokeDetectorUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestSmokeDetectorHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatItemHasState(CHANNEL_CO_ALARM_STATE, new StringType("OK"));
        assertThatItemHasState(CHANNEL_LAST_CONNECTION, NestThingHandlerOSGiTest.parseDateTimeType("2017-02-02T20:53:05.338Z"));
        assertThatItemHasState(CHANNEL_LAST_MANUAL_TEST_TIME, NestThingHandlerOSGiTest.parseDateTimeType("2016-10-31T23:59:59.000Z"));
        assertThatItemHasState(CHANNEL_LOW_BATTERY, OFF);
        assertThatItemHasState(CHANNEL_MANUAL_TEST_ACTIVE, OFF);
        assertThatItemHasState(CHANNEL_SMOKE_ALARM_STATE, new StringType("OK"));
        assertThatItemHasState(CHANNEL_UI_COLOR_STATE, new StringType("GREEN"));
        assertThatAllItemStatesAreNotNull();
    }

    @Test
    public void incompleteSmokeDetectorUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestSmokeDetectorHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatAllItemStatesAreNotNull();
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.INCOMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.UNKNOWN)));
        assertThatAllItemStatesAreNull();
    }

    @Test
    public void smokeDetectorGone() throws IOException {
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.EMPTY_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.OFFLINE)));
        Assert.assertThat(thing.getStatusInfo().getStatusDetail(), Is.is(GONE));
    }

    @Test
    public void channelRefresh() throws IOException {
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatAllItemStatesAreNotNull();
        updateAllItemStatesToNull();
        assertThatAllItemStatesAreNull();
        refreshAllChannels();
        assertThatAllItemStatesAreNotNull();
    }
}

