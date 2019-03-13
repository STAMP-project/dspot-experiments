/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.mgmt.access;


import ApplicationTypeAccessConstraintDefinition.DEPLOYMENT;
import ModelDescriptionConstants.APPLIES_TO;
import ModelDescriptionConstants.CHILD_TYPE;
import ModelDescriptionConstants.ENTIRE_RESOURCE;
import ModelDescriptionConstants.RESULT;
import ModelDescriptionConstants.SENSITIVE;
import Outcome.SUCCESS;
import SensitiveTargetAccessConstraintDefinition.ACCESS_CONTROL;
import SensitiveTargetAccessConstraintDefinition.CREDENTIAL;
import SensitiveTargetAccessConstraintDefinition.EXTENSIONS;
import SensitiveTargetAccessConstraintDefinition.MANAGEMENT_INTERFACES;
import SensitiveTargetAccessConstraintDefinition.MODULE_LOADING;
import SensitiveTargetAccessConstraintDefinition.PATCHING;
import SensitiveTargetAccessConstraintDefinition.READ_WHOLE_CONFIG;
import SensitiveTargetAccessConstraintDefinition.SECURITY_DOMAIN;
import SensitiveTargetAccessConstraintDefinition.SECURITY_DOMAIN_REF;
import SensitiveTargetAccessConstraintDefinition.SECURITY_REALM;
import SensitiveTargetAccessConstraintDefinition.SECURITY_REALM_REF;
import SensitiveTargetAccessConstraintDefinition.SECURITY_VAULT;
import SensitiveTargetAccessConstraintDefinition.SERVICE_CONTAINER;
import SensitiveTargetAccessConstraintDefinition.SNAPSHOTS;
import SensitiveTargetAccessConstraintDefinition.SOCKET_BINDING_REF;
import SensitiveTargetAccessConstraintDefinition.SOCKET_CONFIG;
import SensitiveTargetAccessConstraintDefinition.SYSTEM_PROPERTY;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.access.management.AccessConstraintKey;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.test.integration.management.rbac.RbacUtil;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test of the access constraint utilization resources.
 *
 * @author Brian Stansberry (c) 2013 Red Hat Inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AccessConstraintUtilizationTestCase extends AbstractRbacTestCase {
    private static class ExpectedDef {
        private final AccessConstraintKey key;

        private final boolean expectResource;

        private final boolean expectAttributes;

        private final boolean expectOps;

        private ExpectedDef(AccessConstraintKey key, boolean expectResource, boolean expectAttributes, boolean expectOps) {
            this.key = key;
            this.expectResource = expectResource;
            this.expectAttributes = expectAttributes;
            this.expectOps = expectOps;
        }
    }

    private static final String ADDR_FORMAT = "core-service=management/access=authorization/constraint=%s/type=%s/classification=%s";

    private static final AccessConstraintUtilizationTestCase.ExpectedDef[] EXPECTED_DEFS = new AccessConstraintUtilizationTestCase.ExpectedDef[]{ new AccessConstraintUtilizationTestCase.ExpectedDef(ACCESS_CONTROL.getKey(), true, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(CREDENTIAL.getKey(), false, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(EXTENSIONS.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(MANAGEMENT_INTERFACES.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(MODULE_LOADING.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(PATCHING.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(READ_WHOLE_CONFIG.getKey(), false, false, true), new AccessConstraintUtilizationTestCase.ExpectedDef(SECURITY_DOMAIN.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SECURITY_DOMAIN_REF.getKey(), false, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SECURITY_REALM.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SECURITY_REALM_REF.getKey(), false, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SECURITY_VAULT.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SERVICE_CONTAINER.getKey(), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SOCKET_BINDING_REF.getKey(), false, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(SOCKET_CONFIG.getKey(), true, true, true), new AccessConstraintUtilizationTestCase.ExpectedDef(SNAPSHOTS.getKey(), false, false, true), new AccessConstraintUtilizationTestCase.ExpectedDef(SYSTEM_PROPERTY.getKey(), true, true, true), // A few subsystem ones
    new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getSensKey("undertow", "web-access-log"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getSensKey("datasources", "data-source-security"), false, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getSensKey("resource-adapters", "resource-adapter-security"), false, true, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getSensKey("jdr", "jdr"), false, false, true), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getSensKey("messaging-activemq", "messaging-management"), false, true, false), /* N/A on standalone
    new ExpectedDef(SensitiveTargetAccessConstraintDefinition.DOMAIN_CONTROLLER, false, true, true),
    new ExpectedDef(SensitiveTargetAccessConstraintDefinition.DOMAIN_NAMES, false, true, false),
    new ExpectedDef(SensitiveTargetAccessConstraintDefinition.JVM, false, true, true),
     */
    new AccessConstraintUtilizationTestCase.ExpectedDef(DEPLOYMENT.getKey(), true, false, true), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("datasources", "data-source"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("datasources", "xa-data-source"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("datasources", "jdbc-driver"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("messaging-activemq", "queue"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("messaging-activemq", "jms-queue"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("messaging-activemq", "jms-topic"), true, false, false), // Agroal
    new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("datasources-agroal", "datasource"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("datasources-agroal", "xa-datasource"), true, false, false), new AccessConstraintUtilizationTestCase.ExpectedDef(AccessConstraintUtilizationTestCase.getAppKey("datasources-agroal", "driver"), true, false, false) };

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testConstraintUtilization() throws Exception {
        ModelControllerClient client = managementClient.getControllerClient();
        for (AccessConstraintUtilizationTestCase.ExpectedDef expectedDef : AccessConstraintUtilizationTestCase.EXPECTED_DEFS) {
            AccessConstraintKey acdKey = expectedDef.key;
            String constraint = (SENSITIVE.equals(acdKey.getType())) ? ModelDescriptionConstants.SENSITIVITY_CLASSIFICATION : ModelDescriptionConstants.APPLICATION_CLASSIFICATION;
            String acdType = (acdKey.isCore()) ? "core" : acdKey.getSubsystemName();
            String path = String.format(AccessConstraintUtilizationTestCase.ADDR_FORMAT, acdKey.getType(), acdType, acdKey.getName());
            ModelNode op = createOpNode(path, READ_CHILDREN_RESOURCES_OPERATION);
            op.get(CHILD_TYPE).set(APPLIES_TO);
            // System.out.println("Testing " + acdKey);
            ModelNode result = RbacUtil.executeOperation(client, op, SUCCESS).get(RESULT);
            Assert.assertTrue((acdKey + "result is defined"), result.isDefined());
            Assert.assertTrue((acdKey + "result has content"), ((result.asInt()) > 0));
            boolean foundResource = false;
            boolean foundAttr = false;
            boolean foundOps = false;
            for (Property prop : result.asPropertyList()) {
                ModelNode pathResult = prop.getValue();
                if (pathResult.get(ENTIRE_RESOURCE).asBoolean()) {
                    Assert.assertTrue((((acdKey + " -- ") + (prop.getName())) + " resource"), expectedDef.expectResource);
                    foundResource = true;
                }
                ModelNode attrs = pathResult.get(ATTRIBUTES);
                if ((attrs.isDefined()) && ((attrs.asInt()) > 0)) {
                    Assert.assertTrue(((((acdKey + " -- ") + (prop.getName())) + " attributes = ") + (attrs.asString())), expectedDef.expectAttributes);
                    foundAttr = true;
                }
                ModelNode ops = pathResult.get(OPERATIONS);
                if ((ops.isDefined()) && ((ops.asInt()) > 0)) {
                    Assert.assertTrue(((((acdKey + " -- ") + (prop.getName())) + " operations = ") + (ops.asString())), expectedDef.expectOps);
                    foundOps = true;
                }
            }
            Assert.assertEquals((acdKey + " -- resource"), expectedDef.expectResource, foundResource);
            Assert.assertEquals((acdKey + " -- attributes"), expectedDef.expectAttributes, foundAttr);
            Assert.assertEquals((acdKey + " -- operations"), expectedDef.expectOps, foundOps);
        }
    }
}

