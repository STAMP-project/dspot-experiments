/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.websocket;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Endpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WebsocketComponentTest {
    private static final String PATH_ONE = "foo";

    private static final String PATH_TWO = "bar";

    private static final String PATH_SPEC_ONE = ("/" + (WebsocketComponentTest.PATH_ONE)) + "/*";

    @Mock
    private WebsocketConsumer consumer;

    @Mock
    private NodeSynchronization sync;

    @Mock
    private WebsocketComponentServlet servlet;

    @Mock
    private Map<String, WebsocketComponentServlet> servlets;

    @Mock
    private ServletContextHandler handler;

    private WebsocketComponent component;

    private WebsocketProducer producer;

    private Server server;

    @Test
    public void testCreateContext() throws Exception {
        ServletContextHandler handler = component.createContext(server, server.getConnectors()[0], null);
        Assert.assertNotNull(handler);
    }

    @Test
    public void testCreateServerWithoutStaticContent() throws Exception {
        ServletContextHandler handler = component.createContext(server, server.getConnectors()[0], null);
        Assert.assertEquals(1, server.getConnectors().length);
        Assert.assertEquals("localhost", getHost());
        Assert.assertEquals(1988, getPort());
        Assert.assertFalse(server.getConnectors()[0].isStarted());
        Assert.assertEquals(handler, server.getHandler());
        Assert.assertEquals(1, server.getHandlers().length);
        Assert.assertEquals(handler, server.getHandlers()[0]);
        Assert.assertEquals("/", handler.getContextPath());
        Assert.assertNull(handler.getSessionHandler());
        Assert.assertNull(handler.getResourceBase());
        Assert.assertNull(handler.getServletHandler().getMappedServlet("/"));
    }

    @Test
    public void testCreateServerWithStaticContent() throws Exception {
        ServletContextHandler handler = component.createContext(server, server.getConnectors()[0], null);
        Server server = component.createStaticResourcesServer(handler, "localhost", 1988, "classpath:public");
        Assert.assertEquals(1, server.getConnectors().length);
        Assert.assertEquals("localhost", getHost());
        Assert.assertEquals(1988, getPort());
        Assert.assertFalse(server.getConnectors()[0].isStarted());
        Assert.assertEquals(handler, server.getHandler());
        Assert.assertEquals(1, server.getHandlers().length);
        Assert.assertEquals(handler, server.getHandlers()[0]);
        Assert.assertEquals("/", handler.getContextPath());
        Assert.assertNotNull(handler.getSessionHandler());
        Assert.assertNotNull(handler.getResourceBase());
        Assert.assertTrue(handler.getResourceBase().startsWith(JettyClassPathResource.class.getName()));
        Assert.assertNotNull(handler.getServletHandler().getMappedServlet("/"));
    }

    @Test
    public void testCreateEndpoint() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        Endpoint e1 = component.createEndpoint("websocket://foo", "foo", parameters);
        Endpoint e2 = component.createEndpoint("websocket://foo", "foo", parameters);
        Endpoint e3 = component.createEndpoint("websocket://bar", "bar", parameters);
        Assert.assertNotNull(e1);
        Assert.assertNotNull(e1);
        Assert.assertNotNull(e1);
        Assert.assertEquals(e1, e2);
        Assert.assertNotSame(e1, e3);
        Assert.assertNotSame(e2, e3);
    }

    @Test
    public void testCreateServlet() throws Exception {
        component.createServlet(sync, WebsocketComponentTest.PATH_SPEC_ONE, servlets, handler);
        InOrder inOrder = Mockito.inOrder(servlet, consumer, sync, servlets, handler);
        ArgumentCaptor<WebsocketComponentServlet> servletCaptor = ArgumentCaptor.forClass(WebsocketComponentServlet.class);
        inOrder.verify(servlets, Mockito.times(1)).put(ArgumentMatchers.eq(WebsocketComponentTest.PATH_SPEC_ONE), servletCaptor.capture());
        ArgumentCaptor<ServletHolder> holderCaptor = ArgumentCaptor.forClass(ServletHolder.class);
        inOrder.verify(handler, Mockito.times(1)).addServlet(holderCaptor.capture(), ArgumentMatchers.eq(WebsocketComponentTest.PATH_SPEC_ONE));
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(servletCaptor.getValue(), holderCaptor.getValue().getServlet());
    }

    @Test
    public void testAddServletProducersOnly() throws Exception {
        WebsocketComponentServlet s1 = component.addServlet(sync, producer, WebsocketComponentTest.PATH_ONE);
        WebsocketComponentServlet s2 = component.addServlet(sync, producer, WebsocketComponentTest.PATH_TWO);
        Assert.assertNotNull(s1);
        Assert.assertNotNull(s2);
        Assert.assertNotSame(s1, s2);
        Assert.assertNull(s1.getConsumer());
        Assert.assertNull(s2.getConsumer());
    }

    @Test
    public void testAddServletConsumersOnly() throws Exception {
        WebsocketComponentServlet s1 = component.addServlet(sync, consumer, WebsocketComponentTest.PATH_ONE);
        WebsocketComponentServlet s2 = component.addServlet(sync, consumer, WebsocketComponentTest.PATH_TWO);
        Assert.assertNotNull(s1);
        Assert.assertNotNull(s2);
        Assert.assertNotSame(s1, s2);
        Assert.assertEquals(consumer, s1.getConsumer());
        Assert.assertEquals(consumer, s2.getConsumer());
    }

    @Test
    public void testAddServletProducerAndConsumer() throws Exception {
        WebsocketComponentServlet s1 = component.addServlet(sync, producer, WebsocketComponentTest.PATH_ONE);
        WebsocketComponentServlet s2 = component.addServlet(sync, consumer, WebsocketComponentTest.PATH_ONE);
        Assert.assertNotNull(s1);
        Assert.assertNotNull(s2);
        Assert.assertEquals(s1, s2);
        Assert.assertEquals(consumer, s1.getConsumer());
    }

    @Test
    public void testAddServletConsumerAndProducer() throws Exception {
        WebsocketComponentServlet s1 = component.addServlet(sync, consumer, WebsocketComponentTest.PATH_ONE);
        WebsocketComponentServlet s2 = component.addServlet(sync, producer, WebsocketComponentTest.PATH_ONE);
        Assert.assertNotNull(s1);
        Assert.assertNotNull(s2);
        Assert.assertEquals(s1, s2);
        Assert.assertEquals(consumer, s1.getConsumer());
    }
}

