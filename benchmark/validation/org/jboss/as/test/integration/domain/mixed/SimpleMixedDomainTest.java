/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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


import Constants.AUTHENTICATION;
import Constants.AUTHORIZATION;
import Constants.AUTH_MODULE;
import Constants.CLASSIC;
import Constants.CODE;
import Constants.FLAG;
import Constants.JASPI;
import Constants.LOGIN_MODULE;
import Constants.LOGIN_MODULE_STACK;
import Constants.LOGIN_MODULE_STACK_REF;
import Constants.MODULE;
import Constants.MODULE_OPTIONS;
import Constants.POLICY_MODULE;
import Constants.SECURITY_DOMAIN;
import DomainTestSupport.slaveAddress;
import SecurityExtension.SUBSYSTEM_NAME;
import java.net.URL;
import java.net.URLConnection;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.model.test.ModelTestUtils;
import org.jboss.as.test.integration.domain.management.util.DomainTestUtils;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.xnio.IoUtils;

import static org.jboss.as.test.integration.domain.mixed.Version.AsVersion.EAP_6_2_0;
import static org.jboss.as.test.integration.domain.mixed.Version.AsVersion.EAP_7_0_0;


/**
 *
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class SimpleMixedDomainTest {
    private static final String ACTIVE_PROFILE = "full-ha";

    MixedDomainTestSupport support;

    Version.AsVersion version;

    @Test
    public void test00001_ServerRunning() throws Exception {
        URLConnection connection = new URL((("http://" + (TestSuiteEnvironment.formatPossibleIpv6Address(slaveAddress))) + ":8080")).openConnection();
        connection.connect();
    }

    @Test
    public void test00002_Versioning() throws Exception {
        if (((version) == (EAP_6_2_0)) || ((version) == (EAP_7_0_0))) {
            // 6.2.0 (https://issues.jboss.org/browse/WFLY-3228) and
            // 7.0.0 (https://issues.jboss.org/browse/WFCORE-401)
            // have the slave report back its own version, rather than the one from the DC,
            // which is what should happen
            return;
        }
        DomainClient masterClient = getDomainMasterLifecycleUtil().createDomainClient();
        ModelNode masterModel;
        try {
            masterModel = readDomainModelForVersions(masterClient);
        } finally {
            IoUtils.safeClose(masterClient);
        }
        DomainClient slaveClient = getDomainSlaveLifecycleUtil().createDomainClient();
        ModelNode slaveModel;
        try {
            slaveModel = readDomainModelForVersions(slaveClient);
        } finally {
            IoUtils.safeClose(slaveClient);
        }
        cleanupKnownDifferencesInModelsForVersioningCheck(masterModel, slaveModel);
        // The version fields should be the same
        Assert.assertEquals(masterModel, slaveModel);
    }

    @Test
    public void test00009_SecurityTransformers() throws Exception {
        final DomainClient masterClient = getDomainMasterLifecycleUtil().createDomainClient();
        final DomainClient slaveClient = getDomainSlaveLifecycleUtil().createDomainClient();
        try {
            PathAddress subsystem = PathAddress.pathAddress(PathElement.pathElement(PROFILE, SimpleMixedDomainTest.ACTIVE_PROFILE), PathElement.pathElement(SUBSYSTEM, SUBSYSTEM_NAME));
            PathAddress webPolicy = subsystem.append(SECURITY_DOMAIN, "jboss-web-policy").append(AUTHORIZATION, CLASSIC);
            ModelNode options = new ModelNode();
            options.add("a", "b");
            ModelNode op = Util.getWriteAttributeOperation(webPolicy.append(POLICY_MODULE, "Delegating"), MODULE_OPTIONS, options);
            DomainTestUtils.executeForResult(op, masterClient);
            // TODO check the resources
            // System.out.println(DomainTestUtils.executeForResult(Util.createOperation(READ_RESOURCE_OPERATION, address), modelControllerClient));
            PathAddress jaspi = subsystem.append(SECURITY_DOMAIN, "jaspi-test").append(AUTHENTICATION, JASPI);
            PathAddress jaspiLoginStack = jaspi.append(LOGIN_MODULE_STACK, "lm-stack");
            op = Util.createAddOperation(jaspiLoginStack.append(LOGIN_MODULE, "test2"));
            op.get(CODE).set("UserRoles");
            op.get(FLAG).set("required");
            op.get(MODULE).set("test-jaspi");
            DomainTestUtils.executeForResult(op, masterClient);
            op = Util.createAddOperation(jaspi.append(AUTH_MODULE, "Delegating"));
            op.get(CODE).set("Delegating");
            op.get(LOGIN_MODULE_STACK_REF).set("lm-stack");
            op.get(FLAG).set("optional");
            DomainTestUtils.executeForResult(op, masterClient);
            op = new ModelNode();
            op.get(OP).set(READ_RESOURCE_OPERATION);
            op.get(OP_ADDR).set(jaspi.toModelNode());
            op.get(INCLUDE_ALIASES).set(false);
            op.get(RECURSIVE).set(true);
            ModelNode masterResource = DomainTestUtils.executeForResult(op, masterClient);
            ModelNode slaveResource = DomainTestUtils.executeForResult(op, slaveClient);
            ModelTestUtils.compare(masterResource, slaveResource, true);
        } finally {
            try {
                // The server will be in restart-required mode, so restart it to get rid of the changes
                PathAddress serverAddress = PathAddress.pathAddress(HOST, "slave").append(SERVER_CONFIG, "server-one");
                ModelNode op = Util.createOperation(RESTART, PathAddress.pathAddress(serverAddress));
                op.get(BLOCKING).set(true);
                Assert.assertEquals("STARTED", validateResponse(slaveClient.execute(op), true).asString());
            } finally {
                IoUtils.safeClose(slaveClient);
                IoUtils.safeClose(masterClient);
            }
        }
    }

    @Test
    public void test00010_JgroupsTransformers() throws Exception {
        final DomainClient masterClient = getDomainMasterLifecycleUtil().createDomainClient();
        try {
            // Check composite operation
            final ModelNode compositeOp = new ModelNode();
            compositeOp.get(OP).set(COMPOSITE);
            compositeOp.get(OP_ADDR).setEmptyList();
            compositeOp.get(STEPS).add(SimpleMixedDomainTest.createProtocolPutPropertyOperation("tcp", "MPING", "send_on_all_interfaces", "true"));
            compositeOp.get(STEPS).add(SimpleMixedDomainTest.createProtocolPutPropertyOperation("tcp", "MPING", "receive_on_all_interfaces", "true"));
            DomainTestUtils.executeForResult(compositeOp, masterClient);
        } finally {
            IoUtils.safeClose(masterClient);
        }
    }

    /**
     * Tests test-connection-in-pool() of ExampleDS.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test00011_ExampleDSConnection() throws Exception {
        if ((version) == (EAP_6_2_0)) {
            // see: https://issues.jboss.org/browse/WFLY-7792
            return;
        }
        PathAddress exampleDSAddress = PathAddress.pathAddress(PathElement.pathElement(HOST, "slave"), PathElement.pathElement(RUNNING_SERVER, "server-one"), PathElement.pathElement(SUBSYSTEM, "datasources"), PathElement.pathElement("data-source", "ExampleDS"));
        DomainClient masterClient = getDomainMasterLifecycleUtil().createDomainClient();
        try {
            ModelNode op = Util.createOperation("test-connection-in-pool", PathAddress.pathAddress(exampleDSAddress));
            ModelNode response = masterClient.execute(op);
            Assert.assertEquals((((op.toString()) + '\n') + (response.toString())), SUCCESS, response.get(OUTCOME).asString());
        } finally {
            IoUtils.safeClose(masterClient);
        }
    }

    // Do this one last since it changes the host model of the slaves
    @Test
    public void test99999_ProfileClone() throws Exception {
        if ((version.getMajor()) == 6) {
            // EAP 6 does not have the clone operation
            profileCloneEap6x();
        } else {
            // EAP 7 does not have the clone operation
            profileCloneEap7x();
        }
    }
}

