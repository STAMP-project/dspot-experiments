/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent.statusapi;


import NanoHTTPD.IHTTPSession;
import NanoHTTPD.Method.GET;
import NanoHTTPD.Method.POST;
import NanoHTTPD.Response;
import NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED;
import NanoHTTPD.Response.Status.NOT_FOUND;
import NanoHTTPD.Response.Status.OK;
import com.thoughtworks.go.util.SystemEnvironment;
import fi.iki.elonen.NanoHTTPD;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentStatusHttpdTest {
    @Mock
    private AgentHealthHolder agentHealthHolder;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private IHTTPSession session;

    private AgentStatusHttpd agentStatusHttpd;

    @Test
    public void shouldReturnMethodNotAllowedOnNonGetNonHeadRequests() throws Exception {
        Mockito.when(session.getMethod()).thenReturn(POST);
        NanoHTTPD.Response response = this.agentStatusHttpd.serve(session);
        Assert.assertThat(response.getStatus(), Matchers.is(METHOD_NOT_ALLOWED));
        Assert.assertThat(response.getMimeType(), Matchers.is("text/plain; charset=utf-8"));
        Assert.assertThat(IOUtils.toString(response.getData(), StandardCharsets.UTF_8), Matchers.is("This method is not allowed. Please use GET or HEAD."));
    }

    @Test
    public void shouldReturnNotFoundForBadUrl() throws Exception {
        Mockito.when(session.getMethod()).thenReturn(GET);
        Mockito.when(session.getUri()).thenReturn("/foo");
        NanoHTTPD.Response response = this.agentStatusHttpd.serve(session);
        Assert.assertThat(response.getStatus(), Matchers.is(NOT_FOUND));
        Assert.assertThat(response.getMimeType(), Matchers.is("text/plain; charset=utf-8"));
        Assert.assertThat(IOUtils.toString(response.getData(), StandardCharsets.UTF_8), Matchers.is("The page you requested was not found"));
    }

    @Test
    public void shouldRouteToIsConnectedToServerHandler() throws Exception {
        Mockito.when(session.getMethod()).thenReturn(GET);
        Mockito.when(session.getUri()).thenReturn("/health/latest/isConnectedToServer");
        Mockito.when(agentHealthHolder.hasLostContact()).thenReturn(false);
        NanoHTTPD.Response response = this.agentStatusHttpd.serve(session);
        Assert.assertThat(response.getStatus(), Matchers.is(OK));
        Assert.assertThat(response.getMimeType(), Matchers.is("text/plain; charset=utf-8"));
        Assert.assertThat(IOUtils.toString(response.getData(), StandardCharsets.UTF_8), Matchers.is("OK!"));
    }

    @Test
    public void shouldRouteToIsConnectedToServerV1Handler() throws Exception {
        Mockito.when(session.getMethod()).thenReturn(GET);
        Mockito.when(session.getUri()).thenReturn("/health/v1/isConnectedToServer");
        Mockito.when(agentHealthHolder.hasLostContact()).thenReturn(false);
        NanoHTTPD.Response response = this.agentStatusHttpd.serve(session);
        Assert.assertThat(response.getStatus(), Matchers.is(OK));
        Assert.assertThat(response.getMimeType(), Matchers.is("text/plain; charset=utf-8"));
        Assert.assertThat(IOUtils.toString(response.getData(), StandardCharsets.UTF_8), Matchers.is("OK!"));
    }

    @Test
    public void shouldNotInitializeServerIfSettingIsTurnedOff() throws Exception {
        Mockito.when(systemEnvironment.getAgentStatusEnabled()).thenReturn(true);
        AgentStatusHttpd spy = Mockito.spy(agentStatusHttpd);
        Mockito.doThrow(new RuntimeException("This is not expected to be invoked")).when(spy).start();
        spy.init();
    }

    @Test
    public void shouldInitializeServerIfSettingIsTurnedOn() throws Exception {
        Mockito.when(systemEnvironment.getAgentStatusEnabled()).thenReturn(true);
        AgentStatusHttpd spy = Mockito.spy(agentStatusHttpd);
        spy.init();
        Mockito.verify(spy).start();
    }

    @Test
    public void initShouldNotBlowUpIfServerDoesNotStart() throws Exception {
        Mockito.when(systemEnvironment.getAgentStatusEnabled()).thenReturn(true);
        AgentStatusHttpd spy = Mockito.spy(agentStatusHttpd);
        Mockito.doThrow(new RuntimeException("Server had a problem starting up!")).when(spy).start();
        try {
            spy.init();
        } catch (Exception e) {
            Assert.fail("Did not expect exception!");
        }
    }
}

