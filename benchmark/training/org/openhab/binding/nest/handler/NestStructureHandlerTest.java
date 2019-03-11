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
import org.openhab.binding.nest.internal.handler.NestStructureHandler;


/**
 * Tests for {@link NestStructureHandler}.
 *
 * @author Wouter Born - Increase test coverage
 */
public class NestStructureHandlerTest extends NestThingHandlerOSGiTest {
    private static final ThingUID STRUCTURE_UID = new ThingUID(THING_TYPE_STRUCTURE, "structure1");

    private static final int CHANNEL_COUNT = 11;

    public NestStructureHandlerTest() {
        super(NestStructureHandler.class);
    }

    @Test
    public void completeStructureUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestStructureHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatItemHasState(CHANNEL_AWAY, new StringType("HOME"));
        assertThatItemHasState(CHANNEL_CO_ALARM_STATE, new StringType("OK"));
        assertThatItemHasState(CHANNEL_COUNTRY_CODE, new StringType("US"));
        assertThatItemHasState(CHANNEL_ETA_BEGIN, NestThingHandlerOSGiTest.parseDateTimeType("2017-02-02T03:10:08.000Z"));
        assertThatItemHasState(CHANNEL_PEAK_PERIOD_END_TIME, NestThingHandlerOSGiTest.parseDateTimeType("2017-07-01T01:03:08.400Z"));
        assertThatItemHasState(CHANNEL_PEAK_PERIOD_START_TIME, NestThingHandlerOSGiTest.parseDateTimeType("2017-06-01T13:31:10.870Z"));
        assertThatItemHasState(CHANNEL_POSTAL_CODE, new StringType("98056"));
        assertThatItemHasState(CHANNEL_RUSH_HOUR_REWARDS_ENROLLMENT, OFF);
        assertThatItemHasState(CHANNEL_SECURITY_STATE, new StringType("OK"));
        assertThatItemHasState(CHANNEL_SMOKE_ALARM_STATE, new StringType("OK"));
        assertThatItemHasState(CHANNEL_TIME_ZONE, new StringType("America/Los_Angeles"));
        assertThatAllItemStatesAreNotNull();
    }

    @Test
    public void incompleteStructureUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestStructureHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatAllItemStatesAreNotNull();
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.INCOMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatAllItemStatesAreNull();
    }

    @Test
    public void structureGone() throws IOException {
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

    @Test
    public void handleAwayCommands() throws IOException {
        handleCommand(CHANNEL_AWAY, new StringType("AWAY"));
        assertNestApiPropertyState(NestDataUtil.STRUCTURE1_STRUCTURE_ID, "away", "away");
        handleCommand(CHANNEL_AWAY, new StringType("HOME"));
        assertNestApiPropertyState(NestDataUtil.STRUCTURE1_STRUCTURE_ID, "away", "home");
        handleCommand(CHANNEL_AWAY, new StringType("AWAY"));
        assertNestApiPropertyState(NestDataUtil.STRUCTURE1_STRUCTURE_ID, "away", "away");
    }
}

