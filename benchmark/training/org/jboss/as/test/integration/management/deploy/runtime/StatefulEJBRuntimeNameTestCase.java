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
import org.jboss.as.test.integration.management.deploy.runtime.ejb.statefull.PointLessMathBean;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class StatefulEJBRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static final String EJB_TYPE = "stateful-session-bean";

    private static final Package BEAN_PACKAGE = PointLessMathBean.class.getPackage();

    private static final String BEAN_NAME = "POINT";

    private static final String RT_NAME = ("nooma-nooma2-" + (StatefulEJBRuntimeNameTestCase.EJB_TYPE)) + ".ear";

    private static final String DEPLOYMENT_NAME = ("test2-" + (StatefulEJBRuntimeNameTestCase.EJB_TYPE)) + "-test.ear";

    private static final String SUB_DEPLOYMENT_NAME = "ejb.jar";

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @Test
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatefulEJBRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = StatefulEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, StatefulEJBRuntimeNameTestCase.SUB_DEPLOYMENT_NAME);
        result = StatefulEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "ejb3");
        result = StatefulEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(StatefulEJBRuntimeNameTestCase.EJB_TYPE, StatefulEJBRuntimeNameTestCase.BEAN_NAME);
        result = StatefulEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatefulEJBRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = StatefulEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    public StatefulEJBRuntimeNameTestCase() {
        // TODO Auto-generated constructor stub
    }
}

