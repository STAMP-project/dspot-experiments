/**
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.domain.suites;


import ModelDescriptionConstants.HOST;
import java.io.IOException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cached-connection-manager runtime-only ops registered against domain profile resources
 */
public class JcaCCMRuntimeOnlyProfileOpsTestCase {
    private static final PathAddress MASTER = PathAddress.pathAddress(HOST, "master");

    private static final PathAddress SLAVE = PathAddress.pathAddress(HOST, "slave");

    private static final PathElement MAIN_ONE = PathElement.pathElement("server", "main-one");

    private static final PathElement MAIN_THREE = PathElement.pathElement("server", "main-three");

    private static final PathAddress PROFILE = PathAddress.pathAddress("profile", "default");

    private static final PathElement SUBSYSTEM = PathElement.pathElement("subsystem", "jca");

    private static final PathElement CCM = PathElement.pathElement("cached-connection-manager", "cached-connection-manager");

    private static DomainTestSupport testSupport;

    private static ModelControllerClient client;

    @Test
    public void testGetNumberOfConnections() throws IOException {
        final String opName = "get-number-of-connections";
        ModelNode op = Util.createEmptyOperation(opName, JcaCCMRuntimeOnlyProfileOpsTestCase.PROFILE.append(JcaCCMRuntimeOnlyProfileOpsTestCase.SUBSYSTEM).append(JcaCCMRuntimeOnlyProfileOpsTestCase.CCM));
        ModelNode response = JcaCCMRuntimeOnlyProfileOpsTestCase.executeOp(op, SUCCESS);
        Assert.assertFalse(response.toString(), response.hasDefined(RESULT));// handler doesn't set a result on profile

        Assert.assertTrue(response.toString(), response.hasDefined(SERVER_GROUPS, "main-server-group", "host", "master", "main-one", "response", "result", "TX"));
        Assert.assertTrue(response.toString(), response.hasDefined(SERVER_GROUPS, "main-server-group", "host", "master", "main-one", "response", "result", "NonTX"));
        Assert.assertEquals(0, response.get(SERVER_GROUPS, "main-server-group", "host", "master", "main-one", "response", "result", "TX").asInt());
        Assert.assertEquals(0, response.get(SERVER_GROUPS, "main-server-group", "host", "master", "main-one", "response", "result", "NonTX").asInt());
        Assert.assertTrue(response.toString(), response.hasDefined(SERVER_GROUPS, "main-server-group", "host", "slave", "main-three", "response", "result", "TX"));
        Assert.assertTrue(response.toString(), response.hasDefined(SERVER_GROUPS, "main-server-group", "host", "slave", "main-three", "response", "result", "NonTX"));
        Assert.assertEquals(0, response.get(SERVER_GROUPS, "main-server-group", "host", "slave", "main-three", "response", "result", "TX").asInt());
        Assert.assertEquals(0, response.get(SERVER_GROUPS, "main-server-group", "host", "slave", "main-three", "response", "result", "NonTX").asInt());
        // Now check direct invocation on servers
        op = Util.createEmptyOperation(opName, JcaCCMRuntimeOnlyProfileOpsTestCase.MASTER.append(JcaCCMRuntimeOnlyProfileOpsTestCase.MAIN_ONE).append(JcaCCMRuntimeOnlyProfileOpsTestCase.SUBSYSTEM).append(JcaCCMRuntimeOnlyProfileOpsTestCase.CCM));
        response = JcaCCMRuntimeOnlyProfileOpsTestCase.executeOp(op, SUCCESS);
        Assert.assertEquals(0, response.get(RESULT).get("TX").asInt());
        Assert.assertEquals(0, response.get(RESULT).get("NonTX").asInt());
        op = Util.createEmptyOperation(opName, JcaCCMRuntimeOnlyProfileOpsTestCase.SLAVE.append(JcaCCMRuntimeOnlyProfileOpsTestCase.MAIN_THREE).append(JcaCCMRuntimeOnlyProfileOpsTestCase.SUBSYSTEM).append(JcaCCMRuntimeOnlyProfileOpsTestCase.CCM));
        response = JcaCCMRuntimeOnlyProfileOpsTestCase.executeOp(op, SUCCESS);
        Assert.assertEquals(0, response.get(RESULT).get("TX").asInt());
        Assert.assertEquals(0, response.get(RESULT).get("NonTX").asInt());
    }

    @Test
    public void testListConnections() throws IOException {
        final String opName = "list-connections";
        ModelNode op = Util.createEmptyOperation(opName, JcaCCMRuntimeOnlyProfileOpsTestCase.PROFILE.append(JcaCCMRuntimeOnlyProfileOpsTestCase.SUBSYSTEM).append(JcaCCMRuntimeOnlyProfileOpsTestCase.CCM));
        ModelNode response = JcaCCMRuntimeOnlyProfileOpsTestCase.executeOp(op, SUCCESS);
        Assert.assertFalse(response.toString(), response.hasDefined(RESULT));// handler doesn't set a result on profile

        Assert.assertTrue(response.toString(), response.has(SERVER_GROUPS, "main-server-group", "host", "master", "main-one", "response", "result", "TX"));
        Assert.assertTrue(response.toString(), response.has(SERVER_GROUPS, "main-server-group", "host", "master", "main-one", "response", "result", "NonTX"));
        Assert.assertTrue(response.toString(), response.has(SERVER_GROUPS, "main-server-group", "host", "slave", "main-three", "response", "result", "TX"));
        Assert.assertTrue(response.toString(), response.has(SERVER_GROUPS, "main-server-group", "host", "slave", "main-three", "response", "result", "NonTX"));
        // Now check direct invocation on servers
        op = Util.createEmptyOperation(opName, JcaCCMRuntimeOnlyProfileOpsTestCase.MASTER.append(JcaCCMRuntimeOnlyProfileOpsTestCase.MAIN_ONE).append(JcaCCMRuntimeOnlyProfileOpsTestCase.SUBSYSTEM).append(JcaCCMRuntimeOnlyProfileOpsTestCase.CCM));
        response = JcaCCMRuntimeOnlyProfileOpsTestCase.executeOp(op, SUCCESS);
        Assert.assertTrue(response.has(RESULT, "TX"));
        Assert.assertTrue(response.has(RESULT, "NonTX"));
        op = Util.createEmptyOperation(opName, JcaCCMRuntimeOnlyProfileOpsTestCase.SLAVE.append(JcaCCMRuntimeOnlyProfileOpsTestCase.MAIN_THREE).append(JcaCCMRuntimeOnlyProfileOpsTestCase.SUBSYSTEM).append(JcaCCMRuntimeOnlyProfileOpsTestCase.CCM));
        response = JcaCCMRuntimeOnlyProfileOpsTestCase.executeOp(op, SUCCESS);
        Assert.assertTrue(response.has(RESULT, "TX"));
        Assert.assertTrue(response.has(RESULT, "NonTX"));
    }
}

