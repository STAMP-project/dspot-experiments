/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc. and individual contributors
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
package org.jboss.as.test.integration.management.deploy.runtime;


import ModelDescriptionConstants.ADDRESS;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import ModelDescriptionConstants.RECURSIVE;
import ModelDescriptionConstants.SUBDEPLOYMENT;
import ModelDescriptionConstants.SUBSYSTEM;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.integration.management.deploy.runtime.servlet.Servlet;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class ServletRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static final Logger log = Logger.getLogger(ServletRuntimeNameTestCase.class);

    private static final Class SERVLET_CLASS = Servlet.class;

    private static final String RT_MODULE_NAME = "nooma-nooma7";

    private static final String RT_NAME = (ServletRuntimeNameTestCase.RT_MODULE_NAME) + ".ear";

    private static final String DEPLOYMENT_MODULE_NAME = "test7-test";

    private static final String DEPLOYMENT_NAME = (ServletRuntimeNameTestCase.DEPLOYMENT_MODULE_NAME) + ".ear";

    private static final String SUB_DEPLOYMENT_MODULE_NAME = "servlet";

    private static final String SUB_DEPLOYMENT_NAME = (ServletRuntimeNameTestCase.SUB_DEPLOYMENT_MODULE_NAME) + ".war";

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @Test
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, ServletRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = ServletRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, ServletRuntimeNameTestCase.SUB_DEPLOYMENT_NAME);
        result = ServletRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "undertow");
        result = ServletRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add("servlet", ServletRuntimeNameTestCase.SERVLET_CLASS.getName());
        result = ServletRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, ServletRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = ServletRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    public void testServletCall() throws Exception {
        String url = ((("http://" + (TestSuiteEnvironment.getServerAddress())) + ":8080/") + (ServletRuntimeNameTestCase.SUB_DEPLOYMENT_MODULE_NAME)) + (Servlet.URL_PATTERN);
        String res = HttpRequest.get(url, 2, TimeUnit.SECONDS);
        Assert.assertEquals(Servlet.SUCCESS, res);
    }

    /**
     * WFLY-2061 test
     */
    @Test
    public void testDeploymentStatus() throws IOException {
        PathAddress address = PathAddress.pathAddress(PathElement.pathElement(DEPLOYMENT, ServletRuntimeNameTestCase.DEPLOYMENT_NAME));
        final ModelNode operation = Util.createEmptyOperation(READ_ATTRIBUTE_OPERATION, address);
        operation.get(NAME).set("status");
        final ModelNode outcome = ServletRuntimeNameTestCase.controllerClient.execute(operation);
        Assert.assertEquals(SUCCESS, outcome.get(OUTCOME).asString());
        Assert.assertEquals("OK", outcome.get(RESULT).asString());
    }
}

