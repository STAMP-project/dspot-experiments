/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.test.integration.web;


import java.io.IOException;
import java.net.InetAddress;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that default-web-module works as it should for scenarios:
 * - default host on of single server
 * - non-default host of single server
 * - non default server
 *
 * @author Tomaz Cerar (c) 2015 Red Hat Inc.
 */
// todo this test could probably be done in manual mode test with wildfly runner
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(VirtualHostTestCase.VirtualHostSetupTask.class)
public class VirtualHostTestCase {
    public static class VirtualHostSetupTask extends SnapshotRestoreSetupTask {
        @Override
        public void doSetup(ManagementClient managementClient, String containerId) throws Exception {
            ModelControllerClient client = managementClient.getControllerClient();
            ModelNode addOp = createOpNode("subsystem=undertow/server=default-server/host=test", "add");
            addOp.get("default-web-module").set("test.war");
            addOp.get("alias").add(TestSuiteEnvironment.getServerAddress());// either 127.0.0.1 or ::1

            execute(client, addOp);
            addOp = createOpNode("socket-binding-group=standard-sockets/socket-binding=myserver", "add");
            addOp.get("port").set(8181);
            execute(client, addOp);
            addOp = createOpNode("subsystem=undertow/server=myserver", "add");
            addOp.get("default-host").set("another");
            execute(client, addOp);
            addOp = createOpNode("subsystem=undertow/server=myserver/host=another", "add");
            addOp.get("default-web-module").set("another-server.war");
            execute(client, addOp);
            addOp = createOpNode("subsystem=undertow/server=myserver/http-listener=myserver", "add");
            addOp.get("socket-binding").set("myserver");
            execute(client, addOp);// this one is runtime addable

        }

        private void execute(ModelControllerClient client, ModelNode op) throws IOException {
            op.get(OPERATION_HEADERS, ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            ModelNode response = client.execute(op);
            if (!(SUCCESS.equals(response.get(OUTCOME).asString()))) {
                Assert.fail(((("Could not execute op: '" + op) + "', result: ") + response));
            }
        }
    }

    @Test
    public void testDefaultHost() throws IOException {
        Assume.assumeTrue("This needs to be localhost, as it is by host mapping", InetAddress.getByName(TestSuiteEnvironment.getServerAddress()).isLoopbackAddress());
        callAndTest("http://localhost:8080/", "ROOT");
    }

    @Test
    public void testNonDefaultHost() throws IOException {
        callAndTest((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/"), "test");// second host on first server has alias 127.0.0.1 or ::1

    }

    @Test
    public void testAnotherServerHost() throws IOException {
        callAndTest((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8181/"), "another-server");
    }
}

