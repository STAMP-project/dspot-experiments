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
import org.openhab.binding.nest.internal.handler.NestCameraHandler;


/**
 * Tests for {@link NestCameraHandler}.
 *
 * @author Wouter Born - Increase test coverage
 */
public class NestCameraHandlerTest extends NestThingHandlerOSGiTest {
    private static final ThingUID CAMERA_UID = new ThingUID(THING_TYPE_CAMERA, "camera1");

    private static final int CHANNEL_COUNT = 20;

    public NestCameraHandlerTest() {
        super(NestCameraHandler.class);
    }

    @Test
    public void completeCameraUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestCameraHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        // Camera channel group
        assertThatItemHasState(CHANNEL_CAMERA_APP_URL, new StringType("https://camera_app_url"));
        assertThatItemHasState(CHANNEL_CAMERA_AUDIO_INPUT_ENABLED, ON);
        assertThatItemHasState(CHANNEL_CAMERA_LAST_ONLINE_CHANGE, NestThingHandlerOSGiTest.parseDateTimeType("2017-01-22T08:19:20.000Z"));
        assertThatItemHasState(CHANNEL_CAMERA_PUBLIC_SHARE_ENABLED, OFF);
        assertThatItemHasState(CHANNEL_CAMERA_PUBLIC_SHARE_URL, new StringType("https://camera_public_share_url"));
        assertThatItemHasState(CHANNEL_CAMERA_SNAPSHOT_URL, new StringType("https://camera_snapshot_url"));
        assertThatItemHasState(CHANNEL_CAMERA_STREAMING, OFF);
        assertThatItemHasState(CHANNEL_CAMERA_VIDEO_HISTORY_ENABLED, OFF);
        assertThatItemHasState(CHANNEL_CAMERA_WEB_URL, new StringType("https://camera_web_url"));
        // Last event channel group
        assertThatItemHasState(CHANNEL_LAST_EVENT_ACTIVITY_ZONES, new StringType("id1,id2"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_ANIMATED_IMAGE_URL, new StringType("https://last_event_animated_image_url"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_APP_URL, new StringType("https://last_event_app_url"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_END_TIME, NestThingHandlerOSGiTest.parseDateTimeType("2017-01-22T07:40:38.680Z"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_HAS_MOTION, ON);
        assertThatItemHasState(CHANNEL_LAST_EVENT_HAS_PERSON, OFF);
        assertThatItemHasState(CHANNEL_LAST_EVENT_HAS_SOUND, OFF);
        assertThatItemHasState(CHANNEL_LAST_EVENT_IMAGE_URL, new StringType("https://last_event_image_url"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_START_TIME, NestThingHandlerOSGiTest.parseDateTimeType("2017-01-22T07:40:19.020Z"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_URLS_EXPIRE_TIME, NestThingHandlerOSGiTest.parseDateTimeType("2017-02-05T07:40:19.020Z"));
        assertThatItemHasState(CHANNEL_LAST_EVENT_WEB_URL, new StringType("https://last_event_web_url"));
        assertThatAllItemStatesAreNotNull();
    }

    @Test
    public void incompleteCameraUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestCameraHandlerTest.CHANNEL_COUNT));
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
    public void cameraGone() throws IOException {
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
    public void handleStreamingCommands() throws IOException {
        handleCommand(CHANNEL_CAMERA_STREAMING, ON);
        assertNestApiPropertyState(NestDataUtil.CAMERA1_DEVICE_ID, "is_streaming", "true");
        handleCommand(CHANNEL_CAMERA_STREAMING, OFF);
        assertNestApiPropertyState(NestDataUtil.CAMERA1_DEVICE_ID, "is_streaming", "false");
        handleCommand(CHANNEL_CAMERA_STREAMING, ON);
        assertNestApiPropertyState(NestDataUtil.CAMERA1_DEVICE_ID, "is_streaming", "true");
    }
}

