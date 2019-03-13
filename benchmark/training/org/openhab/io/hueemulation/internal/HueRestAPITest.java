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


import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import HueStateColorBulb.ColorMode.xy;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.io.hueemulation.internal.dto.HueDataStore;
import org.openhab.io.hueemulation.internal.dto.HueStateColorBulb;
import org.openhab.io.hueemulation.internal.dto.HueStatePlug;
import org.openhab.io.hueemulation.internal.dto.HueUserAuth;


/**
 * Tests for {@link RESTApi}.
 *
 * @author David Graeff - Initial contribution
 */
public class HueRestAPITest {
    private Gson gson;

    private HueDataStore ds;

    private RESTApi restAPI;

    private UserManagement userManagement;

    private ConfigManagement configManagement;

    @Mock
    private EventPublisher eventPublisher;

    @Test
    public void invalidUser() throws IOException {
        PrintWriter out = Mockito.mock(PrintWriter.class);
        int result = restAPI.handleUser(GET, "", out, "testuser", Paths.get(""), Paths.get(""), false);
        Assert.assertEquals(403, result);
    }

    @Test
    public void validUser() throws IOException {
        PrintWriter out = Mockito.mock(PrintWriter.class);
        ds.config.whitelist.put("testuser", new HueUserAuth("testuser"));
        int result = restAPI.handleUser(GET, "", out, "testuser", Paths.get("/"), Paths.get(""), false);
        Assert.assertEquals(200, result);
    }

    @Test
    public void addUser() throws IOException {
        PrintWriter out = Mockito.mock(PrintWriter.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        // GET should fail
        int result = restAPI.handle(GET, "", out, Paths.get("/api"), false);
        Assert.assertEquals(405, result);
        // Post should create a user, except: if linkbutton not enabled
        result = restAPI.handle(POST, "", out, Paths.get("/api"), false);
        Assert.assertEquals(10403, result);
        // Post should create a user
        ds.config.linkbutton = true;
        Mockito.when(req.getMethod()).thenReturn("POST");
        String body = "{'username':'testuser','devicetype':'user-label'}";
        result = restAPI.handle(POST, body, out, Paths.get("/api"), false);
        Assert.assertEquals(result, 200);
        Assert.assertThat(ds.config.whitelist.get("testuser").name, CoreMatchers.is("user-label"));
    }

    @Test
    public void changeSwitchState() throws IOException {
        ds.config.whitelist.put("testuser", new HueUserAuth("testuser"));
        Assert.assertThat(((HueStatePlug) (ds.lights.get(1).state)).on, CoreMatchers.is(false));
        StringWriter out = new StringWriter();
        String body = "{'on':true}";
        int result = restAPI.handle(PUT, body, out, Paths.get("/api/testuser/lights/1/state"), false);
        Assert.assertEquals(200, result);
        Assert.assertThat(out.toString(), CoreMatchers.containsString("success"));
        Assert.assertThat(((HueStatePlug) (ds.lights.get(1).state)).on, CoreMatchers.is(true));
        Mockito.verify(eventPublisher).post(ArgumentMatchers.argThat((Event t) -> {
            assertThat(t.getPayload(), is("{\"type\":\"OnOff\",\"value\":\"ON\"}"));
            return true;
        }));
    }

    @Test
    public void changeGroupItemSwitchState() throws IOException {
        ds.config.whitelist.put("testuser", new HueUserAuth("testuser"));
        Assert.assertThat(((HueStatePlug) (ds.lights.get(10).state)).on, CoreMatchers.is(false));
        StringWriter out = new StringWriter();
        String body = "{'on':true}";
        int result = restAPI.handle(PUT, body, out, Paths.get("/api/testuser/lights/10/state"), false);
        Assert.assertEquals(200, result);
        Assert.assertThat(out.toString(), CoreMatchers.containsString("success"));
        Assert.assertThat(((HueStatePlug) (ds.lights.get(10).state)).on, CoreMatchers.is(true));
        Mockito.verify(eventPublisher).post(ArgumentMatchers.argThat((Event t) -> {
            assertThat(t.getPayload(), is("{\"type\":\"OnOff\",\"value\":\"ON\"}"));
            return true;
        }));
    }

    @Test
    public void changeOnAndBriValues() throws IOException {
        ds.config.whitelist.put("testuser", new HueUserAuth("testuser"));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).on, CoreMatchers.is(false));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).bri, CoreMatchers.is(0));
        String body = "{'on':true,'bri':200}";
        StringWriter out = new StringWriter();
        int result = restAPI.handle(PUT, body, out, Paths.get("/api/testuser/lights/2/state"), false);
        Assert.assertEquals(200, result);
        Assert.assertThat(out.toString(), CoreMatchers.containsString("success"));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).on, CoreMatchers.is(true));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).bri, CoreMatchers.is(200));
    }

    @Test
    public void switchOnWithXY() throws IOException {
        ds.config.whitelist.put("testuser", new HueUserAuth("testuser"));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).on, CoreMatchers.is(false));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).bri, CoreMatchers.is(0));
        String body = "{'on':true,'bri':200,'xy':[0.5119,0.4147]}";
        StringWriter out = new StringWriter();
        int result = restAPI.handle(PUT, body, out, Paths.get("/api/testuser/lights/2/state"), false);
        Assert.assertEquals(200, result);
        Assert.assertThat(out.toString(), CoreMatchers.containsString("success"));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).on, CoreMatchers.is(true));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).bri, CoreMatchers.is(200));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).xy[0], CoreMatchers.is(0.5119));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).xy[1], CoreMatchers.is(0.4147));
        Assert.assertThat(((HueStateColorBulb) (ds.lights.get(2).state)).colormode, CoreMatchers.is(xy));
        Assert.assertThat(toHSBType().getHue().intValue(), CoreMatchers.is(((int) (27.47722590981918))));
        Assert.assertThat(toHSBType().getSaturation().intValue(), CoreMatchers.is(88));
        Assert.assertThat(toHSBType().getBrightness().intValue(), CoreMatchers.is(78));
    }
}

