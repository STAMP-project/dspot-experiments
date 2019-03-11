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
package org.openhab.io.hueemulation.internal;


import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.eclipse.smarthome.test.storage.VolatileStorageService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.io.hueemulation.internal.dto.HueUnauthorizedConfig;
import org.openhab.io.hueemulation.internal.dto.HueUserAuth;
import org.osgi.service.cm.ConfigurationAdmin;

import static DeviceType.ColorType;
import static DeviceType.SwitchType;
import static DeviceType.WhiteTemperatureType;


/**
 * Integration tests for {@link HueEmulationService}.
 *
 * @author David Graeff - Initial contribution
 */
public class HueEmulationServiceOSGiTest extends JavaOSGiTest {
    private HueEmulationService hueService;

    VolatileStorageService volatileStorageService = new VolatileStorageService();

    ItemRegistry itemRegistry;

    @Mock
    ConfigurationAdmin configurationAdmin;

    @Mock
    EventPublisher eventPublisher;

    @Mock
    Item item;

    String host;

    @Test
    public void UnauthorizedAccessTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // upnp response
        HttpURLConnection c = ((HttpURLConnection) (new URL(((host) + "/description.xml")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        String body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString(hueService.ds.config.uuid));
        // Unauthorized config access
        c = ((HttpURLConnection) (new URL(((host) + "/api/config")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        HueUnauthorizedConfig config = new Gson().fromJson(body, HueUnauthorizedConfig.class);
        Assert.assertThat(config.bridgeid, CoreMatchers.is(hueService.ds.config.bridgeid));
        Assert.assertThat(config.name, CoreMatchers.is(hueService.ds.config.name));
        // Invalid user name
        c = ((HttpURLConnection) (new URL(((host) + "/api/invalid/lights")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(403));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("error"));
        // Add user name (no link button)
        body = "{'username':'testuser','devicetype':'label'}";
        c = ((HttpURLConnection) (new URL(((host) + "/api")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(403));
        // Add user name (link button)
        hueService.ds.config.linkbutton = true;
        c = ((HttpURLConnection) (new URL(((host) + "/api")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("success"));
        Assert.assertThat(hueService.ds.config.whitelist.get("testuser").name, CoreMatchers.is("label"));
        hueService.ds.config.whitelist.clear();
        // Add user name without proposing one (the bridge generates one)
        body = "{'devicetype':'label'}";
        c = ((HttpURLConnection) (new URL(((host) + "/api")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("success"));
        Assert.assertThat(body, CoreMatchers.containsString(hueService.ds.config.whitelist.keySet().iterator().next()));
    }

    @Test
    public void LightsTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpURLConnection c;
        String body;
        hueService.ds.config.whitelist.put("testuser", new HueUserAuth("testUserLabel"));
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("{}"));
        hueService.ds.lights.put(1, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "switch", SwitchType));
        hueService.ds.lights.put(2, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "color", ColorType));
        hueService.ds.lights.put(3, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "white", WhiteTemperatureType));
        // Full access test
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("switch"));
        Assert.assertThat(body, CoreMatchers.containsString("color"));
        Assert.assertThat(body, CoreMatchers.containsString("white"));
        // Single light access test
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights/2")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("color"));
    }

    @Test
    public void DebugTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpURLConnection c;
        String body;
        hueService.ds.config.whitelist.put("testuser", new HueUserAuth("testUserLabel"));
        hueService.ds.lights.put(2, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "color", ColorType));
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights?debug=true")).openConnection()));
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("Exposed lights"));
    }

    @Test
    public void LightGroupItemSwitchTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpURLConnection c;
        String body;
        GroupItem gitem = new GroupItem("group", item);
        hueService.ds.config.whitelist.put("testuser", new HueUserAuth("testUserLabel"));
        hueService.ds.lights.put(7, new org.openhab.io.hueemulation.internal.dto.HueDevice(gitem, "switch", SwitchType));
        body = "{'on':true}";
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights/7/state")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("PUT");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("success"));
        Assert.assertThat(body, CoreMatchers.containsString("on"));
        Mockito.verify(eventPublisher).post(ArgumentMatchers.argThat(( ce) -> assertOnValue(((ItemCommandEvent) (ce)), true)));
    }

    @Test
    public void LightHueTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpURLConnection c;
        String body;
        hueService.ds.config.whitelist.put("testuser", new HueUserAuth("testUserLabel"));
        hueService.ds.lights.put(2, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "color", ColorType));
        body = "{'hue':1000}";
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights/2/state")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("PUT");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("success"));
        Assert.assertThat(body, CoreMatchers.containsString("hue"));
        Mockito.verify(eventPublisher).post(ArgumentMatchers.argThat(( ce) -> assertHueValue(((ItemCommandEvent) (ce)), 1000)));
    }

    @Test
    public void LightSaturationTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpURLConnection c;
        String body;
        hueService.ds.config.whitelist.put("testuser", new HueUserAuth("testUserLabel"));
        hueService.ds.lights.put(2, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "color", ColorType));
        body = "{'sat':50}";
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights/2/state")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("PUT");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("success"));
        Assert.assertThat(body, CoreMatchers.containsString("sat"));
        Mockito.verify(eventPublisher).post(ArgumentMatchers.argThat(( ce) -> assertSatValue(((ItemCommandEvent) (ce)), 50)));
    }

    /**
     * Amazon echos are setting ct only, if commanded to turn a light white.
     */
    @Test
    public void LightToWhiteTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpURLConnection c;
        String body;
        // We start with a coloured state
        Mockito.when(item.getState()).thenReturn(new HSBType("100,100,100"));
        hueService.ds.config.whitelist.put("testuser", new HueUserAuth("testUserLabel"));
        hueService.ds.lights.put(2, new org.openhab.io.hueemulation.internal.dto.HueDevice(item, "color", ColorType));
        body = "{'ct':500}";
        c = ((HttpURLConnection) (new URL(((host) + "/api/testuser/lights/2/state")).openConnection()));
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestMethod("PUT");
        c.setDoOutput(true);
        c.getOutputStream().write(body.getBytes(), 0, body.getBytes().length);
        Assert.assertThat(c.getResponseCode(), CoreMatchers.is(200));
        body = read(c);
        Assert.assertThat(body, CoreMatchers.containsString("success"));
        Assert.assertThat(body, CoreMatchers.containsString("sat"));
        Assert.assertThat(body, CoreMatchers.containsString("ct"));
        // Saturation is expected to be 0 -> white light
        Mockito.verify(eventPublisher).post(ArgumentMatchers.argThat(( ce) -> assertSatValue(((ItemCommandEvent) (ce)), 0)));
    }
}

