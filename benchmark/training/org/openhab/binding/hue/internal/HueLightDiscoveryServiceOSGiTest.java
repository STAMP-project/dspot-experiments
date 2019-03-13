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
package org.openhab.binding.hue.internal;


import DiscoveryResultFlag.NEW;
import ThingStatus.ONLINE;
import ThingStatusDetail.NONE;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.builder.ThingStatusInfoBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.hue.internal.discovery.HueLightDiscoveryService;
import org.openhab.binding.hue.internal.handler.HueBridgeHandler;
import org.openhab.binding.hue.test.AbstractHueOSGiTest;


/**
 * Tests for {@link HueLightDiscoveryService}.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Andre Fuechsel - added test 'assert start search is called()'
- modified tests after introducing the generic thing types
 * @author Denis Dudnik - switched to internally integrated source of Jue library
 * @author Markus Rathgeb - migrated to plain Java test
 */
public class HueLightDiscoveryServiceOSGiTest extends AbstractHueOSGiTest {
    protected HueThingHandlerFactory hueThingHandlerFactory;

    protected DiscoveryListener discoveryListener;

    protected ThingRegistry thingRegistry;

    protected Bridge hueBridge;

    protected HueBridgeHandler hueBridgeHandler;

    protected HueLightDiscoveryService discoveryService;

    protected final ThingTypeUID BRIDGE_THING_TYPE_UID = new ThingTypeUID("hue", "bridge");

    protected final ThingUID BRIDGE_THING_UID = new ThingUID(BRIDGE_THING_TYPE_UID, "testBridge");

    @Test
    public void hueLightRegistration() {
        FullLight light = new FullLight();
        light.setId("1");
        light.setModelID("LCT001");
        light.setType("Extended color light");
        AtomicReference<DiscoveryResult> resultWrapper = new AtomicReference<>();
        registerDiscoveryListener(new DiscoveryListener() {
            @Override
            public void thingDiscovered(DiscoveryService source, DiscoveryResult result) {
                resultWrapper.set(result);
            }

            @Override
            public void thingRemoved(DiscoveryService source, ThingUID thingUID) {
            }

            @Override
            public Collection<ThingUID> removeOlderResults(DiscoveryService source, long timestamp, Collection<ThingTypeUID> thingTypeUIDs, ThingUID bridgeUID) {
                return null;
            }
        });
        discoveryService.onLightAdded(null, light);
        waitForAssert(() -> {
            assertTrue(((resultWrapper.get()) != null));
        });
        final DiscoveryResult result = resultWrapper.get();
        Assert.assertThat(result.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(result.getThingUID().toString(), CoreMatchers.is(("hue:0210:testBridge:" + (light.getId()))));
        Assert.assertThat(result.getThingTypeUID(), CoreMatchers.is(THING_TYPE_EXTENDED_COLOR_LIGHT));
        Assert.assertThat(result.getBridgeUID(), CoreMatchers.is(hueBridge.getUID()));
        Assert.assertThat(result.getProperties().get(LIGHT_ID), CoreMatchers.is(light.getId()));
    }

    @Test
    public void startSearchIsCalled() {
        final AtomicBoolean searchHasBeenTriggered = new AtomicBoolean(false);
        MockedHttpClient mockedHttpClient = new MockedHttpClient() {
            @Override
            public Result put(String address, String body) throws IOException {
                return new Result("", 200);
            }

            @Override
            public Result get(String address) throws IOException {
                if (address.endsWith("testUserName")) {
                    String body = "{\"lights\":{}}";
                    return new Result(body, 200);
                } else
                    if ((address.endsWith("lights")) || (address.endsWith("sensors"))) {
                        String body = "{}";
                        return new Result(body, 200);
                    } else
                        if (address.endsWith("testUserName/config")) {
                            String body = "{ \"apiversion\": \"1.26.0\"}";
                            return new Result(body, 200);
                        } else {
                            return new Result("", 404);
                        }


            }

            @Override
            public Result post(String address, String body) throws IOException {
                if (address.endsWith("lights")) {
                    String bodyReturn = "{\"success\": {\"/lights\": \"Searching for new devices\"}}";
                    searchHasBeenTriggered.set(true);
                    return new Result(bodyReturn, 200);
                } else {
                    return new Result("", 404);
                }
            }
        };
        installHttpClientMock(hueBridgeHandler, mockedHttpClient);
        ThingStatusInfo online = ThingStatusInfoBuilder.create(ONLINE, NONE).build();
        waitForAssert(() -> {
            assertThat(hueBridge.getStatusInfo(), is(online));
        });
        discoveryService.startScan();
        waitForAssert(() -> {
            assertTrue(searchHasBeenTriggered.get());
        });
    }
}

