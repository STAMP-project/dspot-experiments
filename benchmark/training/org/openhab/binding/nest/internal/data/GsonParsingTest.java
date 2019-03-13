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
package org.openhab.binding.nest.internal.data;


import SIUnits.CELSIUS;
import SmokeDetector.BatteryHealth.OK;
import SmokeDetector.UiColorState.GREEN;
import Structure.HomeAwayState.HOME;
import Thermostat.Mode.HEAT;
import Thermostat.State.OFF;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GsonParsingTest {
    private final Logger logger = LoggerFactory.getLogger(GsonParsingTest.class);

    @Test
    public void verifyCompleteInput() throws IOException {
        TopLevelData topLevel = NestDataUtil.fromJson("top-level-data.json", TopLevelData.class);
        Assert.assertEquals(topLevel.getDevices().getThermostats().size(), 1);
        Assert.assertNotNull(topLevel.getDevices().getThermostats().get(NestDataUtil.THERMOSTAT1_DEVICE_ID));
        Assert.assertEquals(topLevel.getDevices().getCameras().size(), 2);
        Assert.assertNotNull(topLevel.getDevices().getCameras().get(NestDataUtil.CAMERA1_DEVICE_ID));
        Assert.assertNotNull(topLevel.getDevices().getCameras().get(NestDataUtil.CAMERA2_DEVICE_ID));
        Assert.assertEquals(topLevel.getDevices().getSmokeCoAlarms().size(), 4);
        Assert.assertNotNull(topLevel.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE1_DEVICE_ID));
        Assert.assertNotNull(topLevel.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE2_DEVICE_ID));
        Assert.assertNotNull(topLevel.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE3_DEVICE_ID));
        Assert.assertNotNull(topLevel.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE4_DEVICE_ID));
    }

    @Test
    public void verifyCompleteStreamingInput() throws IOException {
        TopLevelStreamingData topLevelStreamingData = NestDataUtil.fromJson("top-level-streaming-data.json", TopLevelStreamingData.class);
        Assert.assertEquals("/", topLevelStreamingData.getPath());
        TopLevelData data = topLevelStreamingData.getData();
        Assert.assertEquals(data.getDevices().getThermostats().size(), 1);
        Assert.assertNotNull(data.getDevices().getThermostats().get(NestDataUtil.THERMOSTAT1_DEVICE_ID));
        Assert.assertEquals(data.getDevices().getCameras().size(), 2);
        Assert.assertNotNull(data.getDevices().getCameras().get(NestDataUtil.CAMERA1_DEVICE_ID));
        Assert.assertNotNull(data.getDevices().getCameras().get(NestDataUtil.CAMERA2_DEVICE_ID));
        Assert.assertEquals(data.getDevices().getSmokeCoAlarms().size(), 4);
        Assert.assertNotNull(data.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE1_DEVICE_ID));
        Assert.assertNotNull(data.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE2_DEVICE_ID));
        Assert.assertNotNull(data.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE3_DEVICE_ID));
        Assert.assertNotNull(data.getDevices().getSmokeCoAlarms().get(NestDataUtil.SMOKE4_DEVICE_ID));
    }

    @Test
    public void verifyThermostat() throws IOException {
        Thermostat thermostat = NestDataUtil.fromJson("thermostat-data.json", Thermostat.class);
        logger.debug("Thermostat: {}", thermostat);
        Assert.assertTrue(thermostat.isOnline());
        Assert.assertTrue(thermostat.isCanHeat());
        Assert.assertTrue(thermostat.isHasLeaf());
        Assert.assertFalse(thermostat.isCanCool());
        Assert.assertFalse(thermostat.isFanTimerActive());
        Assert.assertFalse(thermostat.isLocked());
        Assert.assertFalse(thermostat.isSunlightCorrectionActive());
        Assert.assertTrue(thermostat.isSunlightCorrectionEnabled());
        Assert.assertFalse(thermostat.isUsingEmergencyHeat());
        Assert.assertEquals(NestDataUtil.THERMOSTAT1_DEVICE_ID, thermostat.getDeviceId());
        Assert.assertEquals(Integer.valueOf(15), thermostat.getFanTimerDuration());
        GsonParsingTest.assertEqualDateTime("2017-02-02T21:00:06.000Z", thermostat.getLastConnection());
        GsonParsingTest.assertEqualDateTime("1970-01-01T00:00:00.000Z", thermostat.getFanTimerTimeout());
        Assert.assertEquals(Double.valueOf(24.0), thermostat.getEcoTemperatureHigh());
        Assert.assertEquals(Double.valueOf(12.5), thermostat.getEcoTemperatureLow());
        Assert.assertEquals(Double.valueOf(22.0), thermostat.getLockedTempMax());
        Assert.assertEquals(Double.valueOf(20.0), thermostat.getLockedTempMin());
        Assert.assertEquals(HEAT, thermostat.getMode());
        Assert.assertEquals("Living Room (Living Room)", thermostat.getName());
        Assert.assertEquals("Living Room Thermostat (Living Room)", thermostat.getNameLong());
        Assert.assertEquals(null, thermostat.getPreviousHvacMode());
        Assert.assertEquals("5.6-7", thermostat.getSoftwareVersion());
        Assert.assertEquals(OFF, thermostat.getHvacState());
        Assert.assertEquals(NestDataUtil.STRUCTURE1_STRUCTURE_ID, thermostat.getStructureId());
        Assert.assertEquals(Double.valueOf(15.5), thermostat.getTargetTemperature());
        Assert.assertEquals(Double.valueOf(24.0), thermostat.getTargetTemperatureHigh());
        Assert.assertEquals(Double.valueOf(20.0), thermostat.getTargetTemperatureLow());
        Assert.assertEquals(CELSIUS, thermostat.getTemperatureUnit());
        Assert.assertEquals(Integer.valueOf(0), thermostat.getTimeToTarget());
        Assert.assertEquals(NestDataUtil.THERMOSTAT1_WHERE_ID, thermostat.getWhereId());
        Assert.assertEquals("Living Room", thermostat.getWhereName());
    }

    @Test
    public void thermostatTimeToTargetSupportedValueParsing() {
        Assert.assertEquals(((Integer) (0)), Thermostat.parseTimeToTarget("~0"));
        Assert.assertEquals(((Integer) (5)), Thermostat.parseTimeToTarget("<5"));
        Assert.assertEquals(((Integer) (10)), Thermostat.parseTimeToTarget("<10"));
        Assert.assertEquals(((Integer) (15)), Thermostat.parseTimeToTarget("~15"));
        Assert.assertEquals(((Integer) (90)), Thermostat.parseTimeToTarget("~90"));
        Assert.assertEquals(((Integer) (120)), Thermostat.parseTimeToTarget(">120"));
    }

    @Test(expected = NumberFormatException.class)
    public void thermostatTimeToTargetUnsupportedValueParsing() {
        Thermostat.parseTimeToTarget("#5");
    }

    @Test
    public void verifyCamera() throws IOException {
        Camera camera = NestDataUtil.fromJson("camera-data.json", Camera.class);
        logger.debug("Camera: {}", camera);
        Assert.assertTrue(camera.isOnline());
        Assert.assertEquals("Upstairs", camera.getName());
        Assert.assertEquals("Upstairs Camera", camera.getNameLong());
        Assert.assertEquals(NestDataUtil.STRUCTURE1_STRUCTURE_ID, camera.getStructureId());
        Assert.assertEquals(NestDataUtil.CAMERA1_WHERE_ID, camera.getWhereId());
        Assert.assertTrue(camera.isAudioInputEnabled());
        Assert.assertFalse(camera.isPublicShareEnabled());
        Assert.assertFalse(camera.isStreaming());
        Assert.assertFalse(camera.isVideoHistoryEnabled());
        Assert.assertEquals("https://camera_app_url", camera.getAppUrl());
        Assert.assertEquals(NestDataUtil.CAMERA1_DEVICE_ID, camera.getDeviceId());
        Assert.assertNull(camera.getLastConnection());
        GsonParsingTest.assertEqualDateTime("2017-01-22T08:19:20.000Z", camera.getLastIsOnlineChange());
        Assert.assertNull(camera.getPublicShareUrl());
        Assert.assertEquals("https://camera_snapshot_url", camera.getSnapshotUrl());
        Assert.assertEquals("205-600052", camera.getSoftwareVersion());
        Assert.assertEquals("https://camera_web_url", camera.getWebUrl());
        Assert.assertEquals("https://last_event_animated_image_url", camera.getLastEvent().getAnimatedImageUrl());
        Assert.assertEquals(2, camera.getLastEvent().getActivityZones().size());
        Assert.assertEquals("id1", camera.getLastEvent().getActivityZones().get(0));
        Assert.assertEquals("https://last_event_app_url", camera.getLastEvent().getAppUrl());
        GsonParsingTest.assertEqualDateTime("2017-01-22T07:40:38.680Z", camera.getLastEvent().getEndTime());
        Assert.assertEquals("https://last_event_image_url", camera.getLastEvent().getImageUrl());
        GsonParsingTest.assertEqualDateTime("2017-01-22T07:40:19.020Z", camera.getLastEvent().getStartTime());
        GsonParsingTest.assertEqualDateTime("2017-02-05T07:40:19.020Z", camera.getLastEvent().getUrlsExpireTime());
        Assert.assertEquals("https://last_event_web_url", camera.getLastEvent().getWebUrl());
        Assert.assertTrue(camera.getLastEvent().isHasMotion());
        Assert.assertFalse(camera.getLastEvent().isHasPerson());
        Assert.assertFalse(camera.getLastEvent().isHasSound());
    }

    @Test
    public void verifySmokeDetector() throws IOException {
        SmokeDetector smokeDetector = NestDataUtil.fromJson("smoke-detector-data.json", SmokeDetector.class);
        logger.debug("SmokeDetector: {}", smokeDetector);
        Assert.assertTrue(smokeDetector.isOnline());
        Assert.assertEquals(NestDataUtil.SMOKE1_WHERE_ID, smokeDetector.getWhereId());
        Assert.assertEquals(NestDataUtil.SMOKE1_DEVICE_ID, smokeDetector.getDeviceId());
        Assert.assertEquals("Downstairs", smokeDetector.getName());
        Assert.assertEquals("Downstairs Nest Protect", smokeDetector.getNameLong());
        GsonParsingTest.assertEqualDateTime("2017-02-02T20:53:05.338Z", smokeDetector.getLastConnection());
        Assert.assertEquals(OK, smokeDetector.getBatteryHealth());
        Assert.assertEquals(SmokeDetector.AlarmState.OK, smokeDetector.getCoAlarmState());
        Assert.assertEquals(SmokeDetector.AlarmState.OK, smokeDetector.getSmokeAlarmState());
        Assert.assertEquals("3.1rc9", smokeDetector.getSoftwareVersion());
        Assert.assertEquals(NestDataUtil.STRUCTURE1_STRUCTURE_ID, smokeDetector.getStructureId());
        Assert.assertEquals(GREEN, smokeDetector.getUiColorState());
    }

    @Test
    public void verifyAccessToken() throws IOException {
        AccessTokenData accessToken = NestDataUtil.fromJson("access-token-data.json", AccessTokenData.class);
        logger.debug("AccessTokenData: {}", accessToken);
        Assert.assertEquals("access_token", accessToken.getAccessToken());
        Assert.assertEquals(Long.valueOf(315360000L), accessToken.getExpiresIn());
    }

    @Test
    public void verifyStructure() throws IOException {
        Structure structure = NestDataUtil.fromJson("structure-data.json", Structure.class);
        logger.debug("Structure: {}", structure);
        Assert.assertEquals("Home", structure.getName());
        Assert.assertEquals("US", structure.getCountryCode());
        Assert.assertEquals("98056", structure.getPostalCode());
        Assert.assertEquals(HOME, structure.getAway());
        GsonParsingTest.assertEqualDateTime("2017-02-02T03:10:08.000Z", structure.getEtaBegin());
        Assert.assertNull(structure.getEta());
        Assert.assertNull(structure.getPeakPeriodEndTime());
        Assert.assertNull(structure.getPeakPeriodStartTime());
        Assert.assertEquals(NestDataUtil.STRUCTURE1_STRUCTURE_ID, structure.getStructureId());
        Assert.assertEquals("America/Los_Angeles", structure.getTimeZone());
        Assert.assertFalse(structure.isRhrEnrollment());
    }

    @Test
    public void verifyError() throws IOException {
        ErrorData error = NestDataUtil.fromJson("error-data.json", ErrorData.class);
        logger.debug("ErrorData: {}", error);
        Assert.assertEquals("blocked", error.getError());
        Assert.assertEquals("https://developer.nest.com/documentation/cloud/error-messages#blocked", error.getType());
        Assert.assertEquals("blocked", error.getMessage());
        Assert.assertEquals("bb514046-edc9-4bca-8239-f7a3cfb0925a", error.getInstance());
    }
}

