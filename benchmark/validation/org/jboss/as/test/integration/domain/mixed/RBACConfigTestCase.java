/**
 * Copyright 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.domain.mixed;


import java.io.IOException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 * Tests that RBAC configuration is properly handled in a mixed-domain.
 *
 * @author Brian Stansberry
 */
public class RBACConfigTestCase {
    private static final PathAddress RBAC_BASE = PathAddress.pathAddress(PathElement.pathElement(CORE_SERVICE, MANAGEMENT), PathElement.pathElement(ACCESS, AUTHORIZATION));

    private static final PathAddress SERVER_ONE = PathAddress.pathAddress(PathElement.pathElement(HOST, "slave"), PathElement.pathElement(RUNNING_SERVER, "server-one"));

    private static ModelControllerClient masterClient;

    private static ModelControllerClient slaveClient;

    @Test
    public void testWriteRBACProvider() throws IOException {
        modifyTest(RBACConfigTestCase.RBAC_BASE, PROVIDER);
    }

    @Test
    public void testAddRoleMapping() throws IOException {
        PathAddress address = RBACConfigTestCase.RBAC_BASE.append(ROLE_MAPPING, "Operator");
        String attribute = INCLUDE_ALL;
        ModelNode value = new ModelNode(true);
        ModelNode addOp = Util.createAddOperation(address);
        addOp.get(attribute).set(value);
        addTest(address, attribute, value, addOp);
    }

    @Test
    public void testAddHostScopedRole() throws IOException {
        PathAddress address = RBACConfigTestCase.RBAC_BASE.append(HOST_SCOPED_ROLE, "WFCORE-1622_H");
        String attribute = BASE_ROLE;
        ModelNode value = new ModelNode("Operator");
        ModelNode addOp = Util.createAddOperation(address);
        addOp.get(attribute).set(value);
        addOp.get(HOSTS).add("slave");
        addTest(address, attribute, value, addOp);
    }

    @Test
    public void testAddServerGroupScopedRole() throws IOException {
        PathAddress address = RBACConfigTestCase.RBAC_BASE.append(SERVER_GROUP_SCOPED_ROLE, "WFCORE-1622_S");
        String attribute = BASE_ROLE;
        ModelNode value = new ModelNode("Operator");
        ModelNode addOp = Util.createAddOperation(address);
        addOp.get(attribute).set(value);
        addOp.get(SERVER_GROUPS).add("main-server-group");
        addTest(address, attribute, value, addOp);
    }

    @Test
    public void testModifySensitivityConstraint() throws IOException {
        PathAddress mapping = RBACConfigTestCase.RBAC_BASE.append(CONSTRAINT, SENSITIVITY_CLASSIFICATION).append(TYPE, CORE).append(CLASSIFICATION, "socket-config");
        modifyTest(mapping, CONFIGURED_REQUIRES_WRITE, new ModelNode(true), true);
    }

    @Test
    public void testModifyServerSSLSensitivityConstraint() throws IOException {
        PathAddress mapping = RBACConfigTestCase.RBAC_BASE.append(CONSTRAINT, SENSITIVITY_CLASSIFICATION).append(TYPE, CORE).append(CLASSIFICATION, "server-ssl");
        modifyTest(mapping, CONFIGURED_REQUIRES_WRITE, new ModelNode(true), getSupportsServerSSL());
    }

    @Test
    public void testModifyApplicationConstraint() throws IOException {
        PathAddress mapping = RBACConfigTestCase.RBAC_BASE.append(CONSTRAINT, APPLICATION_CLASSIFICATION).append(TYPE, CORE).append(CLASSIFICATION, "deployment");
        modifyTest(mapping, CONFIGURED_APPLICATION, new ModelNode(true), true);
    }

    @Test
    public void testModifySensitiveExpressionsConstraint() throws IOException {
        PathAddress mapping = RBACConfigTestCase.RBAC_BASE.append(CONSTRAINT, VAULT_EXPRESSION);
        modifyTest(mapping, CONFIGURED_REQUIRES_WRITE, new ModelNode(true), true);
    }
}

