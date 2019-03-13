/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.management.api;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.concurrent.Callable;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.integration.management.util.WebUtil;
import org.jboss.as.test.shared.RetryTaskExecutor;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Dominik Pospisil <dpospisi@redhat.com>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SocketsAndInterfacesTestCase extends ContainerResourceMgmtTestBase {
    private static final Logger logger = Logger.getLogger(SocketsAndInterfacesTestCase.class);

    @ArquillianResource
    URL url;

    private NetworkInterface testNic;

    private String testHost;

    private static final int TEST_PORT = 9695;

    private AutoCloseable snapshot;

    @Test
    public void testAddUpdateRemove() throws Exception {
        if ((testNic) == null) {
            SocketsAndInterfacesTestCase.logger.error("Could not look up non-default interface");
            return;
        }
        // add interface
        ModelNode op = createOpNode("interface=test123-interface", ADD);
        op.get("nic").set(testNic.getName());
        op.get("inet-address").set(testHost);
        ModelNode result = executeOperation(op);
        // add socket binding using created interface
        op = createOpNode("socket-binding-group=standard-sockets/socket-binding=test123-binding", ADD);
        op.get("interface").set("test123-interface");
        op.get("port").set(SocketsAndInterfacesTestCase.TEST_PORT);
        result = executeOperation(op);
        // add a web connector so we can test the interface
        op = createOpNode("subsystem=undertow/server=default-server/http-listener=test", ADD);
        op.get("socket-binding").set("test123-binding");
        result = executeOperation(op);
        final URL url = new URL("http", testHost, SocketsAndInterfacesTestCase.TEST_PORT, "/");
        Assert.assertTrue(((((((("Could not connect to created connector: " + url) + "<>") + (InetAddress.getByName(url.getHost()))) + "...") + (testNic)) + ".>") + result), WebUtil.testHttpURL(url.toString()));
        // change socket binding port
        op = createOpNode("socket-binding-group=standard-sockets/socket-binding=test123-binding", WRITE_ATTRIBUTE_OPERATION);
        op.get(NAME).set("port");
        op.get(VALUE).set(((SocketsAndInterfacesTestCase.TEST_PORT) + 1));
        result = executeOperation(op, false);
        Assert.assertEquals(SUCCESS, result.get(OUTCOME).asString());
        Assert.assertTrue(result.get(RESPONSE_HEADERS).get(PROCESS_STATE).asString().equals("reload-required"));
        SocketsAndInterfacesTestCase.logger.trace("Restarting server.");
        ServerReload.executeReloadAndWaitForCompletion(getModelControllerClient());
        // wait until the connector is available on the new port
        final String testUrl = new URL("http", testHost, ((SocketsAndInterfacesTestCase.TEST_PORT) + 1), "/").toString();
        RetryTaskExecutor<Boolean> rte = new RetryTaskExecutor<Boolean>();
        rte.retryTask(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                boolean available = WebUtil.testHttpURL(testUrl);
                if (!available)
                    throw new Exception("Connector not available.");

                return available;
            }
        });
        SocketsAndInterfacesTestCase.logger.trace("Server is up.");
        // check the connector is not listening on the old port
        Assert.assertFalse("Could not connect to created connector.", WebUtil.testHttpURL(new URL("http", testHost, SocketsAndInterfacesTestCase.TEST_PORT, "/").toString()));
        // try to remove the interface while the socket binding is still  bound to it - should fail
        op = createOpNode("interface=test123-interface", REMOVE);
        result = executeOperation(op, false);
        Assert.assertFalse("Removed interface with socket binding bound to it.", SUCCESS.equals(result.get(OUTCOME).asString()));
        // try to remove socket binding while the connector is still using it - should fail
        op = createOpNode("socket-binding-group=standard-sockets/socket-binding=test123-binding", REMOVE);
        result = executeOperation(op, false);
        Assert.assertFalse("Removed socked binding with connector still using it.", SUCCESS.equals(result.get(OUTCOME).asString()));
    }
}

