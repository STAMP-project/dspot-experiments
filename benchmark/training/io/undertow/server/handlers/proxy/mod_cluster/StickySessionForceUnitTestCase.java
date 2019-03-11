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
import java.io.IOException;
import org.junit.Test;


/**
 * Test sticky session force == false; behavior
 *
 * @author Emanuel Muckenhuber
 */
public class StickySessionForceUnitTestCase extends AbstractModClusterTestBase {
    static NodeTestConfig server1;

    static NodeTestConfig server2;

    static {
        StickySessionForceUnitTestCase.server1 = // Force = false
        NodeTestConfig.builder().setStickySessionForce(false).setJvmRoute("server1").setType(AbstractModClusterTestBase.getType()).setHostname("localhost").setPort(((AbstractModClusterTestBase.port) + 1));
        StickySessionForceUnitTestCase.server2 = // Force = false
        NodeTestConfig.builder().setStickySessionForce(false).setJvmRoute("server2").setType(AbstractModClusterTestBase.getType()).setHostname("localhost").setPort(((AbstractModClusterTestBase.port) + 2));
    }

    @Test
    public void testNoDomainRemovedContext() throws IOException {
        // If no domain is configured apps cannot failover
        registerNodes(true, StickySessionForceUnitTestCase.server1, StickySessionForceUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testNoDomainStoppedContext() throws IOException {
        // If no domain is configured apps cannot failover
        registerNodes(true, StickySessionForceUnitTestCase.server1, StickySessionForceUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testNoDomainNodeInError() throws IOException {
        // If no domain is configured apps cannot failover
        registerNodes(true, StickySessionForceUnitTestCase.server1, StickySessionForceUnitTestCase.server2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionForceUnitTestCase.server1.getJvmRoute(), (-1));
        } else {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionForceUnitTestCase.server2.getJvmRoute(), (-1));
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDifferentDomainRemovedContext() throws IOException {
        // Test failover in a different domain
        final NodeTestConfig config1 = StickySessionForceUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionForceUnitTestCase.server2.clone().setDomain("domain2");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDifferentDomainStoppedContext() throws IOException {
        // Test failover in a different domain
        final NodeTestConfig config1 = StickySessionForceUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionForceUnitTestCase.server2.clone().setDomain("domain2");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDifferentDomainNodeInError() throws IOException {
        // Test failover in a different domain
        final NodeTestConfig config1 = StickySessionForceUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionForceUnitTestCase.server2.clone().setDomain("domain2");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionForceUnitTestCase.server1.getJvmRoute(), (-1));
        } else {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionForceUnitTestCase.server2.getJvmRoute(), (-1));
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDomainStoppedContext() throws IOException {
        // Test failover in the same domain
        final NodeTestConfig config1 = StickySessionForceUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionForceUnitTestCase.server2.clone().setDomain("domain1");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.stopApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDomainRemovedContext() throws IOException {
        // Test failover in the same domain
        final NodeTestConfig config1 = StickySessionForceUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionForceUnitTestCase.server2.clone().setDomain("domain1");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        } else {
            AbstractModClusterTestBase.modClusterClient.removeApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }

    @Test
    public void testDomainNodeInError() throws IOException {
        // Test failover in the same domain
        final NodeTestConfig config1 = StickySessionForceUnitTestCase.server1.clone().setDomain("domain1");
        final NodeTestConfig config2 = StickySessionForceUnitTestCase.server2.clone().setDomain("domain1");
        registerNodes(true, config1, config2);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server1.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        AbstractModClusterTestBase.modClusterClient.enableApp(StickySessionForceUnitTestCase.server2.getJvmRoute(), AbstractModClusterTestBase.SESSION);
        final String response = AbstractModClusterTestBase.checkGet("/session", OK);
        if (response.startsWith(StickySessionForceUnitTestCase.server1.getJvmRoute())) {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionForceUnitTestCase.server1.getJvmRoute(), (-1));
        } else {
            AbstractModClusterTestBase.modClusterClient.updateLoad(StickySessionForceUnitTestCase.server2.getJvmRoute(), (-1));
        }
        AbstractModClusterTestBase.checkGet("/session", OK);
    }
}

