/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.smoke.socketbindings;


import java.io.IOException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test if the /socket-binding=* runtime attributes shows the open ports as bound.
 *
 * @author Claudio Miranda
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SocketBindingBoundPortsTestCase {
    private static final String BOUND = "bound";

    private static final String BOUND_PORT = "bound-port";

    private static final String STANDARD_SOCKETS = "standard-sockets";

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testHttpBoundSocket() throws IOException {
        final ModelNode address = new ModelNode();
        address.add(SOCKET_BINDING_GROUP, "standard-sockets").add(SOCKET_BINDING, "http");
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);
        operation.get(OP_ADDR).set(address);
        ModelNode response = execute(operation);
        ModelNode result = response.get(RESULT);
        Assert.assertTrue("http socket binding is not set as bound.", result.get(SocketBindingBoundPortsTestCase.BOUND).asBoolean());
        Assert.assertEquals(result.get(SocketBindingBoundPortsTestCase.BOUND_PORT).asInt(), 8080);
    }

    @Test
    public void testHttpsBoundSocket() throws IOException {
        final ModelNode address = new ModelNode();
        address.add(SOCKET_BINDING_GROUP, SocketBindingBoundPortsTestCase.STANDARD_SOCKETS).add(SOCKET_BINDING, "https");
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);
        operation.get(OP_ADDR).set(address);
        ModelNode response = execute(operation);
        ModelNode result = response.get(RESULT);
        Assert.assertTrue("https socket binding is not set as bound.", result.get(SocketBindingBoundPortsTestCase.BOUND).asBoolean());
        Assert.assertEquals(result.get(SocketBindingBoundPortsTestCase.BOUND_PORT).asInt(), 8443);
    }

    @Test
    public void testIiopBoundSocket() throws IOException {
        final ModelNode address = new ModelNode();
        address.add(SOCKET_BINDING_GROUP, "standard-sockets").add(SOCKET_BINDING, "iiop");
        address.protect();
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);
        operation.get(OP_ADDR).set(address);
        ModelNode response = execute(operation);
        ModelNode result = response.get(RESULT);
        Assert.assertTrue("iiop socket binding is not set as bound.", result.get(SocketBindingBoundPortsTestCase.BOUND).asBoolean());
        Assert.assertEquals(result.get(SocketBindingBoundPortsTestCase.BOUND_PORT).asInt(), 3528);
    }
}

