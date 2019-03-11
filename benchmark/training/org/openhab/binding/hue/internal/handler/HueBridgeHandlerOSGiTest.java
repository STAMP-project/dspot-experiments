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
package org.openhab.binding.hue.internal.handler;


import ConfigStatusMessage.Builder;
import HueConfigStatusMessage.IP_ADDRESS_MISSING;
import ThingStatus.OFFLINE;
import ThingStatusDetail.BRIDGE_OFFLINE;
import ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.hue.internal.HueBridge;
import org.openhab.binding.hue.internal.exceptions.ApiException;
import org.openhab.binding.hue.internal.exceptions.LinkButtonException;
import org.openhab.binding.hue.internal.exceptions.UnauthorizedException;
import org.openhab.binding.hue.test.AbstractHueOSGiTest;


/**
 * Tests for {@link HueBridgeHandler}.
 *
 * @author Oliver Libutzki - Initial contribution
 * @author Michael Grammling - Initial contribution
 * @author Denis Dudnik - switched to internally integrated source of Jue library
 */
public class HueBridgeHandlerOSGiTest extends AbstractHueOSGiTest {
    private final ThingTypeUID BRIDGE_THING_TYPE_UID = new ThingTypeUID(BINDING_ID, "bridge");

    private static final String TEST_USER_NAME = "eshTestUser";

    private static final String DUMMY_HOST = "1.2.3.4";

    private ThingRegistry thingRegistry;

    private ScheduledExecutorService scheduler;

    @Test
    public void assertThatANewUserIsAddedToConfigIfNotExistingYet() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, HueBridgeHandlerOSGiTest.DUMMY_HOST);
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        hueBridgeHandler.thingUpdated(bridge);
        injectBridge(hueBridgeHandler, new HueBridge(HueBridgeHandlerOSGiTest.DUMMY_HOST, 80, HTTP, scheduler) {
            @Override
            public String link(String deviceType) throws IOException, ApiException {
                return HueBridgeHandlerOSGiTest.TEST_USER_NAME;
            }
        });
        hueBridgeHandler.onNotAuthenticated();
        Assert.assertThat(bridge.getConfiguration().get(USER_NAME), CoreMatchers.equalTo(HueBridgeHandlerOSGiTest.TEST_USER_NAME));
    }

    @Test
    public void assertThatAnExistingUserIsUsedIfAuthenticationWasSuccessful() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, HueBridgeHandlerOSGiTest.DUMMY_HOST);
        configuration.put(USER_NAME, HueBridgeHandlerOSGiTest.TEST_USER_NAME);
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        hueBridgeHandler.thingUpdated(bridge);
        injectBridge(hueBridgeHandler, new HueBridge(HueBridgeHandlerOSGiTest.DUMMY_HOST, 80, HTTP, scheduler) {
            @Override
            public void authenticate(String userName) throws IOException, ApiException {
            }
        });
        hueBridgeHandler.onNotAuthenticated();
        Assert.assertThat(bridge.getConfiguration().get(USER_NAME), CoreMatchers.equalTo(HueBridgeHandlerOSGiTest.TEST_USER_NAME));
    }

    @Test
    public void assertCorrectStatusIfAuthenticationFailedForOldUser() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, HueBridgeHandlerOSGiTest.DUMMY_HOST);
        configuration.put(USER_NAME, "notAuthenticatedUser");
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        hueBridgeHandler.thingUpdated(bridge);
        injectBridge(hueBridgeHandler, new HueBridge(HueBridgeHandlerOSGiTest.DUMMY_HOST, 80, HTTP, scheduler) {
            @Override
            public void authenticate(String userName) throws IOException, ApiException {
                throw new UnauthorizedException();
            }
        });
        hueBridgeHandler.onNotAuthenticated();
        Assert.assertEquals("notAuthenticatedUser", bridge.getConfiguration().get(USER_NAME));
        Assert.assertEquals(OFFLINE, bridge.getStatus());
        Assert.assertEquals(CONFIGURATION_ERROR, bridge.getStatusInfo().getStatusDetail());
    }

    @Test
    public void verifyStatusIfLinkButtonIsNotPressed() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, HueBridgeHandlerOSGiTest.DUMMY_HOST);
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        hueBridgeHandler.thingUpdated(bridge);
        injectBridge(hueBridgeHandler, new HueBridge(HueBridgeHandlerOSGiTest.DUMMY_HOST, 80, HTTP, scheduler) {
            @Override
            public String link(String deviceType) throws IOException, ApiException {
                throw new LinkButtonException();
            }
        });
        hueBridgeHandler.onNotAuthenticated();
        Assert.assertNull(bridge.getConfiguration().get(USER_NAME));
        Assert.assertEquals(OFFLINE, bridge.getStatus());
        Assert.assertEquals(CONFIGURATION_ERROR, bridge.getStatusInfo().getStatusDetail());
    }

    @Test
    public void verifyStatusIfNewUserCannotBeCreated() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, HueBridgeHandlerOSGiTest.DUMMY_HOST);
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        hueBridgeHandler.thingUpdated(bridge);
        injectBridge(hueBridgeHandler, new HueBridge(HueBridgeHandlerOSGiTest.DUMMY_HOST, 80, HTTP, scheduler) {
            @Override
            public String link(String deviceType) throws IOException, ApiException {
                throw new ApiException();
            }
        });
        hueBridgeHandler.onNotAuthenticated();
        Assert.assertNull(bridge.getConfiguration().get(USER_NAME));
        Assert.assertEquals(OFFLINE, bridge.getStatus());
        Assert.assertEquals(CONFIGURATION_ERROR, bridge.getStatusInfo().getStatusDetail());
    }

    @Test
    public void verifyOfflineIsSetWithoutBridgeOfflineStatus() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, HueBridgeHandlerOSGiTest.DUMMY_HOST);
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        hueBridgeHandler.thingUpdated(bridge);
        hueBridgeHandler.onConnectionLost();
        Assert.assertEquals(OFFLINE, bridge.getStatus());
        Assert.assertNotEquals(BRIDGE_OFFLINE, bridge.getStatusInfo().getStatusDetail());
    }

    @Test
    public void assertThatAStatusConfigurationMessageForMissingBridgeIPIsProperlyReturnedIPIsNull() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, null);
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        ConfigStatusMessage expected = Builder.error(HOST).withMessageKeySuffix(IP_ADDRESS_MISSING).withArguments(HOST).build();
        waitForAssert(() -> assertEquals(expected, hueBridgeHandler.getConfigStatus().iterator().next()));
    }

    @Test
    public void assertThatAStatusConfigurationMessageForMissingBridgeIPIsProperlyReturnedIPIsAnEmptyString() {
        Configuration configuration = new Configuration();
        configuration.put(HOST, "");
        configuration.put(PROPERTY_SERIAL_NUMBER, "testSerialNumber");
        Bridge bridge = createBridgeThing(configuration);
        HueBridgeHandler hueBridgeHandler = getThingHandler(bridge, HueBridgeHandler.class);
        ConfigStatusMessage expected = Builder.error(HOST).withMessageKeySuffix(IP_ADDRESS_MISSING).withArguments(HOST).build();
        waitForAssert(() -> assertEquals(expected, hueBridgeHandler.getConfigStatus().iterator().next()));
    }
}

