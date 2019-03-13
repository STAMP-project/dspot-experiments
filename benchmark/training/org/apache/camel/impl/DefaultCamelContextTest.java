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
package org.apache.camel.impl;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.Route;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.BeanComponent;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.log.LogComponent;
import org.apache.camel.spi.UuidGenerator;
import org.apache.camel.support.CamelContextHelper;
import org.apache.camel.support.service.ServiceSupport;
import org.junit.Assert;
import org.junit.Test;


public class DefaultCamelContextTest extends TestSupport {
    @Test
    public void testAutoCreateComponentsOn() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Component component = ctx.getComponent("bean");
        Assert.assertNotNull(component);
        Assert.assertEquals(component.getClass(), BeanComponent.class);
    }

    @Test
    public void testAutoCreateComponentsOff() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.setAutoCreateComponents(false);
        Component component = ctx.getComponent("bean");
        Assert.assertNull(component);
    }

    @Test
    public void testAutoStartComponentsOff() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.start();
        BeanComponent component = ((BeanComponent) (ctx.getComponent("bean", true, false)));
        // should be stopped
        Assert.assertTrue(component.getStatus().isStopped());
    }

    @Test
    public void testAutoStartComponentsOn() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext();
        ctx.disableJMX();
        ctx.start();
        BeanComponent component = ((BeanComponent) (ctx.getComponent("bean", true, true)));
        // should be started
        Assert.assertTrue(component.getStatus().isStarted());
    }

    @Test
    public void testCreateDefaultUuidGenerator() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.init();
        UuidGenerator uuidGenerator = ctx.getUuidGenerator();
        Assert.assertNotNull(uuidGenerator);
        Assert.assertEquals(uuidGenerator.getClass(), DefaultUuidGenerator.class);
    }

    @Test
    public void testGetComponents() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Component component = ctx.getComponent("bean");
        Assert.assertNotNull(component);
        List<String> list = ctx.getComponentNames();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("bean", list.get(0));
    }

    @Test
    public void testGetEndpoint() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Endpoint endpoint = ctx.getEndpoint("log:foo");
        Assert.assertNotNull(endpoint);
        try {
            ctx.getEndpoint(null);
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetEndpointNoScheme() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext();
        ctx.disableJMX();
        Endpoint endpoint = ctx.getEndpoint("log");
        Assert.assertNotNull(endpoint);
    }

    @Test
    public void testGetEndPointByTypeUnknown() {
        DefaultCamelContext camelContext = new DefaultCamelContext();
        try {
            camelContext.getEndpoint("xxx", Endpoint.class);
            Assert.fail();
        } catch (NoSuchEndpointException e) {
            Assert.assertEquals("No endpoint could be found for: xxx, please check your classpath contains the needed Camel component jar.", e.getMessage());
        }
    }

    @Test
    public void testRemoveEndpoint() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.getEndpoint("log:foo");
        ctx.getEndpoint("log:bar");
        ctx.start();
        Collection<Endpoint> list = ctx.removeEndpoints("log:foo");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("log://foo", list.iterator().next().getEndpointUri());
        ctx.getEndpoint("log:baz");
        ctx.getEndpoint("seda:cool");
        list = ctx.removeEndpoints("log:*");
        Assert.assertEquals(2, list.size());
        Iterator<Endpoint> it = list.iterator();
        String s1 = it.next().getEndpointUri();
        String s2 = it.next().getEndpointUri();
        Assert.assertTrue((("log://bar".equals(s1)) || ("log://bar".equals(s2))));
        Assert.assertTrue((("log://baz".equals(s1)) || ("log://baz".equals(s2))));
        Assert.assertTrue((("log://baz".equals(s1)) || ("log://baz".equals(s2))));
        Assert.assertTrue((("log://baz".equals(s1)) || ("log://baz".equals(s2))));
        Assert.assertEquals(1, ctx.getEndpoints().size());
    }

    @Test
    public void testGetEndpointNotFound() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        try {
            ctx.getEndpoint("xxx:foo");
            Assert.fail("Should have thrown a ResolveEndpointFailedException");
        } catch (ResolveEndpointFailedException e) {
            Assert.assertTrue(e.getMessage().contains("No component found with scheme: xxx"));
        }
    }

    @Test
    public void testGetEndpointUnknownComponentNoScheme() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        try {
            CamelContextHelper.getMandatoryEndpoint(ctx, "unknownname");
            Assert.fail("Should have thrown a NoSuchEndpointException");
        } catch (NoSuchEndpointException e) {
            // expected
        }
    }

    @Test
    public void testRestartCamelContext() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:endpointA").to("mock:endpointB");
            }
        });
        ctx.start();
        Assert.assertEquals("Should have one RouteService", 1, ctx.getRouteServices().size());
        String routesString = ctx.getRoutes().toString();
        ctx.stop();
        Assert.assertEquals("The RouteService should NOT be removed even when we stop", 1, ctx.getRouteServices().size());
        ctx.start();
        Assert.assertEquals("Should have one RouteService", 1, ctx.getRouteServices().size());
        Assert.assertEquals("The Routes should be same", routesString, ctx.getRoutes().toString());
        ctx.stop();
        Assert.assertEquals("The RouteService should NOT be removed even when we stop", 1, ctx.getRouteServices().size());
    }

    @Test
    public void testName() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.init();
        Assert.assertNotNull("Should have a default name", ctx.getName());
        ctx.setName("foo");
        Assert.assertEquals("foo", ctx.getName());
        Assert.assertNotNull(ctx.toString());
        Assert.assertTrue(ctx.isAutoStartup());
    }

    @Test
    public void testVersion() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Assert.assertNotNull("Should have a version", ctx.getVersion());
    }

    @Test
    public void testHasComponent() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Assert.assertNull(ctx.hasComponent("log"));
        ctx.addComponent("log", new LogComponent());
        Assert.assertNotNull(ctx.hasComponent("log"));
    }

    @Test
    public void testGetComponent() {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.addComponent("log", new LogComponent());
        LogComponent log = ctx.getComponent("log", LogComponent.class);
        Assert.assertNotNull(log);
        try {
            ctx.addComponent("direct", new DirectComponent());
            ctx.getComponent("log", DirectComponent.class);
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testHasEndpoint() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        ctx.getEndpoint("mock://foo");
        Assert.assertNotNull(ctx.hasEndpoint("mock://foo"));
        Assert.assertNull(ctx.hasEndpoint("mock://bar"));
        Map<String, Endpoint> map = ctx.getEndpointMap();
        Assert.assertEquals(1, map.size());
        try {
            ctx.hasEndpoint(null);
            Assert.fail("Should have thrown exception");
        } catch (ResolveEndpointFailedException e) {
            // expected
        }
    }

    @Test
    public void testGetRouteById() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        // should not throw NPE (CAMEL-3198)
        Route route = ctx.getRoute("coolRoute");
        Assert.assertNull(route);
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").routeId("coolRoute").to("mock:result");
            }
        });
        ctx.start();
        route = ctx.getRoute("coolRoute");
        Assert.assertNotNull(route);
        Assert.assertEquals("coolRoute", route.getId());
        Assert.assertEquals("direct://start", route.getConsumer().getEndpoint().getEndpointUri());
        Assert.assertNull(ctx.getRoute("unknown"));
        ctx.stop();
    }

    @Test
    public void testSuspend() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.start();
        Assert.assertEquals(true, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.suspend();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(true, ctx.isSuspended());
        ctx.suspend();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(true, ctx.isSuspended());
        ctx.stop();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
    }

    @Test
    public void testResume() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext(false);
        ctx.disableJMX();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.start();
        Assert.assertEquals(true, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.resume();
        Assert.assertEquals(true, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.resume();
        Assert.assertEquals(true, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.stop();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
    }

    @Test
    public void testSuspendResume() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.start();
        Assert.assertEquals(true, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.suspend();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(true, ctx.isSuspended());
        ctx.resume();
        Assert.assertEquals(true, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
        ctx.stop();
        Assert.assertEquals(false, ctx.isStarted());
        Assert.assertEquals(false, ctx.isSuspended());
    }

    @Test
    public void testAddServiceInjectCamelContext() throws Exception {
        DefaultCamelContextTest.MyService my = new DefaultCamelContextTest.MyService();
        DefaultCamelContext ctx = new DefaultCamelContext();
        ctx.addService(my);
        ctx.start();
        Assert.assertEquals(ctx, my.getCamelContext());
        Assert.assertEquals("Started", getStatus().name());
        ctx.stop();
        Assert.assertEquals("Stopped", getStatus().name());
    }

    @Test
    public void testAddServiceType() throws Exception {
        DefaultCamelContextTest.MyService my = new DefaultCamelContextTest.MyService();
        DefaultCamelContext ctx = new DefaultCamelContext();
        Assert.assertNull(ctx.hasService(DefaultCamelContextTest.MyService.class));
        ctx.addService(my);
        Assert.assertSame(my, ctx.hasService(DefaultCamelContextTest.MyService.class));
        ctx.stop();
        Assert.assertNull(ctx.hasService(DefaultCamelContextTest.MyService.class));
    }

    private static class MyService extends ServiceSupport implements CamelContextAware {
        private CamelContext camelContext;

        public CamelContext getCamelContext() {
            return camelContext;
        }

        public void setCamelContext(CamelContext camelContext) {
            this.camelContext = camelContext;
        }

        @Override
        protected void doStart() throws Exception {
            // noop
        }

        @Override
        protected void doStop() throws Exception {
            // noop
        }
    }
}

