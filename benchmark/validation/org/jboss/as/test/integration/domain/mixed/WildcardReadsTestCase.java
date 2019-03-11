/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.domain.mixed;


import ModelType.EXPRESSION;
import ModelType.OBJECT;
import ModelType.UNDEFINED;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ValueExpression;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for cross-process wildcard reads in a mixed domain. See https://issues.jboss.org/browse/WFCORE-621.
 *
 * @author Brian Stansberry
 */
public class WildcardReadsTestCase {
    private static final PathElement HOST_WILD = PathElement.pathElement(HOST);

    private static final PathElement HOST_MASTER = PathElement.pathElement(HOST, "master");

    private static final PathElement HOST_SLAVE = PathElement.pathElement(HOST, "slave");

    private static final PathElement SERVER_WILD = PathElement.pathElement(RUNNING_SERVER);

    private static final PathElement SERVER_ONE = PathElement.pathElement(RUNNING_SERVER, "server-one");

    private static final PathElement INTERFACE_WILD = PathElement.pathElement(INTERFACE);

    private static final PathElement INTERFACE_PUBLIC = PathElement.pathElement(INTERFACE, "public");

    private static final PathElement SERVER_CONFIG_WILD = PathElement.pathElement(SERVER_CONFIG);

    private static final PathElement SERVER_CONFIG_ONE = PathElement.pathElement(SERVER_CONFIG, "server-one");

    private static final PathAddress SOCKET_BINDING_HTTP = PathAddress.pathAddress(PathElement.pathElement(SOCKET_BINDING_GROUP, "standard-sockets"), PathElement.pathElement(SOCKET_BINDING, "http"));

    private static final ValueExpression MASTER_ADDRESS = new ValueExpression("${jboss.test.host.master.address}");

    private static final ValueExpression SLAVE_ADDRESS = new ValueExpression("${jboss.test.host.slave.address}");

    private static final Set<String> VALID_STATES = new HashSet<>(Arrays.asList("running", "stopped"));

    private static DomainTestSupport support;

    private static Boolean masterServerOneStarted;

    @Test
    public void testAllHostsAllServersReadInterfaceResources() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 6, resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 3)) {
                masterCount++;
            }
            Assert.assertEquals(item.toString(), EXPRESSION, item.get(RESULT, INET_ADDRESS).getType());
        }
        Assert.assertEquals(resp.toString(), 3, masterCount);
    }

    @Test
    public void testSlaveAllServersReadInterfaceResources() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 3)) {
                masterCount++;
            }
            Assert.assertEquals(item.toString(), EXPRESSION, item.get(RESULT, INET_ADDRESS).getType());
        }
        Assert.assertEquals(resp.toString(), 0, masterCount);
    }

    @Test
    public void testAllHostsAllServersReadRootResource() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD));
        op.get(INCLUDE_RUNTIME).set(true);
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 4 : 3), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), WildcardReadsTestCase.VALID_STATES.contains(item.get(RESULT, "server-state").asString().toLowerCase(Locale.ENGLISH)));
        }
        Assert.assertEquals(resp.toString(), 2, masterCount);
    }

    @Test
    public void testSlaveAllServersReadRootResource() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD));
        op.get(INCLUDE_RUNTIME).set(true);
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 2 : 1), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), WildcardReadsTestCase.VALID_STATES.contains(item.get(RESULT, "server-state").asString().toLowerCase(Locale.ENGLISH)));
        }
        Assert.assertEquals(resp.toString(), 0, masterCount);
    }

    @Test
    public void testAllHostsAllServersReadInterfaceAttribute() {
        ModelNode op = Util.createEmptyOperation(READ_ATTRIBUTE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_PUBLIC));
        op.get(NAME).set("inet-address");
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 2, resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 3)) {
                masterCount++;
            }
            Assert.assertEquals(item.toString(), EXPRESSION, item.get(RESULT).getType());
        }
        Assert.assertEquals(resp.toString(), 1, masterCount);
    }

    @Test
    public void testSlaveAllServersReadInterfaceAttribute() {
        ModelNode op = Util.createEmptyOperation(READ_ATTRIBUTE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_PUBLIC));
        op.get(NAME).set("inet-address");
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 3)) {
                masterCount++;
            }
            Assert.assertEquals(item.toString(), EXPRESSION, item.get(RESULT).getType());
        }
        Assert.assertEquals(resp.toString(), 0, masterCount);
    }

    @Test
    public void testAllHostsAllServersReadRootAttribute() {
        ModelNode op = Util.createEmptyOperation(READ_ATTRIBUTE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD));
        op.get(NAME).set("server-state");
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 4 : 3), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), WildcardReadsTestCase.VALID_STATES.contains(item.get(RESULT).asString().toLowerCase(Locale.ENGLISH)));
        }
        Assert.assertEquals(resp.toString(), 2, masterCount);
    }

    @Test
    public void testSlaveAllServersReadRootAttribute() {
        ModelNode op = Util.createEmptyOperation(READ_ATTRIBUTE_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD));
        op.get(NAME).set("server-state");
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 2 : 1), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), WildcardReadsTestCase.VALID_STATES.contains(item.get(RESULT).asString().toLowerCase(Locale.ENGLISH)));
        }
        Assert.assertEquals(resp.toString(), 0, masterCount);
    }

    @Test
    public void testAllHostsAllServersReadInterfaceDescription() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_DESCRIPTION_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_PUBLIC));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 2, resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 3)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), item.hasDefined(RESULT, ATTRIBUTES, INET_ADDRESS));
        }
        Assert.assertEquals(resp.toString(), 1, masterCount);
    }

    @Test
    public void testSlaveAllServersReadInterfaceDescription() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_DESCRIPTION_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_PUBLIC));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 3)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), item.hasDefined(RESULT, ATTRIBUTES, INET_ADDRESS));
        }
        Assert.assertEquals(resp.toString(), 0, masterCount);
    }

    @Test
    public void testAllHostsAllServersReadRootDescription() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_DESCRIPTION_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 4 : 3), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), item.hasDefined(RESULT, ATTRIBUTES, "server-state"));
        }
        Assert.assertEquals(resp.toString(), 2, masterCount);
    }

    @Test
    public void testSlaveAllServersReadRootDescription() {
        ModelNode op = Util.createEmptyOperation(READ_RESOURCE_DESCRIPTION_OPERATION, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 2 : 1), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            Assert.assertTrue(item.toString(), item.hasDefined(RESULT, ATTRIBUTES, "server-state"));
        }
        Assert.assertEquals(resp.toString(), 0, masterCount);
    }

    @Test
    public void testWildcardHostRootQuery() {
        // Basic /host=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 2, resp.asInt());
        ModelNode slaveResult = null;
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 1)) {
                masterCount++;
            } else {
                slaveResult = item.get(RESULT);
            }
            Assert.assertTrue(item.toString(), item.hasDefined(RESULT, "host-state"));
        }
        Assert.assertEquals(resp.toString(), 1, masterCount);
        Assert.assertNotNull(resp.toString(), slaveResult);
        // Now limit the result to slaves
        op.get(WHERE, MASTER).set(false);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        Assert.assertEquals(resp.toString(), slaveResult, resp.get(0).get(RESULT));
        // Now slim down the output
        op.get(SELECT).add(NAME);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        Assert.assertEquals(resp.toString(), 1, resp.get(0).get(RESULT).keys().size());
        Assert.assertEquals(resp.toString(), "slave", resp.get(0).get(RESULT, NAME).asString());
    }

    @Test
    public void testSpecificHostRootQuery() {
        // /host=slave:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE));
        ModelNode result = WildcardReadsTestCase.executeForResult(op, OBJECT);
        Assert.assertTrue(result.toString(), result.hasDefined("host-state"));
        Assert.assertEquals(result.toString(), "slave", result.get(NAME).asString());
        // Now cause the filter to exclude the slave
        op.get(WHERE, MASTER).set(true);
        WildcardReadsTestCase.executeForResult(op, UNDEFINED);
        // Correct the filter, slim down the input
        op.get(WHERE, MASTER).set(false);
        op.get(SELECT).add(NAME);
        result = WildcardReadsTestCase.executeForResult(op, OBJECT);
        Assert.assertEquals(result.toString(), 1, result.keys().size());
        Assert.assertEquals(result.toString(), "slave", result.get(NAME).asString());
    }

    @Test
    public void testWildcardHostServerConfigQuery() {
        // Basic /host=*/server-config=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_CONFIG_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 4, resp.asInt());
        Set<ModelNode> autoStarts = new HashSet<>();
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            if (isMasterItem(item, 2)) {
                masterCount++;
            }
            ModelNode result = item.get(RESULT);
            Assert.assertTrue(item.toString(), result.has(AUTO_START));
            if ((result.hasDefined(AUTO_START)) && (!(result.get(AUTO_START).asBoolean()))) {
                autoStarts.add(result);
            }
            Assert.assertTrue(item.toString(), result.hasDefined(GROUP));
            Assert.assertTrue(item.toString(), result.hasDefined("status"));
        }
        Assert.assertEquals(resp.toString(), 2, masterCount);
        // Now limit the result to non-auto-start
        op.get(WHERE, AUTO_START).set(false);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        for (ModelNode item : resp.asList()) {
            Assert.assertTrue(resp.toString(), autoStarts.contains(item.get(RESULT)));
        }
        // Now slim down the output
        op.get(SELECT).add(GROUP);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), 1, result.keys().size());
            Assert.assertEquals(resp.toString(), "other-server-group", result.get(GROUP).asString());
        }
    }

    @Test
    public void testSpecificHostServerConfigQuery() {
        // Basic /host=slave/server-config=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_CONFIG_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 2, resp.asInt());
        Set<ModelNode> autoStarts = new HashSet<>();
        for (ModelNode item : resp.asList()) {
            Assert.assertFalse(resp.toString(), isMasterItem(item, 2));
            ModelNode result = item.get(RESULT);
            Assert.assertTrue(item.toString(), result.has(AUTO_START));
            if ((result.hasDefined(AUTO_START)) && (!(result.get(AUTO_START).asBoolean()))) {
                autoStarts.add(result);
            }
            Assert.assertTrue(item.toString(), result.hasDefined(GROUP));
            Assert.assertTrue(item.toString(), result.hasDefined("status"));
        }
        // Now limit the result to auto-start=false servers
        op.get(WHERE, AUTO_START).set(false);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        for (ModelNode item : resp.asList()) {
            Assert.assertTrue(resp.toString(), autoStarts.contains(item.get(RESULT)));
        }
        // Now slim down the output
        op.get(SELECT).add(GROUP);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), 1, result.keys().size());
            Assert.assertEquals(resp.toString(), "other-server-group", result.get(GROUP).asString());
        }
    }

    @Test
    public void testWildcardServerRootQuery() {
        // Basic /host=*/server=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 4 : 3), resp.asInt());
        Set<ModelNode> running = new HashSet<>();
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            String expectedHost;
            if (isMasterItem(item, 2)) {
                masterCount++;
                expectedHost = "master";
            } else {
                expectedHost = "slave";
            }
            ModelNode result = item.get(RESULT);
            Assert.assertTrue(item.toString(), result.hasDefined("server-state"));
            if (result.get("server-state").asString().toLowerCase(Locale.ENGLISH).equals("running")) {
                Assert.assertEquals(resp.toString(), expectedHost, result.get(HOST).asString());
                running.add(result);
            }
        }
        Assert.assertEquals(resp.toString(), 2, masterCount);
        Assert.assertEquals(resp.toString(), 2, running.size());
        // Now limit the result to running servers
        op.get(WHERE, "server-state").set("running");
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 2, resp.asInt());
        for (ModelNode item : resp.asList()) {
            Assert.assertTrue(resp.toString(), running.contains(item.get(RESULT)));
        }
        // Now slim down the output
        op.get(SELECT).add(SERVER_GROUP);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 2, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), 1, result.keys().size());
            Assert.assertEquals(resp.toString(), "other-server-group", result.get(SERVER_GROUP).asString());
        }
    }

    @Test
    public void testSpecificHostServerRootQuery() {
        // Basic /host=slave/server=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (expectUnstartedServerResource() ? 2 : 1), resp.asInt());
        Set<ModelNode> running = new HashSet<>();
        for (ModelNode item : resp.asList()) {
            Assert.assertFalse(resp.toString(), isMasterItem(item, 2));
            ModelNode result = item.get(RESULT);
            Assert.assertTrue(item.toString(), result.hasDefined("server-state"));
            if (result.get("server-state").asString().toLowerCase(Locale.ENGLISH).equals("running")) {
                Assert.assertEquals(resp.toString(), "slave", result.get(HOST).asString());
                running.add(result);
            }
        }
        Assert.assertEquals(resp.toString(), 1, running.size());
        // Now limit the result to running servers
        op.get(WHERE, "server-state").set("running");
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        for (ModelNode item : resp.asList()) {
            Assert.assertTrue(resp.toString(), running.contains(item.get(RESULT)));
        }
        // Now slim down the output
        op.get(SELECT).add(SERVER_GROUP);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 1, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), 1, result.keys().size());
            Assert.assertEquals(resp.toString(), "other-server-group", result.get(SERVER_GROUP).asString());
        }
    }

    @Test
    public void testSpecificServerRootQuery() {
        // /host=slave/server=server-one:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_ONE));
        ModelNode result = WildcardReadsTestCase.executeForResult(op, OBJECT);
        Assert.assertEquals(result.toString(), "running", result.get("server-state").asString());
        Assert.assertEquals(result.toString(), "slave", result.get(HOST).asString());
        // Now cause the filter to exclude the server
        op.get(WHERE, HOST).set("master");
        WildcardReadsTestCase.executeForResult(op, UNDEFINED);
        // Correct the filter, slim down the input
        op.get(WHERE, HOST).set("slave");
        op.get(SELECT).add(NAME);
        result = WildcardReadsTestCase.executeForResult(op, OBJECT);
        Assert.assertEquals(result.toString(), 1, result.keys().size());
        Assert.assertEquals(result.toString(), "server-one", result.get(NAME).asString());
    }

    @Test
    public void testWildcardServerWildcardInterfaceQuery() {
        // Basic /host=*/server=*/interface=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_WILD, WildcardReadsTestCase.SERVER_WILD, WildcardReadsTestCase.INTERFACE_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), (2 * 3), resp.asInt());
        int masterCount = 0;
        for (ModelNode item : resp.asList()) {
            ValueExpression expectedAddress;
            if (isMasterItem(item, 3)) {
                masterCount++;
                expectedAddress = WildcardReadsTestCase.MASTER_ADDRESS;
            } else {
                expectedAddress = WildcardReadsTestCase.SLAVE_ADDRESS;
            }
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), expectedAddress, result.get(INET_ADDRESS).asExpression());
        }
        Assert.assertEquals(resp.toString(), 3, masterCount);
        // Now limit the result to slave servers
        op.get(WHERE, INET_ADDRESS).set(WildcardReadsTestCase.SLAVE_ADDRESS);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        // Now slim down the output
        op.get(SELECT).add(INET_ADDRESS);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), 1, result.keys().size());
            Assert.assertEquals(resp.toString(), WildcardReadsTestCase.SLAVE_ADDRESS, result.get(INET_ADDRESS).asExpression());
        }
    }

    @Test
    public void testSpecificServerWildcardInterfaceQuery() {
        // Basic /host=slave/server=server-one/interface=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_ONE, WildcardReadsTestCase.INTERFACE_WILD));
        ModelNode resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), WildcardReadsTestCase.SLAVE_ADDRESS, result.get(INET_ADDRESS).asExpression());
        }
        // Now limit the result to master servers
        // This is a wildcard request, so the result should be an empty list
        op.get(WHERE, INET_ADDRESS).set(WildcardReadsTestCase.MASTER_ADDRESS);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 0, resp.asInt());
        // Now correct the filter and slim down the output
        op.get(WHERE, INET_ADDRESS).set(WildcardReadsTestCase.SLAVE_ADDRESS);
        op.get(SELECT).add(INET_ADDRESS);
        resp = executeForResult(op);
        Assert.assertEquals(resp.toString(), 3, resp.asInt());
        for (ModelNode item : resp.asList()) {
            ModelNode result = item.get(RESULT);
            Assert.assertEquals(resp.toString(), 1, result.keys().size());
            Assert.assertEquals(resp.toString(), WildcardReadsTestCase.SLAVE_ADDRESS, result.get(INET_ADDRESS).asExpression());
        }
    }

    @Test
    public void testSpecificServerSpecificSocketBindingQuery() {
        // /host=slave/server=server-one/socket-binding-group=standard-sockets/socket-binding=*:query
        ModelNode op = Util.createEmptyOperation(QUERY, PathAddress.pathAddress(WildcardReadsTestCase.HOST_SLAVE, WildcardReadsTestCase.SERVER_ONE).append(WildcardReadsTestCase.SOCKET_BINDING_HTTP));
        ModelNode result = WildcardReadsTestCase.executeForResult(op, OBJECT);
        Assert.assertEquals(result.toString(), 8080, result.get(PORT).asInt());
        Assert.assertFalse(result.toString(), result.hasDefined(INTERFACE));
        // Now cause the filter to exclude the server
        op.get(WHERE, INTERFACE).set("bogus");
        WildcardReadsTestCase.executeForResult(op, UNDEFINED);
        // Correct the filter, slim down the input
        op.get(WHERE, INTERFACE).set("undefined");
        op.get(SELECT).add(PORT);
        result = WildcardReadsTestCase.executeForResult(op, OBJECT);
        Assert.assertEquals(result.toString(), 1, result.keys().size());
        Assert.assertEquals(result.toString(), 8080, result.get(PORT).asInt());
    }
}

