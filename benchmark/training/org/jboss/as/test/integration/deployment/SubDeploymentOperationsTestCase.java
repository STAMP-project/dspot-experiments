/**
 * Copyright (C) 2016 Red Hat, inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jboss.as.test.integration.deployment;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests to check that the subdeployments function properly with other subdeployments after using
 * the explode operation.
 *
 * @author <a href="mailto:mjurc@redhat.com">Michal Jurc</a> (c) 2016 Red Hat, Inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SubDeploymentOperationsTestCase {
    private static final Logger logger = Logger.getLogger(SubDeploymentOperationsTestCase.class);

    private static final String TEST_DEPLOYMENT_NAME = "subdeployment-test.ear";

    private static final String JAR_SUBDEPLOYMENT_NAME = "subdeployment-test-ejb.jar";

    private static final String WAR_SUBDEPLOYMENT_NAME = "subdeployment-test-web.war";

    private static final String ARCHIVED_DEPLOYMENT_ERROR_CODE = "WFLYSRV0258";

    @ContainerResource
    ManagementClient managementClient;

    @Test
    public void testExplodeJarSubDeployment() throws Exception {
        ModelNode result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, "");
        Assert.assertTrue(("Failure to explode the initial deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, SubDeploymentOperationsTestCase.JAR_SUBDEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to explode JAR subdeployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = deploy(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to redeploy the deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        testEjbClassAvailableInServlet();
    }

    @Test
    public void testExplodeWarSubDeployment() throws Exception {
        ModelNode result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, "");
        Assert.assertTrue(("Failure to explode the initial deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, SubDeploymentOperationsTestCase.WAR_SUBDEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to explode WAR subdeployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = deploy(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to redeploy the deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        testEjbClassAvailableInServlet();
    }

    @Test
    public void testExplodeJarAndWarSubDeployment() throws Exception {
        ModelNode result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, "");
        Assert.assertTrue(("Failure to explode the initial deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, SubDeploymentOperationsTestCase.JAR_SUBDEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to explode JAR subdeployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, SubDeploymentOperationsTestCase.WAR_SUBDEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to explode WAR subdeployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        result = deploy(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to redeploy the deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        testEjbClassAvailableInServlet();
    }

    @Test
    public void testExplodeJarSubDeploymentArchiveDeployment() throws Exception {
        ModelNode result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, SubDeploymentOperationsTestCase.JAR_SUBDEPLOYMENT_NAME);
        Assert.assertFalse(("Exploding JAR subdeployment of archived deployment should fail, but outcome was " + (result.toString())), Operations.isSuccessfulOutcome(result));
        String failure = Operations.getFailureDescription(result).asString();
        Assert.assertTrue(("Exploding JAR subdeployment of archived deployment failed with wrong reason: " + failure), failure.contains(SubDeploymentOperationsTestCase.ARCHIVED_DEPLOYMENT_ERROR_CODE));
        result = deploy(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to redeploy the deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        testEjbClassAvailableInServlet();
    }

    @Test
    public void testExplodeWarSubDeploymentArchiveDeployment() throws Exception {
        ModelNode result = explode(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME, SubDeploymentOperationsTestCase.WAR_SUBDEPLOYMENT_NAME);
        Assert.assertFalse(("Exploding WAR subdeployment of archived deployment should fail, but outcome was " + (result.toString())), Operations.isSuccessfulOutcome(result));
        String failure = Operations.getFailureDescription(result).asString();
        Assert.assertTrue(("Exploding WAR subdeployment of archived deployment failed with wrong reason: " + failure), failure.contains(SubDeploymentOperationsTestCase.ARCHIVED_DEPLOYMENT_ERROR_CODE));
        result = deploy(SubDeploymentOperationsTestCase.TEST_DEPLOYMENT_NAME);
        Assert.assertTrue(("Failure to redeploy the deployment: " + (result.toString())), Operations.isSuccessfulOutcome(result));
        testEjbClassAvailableInServlet();
    }
}

