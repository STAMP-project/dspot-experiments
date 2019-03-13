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
package org.openhab.binding.tradfri.discovery;


import DiscoveryResultFlag.NEW;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.openhab.binding.tradfri.internal.discovery.TradfriDiscoveryService;
import org.openhab.binding.tradfri.internal.handler.TradfriGatewayHandler;


/**
 * Tests for {@link TradfriDiscoveryService}.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Christoph Weitkamp - Added support for remote controller and motion sensor devices (read-only battery level)
 */
public class TradfriDiscoveryServiceTest {
    private static final ThingUID GATEWAY_THING_UID = new ThingUID("tradfri:gateway:1");

    @Mock
    private TradfriGatewayHandler handler;

    private DiscoveryListener listener;

    private DiscoveryResult discoveryResult;

    private TradfriDiscoveryService discovery;

    @Test
    public void correctSupportedTypes() {
        Assert.assertThat(discovery.getSupportedThingTypes().size(), CoreMatchers.is(6));
        Assert.assertTrue(discovery.getSupportedThingTypes().contains(THING_TYPE_DIMMABLE_LIGHT));
        Assert.assertTrue(discovery.getSupportedThingTypes().contains(THING_TYPE_COLOR_TEMP_LIGHT));
        Assert.assertTrue(discovery.getSupportedThingTypes().contains(THING_TYPE_COLOR_LIGHT));
        Assert.assertTrue(discovery.getSupportedThingTypes().contains(THING_TYPE_DIMMER));
        Assert.assertTrue(discovery.getSupportedThingTypes().contains(THING_TYPE_MOTION_SENSOR));
        Assert.assertTrue(discovery.getSupportedThingTypes().contains(THING_TYPE_REMOTE_CONTROL));
    }

    @Test
    public void validDiscoveryResultWhiteLightW() {
        String json = "{\"9001\":\"TRADFRI bulb E27 W opal 1000lm\",\"9002\":1492856270,\"9020\":1507194357,\"9003\":65537,\"3311\":[{\"5850\":1,\"5851\":254,\"9003\":0}],\"9054\":0,\"5750\":2,\"9019\":1,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI bulb E27 W opal 1000lm\",\"2\":\"\",\"3\":\"1.2.214\",\"6\":1}}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65537", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0100:1:65537")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_DIMMABLE_LIGHT));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65537));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }

    @Test
    public void validDiscoveryResultWhiteLightWS() {
        String json = "{\"9001\":\"TRADFRI bulb E27 WS opal 980lm\",\"9002\":1492955148,\"9020\":1507200447,\"9003\":65537,\"3311\":[{\"5710\":26909,\"5850\":1,\"5851\":203,\"5707\":0,\"5708\":0,\"5709\":30140,\"5711\":370,\"5706\":\"f1e0b5\",\"9003\":0}],\"9054\":0,\"5750\":2,\"9019\":1,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI bulb E27 WS opal 980lm\",\"2\":\"\",\"3\":\"1.2.217\",\"6\":1}}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65537", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0220:1:65537")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_COLOR_TEMP_LIGHT));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65537));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }

    @Test
    public void validDiscoveryResultWhiteLightWSWithIncompleteJson() {
        // We do not always receive a COLOR = "5706" attribute, even the light supports it - but the gateway does not
        // seem to have this information, if the bulb is unreachable.
        String json = "{\"9001\":\"TRADFRI bulb E27 WS opal 980lm\",\"9002\":1492955148,\"9020\":1506968670,\"9003\":65537,\"3311\":[{\"9003\":0}],\"9054\":0,\"5750\":2,\"9019\":0,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI bulb E27 WS opal 980lm\",\"2\":\"\",\"3\":\"1.2.217\",\"6\":1}}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65537", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0220:1:65537")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_COLOR_TEMP_LIGHT));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65537));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }

    @Test
    public void validDiscoveryResultColorLightCWS() {
        String json = "{\"9001\":\"TRADFRI bulb E27 CWS opal 600lm\",\"9002\":1505151864,\"9020\":1505433527,\"9003\":65550,\"9019\":1,\"9054\":0,\"5750\":2,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI bulb E27 CWS opal 600lm\",\"2\":\"\",\"3\":\"1.3.002\",\"6\":1},\"3311\":[{\"5850\":1,\"5708\":0,\"5851\":254,\"5707\":0,\"5709\":33137,\"5710\":27211,\"5711\":0,\"5706\":\"efd275\",\"9003\":0}]}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65550", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0210:1:65550")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_COLOR_LIGHT));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65550));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }

    @Test
    public void validDiscoveryResultRemoteControl() {
        String json = "{\"9001\":\"TRADFRI remote control\",\"9002\":1492843083,\"9020\":1506977986,\"9003\":65536,\"9054\":0,\"5750\":0,\"9019\":1,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI remote control\",\"2\":\"\",\"3\":\"1.2.214\",\"6\":3,\"9\":47},\"15009\":[{\"9003\":0}]}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65536", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0830:1:65536")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_REMOTE_CONTROL));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65536));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }

    @Test
    public void validDiscoveryResultWirelessDimmer() {
        String json = "{\"9001\":\"TRADFRI wireless dimmer\",\"9002\":1492843083,\"9020\":1506977986,\"9003\":65536,\"9054\":0,\"5750\":0,\"9019\":1,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI wireless dimmer\",\"2\":\"\",\"3\":\"1.2.214\",\"6\":3,\"9\":47},\"15009\":[{\"9003\":0}]}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65536", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0820:1:65536")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_DIMMER));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65536));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }

    @Test
    public void validDiscoveryResultMotionSensor() {
        String json = "{\"9001\":\"TRADFRI motion sensor\",\"9002\":1492955083,\"9020\":1507120083,\"9003\":65538,\"9054\":0,\"5750\":4,\"9019\":1,\"3\":{\"0\":\"IKEA of Sweden\",\"1\":\"TRADFRI motion sensor\",\"2\":\"\",\"3\":\"1.2.214\",\"6\":3,\"9\":60},\"3300\":[{\"9003\":0}]}";
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        discovery.onUpdate("65538", data);
        Assert.assertNotNull(discoveryResult);
        Assert.assertThat(discoveryResult.getFlag(), CoreMatchers.is(NEW));
        Assert.assertThat(discoveryResult.getThingUID(), CoreMatchers.is(new ThingUID("tradfri:0107:1:65538")));
        Assert.assertThat(discoveryResult.getThingTypeUID(), CoreMatchers.is(THING_TYPE_MOTION_SENSOR));
        Assert.assertThat(discoveryResult.getBridgeUID(), CoreMatchers.is(TradfriDiscoveryServiceTest.GATEWAY_THING_UID));
        Assert.assertThat(discoveryResult.getProperties().get(CONFIG_ID), CoreMatchers.is(65538));
        Assert.assertThat(discoveryResult.getRepresentationProperty(), CoreMatchers.is(CONFIG_ID));
    }
}

