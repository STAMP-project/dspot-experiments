package org.jboss.as.test.integration.management.deploy.runtime;


import ModelDescriptionConstants.ADDRESS;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import ModelDescriptionConstants.RECURSIVE;
import ModelDescriptionConstants.SUBDEPLOYMENT;
import ModelDescriptionConstants.SUBSYSTEM;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.jboss.as.test.integration.management.deploy.runtime.ejb.singleton.timer.PointLessBean;
import org.jboss.as.test.integration.management.deploy.runtime.ejb.singleton.timer.PointlessInterface;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class TimerEJBRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static final Logger log = Logger.getLogger(TimerEJBRuntimeNameTestCase.class);

    private static final String EJB_TYPE = EJBManagementUtil.SINGLETON;

    private static final Package BEAN_PACKAGE = PointLessBean.class.getPackage();

    private static final Class BEAN_CLASS = PointlessInterface.class;

    private static final String BEAN_NAME = "POINT";

    private static final String RT_MODULE_NAME = "nooma-nooma6-" + (TimerEJBRuntimeNameTestCase.EJB_TYPE);

    private static final String RT_NAME = (TimerEJBRuntimeNameTestCase.RT_MODULE_NAME) + ".ear";

    private static final String DEPLOYMENT_MODULE_NAME = ("test6-" + (TimerEJBRuntimeNameTestCase.EJB_TYPE)) + "-test";

    private static final String DEPLOYMENT_NAME = (TimerEJBRuntimeNameTestCase.DEPLOYMENT_MODULE_NAME) + ".ear";

    private static final String SUB_DEPLOYMENT_MODULE_NAME = "ejb";

    private static final String SUB_DEPLOYMENT_NAME = (TimerEJBRuntimeNameTestCase.SUB_DEPLOYMENT_MODULE_NAME) + ".jar";

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @Test
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, TimerEJBRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = TimerEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, TimerEJBRuntimeNameTestCase.SUB_DEPLOYMENT_NAME);
        result = TimerEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "ejb3");
        result = TimerEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(TimerEJBRuntimeNameTestCase.EJB_TYPE, TimerEJBRuntimeNameTestCase.BEAN_NAME);
        result = TimerEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, TimerEJBRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = TimerEJBRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    public void testTimer() throws Exception {
        final InitialContext context = TimerEJBRuntimeNameTestCase.getInitialContext();
        try {
            PointlessInterface pointlessInterface = ((PointlessInterface) (context.lookup(getEJBJNDIBinding())));
            pointlessInterface.triggerTimer();
            Thread.currentThread().sleep(1000);
            Assert.assertTrue("Did not receive timer invocation!", ((pointlessInterface.getTimerCount()) > 0));
        } finally {
            TimerEJBRuntimeNameTestCase.safeClose(context);
        }
    }
}

