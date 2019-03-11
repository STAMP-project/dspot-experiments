/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.discovery;


import HttpMethod.POST;
import NodeType.PEON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.druid.curator.discovery.ServerDiscoverySelector;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.http.client.HttpClient;
import org.apache.druid.java.util.http.client.Request;
import org.apache.druid.server.DruidNode;
import org.apache.druid.server.initialization.BaseJettyTest;
import org.apache.druid.server.initialization.jetty.JettyServerInitializer;
import org.easymock.EasyMock;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static NodeType.PEON;


/**
 *
 */
public class DruidLeaderClientTest extends BaseJettyTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DiscoveryDruidNode discoveryDruidNode;

    private HttpClient httpClient;

    @Test
    public void testSimple() throws Exception {
        DruidNodeDiscovery druidNodeDiscovery = EasyMock.createMock(DruidNodeDiscovery.class);
        EasyMock.expect(druidNodeDiscovery.getAllNodes()).andReturn(ImmutableList.of(discoveryDruidNode));
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(PEON)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscovery, druidNodeDiscoveryProvider);
        DruidLeaderClient druidLeaderClient = new DruidLeaderClient(httpClient, druidNodeDiscoveryProvider, PEON, "/simple/leader", EasyMock.createNiceMock(ServerDiscoverySelector.class));
        druidLeaderClient.start();
        Request request = druidLeaderClient.makeRequest(POST, "/simple/direct");
        request.setContent("hello".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals("hello", druidLeaderClient.go(request).getContent());
    }

    @Test
    public void testNoLeaderFound() throws Exception {
        DruidNodeDiscovery druidNodeDiscovery = EasyMock.createMock(DruidNodeDiscovery.class);
        EasyMock.expect(druidNodeDiscovery.getAllNodes()).andReturn(ImmutableList.of());
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(PEON)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscovery, druidNodeDiscoveryProvider);
        DruidLeaderClient druidLeaderClient = new DruidLeaderClient(httpClient, druidNodeDiscoveryProvider, PEON, "/simple/leader", EasyMock.createNiceMock(ServerDiscoverySelector.class));
        druidLeaderClient.start();
        expectedException.expect(IOException.class);
        expectedException.expectMessage("No known server");
        druidLeaderClient.makeRequest(POST, "/simple/direct");
    }

    @Test
    public void testRedirection() throws Exception {
        DruidNodeDiscovery druidNodeDiscovery = EasyMock.createMock(DruidNodeDiscovery.class);
        EasyMock.expect(druidNodeDiscovery.getAllNodes()).andReturn(ImmutableList.of(discoveryDruidNode));
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(PEON)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscovery, druidNodeDiscoveryProvider);
        DruidLeaderClient druidLeaderClient = new DruidLeaderClient(httpClient, druidNodeDiscoveryProvider, PEON, "/simple/leader", EasyMock.createNiceMock(ServerDiscoverySelector.class));
        druidLeaderClient.start();
        Request request = druidLeaderClient.makeRequest(POST, "/simple/redirect");
        request.setContent("hello".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals("hello", druidLeaderClient.go(request).getContent());
    }

    @Test
    public void testServerFailureAndRedirect() throws Exception {
        ServerDiscoverySelector serverDiscoverySelector = EasyMock.createMock(ServerDiscoverySelector.class);
        EasyMock.expect(serverDiscoverySelector.pick()).andReturn(null).anyTimes();
        DruidNodeDiscovery druidNodeDiscovery = EasyMock.createMock(DruidNodeDiscovery.class);
        DiscoveryDruidNode dummyNode = new DiscoveryDruidNode(new DruidNode("test", "dummyhost", false, 64231, null, true, false), PEON, ImmutableMap.of());
        EasyMock.expect(druidNodeDiscovery.getAllNodes()).andReturn(ImmutableList.of(dummyNode));
        EasyMock.expect(druidNodeDiscovery.getAllNodes()).andReturn(ImmutableList.of(discoveryDruidNode));
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(PEON)).andReturn(druidNodeDiscovery).anyTimes();
        EasyMock.replay(serverDiscoverySelector, druidNodeDiscovery, druidNodeDiscoveryProvider);
        DruidLeaderClient druidLeaderClient = new DruidLeaderClient(httpClient, druidNodeDiscoveryProvider, PEON, "/simple/leader", serverDiscoverySelector);
        druidLeaderClient.start();
        Request request = druidLeaderClient.makeRequest(POST, "/simple/redirect");
        request.setContent("hello".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals("hello", druidLeaderClient.go(request).getContent());
    }

    @Test
    public void testFindCurrentLeader() {
        DruidNodeDiscovery druidNodeDiscovery = EasyMock.createMock(DruidNodeDiscovery.class);
        EasyMock.expect(druidNodeDiscovery.getAllNodes()).andReturn(ImmutableList.of(discoveryDruidNode));
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(PEON)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscovery, druidNodeDiscoveryProvider);
        DruidLeaderClient druidLeaderClient = new DruidLeaderClient(httpClient, druidNodeDiscoveryProvider, PEON, "/simple/leader", EasyMock.createNiceMock(ServerDiscoverySelector.class));
        druidLeaderClient.start();
        Assert.assertEquals("http://localhost:1234/", druidLeaderClient.findCurrentLeader());
    }

    private static class TestJettyServerInitializer implements JettyServerInitializer {
        @Override
        public void initialize(Server server, Injector injector) {
            final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
            root.addServlet(new org.eclipse.jetty.servlet.ServletHolder(new DefaultServlet()), "/*");
            root.addFilter(GuiceFilter.class, "/*", null);
            final HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(new Handler[]{ root });
            server.setHandler(handlerList);
        }
    }

    @Path("/simple")
    public static class SimpleResource {
        private final int port;

        @Inject
        public SimpleResource(@Named("port")
        int port) {
            this.port = port;
        }

        @javax.ws.rs.POST
        @Path("/direct")
        @Produces(MediaType.APPLICATION_JSON)
        public Response direct(String input) {
            if ("hello".equals(input)) {
                return Response.ok("hello").build();
            } else {
                return Response.serverError().build();
            }
        }

        @javax.ws.rs.POST
        @Path("/redirect")
        @Produces(MediaType.APPLICATION_JSON)
        public Response redirecting() throws Exception {
            return Response.temporaryRedirect(new URI(StringUtils.format("http://localhost:%s/simple/direct", port))).build();
        }

        @GET
        @Path("/leader")
        @Produces(MediaType.APPLICATION_JSON)
        public Response leader() {
            return Response.ok("http://localhost:1234/").build();
        }
    }
}

