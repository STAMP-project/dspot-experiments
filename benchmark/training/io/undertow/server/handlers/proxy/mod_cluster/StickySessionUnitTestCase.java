/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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
package io.undertow.server.handlers.proxy.mod_cluster;


import StatusCodes.OK;
import StatusCodes.SERVICE_UNAVAILABLE;
import java.io.IOException;
import org.junit.Test;


/**
 * Test failover with force sticky session == true; (which is the default)
 *
 * @author Emanuel Muckenhuber
 */
public class StickySessionUnitTestCase extends AbstractModClusterTestBase {
    static NodeTestConfig server1;

    static NodeTestConfig server2;

    static {
        StickySessionUnitTestCase.server1 = NodeTestConfig.builder().setJvmRoute("server1").setType(AbstractModClusterTestBase.getType()).setHostname("localhost").setPort(((AbstractModClusterTestBase.port) + 1));
        StickySessionUnitTestCase.server2 = NodeTestConfig.builder().setJvmRoute("server2").setType(AbstractModClusterTestBase.getType()).setHostname("localhost").setPort(((AbstractModClusterTestBase.port) + 2));
    }

    @Test
    public void testDisabledApp() throws IOException {
        // 
        registerNodes(true, StickySessionUnitTestCase.server1, StickySessionUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        final String jvmRoute;
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            jvmRoute = StickySessionUnitTestCase.server1.getJvmRoute();
        } else {
            jvmRoute = StickySessionUnitTestCase.server2.getJvmRoute();
        }
        AbstractModClusterTestBase.modClusterClient.disableApp(jvmRoute, AbstractModClusterTestBase.SESSION);
        for (int i = 0; i < 20; i++) {
            AbstractModClusterTestBase.checkGet("/session", OK, jvmRoute).startsWith(jvmRoute);
        }
    }

    @Test
    public void testNoDomainRemovedContext() throws IOException {
        // If no domain is configured apps cannot failover
        registerNodes(true, StickySessionUnitTestCase.server1, StickySessionUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testNoDomainStoppedContext() throws IOException {
        // If no domain is configured apps cannot failover
        registerNodes(true, StickySessionUnitTestCase.server1, StickySessionUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testNoDomainNodeInError() throws IOException {
        // If no domain is configured apps cannot failover
        registerNodes(true, StickySessionUnitTestCase.server1, StickySessionUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionUnitTestCase.server1.getJvmRoute(), (-1));
        } else {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionUnitTestCase.server2.getJvmRoute(), (-1));
        }
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testDifferentDomainRemovedContext() throws IOException {
        // Test failover in a different domain
        final NodeTestConfig config1 = StickySessionUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionUnitTestCase.server2.clone().setDomain("domain2");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testDifferentDomainStoppedContext() throws IOException {
        // Test failover in a different domain
        final NodeTestConfig config1 = StickySessionUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionUnitTestCase.server2.clone().setDomain("domain2");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testDifferentDomainNodeInError() throws IOException {
        // Test failover in a different domain
        final NodeTestConfig config1 = StickySessionUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionUnitTestCase.server2.clone().setDomain("domain2");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionUnitTestCase.server1.getJvmRoute(), (-1));
        } else {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionUnitTestCase.server2.getJvmRoute(), (-1));
        }
        AbstractModClusterTestBase.checkGet("/session", SERVICE_UNAVAILABLE);
    }

    @Test
    public void testDomainStoppedContext() throws IOException {
        // Test failover in the same domain
        final NodeTestConfig config1 = StickySessionUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionUnitTestCase.server2.clone().setDomain("domain1");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDomainRemovedContext() throws IOException {
        // Test failover in the same domain
        final NodeTestConfig config1 = StickySessionUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionUnitTestCase.server2.clone().setDomain("domain1");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDomainNodeInError() throws IOException {
        // Test failover in the same domain
        final NodeTestConfig config1 = StickySessionUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionUnitTestCase.server2.clone().setDomain("domain1");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionUnitTestCase.server1.getJvmRoute(), (-1));
        } else {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionUnitTestCase.server2.getJvmRoute(), (-1));
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }
}

