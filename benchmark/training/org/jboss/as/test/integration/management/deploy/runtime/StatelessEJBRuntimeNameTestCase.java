package org.jboss.as.test.integration.management.deploy.runtime;


import ModelDescriptionConstants.ADDRESS;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import ModelDescriptionConstants.RECURSIVE;
import ModelDescriptionConstants.SUBDEPLOYMENT;
import ModelDescriptionConstants.SUBSYSTEM;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.management.deploy.runtime.ejb.stateless.PointLessMathBean;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class StatelessEJBRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static final String EJB_TYPE = "stateless-session-bean";

    private static final Package BEAN_PACKAGE = PointLessMathBean.class.getPackage();

    private static final String BEAN_NAME = "POINT";

    private static final String RT_NAME = ("nooma-nooma4-" + (StatelessEJBRuntimeNameTestCase.EJB_TYPE)) + ".ear";

    private static final String DEPLOYMENT_NAME = ("test4-" + (StatelessEJBRuntimeNameTestCase.EJB_TYPE)) + "-test.ear";

    private static final String SUB_DEPLOYMENT_NAME = "ejb.jar";

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @Test
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatelessEJBRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = StatelessEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, StatelessEJBRuntimeNameTestCase.SUB_DEPLOYMENT_NAME);
        result = StatelessEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "ejb3");
        result = StatelessEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(StatelessEJBRuntimeNameTestCase.EJB_TYPE, StatelessEJBRuntimeNameTestCase.BEAN_NAME);
        result = StatelessEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatelessEJBRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = StatelessEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }
}

